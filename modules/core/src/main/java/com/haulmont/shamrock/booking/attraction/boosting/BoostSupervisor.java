/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting;

import com.haulmont.monaco.scheduler.annotations.Schedule;
import com.haulmont.monaco.scheduler.annotations.Scheduled;
import com.haulmont.shamrock.booking.attraction.boosting.boost_applier.BookingBoostApplierService;
import com.haulmont.shamrock.booking.attraction.boosting.config.ServiceConfiguration;
import com.haulmont.shamrock.booking.attraction.boosting.model.AttractionBoostingContext;
import com.haulmont.shamrock.booking.attraction.boosting.model.AutoBoostDecision;
import com.haulmont.shamrock.booking.attraction.boosting.model.BookingResponseTime;
import com.haulmont.shamrock.booking.attraction.boosting.model.Strategy;
import com.haulmont.shamrock.booking.attraction.boosting.services.BusyRegionsSupervisorService;
import com.haulmont.shamrock.booking.attraction.boosting.services.ProductRegistryService;
import com.haulmont.shamrock.booking.attraction.boosting.services.dto.booking_cache.Booking;
import com.haulmont.shamrock.booking.attraction.boosting.services.dto.busy_regions_supervisor.BusyRegion;
import com.haulmont.shamrock.booking.attraction.boosting.storage.AutoBoostDecisionStorage;
import com.haulmont.shamrock.booking.attraction.boosting.util.BookingUtil;
import org.joda.time.DateTime;
import org.picocontainer.annotations.Component;
import org.picocontainer.annotations.Inject;
import org.slf4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

import static com.haulmont.shamrock.booking.attraction.boosting.model.AttractionBoostingContext.builder;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Component
@Scheduled
public class BoostSupervisor {
    @Inject
    private Logger log;

    @Inject
    private AutoBoostCalculationService autoBoostCalculationService;
    @Inject
    private BoostConsumptionService consumptionService;
    @Inject
    private BookingBoostApplierService bookingPersistentService;
    @Inject
    private BookingResponseTimeService bookingResponseTimeService;
    @Inject
    private ServiceConfiguration configuration;
    @Inject
    private BusyRegionsSupervisorService busyRegionsSupervisorService;
    @Inject
    private ProductRegistryService productRegistryService;
    @Inject
    private StrategyService strategyService;
    @Inject
    private AutoBoostDecisionStorage autoBoostDecisionStorage;

    @Schedule(schedule = "boost.supervisor.auto.period", singleton = true)
    public void execute() {
        try {
            log.info("Start boosting visibility");
            long start = System.currentTimeMillis();

            __execute();

            long ms = System.currentTimeMillis() - start;
            log.info("Finish boosting visibility in {}s", ms / 1000);
        } catch (Throwable t) {
            log.warn("Failed to execute BoostSupervisor#execute", t);
        }
    }

    private void __execute() {
        AttractionBoostingContext context = buildContext(DateTime.now());

        for (BookingResponseTime bookingResponseTime : bookingResponseTimeService.getBookingResponseTimes(context).values()) {
            DateTime now = context.getCreateTs();

            Booking booking = bookingResponseTime.getBooking();
            BusyRegion busyRegion = bookingResponseTime.getResponseTimeSource();
            Strategy strategy = context.getStrategy(busyRegion);

            try {
                Double currentBoostValue = BookingUtil.getBoostValue(booking, configuration.getBoostInstructionCode());

                Optional<AutoBoostDecision> currentDecision = autoBoostDecisionStorage.getDecision(booking.getId());

                if (currentBoostValue != null && currentDecision.isEmpty()) {
                    //todo log ignored manually boosted booking
                } else {
                    Optional<AutoBoostDecision> newDecision = autoBoostCalculationService.calculate(bookingResponseTime, context, currentDecision.orElse(null));

                    newDecision.ifPresentOrElse(decision -> {
                        if (currentDecision.isEmpty() || decision.getBoostValue() > currentDecision.get().getBoostValue()) {
                            double boostValueDelta = decision.getBoostValue() - currentDecision.map(AutoBoostDecision::getBoostValue).orElse(0.0);
                            boolean hasRemainingCap = consumptionService.hasRemainingCap(now, strategy, boostValueDelta);

                            if (hasRemainingCap) {
                                //todo return true if applied
                                bookingPersistentService.apply(decision.getBoostValue(), booking);

                                consumptionService.addConsumption(now, strategy, boostValueDelta);

                                autoBoostDecisionStorage.saveDecision(booking.getId(), decision);
                                log.info("Boost applied for booking {}. Boost decision: {}", booking, decision);
                            } else {
                                log.debug("Boost for booking {} was not applied because of lack of remaining capacity.", booking);
                            }
                        }  else {
                            log.debug("Keep current boost level for booking {}. Boost decision: {}", booking, decision);;
                        }
                    }, () -> log.info("Keep current boost level for booking {}.", booking));
                }
            } catch (Throwable t) {
                log.warn("Failed to calculate boost for booking {}", booking, t);
            }
        }
    }

    private AttractionBoostingContext buildContext(DateTime now) {
        List<BusyRegion> busyRegions = busyRegionsSupervisorService.getActiveBusyRegions();
        Map<BusyRegion, Strategy> strategyByRegion = busyRegions.stream()
                .collect(toMap(identity(), k -> strategyService.matchStrategy(k)));

        busyRegions.removeIf(it -> {
            Strategy strategy = strategyByRegion.get(it);
            if (strategy == null) {
                //todo this log will be there every time
                log.debug("Busy region '{}' was ignored, cause strategy was not found", it);
                return true;
            }

            if (strategy.isDisabled()) {
                //todo this log will be there every time
                log.debug("Busy region '{}' was ignored, cause strategy '{}' is disabled", it, strategy);
                return true;
            }

            if (consumptionService.isExceeded(now, strategy)) {
                //todo this log will be there every time
                log.debug("Busy region '{}' was ignored, cause strategy '{}' exceeded consumption limits", it, strategy);
                return true;
            }

            return false;
        });

        Set<UUID> uniqueProductGroupIds = busyRegions.stream()
                .map(BusyRegion::getProductGroupId)
                .collect(Collectors.toSet());

        Map<UUID, List<UUID>> productIdsByProductGroup = productRegistryService.getProductsIdsForGroupsIds(uniqueProductGroupIds);
        Map<BusyRegion, List<UUID>> productIdsByRegion = busyRegions.stream()
                .collect(toMap(identity(), k -> productIdsByProductGroup.get(k.getProductGroupId())));

        return builder(now, busyRegions, strategyByRegion, productIdsByRegion).build();
    }
}

/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting;

import com.haulmont.shamrock.booking.attraction.boosting.model.*;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.picocontainer.annotations.Component;
import org.picocontainer.annotations.Inject;
import org.slf4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

import static com.haulmont.shamrock.booking.attraction.boosting.model.strategy.BoostEvaluatorFactory.getEvaluator;

@Component
public class AutoBoostCalculationService {
    private static final Comparator<Band> BAND_COMPARATOR =
            Comparator
                    .comparingLong((Band b) -> b.getMinExtraResponseTime().toStandardSeconds().getSeconds())
                    .reversed()
                    .thenComparingInt(Band::getMinBookingPriority)
                    .reversed();
    @Inject
    private Logger log;
    @Inject
    private StrategyService strategyService;

    @Inject
    private CustomerGradeService customerGradeService;

    //todo possible flaw - during band matching check previous boost level to make sure new band does not have lower boost level + switching between bands?
    public Optional<AutoBoostDecision> calculate(BookingResponseTime bookingResponseTime, AttractionBoostingContext context, AutoBoostDecision currentDecision) {
        Strategy strategy = context.getStrategy(bookingResponseTime.getResponseTimeSource());

        Optional<Band> optionalNewBand = selectBand(strategy.getBands().getItems(), bookingResponseTime);
        if (optionalNewBand.isEmpty()) return Optional.empty();

        Band newBand = optionalNewBand.get();

        DateTime now = context.getCreateTs();

        Period responsetimePeriod = bookingResponseTime.getResponseTime().toPeriod();
        String sourceScope = strategy.getBands().getSourceScope();
        if (currentDecision == null) {
            double value = getEvaluator(strategy.getType(), newBand.getBoostConfiguration()).evaluate(now, null);

            return Optional.of(new AutoBoostDecision(now, responsetimePeriod, sourceScope, newBand, strategy.getCaps(), value));
        } else {
            double value = getEvaluator(strategy.getType(), newBand.getBoostConfiguration()).evaluate(now, currentDecision);

            if (value > currentDecision.getBoostValue()
                    || value == currentDecision.getBoostValue() && !newBand.equals(currentDecision.getBand())) {
                return Optional.of(new AutoBoostDecision(now, responsetimePeriod, sourceScope, newBand,  strategy.getCaps(), value));
            }
        }

        return Optional.empty();
    }
    //todo sort bands in StrategyService
    private Optional<Band> selectBand(List<Band> bands, BookingResponseTime bookingResponseTime) {
        List<Band> sorted = new ArrayList<>(bands).stream().sorted(BAND_COMPARATOR).collect(Collectors.toList());
        return sorted.stream()
                .filter(it -> bookingResponseTime.getBooking().getPriority() >= it.getMinBookingPriority())
                .filter(it -> bookingResponseTime.getResponseTime().toStandardSeconds().getSeconds() >= it.getMinExtraResponseTime().toStandardSeconds().getSeconds())
                .findFirst();
    }
}

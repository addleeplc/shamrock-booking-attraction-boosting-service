/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting;

import com.haulmont.shamrock.booking.attraction.boosting.model.Cap;
import com.haulmont.shamrock.booking.attraction.boosting.model.CapConsumption;
import com.haulmont.shamrock.booking.attraction.boosting.model.Strategy;
import com.haulmont.shamrock.booking.attraction.boosting.storage.ConsumptionStorage;
import org.joda.time.DateTime;
import org.picocontainer.annotations.Component;
import org.picocontainer.annotations.Inject;
import org.slf4j.Logger;

import java.util.Map;
import java.util.function.Function;

@Component
public class BoostConsumptionService {
    private static final Map<CapConsumption.PeriodType, Function<Strategy, Cap>> CAP_SUPPLIERS_TO_PERIOD_TYPE = Map.of(
            CapConsumption.PeriodType.DAILY, (s) -> s.getCaps().getPerDay(),
            CapConsumption.PeriodType.WEEKLY, (s) -> s.getCaps().getPerWeek(),
            CapConsumption.PeriodType.MONTHLY, (s) -> s.getCaps().getPerMonth()
    );
    @Inject
    private Logger log;

    @Inject
    private StrategyService strategyService;

    @Inject
    private ConsumptionStorage consumptionStorage;

    public boolean isExceeded(DateTime now, Strategy strategy) {
        return !hasRemainingCap(now, strategy, 0.0);
    }

    public boolean hasRemainingCap(DateTime now, Strategy strategy, double delta) {
        for(CapConsumption.PeriodType periodType : CapConsumption.PeriodType.values()) {
            CapConsumption consumption = consumptionStorage.get(now, periodType, strategy);

            Cap strategyCap = CAP_SUPPLIERS_TO_PERIOD_TYPE.get(periodType).apply(strategy);
            if(consumption.getConsumption() + delta > strategyCap.getValue()) {
                log.debug("Consumption cap exceeded for {}, Consumption: {}, delta: {}", strategy, consumption, delta);
                return false;
            }
        }

        return true;
    }

    public void addConsumption(DateTime now, Strategy strategy, double delta) {
        for(CapConsumption.PeriodType periodType : CapConsumption.PeriodType.values()) {
            consumptionStorage.addToConsumption(now, periodType, strategy, delta);
        }
    }
}

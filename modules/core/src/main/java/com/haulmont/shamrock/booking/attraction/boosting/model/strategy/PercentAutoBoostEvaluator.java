/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting.model.strategy;

import com.haulmont.shamrock.booking.attraction.boosting.model.BoostConfiguration;

/**
 * One AU is equal to one percent of the driver price.
 */
public class PercentAutoBoostEvaluator extends AbstractAutoBoostEvaluator {

    private final Double driverPrice;

    protected PercentAutoBoostEvaluator(BoostConfiguration boostConfiguration, Double driverPrice) {
        super(boostConfiguration);
        this.driverPrice = driverPrice;
    }

    @Override
    public Double convertToValue(Double percent) {
        return driverPrice * percent / 100.0 ;
    }
}

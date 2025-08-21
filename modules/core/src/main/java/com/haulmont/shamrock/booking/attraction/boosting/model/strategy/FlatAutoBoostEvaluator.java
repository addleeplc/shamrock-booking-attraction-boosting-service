/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting.model.strategy;

import com.haulmont.shamrock.booking.attraction.boosting.model.BoostConfiguration;

/**
 * One AU is equal to one pound.
 */
public class FlatAutoBoostEvaluator extends AbstractAutoBoostEvaluator {
    protected FlatAutoBoostEvaluator(BoostConfiguration boostConfiguration) {
        super(boostConfiguration);
    }

    @Override
    public Double convertToValue(Double amount) {
        return amount;
    }
}

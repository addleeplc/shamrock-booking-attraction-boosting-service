/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting.model.strategy;

import com.haulmont.shamrock.booking.attraction.boosting.model.BoostConfiguration;
import com.haulmont.shamrock.booking.attraction.boosting.model.StrategyType;

//todo we have Strategy in out service domain already,
// it is quite confusing to have same names for different classes/interfaces
public class BoostEvaluatorFactory {

    public static AutoBoostEvaluator getEvaluator(StrategyType type, BoostConfiguration boostConfiguration) {
        switch (type) {
            case PERCENTAGE: {
                throw new IllegalArgumentException("Percentage strategy is not supported yet");
            }
            case FLAT: {
                return new FlatAutoBoostEvaluator(boostConfiguration);
            }
            default: {
                throw new IllegalArgumentException("Unsupported strategy type: " + type);
            }
        }
    }

}

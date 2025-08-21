/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting.model.strategy;

import com.haulmont.shamrock.booking.attraction.boosting.model.AutoBoostDecision;
import com.haulmont.shamrock.booking.attraction.boosting.model.BoostConfiguration;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractAutoBoostEvaluator implements AutoBoostEvaluator {
    private final BoostConfiguration boostConfiguration;

    protected AbstractAutoBoostEvaluator(BoostConfiguration boostConfiguration) {
        this.boostConfiguration = boostConfiguration;
    }

    @Override
    public double evaluate(DateTime now, AutoBoostDecision currentDecision) {
        if (currentDecision == null) {
            return convertToValue(boostConfiguration.getStart());
        } else if (currentDecision.getBoostValue() >= boostConfiguration.getThreshold()) {
            return convertToValue(boostConfiguration.getThreshold());
        } else if (new Duration(currentDecision.getTs(), now).toStandardSeconds().getSeconds() > boostConfiguration.getStep().getIntervalSeconds()) {
            return convertToValue(currentDecision.getBoostValue() + boostConfiguration.getStep().getAmount());
        } else {
            return convertToValue(currentDecision.getBoostValue());
        }
    }

    protected abstract Double convertToValue(Double amount);

}

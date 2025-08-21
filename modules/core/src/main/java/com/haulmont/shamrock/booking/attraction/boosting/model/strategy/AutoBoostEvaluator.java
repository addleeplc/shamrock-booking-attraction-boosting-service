/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting.model.strategy;

import com.haulmont.shamrock.booking.attraction.boosting.model.AutoBoostDecision;
import com.haulmont.shamrock.booking.attraction.boosting.model.BoostConfiguration;
import com.haulmont.shamrock.booking.attraction.boosting.model.Step;
import org.joda.time.DateTime;
import org.joda.time.Period;

/**
 * Defines how the following parameters are interpreted:
 * <ul>
 *     <li>{@link BoostConfiguration#getStart()}</li>
 *     <li>{@link BoostConfiguration#getThreshold()}</li>
 *     <li>{@link Step#getAmount()}</li>
 * </ul>
 */
public interface AutoBoostEvaluator {
    double evaluate(DateTime now, AutoBoostDecision currentDecision);
}

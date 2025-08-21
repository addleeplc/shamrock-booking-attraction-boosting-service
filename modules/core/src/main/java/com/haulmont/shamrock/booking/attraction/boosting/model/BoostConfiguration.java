/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class BoostConfiguration {

    @JsonProperty("start")
    private Double start;
    @JsonProperty("threshold")
    private Double threshold;
    @JsonProperty("step")
    private Step step;

    public Double getStart() {
        return start;
    }

    public void setStart(Double start) {
        this.start = start;
    }

    public Double getThreshold() {
        return threshold;
    }

    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }

    public Step getStep() {
        return step;
    }

    public void setStep(Step step) {
        this.step = step;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BoostConfiguration boostConfiguration = (BoostConfiguration) o;
        return Objects.equals(start, boostConfiguration.start) && Objects.equals(threshold, boostConfiguration.threshold) && Objects.equals(step, boostConfiguration.step);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, threshold, step);
    }
}

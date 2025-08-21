/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Step {

    @JsonProperty("amount")
    private Double amount;
    @JsonProperty("interval_seconds")
    private Integer intervalSeconds;

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Integer getIntervalSeconds() {
        return intervalSeconds;
    }

    public void setIntervalSeconds(Integer intervalSeconds) {
        this.intervalSeconds = intervalSeconds;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Step step = (Step) o;
        return Objects.equals(amount, step.amount) && Objects.equals(intervalSeconds, step.intervalSeconds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, intervalSeconds);
    }
}

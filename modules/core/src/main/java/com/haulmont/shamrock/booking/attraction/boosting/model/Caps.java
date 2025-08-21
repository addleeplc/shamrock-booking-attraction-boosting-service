/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Caps {

    @JsonProperty("currency")
    private String currency;
    @JsonProperty("per_day")
    private Cap perDay;
    @JsonProperty("per_week")
    private Cap perWeek;
    @JsonProperty("per_month")
    private Cap perMonth;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Cap getPerDay() {
        return perDay;
    }

    public void setPerDay(Cap perDay) {
        this.perDay = perDay;
    }

    public Cap getPerWeek() {
        return perWeek;
    }

    public void setPerWeek(Cap perWeek) {
        this.perWeek = perWeek;
    }

    public Cap getPerMonth() {
        return perMonth;
    }

    public void setPerMonth(Cap perMonth) {
        this.perMonth = perMonth;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Caps)) return false;
        Caps caps = (Caps) o;
        return Objects.equals(currency, caps.currency)
                && Objects.equals(perDay, caps.perDay)
                && Objects.equals(perWeek, caps.perWeek)
                && Objects.equals(perMonth, caps.perMonth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currency, perDay, perWeek, perMonth);
    }

    @Override
    public String toString() {
        return "{" +
                "currency='" + currency + '\'' +
                ", perDay=" + perDay +
                ", perWeek=" + perWeek +
                ", perMonth=" + perMonth +
                '}';
    }
}

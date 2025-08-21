/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.haulmont.shamrock.booking.attraction.boosting.util.DateTimeUtil;
import org.joda.time.format.DateTimeFormatter;


public class CapConsumption {
    @JsonProperty("scope")
    private String scope;
    @JsonProperty("period")
    private String period;
    @JsonProperty("consumption")
    private double consumption = 0.0;

    public CapConsumption() {
    }

    public void addToConsumption(Double amount) {
        consumption += amount;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public Double getConsumption() {
        return consumption;
    }

    public void setConsumption(Double consumption) {
        this.consumption = consumption;
    }

    //todo make align with all logs
    @Override
    public String toString() {
        return "{" +
                "scope='" + scope + '\'' +
                ", period='" + period + '\'' +
                ", consumption=" + consumption +
                '}';
    }

    public enum PeriodType {
        DAILY("daily", DateTimeUtil.DAILY_FORMATTER),
        WEEKLY( "weekly", DateTimeUtil.WEEKLY_FORMATTER),
        MONTHLY("monthly", DateTimeUtil.MONTHLY_FORMATTER);

        private final String value;
        private final DateTimeFormatter formatter;

        PeriodType(String value, DateTimeFormatter formatter) {
            this.value = value;
            this.formatter = formatter;
        }

        public String getValue() {
            return value;
        }

        public DateTimeFormatter getFormatter() {
            return formatter;
        }

        @JsonCreator
        public static PeriodType fromString(String text) {
            if (text != null) {
                for (PeriodType b : PeriodType.values()) {
                    if (text.equalsIgnoreCase(b.getValue())) {
                        return b;
                    }
                }
            }
            return null;
        }
    }
}

/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.haulmont.bali.jackson.joda.DateTimeAdapter;
import com.haulmont.bali.jackson.joda.DurationAdapter;
import org.joda.time.DateTime;
import org.joda.time.Period;

public class AutoBoostDecision {
    @JsonSerialize(using = DateTimeAdapter.Serializer.class)
    @JsonDeserialize(using = DateTimeAdapter.Deserializer.class)
    @JsonProperty("ts")
    private DateTime ts;

    @JsonDeserialize(using = DurationAdapter.Deserializer.class)
    @JsonSerialize(using = DurationAdapter.Serializer.class)
    @JsonProperty("extra_response_time")
    private Period extraResponseTime;

    @JsonProperty("bands_scope")
    private String bandsScope;
    @JsonProperty("band")
    private Band band;
    @JsonProperty("boost_value")
    private double boostValue;
    @JsonProperty("caps")
    private Caps caps;

    public AutoBoostDecision(DateTime ts, Period extraResponseTime, String bandsScope, Band band, Caps caps, double boostValue) {
        this.ts = ts;
        this.extraResponseTime = extraResponseTime;
        this.caps = caps;
        this.bandsScope = bandsScope;
        this.band = band;
        this.boostValue = boostValue;
    }

    public AutoBoostDecision() {
    }

    public Period getExtraResponseTime() {
        return extraResponseTime;
    }

    public void setExtraResponseTime(Period extraResponseTime) {
        this.extraResponseTime = extraResponseTime;
    }

    public String getBandsScope() {
        return bandsScope;
    }

    public void setBandsScope(String bandsScope) {
        this.bandsScope = bandsScope;
    }

    public Band getBand() {
        return band;
    }

    public void setBand(Band band) {
        this.band = band;
    }

    public Caps getCaps() {
        return caps;
    }

    public void setCaps(Caps caps) {
        this.caps = caps;
    }

    public DateTime getTs() {
        return ts;
    }

    public void setTs(DateTime ts) {
        this.ts = ts;
    }

    public double getBoostValue() {
        return boostValue;
    }

    public void setBoostValue(double boostValue) {
        this.boostValue = boostValue;
    }

    @Override
    public String toString() {
        return "{" +
                "ts=" + ts +
                ", bandsScope='" + bandsScope + '\'' +
                ", band=" + band +
                ", extraResponseTime=" + extraResponseTime +
                ", boostValue=" + boostValue +
                ", caps=" + caps +
                '}';
    }
}

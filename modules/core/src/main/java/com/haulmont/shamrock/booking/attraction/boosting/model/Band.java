/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.haulmont.bali.jackson.joda.DurationAdapter;
import org.joda.time.Period;

import java.util.Objects;
import java.util.UUID;

public class Band {
    @JsonProperty("id")
    private UUID id;

    @JsonSerialize(using = DurationAdapter.Serializer.class)
    @JsonDeserialize(using = DurationAdapter.Deserializer.class)
    @JsonProperty("min_extra_response_time")
    private Period minExtraResponseTime;
    @JsonProperty("min_booking_priority")
    private int minBookingPriority = -100;
    @JsonProperty("boost_configuration")
    private BoostConfiguration boostConfiguration;
    @JsonProperty("type")
    private BandType type;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Period getMinExtraResponseTime() {
        return minExtraResponseTime;
    }

    public void setMinExtraResponseTime(Period minExtraResponseTime) {
        this.minExtraResponseTime = minExtraResponseTime;
    }

    public Integer getMinBookingPriority() {
        return minBookingPriority;
    }

    public void setMinBookingPriority(Integer minBookingPriority) {
        this.minBookingPriority = minBookingPriority;
    }

    public BoostConfiguration getBoostConfiguration() {
        return boostConfiguration;
    }

    public void setBoost(BoostConfiguration boostConfiguration) {
        this.boostConfiguration = boostConfiguration;
    }

    public BandType getType() {
        return type;
    }

    public void setType(BandType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Band)) return false;
        Band band = (Band) o;
        return Objects.equals(id, band.id)
                && Objects.equals(minExtraResponseTime, band.minExtraResponseTime)
                && Objects.equals(minBookingPriority, band.minBookingPriority)
                && Objects.equals(boostConfiguration, band.boostConfiguration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, minExtraResponseTime, minBookingPriority, boostConfiguration);
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", minExtraResponseTime=" + minExtraResponseTime +
                ", minBookingPriority=" + minBookingPriority +
                '}';
    }
}

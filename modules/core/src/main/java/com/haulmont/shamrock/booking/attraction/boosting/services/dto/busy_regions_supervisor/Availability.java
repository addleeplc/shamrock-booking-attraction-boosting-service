/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting.services.dto.busy_regions_supervisor;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.haulmont.bali.jackson.joda.DurationAdapter;
import org.joda.time.Period;

//todo why Account?
public class Availability {
    @JsonProperty("fully_booked")
    private boolean fullyBooked;
    @JsonDeserialize(using = DurationAdapter.Deserializer.class)
    @JsonSerialize(using = DurationAdapter.Serializer.class)
    @JsonProperty("extra_response_time")
    private Period extraResponseTime;

    public boolean isFullyBooked() {
        return fullyBooked;
    }

    public void setFullyBooked(Boolean fullyBooked) {
        this.fullyBooked = fullyBooked;
    }

    public Period getExtraResponseTime() {
        return extraResponseTime;
    }

    public void setExtraResponseTime(Period extraResponseTime) {
        this.extraResponseTime = extraResponseTime;
    }
}

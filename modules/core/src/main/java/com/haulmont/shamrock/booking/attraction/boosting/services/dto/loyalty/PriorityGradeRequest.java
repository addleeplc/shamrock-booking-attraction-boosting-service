/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting.services.dto.loyalty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.haulmont.shamrock.booking.attraction.boosting.services.dto.booking_cache.Booking;

public class PriorityGradeRequest {
    @JsonProperty("booking")
    private Booking booking;

    public PriorityGradeRequest(Booking booking) {
        this.booking = booking;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

}

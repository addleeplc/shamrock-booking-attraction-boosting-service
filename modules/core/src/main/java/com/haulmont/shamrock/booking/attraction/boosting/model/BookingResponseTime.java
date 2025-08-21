/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting.model;

import com.haulmont.shamrock.booking.attraction.boosting.services.dto.booking_cache.Booking;
import com.haulmont.shamrock.booking.attraction.boosting.services.dto.busy_regions_supervisor.BusyRegion;
import org.joda.time.Duration;


public class BookingResponseTime {
    private final Booking booking;
    private final BusyRegion responseTimeSource;
    private final Duration responseTime;

    public BookingResponseTime(Booking booking, BusyRegion busyRegion, Duration responseTime) {
        this.booking = booking;
        this.responseTimeSource = busyRegion;
        this.responseTime = responseTime;
    }

    public Booking getBooking() {
        return booking;
    }

    public BusyRegion getResponseTimeSource() {
        return responseTimeSource;
    }

    public Duration getResponseTime() {
        return responseTime;
    }
}

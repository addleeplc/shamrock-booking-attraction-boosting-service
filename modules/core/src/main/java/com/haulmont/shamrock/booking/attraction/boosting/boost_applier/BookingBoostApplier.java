/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting.boost_applier;

import com.haulmont.shamrock.booking.attraction.boosting.services.dto.booking_cache.Booking;

//todo explain BookingPersistence
public interface BookingBoostApplier {
    void apply(Double amount, Booking booking);
}

/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting.boost_applier;

import com.haulmont.shamrock.booking.attraction.boosting.services.dto.booking_cache.Booking;
import org.picocontainer.annotations.Component;
import org.picocontainer.annotations.Inject;
import org.slf4j.Logger;

@Component
public class DummyBookingBoostApplierService implements BookingBoostApplier {

    @Inject
    private Logger log;

    /**
     * DO NOT CALL DIRECTLY. use {@link BookingBoostApplierService#apply(Double, Booking)}
     */
    @Override
    public void apply(Double amount, Booking booking) {
        log.info("Update boost for {}. Amount: {}.", booking, amount);
    }
}

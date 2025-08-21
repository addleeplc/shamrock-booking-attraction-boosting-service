/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting.boost_applier;

import com.haulmont.shamrock.booking.attraction.boosting.config.ServiceConfiguration;
import com.haulmont.shamrock.booking.attraction.boosting.services.dto.booking_cache.Booking;
import org.picocontainer.annotations.Component;
import org.picocontainer.annotations.Inject;

import java.util.Map;

@Component
public class BookingBoostApplierService implements BookingBoostApplier {
    @Inject
    private DummyBookingBoostApplierService dummyBookingPersistentService;
    @Inject
    private LiveBookingBoostApplierService liveBookingPersistentService;

    @Inject
    private ServiceConfiguration serviceConfiguration;

    private Map<String, BookingBoostApplier> providers;

    public void start() {
        providers = Map.of(
                "DRY", dummyBookingPersistentService,
                "LIVE", liveBookingPersistentService
        );
    }

    @Override
    public void apply(Double amount, Booking booking) {
        providers.get(serviceConfiguration.getSupervisorMode()).apply(amount, booking);
    }
}

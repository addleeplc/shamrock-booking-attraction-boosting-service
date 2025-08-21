/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting.services.booking_persistent_service;

import com.haulmont.shamrock.booking.attraction.boosting.boost_applier.BookingBoostApplier;
import com.haulmont.shamrock.booking.attraction.boosting.boost_applier.BookingBoostApplierService;
import com.haulmont.shamrock.booking.attraction.boosting.boost_applier.DummyBookingBoostApplierService;
import com.haulmont.shamrock.booking.attraction.boosting.boost_applier.LiveBookingBoostApplierService;
import com.haulmont.shamrock.booking.attraction.boosting.config.ServiceConfiguration;
import com.haulmont.shamrock.booking.attraction.boosting.services.dto.booking_cache.Booking;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Map;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class BookingPersistentServiceTest {

    private BookingBoostApplierService service;

    @Mock
    private DummyBookingBoostApplierService dummyBookingPersistentService;
    @Mock
    private LiveBookingBoostApplierService liveBookingPersistentService;
    @Mock
    private ServiceConfiguration serviceConfiguration;
    @Mock
    private Booking booking;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new BookingBoostApplierService();

        ReflectionTestUtils.setField(service, "dummyBookingPersistentService", dummyBookingPersistentService);
        ReflectionTestUtils.setField(service, "liveBookingPersistentService", liveBookingPersistentService);
        ReflectionTestUtils.setField(service, "serviceConfiguration", serviceConfiguration);
    }

    @Test
    public void start_shouldInitializeProvidersMap() {
        // when
        service.start();

        // then
        @SuppressWarnings("unchecked")
        Map<String, BookingBoostApplier> providers =
                (Map<String, BookingBoostApplier>) ReflectionTestUtils.getField(service, "providers");
        assertNotNull(providers, "providers must be initialized by start()");
        assertEquals(providers.get("DRY"), dummyBookingPersistentService);
        assertEquals(providers.get("LIVE"), liveBookingPersistentService);
        assertEquals(providers.size(), 2);
    }

    @Test
    public void updateBoost_shouldDelegateToDummy_whenModeDRY() {
        when(serviceConfiguration.getSupervisorMode()).thenReturn("DRY");
        service.start();

        Double amount = 42.0;
        service.apply(amount, booking);

        verify(dummyBookingPersistentService, times(1)).apply(amount, booking);
        verifyNoInteractions(liveBookingPersistentService);
    }

    @Test
    public void updateBoost_shouldDelegateToLive_whenModeLIVE() {
        when(serviceConfiguration.getSupervisorMode()).thenReturn("LIVE");
        service.start();

        Double amount = 7.5;
        service.apply(amount, booking);

        verify(liveBookingPersistentService, times(1)).apply(amount, booking);
        verifyNoInteractions(dummyBookingPersistentService);
    }

}
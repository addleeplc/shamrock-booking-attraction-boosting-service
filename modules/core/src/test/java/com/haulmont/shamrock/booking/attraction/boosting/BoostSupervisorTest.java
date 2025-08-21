///*
// * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
// * Haulmont Technology proprietary and confidential.
// * Use is subject to license terms.
// */
//
//package com.haulmont.shamrock.booking.attraction.boosting;
//
//import com.haulmont.shamrock.booking.attraction.boosting.config.ServiceConfiguration;
//import com.haulmont.shamrock.booking.attraction.boosting.booking_persistent_service.BookingPersistentService;
//import com.haulmont.shamrock.booking.attraction.boosting.services.dto.booking_cache.Booking;
//import com.haulmont.shamrock.booking.attraction.boosting.services.dto.busy_regions_supervisor.BusyRegion;
//import com.haulmont.shamrock.booking.attraction.boosting.util.BookingUtil;
//import org.mockito.*;
//import org.slf4j.Logger;
//import org.springframework.test.util.ReflectionTestUtils;
//import org.testng.annotations.BeforeMethod;
//import org.testng.annotations.Test;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.mockito.Mockito.*;
//
//public class BoostSupervisorTest {
//
//    @Mock
//    private Logger log;
//    @Mock
//    private BoostService boostService;
//    @Mock
//    private BoostConsumptionService consumptionService;
//    @Mock
//    private BookingPersistentService bookingPersistentService;
//    @Mock
//    private BookingService bookingService;
//    @Mock
//    private ServiceConfiguration configuration;
//
//    private Supervisor supervisor;
//
//    @BeforeMethod
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//        supervisor = new Supervisor();
//        ReflectionTestUtils.setField(supervisor, "log", log);
//        ReflectionTestUtils.setField(supervisor, "boostService", boostService);
//        ReflectionTestUtils.setField(supervisor, "consumptionService", consumptionService);
//        ReflectionTestUtils.setField(supervisor, "bookingPersistentService", bookingPersistentService);
//        ReflectionTestUtils.setField(supervisor, "bookingService", bookingService);
//        ReflectionTestUtils.setField(supervisor, "configuration", configuration);
//    }
//
//    @Test
//    public void keepsCurrentLevel_whenCalcReturnsEmpty() {
//        var booking = new Booking();
//        booking.setId(UUID.randomUUID());
//        BusyRegion region = new BusyRegion();
//        Map<Booking, BusyRegion> data = new HashMap<>();
//        data.put(booking, region);
////        when(bookingService.mapBookingRegions()).thenReturn(data);
////        try (MockedStatic<BookingUtil> util = mockStatic(BookingUtil.class)) {
////            util.when(() -> BookingUtil.getBoostedVisibilityValue(
////                            booking, configuration.getBoostedVisibilityInstructionCode()))
////                    .thenReturn(1.0d);
////            when(boostService.calcBoostLevel(booking, region)).thenReturn(Optional.empty());
////
////            supervisor.execute();
////
////            verify(bookingPersistentService, never()).updateBoost(anyDouble(), any());
////            verify(consumptionService, never()).addConsumption(any(), anyDouble());
////            verify(consumptionService, never()).isExceeded(any(), anyDouble());
////        }
//    }
//
//    @Test
//    public void updatesAndConsumes_whenHigherAndNotExceeded() {
//        var booking = new Booking();
//        booking.setId(UUID.randomUUID());
//        BusyRegion region = mock(BusyRegion.class);
//        Map<Booking, BusyRegion> data = new HashMap<>();
//        data.put(booking, region);
//        when(bookingService.mapBookingRegions()).thenReturn(data);
//
//        double current = 1.5d;
//        double target = 2.0d;
//        double expectedAdded = target - current;
//
//        try (MockedStatic<BookingUtil> util = mockStatic(BookingUtil.class)) {
//            util.when(() -> BookingUtil.getBoostedVisibilityValue(
//                            booking, configuration.getBoostedVisibilityInstructionCode()))
//                    .thenReturn(current);
//
//            when(boostService.calcBoostLevel(booking, region)).thenReturn(Optional.of(target));
//            when(consumptionService.isExceeded(region, expectedAdded)).thenReturn(false);
//
//            supervisor.execute();
//
//            verify(bookingPersistentService).updateBoost(eq(target), eq(booking));
//
//            ArgumentCaptor<Double> addedCaptor = ArgumentCaptor.forClass(Double.class);
//            verify(consumptionService).addConsumption(eq(region), addedCaptor.capture());
//            org.testng.Assert.assertEquals(addedCaptor.getValue(), expectedAdded, 1e-9);
//
//            verify(consumptionService).isExceeded(eq(region), org.mockito.ArgumentMatchers.doubleThat(d ->
//                    Math.abs(d - expectedAdded) < 1e-9));
//        }
//    }
//
//    @Test
//    public void doesNotUpdate_whenExceeded() {
//        var booking = new Booking();
//        booking.setId(UUID.randomUUID());
//        BusyRegion region = new BusyRegion();
//        Map<Booking, BusyRegion> data = new HashMap<>();
//        data.put(booking, region);
//        when(bookingService.mapBookingRegions()).thenReturn(data);
//
//        double current = 1.0d;
//        double target = 3.0d;
//        double added = target - current;
//
//        try (MockedStatic<BookingUtil> util = mockStatic(BookingUtil.class)) {
//            util.when(() -> BookingUtil.getBoostedVisibilityValue(
//                            booking, configuration.getBoostedVisibilityInstructionCode()))
//                    .thenReturn(current);
//
//            when(boostService.calcBoostLevel(booking, region)).thenReturn(Optional.of(target));
//            when(consumptionService.isExceeded(region, added)).thenReturn(true);
//
//            supervisor.execute();
//
//            verify(bookingPersistentService, never()).updateBoost(anyDouble(), any());
//            verify(consumptionService, never()).addConsumption(any(), anyDouble());
//        }
//    }
//
//    @Test
//    public void doesNothing_whenTargetLessOrEqualToCurrent() {
//        var booking = new Booking();
//        booking.setId(UUID.randomUUID());
//        BusyRegion region = new BusyRegion();
//        Map<Booking, BusyRegion> data = new HashMap<>();
//        data.put(booking, region);
//        when(bookingService.mapBookingRegions()).thenReturn(data);
//
//        double current = 2.0d;
//        double target = 1.5d;
//
//        try (MockedStatic<BookingUtil> util = mockStatic(BookingUtil.class)) {
//            util.when(() -> BookingUtil.getBoostedVisibilityValue(
//                            booking, configuration.getBoostedVisibilityInstructionCode()))
//                    .thenReturn(current);
//
//            when(boostService.calcBoostLevel(booking, region)).thenReturn(Optional.of(target));
//
//            supervisor.execute();
//
//            verify(bookingPersistentService, never()).updateBoost(anyDouble(), any());
//            verify(consumptionService, never()).addConsumption(any(), anyDouble());
//        }
//    }
//
//    @Test
//    public void swallowsExceptions_inExecute() {
//        when(bookingService.mapBookingRegions()).thenThrow(new RuntimeException("boom"));
//        supervisor.execute();
//        verify(log, atLeastOnce()).warn(startsWith("Failed to execute BoostSupervisor#execute"), any(Throwable.class));
//    }
//
//}
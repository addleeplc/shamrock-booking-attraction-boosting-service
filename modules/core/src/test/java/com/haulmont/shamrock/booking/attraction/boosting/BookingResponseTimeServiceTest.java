/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting;

import com.fasterxml.jackson.core.type.TypeReference;
import com.haulmont.monaco.jackson.ObjectMapperContainer;
import com.haulmont.monaco.jackson.ObjectReaderWriterFactory;
import com.haulmont.shamrock.booking.attraction.boosting.cache.GradeCache;
import com.haulmont.shamrock.booking.attraction.boosting.config.ServiceConfiguration;
import com.haulmont.shamrock.booking.attraction.boosting.model.AttractionBoostingContext;
import com.haulmont.shamrock.booking.attraction.boosting.model.BookingResponseTime;
import com.haulmont.shamrock.booking.attraction.boosting.model.Strategy;
import com.haulmont.shamrock.booking.attraction.boosting.services.BookingCacheService;
import com.haulmont.shamrock.booking.attraction.boosting.services.dto.booking_cache.Booking;
import com.haulmont.shamrock.booking.attraction.boosting.services.dto.busy_regions_supervisor.BusyRegion;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.quality.Strictness;
import org.mockito.testng.MockitoSettings;
import org.mockito.testng.MockitoTestNGListener;
import org.slf4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Listeners({MockitoTestNGListener.class})
@MockitoSettings(strictness = Strictness.LENIENT)
public class BookingResponseTimeServiceTest {
    public static String BASE_PATH;

    //todo refactor
    static {
        String pkgPath = BookingResponseTimeServiceTest.class
                .getPackage()
                .getName()
                .replace('.', '/');

        String className = BookingResponseTimeServiceTest.class.getSimpleName();

        BASE_PATH = "/" + pkgPath + "/" + className + "/";
    }

    private ObjectMapperContainer objectMapperContainer = new ObjectMapperContainer();

    @Mock
    private ServiceConfiguration configuration;

    @Spy
    private ObjectReaderWriterFactory objectReaderWriterFactory;

    @Mock
    private Logger log;

    @Mock
    private BookingCacheService bookingCacheService;

    @Mock
    private GradeCache gradeCache;

    @InjectMocks
    private BookingResponseTimeService bookingResponseTimeService;

    @Test
    public void testRegionGroupSelection() {
        List<BusyRegion> busyRegions = loadBusyRegions("testRegionGroupSelection");
        
        Booking booking = new Booking();
        booking.setId(UUID.randomUUID());
        booking.setPaymentType("account");
        
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);
        
        for (BusyRegion busyRegion : busyRegions) {
            Mockito.when(bookingCacheService.getActiveBookings(
                    Mockito.any(DateTime.class), 
                    Mockito.eq(busyRegion.getRegionGroupId()), 
                    Mockito.anyList()))
                    .thenReturn(bookings);
        }
        
        Mockito.when(gradeCache.getGrade(booking)).thenReturn("GOLD"); // Using GOLD grade to match our test data
        
        DateTime now = new DateTime();
        Map<BusyRegion, Strategy> strategyByRegion = new HashMap<>();
        Map<BusyRegion, List<UUID>> productsByRegion = new HashMap<>();
        
        for (BusyRegion busyRegion : busyRegions) {
            productsByRegion.put(busyRegion, busyRegion.getProductIds());
        }
        
        AttractionBoostingContext context = AttractionBoostingContext.builder(
                now, busyRegions, strategyByRegion, productsByRegion).build();
        
        System.out.println("[DEBUG_LOG] Region 1 response time: " +
                busyRegions.get(0).getStatus().getState().getExtraResponseTime("account", "GOLD"));
        System.out.println("[DEBUG_LOG] Region 2 response time: " + 
                busyRegions.get(1).getStatus().getState().getExtraResponseTime("account", "GOLD"));

        Map<Booking, BookingResponseTime> result = bookingResponseTimeService.getBookingResponseTimes(context);
        
        System.out.println("[DEBUG_LOG] Result size: " + result.size());
        if (result.containsKey(booking)) {
            System.out.println("[DEBUG_LOG] Mapped region ID: " + result.get(booking).getResponseTimeSource().getRegionGroupId());
        }
        
        Assert.assertTrue(result.containsKey(booking), "Booking should be mapped to a region");
        
        Assert.assertEquals(1, result.size(), "Should have one booking mapped");
        
        BusyRegion mappedRegion = result.get(booking).getResponseTimeSource();
        Assert.assertEquals(
            UUID.fromString("44444444-4444-4444-4444-444444444444"), 
            mappedRegion.getRegionGroupId(),
            "Booking should be mapped to the region with higher response time"
        );
    }

    private List<BusyRegion> loadBusyRegions(String caseName) {
        return resource(caseName, "busy_regions", new TypeReference<>() {
        });
    }

    private <T> T resource(String caseName, String name, TypeReference<T> typeReference) {
        String path = BASE_PATH + (StringUtils.isBlank(caseName) ? "" : (caseName + "/")) + name + ".json";

        InputStream resource = BookingResponseTimeServiceTest.class.getResourceAsStream(path);
        if (resource == null) {
            throw new IllegalStateException();
        }

        try {
            return objectMapperContainer.mapper().readerFor(typeReference).readValue(resource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

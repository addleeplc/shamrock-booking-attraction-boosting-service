/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting;

import com.haulmont.shamrock.booking.attraction.boosting.model.Band;
import com.haulmont.shamrock.booking.attraction.boosting.model.BookingResponseTime;
import com.haulmont.shamrock.booking.attraction.boosting.services.dto.booking_cache.Booking;
import com.haulmont.shamrock.booking.attraction.boosting.services.dto.busy_regions_supervisor.BusyRegion;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.quality.Strictness;
import org.mockito.testng.MockitoSettings;
import org.mockito.testng.MockitoTestNGListener;
import org.slf4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Listeners({MockitoTestNGListener.class})
@MockitoSettings(strictness = Strictness.LENIENT)
public class AutoBoostCalculationServiceTest {

    @Mock
    private Logger log;

    @Mock
    private StrategyService strategyService;

    @Mock
    private CustomerGradeService customerGradeService;

    @InjectMocks
    private AutoBoostCalculationService autoBoostCalculationService;

    private Method selectBandMethod;

    @BeforeMethod
    public void setUp() throws Exception {
        // Get access to the private selectBand method using reflection
        selectBandMethod = AutoBoostCalculationService.class.getDeclaredMethod("selectBand", List.class, BookingResponseTime.class, boolean.class);
        selectBandMethod.setAccessible(true);
    }

    @Test
    public void testSelectBandWithEmptyList() throws Exception {
        // Arrange
        List<Band> bands = Collections.emptyList();
        BookingResponseTime bookingResponseTime = createBookingResponseTime(10, Duration.standardMinutes(5));

        // Act
        Optional<Band> result = (Optional<Band>) selectBandMethod.invoke(autoBoostCalculationService, bands, bookingResponseTime, false);

        // Assert
        Assert.assertTrue(result.isEmpty(), "Should return empty Optional when band list is empty");
    }

    @Test
    public void testSelectBandWithNoMatchingBands() throws Exception {
        // Arrange
        List<Band> bands = new ArrayList<>();
        bands.add(createBand(UUID.randomUUID(), Period.minutes(10), 20)); // Higher priority and response time than booking
        
        BookingResponseTime bookingResponseTime = createBookingResponseTime(10, Duration.standardMinutes(5));

        // Act
        Optional<Band> result = (Optional<Band>) selectBandMethod.invoke(autoBoostCalculationService, bands, bookingResponseTime);

        // Assert
        Assert.assertTrue(result.isEmpty(), "Should return empty Optional when no bands match criteria");
    }

    @Test
    public void testSelectBandWithMatchingBands() throws Exception {
        // Arrange
        // Create bands with different response times but same priority
        // This will help us understand how bands are sorted by response time
        UUID band1Id = UUID.randomUUID();
        UUID band2Id = UUID.randomUUID();
        UUID band3Id = UUID.randomUUID();
        
        Period lowestResponseTime = Period.minutes(3);
        Period middleResponseTime = Period.minutes(7);
        Period highestResponseTime = Period.minutes(10);
        
        int samePriority = 10;
        
        List<Band> bands = new ArrayList<>();
        Band band1 = createBand(band1Id, lowestResponseTime, samePriority);
        Band band2 = createBand(band2Id, highestResponseTime, samePriority);
        Band band3 = createBand(band3Id, middleResponseTime, samePriority);
        
        bands.add(band1);    // Lowest response time
        bands.add(band2);    // Highest response time
        bands.add(band3);    // Middle response time
        
        BookingResponseTime bookingResponseTime = createBookingResponseTime(20, Duration.standardMinutes(15));

        // Act
        Optional<Band> result = (Optional<Band>) selectBandMethod.invoke(autoBoostCalculationService, bands, bookingResponseTime);

        // Assert
        Assert.assertTrue(result.isPresent(), "Should return a band when there are matching bands");
        
        // Check which band was selected by comparing IDs
        UUID selectedBandId = result.get().getId();
        
        // After analyzing the BAND_COMPARATOR in AutoBoostCalculationService:
        // Comparator
        //         .comparingLong((Band b) -> b.getMinExtraResponseTime().toStandardSeconds().getSeconds())
        //         .reversed()
        //         .thenComparingInt(Band::getMinBookingPriority)
        //         .reversed();
        // 
        // We found that bands are sorted by:
        // 1. minExtraResponseTime in ascending order (lower values first)
        // So band1 (lowest response time) should be selected first
        Assert.assertEquals(selectedBandId, band1Id, 
                "Should select band1 with lowest response time");
    }
    
    @Test
    public void testSelectBandWithSameResponseTimeDifferentPriorities() throws Exception {
        // Arrange
        // Create bands with same response time but different priorities
        // This will help us understand how bands are sorted by priority
        UUID band1Id = UUID.randomUUID();
        UUID band2Id = UUID.randomUUID();
        UUID band3Id = UUID.randomUUID();
        
        Period sameResponseTime = Period.minutes(10);
        
        int lowestPriority = 5;
        int middlePriority = 10;
        int highestPriority = 15;
        
        List<Band> bands = new ArrayList<>();
        Band band1 = createBand(band1Id, sameResponseTime, lowestPriority);
        Band band2 = createBand(band2Id, sameResponseTime, highestPriority);
        Band band3 = createBand(band3Id, sameResponseTime, middlePriority);
        
        bands.add(band1);    // Lowest priority
        bands.add(band2);    // Highest priority
        bands.add(band3);    // Middle priority
        
        BookingResponseTime bookingResponseTime = createBookingResponseTime(20, Duration.standardMinutes(15));

        // Act
        Optional<Band> result = (Optional<Band>) selectBandMethod.invoke(autoBoostCalculationService, bands, bookingResponseTime);

        // Assert
        Assert.assertTrue(result.isPresent(), "Should return a band when there are matching bands");
        
        // Check which band was selected by comparing IDs
        UUID selectedBandId = result.get().getId();
        
        // After analyzing the BAND_COMPARATOR in AutoBoostCalculationService:
        // Comparator
        //         .comparingLong((Band b) -> b.getMinExtraResponseTime().toStandardSeconds().getSeconds())
        //         .reversed()
        //         .thenComparingInt(Band::getMinBookingPriority)
        //         .reversed();
        // 
        // We found that when response times are equal, bands are sorted by:
        // 2. minBookingPriority in descending order (higher values first)
        // So band2 (highest priority) should be selected first
        Assert.assertEquals(selectedBandId, band2Id, 
                "Should select band2 with highest priority when response times are equal");
    }

    @Test
    public void testSelectBandWithEqualResponseTimes() throws Exception {
        // Arrange
        UUID band1Id = UUID.randomUUID();
        UUID band2Id = UUID.randomUUID();
        
        List<Band> bands = new ArrayList<>();
        bands.add(createBand(band1Id, Period.minutes(5), 10)); // Same response time but lower priority
        bands.add(createBand(band2Id, Period.minutes(5), 15)); // Same response time but higher priority, should be selected
        
        BookingResponseTime bookingResponseTime = createBookingResponseTime(20, Duration.standardMinutes(10));

        // Act
        Optional<Band> result = (Optional<Band>) selectBandMethod.invoke(autoBoostCalculationService, bands, bookingResponseTime);

        // Assert
        Assert.assertTrue(result.isPresent(), "Should return a band when there are matching bands");
        Assert.assertEquals(result.get().getId(), band2Id, "Should select the band with higher priority when response times are equal");
    }

    @Test
    public void testSelectBandWithEdgeCaseExactMatch() throws Exception {
        // Arrange
        UUID bandId = UUID.randomUUID();
        
        List<Band> bands = new ArrayList<>();
        bands.add(createBand(bandId, Period.minutes(5), 10)); // Exact match with booking response time and priority
        
        BookingResponseTime bookingResponseTime = createBookingResponseTime(10, Duration.standardMinutes(5));

        // Act
        Optional<Band> result = (Optional<Band>) selectBandMethod.invoke(autoBoostCalculationService, bands, bookingResponseTime);

        // Assert
        Assert.assertTrue(result.isPresent(), "Should return a band when there's an exact match");
        Assert.assertEquals(result.get().getId(), bandId, "Should select the band with exact match");
    }

    // Helper methods to create test objects
    private Band createBand(UUID id, Period minExtraResponseTime, int minBookingPriority) {
        Band band = new Band();
        band.setId(id);
        band.setMinExtraResponseTime(minExtraResponseTime);
        band.setMinBookingPriority(minBookingPriority);
        return band;
    }

    private BookingResponseTime createBookingResponseTime(int priority, Duration responseTime) {
        Booking booking = new Booking();
        booking.setPriority(priority);
        
        BusyRegion busyRegion = new BusyRegion();
        
        return new BookingResponseTime(booking, busyRegion, responseTime);
    }
}
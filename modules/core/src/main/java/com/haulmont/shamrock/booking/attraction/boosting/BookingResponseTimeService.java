/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting;

import com.haulmont.shamrock.booking.attraction.boosting.cache.GradeCache;
import com.haulmont.shamrock.booking.attraction.boosting.model.AttractionBoostingContext;
import com.haulmont.shamrock.booking.attraction.boosting.model.BookingResponseTime;
import com.haulmont.shamrock.booking.attraction.boosting.services.BookingCacheService;
import com.haulmont.shamrock.booking.attraction.boosting.services.dto.booking_cache.Booking;
import com.haulmont.shamrock.booking.attraction.boosting.services.dto.busy_regions_supervisor.BusyRegion;
import org.joda.time.Duration;
import org.picocontainer.annotations.Component;
import org.picocontainer.annotations.Inject;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.haulmont.shamrock.booking.attraction.boosting.util.BusyRegionUtil.extractResponseTime;

@Component
public class BookingResponseTimeService {
    @Inject
    private BookingCacheService bookingCacheService;
    @Inject
    private GradeCache gradeCache;
    @Inject
    private Logger log;

    public Map<Booking, BookingResponseTime> getBookingResponseTimes(AttractionBoostingContext context) {
        Map<Booking, BookingResponseTime> res = new HashMap<>();

        for (BusyRegion busyRegion : context.getBusyRegions()) {
            List<Booking> bookings = bookingCacheService.getActiveBookings(context.getCreateTs(), busyRegion.getRegionGroupId(), context.getProducts(busyRegion));
            //todo log bookings details
            log.debug("Found {} bookings for '{}'.", bookings.size(), busyRegion);

            for (Booking booking : bookings) {
                //todo check is booking was boosted manually ignore it and log (check redis storage with decision)
                String gradeCode = gradeCache.getGrade(booking);
                Duration currentResponseTimeValue = extractResponseTime(busyRegion, booking.getPaymentType(), gradeCode);

                BookingResponseTime previousResponseTime = res.get(booking);

                if (previousResponseTime == null) {
                    res.put(booking, new BookingResponseTime(booking, busyRegion, currentResponseTimeValue));
                } else {
                    Duration previousResponseTimeValue = previousResponseTime.getResponseTime();
                    if (currentResponseTimeValue.isLongerThan(previousResponseTimeValue)) {
                        log.info("Selecting '{}' over '{}' because responseTime is higher in this region (newResponseTime={}, previousResponseTime={}).",
                                busyRegion, res.get(booking), currentResponseTimeValue, previousResponseTimeValue);
                        res.put(booking, new BookingResponseTime(booking, busyRegion, currentResponseTimeValue));
                    }
                }
            }
        }
        return res;
    }

}

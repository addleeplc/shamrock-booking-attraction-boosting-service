/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting.services;

import com.haulmont.monaco.unirest.ServiceCallUtils;
import com.haulmont.monaco.unirest.UnirestCommand;
import com.haulmont.shamrock.booking.attraction.boosting.config.ServiceConfiguration;
import com.haulmont.shamrock.booking.attraction.boosting.services.dto.booking_cache.Booking;
import com.haulmont.shamrock.booking.attraction.boosting.services.dto.booking_cache.BookingsResponse;
import com.haulmont.shamrock.booking.attraction.boosting.services.dto.booking_cache.ExecutionStatus;
import kong.unirest.GetRequest;
import kong.unirest.HttpRequest;
import org.joda.time.DateTime;
import org.picocontainer.annotations.Component;
import org.picocontainer.annotations.Inject;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.haulmont.monaco.unirest.ServiceCallUtils.call;
import static com.haulmont.monaco.unirest.ServiceCallUtils.extract;

@Component
public class BookingCacheService {
    @Inject
    private ServiceConfiguration serviceConfiguration;

    public List<Booking> getActiveBookings(DateTime now, UUID regionsGroupId, List<UUID> productIds) {
        Supplier<UnirestCommand<BookingsResponse>> bookingsResponseSupplier = () -> new LoadActiveBookingsCommand(
                regionsGroupId,
                productIds,
                now.plus(serviceConfiguration.getUpcomingBookingsLoadingPeriod()));

        Function<BookingsResponse, List<Booking>> bookingsExtractor = response -> extract(response, BookingsResponse::getBookings);

        return call(bookingsResponseSupplier, bookingsExtractor);
    }

    public static class LoadActiveBookingsCommand extends UnirestCommand<BookingsResponse> {

        private static final String SERVICE = "shamrock-booking-cache";

        private final UUID regionGroupId;
        private final List<UUID> productIds;
        private final DateTime dateTill;

        public LoadActiveBookingsCommand(UUID regionGroupId, List<UUID> productIds, DateTime dateTill) {
            super(SERVICE, BookingsResponse.class);
            this.regionGroupId = regionGroupId;
            this.productIds = productIds;
            this.dateTill = dateTill;
        }

        @Override
        protected HttpRequest<?> createRequest(String url, Path path) {
            GetRequest request = get(url, path)
                    .queryString("geo_region_group.id", regionGroupId)
                    .queryString("date_till", dateTill)
                    .queryString("execution_status", ExecutionStatus.BOOKED)
                    .queryString("execution_status", ExecutionStatus.WAITING_ALLOC)
                    .queryString("execution_status", ExecutionStatus.CONFIRMED)
                    .queryString("supplier_is_null", "")
                    .queryString("asap", false)
                    .queryString("driver_is_null", "")
                    .queryString("cancellation_date_is_null", "")
                    .queryString("prebooked_by_driver.id_is_null", "")
                    .queryString("preallocated_driver.id_is_null", "");
            for (UUID productId : productIds) {
                request = request.queryString("product.id", productId);
            }
            return request;
        }

        @Override
        protected Path getPath() {
            return new Path("bookings");
        }
    }
}

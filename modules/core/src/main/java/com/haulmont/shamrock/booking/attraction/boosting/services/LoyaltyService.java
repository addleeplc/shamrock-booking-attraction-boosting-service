/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting.services;

import com.haulmont.monaco.response.ErrorCode;
import com.haulmont.monaco.unirest.UnirestCommand;
import com.haulmont.shamrock.booking.attraction.boosting.services.dto.booking_cache.Booking;
import com.haulmont.shamrock.booking.attraction.boosting.services.dto.loyalty.PriorityGrade;
import com.haulmont.shamrock.booking.attraction.boosting.services.dto.loyalty.PriorityGradeRequest;
import com.haulmont.shamrock.booking.attraction.boosting.services.dto.loyalty.PriorityGradeResponse;
import kong.unirest.HttpRequest;
import org.picocontainer.annotations.Component;

import java.util.function.Function;

import static com.haulmont.monaco.unirest.ServiceCallUtils.call;

@Component
public class LoyaltyService {
    public PriorityGrade getPriorityGrade(Booking booking) {
        Function<PriorityGradeResponse, PriorityGrade> extractor = response -> response.getCode() == ErrorCode.NOT_FOUND.getCode()
                ? null
                : response.getGrade();

        return call(() -> new LoadPriorityGradeCommand(booking), extractor);
    }

    private static class LoadPriorityGradeCommand extends UnirestCommand<PriorityGradeResponse> {
        private final static String SERVICE = "shamrock-loyalty-service";

        private final Booking booking;

        public LoadPriorityGradeCommand(Booking booking) {
            super(SERVICE, PriorityGradeResponse.class);
            this.booking = booking;
        }

        @Override
        protected HttpRequest<?> createRequest(String url, Path path) {
            return post(url, path)
                    .header("Content-Type", "application/json")
                    .body(new PriorityGradeRequest(booking));
        }

        @Override
        protected Path getPath() {
            return new Path("bookings/priority-grade");
        }
    }
}

/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.haulmont.shamrock.booking.attraction.boosting.CustomerGradeService;
import com.haulmont.shamrock.booking.attraction.boosting.config.ServiceConfiguration;
import com.haulmont.shamrock.booking.attraction.boosting.services.dto.booking_cache.Booking;
import com.haulmont.shamrock.booking.attraction.boosting.services.dto.booking_cache.Customer;
import com.haulmont.shamrock.booking.attraction.boosting.services.dto.booking_cache.CustomerReference;
import org.picocontainer.annotations.Component;
import org.picocontainer.annotations.Inject;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Component
public class GradeCache {

    @Inject
    private CustomerGradeService customerGradeService;
    @Inject
    private ServiceConfiguration serviceConfiguration;

    private LoadingCache<GradeKey, String> cache;

    public void start() {
        cache = CacheBuilder.newBuilder()
                .maximumSize(serviceConfiguration.getGradeMaxSize())
                .expireAfterWrite(serviceConfiguration.getGradeExpirationMinutes(), TimeUnit.MINUTES)
                .build(new CacheLoader<>() {
                    @Override
                    public String load(GradeKey key) {
                        return customerGradeService.getGrade(key.convertToBooking());
                    }
                });
    }

    public String getGrade(Booking booking) {
        try {
            return cache.get(new GradeKey(booking));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public static class GradeKey {
        // For loyalty service
        private final CustomerReference customerReference;
        // For customer profile service
        private final Customer customer;

        public GradeKey(Booking booking) {
            this.customerReference = booking.getCustomerReference();
            this.customer = booking.getCustomer();
        }

        public Booking convertToBooking() {
            Booking booking = new Booking();
            booking.setCustomerReference(customerReference);
            booking.setCustomer(customer);
            return booking;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            GradeKey gradeKey = (GradeKey) o;
            return Objects.equals(customerReference, gradeKey.customerReference) && Objects.equals(customer, gradeKey.customer);
        }

        @Override
        public int hashCode() {
            return Objects.hash(customerReference, customer);
        }
    }

}

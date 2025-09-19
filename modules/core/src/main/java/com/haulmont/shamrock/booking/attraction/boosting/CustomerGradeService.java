/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting;

import com.haulmont.shamrock.booking.attraction.boosting.services.CustomerProfileService;
import com.haulmont.shamrock.booking.attraction.boosting.services.LoyaltyService;
import com.haulmont.shamrock.booking.attraction.boosting.services.dto.booking_cache.Booking;
import com.haulmont.shamrock.booking.attraction.boosting.services.dto.booking_cache.Customer;
import com.haulmont.shamrock.booking.attraction.boosting.services.dto.loyalty.PriorityGrade;
import com.haulmont.shamrock.booking.attraction.boosting.util.BookingUtil;
import org.picocontainer.annotations.Component;
import org.picocontainer.annotations.Inject;

@Component
public class CustomerGradeService {
    @Inject
    private CustomerProfileService customerProfileService;
    @Inject
    private LoyaltyService loyaltyService;

    public String getGrade(Booking booking) {
        return BookingUtil.isB2C(booking)
                ? getGradeB2C(booking.getCustomer())
                : getGradeB2B(booking);
    }

    private String getGradeB2C(Customer customer) {
        return customerProfileService.getClientAccount(extractPid(customer.getCode())).getGrade().getCode();
    }

    private String getGradeB2B(Booking booking) {
        PriorityGrade priorityGrade = loyaltyService.getPriorityGrade(booking);

        if (priorityGrade != null) {
            return priorityGrade.getCode();
        } else {
            //todo this part of logic was missing (it was in description)
            return customerProfileService.getClientAccount(booking.getCustomerReference().getClient().getPid()).getGrade().getCode();
        }
    }

    private String extractPid(String customerPid) {
        return customerPid.split("\\|")[1];
    }
}

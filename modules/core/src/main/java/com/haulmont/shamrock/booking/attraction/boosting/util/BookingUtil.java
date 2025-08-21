/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting.util;

import com.haulmont.shamrock.booking.attraction.boosting.services.dto.booking_cache.Booking;
import com.haulmont.shamrock.booking.attraction.boosting.services.dto.booking_cache.Instruction;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class BookingUtil {

    public static Double getBoostValue(Booking booking, String si) {
        List<Instruction> instructions = booking.getInstructions();

        if(CollectionUtils.isEmpty(instructions)) {
            return null;
        }

        return instructions.stream()
                .filter(it -> it.getType().equals(si))
                .findFirst()
                .map(Instruction::getValue)
                .map(Double::parseDouble)
                .orElse(null);
    }

}

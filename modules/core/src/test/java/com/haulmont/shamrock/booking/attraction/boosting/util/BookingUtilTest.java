/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting.util;

import com.haulmont.shamrock.booking.attraction.boosting.services.dto.booking_cache.Booking;
import com.haulmont.shamrock.booking.attraction.boosting.services.dto.booking_cache.Instruction;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

public class BookingUtilTest {

    @Test
    public void testGetBoostValue_present() {
        var booking = new Booking();
        booking.setInstructions(List.of(createInstruction("DRIVER_VISIBILITY_BOOST", "20"), createInstruction("VISIBILITY", "10")));
        assertEquals(BookingUtil.getBoostValue(booking, "DRIVER_VISIBILITY_BOOST"), 20.0, 1e-9);
    }

    @Test
    public void testGetBoostValue_notPresent() {
        var booking = new Booking();
        booking.setInstructions(List.of(createInstruction("VISIBILITY", "10")));
        assertNull(BookingUtil.getBoostValue(booking, "DRIVER_VISIBILITY_BOOST"));
    }

    private Instruction createInstruction(String code, String value) {
        var instruction = new Instruction();
        instruction.setType(code);
        instruction.setValue(value);
        return instruction;
    }

}
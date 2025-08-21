/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting.services.dto.booking_cache;

public enum ExecutionStatus {

    UNKNOWN(-1),
    BOOKED(0),
    WAITING_ALLOC(10),
    ALLOCATED(20),
    CONFIRMED(21),
    ON_WAY(30),
    AT_PICKUP(40),
    ON_BOARD(50),
    DONE(60),
    CANCELLED(70),
    ON_HOLD(-70);

    private final Integer code;
    public Integer getCode() {
        return code;
    }

    ExecutionStatus(Integer code) {
        this.code = code;
    }

}

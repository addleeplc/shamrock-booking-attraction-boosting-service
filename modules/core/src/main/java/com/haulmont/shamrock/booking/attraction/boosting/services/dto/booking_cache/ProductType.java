/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting.services.dto.booking_cache;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ProductType {

    PASSENGER("passenger"),
    DELIVERY("delivery");

    private final String type;

    ProductType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @JsonCreator
    public static ProductType fromString(String text) {
        if (text != null) {
            for (ProductType b : ProductType.values()) {
                if (text.equalsIgnoreCase(b.getType())) {
                    return b;
                }
            }
        }
        return null;
    }

}

/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Cap {
    @JsonProperty("value")
    private Double value;

    @JsonProperty("source_scope")
    private String sourceScope;

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getScope() {
        return sourceScope;
    }

    public void setScope(String scope) {
        this.sourceScope = scope;
    }

    @Override
    public String toString() {
        return "{" +
                "value=" + value +
                ", sourceScope='" + sourceScope + '\'' +
                '}';
    }
}

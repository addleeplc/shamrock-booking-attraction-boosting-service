/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */
package com.haulmont.shamrock.booking.attraction.boosting.services.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.haulmont.monaco.jackson.Filterable;
import com.haulmont.shamrock.booking.attraction.boosting.services.dto.booking_cache.ProductType;

import java.util.UUID;

public class Product implements Filterable {
    @JsonProperty("id")
    private UUID id;

    @JsonIgnore
    private Long pid;

    @JsonProperty("code")
    private String code;

    @JsonProperty("service_code")
    private String serviceCode;

    @JsonProperty("type")
    private ProductType type;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ProductType getType() {
        return type;
    }

    public void setType(ProductType type) {
        this.type = type;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }
}

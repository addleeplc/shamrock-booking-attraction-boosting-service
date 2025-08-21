/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting.services.dto.booking_cache;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.haulmont.monaco.jackson.annotations.SensitiveData;
import com.haulmont.monaco.jackson.maskers.NameMasker;

import java.util.Objects;

public class Customer {
    @JsonProperty("id")
    private String id;

    @JsonProperty("pid")
    private String pid;

    @JsonProperty("code")
    private String code;

    @SensitiveData(masker = NameMasker.class)
    @JsonProperty("name")
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(code, customer.code);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(code);
    }
}

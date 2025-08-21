/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

public class Bands {
    @JsonProperty("items")
    private List<Band> items;

    @JsonProperty("source_scope")
    private String sourceScope;

    public List<Band> getItems() {
        return items;
    }

    public void setItems(List<Band> items) {
        this.items = items;
    }

    public String getSourceScope() {
        return sourceScope;
    }

    public void setSourceScope(String sourceScope) {
        this.sourceScope = sourceScope;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Bands)) return false;
        Bands bands = (Bands) o;
        return Objects.equals(items, bands.items) && Objects.equals(sourceScope, bands.sourceScope);
    }

    @Override
    public int hashCode() {
        return Objects.hash(items, sourceScope);
    }
}

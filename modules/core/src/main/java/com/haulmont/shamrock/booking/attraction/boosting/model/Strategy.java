/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Objects;
import java.util.UUID;

public class Strategy {
    @JsonProperty("scope")
    private String scope;
    @JsonProperty("strategy_type")
    private StrategyType type;
    @JsonProperty("enabled")
    private boolean enabled = true;
    @JsonProperty("caps")
    private Caps caps;
    @JsonProperty("bands")
    private Bands bands;

    @JsonIgnore
    private UUID regionGroupId;
    @JsonIgnore
    private UUID productGroupId;


    public StrategyType getType() {
        return type;
    }

    public void setType(StrategyType type) {
        this.type = type;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isDisabled() {
        return !enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Caps getCaps() {
        return caps;
    }

    public void setCaps(Caps caps) {
        this.caps = caps;
    }

    public Bands getBands() {
        return bands;
    }

    public void setBands(Bands bands) {
        this.bands = bands;
    }

    @JsonIgnore
    public UUID getRegionGroupId() {
        return regionGroupId;
    }

    public void setRegionGroupId(UUID regionGroupId) {
        this.regionGroupId = regionGroupId;
    }

    @JsonIgnore
    public UUID getProductGroupId() {
        return productGroupId;
    }

    public void setProductGroupId(UUID productGroupId) {
        this.productGroupId = productGroupId;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public boolean hasBands() {
        return bands != null && CollectionUtils.isNotEmpty(bands.getItems());
    }

    @Override
    public String toString() {
        return String.format("Strategy(%s, %s, %s)", scope, type, enabled);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Strategy strategy = (Strategy) o;
        return Objects.equals(regionGroupId, strategy.regionGroupId) && Objects.equals(productGroupId, strategy.productGroupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(regionGroupId, productGroupId);
    }
}

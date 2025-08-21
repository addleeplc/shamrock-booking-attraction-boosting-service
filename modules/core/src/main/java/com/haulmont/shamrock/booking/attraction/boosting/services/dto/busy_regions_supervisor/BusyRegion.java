/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting.services.dto.busy_regions_supervisor;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.UUID;

public class BusyRegion {

    @JsonProperty("region_group_id")
    private UUID regionGroupId;
    @JsonProperty("product_group_id")
    private UUID productGroupId;
    @JsonProperty("status")
    private RegionStatus status;
    @JsonProperty("product_ids")
    private List<UUID> productIds;

    public UUID getRegionGroupId() {
        return regionGroupId;
    }

    public void setRegionGroupId(UUID regionGroupId) {
        this.regionGroupId = regionGroupId;
    }

    public UUID getProductGroupId() {
        return productGroupId;
    }

    public void setProductGroupId(UUID productGroupId) {
        this.productGroupId = productGroupId;
    }

    public RegionStatus getStatus() {
        return status;
    }

    public void setStatus(RegionStatus status) {
        this.status = status;
    }

    public List<UUID> getProductIds() {
        return productIds;
    }
    public void setProductIds(List<UUID> productIds) {
        this.productIds = productIds;
    }

    @Override
    public String toString() {
        return "(RG|" + regionGroupId + ".PG|" + productGroupId + ")";
    }

}

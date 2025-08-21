/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting.services.dto.busy_regions_supervisor;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.haulmont.monaco.response.Response;

import java.util.List;

public class BusyRegionsResponse extends Response {

    @JsonProperty("busy_regions")
    private List<BusyRegion> busyRegions;

    public List<BusyRegion> getBusyRegions() {
        return busyRegions;
    }

    public void setBusyRegions(List<BusyRegion> busyRegions) {
        this.busyRegions = busyRegions;
    }
}

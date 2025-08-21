/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting.services.dto.busy_regions_supervisor;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RegionStatus {
    @JsonProperty("code")
    private Code code;
    @JsonProperty("state")
    private RegionState state;

    public Code getCode() {
        return code;
    }

    public void setCode(Code code) {
        this.code = code;
    }

    public RegionState getState() {
        return state;
    }

    public void setState(RegionState state) {
        this.state = state;
    }

    public enum Code {
        BUSY,
        NOT_BUSY
    }

}

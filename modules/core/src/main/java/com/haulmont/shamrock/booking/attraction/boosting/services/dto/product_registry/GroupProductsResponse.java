/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting.services.dto.product_registry;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.haulmont.monaco.response.Response;

public class GroupProductsResponse extends Response {

    @JsonProperty("group")
    private Group group;


    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }
}

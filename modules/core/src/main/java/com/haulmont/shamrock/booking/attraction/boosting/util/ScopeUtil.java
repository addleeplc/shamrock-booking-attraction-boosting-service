/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting.util;

import com.haulmont.shamrock.booking.attraction.boosting.services.dto.busy_regions_supervisor.BusyRegion;

public class ScopeUtil {
    public final static String DEFAULT_SCOPE = "default";
    public final static String PRODUCT_SCOPE_FORMAT = "PG|%s";
    public final static String REGION_SCOPE_FORMAT = "RG|%s";
    public final static String SCOPE_PART_DELIMITER  = ".";
    public final static String REGION_PRODUCT_GROUPS_SCOPE_FORMAT = REGION_SCOPE_FORMAT + SCOPE_PART_DELIMITER + PRODUCT_SCOPE_FORMAT;

    public static String regionProduct(String regionCode, String productGroupCode) {
        return String.format(REGION_PRODUCT_GROUPS_SCOPE_FORMAT, regionCode, productGroupCode);
    }

    public static String region(String regionCode) {
        return String.format(REGION_SCOPE_FORMAT, regionCode);
    }

    public static String product(String productGroupCode) {
        return String.format(PRODUCT_SCOPE_FORMAT, productGroupCode);
    }

    public static String regionProduct(BusyRegion busyRegion) {
        return regionProduct(busyRegion.getRegionGroupId().toString(), busyRegion.getProductGroupId().toString());
    }

    public static String region(BusyRegion busyRegion) {
        return region(busyRegion.getRegionGroupId().toString());
    }

    public static String product(BusyRegion busyRegion) {
        return product(busyRegion.getProductGroupId().toString());
    }
}

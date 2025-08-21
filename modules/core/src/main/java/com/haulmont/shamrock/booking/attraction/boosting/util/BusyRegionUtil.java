/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting.util;

import com.haulmont.shamrock.booking.attraction.boosting.services.dto.busy_regions_supervisor.BusyRegion;
import org.joda.time.Duration;

public class BusyRegionUtil {

    public static Duration extractResponseTime(BusyRegion busyRegion, String paymentType, String gradeCode) {
        return busyRegion.getStatus().getState().getExtraResponseTime(paymentType, gradeCode).toStandardDuration();
    }

}

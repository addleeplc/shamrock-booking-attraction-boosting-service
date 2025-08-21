/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateTimeUtil {
    public static final DateTimeFormatter DAILY_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd");
    public static final DateTimeFormatter WEEKLY_FORMATTER = DateTimeFormat.forPattern("YYYY-'W'ww");
    public static final DateTimeFormatter MONTHLY_FORMATTER = DateTimeFormat.forPattern("yyyy-MM");

    public static DateTime startOfWeek() {
        return DateTime.now()
                .withDayOfWeek(DateTimeConstants.MONDAY)
                .withTimeAtStartOfDay();
    }

    public static DateTime startOfMonth() {
        return DateTime.now()
                .withDayOfMonth(1)
                .withTimeAtStartOfDay();
    }

    public static DateTime startOfDay() {
        return DateTime.now().withTimeAtStartOfDay();
    }

}

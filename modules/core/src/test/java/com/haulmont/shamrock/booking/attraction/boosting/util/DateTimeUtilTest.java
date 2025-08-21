/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class DateTimeUtilTest {

    @DataProvider(name = "weekCases")
    public Object[][] weekCases() {
        return new Object[][]{
                {dt(2025, 8, 25, 13, 45, 0), dt(2025, 8, 25, 0, 0, 0)},
                {dt(2025, 1, 1, 12, 0, 0), dt(2024, 12, 30, 0, 0, 0)},
        };
    }

    @Test(dataProvider = "weekCases")
    public void testStartOfWeek(DateTime now, DateTime expected) {
        freeze(now);
        DateTime actual = DateTimeUtil.startOfWeek();

        assertEquals(actual, expected, "startOfWeek instant mismatch");
        assertEquals(actual.getDayOfWeek(), DateTimeConstants.MONDAY);
        assertEquals(actual.getHourOfDay(), 0);
        assertEquals(actual.getMinuteOfHour(), 0);
        assertEquals(actual.getSecondOfMinute(), 0);
        assertEquals(actual.getMillisOfSecond(), 0);
    }

    @DataProvider(name = "monthCases")
    public Object[][] monthCases() {
        return new Object[][]{
                {dt(2025, 8, 28, 23, 11, 0), dt(2025, 8, 1, 0, 0, 0)},
                {dt(2025, 3, 1, 8, 0, 0),   dt(2025, 3, 1, 0, 0, 0)},
        };
    }

    @Test(dataProvider = "monthCases")
    public void testStartOfMonth(DateTime now, DateTime expected) {
        freeze(now);
        DateTime actual = DateTimeUtil.startOfMonth();

        assertEquals(actual, expected, "startOfMonth instant mismatch");
        assertEquals(actual.getDayOfMonth(), 1);
        assertEquals(actual.getHourOfDay(), 0);
        assertEquals(actual.getMinuteOfHour(), 0);
        assertEquals(actual.getSecondOfMinute(), 0);
        assertEquals(actual.getMillisOfSecond(), 0);
    }

    @DataProvider(name = "dayCases")
    public Object[][] dayCases() {
        return new Object[][]{
                {dt(2025, 8, 28, 23, 11, 7), dt(2025, 8, 28, 0, 0, 0)},
                {dt(2025, 8, 29, 0, 0, 1),   dt(2025, 8, 29, 0, 0, 0)},
        };
    }

    @Test(dataProvider = "dayCases")
    public void testStartOfDay(DateTime now, DateTime expected) {
        freeze(now);
        DateTime actual = DateTimeUtil.startOfDay();

        assertEquals(actual, expected, "startOfDay instant mismatch");
        assertEquals(actual.getHourOfDay(), 0);
        assertEquals(actual.getMinuteOfHour(), 0);
        assertEquals(actual.getSecondOfMinute(), 0);
        assertEquals(actual.getMillisOfSecond(), 0);
    }

    private static DateTime dt(int y, int m, int d, int h, int min, int s) {
        return new DateTime(y, m, d, h, min, s, 0);
    }

    private static void freeze(DateTime now) {
        DateTimeUtils.setCurrentMillisFixed(now.getMillis());
    }

}
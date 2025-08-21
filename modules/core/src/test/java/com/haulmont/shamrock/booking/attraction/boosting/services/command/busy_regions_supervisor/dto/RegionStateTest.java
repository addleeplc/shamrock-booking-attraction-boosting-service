/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting.services.command.busy_regions_supervisor.dto;

import com.haulmont.shamrock.booking.attraction.boosting.services.dto.busy_regions_supervisor.Availability;
import com.haulmont.shamrock.booking.attraction.boosting.services.dto.busy_regions_supervisor.Grade;
import com.haulmont.shamrock.booking.attraction.boosting.services.dto.busy_regions_supervisor.RegionState;
import org.joda.time.Period;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.testng.Assert.assertEquals;

public class RegionStateTest {


    private static Availability availability(Boolean fullyBooked, Period extraResponse) {
        Availability a = new Availability();
        a.setFullyBooked(fullyBooked);
        a.setExtraResponseTime(extraResponse);
        return a;
    }

    private static Grade gradeAvailability(String code, boolean fullyBooked, Period extraResponse) {
        Grade g = new Grade();
        g.setGradeCode(code);
        g.setFullyBooked(fullyBooked);
        g.setExtraResponseTime(extraResponse);
        return g;
    }


    @Test
    public void fallsBackToCashWhenNoGradeAndPaymentTypeCash() {
        RegionState rs = new RegionState();
        rs.setAccount(availability(false, Period.minutes(3)));
        rs.setCash(availability(true, Period.seconds(90)));
        rs.setPriorityGrades(Collections.emptyList());

        var result = rs.getExtraResponseTime("cash", "UNKNOWN");
        assertEquals(result, Period.seconds(90));
    }

    @Test
    public void fallsBackToAccountWhenNoGradeAndPaymentTypeNotCash() {
        RegionState rs = new RegionState();
        rs.setAccount(availability(false, Period.seconds(75)));
        rs.setCash(availability(true, Period.minutes(2)));
        rs.setPriorityGrades(null); // проверяем null-ветку

        var result = rs.getExtraResponseTime("invoice", "UNKNOWN");
        assertEquals(result, Period.seconds(75));
    }

    @Test
    public void paymentTypeComparisonIsCaseSensitive_cashUppercaseGoesToCash() {
        RegionState rs = new RegionState();
        rs.setAccount(availability(false, Period.seconds(33)));
        rs.setCash(availability(true, Period.seconds(44)));
        rs.setPriorityGrades(Collections.emptyList());

        var result = rs.getExtraResponseTime("CASH", "UNKNOWN");
        assertEquals(result, Period.seconds(44));
    }

    @Test
    public void gradeNotFoundFallsBackCorrectly_evenIfGradesPresent() {
        RegionState rs = new RegionState();
        rs.setAccount(availability(false, Period.seconds(13)));
        rs.setCash(availability(true, Period.seconds(21)));
        rs.setPriorityGrades(Arrays.asList(
                gradeAvailability("VIP", false, Period.seconds(40)),
                gradeAvailability("STD", false, Period.seconds(15))
        ));

        var res1 = rs.getExtraResponseTime("cash", "UNKNOWN");
        var res2 = rs.getExtraResponseTime("account", "UNKNOWN");

        assertEquals(res1, Period.seconds(21));
        assertEquals(res2, Period.seconds(13));
    }

    @Test
    public void picksFromPriorityGrades_evenWhenPaymentTypeCash() {
        // arrange
        RegionState rs = new RegionState();
        Availability acc = new Availability(); acc.setExtraResponseTime(Period.minutes(5));
        Availability cash = new Availability(); cash.setExtraResponseTime(Period.minutes(2));
        rs.setAccount(acc);
        rs.setCash(cash);

        Grade g1 = new Grade(); g1.setGradeCode("STD"); g1.setExtraResponseTime(Period.seconds(30));
        Grade g2 = new Grade(); g2.setGradeCode("VIP"); g2.setExtraResponseTime(Period.seconds(45));
        rs.setPriorityGrades(Arrays.asList(g1, g2));

        Period result = rs.getExtraResponseTime("cash", "VIP");

        // assert
        assertEquals(result, Period.seconds(45));
        assertEquals(result, g2.getExtraResponseTime());
    }

}
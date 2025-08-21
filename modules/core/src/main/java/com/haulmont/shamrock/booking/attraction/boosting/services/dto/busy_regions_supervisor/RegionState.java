/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting.services.dto.busy_regions_supervisor;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.Period;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class RegionState {

    @JsonProperty("account")
    private Availability account;
    @JsonProperty("cash")
    private Availability cash;
    @JsonProperty("priority_grades")
    private List<Grade> priorityGrades;

    public Period getExtraResponseTime(String paymentType, String gradeCode) {
        return Optional.ofNullable(priorityGrades)
                .orElse(Collections.emptyList())
                .stream()
                .filter(it -> StringUtils.equalsIgnoreCase(it.getGradeCode(), gradeCode))
                .map(Availability::getExtraResponseTime)
                .findFirst()
                .orElseGet(() ->
                        StringUtils.equalsIgnoreCase(paymentType, "cash")
                                ? cash.getExtraResponseTime()
                                : account.getExtraResponseTime());
    }


    public Availability getAccount() {
        return account;
    }

    public void setAccount(Availability account) {
        this.account = account;
    }

    public Availability getCash() {
        return cash;
    }

    public void setCash(Availability cash) {
        this.cash = cash;
    }

    public List<Grade> getPriorityGrades() {
        return priorityGrades;
    }

    public void setPriorityGrades(List<Grade> priorityGrades) {
        this.priorityGrades = priorityGrades;
    }
}

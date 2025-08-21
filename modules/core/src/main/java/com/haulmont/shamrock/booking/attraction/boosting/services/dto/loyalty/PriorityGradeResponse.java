/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting.services.dto.loyalty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.haulmont.monaco.response.ErrorCode;
import com.haulmont.monaco.response.Response;

public class PriorityGradeResponse extends Response {

    @JsonProperty("grade")
    private PriorityGrade grade;

    public PriorityGradeResponse() {
        super(ErrorCode.OK);
    }

    public PriorityGrade getGrade() {
        return grade;
    }

    public void setGrade(PriorityGrade grade) {
        this.grade = grade;
    }
}

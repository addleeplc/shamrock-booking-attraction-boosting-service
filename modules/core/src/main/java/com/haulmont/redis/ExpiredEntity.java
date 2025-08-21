/*
 * Copyright 2008 - 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.redis;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.haulmont.bali.jackson.joda.DateTimeAdapter;
import org.joda.time.DateTime;

public class ExpiredEntity {

    @JsonSerialize(using = DateTimeAdapter.Serializer.class)
    @JsonDeserialize(using = DateTimeAdapter.Deserializer.class)
    @JsonProperty("expire_at")
    private DateTime expireAt;

    public DateTime getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(DateTime expireAt) {
        this.expireAt = expireAt;
    }
}

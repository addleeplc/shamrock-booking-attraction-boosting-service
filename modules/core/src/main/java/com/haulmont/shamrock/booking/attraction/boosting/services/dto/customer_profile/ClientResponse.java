/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */
package com.haulmont.shamrock.booking.attraction.boosting.services.dto.customer_profile;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.haulmont.monaco.response.Response;

public class ClientResponse extends Response {
    @JsonProperty("account")
    private Account account;

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

}

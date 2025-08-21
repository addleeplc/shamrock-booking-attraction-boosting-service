/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting.services;

import com.haulmont.monaco.response.ErrorCode;
import com.haulmont.monaco.unirest.UnirestCommand;
import com.haulmont.shamrock.booking.attraction.boosting.services.dto.customer_profile.Account;
import com.haulmont.shamrock.booking.attraction.boosting.services.dto.customer_profile.ClientResponse;
import kong.unirest.HttpRequest;
import org.picocontainer.annotations.Component;

import java.util.Collections;
import java.util.function.Function;

import static com.haulmont.monaco.unirest.ServiceCallUtils.call;

@Component
public class CustomerProfileService {
    public Account getClientAccount(String clientPid) {
        Function<ClientResponse, Account> extractor = clientResponse -> clientResponse.getCode() == ErrorCode.NOT_FOUND.getCode()
                ? null
                : clientResponse.getAccount();

        return call(() -> new LoadClientCommand(clientPid), extractor);
    }

    private static class LoadClientCommand extends UnirestCommand<ClientResponse> {
        private static final String SERVICE = "shamrock-customer-profile-service";

        private final String clientPid;

        public LoadClientCommand(String clientPid) {
            super(SERVICE, ClientResponse.class);
            this.clientPid = clientPid;
        }

        @Override
        protected HttpRequest<?> createRequest(String url, Path path) {
            return get(url, path);
        }

        @Override
        protected Path getPath() {
            return new Path("clients/{customer_pid}", Collections.singletonMap("customer_pid", clientPid));
        }
    }
}

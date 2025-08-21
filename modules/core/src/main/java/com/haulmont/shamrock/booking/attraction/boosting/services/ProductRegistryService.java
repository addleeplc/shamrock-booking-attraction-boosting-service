/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting.services;

import com.haulmont.monaco.unirest.ServiceCallUtils;
import com.haulmont.monaco.unirest.UnirestCommand;
import com.haulmont.shamrock.booking.attraction.boosting.services.dto.Product;
import com.haulmont.shamrock.booking.attraction.boosting.services.dto.product_registry.GroupProductsResponse;
import kong.unirest.HttpRequest;
import org.picocontainer.annotations.Component;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Component
public class ProductRegistryService {
    public Map<UUID, List<UUID>> getProductsIdsForGroupsIds(Set<UUID> groupIds) {
        return groupIds.stream()
                .collect(Collectors.toMap(groupId -> groupId, this::getProductIdsForGroupId));
    }

    public List<UUID> getProductIdsForGroupId(UUID groupId) {
        return ServiceCallUtils.call(
                () -> new LoadGroupProductsCommand(groupId),
                response -> ServiceCallUtils.extract(
                        response, o -> o.getGroup().getProducts().stream().map(Product::getId).collect(toList()))
        );
    }

    private static class LoadGroupProductsCommand extends UnirestCommand<GroupProductsResponse> {
        private static final String SERVICE = "shamrock-product-registry-service";

        private final UUID groupId;

        public LoadGroupProductsCommand(UUID groupId) {
            super(SERVICE, GroupProductsResponse.class);
            this.groupId = groupId;
        }

        @Override
        protected HttpRequest<?> createRequest(String url, Path path) {
            return get(url, path);
        }

        @Override
        protected Path getPath() {
            return new Path("groups/{group_id}", Collections.singletonMap("group_id", groupId));
        }
    }
}

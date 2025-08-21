/*
 * Copyright 2008 - 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.redis;

import com.google.common.base.Objects;
import com.haulmont.redis.key.PairKey;
import com.haulmont.redis.key.SingleKey;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class Key<T> {

    private final List<Optional<T>> parameters;

    protected Key(List<Optional<T>> parameters){
        this.parameters = parameters;
    }

    public static final String PLACEHOLDER_CHAR = "*";

    /**
     * get list of parameters. {@link Key#PLACEHOLDER_CHAR} will be return for parameter if we need fetch all possible
     * values for this position.
     */
    public List<String> getParameter(){
        return parameters.stream()
                .map(it -> it.isPresent()? it.get().toString() : PLACEHOLDER_CHAR)
                .collect(Collectors.toList());
    }

    public Integer getCount(){
        return parameters.size();
    }

    public Boolean isPattern(){
        return parameters
                .stream()
                .anyMatch(Optional::isEmpty);
    }

    public Boolean isKey(){
        return parameters
                .stream()
                .noneMatch(Optional::isEmpty);
    }

    public static Key<UUID> fromString(String key){
        String[] parts = key.split(":");

        if (parts.length == 3){
            return SingleKey.key(UUID.fromString(parts[2]));
        }

        if (parts.length == 6){
            return PairKey.key(UUID.fromString(parts[3]), UUID.fromString(parts[5]));
        }

        throw new IllegalArgumentException();
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Key<?> key = (Key<?>) o;
        return Objects.equal(parameters, key.parameters);
    }

    @Override
    public int hashCode () {
        return Objects.hashCode(parameters);
    }
}

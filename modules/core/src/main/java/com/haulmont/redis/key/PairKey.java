/*
 * Copyright 2008 - 2022 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.redis.key;

import com.haulmont.redis.Key;

import java.util.List;
import java.util.Optional;

/**
 * Represent key with two parameter.
 *
 * @param <T> type of parameters
 */
public class PairKey<T> extends Key<T> {

    private PairKey(List<Optional<T>> parameters) {
        super(parameters);
    }

    /**
     * create key with two parameter
     */
    public static <T> PairKey<T> key(T first, T second) {
        return new <T>PairKey<T>(List.of(Optional.of(first), Optional.of(second)));
    }

    /**
     * Define pattern for wildcard search by second parameter.
     *
     * @param value value of second parameter
     * @param <T>   type of entity stored in redis
     * @return pattern
     */
    public static <T> PairKey<T> keyCodecLeft(T value) {
        return new <T>PairKey<T>(List.of(Optional.empty(), Optional.of(value)));
    }

    /**
     * Define pattern for wildcard search by first parameter.
     *
     * @param value value of first parameter
     * @param <T>   type of entity stored in redis
     * @return pattern
     */
    public static <T> PairKey<T> keyCodecRight(T value) {
        return new <T>PairKey<T>(List.of(Optional.of(value), Optional.empty()));
    }

}

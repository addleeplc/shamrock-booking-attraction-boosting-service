/*
 * Copyright 2008 - 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.redis.key;

import com.haulmont.redis.Key;

import java.util.List;
import java.util.Optional;

/**
 * Represent key with one parameter
 * @param <T> type of parameter
 */
public class SingleKey<T> extends Key<T> {

    private SingleKey (List<Optional<T>> parameters) {
        super(parameters);
    }

    public static <T> SingleKey<T> key(T value) {
        return new SingleKey<>(List.of(Optional.of(value)));
    }

}

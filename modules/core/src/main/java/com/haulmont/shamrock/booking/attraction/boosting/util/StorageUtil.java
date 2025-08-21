/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting.util;

import com.haulmont.redis.key.TriKey;

public class StorageUtil {

    public static <F,S,T> TriKey<String> keyNullable(F f, S s, T t) {
        return TriKey.key(toStringOrEmpty(f), toStringOrEmpty(s), t.toString());
    }

    private static <T> String toStringOrEmpty(T val) {
        return val == null
                ? ""
                : val.toString();
    }

}

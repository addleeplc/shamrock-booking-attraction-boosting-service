/*
 * Copyright 2008 - 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.redis;

public interface KeyCodec<KeyType> {

    String encode(Key<KeyType> key);

    String keyPattern();

}

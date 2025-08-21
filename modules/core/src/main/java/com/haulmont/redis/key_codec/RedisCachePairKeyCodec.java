/*
 * Copyright 2008 - 2022 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.redis.key_codec;

import com.haulmont.redis.RedisCacheKeyCodec;

/**
 * Define the following key codec with two key parameter:
 * <pre>
 *     globalPrefix:firstPrefix:firstParameter:secondPrefix:secondParameter
 * </pre>
 * @param <KeyType> type of key parameter
 */
public class RedisCachePairKeyCodec<KeyType> extends RedisCacheKeyCodec<KeyType> {
    public RedisCachePairKeyCodec(String globalPrefix) {
        super(globalPrefix
                + GROUP_DELIMITER
                + PARAMETER_PLACEHOLDER
                + GROUP_DELIMITER
                + PARAMETER_PLACEHOLDER);
    }

}

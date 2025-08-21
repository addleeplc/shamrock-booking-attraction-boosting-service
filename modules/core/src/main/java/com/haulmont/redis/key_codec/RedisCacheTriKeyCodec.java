/*
 * Copyright 2008 - 2022 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.redis.key_codec;

import com.haulmont.redis.RedisCacheKeyCodec;

/**
 * Define the following key codec with three key parameter:
 * <pre>
 *     globalPrefix:firstPrefix:firstParameter:secondPrefix:secondParameter:thirdPrefix:thirdParameter
 * </pre>
 * @param <KeyType> type of key parameter
 */
public class RedisCacheTriKeyCodec<KeyType> extends RedisCacheKeyCodec<KeyType> {


    public RedisCacheTriKeyCodec(String globalPrefix, String firstPrefix, String secondPrefix, String thirdPrefix) {
        super(globalPrefix
                + GROUP_DELIMITER
                + firstPrefix
                + GROUP_DELIMITER
                + PARAMETER_PLACEHOLDER
                + GROUP_DELIMITER
                + secondPrefix
                + GROUP_DELIMITER
                + PARAMETER_PLACEHOLDER
                + GROUP_DELIMITER
                + thirdPrefix
                + GROUP_DELIMITER
                + PARAMETER_PLACEHOLDER );
    }

}

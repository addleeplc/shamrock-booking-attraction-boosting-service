/*
 * Copyright 2008 - 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.redis;

import org.apache.commons.lang3.StringUtils;

public class RedisCacheKeyCodec <KeyType> implements KeyCodec<KeyType>{


    /**
     * {@link String#format(String, Object...)} used for formatting key codec so use {@link Formatter} placeholder
     * format for strings
     */
    protected static final String PARAMETER_PLACEHOLDER = "%s";
    protected static final String GROUP_DELIMITER = ":";

    protected final String format;

    /**
     * it's supposed to this constructor should be used only in inherited classes that define more suitable api for
     * each specific case
     */
    protected RedisCacheKeyCodec(String format) {
        if (format.endsWith(":")){
            this.format = StringUtils.chop(format);
        } else {
            this.format = format;
        }
    }

    @Override
    public String encode(Key<KeyType> key) {
        if (key.getCount() != StringUtils.countMatches(format, "%s")) {
            throw new IllegalArgumentException("passed key with different count of parameters that defined in key codec");
        }

        var keyString = String.format(format, key.getParameter().toArray());
        if (keyString.endsWith(":")) keyString = StringUtils.chop(keyString);
        return keyString;
    }

    /**
     * get pattern that return all stored entity for giving key codec.
     */
    @Override
    public String keyPattern() {
        return format.replace(PARAMETER_PLACEHOLDER, Key.PLACEHOLDER_CHAR);
    }

}

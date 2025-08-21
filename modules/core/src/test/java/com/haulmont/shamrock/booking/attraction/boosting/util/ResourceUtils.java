/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ResourceUtils {

    public static String readResourceAsString(String resourcePath) {
        try {
            URL url = Resources.getResource(resourcePath); // ищет в classpath
            return Resources.toString(url, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read resource: " + resourcePath, e);
        }
    }

    public static <T> T readResourceAsJson(String resourcePath, TypeReference<T> typeRef) {
        try {
            return new ObjectMapper().readValue(readResourceAsString(resourcePath), typeRef);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}

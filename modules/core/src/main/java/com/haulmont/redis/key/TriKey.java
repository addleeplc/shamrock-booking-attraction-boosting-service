package com.haulmont.redis.key;

import com.haulmont.redis.Key;

import java.util.List;
import java.util.Optional;

/**
 * Represent key with three parameter.
 *
 * @param <T> type of parameters
 */
public class TriKey<T> extends Key<T> {
    protected TriKey(List<Optional<T>> parameters) {
        super(parameters);
    }

    /**
     * create key with three parameter
     */
    public static <T> TriKey<T> key(T first, T second, T third) {
        return new <T>TriKey<T>(List.of(Optional.of(first), Optional.of(second), Optional.of(third)));
    }

    /**
     * Define pattern for wildcard search by third parameter.
     *
     * @param value value of second parameter
     * @param <T>   type of entity stored in redis
     * @return pattern
     */
    public static <T> TriKey<T> keyCodecLeft(T value) {
        return new <T>TriKey<T>(List.of(Optional.empty(), Optional.empty(), Optional.of(value)));
    }

    /**
     * Define pattern for wildcard search by second parameter.
     *
     * @param value value of first parameter
     * @param <T>   type of entity stored in redis
     * @return pattern
     */
    public static <T> TriKey<T> keyCodecMiddle(T value) {
        return new <T>TriKey<T>(List.of(Optional.empty(), Optional.of(value), Optional.empty()));
    }

    /**
     * Define pattern for wildcard search by first parameter.
     *
     * @param value value of first parameter
     * @param <T>   type of entity stored in redis
     * @return pattern
     */
    public static <T> TriKey<T> keyCodecRight(T value) {
        return new <T>TriKey<T>(List.of(Optional.of(value), Optional.empty(), Optional.empty()));
    }

}

/*
 * Copyright 2008 - 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.redis;

import com.haulmont.monaco.AppContext;
import com.haulmont.monaco.redis.Redis;
import com.haulmont.monaco.redis.cache.RedisCacheObjectCodec;
import com.haulmont.monaco.redis.cache.codec.JacksonObjectCodec;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.picocontainer.annotations.Inject;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RedisStorageBase<KeyType, ValueType, K extends Key<KeyType>> {

    @Inject
    protected Logger log;

    private Redis<String, String> redis;

    protected final RedisCacheObjectCodec<ValueType> valueCodec;
    protected final KeyCodec<KeyType> keyCodec;

    private final Class<ValueType> valueClass;

    protected final Supplier<Long> ttlProvider;

    private final Supplier<String> redisResourceProvider;

    public RedisStorageBase(Class<ValueType> valueClass, KeyCodec<KeyType> keyCodec, Supplier<Long> ttlProvider, Supplier<String> redisResourceProvider) {
        this.valueClass = valueClass;
        this.keyCodec = keyCodec;
        valueCodec = new JacksonObjectCodec<>(valueClass);

        this.ttlProvider = ttlProvider;
        this.redisResourceProvider = redisResourceProvider;
    }

    public Optional<ValueType> get(K key) {
        if (key.isPattern()) throw new IllegalArgumentException("pattern isn't allowed in this method");

        log.debug("fetch {} from redis by key {}", valueClass.getSimpleName(), keyCodec.encode(key));
        return Optional.ofNullable(getRedis().get(keyCodec.encode(key)))
                .map(valueCodec::decode);
    }

    public List<ValueType> getAll(K key) {
        return getByPattern(keyCodec.encode(key)).collect(Collectors.toList());
    }

    public List<ValueType> getAll() {
        return getByPattern(keyCodec.keyPattern()).collect(Collectors.toList());
    }

    /**
     * It's allowed to optimize the count of redis operation by apply to the stream intermediate operation that filters
     * out stream elements
     */
    public Stream<ValueType> getAllStream(K key){
        return getByPattern(keyCodec.encode(key));
    }

    private Stream<ValueType> getByPattern(String keyPattern) {
        return getRedis()
                .keys(keyPattern)
                .stream()
                .map(getRedis()::get)
                .filter(Objects::nonNull)
                .filter(StringUtils::isNotBlank)
                .map(valueCodec::decode);
    }

    public void save(K key, ValueType valueType, Long ttl) {
        if (key.isPattern()) throw new IllegalArgumentException("pattern isn't allowed in this method");

        getRedis().setex(keyCodec.encode(key), ttl, valueCodec.encode(valueType));
        log.debug("redis save {} with key {}}", valueClass.getSimpleName(), keyCodec.encode(key));
    }

    public void save(K key, ValueType valueType){
        save(key, valueType, ttlProvider.get());
    }

    public void delete(K key) {
        if (key.isPattern()) throw new IllegalArgumentException("pattern isn't allowed in this method");

        delete(keyCodec.encode(key));
    }

    public void deleteAll(K key) {
        deleteByPattern(keyCodec.encode(key));
    }

    private void delete(String key) {
        Long deletedCount = getRedis().del(key);

        if (deletedCount == null || deletedCount <= 0) return;

        log.debug("{} for key {} has been removed from redis", valueClass.getSimpleName(), key);
    }

    private void deleteByPattern(String keyPattern) {
        getRedis()
                .keys(keyPattern)
                .forEach(this::delete);
    }

    public void expire(K key, Long ttl) {
        Boolean expired = getRedis().expire(keyCodec.encode(key), ttl);

        if (BooleanUtils.isTrue(expired)) {
            log.debug("Key {} has been scheduled for removal, ttl: {}", key, ttl);
        }
    }

    public List<K> keys(K key){
        //noinspection unchecked
        return Optional.ofNullable(getRedis().keys(keyCodec.encode(key)))
                .orElse(Collections.emptyList())
                .stream()
                .map(it -> (K)Key.fromString(it))
                .collect(Collectors.toList());
    }

    public Boolean exist(K key){
        return getRedis().exists(keyCodec.encode(key));
    }

    public Boolean notExist(K key){
        return !exist(key);
    }


    public Redis<String, String> getRedis() {
        if (redis == null) {
            //noinspection unchecked
            redis = AppContext.getResources().get(redisResourceProvider.get(), Redis.class);
        }

        return redis;
    }

}

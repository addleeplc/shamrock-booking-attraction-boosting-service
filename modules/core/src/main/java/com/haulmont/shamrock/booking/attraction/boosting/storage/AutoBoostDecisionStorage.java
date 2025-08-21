package com.haulmont.shamrock.booking.attraction.boosting.storage;

import com.haulmont.redis.RedisStorageBase;
import com.haulmont.redis.key.SingleKey;
import com.haulmont.redis.key_codec.RedisCacheSingleKeyCode;
import com.haulmont.shamrock.booking.attraction.boosting.config.ServiceConfiguration;
import com.haulmont.shamrock.booking.attraction.boosting.model.AutoBoostDecision;
import org.picocontainer.annotations.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class AutoBoostDecisionStorage extends RedisStorageBase<UUID, AutoBoostDecision, SingleKey<UUID>> {
    public AutoBoostDecisionStorage(ServiceConfiguration serviceConfiguration) {
        super(AutoBoostDecision.class,
                new RedisCacheSingleKeyCode<>("shamrock-booking-attractions-boosting-service:auto_boost", ""),
                serviceConfiguration::getDecisionTtl, serviceConfiguration::getRedisResource);
    }

    public Optional<AutoBoostDecision> getDecision(UUID bookingId) {
        return get(SingleKey.key(bookingId));
    }

    public void saveDecision(UUID bookingId, AutoBoostDecision decision) {
        save(SingleKey.key(bookingId), decision);
    }

}

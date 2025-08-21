package com.haulmont.shamrock.booking.attraction.boosting.storage;

import com.haulmont.redis.RedisStorageBase;
import com.haulmont.redis.key.PairKey;
import com.haulmont.redis.key_codec.RedisCachePairKeyCodec;
import com.haulmont.shamrock.booking.attraction.boosting.config.ServiceConfiguration;
import com.haulmont.shamrock.booking.attraction.boosting.model.Cap;
import com.haulmont.shamrock.booking.attraction.boosting.model.CapConsumption;
import com.haulmont.shamrock.booking.attraction.boosting.model.Strategy;
import org.joda.time.DateTime;
import org.picocontainer.annotations.Component;

@Component
public class ConsumptionStorage extends RedisStorageBase<String, CapConsumption, PairKey<String>> {

    public ConsumptionStorage(ServiceConfiguration serviceConfiguration) {
        super(CapConsumption.class,
                new RedisCachePairKeyCodec<>("shamrock-booking-attractions-boosting-service:caps_consumption"),
                serviceConfiguration::getConsumptionTtl, serviceConfiguration::getRedisResource);
    }

    public void addToConsumption(DateTime now, CapConsumption.PeriodType periodType, Strategy strategy, Double delta) {
        CapConsumption consumption = get(now, periodType, strategy);
        consumption.addToConsumption(delta);

        save(PairKey.key(getCap(strategy, periodType).getScope(), consumption.getPeriod()), consumption);
    }

    public CapConsumption get(DateTime now, CapConsumption.PeriodType periodType, Strategy strategy) {
        //todo do we actually need this?
        DateTime startOfDay = now.withTimeAtStartOfDay();

        String formattedPeriod = periodType.getFormatter().print(startOfDay);

        String capScope = getCap(strategy, periodType).getScope();
        return get(PairKey.key(capScope, formattedPeriod)).orElseGet(() -> {
            CapConsumption capsConsumption = new CapConsumption();
            capsConsumption.setScope(capScope);
            capsConsumption.setPeriod(formattedPeriod);

            return capsConsumption;
        });
    }

    private Cap getCap(Strategy strategy, CapConsumption.PeriodType periodType) {
        switch (periodType) {
            case DAILY:
                return strategy.getCaps().getPerDay();
            case WEEKLY:
                return strategy.getCaps().getPerWeek();
            case MONTHLY:
                return strategy.getCaps().getPerMonth();
            default:
                throw new IllegalArgumentException("Unsupported period type: " + periodType);
        }
    }
}

/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting.config;

import com.haulmont.monaco.config.annotations.Config;
import com.haulmont.monaco.config.annotations.Property;
import org.joda.time.Period;
import org.picocontainer.annotations.Component;

@Config
@Component
public interface ServiceConfiguration {
    String BOOST_STRATEGIES_CONFIG_PROPERTY = "boost.strategies";

    @Property("redis.resourceName")
    String getRedisResource();

    @Property("redis.boost.decision.ttl")
    Long getDecisionTtl();

    @Property("redis.boost.capConsumption.ttl")
    Long getConsumptionTtl();

    @Property("boost.supervisor.auto.mode")
    String getSupervisorMode();

    @Property("boost.supervisor.auto.upcomingBookingsLoadingPeriod")
    Period getUpcomingBookingsLoadingPeriod();

    @Property("boost.instruction.code")
    String getBoostInstructionCode();

    @Property("boost.extra.code")
    String getBoostExtraCode();

    @Property("boost.executionLog.createdBy")
    Integer getBoostExecutionLogCreatedBy();

    @Property(BOOST_STRATEGIES_CONFIG_PROPERTY)
    String getBoostStrategies();

    @Property("caches.grade.maxSize")
    Integer getGradeMaxSize();

    @Property("caches.grade.expirationMinutes")
    Integer getGradeExpirationMinutes();
}

/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting.model;

import com.haulmont.shamrock.booking.attraction.boosting.services.dto.busy_regions_supervisor.BusyRegion;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public class AttractionBoostingContext {
    private DateTime createTs;

    private List<BusyRegion> busyRegions;
    
    private Map<BusyRegion, Strategy> strategyByRegion;
    
    private Map<BusyRegion, List<UUID>> productsByRegion;

    private AttractionBoostingContext() {
    }

    public DateTime getCreateTs() {
        return createTs;
    }

    public List<BusyRegion> getBusyRegions() {
        return busyRegions;
    }

    public Strategy getStrategy(BusyRegion busyRegion) {
        return strategyByRegion.get(busyRegion);
    }

    public List<UUID> getProducts(BusyRegion busyRegion) {
        return productsByRegion.get(busyRegion);
    }

    public Map<BusyRegion, Strategy> getStrategyByRegion() {
        return strategyByRegion;
    }

    public Map<BusyRegion, List<UUID>> getProductsByRegion() {
        return productsByRegion;
    }

    public static Builder builder(DateTime now, List<BusyRegion> busyRegions, Map<BusyRegion, Strategy> strategyByRegion, Map<BusyRegion, List<UUID>> productsByRegion) {
        return new Builder(now, busyRegions, strategyByRegion, productsByRegion);
    }

    public static class Builder {
        private final DateTime now;

        private final List<BusyRegion> busyRegions;
        private final Map<BusyRegion, Strategy> strategyByRegion;
        private final Map<BusyRegion, List<UUID>> productsByRegion;

        public Builder(DateTime now, List<BusyRegion> busyRegions, Map<BusyRegion, Strategy> strategyByRegion, Map<BusyRegion, List<UUID>> productsByRegion) {
            this.now = now;
            this.busyRegions = busyRegions;
            this.strategyByRegion = strategyByRegion;
            this.productsByRegion = productsByRegion;
        }
        
        public AttractionBoostingContext build() {
            AttractionBoostingContext context = new AttractionBoostingContext();
            context.createTs = now;
            context.busyRegions = this.busyRegions;
            context.strategyByRegion = this.strategyByRegion;
            context.productsByRegion = this.productsByRegion;
            return context;
        }
    }

}

/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting.services;

import com.haulmont.monaco.unirest.ServiceCallUtils;
import com.haulmont.monaco.unirest.UnirestCommand;
import com.haulmont.shamrock.booking.attraction.boosting.BoostConsumptionService;
import com.haulmont.shamrock.booking.attraction.boosting.StrategyService;
import com.haulmont.shamrock.booking.attraction.boosting.services.dto.busy_regions_supervisor.BusyRegion;
import com.haulmont.shamrock.booking.attraction.boosting.services.dto.busy_regions_supervisor.BusyRegionsResponse;
import kong.unirest.HttpRequest;
import org.picocontainer.annotations.Component;
import org.picocontainer.annotations.Inject;
import org.slf4j.Logger;

import java.util.List;
import java.util.function.Function;

import static com.haulmont.monaco.unirest.ServiceCallUtils.call;

@Component
public class BusyRegionsSupervisorService {
    @Inject
    private Logger log;

    @Inject
    private StrategyService strategyService;

    @Inject
    private BoostConsumptionService consumptionService;

    @Inject
    private ProductRegistryService productRegistryService;

    public List<BusyRegion> getActiveBusyRegions() {
        Function<BusyRegionsResponse, List<BusyRegion>> extractor = response -> ServiceCallUtils.extract(response, BusyRegionsResponse::getBusyRegions);

        return call(LoadActiveBusyRegionsCommand::new, extractor);
    }

    public static class LoadActiveBusyRegionsCommand extends UnirestCommand<BusyRegionsResponse> {

        private static final String SERVICE = "shamrock-busy-regions-supervisor-service";

        public LoadActiveBusyRegionsCommand() {
            super(SERVICE, BusyRegionsResponse.class);
        }

        @Override
        protected HttpRequest<?> createRequest(String url, Path path) {
            return get(url, path)
                    .queryString("status", "busy");
        }

        @Override
        protected Path getPath() {
            return new Path("busy-regions");
        }
    }
}

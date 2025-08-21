/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectReader;
import com.haulmont.monaco.config.AppConfig;
import com.haulmont.monaco.jackson.ObjectReaderWriterFactory;
import com.haulmont.shamrock.booking.attraction.boosting.config.ServiceConfiguration;
import com.haulmont.shamrock.booking.attraction.boosting.model.Cap;
import com.haulmont.shamrock.booking.attraction.boosting.model.Caps;
import com.haulmont.shamrock.booking.attraction.boosting.model.Strategy;
import com.haulmont.shamrock.booking.attraction.boosting.services.dto.busy_regions_supervisor.BusyRegion;
import org.apache.commons.collections4.CollectionUtils;
import org.picocontainer.annotations.Component;
import org.picocontainer.annotations.Inject;
import org.slf4j.Logger;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.haulmont.shamrock.booking.attraction.boosting.config.ServiceConfiguration.BOOST_STRATEGIES_CONFIG_PROPERTY;
import static com.haulmont.shamrock.booking.attraction.boosting.util.ScopeUtil.*;
import static java.util.Collections.emptyList;

@Component
public class StrategyService {
    private static final ScopeType[] SCOPE_TYPES_RESOLUTION_SEQUENCE = new ScopeType[]{
            ScopeType.PRODUCT_GROUP,
            ScopeType.REGION_GROUP,
            ScopeType.REGION_PRODUCT_GROUP
    };

    private static final Pattern REGION_PATTERN = Pattern.compile("RG\\|([0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12})");
    private static final Pattern PRODUCT_PATTERN = Pattern.compile("PG\\|([0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12})");

    @Inject
    private Logger log;

    @Inject
    private ObjectReaderWriterFactory objectReaderWriterFactory;

    @Inject
    private AppConfig appConfig;

    @Inject
    private ServiceConfiguration configuration;

    private Map<String, Strategy> strategyByScope;

    public void start() {
        this.strategyByScope = parseStrategies();

        appConfig.registerListener(event -> {
            if (BOOST_STRATEGIES_CONFIG_PROPERTY.equals(event.getKey())) {
                //todo lock?
                this.strategyByScope = parseStrategies();
                //todo log more details?
                log.info("Strategies were updated");
            }
        });
    }

    private Map<String, Strategy> parseStrategies() {
        try {
            String strategiesJson = configuration.getBoostStrategies();
            if (strategiesJson == null || strategiesJson.isEmpty()) {
                log.warn("No strategies configuration found");
                return new HashMap<>();
            }

            // Parse JSON into Strategy objects
            List<Strategy> strategies;
            try {
                // Try using ObjectMapper directly
                ObjectReader reader = objectReaderWriterFactory.reader(List.class, Strategy.class);
                strategies = reader.readValue(strategiesJson);
            } catch (JsonProcessingException e) {
                log.error("Error parsing strategies JSON", e);
                return new HashMap<>();
            }

            if (CollectionUtils.isEmpty(strategies)) {
                log.warn("Strategies configuration has no data");
                return new HashMap<>();
            }

            Map<String, Strategy> strategyMap = new HashMap<>();
            Map<ScopeType, List<Strategy>> strategiesByScopeType = new HashMap<>();

            for (Strategy strategy : strategies) {
                fetchComputableFields(strategy);
                strategyMap.put(strategy.getScope(), strategy);

                ScopeType scopeType = getScopeType(strategy.getScope());
                strategiesByScopeType.computeIfAbsent(scopeType, k -> new ArrayList<>()).add(strategy);
            }

            resolveHierarchyFields(strategyMap, strategiesByScopeType, SCOPE_TYPES_RESOLUTION_SEQUENCE);

            return strategyMap;
        } catch (Exception e) {
            log.error("Error parsing strategies", e);
            return new HashMap<>();
        }
    }

    private ScopeType getScopeType(String scope) {
        if (scope == null || DEFAULT_SCOPE.equals(scope)) return ScopeType.DEFAULT;
        boolean startsRg = scope.startsWith(REGION_SCOPE_FORMAT.replace("%s", ""));
        boolean hasPg = scope.contains(SCOPE_PART_DELIMITER + PRODUCT_SCOPE_FORMAT.replace("%s", ""));
        boolean startsPg = scope.startsWith(PRODUCT_SCOPE_FORMAT.replace("%s", ""));

        if (startsRg && hasPg) return ScopeType.REGION_PRODUCT_GROUP;
        if (startsRg) return ScopeType.REGION_GROUP;
        if (startsPg) return ScopeType.PRODUCT_GROUP;

        return ScopeType.DEFAULT;
    }

    private void resolveHierarchyFields(Map<String, Strategy> srategiesByScope, Map<ScopeType, List<Strategy>> strategiesByScopeType, ScopeType... scopeTypes) {
        for (ScopeType scopeType : scopeTypes) {
            for (Strategy strategy : strategiesByScopeType.getOrDefault(scopeType, emptyList())) {
                if (strategy.isEnabled() && hasFieldsForResolution(strategy)) {
                    resolveHierarchyFields(strategy, scopeType, strategiesByScopeType, srategiesByScope);
                }
            }
        }
    }

    private void resolveHierarchyFields(Strategy strategy, ScopeType scopeType, Map<ScopeType, List<Strategy>> strategiesByScopeType, Map<String, Strategy> strategyMap) {
        if (scopeType == ScopeType.PRODUCT_GROUP) {
            Strategy parent = strategyMap.get(ScopeType.DEFAULT.getValue());

            if (!strategy.hasBands()) {
                //todo copy object?
                strategy.setBands(parent.getBands());
            }

            strategy.setCaps(mergeCaps(strategy.getCaps(), parent.getCaps()));
        } else if (scopeType == ScopeType.REGION_GROUP) {
            List<Strategy> productGroupStrategies = strategiesByScopeType.get(ScopeType.PRODUCT_GROUP);

            if (CollectionUtils.isNotEmpty(productGroupStrategies)) {
                for (Strategy productStrategy : productGroupStrategies) {
                    Strategy regionProductStrategy = new Strategy();
                    regionProductStrategy.setScope(regionProduct(strategy.getRegionGroupId().toString(), productStrategy.getProductGroupId().toString()));

                    if (!strategyMap.containsKey(regionProductStrategy.getScope())) {
                        regionProductStrategy.setType(strategy.getType());
                        regionProductStrategy.setEnabled(strategy.isEnabled());
                        regionProductStrategy.setRegionGroupId(strategy.getRegionGroupId());
                        regionProductStrategy.setProductGroupId(productStrategy.getProductGroupId());
                        regionProductStrategy.setBands(strategy.hasBands() ? strategy.getBands() : productStrategy.getBands());

                        regionProductStrategy.setCaps(mergeCaps(strategy.getCaps(), productStrategy.getCaps()));

                        strategyMap.put(regionProductStrategy.getScope(), regionProductStrategy);
                    } else {
                        Strategy existingStrategy = strategyMap.get(regionProductStrategy.getScope());

                        if(!existingStrategy.hasBands()) {
                            existingStrategy.setBands(strategy.hasBands() ? strategy.getBands() : productStrategy.getBands());
                        }

                        if(strategy.getCaps() != null) {
                            existingStrategy.setCaps(mergeCaps(existingStrategy.getCaps(), strategy.getCaps()));
                        }
                        existingStrategy.setCaps(mergeCaps(existingStrategy.getCaps(), productStrategy.getCaps()));
                    }
                }
            }

            Strategy defaultStrategy = strategyMap.get(ScopeType.DEFAULT.getValue());

            if (!strategy.hasBands()) {
                strategy.setBands(defaultStrategy.getBands());
            }

            strategy.setCaps(mergeCaps(strategy.getCaps(), defaultStrategy.getCaps()));
        } else if (scopeType == ScopeType.REGION_PRODUCT_GROUP) {
            Strategy parent = strategyMap.get(region(strategy.getRegionGroupId().toString()));

            if (parent == null) {
                parent = strategyMap.get(product(strategy.getProductGroupId().toString()));
            }

            if (parent == null) {
                parent = strategyMap.get(DEFAULT_SCOPE);
            }

            if (parent != null) {
                if (!strategy.hasBands()) {
                    strategy.setBands(parent.getBands());
                }

                strategy.setCaps(mergeCaps(strategy.getCaps(), parent.getCaps()));
            }
        } else {
            throw new IllegalArgumentException("Unknown scope type: " + scopeType);
        }
    }

    //todo copy?
    private Caps mergeCaps(Caps main, Caps fallback) {
        Caps mergedCaps = new Caps();
        if (main == null) {
            mergedCaps.setCurrency(fallback.getCurrency());
            mergedCaps.setPerDay(fallback.getPerDay());
            mergedCaps.setPerWeek(fallback.getPerWeek());
            mergedCaps.setPerMonth(fallback.getPerMonth());

            return mergedCaps;
        } else {
            mergedCaps.setCurrency(main.getCurrency());

            if (main.getPerDay() != null) {
                mergedCaps.setPerDay(main.getPerDay());
            } else {
                mergedCaps.setPerDay(fallback.getPerDay());
            }

            if (main.getPerWeek() != null) {
                mergedCaps.setPerWeek(main.getPerWeek());
            } else {
                mergedCaps.setPerWeek(fallback.getPerWeek());
            }

            if (main.getPerMonth() != null) {
                mergedCaps.setPerMonth(main.getPerMonth());
            } else {
                mergedCaps.setPerMonth(fallback.getPerMonth());
            }

            return mergedCaps;
        }
    }

    private boolean hasFieldsForResolution(Strategy strategy) {
        return strategy.getBands() == null
                || strategy.getCaps() == null
                || strategy.getCaps().getPerDay() == null
                || strategy.getCaps().getPerDay().getValue() == null
                || strategy.getCaps().getPerWeek() == null
                || strategy.getCaps().getPerWeek().getValue() == null
                || strategy.getCaps().getPerMonth() == null
                || strategy.getCaps().getPerMonth().getValue() == null;
    }

    private void fetchComputableFields(Strategy strategy) {
        String scope = strategy.getScope();
        if (scope == null) {
            return;
        }

        Matcher regionMatcher = REGION_PATTERN.matcher(scope);
        if (regionMatcher.find()) {
            strategy.setRegionGroupId(UUID.fromString(regionMatcher.group(1)));
        }

        Matcher productMatcher = PRODUCT_PATTERN.matcher(scope);
        if (productMatcher.find()) {
            strategy.setProductGroupId(UUID.fromString(productMatcher.group(1)));
        }

        if (strategy.hasBands()) {
            strategy.getBands().setSourceScope(scope);
        }

        if (strategy.getCaps() != null) {
            if (strategy.getCaps().getPerDay() != null) {
                strategy.getCaps().getPerDay().setScope(scope);
            }
            if (strategy.getCaps().getPerWeek() != null) {
                strategy.getCaps().getPerWeek().setScope(scope);
            }
            if (strategy.getCaps().getPerMonth() != null) {
                strategy.getCaps().getPerMonth().setScope(scope);
            }
        }
    }

    private Strategy cloneStrategy(Strategy original) {
        Strategy clone = new Strategy();
        clone.setScope(original.getScope());
        clone.setType(original.getType());
        clone.setEnabled(original.isEnabled());
        clone.setRegionGroupId(original.getRegionGroupId());
        clone.setProductGroupId(original.getProductGroupId());

        if (original.getCaps() != null) {
            clone.setCaps(cloneCaps(original.getCaps()));
        }

        return clone;
    }


    private Caps cloneCaps(Caps original) {
        if (original == null) {
            return null;
        }

        Caps clone = new Caps();
        clone.setCurrency(original.getCurrency());

        if (original.getPerDay() != null) {
            Cap perDay = new Cap();
            perDay.setValue(original.getPerDay().getValue());
            perDay.setScope(original.getPerDay().getScope());
            clone.setPerDay(perDay);
        }

        if (original.getPerWeek() != null) {
            Cap perWeek = new Cap();
            perWeek.setValue(original.getPerWeek().getValue());
            perWeek.setScope(original.getPerWeek().getScope());
            clone.setPerWeek(perWeek);
        }

        if (original.getPerMonth() != null) {
            Cap perMonth = new Cap();
            perMonth.setValue(original.getPerMonth().getValue());
            perMonth.setScope(original.getPerMonth().getScope());
            clone.setPerMonth(perMonth);
        }

        return clone;
    }

    public List<Strategy> getStrategies() {
        return new ArrayList<>(strategyByScope.values());
    }

    public Strategy getStrategy(String scope) {
        return strategyByScope.get(scope);
    }

    public Strategy matchStrategy(BusyRegion busyRegion) {
        return Stream.of(
                        regionProduct(busyRegion),
                        region(busyRegion),
                        product(busyRegion),
                        DEFAULT_SCOPE
                )
                .map(strategyByScope::get)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    private enum ScopeType {
        DEFAULT("default"),
        PRODUCT_GROUP("product_group", DEFAULT),
        REGION_GROUP("region_group", PRODUCT_GROUP, DEFAULT),
        REGION_PRODUCT_GROUP("region_product_group", REGION_GROUP, PRODUCT_GROUP, DEFAULT);

        private final String value;
        private final List<ScopeType> hierarchy;

        ScopeType(String value, ScopeType... hierarchy) {
            this.value = value;
            this.hierarchy = Arrays.asList(hierarchy);
        }

        public String getValue() {
            return value;
        }

        public List<ScopeType> getHierarchy() {
            return hierarchy;
        }
    }
}

/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.attraction.boosting;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.haulmont.monaco.config.AppConfig;
import com.haulmont.monaco.jackson.ObjectMapperContainer;
import com.haulmont.monaco.jackson.ObjectReaderWriterFactory;
import com.haulmont.shamrock.booking.attraction.boosting.config.ServiceConfiguration;
import com.haulmont.shamrock.booking.attraction.boosting.model.Strategy;
import com.haulmont.shamrock.booking.attraction.boosting.model.StrategyType;
import com.haulmont.shamrock.booking.attraction.boosting.services.dto.busy_regions_supervisor.BusyRegion;
import org.apache.commons.lang3.StringUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.quality.Strictness;
import org.mockito.testng.MockitoSettings;
import org.mockito.testng.MockitoTestNGListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import static com.haulmont.shamrock.booking.attraction.boosting.util.ScopeUtil.*;

//todo refactor tests + cleanup cases
@Listeners({MockitoTestNGListener.class})
@MockitoSettings(strictness = Strictness.LENIENT)
public class StrategyServiceTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(StrategyServiceTest.class);

    public static String BASE_PATH;

    static {
        String pkgPath = StrategyServiceTest.class
                .getPackage()
                .getName()
                .replace('.', '/');

        String className = StrategyServiceTest.class.getSimpleName();

        BASE_PATH = "/" + pkgPath + "/" + className + "/";
    }

    ObjectMapperContainer objectMapperContainer = new ObjectMapperContainer();

    @InjectMocks
    private StrategyService strategyService;

    @Mock
    private AppConfig applicationConfiguration;

    @Mock
    private Logger log;

    @Mock
    private ServiceConfiguration configuration;

    @Spy
    private ObjectReaderWriterFactory objectReaderWriterFactory;

    @Test
    public void testEmptyStrategies() {
        initStrategies("emptyStrategies");
        Assert.assertEquals(strategyService.getStrategies().size(), 0);
    }

    @Test
    public void defaultEnabledRegionProductGroupScopeDisabled() {
        String caseName = "defaultEnabledRegionProductGroupScopeDisabled";
        initStrategies(caseName);

        List<BusyRegion> busyRegions = loadBusyRegions(caseName);

        Assert.assertEquals(strategyService.getStrategies().size(), 2);

        BusyRegion busyRegion = busyRegions.get(0);
        Strategy expectedStrategy = buildExpectedStrategy(
                busyRegion.getRegionGroupId(), busyRegion.getProductGroupId(), regionProduct(busyRegion),
                false, StrategyType.FLAT, null, null
        );
        Strategy matchedStrategy = strategyService.matchStrategy(busyRegion);
        assertMatchedStrategy(matchedStrategy, expectedStrategy);

        Strategy defaultStrategy = strategyService.getStrategy(DEFAULT_SCOPE);
        expectedStrategy = buildExpectedStrategy(
                defaultStrategy.getScope(), defaultStrategy.isEnabled(),
                StrategyType.FLAT, defaultStrategy, defaultStrategy
        );

        busyRegion = busyRegions.get(1);
        matchedStrategy = strategyService.matchStrategy(busyRegion);
        assertMatchedStrategy(matchedStrategy, expectedStrategy);

        busyRegion = busyRegions.get(2);
        matchedStrategy = strategyService.matchStrategy(busyRegion);
        assertMatchedStrategy(matchedStrategy, expectedStrategy);
    }

    @Test
    public void regionBeatsProductFullStrategy() {
        String caseName = "regionBeatsProductFullStrategy";
        initStrategies(caseName);

        List<BusyRegion> busyRegions = loadBusyRegions(caseName);

        Assert.assertEquals(strategyService.getStrategies().size(), 3);

        BusyRegion busyRegion = busyRegions.get(0);
        Strategy matchedStrategy = strategyService.matchStrategy(busyRegion);
        Strategy regionStrategy  = strategyService.getStrategy(region(busyRegion));
        Strategy expectedStrategy = buildExpectedStrategy(
                busyRegion.getRegionGroupId(), null, region(busyRegion),
                true, StrategyType.FLAT, regionStrategy, regionStrategy
        );
        assertMatchedStrategy(matchedStrategy, expectedStrategy);

        busyRegion = busyRegions.get(1);
        Strategy productStrategy  = strategyService.getStrategy(product(busyRegion));
        expectedStrategy = buildExpectedStrategy(
                null, busyRegion.getProductGroupId(), product(busyRegion),
                true, StrategyType.FLAT, productStrategy, productStrategy
        );
        matchedStrategy = strategyService.matchStrategy(busyRegion);
        assertMatchedStrategy(matchedStrategy, expectedStrategy);

        busyRegion = busyRegions.get(2);
        Strategy defaultStrategy = strategyService.getStrategy(DEFAULT_SCOPE);
        expectedStrategy = buildExpectedStrategy(
                DEFAULT_SCOPE, defaultStrategy.isEnabled(),
                StrategyType.FLAT, defaultStrategy, defaultStrategy
        );
        matchedStrategy = strategyService.matchStrategy(busyRegion);
        assertMatchedStrategy(matchedStrategy, expectedStrategy);
    }

    //missing bands caps

    @Test
    public void regionProductGroupMissingBandsCaps_regionBandCaps() {
        String caseName = "regionProductGroupMissingBandsCaps_regionBandsCaps";
        initStrategies(caseName);

        List<BusyRegion> busyRegions = loadBusyRegions(caseName);

        Assert.assertEquals(strategyService.getStrategies().size(), 4);

        BusyRegion busyRegion = busyRegions.get(0);
        Strategy regionStrategy  = strategyService.getStrategy(region(busyRegion));
        Strategy expectedStrategy = buildExpectedStrategy(
                busyRegion.getRegionGroupId(), busyRegion.getProductGroupId(), regionProduct(busyRegion),
                true, StrategyType.FLAT, regionStrategy, regionStrategy
        );
        Strategy matchedStrategy = strategyService.matchStrategy(busyRegion);
        assertMatchedStrategy(matchedStrategy, expectedStrategy);
    }

    @Test
    public void regionProductGroupMissingBandsCaps_productBandCaps() {
        String caseName = "regionProductGroupMissingBandsCaps_productBandsCaps";
        initStrategies(caseName);

        List<BusyRegion> busyRegions = loadBusyRegions(caseName);

        Assert.assertEquals(strategyService.getStrategies().size(), 3);

        BusyRegion busyRegion = busyRegions.get(0);
        Strategy productStrategy  = strategyService.getStrategy(product(busyRegion));
        Strategy expectedStrategy = buildExpectedStrategy(
                busyRegion.getRegionGroupId(), busyRegion.getProductGroupId(), regionProduct(busyRegion),
                true, StrategyType.FLAT, productStrategy, productStrategy
        );
        Strategy matchedStrategy = strategyService.matchStrategy(busyRegion);
        assertMatchedStrategy(matchedStrategy, expectedStrategy);
    }

    //missing bands

    @Test
    public void regionGroupMissingBands_productBandsRegionCaps() {
        String caseName = "regionGroupMissingBands_productBandsRegionCaps";
        initStrategies(caseName);

        List<BusyRegion> busyRegions = loadBusyRegions(caseName);

        Assert.assertEquals(strategyService.getStrategies().size(), 8);

        Strategy defaultStrategy = strategyService.getStrategy(DEFAULT_SCOPE);

        BusyRegion busyRegion = busyRegions.get(0);
        Strategy regionStrategy  = strategyService.getStrategy(region(busyRegion));
        Strategy productStrategy  = strategyService.getStrategy(product(busyRegion));
        Strategy expectedStrategy = buildExpectedStrategy(
                busyRegion.getRegionGroupId(), busyRegion.getProductGroupId(), regionProduct(busyRegion),
                true, StrategyType.FLAT, productStrategy, regionStrategy
        );
        Strategy matchedStrategy = strategyService.matchStrategy(busyRegion);
        assertMatchedStrategy(matchedStrategy, expectedStrategy);

        busyRegion = busyRegions.get(1);
        regionStrategy  = strategyService.getStrategy(region(busyRegion));
        expectedStrategy = buildExpectedStrategy(
                busyRegion.getRegionGroupId(), busyRegion.getProductGroupId(), regionProduct(busyRegion),
                true, StrategyType.FLAT, defaultStrategy, regionStrategy
        );
        matchedStrategy = strategyService.matchStrategy(busyRegion);
        assertMatchedStrategy(matchedStrategy, expectedStrategy);

        busyRegion = busyRegions.get(2);
        regionStrategy  = strategyService.getStrategy(region(busyRegion));
        productStrategy  = strategyService.getStrategy(product(busyRegion));
        expectedStrategy = buildExpectedStrategy(
                busyRegion.getRegionGroupId(), busyRegion.getProductGroupId(), regionProduct(busyRegion),
                true, StrategyType.FLAT, productStrategy, regionStrategy
        );
        matchedStrategy = strategyService.matchStrategy(busyRegion);
        assertMatchedStrategy(matchedStrategy, expectedStrategy);
    }

    //missing caps

    @Test
    public void regionProductGroupMissingCaps_regionBandsProductCaps() {
        String caseName = "regionProductGroupMissingCaps_regionBandsProductCaps";
        initStrategies(caseName);

        List<BusyRegion> busyRegions = loadBusyRegions(caseName);

        Assert.assertEquals(strategyService.getStrategies().size(), 4);

        Strategy defaultStrategy = strategyService.getStrategy(DEFAULT_SCOPE);

        BusyRegion busyRegion = busyRegions.get(0);
        Strategy expectedStrategy = defaultStrategy;
        Strategy matchedStrategy = strategyService.matchStrategy(busyRegion);
        assertMatchedStrategy(matchedStrategy, expectedStrategy);

        busyRegion = busyRegions.get(1);
        Strategy regionStrategy  = strategyService.getStrategy(region(busyRegion));
        Strategy regionProductStrategy  = strategyService.getStrategy(regionProduct(busyRegion));
        expectedStrategy = buildExpectedStrategy(busyRegion.getRegionGroupId(), busyRegion.getProductGroupId(), regionProduct(busyRegion),
                true, StrategyType.FLAT, regionProductStrategy, regionStrategy);
        matchedStrategy = strategyService.matchStrategy(busyRegion);
        assertMatchedStrategy(matchedStrategy, expectedStrategy);

        busyRegion = busyRegions.get(2);
        regionStrategy = strategyService.getStrategy(region(busyRegion));
        expectedStrategy = buildExpectedStrategy(busyRegion.getRegionGroupId(), null, region(busyRegion),
                true, StrategyType.FLAT, regionStrategy, regionStrategy);
        matchedStrategy = strategyService.matchStrategy(busyRegion);
        assertMatchedStrategy(matchedStrategy, expectedStrategy);

        busyRegion = busyRegions.get(3);
        Strategy productStrategy = strategyService.getStrategy(product(busyRegion));
        expectedStrategy = buildExpectedStrategy(null, busyRegion.getProductGroupId(), product(busyRegion),
                true, StrategyType.FLAT, productStrategy, productStrategy);
        matchedStrategy = strategyService.matchStrategy(busyRegion);
        assertMatchedStrategy(matchedStrategy, expectedStrategy);
    }


    private void assertMatchedStrategy(Strategy matchedStrategy, Strategy expectedStrategy) {
        Assert.assertNotNull(matchedStrategy);
        Assert.assertEquals(matchedStrategy.isEnabled(), expectedStrategy.isEnabled());
        Assert.assertEquals(matchedStrategy.getRegionGroupId(), expectedStrategy.getRegionGroupId());
        Assert.assertEquals(matchedStrategy.getProductGroupId(), expectedStrategy.getProductGroupId());
        Assert.assertEquals(matchedStrategy.getScope(), expectedStrategy.getScope());
        Assert.assertEquals(matchedStrategy.getBands(), expectedStrategy.getBands());
        Assert.assertEquals(matchedStrategy.getCaps(), expectedStrategy.getCaps());
    }

    private void initStrategies(String caseName) {
        mockData(caseName);

        strategyService.start();
    }

    private void mockData(String caseName) {
        List<Strategy> strategies = loadStrategies(caseName);

        try {
            Mockito.doReturn(objectMapperContainer.mapper().writeValueAsString(strategies)).when(configuration).getBoostStrategies();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private Strategy buildExpectedStrategy(String scope, boolean enabled, StrategyType type, Strategy bandsSource, Strategy capsSource) {
        return buildExpectedStrategy(null, null, scope, enabled, type, bandsSource, capsSource);
    }

    private Strategy buildExpectedStrategy(UUID regionGroupId, UUID productGroupId, String scope, boolean enabled, StrategyType type, Strategy bandsSource, Strategy capsSource) {
        Strategy strategy = new Strategy();
        strategy.setScope(scope);
        strategy.setEnabled(enabled);
        strategy.setType(type);

        strategy.setRegionGroupId(regionGroupId);
        strategy.setProductGroupId(productGroupId);

        if (bandsSource != null) {
            strategy.setBands(bandsSource.getBands());
        }
        if (capsSource != null) {
            strategy.setCaps(capsSource.getCaps());
        }

        return strategy;
    }

    private BusyRegion createBusyRegion(String rg, String pg) {
        return createBusyRegion(UUID.fromString(rg), UUID.fromString(pg));
    }

    private BusyRegion createBusyRegion(UUID rg, UUID pg) {
        var busyRegion = new BusyRegion();
        busyRegion.setRegionGroupId(rg);
        busyRegion.setProductGroupId(pg);
        return busyRegion;
    }

    private List<Strategy> loadStrategies(String caseName) {
        return resource(caseName, "strategies", new TypeReference<>() {
        });
    }

    private List<BusyRegion> loadBusyRegions(String caseName) {
        return resource(caseName, "busy_regions", new TypeReference<>() {
        });
    }

    private <T> T resource(String caseName, String name, TypeReference<T> typeReference) {
        String path = BASE_PATH + (StringUtils.isBlank(caseName) ? "" : (caseName + "/")) + name + ".json";

        InputStream resource = StrategyServiceTest.class.getResourceAsStream(path);
        if (resource == null) {
            throw new IllegalStateException();
        }

        try {
            return objectMapperContainer.mapper().readerFor(typeReference).readValue(resource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
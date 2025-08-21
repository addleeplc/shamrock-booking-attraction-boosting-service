package com.haulmont.shamrock.booking.attraction.boosting;

import com.haulmont.monaco.annotations.Module;
import com.haulmont.monaco.container.ModuleLoader;
import com.haulmont.monaco.jackson.ObjectReaderWriterFactory;

@Module(
        name = "shamrock-booking-attraction-boosting-service-module",
        depends = {
                "monaco-jetty",
                "monaco-core",
                "monaco-config",
                "monaco-graylog-reporter",
                "monaco-sentry-reporter",
                "monaco-ds",
                "monaco-ds-postgresql",
                "monaco-ds-sybase",
                "monaco-metrics",
                "monaco-mybatis",
                "monaco-redis",
                "monaco-rs",
                "monaco-scheduler",
                "monaco-sql2o",
                "monaco-unirest"
        }
)
public class ShamrockBookingAttractionBoostingServiceModule extends ModuleLoader {

    public ShamrockBookingAttractionBoostingServiceModule () {
        super();
        packages(ShamrockBookingAttractionBoostingServiceModule.class.getPackageName());

        component(ObjectReaderWriterFactory.class);
    }
}

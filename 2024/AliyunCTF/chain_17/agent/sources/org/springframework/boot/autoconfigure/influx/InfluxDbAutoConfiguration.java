package org.springframework.boot.autoconfigure.influx;

import okhttp3.OkHttpClient;
import org.influxdb.InfluxDB;
import org.influxdb.impl.InfluxDBImpl;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@EnableConfigurationProperties({InfluxDbProperties.class})
@AutoConfiguration
@ConditionalOnClass({InfluxDB.class})
@Deprecated(since = "3.2.0", forRemoval = true)
@ConditionalOnProperty({"spring.influx.url"})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/influx/InfluxDbAutoConfiguration.class */
public class InfluxDbAutoConfiguration {
    @ConditionalOnMissingBean
    @Bean
    public InfluxDB influxDb(InfluxDbProperties properties, ObjectProvider<InfluxDbOkHttpClientBuilderProvider> builder, ObjectProvider<InfluxDbCustomizer> customizers) {
        InfluxDBImpl influxDBImpl = new InfluxDBImpl(properties.getUrl().toString(), properties.getUser(), properties.getPassword(), determineBuilder(builder.getIfAvailable()));
        customizers.orderedStream().forEach(customizer -> {
            customizer.customize(influxDBImpl);
        });
        return influxDBImpl;
    }

    private static OkHttpClient.Builder determineBuilder(InfluxDbOkHttpClientBuilderProvider builder) {
        if (builder != null) {
            return builder.get();
        }
        return new OkHttpClient.Builder();
    }
}

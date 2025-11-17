package org.springframework.boot.autoconfigure.r2dbc;

import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.r2dbc.spi.Option;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.r2dbc.ConnectionFactoryConfigurations;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.util.StringUtils;

@EnableConfigurationProperties({R2dbcProperties.class})
@AutoConfiguration(before = {DataSourceAutoConfiguration.class, SqlInitializationAutoConfiguration.class})
@ConditionalOnClass({ConnectionFactory.class})
@ConditionalOnResource(resources = {"classpath:META-INF/services/io.r2dbc.spi.ConnectionFactoryProvider"})
@Import({ConnectionFactoryConfigurations.PoolConfiguration.class, ConnectionFactoryConfigurations.GenericConfiguration.class, ConnectionFactoryDependentConfiguration.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/r2dbc/R2dbcAutoConfiguration.class */
public class R2dbcAutoConfiguration {
    @ConditionalOnMissingBean({R2dbcConnectionDetails.class})
    @ConditionalOnProperty({"spring.r2dbc.url"})
    @Bean
    PropertiesR2dbcConnectionDetails propertiesR2dbcConnectionDetails(R2dbcProperties properties) {
        return new PropertiesR2dbcConnectionDetails(properties);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/r2dbc/R2dbcAutoConfiguration$PropertiesR2dbcConnectionDetails.class */
    static class PropertiesR2dbcConnectionDetails implements R2dbcConnectionDetails {
        private final R2dbcProperties properties;

        PropertiesR2dbcConnectionDetails(R2dbcProperties properties) {
            this.properties = properties;
        }

        @Override // org.springframework.boot.autoconfigure.r2dbc.R2dbcConnectionDetails
        public ConnectionFactoryOptions getConnectionFactoryOptions() {
            ConnectionFactoryOptions urlOptions = ConnectionFactoryOptions.parse(this.properties.getUrl());
            ConnectionFactoryOptions.Builder optionsBuilder = urlOptions.mutate();
            Option option = ConnectionFactoryOptions.USER;
            R2dbcProperties r2dbcProperties = this.properties;
            Objects.requireNonNull(r2dbcProperties);
            configureIf(optionsBuilder, urlOptions, option, r2dbcProperties::getUsername, StringUtils::hasText);
            Option option2 = ConnectionFactoryOptions.PASSWORD;
            R2dbcProperties r2dbcProperties2 = this.properties;
            Objects.requireNonNull(r2dbcProperties2);
            configureIf(optionsBuilder, urlOptions, option2, r2dbcProperties2::getPassword, StringUtils::hasText);
            configureIf(optionsBuilder, urlOptions, ConnectionFactoryOptions.DATABASE, () -> {
                return determineDatabaseName(this.properties);
            }, StringUtils::hasText);
            if (this.properties.getProperties() != null) {
                this.properties.getProperties().forEach((key, value) -> {
                    optionsBuilder.option(Option.valueOf(key), value);
                });
            }
            return optionsBuilder.build();
        }

        private <T extends CharSequence> void configureIf(ConnectionFactoryOptions.Builder optionsBuilder, ConnectionFactoryOptions originalOptions, Option<T> option, Supplier<T> valueSupplier, Predicate<T> setIf) {
            if (originalOptions.hasOption(option)) {
                return;
            }
            T value = valueSupplier.get();
            if (setIf.test(value)) {
                optionsBuilder.option(option, value);
            }
        }

        private String determineDatabaseName(R2dbcProperties properties) {
            if (properties.isGenerateUniqueName()) {
                return properties.determineUniqueName();
            }
            if (StringUtils.hasLength(properties.getName())) {
                return properties.getName();
            }
            return null;
        }
    }
}

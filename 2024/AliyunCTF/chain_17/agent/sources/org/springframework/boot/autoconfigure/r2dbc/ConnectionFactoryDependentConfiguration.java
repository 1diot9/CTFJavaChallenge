package org.springframework.boot.autoconfigure.r2dbc;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.core.DatabaseClient;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({DatabaseClient.class})
@ConditionalOnSingleCandidate(ConnectionFactory.class)
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/r2dbc/ConnectionFactoryDependentConfiguration.class */
class ConnectionFactoryDependentConfiguration {
    ConnectionFactoryDependentConfiguration() {
    }

    @ConditionalOnMissingBean
    @Bean
    DatabaseClient r2dbcDatabaseClient(ConnectionFactory connectionFactory) {
        return DatabaseClient.builder().connectionFactory(connectionFactory).build();
    }
}

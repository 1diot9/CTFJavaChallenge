package org.springframework.boot.autoconfigure.sql.init;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.r2dbc.ConnectionFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.connection.init.DatabasePopulator;
import org.springframework.util.StringUtils;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ConnectionFactory.class, DatabasePopulator.class})
@ConditionalOnSingleCandidate(ConnectionFactory.class)
@ConditionalOnMissingBean({SqlR2dbcScriptDatabaseInitializer.class, SqlDataSourceScriptDatabaseInitializer.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/sql/init/R2dbcInitializationConfiguration.class */
class R2dbcInitializationConfiguration {
    R2dbcInitializationConfiguration() {
    }

    @Bean
    SqlR2dbcScriptDatabaseInitializer r2dbcScriptDatabaseInitializer(ConnectionFactory connectionFactory, SqlInitializationProperties properties) {
        return new SqlR2dbcScriptDatabaseInitializer(determineConnectionFactory(connectionFactory, properties.getUsername(), properties.getPassword()), properties);
    }

    private static ConnectionFactory determineConnectionFactory(ConnectionFactory connectionFactory, String username, String password) {
        if (StringUtils.hasText(username) && StringUtils.hasText(password)) {
            return ConnectionFactoryBuilder.derivedFrom(connectionFactory).username(username).password(password).build();
        }
        return connectionFactory;
    }
}

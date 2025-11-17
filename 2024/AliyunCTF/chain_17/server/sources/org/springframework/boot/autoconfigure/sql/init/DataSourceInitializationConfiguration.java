package org.springframework.boot.autoconfigure.sql.init;

import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.util.StringUtils;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({DatabasePopulator.class})
@ConditionalOnSingleCandidate(DataSource.class)
@ConditionalOnMissingBean({SqlDataSourceScriptDatabaseInitializer.class, SqlR2dbcScriptDatabaseInitializer.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/sql/init/DataSourceInitializationConfiguration.class */
class DataSourceInitializationConfiguration {
    DataSourceInitializationConfiguration() {
    }

    @Bean
    SqlDataSourceScriptDatabaseInitializer dataSourceScriptDatabaseInitializer(DataSource dataSource, SqlInitializationProperties properties) {
        return new SqlDataSourceScriptDatabaseInitializer(determineDataSource(dataSource, properties.getUsername(), properties.getPassword()), properties);
    }

    private static DataSource determineDataSource(DataSource dataSource, String username, String password) {
        if (StringUtils.hasText(username) && StringUtils.hasText(password)) {
            return DataSourceBuilder.derivedFrom(dataSource).username(username).password(password).type(SimpleDriverDataSource.class).build();
        }
        return dataSource;
    }
}

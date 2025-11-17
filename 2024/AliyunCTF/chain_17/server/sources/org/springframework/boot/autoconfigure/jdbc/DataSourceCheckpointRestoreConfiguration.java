package org.springframework.boot.autoconfigure.jdbc;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnCheckpointRestore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.jdbc.HikariCheckpointRestoreLifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnCheckpointRestore
@Configuration(proxyBeanMethods = false)
@ConditionalOnBean({DataSource.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jdbc/DataSourceCheckpointRestoreConfiguration.class */
class DataSourceCheckpointRestoreConfiguration {
    DataSourceCheckpointRestoreConfiguration() {
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({HikariDataSource.class})
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jdbc/DataSourceCheckpointRestoreConfiguration$Hikari.class */
    static class Hikari {
        Hikari() {
        }

        @ConditionalOnMissingBean
        @Bean
        HikariCheckpointRestoreLifecycle hikariCheckpointRestoreLifecycle(DataSource dataSource) {
            return new HikariCheckpointRestoreLifecycle(dataSource);
        }
    }
}

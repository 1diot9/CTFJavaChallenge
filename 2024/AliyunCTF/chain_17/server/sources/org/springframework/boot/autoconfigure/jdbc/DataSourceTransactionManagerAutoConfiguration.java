package org.springframework.boot.autoconfigure.jdbc;

import javax.sql.DataSource;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizationAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.TransactionManager;

@EnableConfigurationProperties({DataSourceProperties.class})
@AutoConfiguration(before = {TransactionAutoConfiguration.class}, after = {TransactionManagerCustomizationAutoConfiguration.class})
@ConditionalOnClass({JdbcTemplate.class, TransactionManager.class})
@AutoConfigureOrder(Integer.MAX_VALUE)
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jdbc/DataSourceTransactionManagerAutoConfiguration.class */
public class DataSourceTransactionManagerAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnSingleCandidate(DataSource.class)
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jdbc/DataSourceTransactionManagerAutoConfiguration$JdbcTransactionManagerConfiguration.class */
    static class JdbcTransactionManagerConfiguration {
        JdbcTransactionManagerConfiguration() {
        }

        @ConditionalOnMissingBean({TransactionManager.class})
        @Bean
        DataSourceTransactionManager transactionManager(Environment environment, DataSource dataSource, ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers) {
            DataSourceTransactionManager transactionManager = createTransactionManager(environment, dataSource);
            transactionManagerCustomizers.ifAvailable(customizers -> {
                customizers.customize((TransactionManager) transactionManager);
            });
            return transactionManager;
        }

        private DataSourceTransactionManager createTransactionManager(Environment environment, DataSource dataSource) {
            return ((Boolean) environment.getProperty("spring.dao.exceptiontranslation.enabled", Boolean.class, Boolean.TRUE)).booleanValue() ? new JdbcTransactionManager(dataSource) : new DataSourceTransactionManager(dataSource);
        }
    }
}

package org.springframework.boot.autoconfigure.jooq;

import java.util.Objects;
import javax.sql.DataSource;
import org.jooq.ConnectionProvider;
import org.jooq.DSLContext;
import org.jooq.ExecuteListenerProvider;
import org.jooq.TransactionProvider;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.transaction.PlatformTransactionManager;

@AutoConfiguration(after = {DataSourceAutoConfiguration.class, TransactionAutoConfiguration.class})
@ConditionalOnClass({DSLContext.class})
@ConditionalOnBean({DataSource.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jooq/JooqAutoConfiguration.class */
public class JooqAutoConfiguration {
    @ConditionalOnMissingBean({ConnectionProvider.class})
    @Bean
    public DataSourceConnectionProvider dataSourceConnectionProvider(DataSource dataSource) {
        return new DataSourceConnectionProvider(new TransactionAwareDataSourceProxy(dataSource));
    }

    @ConditionalOnMissingBean({TransactionProvider.class})
    @ConditionalOnBean({PlatformTransactionManager.class})
    @Bean
    public SpringTransactionProvider transactionProvider(PlatformTransactionManager txManager) {
        return new SpringTransactionProvider(txManager);
    }

    @Bean
    @Order(0)
    public DefaultExecuteListenerProvider jooqExceptionTranslatorExecuteListenerProvider() {
        return new DefaultExecuteListenerProvider(new JooqExceptionTranslator());
    }

    @ConditionalOnMissingBean({DSLContext.class})
    @EnableConfigurationProperties({JooqProperties.class})
    @Configuration(proxyBeanMethods = false)
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jooq/JooqAutoConfiguration$DslContextConfiguration.class */
    public static class DslContextConfiguration {
        @Bean
        public DefaultDSLContext dslContext(org.jooq.Configuration configuration) {
            return new DefaultDSLContext(configuration);
        }

        @ConditionalOnMissingBean({org.jooq.Configuration.class})
        @Bean
        public DefaultConfiguration jooqConfiguration(JooqProperties properties, ConnectionProvider connectionProvider, DataSource dataSource, ObjectProvider<TransactionProvider> transactionProvider, ObjectProvider<ExecuteListenerProvider> executeListenerProviders, ObjectProvider<DefaultConfigurationCustomizer> configurationCustomizers) {
            DefaultConfiguration configuration = new DefaultConfiguration();
            configuration.set(properties.determineSqlDialect(dataSource));
            configuration.set(connectionProvider);
            Objects.requireNonNull(configuration);
            transactionProvider.ifAvailable(configuration::set);
            configuration.set((ExecuteListenerProvider[]) executeListenerProviders.orderedStream().toArray(x$0 -> {
                return new ExecuteListenerProvider[x$0];
            }));
            configurationCustomizers.orderedStream().forEach(customizer -> {
                customizer.customize(configuration);
            });
            return configuration;
        }
    }
}

package org.springframework.boot.autoconfigure.r2dbc;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;

@AutoConfiguration(before = {TransactionAutoConfiguration.class})
@ConditionalOnClass({R2dbcTransactionManager.class, ReactiveTransactionManager.class})
@ConditionalOnSingleCandidate(ConnectionFactory.class)
@AutoConfigureOrder(Integer.MAX_VALUE)
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/r2dbc/R2dbcTransactionManagerAutoConfiguration.class */
public class R2dbcTransactionManagerAutoConfiguration {
    @ConditionalOnMissingBean({ReactiveTransactionManager.class})
    @Bean
    public R2dbcTransactionManager connectionFactoryTransactionManager(ConnectionFactory connectionFactory) {
        return new R2dbcTransactionManager(connectionFactory);
    }
}

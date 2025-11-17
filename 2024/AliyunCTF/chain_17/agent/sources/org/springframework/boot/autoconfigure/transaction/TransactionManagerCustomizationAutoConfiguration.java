package org.springframework.boot.autoconfigure.transaction;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionExecutionListener;

@EnableConfigurationProperties({TransactionProperties.class})
@ConditionalOnClass({PlatformTransactionManager.class})
@AutoConfiguration(before = {TransactionAutoConfiguration.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/transaction/TransactionManagerCustomizationAutoConfiguration.class */
public class TransactionManagerCustomizationAutoConfiguration {
    @ConditionalOnMissingBean
    @Bean
    TransactionManagerCustomizers platformTransactionManagerCustomizers(ObjectProvider<TransactionManagerCustomizer<?>> customizers) {
        return TransactionManagerCustomizers.of(customizers.orderedStream().toList());
    }

    @Bean
    ExecutionListenersTransactionManagerCustomizer transactionExecutionListeners(ObjectProvider<TransactionExecutionListener> listeners) {
        return new ExecutionListenersTransactionManagerCustomizer(listeners.orderedStream().toList());
    }
}

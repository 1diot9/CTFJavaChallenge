package org.springframework.boot.autoconfigure.transaction;

import org.springframework.aop.config.AopNamespaceUtils;
import org.springframework.boot.LazyInitializationExcludeFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.AbstractTransactionManagementConfiguration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.aspectj.AbstractTransactionAspect;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.transaction.support.TransactionOperations;
import org.springframework.transaction.support.TransactionTemplate;

@AutoConfiguration
@ConditionalOnClass({PlatformTransactionManager.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/transaction/TransactionAutoConfiguration.class */
public class TransactionAutoConfiguration {

    @ConditionalOnMissingBean({AbstractTransactionManagementConfiguration.class})
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnBean({TransactionManager.class})
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/transaction/TransactionAutoConfiguration$EnableTransactionManagementConfiguration.class */
    public static class EnableTransactionManagementConfiguration {

        @Configuration(proxyBeanMethods = false)
        @EnableTransactionManagement(proxyTargetClass = true)
        @ConditionalOnProperty(prefix = "spring.aop", name = {AopNamespaceUtils.PROXY_TARGET_CLASS_ATTRIBUTE}, havingValue = "true", matchIfMissing = true)
        /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/transaction/TransactionAutoConfiguration$EnableTransactionManagementConfiguration$CglibAutoProxyConfiguration.class */
        public static class CglibAutoProxyConfiguration {
        }

        @Configuration(proxyBeanMethods = false)
        @EnableTransactionManagement(proxyTargetClass = false)
        @ConditionalOnProperty(prefix = "spring.aop", name = {AopNamespaceUtils.PROXY_TARGET_CLASS_ATTRIBUTE}, havingValue = "false")
        /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/transaction/TransactionAutoConfiguration$EnableTransactionManagementConfiguration$JdkDynamicAutoProxyConfiguration.class */
        public static class JdkDynamicAutoProxyConfiguration {
        }
    }

    @ConditionalOnMissingBean
    @Bean
    @ConditionalOnSingleCandidate(ReactiveTransactionManager.class)
    public TransactionalOperator transactionalOperator(ReactiveTransactionManager transactionManager) {
        return TransactionalOperator.create(transactionManager);
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnSingleCandidate(PlatformTransactionManager.class)
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/transaction/TransactionAutoConfiguration$TransactionTemplateConfiguration.class */
    public static class TransactionTemplateConfiguration {
        @ConditionalOnMissingBean({TransactionOperations.class})
        @Bean
        public TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager) {
            return new TransactionTemplate(transactionManager);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnBean({AbstractTransactionAspect.class})
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/transaction/TransactionAutoConfiguration$AspectJTransactionManagementConfiguration.class */
    static class AspectJTransactionManagementConfiguration {
        AspectJTransactionManagementConfiguration() {
        }

        @Bean
        static LazyInitializationExcludeFilter eagerTransactionAspect() {
            return LazyInitializationExcludeFilter.forBeanTypes(AbstractTransactionAspect.class);
        }
    }
}

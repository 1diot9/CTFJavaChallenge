package org.springframework.boot.autoconfigure.context;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.DefaultLifecycleProcessor;

@EnableConfigurationProperties({LifecycleProperties.class})
@AutoConfiguration
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/context/LifecycleAutoConfiguration.class */
public class LifecycleAutoConfiguration {
    @ConditionalOnMissingBean(name = {AbstractApplicationContext.LIFECYCLE_PROCESSOR_BEAN_NAME}, search = SearchStrategy.CURRENT)
    @Bean(name = {AbstractApplicationContext.LIFECYCLE_PROCESSOR_BEAN_NAME})
    public DefaultLifecycleProcessor defaultLifecycleProcessor(LifecycleProperties properties) {
        DefaultLifecycleProcessor lifecycleProcessor = new DefaultLifecycleProcessor();
        lifecycleProcessor.setTimeoutPerShutdownPhase(properties.getTimeoutPerShutdownPhase().toMillis());
        return lifecycleProcessor;
    }
}

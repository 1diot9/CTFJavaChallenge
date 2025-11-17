package org.springframework.boot.autoconfigure.dao;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;

@AutoConfiguration
@ConditionalOnClass({PersistenceExceptionTranslationPostProcessor.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/dao/PersistenceExceptionTranslationAutoConfiguration.class */
public class PersistenceExceptionTranslationAutoConfiguration {
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "spring.dao.exceptiontranslation", name = {"enabled"}, matchIfMissing = true)
    @Bean
    public static PersistenceExceptionTranslationPostProcessor persistenceExceptionTranslationPostProcessor(Environment environment) {
        PersistenceExceptionTranslationPostProcessor postProcessor = new PersistenceExceptionTranslationPostProcessor();
        boolean proxyTargetClass = ((Boolean) environment.getProperty("spring.aop.proxy-target-class", Boolean.class, Boolean.TRUE)).booleanValue();
        postProcessor.setProxyTargetClass(proxyTargetClass);
        return postProcessor;
    }
}

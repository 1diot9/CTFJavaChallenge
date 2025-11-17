package org.springframework.boot.autoconfigure.aop;

import org.aspectj.weaver.Advice;
import org.springframework.aop.config.AopConfigUtils;
import org.springframework.aop.config.AopNamespaceUtils;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@AutoConfiguration
@ConditionalOnProperty(prefix = "spring.aop", name = {"auto"}, havingValue = "true", matchIfMissing = true)
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/aop/AopAutoConfiguration.class */
public class AopAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({Advice.class})
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/aop/AopAutoConfiguration$AspectJAutoProxyingConfiguration.class */
    static class AspectJAutoProxyingConfiguration {
        AspectJAutoProxyingConfiguration() {
        }

        @EnableAspectJAutoProxy(proxyTargetClass = false)
        @Configuration(proxyBeanMethods = false)
        @ConditionalOnProperty(prefix = "spring.aop", name = {AopNamespaceUtils.PROXY_TARGET_CLASS_ATTRIBUTE}, havingValue = "false")
        /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/aop/AopAutoConfiguration$AspectJAutoProxyingConfiguration$JdkDynamicAutoProxyConfiguration.class */
        static class JdkDynamicAutoProxyConfiguration {
            JdkDynamicAutoProxyConfiguration() {
            }
        }

        @EnableAspectJAutoProxy(proxyTargetClass = true)
        @Configuration(proxyBeanMethods = false)
        @ConditionalOnProperty(prefix = "spring.aop", name = {AopNamespaceUtils.PROXY_TARGET_CLASS_ATTRIBUTE}, havingValue = "true", matchIfMissing = true)
        /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/aop/AopAutoConfiguration$AspectJAutoProxyingConfiguration$CglibAutoProxyConfiguration.class */
        static class CglibAutoProxyConfiguration {
            CglibAutoProxyConfiguration() {
            }
        }
    }

    @ConditionalOnMissingClass({"org.aspectj.weaver.Advice"})
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(prefix = "spring.aop", name = {AopNamespaceUtils.PROXY_TARGET_CLASS_ATTRIBUTE}, havingValue = "true", matchIfMissing = true)
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/aop/AopAutoConfiguration$ClassProxyingConfiguration.class */
    static class ClassProxyingConfiguration {
        ClassProxyingConfiguration() {
        }

        @Bean
        static BeanFactoryPostProcessor forceAutoProxyCreatorToUseClassProxying() {
            return beanFactory -> {
                if (beanFactory instanceof BeanDefinitionRegistry) {
                    BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
                    AopConfigUtils.registerAutoProxyCreatorIfNecessary(registry);
                    AopConfigUtils.forceAutoProxyCreatorToUseClassProxying(registry);
                }
            };
        }
    }
}

package org.springframework.context.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.Aware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.EmbeddedValueResolver;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ApplicationStartupAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.lang.Nullable;
import org.springframework.util.StringValueResolver;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/support/ApplicationContextAwareProcessor.class */
public class ApplicationContextAwareProcessor implements BeanPostProcessor {
    private final ConfigurableApplicationContext applicationContext;
    private final StringValueResolver embeddedValueResolver;

    public ApplicationContextAwareProcessor(ConfigurableApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.embeddedValueResolver = new EmbeddedValueResolver(applicationContext.getBeanFactory());
    }

    @Override // org.springframework.beans.factory.config.BeanPostProcessor
    @Nullable
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (!(bean instanceof EnvironmentAware) && !(bean instanceof EmbeddedValueResolverAware) && !(bean instanceof ResourceLoaderAware) && !(bean instanceof ApplicationEventPublisherAware) && !(bean instanceof MessageSourceAware) && !(bean instanceof ApplicationContextAware) && !(bean instanceof ApplicationStartupAware)) {
            return bean;
        }
        invokeAwareInterfaces(bean);
        return bean;
    }

    private void invokeAwareInterfaces(Object bean) {
        if (bean instanceof Aware) {
            if (bean instanceof EnvironmentAware) {
                EnvironmentAware environmentAware = (EnvironmentAware) bean;
                environmentAware.setEnvironment(this.applicationContext.getEnvironment());
            }
            if (bean instanceof EmbeddedValueResolverAware) {
                EmbeddedValueResolverAware embeddedValueResolverAware = (EmbeddedValueResolverAware) bean;
                embeddedValueResolverAware.setEmbeddedValueResolver(this.embeddedValueResolver);
            }
            if (bean instanceof ResourceLoaderAware) {
                ResourceLoaderAware resourceLoaderAware = (ResourceLoaderAware) bean;
                resourceLoaderAware.setResourceLoader(this.applicationContext);
            }
            if (bean instanceof ApplicationEventPublisherAware) {
                ApplicationEventPublisherAware applicationEventPublisherAware = (ApplicationEventPublisherAware) bean;
                applicationEventPublisherAware.setApplicationEventPublisher(this.applicationContext);
            }
            if (bean instanceof MessageSourceAware) {
                MessageSourceAware messageSourceAware = (MessageSourceAware) bean;
                messageSourceAware.setMessageSource(this.applicationContext);
            }
            if (bean instanceof ApplicationStartupAware) {
                ApplicationStartupAware applicationStartupAware = (ApplicationStartupAware) bean;
                applicationStartupAware.setApplicationStartup(this.applicationContext.getApplicationStartup());
            }
            if (bean instanceof ApplicationContextAware) {
                ApplicationContextAware applicationContextAware = (ApplicationContextAware) bean;
                applicationContextAware.setApplicationContext(this.applicationContext);
            }
        }
    }
}

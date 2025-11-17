package org.springframework.context.support;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.lang.Nullable;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/support/ApplicationListenerDetector.class */
public class ApplicationListenerDetector implements DestructionAwareBeanPostProcessor, MergedBeanDefinitionPostProcessor {
    private static final Log logger = LogFactory.getLog((Class<?>) ApplicationListenerDetector.class);
    private final transient AbstractApplicationContext applicationContext;
    private final transient Map<String, Boolean> singletonNames = new ConcurrentHashMap(256);

    public ApplicationListenerDetector(AbstractApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override // org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor
    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
        if (ApplicationListener.class.isAssignableFrom(beanType)) {
            this.singletonNames.put(beanName, Boolean.valueOf(beanDefinition.isSingleton()));
        }
    }

    @Override // org.springframework.beans.factory.config.BeanPostProcessor
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    @Override // org.springframework.beans.factory.config.BeanPostProcessor
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (bean instanceof ApplicationListener) {
            ApplicationListener<?> applicationListener = (ApplicationListener) bean;
            Boolean flag = this.singletonNames.get(beanName);
            if (Boolean.TRUE.equals(flag)) {
                this.applicationContext.addApplicationListener(applicationListener);
            } else if (Boolean.FALSE.equals(flag)) {
                if (logger.isWarnEnabled() && !this.applicationContext.containsBean(beanName)) {
                    logger.warn("Inner bean '" + beanName + "' implements ApplicationListener interface but is not reachable for event multicasting by its containing ApplicationContext because it does not have singleton scope. Only top-level listener beans are allowed to be of non-singleton scope.");
                }
                this.singletonNames.remove(beanName);
            }
        }
        return bean;
    }

    @Override // org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor
    public void postProcessBeforeDestruction(Object bean, String beanName) {
        if (bean instanceof ApplicationListener) {
            ApplicationListener<?> applicationListener = (ApplicationListener) bean;
            try {
                ApplicationEventMulticaster multicaster = this.applicationContext.getApplicationEventMulticaster();
                multicaster.removeApplicationListener(applicationListener);
                multicaster.removeApplicationListenerBean(beanName);
            } catch (IllegalStateException e) {
            }
        }
    }

    @Override // org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor
    public boolean requiresDestruction(Object bean) {
        return bean instanceof ApplicationListener;
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof ApplicationListenerDetector) {
                ApplicationListenerDetector that = (ApplicationListenerDetector) other;
                if (this.applicationContext == that.applicationContext) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return Objects.hashCode(this.applicationContext);
    }
}

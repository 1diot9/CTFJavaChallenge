package org.springframework.beans.factory.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/config/DeprecatedBeanWarner.class */
public class DeprecatedBeanWarner implements BeanFactoryPostProcessor {
    protected transient Log logger = LogFactory.getLog(getClass());

    public void setLoggerName(String loggerName) {
        this.logger = LogFactory.getLog(loggerName);
    }

    @Override // org.springframework.beans.factory.config.BeanFactoryPostProcessor
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (isLogEnabled()) {
            String[] beanNames = beanFactory.getBeanDefinitionNames();
            for (String beanName : beanNames) {
                String nameToLookup = beanName;
                if (beanFactory.isFactoryBean(beanName)) {
                    nameToLookup = "&" + beanName;
                }
                Class<?> beanType = beanFactory.getType(nameToLookup);
                if (beanType != null) {
                    Class<?> userClass = ClassUtils.getUserClass(beanType);
                    if (userClass.isAnnotationPresent(Deprecated.class)) {
                        BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
                        logDeprecatedBean(beanName, beanType, beanDefinition);
                    }
                }
            }
        }
    }

    protected void logDeprecatedBean(String beanName, Class<?> beanType, BeanDefinition beanDefinition) {
        StringBuilder builder = new StringBuilder();
        builder.append(beanType);
        builder.append(" ['");
        builder.append(beanName);
        builder.append('\'');
        String resourceDescription = beanDefinition.getResourceDescription();
        if (StringUtils.hasLength(resourceDescription)) {
            builder.append(" in ");
            builder.append(resourceDescription);
        }
        builder.append("] has been deprecated");
        writeToLog(builder.toString());
    }

    protected void writeToLog(String message) {
        this.logger.warn(message);
    }

    protected boolean isLogEnabled() {
        return this.logger.isWarnEnabled();
    }
}

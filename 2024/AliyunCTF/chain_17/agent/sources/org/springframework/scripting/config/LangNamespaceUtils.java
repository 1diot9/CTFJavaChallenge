package org.springframework.scripting.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.scripting.support.ScriptFactoryPostProcessor;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scripting/config/LangNamespaceUtils.class */
public abstract class LangNamespaceUtils {
    private static final String SCRIPT_FACTORY_POST_PROCESSOR_BEAN_NAME = "org.springframework.scripting.config.scriptFactoryPostProcessor";

    public static BeanDefinition registerScriptFactoryPostProcessorIfNecessary(BeanDefinitionRegistry registry) {
        BeanDefinition beanDefinition;
        if (registry.containsBeanDefinition(SCRIPT_FACTORY_POST_PROCESSOR_BEAN_NAME)) {
            beanDefinition = registry.getBeanDefinition(SCRIPT_FACTORY_POST_PROCESSOR_BEAN_NAME);
        } else {
            beanDefinition = new RootBeanDefinition((Class<?>) ScriptFactoryPostProcessor.class);
            registry.registerBeanDefinition(SCRIPT_FACTORY_POST_PROCESSOR_BEAN_NAME, beanDefinition);
        }
        return beanDefinition;
    }
}

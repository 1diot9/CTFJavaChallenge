package org.springframework.beans.factory.support;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/BeanDefinitionReader.class */
public interface BeanDefinitionReader {
    BeanDefinitionRegistry getRegistry();

    @Nullable
    ResourceLoader getResourceLoader();

    @Nullable
    ClassLoader getBeanClassLoader();

    BeanNameGenerator getBeanNameGenerator();

    int loadBeanDefinitions(Resource resource) throws BeanDefinitionStoreException;

    int loadBeanDefinitions(Resource... resources) throws BeanDefinitionStoreException;

    int loadBeanDefinitions(String location) throws BeanDefinitionStoreException;

    int loadBeanDefinitions(String... locations) throws BeanDefinitionStoreException;
}

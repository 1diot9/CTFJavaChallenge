package org.springframework.beans.factory.parsing;

import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanReference;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/parsing/ComponentDefinition.class */
public interface ComponentDefinition extends BeanMetadataElement {
    String getName();

    String getDescription();

    BeanDefinition[] getBeanDefinitions();

    BeanDefinition[] getInnerBeanDefinitions();

    BeanReference[] getBeanReferences();
}

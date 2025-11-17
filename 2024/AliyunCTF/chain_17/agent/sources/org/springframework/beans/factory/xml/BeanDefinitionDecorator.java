package org.springframework.beans.factory.xml;

import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.w3c.dom.Node;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/xml/BeanDefinitionDecorator.class */
public interface BeanDefinitionDecorator {
    BeanDefinitionHolder decorate(Node node, BeanDefinitionHolder definition, ParserContext parserContext);
}

package org.springframework.beans.factory.xml;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.lang.Nullable;
import org.w3c.dom.Element;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/xml/BeanDefinitionParser.class */
public interface BeanDefinitionParser {
    @Nullable
    BeanDefinition parse(Element element, ParserContext parserContext);
}

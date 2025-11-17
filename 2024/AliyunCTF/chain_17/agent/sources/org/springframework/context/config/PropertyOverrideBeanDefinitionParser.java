package org.springframework.context.config;

import org.springframework.beans.factory.config.PropertyOverrideConfigurer;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/config/PropertyOverrideBeanDefinitionParser.class */
class PropertyOverrideBeanDefinitionParser extends AbstractPropertyLoadingBeanDefinitionParser {
    @Override // org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser
    protected Class<?> getBeanClass(Element element) {
        return PropertyOverrideConfigurer.class;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.context.config.AbstractPropertyLoadingBeanDefinitionParser, org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser
    public void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        super.doParse(element, parserContext, builder);
        builder.addPropertyValue("ignoreInvalidKeys", Boolean.valueOf(element.getAttribute("ignore-unresolvable")));
    }
}

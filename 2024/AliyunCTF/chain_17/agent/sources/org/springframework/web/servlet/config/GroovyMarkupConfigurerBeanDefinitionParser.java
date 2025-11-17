package org.springframework.web.servlet.config;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/config/GroovyMarkupConfigurerBeanDefinitionParser.class */
public class GroovyMarkupConfigurerBeanDefinitionParser extends AbstractSimpleBeanDefinitionParser {
    public static final String BEAN_NAME = "mvcGroovyMarkupConfigurer";

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.beans.factory.xml.AbstractBeanDefinitionParser
    public String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) {
        return BEAN_NAME;
    }

    @Override // org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser
    protected String getBeanClassName(Element element) {
        return "org.springframework.web.servlet.view.groovy.GroovyMarkupConfigurer";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser
    public boolean isEligibleAttribute(String name) {
        return name.equals("auto-indent") || name.equals("cache-templates") || name.equals("resource-loader-path");
    }
}

package org.springframework.aop.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.lang.Nullable;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/config/AspectJAutoProxyBeanDefinitionParser.class */
class AspectJAutoProxyBeanDefinitionParser implements BeanDefinitionParser {
    @Override // org.springframework.beans.factory.xml.BeanDefinitionParser
    @Nullable
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        AopNamespaceUtils.registerAspectJAnnotationAutoProxyCreatorIfNecessary(parserContext, element);
        extendBeanDefinition(element, parserContext);
        return null;
    }

    private void extendBeanDefinition(Element element, ParserContext parserContext) {
        BeanDefinition beanDef = parserContext.getRegistry().getBeanDefinition(AopConfigUtils.AUTO_PROXY_CREATOR_BEAN_NAME);
        if (element.hasChildNodes()) {
            addIncludePatterns(element, parserContext, beanDef);
        }
    }

    private void addIncludePatterns(Element element, ParserContext parserContext, BeanDefinition beanDef) {
        ManagedList<TypedStringValue> includePatterns = new ManagedList<>();
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node instanceof Element) {
                Element includeElement = (Element) node;
                TypedStringValue valueHolder = new TypedStringValue(includeElement.getAttribute("name"));
                valueHolder.setSource(parserContext.extractSource(includeElement));
                includePatterns.add(valueHolder);
            }
        }
        if (!includePatterns.isEmpty()) {
            includePatterns.setSource(parserContext.extractSource(element));
            beanDef.getPropertyValues().add("includePatterns", includePatterns);
        }
    }
}

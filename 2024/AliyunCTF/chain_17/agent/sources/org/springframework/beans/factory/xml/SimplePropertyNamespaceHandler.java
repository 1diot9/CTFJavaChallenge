package org.springframework.beans.factory.xml;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.core.Conventions;
import org.springframework.lang.Nullable;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/xml/SimplePropertyNamespaceHandler.class */
public class SimplePropertyNamespaceHandler implements NamespaceHandler {
    private static final String REF_SUFFIX = "-ref";

    @Override // org.springframework.beans.factory.xml.NamespaceHandler
    public void init() {
    }

    @Override // org.springframework.beans.factory.xml.NamespaceHandler
    @Nullable
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        parserContext.getReaderContext().error("Class [" + getClass().getName() + "] does not support custom elements.", element);
        return null;
    }

    @Override // org.springframework.beans.factory.xml.NamespaceHandler
    public BeanDefinitionHolder decorate(Node node, BeanDefinitionHolder definition, ParserContext parserContext) {
        if (node instanceof Attr) {
            Attr attr = (Attr) node;
            String propertyName = parserContext.getDelegate().getLocalName(attr);
            String propertyValue = attr.getValue();
            MutablePropertyValues pvs = definition.getBeanDefinition().getPropertyValues();
            if (pvs.contains(propertyName)) {
                parserContext.getReaderContext().error("Property '" + propertyName + "' is already defined using both <property> and inline syntax. Only one approach may be used per property.", attr);
            }
            if (propertyName.endsWith(REF_SUFFIX)) {
                pvs.add(Conventions.attributeNameToPropertyName(propertyName.substring(0, propertyName.length() - REF_SUFFIX.length())), new RuntimeBeanReference(propertyValue));
            } else {
                pvs.add(Conventions.attributeNameToPropertyName(propertyName), propertyValue);
            }
        }
        return definition;
    }
}

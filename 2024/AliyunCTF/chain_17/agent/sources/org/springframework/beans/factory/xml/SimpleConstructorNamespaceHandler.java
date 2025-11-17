package org.springframework.beans.factory.xml;

import java.util.Collection;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.core.Conventions;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/xml/SimpleConstructorNamespaceHandler.class */
public class SimpleConstructorNamespaceHandler implements NamespaceHandler {
    private static final String REF_SUFFIX = "-ref";
    private static final String DELIMITER_PREFIX = "_";

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
            String argName = parserContext.getDelegate().getLocalName(attr).strip();
            String argValue = attr.getValue().strip();
            ConstructorArgumentValues cvs = definition.getBeanDefinition().getConstructorArgumentValues();
            boolean ref = false;
            if (argName.endsWith(REF_SUFFIX)) {
                ref = true;
                argName = argName.substring(0, argName.length() - REF_SUFFIX.length());
            }
            ConstructorArgumentValues.ValueHolder valueHolder = new ConstructorArgumentValues.ValueHolder(ref ? new RuntimeBeanReference(argValue) : argValue);
            valueHolder.setSource(parserContext.getReaderContext().extractSource(attr));
            if (argName.startsWith("_")) {
                String arg = argName.substring(1).trim();
                if (!StringUtils.hasText(arg)) {
                    cvs.addGenericArgumentValue(valueHolder);
                } else {
                    int index = -1;
                    try {
                        index = Integer.parseInt(arg);
                    } catch (NumberFormatException e) {
                        parserContext.getReaderContext().error("Constructor argument '" + argName + "' specifies an invalid integer", attr);
                    }
                    if (index < 0) {
                        parserContext.getReaderContext().error("Constructor argument '" + argName + "' specifies a negative index", attr);
                    }
                    if (cvs.hasIndexedArgumentValue(index)) {
                        parserContext.getReaderContext().error("Constructor argument '" + argName + "' with index " + index + " already defined using <constructor-arg>. Only one approach may be used per argument.", attr);
                    }
                    cvs.addIndexedArgumentValue(index, valueHolder);
                }
            } else {
                String name = Conventions.attributeNameToPropertyName(argName);
                if (containsArgWithName(name, cvs)) {
                    parserContext.getReaderContext().error("Constructor argument '" + argName + "' already defined using <constructor-arg>. Only one approach may be used per argument.", attr);
                }
                valueHolder.setName(Conventions.attributeNameToPropertyName(argName));
                cvs.addGenericArgumentValue(valueHolder);
            }
        }
        return definition;
    }

    private boolean containsArgWithName(String name, ConstructorArgumentValues cvs) {
        return checkName(name, cvs.getGenericArgumentValues()) || checkName(name, cvs.getIndexedArgumentValues().values());
    }

    private boolean checkName(String name, Collection<ConstructorArgumentValues.ValueHolder> values) {
        for (ConstructorArgumentValues.ValueHolder holder : values) {
            if (name.equals(holder.getName())) {
                return true;
            }
        }
        return false;
    }
}

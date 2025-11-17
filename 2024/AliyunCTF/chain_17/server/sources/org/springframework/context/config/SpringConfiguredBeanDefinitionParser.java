package org.springframework.context.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.lang.Nullable;
import org.w3c.dom.Element;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/config/SpringConfiguredBeanDefinitionParser.class */
class SpringConfiguredBeanDefinitionParser implements BeanDefinitionParser {
    public static final String BEAN_CONFIGURER_ASPECT_BEAN_NAME = "org.springframework.context.config.internalBeanConfigurerAspect";
    static final String BEAN_CONFIGURER_ASPECT_CLASS_NAME = "org.springframework.beans.factory.aspectj.AnnotationBeanConfigurerAspect";

    @Override // org.springframework.beans.factory.xml.BeanDefinitionParser
    @Nullable
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        if (!parserContext.getRegistry().containsBeanDefinition("org.springframework.context.config.internalBeanConfigurerAspect")) {
            RootBeanDefinition def = new RootBeanDefinition();
            def.setBeanClassName(BEAN_CONFIGURER_ASPECT_CLASS_NAME);
            def.setFactoryMethodName("aspectOf");
            def.setRole(2);
            def.setSource(parserContext.extractSource(element));
            parserContext.registerBeanComponent(new BeanComponentDefinition(def, "org.springframework.context.config.internalBeanConfigurerAspect"));
            return null;
        }
        return null;
    }
}

package org.springframework.context.config;

import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.weaving.AspectJWeavingEnabler;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.w3c.dom.Element;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/config/LoadTimeWeaverBeanDefinitionParser.class */
class LoadTimeWeaverBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {
    public static final String ASPECTJ_WEAVING_ENABLER_BEAN_NAME = "org.springframework.context.config.internalAspectJWeavingEnabler";
    private static final String ASPECTJ_WEAVING_ENABLER_CLASS_NAME = "org.springframework.context.weaving.AspectJWeavingEnabler";
    private static final String DEFAULT_LOAD_TIME_WEAVER_CLASS_NAME = "org.springframework.context.weaving.DefaultContextLoadTimeWeaver";
    private static final String WEAVER_CLASS_ATTRIBUTE = "weaver-class";
    private static final String ASPECTJ_WEAVING_ATTRIBUTE = "aspectj-weaving";

    @Override // org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser
    protected String getBeanClassName(Element element) {
        if (element.hasAttribute(WEAVER_CLASS_ATTRIBUTE)) {
            return element.getAttribute(WEAVER_CLASS_ATTRIBUTE);
        }
        return DEFAULT_LOAD_TIME_WEAVER_CLASS_NAME;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.beans.factory.xml.AbstractBeanDefinitionParser
    public String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) {
        return ConfigurableApplicationContext.LOAD_TIME_WEAVER_BEAN_NAME;
    }

    @Override // org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        builder.setRole(2);
        if (isAspectJWeavingEnabled(element.getAttribute(ASPECTJ_WEAVING_ATTRIBUTE), parserContext)) {
            if (!parserContext.getRegistry().containsBeanDefinition(ASPECTJ_WEAVING_ENABLER_BEAN_NAME)) {
                RootBeanDefinition def = new RootBeanDefinition(ASPECTJ_WEAVING_ENABLER_CLASS_NAME);
                parserContext.registerBeanComponent(new BeanComponentDefinition(def, ASPECTJ_WEAVING_ENABLER_BEAN_NAME));
            }
            if (isBeanConfigurerAspectEnabled(parserContext.getReaderContext().getBeanClassLoader())) {
                new SpringConfiguredBeanDefinitionParser().parse(element, parserContext);
            }
        }
    }

    protected boolean isAspectJWeavingEnabled(String value, ParserContext parserContext) {
        ClassLoader cl;
        if (CustomBooleanEditor.VALUE_ON.equals(value)) {
            return true;
        }
        return (CustomBooleanEditor.VALUE_OFF.equals(value) || (cl = parserContext.getReaderContext().getBeanClassLoader()) == null || cl.getResource(AspectJWeavingEnabler.ASPECTJ_AOP_XML_RESOURCE) == null) ? false : true;
    }

    protected boolean isBeanConfigurerAspectEnabled(@Nullable ClassLoader beanClassLoader) {
        return ClassUtils.isPresent("org.springframework.beans.factory.aspectj.AnnotationBeanConfigurerAspect", beanClassLoader);
    }
}

package org.springframework.ejb.config;

import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/ejb/config/AbstractJndiLocatingBeanDefinitionParser.class */
abstract class AbstractJndiLocatingBeanDefinitionParser extends AbstractSimpleBeanDefinitionParser {
    public static final String ENVIRONMENT = "environment";
    public static final String ENVIRONMENT_REF = "environment-ref";
    public static final String JNDI_ENVIRONMENT = "jndiEnvironment";

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser
    public boolean isEligibleAttribute(String attributeName) {
        return (!super.isEligibleAttribute(attributeName) || ENVIRONMENT_REF.equals(attributeName) || BeanDefinitionParserDelegate.LAZY_INIT_ATTRIBUTE.equals(attributeName)) ? false : true;
    }

    @Override // org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser
    protected void postProcess(BeanDefinitionBuilder definitionBuilder, Element element) {
        Object envValue = DomUtils.getChildElementValueByTagName(element, "environment");
        if (envValue != null) {
            definitionBuilder.addPropertyValue(JNDI_ENVIRONMENT, envValue);
        } else {
            String envRef = element.getAttribute(ENVIRONMENT_REF);
            if (StringUtils.hasLength(envRef)) {
                definitionBuilder.addPropertyValue(JNDI_ENVIRONMENT, new RuntimeBeanReference(envRef));
            }
        }
        String lazyInit = element.getAttribute(BeanDefinitionParserDelegate.LAZY_INIT_ATTRIBUTE);
        if (StringUtils.hasText(lazyInit) && !"default".equals(lazyInit)) {
            definitionBuilder.setLazyInit("true".equals(lazyInit));
        }
    }
}

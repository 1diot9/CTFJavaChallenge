package org.springframework.ejb.config;

import org.springframework.beans.BeanUtils;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.w3c.dom.Element;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/ejb/config/RemoteStatelessSessionBeanDefinitionParser.class */
class RemoteStatelessSessionBeanDefinitionParser extends AbstractJndiLocatingBeanDefinitionParser {
    @Override // org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser
    protected Class<?> getBeanClass(Element element) {
        return JndiObjectFactoryBean.class;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.ejb.config.AbstractJndiLocatingBeanDefinitionParser, org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser
    public boolean isEligibleAttribute(String attributeName) {
        return super.isEligibleAttribute(attributeName) && BeanUtils.getPropertyDescriptor(JndiObjectFactoryBean.class, extractPropertyName(attributeName)) != null;
    }
}

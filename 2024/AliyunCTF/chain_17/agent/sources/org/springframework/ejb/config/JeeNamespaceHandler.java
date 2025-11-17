package org.springframework.ejb.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/ejb/config/JeeNamespaceHandler.class */
public class JeeNamespaceHandler extends NamespaceHandlerSupport {
    @Override // org.springframework.beans.factory.xml.NamespaceHandler
    public void init() {
        registerBeanDefinitionParser("jndi-lookup", new JndiLookupBeanDefinitionParser());
        registerBeanDefinitionParser("local-slsb", new LocalStatelessSessionBeanDefinitionParser());
        registerBeanDefinitionParser("remote-slsb", new RemoteStatelessSessionBeanDefinitionParser());
    }
}

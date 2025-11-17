package org.springframework.web.servlet.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/config/MvcNamespaceHandler.class */
public class MvcNamespaceHandler extends NamespaceHandlerSupport {
    @Override // org.springframework.beans.factory.xml.NamespaceHandler
    public void init() {
        registerBeanDefinitionParser("annotation-driven", new AnnotationDrivenBeanDefinitionParser());
        registerBeanDefinitionParser("default-servlet-handler", new DefaultServletHandlerBeanDefinitionParser());
        registerBeanDefinitionParser("interceptors", new InterceptorsBeanDefinitionParser());
        registerBeanDefinitionParser("resources", new ResourcesBeanDefinitionParser());
        registerBeanDefinitionParser("view-controller", new ViewControllerBeanDefinitionParser());
        registerBeanDefinitionParser("redirect-view-controller", new ViewControllerBeanDefinitionParser());
        registerBeanDefinitionParser("status-controller", new ViewControllerBeanDefinitionParser());
        registerBeanDefinitionParser("view-resolvers", new ViewResolversBeanDefinitionParser());
        registerBeanDefinitionParser("freemarker-configurer", new FreeMarkerConfigurerBeanDefinitionParser());
        registerBeanDefinitionParser("groovy-configurer", new GroovyMarkupConfigurerBeanDefinitionParser());
        registerBeanDefinitionParser("script-template-configurer", new ScriptTemplateConfigurerBeanDefinitionParser());
        registerBeanDefinitionParser("cors", new CorsBeanDefinitionParser());
    }
}

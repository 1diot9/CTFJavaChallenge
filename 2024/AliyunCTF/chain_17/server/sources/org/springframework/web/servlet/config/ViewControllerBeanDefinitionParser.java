package org.springframework.web.servlet.config;

import java.util.Map;
import org.apache.catalina.filters.RateLimitFilter;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.http.HttpStatusCode;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.ParameterizableViewController;
import org.springframework.web.servlet.view.RedirectView;
import org.w3c.dom.Element;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/config/ViewControllerBeanDefinitionParser.class */
class ViewControllerBeanDefinitionParser implements BeanDefinitionParser {
    private static final String HANDLER_MAPPING_BEAN_NAME = "org.springframework.web.servlet.config.viewControllerHandlerMapping";

    @Override // org.springframework.beans.factory.xml.BeanDefinitionParser
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        Object source = parserContext.extractSource(element);
        BeanDefinition hm = registerHandlerMapping(parserContext, source);
        MvcNamespaceUtils.registerDefaultComponents(parserContext, source);
        RootBeanDefinition controller = new RootBeanDefinition((Class<?>) ParameterizableViewController.class);
        controller.setSource(source);
        HttpStatusCode statusCode = null;
        if (element.hasAttribute("status-code")) {
            int statusValue = Integer.parseInt(element.getAttribute("status-code"));
            statusCode = HttpStatusCode.valueOf(statusValue);
        }
        String name = element.getLocalName();
        boolean z = -1;
        switch (name.hashCode()) {
            case -1611991977:
                if (name.equals("status-controller")) {
                    z = 2;
                    break;
                }
                break;
            case -971002925:
                if (name.equals("redirect-view-controller")) {
                    z = true;
                    break;
                }
                break;
            case 244444100:
                if (name.equals("view-controller")) {
                    z = false;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
                if (element.hasAttribute("view-name")) {
                    controller.getPropertyValues().add("viewName", element.getAttribute("view-name"));
                }
                if (statusCode != null) {
                    controller.getPropertyValues().add(RateLimitFilter.PARAM_STATUS_CODE, statusCode);
                    break;
                }
                break;
            case true:
                controller.getPropertyValues().add("view", getRedirectView(element, statusCode, source));
                break;
            case true:
                controller.getPropertyValues().add(RateLimitFilter.PARAM_STATUS_CODE, statusCode);
                controller.getPropertyValues().add("statusOnly", true);
                break;
            default:
                throw new IllegalStateException("Unexpected tag name: " + name);
        }
        Map<String, BeanDefinition> urlMap = (Map) hm.getPropertyValues().get("urlMap");
        if (urlMap == null) {
            urlMap = new ManagedMap<>();
            hm.getPropertyValues().add("urlMap", urlMap);
        }
        urlMap.put(element.getAttribute("path"), controller);
        return null;
    }

    private BeanDefinition registerHandlerMapping(ParserContext context, @Nullable Object source) {
        if (context.getRegistry().containsBeanDefinition(HANDLER_MAPPING_BEAN_NAME)) {
            return context.getRegistry().getBeanDefinition(HANDLER_MAPPING_BEAN_NAME);
        }
        RootBeanDefinition beanDef = new RootBeanDefinition((Class<?>) SimpleUrlHandlerMapping.class);
        beanDef.setRole(2);
        context.getRegistry().registerBeanDefinition(HANDLER_MAPPING_BEAN_NAME, beanDef);
        context.registerComponent(new BeanComponentDefinition(beanDef, HANDLER_MAPPING_BEAN_NAME));
        beanDef.setSource(source);
        beanDef.getPropertyValues().add(AbstractBeanDefinition.ORDER_ATTRIBUTE, CustomBooleanEditor.VALUE_1);
        beanDef.getPropertyValues().add("pathMatcher", MvcNamespaceUtils.registerPathMatcher(null, context, source));
        beanDef.getPropertyValues().add("urlPathHelper", MvcNamespaceUtils.registerUrlPathHelper(null, context, source));
        RuntimeBeanReference corsConfigurationsRef = MvcNamespaceUtils.registerCorsConfigurations(null, context, source);
        beanDef.getPropertyValues().add("corsConfigurations", corsConfigurationsRef);
        return beanDef;
    }

    private RootBeanDefinition getRedirectView(Element element, @Nullable HttpStatusCode status, @Nullable Object source) {
        RootBeanDefinition redirectView = new RootBeanDefinition((Class<?>) RedirectView.class);
        redirectView.setSource(source);
        redirectView.getConstructorArgumentValues().addIndexedArgumentValue(0, element.getAttribute("redirect-url"));
        if (status != null) {
            redirectView.getPropertyValues().add(RateLimitFilter.PARAM_STATUS_CODE, status);
        }
        if (element.hasAttribute("context-relative")) {
            redirectView.getPropertyValues().add("contextRelative", element.getAttribute("context-relative"));
        } else {
            redirectView.getPropertyValues().add("contextRelative", true);
        }
        if (element.hasAttribute("keep-query-params")) {
            redirectView.getPropertyValues().add("propagateQueryParams", element.getAttribute("keep-query-params"));
        }
        return redirectView;
    }
}

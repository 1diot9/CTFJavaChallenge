package org.springframework.web.jsf;

import jakarta.faces.application.NavigationHandler;
import jakarta.faces.context.FacesContext;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.lang.Nullable;
import org.springframework.web.context.WebApplicationContext;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/jsf/DelegatingNavigationHandlerProxy.class */
public class DelegatingNavigationHandlerProxy extends NavigationHandler {
    public static final String DEFAULT_TARGET_BEAN_NAME = "jsfNavigationHandler";

    @Nullable
    private NavigationHandler originalNavigationHandler;

    public DelegatingNavigationHandlerProxy() {
    }

    public DelegatingNavigationHandlerProxy(NavigationHandler originalNavigationHandler) {
        this.originalNavigationHandler = originalNavigationHandler;
    }

    public void handleNavigation(FacesContext facesContext, String fromAction, String outcome) {
        NavigationHandler handler = getDelegate(facesContext);
        if (handler instanceof DecoratingNavigationHandler) {
            DecoratingNavigationHandler decoratingNavigationHandler = (DecoratingNavigationHandler) handler;
            decoratingNavigationHandler.handleNavigation(facesContext, fromAction, outcome, this.originalNavigationHandler);
        } else {
            handler.handleNavigation(facesContext, fromAction, outcome);
        }
    }

    protected NavigationHandler getDelegate(FacesContext facesContext) {
        String targetBeanName = getTargetBeanName(facesContext);
        return (NavigationHandler) getBeanFactory(facesContext).getBean(targetBeanName, NavigationHandler.class);
    }

    protected String getTargetBeanName(FacesContext facesContext) {
        return DEFAULT_TARGET_BEAN_NAME;
    }

    protected BeanFactory getBeanFactory(FacesContext facesContext) {
        return getWebApplicationContext(facesContext);
    }

    protected WebApplicationContext getWebApplicationContext(FacesContext facesContext) {
        return FacesContextUtils.getRequiredWebApplicationContext(facesContext);
    }
}

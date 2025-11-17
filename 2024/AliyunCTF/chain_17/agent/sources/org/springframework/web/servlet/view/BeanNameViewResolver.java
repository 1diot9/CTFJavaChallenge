package org.springframework.web.servlet.view;

import java.util.Locale;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;
import org.springframework.web.context.support.WebApplicationObjectSupport;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/view/BeanNameViewResolver.class */
public class BeanNameViewResolver extends WebApplicationObjectSupport implements ViewResolver, Ordered {
    private int order = Integer.MAX_VALUE;

    public void setOrder(int order) {
        this.order = order;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return this.order;
    }

    @Override // org.springframework.web.servlet.ViewResolver
    @Nullable
    public View resolveViewName(String viewName, Locale locale) throws BeansException {
        ApplicationContext context = obtainApplicationContext();
        if (!context.containsBean(viewName)) {
            return null;
        }
        if (!context.isTypeMatch(viewName, View.class)) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Found bean named '" + viewName + "' but it does not implement View");
                return null;
            }
            return null;
        }
        return (View) context.getBean(viewName, View.class);
    }
}

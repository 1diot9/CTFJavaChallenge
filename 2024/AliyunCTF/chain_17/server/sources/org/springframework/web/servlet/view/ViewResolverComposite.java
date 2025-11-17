package org.springframework.web.servlet.view;

import jakarta.servlet.ServletContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/view/ViewResolverComposite.class */
public class ViewResolverComposite implements ViewResolver, Ordered, InitializingBean, ApplicationContextAware, ServletContextAware {
    private final List<ViewResolver> viewResolvers = new ArrayList();
    private int order = Integer.MAX_VALUE;

    public void setViewResolvers(List<ViewResolver> viewResolvers) {
        this.viewResolvers.clear();
        if (!CollectionUtils.isEmpty(viewResolvers)) {
            this.viewResolvers.addAll(viewResolvers);
        }
    }

    public List<ViewResolver> getViewResolvers() {
        return Collections.unmodifiableList(this.viewResolvers);
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return this.order;
    }

    @Override // org.springframework.context.ApplicationContextAware
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        for (ViewResolver viewResolver : this.viewResolvers) {
            if (viewResolver instanceof ApplicationContextAware) {
                ApplicationContextAware applicationContextAware = (ApplicationContextAware) viewResolver;
                applicationContextAware.setApplicationContext(applicationContext);
            }
        }
    }

    @Override // org.springframework.web.context.ServletContextAware
    public void setServletContext(ServletContext servletContext) {
        for (ViewResolver viewResolver : this.viewResolvers) {
            if (viewResolver instanceof ServletContextAware) {
                ServletContextAware servletContextAware = (ServletContextAware) viewResolver;
                servletContextAware.setServletContext(servletContext);
            }
        }
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws Exception {
        for (ViewResolver viewResolver : this.viewResolvers) {
            if (viewResolver instanceof InitializingBean) {
                InitializingBean initializingBean = (InitializingBean) viewResolver;
                initializingBean.afterPropertiesSet();
            }
        }
    }

    @Override // org.springframework.web.servlet.ViewResolver
    @Nullable
    public View resolveViewName(String viewName, Locale locale) throws Exception {
        for (ViewResolver viewResolver : this.viewResolvers) {
            View view = viewResolver.resolveViewName(viewName, locale);
            if (view != null) {
                return view;
            }
        }
        return null;
    }
}

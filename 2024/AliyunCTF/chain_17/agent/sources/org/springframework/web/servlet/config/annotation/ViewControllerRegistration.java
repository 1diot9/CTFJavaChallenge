package org.springframework.web.servlet.config.annotation;

import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatusCode;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/config/annotation/ViewControllerRegistration.class */
public class ViewControllerRegistration {
    private final String urlPath;
    private final ParameterizableViewController controller = new ParameterizableViewController();

    public ViewControllerRegistration(String urlPath) {
        Assert.notNull(urlPath, "'urlPath' is required.");
        this.urlPath = urlPath;
    }

    public ViewControllerRegistration setStatusCode(HttpStatusCode statusCode) {
        this.controller.setStatusCode(statusCode);
        return this;
    }

    public void setViewName(String viewName) {
        this.controller.setViewName(viewName);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setApplicationContext(@Nullable ApplicationContext applicationContext) {
        this.controller.setApplicationContext(applicationContext);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String getUrlPath() {
        return this.urlPath;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ParameterizableViewController getViewController() {
        return this.controller;
    }
}

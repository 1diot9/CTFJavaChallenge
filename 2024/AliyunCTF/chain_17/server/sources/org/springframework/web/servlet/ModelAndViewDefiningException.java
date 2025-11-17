package org.springframework.web.servlet;

import jakarta.servlet.ServletException;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/ModelAndViewDefiningException.class */
public class ModelAndViewDefiningException extends ServletException {
    private final ModelAndView modelAndView;

    public ModelAndViewDefiningException(ModelAndView modelAndView) {
        Assert.notNull(modelAndView, "ModelAndView must not be null in ModelAndViewDefiningException");
        this.modelAndView = modelAndView;
    }

    public ModelAndView getModelAndView() {
        return this.modelAndView;
    }
}

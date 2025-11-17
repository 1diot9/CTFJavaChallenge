package org.springframework.web.servlet.mvc;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.ModelAndView;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/Controller.class */
public interface Controller {
    @Nullable
    ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception;
}

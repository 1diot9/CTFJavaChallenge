package org.springframework.boot.autoconfigure.web.servlet.error;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/servlet/error/ErrorViewResolver.class */
public interface ErrorViewResolver {
    ModelAndView resolveErrorView(HttpServletRequest request, HttpStatus status, Map<String, Object> model);
}

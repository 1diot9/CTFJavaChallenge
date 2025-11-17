package org.springframework.web.servlet.config.annotation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import org.springframework.core.OrderComparator;
import org.springframework.web.context.request.WebRequestInterceptor;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.WebRequestHandlerInterceptorAdapter;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/config/annotation/InterceptorRegistry.class */
public class InterceptorRegistry {
    private final List<InterceptorRegistration> registrations = new ArrayList();
    private static final Comparator<Object> INTERCEPTOR_ORDER_COMPARATOR = OrderComparator.INSTANCE.withSourceProvider(object -> {
        if (object instanceof InterceptorRegistration) {
            InterceptorRegistration interceptorRegistration = (InterceptorRegistration) object;
            Objects.requireNonNull(interceptorRegistration);
            return interceptorRegistration::getOrder;
        }
        return null;
    });

    public InterceptorRegistration addInterceptor(HandlerInterceptor interceptor) {
        InterceptorRegistration registration = new InterceptorRegistration(interceptor);
        this.registrations.add(registration);
        return registration;
    }

    public InterceptorRegistration addWebRequestInterceptor(WebRequestInterceptor interceptor) {
        WebRequestHandlerInterceptorAdapter adapted = new WebRequestHandlerInterceptorAdapter(interceptor);
        InterceptorRegistration registration = new InterceptorRegistration(adapted);
        this.registrations.add(registration);
        return registration;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public List<Object> getInterceptors() {
        return this.registrations.stream().sorted(INTERCEPTOR_ORDER_COMPARATOR).map((v0) -> {
            return v0.getInterceptor();
        }).toList();
    }
}

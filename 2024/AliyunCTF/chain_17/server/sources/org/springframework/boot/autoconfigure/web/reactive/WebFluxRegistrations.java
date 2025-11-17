package org.springframework.boot.autoconfigure.web.reactive;

import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/reactive/WebFluxRegistrations.class */
public interface WebFluxRegistrations {
    default RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        return null;
    }

    default RequestMappingHandlerAdapter getRequestMappingHandlerAdapter() {
        return null;
    }
}

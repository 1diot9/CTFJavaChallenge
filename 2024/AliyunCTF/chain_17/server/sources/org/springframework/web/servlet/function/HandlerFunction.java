package org.springframework.web.servlet.function;

import org.springframework.web.servlet.function.ServerResponse;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/HandlerFunction.class */
public interface HandlerFunction<T extends ServerResponse> {
    T handle(ServerRequest request) throws Exception;
}

package org.springframework.http.server.reactive;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/ContextPathCompositeHandler.class */
public class ContextPathCompositeHandler implements HttpHandler {
    private final Map<String, HttpHandler> handlerMap;

    public ContextPathCompositeHandler(Map<String, ? extends HttpHandler> handlerMap) {
        Assert.notEmpty(handlerMap, "Handler map must not be empty");
        this.handlerMap = initHandlers(handlerMap);
    }

    private static Map<String, HttpHandler> initHandlers(Map<String, ? extends HttpHandler> map) {
        map.keySet().forEach(ContextPathCompositeHandler::assertValidContextPath);
        return new LinkedHashMap(map);
    }

    private static void assertValidContextPath(String contextPath) {
        Assert.hasText(contextPath, "Context path must not be empty");
        if (contextPath.equals("/")) {
            return;
        }
        Assert.isTrue(contextPath.startsWith("/"), "Context path must begin with '/'");
        Assert.isTrue(!contextPath.endsWith("/"), "Context path must not end with '/'");
    }

    @Override // org.springframework.http.server.reactive.HttpHandler
    public Mono<Void> handle(ServerHttpRequest request, ServerHttpResponse response) {
        String path = request.getPath().pathWithinApplication().value();
        return (Mono) this.handlerMap.entrySet().stream().filter(entry -> {
            return path.startsWith((String) entry.getKey());
        }).findFirst().map(entry2 -> {
            String contextPath = request.getPath().contextPath().value() + ((String) entry2.getKey());
            ServerHttpRequest newRequest = request.mutate().contextPath(contextPath).build();
            return ((HttpHandler) entry2.getValue()).handle(newRequest, response);
        }).orElseGet(() -> {
            response.setStatusCode(HttpStatus.NOT_FOUND);
            return response.setComplete();
        });
    }
}

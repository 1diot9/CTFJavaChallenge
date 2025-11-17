package org.springframework.web.server.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import org.springframework.web.server.WebHandler;
import reactor.core.publisher.Mono;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/server/handler/ExceptionHandlingWebHandler.class */
public class ExceptionHandlingWebHandler extends WebHandlerDecorator {
    public static final String HANDLED_WEB_EXCEPTION = ExceptionHandlingWebHandler.class.getSimpleName() + ".handledException";
    private final List<WebExceptionHandler> exceptionHandlers;

    public ExceptionHandlingWebHandler(WebHandler delegate, List<WebExceptionHandler> handlers) {
        super(delegate);
        List<WebExceptionHandler> handlersToUse = new ArrayList<>();
        handlersToUse.add(new CheckpointInsertingHandler());
        handlersToUse.addAll(handlers);
        this.exceptionHandlers = Collections.unmodifiableList(handlersToUse);
    }

    public List<WebExceptionHandler> getExceptionHandlers() {
        return this.exceptionHandlers;
    }

    @Override // org.springframework.web.server.handler.WebHandlerDecorator, org.springframework.web.server.WebHandler
    public Mono<Void> handle(ServerWebExchange exchange) {
        Mono<Void> completion;
        try {
            completion = super.handle(exchange);
        } catch (Throwable ex) {
            completion = Mono.error(ex);
        }
        for (WebExceptionHandler handler : this.exceptionHandlers) {
            completion = completion.doOnError(error -> {
                exchange.getAttributes().put(HANDLED_WEB_EXCEPTION, error);
            }).onErrorResume(ex2 -> {
                return handler.handle(exchange, ex2);
            });
        }
        return completion;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/server/handler/ExceptionHandlingWebHandler$CheckpointInsertingHandler.class */
    private static class CheckpointInsertingHandler implements WebExceptionHandler {
        private CheckpointInsertingHandler() {
        }

        @Override // org.springframework.web.server.WebExceptionHandler
        public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
            ServerHttpRequest request = exchange.getRequest();
            String rawQuery = request.getURI().getRawQuery();
            String query = StringUtils.hasText(rawQuery) ? "?" + rawQuery : "";
            HttpMethod httpMethod = request.getMethod();
            String description = "HTTP " + httpMethod + " \"" + request.getPath() + query + "\"";
            return Mono.error(ex).checkpoint(description + " [ExceptionHandlingWebHandler]");
        }
    }
}

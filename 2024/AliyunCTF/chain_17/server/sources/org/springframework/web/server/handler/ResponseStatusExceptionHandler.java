package org.springframework.web.server.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/server/handler/ResponseStatusExceptionHandler.class */
public class ResponseStatusExceptionHandler implements WebExceptionHandler {
    private static final Log logger = LogFactory.getLog((Class<?>) ResponseStatusExceptionHandler.class);

    @Nullable
    private Log warnLogger;

    public void setWarnLogCategory(String loggerName) {
        this.warnLogger = LogFactory.getLog(loggerName);
    }

    @Override // org.springframework.web.server.WebExceptionHandler
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        if (!updateResponse(exchange.getResponse(), ex)) {
            return Mono.error(ex);
        }
        String logPrefix = exchange.getLogPrefix();
        if (this.warnLogger != null && this.warnLogger.isWarnEnabled()) {
            this.warnLogger.warn(logPrefix + formatError(ex, exchange.getRequest()));
        } else if (logger.isDebugEnabled()) {
            logger.debug(logPrefix + formatError(ex, exchange.getRequest()));
        }
        return exchange.getResponse().setComplete();
    }

    private String formatError(Throwable ex, ServerHttpRequest request) {
        String className = ex.getClass().getSimpleName();
        String message = LogFormatUtils.formatValue(ex.getMessage(), -1, true);
        String path = request.getURI().getRawPath();
        return "Resolved [" + className + ": " + message + "] for HTTP " + request.getMethod() + " " + path;
    }

    private boolean updateResponse(ServerHttpResponse response, Throwable ex) {
        boolean result = false;
        HttpStatusCode statusCode = determineStatus(ex);
        int code = statusCode != null ? statusCode.value() : determineRawStatusCode(ex);
        if (code != -1) {
            if (response.setStatusCode(statusCode)) {
                if (ex instanceof ResponseStatusException) {
                    ResponseStatusException responseStatusException = (ResponseStatusException) ex;
                    responseStatusException.getHeaders().forEach((name, values) -> {
                        values.forEach(value -> {
                            response.getHeaders().add(name, value);
                        });
                    });
                }
                result = true;
            }
        } else {
            Throwable cause = ex.getCause();
            if (cause != null) {
                result = updateResponse(response, cause);
            }
        }
        return result;
    }

    @Nullable
    protected HttpStatusCode determineStatus(Throwable ex) {
        if (ex instanceof ResponseStatusException) {
            ResponseStatusException responseStatusException = (ResponseStatusException) ex;
            return responseStatusException.getStatusCode();
        }
        return null;
    }

    @Deprecated(since = "6.0")
    protected int determineRawStatusCode(Throwable ex) {
        if (ex instanceof ResponseStatusException) {
            ResponseStatusException responseStatusException = (ResponseStatusException) ex;
            return responseStatusException.getStatusCode().value();
        }
        return -1;
    }
}

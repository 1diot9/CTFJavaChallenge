package org.springframework.http.server.reactive;

import io.netty5.handler.codec.http.HttpResponseStatus;
import java.net.URISyntaxException;
import java.util.function.BiFunction;
import org.apache.commons.logging.Log;
import org.springframework.core.io.buffer.Netty5DataBufferFactory;
import org.springframework.http.HttpLogging;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;
import reactor.netty5.http.server.HttpServerRequest;
import reactor.netty5.http.server.HttpServerResponse;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/ReactorNetty2HttpHandlerAdapter.class */
public class ReactorNetty2HttpHandlerAdapter implements BiFunction<HttpServerRequest, HttpServerResponse, Mono<Void>> {
    private static final Log logger = HttpLogging.forLogName(ReactorNetty2HttpHandlerAdapter.class);
    private final HttpHandler httpHandler;

    public ReactorNetty2HttpHandlerAdapter(HttpHandler httpHandler) {
        Assert.notNull(httpHandler, "HttpHandler must not be null");
        this.httpHandler = httpHandler;
    }

    @Override // java.util.function.BiFunction
    public Mono<Void> apply(HttpServerRequest reactorRequest, HttpServerResponse reactorResponse) {
        Netty5DataBufferFactory bufferFactory = new Netty5DataBufferFactory(reactorResponse.alloc());
        try {
            ReactorNetty2ServerHttpRequest request = new ReactorNetty2ServerHttpRequest(reactorRequest, bufferFactory);
            ServerHttpResponse response = new ReactorNetty2ServerHttpResponse(reactorResponse, bufferFactory);
            if (request.getMethod() == HttpMethod.HEAD) {
                response = new HttpHeadResponseDecorator(response);
            }
            return this.httpHandler.handle(request, response).doOnError(ex -> {
                logger.trace(request.getLogPrefix() + "Failed to complete: " + ex.getMessage());
            }).doOnSuccess(aVoid -> {
                logger.trace(request.getLogPrefix() + "Handling completed");
            });
        } catch (URISyntaxException ex2) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to get request URI: " + ex2.getMessage());
            }
            reactorResponse.status(HttpResponseStatus.BAD_REQUEST);
            return Mono.empty();
        }
    }
}

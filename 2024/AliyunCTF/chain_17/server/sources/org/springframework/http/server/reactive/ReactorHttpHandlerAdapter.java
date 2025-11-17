package org.springframework.http.server.reactive;

import io.netty.handler.codec.http.HttpResponseStatus;
import java.net.URISyntaxException;
import java.util.function.BiFunction;
import org.apache.commons.logging.Log;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpLogging;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/ReactorHttpHandlerAdapter.class */
public class ReactorHttpHandlerAdapter implements BiFunction<HttpServerRequest, HttpServerResponse, Mono<Void>> {
    private static final Log logger = HttpLogging.forLogName(ReactorHttpHandlerAdapter.class);
    private final HttpHandler httpHandler;

    public ReactorHttpHandlerAdapter(HttpHandler httpHandler) {
        Assert.notNull(httpHandler, "HttpHandler must not be null");
        this.httpHandler = httpHandler;
    }

    @Override // java.util.function.BiFunction
    public Mono<Void> apply(HttpServerRequest reactorRequest, HttpServerResponse reactorResponse) {
        NettyDataBufferFactory bufferFactory = new NettyDataBufferFactory(reactorResponse.alloc());
        try {
            ReactorServerHttpRequest request = new ReactorServerHttpRequest(reactorRequest, bufferFactory);
            ServerHttpResponse response = new ReactorServerHttpResponse(reactorResponse, bufferFactory);
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

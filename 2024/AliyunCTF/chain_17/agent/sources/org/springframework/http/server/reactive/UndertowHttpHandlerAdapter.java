package org.springframework.http.server.reactive;

import io.undertow.server.HttpServerExchange;
import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.commons.logging.Log;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpLogging;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/UndertowHttpHandlerAdapter.class */
public class UndertowHttpHandlerAdapter implements io.undertow.server.HttpHandler {
    private static final Log logger = HttpLogging.forLogName(UndertowHttpHandlerAdapter.class);
    private final HttpHandler httpHandler;
    private DataBufferFactory bufferFactory = DefaultDataBufferFactory.sharedInstance;

    public UndertowHttpHandlerAdapter(HttpHandler httpHandler) {
        Assert.notNull(httpHandler, "HttpHandler must not be null");
        this.httpHandler = httpHandler;
    }

    public void setDataBufferFactory(DataBufferFactory bufferFactory) {
        Assert.notNull(bufferFactory, "DataBufferFactory must not be null");
        this.bufferFactory = bufferFactory;
    }

    public DataBufferFactory getDataBufferFactory() {
        return this.bufferFactory;
    }

    public void handleRequest(HttpServerExchange exchange) {
        try {
            UndertowServerHttpRequest request = new UndertowServerHttpRequest(exchange, getDataBufferFactory());
            ServerHttpResponse response = new UndertowServerHttpResponse(exchange, getDataBufferFactory(), request);
            if (request.getMethod() == HttpMethod.HEAD) {
                response = new HttpHeadResponseDecorator(response);
            }
            HandlerResultSubscriber resultSubscriber = new HandlerResultSubscriber(exchange, request);
            this.httpHandler.handle(request, response).subscribe(resultSubscriber);
        } catch (URISyntaxException ex) {
            if (logger.isWarnEnabled()) {
                logger.debug("Failed to get request URI: " + ex.getMessage());
            }
            exchange.setStatusCode(400);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/UndertowHttpHandlerAdapter$HandlerResultSubscriber.class */
    private static class HandlerResultSubscriber implements Subscriber<Void> {
        private final HttpServerExchange exchange;
        private final String logPrefix;

        public HandlerResultSubscriber(HttpServerExchange exchange, UndertowServerHttpRequest request) {
            this.exchange = exchange;
            this.logPrefix = request.getLogPrefix();
        }

        public void onSubscribe(Subscription subscription) {
            subscription.request(Long.MAX_VALUE);
        }

        public void onNext(Void aVoid) {
        }

        public void onError(Throwable ex) {
            UndertowHttpHandlerAdapter.logger.trace(this.logPrefix + "Failed to complete: " + ex.getMessage());
            if (this.exchange.isResponseStarted()) {
                try {
                    UndertowHttpHandlerAdapter.logger.debug(this.logPrefix + "Closing connection");
                    this.exchange.getConnection().close();
                } catch (IOException e) {
                }
            } else {
                UndertowHttpHandlerAdapter.logger.debug(this.logPrefix + "Setting HttpServerExchange status to 500 Server Error");
                this.exchange.setStatusCode(500);
                this.exchange.endExchange();
            }
        }

        public void onComplete() {
            UndertowHttpHandlerAdapter.logger.trace(this.logPrefix + "Handling completed");
            this.exchange.endExchange();
        }
    }
}

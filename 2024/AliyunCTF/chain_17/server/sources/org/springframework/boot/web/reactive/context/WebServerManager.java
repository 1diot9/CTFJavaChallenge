package org.springframework.boot.web.reactive.context;

import java.util.function.Supplier;
import org.springframework.boot.web.reactive.server.ReactiveWebServerFactory;
import org.springframework.boot.web.server.GracefulShutdownCallback;
import org.springframework.boot.web.server.WebServer;
import org.springframework.context.ApplicationEvent;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/reactive/context/WebServerManager.class */
public class WebServerManager {
    private final ReactiveWebServerApplicationContext applicationContext;
    private final DelayedInitializationHttpHandler handler;
    private final WebServer webServer;

    /* JADX INFO: Access modifiers changed from: package-private */
    public WebServerManager(ReactiveWebServerApplicationContext applicationContext, ReactiveWebServerFactory factory, Supplier<HttpHandler> handlerSupplier, boolean lazyInit) {
        this.applicationContext = applicationContext;
        Assert.notNull(factory, "Factory must not be null");
        this.handler = new DelayedInitializationHttpHandler(handlerSupplier, lazyInit);
        this.webServer = factory.getWebServer(this.handler);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void start() {
        this.handler.initializeHandler();
        this.webServer.start();
        this.applicationContext.publishEvent((ApplicationEvent) new ReactiveWebServerInitializedEvent(this.webServer, this.applicationContext));
    }

    void shutDownGracefully(GracefulShutdownCallback callback) {
        this.webServer.shutDownGracefully(callback);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void stop() {
        this.webServer.stop();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public WebServer getWebServer() {
        return this.webServer;
    }

    HttpHandler getHandler() {
        return this.handler;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/reactive/context/WebServerManager$DelayedInitializationHttpHandler.class */
    static final class DelayedInitializationHttpHandler implements HttpHandler {
        private final Supplier<HttpHandler> handlerSupplier;
        private final boolean lazyInit;
        private volatile HttpHandler delegate = this::handleUninitialized;

        private DelayedInitializationHttpHandler(Supplier<HttpHandler> handlerSupplier, boolean lazyInit) {
            this.handlerSupplier = handlerSupplier;
            this.lazyInit = lazyInit;
        }

        private Mono<Void> handleUninitialized(ServerHttpRequest request, ServerHttpResponse response) {
            throw new IllegalStateException("The HttpHandler has not yet been initialized");
        }

        @Override // org.springframework.http.server.reactive.HttpHandler
        public Mono<Void> handle(ServerHttpRequest request, ServerHttpResponse response) {
            return this.delegate.handle(request, response);
        }

        void initializeHandler() {
            this.delegate = this.lazyInit ? new LazyHttpHandler(this.handlerSupplier) : this.handlerSupplier.get();
        }

        HttpHandler getHandler() {
            return this.delegate;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/reactive/context/WebServerManager$LazyHttpHandler.class */
    public static final class LazyHttpHandler implements HttpHandler {
        private final Mono<HttpHandler> delegate;

        private LazyHttpHandler(Supplier<HttpHandler> handlerSupplier) {
            this.delegate = Mono.fromSupplier(handlerSupplier);
        }

        @Override // org.springframework.http.server.reactive.HttpHandler
        public Mono<Void> handle(ServerHttpRequest request, ServerHttpResponse response) {
            return this.delegate.flatMap(handler -> {
                return handler.handle(request, response);
            });
        }
    }
}

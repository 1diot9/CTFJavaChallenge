package org.springframework.boot.web.embedded.netty;

import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.unix.Errors;
import io.netty.util.concurrent.DefaultEventExecutor;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reactivestreams.Publisher;
import org.springframework.boot.web.server.GracefulShutdownCallback;
import org.springframework.boot.web.server.GracefulShutdownResult;
import org.springframework.boot.web.server.PortInUseException;
import org.springframework.boot.web.server.Shutdown;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.server.WebServerException;
import org.springframework.http.client.ReactorResourceFactory;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.util.Assert;
import reactor.netty.ChannelBindException;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;
import reactor.netty.http.server.HttpServerRoutes;
import reactor.netty.resources.LoopResources;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/netty/NettyWebServer.class */
public class NettyWebServer implements WebServer {
    private static final int ERROR_NO_EACCES = -13;
    private static final Predicate<HttpServerRequest> ALWAYS = request -> {
        return true;
    };
    private static final Log logger = LogFactory.getLog((Class<?>) NettyWebServer.class);
    private final HttpServer httpServer;
    private final BiFunction<? super HttpServerRequest, ? super HttpServerResponse, ? extends Publisher<Void>> handler;
    private final Duration lifecycleTimeout;
    private final GracefulShutdown gracefulShutdown;
    private final ReactorResourceFactory resourceFactory;
    private List<NettyRouteProvider> routeProviders;
    private volatile DisposableServer disposableServer;

    @Deprecated(since = "3.2.0", forRemoval = true)
    public NettyWebServer(HttpServer httpServer, ReactorHttpHandlerAdapter handlerAdapter, Duration lifecycleTimeout, Shutdown shutdown) {
        this(httpServer, handlerAdapter, lifecycleTimeout, shutdown, null);
    }

    public NettyWebServer(HttpServer httpServer, ReactorHttpHandlerAdapter handlerAdapter, Duration lifecycleTimeout, Shutdown shutdown, ReactorResourceFactory resourceFactory) {
        this.routeProviders = Collections.emptyList();
        Assert.notNull(httpServer, "HttpServer must not be null");
        Assert.notNull(handlerAdapter, "HandlerAdapter must not be null");
        this.lifecycleTimeout = lifecycleTimeout;
        this.handler = handlerAdapter;
        this.httpServer = httpServer.channelGroup(new DefaultChannelGroup(new DefaultEventExecutor()));
        this.gracefulShutdown = shutdown == Shutdown.GRACEFUL ? new GracefulShutdown(() -> {
            return this.disposableServer;
        }) : null;
        this.resourceFactory = resourceFactory;
    }

    public void setRouteProviders(List<NettyRouteProvider> routeProviders) {
        this.routeProviders = routeProviders;
    }

    @Override // org.springframework.boot.web.server.WebServer
    public void start() throws WebServerException {
        if (this.disposableServer == null) {
            try {
                this.disposableServer = startHttpServer();
                if (this.disposableServer != null) {
                    logger.info(getStartedOnMessage(this.disposableServer));
                }
                startDaemonAwaitThread(this.disposableServer);
            } catch (Exception ex) {
                PortInUseException.ifCausedBy(ex, ChannelBindException.class, bindException -> {
                    if (bindException.localPort() > 0 && !isPermissionDenied(bindException.getCause())) {
                        throw new PortInUseException(bindException.localPort(), ex);
                    }
                });
                throw new WebServerException("Unable to start Netty", ex);
            }
        }
    }

    private String getStartedOnMessage(DisposableServer server) {
        StringBuilder message = new StringBuilder();
        Objects.requireNonNull(server);
        tryAppend(message, "port %s", server::port);
        Objects.requireNonNull(server);
        tryAppend(message, "path %s", server::path);
        return !message.isEmpty() ? "Netty started on " + message : "Netty started";
    }

    protected String getStartedLogMessage() {
        return getStartedOnMessage(this.disposableServer);
    }

    private void tryAppend(StringBuilder message, String format, Supplier<Object> supplier) {
        try {
            Object value = supplier.get();
            message.append(!message.isEmpty() ? " " : "");
            message.append(String.format(format, value));
        } catch (UnsupportedOperationException e) {
        }
    }

    DisposableServer startHttpServer() {
        HttpServer server;
        HttpServer server2 = this.httpServer;
        if (this.routeProviders.isEmpty()) {
            server = server2.handle(this.handler);
        } else {
            server = server2.route(this::applyRouteProviders);
        }
        if (this.resourceFactory != null) {
            LoopResources resources = this.resourceFactory.getLoopResources();
            Assert.notNull(resources, "No LoopResources: is ReactorResourceFactory not initialized yet?");
            server = (HttpServer) server.runOn(resources);
        }
        if (this.lifecycleTimeout != null) {
            return server.bindNow(this.lifecycleTimeout);
        }
        return server.bindNow();
    }

    private boolean isPermissionDenied(Throwable bindExceptionCause) {
        try {
            if (!(bindExceptionCause instanceof Errors.NativeIoException)) {
                return false;
            }
            Errors.NativeIoException nativeException = (Errors.NativeIoException) bindExceptionCause;
            return nativeException.expectedErr() == ERROR_NO_EACCES;
        } catch (Throwable th) {
            return false;
        }
    }

    @Override // org.springframework.boot.web.server.WebServer
    public void shutDownGracefully(GracefulShutdownCallback callback) {
        if (this.gracefulShutdown == null) {
            callback.shutdownComplete(GracefulShutdownResult.IMMEDIATE);
        } else {
            this.gracefulShutdown.shutDownGracefully(callback);
        }
    }

    private void applyRouteProviders(HttpServerRoutes routes) {
        for (NettyRouteProvider provider : this.routeProviders) {
            routes = provider.apply(routes);
        }
        routes.route(ALWAYS, this.handler);
    }

    private void startDaemonAwaitThread(final DisposableServer disposableServer) {
        Thread awaitThread = new Thread("server") { // from class: org.springframework.boot.web.embedded.netty.NettyWebServer.1
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                disposableServer.onDispose().block();
            }
        };
        awaitThread.setContextClassLoader(getClass().getClassLoader());
        awaitThread.setDaemon(false);
        awaitThread.start();
    }

    @Override // org.springframework.boot.web.server.WebServer
    public void stop() throws WebServerException {
        if (this.disposableServer != null) {
            if (this.gracefulShutdown != null) {
                this.gracefulShutdown.abort();
            }
            try {
                if (this.lifecycleTimeout != null) {
                    this.disposableServer.disposeNow(this.lifecycleTimeout);
                } else {
                    this.disposableServer.disposeNow();
                }
            } catch (IllegalStateException e) {
            }
            this.disposableServer = null;
        }
    }

    @Override // org.springframework.boot.web.server.WebServer
    public int getPort() {
        if (this.disposableServer != null) {
            try {
                return this.disposableServer.port();
            } catch (UnsupportedOperationException e) {
                return -1;
            }
        }
        return -1;
    }
}

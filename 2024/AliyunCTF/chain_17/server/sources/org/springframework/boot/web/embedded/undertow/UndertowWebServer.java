package org.springframework.boot.web.embedded.undertow;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.GracefulShutdownHandler;
import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.boot.web.server.GracefulShutdownCallback;
import org.springframework.boot.web.server.GracefulShutdownResult;
import org.springframework.boot.web.server.PortInUseException;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.server.WebServerException;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.xnio.channels.BoundChannel;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/undertow/UndertowWebServer.class */
public class UndertowWebServer implements WebServer {
    private static final Log logger = LogFactory.getLog((Class<?>) UndertowWebServer.class);
    private final AtomicReference<GracefulShutdownCallback> gracefulShutdownCallback;
    private final Object monitor;
    private final Undertow.Builder builder;
    private final Iterable<HttpHandlerFactory> httpHandlerFactories;
    private final boolean autoStart;
    private Undertow undertow;
    private volatile boolean started;
    private volatile GracefulShutdownHandler gracefulShutdown;
    private volatile List<Closeable> closeables;

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/undertow/UndertowWebServer$CloseableHttpHandler.class */
    private interface CloseableHttpHandler extends HttpHandler, Closeable {
    }

    public UndertowWebServer(Undertow.Builder builder, boolean autoStart) {
        this(builder, Collections.singleton(new CloseableHttpHandlerFactory(null)), autoStart);
    }

    public UndertowWebServer(Undertow.Builder builder, Iterable<HttpHandlerFactory> httpHandlerFactories, boolean autoStart) {
        this.gracefulShutdownCallback = new AtomicReference<>();
        this.monitor = new Object();
        this.started = false;
        this.builder = builder;
        this.httpHandlerFactories = httpHandlerFactories;
        this.autoStart = autoStart;
    }

    @Override // org.springframework.boot.web.server.WebServer
    public void start() throws WebServerException {
        synchronized (this.monitor) {
            if (this.started) {
                return;
            }
            try {
                if (this.autoStart) {
                    if (this.undertow == null) {
                        this.undertow = createUndertowServer();
                    }
                    this.undertow.start();
                    this.started = true;
                    String message = getStartLogMessage();
                    logger.info(message);
                }
            } catch (Exception ex) {
                try {
                    PortInUseException.ifPortBindingException(ex, bindException -> {
                        List<Port> failedPorts = getConfiguredPorts();
                        failedPorts.removeAll(getActualPorts());
                        if (failedPorts.size() == 1) {
                            throw new PortInUseException(failedPorts.get(0).getNumber());
                        }
                    });
                    throw new WebServerException("Unable to start embedded Undertow", ex);
                } catch (Throwable th) {
                    destroySilently();
                    throw th;
                }
            }
        }
    }

    private void destroySilently() {
        try {
            if (this.undertow != null) {
                this.undertow.stop();
                this.closeables.forEach(this::closeSilently);
            }
        } catch (Exception e) {
        }
    }

    private void closeSilently(Closeable closeable) {
        try {
            closeable.close();
        } catch (Exception e) {
        }
    }

    private Undertow createUndertowServer() {
        this.closeables = new ArrayList();
        this.gracefulShutdown = null;
        HttpHandler handler = createHttpHandler();
        this.builder.setHandler(handler);
        return this.builder.build();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public HttpHandler createHttpHandler() {
        Closeable closeable = null;
        for (HttpHandlerFactory factory : this.httpHandlerFactories) {
            closeable = factory.getHandler(closeable);
            if (closeable instanceof Closeable) {
                Closeable closeable2 = closeable;
                this.closeables.add(closeable2);
            }
            if (closeable instanceof GracefulShutdownHandler) {
                GracefulShutdownHandler shutdownHandler = (GracefulShutdownHandler) closeable;
                Assert.isNull(this.gracefulShutdown, "Only a single GracefulShutdownHandler can be defined");
                this.gracefulShutdown = shutdownHandler;
            }
        }
        return closeable;
    }

    private String getPortsDescription() {
        StringBuilder description = new StringBuilder();
        List<Port> ports = getActualPorts();
        description.append("port");
        if (ports.size() != 1) {
            description.append("s");
        }
        description.append(" ");
        if (!ports.isEmpty()) {
            description.append(StringUtils.collectionToDelimitedString(ports, ", "));
        } else {
            description.append("unknown");
        }
        return description.toString();
    }

    private List<Port> getActualPorts() {
        List<Port> ports = new ArrayList<>();
        try {
            if (!this.autoStart) {
                ports.add(new Port(-1, "unknown"));
            } else {
                for (BoundChannel channel : extractChannels()) {
                    ports.add(getPortFromChannel(channel));
                }
            }
        } catch (Exception e) {
        }
        return ports;
    }

    private List<BoundChannel> extractChannels() {
        Field channelsField = ReflectionUtils.findField(Undertow.class, "channels");
        ReflectionUtils.makeAccessible(channelsField);
        return (List) ReflectionUtils.getField(channelsField, this.undertow);
    }

    private Port getPortFromChannel(BoundChannel channel) {
        SocketAddress socketAddress = channel.getLocalAddress();
        if (socketAddress instanceof InetSocketAddress) {
            InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
            Field sslField = ReflectionUtils.findField(channel.getClass(), "ssl");
            String protocol = sslField != null ? "https" : "http";
            return new Port(inetSocketAddress.getPort(), protocol);
        }
        return null;
    }

    private List<Port> getConfiguredPorts() {
        List<Port> ports = new ArrayList<>();
        for (Object listener : extractListeners()) {
            try {
                Port port = getPortFromListener(listener);
                if (port.getNumber() != 0) {
                    ports.add(port);
                }
            } catch (Exception e) {
            }
        }
        return ports;
    }

    private List<Object> extractListeners() {
        Field listenersField = ReflectionUtils.findField(Undertow.class, "listeners");
        ReflectionUtils.makeAccessible(listenersField);
        return (List) ReflectionUtils.getField(listenersField, this.undertow);
    }

    private Port getPortFromListener(Object listener) {
        Field typeField = ReflectionUtils.findField(listener.getClass(), "type");
        ReflectionUtils.makeAccessible(typeField);
        String protocol = ReflectionUtils.getField(typeField, listener).toString();
        Field portField = ReflectionUtils.findField(listener.getClass(), "port");
        ReflectionUtils.makeAccessible(portField);
        int port = ((Integer) ReflectionUtils.getField(portField, listener)).intValue();
        return new Port(port, protocol);
    }

    @Override // org.springframework.boot.web.server.WebServer
    public void stop() throws WebServerException {
        synchronized (this.monitor) {
            if (this.started) {
                this.started = false;
                if (this.gracefulShutdown != null) {
                    notifyGracefulCallback(false);
                }
                try {
                    this.undertow.stop();
                    for (Closeable closeable : this.closeables) {
                        closeable.close();
                    }
                } catch (Exception ex) {
                    throw new WebServerException("Unable to stop Undertow", ex);
                }
            }
        }
    }

    @Override // org.springframework.boot.web.server.WebServer
    public int getPort() {
        List<Port> ports = getActualPorts();
        if (ports.isEmpty()) {
            return -1;
        }
        return ports.get(0).getNumber();
    }

    @Override // org.springframework.boot.web.server.WebServer
    public void shutDownGracefully(GracefulShutdownCallback callback) {
        if (this.gracefulShutdown == null) {
            callback.shutdownComplete(GracefulShutdownResult.IMMEDIATE);
            return;
        }
        logger.info("Commencing graceful shutdown. Waiting for active requests to complete");
        this.gracefulShutdownCallback.set(callback);
        this.gracefulShutdown.shutdown();
        this.gracefulShutdown.addShutdownListener(this::notifyGracefulCallback);
    }

    private void notifyGracefulCallback(boolean success) {
        GracefulShutdownCallback callback = this.gracefulShutdownCallback.getAndSet(null);
        if (callback != null) {
            if (success) {
                logger.info("Graceful shutdown complete");
                callback.shutdownComplete(GracefulShutdownResult.IDLE);
            } else {
                logger.info("Graceful shutdown aborted with one or more requests still active");
                callback.shutdownComplete(GracefulShutdownResult.REQUESTS_ACTIVE);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String getStartLogMessage() {
        return "Undertow started on " + getPortsDescription();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/undertow/UndertowWebServer$Port.class */
    public static final class Port {
        private final int number;
        private final String protocol;

        private Port(int number, String protocol) {
            this.number = number;
            this.protocol = protocol;
        }

        int getNumber() {
            return this.number;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Port other = (Port) obj;
            return this.number == other.number;
        }

        public int hashCode() {
            return this.number;
        }

        public String toString() {
            return this.number + " (" + this.protocol + ")";
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/undertow/UndertowWebServer$CloseableHttpHandlerFactory.class */
    private static final class CloseableHttpHandlerFactory implements HttpHandlerFactory {
        private final Closeable closeable;

        private CloseableHttpHandlerFactory(Closeable closeable) {
            this.closeable = closeable;
        }

        @Override // org.springframework.boot.web.embedded.undertow.HttpHandlerFactory
        public HttpHandler getHandler(final HttpHandler next) {
            if (this.closeable == null) {
                return next;
            }
            return new CloseableHttpHandler() { // from class: org.springframework.boot.web.embedded.undertow.UndertowWebServer.CloseableHttpHandlerFactory.1
                public void handleRequest(HttpServerExchange exchange) throws Exception {
                    next.handleRequest(exchange);
                }

                @Override // java.io.Closeable, java.lang.AutoCloseable
                public void close() throws IOException {
                    CloseableHttpHandlerFactory.this.closeable.close();
                }
            };
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/undertow/UndertowWebServer$UndertowWebServerRuntimeHints.class */
    static class UndertowWebServerRuntimeHints implements RuntimeHintsRegistrar {
        UndertowWebServerRuntimeHints() {
        }

        @Override // org.springframework.aot.hint.RuntimeHintsRegistrar
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            hints.reflection().registerTypeIfPresent(classLoader, "io.undertow.Undertow", hint -> {
                hint.withField("listeners").withField("channels");
            });
            hints.reflection().registerTypeIfPresent(classLoader, "io.undertow.Undertow$ListenerConfig", hint2 -> {
                hint2.withField("type").withField("port");
            });
            hints.reflection().registerTypeIfPresent(classLoader, "io.undertow.protocols.ssl.UndertowAcceptingSslChannel", hint3 -> {
                hint3.withField("ssl");
            });
        }
    }
}

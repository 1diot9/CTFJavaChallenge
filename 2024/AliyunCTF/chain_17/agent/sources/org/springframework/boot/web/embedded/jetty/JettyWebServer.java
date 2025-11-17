package org.springframework.boot.web.embedded.jetty;

import cn.hutool.core.text.CharSequenceUtil;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.NetworkConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.springframework.boot.web.server.GracefulShutdownCallback;
import org.springframework.boot.web.server.GracefulShutdownResult;
import org.springframework.boot.web.server.PortInUseException;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.server.WebServerException;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/jetty/JettyWebServer.class */
public class JettyWebServer implements WebServer {
    private static final Log logger = LogFactory.getLog((Class<?>) JettyWebServer.class);
    private final Object monitor;
    private final Server server;
    private final boolean autoStart;
    private final GracefulShutdown gracefulShutdown;
    private Connector[] connectors;
    private volatile boolean started;

    public JettyWebServer(Server server) {
        this(server, true);
    }

    public JettyWebServer(Server server, boolean autoStart) {
        this.monitor = new Object();
        this.autoStart = autoStart;
        Assert.notNull(server, "Jetty Server must not be null");
        this.server = server;
        this.gracefulShutdown = createGracefulShutdown(server);
        initialize();
    }

    private GracefulShutdown createGracefulShutdown(Server server) {
        StatisticsHandler statisticsHandler = findStatisticsHandler(server);
        if (statisticsHandler == null) {
            return null;
        }
        Objects.requireNonNull(statisticsHandler);
        return new GracefulShutdown(server, statisticsHandler::getRequestsActive);
    }

    private StatisticsHandler findStatisticsHandler(Server server) {
        return findStatisticsHandler(server.getHandler());
    }

    private StatisticsHandler findStatisticsHandler(Handler handler) {
        if (handler instanceof StatisticsHandler) {
            StatisticsHandler statisticsHandler = (StatisticsHandler) handler;
            return statisticsHandler;
        }
        if (handler instanceof Handler.Wrapper) {
            Handler.Wrapper handlerWrapper = (Handler.Wrapper) handler;
            return findStatisticsHandler(handlerWrapper.getHandler());
        }
        return null;
    }

    private void initialize() {
        synchronized (this.monitor) {
            try {
                this.connectors = this.server.getConnectors();
                this.server.setConnectors((Connector[]) null);
                this.server.start();
                this.server.setStopAtShutdown(false);
            } catch (Throwable ex) {
                stopSilently();
                throw new WebServerException("Unable to start embedded Jetty web server", ex);
            }
        }
    }

    private void stopSilently() {
        try {
            this.server.stop();
        } catch (Exception e) {
        }
    }

    @Override // org.springframework.boot.web.server.WebServer
    public void start() throws WebServerException {
        synchronized (this.monitor) {
            if (this.started) {
                return;
            }
            this.server.setConnectors(this.connectors);
            if (this.autoStart) {
                try {
                    this.server.start();
                    for (Handler handler : this.server.getHandlers()) {
                        handleDeferredInitialize(handler);
                    }
                    for (NetworkConnector networkConnector : this.server.getConnectors()) {
                        try {
                            networkConnector.start();
                        } catch (IOException ex) {
                            if (networkConnector instanceof NetworkConnector) {
                                NetworkConnector networkConnector2 = networkConnector;
                                Objects.requireNonNull(networkConnector2);
                                PortInUseException.throwIfPortBindingException(ex, networkConnector2::getPort);
                            }
                            throw ex;
                        }
                    }
                    this.started = true;
                    logger.info(getStartedLogMessage());
                } catch (WebServerException ex2) {
                    stopSilently();
                    throw ex2;
                } catch (Exception ex3) {
                    stopSilently();
                    throw new WebServerException("Unable to start embedded Jetty server", ex3);
                }
            }
        }
    }

    String getStartedLogMessage() {
        return "Jetty started on " + getActualPortsDescription() + " with context path '" + getContextPath() + "'";
    }

    private String getActualPortsDescription() {
        StringBuilder description = new StringBuilder("port");
        Connector[] connectors = this.server.getConnectors();
        if (connectors.length != 1) {
            description.append("s");
        }
        description.append(CharSequenceUtil.SPACE);
        for (int i = 0; i < connectors.length; i++) {
            if (i != 0) {
                description.append(", ");
            }
            Connector connector = connectors[i];
            description.append(getLocalPort(connector)).append(getProtocols(connector));
        }
        return description.toString();
    }

    private String getProtocols(Connector connector) {
        List<String> protocols = connector.getProtocols();
        return " (" + StringUtils.collectionToDelimitedString(protocols, ", ") + ")";
    }

    private String getContextPath() {
        return (String) this.server.getHandlers().stream().map(this::findContextHandler).filter((v0) -> {
            return Objects.nonNull(v0);
        }).map((v0) -> {
            return v0.getContextPath();
        }).collect(Collectors.joining(CharSequenceUtil.SPACE));
    }

    private ContextHandler findContextHandler(Handler handler) {
        while (handler instanceof Handler.Wrapper) {
            Handler.Wrapper handlerWrapper = (Handler.Wrapper) handler;
            if (handler instanceof ContextHandler) {
                ContextHandler contextHandler = (ContextHandler) handler;
                return contextHandler;
            }
            handler = handlerWrapper.getHandler();
        }
        return null;
    }

    private void handleDeferredInitialize(List<Handler> handlers) throws Exception {
        for (Handler handler : handlers) {
            handleDeferredInitialize(handler);
        }
    }

    private void handleDeferredInitialize(Handler handler) throws Exception {
        if (handler instanceof JettyEmbeddedWebAppContext) {
            JettyEmbeddedWebAppContext jettyEmbeddedWebAppContext = (JettyEmbeddedWebAppContext) handler;
            jettyEmbeddedWebAppContext.deferredInitialize();
        } else if (handler instanceof Handler.Wrapper) {
            Handler.Wrapper handlerWrapper = (Handler.Wrapper) handler;
            handleDeferredInitialize(handlerWrapper.getHandler());
        } else if (handler instanceof Handler.Collection) {
            Handler.Collection handlerCollection = (Handler.Collection) handler;
            handleDeferredInitialize(handlerCollection.getHandlers());
        }
    }

    @Override // org.springframework.boot.web.server.WebServer
    public void stop() {
        synchronized (this.monitor) {
            this.started = false;
            if (this.gracefulShutdown != null) {
                this.gracefulShutdown.abort();
            }
            try {
                try {
                    for (Connector connector : this.server.getConnectors()) {
                        connector.stop();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } catch (Exception ex) {
                throw new WebServerException("Unable to stop embedded Jetty server", ex);
            }
        }
    }

    @Override // org.springframework.boot.web.server.WebServer
    public void destroy() {
        synchronized (this.monitor) {
            try {
                this.server.stop();
            } catch (Exception ex) {
                throw new WebServerException("Unable to destroy embedded Jetty server", ex);
            }
        }
    }

    @Override // org.springframework.boot.web.server.WebServer
    public int getPort() {
        Connector[] connectors = this.server.getConnectors();
        for (Connector connector : connectors) {
            int localPort = getLocalPort(connector);
            if (localPort > 0) {
                return localPort;
            }
        }
        return -1;
    }

    private int getLocalPort(Connector connector) {
        if (connector instanceof NetworkConnector) {
            NetworkConnector networkConnector = (NetworkConnector) connector;
            return networkConnector.getLocalPort();
        }
        return 0;
    }

    @Override // org.springframework.boot.web.server.WebServer
    public void shutDownGracefully(GracefulShutdownCallback callback) {
        if (this.gracefulShutdown == null) {
            callback.shutdownComplete(GracefulShutdownResult.IMMEDIATE);
        } else {
            this.gracefulShutdown.shutDownGracefully(callback);
        }
    }

    public Server getServer() {
        return this.server;
    }
}

package org.springframework.boot.web.embedded.tomcat;

import cn.hutool.core.text.CharSequenceUtil;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.naming.NamingException;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Service;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.naming.ContextBindings;
import org.springframework.boot.web.server.GracefulShutdownCallback;
import org.springframework.boot.web.server.GracefulShutdownResult;
import org.springframework.boot.web.server.PortInUseException;
import org.springframework.boot.web.server.Shutdown;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.server.WebServerException;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/tomcat/TomcatWebServer.class */
public class TomcatWebServer implements WebServer {
    private static final Log logger = LogFactory.getLog((Class<?>) TomcatWebServer.class);
    private static final AtomicInteger containerCounter = new AtomicInteger(-1);
    private final Object monitor;
    private final Map<Service, Connector[]> serviceConnectors;
    private final Tomcat tomcat;
    private final boolean autoStart;
    private final GracefulShutdown gracefulShutdown;
    private volatile boolean started;

    public TomcatWebServer(Tomcat tomcat) {
        this(tomcat, true);
    }

    public TomcatWebServer(Tomcat tomcat, boolean autoStart) {
        this(tomcat, autoStart, Shutdown.IMMEDIATE);
    }

    public TomcatWebServer(Tomcat tomcat, boolean autoStart, Shutdown shutdown) {
        this.monitor = new Object();
        this.serviceConnectors = new HashMap();
        Assert.notNull(tomcat, "Tomcat Server must not be null");
        this.tomcat = tomcat;
        this.autoStart = autoStart;
        this.gracefulShutdown = shutdown == Shutdown.GRACEFUL ? new GracefulShutdown(tomcat) : null;
        initialize();
    }

    private void initialize() throws WebServerException {
        logger.info("Tomcat initialized with " + getPortsDescription(false));
        synchronized (this.monitor) {
            try {
                addInstanceIdToEngineName();
                Context context = findContext();
                context.addLifecycleListener(event -> {
                    if (context.equals(event.getSource()) && Lifecycle.START_EVENT.equals(event.getType())) {
                        removeServiceConnectors();
                    }
                });
                disableBindOnInit();
                this.tomcat.start();
                rethrowDeferredStartupExceptions();
                try {
                    ContextBindings.bindClassLoader(context, context.getNamingToken(), getClass().getClassLoader());
                } catch (NamingException e) {
                }
                startNonDaemonAwaitThread();
            } catch (Exception ex) {
                stopSilently();
                destroySilently();
                throw new WebServerException("Unable to start embedded Tomcat", ex);
            }
        }
    }

    private Context findContext() {
        for (Container child : this.tomcat.getHost().findChildren()) {
            if (child instanceof Context) {
                Context context = (Context) child;
                return context;
            }
        }
        throw new IllegalStateException("The host does not contain a Context");
    }

    private void addInstanceIdToEngineName() {
        int instanceId = containerCounter.incrementAndGet();
        if (instanceId > 0) {
            Engine engine = this.tomcat.getEngine();
            engine.setName(engine.getName() + "-" + instanceId);
        }
    }

    private void removeServiceConnectors() {
        doWithConnectors((service, connectors) -> {
            this.serviceConnectors.put(service, connectors);
            for (Connector connector : connectors) {
                service.removeConnector(connector);
            }
        });
    }

    private void disableBindOnInit() {
        doWithConnectors((service, connectors) -> {
            for (Connector connector : connectors) {
                Object bindOnInit = connector.getProperty("bindOnInit");
                if (bindOnInit == null) {
                    connector.setProperty("bindOnInit", "false");
                }
            }
        });
    }

    private void doWithConnectors(BiConsumer<Service, Connector[]> consumer) {
        for (Service service : this.tomcat.getServer().findServices()) {
            Connector[] connectors = (Connector[]) service.findConnectors().clone();
            consumer.accept(service, connectors);
        }
    }

    private void rethrowDeferredStartupExceptions() throws Exception {
        Exception exception;
        Container[] children = this.tomcat.getHost().findChildren();
        for (Container container : children) {
            if (container instanceof TomcatEmbeddedContext) {
                TomcatEmbeddedContext embeddedContext = (TomcatEmbeddedContext) container;
                TomcatStarter tomcatStarter = embeddedContext.getStarter();
                if (tomcatStarter != null && (exception = tomcatStarter.getStartUpException()) != null) {
                    throw exception;
                }
            }
            if (!LifecycleState.STARTED.equals(container.getState())) {
                throw new IllegalStateException(container + " failed to start");
            }
        }
    }

    private void startNonDaemonAwaitThread() {
        Thread awaitThread = new Thread("container-" + containerCounter.get()) { // from class: org.springframework.boot.web.embedded.tomcat.TomcatWebServer.1
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                TomcatWebServer.this.tomcat.getServer().await();
            }
        };
        awaitThread.setContextClassLoader(getClass().getClassLoader());
        awaitThread.setDaemon(false);
        awaitThread.start();
    }

    @Override // org.springframework.boot.web.server.WebServer
    public void start() throws WebServerException {
        synchronized (this.monitor) {
            try {
                if (this.started) {
                    return;
                }
                try {
                    addPreviouslyRemovedConnectors();
                    Connector connector = this.tomcat.getConnector();
                    if (connector != null && this.autoStart) {
                        performDeferredLoadOnStartup();
                    }
                    checkThatConnectorsHaveStarted();
                    this.started = true;
                    logger.info(getStartedLogMessage());
                    Context context = findContext();
                    ContextBindings.unbindClassLoader(context, context.getNamingToken(), getClass().getClassLoader());
                } catch (ConnectorStartFailedException ex) {
                    stopSilently();
                    throw ex;
                } catch (Exception ex2) {
                    PortInUseException.throwIfPortBindingException(ex2, () -> {
                        return this.tomcat.getConnector().getPort();
                    });
                    throw new WebServerException("Unable to start embedded Tomcat server", ex2);
                }
            } catch (Throwable th) {
                Context context2 = findContext();
                ContextBindings.unbindClassLoader(context2, context2.getNamingToken(), getClass().getClassLoader());
                throw th;
            }
        }
    }

    String getStartedLogMessage() {
        return "Tomcat started on " + getPortsDescription(true) + " with context path '" + getContextPath() + "'";
    }

    private void checkThatConnectorsHaveStarted() {
        checkConnectorHasStarted(this.tomcat.getConnector());
        for (Connector connector : this.tomcat.getService().findConnectors()) {
            checkConnectorHasStarted(connector);
        }
    }

    private void checkConnectorHasStarted(Connector connector) {
        if (LifecycleState.FAILED.equals(connector.getState())) {
            throw new ConnectorStartFailedException(connector.getPort());
        }
    }

    private void stopSilently() {
        try {
            stopTomcat();
        } catch (LifecycleException e) {
        }
    }

    private void destroySilently() {
        try {
            this.tomcat.destroy();
        } catch (LifecycleException e) {
        }
    }

    private void stopTomcat() throws LifecycleException {
        if (Thread.currentThread().getContextClassLoader() instanceof TomcatEmbeddedWebappClassLoader) {
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
        }
        this.tomcat.stop();
    }

    private void addPreviouslyRemovedConnectors() {
        Service[] services = this.tomcat.getServer().findServices();
        for (Service service : services) {
            Connector[] connectors = this.serviceConnectors.get(service);
            if (connectors != null) {
                for (Connector connector : connectors) {
                    service.addConnector(connector);
                    if (!this.autoStart) {
                        stopProtocolHandler(connector);
                    }
                }
                this.serviceConnectors.remove(service);
            }
        }
    }

    private void stopProtocolHandler(Connector connector) {
        try {
            connector.getProtocolHandler().stop();
        } catch (Exception ex) {
            logger.error("Cannot pause connector: ", ex);
        }
    }

    private void performDeferredLoadOnStartup() {
        try {
            for (Container child : this.tomcat.getHost().findChildren()) {
                if (child instanceof TomcatEmbeddedContext) {
                    TomcatEmbeddedContext embeddedContext = (TomcatEmbeddedContext) child;
                    embeddedContext.deferredLoadOnStartup();
                }
            }
        } catch (Exception ex) {
            if (ex instanceof WebServerException) {
                WebServerException webServerException = (WebServerException) ex;
                throw webServerException;
            }
            throw new WebServerException("Unable to start embedded Tomcat connectors", ex);
        }
    }

    Map<Service, Connector[]> getServiceConnectors() {
        return this.serviceConnectors;
    }

    @Override // org.springframework.boot.web.server.WebServer
    public void stop() throws WebServerException {
        synchronized (this.monitor) {
            boolean wasStarted = this.started;
            try {
                try {
                    this.started = false;
                    if (this.gracefulShutdown != null) {
                        this.gracefulShutdown.abort();
                    }
                    removeServiceConnectors();
                    if (wasStarted) {
                        containerCounter.decrementAndGet();
                    }
                } catch (Exception ex) {
                    throw new WebServerException("Unable to stop embedded Tomcat", ex);
                }
            } catch (Throwable th) {
                if (wasStarted) {
                    containerCounter.decrementAndGet();
                }
                throw th;
            }
        }
    }

    @Override // org.springframework.boot.web.server.WebServer
    public void destroy() throws WebServerException {
        try {
            stopTomcat();
            this.tomcat.destroy();
        } catch (LifecycleException e) {
        } catch (Exception ex) {
            throw new WebServerException("Unable to destroy embedded Tomcat", ex);
        }
    }

    private String getPortsDescription(boolean localPort) {
        StringBuilder description = new StringBuilder();
        Connector[] connectors = this.tomcat.getService().findConnectors();
        description.append("port");
        if (connectors.length != 1) {
            description.append("s");
        }
        description.append(CharSequenceUtil.SPACE);
        for (int i = 0; i < connectors.length; i++) {
            if (i != 0) {
                description.append(", ");
            }
            Connector connector = connectors[i];
            int port = localPort ? connector.getLocalPort() : connector.getPort();
            description.append(port).append(" (").append(connector.getScheme()).append(')');
        }
        return description.toString();
    }

    @Override // org.springframework.boot.web.server.WebServer
    public int getPort() {
        Connector connector = this.tomcat.getConnector();
        if (connector != null) {
            return connector.getLocalPort();
        }
        return -1;
    }

    private String getContextPath() {
        Stream stream = Arrays.stream(this.tomcat.getHost().findChildren());
        Class<TomcatEmbeddedContext> cls = TomcatEmbeddedContext.class;
        Objects.requireNonNull(TomcatEmbeddedContext.class);
        Stream filter = stream.filter((v1) -> {
            return r1.isInstance(v1);
        });
        Class<TomcatEmbeddedContext> cls2 = TomcatEmbeddedContext.class;
        Objects.requireNonNull(TomcatEmbeddedContext.class);
        return (String) filter.map((v1) -> {
            return r1.cast(v1);
        }).map((v0) -> {
            return v0.getPath();
        }).collect(Collectors.joining(CharSequenceUtil.SPACE));
    }

    public Tomcat getTomcat() {
        return this.tomcat;
    }

    @Override // org.springframework.boot.web.server.WebServer
    public void shutDownGracefully(GracefulShutdownCallback callback) {
        if (this.gracefulShutdown == null) {
            callback.shutdownComplete(GracefulShutdownResult.IMMEDIATE);
        } else {
            this.gracefulShutdown.shutDownGracefully(callback);
        }
    }
}

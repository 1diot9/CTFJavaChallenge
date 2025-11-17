package org.springframework.boot.web.embedded.jetty;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.http2.server.HTTP2CServerConnectionFactory;
import org.eclipse.jetty.server.AbstractConnector;
import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.ConnectionLimit;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.springframework.boot.web.reactive.server.AbstractReactiveWebServerFactory;
import org.springframework.boot.web.server.Shutdown;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.server.WebServer;
import org.springframework.http.client.reactive.JettyResourceFactory;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.JettyHttpHandlerAdapter;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/jetty/JettyReactiveWebServerFactory.class */
public class JettyReactiveWebServerFactory extends AbstractReactiveWebServerFactory implements ConfigurableJettyWebServerFactory {
    private static final Log logger = LogFactory.getLog((Class<?>) JettyReactiveWebServerFactory.class);
    private int acceptors;
    private int selectors;
    private boolean useForwardHeaders;
    private Set<JettyServerCustomizer> jettyServerCustomizers;
    private JettyResourceFactory resourceFactory;
    private ThreadPool threadPool;
    private int maxConnections;

    public JettyReactiveWebServerFactory() {
        this.acceptors = -1;
        this.selectors = -1;
        this.jettyServerCustomizers = new LinkedHashSet();
        this.maxConnections = -1;
    }

    public JettyReactiveWebServerFactory(int port) {
        super(port);
        this.acceptors = -1;
        this.selectors = -1;
        this.jettyServerCustomizers = new LinkedHashSet();
        this.maxConnections = -1;
    }

    @Override // org.springframework.boot.web.embedded.jetty.ConfigurableJettyWebServerFactory
    public void setUseForwardHeaders(boolean useForwardHeaders) {
        this.useForwardHeaders = useForwardHeaders;
    }

    @Override // org.springframework.boot.web.embedded.jetty.ConfigurableJettyWebServerFactory
    public void setAcceptors(int acceptors) {
        this.acceptors = acceptors;
    }

    @Override // org.springframework.boot.web.reactive.server.ReactiveWebServerFactory
    public WebServer getWebServer(HttpHandler httpHandler) {
        JettyHttpHandlerAdapter servlet = new JettyHttpHandlerAdapter(httpHandler);
        Server server = createJettyServer(servlet);
        return new JettyWebServer(server, getPort() >= 0);
    }

    @Override // org.springframework.boot.web.embedded.jetty.ConfigurableJettyWebServerFactory
    public void addServerCustomizers(JettyServerCustomizer... customizers) {
        Assert.notNull(customizers, "Customizers must not be null");
        this.jettyServerCustomizers.addAll(Arrays.asList(customizers));
    }

    @Override // org.springframework.boot.web.embedded.jetty.ConfigurableJettyWebServerFactory
    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    public void setServerCustomizers(Collection<? extends JettyServerCustomizer> customizers) {
        Assert.notNull(customizers, "Customizers must not be null");
        this.jettyServerCustomizers = new LinkedHashSet(customizers);
    }

    public Collection<JettyServerCustomizer> getServerCustomizers() {
        return this.jettyServerCustomizers;
    }

    public ThreadPool getThreadPool() {
        return this.threadPool;
    }

    @Override // org.springframework.boot.web.embedded.jetty.ConfigurableJettyWebServerFactory
    public void setThreadPool(ThreadPool threadPool) {
        this.threadPool = threadPool;
    }

    @Override // org.springframework.boot.web.embedded.jetty.ConfigurableJettyWebServerFactory
    public void setSelectors(int selectors) {
        this.selectors = selectors;
    }

    public void setResourceFactory(JettyResourceFactory resourceFactory) {
        this.resourceFactory = resourceFactory;
    }

    protected JettyResourceFactory getResourceFactory() {
        return this.resourceFactory;
    }

    protected Server createJettyServer(JettyHttpHandlerAdapter servlet) {
        int port = Math.max(getPort(), 0);
        InetSocketAddress address = new InetSocketAddress(getAddress(), port);
        Server server = new Server(getThreadPool());
        server.addConnector(createConnector(address, server));
        server.setStopTimeout(0L);
        ServletHolder servletHolder = new ServletHolder(servlet);
        servletHolder.setAsyncSupported(true);
        ServletContextHandler contextHandler = new ServletContextHandler("/", false, false);
        contextHandler.addServlet(servletHolder, "/");
        server.setHandler(addHandlerWrappers(contextHandler));
        logger.info("Server initialized with port: " + port);
        if (this.maxConnections > -1) {
            server.addBean(new ConnectionLimit(this.maxConnections, server));
        }
        if (Ssl.isEnabled(getSsl())) {
            customizeSsl(server, address);
        }
        for (JettyServerCustomizer customizer : getServerCustomizers()) {
            customizer.customize(server);
        }
        if (this.useForwardHeaders) {
            new ForwardHeadersCustomizer().customize(server);
        }
        if (getShutdown() == Shutdown.GRACEFUL) {
            StatisticsHandler statisticsHandler = new StatisticsHandler();
            statisticsHandler.setHandler(server.getHandler());
            server.setHandler(statisticsHandler);
        }
        return server;
    }

    private AbstractConnector createConnector(InetSocketAddress address, Server server) {
        ServerConnector connector;
        HttpConfiguration httpConfiguration = new HttpConfiguration();
        httpConfiguration.setSendServerVersion(false);
        List<ConnectionFactory> connectionFactories = new ArrayList<>();
        connectionFactories.add(new HttpConnectionFactory(httpConfiguration));
        if (getHttp2() != null && getHttp2().isEnabled()) {
            connectionFactories.add(new HTTP2CServerConnectionFactory(httpConfiguration));
        }
        JettyResourceFactory resourceFactory = getResourceFactory();
        if (resourceFactory != null) {
            connector = new ServerConnector(server, resourceFactory.getExecutor(), resourceFactory.getScheduler(), resourceFactory.getByteBufferPool(), this.acceptors, this.selectors, (ConnectionFactory[]) connectionFactories.toArray(new ConnectionFactory[0]));
        } else {
            connector = new ServerConnector(server, this.acceptors, this.selectors, (ConnectionFactory[]) connectionFactories.toArray(new ConnectionFactory[0]));
        }
        connector.setHost(address.getHostString());
        connector.setPort(address.getPort());
        return connector;
    }

    private Handler addHandlerWrappers(Handler handler) {
        if (getCompression() != null && getCompression().getEnabled()) {
            handler = applyWrapper(handler, JettyHandlerWrappers.createGzipHandlerWrapper(getCompression()));
        }
        if (StringUtils.hasText(getServerHeader())) {
            handler = applyWrapper(handler, JettyHandlerWrappers.createServerHeaderHandlerWrapper(getServerHeader()));
        }
        return handler;
    }

    private Handler applyWrapper(Handler handler, Handler.Wrapper wrapper) {
        wrapper.setHandler(handler);
        return wrapper;
    }

    private void customizeSsl(Server server, InetSocketAddress address) {
        new SslServerCustomizer(getHttp2(), address, getSsl().getClientAuth(), getSslBundle()).customize(server);
    }
}

package org.springframework.boot.web.embedded.tomcat;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Valve;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.AprLifecycleListener;
import org.apache.catalina.loader.WebappLoader;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.coyote.AbstractProtocol;
import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.http2.Http2Protocol;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.net.SSLUtilBase;
import org.apache.tomcat.util.scan.StandardJarScanFilter;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.util.LambdaSafe;
import org.springframework.boot.web.reactive.server.AbstractReactiveWebServerFactory;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.server.WebServer;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.TomcatHttpHandlerAdapter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/tomcat/TomcatReactiveWebServerFactory.class */
public class TomcatReactiveWebServerFactory extends AbstractReactiveWebServerFactory implements ConfigurableTomcatWebServerFactory {
    private static final Log logger = LogFactory.getLog((Class<?>) TomcatReactiveWebServerFactory.class);
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    public static final String DEFAULT_PROTOCOL = "org.apache.coyote.http11.Http11NioProtocol";
    private File baseDirectory;
    private final List<Valve> engineValves;
    private List<LifecycleListener> contextLifecycleListeners;
    private final List<LifecycleListener> serverLifecycleListeners;
    private Set<TomcatContextCustomizer> tomcatContextCustomizers;
    private Set<TomcatConnectorCustomizer> tomcatConnectorCustomizers;
    private Set<TomcatProtocolHandlerCustomizer<?>> tomcatProtocolHandlerCustomizers;
    private final List<Connector> additionalTomcatConnectors;
    private String protocol;
    private Charset uriEncoding;
    private int backgroundProcessorDelay;
    private boolean disableMBeanRegistry;

    public TomcatReactiveWebServerFactory() {
        this.engineValves = new ArrayList();
        this.contextLifecycleListeners = new ArrayList();
        this.serverLifecycleListeners = getDefaultServerLifecycleListeners();
        this.tomcatContextCustomizers = new LinkedHashSet();
        this.tomcatConnectorCustomizers = new LinkedHashSet();
        this.tomcatProtocolHandlerCustomizers = new LinkedHashSet();
        this.additionalTomcatConnectors = new ArrayList();
        this.protocol = "org.apache.coyote.http11.Http11NioProtocol";
        this.uriEncoding = DEFAULT_CHARSET;
        this.disableMBeanRegistry = true;
    }

    public TomcatReactiveWebServerFactory(int port) {
        super(port);
        this.engineValves = new ArrayList();
        this.contextLifecycleListeners = new ArrayList();
        this.serverLifecycleListeners = getDefaultServerLifecycleListeners();
        this.tomcatContextCustomizers = new LinkedHashSet();
        this.tomcatConnectorCustomizers = new LinkedHashSet();
        this.tomcatProtocolHandlerCustomizers = new LinkedHashSet();
        this.additionalTomcatConnectors = new ArrayList();
        this.protocol = "org.apache.coyote.http11.Http11NioProtocol";
        this.uriEncoding = DEFAULT_CHARSET;
        this.disableMBeanRegistry = true;
    }

    private static List<LifecycleListener> getDefaultServerLifecycleListeners() {
        AprLifecycleListener aprLifecycleListener = new AprLifecycleListener();
        return AprLifecycleListener.isAprAvailable() ? new ArrayList(Arrays.asList(aprLifecycleListener)) : new ArrayList();
    }

    @Override // org.springframework.boot.web.reactive.server.ReactiveWebServerFactory
    public WebServer getWebServer(HttpHandler httpHandler) {
        if (this.disableMBeanRegistry) {
            Registry.disableRegistry();
        }
        Tomcat tomcat = new Tomcat();
        File baseDir = this.baseDirectory != null ? this.baseDirectory : createTempDir(SSLUtilBase.DEFAULT_KEY_ALIAS);
        tomcat.setBaseDir(baseDir.getAbsolutePath());
        for (LifecycleListener listener : this.serverLifecycleListeners) {
            tomcat.getServer().addLifecycleListener(listener);
        }
        Connector connector = new Connector(this.protocol);
        connector.setThrowOnFailure(true);
        tomcat.getService().addConnector(connector);
        customizeConnector(connector);
        tomcat.setConnector(connector);
        tomcat.getHost().setAutoDeploy(false);
        configureEngine(tomcat.getEngine());
        for (Connector additionalConnector : this.additionalTomcatConnectors) {
            tomcat.getService().addConnector(additionalConnector);
        }
        TomcatHttpHandlerAdapter servlet = new TomcatHttpHandlerAdapter(httpHandler);
        prepareContext(tomcat.getHost(), servlet);
        return getTomcatWebServer(tomcat);
    }

    private void configureEngine(Engine engine) {
        engine.setBackgroundProcessorDelay(this.backgroundProcessorDelay);
        for (Valve valve : this.engineValves) {
            engine.getPipeline().addValve(valve);
        }
    }

    protected void prepareContext(Host host, TomcatHttpHandlerAdapter servlet) {
        File docBase = createTempDir("tomcat-docbase");
        TomcatEmbeddedContext context = new TomcatEmbeddedContext();
        context.setPath("");
        context.setDocBase(docBase.getAbsolutePath());
        context.addLifecycleListener(new Tomcat.FixContextListener());
        ClassLoader parentClassLoader = ClassUtils.getDefaultClassLoader();
        context.setParentClassLoader(parentClassLoader);
        skipAllTldScanning(context);
        WebappLoader loader = new WebappLoader();
        loader.setLoaderInstance(new TomcatEmbeddedWebappClassLoader(parentClassLoader));
        loader.setDelegate(true);
        context.setLoader(loader);
        Tomcat.addServlet(context, "httpHandlerServlet", servlet).setAsyncSupported(true);
        context.addServletMappingDecoded("/", "httpHandlerServlet");
        host.addChild(context);
        configureContext(context);
    }

    private void skipAllTldScanning(TomcatEmbeddedContext context) {
        StandardJarScanFilter filter = new StandardJarScanFilter();
        filter.setTldSkip("*.jar");
        context.getJarScanner().setJarScanFilter(filter);
    }

    protected void configureContext(Context context) {
        List<LifecycleListener> list = this.contextLifecycleListeners;
        Objects.requireNonNull(context);
        list.forEach(context::addLifecycleListener);
        new DisableReferenceClearingContextCustomizer().customize(context);
        this.tomcatContextCustomizers.forEach(customizer -> {
            customizer.customize(context);
        });
    }

    protected void customizeConnector(Connector connector) {
        int port = Math.max(getPort(), 0);
        connector.setPort(port);
        if (StringUtils.hasText(getServerHeader())) {
            connector.setProperty("server", getServerHeader());
        }
        if (connector.getProtocolHandler() instanceof AbstractProtocol) {
            customizeProtocol((AbstractProtocol) connector.getProtocolHandler());
        }
        invokeProtocolHandlerCustomizers(connector.getProtocolHandler());
        if (getUriEncoding() != null) {
            connector.setURIEncoding(getUriEncoding().name());
        }
        if (getHttp2() != null && getHttp2().isEnabled()) {
            connector.addUpgradeProtocol(new Http2Protocol());
        }
        if (Ssl.isEnabled(getSsl())) {
            customizeSsl(connector);
        }
        TomcatConnectorCustomizer compression = new CompressionConnectorCustomizer(getCompression());
        compression.customize(connector);
        for (TomcatConnectorCustomizer customizer : this.tomcatConnectorCustomizers) {
            customizer.customize(connector);
        }
    }

    private void invokeProtocolHandlerCustomizers(ProtocolHandler protocolHandler) {
        LambdaSafe.callbacks(TomcatProtocolHandlerCustomizer.class, this.tomcatProtocolHandlerCustomizers, protocolHandler, new Object[0]).invoke(customizer -> {
            customizer.customize(protocolHandler);
        });
    }

    private void customizeProtocol(AbstractProtocol<?> protocol) {
        if (getAddress() != null) {
            protocol.setAddress(getAddress());
        }
    }

    private void customizeSsl(Connector connector) {
        SslConnectorCustomizer customizer = new SslConnectorCustomizer(logger, connector, getSsl().getClientAuth());
        customizer.customize(getSslBundle());
        String sslBundleName = getSsl().getBundle();
        if (StringUtils.hasText(sslBundleName)) {
            SslBundles sslBundles = getSslBundles();
            Objects.requireNonNull(customizer);
            sslBundles.addBundleUpdateHandler(sslBundleName, customizer::update);
        }
    }

    @Override // org.springframework.boot.web.embedded.tomcat.ConfigurableTomcatWebServerFactory
    public void setBaseDirectory(File baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    @Override // org.springframework.boot.web.embedded.tomcat.ConfigurableTomcatWebServerFactory
    public void setBackgroundProcessorDelay(int delay) {
        this.backgroundProcessorDelay = delay;
    }

    public void setTomcatContextCustomizers(Collection<? extends TomcatContextCustomizer> tomcatContextCustomizers) {
        Assert.notNull(tomcatContextCustomizers, "TomcatContextCustomizers must not be null");
        this.tomcatContextCustomizers = new LinkedHashSet(tomcatContextCustomizers);
    }

    public Collection<TomcatContextCustomizer> getTomcatContextCustomizers() {
        return this.tomcatContextCustomizers;
    }

    @Override // org.springframework.boot.web.embedded.tomcat.ConfigurableTomcatWebServerFactory
    public void addContextCustomizers(TomcatContextCustomizer... tomcatContextCustomizers) {
        Assert.notNull(tomcatContextCustomizers, "TomcatContextCustomizers must not be null");
        this.tomcatContextCustomizers.addAll(Arrays.asList(tomcatContextCustomizers));
    }

    public void setTomcatConnectorCustomizers(Collection<? extends TomcatConnectorCustomizer> tomcatConnectorCustomizers) {
        Assert.notNull(tomcatConnectorCustomizers, "TomcatConnectorCustomizers must not be null");
        this.tomcatConnectorCustomizers = new LinkedHashSet(tomcatConnectorCustomizers);
    }

    @Override // org.springframework.boot.web.embedded.tomcat.ConfigurableTomcatWebServerFactory
    public void addConnectorCustomizers(TomcatConnectorCustomizer... tomcatConnectorCustomizers) {
        Assert.notNull(tomcatConnectorCustomizers, "TomcatConnectorCustomizers must not be null");
        this.tomcatConnectorCustomizers.addAll(Arrays.asList(tomcatConnectorCustomizers));
    }

    public Collection<TomcatConnectorCustomizer> getTomcatConnectorCustomizers() {
        return this.tomcatConnectorCustomizers;
    }

    public void setTomcatProtocolHandlerCustomizers(Collection<? extends TomcatProtocolHandlerCustomizer<?>> tomcatProtocolHandlerCustomizers) {
        Assert.notNull(tomcatProtocolHandlerCustomizers, "TomcatProtocolHandlerCustomizers must not be null");
        this.tomcatProtocolHandlerCustomizers = new LinkedHashSet(tomcatProtocolHandlerCustomizers);
    }

    @Override // org.springframework.boot.web.embedded.tomcat.ConfigurableTomcatWebServerFactory
    public void addProtocolHandlerCustomizers(TomcatProtocolHandlerCustomizer<?>... tomcatProtocolHandlerCustomizers) {
        Assert.notNull(tomcatProtocolHandlerCustomizers, "TomcatProtocolHandlerCustomizers must not be null");
        this.tomcatProtocolHandlerCustomizers.addAll(Arrays.asList(tomcatProtocolHandlerCustomizers));
    }

    public Collection<TomcatProtocolHandlerCustomizer<?>> getTomcatProtocolHandlerCustomizers() {
        return this.tomcatProtocolHandlerCustomizers;
    }

    public void addAdditionalTomcatConnectors(Connector... connectors) {
        Assert.notNull(connectors, "Connectors must not be null");
        this.additionalTomcatConnectors.addAll(Arrays.asList(connectors));
    }

    public List<Connector> getAdditionalTomcatConnectors() {
        return this.additionalTomcatConnectors;
    }

    @Override // org.springframework.boot.web.embedded.tomcat.ConfigurableTomcatWebServerFactory
    public void addEngineValves(Valve... engineValves) {
        Assert.notNull(engineValves, "Valves must not be null");
        this.engineValves.addAll(Arrays.asList(engineValves));
    }

    public List<Valve> getEngineValves() {
        return this.engineValves;
    }

    @Override // org.springframework.boot.web.embedded.tomcat.ConfigurableTomcatWebServerFactory
    public void setUriEncoding(Charset uriEncoding) {
        this.uriEncoding = uriEncoding;
    }

    public Charset getUriEncoding() {
        return this.uriEncoding;
    }

    public void setContextLifecycleListeners(Collection<? extends LifecycleListener> contextLifecycleListeners) {
        Assert.notNull(contextLifecycleListeners, "ContextLifecycleListeners must not be null");
        this.contextLifecycleListeners = new ArrayList(contextLifecycleListeners);
    }

    public Collection<LifecycleListener> getContextLifecycleListeners() {
        return this.contextLifecycleListeners;
    }

    public void addContextLifecycleListeners(LifecycleListener... contextLifecycleListeners) {
        Assert.notNull(contextLifecycleListeners, "ContextLifecycleListeners must not be null");
        this.contextLifecycleListeners.addAll(Arrays.asList(contextLifecycleListeners));
    }

    protected TomcatWebServer getTomcatWebServer(Tomcat tomcat) {
        return new TomcatWebServer(tomcat, getPort() >= 0, getShutdown());
    }

    public void setProtocol(String protocol) {
        Assert.hasLength(protocol, "Protocol must not be empty");
        this.protocol = protocol;
    }

    public void setDisableMBeanRegistry(boolean disableMBeanRegistry) {
        this.disableMBeanRegistry = disableMBeanRegistry;
    }
}

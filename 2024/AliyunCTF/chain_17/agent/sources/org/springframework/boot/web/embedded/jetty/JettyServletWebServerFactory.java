package org.springframework.boot.web.embedded.jetty;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EventListener;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.UUID;
import java.util.function.Consumer;
import org.eclipse.jetty.ee10.servlet.ErrorHandler;
import org.eclipse.jetty.ee10.servlet.ErrorPageErrorHandler;
import org.eclipse.jetty.ee10.servlet.ListenerHolder;
import org.eclipse.jetty.ee10.servlet.ServletHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.ee10.servlet.ServletMapping;
import org.eclipse.jetty.ee10.servlet.SessionHandler;
import org.eclipse.jetty.ee10.servlet.Source;
import org.eclipse.jetty.ee10.webapp.AbstractConfiguration;
import org.eclipse.jetty.ee10.webapp.Configuration;
import org.eclipse.jetty.ee10.webapp.WebAppContext;
import org.eclipse.jetty.ee10.webapp.WebInfConfiguration;
import org.eclipse.jetty.http.CookieCompliance;
import org.eclipse.jetty.http.HttpCookie;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.http.SetCookieParser;
import org.eclipse.jetty.http2.server.HTTP2CServerConnectionFactory;
import org.eclipse.jetty.server.AbstractConnector;
import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.ConnectionLimit;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.HttpCookieUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.eclipse.jetty.session.DefaultSessionCache;
import org.eclipse.jetty.session.FileSessionDataStore;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.resource.CombinedResource;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceFactory;
import org.eclipse.jetty.util.resource.URLResourceFactory;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.springframework.boot.web.server.Cookie;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.MimeMappings;
import org.springframework.boot.web.server.Shutdown;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.server.AbstractServletWebServerFactory;
import org.springframework.boot.web.servlet.server.CookieSameSiteSupplier;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/jetty/JettyServletWebServerFactory.class */
public class JettyServletWebServerFactory extends AbstractServletWebServerFactory implements ConfigurableJettyWebServerFactory, ResourceLoaderAware {
    private List<Configuration> configurations;
    private boolean useForwardHeaders;
    private int acceptors;
    private int selectors;
    private Set<JettyServerCustomizer> jettyServerCustomizers;
    private ResourceLoader resourceLoader;
    private ThreadPool threadPool;
    private int maxConnections;

    public JettyServletWebServerFactory() {
        this.configurations = new ArrayList();
        this.acceptors = -1;
        this.selectors = -1;
        this.jettyServerCustomizers = new LinkedHashSet();
        this.maxConnections = -1;
    }

    public JettyServletWebServerFactory(int port) {
        super(port);
        this.configurations = new ArrayList();
        this.acceptors = -1;
        this.selectors = -1;
        this.jettyServerCustomizers = new LinkedHashSet();
        this.maxConnections = -1;
    }

    public JettyServletWebServerFactory(String contextPath, int port) {
        super(contextPath, port);
        this.configurations = new ArrayList();
        this.acceptors = -1;
        this.selectors = -1;
        this.jettyServerCustomizers = new LinkedHashSet();
        this.maxConnections = -1;
    }

    @Override // org.springframework.boot.web.servlet.server.ServletWebServerFactory
    public WebServer getWebServer(ServletContextInitializer... initializers) {
        JettyEmbeddedWebAppContext context = new JettyEmbeddedWebAppContext();
        context.getContext().getServletContext().setExtendedListenerTypes(true);
        int port = Math.max(getPort(), 0);
        InetSocketAddress address = new InetSocketAddress(getAddress(), port);
        Server server = createServer(address);
        context.setServer(server);
        configureWebAppContext(context, initializers);
        server.setHandler(addHandlerWrappers(context));
        this.logger.info("Server initialized with port: " + port);
        if (this.maxConnections > -1) {
            server.addBean(new ConnectionLimit(this.maxConnections, server.getConnectors()));
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
        return getJettyWebServer(server);
    }

    private Server createServer(InetSocketAddress address) {
        Server server = new Server(getThreadPool());
        server.setConnectors(new Connector[]{createConnector(address, server)});
        server.setStopTimeout(0L);
        MimeTypes.Mutable mimeTypes = server.getMimeTypes();
        Iterator<MimeMappings.Mapping> it = getMimeMappings().iterator();
        while (it.hasNext()) {
            MimeMappings.Mapping mapping = it.next();
            mimeTypes.addMimeMapping(mapping.getExtension(), mapping.getMimeType());
        }
        return server;
    }

    private AbstractConnector createConnector(InetSocketAddress address, Server server) {
        HttpConfiguration httpConfiguration = new HttpConfiguration();
        httpConfiguration.setSendServerVersion(false);
        List<ConnectionFactory> connectionFactories = new ArrayList<>();
        connectionFactories.add(new HttpConnectionFactory(httpConfiguration));
        if (getHttp2() != null && getHttp2().isEnabled()) {
            connectionFactories.add(new HTTP2CServerConnectionFactory(httpConfiguration));
        }
        ServerConnector connector = new ServerConnector(server, this.acceptors, this.selectors, (ConnectionFactory[]) connectionFactories.toArray(new ConnectionFactory[0]));
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
        if (!CollectionUtils.isEmpty(getCookieSameSiteSuppliers())) {
            handler = applyWrapper(handler, new SuppliedSameSiteCookieHandlerWrapper(getCookieSameSiteSuppliers()));
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

    protected final void configureWebAppContext(WebAppContext context, ServletContextInitializer... initializers) {
        Assert.notNull(context, "Context must not be null");
        context.clearAliasChecks();
        if (this.resourceLoader != null) {
            context.setClassLoader(this.resourceLoader.getClassLoader());
        }
        String contextPath = getContextPath();
        context.setContextPath(StringUtils.hasLength(contextPath) ? contextPath : "/");
        context.setDisplayName(getDisplayName());
        configureDocumentRoot(context);
        if (isRegisterDefaultServlet()) {
            addDefaultServlet(context);
        }
        if (shouldRegisterJspServlet()) {
            addJspServlet(context);
            context.addBean(new JasperInitializer(context), true);
        }
        addLocaleMappings(context);
        ServletContextInitializer[] initializersToUse = mergeInitializers(initializers);
        Configuration[] configurations = getWebAppContextConfigurations(context, initializersToUse);
        context.setConfigurations(configurations);
        context.setThrowUnavailableOnStartupException(true);
        configureSession(context);
        context.setTempDirectory(getTempDirectory(context));
        postProcessWebAppContext(context);
    }

    private void configureSession(WebAppContext context) {
        SessionHandler handler = context.getSessionHandler();
        Cookie.SameSite sessionSameSite = getSession().getCookie().getSameSite();
        if (sessionSameSite != null) {
            handler.setSameSite(HttpCookie.SameSite.valueOf(sessionSameSite.name()));
        }
        Duration sessionTimeout = getSession().getTimeout();
        handler.setMaxInactiveInterval(isNegative(sessionTimeout) ? -1 : (int) sessionTimeout.getSeconds());
        if (getSession().isPersistent()) {
            DefaultSessionCache cache = new DefaultSessionCache(handler);
            FileSessionDataStore store = new FileSessionDataStore();
            store.setStoreDir(getValidSessionStoreDir());
            cache.setSessionDataStore(store);
            handler.setSessionCache(cache);
        }
    }

    private boolean isNegative(Duration sessionTimeout) {
        return sessionTimeout == null || sessionTimeout.isNegative();
    }

    private void addLocaleMappings(WebAppContext context) {
        getLocaleCharsetMappings().forEach((locale, charset) -> {
            context.addLocaleEncoding(locale.toString(), charset.toString());
        });
    }

    private File getTempDirectory(WebAppContext context) {
        String temp = System.getProperty("java.io.tmpdir");
        if (temp != null) {
            return new File(temp, WebInfConfiguration.getCanonicalNameForWebAppTmpDir(context) + UUID.randomUUID());
        }
        return null;
    }

    private void configureDocumentRoot(WebAppContext handler) {
        Resource newJarFileResource;
        File root = getValidDocumentRoot();
        File docBase = root != null ? root : createTempDir("jetty-docbase");
        try {
            ResourceFactory resourceFactory = handler.getResourceFactory();
            List<Resource> resources = new ArrayList<>();
            if (docBase.isDirectory()) {
                newJarFileResource = resourceFactory.newResource(docBase.getCanonicalFile().toURI());
            } else {
                newJarFileResource = resourceFactory.newJarFileResource(docBase.toURI());
            }
            Resource rootResource = newJarFileResource;
            resources.add(root != null ? new LoaderHidingResource(rootResource, rootResource) : rootResource);
            URLResourceFactory urlResourceFactory = new URLResourceFactory();
            for (URL resourceJarUrl : getUrlsOfJarsWithMetaInfResources()) {
                Resource resource = createResource(resourceJarUrl, resourceFactory, urlResourceFactory);
                if (resource != null) {
                    resources.add(resource);
                }
            }
            handler.setBaseResource(ResourceFactory.combine(resources));
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    private Resource createResource(URL url, ResourceFactory resourceFactory, URLResourceFactory urlResourceFactory) throws Exception {
        if ("file".equals(url.getProtocol())) {
            File file = new File(url.toURI());
            if (file.isFile()) {
                return resourceFactory.newResource("jar:" + url + "!/META-INF/resources/");
            }
            if (file.isDirectory()) {
                return resourceFactory.newResource(url).resolve("META-INF/resources/");
            }
        }
        return urlResourceFactory.newResource(url + "META-INF/resources/");
    }

    protected final void addDefaultServlet(WebAppContext context) {
        Assert.notNull(context, "Context must not be null");
        ServletHolder holder = new ServletHolder();
        holder.setName("default");
        holder.setClassName("org.eclipse.jetty.ee10.servlet.DefaultServlet");
        holder.setInitParameter("dirAllowed", "false");
        holder.setInitOrder(1);
        context.getServletHandler().addServletWithMapping(holder, "/");
        ServletMapping servletMapping = context.getServletHandler().getServletMapping("/");
        servletMapping.setFromDefaultDescriptor(true);
    }

    protected final void addJspServlet(WebAppContext context) {
        Assert.notNull(context, "Context must not be null");
        ServletHolder holder = new ServletHolder();
        holder.setName("jsp");
        holder.setClassName(getJsp().getClassName());
        holder.setInitParameter("fork", "false");
        holder.setInitParameters(getJsp().getInitParameters());
        holder.setInitOrder(3);
        context.getServletHandler().addServlet(holder);
        ServletMapping mapping = new ServletMapping();
        mapping.setServletName("jsp");
        mapping.setPathSpecs(new String[]{"*.jsp", "*.jspx"});
        context.getServletHandler().addServletMapping(mapping);
    }

    protected Configuration[] getWebAppContextConfigurations(WebAppContext webAppContext, ServletContextInitializer... initializers) {
        List<Configuration> configurations = new ArrayList<>();
        configurations.add(getServletContextInitializerConfiguration(webAppContext, initializers));
        configurations.add(getErrorPageConfiguration());
        configurations.add(getMimeTypeConfiguration());
        configurations.add(new WebListenersConfiguration(getWebListenerClassNames()));
        configurations.addAll(getConfigurations());
        return (Configuration[]) configurations.toArray(new Configuration[0]);
    }

    private Configuration getErrorPageConfiguration() {
        return new AbstractConfiguration(new AbstractConfiguration.Builder()) { // from class: org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory.1
            public void configure(WebAppContext context) throws Exception {
                ErrorHandler jettyEmbeddedErrorHandler = new JettyEmbeddedErrorHandler();
                context.setErrorHandler(jettyEmbeddedErrorHandler);
                JettyServletWebServerFactory.this.addJettyErrorPages(jettyEmbeddedErrorHandler, JettyServletWebServerFactory.this.getErrorPages());
            }
        };
    }

    private Configuration getMimeTypeConfiguration() {
        return new AbstractConfiguration(new AbstractConfiguration.Builder()) { // from class: org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory.2
            public void configure(WebAppContext context) throws Exception {
                MimeTypes.Wrapper mimeTypes = context.getMimeTypes();
                mimeTypes.setWrapped(new MimeTypes((MimeTypes) null));
                Iterator<MimeMappings.Mapping> it = JettyServletWebServerFactory.this.getMimeMappings().iterator();
                while (it.hasNext()) {
                    MimeMappings.Mapping mapping = it.next();
                    mimeTypes.addMimeMapping(mapping.getExtension(), mapping.getMimeType());
                }
            }
        };
    }

    protected Configuration getServletContextInitializerConfiguration(WebAppContext webAppContext, ServletContextInitializer... initializers) {
        return new ServletContextInitializerConfiguration(initializers);
    }

    protected void postProcessWebAppContext(WebAppContext webAppContext) {
    }

    protected JettyWebServer getJettyWebServer(Server server) {
        return new JettyWebServer(server, getPort() >= 0);
    }

    @Override // org.springframework.context.ResourceLoaderAware
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override // org.springframework.boot.web.embedded.jetty.ConfigurableJettyWebServerFactory
    public void setUseForwardHeaders(boolean useForwardHeaders) {
        this.useForwardHeaders = useForwardHeaders;
    }

    @Override // org.springframework.boot.web.embedded.jetty.ConfigurableJettyWebServerFactory
    public void setAcceptors(int acceptors) {
        this.acceptors = acceptors;
    }

    @Override // org.springframework.boot.web.embedded.jetty.ConfigurableJettyWebServerFactory
    public void setSelectors(int selectors) {
        this.selectors = selectors;
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

    @Override // org.springframework.boot.web.embedded.jetty.ConfigurableJettyWebServerFactory
    public void addServerCustomizers(JettyServerCustomizer... customizers) {
        Assert.notNull(customizers, "Customizers must not be null");
        this.jettyServerCustomizers.addAll(Arrays.asList(customizers));
    }

    public void setConfigurations(Collection<? extends Configuration> configurations) {
        Assert.notNull(configurations, "Configurations must not be null");
        this.configurations = new ArrayList(configurations);
    }

    public Collection<Configuration> getConfigurations() {
        return this.configurations;
    }

    public void addConfigurations(Configuration... configurations) {
        Assert.notNull(configurations, "Configurations must not be null");
        this.configurations.addAll(Arrays.asList(configurations));
    }

    public ThreadPool getThreadPool() {
        return this.threadPool;
    }

    @Override // org.springframework.boot.web.embedded.jetty.ConfigurableJettyWebServerFactory
    public void setThreadPool(ThreadPool threadPool) {
        this.threadPool = threadPool;
    }

    private void addJettyErrorPages(ErrorHandler errorHandler, Collection<ErrorPage> errorPages) {
        if (errorHandler instanceof ErrorPageErrorHandler) {
            ErrorPageErrorHandler handler = (ErrorPageErrorHandler) errorHandler;
            for (ErrorPage errorPage : errorPages) {
                if (errorPage.isGlobal()) {
                    handler.addErrorPage("org.eclipse.jetty.server.error_page.global", errorPage.getPath());
                } else if (errorPage.getExceptionName() != null) {
                    handler.addErrorPage(errorPage.getExceptionName(), errorPage.getPath());
                } else {
                    handler.addErrorPage(errorPage.getStatusCode(), errorPage.getPath());
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/jetty/JettyServletWebServerFactory$LoaderHidingResource.class */
    public static final class LoaderHidingResource extends Resource {
        private static final String LOADER_RESOURCE_PATH_PREFIX = "/org/springframework/boot/";
        private final Resource base;
        private final Resource delegate;

        private LoaderHidingResource(Resource base, Resource delegate) {
            this.base = base;
            this.delegate = delegate;
        }

        public void forEach(Consumer<? super Resource> action) {
            this.delegate.forEach(action);
        }

        public Path getPath() {
            return this.delegate.getPath();
        }

        public boolean isContainedIn(Resource r) {
            return this.delegate.isContainedIn(r);
        }

        public Iterator<Resource> iterator() {
            if (this.delegate instanceof CombinedResource) {
                return list().iterator();
            }
            return List.of(this).iterator();
        }

        public boolean equals(Object obj) {
            return this.delegate.equals(obj);
        }

        public int hashCode() {
            return this.delegate.hashCode();
        }

        public boolean exists() {
            return this.delegate.exists();
        }

        public Spliterator<Resource> spliterator() {
            return this.delegate.spliterator();
        }

        public boolean isDirectory() {
            return this.delegate.isDirectory();
        }

        public boolean isReadable() {
            return this.delegate.isReadable();
        }

        public Instant lastModified() {
            return this.delegate.lastModified();
        }

        public long length() {
            return this.delegate.length();
        }

        public URI getURI() {
            return this.delegate.getURI();
        }

        public String getName() {
            return this.delegate.getName();
        }

        public String getFileName() {
            return this.delegate.getFileName();
        }

        public InputStream newInputStream() throws IOException {
            return this.delegate.newInputStream();
        }

        public ReadableByteChannel newReadableByteChannel() throws IOException {
            return this.delegate.newReadableByteChannel();
        }

        public List<Resource> list() {
            return this.delegate.list().stream().filter(this::nonLoaderResource).toList();
        }

        private boolean nonLoaderResource(Resource resource) {
            Path prefix = this.base.getPath().resolve(Path.of("org", "springframework", "boot"));
            return !resource.getPath().startsWith(prefix);
        }

        public Resource resolve(String subUriPath) {
            Resource resolved;
            if (subUriPath.startsWith(LOADER_RESOURCE_PATH_PREFIX) || (resolved = this.delegate.resolve(subUriPath)) == null) {
                return null;
            }
            return new LoaderHidingResource(this.base, resolved);
        }

        public boolean isAlias() {
            return this.delegate.isAlias();
        }

        public URI getRealURI() {
            return this.delegate.getRealURI();
        }

        public void copyTo(Path destination) throws IOException {
            this.delegate.copyTo(destination);
        }

        public Collection<Resource> getAllResources() {
            return this.delegate.getAllResources().stream().filter(this::nonLoaderResource).toList();
        }

        public String toString() {
            return this.delegate.toString();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/jetty/JettyServletWebServerFactory$WebListenersConfiguration.class */
    public static class WebListenersConfiguration extends AbstractConfiguration {
        private final Set<String> classNames;

        WebListenersConfiguration(Set<String> webListenerClassNames) {
            super(new AbstractConfiguration.Builder());
            this.classNames = webListenerClassNames;
        }

        public void configure(WebAppContext context) throws Exception {
            ServletHandler servletHandler = context.getServletHandler();
            for (String className : this.classNames) {
                configure(context, servletHandler, className);
            }
        }

        private void configure(WebAppContext context, ServletHandler servletHandler, String className) throws ClassNotFoundException {
            ListenerHolder holder = servletHandler.newListenerHolder(new Source(Source.Origin.ANNOTATION, className));
            holder.setHeldClass(loadClass(context, className));
            servletHandler.addListener(holder);
        }

        private Class<? extends EventListener> loadClass(WebAppContext context, String className) throws ClassNotFoundException {
            ClassLoader classLoader = context.getClassLoader();
            return (classLoader != null ? classLoader : getClass().getClassLoader()).loadClass(className);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/jetty/JettyServletWebServerFactory$SuppliedSameSiteCookieHandlerWrapper.class */
    public static class SuppliedSameSiteCookieHandlerWrapper extends Handler.Wrapper {
        private static final SetCookieParser setCookieParser = SetCookieParser.newInstance();
        private final List<CookieSameSiteSupplier> suppliers;

        SuppliedSameSiteCookieHandlerWrapper(List<CookieSameSiteSupplier> suppliers) {
            this.suppliers = suppliers;
        }

        public boolean handle(Request request, Response response, Callback callback) throws Exception {
            SuppliedSameSiteCookieResponse wrappedResponse = new SuppliedSameSiteCookieResponse(request, response);
            return super.handle(request, wrappedResponse, callback);
        }

        /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/jetty/JettyServletWebServerFactory$SuppliedSameSiteCookieHandlerWrapper$SuppliedSameSiteCookieResponse.class */
        private class SuppliedSameSiteCookieResponse extends Response.Wrapper {
            private HttpFields.Mutable wrappedHeaders;

            SuppliedSameSiteCookieResponse(Request request, Response wrapped) {
                super(request, wrapped);
                this.wrappedHeaders = new SuppliedSameSiteCookieHeaders(request.getConnectionMetaData().getHttpConfiguration().getResponseCookieCompliance(), wrapped.getHeaders());
            }

            public HttpFields.Mutable getHeaders() {
                return this.wrappedHeaders;
            }
        }

        /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/jetty/JettyServletWebServerFactory$SuppliedSameSiteCookieHandlerWrapper$SuppliedSameSiteCookieHeaders.class */
        private class SuppliedSameSiteCookieHeaders extends HttpFields.Mutable.Wrapper {
            private final CookieCompliance compliance;

            SuppliedSameSiteCookieHeaders(CookieCompliance compliance, HttpFields.Mutable fields) {
                super(fields);
                this.compliance = compliance;
            }

            public HttpField onAddField(HttpField field) {
                return field.getHeader() != HttpHeader.SET_COOKIE ? field : onAddSetCookieField(field);
            }

            private HttpField onAddSetCookieField(HttpField field) {
                HttpCookie cookie = SuppliedSameSiteCookieHandlerWrapper.setCookieParser.parse(field.getValue());
                Cookie.SameSite sameSite = cookie != null ? getSameSite(cookie) : null;
                if (sameSite == null) {
                    return field;
                }
                HttpCookie updatedCookie = buildCookieWithUpdatedSameSite(cookie, sameSite);
                return new HttpCookieUtils.SetCookieHttpField(updatedCookie, this.compliance);
            }

            private HttpCookie buildCookieWithUpdatedSameSite(HttpCookie cookie, Cookie.SameSite sameSite) {
                return HttpCookie.build(cookie).sameSite(HttpCookie.SameSite.from(sameSite.name())).build();
            }

            private Cookie.SameSite getSameSite(HttpCookie cookie) {
                return getSameSite(asServletCookie(cookie));
            }

            private Cookie.SameSite getSameSite(jakarta.servlet.http.Cookie cookie) {
                return (Cookie.SameSite) SuppliedSameSiteCookieHandlerWrapper.this.suppliers.stream().map(supplier -> {
                    return supplier.getSameSite(cookie);
                }).filter((v0) -> {
                    return Objects.nonNull(v0);
                }).findFirst().orElse(null);
            }

            private jakarta.servlet.http.Cookie asServletCookie(HttpCookie cookie) {
                jakarta.servlet.http.Cookie servletCookie = new jakarta.servlet.http.Cookie(cookie.getName(), cookie.getValue());
                Map attributes = cookie.getAttributes();
                Objects.requireNonNull(servletCookie);
                attributes.forEach(servletCookie::setAttribute);
                return servletCookie;
            }
        }
    }
}

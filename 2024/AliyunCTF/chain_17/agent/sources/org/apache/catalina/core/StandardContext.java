package org.apache.catalina.core;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.FilterRegistration;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextAttributeListener;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletRequestAttributeListener;
import jakarta.servlet.ServletRequestEvent;
import jakarta.servlet.ServletRequestListener;
import jakarta.servlet.ServletSecurityElement;
import jakarta.servlet.SessionCookieConfig;
import jakarta.servlet.SessionTrackingMode;
import jakarta.servlet.descriptor.JspConfigDescriptor;
import jakarta.servlet.http.HttpSessionAttributeListener;
import jakarta.servlet.http.HttpSessionIdListener;
import jakarta.servlet.http.HttpSessionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanNotificationInfo;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.NotificationEmitter;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.naming.NamingException;
import org.apache.catalina.Authenticator;
import org.apache.catalina.Container;
import org.apache.catalina.ContainerListener;
import org.apache.catalina.Context;
import org.apache.catalina.CredentialHandler;
import org.apache.catalina.Globals;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Loader;
import org.apache.catalina.Manager;
import org.apache.catalina.Pipeline;
import org.apache.catalina.Realm;
import org.apache.catalina.ThreadBindingListener;
import org.apache.catalina.Valve;
import org.apache.catalina.WebResource;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.Wrapper;
import org.apache.catalina.deploy.NamingResourcesImpl;
import org.apache.catalina.loader.WebappClassLoaderBase;
import org.apache.catalina.loader.WebappLoader;
import org.apache.catalina.session.StandardManager;
import org.apache.catalina.util.CharsetMapper;
import org.apache.catalina.util.ContextName;
import org.apache.catalina.util.ErrorPageSupport;
import org.apache.catalina.util.URLEncoder;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.naming.ContextBindings;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.InstanceManagerBindings;
import org.apache.tomcat.JarScanner;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.buf.StringUtils;
import org.apache.tomcat.util.compat.JreCompat;
import org.apache.tomcat.util.descriptor.XmlIdentifiers;
import org.apache.tomcat.util.descriptor.web.ApplicationParameter;
import org.apache.tomcat.util.descriptor.web.ErrorPage;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.apache.tomcat.util.descriptor.web.Injectable;
import org.apache.tomcat.util.descriptor.web.InjectionTarget;
import org.apache.tomcat.util.descriptor.web.LoginConfig;
import org.apache.tomcat.util.descriptor.web.MessageDestination;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.apache.tomcat.util.http.CookieProcessor;
import org.apache.tomcat.util.http.Rfc6265CookieProcessor;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.scan.StandardJarScanner;
import org.apache.tomcat.util.security.PrivilegedGetTccl;
import org.apache.tomcat.util.security.PrivilegedSetTccl;
import org.apache.tomcat.util.threads.ScheduledThreadPoolExecutor;
import org.springframework.util.backoff.ExponentialBackOff;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/StandardContext.class */
public class StandardContext extends ContainerBase implements Context, NotificationEmitter {
    private NotificationBroadcasterSupport broadcaster;
    private boolean denyUncoveredHttpMethods;
    private String defaultContextXml;
    private String defaultWebXml;
    private WebResourceRoot resources;
    private long startupTime;
    private long startTime;
    private long tldScanTime;
    private String sessionCookieName;
    private String sessionCookieDomain;
    private String sessionCookiePath;
    private String containerSciFilter;
    private Boolean failCtxIfServletStartFails;
    private CookieProcessor cookieProcessor;
    private boolean useRelativeRedirects;
    private boolean dispatchersUseEncodedPaths;
    private String requestEncoding;
    private String responseEncoding;
    private boolean allowMultipleLeadingForwardSlashInPath;
    private final AtomicLong inProgressAsyncCount;
    private boolean createUploadTargets;
    private boolean alwaysAccessSession;
    private boolean contextGetResourceRequiresSlash;
    private boolean dispatcherWrapsSameObject;
    private boolean parallelAnnotationScanning;
    private boolean useBloomFilterForArchives;
    private MBeanNotificationInfo[] notificationInfo;
    private String server;
    private static final Log log = LogFactory.getLog((Class<?>) StandardContext.class);
    protected static final ThreadBindingListener DEFAULT_NAMING_LISTENER = new ThreadBindingListener() { // from class: org.apache.catalina.core.StandardContext.1
        @Override // org.apache.catalina.ThreadBindingListener
        public void bind() {
        }

        @Override // org.apache.catalina.ThreadBindingListener
        public void unbind() {
        }
    };
    protected boolean allowCasualMultipartParsing = false;
    private boolean swallowAbortedUploads = true;
    private String altDDName = null;
    private InstanceManager instanceManager = null;
    private boolean antiResourceLocking = false;
    private CopyOnWriteArrayList<String> applicationListeners = new CopyOnWriteArrayList<>();
    private final Set<Object> noPluggabilityListeners = new HashSet();
    private List<Object> applicationEventListenersList = new CopyOnWriteArrayList();
    private Object[] applicationLifecycleListenersObjects = new Object[0];
    private Map<ServletContainerInitializer, Set<Class<?>>> initializers = new LinkedHashMap();
    private ApplicationParameter[] applicationParameters = new ApplicationParameter[0];
    private final Object applicationParametersLock = new Object();
    private CharsetMapper charsetMapper = null;
    private String charsetMapperClass = "org.apache.catalina.util.CharsetMapper";
    private URL configFile = null;
    private boolean configured = false;
    private volatile SecurityConstraint[] constraints = new SecurityConstraint[0];
    private final Object constraintsLock = new Object();
    protected ApplicationContext context = null;
    private NoPluggabilityServletContext noPluggabilityServletContext = null;
    private boolean cookies = true;
    private boolean crossContext = false;
    private String encodedPath = null;
    private String path = null;
    private boolean delegate = JreCompat.isGraalAvailable();
    private String displayName = null;
    private boolean distributable = false;
    private String docBase = null;
    private final ErrorPageSupport errorPageSupport = new ErrorPageSupport();
    private Map<String, ApplicationFilterConfig> filterConfigs = new HashMap();
    private Map<String, FilterDef> filterDefs = new HashMap();
    private final ContextFilterMaps filterMaps = new ContextFilterMaps();
    private boolean ignoreAnnotations = false;
    private Loader loader = null;
    private final ReadWriteLock loaderLock = new ReentrantReadWriteLock();
    private LoginConfig loginConfig = null;
    protected Manager manager = null;
    private final ReadWriteLock managerLock = new ReentrantReadWriteLock();
    private NamingContextListener namingContextListener = null;
    private NamingResourcesImpl namingResources = null;
    private HashMap<String, MessageDestination> messageDestinations = new HashMap<>();
    private Map<String, String> mimeMappings = new HashMap();
    private final Map<String, String> parameters = new ConcurrentHashMap();
    private volatile boolean paused = false;
    private String publicId = null;
    private boolean reloadable = false;
    private boolean unpackWAR = true;
    private boolean copyXML = false;
    private boolean override = false;
    private String originalDocBase = null;
    private boolean privileged = false;
    private boolean replaceWelcomeFiles = false;
    private Map<String, String> roleMappings = new HashMap();
    private String[] securityRoles = new String[0];
    private final Object securityRolesLock = new Object();
    private Map<String, String> servletMappings = new HashMap();
    private final Object servletMappingsLock = new Object();
    private int sessionTimeout = 30;
    private AtomicLong sequenceNumber = new AtomicLong(0);
    private boolean swallowOutput = false;
    private long unloadDelay = ExponentialBackOff.DEFAULT_INITIAL_INTERVAL;
    private String[] watchedResources = new String[0];
    private final Object watchedResourcesLock = new Object();
    private String[] welcomeFiles = new String[0];
    private final Object welcomeFilesLock = new Object();
    private String[] wrapperLifecycles = new String[0];
    private final Object wrapperLifecyclesLock = new Object();
    private String[] wrapperListeners = new String[0];
    private final Object wrapperListenersLock = new Object();
    private String workDir = null;
    private String wrapperClassName = StandardWrapper.class.getName();
    private Class<?> wrapperClass = null;
    private boolean useNaming = true;
    private String namingContextName = null;
    private final ReadWriteLock resourcesLock = new ReentrantReadWriteLock();
    private String j2EEApplication = "none";
    private String j2EEServer = "none";
    private boolean webXmlValidation = Globals.STRICT_SERVLET_COMPLIANCE;
    private boolean webXmlNamespaceAware = Globals.STRICT_SERVLET_COMPLIANCE;
    private boolean xmlBlockExternal = true;
    private boolean tldValidation = Globals.STRICT_SERVLET_COMPLIANCE;
    private boolean useHttpOnly = true;
    private boolean usePartitioned = false;
    private boolean sessionCookiePathUsesTrailingSlash = false;
    private JarScanner jarScanner = null;
    private boolean clearReferencesRmiTargets = true;
    private boolean clearReferencesStopThreads = false;
    private boolean clearReferencesStopTimerThreads = false;
    private boolean clearReferencesHttpClientKeepAliveThread = true;
    private boolean renewThreadsWhenStoppingContext = true;
    private boolean clearReferencesObjectStreamClassCaches = true;
    private boolean clearReferencesThreadLocals = true;
    private boolean skipMemoryLeakChecksOnJvmShutdown = false;
    private boolean logEffectiveWebXml = false;
    private int effectiveMajorVersion = 3;
    private int effectiveMinorVersion = 0;
    private JspConfigDescriptor jspConfigDescriptor = null;
    private Set<String> resourceOnlyServlets = new HashSet();
    private String webappVersion = "";
    private boolean addWebinfClassesResources = false;
    private boolean fireRequestListenersOnForwards = false;
    private Set<Servlet> createdServlets = new HashSet();
    private boolean preemptiveAuthentication = false;
    private boolean sendRedirectBody = false;
    private boolean jndiExceptionOnFailedWrite = true;
    private Map<String, String> postConstructMethods = new HashMap();
    private Map<String, String> preDestroyMethods = new HashMap();
    protected ThreadBindingListener threadBindingListener = DEFAULT_NAMING_LISTENER;
    private final Object namingToken = new Object();
    private boolean validateClientProvidedNewSessionId = true;
    private boolean mapperContextRootRedirectEnabled = true;
    private boolean mapperDirectoryRedirectEnabled = false;

    public StandardContext() {
        this.broadcaster = null;
        this.useRelativeRedirects = !Globals.STRICT_SERVLET_COMPLIANCE;
        this.dispatchersUseEncodedPaths = true;
        this.requestEncoding = null;
        this.responseEncoding = null;
        this.allowMultipleLeadingForwardSlashInPath = false;
        this.inProgressAsyncCount = new AtomicLong(0L);
        this.createUploadTargets = false;
        this.alwaysAccessSession = Globals.STRICT_SERVLET_COMPLIANCE;
        this.contextGetResourceRequiresSlash = Globals.STRICT_SERVLET_COMPLIANCE;
        this.dispatcherWrapsSameObject = Globals.STRICT_SERVLET_COMPLIANCE;
        this.parallelAnnotationScanning = false;
        this.useBloomFilterForArchives = false;
        this.server = null;
        this.pipeline.setBasic(new StandardContextValve());
        this.broadcaster = new NotificationBroadcasterSupport();
        if (!Globals.STRICT_SERVLET_COMPLIANCE) {
            this.resourceOnlyServlets.add("jsp");
        }
    }

    @Override // org.apache.catalina.Context
    public void setCreateUploadTargets(boolean createUploadTargets) {
        this.createUploadTargets = createUploadTargets;
    }

    @Override // org.apache.catalina.Context
    public boolean getCreateUploadTargets() {
        return this.createUploadTargets;
    }

    @Override // org.apache.catalina.Context
    public void incrementInProgressAsyncCount() {
        this.inProgressAsyncCount.incrementAndGet();
    }

    @Override // org.apache.catalina.Context
    public void decrementInProgressAsyncCount() {
        this.inProgressAsyncCount.decrementAndGet();
    }

    public long getInProgressAsyncCount() {
        return this.inProgressAsyncCount.get();
    }

    @Override // org.apache.catalina.Context
    public void setAllowMultipleLeadingForwardSlashInPath(boolean allowMultipleLeadingForwardSlashInPath) {
        this.allowMultipleLeadingForwardSlashInPath = allowMultipleLeadingForwardSlashInPath;
    }

    @Override // org.apache.catalina.Context
    public boolean getAllowMultipleLeadingForwardSlashInPath() {
        return this.allowMultipleLeadingForwardSlashInPath;
    }

    @Override // org.apache.catalina.Context
    public boolean getAlwaysAccessSession() {
        return this.alwaysAccessSession;
    }

    @Override // org.apache.catalina.Context
    public void setAlwaysAccessSession(boolean alwaysAccessSession) {
        this.alwaysAccessSession = alwaysAccessSession;
    }

    @Override // org.apache.catalina.Context
    public boolean getContextGetResourceRequiresSlash() {
        return this.contextGetResourceRequiresSlash;
    }

    @Override // org.apache.catalina.Context
    public void setContextGetResourceRequiresSlash(boolean contextGetResourceRequiresSlash) {
        this.contextGetResourceRequiresSlash = contextGetResourceRequiresSlash;
    }

    @Override // org.apache.catalina.Context
    public boolean getDispatcherWrapsSameObject() {
        return this.dispatcherWrapsSameObject;
    }

    @Override // org.apache.catalina.Context
    public void setDispatcherWrapsSameObject(boolean dispatcherWrapsSameObject) {
        this.dispatcherWrapsSameObject = dispatcherWrapsSameObject;
    }

    @Override // org.apache.catalina.Context
    public String getRequestCharacterEncoding() {
        return this.requestEncoding;
    }

    @Override // org.apache.catalina.Context
    public void setRequestCharacterEncoding(String requestEncoding) {
        this.requestEncoding = requestEncoding;
    }

    @Override // org.apache.catalina.Context
    public String getResponseCharacterEncoding() {
        return this.responseEncoding;
    }

    @Override // org.apache.catalina.Context
    public void setResponseCharacterEncoding(String responseEncoding) {
        if (responseEncoding == null) {
            this.responseEncoding = null;
        } else {
            this.responseEncoding = new String(responseEncoding);
        }
    }

    @Override // org.apache.catalina.Context
    public void setDispatchersUseEncodedPaths(boolean dispatchersUseEncodedPaths) {
        this.dispatchersUseEncodedPaths = dispatchersUseEncodedPaths;
    }

    @Override // org.apache.catalina.Context
    public boolean getDispatchersUseEncodedPaths() {
        return this.dispatchersUseEncodedPaths;
    }

    @Override // org.apache.catalina.Context
    public void setUseRelativeRedirects(boolean useRelativeRedirects) {
        this.useRelativeRedirects = useRelativeRedirects;
    }

    @Override // org.apache.catalina.Context
    public boolean getUseRelativeRedirects() {
        return this.useRelativeRedirects;
    }

    @Override // org.apache.catalina.Context
    public void setMapperContextRootRedirectEnabled(boolean mapperContextRootRedirectEnabled) {
        this.mapperContextRootRedirectEnabled = mapperContextRootRedirectEnabled;
    }

    @Override // org.apache.catalina.Context
    public boolean getMapperContextRootRedirectEnabled() {
        return this.mapperContextRootRedirectEnabled;
    }

    @Override // org.apache.catalina.Context
    public void setMapperDirectoryRedirectEnabled(boolean mapperDirectoryRedirectEnabled) {
        this.mapperDirectoryRedirectEnabled = mapperDirectoryRedirectEnabled;
    }

    @Override // org.apache.catalina.Context
    public boolean getMapperDirectoryRedirectEnabled() {
        return this.mapperDirectoryRedirectEnabled;
    }

    @Override // org.apache.catalina.Context
    public void setValidateClientProvidedNewSessionId(boolean validateClientProvidedNewSessionId) {
        this.validateClientProvidedNewSessionId = validateClientProvidedNewSessionId;
    }

    @Override // org.apache.catalina.Context
    public boolean getValidateClientProvidedNewSessionId() {
        return this.validateClientProvidedNewSessionId;
    }

    @Override // org.apache.catalina.Context
    public void setCookieProcessor(CookieProcessor cookieProcessor) {
        if (cookieProcessor == null) {
            throw new IllegalArgumentException(sm.getString("standardContext.cookieProcessor.null"));
        }
        this.cookieProcessor = cookieProcessor;
    }

    @Override // org.apache.catalina.Context
    public CookieProcessor getCookieProcessor() {
        return this.cookieProcessor;
    }

    @Override // org.apache.catalina.Context
    public Object getNamingToken() {
        return this.namingToken;
    }

    @Override // org.apache.catalina.Context
    public void setContainerSciFilter(String containerSciFilter) {
        this.containerSciFilter = containerSciFilter;
    }

    @Override // org.apache.catalina.Context
    public String getContainerSciFilter() {
        return this.containerSciFilter;
    }

    @Override // org.apache.catalina.Context
    public boolean getSendRedirectBody() {
        return this.sendRedirectBody;
    }

    @Override // org.apache.catalina.Context
    public void setSendRedirectBody(boolean sendRedirectBody) {
        this.sendRedirectBody = sendRedirectBody;
    }

    @Override // org.apache.catalina.Context
    public boolean getPreemptiveAuthentication() {
        return this.preemptiveAuthentication;
    }

    @Override // org.apache.catalina.Context
    public void setPreemptiveAuthentication(boolean preemptiveAuthentication) {
        this.preemptiveAuthentication = preemptiveAuthentication;
    }

    @Override // org.apache.catalina.Context
    public void setFireRequestListenersOnForwards(boolean enable) {
        this.fireRequestListenersOnForwards = enable;
    }

    @Override // org.apache.catalina.Context
    public boolean getFireRequestListenersOnForwards() {
        return this.fireRequestListenersOnForwards;
    }

    @Override // org.apache.catalina.Context
    public void setAddWebinfClassesResources(boolean addWebinfClassesResources) {
        this.addWebinfClassesResources = addWebinfClassesResources;
    }

    @Override // org.apache.catalina.Context
    public boolean getAddWebinfClassesResources() {
        return this.addWebinfClassesResources;
    }

    @Override // org.apache.catalina.Context
    public void setWebappVersion(String webappVersion) {
        if (null == webappVersion) {
            this.webappVersion = "";
        } else {
            this.webappVersion = webappVersion;
        }
    }

    @Override // org.apache.catalina.Context
    public String getWebappVersion() {
        return this.webappVersion;
    }

    @Override // org.apache.catalina.Context
    public String getBaseName() {
        return new ContextName(this.path, this.webappVersion).getBaseName();
    }

    @Override // org.apache.catalina.Context
    public String getResourceOnlyServlets() {
        return StringUtils.join(this.resourceOnlyServlets);
    }

    @Override // org.apache.catalina.Context
    public void setResourceOnlyServlets(String resourceOnlyServlets) {
        this.resourceOnlyServlets.clear();
        if (resourceOnlyServlets == null) {
            return;
        }
        for (String str : resourceOnlyServlets.split(",")) {
            String servletName = str.trim();
            if (servletName.length() > 0) {
                this.resourceOnlyServlets.add(servletName);
            }
        }
    }

    @Override // org.apache.catalina.Context
    public boolean isResourceOnlyServlet(String servletName) {
        return this.resourceOnlyServlets.contains(servletName);
    }

    @Override // org.apache.catalina.Context
    public int getEffectiveMajorVersion() {
        return this.effectiveMajorVersion;
    }

    @Override // org.apache.catalina.Context
    public void setEffectiveMajorVersion(int effectiveMajorVersion) {
        this.effectiveMajorVersion = effectiveMajorVersion;
    }

    @Override // org.apache.catalina.Context
    public int getEffectiveMinorVersion() {
        return this.effectiveMinorVersion;
    }

    @Override // org.apache.catalina.Context
    public void setEffectiveMinorVersion(int effectiveMinorVersion) {
        this.effectiveMinorVersion = effectiveMinorVersion;
    }

    @Override // org.apache.catalina.Context
    public void setLogEffectiveWebXml(boolean logEffectiveWebXml) {
        this.logEffectiveWebXml = logEffectiveWebXml;
    }

    @Override // org.apache.catalina.Context
    public boolean getLogEffectiveWebXml() {
        return this.logEffectiveWebXml;
    }

    @Override // org.apache.catalina.Context
    public Authenticator getAuthenticator() {
        Pipeline pipeline = getPipeline();
        if (pipeline != null) {
            Valve basic = pipeline.getBasic();
            if (basic instanceof Authenticator) {
                return (Authenticator) basic;
            }
            for (Valve valve : pipeline.getValves()) {
                if (valve instanceof Authenticator) {
                    return (Authenticator) valve;
                }
            }
            return null;
        }
        return null;
    }

    @Override // org.apache.catalina.Context
    public JarScanner getJarScanner() {
        if (this.jarScanner == null) {
            this.jarScanner = new StandardJarScanner();
        }
        return this.jarScanner;
    }

    @Override // org.apache.catalina.Context
    public void setJarScanner(JarScanner jarScanner) {
        this.jarScanner = jarScanner;
    }

    @Override // org.apache.catalina.Context
    public InstanceManager getInstanceManager() {
        return this.instanceManager;
    }

    @Override // org.apache.catalina.Context
    public void setInstanceManager(InstanceManager instanceManager) {
        this.instanceManager = instanceManager;
    }

    @Override // org.apache.catalina.Context
    public String getEncodedPath() {
        return this.encodedPath;
    }

    @Override // org.apache.catalina.Context
    public void setAllowCasualMultipartParsing(boolean allowCasualMultipartParsing) {
        this.allowCasualMultipartParsing = allowCasualMultipartParsing;
    }

    @Override // org.apache.catalina.Context
    public boolean getAllowCasualMultipartParsing() {
        return this.allowCasualMultipartParsing;
    }

    @Override // org.apache.catalina.Context
    public void setSwallowAbortedUploads(boolean swallowAbortedUploads) {
        this.swallowAbortedUploads = swallowAbortedUploads;
    }

    @Override // org.apache.catalina.Context
    public boolean getSwallowAbortedUploads() {
        return this.swallowAbortedUploads;
    }

    @Override // org.apache.catalina.Context
    public void addServletContainerInitializer(ServletContainerInitializer sci, Set<Class<?>> classes) {
        this.initializers.put(sci, classes);
    }

    public boolean getDelegate() {
        return this.delegate;
    }

    public void setDelegate(boolean delegate) {
        boolean oldDelegate = this.delegate;
        this.delegate = delegate;
        this.support.firePropertyChange("delegate", oldDelegate, this.delegate);
    }

    public boolean isUseNaming() {
        return this.useNaming;
    }

    public void setUseNaming(boolean useNaming) {
        this.useNaming = useNaming;
    }

    @Override // org.apache.catalina.Context
    public Object[] getApplicationEventListeners() {
        return this.applicationEventListenersList.toArray();
    }

    @Override // org.apache.catalina.Context
    public void setApplicationEventListeners(Object[] listeners) {
        this.applicationEventListenersList.clear();
        if (listeners != null && listeners.length > 0) {
            this.applicationEventListenersList.addAll(Arrays.asList(listeners));
        }
    }

    public void addApplicationEventListener(Object listener) {
        this.applicationEventListenersList.add(listener);
    }

    @Override // org.apache.catalina.Context
    public Object[] getApplicationLifecycleListeners() {
        return this.applicationLifecycleListenersObjects;
    }

    @Override // org.apache.catalina.Context
    public void setApplicationLifecycleListeners(Object[] listeners) {
        this.applicationLifecycleListenersObjects = listeners;
    }

    public void addApplicationLifecycleListener(Object listener) {
        int len = this.applicationLifecycleListenersObjects.length;
        Object[] newListeners = Arrays.copyOf(this.applicationLifecycleListenersObjects, len + 1);
        newListeners[len] = listener;
        this.applicationLifecycleListenersObjects = newListeners;
    }

    public boolean getAntiResourceLocking() {
        return this.antiResourceLocking;
    }

    public void setAntiResourceLocking(boolean antiResourceLocking) {
        boolean oldAntiResourceLocking = this.antiResourceLocking;
        this.antiResourceLocking = antiResourceLocking;
        this.support.firePropertyChange("antiResourceLocking", oldAntiResourceLocking, this.antiResourceLocking);
    }

    @Override // org.apache.catalina.Context
    @Deprecated
    public boolean getUseBloomFilterForArchives() {
        return this.useBloomFilterForArchives;
    }

    @Override // org.apache.catalina.Context
    @Deprecated
    public void setUseBloomFilterForArchives(boolean useBloomFilterForArchives) {
        boolean oldUseBloomFilterForArchives = this.useBloomFilterForArchives;
        this.useBloomFilterForArchives = useBloomFilterForArchives;
        this.support.firePropertyChange("useBloomFilterForArchives", oldUseBloomFilterForArchives, this.useBloomFilterForArchives);
    }

    @Override // org.apache.catalina.Context
    public void setParallelAnnotationScanning(boolean parallelAnnotationScanning) {
        boolean oldParallelAnnotationScanning = this.parallelAnnotationScanning;
        this.parallelAnnotationScanning = parallelAnnotationScanning;
        this.support.firePropertyChange("parallelAnnotationScanning", oldParallelAnnotationScanning, this.parallelAnnotationScanning);
    }

    @Override // org.apache.catalina.Context
    public boolean getParallelAnnotationScanning() {
        return this.parallelAnnotationScanning;
    }

    public CharsetMapper getCharsetMapper() {
        if (this.charsetMapper == null) {
            try {
                Class<?> clazz = Class.forName(this.charsetMapperClass);
                this.charsetMapper = (CharsetMapper) clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
            } catch (Throwable t) {
                ExceptionUtils.handleThrowable(t);
                this.charsetMapper = new CharsetMapper();
            }
        }
        return this.charsetMapper;
    }

    public void setCharsetMapper(CharsetMapper mapper) {
        CharsetMapper oldCharsetMapper = this.charsetMapper;
        this.charsetMapper = mapper;
        if (mapper != null) {
            this.charsetMapperClass = mapper.getClass().getName();
        }
        this.support.firePropertyChange("charsetMapper", oldCharsetMapper, this.charsetMapper);
    }

    @Override // org.apache.catalina.Context
    public String getCharset(Locale locale) {
        return getCharsetMapper().getCharset(locale);
    }

    @Override // org.apache.catalina.Context
    public URL getConfigFile() {
        return this.configFile;
    }

    @Override // org.apache.catalina.Context
    public void setConfigFile(URL configFile) {
        this.configFile = configFile;
    }

    @Override // org.apache.catalina.Context
    public boolean getConfigured() {
        return this.configured;
    }

    @Override // org.apache.catalina.Context
    public void setConfigured(boolean configured) {
        boolean oldConfigured = this.configured;
        this.configured = configured;
        this.support.firePropertyChange("configured", oldConfigured, this.configured);
    }

    @Override // org.apache.catalina.Context
    public boolean getCookies() {
        return this.cookies;
    }

    @Override // org.apache.catalina.Context
    public void setCookies(boolean cookies) {
        boolean oldCookies = this.cookies;
        this.cookies = cookies;
        this.support.firePropertyChange("cookies", oldCookies, this.cookies);
    }

    @Override // org.apache.catalina.Context
    public String getSessionCookieName() {
        return this.sessionCookieName;
    }

    @Override // org.apache.catalina.Context
    public void setSessionCookieName(String sessionCookieName) {
        String oldSessionCookieName = this.sessionCookieName;
        this.sessionCookieName = sessionCookieName;
        this.support.firePropertyChange("sessionCookieName", oldSessionCookieName, sessionCookieName);
    }

    @Override // org.apache.catalina.Context
    public boolean getUseHttpOnly() {
        return this.useHttpOnly;
    }

    @Override // org.apache.catalina.Context
    public void setUseHttpOnly(boolean useHttpOnly) {
        boolean oldUseHttpOnly = this.useHttpOnly;
        this.useHttpOnly = useHttpOnly;
        this.support.firePropertyChange("useHttpOnly", oldUseHttpOnly, this.useHttpOnly);
    }

    @Override // org.apache.catalina.Context
    public boolean getUsePartitioned() {
        return this.usePartitioned;
    }

    @Override // org.apache.catalina.Context
    public void setUsePartitioned(boolean usePartitioned) {
        boolean oldUsePartitioned = this.usePartitioned;
        this.usePartitioned = usePartitioned;
        this.support.firePropertyChange("usePartitioned", oldUsePartitioned, this.usePartitioned);
    }

    @Override // org.apache.catalina.Context
    public String getSessionCookieDomain() {
        return this.sessionCookieDomain;
    }

    @Override // org.apache.catalina.Context
    public void setSessionCookieDomain(String sessionCookieDomain) {
        String oldSessionCookieDomain = this.sessionCookieDomain;
        this.sessionCookieDomain = sessionCookieDomain;
        this.support.firePropertyChange("sessionCookieDomain", oldSessionCookieDomain, sessionCookieDomain);
    }

    @Override // org.apache.catalina.Context
    public String getSessionCookiePath() {
        return this.sessionCookiePath;
    }

    @Override // org.apache.catalina.Context
    public void setSessionCookiePath(String sessionCookiePath) {
        String oldSessionCookiePath = this.sessionCookiePath;
        this.sessionCookiePath = sessionCookiePath;
        this.support.firePropertyChange("sessionCookiePath", oldSessionCookiePath, sessionCookiePath);
    }

    @Override // org.apache.catalina.Context
    public boolean getSessionCookiePathUsesTrailingSlash() {
        return this.sessionCookiePathUsesTrailingSlash;
    }

    @Override // org.apache.catalina.Context
    public void setSessionCookiePathUsesTrailingSlash(boolean sessionCookiePathUsesTrailingSlash) {
        this.sessionCookiePathUsesTrailingSlash = sessionCookiePathUsesTrailingSlash;
    }

    @Override // org.apache.catalina.Context
    public boolean getCrossContext() {
        return this.crossContext;
    }

    @Override // org.apache.catalina.Context
    public void setCrossContext(boolean crossContext) {
        boolean oldCrossContext = this.crossContext;
        this.crossContext = crossContext;
        this.support.firePropertyChange("crossContext", oldCrossContext, this.crossContext);
    }

    public String getDefaultContextXml() {
        return this.defaultContextXml;
    }

    public void setDefaultContextXml(String defaultContextXml) {
        this.defaultContextXml = defaultContextXml;
    }

    public String getDefaultWebXml() {
        return this.defaultWebXml;
    }

    public void setDefaultWebXml(String defaultWebXml) {
        this.defaultWebXml = defaultWebXml;
    }

    public long getStartupTime() {
        return this.startupTime;
    }

    public void setStartupTime(long startupTime) {
        this.startupTime = startupTime;
    }

    public long getTldScanTime() {
        return this.tldScanTime;
    }

    public void setTldScanTime(long tldScanTime) {
        this.tldScanTime = tldScanTime;
    }

    @Override // org.apache.catalina.Context
    public boolean getDenyUncoveredHttpMethods() {
        return this.denyUncoveredHttpMethods;
    }

    @Override // org.apache.catalina.Context
    public void setDenyUncoveredHttpMethods(boolean denyUncoveredHttpMethods) {
        this.denyUncoveredHttpMethods = denyUncoveredHttpMethods;
    }

    @Override // org.apache.catalina.Context
    public String getDisplayName() {
        return this.displayName;
    }

    @Override // org.apache.catalina.Context
    public String getAltDDName() {
        return this.altDDName;
    }

    @Override // org.apache.catalina.Context
    public void setAltDDName(String altDDName) {
        this.altDDName = altDDName;
        if (this.context != null) {
            this.context.setAttribute(Globals.ALT_DD_ATTR, altDDName);
        }
    }

    @Override // org.apache.catalina.Context
    public void setDisplayName(String displayName) {
        String oldDisplayName = this.displayName;
        this.displayName = displayName;
        this.support.firePropertyChange("displayName", oldDisplayName, this.displayName);
    }

    @Override // org.apache.catalina.Context
    public boolean getDistributable() {
        return this.distributable;
    }

    @Override // org.apache.catalina.Context
    public void setDistributable(boolean distributable) {
        boolean oldDistributable = this.distributable;
        this.distributable = distributable;
        this.support.firePropertyChange("distributable", oldDistributable, this.distributable);
    }

    @Override // org.apache.catalina.Context
    public String getDocBase() {
        return this.docBase;
    }

    @Override // org.apache.catalina.Context
    public void setDocBase(String docBase) {
        this.docBase = docBase;
    }

    public String getJ2EEApplication() {
        return this.j2EEApplication;
    }

    public void setJ2EEApplication(String j2EEApplication) {
        this.j2EEApplication = j2EEApplication;
    }

    public String getJ2EEServer() {
        return this.j2EEServer;
    }

    public void setJ2EEServer(String j2EEServer) {
        this.j2EEServer = j2EEServer;
    }

    @Override // org.apache.catalina.Context
    public Loader getLoader() {
        Lock readLock = this.loaderLock.readLock();
        readLock.lock();
        try {
            return this.loader;
        } finally {
            readLock.unlock();
        }
    }

    @Override // org.apache.catalina.Context
    public void setLoader(Loader loader) {
        Lock writeLock = this.loaderLock.writeLock();
        writeLock.lock();
        try {
            Loader oldLoader = this.loader;
            if (oldLoader == loader) {
                return;
            }
            this.loader = loader;
            if (getState().isAvailable() && oldLoader != null && (oldLoader instanceof Lifecycle)) {
                try {
                    ((Lifecycle) oldLoader).stop();
                } catch (LifecycleException e) {
                    log.error(sm.getString("standardContext.setLoader.stop"), e);
                }
            }
            if (loader != null) {
                loader.setContext(this);
            }
            if (getState().isAvailable() && loader != null && (loader instanceof Lifecycle)) {
                try {
                    ((Lifecycle) loader).start();
                } catch (LifecycleException e2) {
                    log.error(sm.getString("standardContext.setLoader.start"), e2);
                }
            }
            writeLock.unlock();
            this.support.firePropertyChange("loader", oldLoader, loader);
        } finally {
            writeLock.unlock();
        }
    }

    @Override // org.apache.catalina.Context
    public Manager getManager() {
        Lock readLock = this.managerLock.readLock();
        readLock.lock();
        try {
            return this.manager;
        } finally {
            readLock.unlock();
        }
    }

    @Override // org.apache.catalina.Context
    public void setManager(Manager manager) {
        Lock writeLock = this.managerLock.writeLock();
        writeLock.lock();
        try {
            Manager oldManager = this.manager;
            if (oldManager == manager) {
                return;
            }
            this.manager = manager;
            if (oldManager instanceof Lifecycle) {
                try {
                    ((Lifecycle) oldManager).stop();
                    ((Lifecycle) oldManager).destroy();
                } catch (LifecycleException e) {
                    log.error(sm.getString("standardContext.setManager.stop"), e);
                }
            }
            if (manager != null) {
                manager.setContext(this);
            }
            if (getState().isAvailable() && (manager instanceof Lifecycle)) {
                try {
                    ((Lifecycle) manager).start();
                } catch (LifecycleException e2) {
                    log.error(sm.getString("standardContext.setManager.start"), e2);
                }
            }
            writeLock.unlock();
            this.support.firePropertyChange("manager", oldManager, manager);
        } finally {
            writeLock.unlock();
        }
    }

    @Override // org.apache.catalina.Context
    public boolean getIgnoreAnnotations() {
        return this.ignoreAnnotations;
    }

    @Override // org.apache.catalina.Context
    public void setIgnoreAnnotations(boolean ignoreAnnotations) {
        boolean oldIgnoreAnnotations = this.ignoreAnnotations;
        this.ignoreAnnotations = ignoreAnnotations;
        this.support.firePropertyChange("ignoreAnnotations", oldIgnoreAnnotations, this.ignoreAnnotations);
    }

    @Override // org.apache.catalina.Context
    public LoginConfig getLoginConfig() {
        return this.loginConfig;
    }

    @Override // org.apache.catalina.Context
    public void setLoginConfig(LoginConfig config) {
        if (config == null) {
            throw new IllegalArgumentException(sm.getString("standardContext.loginConfig.required"));
        }
        String loginPage = config.getLoginPage();
        if (loginPage != null && !loginPage.startsWith("/")) {
            if (isServlet22()) {
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("standardContext.loginConfig.loginWarning", loginPage));
                }
                config.setLoginPage("/" + loginPage);
            } else {
                throw new IllegalArgumentException(sm.getString("standardContext.loginConfig.loginPage", loginPage));
            }
        }
        String errorPage = config.getErrorPage();
        if (errorPage != null && !errorPage.startsWith("/")) {
            if (isServlet22()) {
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("standardContext.loginConfig.errorWarning", errorPage));
                }
                config.setErrorPage("/" + errorPage);
            } else {
                throw new IllegalArgumentException(sm.getString("standardContext.loginConfig.errorPage", errorPage));
            }
        }
        LoginConfig oldLoginConfig = this.loginConfig;
        this.loginConfig = config;
        this.support.firePropertyChange("loginConfig", oldLoginConfig, this.loginConfig);
    }

    @Override // org.apache.catalina.Context
    public NamingResourcesImpl getNamingResources() {
        if (this.namingResources == null) {
            setNamingResources(new NamingResourcesImpl());
        }
        return this.namingResources;
    }

    @Override // org.apache.catalina.Context
    public void setNamingResources(NamingResourcesImpl namingResources) {
        NamingResourcesImpl oldNamingResources = this.namingResources;
        this.namingResources = namingResources;
        if (namingResources != null) {
            namingResources.setContainer(this);
        }
        this.support.firePropertyChange("namingResources", oldNamingResources, this.namingResources);
        if (getState() == LifecycleState.NEW || getState() == LifecycleState.INITIALIZING || getState() == LifecycleState.INITIALIZED) {
            return;
        }
        if (oldNamingResources != null) {
            try {
                oldNamingResources.stop();
                oldNamingResources.destroy();
            } catch (LifecycleException e) {
                log.error(sm.getString("standardContext.namingResource.destroy.fail"), e);
            }
        }
        if (namingResources != null) {
            try {
                namingResources.init();
                namingResources.start();
            } catch (LifecycleException e2) {
                log.error(sm.getString("standardContext.namingResource.init.fail"), e2);
            }
        }
    }

    @Override // org.apache.catalina.Context
    public String getPath() {
        return this.path;
    }

    @Override // org.apache.catalina.Context
    public void setPath(String path) {
        boolean invalid = false;
        if (path == null || path.equals("/")) {
            invalid = true;
            this.path = "";
        } else if (path.isEmpty() || path.startsWith("/")) {
            this.path = path;
        } else {
            invalid = true;
            this.path = "/" + path;
        }
        if (this.path.endsWith("/")) {
            invalid = true;
            this.path = this.path.substring(0, this.path.length() - 1);
        }
        if (invalid) {
            log.warn(sm.getString("standardContext.pathInvalid", path, this.path));
        }
        this.encodedPath = URLEncoder.DEFAULT.encode(this.path, StandardCharsets.UTF_8);
        if (getName() == null) {
            setName(this.path);
        }
    }

    @Override // org.apache.catalina.Context
    public String getPublicId() {
        return this.publicId;
    }

    @Override // org.apache.catalina.Context
    public void setPublicId(String publicId) {
        if (log.isDebugEnabled()) {
            log.debug("Setting deployment descriptor public ID to '" + publicId + "'");
        }
        String oldPublicId = this.publicId;
        this.publicId = publicId;
        this.support.firePropertyChange("publicId", oldPublicId, publicId);
    }

    @Override // org.apache.catalina.Context
    public boolean getReloadable() {
        return this.reloadable;
    }

    @Override // org.apache.catalina.Context
    public boolean getOverride() {
        return this.override;
    }

    public String getOriginalDocBase() {
        return this.originalDocBase;
    }

    public void setOriginalDocBase(String docBase) {
        this.originalDocBase = docBase;
    }

    @Override // org.apache.catalina.core.ContainerBase, org.apache.catalina.Container
    public ClassLoader getParentClassLoader() {
        if (this.parentClassLoader != null) {
            return this.parentClassLoader;
        }
        if (getPrivileged()) {
            return getClass().getClassLoader();
        }
        if (this.parent != null) {
            return this.parent.getParentClassLoader();
        }
        return ClassLoader.getSystemClassLoader();
    }

    @Override // org.apache.catalina.Context
    public boolean getPrivileged() {
        return this.privileged;
    }

    @Override // org.apache.catalina.Context
    public void setPrivileged(boolean privileged) {
        boolean oldPrivileged = this.privileged;
        this.privileged = privileged;
        this.support.firePropertyChange("privileged", oldPrivileged, this.privileged);
    }

    @Override // org.apache.catalina.Context
    public void setReloadable(boolean reloadable) {
        boolean oldReloadable = this.reloadable;
        this.reloadable = reloadable;
        this.support.firePropertyChange("reloadable", oldReloadable, this.reloadable);
    }

    @Override // org.apache.catalina.Context
    public void setOverride(boolean override) {
        boolean oldOverride = this.override;
        this.override = override;
        this.support.firePropertyChange("override", oldOverride, this.override);
    }

    public void setReplaceWelcomeFiles(boolean replaceWelcomeFiles) {
        boolean oldReplaceWelcomeFiles = this.replaceWelcomeFiles;
        this.replaceWelcomeFiles = replaceWelcomeFiles;
        this.support.firePropertyChange("replaceWelcomeFiles", oldReplaceWelcomeFiles, this.replaceWelcomeFiles);
    }

    @Override // org.apache.catalina.Context
    public ServletContext getServletContext() {
        if (this.context == null) {
            this.context = new ApplicationContext(this);
            if (this.altDDName != null) {
                this.context.setAttribute(Globals.ALT_DD_ATTR, this.altDDName);
            }
        }
        return this.context.getFacade();
    }

    @Override // org.apache.catalina.Context
    public int getSessionTimeout() {
        return this.sessionTimeout;
    }

    @Override // org.apache.catalina.Context
    public void setSessionTimeout(int timeout) {
        int oldSessionTimeout = this.sessionTimeout;
        this.sessionTimeout = timeout == 0 ? -1 : timeout;
        this.support.firePropertyChange("sessionTimeout", oldSessionTimeout, this.sessionTimeout);
    }

    @Override // org.apache.catalina.Context
    public boolean getSwallowOutput() {
        return this.swallowOutput;
    }

    @Override // org.apache.catalina.Context
    public void setSwallowOutput(boolean swallowOutput) {
        boolean oldSwallowOutput = this.swallowOutput;
        this.swallowOutput = swallowOutput;
        this.support.firePropertyChange("swallowOutput", oldSwallowOutput, this.swallowOutput);
    }

    public long getUnloadDelay() {
        return this.unloadDelay;
    }

    public void setUnloadDelay(long unloadDelay) {
        long oldUnloadDelay = this.unloadDelay;
        this.unloadDelay = unloadDelay;
        this.support.firePropertyChange("unloadDelay", Long.valueOf(oldUnloadDelay), Long.valueOf(this.unloadDelay));
    }

    public boolean getUnpackWAR() {
        return this.unpackWAR;
    }

    public void setUnpackWAR(boolean unpackWAR) {
        this.unpackWAR = unpackWAR;
    }

    public boolean getCopyXML() {
        return this.copyXML;
    }

    public void setCopyXML(boolean copyXML) {
        this.copyXML = copyXML;
    }

    @Override // org.apache.catalina.Context
    public String getWrapperClass() {
        return this.wrapperClassName;
    }

    @Override // org.apache.catalina.Context
    public void setWrapperClass(String wrapperClassName) {
        this.wrapperClassName = wrapperClassName;
        try {
            this.wrapperClass = Class.forName(wrapperClassName);
            if (!StandardWrapper.class.isAssignableFrom(this.wrapperClass)) {
                throw new IllegalArgumentException(sm.getString("standardContext.invalidWrapperClass", wrapperClassName));
            }
        } catch (ClassNotFoundException cnfe) {
            throw new IllegalArgumentException(cnfe.getMessage());
        }
    }

    @Override // org.apache.catalina.Context
    public WebResourceRoot getResources() {
        Lock readLock = this.resourcesLock.readLock();
        readLock.lock();
        try {
            return this.resources;
        } finally {
            readLock.unlock();
        }
    }

    @Override // org.apache.catalina.Context
    public void setResources(WebResourceRoot resources) {
        Lock writeLock = this.resourcesLock.writeLock();
        writeLock.lock();
        try {
            if (getState().isAvailable()) {
                throw new IllegalStateException(sm.getString("standardContext.resourcesStart"));
            }
            WebResourceRoot oldResources = this.resources;
            if (oldResources == resources) {
                return;
            }
            this.resources = resources;
            if (oldResources != null) {
                oldResources.setContext(null);
            }
            if (resources != null) {
                resources.setContext(this);
            }
            this.support.firePropertyChange("resources", oldResources, resources);
            writeLock.unlock();
        } finally {
            writeLock.unlock();
        }
    }

    @Override // org.apache.catalina.Context
    public JspConfigDescriptor getJspConfigDescriptor() {
        return this.jspConfigDescriptor;
    }

    @Override // org.apache.catalina.Context
    public void setJspConfigDescriptor(JspConfigDescriptor descriptor) {
        this.jspConfigDescriptor = descriptor;
    }

    @Override // org.apache.catalina.Context
    public ThreadBindingListener getThreadBindingListener() {
        return this.threadBindingListener;
    }

    @Override // org.apache.catalina.Context
    public void setThreadBindingListener(ThreadBindingListener threadBindingListener) {
        this.threadBindingListener = threadBindingListener;
    }

    public boolean getJndiExceptionOnFailedWrite() {
        return this.jndiExceptionOnFailedWrite;
    }

    public void setJndiExceptionOnFailedWrite(boolean jndiExceptionOnFailedWrite) {
        this.jndiExceptionOnFailedWrite = jndiExceptionOnFailedWrite;
    }

    public String getCharsetMapperClass() {
        return this.charsetMapperClass;
    }

    public void setCharsetMapperClass(String mapper) {
        String oldCharsetMapperClass = this.charsetMapperClass;
        this.charsetMapperClass = mapper;
        this.support.firePropertyChange("charsetMapperClass", oldCharsetMapperClass, this.charsetMapperClass);
    }

    public String getWorkPath() {
        if (getWorkDir() == null) {
            return null;
        }
        File workDir = new File(getWorkDir());
        if (!workDir.isAbsolute()) {
            try {
                workDir = new File(getCatalinaBase().getCanonicalFile(), getWorkDir());
            } catch (IOException e) {
                log.warn(sm.getString("standardContext.workPath", getName()), e);
            }
        }
        return workDir.getAbsolutePath();
    }

    public String getWorkDir() {
        return this.workDir;
    }

    public void setWorkDir(String workDir) {
        this.workDir = workDir;
        if (getState().isAvailable()) {
            postWorkDirectory();
        }
    }

    public boolean getClearReferencesRmiTargets() {
        return this.clearReferencesRmiTargets;
    }

    public void setClearReferencesRmiTargets(boolean clearReferencesRmiTargets) {
        boolean oldClearReferencesRmiTargets = this.clearReferencesRmiTargets;
        this.clearReferencesRmiTargets = clearReferencesRmiTargets;
        this.support.firePropertyChange("clearReferencesRmiTargets", oldClearReferencesRmiTargets, this.clearReferencesRmiTargets);
    }

    public boolean getClearReferencesStopThreads() {
        return this.clearReferencesStopThreads;
    }

    public void setClearReferencesStopThreads(boolean clearReferencesStopThreads) {
        boolean oldClearReferencesStopThreads = this.clearReferencesStopThreads;
        this.clearReferencesStopThreads = clearReferencesStopThreads;
        this.support.firePropertyChange("clearReferencesStopThreads", oldClearReferencesStopThreads, this.clearReferencesStopThreads);
    }

    public boolean getClearReferencesStopTimerThreads() {
        return this.clearReferencesStopTimerThreads;
    }

    public void setClearReferencesStopTimerThreads(boolean clearReferencesStopTimerThreads) {
        boolean oldClearReferencesStopTimerThreads = this.clearReferencesStopTimerThreads;
        this.clearReferencesStopTimerThreads = clearReferencesStopTimerThreads;
        this.support.firePropertyChange("clearReferencesStopTimerThreads", oldClearReferencesStopTimerThreads, this.clearReferencesStopTimerThreads);
    }

    public boolean getClearReferencesHttpClientKeepAliveThread() {
        return this.clearReferencesHttpClientKeepAliveThread;
    }

    public void setClearReferencesHttpClientKeepAliveThread(boolean clearReferencesHttpClientKeepAliveThread) {
        this.clearReferencesHttpClientKeepAliveThread = clearReferencesHttpClientKeepAliveThread;
    }

    public boolean getRenewThreadsWhenStoppingContext() {
        return this.renewThreadsWhenStoppingContext;
    }

    public void setRenewThreadsWhenStoppingContext(boolean renewThreadsWhenStoppingContext) {
        boolean oldRenewThreadsWhenStoppingContext = this.renewThreadsWhenStoppingContext;
        this.renewThreadsWhenStoppingContext = renewThreadsWhenStoppingContext;
        this.support.firePropertyChange("renewThreadsWhenStoppingContext", oldRenewThreadsWhenStoppingContext, this.renewThreadsWhenStoppingContext);
    }

    public boolean getClearReferencesObjectStreamClassCaches() {
        return this.clearReferencesObjectStreamClassCaches;
    }

    public void setClearReferencesObjectStreamClassCaches(boolean clearReferencesObjectStreamClassCaches) {
        boolean oldClearReferencesObjectStreamClassCaches = this.clearReferencesObjectStreamClassCaches;
        this.clearReferencesObjectStreamClassCaches = clearReferencesObjectStreamClassCaches;
        this.support.firePropertyChange("clearReferencesObjectStreamClassCaches", oldClearReferencesObjectStreamClassCaches, this.clearReferencesObjectStreamClassCaches);
    }

    public boolean getClearReferencesThreadLocals() {
        return this.clearReferencesThreadLocals;
    }

    public void setClearReferencesThreadLocals(boolean clearReferencesThreadLocals) {
        boolean oldClearReferencesThreadLocals = this.clearReferencesThreadLocals;
        this.clearReferencesThreadLocals = clearReferencesThreadLocals;
        this.support.firePropertyChange("clearReferencesThreadLocals", oldClearReferencesThreadLocals, this.clearReferencesThreadLocals);
    }

    public boolean getSkipMemoryLeakChecksOnJvmShutdown() {
        return this.skipMemoryLeakChecksOnJvmShutdown;
    }

    public void setSkipMemoryLeakChecksOnJvmShutdown(boolean skipMemoryLeakChecksOnJvmShutdown) {
        this.skipMemoryLeakChecksOnJvmShutdown = skipMemoryLeakChecksOnJvmShutdown;
    }

    public Boolean getFailCtxIfServletStartFails() {
        return this.failCtxIfServletStartFails;
    }

    public void setFailCtxIfServletStartFails(Boolean failCtxIfServletStartFails) {
        Boolean oldFailCtxIfServletStartFails = this.failCtxIfServletStartFails;
        this.failCtxIfServletStartFails = failCtxIfServletStartFails;
        this.support.firePropertyChange("failCtxIfServletStartFails", oldFailCtxIfServletStartFails, failCtxIfServletStartFails);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean getComputedFailCtxIfServletStartFails() {
        if (this.failCtxIfServletStartFails != null) {
            return this.failCtxIfServletStartFails.booleanValue();
        }
        if (getParent() instanceof StandardHost) {
            return ((StandardHost) getParent()).isFailCtxIfServletStartFails();
        }
        return false;
    }

    @Override // org.apache.catalina.Context
    public void addApplicationListener(String listener) {
        if (this.applicationListeners.addIfAbsent(listener)) {
            fireContainerEvent("addApplicationListener", listener);
        } else {
            log.info(sm.getString("standardContext.duplicateListener", listener));
        }
    }

    @Override // org.apache.catalina.Context
    public void addApplicationParameter(ApplicationParameter parameter) {
        synchronized (this.applicationParametersLock) {
            String newName = parameter.getName();
            for (ApplicationParameter p : this.applicationParameters) {
                if (newName.equals(p.getName()) && !p.getOverride()) {
                    return;
                }
            }
            ApplicationParameter[] results = (ApplicationParameter[]) Arrays.copyOf(this.applicationParameters, this.applicationParameters.length + 1);
            results[this.applicationParameters.length] = parameter;
            this.applicationParameters = results;
            fireContainerEvent("addApplicationParameter", parameter);
        }
    }

    @Override // org.apache.catalina.core.ContainerBase, org.apache.catalina.Container
    public void addChild(Container child) {
        Wrapper oldJspServlet = null;
        if (!(child instanceof Wrapper)) {
            throw new IllegalArgumentException(sm.getString("standardContext.notWrapper"));
        }
        boolean isJspServlet = "jsp".equals(child.getName());
        if (isJspServlet) {
            oldJspServlet = (Wrapper) findChild("jsp");
            if (oldJspServlet != null) {
                removeChild(oldJspServlet);
            }
        }
        super.addChild(child);
        if (isJspServlet && oldJspServlet != null) {
            String[] jspMappings = oldJspServlet.findMappings();
            for (int i = 0; jspMappings != null && i < jspMappings.length; i++) {
                addServletMappingDecoded(jspMappings[i], child.getName());
            }
        }
    }

    @Override // org.apache.catalina.Context
    public void addConstraint(SecurityConstraint constraint) {
        SecurityCollection[] collections = constraint.findCollections();
        for (SecurityCollection collection : collections) {
            String[] patterns = collection.findPatterns();
            for (int j = 0; j < patterns.length; j++) {
                patterns[j] = adjustURLPattern(patterns[j]);
                if (!validateURLPattern(patterns[j])) {
                    throw new IllegalArgumentException(sm.getString("standardContext.securityConstraint.pattern", patterns[j]));
                }
            }
            if (collection.findMethods().length > 0 && collection.findOmittedMethods().length > 0) {
                throw new IllegalArgumentException(sm.getString("standardContext.securityConstraint.mixHttpMethod"));
            }
        }
        synchronized (this.constraintsLock) {
            SecurityConstraint[] results = (SecurityConstraint[]) Arrays.copyOf(this.constraints, this.constraints.length + 1);
            results[this.constraints.length] = constraint;
            this.constraints = results;
        }
    }

    @Override // org.apache.catalina.Context
    public void addErrorPage(ErrorPage errorPage) {
        if (errorPage == null) {
            throw new IllegalArgumentException(sm.getString("standardContext.errorPage.required"));
        }
        String location = errorPage.getLocation();
        if (location != null && !location.startsWith("/")) {
            if (isServlet22()) {
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("standardContext.errorPage.warning", location));
                }
                errorPage.setLocation("/" + location);
            } else {
                throw new IllegalArgumentException(sm.getString("standardContext.errorPage.error", location));
            }
        }
        this.errorPageSupport.add(errorPage);
        fireContainerEvent("addErrorPage", errorPage);
    }

    @Override // org.apache.catalina.Context
    public void addFilterDef(FilterDef filterDef) {
        synchronized (this.filterDefs) {
            this.filterDefs.put(filterDef.getFilterName(), filterDef);
        }
        fireContainerEvent("addFilterDef", filterDef);
    }

    @Override // org.apache.catalina.Context
    public void addFilterMap(FilterMap filterMap) {
        validateFilterMap(filterMap);
        this.filterMaps.add(filterMap);
        fireContainerEvent("addFilterMap", filterMap);
    }

    @Override // org.apache.catalina.Context
    public void addFilterMapBefore(FilterMap filterMap) {
        validateFilterMap(filterMap);
        this.filterMaps.addBefore(filterMap);
        fireContainerEvent("addFilterMap", filterMap);
    }

    private void validateFilterMap(FilterMap filterMap) {
        String filterName = filterMap.getFilterName();
        String[] servletNames = filterMap.getServletNames();
        String[] urlPatterns = filterMap.getURLPatterns();
        if (findFilterDef(filterName) == null) {
            throw new IllegalArgumentException(sm.getString("standardContext.filterMap.name", filterName));
        }
        if (!filterMap.getMatchAllServletNames() && !filterMap.getMatchAllUrlPatterns() && servletNames.length == 0 && urlPatterns.length == 0) {
            throw new IllegalArgumentException(sm.getString("standardContext.filterMap.either"));
        }
        for (String urlPattern : urlPatterns) {
            if (!validateURLPattern(urlPattern)) {
                throw new IllegalArgumentException(sm.getString("standardContext.filterMap.pattern", urlPattern));
            }
        }
    }

    @Override // org.apache.catalina.Context
    public void addLocaleEncodingMappingParameter(String locale, String encoding) {
        getCharsetMapper().addCharsetMappingFromDeploymentDescriptor(locale, encoding);
    }

    public void addMessageDestination(MessageDestination md) {
        synchronized (this.messageDestinations) {
            this.messageDestinations.put(md.getName(), md);
        }
        fireContainerEvent("addMessageDestination", md.getName());
    }

    @Override // org.apache.catalina.Context
    public void addMimeMapping(String extension, String mimeType) {
        synchronized (this.mimeMappings) {
            this.mimeMappings.put(extension.toLowerCase(Locale.ENGLISH), mimeType);
        }
        fireContainerEvent("addMimeMapping", extension);
    }

    @Override // org.apache.catalina.Context
    public void addParameter(String name, String value) {
        if (name == null || value == null) {
            throw new IllegalArgumentException(sm.getString("standardContext.parameter.required"));
        }
        String oldValue = this.parameters.putIfAbsent(name, value);
        if (oldValue != null) {
            throw new IllegalArgumentException(sm.getString("standardContext.parameter.duplicate", name));
        }
        fireContainerEvent("addParameter", name);
    }

    @Override // org.apache.catalina.Context
    public void addRoleMapping(String role, String link) {
        synchronized (this.roleMappings) {
            this.roleMappings.put(role, link);
        }
        fireContainerEvent("addRoleMapping", role);
    }

    @Override // org.apache.catalina.Context
    public void addSecurityRole(String role) {
        synchronized (this.securityRolesLock) {
            String[] results = (String[]) Arrays.copyOf(this.securityRoles, this.securityRoles.length + 1);
            results[this.securityRoles.length] = role;
            this.securityRoles = results;
        }
        fireContainerEvent("addSecurityRole", role);
    }

    @Override // org.apache.catalina.Context
    public void addServletMappingDecoded(String pattern, String name, boolean jspWildCard) {
        if (findChild(name) == null) {
            throw new IllegalArgumentException(sm.getString("standardContext.servletMap.name", name));
        }
        String adjustedPattern = adjustURLPattern(pattern);
        if (!validateURLPattern(adjustedPattern)) {
            throw new IllegalArgumentException(sm.getString("standardContext.servletMap.pattern", adjustedPattern));
        }
        synchronized (this.servletMappingsLock) {
            String name2 = this.servletMappings.get(adjustedPattern);
            if (name2 != null) {
                Wrapper wrapper = (Wrapper) findChild(name2);
                wrapper.removeMapping(adjustedPattern);
            }
            this.servletMappings.put(adjustedPattern, name);
        }
        Wrapper wrapper2 = (Wrapper) findChild(name);
        wrapper2.addMapping(adjustedPattern);
        fireContainerEvent("addServletMapping", adjustedPattern);
    }

    @Override // org.apache.catalina.Context
    public void addWatchedResource(String name) {
        synchronized (this.watchedResourcesLock) {
            String[] results = (String[]) Arrays.copyOf(this.watchedResources, this.watchedResources.length + 1);
            results[this.watchedResources.length] = name;
            this.watchedResources = results;
        }
        fireContainerEvent("addWatchedResource", name);
    }

    @Override // org.apache.catalina.Context
    public void addWelcomeFile(String name) {
        synchronized (this.welcomeFilesLock) {
            if (this.replaceWelcomeFiles) {
                fireContainerEvent(Context.CLEAR_WELCOME_FILES_EVENT, null);
                this.welcomeFiles = new String[0];
                setReplaceWelcomeFiles(false);
            }
            String[] results = (String[]) Arrays.copyOf(this.welcomeFiles, this.welcomeFiles.length + 1);
            results[this.welcomeFiles.length] = name;
            this.welcomeFiles = results;
        }
        if (getState().equals(LifecycleState.STARTED)) {
            fireContainerEvent(Context.ADD_WELCOME_FILE_EVENT, name);
        }
    }

    @Override // org.apache.catalina.Context
    public void addWrapperLifecycle(String listener) {
        synchronized (this.wrapperLifecyclesLock) {
            String[] results = (String[]) Arrays.copyOf(this.wrapperLifecycles, this.wrapperLifecycles.length + 1);
            results[this.wrapperLifecycles.length] = listener;
            this.wrapperLifecycles = results;
        }
        fireContainerEvent("addWrapperLifecycle", listener);
    }

    @Override // org.apache.catalina.Context
    public void addWrapperListener(String listener) {
        synchronized (this.wrapperListenersLock) {
            String[] results = (String[]) Arrays.copyOf(this.wrapperListeners, this.wrapperListeners.length + 1);
            results[this.wrapperListeners.length] = listener;
            this.wrapperListeners = results;
        }
        fireContainerEvent("addWrapperListener", listener);
    }

    @Override // org.apache.catalina.Context
    public Wrapper createWrapper() {
        Wrapper wrapper;
        if (this.wrapperClass != null) {
            try {
                wrapper = (Wrapper) this.wrapperClass.getConstructor(new Class[0]).newInstance(new Object[0]);
            } catch (Throwable t) {
                ExceptionUtils.handleThrowable(t);
                log.error(sm.getString("standardContext.createWrapper.error"), t);
                return null;
            }
        } else {
            wrapper = new StandardWrapper();
        }
        synchronized (this.wrapperLifecyclesLock) {
            for (String wrapperLifecycle : this.wrapperLifecycles) {
                try {
                    Class<?> clazz = Class.forName(wrapperLifecycle);
                    LifecycleListener listener = (LifecycleListener) clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
                    wrapper.addLifecycleListener(listener);
                } catch (Throwable t2) {
                    ExceptionUtils.handleThrowable(t2);
                    log.error(sm.getString("standardContext.createWrapper.listenerError"), t2);
                    return null;
                }
            }
        }
        synchronized (this.wrapperListenersLock) {
            for (String wrapperListener : this.wrapperListeners) {
                try {
                    Class<?> clazz2 = Class.forName(wrapperListener);
                    ContainerListener listener2 = (ContainerListener) clazz2.getConstructor(new Class[0]).newInstance(new Object[0]);
                    wrapper.addContainerListener(listener2);
                } catch (Throwable t3) {
                    ExceptionUtils.handleThrowable(t3);
                    log.error(sm.getString("standardContext.createWrapper.containerListenerError"), t3);
                    return null;
                }
            }
        }
        return wrapper;
    }

    @Override // org.apache.catalina.Context
    public String[] findApplicationListeners() {
        return (String[]) this.applicationListeners.toArray(new String[0]);
    }

    @Override // org.apache.catalina.Context
    public ApplicationParameter[] findApplicationParameters() {
        ApplicationParameter[] applicationParameterArr;
        synchronized (this.applicationParametersLock) {
            applicationParameterArr = this.applicationParameters;
        }
        return applicationParameterArr;
    }

    @Override // org.apache.catalina.Context
    public SecurityConstraint[] findConstraints() {
        return this.constraints;
    }

    @Override // org.apache.catalina.Context
    public ErrorPage findErrorPage(int errorCode) {
        return this.errorPageSupport.find(errorCode);
    }

    @Override // org.apache.catalina.Context
    public ErrorPage findErrorPage(Throwable exceptionType) {
        return this.errorPageSupport.find(exceptionType);
    }

    @Override // org.apache.catalina.Context
    public ErrorPage[] findErrorPages() {
        return this.errorPageSupport.findAll();
    }

    @Override // org.apache.catalina.Context
    public FilterDef findFilterDef(String filterName) {
        FilterDef filterDef;
        synchronized (this.filterDefs) {
            filterDef = this.filterDefs.get(filterName);
        }
        return filterDef;
    }

    @Override // org.apache.catalina.Context
    public FilterDef[] findFilterDefs() {
        FilterDef[] filterDefArr;
        synchronized (this.filterDefs) {
            filterDefArr = (FilterDef[]) this.filterDefs.values().toArray(new FilterDef[0]);
        }
        return filterDefArr;
    }

    @Override // org.apache.catalina.Context
    public FilterMap[] findFilterMaps() {
        return this.filterMaps.asArray();
    }

    public MessageDestination findMessageDestination(String name) {
        MessageDestination messageDestination;
        synchronized (this.messageDestinations) {
            messageDestination = this.messageDestinations.get(name);
        }
        return messageDestination;
    }

    public MessageDestination[] findMessageDestinations() {
        MessageDestination[] messageDestinationArr;
        synchronized (this.messageDestinations) {
            messageDestinationArr = (MessageDestination[]) this.messageDestinations.values().toArray(new MessageDestination[0]);
        }
        return messageDestinationArr;
    }

    @Override // org.apache.catalina.Context
    public String findMimeMapping(String extension) {
        return this.mimeMappings.get(extension.toLowerCase(Locale.ENGLISH));
    }

    @Override // org.apache.catalina.Context
    public String[] findMimeMappings() {
        String[] strArr;
        synchronized (this.mimeMappings) {
            strArr = (String[]) this.mimeMappings.keySet().toArray(new String[0]);
        }
        return strArr;
    }

    @Override // org.apache.catalina.Context
    public String findParameter(String name) {
        return this.parameters.get(name);
    }

    @Override // org.apache.catalina.Context
    public String[] findParameters() {
        return (String[]) this.parameters.keySet().toArray(new String[0]);
    }

    @Override // org.apache.catalina.Context
    public String findRoleMapping(String role) {
        String realRole;
        synchronized (this.roleMappings) {
            realRole = this.roleMappings.get(role);
        }
        if (realRole != null) {
            return realRole;
        }
        return role;
    }

    @Override // org.apache.catalina.Context
    public boolean findSecurityRole(String role) {
        synchronized (this.securityRolesLock) {
            for (String securityRole : this.securityRoles) {
                if (role.equals(securityRole)) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override // org.apache.catalina.Context
    public String[] findSecurityRoles() {
        String[] strArr;
        synchronized (this.securityRolesLock) {
            strArr = this.securityRoles;
        }
        return strArr;
    }

    @Override // org.apache.catalina.Context
    public String findServletMapping(String pattern) {
        String str;
        synchronized (this.servletMappingsLock) {
            str = this.servletMappings.get(pattern);
        }
        return str;
    }

    @Override // org.apache.catalina.Context
    public String[] findServletMappings() {
        String[] strArr;
        synchronized (this.servletMappingsLock) {
            strArr = (String[]) this.servletMappings.keySet().toArray(new String[0]);
        }
        return strArr;
    }

    @Override // org.apache.catalina.Context
    public boolean findWelcomeFile(String name) {
        synchronized (this.welcomeFilesLock) {
            for (String welcomeFile : this.welcomeFiles) {
                if (name.equals(welcomeFile)) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override // org.apache.catalina.Context
    public String[] findWatchedResources() {
        String[] strArr;
        synchronized (this.watchedResourcesLock) {
            strArr = this.watchedResources;
        }
        return strArr;
    }

    @Override // org.apache.catalina.Context
    public String[] findWelcomeFiles() {
        String[] strArr;
        synchronized (this.welcomeFilesLock) {
            strArr = this.welcomeFiles;
        }
        return strArr;
    }

    @Override // org.apache.catalina.Context
    public String[] findWrapperLifecycles() {
        String[] strArr;
        synchronized (this.wrapperLifecyclesLock) {
            strArr = this.wrapperLifecycles;
        }
        return strArr;
    }

    @Override // org.apache.catalina.Context
    public String[] findWrapperListeners() {
        String[] strArr;
        synchronized (this.wrapperListenersLock) {
            strArr = this.wrapperListeners;
        }
        return strArr;
    }

    @Override // org.apache.catalina.Context
    public synchronized void reload() {
        if (!getState().isAvailable()) {
            throw new IllegalStateException(sm.getString("standardContext.notStarted", getName()));
        }
        if (log.isInfoEnabled()) {
            log.info(sm.getString("standardContext.reloadingStarted", getName()));
        }
        setPaused(true);
        try {
            stop();
        } catch (LifecycleException e) {
            log.error(sm.getString("standardContext.stoppingContext", getName()), e);
        }
        try {
            start();
        } catch (LifecycleException e2) {
            log.error(sm.getString("standardContext.startingContext", getName()), e2);
        }
        setPaused(false);
        if (log.isInfoEnabled()) {
            log.info(sm.getString("standardContext.reloadingCompleted", getName()));
        }
    }

    @Override // org.apache.catalina.Context
    public void removeApplicationListener(String listener) {
        if (this.applicationListeners.remove(listener)) {
            fireContainerEvent("removeApplicationListener", listener);
        }
    }

    @Override // org.apache.catalina.Context
    public void removeApplicationParameter(String name) {
        synchronized (this.applicationParametersLock) {
            int n = -1;
            int i = 0;
            while (true) {
                if (i >= this.applicationParameters.length) {
                    break;
                }
                if (!name.equals(this.applicationParameters[i].getName())) {
                    i++;
                } else {
                    n = i;
                    break;
                }
            }
            if (n < 0) {
                return;
            }
            int j = 0;
            ApplicationParameter[] results = new ApplicationParameter[this.applicationParameters.length - 1];
            for (int i2 = 0; i2 < this.applicationParameters.length; i2++) {
                if (i2 != n) {
                    int i3 = j;
                    j++;
                    results[i3] = this.applicationParameters[i2];
                }
            }
            this.applicationParameters = results;
            fireContainerEvent("removeApplicationParameter", name);
        }
    }

    @Override // org.apache.catalina.core.ContainerBase, org.apache.catalina.Container
    public void removeChild(Container child) {
        if (!(child instanceof Wrapper)) {
            throw new IllegalArgumentException(sm.getString("standardContext.notWrapper"));
        }
        super.removeChild(child);
    }

    @Override // org.apache.catalina.Context
    public void removeConstraint(SecurityConstraint constraint) {
        synchronized (this.constraintsLock) {
            int n = -1;
            int i = 0;
            while (true) {
                if (i >= this.constraints.length) {
                    break;
                }
                if (!this.constraints[i].equals(constraint)) {
                    i++;
                } else {
                    n = i;
                    break;
                }
            }
            if (n < 0) {
                return;
            }
            int j = 0;
            SecurityConstraint[] results = new SecurityConstraint[this.constraints.length - 1];
            for (int i2 = 0; i2 < this.constraints.length; i2++) {
                if (i2 != n) {
                    int i3 = j;
                    j++;
                    results[i3] = this.constraints[i2];
                }
            }
            this.constraints = results;
            fireContainerEvent("removeConstraint", constraint);
        }
    }

    @Override // org.apache.catalina.Context
    public void removeErrorPage(ErrorPage errorPage) {
        this.errorPageSupport.remove(errorPage);
        fireContainerEvent("removeErrorPage", errorPage);
    }

    @Override // org.apache.catalina.Context
    public void removeFilterDef(FilterDef filterDef) {
        synchronized (this.filterDefs) {
            this.filterDefs.remove(filterDef.getFilterName());
        }
        fireContainerEvent("removeFilterDef", filterDef);
    }

    @Override // org.apache.catalina.Context
    public void removeFilterMap(FilterMap filterMap) {
        this.filterMaps.remove(filterMap);
        fireContainerEvent("removeFilterMap", filterMap);
    }

    public void removeMessageDestination(String name) {
        synchronized (this.messageDestinations) {
            this.messageDestinations.remove(name);
        }
        fireContainerEvent("removeMessageDestination", name);
    }

    @Override // org.apache.catalina.Context
    public void removeMimeMapping(String extension) {
        synchronized (this.mimeMappings) {
            this.mimeMappings.remove(extension);
        }
        fireContainerEvent("removeMimeMapping", extension);
    }

    @Override // org.apache.catalina.Context
    public void removeParameter(String name) {
        this.parameters.remove(name);
        fireContainerEvent("removeParameter", name);
    }

    @Override // org.apache.catalina.Context
    public void removeRoleMapping(String role) {
        synchronized (this.roleMappings) {
            this.roleMappings.remove(role);
        }
        fireContainerEvent("removeRoleMapping", role);
    }

    @Override // org.apache.catalina.Context
    public void removeSecurityRole(String role) {
        synchronized (this.securityRolesLock) {
            int n = -1;
            int i = 0;
            while (true) {
                if (i >= this.securityRoles.length) {
                    break;
                }
                if (!role.equals(this.securityRoles[i])) {
                    i++;
                } else {
                    n = i;
                    break;
                }
            }
            if (n < 0) {
                return;
            }
            int j = 0;
            String[] results = new String[this.securityRoles.length - 1];
            for (int i2 = 0; i2 < this.securityRoles.length; i2++) {
                if (i2 != n) {
                    int i3 = j;
                    j++;
                    results[i3] = this.securityRoles[i2];
                }
            }
            this.securityRoles = results;
            fireContainerEvent("removeSecurityRole", role);
        }
    }

    @Override // org.apache.catalina.Context
    public void removeServletMapping(String pattern) {
        String name;
        synchronized (this.servletMappingsLock) {
            name = this.servletMappings.remove(pattern);
        }
        Wrapper wrapper = (Wrapper) findChild(name);
        if (wrapper != null) {
            wrapper.removeMapping(pattern);
        }
        fireContainerEvent("removeServletMapping", pattern);
    }

    @Override // org.apache.catalina.Context
    public void removeWatchedResource(String name) {
        synchronized (this.watchedResourcesLock) {
            int n = -1;
            int i = 0;
            while (true) {
                if (i >= this.watchedResources.length) {
                    break;
                }
                if (!this.watchedResources[i].equals(name)) {
                    i++;
                } else {
                    n = i;
                    break;
                }
            }
            if (n < 0) {
                return;
            }
            int j = 0;
            String[] results = new String[this.watchedResources.length - 1];
            for (int i2 = 0; i2 < this.watchedResources.length; i2++) {
                if (i2 != n) {
                    int i3 = j;
                    j++;
                    results[i3] = this.watchedResources[i2];
                }
            }
            this.watchedResources = results;
            fireContainerEvent("removeWatchedResource", name);
        }
    }

    @Override // org.apache.catalina.Context
    public void removeWelcomeFile(String name) {
        synchronized (this.welcomeFilesLock) {
            int n = -1;
            int i = 0;
            while (true) {
                if (i >= this.welcomeFiles.length) {
                    break;
                }
                if (!this.welcomeFiles[i].equals(name)) {
                    i++;
                } else {
                    n = i;
                    break;
                }
            }
            if (n < 0) {
                return;
            }
            int j = 0;
            String[] results = new String[this.welcomeFiles.length - 1];
            for (int i2 = 0; i2 < this.welcomeFiles.length; i2++) {
                if (i2 != n) {
                    int i3 = j;
                    j++;
                    results[i3] = this.welcomeFiles[i2];
                }
            }
            this.welcomeFiles = results;
            if (getState().equals(LifecycleState.STARTED)) {
                fireContainerEvent(Context.REMOVE_WELCOME_FILE_EVENT, name);
            }
        }
    }

    @Override // org.apache.catalina.Context
    public void removeWrapperLifecycle(String listener) {
        synchronized (this.wrapperLifecyclesLock) {
            int n = -1;
            int i = 0;
            while (true) {
                if (i >= this.wrapperLifecycles.length) {
                    break;
                }
                if (!this.wrapperLifecycles[i].equals(listener)) {
                    i++;
                } else {
                    n = i;
                    break;
                }
            }
            if (n < 0) {
                return;
            }
            int j = 0;
            String[] results = new String[this.wrapperLifecycles.length - 1];
            for (int i2 = 0; i2 < this.wrapperLifecycles.length; i2++) {
                if (i2 != n) {
                    int i3 = j;
                    j++;
                    results[i3] = this.wrapperLifecycles[i2];
                }
            }
            this.wrapperLifecycles = results;
            fireContainerEvent("removeWrapperLifecycle", listener);
        }
    }

    @Override // org.apache.catalina.Context
    public void removeWrapperListener(String listener) {
        synchronized (this.wrapperListenersLock) {
            int n = -1;
            int i = 0;
            while (true) {
                if (i >= this.wrapperListeners.length) {
                    break;
                }
                if (!this.wrapperListeners[i].equals(listener)) {
                    i++;
                } else {
                    n = i;
                    break;
                }
            }
            if (n < 0) {
                return;
            }
            int j = 0;
            String[] results = new String[this.wrapperListeners.length - 1];
            for (int i2 = 0; i2 < this.wrapperListeners.length; i2++) {
                if (i2 != n) {
                    int i3 = j;
                    j++;
                    results[i3] = this.wrapperListeners[i2];
                }
            }
            this.wrapperListeners = results;
            fireContainerEvent("removeWrapperListener", listener);
        }
    }

    public long getProcessingTime() {
        long result = 0;
        Container[] children = findChildren();
        if (children != null) {
            for (Container child : children) {
                result += ((StandardWrapper) child).getProcessingTime();
            }
        }
        return result;
    }

    public long getMaxTime() {
        long result = 0;
        Container[] children = findChildren();
        if (children != null) {
            for (Container child : children) {
                long time = ((StandardWrapper) child).getMaxTime();
                if (time > result) {
                    result = time;
                }
            }
        }
        return result;
    }

    public long getMinTime() {
        long result = -1;
        Container[] children = findChildren();
        if (children != null) {
            for (Container child : children) {
                long time = ((StandardWrapper) child).getMinTime();
                if (result < 0 || time < result) {
                    result = time;
                }
            }
        }
        return result;
    }

    @Deprecated
    public int getRequestCount() {
        int result = 0;
        Container[] children = findChildren();
        if (children != null) {
            for (Container child : children) {
                result += ((StandardWrapper) child).getRequestCount();
            }
        }
        return result;
    }

    @Deprecated
    public int getErrorCount() {
        int result = 0;
        Container[] children = findChildren();
        if (children != null) {
            for (Container child : children) {
                result += ((StandardWrapper) child).getErrorCount();
            }
        }
        return result;
    }

    @Override // org.apache.catalina.Context
    public String getRealPath(String path) {
        if ("".equals(path)) {
            path = "/";
        }
        if (this.resources != null) {
            try {
                WebResource resource = this.resources.getResource(path);
                String canonicalPath = resource.getCanonicalPath();
                if (canonicalPath == null) {
                    return null;
                }
                if (((resource.isDirectory() && !canonicalPath.endsWith(File.separator)) || !resource.exists()) && path.endsWith("/")) {
                    return canonicalPath + File.separatorChar;
                }
                return canonicalPath;
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }

    public void dynamicServletCreated(Servlet servlet) {
        this.createdServlets.add(servlet);
    }

    public boolean wasCreatedDynamicServlet(Servlet servlet) {
        return this.createdServlets.contains(servlet);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/StandardContext$ContextFilterMaps.class */
    private static final class ContextFilterMaps {
        private final Object lock = new Object();
        private FilterMap[] array = new FilterMap[0];
        private int insertPoint = 0;

        private ContextFilterMaps() {
        }

        public FilterMap[] asArray() {
            FilterMap[] filterMapArr;
            synchronized (this.lock) {
                filterMapArr = this.array;
            }
            return filterMapArr;
        }

        public void add(FilterMap filterMap) {
            synchronized (this.lock) {
                FilterMap[] results = (FilterMap[]) Arrays.copyOf(this.array, this.array.length + 1);
                results[this.array.length] = filterMap;
                this.array = results;
            }
        }

        public void addBefore(FilterMap filterMap) {
            synchronized (this.lock) {
                FilterMap[] results = new FilterMap[this.array.length + 1];
                System.arraycopy(this.array, 0, results, 0, this.insertPoint);
                System.arraycopy(this.array, this.insertPoint, results, this.insertPoint + 1, this.array.length - this.insertPoint);
                results[this.insertPoint] = filterMap;
                this.array = results;
                this.insertPoint++;
            }
        }

        public void remove(FilterMap filterMap) {
            synchronized (this.lock) {
                int n = -1;
                int i = 0;
                while (true) {
                    if (i >= this.array.length) {
                        break;
                    }
                    if (this.array[i] != filterMap) {
                        i++;
                    } else {
                        n = i;
                        break;
                    }
                }
                if (n < 0) {
                    return;
                }
                FilterMap[] results = new FilterMap[this.array.length - 1];
                System.arraycopy(this.array, 0, results, 0, n);
                System.arraycopy(this.array, n + 1, results, n, (this.array.length - 1) - n);
                this.array = results;
                if (n < this.insertPoint) {
                    this.insertPoint--;
                }
            }
        }
    }

    public boolean filterStart() {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Starting filters");
        }
        boolean ok = true;
        synchronized (this.filterDefs) {
            this.filterConfigs.clear();
            for (Map.Entry<String, FilterDef> entry : this.filterDefs.entrySet()) {
                String name = entry.getKey();
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug(" Starting filter '" + name + "'");
                }
                try {
                    ApplicationFilterConfig filterConfig = new ApplicationFilterConfig(this, entry.getValue());
                    this.filterConfigs.put(name, filterConfig);
                } catch (Throwable t) {
                    Throwable t2 = ExceptionUtils.unwrapInvocationTargetException(t);
                    ExceptionUtils.handleThrowable(t2);
                    getLogger().error(sm.getString("standardContext.filterStart", name), t2);
                    ok = false;
                }
            }
        }
        return ok;
    }

    public boolean filterStop() {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Stopping filters");
        }
        synchronized (this.filterDefs) {
            for (Map.Entry<String, ApplicationFilterConfig> entry : this.filterConfigs.entrySet()) {
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug(" Stopping filter '" + entry.getKey() + "'");
                }
                ApplicationFilterConfig filterConfig = entry.getValue();
                filterConfig.release();
            }
            this.filterConfigs.clear();
        }
        return true;
    }

    public FilterConfig findFilterConfig(String name) {
        ApplicationFilterConfig applicationFilterConfig;
        synchronized (this.filterDefs) {
            applicationFilterConfig = this.filterConfigs.get(name);
        }
        return applicationFilterConfig;
    }

    public boolean listenerStart() {
        if (log.isDebugEnabled()) {
            log.debug("Configuring application event listeners");
        }
        String[] listeners = findApplicationListeners();
        Object[] results = new Object[listeners.length];
        boolean ok = true;
        for (int i = 0; i < results.length; i++) {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug(" Configuring event listener class '" + listeners[i] + "'");
            }
            try {
                results[i] = getInstanceManager().newInstance(listeners[i]);
            } catch (Throwable t) {
                Throwable t2 = ExceptionUtils.unwrapInvocationTargetException(t);
                ExceptionUtils.handleThrowable(t2);
                getLogger().error(sm.getString("standardContext.applicationListener", listeners[i]), t2);
                ok = false;
            }
        }
        if (!ok) {
            getLogger().error(sm.getString("standardContext.applicationSkipped"));
            return false;
        }
        List<Object> eventListeners = new ArrayList<>();
        List<Object> lifecycleListeners = new ArrayList<>();
        for (Object result : results) {
            if ((result instanceof ServletContextAttributeListener) || (result instanceof ServletRequestAttributeListener) || (result instanceof ServletRequestListener) || (result instanceof HttpSessionIdListener) || (result instanceof HttpSessionAttributeListener)) {
                eventListeners.add(result);
            }
            if ((result instanceof ServletContextListener) || (result instanceof HttpSessionListener)) {
                lifecycleListeners.add(result);
            }
        }
        eventListeners.addAll(Arrays.asList(getApplicationEventListeners()));
        setApplicationEventListeners(eventListeners.toArray());
        for (Object lifecycleListener : getApplicationLifecycleListeners()) {
            lifecycleListeners.add(lifecycleListener);
            if (lifecycleListener instanceof ServletContextListener) {
                this.noPluggabilityListeners.add(lifecycleListener);
            }
        }
        setApplicationLifecycleListeners(lifecycleListeners.toArray());
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Sending application start events");
        }
        getServletContext();
        this.context.setNewServletContextListenerAllowed(false);
        Object[] instances = getApplicationLifecycleListeners();
        if (instances == null || instances.length == 0) {
            return ok;
        }
        ServletContextEvent event = new ServletContextEvent(getServletContext());
        ServletContextEvent tldEvent = null;
        if (this.noPluggabilityListeners.size() > 0) {
            this.noPluggabilityServletContext = new NoPluggabilityServletContext(getServletContext());
            tldEvent = new ServletContextEvent(this.noPluggabilityServletContext);
        }
        for (Object instance : instances) {
            if (instance instanceof ServletContextListener) {
                ServletContextListener listener = (ServletContextListener) instance;
                try {
                    fireContainerEvent("beforeContextInitialized", listener);
                    if (this.noPluggabilityListeners.contains(listener)) {
                        listener.contextInitialized(tldEvent);
                    } else {
                        listener.contextInitialized(event);
                    }
                    fireContainerEvent("afterContextInitialized", listener);
                } catch (Throwable t3) {
                    ExceptionUtils.handleThrowable(t3);
                    fireContainerEvent("afterContextInitialized", listener);
                    getLogger().error(sm.getString("standardContext.listenerStart", instance.getClass().getName()), t3);
                    ok = false;
                }
            }
        }
        return ok;
    }

    public boolean listenerStop() {
        if (log.isDebugEnabled()) {
            log.debug("Sending application stop events");
        }
        boolean ok = true;
        Object[] listeners = getApplicationLifecycleListeners();
        if (listeners != null && listeners.length > 0) {
            ServletContextEvent event = new ServletContextEvent(getServletContext());
            ServletContextEvent tldEvent = null;
            if (this.noPluggabilityServletContext != null) {
                tldEvent = new ServletContextEvent(this.noPluggabilityServletContext);
            }
            for (int i = 0; i < listeners.length; i++) {
                int j = (listeners.length - 1) - i;
                if (listeners[j] != null) {
                    if (listeners[j] instanceof ServletContextListener) {
                        ServletContextListener listener = (ServletContextListener) listeners[j];
                        try {
                            fireContainerEvent("beforeContextDestroyed", listener);
                            if (this.noPluggabilityListeners.contains(listener)) {
                                listener.contextDestroyed(tldEvent);
                            } else {
                                listener.contextDestroyed(event);
                            }
                            fireContainerEvent("afterContextDestroyed", listener);
                        } catch (Throwable t) {
                            ExceptionUtils.handleThrowable(t);
                            fireContainerEvent("afterContextDestroyed", listener);
                            getLogger().error(sm.getString("standardContext.listenerStop", listeners[j].getClass().getName()), t);
                            ok = false;
                        }
                    }
                    try {
                        if (getInstanceManager() != null) {
                            getInstanceManager().destroyInstance(listeners[j]);
                        }
                    } catch (Throwable t2) {
                        Throwable t3 = ExceptionUtils.unwrapInvocationTargetException(t2);
                        ExceptionUtils.handleThrowable(t3);
                        getLogger().error(sm.getString("standardContext.listenerStop", listeners[j].getClass().getName()), t3);
                        ok = false;
                    }
                }
            }
        }
        Object[] listeners2 = getApplicationEventListeners();
        if (listeners2 != null) {
            for (int i2 = 0; i2 < listeners2.length; i2++) {
                int j2 = (listeners2.length - 1) - i2;
                if (listeners2[j2] != null) {
                    try {
                        if (getInstanceManager() != null) {
                            getInstanceManager().destroyInstance(listeners2[j2]);
                        }
                    } catch (Throwable t4) {
                        Throwable t5 = ExceptionUtils.unwrapInvocationTargetException(t4);
                        ExceptionUtils.handleThrowable(t5);
                        getLogger().error(sm.getString("standardContext.listenerStop", listeners2[j2].getClass().getName()), t5);
                        ok = false;
                    }
                }
            }
        }
        setApplicationEventListeners(null);
        setApplicationLifecycleListeners(null);
        this.noPluggabilityServletContext = null;
        this.noPluggabilityListeners.clear();
        return ok;
    }

    public void resourcesStart() throws LifecycleException {
        if (!this.resources.getState().isAvailable()) {
            this.resources.start();
        }
        if (this.effectiveMajorVersion >= 3 && this.addWebinfClassesResources) {
            WebResource webinfClassesResource = this.resources.getResource("/WEB-INF/classes/META-INF/resources");
            if (webinfClassesResource.isDirectory()) {
                getResources().createWebResourceSet(WebResourceRoot.ResourceSetType.RESOURCE_JAR, "/", webinfClassesResource.getURL(), "/");
            }
        }
    }

    public boolean resourcesStop() {
        boolean ok = true;
        Lock writeLock = this.resourcesLock.writeLock();
        writeLock.lock();
        try {
            try {
                if (this.resources != null) {
                    this.resources.stop();
                }
            } catch (Throwable t) {
                ExceptionUtils.handleThrowable(t);
                log.error(sm.getString("standardContext.resourcesStop"), t);
                ok = false;
                writeLock.unlock();
            }
            return ok;
        } finally {
            writeLock.unlock();
        }
    }

    public boolean loadOnStartup(Container[] children) {
        TreeMap<Integer, ArrayList<Wrapper>> map = new TreeMap<>();
        for (Container child : children) {
            Wrapper wrapper = (Wrapper) child;
            int loadOnStartup = wrapper.getLoadOnStartup();
            if (loadOnStartup >= 0) {
                Integer key = Integer.valueOf(loadOnStartup);
                ((ArrayList) map.computeIfAbsent(key, k -> {
                    return new ArrayList();
                })).add(wrapper);
            }
        }
        for (ArrayList<Wrapper> list : map.values()) {
            Iterator<Wrapper> it = list.iterator();
            while (it.hasNext()) {
                Wrapper wrapper2 = it.next();
                try {
                    wrapper2.load();
                } catch (ServletException e) {
                    getLogger().error(sm.getString("standardContext.loadOnStartup.loadException", getName(), wrapper2.getName()), StandardWrapper.getRootCause(e));
                    if (getComputedFailCtxIfServletStartFails()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.core.ContainerBase, org.apache.catalina.util.LifecycleBase
    public synchronized void startInternal() throws LifecycleException {
        if (log.isDebugEnabled()) {
            log.debug("Starting " + getBaseName());
        }
        if (getObjectName() != null) {
            Notification notification = new Notification("j2ee.state.starting", getObjectName(), this.sequenceNumber.getAndIncrement());
            this.broadcaster.sendNotification(notification);
        }
        setConfigured(false);
        boolean ok = true;
        if (this.namingResources != null) {
            this.namingResources.start();
        }
        postWorkDirectory();
        if (getResources() == null) {
            if (log.isDebugEnabled()) {
                log.debug("Configuring default Resources");
            }
            try {
                setResources(new StandardRoot(this));
            } catch (IllegalArgumentException e) {
                log.error(sm.getString("standardContext.resourcesInit"), e);
                ok = false;
            }
        }
        if (ok) {
            resourcesStart();
        }
        if (getLoader() == null) {
            WebappLoader webappLoader = new WebappLoader();
            webappLoader.setDelegate(getDelegate());
            setLoader(webappLoader);
        }
        if (this.cookieProcessor == null) {
            this.cookieProcessor = new Rfc6265CookieProcessor();
        }
        getCharsetMapper();
        String useNamingProperty = System.getProperty("catalina.useNaming");
        if (useNamingProperty != null && useNamingProperty.equals("false")) {
            this.useNaming = false;
        }
        if (ok && isUseNaming() && getNamingContextListener() == null) {
            NamingContextListener ncl = new NamingContextListener();
            ncl.setName(getNamingContextName());
            ncl.setExceptionOnFailedWrite(getJndiExceptionOnFailedWrite());
            addLifecycleListener(ncl);
            setNamingContextListener(ncl);
        }
        if (log.isDebugEnabled()) {
            log.debug("Processing standard container startup");
        }
        ClassLoader oldCCL = bindThread();
        if (ok) {
            try {
                Loader loader = getLoader();
                if (loader instanceof Lifecycle) {
                    ((Lifecycle) loader).start();
                }
                if (loader.getClassLoader() instanceof WebappClassLoaderBase) {
                    WebappClassLoaderBase cl = (WebappClassLoaderBase) loader.getClassLoader();
                    cl.setClearReferencesRmiTargets(getClearReferencesRmiTargets());
                    cl.setClearReferencesStopThreads(getClearReferencesStopThreads());
                    cl.setClearReferencesStopTimerThreads(getClearReferencesStopTimerThreads());
                    cl.setClearReferencesHttpClientKeepAliveThread(getClearReferencesHttpClientKeepAliveThread());
                    cl.setClearReferencesObjectStreamClassCaches(getClearReferencesObjectStreamClassCaches());
                    cl.setClearReferencesThreadLocals(getClearReferencesThreadLocals());
                    cl.setSkipMemoryLeakChecksOnJvmShutdown(getSkipMemoryLeakChecksOnJvmShutdown());
                }
                unbindThread(oldCCL);
                oldCCL = bindThread();
                this.logger = null;
                getLogger();
                Realm realm = getRealmInternal();
                if (null != realm) {
                    if (realm instanceof Lifecycle) {
                        ((Lifecycle) realm).start();
                    }
                    CredentialHandler safeHandler = new CredentialHandler() { // from class: org.apache.catalina.core.StandardContext.2
                        @Override // org.apache.catalina.CredentialHandler
                        public boolean matches(String inputCredentials, String storedCredentials) {
                            return StandardContext.this.getRealmInternal().getCredentialHandler().matches(inputCredentials, storedCredentials);
                        }

                        @Override // org.apache.catalina.CredentialHandler
                        public String mutate(String inputCredentials) {
                            return StandardContext.this.getRealmInternal().getCredentialHandler().mutate(inputCredentials);
                        }
                    };
                    this.context.setAttribute(Globals.CREDENTIAL_HANDLER, safeHandler);
                }
                fireLifecycleEvent(Lifecycle.CONFIGURE_START_EVENT, null);
                for (Container child : findChildren()) {
                    if (!child.getState().isAvailable()) {
                        child.start();
                    }
                }
                if (this.pipeline instanceof Lifecycle) {
                    ((Lifecycle) this.pipeline).start();
                }
                Manager contextManager = null;
                Manager manager = getManager();
                if (manager == null) {
                    if (log.isDebugEnabled()) {
                        Log log2 = log;
                        StringManager stringManager = sm;
                        Object[] objArr = new Object[2];
                        objArr[0] = Boolean.valueOf(getCluster() != null);
                        objArr[1] = Boolean.valueOf(this.distributable);
                        log2.debug(stringManager.getString("standardContext.cluster.noManager", objArr));
                    }
                    if (getCluster() != null && this.distributable) {
                        try {
                            contextManager = getCluster().createManager(getName());
                        } catch (Exception ex) {
                            log.error(sm.getString("standardContext.cluster.managerError"), ex);
                            ok = false;
                        }
                    } else {
                        contextManager = new StandardManager();
                    }
                }
                if (contextManager != null) {
                    if (log.isDebugEnabled()) {
                        log.debug(sm.getString("standardContext.manager", contextManager.getClass().getName()));
                    }
                    setManager(contextManager);
                }
                if (manager != null && getCluster() != null && this.distributable) {
                    getCluster().registerManager(manager);
                }
            } catch (Throwable th) {
                unbindThread(oldCCL);
                throw th;
            }
        }
        if (!getConfigured()) {
            log.error(sm.getString("standardContext.configurationFail"));
            ok = false;
        }
        if (ok) {
            getServletContext().setAttribute(Globals.RESOURCES_ATTR, getResources());
            if (getInstanceManager() == null) {
                setInstanceManager(createInstanceManager());
            }
            getServletContext().setAttribute(InstanceManager.class.getName(), getInstanceManager());
            InstanceManagerBindings.bind(getLoader().getClassLoader(), getInstanceManager());
            getServletContext().setAttribute(JarScanner.class.getName(), getJarScanner());
            getServletContext().setAttribute(Globals.WEBAPP_VERSION, getWebappVersion());
            if (!Globals.IS_SECURITY_ENABLED) {
                getServletContext().setAttribute(ScheduledThreadPoolExecutor.class.getName(), Container.getService(this).getServer().getUtilityExecutor());
            }
        }
        mergeParameters();
        for (Map.Entry<ServletContainerInitializer, Set<Class<?>>> entry : this.initializers.entrySet()) {
            try {
                entry.getKey().onStartup(entry.getValue(), getServletContext());
            } catch (ServletException e2) {
                log.error(sm.getString("standardContext.sciFail"), e2);
                ok = false;
            }
        }
        if (ok && !listenerStart()) {
            log.error(sm.getString("standardContext.listenerFail"));
            ok = false;
        }
        if (ok) {
            checkConstraintsForUncoveredMethods(findConstraints());
        }
        try {
            Manager manager2 = getManager();
            if (manager2 instanceof Lifecycle) {
                ((Lifecycle) manager2).start();
            }
        } catch (Exception e3) {
            log.error(sm.getString("standardContext.managerFail"), e3);
            ok = false;
        }
        if (ok && !filterStart()) {
            log.error(sm.getString("standardContext.filterFail"));
            ok = false;
        }
        if (ok && !loadOnStartup(findChildren())) {
            log.error(sm.getString("standardContext.servletFail"));
            ok = false;
        }
        super.threadStart();
        unbindThread(oldCCL);
        if (ok) {
            if (log.isDebugEnabled()) {
                log.debug("Starting completed");
            }
        } else {
            log.error(sm.getString("standardContext.startFailed", getName()));
        }
        this.startTime = System.currentTimeMillis();
        if (ok && getObjectName() != null) {
            Notification notification2 = new Notification("j2ee.state.running", getObjectName(), this.sequenceNumber.getAndIncrement());
            this.broadcaster.sendNotification(notification2);
        }
        getResources().gc();
        if (!ok) {
            setState(LifecycleState.FAILED);
            if (getObjectName() != null) {
                Notification notification3 = new Notification("j2ee.object.failed", getObjectName(), this.sequenceNumber.getAndIncrement());
                this.broadcaster.sendNotification(notification3);
                return;
            }
            return;
        }
        setState(LifecycleState.STARTING);
    }

    private void checkConstraintsForUncoveredMethods(SecurityConstraint[] constraints) {
        SecurityConstraint[] newConstraints = SecurityConstraint.findUncoveredHttpMethods(constraints, getDenyUncoveredHttpMethods(), getLogger());
        for (SecurityConstraint constraint : newConstraints) {
            addConstraint(constraint);
        }
    }

    @Override // org.apache.catalina.Context
    public InstanceManager createInstanceManager() {
        javax.naming.Context context = null;
        if (isUseNaming() && getNamingContextListener() != null) {
            context = getNamingContextListener().getEnvContext();
        }
        Map<String, Map<String, String>> injectionMap = buildInjectionMap(getIgnoreAnnotations() ? new NamingResourcesImpl() : getNamingResources());
        return new DefaultInstanceManager(context, injectionMap, this, getClass().getClassLoader());
    }

    private Map<String, Map<String, String>> buildInjectionMap(NamingResourcesImpl namingResources) {
        Map<String, Map<String, String>> injectionMap = new HashMap<>();
        for (Injectable resource : namingResources.findLocalEjbs()) {
            addInjectionTarget(resource, injectionMap);
        }
        for (Injectable resource2 : namingResources.findEjbs()) {
            addInjectionTarget(resource2, injectionMap);
        }
        for (Injectable resource3 : namingResources.findEnvironments()) {
            addInjectionTarget(resource3, injectionMap);
        }
        for (Injectable resource4 : namingResources.findMessageDestinationRefs()) {
            addInjectionTarget(resource4, injectionMap);
        }
        for (Injectable resource5 : namingResources.findResourceEnvRefs()) {
            addInjectionTarget(resource5, injectionMap);
        }
        for (Injectable resource6 : namingResources.findResources()) {
            addInjectionTarget(resource6, injectionMap);
        }
        for (Injectable resource7 : namingResources.findServices()) {
            addInjectionTarget(resource7, injectionMap);
        }
        return injectionMap;
    }

    private void addInjectionTarget(Injectable resource, Map<String, Map<String, String>> injectionMap) {
        List<InjectionTarget> injectionTargets = resource.getInjectionTargets();
        if (injectionTargets != null && injectionTargets.size() > 0) {
            String jndiName = resource.getName();
            for (InjectionTarget injectionTarget : injectionTargets) {
                String clazz = injectionTarget.getTargetClass();
                injectionMap.computeIfAbsent(clazz, k -> {
                    return new HashMap();
                }).put(injectionTarget.getTargetName(), jndiName);
            }
        }
    }

    private void mergeParameters() {
        Map<String, String> mergedParams = new HashMap<>();
        String[] names = findParameters();
        for (String s : names) {
            mergedParams.put(s, findParameter(s));
        }
        ApplicationParameter[] params = findApplicationParameters();
        for (ApplicationParameter param : params) {
            if (param.getOverride()) {
                mergedParams.computeIfAbsent(param.getName(), k -> {
                    return param.getValue();
                });
            } else {
                mergedParams.put(param.getName(), param.getValue());
            }
        }
        ServletContext sc = getServletContext();
        for (Map.Entry<String, String> entry : mergedParams.entrySet()) {
            sc.setInitParameter(entry.getKey(), entry.getValue());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.core.ContainerBase, org.apache.catalina.util.LifecycleBase
    public synchronized void stopInternal() throws LifecycleException {
        if (getObjectName() != null) {
            Notification notification = new Notification("j2ee.state.stopping", getObjectName(), this.sequenceNumber.getAndIncrement());
            this.broadcaster.sendNotification(notification);
        }
        long limit = System.currentTimeMillis() + this.unloadDelay;
        while (this.inProgressAsyncCount.get() > 0 && System.currentTimeMillis() < limit) {
            try {
                Thread.sleep(50L);
            } catch (InterruptedException e) {
                log.info(sm.getString("standardContext.stop.asyncWaitInterrupted"), e);
            }
        }
        setState(LifecycleState.STOPPING);
        ClassLoader oldCCL = bindThread();
        try {
            Container[] children = findChildren();
            threadStop();
            for (Container child : children) {
                child.stop();
            }
            filterStop();
            Manager manager = getManager();
            if ((manager instanceof Lifecycle) && ((Lifecycle) manager).getState().isAvailable()) {
                ((Lifecycle) manager).stop();
            }
            listenerStop();
            setCharsetMapper(null);
            if (log.isDebugEnabled()) {
                log.debug("Processing standard container shutdown");
            }
            if (this.namingResources != null) {
                this.namingResources.stop();
            }
            fireLifecycleEvent(Lifecycle.CONFIGURE_STOP_EVENT, null);
            if ((this.pipeline instanceof Lifecycle) && ((Lifecycle) this.pipeline).getState().isAvailable()) {
                ((Lifecycle) this.pipeline).stop();
            }
            if (this.context != null) {
                this.context.clearAttributes();
            }
            Realm realm = getRealmInternal();
            if (realm instanceof Lifecycle) {
                ((Lifecycle) realm).stop();
            }
            Loader loader = getLoader();
            if (loader instanceof Lifecycle) {
                ClassLoader classLoader = loader.getClassLoader();
                ((Lifecycle) loader).stop();
                if (classLoader != null) {
                    InstanceManagerBindings.unbind(classLoader);
                }
            }
            resourcesStop();
            unbindThread(oldCCL);
            if (getObjectName() != null) {
                Notification notification2 = new Notification("j2ee.state.stopped", getObjectName(), this.sequenceNumber.getAndIncrement());
                this.broadcaster.sendNotification(notification2);
            }
            this.context = null;
            try {
                resetContext();
            } catch (Exception ex) {
                log.error("Error resetting context " + this + " " + ex, ex);
            }
            setInstanceManager(null);
            if (log.isDebugEnabled()) {
                log.debug("Stopping complete");
            }
        } catch (Throwable th) {
            unbindThread(oldCCL);
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.core.ContainerBase, org.apache.catalina.util.LifecycleMBeanBase, org.apache.catalina.util.LifecycleBase
    public void destroyInternal() throws LifecycleException {
        if (getObjectName() != null) {
            Notification notification = new Notification("j2ee.object.deleted", getObjectName(), this.sequenceNumber.getAndIncrement());
            this.broadcaster.sendNotification(notification);
        }
        if (this.namingResources != null) {
            this.namingResources.destroy();
        }
        Loader loader = getLoader();
        if (loader instanceof Lifecycle) {
            ((Lifecycle) loader).destroy();
        }
        Manager manager = getManager();
        if (manager instanceof Lifecycle) {
            ((Lifecycle) manager).destroy();
        }
        if (this.resources != null) {
            this.resources.destroy();
        }
        super.destroyInternal();
    }

    @Override // org.apache.catalina.core.ContainerBase, org.apache.catalina.Container
    public synchronized void backgroundProcess() {
        if (!getState().isAvailable()) {
            return;
        }
        Loader loader = getLoader();
        if (loader != null) {
            try {
                loader.backgroundProcess();
            } catch (Exception e) {
                log.warn(sm.getString("standardContext.backgroundProcess.loader", loader), e);
            }
        }
        Manager manager = getManager();
        if (manager != null) {
            try {
                manager.backgroundProcess();
            } catch (Exception e2) {
                log.warn(sm.getString("standardContext.backgroundProcess.manager", manager), e2);
            }
        }
        WebResourceRoot resources = getResources();
        if (resources != null) {
            try {
                resources.backgroundProcess();
            } catch (Exception e3) {
                log.warn(sm.getString("standardContext.backgroundProcess.resources", resources), e3);
            }
        }
        InstanceManager instanceManager = getInstanceManager();
        if (instanceManager != null) {
            try {
                instanceManager.backgroundProcess();
            } catch (Exception e4) {
                log.warn(sm.getString("standardContext.backgroundProcess.instanceManager", resources), e4);
            }
        }
        super.backgroundProcess();
    }

    private void resetContext() throws Exception {
        for (Container child : findChildren()) {
            removeChild(child);
        }
        this.startupTime = 0L;
        this.startTime = 0L;
        this.tldScanTime = 0L;
        this.distributable = false;
        this.applicationListeners.clear();
        this.applicationEventListenersList.clear();
        this.applicationLifecycleListenersObjects = new Object[0];
        this.jspConfigDescriptor = null;
        this.initializers.clear();
        this.createdServlets.clear();
        this.postConstructMethods.clear();
        this.preDestroyMethods.clear();
        if (log.isDebugEnabled()) {
            log.debug("resetContext " + getObjectName());
        }
    }

    protected String adjustURLPattern(String urlPattern) {
        if (urlPattern == null) {
            return urlPattern;
        }
        if (urlPattern.startsWith("/") || urlPattern.startsWith("*.")) {
            return urlPattern;
        }
        if (!isServlet22()) {
            return urlPattern;
        }
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("standardContext.urlPattern.patternWarning", urlPattern));
        }
        return "/" + urlPattern;
    }

    @Override // org.apache.catalina.Context
    public boolean isServlet22() {
        return XmlIdentifiers.WEB_22_PUBLIC.equals(this.publicId);
    }

    @Override // org.apache.catalina.Context
    public Set<String> addServletSecurity(ServletRegistration.Dynamic registration, ServletSecurityElement servletSecurityElement) {
        Set<String> conflicts = new HashSet<>();
        Collection<String> urlPatterns = registration.getMappings();
        for (String urlPattern : urlPatterns) {
            boolean foundConflict = false;
            SecurityConstraint[] securityConstraints = findConstraints();
            for (SecurityConstraint securityConstraint : securityConstraints) {
                SecurityCollection[] collections = securityConstraint.findCollections();
                int length = collections.length;
                int i = 0;
                while (true) {
                    if (i >= length) {
                        break;
                    }
                    SecurityCollection collection = collections[i];
                    if (collection.findPattern(urlPattern)) {
                        if (collection.isFromDescriptor()) {
                            foundConflict = true;
                            conflicts.add(urlPattern);
                            break;
                        }
                        collection.removePattern(urlPattern);
                        if (collection.findPatterns().length == 0) {
                            securityConstraint.removeCollection(collection);
                        }
                    }
                    i++;
                }
                if (securityConstraint.findCollections().length == 0) {
                    removeConstraint(securityConstraint);
                }
                if (foundConflict) {
                    break;
                }
            }
            if (!foundConflict) {
                SecurityConstraint[] newSecurityConstraints = SecurityConstraint.createConstraints(servletSecurityElement, urlPattern);
                for (SecurityConstraint securityConstraint2 : newSecurityConstraints) {
                    addConstraint(securityConstraint2);
                }
            }
        }
        return conflicts;
    }

    protected ClassLoader bindThread() {
        ClassLoader oldContextClassLoader = bind(false, null);
        if (isUseNaming()) {
            try {
                ContextBindings.bindThread(this, getNamingToken());
            } catch (NamingException e) {
            }
        }
        return oldContextClassLoader;
    }

    protected void unbindThread(ClassLoader oldContextClassLoader) {
        if (isUseNaming()) {
            ContextBindings.unbindThread(this, getNamingToken());
        }
        unbind(false, oldContextClassLoader);
    }

    @Override // org.apache.tomcat.ContextBind
    public ClassLoader bind(boolean usePrivilegedAction, ClassLoader originalClassLoader) {
        Loader loader = getLoader();
        ClassLoader webApplicationClassLoader = null;
        if (loader != null) {
            webApplicationClassLoader = loader.getClassLoader();
        }
        Thread currentThread = Thread.currentThread();
        if (originalClassLoader == null) {
            if (usePrivilegedAction) {
                PrivilegedAction<ClassLoader> pa = new PrivilegedGetTccl(currentThread);
                originalClassLoader = (ClassLoader) AccessController.doPrivileged(pa);
            } else {
                originalClassLoader = currentThread.getContextClassLoader();
            }
        }
        if (webApplicationClassLoader == null || webApplicationClassLoader == originalClassLoader) {
            return null;
        }
        ThreadBindingListener threadBindingListener = getThreadBindingListener();
        if (usePrivilegedAction) {
            PrivilegedAction<Void> pa2 = new PrivilegedSetTccl(currentThread, webApplicationClassLoader);
            AccessController.doPrivileged(pa2);
        } else {
            currentThread.setContextClassLoader(webApplicationClassLoader);
        }
        if (threadBindingListener != null) {
            try {
                threadBindingListener.bind();
            } catch (Throwable t) {
                ExceptionUtils.handleThrowable(t);
                log.error(sm.getString("standardContext.threadBindingListenerError", getName()), t);
            }
        }
        return originalClassLoader;
    }

    @Override // org.apache.tomcat.ContextBind
    public void unbind(boolean usePrivilegedAction, ClassLoader originalClassLoader) {
        if (originalClassLoader == null) {
            return;
        }
        if (this.threadBindingListener != null) {
            try {
                this.threadBindingListener.unbind();
            } catch (Throwable t) {
                ExceptionUtils.handleThrowable(t);
                log.error(sm.getString("standardContext.threadBindingListenerError", getName()), t);
            }
        }
        Thread currentThread = Thread.currentThread();
        if (usePrivilegedAction) {
            PrivilegedAction<Void> pa = new PrivilegedSetTccl(currentThread, originalClassLoader);
            AccessController.doPrivileged(pa);
        } else {
            currentThread.setContextClassLoader(originalClassLoader);
        }
    }

    private String getNamingContextName() {
        if (this.namingContextName == null) {
            Container parent = getParent();
            if (parent == null) {
                this.namingContextName = getName();
            } else {
                Deque<String> stk = new ArrayDeque<>();
                StringBuilder buff = new StringBuilder();
                while (parent != null) {
                    stk.addFirst(parent.getName());
                    parent = parent.getParent();
                }
                while (!stk.isEmpty()) {
                    buff.append('/').append(stk.remove());
                }
                buff.append(getName());
                this.namingContextName = buff.toString();
            }
        }
        return this.namingContextName;
    }

    public NamingContextListener getNamingContextListener() {
        return this.namingContextListener;
    }

    public void setNamingContextListener(NamingContextListener namingContextListener) {
        this.namingContextListener = namingContextListener;
    }

    @Override // org.apache.catalina.Context
    public boolean getPaused() {
        return this.paused;
    }

    @Override // org.apache.catalina.Context
    public boolean fireRequestInitEvent(ServletRequest request) {
        Object[] instances = getApplicationEventListeners();
        if (instances != null && instances.length > 0) {
            ServletRequestEvent event = new ServletRequestEvent(getServletContext(), request);
            for (Object instance : instances) {
                if (instance != null && (instance instanceof ServletRequestListener)) {
                    ServletRequestListener listener = (ServletRequestListener) instance;
                    try {
                        listener.requestInitialized(event);
                    } catch (Throwable t) {
                        ExceptionUtils.handleThrowable(t);
                        getLogger().error(sm.getString("standardContext.requestListener.requestInit", instance.getClass().getName()), t);
                        request.setAttribute("jakarta.servlet.error.exception", t);
                        return false;
                    }
                }
            }
            return true;
        }
        return true;
    }

    @Override // org.apache.catalina.Context
    public boolean fireRequestDestroyEvent(ServletRequest request) {
        Object[] instances = getApplicationEventListeners();
        if (instances != null && instances.length > 0) {
            ServletRequestEvent event = new ServletRequestEvent(getServletContext(), request);
            for (int i = 0; i < instances.length; i++) {
                int j = (instances.length - 1) - i;
                if (instances[j] != null && (instances[j] instanceof ServletRequestListener)) {
                    ServletRequestListener listener = (ServletRequestListener) instances[j];
                    try {
                        listener.requestDestroyed(event);
                    } catch (Throwable t) {
                        ExceptionUtils.handleThrowable(t);
                        getLogger().error(sm.getString("standardContext.requestListener.requestDestroyed", instances[j].getClass().getName()), t);
                        request.setAttribute("jakarta.servlet.error.exception", t);
                        return false;
                    }
                }
            }
            return true;
        }
        return true;
    }

    @Override // org.apache.catalina.Context
    public void addPostConstructMethod(String clazz, String method) {
        if (clazz == null || method == null) {
            throw new IllegalArgumentException(sm.getString("standardContext.postconstruct.required"));
        }
        if (this.postConstructMethods.get(clazz) != null) {
            throw new IllegalArgumentException(sm.getString("standardContext.postconstruct.duplicate", clazz));
        }
        this.postConstructMethods.put(clazz, method);
        fireContainerEvent("addPostConstructMethod", clazz);
    }

    @Override // org.apache.catalina.Context
    public void removePostConstructMethod(String clazz) {
        this.postConstructMethods.remove(clazz);
        fireContainerEvent("removePostConstructMethod", clazz);
    }

    @Override // org.apache.catalina.Context
    public void addPreDestroyMethod(String clazz, String method) {
        if (clazz == null || method == null) {
            throw new IllegalArgumentException(sm.getString("standardContext.predestroy.required"));
        }
        if (this.preDestroyMethods.get(clazz) != null) {
            throw new IllegalArgumentException(sm.getString("standardContext.predestroy.duplicate", clazz));
        }
        this.preDestroyMethods.put(clazz, method);
        fireContainerEvent("addPreDestroyMethod", clazz);
    }

    @Override // org.apache.catalina.Context
    public void removePreDestroyMethod(String clazz) {
        this.preDestroyMethods.remove(clazz);
        fireContainerEvent("removePreDestroyMethod", clazz);
    }

    @Override // org.apache.catalina.Context
    public String findPostConstructMethod(String clazz) {
        return this.postConstructMethods.get(clazz);
    }

    @Override // org.apache.catalina.Context
    public String findPreDestroyMethod(String clazz) {
        return this.preDestroyMethods.get(clazz);
    }

    @Override // org.apache.catalina.Context
    public Map<String, String> findPostConstructMethods() {
        return this.postConstructMethods;
    }

    @Override // org.apache.catalina.Context
    public Map<String, String> findPreDestroyMethods() {
        return this.preDestroyMethods;
    }

    protected void postWorkDirectory() {
        String workDir = getWorkDir();
        if (workDir == null || workDir.length() == 0) {
            String hostName = null;
            String engineName = null;
            String hostWorkDir = null;
            Container parentHost = getParent();
            if (parentHost != null) {
                hostName = parentHost.getName();
                if (parentHost instanceof StandardHost) {
                    hostWorkDir = ((StandardHost) parentHost).getWorkDir();
                }
                Container parentEngine = parentHost.getParent();
                if (parentEngine != null) {
                    engineName = parentEngine.getName();
                }
            }
            if (hostName == null || hostName.length() < 1) {
                hostName = "_";
            }
            if (engineName == null || engineName.length() < 1) {
                engineName = "_";
            }
            String temp = getBaseName();
            if (temp.startsWith("/")) {
                temp = temp.substring(1);
            }
            String temp2 = temp.replace('/', '_').replace('\\', '_');
            if (temp2.length() < 1) {
                temp2 = "ROOT";
            }
            if (hostWorkDir != null) {
                workDir = hostWorkDir + File.separator + temp2;
            } else {
                workDir = "work" + File.separator + engineName + File.separator + hostName + File.separator + temp2;
            }
            setWorkDir(workDir);
        }
        File dir = new File(workDir);
        if (!dir.isAbsolute()) {
            String catalinaHomePath = null;
            try {
                catalinaHomePath = getCatalinaBase().getCanonicalPath();
                dir = new File(catalinaHomePath, workDir);
            } catch (IOException e) {
                log.warn(sm.getString("standardContext.workCreateException", workDir, catalinaHomePath, getName()), e);
            }
        }
        if (!dir.mkdirs() && !dir.isDirectory()) {
            log.warn(sm.getString("standardContext.workCreateFail", dir, getName()));
        }
        if (this.context == null) {
            getServletContext();
        }
        this.context.setAttribute("jakarta.servlet.context.tempdir", dir);
        this.context.setAttributeReadOnly("jakarta.servlet.context.tempdir");
    }

    private void setPaused(boolean paused) {
        this.paused = paused;
    }

    private boolean validateURLPattern(String urlPattern) {
        if (urlPattern == null || urlPattern.indexOf(10) >= 0 || urlPattern.indexOf(13) >= 0) {
            return false;
        }
        if (urlPattern.equals("")) {
            return true;
        }
        if (urlPattern.startsWith("*.")) {
            if (urlPattern.indexOf(47) < 0) {
                checkUnusualURLPattern(urlPattern);
                return true;
            }
            return false;
        }
        if (urlPattern.startsWith("/") && !urlPattern.contains("*.")) {
            checkUnusualURLPattern(urlPattern);
            return true;
        }
        return false;
    }

    private void checkUnusualURLPattern(String urlPattern) {
        if (log.isInfoEnabled()) {
            if ((urlPattern.endsWith("*") && (urlPattern.length() < 2 || urlPattern.charAt(urlPattern.length() - 2) != '/')) || (urlPattern.startsWith("*.") && urlPattern.length() > 2 && urlPattern.lastIndexOf(46) > 1)) {
                log.info(sm.getString("standardContext.suspiciousUrl", urlPattern, getName()));
            }
        }
    }

    @Override // org.apache.catalina.util.LifecycleMBeanBase
    protected String getObjectNameKeyProperties() {
        return "j2eeType=WebModule," + getObjectKeyPropertiesNameOnly() + ",J2EEApplication=" + getJ2EEApplication() + ",J2EEServer=" + getJ2EEServer();
    }

    private String getObjectKeyPropertiesNameOnly() {
        StringBuilder result = new StringBuilder("name=//");
        String hostname = getParent().getName();
        if (hostname == null) {
            result.append("DEFAULT");
        } else {
            result.append(hostname);
        }
        String contextName = getName();
        if (!contextName.startsWith("/")) {
            result.append('/');
        }
        result.append(contextName);
        return result.toString();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.util.LifecycleMBeanBase, org.apache.catalina.util.LifecycleBase
    public void initInternal() throws LifecycleException {
        super.initInternal();
        if (this.namingResources != null) {
            this.namingResources.init();
        }
        if (getObjectName() != null) {
            Notification notification = new Notification("j2ee.object.created", getObjectName(), this.sequenceNumber.getAndIncrement());
            this.broadcaster.sendNotification(notification);
        }
    }

    public void removeNotificationListener(NotificationListener listener, NotificationFilter filter, Object object) throws ListenerNotFoundException {
        this.broadcaster.removeNotificationListener(listener, filter, object);
    }

    public MBeanNotificationInfo[] getNotificationInfo() {
        if (this.notificationInfo == null) {
            this.notificationInfo = new MBeanNotificationInfo[]{new MBeanNotificationInfo(new String[]{"j2ee.object.created"}, Notification.class.getName(), "web application is created"), new MBeanNotificationInfo(new String[]{"j2ee.state.starting"}, Notification.class.getName(), "change web application is starting"), new MBeanNotificationInfo(new String[]{"j2ee.state.running"}, Notification.class.getName(), "web application is running"), new MBeanNotificationInfo(new String[]{"j2ee.state.stopping"}, Notification.class.getName(), "web application start to stopped"), new MBeanNotificationInfo(new String[]{"j2ee.object.stopped"}, Notification.class.getName(), "web application is stopped"), new MBeanNotificationInfo(new String[]{"j2ee.object.deleted"}, Notification.class.getName(), "web application is deleted"), new MBeanNotificationInfo(new String[]{"j2ee.object.failed"}, Notification.class.getName(), "web application failed")};
        }
        return this.notificationInfo;
    }

    public void addNotificationListener(NotificationListener listener, NotificationFilter filter, Object object) throws IllegalArgumentException {
        this.broadcaster.addNotificationListener(listener, filter, object);
    }

    public void removeNotificationListener(NotificationListener listener) throws ListenerNotFoundException {
        this.broadcaster.removeNotificationListener(listener);
    }

    public String[] getWelcomeFiles() {
        return findWelcomeFiles();
    }

    @Override // org.apache.catalina.Context
    public boolean getXmlNamespaceAware() {
        return this.webXmlNamespaceAware;
    }

    @Override // org.apache.catalina.Context
    public void setXmlNamespaceAware(boolean webXmlNamespaceAware) {
        this.webXmlNamespaceAware = webXmlNamespaceAware;
    }

    @Override // org.apache.catalina.Context
    public void setXmlValidation(boolean webXmlValidation) {
        this.webXmlValidation = webXmlValidation;
    }

    @Override // org.apache.catalina.Context
    public boolean getXmlValidation() {
        return this.webXmlValidation;
    }

    @Override // org.apache.catalina.Context
    public void setXmlBlockExternal(boolean xmlBlockExternal) {
        this.xmlBlockExternal = xmlBlockExternal;
    }

    @Override // org.apache.catalina.Context
    public boolean getXmlBlockExternal() {
        return this.xmlBlockExternal;
    }

    @Override // org.apache.catalina.Context
    public void setTldValidation(boolean tldValidation) {
        this.tldValidation = tldValidation;
    }

    @Override // org.apache.catalina.Context
    public boolean getTldValidation() {
        return this.tldValidation;
    }

    public String getServer() {
        return this.server;
    }

    public String setServer(String server) {
        this.server = server;
        return server;
    }

    public long getStartTime() {
        return this.startTime;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/StandardContext$NoPluggabilityServletContext.class */
    public static class NoPluggabilityServletContext implements ServletContext {
        private final ServletContext sc;

        NoPluggabilityServletContext(ServletContext sc) {
            this.sc = sc;
        }

        @Override // jakarta.servlet.ServletContext
        public String getContextPath() {
            return this.sc.getContextPath();
        }

        @Override // jakarta.servlet.ServletContext
        public ServletContext getContext(String uripath) {
            return this.sc.getContext(uripath);
        }

        @Override // jakarta.servlet.ServletContext
        public int getMajorVersion() {
            return this.sc.getMajorVersion();
        }

        @Override // jakarta.servlet.ServletContext
        public int getMinorVersion() {
            return this.sc.getMinorVersion();
        }

        @Override // jakarta.servlet.ServletContext
        public int getEffectiveMajorVersion() {
            return this.sc.getEffectiveMajorVersion();
        }

        @Override // jakarta.servlet.ServletContext
        public int getEffectiveMinorVersion() {
            return this.sc.getEffectiveMinorVersion();
        }

        @Override // jakarta.servlet.ServletContext
        public String getMimeType(String file) {
            return this.sc.getMimeType(file);
        }

        @Override // jakarta.servlet.ServletContext
        public Set<String> getResourcePaths(String path) {
            return this.sc.getResourcePaths(path);
        }

        @Override // jakarta.servlet.ServletContext
        public URL getResource(String path) throws MalformedURLException {
            return this.sc.getResource(path);
        }

        @Override // jakarta.servlet.ServletContext
        public InputStream getResourceAsStream(String path) {
            return this.sc.getResourceAsStream(path);
        }

        @Override // jakarta.servlet.ServletContext
        public RequestDispatcher getRequestDispatcher(String path) {
            return this.sc.getRequestDispatcher(path);
        }

        @Override // jakarta.servlet.ServletContext
        public RequestDispatcher getNamedDispatcher(String name) {
            return this.sc.getNamedDispatcher(name);
        }

        @Override // jakarta.servlet.ServletContext
        public void log(String msg) {
            this.sc.log(msg);
        }

        @Override // jakarta.servlet.ServletContext
        public void log(String message, Throwable throwable) {
            this.sc.log(message, throwable);
        }

        @Override // jakarta.servlet.ServletContext
        public String getRealPath(String path) {
            return this.sc.getRealPath(path);
        }

        @Override // jakarta.servlet.ServletContext
        public String getServerInfo() {
            return this.sc.getServerInfo();
        }

        @Override // jakarta.servlet.ServletContext
        public String getInitParameter(String name) {
            return this.sc.getInitParameter(name);
        }

        @Override // jakarta.servlet.ServletContext
        public Enumeration<String> getInitParameterNames() {
            return this.sc.getInitParameterNames();
        }

        @Override // jakarta.servlet.ServletContext
        public boolean setInitParameter(String name, String value) {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }

        @Override // jakarta.servlet.ServletContext
        public Object getAttribute(String name) {
            return this.sc.getAttribute(name);
        }

        @Override // jakarta.servlet.ServletContext
        public Enumeration<String> getAttributeNames() {
            return this.sc.getAttributeNames();
        }

        @Override // jakarta.servlet.ServletContext
        public void setAttribute(String name, Object object) {
            this.sc.setAttribute(name, object);
        }

        @Override // jakarta.servlet.ServletContext
        public void removeAttribute(String name) {
            this.sc.removeAttribute(name);
        }

        @Override // jakarta.servlet.ServletContext
        public String getServletContextName() {
            return this.sc.getServletContextName();
        }

        @Override // jakarta.servlet.ServletContext
        public ServletRegistration.Dynamic addServlet(String servletName, String className) {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }

        @Override // jakarta.servlet.ServletContext
        public ServletRegistration.Dynamic addServlet(String servletName, Servlet servlet) {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }

        @Override // jakarta.servlet.ServletContext
        public ServletRegistration.Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass) {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }

        @Override // jakarta.servlet.ServletContext
        public ServletRegistration.Dynamic addJspFile(String jspName, String jspFile) {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }

        @Override // jakarta.servlet.ServletContext
        public <T extends Servlet> T createServlet(Class<T> c) throws ServletException {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }

        @Override // jakarta.servlet.ServletContext
        public ServletRegistration getServletRegistration(String servletName) {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }

        @Override // jakarta.servlet.ServletContext
        public Map<String, ? extends ServletRegistration> getServletRegistrations() {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }

        @Override // jakarta.servlet.ServletContext
        public FilterRegistration.Dynamic addFilter(String filterName, String className) {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }

        @Override // jakarta.servlet.ServletContext
        public FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }

        @Override // jakarta.servlet.ServletContext
        public FilterRegistration.Dynamic addFilter(String filterName, Class<? extends Filter> filterClass) {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }

        @Override // jakarta.servlet.ServletContext
        public <T extends Filter> T createFilter(Class<T> c) throws ServletException {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }

        @Override // jakarta.servlet.ServletContext
        public FilterRegistration getFilterRegistration(String filterName) {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }

        @Override // jakarta.servlet.ServletContext
        public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }

        @Override // jakarta.servlet.ServletContext
        public SessionCookieConfig getSessionCookieConfig() {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }

        @Override // jakarta.servlet.ServletContext
        public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }

        @Override // jakarta.servlet.ServletContext
        public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
            return this.sc.getDefaultSessionTrackingModes();
        }

        @Override // jakarta.servlet.ServletContext
        public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
            return this.sc.getEffectiveSessionTrackingModes();
        }

        @Override // jakarta.servlet.ServletContext
        public void addListener(String className) {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }

        @Override // jakarta.servlet.ServletContext
        public <T extends EventListener> void addListener(T t) {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }

        @Override // jakarta.servlet.ServletContext
        public void addListener(Class<? extends EventListener> listenerClass) {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }

        @Override // jakarta.servlet.ServletContext
        public <T extends EventListener> T createListener(Class<T> c) throws ServletException {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }

        @Override // jakarta.servlet.ServletContext
        public JspConfigDescriptor getJspConfigDescriptor() {
            return this.sc.getJspConfigDescriptor();
        }

        @Override // jakarta.servlet.ServletContext
        public ClassLoader getClassLoader() {
            return this.sc.getClassLoader();
        }

        @Override // jakarta.servlet.ServletContext
        public void declareRoles(String... roleNames) {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }

        @Override // jakarta.servlet.ServletContext
        public String getVirtualServerName() {
            return this.sc.getVirtualServerName();
        }

        @Override // jakarta.servlet.ServletContext
        public int getSessionTimeout() {
            return this.sc.getSessionTimeout();
        }

        @Override // jakarta.servlet.ServletContext
        public void setSessionTimeout(int sessionTimeout) {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }

        @Override // jakarta.servlet.ServletContext
        public String getRequestCharacterEncoding() {
            return this.sc.getRequestCharacterEncoding();
        }

        @Override // jakarta.servlet.ServletContext
        public void setRequestCharacterEncoding(String encoding) {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }

        @Override // jakarta.servlet.ServletContext
        public String getResponseCharacterEncoding() {
            return this.sc.getResponseCharacterEncoding();
        }

        @Override // jakarta.servlet.ServletContext
        public void setResponseCharacterEncoding(String encoding) {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }
    }
}

package org.springframework.web.servlet;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.SourceFilteringListener;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.i18n.SimpleLocaleContext;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ConfigurableWebEnvironment;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.async.CallableProcessingInterceptor;
import org.springframework.web.context.request.async.WebAsyncManager;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.context.support.ServletRequestHandledEvent;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.support.WebContentGenerator;
import org.springframework.web.util.WebUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/FrameworkServlet.class */
public abstract class FrameworkServlet extends HttpServletBean implements ApplicationContextAware {
    public static final String DEFAULT_NAMESPACE_SUFFIX = "-servlet";
    private static final String INIT_PARAM_DELIMITERS = ",; \t\n";

    @Nullable
    private String contextAttribute;

    @Nullable
    private String contextId;

    @Nullable
    private String namespace;

    @Nullable
    private String contextConfigLocation;

    @Nullable
    private String contextInitializerClasses;

    @Nullable
    private WebApplicationContext webApplicationContext;
    private volatile boolean refreshEventReceived;
    public static final Class<?> DEFAULT_CONTEXT_CLASS = XmlWebApplicationContext.class;
    public static final String SERVLET_CONTEXT_PREFIX = FrameworkServlet.class.getName() + ".CONTEXT.";
    private static final Set<String> HTTP_SERVLET_METHODS = Set.of("DELETE", WebContentGenerator.METHOD_HEAD, "GET", "OPTIONS", WebContentGenerator.METHOD_POST, "PUT", "TRACE");
    private Class<?> contextClass = DEFAULT_CONTEXT_CLASS;
    private final List<ApplicationContextInitializer<ConfigurableApplicationContext>> contextInitializers = new ArrayList();
    private boolean publishContext = true;
    private boolean publishEvents = true;
    private boolean threadContextInheritable = false;
    private boolean dispatchOptionsRequest = false;
    private boolean dispatchTraceRequest = false;
    private boolean enableLoggingRequestDetails = false;
    private boolean webApplicationContextInjected = false;
    private final Object onRefreshMonitor = new Object();

    protected abstract void doService(HttpServletRequest request, HttpServletResponse response) throws Exception;

    public FrameworkServlet() {
    }

    public FrameworkServlet(WebApplicationContext webApplicationContext) {
        this.webApplicationContext = webApplicationContext;
    }

    public void setContextAttribute(@Nullable String contextAttribute) {
        this.contextAttribute = contextAttribute;
    }

    @Nullable
    public String getContextAttribute() {
        return this.contextAttribute;
    }

    public void setContextClass(Class<?> contextClass) {
        this.contextClass = contextClass;
    }

    public Class<?> getContextClass() {
        return this.contextClass;
    }

    public void setContextId(@Nullable String contextId) {
        this.contextId = contextId;
    }

    @Nullable
    public String getContextId() {
        return this.contextId;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getNamespace() {
        return this.namespace != null ? this.namespace : getServletName() + "-servlet";
    }

    public void setContextConfigLocation(@Nullable String contextConfigLocation) {
        this.contextConfigLocation = contextConfigLocation;
    }

    @Nullable
    public String getContextConfigLocation() {
        return this.contextConfigLocation;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void setContextInitializers(@Nullable ApplicationContextInitializer<?>... initializers) {
        if (initializers != null) {
            for (ApplicationContextInitializer<?> initializer : initializers) {
                this.contextInitializers.add(initializer);
            }
        }
    }

    public void setContextInitializerClasses(String contextInitializerClasses) {
        this.contextInitializerClasses = contextInitializerClasses;
    }

    public void setPublishContext(boolean publishContext) {
        this.publishContext = publishContext;
    }

    public void setPublishEvents(boolean publishEvents) {
        this.publishEvents = publishEvents;
    }

    public void setThreadContextInheritable(boolean threadContextInheritable) {
        this.threadContextInheritable = threadContextInheritable;
    }

    public void setDispatchOptionsRequest(boolean dispatchOptionsRequest) {
        this.dispatchOptionsRequest = dispatchOptionsRequest;
    }

    public void setDispatchTraceRequest(boolean dispatchTraceRequest) {
        this.dispatchTraceRequest = dispatchTraceRequest;
    }

    public void setEnableLoggingRequestDetails(boolean enable) {
        this.enableLoggingRequestDetails = enable;
    }

    public boolean isEnableLoggingRequestDetails() {
        return this.enableLoggingRequestDetails;
    }

    @Override // org.springframework.context.ApplicationContextAware
    public void setApplicationContext(ApplicationContext applicationContext) {
        if (this.webApplicationContext == null && (applicationContext instanceof WebApplicationContext)) {
            WebApplicationContext wac = (WebApplicationContext) applicationContext;
            this.webApplicationContext = wac;
            this.webApplicationContextInjected = true;
        }
    }

    @Override // org.springframework.web.servlet.HttpServletBean
    protected final void initServletBean() throws ServletException {
        String str;
        getServletContext().log("Initializing Spring " + getClass().getSimpleName() + " '" + getServletName() + "'");
        if (this.logger.isInfoEnabled()) {
            this.logger.info("Initializing Servlet '" + getServletName() + "'");
        }
        long startTime = System.currentTimeMillis();
        try {
            this.webApplicationContext = initWebApplicationContext();
            initFrameworkServlet();
            if (this.logger.isDebugEnabled()) {
                if (this.enableLoggingRequestDetails) {
                    str = "shown which may lead to unsafe logging of potentially sensitive data";
                } else {
                    str = "masked to prevent unsafe logging of potentially sensitive data";
                }
                String value = str;
                this.logger.debug("enableLoggingRequestDetails='" + this.enableLoggingRequestDetails + "': request parameters and headers will be " + value);
            }
            if (this.logger.isInfoEnabled()) {
                this.logger.info("Completed initialization in " + (System.currentTimeMillis() - startTime) + " ms");
            }
        } catch (ServletException | RuntimeException ex) {
            this.logger.error("Context initialization failed", ex);
            throw ex;
        }
    }

    protected WebApplicationContext initWebApplicationContext() {
        WebApplicationContext rootContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        WebApplicationContext wac = null;
        if (this.webApplicationContext != null) {
            wac = this.webApplicationContext;
            if (wac instanceof ConfigurableWebApplicationContext) {
                ConfigurableWebApplicationContext cwac = (ConfigurableWebApplicationContext) wac;
                if (!cwac.isActive()) {
                    if (cwac.getParent() == null) {
                        cwac.setParent(rootContext);
                    }
                    configureAndRefreshWebApplicationContext(cwac);
                }
            }
        }
        if (wac == null) {
            wac = findWebApplicationContext();
        }
        if (wac == null) {
            wac = createWebApplicationContext(rootContext);
        }
        if (!this.refreshEventReceived) {
            synchronized (this.onRefreshMonitor) {
                onRefresh(wac);
            }
        }
        if (this.publishContext) {
            String attrName = getServletContextAttributeName();
            getServletContext().setAttribute(attrName, wac);
        }
        return wac;
    }

    @Nullable
    protected WebApplicationContext findWebApplicationContext() {
        String attrName = getContextAttribute();
        if (attrName == null) {
            return null;
        }
        WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(getServletContext(), attrName);
        if (wac == null) {
            throw new IllegalStateException("No WebApplicationContext found: initializer not registered?");
        }
        return wac;
    }

    protected WebApplicationContext createWebApplicationContext(@Nullable ApplicationContext parent) {
        Class<?> contextClass = getContextClass();
        if (!ConfigurableWebApplicationContext.class.isAssignableFrom(contextClass)) {
            throw new ApplicationContextException("Fatal initialization error in servlet with name '" + getServletName() + "': custom WebApplicationContext class [" + contextClass.getName() + "] is not of type ConfigurableWebApplicationContext");
        }
        ConfigurableWebApplicationContext wac = (ConfigurableWebApplicationContext) BeanUtils.instantiateClass(contextClass);
        wac.setEnvironment(getEnvironment());
        wac.setParent(parent);
        String configLocation = getContextConfigLocation();
        if (configLocation != null) {
            wac.setConfigLocation(configLocation);
        }
        configureAndRefreshWebApplicationContext(wac);
        return wac;
    }

    protected void configureAndRefreshWebApplicationContext(ConfigurableWebApplicationContext wac) {
        if (ObjectUtils.identityToString(wac).equals(wac.getId())) {
            if (this.contextId != null) {
                wac.setId(this.contextId);
            } else {
                wac.setId(ConfigurableWebApplicationContext.APPLICATION_CONTEXT_ID_PREFIX + ObjectUtils.getDisplayString(getServletContext().getContextPath()) + "/" + getServletName());
            }
        }
        wac.setServletContext(getServletContext());
        wac.setServletConfig(getServletConfig());
        wac.setNamespace(getNamespace());
        wac.addApplicationListener(new SourceFilteringListener(wac, new ContextRefreshListener()));
        ConfigurableEnvironment env = wac.getEnvironment();
        if (env instanceof ConfigurableWebEnvironment) {
            ConfigurableWebEnvironment cwe = (ConfigurableWebEnvironment) env;
            cwe.initPropertySources(getServletContext(), getServletConfig());
        }
        postProcessWebApplicationContext(wac);
        applyInitializers(wac);
        wac.refresh();
    }

    protected WebApplicationContext createWebApplicationContext(@Nullable WebApplicationContext parent) {
        return createWebApplicationContext((ApplicationContext) parent);
    }

    protected void postProcessWebApplicationContext(ConfigurableWebApplicationContext wac) {
    }

    protected void applyInitializers(ConfigurableApplicationContext wac) {
        String globalClassNames = getServletContext().getInitParameter(ContextLoader.GLOBAL_INITIALIZER_CLASSES_PARAM);
        if (globalClassNames != null) {
            for (String className : StringUtils.tokenizeToStringArray(globalClassNames, ",; \t\n")) {
                this.contextInitializers.add(loadInitializer(className, wac));
            }
        }
        if (this.contextInitializerClasses != null) {
            for (String className2 : StringUtils.tokenizeToStringArray(this.contextInitializerClasses, ",; \t\n")) {
                this.contextInitializers.add(loadInitializer(className2, wac));
            }
        }
        AnnotationAwareOrderComparator.sort(this.contextInitializers);
        for (ApplicationContextInitializer<ConfigurableApplicationContext> initializer : this.contextInitializers) {
            initializer.initialize(wac);
        }
    }

    private ApplicationContextInitializer<ConfigurableApplicationContext> loadInitializer(String className, ConfigurableApplicationContext wac) {
        try {
            Class<?> initializerClass = ClassUtils.forName(className, wac.getClassLoader());
            Class<?> initializerContextClass = GenericTypeResolver.resolveTypeArgument(initializerClass, ApplicationContextInitializer.class);
            if (initializerContextClass != null && !initializerContextClass.isInstance(wac)) {
                throw new ApplicationContextException(String.format("Could not apply context initializer [%s] since its generic parameter [%s] is not assignable from the type of application context used by this framework servlet: [%s]", initializerClass.getName(), initializerContextClass.getName(), wac.getClass().getName()));
            }
            return (ApplicationContextInitializer) BeanUtils.instantiateClass(initializerClass, ApplicationContextInitializer.class);
        } catch (ClassNotFoundException ex) {
            throw new ApplicationContextException(String.format("Could not load class [%s] specified via 'contextInitializerClasses' init-param", className), ex);
        }
    }

    public String getServletContextAttributeName() {
        return SERVLET_CONTEXT_PREFIX + getServletName();
    }

    @Nullable
    public final WebApplicationContext getWebApplicationContext() {
        return this.webApplicationContext;
    }

    protected void initFrameworkServlet() throws ServletException {
    }

    public void refresh() {
        WebApplicationContext wac = getWebApplicationContext();
        if (!(wac instanceof ConfigurableApplicationContext)) {
            throw new IllegalStateException("WebApplicationContext does not support refresh: " + wac);
        }
        ConfigurableApplicationContext cac = (ConfigurableApplicationContext) wac;
        cac.refresh();
    }

    public void onApplicationEvent(ContextRefreshedEvent event) {
        this.refreshEventReceived = true;
        synchronized (this.onRefreshMonitor) {
            onRefresh(event.getApplicationContext());
        }
    }

    protected void onRefresh(ApplicationContext context) {
    }

    @Override // jakarta.servlet.GenericServlet, jakarta.servlet.Servlet
    public void destroy() {
        getServletContext().log("Destroying Spring FrameworkServlet '" + getServletName() + "'");
        if (!this.webApplicationContextInjected) {
            WebApplicationContext webApplicationContext = this.webApplicationContext;
            if (webApplicationContext instanceof ConfigurableApplicationContext) {
                ConfigurableApplicationContext cac = (ConfigurableApplicationContext) webApplicationContext;
                cac.close();
            }
        }
    }

    @Override // jakarta.servlet.http.HttpServlet
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (HTTP_SERVLET_METHODS.contains(request.getMethod())) {
            super.service(request, response);
        } else {
            processRequest(request, response);
        }
    }

    @Override // jakarta.servlet.http.HttpServlet
    protected final void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override // jakarta.servlet.http.HttpServlet
    protected final void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override // jakarta.servlet.http.HttpServlet
    protected final void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override // jakarta.servlet.http.HttpServlet
    protected final void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override // jakarta.servlet.http.HttpServlet
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (this.dispatchOptionsRequest || CorsUtils.isPreFlightRequest(request)) {
            processRequest(request, response);
            if (response.containsHeader(HttpHeaders.ALLOW)) {
                return;
            }
        }
        super.doOptions(request, new HttpServletResponseWrapper(response) { // from class: org.springframework.web.servlet.FrameworkServlet.1
            @Override // jakarta.servlet.http.HttpServletResponseWrapper, jakarta.servlet.http.HttpServletResponse
            public void setHeader(String name, String value) {
                if (HttpHeaders.ALLOW.equals(name)) {
                    value = (StringUtils.hasLength(value) ? value + ", " : "") + HttpMethod.PATCH.name();
                }
                super.setHeader(name, value);
            }
        });
    }

    @Override // jakarta.servlet.http.HttpServlet
    protected void doTrace(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (this.dispatchTraceRequest) {
            processRequest(request, response);
            if ("message/http".equals(response.getContentType())) {
                return;
            }
        }
        if (request.getDispatcherType() != DispatcherType.ERROR) {
            super.doTrace(request, response);
        }
    }

    protected final void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        LocaleContext previousLocaleContext = LocaleContextHolder.getLocaleContext();
        LocaleContext localeContext = buildLocaleContext(request);
        RequestAttributes previousAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes requestAttributes = buildRequestAttributes(request, response, previousAttributes);
        WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(request);
        asyncManager.registerCallableInterceptor(FrameworkServlet.class.getName(), new RequestBindingInterceptor());
        initContextHolders(request, localeContext, requestAttributes);
        try {
            try {
                doService(request, response);
                resetContextHolders(request, previousLocaleContext, previousAttributes);
                if (requestAttributes != null) {
                    requestAttributes.requestCompleted();
                }
                logResult(request, response, null, asyncManager);
                publishRequestHandledEvent(request, response, startTime, null);
            } catch (ServletException | IOException ex) {
                throw ex;
            } catch (Throwable ex2) {
                throw new ServletException("Request processing failed: " + ex2, ex2);
            }
        } catch (Throwable th) {
            resetContextHolders(request, previousLocaleContext, previousAttributes);
            if (requestAttributes != null) {
                requestAttributes.requestCompleted();
            }
            logResult(request, response, null, asyncManager);
            publishRequestHandledEvent(request, response, startTime, null);
            throw th;
        }
    }

    @Nullable
    protected LocaleContext buildLocaleContext(HttpServletRequest request) {
        return new SimpleLocaleContext(request.getLocale());
    }

    @Nullable
    protected ServletRequestAttributes buildRequestAttributes(HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable RequestAttributes previousAttributes) {
        if (previousAttributes == null || (previousAttributes instanceof ServletRequestAttributes)) {
            return new ServletRequestAttributes(request, response);
        }
        return null;
    }

    private void initContextHolders(HttpServletRequest request, @Nullable LocaleContext localeContext, @Nullable RequestAttributes requestAttributes) {
        if (localeContext != null) {
            LocaleContextHolder.setLocaleContext(localeContext, this.threadContextInheritable);
        }
        if (requestAttributes != null) {
            RequestContextHolder.setRequestAttributes(requestAttributes, this.threadContextInheritable);
        }
    }

    private void resetContextHolders(HttpServletRequest request, @Nullable LocaleContext prevLocaleContext, @Nullable RequestAttributes previousAttributes) {
        LocaleContextHolder.setLocaleContext(prevLocaleContext, this.threadContextInheritable);
        RequestContextHolder.setRequestAttributes(previousAttributes, this.threadContextInheritable);
    }

    private void logResult(HttpServletRequest request, HttpServletResponse response, @Nullable Throwable failureCause, WebAsyncManager asyncManager) {
        String headers;
        if (!this.logger.isDebugEnabled()) {
            return;
        }
        DispatcherType dispatchType = request.getDispatcherType();
        boolean initialDispatch = dispatchType == DispatcherType.REQUEST;
        if (failureCause != null) {
            if (!initialDispatch) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Unresolved failure from \"" + dispatchType + "\" dispatch: " + failureCause);
                    return;
                }
                return;
            } else if (this.logger.isTraceEnabled()) {
                this.logger.trace("Failed to complete request", failureCause);
                return;
            } else {
                this.logger.debug("Failed to complete request: " + failureCause);
                return;
            }
        }
        if (asyncManager.isConcurrentHandlingStarted()) {
            this.logger.debug("Exiting but response remains open for further handling");
            return;
        }
        int status = response.getStatus();
        String headers2 = "";
        if (this.logger.isTraceEnabled()) {
            Collection<String> names = response.getHeaderNames();
            if (this.enableLoggingRequestDetails) {
                headers = (String) names.stream().map(name -> {
                    return name + ":" + response.getHeaders(name);
                }).collect(Collectors.joining(", "));
            } else {
                headers = names.isEmpty() ? "" : "masked";
            }
            headers2 = ", headers={" + headers + "}";
        }
        if (!initialDispatch) {
            this.logger.debug("Exiting from \"" + dispatchType + "\" dispatch, status " + status + headers2);
        } else {
            this.logger.debug("Completed " + HttpStatusCode.valueOf(status) + headers2);
        }
    }

    private void publishRequestHandledEvent(HttpServletRequest request, HttpServletResponse response, long startTime, @Nullable Throwable failureCause) {
        if (this.publishEvents && this.webApplicationContext != null) {
            long processingTime = System.currentTimeMillis() - startTime;
            this.webApplicationContext.publishEvent((ApplicationEvent) new ServletRequestHandledEvent(this, request.getRequestURI(), request.getRemoteAddr(), request.getMethod(), getServletConfig().getServletName(), WebUtils.getSessionId(request), getUsernameForRequest(request), processingTime, failureCause, response.getStatus()));
        }
    }

    @Nullable
    protected String getUsernameForRequest(HttpServletRequest request) {
        Principal userPrincipal = request.getUserPrincipal();
        if (userPrincipal != null) {
            return userPrincipal.getName();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/FrameworkServlet$ContextRefreshListener.class */
    public class ContextRefreshListener implements ApplicationListener<ContextRefreshedEvent> {
        private ContextRefreshListener() {
        }

        @Override // org.springframework.context.ApplicationListener
        public void onApplicationEvent(ContextRefreshedEvent event) {
            FrameworkServlet.this.onApplicationEvent(event);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/FrameworkServlet$RequestBindingInterceptor.class */
    public class RequestBindingInterceptor implements CallableProcessingInterceptor {
        private RequestBindingInterceptor() {
        }

        @Override // org.springframework.web.context.request.async.CallableProcessingInterceptor
        public <T> void preProcess(NativeWebRequest webRequest, Callable<T> task) {
            HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest(HttpServletRequest.class);
            if (request != null) {
                HttpServletResponse response = (HttpServletResponse) webRequest.getNativeResponse(HttpServletResponse.class);
                FrameworkServlet.this.initContextHolders(request, FrameworkServlet.this.buildLocaleContext(request), FrameworkServlet.this.buildRequestAttributes(request, response, null));
            }
        }

        @Override // org.springframework.web.context.request.async.CallableProcessingInterceptor
        public <T> void postProcess(NativeWebRequest webRequest, Callable<T> task, Object concurrentResult) {
            HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest(HttpServletRequest.class);
            if (request != null) {
                FrameworkServlet.this.resetContextHolders(request, null, null);
            }
        }
    }
}

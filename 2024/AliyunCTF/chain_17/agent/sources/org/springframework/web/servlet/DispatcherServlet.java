package org.springframework.web.servlet;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tomcat.util.http.fileupload.FileUploadBase;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.ui.context.ThemeSource;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.async.WebAsyncManager;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.util.ServletRequestPathUtils;
import org.springframework.web.util.WebUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/DispatcherServlet.class */
public class DispatcherServlet extends FrameworkServlet {
    public static final String MULTIPART_RESOLVER_BEAN_NAME = "multipartResolver";
    public static final String LOCALE_RESOLVER_BEAN_NAME = "localeResolver";

    @Deprecated
    public static final String THEME_RESOLVER_BEAN_NAME = "themeResolver";
    public static final String HANDLER_MAPPING_BEAN_NAME = "handlerMapping";
    public static final String HANDLER_ADAPTER_BEAN_NAME = "handlerAdapter";
    public static final String HANDLER_EXCEPTION_RESOLVER_BEAN_NAME = "handlerExceptionResolver";
    public static final String REQUEST_TO_VIEW_NAME_TRANSLATOR_BEAN_NAME = "viewNameTranslator";
    public static final String VIEW_RESOLVER_BEAN_NAME = "viewResolver";
    public static final String FLASH_MAP_MANAGER_BEAN_NAME = "flashMapManager";
    public static final String PAGE_NOT_FOUND_LOG_CATEGORY = "org.springframework.web.servlet.PageNotFound";
    private static final String DEFAULT_STRATEGIES_PATH = "DispatcherServlet.properties";
    private static final String DEFAULT_STRATEGIES_PREFIX = "org.springframework.web.servlet";

    @Nullable
    private static Properties defaultStrategies;
    private boolean detectAllHandlerMappings;
    private boolean detectAllHandlerAdapters;
    private boolean detectAllHandlerExceptionResolvers;
    private boolean detectAllViewResolvers;
    private boolean throwExceptionIfNoHandlerFound;
    private boolean cleanupAfterInclude;

    @Nullable
    private MultipartResolver multipartResolver;

    @Nullable
    private LocaleResolver localeResolver;

    @Nullable
    @Deprecated
    private ThemeResolver themeResolver;

    @Nullable
    private List<HandlerMapping> handlerMappings;

    @Nullable
    private List<HandlerAdapter> handlerAdapters;

    @Nullable
    private List<HandlerExceptionResolver> handlerExceptionResolvers;

    @Nullable
    private RequestToViewNameTranslator viewNameTranslator;

    @Nullable
    private FlashMapManager flashMapManager;

    @Nullable
    private List<ViewResolver> viewResolvers;
    private boolean parseRequestPath;
    public static final String WEB_APPLICATION_CONTEXT_ATTRIBUTE = DispatcherServlet.class.getName() + ".CONTEXT";
    public static final String LOCALE_RESOLVER_ATTRIBUTE = DispatcherServlet.class.getName() + ".LOCALE_RESOLVER";

    @Deprecated
    public static final String THEME_RESOLVER_ATTRIBUTE = DispatcherServlet.class.getName() + ".THEME_RESOLVER";

    @Deprecated
    public static final String THEME_SOURCE_ATTRIBUTE = DispatcherServlet.class.getName() + ".THEME_SOURCE";
    public static final String INPUT_FLASH_MAP_ATTRIBUTE = DispatcherServlet.class.getName() + ".INPUT_FLASH_MAP";
    public static final String OUTPUT_FLASH_MAP_ATTRIBUTE = DispatcherServlet.class.getName() + ".OUTPUT_FLASH_MAP";
    public static final String FLASH_MAP_MANAGER_ATTRIBUTE = DispatcherServlet.class.getName() + ".FLASH_MAP_MANAGER";
    public static final String EXCEPTION_ATTRIBUTE = DispatcherServlet.class.getName() + ".EXCEPTION";
    protected static final Log pageNotFoundLogger = LogFactory.getLog("org.springframework.web.servlet.PageNotFound");

    public DispatcherServlet() {
        this.detectAllHandlerMappings = true;
        this.detectAllHandlerAdapters = true;
        this.detectAllHandlerExceptionResolvers = true;
        this.detectAllViewResolvers = true;
        this.throwExceptionIfNoHandlerFound = true;
        this.cleanupAfterInclude = true;
        setDispatchOptionsRequest(true);
    }

    public DispatcherServlet(WebApplicationContext webApplicationContext) {
        super(webApplicationContext);
        this.detectAllHandlerMappings = true;
        this.detectAllHandlerAdapters = true;
        this.detectAllHandlerExceptionResolvers = true;
        this.detectAllViewResolvers = true;
        this.throwExceptionIfNoHandlerFound = true;
        this.cleanupAfterInclude = true;
        setDispatchOptionsRequest(true);
    }

    public void setDetectAllHandlerMappings(boolean detectAllHandlerMappings) {
        this.detectAllHandlerMappings = detectAllHandlerMappings;
    }

    public void setDetectAllHandlerAdapters(boolean detectAllHandlerAdapters) {
        this.detectAllHandlerAdapters = detectAllHandlerAdapters;
    }

    public void setDetectAllHandlerExceptionResolvers(boolean detectAllHandlerExceptionResolvers) {
        this.detectAllHandlerExceptionResolvers = detectAllHandlerExceptionResolvers;
    }

    public void setDetectAllViewResolvers(boolean detectAllViewResolvers) {
        this.detectAllViewResolvers = detectAllViewResolvers;
    }

    @Deprecated(since = "6.1", forRemoval = true)
    public void setThrowExceptionIfNoHandlerFound(boolean throwExceptionIfNoHandlerFound) {
        this.throwExceptionIfNoHandlerFound = throwExceptionIfNoHandlerFound;
    }

    public void setCleanupAfterInclude(boolean cleanupAfterInclude) {
        this.cleanupAfterInclude = cleanupAfterInclude;
    }

    @Override // org.springframework.web.servlet.FrameworkServlet
    protected void onRefresh(ApplicationContext context) {
        initStrategies(context);
    }

    protected void initStrategies(ApplicationContext context) {
        initMultipartResolver(context);
        initLocaleResolver(context);
        initThemeResolver(context);
        initHandlerMappings(context);
        initHandlerAdapters(context);
        initHandlerExceptionResolvers(context);
        initRequestToViewNameTranslator(context);
        initViewResolvers(context);
        initFlashMapManager(context);
    }

    private void initMultipartResolver(ApplicationContext context) {
        try {
            this.multipartResolver = (MultipartResolver) context.getBean(MULTIPART_RESOLVER_BEAN_NAME, MultipartResolver.class);
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Detected " + this.multipartResolver);
            } else if (this.logger.isDebugEnabled()) {
                this.logger.debug("Detected " + this.multipartResolver.getClass().getSimpleName());
            }
        } catch (NoSuchBeanDefinitionException e) {
            this.multipartResolver = null;
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("No MultipartResolver 'multipartResolver' declared");
            }
        }
    }

    private void initLocaleResolver(ApplicationContext context) {
        try {
            this.localeResolver = (LocaleResolver) context.getBean(LOCALE_RESOLVER_BEAN_NAME, LocaleResolver.class);
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Detected " + this.localeResolver);
            } else if (this.logger.isDebugEnabled()) {
                this.logger.debug("Detected " + this.localeResolver.getClass().getSimpleName());
            }
        } catch (NoSuchBeanDefinitionException e) {
            this.localeResolver = (LocaleResolver) getDefaultStrategy(context, LocaleResolver.class);
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("No LocaleResolver 'localeResolver': using default [" + this.localeResolver.getClass().getSimpleName() + "]");
            }
        }
    }

    @Deprecated
    private void initThemeResolver(ApplicationContext context) {
        try {
            this.themeResolver = (ThemeResolver) context.getBean(THEME_RESOLVER_BEAN_NAME, ThemeResolver.class);
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Detected " + this.themeResolver);
            } else if (this.logger.isDebugEnabled()) {
                this.logger.debug("Detected " + this.themeResolver.getClass().getSimpleName());
            }
        } catch (NoSuchBeanDefinitionException e) {
            this.themeResolver = (ThemeResolver) getDefaultStrategy(context, ThemeResolver.class);
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("No ThemeResolver 'themeResolver': using default [" + this.themeResolver.getClass().getSimpleName() + "]");
            }
        }
    }

    private void initHandlerMappings(ApplicationContext context) {
        this.handlerMappings = null;
        if (this.detectAllHandlerMappings) {
            Map<String, HandlerMapping> matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(context, HandlerMapping.class, true, false);
            if (!matchingBeans.isEmpty()) {
                this.handlerMappings = new ArrayList(matchingBeans.values());
                AnnotationAwareOrderComparator.sort(this.handlerMappings);
            }
        } else {
            try {
                HandlerMapping hm = (HandlerMapping) context.getBean(HANDLER_MAPPING_BEAN_NAME, HandlerMapping.class);
                this.handlerMappings = Collections.singletonList(hm);
            } catch (NoSuchBeanDefinitionException e) {
            }
        }
        if (this.handlerMappings == null) {
            this.handlerMappings = getDefaultStrategies(context, HandlerMapping.class);
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("No HandlerMappings declared for servlet '" + getServletName() + "': using default strategies from DispatcherServlet.properties");
            }
        }
        for (HandlerMapping mapping : this.handlerMappings) {
            if (mapping.usesPathPatterns()) {
                this.parseRequestPath = true;
                return;
            }
        }
    }

    private void initHandlerAdapters(ApplicationContext context) {
        this.handlerAdapters = null;
        if (this.detectAllHandlerAdapters) {
            Map<String, HandlerAdapter> matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(context, HandlerAdapter.class, true, false);
            if (!matchingBeans.isEmpty()) {
                this.handlerAdapters = new ArrayList(matchingBeans.values());
                AnnotationAwareOrderComparator.sort(this.handlerAdapters);
            }
        } else {
            try {
                HandlerAdapter ha = (HandlerAdapter) context.getBean(HANDLER_ADAPTER_BEAN_NAME, HandlerAdapter.class);
                this.handlerAdapters = Collections.singletonList(ha);
            } catch (NoSuchBeanDefinitionException e) {
            }
        }
        if (this.handlerAdapters == null) {
            this.handlerAdapters = getDefaultStrategies(context, HandlerAdapter.class);
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("No HandlerAdapters declared for servlet '" + getServletName() + "': using default strategies from DispatcherServlet.properties");
            }
        }
    }

    private void initHandlerExceptionResolvers(ApplicationContext context) {
        this.handlerExceptionResolvers = null;
        if (this.detectAllHandlerExceptionResolvers) {
            Map<String, HandlerExceptionResolver> matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(context, HandlerExceptionResolver.class, true, false);
            if (!matchingBeans.isEmpty()) {
                this.handlerExceptionResolvers = new ArrayList(matchingBeans.values());
                AnnotationAwareOrderComparator.sort(this.handlerExceptionResolvers);
            }
        } else {
            try {
                HandlerExceptionResolver her = (HandlerExceptionResolver) context.getBean(HANDLER_EXCEPTION_RESOLVER_BEAN_NAME, HandlerExceptionResolver.class);
                this.handlerExceptionResolvers = Collections.singletonList(her);
            } catch (NoSuchBeanDefinitionException e) {
            }
        }
        if (this.handlerExceptionResolvers == null) {
            this.handlerExceptionResolvers = getDefaultStrategies(context, HandlerExceptionResolver.class);
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("No HandlerExceptionResolvers declared in servlet '" + getServletName() + "': using default strategies from DispatcherServlet.properties");
            }
        }
    }

    private void initRequestToViewNameTranslator(ApplicationContext context) {
        try {
            this.viewNameTranslator = (RequestToViewNameTranslator) context.getBean(REQUEST_TO_VIEW_NAME_TRANSLATOR_BEAN_NAME, RequestToViewNameTranslator.class);
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Detected " + this.viewNameTranslator.getClass().getSimpleName());
            } else if (this.logger.isDebugEnabled()) {
                this.logger.debug("Detected " + this.viewNameTranslator);
            }
        } catch (NoSuchBeanDefinitionException e) {
            this.viewNameTranslator = (RequestToViewNameTranslator) getDefaultStrategy(context, RequestToViewNameTranslator.class);
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("No RequestToViewNameTranslator 'viewNameTranslator': using default [" + this.viewNameTranslator.getClass().getSimpleName() + "]");
            }
        }
    }

    private void initViewResolvers(ApplicationContext context) {
        this.viewResolvers = null;
        if (this.detectAllViewResolvers) {
            Map<String, ViewResolver> matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(context, ViewResolver.class, true, false);
            if (!matchingBeans.isEmpty()) {
                this.viewResolvers = new ArrayList(matchingBeans.values());
                AnnotationAwareOrderComparator.sort(this.viewResolvers);
            }
        } else {
            try {
                ViewResolver vr = (ViewResolver) context.getBean(VIEW_RESOLVER_BEAN_NAME, ViewResolver.class);
                this.viewResolvers = Collections.singletonList(vr);
            } catch (NoSuchBeanDefinitionException e) {
            }
        }
        if (this.viewResolvers == null) {
            this.viewResolvers = getDefaultStrategies(context, ViewResolver.class);
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("No ViewResolvers declared for servlet '" + getServletName() + "': using default strategies from DispatcherServlet.properties");
            }
        }
    }

    private void initFlashMapManager(ApplicationContext context) {
        try {
            this.flashMapManager = (FlashMapManager) context.getBean(FLASH_MAP_MANAGER_BEAN_NAME, FlashMapManager.class);
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Detected " + this.flashMapManager.getClass().getSimpleName());
            } else if (this.logger.isDebugEnabled()) {
                this.logger.debug("Detected " + this.flashMapManager);
            }
        } catch (NoSuchBeanDefinitionException e) {
            this.flashMapManager = (FlashMapManager) getDefaultStrategy(context, FlashMapManager.class);
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("No FlashMapManager 'flashMapManager': using default [" + this.flashMapManager.getClass().getSimpleName() + "]");
            }
        }
    }

    @Nullable
    @Deprecated
    public final ThemeSource getThemeSource() {
        WebApplicationContext webApplicationContext = getWebApplicationContext();
        if (!(webApplicationContext instanceof ThemeSource)) {
            return null;
        }
        ThemeSource themeSource = (ThemeSource) webApplicationContext;
        return themeSource;
    }

    @Nullable
    public final MultipartResolver getMultipartResolver() {
        return this.multipartResolver;
    }

    @Nullable
    public final List<HandlerMapping> getHandlerMappings() {
        if (this.handlerMappings != null) {
            return Collections.unmodifiableList(this.handlerMappings);
        }
        return null;
    }

    protected <T> T getDefaultStrategy(ApplicationContext context, Class<T> strategyInterface) {
        List<T> strategies = getDefaultStrategies(context, strategyInterface);
        if (strategies.size() != 1) {
            throw new BeanInitializationException("DispatcherServlet needs exactly 1 strategy for interface [" + strategyInterface.getName() + "]");
        }
        return strategies.get(0);
    }

    protected <T> List<T> getDefaultStrategies(ApplicationContext context, Class<T> strategyInterface) {
        if (defaultStrategies == null) {
            try {
                ClassPathResource resource = new ClassPathResource(DEFAULT_STRATEGIES_PATH, (Class<?>) DispatcherServlet.class);
                defaultStrategies = PropertiesLoaderUtils.loadProperties(resource);
            } catch (IOException ex) {
                throw new IllegalStateException("Could not load 'DispatcherServlet.properties': " + ex.getMessage());
            }
        }
        String key = strategyInterface.getName();
        String value = defaultStrategies.getProperty(key);
        if (value != null) {
            String[] classNames = StringUtils.commaDelimitedListToStringArray(value);
            ArrayList arrayList = new ArrayList(classNames.length);
            for (String className : classNames) {
                try {
                    Class<?> clazz = ClassUtils.forName(className, DispatcherServlet.class.getClassLoader());
                    Object strategy = createDefaultStrategy(context, clazz);
                    arrayList.add(strategy);
                } catch (ClassNotFoundException ex2) {
                    throw new BeanInitializationException("Could not find DispatcherServlet's default strategy class [" + className + "] for interface [" + key + "]", ex2);
                } catch (LinkageError err) {
                    throw new BeanInitializationException("Unresolvable class definition for DispatcherServlet's default strategy class [" + className + "] for interface [" + key + "]", err);
                }
            }
            return arrayList;
        }
        return Collections.emptyList();
    }

    protected Object createDefaultStrategy(ApplicationContext context, Class<?> clazz) {
        return context.getAutowireCapableBeanFactory().createBean(clazz);
    }

    @Override // org.springframework.web.servlet.FrameworkServlet
    protected void doService(HttpServletRequest request, HttpServletResponse response) throws Exception {
        logRequest(request);
        Map<String, Object> attributesSnapshot = null;
        if (WebUtils.isIncludeRequest(request)) {
            attributesSnapshot = new HashMap<>();
            Enumeration<?> attrNames = request.getAttributeNames();
            while (attrNames.hasMoreElements()) {
                String attrName = attrNames.nextElement();
                if (this.cleanupAfterInclude || attrName.startsWith(DEFAULT_STRATEGIES_PREFIX)) {
                    attributesSnapshot.put(attrName, request.getAttribute(attrName));
                }
            }
        }
        request.setAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE, getWebApplicationContext());
        request.setAttribute(LOCALE_RESOLVER_ATTRIBUTE, this.localeResolver);
        request.setAttribute(THEME_RESOLVER_ATTRIBUTE, this.themeResolver);
        request.setAttribute(THEME_SOURCE_ATTRIBUTE, getThemeSource());
        if (this.flashMapManager != null) {
            FlashMap inputFlashMap = this.flashMapManager.retrieveAndUpdate(request, response);
            if (inputFlashMap != null) {
                request.setAttribute(INPUT_FLASH_MAP_ATTRIBUTE, Collections.unmodifiableMap(inputFlashMap));
            }
            request.setAttribute(OUTPUT_FLASH_MAP_ATTRIBUTE, new FlashMap());
            request.setAttribute(FLASH_MAP_MANAGER_ATTRIBUTE, this.flashMapManager);
        }
        RequestPath previousRequestPath = null;
        if (this.parseRequestPath) {
            previousRequestPath = (RequestPath) request.getAttribute(ServletRequestPathUtils.PATH_ATTRIBUTE);
            ServletRequestPathUtils.parseAndCache(request);
        }
        try {
            doDispatch(request, response);
            if (!WebAsyncUtils.getAsyncManager(request).isConcurrentHandlingStarted() && attributesSnapshot != null) {
                restoreAttributesAfterInclude(request, attributesSnapshot);
            }
            if (this.parseRequestPath) {
                ServletRequestPathUtils.setParsedRequestPath(previousRequestPath, request);
            }
        } catch (Throwable th) {
            if (!WebAsyncUtils.getAsyncManager(request).isConcurrentHandlingStarted() && attributesSnapshot != null) {
                restoreAttributesAfterInclude(request, attributesSnapshot);
            }
            if (this.parseRequestPath) {
                ServletRequestPathUtils.setParsedRequestPath(previousRequestPath, request);
            }
            throw th;
        }
    }

    private void logRequest(HttpServletRequest request) {
        LogFormatUtils.traceDebug(this.logger, traceOn -> {
            String params;
            String headers;
            String contentType = request.getContentType();
            if (StringUtils.startsWithIgnoreCase(contentType, FileUploadBase.MULTIPART)) {
                params = "multipart";
            } else if (isEnableLoggingRequestDetails()) {
                params = (String) request.getParameterMap().entrySet().stream().map(entry -> {
                    return ((String) entry.getKey()) + ":" + Arrays.toString((Object[]) entry.getValue());
                }).collect(Collectors.joining(", "));
            } else {
                params = (StringUtils.startsWithIgnoreCase(contentType, MediaType.APPLICATION_FORM_URLENCODED_VALUE) || !request.getParameterMap().isEmpty()) ? "masked" : "";
            }
            String queryString = request.getQueryString();
            String queryClause = StringUtils.hasLength(queryString) ? "?" + queryString : "";
            String dispatchType = !DispatcherType.REQUEST.equals(request.getDispatcherType()) ? "\"" + request.getDispatcherType() + "\" dispatch for " : "";
            String message = dispatchType + request.getMethod() + " \"" + getRequestUri(request) + queryClause + "\", parameters={" + params + "}";
            if (traceOn.booleanValue()) {
                List<String> values = Collections.list(request.getHeaderNames());
                if (isEnableLoggingRequestDetails()) {
                    headers = (String) values.stream().map(name -> {
                        return name + ":" + Collections.list(request.getHeaders(name));
                    }).collect(Collectors.joining(", "));
                } else {
                    headers = !values.isEmpty() ? "masked" : "";
                }
                return message + ", headers={" + headers + "} in DispatcherServlet '" + getServletName() + "'";
            }
            return message;
        });
    }

    protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpServletRequest processedRequest = request;
        HandlerExecutionChain mappedHandler = null;
        boolean multipartRequestParsed = false;
        WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(request);
        try {
            ModelAndView mv = null;
            Exception dispatchException = null;
            try {
                try {
                    try {
                        processedRequest = checkMultipart(request);
                        multipartRequestParsed = processedRequest != request;
                        mappedHandler = getHandler(processedRequest);
                    } catch (Exception ex) {
                        dispatchException = ex;
                    } catch (Throwable err) {
                        dispatchException = new ServletException("Handler dispatch failed: " + err, err);
                    }
                    if (mappedHandler == null) {
                        noHandlerFound(processedRequest, response);
                        if (asyncManager.isConcurrentHandlingStarted()) {
                            if (mappedHandler != null) {
                                mappedHandler.applyAfterConcurrentHandlingStarted(processedRequest, response);
                                return;
                            }
                            return;
                        } else {
                            if (multipartRequestParsed) {
                                cleanupMultipart(processedRequest);
                                return;
                            }
                            return;
                        }
                    }
                    HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());
                    String method = request.getMethod();
                    boolean isGet = HttpMethod.GET.matches(method);
                    if (isGet || HttpMethod.HEAD.matches(method)) {
                        long lastModified = ha.getLastModified(request, mappedHandler.getHandler());
                        if (new ServletWebRequest(request, response).checkNotModified(lastModified) && isGet) {
                            if (asyncManager.isConcurrentHandlingStarted()) {
                                if (mappedHandler != null) {
                                    mappedHandler.applyAfterConcurrentHandlingStarted(processedRequest, response);
                                    return;
                                }
                                return;
                            } else {
                                if (multipartRequestParsed) {
                                    cleanupMultipart(processedRequest);
                                    return;
                                }
                                return;
                            }
                        }
                    }
                    if (!mappedHandler.applyPreHandle(processedRequest, response)) {
                        if (asyncManager.isConcurrentHandlingStarted()) {
                            if (mappedHandler != null) {
                                mappedHandler.applyAfterConcurrentHandlingStarted(processedRequest, response);
                                return;
                            }
                            return;
                        } else {
                            if (multipartRequestParsed) {
                                cleanupMultipart(processedRequest);
                                return;
                            }
                            return;
                        }
                    }
                    mv = ha.handle(processedRequest, response, mappedHandler.getHandler());
                    if (asyncManager.isConcurrentHandlingStarted()) {
                        if (asyncManager.isConcurrentHandlingStarted()) {
                            if (mappedHandler != null) {
                                mappedHandler.applyAfterConcurrentHandlingStarted(processedRequest, response);
                                return;
                            }
                            return;
                        } else {
                            if (multipartRequestParsed) {
                                cleanupMultipart(processedRequest);
                                return;
                            }
                            return;
                        }
                    }
                    applyDefaultViewName(processedRequest, mv);
                    mappedHandler.applyPostHandle(processedRequest, response, mv);
                    processDispatchResult(processedRequest, response, mappedHandler, mv, dispatchException);
                    if (asyncManager.isConcurrentHandlingStarted()) {
                        if (mappedHandler != null) {
                            mappedHandler.applyAfterConcurrentHandlingStarted(processedRequest, response);
                        }
                    } else if (multipartRequestParsed) {
                        cleanupMultipart(processedRequest);
                    }
                } catch (Throwable err2) {
                    triggerAfterCompletion(processedRequest, response, null, new ServletException("Handler processing failed: " + err2, err2));
                    if (asyncManager.isConcurrentHandlingStarted()) {
                        if (0 != 0) {
                            mappedHandler.applyAfterConcurrentHandlingStarted(processedRequest, response);
                        }
                    } else if (0 != 0) {
                        cleanupMultipart(processedRequest);
                    }
                }
            } catch (Exception ex2) {
                triggerAfterCompletion(processedRequest, response, null, ex2);
                if (asyncManager.isConcurrentHandlingStarted()) {
                    if (0 != 0) {
                        mappedHandler.applyAfterConcurrentHandlingStarted(processedRequest, response);
                    }
                } else if (0 != 0) {
                    cleanupMultipart(processedRequest);
                }
            }
        } catch (Throwable th) {
            if (asyncManager.isConcurrentHandlingStarted()) {
                if (0 != 0) {
                    mappedHandler.applyAfterConcurrentHandlingStarted(processedRequest, response);
                }
            } else if (0 != 0) {
                cleanupMultipart(processedRequest);
            }
            throw th;
        }
    }

    private void applyDefaultViewName(HttpServletRequest request, @Nullable ModelAndView mv) throws Exception {
        String defaultViewName;
        if (mv != null && !mv.hasView() && (defaultViewName = getDefaultViewName(request)) != null) {
            mv.setViewName(defaultViewName);
        }
    }

    private void processDispatchResult(HttpServletRequest request, HttpServletResponse response, @Nullable HandlerExecutionChain mappedHandler, @Nullable ModelAndView mv, @Nullable Exception exception) throws Exception {
        boolean errorView = false;
        if (exception != null) {
            if (exception instanceof ModelAndViewDefiningException) {
                ModelAndViewDefiningException mavDefiningException = (ModelAndViewDefiningException) exception;
                this.logger.debug("ModelAndViewDefiningException encountered", exception);
                mv = mavDefiningException.getModelAndView();
            } else {
                Object handler = mappedHandler != null ? mappedHandler.getHandler() : null;
                mv = processHandlerException(request, response, handler, exception);
                errorView = mv != null;
            }
        }
        if (mv != null && !mv.wasCleared()) {
            render(mv, request, response);
            if (errorView) {
                WebUtils.clearErrorRequestAttributes(request);
            }
        } else if (this.logger.isTraceEnabled()) {
            this.logger.trace("No view rendering, null ModelAndView returned.");
        }
        if (!WebAsyncUtils.getAsyncManager(request).isConcurrentHandlingStarted() && mappedHandler != null) {
            mappedHandler.triggerAfterCompletion(request, response, null);
        }
    }

    @Override // org.springframework.web.servlet.FrameworkServlet
    protected LocaleContext buildLocaleContext(final HttpServletRequest request) {
        LocaleResolver lr = this.localeResolver;
        if (lr instanceof LocaleContextResolver) {
            LocaleContextResolver localeContextResolver = (LocaleContextResolver) lr;
            return localeContextResolver.resolveLocaleContext(request);
        }
        return () -> {
            return lr != null ? lr.resolveLocale(request) : request.getLocale();
        };
    }

    protected HttpServletRequest checkMultipart(HttpServletRequest request) throws MultipartException {
        if (this.multipartResolver != null && this.multipartResolver.isMultipart(request)) {
            if (WebUtils.getNativeRequest(request, MultipartHttpServletRequest.class) != null) {
                if (DispatcherType.REQUEST.equals(request.getDispatcherType())) {
                    this.logger.trace("Request already resolved to MultipartHttpServletRequest, e.g. by MultipartFilter");
                }
            } else if (hasMultipartException(request)) {
                this.logger.debug("Multipart resolution previously failed for current request - skipping re-resolution for undisturbed error rendering");
            } else {
                try {
                    return this.multipartResolver.resolveMultipart(request);
                } catch (MultipartException ex) {
                    if (request.getAttribute("jakarta.servlet.error.exception") != null) {
                        this.logger.debug("Multipart resolution failed for error dispatch", ex);
                    } else {
                        throw ex;
                    }
                }
            }
        }
        return request;
    }

    private static boolean hasMultipartException(HttpServletRequest request) {
        Throwable th = (Throwable) request.getAttribute("jakarta.servlet.error.exception");
        while (true) {
            Throwable error = th;
            if (error != null) {
                if (error instanceof MultipartException) {
                    return true;
                }
                th = error.getCause();
            } else {
                return false;
            }
        }
    }

    protected void cleanupMultipart(HttpServletRequest request) {
        MultipartHttpServletRequest multipartRequest;
        if (this.multipartResolver != null && (multipartRequest = (MultipartHttpServletRequest) WebUtils.getNativeRequest(request, MultipartHttpServletRequest.class)) != null) {
            this.multipartResolver.cleanupMultipart(multipartRequest);
        }
    }

    @Nullable
    protected HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
        if (this.handlerMappings != null) {
            for (HandlerMapping mapping : this.handlerMappings) {
                HandlerExecutionChain handler = mapping.getHandler(request);
                if (handler != null) {
                    return handler;
                }
            }
            return null;
        }
        return null;
    }

    protected void noHandlerFound(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (pageNotFoundLogger.isWarnEnabled()) {
            pageNotFoundLogger.warn("No mapping for " + request.getMethod() + " " + getRequestUri(request));
        }
        if (this.throwExceptionIfNoHandlerFound) {
            throw new NoHandlerFoundException(request.getMethod(), getRequestUri(request), new ServletServerHttpRequest(request).getHeaders());
        }
        response.sendError(404);
    }

    protected HandlerAdapter getHandlerAdapter(Object handler) throws ServletException {
        if (this.handlerAdapters != null) {
            for (HandlerAdapter adapter : this.handlerAdapters) {
                if (adapter.supports(handler)) {
                    return adapter;
                }
            }
        }
        throw new ServletException("No adapter for handler [" + handler + "]: The DispatcherServlet configuration needs to include a HandlerAdapter that supports this handler");
    }

    @Nullable
    protected ModelAndView processHandlerException(HttpServletRequest request, HttpServletResponse response, @Nullable Object handler, Exception ex) throws Exception {
        String defaultViewName;
        request.removeAttribute(HandlerMapping.PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE);
        try {
            response.resetBuffer();
        } catch (IllegalStateException e) {
        }
        ModelAndView exMv = null;
        if (this.handlerExceptionResolvers != null) {
            for (HandlerExceptionResolver resolver : this.handlerExceptionResolvers) {
                exMv = resolver.resolveException(request, response, handler, ex);
                if (exMv != null) {
                    break;
                }
            }
        }
        if (exMv != null) {
            if (exMv.isEmpty()) {
                request.setAttribute(EXCEPTION_ATTRIBUTE, ex);
                return null;
            }
            if (!exMv.hasView() && (defaultViewName = getDefaultViewName(request)) != null) {
                exMv.setViewName(defaultViewName);
            }
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Using resolved error view: " + exMv, ex);
            } else if (this.logger.isDebugEnabled()) {
                this.logger.debug("Using resolved error view: " + exMv);
            }
            WebUtils.exposeErrorRequestAttributes(request, ex, getServletName());
            return exMv;
        }
        throw ex;
    }

    protected void render(ModelAndView mv, HttpServletRequest request, HttpServletResponse response) throws Exception {
        View view;
        Locale locale = this.localeResolver != null ? this.localeResolver.resolveLocale(request) : request.getLocale();
        response.setLocale(locale);
        String viewName = mv.getViewName();
        if (viewName != null) {
            view = resolveViewName(viewName, mv.getModelInternal(), locale, request);
            if (view == null) {
                throw new ServletException("Could not resolve view with name '" + mv.getViewName() + "' in servlet with name '" + getServletName() + "'");
            }
        } else {
            view = mv.getView();
            if (view == null) {
                throw new ServletException("ModelAndView [" + mv + "] neither contains a view name nor a View object in servlet with name '" + getServletName() + "'");
            }
        }
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Rendering view [" + view + "] ");
        }
        try {
            if (mv.getStatus() != null) {
                request.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, mv.getStatus());
                response.setStatus(mv.getStatus().value());
            }
            view.render(mv.getModelInternal(), request, response);
        } catch (Exception ex) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Error rendering view [" + view + "]", ex);
            }
            throw ex;
        }
    }

    @Nullable
    protected String getDefaultViewName(HttpServletRequest request) throws Exception {
        if (this.viewNameTranslator != null) {
            return this.viewNameTranslator.getViewName(request);
        }
        return null;
    }

    @Nullable
    protected View resolveViewName(String viewName, @Nullable Map<String, Object> model, Locale locale, HttpServletRequest request) throws Exception {
        if (this.viewResolvers != null) {
            for (ViewResolver viewResolver : this.viewResolvers) {
                View view = viewResolver.resolveViewName(viewName, locale);
                if (view != null) {
                    return view;
                }
            }
            return null;
        }
        return null;
    }

    private static void triggerAfterCompletion(HttpServletRequest request, HttpServletResponse response, @Nullable HandlerExecutionChain mappedHandler, Exception ex) throws Exception {
        if (mappedHandler != null) {
            mappedHandler.triggerAfterCompletion(request, response, ex);
        }
        throw ex;
    }

    private void restoreAttributesAfterInclude(HttpServletRequest request, Map<?, ?> attributesSnapshot) {
        Set<String> attrsToCheck = new HashSet<>();
        Enumeration<?> attrNames = request.getAttributeNames();
        while (attrNames.hasMoreElements()) {
            String attrName = attrNames.nextElement();
            if (this.cleanupAfterInclude || attrName.startsWith(DEFAULT_STRATEGIES_PREFIX)) {
                attrsToCheck.add(attrName);
            }
        }
        attrsToCheck.addAll(attributesSnapshot.keySet());
        for (String attrName2 : attrsToCheck) {
            Object attrValue = attributesSnapshot.get(attrName2);
            if (attrValue == null) {
                request.removeAttribute(attrName2);
            } else if (attrValue != request.getAttribute(attrName2)) {
                request.setAttribute(attrName2, attrValue);
            }
        }
    }

    private static String getRequestUri(HttpServletRequest request) {
        String uri = (String) request.getAttribute("jakarta.servlet.include.request_uri");
        if (uri == null) {
            uri = request.getRequestURI();
        }
        return uri;
    }
}

package org.springframework.web.servlet.handler;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.core.Ordered;
import org.springframework.core.log.LogDelegateFactory;
import org.springframework.http.server.RequestPath;
import org.springframework.lang.Nullable;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.PathMatcher;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.context.request.WebRequestInterceptor;
import org.springframework.web.context.request.async.WebAsyncManager;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.context.support.WebApplicationObjectSupport;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsProcessor;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.DefaultCorsProcessor;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.util.ServletRequestPathUtils;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.pattern.PathPatternParser;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/handler/AbstractHandlerMapping.class */
public abstract class AbstractHandlerMapping extends WebApplicationObjectSupport implements HandlerMapping, Ordered, BeanNameAware {
    static final String SUPPRESS_LOGGING_ATTRIBUTE = AbstractHandlerMapping.class.getName() + ".SUPPRESS_LOGGING";

    @Nullable
    private Object defaultHandler;

    @Nullable
    private CorsConfigurationSource corsConfigurationSource;

    @Nullable
    private String beanName;
    protected final Log mappingsLogger = LogDelegateFactory.getHiddenLog(HandlerMapping.class.getName() + ".Mappings");

    @Nullable
    private PathPatternParser patternParser = new PathPatternParser();
    private UrlPathHelper urlPathHelper = new UrlPathHelper();
    private PathMatcher pathMatcher = new AntPathMatcher();
    private final List<Object> interceptors = new ArrayList();
    private final List<HandlerInterceptor> adaptedInterceptors = new ArrayList();
    private CorsProcessor corsProcessor = new DefaultCorsProcessor();
    private int order = Integer.MAX_VALUE;

    @Nullable
    protected abstract Object getHandlerInternal(HttpServletRequest request) throws Exception;

    public void setDefaultHandler(@Nullable Object defaultHandler) {
        this.defaultHandler = defaultHandler;
    }

    @Nullable
    public Object getDefaultHandler() {
        return this.defaultHandler;
    }

    public void setPatternParser(@Nullable PathPatternParser patternParser) {
        this.patternParser = patternParser;
    }

    @Nullable
    public PathPatternParser getPatternParser() {
        return this.patternParser;
    }

    @Deprecated(since = "6.0")
    public void setAlwaysUseFullPath(boolean alwaysUseFullPath) {
        this.urlPathHelper.setAlwaysUseFullPath(alwaysUseFullPath);
        CorsConfigurationSource corsConfigurationSource = this.corsConfigurationSource;
        if (corsConfigurationSource instanceof UrlBasedCorsConfigurationSource) {
            UrlBasedCorsConfigurationSource urlConfigSource = (UrlBasedCorsConfigurationSource) corsConfigurationSource;
            urlConfigSource.setAlwaysUseFullPath(alwaysUseFullPath);
        }
    }

    @Deprecated(since = "6.0")
    public void setUrlDecode(boolean urlDecode) {
        this.urlPathHelper.setUrlDecode(urlDecode);
        CorsConfigurationSource corsConfigurationSource = this.corsConfigurationSource;
        if (corsConfigurationSource instanceof UrlBasedCorsConfigurationSource) {
            UrlBasedCorsConfigurationSource urlConfigSource = (UrlBasedCorsConfigurationSource) corsConfigurationSource;
            urlConfigSource.setUrlDecode(urlDecode);
        }
    }

    @Deprecated(since = "6.0")
    public void setRemoveSemicolonContent(boolean removeSemicolonContent) {
        this.urlPathHelper.setRemoveSemicolonContent(removeSemicolonContent);
        CorsConfigurationSource corsConfigurationSource = this.corsConfigurationSource;
        if (corsConfigurationSource instanceof UrlBasedCorsConfigurationSource) {
            UrlBasedCorsConfigurationSource urlConfigSource = (UrlBasedCorsConfigurationSource) corsConfigurationSource;
            urlConfigSource.setRemoveSemicolonContent(removeSemicolonContent);
        }
    }

    public void setUrlPathHelper(UrlPathHelper urlPathHelper) {
        Assert.notNull(urlPathHelper, "UrlPathHelper must not be null");
        this.urlPathHelper = urlPathHelper;
        CorsConfigurationSource corsConfigurationSource = this.corsConfigurationSource;
        if (corsConfigurationSource instanceof UrlBasedCorsConfigurationSource) {
            UrlBasedCorsConfigurationSource urlConfigSource = (UrlBasedCorsConfigurationSource) corsConfigurationSource;
            urlConfigSource.setUrlPathHelper(urlPathHelper);
        }
    }

    public UrlPathHelper getUrlPathHelper() {
        return this.urlPathHelper;
    }

    public void setPathMatcher(PathMatcher pathMatcher) {
        Assert.notNull(pathMatcher, "PathMatcher must not be null");
        this.pathMatcher = pathMatcher;
        CorsConfigurationSource corsConfigurationSource = this.corsConfigurationSource;
        if (corsConfigurationSource instanceof UrlBasedCorsConfigurationSource) {
            UrlBasedCorsConfigurationSource urlConfigSource = (UrlBasedCorsConfigurationSource) corsConfigurationSource;
            urlConfigSource.setPathMatcher(pathMatcher);
        }
    }

    public PathMatcher getPathMatcher() {
        return this.pathMatcher;
    }

    public void setInterceptors(Object... interceptors) {
        this.interceptors.addAll(Arrays.asList(interceptors));
    }

    @Nullable
    public final HandlerInterceptor[] getAdaptedInterceptors() {
        if (this.adaptedInterceptors.isEmpty()) {
            return null;
        }
        return (HandlerInterceptor[]) this.adaptedInterceptors.toArray(new HandlerInterceptor[0]);
    }

    @Nullable
    protected final MappedInterceptor[] getMappedInterceptors() {
        List<MappedInterceptor> mappedInterceptors = new ArrayList<>(this.adaptedInterceptors.size());
        for (HandlerInterceptor interceptor : this.adaptedInterceptors) {
            if (interceptor instanceof MappedInterceptor) {
                MappedInterceptor mappedInterceptor = (MappedInterceptor) interceptor;
                mappedInterceptors.add(mappedInterceptor);
            }
        }
        if (mappedInterceptors.isEmpty()) {
            return null;
        }
        return (MappedInterceptor[]) mappedInterceptors.toArray(new MappedInterceptor[0]);
    }

    public void setCorsConfigurations(Map<String, CorsConfiguration> corsConfigurations) {
        UrlBasedCorsConfigurationSource source;
        if (CollectionUtils.isEmpty(corsConfigurations)) {
            this.corsConfigurationSource = null;
            return;
        }
        if (getPatternParser() != null) {
            source = new UrlBasedCorsConfigurationSource(getPatternParser());
            source.setCorsConfigurations(corsConfigurations);
        } else {
            source = new UrlBasedCorsConfigurationSource();
            source.setCorsConfigurations(corsConfigurations);
            source.setPathMatcher(this.pathMatcher);
            source.setUrlPathHelper(this.urlPathHelper);
        }
        setCorsConfigurationSource(source);
    }

    public void setCorsConfigurationSource(CorsConfigurationSource source) {
        Assert.notNull(source, "CorsConfigurationSource must not be null");
        this.corsConfigurationSource = source;
        if (source instanceof UrlBasedCorsConfigurationSource) {
            UrlBasedCorsConfigurationSource urlConfigSource = (UrlBasedCorsConfigurationSource) source;
            urlConfigSource.setAllowInitLookupPath(false);
        }
    }

    @Nullable
    public CorsConfigurationSource getCorsConfigurationSource() {
        return this.corsConfigurationSource;
    }

    public void setCorsProcessor(CorsProcessor corsProcessor) {
        Assert.notNull(corsProcessor, "CorsProcessor must not be null");
        this.corsProcessor = corsProcessor;
    }

    public CorsProcessor getCorsProcessor() {
        return this.corsProcessor;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return this.order;
    }

    @Override // org.springframework.beans.factory.BeanNameAware
    public void setBeanName(String name) {
        this.beanName = name;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String formatMappingName() {
        return this.beanName != null ? "'" + this.beanName + "'" : getClass().getName();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.context.support.ApplicationObjectSupport
    public void initApplicationContext() throws BeansException {
        extendInterceptors(this.interceptors);
        detectMappedInterceptors(this.adaptedInterceptors);
        initInterceptors();
    }

    protected void extendInterceptors(List<Object> interceptors) {
    }

    protected void detectMappedInterceptors(List<HandlerInterceptor> mappedInterceptors) {
        mappedInterceptors.addAll(BeanFactoryUtils.beansOfTypeIncludingAncestors(obtainApplicationContext(), MappedInterceptor.class, true, false).values());
    }

    protected void initInterceptors() {
        if (!this.interceptors.isEmpty()) {
            for (int i = 0; i < this.interceptors.size(); i++) {
                Object interceptor = this.interceptors.get(i);
                if (interceptor == null) {
                    throw new IllegalArgumentException("Entry number " + i + " in interceptors array is null");
                }
                this.adaptedInterceptors.add(adaptInterceptor(interceptor));
            }
        }
    }

    protected HandlerInterceptor adaptInterceptor(Object interceptor) {
        if (interceptor instanceof HandlerInterceptor) {
            HandlerInterceptor handlerInterceptor = (HandlerInterceptor) interceptor;
            return handlerInterceptor;
        }
        if (interceptor instanceof WebRequestInterceptor) {
            WebRequestInterceptor webRequestInterceptor = (WebRequestInterceptor) interceptor;
            return new WebRequestHandlerInterceptorAdapter(webRequestInterceptor);
        }
        throw new IllegalArgumentException("Interceptor type not supported: " + interceptor.getClass().getName());
    }

    @Override // org.springframework.web.servlet.HandlerMapping
    public boolean usesPathPatterns() {
        return getPatternParser() != null;
    }

    @Override // org.springframework.web.servlet.HandlerMapping
    @Nullable
    public final HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
        Object handler = getHandlerInternal(request);
        if (handler == null) {
            handler = getDefaultHandler();
        }
        if (handler == null) {
            return null;
        }
        if (handler instanceof String) {
            String handlerName = (String) handler;
            handler = obtainApplicationContext().getBean(handlerName);
        }
        if (!ServletRequestPathUtils.hasCachedPath(request)) {
            initLookupPath(request);
        }
        HandlerExecutionChain executionChain = getHandlerExecutionChain(handler, request);
        if (request.getAttribute(SUPPRESS_LOGGING_ATTRIBUTE) == null) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Mapped to " + handler);
            } else if (this.logger.isDebugEnabled() && !DispatcherType.ASYNC.equals(request.getDispatcherType())) {
                this.logger.debug("Mapped to " + executionChain.getHandler());
            }
        }
        if (hasCorsConfigurationSource(handler) || CorsUtils.isPreFlightRequest(request)) {
            CorsConfiguration config = getCorsConfiguration(handler, request);
            if (getCorsConfigurationSource() != null) {
                CorsConfiguration globalConfig = getCorsConfigurationSource().getCorsConfiguration(request);
                config = globalConfig != null ? globalConfig.combine(config) : config;
            }
            if (config != null) {
                config.validateAllowCredentials();
                config.validateAllowPrivateNetwork();
            }
            executionChain = getCorsHandlerExecutionChain(request, executionChain, config);
        }
        return executionChain;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String initLookupPath(HttpServletRequest request) {
        if (usesPathPatterns()) {
            request.removeAttribute(UrlPathHelper.PATH_ATTRIBUTE);
            RequestPath requestPath = getRequestPath(request);
            String lookupPath = requestPath.pathWithinApplication().value();
            return UrlPathHelper.defaultInstance.removeSemicolonContent(lookupPath);
        }
        return getUrlPathHelper().resolveAndCacheLookupPath(request);
    }

    private RequestPath getRequestPath(HttpServletRequest request) {
        if (request.getAttribute(DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE) != null) {
            return ServletRequestPathUtils.getParsedRequestPath(request);
        }
        return ServletRequestPathUtils.parseAndCache(request);
    }

    protected HandlerExecutionChain getHandlerExecutionChain(Object handler, HttpServletRequest request) {
        HandlerExecutionChain handlerExecutionChain;
        if (handler instanceof HandlerExecutionChain) {
            HandlerExecutionChain handlerExecutionChain2 = (HandlerExecutionChain) handler;
            handlerExecutionChain = handlerExecutionChain2;
        } else {
            handlerExecutionChain = new HandlerExecutionChain(handler);
        }
        HandlerExecutionChain chain = handlerExecutionChain;
        for (HandlerInterceptor interceptor : this.adaptedInterceptors) {
            if (interceptor instanceof MappedInterceptor) {
                MappedInterceptor mappedInterceptor = (MappedInterceptor) interceptor;
                if (mappedInterceptor.matches(request)) {
                    chain.addInterceptor(mappedInterceptor.getInterceptor());
                }
            } else {
                chain.addInterceptor(interceptor);
            }
        }
        return chain;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean hasCorsConfigurationSource(Object handler) {
        if (handler instanceof HandlerExecutionChain) {
            HandlerExecutionChain handlerExecutionChain = (HandlerExecutionChain) handler;
            handler = handlerExecutionChain.getHandler();
        }
        return (handler instanceof CorsConfigurationSource) || this.corsConfigurationSource != null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public CorsConfiguration getCorsConfiguration(Object handler, HttpServletRequest request) {
        Object resolvedHandler = handler;
        if (handler instanceof HandlerExecutionChain) {
            HandlerExecutionChain handlerExecutionChain = (HandlerExecutionChain) handler;
            resolvedHandler = handlerExecutionChain.getHandler();
        }
        if (resolvedHandler instanceof CorsConfigurationSource) {
            CorsConfigurationSource configSource = (CorsConfigurationSource) resolvedHandler;
            return configSource.getCorsConfiguration(request);
        }
        return null;
    }

    protected HandlerExecutionChain getCorsHandlerExecutionChain(HttpServletRequest request, HandlerExecutionChain chain, @Nullable CorsConfiguration config) {
        if (CorsUtils.isPreFlightRequest(request)) {
            PreFlightHandler preFlightHandler = new PreFlightHandler(config);
            chain.addInterceptor(0, preFlightHandler);
            return new HandlerExecutionChain(preFlightHandler, chain.getInterceptors());
        }
        chain.addInterceptor(0, new CorsInterceptor(config));
        return chain;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/handler/AbstractHandlerMapping$CorsInterceptor.class */
    public class CorsInterceptor implements HandlerInterceptor, CorsConfigurationSource {

        @Nullable
        private final CorsConfiguration config;

        public CorsInterceptor(@Nullable CorsConfiguration config) {
            this.config = config;
        }

        @Override // org.springframework.web.servlet.HandlerInterceptor
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(request);
            if (asyncManager.hasConcurrentResult()) {
                return true;
            }
            return AbstractHandlerMapping.this.corsProcessor.processRequest(this.config, request, response);
        }

        @Override // org.springframework.web.cors.CorsConfigurationSource
        @Nullable
        public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
            return this.config;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/handler/AbstractHandlerMapping$PreFlightHandler.class */
    public class PreFlightHandler extends CorsInterceptor implements HttpRequestHandler {
        public PreFlightHandler(@Nullable CorsConfiguration config) {
            super(config);
        }

        @Override // org.springframework.web.HttpRequestHandler
        public void handleRequest(HttpServletRequest request, HttpServletResponse response) {
        }
    }
}

package org.springframework.web.servlet.handler;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.http.server.RequestPath;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.util.ServletRequestPathUtils;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.pattern.PathPatternParser;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/handler/HandlerMappingIntrospector.class */
public class HandlerMappingIntrospector implements CorsConfigurationSource, ApplicationContextAware, InitializingBean {
    private static final Log logger = LogFactory.getLog(HandlerMappingIntrospector.class.getName());
    private static final String CACHED_RESULT_ATTRIBUTE = HandlerMappingIntrospector.class.getName() + ".CachedResult";

    @Nullable
    private ApplicationContext applicationContext;

    @Nullable
    private List<HandlerMapping> handlerMappings;
    private Map<HandlerMapping, PathPatternMatchableHandlerMapping> pathPatternMappings = Collections.emptyMap();
    private final CacheResultLogHelper cacheLogHelper = new CacheResultLogHelper();

    @Override // org.springframework.context.ApplicationContextAware
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        if (this.handlerMappings == null) {
            Assert.notNull(this.applicationContext, "No ApplicationContext");
            this.handlerMappings = initHandlerMappings(this.applicationContext);
            this.pathPatternMappings = (Map) this.handlerMappings.stream().filter(m -> {
                if (m instanceof MatchableHandlerMapping) {
                    MatchableHandlerMapping hm = (MatchableHandlerMapping) m;
                    if (hm.getPatternParser() != null) {
                        return true;
                    }
                }
                return false;
            }).map(mapping -> {
                return (MatchableHandlerMapping) mapping;
            }).collect(Collectors.toMap(mapping2 -> {
                return mapping2;
            }, PathPatternMatchableHandlerMapping::new));
        }
    }

    private static List<HandlerMapping> initHandlerMappings(ApplicationContext context) {
        Map<String, HandlerMapping> beans = BeanFactoryUtils.beansOfTypeIncludingAncestors(context, HandlerMapping.class, true, false);
        if (!beans.isEmpty()) {
            List<HandlerMapping> mappings = new ArrayList<>(beans.values());
            AnnotationAwareOrderComparator.sort(mappings);
            return Collections.unmodifiableList(mappings);
        }
        return Collections.unmodifiableList(initFallback(context));
    }

    private static List<HandlerMapping> initFallback(ApplicationContext applicationContext) {
        try {
            Resource resource = new ClassPathResource("DispatcherServlet.properties", (Class<?>) DispatcherServlet.class);
            Properties properties = PropertiesLoaderUtils.loadProperties(resource);
            String value = properties.getProperty(HandlerMapping.class.getName());
            String[] names = StringUtils.commaDelimitedListToStringArray(value);
            List<HandlerMapping> result = new ArrayList<>(names.length);
            for (String name : names) {
                try {
                    Class<?> clazz = ClassUtils.forName(name, DispatcherServlet.class.getClassLoader());
                    Object mapping = applicationContext.getAutowireCapableBeanFactory().createBean(clazz);
                    result.add((HandlerMapping) mapping);
                } catch (ClassNotFoundException e) {
                    throw new IllegalStateException("Could not find default HandlerMapping [" + name + "]");
                }
            }
            return result;
        } catch (IOException ex) {
            throw new IllegalStateException("Could not load DispatcherServlet.properties: " + ex.getMessage());
        }
    }

    public List<HandlerMapping> getHandlerMappings() {
        return this.handlerMappings != null ? this.handlerMappings : Collections.emptyList();
    }

    public Filter createCacheFilter() {
        return (request, response, chain) -> {
            CachedResult previous = setCache((HttpServletRequest) request);
            try {
                chain.doFilter(request, response);
                resetCache(request, previous);
            } catch (Throwable th) {
                resetCache(request, previous);
                throw th;
            }
        };
    }

    @Nullable
    public CachedResult setCache(HttpServletRequest request) {
        CachedResult result;
        CachedResult previous = (CachedResult) request.getAttribute(CACHED_RESULT_ATTRIBUTE);
        if (previous == null || !previous.matches(request)) {
            HttpServletRequest wrapped = new AttributesPreservingRequest(request);
            try {
                result = (CachedResult) doWithHandlerMapping(wrapped, false, (mapping, executionChain) -> {
                    MatchableHandlerMapping matchableMapping = createMatchableHandlerMapping(mapping, wrapped);
                    CorsConfiguration corsConfig = getCorsConfiguration(executionChain, wrapped);
                    return new CachedResult(request, matchableMapping, corsConfig, null, null);
                });
            } catch (Exception ex) {
                try {
                    AttributesPreservingRequest requestToUse = new AttributesPreservingRequest(request);
                    result = (CachedResult) doWithHandlerMapping(requestToUse, true, (mapping2, executionChain2) -> {
                        CorsConfiguration corsConfig = getCorsConfiguration(executionChain2, wrapped);
                        return new CachedResult(request, null, corsConfig, ex, null);
                    });
                } catch (Exception ex2) {
                    result = new CachedResult(request, null, null, ex, new IllegalStateException(ex2));
                }
            }
            if (result == null) {
                result = new CachedResult(request, null, null, null, null);
            }
            request.setAttribute(CACHED_RESULT_ATTRIBUTE, result);
        }
        return previous;
    }

    public void resetCache(ServletRequest request, @Nullable CachedResult cachedResult) {
        request.setAttribute(CACHED_RESULT_ATTRIBUTE, cachedResult);
    }

    @Nullable
    public MatchableHandlerMapping getMatchableHandlerMapping(HttpServletRequest request) throws Exception {
        CachedResult result = CachedResult.getResultFor(request);
        if (result != null) {
            return result.getHandlerMapping();
        }
        this.cacheLogHelper.logHandlerMappingCacheMiss(request);
        HttpServletRequest requestToUse = new AttributesPreservingRequest(request);
        return (MatchableHandlerMapping) doWithHandlerMapping(requestToUse, false, (mapping, executionChain) -> {
            return createMatchableHandlerMapping(mapping, requestToUse);
        });
    }

    private MatchableHandlerMapping createMatchableHandlerMapping(HandlerMapping mapping, HttpServletRequest request) {
        if (mapping instanceof MatchableHandlerMapping) {
            PathPatternMatchableHandlerMapping pathPatternMapping = this.pathPatternMappings.get(mapping);
            if (pathPatternMapping != null) {
                RequestPath requestPath = ServletRequestPathUtils.getParsedRequestPath(request);
                return new LookupPathMatchableHandlerMapping(pathPatternMapping, requestPath);
            }
            String lookupPath = (String) request.getAttribute(UrlPathHelper.PATH_ATTRIBUTE);
            return new LookupPathMatchableHandlerMapping((MatchableHandlerMapping) mapping, lookupPath);
        }
        throw new IllegalStateException("HandlerMapping is not a MatchableHandlerMapping");
    }

    @Override // org.springframework.web.cors.CorsConfigurationSource
    @Nullable
    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
        CachedResult result = CachedResult.getResultFor(request);
        if (result != null) {
            return result.getCorsConfig();
        }
        this.cacheLogHelper.logCorsConfigCacheMiss(request);
        try {
            AttributesPreservingRequest requestToUse = new AttributesPreservingRequest(request);
            return (CorsConfiguration) doWithHandlerMapping(requestToUse, true, (handlerMapping, executionChain) -> {
                return getCorsConfiguration(executionChain, requestToUse);
            });
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    @Nullable
    public static CorsConfiguration getCorsConfiguration(HandlerExecutionChain chain, HttpServletRequest request) {
        for (HandlerInterceptor interceptor : chain.getInterceptorList()) {
            if (interceptor instanceof CorsConfigurationSource) {
                CorsConfigurationSource source = (CorsConfigurationSource) interceptor;
                return source.getCorsConfiguration(request);
            }
        }
        Object handler = chain.getHandler();
        if (handler instanceof CorsConfigurationSource) {
            CorsConfigurationSource source2 = (CorsConfigurationSource) handler;
            return source2.getCorsConfiguration(request);
        }
        return null;
    }

    @Nullable
    private <T> T doWithHandlerMapping(HttpServletRequest request, boolean ignoreException, BiFunction<HandlerMapping, HandlerExecutionChain, T> extractor) throws Exception {
        Assert.state(this.handlerMappings != null, "HandlerMapping's not initialized");
        boolean parsePath = !this.pathPatternMappings.isEmpty();
        RequestPath previousPath = null;
        if (parsePath) {
            previousPath = (RequestPath) request.getAttribute(ServletRequestPathUtils.PATH_ATTRIBUTE);
            ServletRequestPathUtils.parseAndCache(request);
        }
        try {
            for (HandlerMapping handlerMapping : this.handlerMappings) {
                HandlerExecutionChain chain = null;
                try {
                    chain = handlerMapping.getHandler(request);
                } catch (Exception ex) {
                    if (!ignoreException) {
                        throw ex;
                    }
                }
                if (chain != null) {
                    T apply = extractor.apply(handlerMapping, chain);
                    if (parsePath) {
                        ServletRequestPathUtils.setParsedRequestPath(previousPath, request);
                    }
                    return apply;
                }
            }
        } finally {
            if (parsePath) {
                ServletRequestPathUtils.setParsedRequestPath(previousPath, request);
            }
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/handler/HandlerMappingIntrospector$CachedResult.class */
    public static final class CachedResult {
        private final DispatcherType dispatcherType;
        private final String requestURI;

        @Nullable
        private final MatchableHandlerMapping handlerMapping;

        @Nullable
        private final CorsConfiguration corsConfig;

        @Nullable
        private final Exception failure;

        @Nullable
        private final IllegalStateException corsConfigFailure;

        private CachedResult(HttpServletRequest request, @Nullable MatchableHandlerMapping mapping, @Nullable CorsConfiguration config, @Nullable Exception failure, @Nullable IllegalStateException corsConfigFailure) {
            this.dispatcherType = request.getDispatcherType();
            this.requestURI = request.getRequestURI();
            this.handlerMapping = mapping;
            this.corsConfig = config;
            this.failure = failure;
            this.corsConfigFailure = corsConfigFailure;
        }

        public boolean matches(HttpServletRequest request) {
            return this.dispatcherType.equals(request.getDispatcherType()) && this.requestURI.equals(request.getRequestURI());
        }

        @Nullable
        public MatchableHandlerMapping getHandlerMapping() throws Exception {
            if (this.failure != null) {
                throw this.failure;
            }
            return this.handlerMapping;
        }

        @Nullable
        public CorsConfiguration getCorsConfig() {
            if (this.corsConfigFailure != null) {
                throw this.corsConfigFailure;
            }
            return this.corsConfig;
        }

        public String toString() {
            return "CachedResult for " + this.dispatcherType + " dispatch to '" + this.requestURI + "'";
        }

        @Nullable
        public static CachedResult getResultFor(HttpServletRequest request) {
            CachedResult result = (CachedResult) request.getAttribute(HandlerMappingIntrospector.CACHED_RESULT_ATTRIBUTE);
            if (result == null || !result.matches(request)) {
                return null;
            }
            return result;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/handler/HandlerMappingIntrospector$CacheResultLogHelper.class */
    private static class CacheResultLogHelper {
        private final Map<String, AtomicInteger> counters = Map.of("MatchableHandlerMapping", new AtomicInteger(), "CorsConfiguration", new AtomicInteger());

        private CacheResultLogHelper() {
        }

        public void logHandlerMappingCacheMiss(HttpServletRequest request) {
            logCacheMiss("MatchableHandlerMapping", request);
        }

        public void logCorsConfigCacheMiss(HttpServletRequest request) {
            logCacheMiss("CorsConfiguration", request);
        }

        private void logCacheMiss(String label, HttpServletRequest request) {
            AtomicInteger counter = this.counters.get(label);
            Assert.notNull(counter, "Expected '" + label + "' counter.");
            String message = getLogMessage(label, request);
            if (HandlerMappingIntrospector.logger.isWarnEnabled() && counter.getAndIncrement() == 0) {
                HandlerMappingIntrospector.logger.warn(message + " This is logged once only at WARN level, and every time at TRACE.");
            } else if (HandlerMappingIntrospector.logger.isTraceEnabled()) {
                HandlerMappingIntrospector.logger.trace("No CachedResult, performing " + label + " lookup instead.");
            }
        }

        private static String getLogMessage(String label, HttpServletRequest request) {
            return "Cache miss for " + request.getDispatcherType() + " dispatch to '" + request.getRequestURI() + "' (previous " + request.getAttribute(HandlerMappingIntrospector.CACHED_RESULT_ATTRIBUTE) + "). Performing " + label + " lookup.";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/handler/HandlerMappingIntrospector$AttributesPreservingRequest.class */
    public static class AttributesPreservingRequest extends HttpServletRequestWrapper {
        private final Map<String, Object> attributes;

        AttributesPreservingRequest(HttpServletRequest request) {
            super(request);
            this.attributes = initAttributes(request);
            this.attributes.put(AbstractHandlerMapping.SUPPRESS_LOGGING_ATTRIBUTE, Boolean.TRUE);
        }

        private Map<String, Object> initAttributes(HttpServletRequest request) {
            Map<String, Object> map = new HashMap<>();
            Enumeration<String> names = request.getAttributeNames();
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                map.put(name, request.getAttribute(name));
            }
            return map;
        }

        @Override // jakarta.servlet.ServletRequestWrapper, jakarta.servlet.ServletRequest
        public void setAttribute(String name, Object value) {
            this.attributes.put(name, value);
        }

        @Override // jakarta.servlet.ServletRequestWrapper, jakarta.servlet.ServletRequest
        public Object getAttribute(String name) {
            return this.attributes.get(name);
        }

        @Override // jakarta.servlet.ServletRequestWrapper, jakarta.servlet.ServletRequest
        public Enumeration<String> getAttributeNames() {
            return Collections.enumeration(this.attributes.keySet());
        }

        @Override // jakarta.servlet.ServletRequestWrapper, jakarta.servlet.ServletRequest
        public void removeAttribute(String name) {
            this.attributes.remove(name);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/handler/HandlerMappingIntrospector$LookupPathMatchableHandlerMapping.class */
    public static class LookupPathMatchableHandlerMapping implements MatchableHandlerMapping {
        private final MatchableHandlerMapping delegate;
        private final Object lookupPath;
        private final String pathAttributeName;

        LookupPathMatchableHandlerMapping(MatchableHandlerMapping delegate, Object lookupPath) {
            this.delegate = delegate;
            this.lookupPath = lookupPath;
            this.pathAttributeName = lookupPath instanceof RequestPath ? ServletRequestPathUtils.PATH_ATTRIBUTE : UrlPathHelper.PATH_ATTRIBUTE;
        }

        @Override // org.springframework.web.servlet.handler.MatchableHandlerMapping
        public PathPatternParser getPatternParser() {
            return this.delegate.getPatternParser();
        }

        @Override // org.springframework.web.servlet.handler.MatchableHandlerMapping
        @Nullable
        public RequestMatchResult match(HttpServletRequest request, String pattern) {
            String pattern2 = initFullPathPattern(pattern);
            Object previousPath = request.getAttribute(this.pathAttributeName);
            request.setAttribute(this.pathAttributeName, this.lookupPath);
            try {
                RequestMatchResult match = this.delegate.match(request, pattern2);
                request.setAttribute(this.pathAttributeName, previousPath);
                return match;
            } catch (Throwable th) {
                request.setAttribute(this.pathAttributeName, previousPath);
                throw th;
            }
        }

        private String initFullPathPattern(String pattern) {
            PathPatternParser parser = getPatternParser() != null ? getPatternParser() : PathPatternParser.defaultInstance;
            return parser.initFullPathPattern(pattern);
        }

        @Override // org.springframework.web.servlet.HandlerMapping
        @Nullable
        public HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
            return this.delegate.getHandler(request);
        }
    }
}

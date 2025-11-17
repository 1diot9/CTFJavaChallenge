package org.springframework.web.servlet.mvc.method.annotation;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;
import org.springframework.validation.DefaultBindingErrorProcessor;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.servlet.handler.MatchableHandlerMapping;
import org.springframework.web.servlet.handler.RequestMatchResult;
import org.springframework.web.servlet.mvc.condition.ConsumesRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.pattern.PathPatternParser;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/annotation/RequestMappingHandlerMapping.class */
public class RequestMappingHandlerMapping extends RequestMappingInfoHandlerMapping implements MatchableHandlerMapping, EmbeddedValueResolverAware {
    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    private static final RequestMethod[] EMPTY_REQUEST_METHOD_ARRAY = new RequestMethod[0];

    @Nullable
    private StringValueResolver embeddedValueResolver;
    private boolean defaultPatternParser = true;
    private boolean useSuffixPatternMatch = false;
    private boolean useRegisteredSuffixPatternMatch = false;
    private boolean useTrailingSlashMatch = false;
    private Map<String, Predicate<Class<?>>> pathPrefixes = Collections.emptyMap();
    private ContentNegotiationManager contentNegotiationManager = new ContentNegotiationManager();
    private RequestMappingInfo.BuilderConfiguration config = new RequestMappingInfo.BuilderConfiguration();

    @Override // org.springframework.web.servlet.handler.AbstractHandlerMethodMapping
    @Nullable
    protected /* bridge */ /* synthetic */ RequestMappingInfo getMappingForMethod(Method method, Class handlerType) {
        return getMappingForMethod(method, (Class<?>) handlerType);
    }

    @Override // org.springframework.web.servlet.handler.AbstractHandlerMethodMapping, org.springframework.web.servlet.handler.AbstractHandlerMapping
    public void setPatternParser(@Nullable PathPatternParser patternParser) {
        if (patternParser != null) {
            this.defaultPatternParser = false;
        }
        super.setPatternParser(patternParser);
    }

    @Deprecated
    public void setUseSuffixPatternMatch(boolean useSuffixPatternMatch) {
        this.useSuffixPatternMatch = useSuffixPatternMatch;
    }

    @Deprecated
    public void setUseRegisteredSuffixPatternMatch(boolean useRegisteredSuffixPatternMatch) {
        this.useRegisteredSuffixPatternMatch = useRegisteredSuffixPatternMatch;
        this.useSuffixPatternMatch = useRegisteredSuffixPatternMatch || this.useSuffixPatternMatch;
    }

    @Deprecated(since = "6.0")
    public void setUseTrailingSlashMatch(boolean useTrailingSlashMatch) {
        this.useTrailingSlashMatch = useTrailingSlashMatch;
        if (getPatternParser() != null) {
            getPatternParser().setMatchOptionalTrailingSeparator(useTrailingSlashMatch);
        }
    }

    public void setPathPrefixes(Map<String, Predicate<Class<?>>> prefixes) {
        Map<String, Predicate<Class<?>>> emptyMap;
        if (!prefixes.isEmpty()) {
            emptyMap = Collections.unmodifiableMap(new LinkedHashMap(prefixes));
        } else {
            emptyMap = Collections.emptyMap();
        }
        this.pathPrefixes = emptyMap;
    }

    public Map<String, Predicate<Class<?>>> getPathPrefixes() {
        return this.pathPrefixes;
    }

    public void setContentNegotiationManager(ContentNegotiationManager contentNegotiationManager) {
        Assert.notNull(contentNegotiationManager, "ContentNegotiationManager must not be null");
        this.contentNegotiationManager = contentNegotiationManager;
    }

    public ContentNegotiationManager getContentNegotiationManager() {
        return this.contentNegotiationManager;
    }

    @Override // org.springframework.context.EmbeddedValueResolverAware
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.embeddedValueResolver = resolver;
    }

    @Override // org.springframework.web.servlet.handler.AbstractHandlerMethodMapping, org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        this.config = new RequestMappingInfo.BuilderConfiguration();
        this.config.setTrailingSlashMatch(useTrailingSlashMatch());
        this.config.setContentNegotiationManager(getContentNegotiationManager());
        if (getPatternParser() != null && this.defaultPatternParser && (this.useSuffixPatternMatch || this.useRegisteredSuffixPatternMatch)) {
            setPatternParser(null);
        }
        if (getPatternParser() != null) {
            this.config.setPatternParser(getPatternParser());
            Assert.isTrue((this.useSuffixPatternMatch || this.useRegisteredSuffixPatternMatch) ? false : true, "Suffix pattern matching not supported with PathPatternParser.");
        } else {
            this.config.setSuffixPatternMatch(useSuffixPatternMatch());
            this.config.setRegisteredSuffixPatternMatch(useRegisteredSuffixPatternMatch());
            this.config.setPathMatcher(getPathMatcher());
        }
        super.afterPropertiesSet();
    }

    @Deprecated
    public boolean useSuffixPatternMatch() {
        return this.useSuffixPatternMatch;
    }

    @Deprecated
    public boolean useRegisteredSuffixPatternMatch() {
        return this.useRegisteredSuffixPatternMatch;
    }

    public boolean useTrailingSlashMatch() {
        return this.useTrailingSlashMatch;
    }

    @Nullable
    @Deprecated
    public List<String> getFileExtensions() {
        return this.config.getFileExtensions();
    }

    public RequestMappingInfo.BuilderConfiguration getBuilderConfiguration() {
        return this.config;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.handler.AbstractHandlerMethodMapping
    public boolean isHandler(Class<?> beanType) {
        return AnnotatedElementUtils.hasAnnotation(beanType, Controller.class);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.web.servlet.handler.AbstractHandlerMethodMapping
    @Nullable
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        RequestMappingInfo info = createRequestMappingInfo(method);
        if (info != null) {
            RequestMappingInfo typeInfo = createRequestMappingInfo(handlerType);
            if (typeInfo != null) {
                info = typeInfo.combine(info);
            }
            if (info.isEmptyMapping()) {
                info = info.mutate().paths("", "/").options(this.config).build();
            }
            String prefix = getPathPrefix(handlerType);
            if (prefix != null) {
                info = RequestMappingInfo.paths(prefix).options(this.config).build().combine(info);
            }
        }
        return info;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nullable
    public String getPathPrefix(Class<?> handlerType) {
        for (Map.Entry<String, Predicate<Class<?>>> entry : this.pathPrefixes.entrySet()) {
            if (entry.getValue().test(handlerType)) {
                String prefix = entry.getKey();
                if (this.embeddedValueResolver != null) {
                    prefix = this.embeddedValueResolver.resolveStringValue(prefix);
                }
                return prefix;
            }
        }
        return null;
    }

    @Nullable
    private RequestMappingInfo createRequestMappingInfo(AnnotatedElement element) {
        RequestCondition<?> customMethodCondition;
        if (element instanceof Class) {
            Class<?> clazz = (Class) element;
            customMethodCondition = getCustomTypeCondition(clazz);
        } else {
            customMethodCondition = getCustomMethodCondition((Method) element);
        }
        RequestCondition<?> customCondition = customMethodCondition;
        RequestMapping requestMapping = (RequestMapping) AnnotatedElementUtils.findMergedAnnotation(element, RequestMapping.class);
        if (requestMapping != null) {
            return createRequestMappingInfo(requestMapping, customCondition);
        }
        HttpExchange httpExchange = (HttpExchange) AnnotatedElementUtils.findMergedAnnotation(element, HttpExchange.class);
        if (httpExchange != null) {
            return createRequestMappingInfo(httpExchange, customCondition);
        }
        return null;
    }

    @Nullable
    protected RequestCondition<?> getCustomTypeCondition(Class<?> handlerType) {
        return null;
    }

    @Nullable
    protected RequestCondition<?> getCustomMethodCondition(Method method) {
        return null;
    }

    protected RequestMappingInfo createRequestMappingInfo(RequestMapping requestMapping, @Nullable RequestCondition<?> customCondition) {
        RequestMappingInfo.Builder builder = RequestMappingInfo.paths(resolveEmbeddedValuesInPatterns(requestMapping.path())).methods(requestMapping.method()).params(requestMapping.params()).headers(requestMapping.headers()).consumes(requestMapping.consumes()).produces(requestMapping.produces()).mappingName(requestMapping.name());
        if (customCondition != null) {
            builder.customCondition(customCondition);
        }
        return builder.options(this.config).build();
    }

    protected RequestMappingInfo createRequestMappingInfo(HttpExchange httpExchange, @Nullable RequestCondition<?> customCondition) {
        RequestMappingInfo.Builder builder = RequestMappingInfo.paths(resolveEmbeddedValuesInPatterns(toStringArray(httpExchange.value()))).methods(toMethodArray(httpExchange.method())).consumes(toStringArray(httpExchange.contentType())).produces(httpExchange.accept());
        if (customCondition != null) {
            builder.customCondition(customCondition);
        }
        return builder.options(this.config).build();
    }

    protected String[] resolveEmbeddedValuesInPatterns(String[] patterns) {
        if (this.embeddedValueResolver == null) {
            return patterns;
        }
        String[] resolvedPatterns = new String[patterns.length];
        for (int i = 0; i < patterns.length; i++) {
            resolvedPatterns[i] = this.embeddedValueResolver.resolveStringValue(patterns[i]);
        }
        return resolvedPatterns;
    }

    private static String[] toStringArray(String value) {
        return StringUtils.hasText(value) ? new String[]{value} : EMPTY_STRING_ARRAY;
    }

    private static RequestMethod[] toMethodArray(String method) {
        return StringUtils.hasText(method) ? new RequestMethod[]{RequestMethod.valueOf(method)} : EMPTY_REQUEST_METHOD_ARRAY;
    }

    @Override // org.springframework.web.servlet.handler.AbstractHandlerMethodMapping
    public void registerMapping(RequestMappingInfo mapping, Object handler, Method method) {
        super.registerMapping((RequestMappingHandlerMapping) mapping, handler, method);
        updateConsumesCondition(mapping, method);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.handler.AbstractHandlerMethodMapping
    public void registerHandlerMethod(Object handler, Method method, RequestMappingInfo mapping) {
        super.registerHandlerMethod(handler, method, (Method) mapping);
        updateConsumesCondition(mapping, method);
    }

    private void updateConsumesCondition(RequestMappingInfo info, Method method) {
        ConsumesRequestCondition condition = info.getConsumesCondition();
        if (!condition.isEmpty()) {
            for (Parameter parameter : method.getParameters()) {
                MergedAnnotation<RequestBody> annot = MergedAnnotations.from(parameter).get(RequestBody.class);
                if (annot.isPresent()) {
                    condition.setBodyRequired(annot.getBoolean(DefaultBindingErrorProcessor.MISSING_FIELD_ERROR_CODE));
                    return;
                }
            }
        }
    }

    @Override // org.springframework.web.servlet.handler.MatchableHandlerMapping
    public RequestMatchResult match(HttpServletRequest request, String pattern) {
        Assert.state(getPatternParser() == null, "This HandlerMapping uses PathPatterns.");
        RequestMappingInfo info = RequestMappingInfo.paths(pattern).options(this.config).build();
        RequestMappingInfo match = info.getMatchingCondition(request);
        if (match == null || match.getPatternsCondition() == null) {
            return null;
        }
        return new RequestMatchResult(match.getPatternsCondition().getPatterns().iterator().next(), UrlPathHelper.getResolvedLookupPath(request), getPathMatcher());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.handler.AbstractHandlerMethodMapping
    public CorsConfiguration initCorsConfiguration(Object handler, Method method, RequestMappingInfo mappingInfo) {
        HandlerMethod handlerMethod = createHandlerMethod(handler, method);
        Class<?> beanType = handlerMethod.getBeanType();
        CrossOrigin typeAnnotation = (CrossOrigin) AnnotatedElementUtils.findMergedAnnotation(beanType, CrossOrigin.class);
        CrossOrigin methodAnnotation = (CrossOrigin) AnnotatedElementUtils.findMergedAnnotation(method, CrossOrigin.class);
        if (typeAnnotation == null && methodAnnotation == null) {
            return null;
        }
        CorsConfiguration config = new CorsConfiguration();
        updateCorsConfig(config, typeAnnotation);
        updateCorsConfig(config, methodAnnotation);
        if (CollectionUtils.isEmpty(config.getAllowedMethods())) {
            for (RequestMethod allowedMethod : mappingInfo.getMethodsCondition().getMethods()) {
                config.addAllowedMethod(allowedMethod.name());
            }
        }
        return config.applyPermitDefaultValues();
    }

    private void updateCorsConfig(CorsConfiguration config, @Nullable CrossOrigin annotation) {
        if (annotation == null) {
            return;
        }
        for (String origin : annotation.origins()) {
            config.addAllowedOrigin(resolveCorsAnnotationValue(origin));
        }
        for (String patterns : annotation.originPatterns()) {
            config.addAllowedOriginPattern(resolveCorsAnnotationValue(patterns));
        }
        for (RequestMethod method : annotation.methods()) {
            config.addAllowedMethod(method.name());
        }
        for (String header : annotation.allowedHeaders()) {
            config.addAllowedHeader(resolveCorsAnnotationValue(header));
        }
        for (String header2 : annotation.exposedHeaders()) {
            config.addExposedHeader(resolveCorsAnnotationValue(header2));
        }
        String allowCredentials = resolveCorsAnnotationValue(annotation.allowCredentials());
        if ("true".equalsIgnoreCase(allowCredentials)) {
            config.setAllowCredentials(true);
        } else if ("false".equalsIgnoreCase(allowCredentials)) {
            config.setAllowCredentials(false);
        } else if (!allowCredentials.isEmpty()) {
            throw new IllegalStateException("@CrossOrigin's allowCredentials value must be \"true\", \"false\", or an empty string (\"\"): current value is [" + allowCredentials + "]");
        }
        String allowPrivateNetwork = resolveCorsAnnotationValue(annotation.allowPrivateNetwork());
        if ("true".equalsIgnoreCase(allowPrivateNetwork)) {
            config.setAllowPrivateNetwork(true);
        } else if ("false".equalsIgnoreCase(allowPrivateNetwork)) {
            config.setAllowPrivateNetwork(false);
        } else if (!allowPrivateNetwork.isEmpty()) {
            throw new IllegalStateException("@CrossOrigin's allowPrivateNetwork value must be \"true\", \"false\", or an empty string (\"\"): current value is [" + allowPrivateNetwork + "]");
        }
        if (annotation.maxAge() >= 0) {
            config.setMaxAge(Long.valueOf(annotation.maxAge()));
        }
    }

    private String resolveCorsAnnotationValue(String value) {
        if (this.embeddedValueResolver != null) {
            String resolved = this.embeddedValueResolver.resolveStringValue(value);
            return resolved != null ? resolved : "";
        }
        return value;
    }
}

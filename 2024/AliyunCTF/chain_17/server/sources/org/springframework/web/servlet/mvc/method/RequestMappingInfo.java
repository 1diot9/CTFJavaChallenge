package org.springframework.web.servlet.mvc.method;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.condition.ConsumesRequestCondition;
import org.springframework.web.servlet.mvc.condition.HeadersRequestCondition;
import org.springframework.web.servlet.mvc.condition.ParamsRequestCondition;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.ProducesRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestConditionHolder;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.pattern.PathPatternParser;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/RequestMappingInfo.class */
public final class RequestMappingInfo implements RequestCondition<RequestMappingInfo> {
    private static final PathPatternsRequestCondition EMPTY_PATH_PATTERNS = new PathPatternsRequestCondition();
    private static final PatternsRequestCondition EMPTY_PATTERNS = new PatternsRequestCondition(new String[0]);
    private static final RequestMethodsRequestCondition EMPTY_REQUEST_METHODS = new RequestMethodsRequestCondition(new RequestMethod[0]);
    private static final ParamsRequestCondition EMPTY_PARAMS = new ParamsRequestCondition(new String[0]);
    private static final HeadersRequestCondition EMPTY_HEADERS = new HeadersRequestCondition(new String[0]);
    private static final ConsumesRequestCondition EMPTY_CONSUMES = new ConsumesRequestCondition(new String[0]);
    private static final ProducesRequestCondition EMPTY_PRODUCES = new ProducesRequestCondition(new String[0]);
    private static final RequestConditionHolder EMPTY_CUSTOM = new RequestConditionHolder(null);

    @Nullable
    private final String name;

    @Nullable
    private final PathPatternsRequestCondition pathPatternsCondition;

    @Nullable
    private final PatternsRequestCondition patternsCondition;
    private final RequestMethodsRequestCondition methodsCondition;
    private final ParamsRequestCondition paramsCondition;
    private final HeadersRequestCondition headersCondition;
    private final ConsumesRequestCondition consumesCondition;
    private final ProducesRequestCondition producesCondition;
    private final RequestConditionHolder customConditionHolder;
    private final int hashCode;
    private final BuilderConfiguration options;

    /* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/RequestMappingInfo$Builder.class */
    public interface Builder {
        Builder paths(String... paths);

        Builder methods(RequestMethod... methods);

        Builder params(String... params);

        Builder headers(String... headers);

        Builder consumes(String... consumes);

        Builder produces(String... produces);

        Builder mappingName(String name);

        Builder customCondition(RequestCondition<?> condition);

        Builder options(BuilderConfiguration options);

        RequestMappingInfo build();
    }

    @Deprecated
    public RequestMappingInfo(@Nullable String name, @Nullable PatternsRequestCondition patterns, @Nullable RequestMethodsRequestCondition methods, @Nullable ParamsRequestCondition params, @Nullable HeadersRequestCondition headers, @Nullable ConsumesRequestCondition consumes, @Nullable ProducesRequestCondition produces, @Nullable RequestCondition<?> custom) {
        this(name, null, patterns != null ? patterns : EMPTY_PATTERNS, methods != null ? methods : EMPTY_REQUEST_METHODS, params != null ? params : EMPTY_PARAMS, headers != null ? headers : EMPTY_HEADERS, consumes != null ? consumes : EMPTY_CONSUMES, produces != null ? produces : EMPTY_PRODUCES, custom != null ? new RequestConditionHolder(custom) : EMPTY_CUSTOM, new BuilderConfiguration());
    }

    @Deprecated
    public RequestMappingInfo(@Nullable PatternsRequestCondition patterns, @Nullable RequestMethodsRequestCondition methods, @Nullable ParamsRequestCondition params, @Nullable HeadersRequestCondition headers, @Nullable ConsumesRequestCondition consumes, @Nullable ProducesRequestCondition produces, @Nullable RequestCondition<?> custom) {
        this(null, patterns, methods, params, headers, consumes, produces, custom);
    }

    @Deprecated
    public RequestMappingInfo(RequestMappingInfo info, @Nullable RequestCondition<?> customRequestCondition) {
        this(info.name, info.patternsCondition, info.methodsCondition, info.paramsCondition, info.headersCondition, info.consumesCondition, info.producesCondition, customRequestCondition);
    }

    private RequestMappingInfo(@Nullable String name, @Nullable PathPatternsRequestCondition pathPatternsCondition, @Nullable PatternsRequestCondition patternsCondition, RequestMethodsRequestCondition methodsCondition, ParamsRequestCondition paramsCondition, HeadersRequestCondition headersCondition, ConsumesRequestCondition consumesCondition, ProducesRequestCondition producesCondition, RequestConditionHolder customCondition, BuilderConfiguration options) {
        Assert.isTrue((pathPatternsCondition == null && patternsCondition == null) ? false : true, "Neither PathPatterns nor String patterns condition");
        this.name = StringUtils.hasText(name) ? name : null;
        this.pathPatternsCondition = pathPatternsCondition;
        this.patternsCondition = patternsCondition;
        this.methodsCondition = methodsCondition;
        this.paramsCondition = paramsCondition;
        this.headersCondition = headersCondition;
        this.consumesCondition = consumesCondition;
        this.producesCondition = producesCondition;
        this.customConditionHolder = customCondition;
        this.options = options;
        this.hashCode = calculateHashCode(this.pathPatternsCondition, this.patternsCondition, this.methodsCondition, this.paramsCondition, this.headersCondition, this.consumesCondition, this.producesCondition, this.customConditionHolder);
    }

    @Nullable
    public String getName() {
        return this.name;
    }

    @Nullable
    public PathPatternsRequestCondition getPathPatternsCondition() {
        return this.pathPatternsCondition;
    }

    @Nullable
    public PatternsRequestCondition getPatternsCondition() {
        return this.patternsCondition;
    }

    public <T> RequestCondition<T> getActivePatternsCondition() {
        if (this.pathPatternsCondition != null) {
            return this.pathPatternsCondition;
        }
        if (this.patternsCondition != null) {
            return this.patternsCondition;
        }
        throw new IllegalStateException();
    }

    public Set<String> getDirectPaths() {
        RequestCondition<?> condition = getActivePatternsCondition();
        if (!(condition instanceof PathPatternsRequestCondition)) {
            return ((PatternsRequestCondition) condition).getDirectPaths();
        }
        PathPatternsRequestCondition pprc = (PathPatternsRequestCondition) condition;
        return pprc.getDirectPaths();
    }

    public Set<String> getPatternValues() {
        RequestCondition<?> condition = getActivePatternsCondition();
        if (!(condition instanceof PathPatternsRequestCondition)) {
            return ((PatternsRequestCondition) condition).getPatterns();
        }
        PathPatternsRequestCondition pprc = (PathPatternsRequestCondition) condition;
        return pprc.getPatternValues();
    }

    public boolean isEmptyMapping() {
        RequestCondition<?> condition = getActivePatternsCondition();
        if (!(condition instanceof PathPatternsRequestCondition)) {
            return ((PatternsRequestCondition) condition).isEmptyPathMapping();
        }
        PathPatternsRequestCondition pprc = (PathPatternsRequestCondition) condition;
        return pprc.isEmptyPathMapping();
    }

    public RequestMethodsRequestCondition getMethodsCondition() {
        return this.methodsCondition;
    }

    public ParamsRequestCondition getParamsCondition() {
        return this.paramsCondition;
    }

    public HeadersRequestCondition getHeadersCondition() {
        return this.headersCondition;
    }

    public ConsumesRequestCondition getConsumesCondition() {
        return this.consumesCondition;
    }

    public ProducesRequestCondition getProducesCondition() {
        return this.producesCondition;
    }

    @Nullable
    public RequestCondition<?> getCustomCondition() {
        return this.customConditionHolder.getCondition();
    }

    public RequestMappingInfo addCustomCondition(RequestCondition<?> customCondition) {
        return new RequestMappingInfo(this.name, this.pathPatternsCondition, this.patternsCondition, this.methodsCondition, this.paramsCondition, this.headersCondition, this.consumesCondition, this.producesCondition, new RequestConditionHolder(customCondition), this.options);
    }

    @Override // org.springframework.web.servlet.mvc.condition.RequestCondition
    public RequestMappingInfo combine(RequestMappingInfo other) {
        String name = combineNames(other);
        PathPatternsRequestCondition pathPatterns = (this.pathPatternsCondition == null || other.pathPatternsCondition == null) ? null : this.pathPatternsCondition.combine(other.pathPatternsCondition);
        PatternsRequestCondition patterns = (this.patternsCondition == null || other.patternsCondition == null) ? null : this.patternsCondition.combine(other.patternsCondition);
        RequestMethodsRequestCondition methods = this.methodsCondition.combine(other.methodsCondition);
        ParamsRequestCondition params = this.paramsCondition.combine(other.paramsCondition);
        HeadersRequestCondition headers = this.headersCondition.combine(other.headersCondition);
        ConsumesRequestCondition consumes = this.consumesCondition.combine(other.consumesCondition);
        ProducesRequestCondition produces = this.producesCondition.combine(other.producesCondition);
        RequestConditionHolder custom = this.customConditionHolder.combine(other.customConditionHolder);
        return new RequestMappingInfo(name, pathPatterns, patterns, methods, params, headers, consumes, produces, custom, this.options);
    }

    @Nullable
    private String combineNames(RequestMappingInfo other) {
        if (this.name != null && other.name != null) {
            return this.name + "#" + other.name;
        }
        if (this.name != null) {
            return this.name;
        }
        return other.name;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.web.servlet.mvc.condition.RequestCondition
    @Nullable
    public RequestMappingInfo getMatchingCondition(HttpServletRequest request) {
        ParamsRequestCondition params;
        HeadersRequestCondition headers;
        ConsumesRequestCondition consumes;
        ProducesRequestCondition produces;
        RequestMethodsRequestCondition methods = this.methodsCondition.getMatchingCondition(request);
        if (methods == null || (params = this.paramsCondition.getMatchingCondition(request)) == null || (headers = this.headersCondition.getMatchingCondition(request)) == null || (consumes = this.consumesCondition.getMatchingCondition(request)) == null || (produces = this.producesCondition.getMatchingCondition(request)) == null) {
            return null;
        }
        PathPatternsRequestCondition pathPatterns = null;
        if (this.pathPatternsCondition != null) {
            pathPatterns = this.pathPatternsCondition.getMatchingCondition(request);
            if (pathPatterns == null) {
                return null;
            }
        }
        PatternsRequestCondition patterns = null;
        if (this.patternsCondition != null) {
            patterns = this.patternsCondition.getMatchingCondition(request);
            if (patterns == null) {
                return null;
            }
        }
        RequestConditionHolder custom = this.customConditionHolder.getMatchingCondition(request);
        if (custom == null) {
            return null;
        }
        return new RequestMappingInfo(this.name, pathPatterns, patterns, methods, params, headers, consumes, produces, custom, this.options);
    }

    @Override // org.springframework.web.servlet.mvc.condition.RequestCondition
    public int compareTo(RequestMappingInfo other, HttpServletRequest request) {
        int result;
        if (HttpMethod.HEAD.matches(request.getMethod()) && (result = this.methodsCondition.compareTo(other.getMethodsCondition(), request)) != 0) {
            return result;
        }
        int result2 = getActivePatternsCondition().compareTo(other.getActivePatternsCondition(), request);
        if (result2 != 0) {
            return result2;
        }
        int result3 = this.paramsCondition.compareTo(other.getParamsCondition(), request);
        if (result3 != 0) {
            return result3;
        }
        int result4 = this.headersCondition.compareTo(other.getHeadersCondition(), request);
        if (result4 != 0) {
            return result4;
        }
        int result5 = this.consumesCondition.compareTo(other.getConsumesCondition(), request);
        if (result5 != 0) {
            return result5;
        }
        int result6 = this.producesCondition.compareTo(other.getProducesCondition(), request);
        if (result6 != 0) {
            return result6;
        }
        int result7 = this.methodsCondition.compareTo(other.getMethodsCondition(), request);
        if (result7 != 0) {
            return result7;
        }
        int result8 = this.customConditionHolder.compareTo(other.customConditionHolder, request);
        if (result8 != 0) {
            return result8;
        }
        return 0;
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof RequestMappingInfo) {
                RequestMappingInfo that = (RequestMappingInfo) other;
                if (!getActivePatternsCondition().equals(that.getActivePatternsCondition()) || !this.methodsCondition.equals(that.methodsCondition) || !this.paramsCondition.equals(that.paramsCondition) || !this.headersCondition.equals(that.headersCondition) || !this.consumesCondition.equals(that.consumesCondition) || !this.producesCondition.equals(that.producesCondition) || !this.customConditionHolder.equals(that.customConditionHolder)) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.hashCode;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static int calculateHashCode(@Nullable PathPatternsRequestCondition pathPatterns, @Nullable PatternsRequestCondition patterns, RequestMethodsRequestCondition methods, ParamsRequestCondition params, HeadersRequestCondition headers, ConsumesRequestCondition consumes, ProducesRequestCondition produces, RequestConditionHolder custom) {
        return ((pathPatterns != null ? pathPatterns : patterns).hashCode() * 31) + methods.hashCode() + params.hashCode() + headers.hashCode() + consumes.hashCode() + produces.hashCode() + custom.hashCode();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("{");
        if (!this.methodsCondition.isEmpty()) {
            Set<RequestMethod> httpMethods = this.methodsCondition.getMethods();
            builder.append(httpMethods.size() == 1 ? httpMethods.iterator().next() : httpMethods);
        }
        builder.append(' ').append(getActivePatternsCondition());
        if (!this.paramsCondition.isEmpty()) {
            builder.append(", params ").append(this.paramsCondition);
        }
        if (!this.headersCondition.isEmpty()) {
            builder.append(", headers ").append(this.headersCondition);
        }
        if (!this.consumesCondition.isEmpty()) {
            builder.append(", consumes ").append(this.consumesCondition);
        }
        if (!this.producesCondition.isEmpty()) {
            builder.append(", produces ").append(this.producesCondition);
        }
        if (!this.customConditionHolder.isEmpty()) {
            builder.append(", and ").append(this.customConditionHolder);
        }
        builder.append('}');
        return builder.toString();
    }

    public Builder mutate() {
        return new MutateBuilder(this);
    }

    public static Builder paths(String... paths) {
        return new DefaultBuilder(paths);
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/RequestMappingInfo$DefaultBuilder.class */
    private static class DefaultBuilder implements Builder {
        private String[] paths;
        private boolean hasContentType;
        private boolean hasAccept;

        @Nullable
        private String mappingName;

        @Nullable
        private RequestCondition<?> customCondition;
        private RequestMethod[] methods = new RequestMethod[0];
        private String[] params = new String[0];
        private String[] headers = new String[0];
        private String[] consumes = new String[0];
        private String[] produces = new String[0];
        private BuilderConfiguration options = new BuilderConfiguration();

        @Override // org.springframework.web.servlet.mvc.method.RequestMappingInfo.Builder
        public /* bridge */ /* synthetic */ Builder customCondition(RequestCondition condition) {
            return customCondition((RequestCondition<?>) condition);
        }

        public DefaultBuilder(String... paths) {
            this.paths = paths;
        }

        @Override // org.springframework.web.servlet.mvc.method.RequestMappingInfo.Builder
        public Builder paths(String... paths) {
            this.paths = paths;
            return this;
        }

        @Override // org.springframework.web.servlet.mvc.method.RequestMappingInfo.Builder
        public DefaultBuilder methods(RequestMethod... methods) {
            this.methods = methods;
            return this;
        }

        @Override // org.springframework.web.servlet.mvc.method.RequestMappingInfo.Builder
        public DefaultBuilder params(String... params) {
            this.params = params;
            return this;
        }

        @Override // org.springframework.web.servlet.mvc.method.RequestMappingInfo.Builder
        public DefaultBuilder headers(String... headers) {
            for (String header : headers) {
                this.hasContentType = this.hasContentType || header.contains(HttpHeaders.CONTENT_TYPE) || header.contains("content-type");
                this.hasAccept = this.hasAccept || header.contains(HttpHeaders.ACCEPT) || header.contains("accept");
            }
            this.headers = headers;
            return this;
        }

        @Override // org.springframework.web.servlet.mvc.method.RequestMappingInfo.Builder
        public DefaultBuilder consumes(String... consumes) {
            this.consumes = consumes;
            return this;
        }

        @Override // org.springframework.web.servlet.mvc.method.RequestMappingInfo.Builder
        public DefaultBuilder produces(String... produces) {
            this.produces = produces;
            return this;
        }

        @Override // org.springframework.web.servlet.mvc.method.RequestMappingInfo.Builder
        public DefaultBuilder mappingName(String name) {
            this.mappingName = name;
            return this;
        }

        @Override // org.springframework.web.servlet.mvc.method.RequestMappingInfo.Builder
        public DefaultBuilder customCondition(RequestCondition<?> condition) {
            this.customCondition = condition;
            return this;
        }

        @Override // org.springframework.web.servlet.mvc.method.RequestMappingInfo.Builder
        public Builder options(BuilderConfiguration options) {
            this.options = options;
            return this;
        }

        @Override // org.springframework.web.servlet.mvc.method.RequestMappingInfo.Builder
        public RequestMappingInfo build() {
            PatternsRequestCondition patternsRequestCondition;
            PathPatternsRequestCondition pathPatternsRequestCondition;
            PathPatternsRequestCondition pathPatternsCondition = null;
            PatternsRequestCondition patternsCondition = null;
            PathPatternParser parser = this.options.getPatternParserToUse();
            if (parser != null) {
                if (ObjectUtils.isEmpty((Object[]) this.paths)) {
                    pathPatternsRequestCondition = RequestMappingInfo.EMPTY_PATH_PATTERNS;
                } else {
                    pathPatternsRequestCondition = new PathPatternsRequestCondition(parser, this.paths);
                }
                pathPatternsCondition = pathPatternsRequestCondition;
            } else {
                if (ObjectUtils.isEmpty((Object[]) this.paths)) {
                    patternsRequestCondition = RequestMappingInfo.EMPTY_PATTERNS;
                } else {
                    patternsRequestCondition = new PatternsRequestCondition(this.paths, null, this.options.pathMatcher, this.options.useSuffixPatternMatch(), this.options.useTrailingSlashMatch(), this.options.getFileExtensions());
                }
                patternsCondition = patternsRequestCondition;
            }
            ContentNegotiationManager manager = this.options.getContentNegotiationManager();
            return new RequestMappingInfo(this.mappingName, pathPatternsCondition, patternsCondition, ObjectUtils.isEmpty((Object[]) this.methods) ? RequestMappingInfo.EMPTY_REQUEST_METHODS : new RequestMethodsRequestCondition(this.methods), ObjectUtils.isEmpty((Object[]) this.params) ? RequestMappingInfo.EMPTY_PARAMS : new ParamsRequestCondition(this.params), ObjectUtils.isEmpty((Object[]) this.headers) ? RequestMappingInfo.EMPTY_HEADERS : new HeadersRequestCondition(this.headers), (!ObjectUtils.isEmpty((Object[]) this.consumes) || this.hasContentType) ? new ConsumesRequestCondition(this.consumes, this.headers) : RequestMappingInfo.EMPTY_CONSUMES, (!ObjectUtils.isEmpty((Object[]) this.produces) || this.hasAccept) ? new ProducesRequestCondition(this.produces, this.headers, manager) : RequestMappingInfo.EMPTY_PRODUCES, this.customCondition != null ? new RequestConditionHolder(this.customCondition) : RequestMappingInfo.EMPTY_CUSTOM, this.options);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/RequestMappingInfo$MutateBuilder.class */
    private static class MutateBuilder implements Builder {

        @Nullable
        private String name;

        @Nullable
        private PathPatternsRequestCondition pathPatternsCondition;

        @Nullable
        private PatternsRequestCondition patternsCondition;
        private RequestMethodsRequestCondition methodsCondition;
        private ParamsRequestCondition paramsCondition;
        private HeadersRequestCondition headersCondition;
        private ConsumesRequestCondition consumesCondition;
        private ProducesRequestCondition producesCondition;
        private RequestConditionHolder customConditionHolder;
        private BuilderConfiguration options;

        public MutateBuilder(RequestMappingInfo other) {
            this.name = other.name;
            this.pathPatternsCondition = other.pathPatternsCondition;
            this.patternsCondition = other.patternsCondition;
            this.methodsCondition = other.methodsCondition;
            this.paramsCondition = other.paramsCondition;
            this.headersCondition = other.headersCondition;
            this.consumesCondition = other.consumesCondition;
            this.producesCondition = other.producesCondition;
            this.customConditionHolder = other.customConditionHolder;
            this.options = other.options;
        }

        @Override // org.springframework.web.servlet.mvc.method.RequestMappingInfo.Builder
        public Builder paths(String... paths) {
            PatternsRequestCondition patternsRequestCondition;
            PathPatternParser parser = this.options.getPatternParserToUse();
            if (parser != null) {
                this.pathPatternsCondition = ObjectUtils.isEmpty((Object[]) paths) ? RequestMappingInfo.EMPTY_PATH_PATTERNS : new PathPatternsRequestCondition(parser, paths);
            } else {
                if (ObjectUtils.isEmpty((Object[]) paths)) {
                    patternsRequestCondition = RequestMappingInfo.EMPTY_PATTERNS;
                } else {
                    patternsRequestCondition = new PatternsRequestCondition(paths, null, this.options.getPathMatcher(), this.options.useSuffixPatternMatch(), this.options.useTrailingSlashMatch(), this.options.getFileExtensions());
                }
                this.patternsCondition = patternsRequestCondition;
            }
            return this;
        }

        @Override // org.springframework.web.servlet.mvc.method.RequestMappingInfo.Builder
        public Builder methods(RequestMethod... methods) {
            this.methodsCondition = ObjectUtils.isEmpty((Object[]) methods) ? RequestMappingInfo.EMPTY_REQUEST_METHODS : new RequestMethodsRequestCondition(methods);
            return this;
        }

        @Override // org.springframework.web.servlet.mvc.method.RequestMappingInfo.Builder
        public Builder params(String... params) {
            this.paramsCondition = ObjectUtils.isEmpty((Object[]) params) ? RequestMappingInfo.EMPTY_PARAMS : new ParamsRequestCondition(params);
            return this;
        }

        @Override // org.springframework.web.servlet.mvc.method.RequestMappingInfo.Builder
        public Builder headers(String... headers) {
            this.headersCondition = ObjectUtils.isEmpty((Object[]) headers) ? RequestMappingInfo.EMPTY_HEADERS : new HeadersRequestCondition(headers);
            return this;
        }

        @Override // org.springframework.web.servlet.mvc.method.RequestMappingInfo.Builder
        public Builder consumes(String... consumes) {
            this.consumesCondition = ObjectUtils.isEmpty((Object[]) consumes) ? RequestMappingInfo.EMPTY_CONSUMES : new ConsumesRequestCondition(consumes);
            return this;
        }

        @Override // org.springframework.web.servlet.mvc.method.RequestMappingInfo.Builder
        public Builder produces(String... produces) {
            ProducesRequestCondition producesRequestCondition;
            if (ObjectUtils.isEmpty((Object[]) produces)) {
                producesRequestCondition = RequestMappingInfo.EMPTY_PRODUCES;
            } else {
                producesRequestCondition = new ProducesRequestCondition(produces, null, this.options.getContentNegotiationManager());
            }
            this.producesCondition = producesRequestCondition;
            return this;
        }

        @Override // org.springframework.web.servlet.mvc.method.RequestMappingInfo.Builder
        public Builder mappingName(String name) {
            this.name = name;
            return this;
        }

        @Override // org.springframework.web.servlet.mvc.method.RequestMappingInfo.Builder
        public Builder customCondition(RequestCondition<?> condition) {
            this.customConditionHolder = new RequestConditionHolder(condition);
            return this;
        }

        @Override // org.springframework.web.servlet.mvc.method.RequestMappingInfo.Builder
        public Builder options(BuilderConfiguration options) {
            this.options = options;
            return this;
        }

        @Override // org.springframework.web.servlet.mvc.method.RequestMappingInfo.Builder
        public RequestMappingInfo build() {
            return new RequestMappingInfo(this.name, this.pathPatternsCondition, this.patternsCondition, this.methodsCondition, this.paramsCondition, this.headersCondition, this.consumesCondition, this.producesCondition, this.customConditionHolder, this.options);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/RequestMappingInfo$BuilderConfiguration.class */
    public static class BuilderConfiguration {
        private static PathPatternParser defaultPatternParser = new PathPatternParser();

        @Nullable
        private PathPatternParser patternParser;

        @Nullable
        private PathMatcher pathMatcher;
        private boolean trailingSlashMatch = false;
        private boolean suffixPatternMatch = false;
        private boolean registeredSuffixPatternMatch = false;

        @Nullable
        private ContentNegotiationManager contentNegotiationManager;

        public void setPatternParser(@Nullable PathPatternParser patternParser) {
            this.patternParser = patternParser;
        }

        @Nullable
        public PathPatternParser getPatternParser() {
            return this.patternParser;
        }

        @Deprecated
        public void setUrlPathHelper(@Nullable UrlPathHelper urlPathHelper) {
        }

        @Nullable
        @Deprecated
        public UrlPathHelper getUrlPathHelper() {
            return UrlPathHelper.defaultInstance;
        }

        public void setPathMatcher(@Nullable PathMatcher pathMatcher) {
            this.pathMatcher = pathMatcher;
        }

        @Nullable
        public PathMatcher getPathMatcher() {
            return this.pathMatcher;
        }

        @Nullable
        public PathPatternParser getPatternParserToUse() {
            if (this.patternParser == null && this.pathMatcher == null) {
                return defaultPatternParser;
            }
            return this.patternParser;
        }

        @Deprecated(since = "6.0")
        public void setTrailingSlashMatch(boolean trailingSlashMatch) {
            this.trailingSlashMatch = trailingSlashMatch;
        }

        @Deprecated(since = "6.0")
        public boolean useTrailingSlashMatch() {
            return this.trailingSlashMatch;
        }

        @Deprecated
        public void setSuffixPatternMatch(boolean suffixPatternMatch) {
            this.suffixPatternMatch = suffixPatternMatch;
        }

        @Deprecated
        public boolean useSuffixPatternMatch() {
            return this.suffixPatternMatch;
        }

        @Deprecated
        public void setRegisteredSuffixPatternMatch(boolean registeredSuffixPatternMatch) {
            this.registeredSuffixPatternMatch = registeredSuffixPatternMatch;
            this.suffixPatternMatch = registeredSuffixPatternMatch || this.suffixPatternMatch;
        }

        @Deprecated
        public boolean useRegisteredSuffixPatternMatch() {
            return this.registeredSuffixPatternMatch;
        }

        @Nullable
        @Deprecated
        public List<String> getFileExtensions() {
            if (useRegisteredSuffixPatternMatch() && this.contentNegotiationManager != null) {
                return this.contentNegotiationManager.getAllFileExtensions();
            }
            return null;
        }

        public void setContentNegotiationManager(ContentNegotiationManager contentNegotiationManager) {
            this.contentNegotiationManager = contentNegotiationManager;
        }

        @Nullable
        public ContentNegotiationManager getContentNegotiationManager() {
            return this.contentNegotiationManager;
        }
    }
}

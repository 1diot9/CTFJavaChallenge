package org.springframework.web.servlet.mvc.method;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.server.PathContainer;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.filter.ServerHttpObservationFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.AbstractHandlerMethodMapping;
import org.springframework.web.servlet.mvc.condition.NameValueExpression;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.ProducesRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.util.ServletRequestPathUtils;
import org.springframework.web.util.WebUtils;
import org.springframework.web.util.pattern.PathPattern;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/RequestMappingInfoHandlerMapping.class */
public abstract class RequestMappingInfoHandlerMapping extends AbstractHandlerMethodMapping<RequestMappingInfo> {
    private static final Method HTTP_OPTIONS_HANDLE_METHOD;

    static {
        try {
            HTTP_OPTIONS_HANDLE_METHOD = HttpOptionsHandler.class.getMethod("handle", new Class[0]);
        } catch (NoSuchMethodException ex) {
            throw new IllegalStateException("Failed to retrieve internal handler method for HTTP OPTIONS", ex);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public RequestMappingInfoHandlerMapping() {
        setHandlerMethodMappingNamingStrategy(new RequestMappingInfoHandlerMethodMappingNamingStrategy());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.handler.AbstractHandlerMethodMapping
    public Set<String> getMappingPathPatterns(RequestMappingInfo info) {
        return info.getPatternValues();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.handler.AbstractHandlerMethodMapping
    public Set<String> getDirectPaths(RequestMappingInfo info) {
        return info.getDirectPaths();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.handler.AbstractHandlerMethodMapping
    public RequestMappingInfo getMatchingMapping(RequestMappingInfo info, HttpServletRequest request) {
        return info.getMatchingCondition(request);
    }

    @Override // org.springframework.web.servlet.handler.AbstractHandlerMethodMapping
    protected Comparator<RequestMappingInfo> getMappingComparator(final HttpServletRequest request) {
        return (info1, info2) -> {
            return info1.compareTo(info2, request);
        };
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.handler.AbstractHandlerMethodMapping, org.springframework.web.servlet.handler.AbstractHandlerMapping
    @Nullable
    public HandlerMethod getHandlerInternal(HttpServletRequest request) throws Exception {
        request.removeAttribute(PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE);
        try {
            return super.getHandlerInternal(request);
        } finally {
            ProducesRequestCondition.clearMediaTypesAttribute(request);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.handler.AbstractHandlerMethodMapping
    public void handleMatch(RequestMappingInfo info, String lookupPath, HttpServletRequest request) {
        super.handleMatch((RequestMappingInfoHandlerMapping) info, lookupPath, request);
        RequestCondition<?> condition = info.getActivePatternsCondition();
        if (condition instanceof PathPatternsRequestCondition) {
            PathPatternsRequestCondition pprc = (PathPatternsRequestCondition) condition;
            extractMatchDetails(pprc, lookupPath, request);
        } else {
            extractMatchDetails((PatternsRequestCondition) condition, lookupPath, request);
        }
        ProducesRequestCondition producesCondition = info.getProducesCondition();
        if (!producesCondition.isEmpty()) {
            Set<MediaType> mediaTypes = producesCondition.getProducibleMediaTypes();
            if (!mediaTypes.isEmpty()) {
                request.setAttribute(PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE, mediaTypes);
            }
        }
    }

    private void extractMatchDetails(PathPatternsRequestCondition condition, String lookupPath, HttpServletRequest request) {
        PathPattern bestPattern;
        Map<String, String> uriVariables;
        if (condition.isEmptyPathMapping()) {
            bestPattern = condition.getFirstPattern();
            uriVariables = Collections.emptyMap();
        } else {
            PathContainer path = ServletRequestPathUtils.getParsedRequestPath(request).pathWithinApplication();
            bestPattern = condition.getFirstPattern();
            PathPattern.PathMatchInfo result = bestPattern.matchAndExtract(path);
            Assert.notNull(result, (Supplier<String>) () -> {
                return "Expected bestPattern: " + bestPattern + " to match lookupPath " + path;
            });
            uriVariables = result.getUriVariables();
            request.setAttribute(MATRIX_VARIABLES_ATTRIBUTE, result.getMatrixVariables());
        }
        request.setAttribute(BEST_MATCHING_PATTERN_ATTRIBUTE, bestPattern.getPatternString());
        PathPattern pathPattern = bestPattern;
        ServerHttpObservationFilter.findObservationContext(request).ifPresent(context -> {
            context.setPathPattern(pathPattern.getPatternString());
        });
        request.setAttribute(URI_TEMPLATE_VARIABLES_ATTRIBUTE, uriVariables);
    }

    private void extractMatchDetails(PatternsRequestCondition condition, String lookupPath, HttpServletRequest request) {
        String bestPattern;
        Map<String, String> uriVariables;
        if (condition.isEmptyPathMapping()) {
            bestPattern = lookupPath;
            uriVariables = Collections.emptyMap();
        } else {
            bestPattern = condition.getPatterns().iterator().next();
            Map<String, String> uriVariables2 = getPathMatcher().extractUriTemplateVariables(bestPattern, lookupPath);
            if (!getUrlPathHelper().shouldRemoveSemicolonContent()) {
                request.setAttribute(MATRIX_VARIABLES_ATTRIBUTE, extractMatrixVariables(request, uriVariables2));
            }
            uriVariables = getUrlPathHelper().decodePathVariables(request, uriVariables2);
        }
        request.setAttribute(BEST_MATCHING_PATTERN_ATTRIBUTE, bestPattern);
        String str = bestPattern;
        ServerHttpObservationFilter.findObservationContext(request).ifPresent(context -> {
            context.setPathPattern(str);
        });
        request.setAttribute(URI_TEMPLATE_VARIABLES_ATTRIBUTE, uriVariables);
    }

    private Map<String, MultiValueMap<String, String>> extractMatrixVariables(HttpServletRequest request, Map<String, String> uriVariables) {
        Map<String, MultiValueMap<String, String>> result = new LinkedHashMap<>();
        uriVariables.forEach((uriVarKey, uriVarValue) -> {
            String matrixVariables;
            int equalsIndex = uriVarValue.indexOf(61);
            if (equalsIndex == -1) {
                return;
            }
            int semicolonIndex = uriVarValue.indexOf(59);
            if (semicolonIndex != -1 && semicolonIndex != 0) {
                uriVariables.put(uriVarKey, uriVarValue.substring(0, semicolonIndex));
            }
            if (semicolonIndex == -1 || semicolonIndex == 0 || equalsIndex < semicolonIndex) {
                matrixVariables = uriVarValue;
            } else {
                matrixVariables = uriVarValue.substring(semicolonIndex + 1);
            }
            MultiValueMap<String, String> vars = WebUtils.parseMatrixVariables(matrixVariables);
            result.put(uriVarKey, getUrlPathHelper().decodeMatrixVariables(request, vars));
        });
        return result;
    }

    @Override // org.springframework.web.servlet.handler.AbstractHandlerMethodMapping
    protected HandlerMethod handleNoMatch(Set<RequestMappingInfo> infos, String lookupPath, HttpServletRequest request) throws ServletException {
        if (CollectionUtils.isEmpty(infos)) {
            return null;
        }
        PartialMatchHelper helper = new PartialMatchHelper(infos, request);
        if (helper.isEmpty()) {
            return null;
        }
        if (helper.hasMethodsMismatch()) {
            Set<String> methods = helper.getAllowedMethods();
            if (HttpMethod.OPTIONS.matches(request.getMethod())) {
                HttpOptionsHandler handler = new HttpOptionsHandler(methods, helper.getConsumablePatchMediaTypes());
                return new HandlerMethod(handler, HTTP_OPTIONS_HANDLE_METHOD);
            }
            throw new HttpRequestMethodNotSupportedException(request.getMethod(), methods);
        }
        if (helper.hasConsumesMismatch()) {
            Set<MediaType> mediaTypes = helper.getConsumableMediaTypes();
            MediaType contentType = null;
            if (StringUtils.hasLength(request.getContentType())) {
                try {
                    contentType = MediaType.parseMediaType(request.getContentType());
                } catch (InvalidMediaTypeException ex) {
                    throw new HttpMediaTypeNotSupportedException(ex.getMessage(), new ArrayList(mediaTypes));
                }
            }
            throw new HttpMediaTypeNotSupportedException(contentType, new ArrayList(mediaTypes), HttpMethod.valueOf(request.getMethod()));
        }
        if (helper.hasProducesMismatch()) {
            throw new HttpMediaTypeNotAcceptableException(new ArrayList(helper.getProducibleMediaTypes()));
        }
        if (helper.hasParamsMismatch()) {
            List<String[]> conditions = helper.getParamConditions();
            throw new UnsatisfiedServletRequestParameterException(conditions, request.getParameterMap());
        }
        return null;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/RequestMappingInfoHandlerMapping$PartialMatchHelper.class */
    private static final class PartialMatchHelper {
        private final List<PartialMatch> partialMatches = new ArrayList();

        PartialMatchHelper(Set<RequestMappingInfo> infos, HttpServletRequest request) {
            for (RequestMappingInfo info : infos) {
                if (info.getActivePatternsCondition().getMatchingCondition(request) != null) {
                    this.partialMatches.add(new PartialMatch(info, request));
                }
            }
        }

        public boolean isEmpty() {
            return this.partialMatches.isEmpty();
        }

        public boolean hasMethodsMismatch() {
            for (PartialMatch match : this.partialMatches) {
                if (match.hasMethodsMatch()) {
                    return false;
                }
            }
            return true;
        }

        public boolean hasConsumesMismatch() {
            for (PartialMatch match : this.partialMatches) {
                if (match.hasConsumesMatch()) {
                    return false;
                }
            }
            return true;
        }

        public boolean hasProducesMismatch() {
            for (PartialMatch match : this.partialMatches) {
                if (match.hasProducesMatch()) {
                    return false;
                }
            }
            return true;
        }

        public boolean hasParamsMismatch() {
            for (PartialMatch match : this.partialMatches) {
                if (match.hasParamsMatch()) {
                    return false;
                }
            }
            return true;
        }

        public Set<String> getAllowedMethods() {
            Set<String> result = new LinkedHashSet<>();
            for (PartialMatch match : this.partialMatches) {
                for (RequestMethod method : match.getInfo().getMethodsCondition().getMethods()) {
                    result.add(method.name());
                }
            }
            return result;
        }

        public Set<MediaType> getConsumableMediaTypes() {
            Set<MediaType> result = new LinkedHashSet<>();
            for (PartialMatch match : this.partialMatches) {
                if (match.hasMethodsMatch()) {
                    result.addAll(match.getInfo().getConsumesCondition().getConsumableMediaTypes());
                }
            }
            return result;
        }

        public Set<MediaType> getProducibleMediaTypes() {
            Set<MediaType> result = new LinkedHashSet<>();
            for (PartialMatch match : this.partialMatches) {
                if (match.hasConsumesMatch()) {
                    result.addAll(match.getInfo().getProducesCondition().getProducibleMediaTypes());
                }
            }
            return result;
        }

        public List<String[]> getParamConditions() {
            List<String[]> result = new ArrayList<>();
            for (PartialMatch match : this.partialMatches) {
                if (match.hasProducesMatch()) {
                    Set<NameValueExpression<String>> set = match.getInfo().getParamsCondition().getExpressions();
                    if (!CollectionUtils.isEmpty(set)) {
                        int i = 0;
                        String[] array = new String[set.size()];
                        for (NameValueExpression<String> expression : set) {
                            int i2 = i;
                            i++;
                            array[i2] = expression.toString();
                        }
                        result.add(array);
                    }
                }
            }
            return result;
        }

        public Set<MediaType> getConsumablePatchMediaTypes() {
            Set<MediaType> result = new LinkedHashSet<>();
            for (PartialMatch match : this.partialMatches) {
                Set<RequestMethod> methods = match.getInfo().getMethodsCondition().getMethods();
                if (methods.isEmpty() || methods.contains(RequestMethod.PATCH)) {
                    result.addAll(match.getInfo().getConsumesCondition().getConsumableMediaTypes());
                }
            }
            return result;
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/RequestMappingInfoHandlerMapping$PartialMatchHelper$PartialMatch.class */
        public static class PartialMatch {
            private final RequestMappingInfo info;
            private final boolean methodsMatch;
            private final boolean consumesMatch;
            private final boolean producesMatch;
            private final boolean paramsMatch;

            public PartialMatch(RequestMappingInfo info, HttpServletRequest request) {
                this.info = info;
                this.methodsMatch = info.getMethodsCondition().getMatchingCondition(request) != null;
                this.consumesMatch = info.getConsumesCondition().getMatchingCondition(request) != null;
                this.producesMatch = info.getProducesCondition().getMatchingCondition(request) != null;
                this.paramsMatch = info.getParamsCondition().getMatchingCondition(request) != null;
            }

            public RequestMappingInfo getInfo() {
                return this.info;
            }

            public boolean hasMethodsMatch() {
                return this.methodsMatch;
            }

            public boolean hasConsumesMatch() {
                return hasMethodsMatch() && this.consumesMatch;
            }

            public boolean hasProducesMatch() {
                return hasConsumesMatch() && this.producesMatch;
            }

            public boolean hasParamsMatch() {
                return hasProducesMatch() && this.paramsMatch;
            }

            public String toString() {
                return this.info.toString();
            }
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/RequestMappingInfoHandlerMapping$HttpOptionsHandler.class */
    private static class HttpOptionsHandler {
        private final HttpHeaders headers = new HttpHeaders();

        public HttpOptionsHandler(Set<String> declaredMethods, Set<MediaType> acceptPatch) {
            this.headers.setAllow(initAllowedHttpMethods(declaredMethods));
            this.headers.setAcceptPatch(new ArrayList(acceptPatch));
        }

        private static Set<HttpMethod> initAllowedHttpMethods(Set<String> declaredMethods) {
            Set<HttpMethod> result = new LinkedHashSet<>(declaredMethods.size());
            if (declaredMethods.isEmpty()) {
                for (HttpMethod method : HttpMethod.values()) {
                    if (method != HttpMethod.TRACE) {
                        result.add(method);
                    }
                }
            } else {
                Iterator<String> it = declaredMethods.iterator();
                while (it.hasNext()) {
                    HttpMethod httpMethod = HttpMethod.valueOf(it.next());
                    result.add(httpMethod);
                    if (httpMethod == HttpMethod.GET) {
                        result.add(HttpMethod.HEAD);
                    }
                }
                result.add(HttpMethod.OPTIONS);
            }
            return result;
        }

        public HttpHeaders handle() {
            return this.headers;
        }
    }
}

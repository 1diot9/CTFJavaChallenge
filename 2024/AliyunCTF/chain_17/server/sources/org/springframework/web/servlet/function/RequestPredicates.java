package org.springframework.web.servlet.function;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.security.Principal;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.PathContainer;
import org.springframework.http.server.RequestPath;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.function.ChangePathPatternParserVisitor;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/RequestPredicates.class */
public abstract class RequestPredicates {
    private static final Log logger = LogFactory.getLog((Class<?>) RequestPredicates.class);

    /* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/RequestPredicates$Visitor.class */
    public interface Visitor {
        void method(Set<HttpMethod> methods);

        void path(String pattern);

        void pathExtension(String extension);

        void header(String name, String value);

        void param(String name, String value);

        void startAnd();

        void and();

        void endAnd();

        void startOr();

        void or();

        void endOr();

        void startNegate();

        void endNegate();

        void unknown(RequestPredicate predicate);
    }

    public static RequestPredicate all() {
        return request -> {
            return true;
        };
    }

    public static RequestPredicate method(HttpMethod httpMethod) {
        return new HttpMethodPredicate(httpMethod);
    }

    public static RequestPredicate methods(HttpMethod... httpMethods) {
        return new HttpMethodPredicate(httpMethods);
    }

    public static RequestPredicate path(String pattern) {
        Assert.notNull(pattern, "'pattern' must not be null");
        PathPatternParser parser = PathPatternParser.defaultInstance;
        return pathPredicates(parser).apply(parser.initFullPathPattern(pattern));
    }

    public static Function<String, RequestPredicate> pathPredicates(PathPatternParser patternParser) {
        Assert.notNull(patternParser, "PathPatternParser must not be null");
        return pattern -> {
            return new PathPatternPredicate(patternParser.parse(pattern));
        };
    }

    public static RequestPredicate headers(Predicate<ServerRequest.Headers> headersPredicate) {
        return new HeadersPredicate(headersPredicate);
    }

    public static RequestPredicate contentType(MediaType... mediaTypes) {
        Assert.notEmpty(mediaTypes, "'mediaTypes' must not be empty");
        return new ContentTypePredicate(mediaTypes);
    }

    public static RequestPredicate accept(MediaType... mediaTypes) {
        Assert.notEmpty(mediaTypes, "'mediaTypes' must not be empty");
        return new AcceptPredicate(mediaTypes);
    }

    public static RequestPredicate GET(String pattern) {
        return method(HttpMethod.GET).and(path(pattern));
    }

    public static RequestPredicate HEAD(String pattern) {
        return method(HttpMethod.HEAD).and(path(pattern));
    }

    public static RequestPredicate POST(String pattern) {
        return method(HttpMethod.POST).and(path(pattern));
    }

    public static RequestPredicate PUT(String pattern) {
        return method(HttpMethod.PUT).and(path(pattern));
    }

    public static RequestPredicate PATCH(String pattern) {
        return method(HttpMethod.PATCH).and(path(pattern));
    }

    public static RequestPredicate DELETE(String pattern) {
        return method(HttpMethod.DELETE).and(path(pattern));
    }

    public static RequestPredicate OPTIONS(String pattern) {
        return method(HttpMethod.OPTIONS).and(path(pattern));
    }

    public static RequestPredicate pathExtension(String extension) {
        Assert.notNull(extension, "'extension' must not be null");
        return new PathExtensionPredicate(extension);
    }

    public static RequestPredicate pathExtension(Predicate<String> extensionPredicate) {
        return new PathExtensionPredicate(extensionPredicate);
    }

    public static RequestPredicate param(String name, String value) {
        return new ParamPredicate(name, value);
    }

    public static RequestPredicate param(String name, Predicate<String> predicate) {
        return new ParamPredicate(name, predicate);
    }

    private static void traceMatch(String prefix, Object desired, @Nullable Object actual, boolean match) {
        if (logger.isTraceEnabled()) {
            Log log = logger;
            Object[] objArr = new Object[4];
            objArr[0] = prefix;
            objArr[1] = desired;
            objArr[2] = match ? "matches" : "does not match";
            objArr[3] = actual;
            log.trace(String.format("%s \"%s\" %s against value \"%s\"", objArr));
        }
    }

    private static PathPattern mergePatterns(@Nullable PathPattern oldPattern, PathPattern newPattern) {
        if (oldPattern != null) {
            return oldPattern.combine(newPattern);
        }
        return newPattern;
    }

    private static <K, V> Map<K, V> mergeMaps(Map<K, V> left, Map<K, V> right) {
        if (left.isEmpty()) {
            if (right.isEmpty()) {
                return Collections.emptyMap();
            }
            return right;
        }
        if (right.isEmpty()) {
            return left;
        }
        Map<K, V> result = new LinkedHashMap<>(left.size() + right.size());
        result.putAll(left);
        result.putAll(right);
        return result;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/RequestPredicates$RequestModifyingPredicate.class */
    private static abstract class RequestModifyingPredicate implements RequestPredicate {
        protected abstract Result testInternal(ServerRequest request);

        private RequestModifyingPredicate() {
        }

        public static RequestModifyingPredicate of(final RequestPredicate requestPredicate) {
            if (requestPredicate instanceof RequestModifyingPredicate) {
                RequestModifyingPredicate modifyingPredicate = (RequestModifyingPredicate) requestPredicate;
                return modifyingPredicate;
            }
            return new RequestModifyingPredicate() { // from class: org.springframework.web.servlet.function.RequestPredicates.RequestModifyingPredicate.1
                @Override // org.springframework.web.servlet.function.RequestPredicates.RequestModifyingPredicate
                protected Result testInternal(ServerRequest request) {
                    return Result.of(RequestPredicate.this.test(request));
                }
            };
        }

        @Override // org.springframework.web.servlet.function.RequestPredicate
        public final boolean test(ServerRequest request) {
            Result result = testInternal(request);
            boolean value = result.value();
            if (value) {
                result.modifyAttributes(request.attributes());
            }
            return value;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/RequestPredicates$RequestModifyingPredicate$Result.class */
        public static final class Result {
            private static final Result TRUE = new Result(true, null);
            private static final Result FALSE = new Result(false, null);
            private final boolean value;

            @Nullable
            private final Consumer<Map<String, Object>> modifyAttributes;

            private Result(boolean value, @Nullable Consumer<Map<String, Object>> modifyAttributes) {
                this.value = value;
                this.modifyAttributes = modifyAttributes;
            }

            public static Result of(boolean value) {
                return of(value, null);
            }

            public static Result of(boolean value, @Nullable Consumer<Map<String, Object>> modifyAttributes) {
                if (modifyAttributes == null) {
                    return value ? TRUE : FALSE;
                }
                return new Result(value, modifyAttributes);
            }

            public boolean value() {
                return this.value;
            }

            public void modifyAttributes(Map<String, Object> attributes) {
                if (this.modifyAttributes != null) {
                    this.modifyAttributes.accept(attributes);
                }
            }

            public boolean modifiesAttributes() {
                return this.modifyAttributes != null;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/RequestPredicates$HttpMethodPredicate.class */
    public static class HttpMethodPredicate implements RequestPredicate {
        private final Set<HttpMethod> httpMethods;

        public HttpMethodPredicate(HttpMethod httpMethod) {
            Assert.notNull(httpMethod, "HttpMethod must not be null");
            this.httpMethods = Set.of(httpMethod);
        }

        public HttpMethodPredicate(HttpMethod... httpMethods) {
            Assert.notEmpty(httpMethods, "HttpMethods must not be empty");
            this.httpMethods = new LinkedHashSet(Arrays.asList(httpMethods));
        }

        @Override // org.springframework.web.servlet.function.RequestPredicate
        public boolean test(ServerRequest request) {
            HttpMethod method = method(request);
            boolean match = this.httpMethods.contains(method);
            RequestPredicates.traceMatch("Method", this.httpMethods, method, match);
            return match;
        }

        private static HttpMethod method(ServerRequest request) {
            String accessControlRequestMethod;
            if (CorsUtils.isPreFlightRequest(request.servletRequest()) && (accessControlRequestMethod = request.headers().firstHeader("Access-Control-Request-Method")) != null) {
                return HttpMethod.valueOf(accessControlRequestMethod);
            }
            return request.method();
        }

        @Override // org.springframework.web.servlet.function.RequestPredicate
        public void accept(Visitor visitor) {
            visitor.method(Collections.unmodifiableSet(this.httpMethods));
        }

        public String toString() {
            if (this.httpMethods.size() == 1) {
                return this.httpMethods.iterator().next().toString();
            }
            return this.httpMethods.toString();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/RequestPredicates$PathPatternPredicate.class */
    public static class PathPatternPredicate extends RequestModifyingPredicate implements ChangePathPatternParserVisitor.Target {
        private PathPattern pattern;

        public PathPatternPredicate(PathPattern pattern) {
            Assert.notNull(pattern, "'pattern' must not be null");
            this.pattern = pattern;
        }

        @Override // org.springframework.web.servlet.function.RequestPredicates.RequestModifyingPredicate
        protected RequestModifyingPredicate.Result testInternal(ServerRequest request) {
            PathContainer pathContainer = request.requestPath().pathWithinApplication();
            PathPattern.PathMatchInfo info = this.pattern.matchAndExtract(pathContainer);
            RequestPredicates.traceMatch("Pattern", this.pattern.getPatternString(), request.path(), info != null);
            if (info != null) {
                return RequestModifyingPredicate.Result.of(true, attributes -> {
                    modifyAttributes(attributes, request, info.getUriVariables());
                });
            }
            return RequestModifyingPredicate.Result.of(false);
        }

        private void modifyAttributes(Map<String, Object> attributes, ServerRequest request, Map<String, String> variables) {
            Map<String, String> pathVariables = RequestPredicates.mergeMaps(request.pathVariables(), variables);
            attributes.put(RouterFunctions.URI_TEMPLATE_VARIABLES_ATTRIBUTE, Collections.unmodifiableMap(pathVariables));
            PathPattern pattern = RequestPredicates.mergePatterns((PathPattern) attributes.get(RouterFunctions.MATCHING_PATTERN_ATTRIBUTE), this.pattern);
            attributes.put(RouterFunctions.MATCHING_PATTERN_ATTRIBUTE, pattern);
        }

        @Override // org.springframework.web.servlet.function.RequestPredicate
        public Optional<ServerRequest> nest(ServerRequest request) {
            return Optional.ofNullable(this.pattern.matchStartOfPath(request.requestPath().pathWithinApplication())).map(info -> {
                return new NestedPathPatternServerRequestWrapper(request, info, this.pattern);
            });
        }

        @Override // org.springframework.web.servlet.function.RequestPredicate
        public void accept(Visitor visitor) {
            visitor.path(this.pattern.getPatternString());
        }

        @Override // org.springframework.web.servlet.function.ChangePathPatternParserVisitor.Target
        public void changeParser(PathPatternParser parser) {
            String patternString = this.pattern.getPatternString();
            this.pattern = parser.parse(patternString);
        }

        public String toString() {
            return this.pattern.getPatternString();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/RequestPredicates$HeadersPredicate.class */
    private static class HeadersPredicate implements RequestPredicate {
        private final Predicate<ServerRequest.Headers> headersPredicate;

        public HeadersPredicate(Predicate<ServerRequest.Headers> headersPredicate) {
            Assert.notNull(headersPredicate, "Predicate must not be null");
            this.headersPredicate = headersPredicate;
        }

        @Override // org.springframework.web.servlet.function.RequestPredicate
        public boolean test(ServerRequest request) {
            if (CorsUtils.isPreFlightRequest(request.servletRequest())) {
                return true;
            }
            return this.headersPredicate.test(request.headers());
        }

        public String toString() {
            return this.headersPredicate.toString();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/RequestPredicates$ContentTypePredicate.class */
    private static class ContentTypePredicate extends HeadersPredicate {
        private final Set<MediaType> mediaTypes;

        public ContentTypePredicate(MediaType... mediaTypes) {
            this((Set<MediaType>) Set.of((Object[]) mediaTypes));
        }

        private ContentTypePredicate(Set<MediaType> mediaTypes) {
            super(headers -> {
                MediaType contentType = headers.contentType().orElse(MediaType.APPLICATION_OCTET_STREAM);
                boolean match = mediaTypes.stream().anyMatch(mediaType -> {
                    return mediaType.includes(contentType);
                });
                RequestPredicates.traceMatch(HttpHeaders.CONTENT_TYPE, mediaTypes, contentType, match);
                return match;
            });
            this.mediaTypes = mediaTypes;
        }

        @Override // org.springframework.web.servlet.function.RequestPredicate
        public void accept(Visitor visitor) {
            String obj;
            if (this.mediaTypes.size() == 1) {
                obj = this.mediaTypes.iterator().next().toString();
            } else {
                obj = this.mediaTypes.toString();
            }
            visitor.header(HttpHeaders.CONTENT_TYPE, obj);
        }

        @Override // org.springframework.web.servlet.function.RequestPredicates.HeadersPredicate
        public String toString() {
            String obj;
            Object[] objArr = new Object[1];
            if (this.mediaTypes.size() == 1) {
                obj = this.mediaTypes.iterator().next().toString();
            } else {
                obj = this.mediaTypes.toString();
            }
            objArr[0] = obj;
            return String.format("Content-Type: %s", objArr);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/RequestPredicates$AcceptPredicate.class */
    private static class AcceptPredicate extends HeadersPredicate {
        private final Set<MediaType> mediaTypes;

        public AcceptPredicate(MediaType... mediaTypes) {
            this((Set<MediaType>) Set.of((Object[]) mediaTypes));
        }

        private AcceptPredicate(Set<MediaType> mediaTypes) {
            super(headers -> {
                List<MediaType> acceptedMediaTypes = acceptedMediaTypes(headers);
                boolean match = acceptedMediaTypes.stream().anyMatch(acceptedMediaType -> {
                    Stream stream = mediaTypes.stream();
                    Objects.requireNonNull(acceptedMediaType);
                    return stream.anyMatch(acceptedMediaType::isCompatibleWith);
                });
                RequestPredicates.traceMatch(HttpHeaders.ACCEPT, mediaTypes, acceptedMediaTypes, match);
                return match;
            });
            this.mediaTypes = mediaTypes;
        }

        @NonNull
        private static List<MediaType> acceptedMediaTypes(ServerRequest.Headers headers) {
            List<MediaType> acceptedMediaTypes = headers.accept();
            if (acceptedMediaTypes.isEmpty()) {
                acceptedMediaTypes = Collections.singletonList(MediaType.ALL);
            } else {
                MimeTypeUtils.sortBySpecificity(acceptedMediaTypes);
            }
            return acceptedMediaTypes;
        }

        @Override // org.springframework.web.servlet.function.RequestPredicate
        public void accept(Visitor visitor) {
            String obj;
            if (this.mediaTypes.size() == 1) {
                obj = this.mediaTypes.iterator().next().toString();
            } else {
                obj = this.mediaTypes.toString();
            }
            visitor.header(HttpHeaders.ACCEPT, obj);
        }

        @Override // org.springframework.web.servlet.function.RequestPredicates.HeadersPredicate
        public String toString() {
            String obj;
            Object[] objArr = new Object[1];
            if (this.mediaTypes.size() == 1) {
                obj = this.mediaTypes.iterator().next().toString();
            } else {
                obj = this.mediaTypes.toString();
            }
            objArr[0] = obj;
            return String.format("Accept: %s", objArr);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/RequestPredicates$PathExtensionPredicate.class */
    private static class PathExtensionPredicate implements RequestPredicate {
        private final Predicate<String> extensionPredicate;

        @Nullable
        private final String extension;

        public PathExtensionPredicate(Predicate<String> extensionPredicate) {
            Assert.notNull(extensionPredicate, "Predicate must not be null");
            this.extensionPredicate = extensionPredicate;
            this.extension = null;
        }

        public PathExtensionPredicate(String extension) {
            Assert.notNull(extension, "Extension must not be null");
            this.extensionPredicate = s -> {
                boolean match = extension.equalsIgnoreCase(s);
                RequestPredicates.traceMatch("Extension", extension, s, match);
                return match;
            };
            this.extension = extension;
        }

        @Override // org.springframework.web.servlet.function.RequestPredicate
        public boolean test(ServerRequest request) {
            String pathExtension = UriUtils.extractFileExtension(request.path());
            return this.extensionPredicate.test(pathExtension);
        }

        @Override // org.springframework.web.servlet.function.RequestPredicate
        public void accept(Visitor visitor) {
            String obj;
            if (this.extension != null) {
                obj = this.extension;
            } else {
                obj = this.extensionPredicate.toString();
            }
            visitor.pathExtension(obj);
        }

        public String toString() {
            Object obj;
            Object[] objArr = new Object[1];
            if (this.extension != null) {
                obj = this.extension;
            } else {
                obj = this.extensionPredicate;
            }
            objArr[0] = obj;
            return String.format("*.%s", objArr);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/RequestPredicates$ParamPredicate.class */
    private static class ParamPredicate implements RequestPredicate {
        private final String name;
        private final Predicate<String> valuePredicate;

        @Nullable
        private final String value;

        public ParamPredicate(String name, Predicate<String> valuePredicate) {
            Assert.notNull(name, "Name must not be null");
            Assert.notNull(valuePredicate, "Predicate must not be null");
            this.name = name;
            this.valuePredicate = valuePredicate;
            this.value = null;
        }

        public ParamPredicate(String name, String value) {
            Assert.notNull(name, "Name must not be null");
            Assert.notNull(value, "Value must not be null");
            this.name = name;
            Objects.requireNonNull(value);
            this.valuePredicate = (v1) -> {
                return r1.equals(v1);
            };
            this.value = value;
        }

        @Override // org.springframework.web.servlet.function.RequestPredicate
        public boolean test(ServerRequest request) {
            Optional<String> s = request.param(this.name);
            return s.filter(this.valuePredicate).isPresent();
        }

        @Override // org.springframework.web.servlet.function.RequestPredicate
        public void accept(Visitor visitor) {
            String obj;
            String str = this.name;
            if (this.value != null) {
                obj = this.value;
            } else {
                obj = this.valuePredicate.toString();
            }
            visitor.param(str, obj);
        }

        public String toString() {
            Object obj;
            Object[] objArr = new Object[2];
            objArr[0] = this.name;
            if (this.value != null) {
                obj = this.value;
            } else {
                obj = this.valuePredicate;
            }
            objArr[1] = obj;
            return String.format("?%s %s", objArr);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/RequestPredicates$AndRequestPredicate.class */
    public static class AndRequestPredicate extends RequestModifyingPredicate implements ChangePathPatternParserVisitor.Target {
        private final RequestPredicate left;
        private final RequestModifyingPredicate leftModifying;
        private final RequestPredicate right;
        private final RequestModifyingPredicate rightModifying;

        public AndRequestPredicate(RequestPredicate left, RequestPredicate right) {
            Assert.notNull(left, "Left RequestPredicate must not be null");
            Assert.notNull(right, "Right RequestPredicate must not be null");
            this.left = left;
            this.leftModifying = of(left);
            this.right = right;
            this.rightModifying = of(right);
        }

        @Override // org.springframework.web.servlet.function.RequestPredicates.RequestModifyingPredicate
        protected RequestModifyingPredicate.Result testInternal(ServerRequest request) {
            ServerRequest rightRequest;
            RequestModifyingPredicate.Result leftResult = this.leftModifying.testInternal(request);
            if (!leftResult.value()) {
                return leftResult;
            }
            if (leftResult.modifiesAttributes()) {
                Map<String, Object> leftAttributes = new LinkedHashMap<>(2);
                leftResult.modifyAttributes(leftAttributes);
                rightRequest = new ExtendedAttributesServerRequestWrapper(request, leftAttributes);
            } else {
                rightRequest = request;
            }
            RequestModifyingPredicate.Result rightResult = this.rightModifying.testInternal(rightRequest);
            if (!rightResult.value()) {
                return rightResult;
            }
            return RequestModifyingPredicate.Result.of(true, attributes -> {
                leftResult.modifyAttributes(attributes);
                rightResult.modifyAttributes(attributes);
            });
        }

        @Override // org.springframework.web.servlet.function.RequestPredicate
        public Optional<ServerRequest> nest(ServerRequest request) {
            Optional<ServerRequest> nest = this.left.nest(request);
            RequestPredicate requestPredicate = this.right;
            Objects.requireNonNull(requestPredicate);
            return nest.flatMap(requestPredicate::nest);
        }

        @Override // org.springframework.web.servlet.function.RequestPredicate
        public void accept(Visitor visitor) {
            visitor.startAnd();
            this.left.accept(visitor);
            visitor.and();
            this.right.accept(visitor);
            visitor.endAnd();
        }

        @Override // org.springframework.web.servlet.function.ChangePathPatternParserVisitor.Target
        public void changeParser(PathPatternParser parser) {
            RequestPredicate requestPredicate = this.left;
            if (requestPredicate instanceof ChangePathPatternParserVisitor.Target) {
                ChangePathPatternParserVisitor.Target target = (ChangePathPatternParserVisitor.Target) requestPredicate;
                target.changeParser(parser);
            }
            RequestPredicate requestPredicate2 = this.right;
            if (requestPredicate2 instanceof ChangePathPatternParserVisitor.Target) {
                ChangePathPatternParserVisitor.Target target2 = (ChangePathPatternParserVisitor.Target) requestPredicate2;
                target2.changeParser(parser);
            }
        }

        public String toString() {
            return String.format("(%s && %s)", this.left, this.right);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/RequestPredicates$NegateRequestPredicate.class */
    static class NegateRequestPredicate extends RequestModifyingPredicate implements ChangePathPatternParserVisitor.Target {
        private final RequestPredicate delegate;
        private final RequestModifyingPredicate delegateModifying;

        public NegateRequestPredicate(RequestPredicate delegate) {
            Assert.notNull(delegate, "Delegate must not be null");
            this.delegate = delegate;
            this.delegateModifying = of(delegate);
        }

        @Override // org.springframework.web.servlet.function.RequestPredicates.RequestModifyingPredicate
        protected RequestModifyingPredicate.Result testInternal(ServerRequest request) {
            RequestModifyingPredicate.Result result = this.delegateModifying.testInternal(request);
            boolean z = !result.value();
            Objects.requireNonNull(result);
            return RequestModifyingPredicate.Result.of(z, result::modifyAttributes);
        }

        @Override // org.springframework.web.servlet.function.RequestPredicate
        public void accept(Visitor visitor) {
            visitor.startNegate();
            this.delegate.accept(visitor);
            visitor.endNegate();
        }

        @Override // org.springframework.web.servlet.function.ChangePathPatternParserVisitor.Target
        public void changeParser(PathPatternParser parser) {
            RequestPredicate requestPredicate = this.delegate;
            if (requestPredicate instanceof ChangePathPatternParserVisitor.Target) {
                ChangePathPatternParserVisitor.Target target = (ChangePathPatternParserVisitor.Target) requestPredicate;
                target.changeParser(parser);
            }
        }

        public String toString() {
            return "!" + this.delegate.toString();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/RequestPredicates$OrRequestPredicate.class */
    static class OrRequestPredicate extends RequestModifyingPredicate implements ChangePathPatternParserVisitor.Target {
        private final RequestPredicate left;
        private final RequestModifyingPredicate leftModifying;
        private final RequestPredicate right;
        private final RequestModifyingPredicate rightModifying;

        public OrRequestPredicate(RequestPredicate left, RequestPredicate right) {
            Assert.notNull(left, "Left RequestPredicate must not be null");
            Assert.notNull(right, "Right RequestPredicate must not be null");
            this.left = left;
            this.leftModifying = of(left);
            this.right = right;
            this.rightModifying = of(right);
        }

        @Override // org.springframework.web.servlet.function.RequestPredicates.RequestModifyingPredicate
        protected RequestModifyingPredicate.Result testInternal(ServerRequest request) {
            RequestModifyingPredicate.Result leftResult = this.leftModifying.testInternal(request);
            if (leftResult.value()) {
                return leftResult;
            }
            return this.rightModifying.testInternal(request);
        }

        @Override // org.springframework.web.servlet.function.RequestPredicate
        public Optional<ServerRequest> nest(ServerRequest request) {
            Optional<ServerRequest> leftResult = this.left.nest(request);
            if (leftResult.isPresent()) {
                return leftResult;
            }
            return this.right.nest(request);
        }

        @Override // org.springframework.web.servlet.function.RequestPredicate
        public void accept(Visitor visitor) {
            visitor.startOr();
            this.left.accept(visitor);
            visitor.or();
            this.right.accept(visitor);
            visitor.endOr();
        }

        @Override // org.springframework.web.servlet.function.ChangePathPatternParserVisitor.Target
        public void changeParser(PathPatternParser parser) {
            RequestPredicate requestPredicate = this.left;
            if (requestPredicate instanceof ChangePathPatternParserVisitor.Target) {
                ChangePathPatternParserVisitor.Target target = (ChangePathPatternParserVisitor.Target) requestPredicate;
                target.changeParser(parser);
            }
            RequestPredicate requestPredicate2 = this.right;
            if (requestPredicate2 instanceof ChangePathPatternParserVisitor.Target) {
                ChangePathPatternParserVisitor.Target target2 = (ChangePathPatternParserVisitor.Target) requestPredicate2;
                target2.changeParser(parser);
            }
        }

        public String toString() {
            return String.format("(%s || %s)", this.left, this.right);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/RequestPredicates$DelegatingServerRequest.class */
    private static abstract class DelegatingServerRequest implements ServerRequest {
        private final ServerRequest delegate;

        protected DelegatingServerRequest(ServerRequest delegate) {
            Assert.notNull(delegate, "Delegate must not be null");
            this.delegate = delegate;
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public HttpMethod method() {
            return this.delegate.method();
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        @Deprecated
        public String methodName() {
            return this.delegate.methodName();
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public URI uri() {
            return this.delegate.uri();
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public UriBuilder uriBuilder() {
            return this.delegate.uriBuilder();
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public String path() {
            return this.delegate.path();
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public RequestPath requestPath() {
            return this.delegate.requestPath();
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public ServerRequest.Headers headers() {
            return this.delegate.headers();
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public MultiValueMap<String, Cookie> cookies() {
            return this.delegate.cookies();
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public Optional<InetSocketAddress> remoteAddress() {
            return this.delegate.remoteAddress();
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public List<HttpMessageConverter<?>> messageConverters() {
            return this.delegate.messageConverters();
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public <T> T body(Class<T> cls) throws ServletException, IOException {
            return (T) this.delegate.body(cls);
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public <T> T body(ParameterizedTypeReference<T> parameterizedTypeReference) throws ServletException, IOException {
            return (T) this.delegate.body(parameterizedTypeReference);
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public <T> T bind(Class<T> cls) throws BindException {
            return (T) this.delegate.bind(cls);
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public <T> T bind(Class<T> cls, Consumer<WebDataBinder> consumer) throws BindException {
            return (T) this.delegate.bind(cls, consumer);
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public Map<String, Object> attributes() {
            return this.delegate.attributes();
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public MultiValueMap<String, String> params() {
            return this.delegate.params();
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public MultiValueMap<String, Part> multipartData() throws IOException, ServletException {
            return this.delegate.multipartData();
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public Map<String, String> pathVariables() {
            return this.delegate.pathVariables();
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public HttpSession session() {
            return this.delegate.session();
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public Optional<Principal> principal() {
            return this.delegate.principal();
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public HttpServletRequest servletRequest() {
            return this.delegate.servletRequest();
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public Optional<ServerResponse> checkNotModified(Instant lastModified) {
            return this.delegate.checkNotModified(lastModified);
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public Optional<ServerResponse> checkNotModified(String etag) {
            return this.delegate.checkNotModified(etag);
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public Optional<ServerResponse> checkNotModified(Instant lastModified, String etag) {
            return this.delegate.checkNotModified(lastModified, etag);
        }

        public String toString() {
            return this.delegate.toString();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/RequestPredicates$ExtendedAttributesServerRequestWrapper.class */
    private static class ExtendedAttributesServerRequestWrapper extends DelegatingServerRequest {
        private final Map<String, Object> attributes;

        public ExtendedAttributesServerRequestWrapper(ServerRequest delegate, Map<String, Object> newAttributes) {
            super(delegate);
            Assert.notNull(newAttributes, "NewAttributes must not be null");
            this.attributes = RequestPredicates.mergeMaps(delegate.attributes(), newAttributes);
        }

        @Override // org.springframework.web.servlet.function.RequestPredicates.DelegatingServerRequest, org.springframework.web.servlet.function.ServerRequest
        public Map<String, Object> attributes() {
            return this.attributes;
        }

        @Override // org.springframework.web.servlet.function.RequestPredicates.DelegatingServerRequest, org.springframework.web.servlet.function.ServerRequest
        public Map<String, String> pathVariables() {
            return (Map) this.attributes.getOrDefault(RouterFunctions.URI_TEMPLATE_VARIABLES_ATTRIBUTE, Collections.emptyMap());
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/RequestPredicates$NestedPathPatternServerRequestWrapper.class */
    private static class NestedPathPatternServerRequestWrapper extends ExtendedAttributesServerRequestWrapper {
        private final RequestPath requestPath;

        public NestedPathPatternServerRequestWrapper(ServerRequest request, PathPattern.PathRemainingMatchInfo info, PathPattern pattern) {
            super(request, mergeAttributes(request, info.getUriVariables(), pattern));
            this.requestPath = requestPath(request.requestPath(), info);
        }

        private static Map<String, Object> mergeAttributes(ServerRequest request, Map<String, String> newPathVariables, PathPattern newPathPattern) {
            Map<String, String> oldPathVariables = request.pathVariables();
            PathPattern oldPathPattern = (PathPattern) request.attribute(RouterFunctions.MATCHING_PATTERN_ATTRIBUTE).orElse(null);
            Map<String, Object> result = new LinkedHashMap<>(2);
            result.put(RouterFunctions.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestPredicates.mergeMaps(oldPathVariables, newPathVariables));
            result.put(RouterFunctions.MATCHING_PATTERN_ATTRIBUTE, RequestPredicates.mergePatterns(oldPathPattern, newPathPattern));
            return result;
        }

        private static RequestPath requestPath(RequestPath original, PathPattern.PathRemainingMatchInfo info) {
            StringBuilder contextPath = new StringBuilder(original.contextPath().value());
            contextPath.append(info.getPathMatched().value());
            int length = contextPath.length();
            if (length > 0 && contextPath.charAt(length - 1) == '/') {
                contextPath.setLength(length - 1);
            }
            return original.modifyContextPath(contextPath.toString());
        }

        @Override // org.springframework.web.servlet.function.RequestPredicates.DelegatingServerRequest, org.springframework.web.servlet.function.ServerRequest
        public RequestPath requestPath() {
            return this.requestPath;
        }
    }
}

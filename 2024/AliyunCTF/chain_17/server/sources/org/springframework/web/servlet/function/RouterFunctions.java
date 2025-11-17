package org.springframework.web.servlet.function;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;
import org.springframework.web.util.pattern.PathPatternParser;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/RouterFunctions.class */
public abstract class RouterFunctions {
    private static final Log logger = LogFactory.getLog((Class<?>) RouterFunctions.class);
    public static final String REQUEST_ATTRIBUTE = RouterFunctions.class.getName() + ".request";
    public static final String URI_TEMPLATE_VARIABLES_ATTRIBUTE = RouterFunctions.class.getName() + ".uriTemplateVariables";
    public static final String MATCHING_PATTERN_ATTRIBUTE = RouterFunctions.class.getName() + ".matchingPattern";

    /* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/RouterFunctions$Builder.class */
    public interface Builder {
        Builder GET(HandlerFunction<ServerResponse> handlerFunction);

        Builder GET(String pattern, HandlerFunction<ServerResponse> handlerFunction);

        Builder GET(RequestPredicate predicate, HandlerFunction<ServerResponse> handlerFunction);

        Builder GET(String pattern, RequestPredicate predicate, HandlerFunction<ServerResponse> handlerFunction);

        Builder HEAD(HandlerFunction<ServerResponse> handlerFunction);

        Builder HEAD(String pattern, HandlerFunction<ServerResponse> handlerFunction);

        Builder HEAD(RequestPredicate predicate, HandlerFunction<ServerResponse> handlerFunction);

        Builder HEAD(String pattern, RequestPredicate predicate, HandlerFunction<ServerResponse> handlerFunction);

        Builder POST(HandlerFunction<ServerResponse> handlerFunction);

        Builder POST(String pattern, HandlerFunction<ServerResponse> handlerFunction);

        Builder POST(RequestPredicate predicate, HandlerFunction<ServerResponse> handlerFunction);

        Builder POST(String pattern, RequestPredicate predicate, HandlerFunction<ServerResponse> handlerFunction);

        Builder PUT(HandlerFunction<ServerResponse> handlerFunction);

        Builder PUT(String pattern, HandlerFunction<ServerResponse> handlerFunction);

        Builder PUT(RequestPredicate predicate, HandlerFunction<ServerResponse> handlerFunction);

        Builder PUT(String pattern, RequestPredicate predicate, HandlerFunction<ServerResponse> handlerFunction);

        Builder PATCH(HandlerFunction<ServerResponse> handlerFunction);

        Builder PATCH(String pattern, HandlerFunction<ServerResponse> handlerFunction);

        Builder PATCH(RequestPredicate predicate, HandlerFunction<ServerResponse> handlerFunction);

        Builder PATCH(String pattern, RequestPredicate predicate, HandlerFunction<ServerResponse> handlerFunction);

        Builder DELETE(HandlerFunction<ServerResponse> handlerFunction);

        Builder DELETE(String pattern, HandlerFunction<ServerResponse> handlerFunction);

        Builder DELETE(RequestPredicate predicate, HandlerFunction<ServerResponse> handlerFunction);

        Builder DELETE(String pattern, RequestPredicate predicate, HandlerFunction<ServerResponse> handlerFunction);

        Builder OPTIONS(HandlerFunction<ServerResponse> handlerFunction);

        Builder OPTIONS(String pattern, HandlerFunction<ServerResponse> handlerFunction);

        Builder OPTIONS(RequestPredicate predicate, HandlerFunction<ServerResponse> handlerFunction);

        Builder OPTIONS(String pattern, RequestPredicate predicate, HandlerFunction<ServerResponse> handlerFunction);

        Builder route(RequestPredicate predicate, HandlerFunction<ServerResponse> handlerFunction);

        Builder add(RouterFunction<ServerResponse> routerFunction);

        Builder resources(String pattern, Resource location);

        Builder resources(String pattern, Resource location, BiConsumer<Resource, HttpHeaders> headersConsumer);

        Builder resources(Function<ServerRequest, Optional<Resource>> lookupFunction);

        Builder resources(Function<ServerRequest, Optional<Resource>> lookupFunction, BiConsumer<Resource, HttpHeaders> headersConsumer);

        Builder nest(RequestPredicate predicate, Supplier<RouterFunction<ServerResponse>> routerFunctionSupplier);

        Builder nest(RequestPredicate predicate, Consumer<Builder> builderConsumer);

        Builder path(String pattern, Supplier<RouterFunction<ServerResponse>> routerFunctionSupplier);

        Builder path(String pattern, Consumer<Builder> builderConsumer);

        Builder filter(HandlerFilterFunction<ServerResponse, ServerResponse> filterFunction);

        Builder before(Function<ServerRequest, ServerRequest> requestProcessor);

        Builder after(BiFunction<ServerRequest, ServerResponse, ServerResponse> responseProcessor);

        Builder onError(Predicate<Throwable> predicate, BiFunction<Throwable, ServerRequest, ServerResponse> responseProvider);

        Builder onError(Class<? extends Throwable> exceptionType, BiFunction<Throwable, ServerRequest, ServerResponse> responseProvider);

        Builder withAttribute(String name, Object value);

        Builder withAttributes(Consumer<Map<String, Object>> attributesConsumer);

        RouterFunction<ServerResponse> build();
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/RouterFunctions$Visitor.class */
    public interface Visitor {
        void startNested(RequestPredicate predicate);

        void endNested(RequestPredicate predicate);

        void route(RequestPredicate predicate, HandlerFunction<?> handlerFunction);

        void resources(Function<ServerRequest, Optional<Resource>> lookupFunction);

        void attributes(Map<String, Object> attributes);

        void unknown(RouterFunction<?> routerFunction);
    }

    public static Builder route() {
        return new RouterFunctionBuilder();
    }

    public static <T extends ServerResponse> RouterFunction<T> route(RequestPredicate predicate, HandlerFunction<T> handlerFunction) {
        return new DefaultRouterFunction(predicate, handlerFunction);
    }

    public static <T extends ServerResponse> RouterFunction<T> nest(RequestPredicate predicate, RouterFunction<T> routerFunction) {
        return new DefaultNestedRouterFunction(predicate, routerFunction);
    }

    public static RouterFunction<ServerResponse> resources(String pattern, Resource location) {
        return resources(resourceLookupFunction(pattern, location), (BiConsumer<Resource, HttpHeaders>) (resource, httpHeaders) -> {
        });
    }

    public static RouterFunction<ServerResponse> resources(String pattern, Resource location, BiConsumer<Resource, HttpHeaders> headersConsumer) {
        return resources(resourceLookupFunction(pattern, location), headersConsumer);
    }

    public static Function<ServerRequest, Optional<Resource>> resourceLookupFunction(String pattern, Resource location) {
        return new PathResourceLookupFunction(pattern, location);
    }

    public static RouterFunction<ServerResponse> resources(Function<ServerRequest, Optional<Resource>> lookupFunction) {
        return new ResourcesRouterFunction(lookupFunction, (resource, httpHeaders) -> {
        });
    }

    public static RouterFunction<ServerResponse> resources(Function<ServerRequest, Optional<Resource>> lookupFunction, BiConsumer<Resource, HttpHeaders> headersConsumer) {
        return new ResourcesRouterFunction(lookupFunction, headersConsumer);
    }

    public static <T extends ServerResponse> RouterFunction<T> changeParser(RouterFunction<T> routerFunction, PathPatternParser parser) {
        Assert.notNull(routerFunction, "RouterFunction must not be null");
        Assert.notNull(parser, "Parser must not be null");
        ChangePathPatternParserVisitor visitor = new ChangePathPatternParserVisitor(parser);
        routerFunction.accept(visitor);
        return routerFunction;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/RouterFunctions$AbstractRouterFunction.class */
    static abstract class AbstractRouterFunction<T extends ServerResponse> implements RouterFunction<T> {
        public String toString() {
            ToStringVisitor visitor = new ToStringVisitor();
            accept(visitor);
            return visitor.toString();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/RouterFunctions$SameComposedRouterFunction.class */
    static final class SameComposedRouterFunction<T extends ServerResponse> extends AbstractRouterFunction<T> {
        private final RouterFunction<T> first;
        private final RouterFunction<T> second;

        public SameComposedRouterFunction(RouterFunction<T> first, RouterFunction<T> second) {
            this.first = first;
            this.second = second;
        }

        @Override // org.springframework.web.servlet.function.RouterFunction
        public Optional<HandlerFunction<T>> route(ServerRequest request) {
            Optional<HandlerFunction<T>> firstRoute = this.first.route(request);
            if (firstRoute.isPresent()) {
                return firstRoute;
            }
            return this.second.route(request);
        }

        @Override // org.springframework.web.servlet.function.RouterFunction
        public void accept(Visitor visitor) {
            this.first.accept(visitor);
            this.second.accept(visitor);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/RouterFunctions$DifferentComposedRouterFunction.class */
    static final class DifferentComposedRouterFunction extends AbstractRouterFunction<ServerResponse> {
        private final RouterFunction<?> first;
        private final RouterFunction<?> second;

        public DifferentComposedRouterFunction(RouterFunction<?> first, RouterFunction<?> second) {
            this.first = first;
            this.second = second;
        }

        @Override // org.springframework.web.servlet.function.RouterFunction
        public Optional<HandlerFunction<ServerResponse>> route(ServerRequest request) {
            Optional route = this.first.route(request);
            if (route.isPresent()) {
                return route;
            }
            return this.second.route(request);
        }

        @Override // org.springframework.web.servlet.function.RouterFunction
        public void accept(Visitor visitor) {
            this.first.accept(visitor);
            this.second.accept(visitor);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/RouterFunctions$FilteredRouterFunction.class */
    static final class FilteredRouterFunction<T extends ServerResponse, S extends ServerResponse> implements RouterFunction<S> {
        private final RouterFunction<T> routerFunction;
        private final HandlerFilterFunction<T, S> filterFunction;

        public FilteredRouterFunction(RouterFunction<T> routerFunction, HandlerFilterFunction<T, S> filterFunction) {
            this.routerFunction = routerFunction;
            this.filterFunction = filterFunction;
        }

        @Override // org.springframework.web.servlet.function.RouterFunction
        public Optional<HandlerFunction<S>> route(ServerRequest serverRequest) {
            Optional<HandlerFunction<T>> route = this.routerFunction.route(serverRequest);
            HandlerFilterFunction<T, S> handlerFilterFunction = this.filterFunction;
            Objects.requireNonNull(handlerFilterFunction);
            return (Optional<HandlerFunction<S>>) route.map(handlerFilterFunction::apply);
        }

        @Override // org.springframework.web.servlet.function.RouterFunction
        public void accept(Visitor visitor) {
            this.routerFunction.accept(visitor);
        }

        public String toString() {
            return this.routerFunction.toString();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/RouterFunctions$DefaultRouterFunction.class */
    private static final class DefaultRouterFunction<T extends ServerResponse> extends AbstractRouterFunction<T> {
        private final RequestPredicate predicate;
        private final HandlerFunction<T> handlerFunction;

        public DefaultRouterFunction(RequestPredicate predicate, HandlerFunction<T> handlerFunction) {
            Assert.notNull(predicate, "Predicate must not be null");
            Assert.notNull(handlerFunction, "HandlerFunction must not be null");
            this.predicate = predicate;
            this.handlerFunction = handlerFunction;
        }

        @Override // org.springframework.web.servlet.function.RouterFunction
        public Optional<HandlerFunction<T>> route(ServerRequest request) {
            if (this.predicate.test(request)) {
                if (RouterFunctions.logger.isTraceEnabled()) {
                    RouterFunctions.logger.trace(String.format("Predicate \"%s\" matches against \"%s\"", this.predicate, request));
                }
                return Optional.of(this.handlerFunction);
            }
            return Optional.empty();
        }

        @Override // org.springframework.web.servlet.function.RouterFunction
        public void accept(Visitor visitor) {
            visitor.route(this.predicate, this.handlerFunction);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/RouterFunctions$DefaultNestedRouterFunction.class */
    private static final class DefaultNestedRouterFunction<T extends ServerResponse> extends AbstractRouterFunction<T> {
        private final RequestPredicate predicate;
        private final RouterFunction<T> routerFunction;

        public DefaultNestedRouterFunction(RequestPredicate predicate, RouterFunction<T> routerFunction) {
            Assert.notNull(predicate, "Predicate must not be null");
            Assert.notNull(routerFunction, "RouterFunction must not be null");
            this.predicate = predicate;
            this.routerFunction = routerFunction;
        }

        @Override // org.springframework.web.servlet.function.RouterFunction
        public Optional<HandlerFunction<T>> route(ServerRequest serverRequest) {
            return (Optional) this.predicate.nest(serverRequest).map(nestedRequest -> {
                if (RouterFunctions.logger.isTraceEnabled()) {
                    RouterFunctions.logger.trace(String.format("Nested predicate \"%s\" matches against \"%s\"", this.predicate, serverRequest));
                }
                Optional<HandlerFunction<T>> result = this.routerFunction.route(nestedRequest);
                if (result.isPresent() && nestedRequest != serverRequest) {
                    serverRequest.attributes().clear();
                    serverRequest.attributes().putAll(nestedRequest.attributes());
                }
                return result;
            }).orElseGet(Optional::empty);
        }

        @Override // org.springframework.web.servlet.function.RouterFunction
        public void accept(Visitor visitor) {
            visitor.startNested(this.predicate);
            this.routerFunction.accept(visitor);
            visitor.endNested(this.predicate);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/RouterFunctions$ResourcesRouterFunction.class */
    public static class ResourcesRouterFunction extends AbstractRouterFunction<ServerResponse> {
        private final Function<ServerRequest, Optional<Resource>> lookupFunction;
        private final BiConsumer<Resource, HttpHeaders> headersConsumer;

        public ResourcesRouterFunction(Function<ServerRequest, Optional<Resource>> lookupFunction, BiConsumer<Resource, HttpHeaders> headersConsumer) {
            Assert.notNull(lookupFunction, "Function must not be null");
            Assert.notNull(headersConsumer, "HeadersConsumer must not be null");
            this.lookupFunction = lookupFunction;
            this.headersConsumer = headersConsumer;
        }

        @Override // org.springframework.web.servlet.function.RouterFunction
        public Optional<HandlerFunction<ServerResponse>> route(ServerRequest request) {
            return this.lookupFunction.apply(request).map(resource -> {
                return new ResourceHandlerFunction(resource, this.headersConsumer);
            });
        }

        @Override // org.springframework.web.servlet.function.RouterFunction
        public void accept(Visitor visitor) {
            visitor.resources(this.lookupFunction);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/RouterFunctions$AttributesRouterFunction.class */
    static final class AttributesRouterFunction<T extends ServerResponse> extends AbstractRouterFunction<T> {
        private final RouterFunction<T> delegate;
        private final Map<String, Object> attributes;

        public AttributesRouterFunction(RouterFunction<T> delegate, Map<String, Object> attributes) {
            this.delegate = delegate;
            this.attributes = initAttributes(attributes);
        }

        private static Map<String, Object> initAttributes(Map<String, Object> attributes) {
            if (attributes.isEmpty()) {
                return Collections.emptyMap();
            }
            return Collections.unmodifiableMap(new LinkedHashMap(attributes));
        }

        @Override // org.springframework.web.servlet.function.RouterFunction
        public Optional<HandlerFunction<T>> route(ServerRequest request) {
            return this.delegate.route(request);
        }

        @Override // org.springframework.web.servlet.function.RouterFunction
        public void accept(Visitor visitor) {
            visitor.attributes(this.attributes);
            this.delegate.accept(visitor);
        }

        @Override // org.springframework.web.servlet.function.RouterFunction
        public RouterFunction<T> withAttribute(String name, Object value) {
            Assert.hasLength(name, "Name must not be empty");
            Assert.notNull(value, "Value must not be null");
            Map<String, Object> attributes = new LinkedHashMap<>(this.attributes);
            attributes.put(name, value);
            return new AttributesRouterFunction(this.delegate, attributes);
        }

        @Override // org.springframework.web.servlet.function.RouterFunction
        public RouterFunction<T> withAttributes(Consumer<Map<String, Object>> attributesConsumer) {
            Assert.notNull(attributesConsumer, "AttributesConsumer must not be null");
            Map<String, Object> attributes = new LinkedHashMap<>(this.attributes);
            attributesConsumer.accept(attributes);
            return new AttributesRouterFunction(this.delegate, attributes);
        }
    }
}

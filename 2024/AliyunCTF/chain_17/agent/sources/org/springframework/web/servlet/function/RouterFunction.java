package org.springframework.web.servlet.function;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import org.springframework.util.Assert;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/RouterFunction.class */
public interface RouterFunction<T extends ServerResponse> {
    Optional<HandlerFunction<T>> route(ServerRequest request);

    default RouterFunction<T> and(RouterFunction<T> other) {
        return new RouterFunctions.SameComposedRouterFunction(this, other);
    }

    default RouterFunction<?> andOther(RouterFunction<?> other) {
        return new RouterFunctions.DifferentComposedRouterFunction(this, other);
    }

    default RouterFunction<T> andRoute(RequestPredicate predicate, HandlerFunction<T> handlerFunction) {
        return and(RouterFunctions.route(predicate, handlerFunction));
    }

    default RouterFunction<T> andNest(RequestPredicate predicate, RouterFunction<T> routerFunction) {
        return and(RouterFunctions.nest(predicate, routerFunction));
    }

    default <S extends ServerResponse> RouterFunction<S> filter(HandlerFilterFunction<T, S> filterFunction) {
        return new RouterFunctions.FilteredRouterFunction(this, filterFunction);
    }

    default void accept(RouterFunctions.Visitor visitor) {
        visitor.unknown(this);
    }

    default RouterFunction<T> withAttribute(String name, Object value) {
        Assert.hasLength(name, "Name must not be empty");
        Assert.notNull(value, "Value must not be null");
        Map<String, Object> attributes = new LinkedHashMap<>();
        attributes.put(name, value);
        return new RouterFunctions.AttributesRouterFunction(this, attributes);
    }

    default RouterFunction<T> withAttributes(Consumer<Map<String, Object>> attributesConsumer) {
        Assert.notNull(attributesConsumer, "AttributesConsumer must not be null");
        Map<String, Object> attributes = new LinkedHashMap<>();
        attributesConsumer.accept(attributes);
        return new RouterFunctions.AttributesRouterFunction(this, attributes);
    }
}

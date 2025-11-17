package org.springframework.web.servlet.function;

import java.util.Optional;
import org.springframework.web.servlet.function.RequestPredicates;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/RequestPredicate.class */
public interface RequestPredicate {
    boolean test(ServerRequest request);

    default RequestPredicate and(RequestPredicate other) {
        return new RequestPredicates.AndRequestPredicate(this, other);
    }

    default RequestPredicate negate() {
        return new RequestPredicates.NegateRequestPredicate(this);
    }

    default RequestPredicate or(RequestPredicate other) {
        return new RequestPredicates.OrRequestPredicate(this, other);
    }

    default Optional<ServerRequest> nest(ServerRequest request) {
        return test(request) ? Optional.of(request) : Optional.empty();
    }

    default void accept(RequestPredicates.Visitor visitor) {
        visitor.unknown(this);
    }
}

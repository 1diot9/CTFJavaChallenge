package org.springframework.web.servlet.function;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import org.springframework.util.Assert;
import org.springframework.web.servlet.function.ServerResponse;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/HandlerFilterFunction.class */
public interface HandlerFilterFunction<T extends ServerResponse, R extends ServerResponse> {
    R filter(ServerRequest request, HandlerFunction<T> next) throws Exception;

    default HandlerFilterFunction<T, R> andThen(HandlerFilterFunction<T, T> after) {
        Assert.notNull(after, "HandlerFilterFunction must not be null");
        return (request, next) -> {
            HandlerFunction<T> nextHandler = handlerRequest -> {
                return after.filter(handlerRequest, next);
            };
            return filter(request, nextHandler);
        };
    }

    default HandlerFunction<R> apply(HandlerFunction<T> handler) {
        Assert.notNull(handler, "HandlerFunction must not be null");
        return request -> {
            return filter(request, handler);
        };
    }

    static <T extends ServerResponse> HandlerFilterFunction<T, T> ofRequestProcessor(Function<ServerRequest, ServerRequest> requestProcessor) {
        Assert.notNull(requestProcessor, "Function must not be null");
        return (request, next) -> {
            return next.handle((ServerRequest) requestProcessor.apply(request));
        };
    }

    static <T extends ServerResponse, R extends ServerResponse> HandlerFilterFunction<T, R> ofResponseProcessor(BiFunction<ServerRequest, T, R> responseProcessor) {
        Assert.notNull(responseProcessor, "Function must not be null");
        return (request, next) -> {
            return (ServerResponse) responseProcessor.apply(request, next.handle(request));
        };
    }

    static <T extends ServerResponse> HandlerFilterFunction<T, T> ofErrorHandler(Predicate<Throwable> predicate, BiFunction<Throwable, ServerRequest, T> errorHandler) {
        Assert.notNull(predicate, "Predicate must not be null");
        Assert.notNull(errorHandler, "ErrorHandler must not be null");
        return (request, next) -> {
            try {
                ServerResponse handle = next.handle(request);
                if (handle instanceof ErrorHandlingServerResponse) {
                    ErrorHandlingServerResponse response = (ErrorHandlingServerResponse) handle;
                    response.addErrorHandler(predicate, errorHandler);
                }
                return handle;
            } catch (Throwable throwable) {
                if (predicate.test(throwable)) {
                    return (ServerResponse) errorHandler.apply(throwable, request);
                }
                throw throwable;
            }
        };
    }
}

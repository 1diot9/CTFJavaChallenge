package org.springframework.web.service.invoker;

import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ReactiveAdapter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.invoker.HttpRequestValues;
import org.springframework.web.service.invoker.ReactiveHttpRequestValues;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/service/invoker/RequestBodyArgumentResolver.class */
public class RequestBodyArgumentResolver implements HttpServiceArgumentResolver {
    private static final boolean REACTOR_PRESENT = ClassUtils.isPresent("reactor.core.publisher.Mono", RequestBodyArgumentResolver.class.getClassLoader());

    @Nullable
    private final ReactiveAdapterRegistry reactiveAdapterRegistry;

    public RequestBodyArgumentResolver(HttpExchangeAdapter exchangeAdapter) {
        ReactiveAdapterRegistry sharedInstance;
        if (REACTOR_PRESENT) {
            if (exchangeAdapter instanceof ReactorHttpExchangeAdapter) {
                ReactorHttpExchangeAdapter reactorAdapter = (ReactorHttpExchangeAdapter) exchangeAdapter;
                sharedInstance = reactorAdapter.getReactiveAdapterRegistry();
            } else {
                sharedInstance = ReactiveAdapterRegistry.getSharedInstance();
            }
            this.reactiveAdapterRegistry = sharedInstance;
            return;
        }
        this.reactiveAdapterRegistry = null;
    }

    @Override // org.springframework.web.service.invoker.HttpServiceArgumentResolver
    public boolean resolve(@Nullable Object argument, MethodParameter parameter, HttpRequestValues.Builder requestValues) {
        ReactiveAdapter adapter;
        RequestBody annot = (RequestBody) parameter.getParameterAnnotation(RequestBody.class);
        if (annot == null) {
            return false;
        }
        if (argument != null) {
            if (this.reactiveAdapterRegistry != null && (adapter = this.reactiveAdapterRegistry.getAdapter(parameter.getParameterType())) != null) {
                MethodParameter nestedParameter = parameter.nested();
                Assert.isTrue(!adapter.isNoValue(), "Async type for @RequestBody should produce value(s)");
                Assert.isTrue(nestedParameter.getNestedParameterType() != Void.class, "Async type for @RequestBody should produce value(s)");
                if (requestValues instanceof ReactiveHttpRequestValues.Builder) {
                    ReactiveHttpRequestValues.Builder reactiveRequestValues = (ReactiveHttpRequestValues.Builder) requestValues;
                    reactiveRequestValues.setBodyPublisher(adapter.toPublisher(argument), asParameterizedTypeRef(nestedParameter));
                    return true;
                }
                throw new IllegalStateException("RequestBody with a reactive type is only supported with reactive client");
            }
            requestValues.setBodyValue(argument);
            return true;
        }
        return true;
    }

    private static ParameterizedTypeReference<Object> asParameterizedTypeRef(MethodParameter nestedParam) {
        return ParameterizedTypeReference.forType(nestedParam.getNestedGenericParameterType());
    }
}

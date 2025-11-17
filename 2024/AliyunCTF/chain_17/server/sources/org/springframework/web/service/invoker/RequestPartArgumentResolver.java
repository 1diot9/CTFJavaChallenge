package org.springframework.web.service.invoker;

import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ReactiveAdapter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.service.invoker.AbstractNamedValueArgumentResolver;
import org.springframework.web.service.invoker.HttpRequestValues;
import org.springframework.web.service.invoker.ReactiveHttpRequestValues;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/service/invoker/RequestPartArgumentResolver.class */
public class RequestPartArgumentResolver extends AbstractNamedValueArgumentResolver {
    private static final boolean REACTOR_PRESENT = ClassUtils.isPresent("reactor.core.publisher.Mono", RequestPartArgumentResolver.class.getClassLoader());

    @Nullable
    private final ReactiveAdapterRegistry reactiveAdapterRegistry;

    public RequestPartArgumentResolver(HttpExchangeAdapter exchangeAdapter) {
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

    @Override // org.springframework.web.service.invoker.AbstractNamedValueArgumentResolver
    protected AbstractNamedValueArgumentResolver.NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
        RequestPart annot = (RequestPart) parameter.getParameterAnnotation(RequestPart.class);
        boolean isMultiPartFile = parameter.nestedIfOptional().getNestedParameterType().equals(MultipartFile.class);
        String label = isMultiPartFile ? "MultipartFile" : "request part";
        if (annot != null) {
            return new AbstractNamedValueArgumentResolver.NamedValueInfo(annot.name(), annot.required(), null, label, true);
        }
        if (isMultiPartFile) {
            return new AbstractNamedValueArgumentResolver.NamedValueInfo("", true, null, label, true);
        }
        return null;
    }

    @Override // org.springframework.web.service.invoker.AbstractNamedValueArgumentResolver
    protected void addRequestValue(String name, Object value, MethodParameter parameter, HttpRequestValues.Builder requestValues) {
        if (this.reactiveAdapterRegistry != null) {
            Class<?> type = parameter.getParameterType();
            ReactiveAdapter adapter = this.reactiveAdapterRegistry.getAdapter(type);
            if (adapter != null) {
                MethodParameter nestedParameter = parameter.nested();
                Assert.isTrue(!adapter.isNoValue(), "Async type for @RequestPart should produce value(s)");
                Assert.isTrue(nestedParameter.getNestedParameterType() != Void.class, "Async type for @RequestPart should produce value(s)");
                if (requestValues instanceof ReactiveHttpRequestValues.Builder) {
                    ReactiveHttpRequestValues.Builder reactiveValues = (ReactiveHttpRequestValues.Builder) requestValues;
                    reactiveValues.addRequestPartPublisher(name, adapter.toPublisher(value), asParameterizedTypeRef(nestedParameter));
                    return;
                }
                throw new IllegalStateException("RequestPart with a reactive type is only supported with reactive client");
            }
        }
        if (value instanceof MultipartFile) {
            MultipartFile multipartFile = (MultipartFile) value;
            value = toHttpEntity(name, multipartFile);
        }
        requestValues.addRequestPart(name, value);
    }

    private static ParameterizedTypeReference<Object> asParameterizedTypeRef(MethodParameter nestedParam) {
        return ParameterizedTypeReference.forType(nestedParam.getNestedGenericParameterType());
    }

    private static Object toHttpEntity(String name, MultipartFile multipartFile) {
        HttpHeaders headers = new HttpHeaders();
        if (multipartFile.getOriginalFilename() != null) {
            headers.setContentDispositionFormData(name, multipartFile.getOriginalFilename());
        }
        if (multipartFile.getContentType() != null) {
            headers.add(HttpHeaders.CONTENT_TYPE, multipartFile.getContentType());
        }
        return new HttpEntity(multipartFile.getResource(), headers);
    }
}

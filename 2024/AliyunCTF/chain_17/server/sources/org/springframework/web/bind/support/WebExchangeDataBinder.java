package org.springframework.web.bind.support;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.core.MethodParameter;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/bind/support/WebExchangeDataBinder.class */
public class WebExchangeDataBinder extends WebDataBinder {
    public WebExchangeDataBinder(@Nullable Object target) {
        super(target);
    }

    public WebExchangeDataBinder(@Nullable Object target, String objectName) {
        super(target, objectName);
    }

    public Mono<Void> construct(ServerWebExchange exchange) {
        return getValuesToBind(exchange).doOnNext(map -> {
            construct(new MapValueResolver(map));
        }).then();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.validation.DataBinder
    public boolean shouldConstructArgument(MethodParameter param) {
        Class<?> type = param.nestedIfOptional().getNestedParameterType();
        return super.shouldConstructArgument(param) && !Part.class.isAssignableFrom(type);
    }

    public Mono<Void> bind(ServerWebExchange exchange) {
        if (shouldNotBindPropertyValues()) {
            return Mono.empty();
        }
        return getValuesToBind(exchange).doOnNext(map -> {
            doBind(new MutablePropertyValues((Map<?, ?>) map));
        }).then();
    }

    public Mono<Map<String, Object>> getValuesToBind(ServerWebExchange exchange) {
        return extractValuesToBind(exchange);
    }

    public static Mono<Map<String, Object>> extractValuesToBind(ServerWebExchange exchange) {
        MultiValueMap<String, String> queryParams = exchange.getRequest().getQueryParams();
        Mono<MultiValueMap<String, String>> formData = exchange.getFormData();
        Mono<MultiValueMap<String, Part>> multipartData = exchange.getMultipartData();
        return Mono.zip(Mono.just(queryParams), formData, multipartData).map(tuple -> {
            Map<String, Object> result = new TreeMap<>();
            ((MultiValueMap) tuple.getT1()).forEach((key, values) -> {
                addBindValue(result, key, values);
            });
            ((MultiValueMap) tuple.getT2()).forEach((key2, values2) -> {
                addBindValue(result, key2, values2);
            });
            ((MultiValueMap) tuple.getT3()).forEach((key3, values3) -> {
                addBindValue(result, key3, values3);
            });
            return result;
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void addBindValue(Map<String, Object> params, String key, List<?> values) {
        if (!CollectionUtils.isEmpty(values)) {
            List<?> values2 = values.stream().map(value -> {
                if (!(value instanceof FormFieldPart)) {
                    return value;
                }
                FormFieldPart formFieldPart = (FormFieldPart) value;
                return formFieldPart.value();
            }).toList();
            params.put(key, values2.size() == 1 ? values2.get(0) : values2);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/bind/support/WebExchangeDataBinder$MapValueResolver.class */
    private static final class MapValueResolver extends Record implements DataBinder.ValueResolver {
        private final Map<String, Object> map;

        private MapValueResolver(Map<String, Object> map) {
            this.map = map;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, MapValueResolver.class), MapValueResolver.class, "map", "FIELD:Lorg/springframework/web/bind/support/WebExchangeDataBinder$MapValueResolver;->map:Ljava/util/Map;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, MapValueResolver.class), MapValueResolver.class, "map", "FIELD:Lorg/springframework/web/bind/support/WebExchangeDataBinder$MapValueResolver;->map:Ljava/util/Map;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, MapValueResolver.class, Object.class), MapValueResolver.class, "map", "FIELD:Lorg/springframework/web/bind/support/WebExchangeDataBinder$MapValueResolver;->map:Ljava/util/Map;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public Map<String, Object> map() {
            return this.map;
        }

        @Override // org.springframework.validation.DataBinder.ValueResolver
        public Object resolveValue(String name, Class<?> type) {
            return this.map.get(name);
        }

        @Override // org.springframework.validation.DataBinder.ValueResolver
        public Set<String> getNames() {
            return this.map.keySet();
        }
    }
}

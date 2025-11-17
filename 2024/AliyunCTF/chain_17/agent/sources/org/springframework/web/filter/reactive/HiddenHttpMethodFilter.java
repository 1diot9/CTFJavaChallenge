package org.springframework.web.filter.reactive;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/filter/reactive/HiddenHttpMethodFilter.class */
public class HiddenHttpMethodFilter implements WebFilter {
    private static final List<HttpMethod> ALLOWED_METHODS = List.of(HttpMethod.PUT, HttpMethod.DELETE, HttpMethod.PATCH);
    public static final String DEFAULT_METHOD_PARAMETER_NAME = "_method";
    private String methodParamName = "_method";

    public void setMethodParamName(String methodParamName) {
        Assert.hasText(methodParamName, "'methodParamName' must not be empty");
        this.methodParamName = methodParamName;
    }

    @Override // org.springframework.web.server.WebFilter
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (exchange.getRequest().getMethod() != HttpMethod.POST) {
            return chain.filter(exchange);
        }
        Mono map = exchange.getFormData().map(formData -> {
            String method = (String) formData.getFirst(this.methodParamName);
            return StringUtils.hasLength(method) ? mapExchange(exchange, method) : exchange;
        });
        Objects.requireNonNull(chain);
        return map.flatMap(chain::filter);
    }

    private ServerWebExchange mapExchange(ServerWebExchange exchange, String methodParamValue) {
        HttpMethod httpMethod = HttpMethod.valueOf(methodParamValue.toUpperCase(Locale.ENGLISH));
        if (ALLOWED_METHODS.contains(httpMethod)) {
            return exchange.mutate().request(builder -> {
                builder.method(httpMethod);
            }).build();
        }
        return exchange;
    }
}

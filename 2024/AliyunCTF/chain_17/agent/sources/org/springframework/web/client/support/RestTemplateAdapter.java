package org.springframework.web.client.support;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.service.invoker.HttpExchangeAdapter;
import org.springframework.web.service.invoker.HttpRequestValues;
import org.springframework.web.util.UriBuilderFactory;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/client/support/RestTemplateAdapter.class */
public final class RestTemplateAdapter implements HttpExchangeAdapter {
    private final RestTemplate restTemplate;

    private RestTemplateAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override // org.springframework.web.service.invoker.HttpExchangeAdapter
    public boolean supportsRequestAttributes() {
        return false;
    }

    @Override // org.springframework.web.service.invoker.HttpExchangeAdapter
    public void exchange(HttpRequestValues values) {
        this.restTemplate.exchange(newRequest(values), Void.class);
    }

    @Override // org.springframework.web.service.invoker.HttpExchangeAdapter
    public HttpHeaders exchangeForHeaders(HttpRequestValues values) {
        return this.restTemplate.exchange(newRequest(values), Void.class).getHeaders();
    }

    @Override // org.springframework.web.service.invoker.HttpExchangeAdapter
    public <T> T exchangeForBody(HttpRequestValues values, ParameterizedTypeReference<T> bodyType) {
        return this.restTemplate.exchange(newRequest(values), bodyType).getBody();
    }

    @Override // org.springframework.web.service.invoker.HttpExchangeAdapter
    public ResponseEntity<Void> exchangeForBodilessEntity(HttpRequestValues values) {
        return this.restTemplate.exchange(newRequest(values), Void.class);
    }

    @Override // org.springframework.web.service.invoker.HttpExchangeAdapter
    public <T> ResponseEntity<T> exchangeForEntity(HttpRequestValues values, ParameterizedTypeReference<T> bodyType) {
        return this.restTemplate.exchange(newRequest(values), bodyType);
    }

    private RequestEntity<?> newRequest(HttpRequestValues values) {
        RequestEntity.BodyBuilder builder;
        HttpMethod httpMethod = values.getHttpMethod();
        Assert.notNull(httpMethod, "HttpMethod is required");
        if (values.getUri() != null) {
            builder = RequestEntity.method(httpMethod, values.getUri());
        } else if (values.getUriTemplate() != null) {
            UriBuilderFactory uriBuilderFactory = values.getUriBuilderFactory();
            if (uriBuilderFactory != null) {
                URI expanded = uriBuilderFactory.expand(values.getUriTemplate(), values.getUriVariables());
                builder = RequestEntity.method(httpMethod, expanded);
            } else {
                builder = RequestEntity.method(httpMethod, values.getUriTemplate(), values.getUriVariables());
            }
        } else {
            throw new IllegalStateException("Neither full URL nor URI template");
        }
        builder.headers(values.getHeaders());
        if (!values.getCookies().isEmpty()) {
            List<String> cookies = new ArrayList<>();
            values.getCookies().forEach((name, cookieValues) -> {
                cookieValues.forEach(value -> {
                    HttpCookie cookie = new HttpCookie(name, value);
                    cookies.add(cookie.toString());
                });
            });
            builder.header(HttpHeaders.COOKIE, String.join("; ", cookies));
        }
        if (values.getBodyValue() != null) {
            return builder.body(values.getBodyValue());
        }
        return builder.build();
    }

    public static RestTemplateAdapter create(RestTemplate restTemplate) {
        return new RestTemplateAdapter(restTemplate);
    }
}

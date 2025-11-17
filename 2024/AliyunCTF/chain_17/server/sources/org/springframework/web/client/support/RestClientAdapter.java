package org.springframework.web.client.support;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClient;
import org.springframework.web.service.invoker.HttpExchangeAdapter;
import org.springframework.web.service.invoker.HttpRequestValues;
import org.springframework.web.util.UriBuilderFactory;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/client/support/RestClientAdapter.class */
public final class RestClientAdapter implements HttpExchangeAdapter {
    private final RestClient restClient;

    private RestClientAdapter(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override // org.springframework.web.service.invoker.HttpExchangeAdapter
    public boolean supportsRequestAttributes() {
        return true;
    }

    @Override // org.springframework.web.service.invoker.HttpExchangeAdapter
    public void exchange(HttpRequestValues requestValues) {
        newRequest(requestValues).retrieve().toBodilessEntity();
    }

    @Override // org.springframework.web.service.invoker.HttpExchangeAdapter
    public HttpHeaders exchangeForHeaders(HttpRequestValues values) {
        return newRequest(values).retrieve().toBodilessEntity().getHeaders();
    }

    @Override // org.springframework.web.service.invoker.HttpExchangeAdapter
    public <T> T exchangeForBody(HttpRequestValues httpRequestValues, ParameterizedTypeReference<T> parameterizedTypeReference) {
        return (T) newRequest(httpRequestValues).retrieve().body(parameterizedTypeReference);
    }

    @Override // org.springframework.web.service.invoker.HttpExchangeAdapter
    public ResponseEntity<Void> exchangeForBodilessEntity(HttpRequestValues values) {
        return newRequest(values).retrieve().toBodilessEntity();
    }

    @Override // org.springframework.web.service.invoker.HttpExchangeAdapter
    public <T> ResponseEntity<T> exchangeForEntity(HttpRequestValues values, ParameterizedTypeReference<T> bodyType) {
        return newRequest(values).retrieve().toEntity(bodyType);
    }

    private RestClient.RequestBodySpec newRequest(HttpRequestValues values) {
        RestClient.RequestBodySpec bodySpec;
        HttpMethod httpMethod = values.getHttpMethod();
        Assert.notNull(httpMethod, "HttpMethod is required");
        RestClient.RequestBodyUriSpec uriSpec = this.restClient.method(httpMethod);
        if (values.getUri() != null) {
            bodySpec = (RestClient.RequestBodySpec) uriSpec.uri(values.getUri());
        } else if (values.getUriTemplate() != null) {
            UriBuilderFactory uriBuilderFactory = values.getUriBuilderFactory();
            if (uriBuilderFactory != null) {
                URI uri = uriBuilderFactory.expand(values.getUriTemplate(), values.getUriVariables());
                bodySpec = (RestClient.RequestBodySpec) uriSpec.uri(uri);
            } else {
                bodySpec = (RestClient.RequestBodySpec) uriSpec.uri(values.getUriTemplate(), values.getUriVariables());
            }
        } else {
            throw new IllegalStateException("Neither full URL nor URI template");
        }
        bodySpec.headers(headers -> {
            headers.putAll(values.getHeaders());
        });
        if (!values.getCookies().isEmpty()) {
            List<String> cookies = new ArrayList<>();
            values.getCookies().forEach((name, cookieValues) -> {
                cookieValues.forEach(value -> {
                    HttpCookie cookie = new HttpCookie(name, value);
                    cookies.add(cookie.toString());
                });
            });
            bodySpec.header(HttpHeaders.COOKIE, String.join("; ", cookies));
        }
        if (values.getBodyValue() != null) {
            bodySpec.body(values.getBodyValue());
        }
        return bodySpec;
    }

    public static RestClientAdapter create(RestClient restClient) {
        return new RestClientAdapter(restClient);
    }
}

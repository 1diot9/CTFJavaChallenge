package org.springframework.web.service.invoker;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/service/invoker/HttpExchangeAdapter.class */
public interface HttpExchangeAdapter {
    boolean supportsRequestAttributes();

    void exchange(HttpRequestValues requestValues);

    HttpHeaders exchangeForHeaders(HttpRequestValues requestValues);

    @Nullable
    <T> T exchangeForBody(HttpRequestValues requestValues, ParameterizedTypeReference<T> bodyType);

    ResponseEntity<Void> exchangeForBodilessEntity(HttpRequestValues requestValues);

    <T> ResponseEntity<T> exchangeForEntity(HttpRequestValues requestValues, ParameterizedTypeReference<T> bodyType);
}

package org.springframework.http.client.reactive;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.Message;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseCookie;
import org.springframework.http.support.HttpComponentsHeadersAdapter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/reactive/HttpComponentsClientHttpResponse.class */
class HttpComponentsClientHttpResponse implements ClientHttpResponse {
    private final DataBufferFactory dataBufferFactory;
    private final Message<HttpResponse, Publisher<ByteBuffer>> message;
    private final HttpHeaders headers;
    private final HttpClientContext context;
    private final AtomicBoolean rejectSubscribers = new AtomicBoolean();

    public HttpComponentsClientHttpResponse(DataBufferFactory dataBufferFactory, Message<HttpResponse, Publisher<ByteBuffer>> message, HttpClientContext context) {
        this.dataBufferFactory = dataBufferFactory;
        this.message = message;
        this.context = context;
        MultiValueMap<String, String> adapter = new HttpComponentsHeadersAdapter(message.getHead());
        this.headers = HttpHeaders.readOnlyHttpHeaders(adapter);
    }

    @Override // org.springframework.http.client.reactive.ClientHttpResponse
    public HttpStatusCode getStatusCode() {
        return HttpStatusCode.valueOf(this.message.getHead().getCode());
    }

    @Override // org.springframework.http.client.reactive.ClientHttpResponse
    public MultiValueMap<String, ResponseCookie> getCookies() {
        LinkedMultiValueMap<String, ResponseCookie> result = new LinkedMultiValueMap<>();
        this.context.getCookieStore().getCookies().forEach(cookie -> {
            result.add(cookie.getName(), ResponseCookie.fromClientResponse(cookie.getName(), cookie.getValue()).domain(cookie.getDomain()).path(cookie.getPath()).maxAge(getMaxAgeSeconds(cookie)).secure(cookie.isSecure()).httpOnly(cookie.containsAttribute("httponly")).sameSite(cookie.getAttribute("samesite")).build());
        });
        return result;
    }

    private long getMaxAgeSeconds(Cookie cookie) {
        String maxAgeAttribute = cookie.getAttribute("max-age");
        if (maxAgeAttribute != null) {
            return Long.parseLong(maxAgeAttribute);
        }
        return -1L;
    }

    @Override // org.springframework.http.ReactiveHttpInputMessage
    public Flux<DataBuffer> getBody() {
        Flux doOnSubscribe = Flux.from((Publisher) this.message.getBody()).doOnSubscribe(s -> {
            if (!this.rejectSubscribers.compareAndSet(false, true)) {
                throw new IllegalStateException("The client response body can only be consumed once.");
            }
        });
        DataBufferFactory dataBufferFactory = this.dataBufferFactory;
        Objects.requireNonNull(dataBufferFactory);
        return doOnSubscribe.map(dataBufferFactory::wrap);
    }

    @Override // org.springframework.http.HttpMessage
    public HttpHeaders getHeaders() {
        return this.headers;
    }
}

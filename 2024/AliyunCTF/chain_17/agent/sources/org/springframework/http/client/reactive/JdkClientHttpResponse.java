package org.springframework.http.client.reactive;

import java.net.HttpCookie;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Flow;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseCookie;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.adapter.JdkFlowAdapter;
import reactor.core.publisher.Flux;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/reactive/JdkClientHttpResponse.class */
class JdkClientHttpResponse implements ClientHttpResponse {
    private static final Pattern SAME_SITE_PATTERN = Pattern.compile("(?i).*SameSite=(Strict|Lax|None).*");
    private final HttpResponse<Flow.Publisher<List<ByteBuffer>>> response;
    private final DataBufferFactory bufferFactory;
    private final HttpHeaders headers;

    public JdkClientHttpResponse(HttpResponse<Flow.Publisher<List<ByteBuffer>>> response, DataBufferFactory bufferFactory) {
        this.response = response;
        this.bufferFactory = bufferFactory;
        this.headers = adaptHeaders(response);
    }

    private static HttpHeaders adaptHeaders(HttpResponse<Flow.Publisher<List<ByteBuffer>>> response) {
        Map<? extends String, ? extends String> rawHeaders = response.headers().map();
        Map<String, List<String>> map = new LinkedCaseInsensitiveMap<>(rawHeaders.size(), Locale.ENGLISH);
        MultiValueMap<String, String> multiValueMap = CollectionUtils.toMultiValueMap(map);
        multiValueMap.putAll(rawHeaders);
        return HttpHeaders.readOnlyHttpHeaders(multiValueMap);
    }

    @Override // org.springframework.http.client.reactive.ClientHttpResponse
    public HttpStatusCode getStatusCode() {
        return HttpStatusCode.valueOf(this.response.statusCode());
    }

    @Override // org.springframework.http.HttpMessage
    public HttpHeaders getHeaders() {
        return this.headers;
    }

    @Override // org.springframework.http.client.reactive.ClientHttpResponse
    public MultiValueMap<String, ResponseCookie> getCookies() {
        return (MultiValueMap) this.response.headers().allValues(HttpHeaders.SET_COOKIE).stream().flatMap(header -> {
            Matcher matcher = SAME_SITE_PATTERN.matcher(header);
            String sameSite = matcher.matches() ? matcher.group(1) : null;
            return HttpCookie.parse(header).stream().map(cookie -> {
                return toResponseCookie(cookie, sameSite);
            });
        }).collect(LinkedMultiValueMap::new, (cookies, cookie) -> {
            cookies.add(cookie.getName(), cookie);
        }, (v0, v1) -> {
            v0.addAll(v1);
        });
    }

    private ResponseCookie toResponseCookie(HttpCookie cookie, @Nullable String sameSite) {
        return ResponseCookie.from(cookie.getName(), cookie.getValue()).domain(cookie.getDomain()).httpOnly(cookie.isHttpOnly()).maxAge(cookie.getMaxAge()).path(cookie.getPath()).secure(cookie.getSecure()).sameSite(sameSite).build();
    }

    @Override // org.springframework.http.ReactiveHttpInputMessage
    public Flux<DataBuffer> getBody() {
        Flux flatMapIterable = JdkFlowAdapter.flowPublisherToFlux((Flow.Publisher) this.response.body()).flatMapIterable(Function.identity());
        DataBufferFactory dataBufferFactory = this.bufferFactory;
        Objects.requireNonNull(dataBufferFactory);
        return flatMapIterable.map(dataBufferFactory::wrap).doOnDiscard(DataBuffer.class, DataBufferUtils::release);
    }
}

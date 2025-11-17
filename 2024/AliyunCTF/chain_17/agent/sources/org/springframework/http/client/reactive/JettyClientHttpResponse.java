package org.springframework.http.client.reactive;

import java.net.HttpCookie;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.jetty.reactive.client.ReactiveResponse;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseCookie;
import org.springframework.http.support.JettyHeadersAdapter;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/reactive/JettyClientHttpResponse.class */
class JettyClientHttpResponse implements ClientHttpResponse {
    private static final Pattern SAME_SITE_PATTERN = Pattern.compile("(?i).*SameSite=(Strict|Lax|None).*");
    private final ReactiveResponse reactiveResponse;
    private final Flux<DataBuffer> content;
    private final HttpHeaders headers;

    public JettyClientHttpResponse(ReactiveResponse reactiveResponse, Publisher<DataBuffer> content) {
        this.reactiveResponse = reactiveResponse;
        this.content = Flux.from(content);
        MultiValueMap<String, String> headers = new JettyHeadersAdapter(reactiveResponse.getHeaders());
        this.headers = HttpHeaders.readOnlyHttpHeaders(headers);
    }

    @Override // org.springframework.http.client.reactive.ClientHttpResponse
    public HttpStatusCode getStatusCode() {
        return HttpStatusCode.valueOf(this.reactiveResponse.getStatus());
    }

    @Override // org.springframework.http.client.reactive.ClientHttpResponse
    public MultiValueMap<String, ResponseCookie> getCookies() {
        MultiValueMap<String, ResponseCookie> result = new LinkedMultiValueMap<>();
        List<String> cookieHeader = getHeaders().get(HttpHeaders.SET_COOKIE);
        if (cookieHeader != null) {
            cookieHeader.forEach(header -> {
                HttpCookie.parse(header).forEach(cookie -> {
                    result.add(cookie.getName(), ResponseCookie.fromClientResponse(cookie.getName(), cookie.getValue()).domain(cookie.getDomain()).path(cookie.getPath()).maxAge(cookie.getMaxAge()).secure(cookie.getSecure()).httpOnly(cookie.isHttpOnly()).sameSite(parseSameSite(header)).build());
                });
            });
        }
        return CollectionUtils.unmodifiableMultiValueMap(result);
    }

    @Nullable
    private static String parseSameSite(String headerValue) {
        Matcher matcher = SAME_SITE_PATTERN.matcher(headerValue);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return null;
    }

    @Override // org.springframework.http.ReactiveHttpInputMessage
    public Flux<DataBuffer> getBody() {
        return this.content;
    }

    @Override // org.springframework.http.HttpMessage
    public HttpHeaders getHeaders() {
        return this.headers;
    }
}

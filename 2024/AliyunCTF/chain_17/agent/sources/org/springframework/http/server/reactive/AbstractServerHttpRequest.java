package org.springframework.http.server.reactive;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.RequestPath;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/AbstractServerHttpRequest.class */
public abstract class AbstractServerHttpRequest implements ServerHttpRequest {
    private static final Pattern QUERY_PATTERN = Pattern.compile("([^&=]+)(=?)([^&]+)?");
    private final URI uri;
    private final RequestPath path;
    private final HttpHeaders headers;
    private final HttpMethod method;

    @Nullable
    private MultiValueMap<String, String> queryParams;

    @Nullable
    private MultiValueMap<String, HttpCookie> cookies;

    @Nullable
    private SslInfo sslInfo;

    @Nullable
    private String id;

    @Nullable
    private String logPrefix;

    protected abstract MultiValueMap<String, HttpCookie> initCookies();

    @Nullable
    protected abstract SslInfo initSslInfo();

    public abstract <T> T getNativeRequest();

    public AbstractServerHttpRequest(HttpMethod method, URI uri, @Nullable String contextPath, MultiValueMap<String, String> headers) {
        Assert.notNull(method, "Method must not be null");
        Assert.notNull(uri, "Uri must not be null");
        Assert.notNull(headers, "Headers must not be null");
        this.method = method;
        this.uri = uri;
        this.path = RequestPath.parse(uri, contextPath);
        this.headers = HttpHeaders.readOnlyHttpHeaders(headers);
    }

    @Override // org.springframework.http.server.reactive.ServerHttpRequest
    public String getId() {
        if (this.id == null) {
            this.id = initId();
            if (this.id == null) {
                this.id = ObjectUtils.getIdentityHexString(this);
            }
        }
        return this.id;
    }

    @Nullable
    protected String initId() {
        return null;
    }

    @Override // org.springframework.http.HttpRequest
    public HttpMethod getMethod() {
        return this.method;
    }

    @Override // org.springframework.http.HttpRequest
    public URI getURI() {
        return this.uri;
    }

    @Override // org.springframework.http.server.reactive.ServerHttpRequest
    public RequestPath getPath() {
        return this.path;
    }

    @Override // org.springframework.http.HttpMessage
    public HttpHeaders getHeaders() {
        return this.headers;
    }

    @Override // org.springframework.http.server.reactive.ServerHttpRequest
    public MultiValueMap<String, String> getQueryParams() {
        if (this.queryParams == null) {
            this.queryParams = CollectionUtils.unmodifiableMultiValueMap(initQueryParams());
        }
        return this.queryParams;
    }

    protected MultiValueMap<String, String> initQueryParams() {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        String query = getURI().getRawQuery();
        if (query != null) {
            Matcher matcher = QUERY_PATTERN.matcher(query);
            while (matcher.find()) {
                String name = decodeQueryParam(matcher.group(1));
                String eq = matcher.group(2);
                String value = matcher.group(3);
                queryParams.add(name, value != null ? decodeQueryParam(value) : StringUtils.hasLength(eq) ? "" : null);
            }
        }
        return queryParams;
    }

    private String decodeQueryParam(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    @Override // org.springframework.http.server.reactive.ServerHttpRequest
    public MultiValueMap<String, HttpCookie> getCookies() {
        if (this.cookies == null) {
            this.cookies = CollectionUtils.unmodifiableMultiValueMap(initCookies());
        }
        return this.cookies;
    }

    @Override // org.springframework.http.server.reactive.ServerHttpRequest
    @Nullable
    public SslInfo getSslInfo() {
        if (this.sslInfo == null) {
            this.sslInfo = initSslInfo();
        }
        return this.sslInfo;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getLogPrefix() {
        if (this.logPrefix == null) {
            this.logPrefix = "[" + initLogPrefix() + "] ";
        }
        return this.logPrefix;
    }

    protected String initLogPrefix() {
        return getId();
    }
}

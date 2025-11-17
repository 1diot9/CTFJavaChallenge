package org.springframework.web.server;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/server/MethodNotAllowedException.class */
public class MethodNotAllowedException extends ResponseStatusException {
    private final String method;
    private final Set<HttpMethod> httpMethods;

    public MethodNotAllowedException(HttpMethod method, Collection<HttpMethod> supportedMethods) {
        this(method.name(), supportedMethods);
    }

    public MethodNotAllowedException(String method, @Nullable Collection<HttpMethod> supportedMethods) {
        super(HttpStatus.METHOD_NOT_ALLOWED, "Request method '" + method + "' is not supported.", null, null, new Object[]{method, supportedMethods});
        Assert.notNull(method, "'method' is required");
        supportedMethods = supportedMethods == null ? Collections.emptySet() : supportedMethods;
        this.method = method;
        this.httpMethods = Collections.unmodifiableSet(new LinkedHashSet(supportedMethods));
        if (!this.httpMethods.isEmpty()) {
            setDetail("Supported methods: " + this.httpMethods);
        }
    }

    @Override // org.springframework.web.server.ResponseStatusException, org.springframework.web.ErrorResponseException, org.springframework.web.ErrorResponse
    public HttpHeaders getHeaders() {
        if (CollectionUtils.isEmpty(this.httpMethods)) {
            return HttpHeaders.EMPTY;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setAllow(this.httpMethods);
        return headers;
    }

    @Override // org.springframework.web.server.ResponseStatusException
    @Deprecated(since = "6.0")
    public HttpHeaders getResponseHeaders() {
        return getHeaders();
    }

    public String getHttpMethod() {
        return this.method;
    }

    public Set<HttpMethod> getSupportedMethods() {
        return this.httpMethods;
    }
}

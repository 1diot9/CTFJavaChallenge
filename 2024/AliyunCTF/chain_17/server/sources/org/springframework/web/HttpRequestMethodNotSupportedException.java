package org.springframework.web;

import jakarta.servlet.ServletException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/HttpRequestMethodNotSupportedException.class */
public class HttpRequestMethodNotSupportedException extends ServletException implements ErrorResponse {
    private final String method;

    @Nullable
    private final String[] supportedMethods;
    private final ProblemDetail body;

    public HttpRequestMethodNotSupportedException(String method) {
        this(method, (String[]) null);
    }

    public HttpRequestMethodNotSupportedException(String method, @Nullable Collection<String> supportedMethods) {
        this(method, supportedMethods != null ? StringUtils.toStringArray(supportedMethods) : null);
    }

    private HttpRequestMethodNotSupportedException(String method, @Nullable String[] supportedMethods) {
        super("Request method '" + method + "' is not supported");
        this.method = method;
        this.supportedMethods = supportedMethods;
        String detail = "Method '" + method + "' is not supported.";
        this.body = ProblemDetail.forStatusAndDetail(getStatusCode(), detail);
    }

    public String getMethod() {
        return this.method;
    }

    @Nullable
    public String[] getSupportedMethods() {
        return this.supportedMethods;
    }

    @Nullable
    public Set<HttpMethod> getSupportedHttpMethods() {
        if (this.supportedMethods == null) {
            return null;
        }
        Set<HttpMethod> supportedMethods = new LinkedHashSet<>(this.supportedMethods.length);
        for (String value : this.supportedMethods) {
            HttpMethod method = HttpMethod.valueOf(value);
            supportedMethods.add(method);
        }
        return supportedMethods;
    }

    @Override // org.springframework.web.ErrorResponse
    public HttpStatusCode getStatusCode() {
        return HttpStatus.METHOD_NOT_ALLOWED;
    }

    @Override // org.springframework.web.ErrorResponse
    public HttpHeaders getHeaders() {
        if (ObjectUtils.isEmpty((Object[]) this.supportedMethods)) {
            return HttpHeaders.EMPTY;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.ALLOW, StringUtils.arrayToDelimitedString(this.supportedMethods, ", "));
        return headers;
    }

    @Override // org.springframework.web.ErrorResponse
    public ProblemDetail getBody() {
        return this.body;
    }

    @Override // org.springframework.web.ErrorResponse
    public Object[] getDetailMessageArguments() {
        return new Object[]{getMethod(), getSupportedHttpMethods()};
    }
}

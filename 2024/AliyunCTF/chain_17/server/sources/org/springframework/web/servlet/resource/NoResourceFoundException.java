package org.springframework.web.servlet.resource;

import jakarta.servlet.ServletException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/resource/NoResourceFoundException.class */
public class NoResourceFoundException extends ServletException implements ErrorResponse {
    private final HttpMethod httpMethod;
    private final String resourcePath;
    private final ProblemDetail body;

    public NoResourceFoundException(HttpMethod httpMethod, String resourcePath) {
        super("No static resource " + resourcePath + ".");
        this.httpMethod = httpMethod;
        this.resourcePath = resourcePath;
        this.body = ProblemDetail.forStatusAndDetail(getStatusCode(), getMessage());
    }

    public HttpMethod getHttpMethod() {
        return this.httpMethod;
    }

    public String getResourcePath() {
        return this.resourcePath;
    }

    @Override // org.springframework.web.ErrorResponse
    public HttpStatusCode getStatusCode() {
        return HttpStatus.NOT_FOUND;
    }

    @Override // org.springframework.web.ErrorResponse
    public ProblemDetail getBody() {
        return this.body;
    }
}

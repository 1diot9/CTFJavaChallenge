package org.springframework.web.servlet;

import jakarta.servlet.ServletException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/NoHandlerFoundException.class */
public class NoHandlerFoundException extends ServletException implements ErrorResponse {
    private final String httpMethod;
    private final String requestURL;
    private final HttpHeaders requestHeaders;
    private final ProblemDetail body;

    public NoHandlerFoundException(String httpMethod, String requestURL, HttpHeaders headers) {
        super("No endpoint " + httpMethod + " " + requestURL + ".");
        this.httpMethod = httpMethod;
        this.requestURL = requestURL;
        this.requestHeaders = headers;
        this.body = ProblemDetail.forStatusAndDetail(getStatusCode(), getMessage());
    }

    @Override // org.springframework.web.ErrorResponse
    public HttpStatusCode getStatusCode() {
        return HttpStatus.NOT_FOUND;
    }

    public String getHttpMethod() {
        return this.httpMethod;
    }

    public String getRequestURL() {
        return this.requestURL;
    }

    @Override // org.springframework.web.ErrorResponse
    public HttpHeaders getHeaders() {
        return super.getHeaders();
    }

    public HttpHeaders getRequestHeaders() {
        return this.requestHeaders;
    }

    @Override // org.springframework.web.ErrorResponse
    public ProblemDetail getBody() {
        return this.body;
    }
}

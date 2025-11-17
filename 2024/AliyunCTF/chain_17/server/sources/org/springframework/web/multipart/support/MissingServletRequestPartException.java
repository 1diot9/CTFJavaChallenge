package org.springframework.web.multipart.support;

import jakarta.servlet.ServletException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/multipart/support/MissingServletRequestPartException.class */
public class MissingServletRequestPartException extends ServletException implements ErrorResponse {
    private final String requestPartName;
    private final ProblemDetail body;

    public MissingServletRequestPartException(String requestPartName) {
        super("Required part '" + requestPartName + "' is not present.");
        this.body = ProblemDetail.forStatus(getStatusCode());
        this.requestPartName = requestPartName;
        getBody().setDetail(getMessage());
    }

    public String getRequestPartName() {
        return this.requestPartName;
    }

    @Override // org.springframework.web.ErrorResponse
    public HttpStatusCode getStatusCode() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override // org.springframework.web.ErrorResponse
    public ProblemDetail getBody() {
        return this.body;
    }

    @Override // org.springframework.web.ErrorResponse
    public Object[] getDetailMessageArguments() {
        return new Object[]{getRequestPartName()};
    }
}

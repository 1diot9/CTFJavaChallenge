package org.springframework.web;

import java.net.URI;
import org.springframework.core.NestedRuntimeException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/ErrorResponseException.class */
public class ErrorResponseException extends NestedRuntimeException implements ErrorResponse {
    private final HttpStatusCode status;
    private final HttpHeaders headers;
    private final ProblemDetail body;
    private final String messageDetailCode;

    @Nullable
    private final Object[] messageDetailArguments;

    public ErrorResponseException(HttpStatusCode status) {
        this(status, null);
    }

    public ErrorResponseException(HttpStatusCode status, @Nullable Throwable cause) {
        this(status, ProblemDetail.forStatus(status), cause);
    }

    public ErrorResponseException(HttpStatusCode status, ProblemDetail body, @Nullable Throwable cause) {
        this(status, body, cause, null, null);
    }

    public ErrorResponseException(HttpStatusCode status, ProblemDetail body, @Nullable Throwable cause, @Nullable String messageDetailCode, @Nullable Object[] messageDetailArguments) {
        super(null, cause);
        this.headers = new HttpHeaders();
        this.status = status;
        this.body = body;
        this.messageDetailCode = initMessageDetailCode(messageDetailCode);
        this.messageDetailArguments = messageDetailArguments;
    }

    private String initMessageDetailCode(@Nullable String messageDetailCode) {
        return messageDetailCode != null ? messageDetailCode : ErrorResponse.getDefaultDetailMessageCode(getClass(), null);
    }

    @Override // org.springframework.web.ErrorResponse
    public HttpStatusCode getStatusCode() {
        return this.status;
    }

    @Override // org.springframework.web.ErrorResponse
    public HttpHeaders getHeaders() {
        return this.headers;
    }

    public void setType(URI type) {
        this.body.setType(type);
    }

    public void setTitle(@Nullable String title) {
        this.body.setTitle(title);
    }

    public void setDetail(@Nullable String detail) {
        this.body.setDetail(detail);
    }

    public void setInstance(@Nullable URI instance) {
        this.body.setInstance(instance);
    }

    @Override // org.springframework.web.ErrorResponse
    public final ProblemDetail getBody() {
        return this.body;
    }

    @Override // org.springframework.web.ErrorResponse
    public String getDetailMessageCode() {
        return this.messageDetailCode;
    }

    @Override // org.springframework.web.ErrorResponse
    public Object[] getDetailMessageArguments() {
        return this.messageDetailArguments;
    }

    @Override // java.lang.Throwable
    public String getMessage() {
        return this.status + (!this.headers.isEmpty() ? ", headers=" + this.headers : "") + ", " + this.body;
    }
}

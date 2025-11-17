package org.springframework.web.bind;

import jakarta.servlet.ServletException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.lang.Nullable;
import org.springframework.web.ErrorResponse;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/bind/ServletRequestBindingException.class */
public class ServletRequestBindingException extends ServletException implements ErrorResponse {
    private final ProblemDetail body;
    private final String messageDetailCode;

    @Nullable
    private final Object[] messageDetailArguments;

    public ServletRequestBindingException(String msg) {
        this(msg, null, null);
    }

    public ServletRequestBindingException(String msg, Throwable cause) {
        this(msg, cause, null, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ServletRequestBindingException(String msg, @Nullable String messageDetailCode, @Nullable Object[] messageDetailArguments) {
        this(msg, null, messageDetailCode, messageDetailArguments);
    }

    protected ServletRequestBindingException(String msg, @Nullable Throwable cause, @Nullable String messageDetailCode, @Nullable Object[] messageDetailArguments) {
        super(msg, cause);
        this.body = ProblemDetail.forStatus(getStatusCode());
        this.messageDetailCode = initMessageDetailCode(messageDetailCode);
        this.messageDetailArguments = messageDetailArguments;
    }

    private String initMessageDetailCode(@Nullable String messageDetailCode) {
        return messageDetailCode != null ? messageDetailCode : ErrorResponse.getDefaultDetailMessageCode(getClass(), null);
    }

    public HttpStatusCode getStatusCode() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override // org.springframework.web.ErrorResponse
    public ProblemDetail getBody() {
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
}

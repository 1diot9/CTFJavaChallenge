package org.springframework.web;

import jakarta.servlet.ServletException;
import java.util.Collections;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/HttpMediaTypeException.class */
public abstract class HttpMediaTypeException extends ServletException implements ErrorResponse {
    private final List<MediaType> supportedMediaTypes;
    private final ProblemDetail body;
    private final String messageDetailCode;

    @Nullable
    private final Object[] messageDetailArguments;

    @Deprecated
    protected HttpMediaTypeException(String message) {
        this(message, Collections.emptyList());
    }

    @Deprecated
    protected HttpMediaTypeException(String message, List<MediaType> supportedMediaTypes) {
        this(message, supportedMediaTypes, null, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public HttpMediaTypeException(String message, List<MediaType> supportedMediaTypes, @Nullable String messageDetailCode, @Nullable Object[] messageDetailArguments) {
        super(message);
        this.body = ProblemDetail.forStatus(getStatusCode());
        this.supportedMediaTypes = Collections.unmodifiableList(supportedMediaTypes);
        this.messageDetailCode = messageDetailCode != null ? messageDetailCode : ErrorResponse.getDefaultDetailMessageCode(getClass(), null);
        this.messageDetailArguments = messageDetailArguments;
    }

    public List<MediaType> getSupportedMediaTypes() {
        return this.supportedMediaTypes;
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

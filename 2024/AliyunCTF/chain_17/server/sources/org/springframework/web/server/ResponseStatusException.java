package org.springframework.web.server;

import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.lang.Nullable;
import org.springframework.web.ErrorResponseException;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/server/ResponseStatusException.class */
public class ResponseStatusException extends ErrorResponseException {

    @Nullable
    private final String reason;

    public ResponseStatusException(HttpStatusCode status) {
        this(status, null);
    }

    public ResponseStatusException(HttpStatusCode status, @Nullable String reason) {
        this(status, reason, (Throwable) null);
    }

    public ResponseStatusException(int rawStatusCode, @Nullable String reason, @Nullable Throwable cause) {
        this(HttpStatusCode.valueOf(rawStatusCode), reason, cause);
    }

    public ResponseStatusException(HttpStatusCode status, @Nullable String reason, @Nullable Throwable cause) {
        this(status, reason, cause, null, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ResponseStatusException(HttpStatusCode status, @Nullable String reason, @Nullable Throwable cause, @Nullable String messageDetailCode, @Nullable Object[] messageDetailArguments) {
        super(status, ProblemDetail.forStatus(status), cause, messageDetailCode, messageDetailArguments);
        this.reason = reason;
        setDetail(reason);
    }

    @Nullable
    public String getReason() {
        return this.reason;
    }

    @Override // org.springframework.web.ErrorResponseException, org.springframework.web.ErrorResponse
    public HttpHeaders getHeaders() {
        return getResponseHeaders();
    }

    @Deprecated(since = "6.0")
    public HttpHeaders getResponseHeaders() {
        return HttpHeaders.EMPTY;
    }

    @Override // org.springframework.web.ErrorResponse
    public ProblemDetail updateAndGetBody(@Nullable MessageSource messageSource, Locale locale) {
        super.updateAndGetBody(messageSource, locale);
        if (messageSource != null && getReason() != null && getReason().equals(getBody().getDetail())) {
            Object[] arguments = getDetailMessageArguments(messageSource, locale);
            String resolved = messageSource.getMessage(getReason(), arguments, null, locale);
            if (resolved != null) {
                getBody().setDetail(resolved);
            }
        }
        return getBody();
    }

    @Override // org.springframework.web.ErrorResponseException, java.lang.Throwable
    public String getMessage() {
        return getStatusCode() + (this.reason != null ? " \"" + this.reason + "\"" : "");
    }
}

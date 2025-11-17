package org.springframework.web.client;

import java.nio.charset.Charset;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/client/HttpStatusCodeException.class */
public abstract class HttpStatusCodeException extends RestClientResponseException {
    private static final long serialVersionUID = 5696801857651587810L;

    /* JADX INFO: Access modifiers changed from: protected */
    public HttpStatusCodeException(HttpStatusCode statusCode) {
        this(statusCode, name(statusCode), null, null, null);
    }

    private static String name(HttpStatusCode statusCode) {
        if (statusCode instanceof HttpStatus) {
            HttpStatus status = (HttpStatus) statusCode;
            return status.name();
        }
        return "";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public HttpStatusCodeException(HttpStatusCode statusCode, String statusText) {
        this(statusCode, statusText, null, null, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public HttpStatusCodeException(HttpStatusCode statusCode, String statusText, @Nullable byte[] responseBody, @Nullable Charset responseCharset) {
        this(statusCode, statusText, null, responseBody, responseCharset);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public HttpStatusCodeException(HttpStatusCode statusCode, String statusText, @Nullable HttpHeaders responseHeaders, @Nullable byte[] responseBody, @Nullable Charset responseCharset) {
        this(getMessage(statusCode, statusText), statusCode, statusText, responseHeaders, responseBody, responseCharset);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public HttpStatusCodeException(String message, HttpStatusCode statusCode, String statusText, @Nullable HttpHeaders responseHeaders, @Nullable byte[] responseBody, @Nullable Charset responseCharset) {
        super(message, statusCode, statusText, responseHeaders, responseBody, responseCharset);
    }

    private static String getMessage(HttpStatusCode statusCode, String statusText) {
        if (!StringUtils.hasLength(statusText) && (statusCode instanceof HttpStatus)) {
            HttpStatus status = (HttpStatus) statusCode;
            statusText = status.getReasonPhrase();
        }
        return statusCode.value() + " " + statusText;
    }
}

package org.springframework.web.client;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/client/UnknownContentTypeException.class */
public class UnknownContentTypeException extends RestClientException {
    private static final long serialVersionUID = 2759516676367274084L;
    private final transient Type targetType;
    private final MediaType contentType;
    private final HttpStatusCode statusCode;
    private final String statusText;
    private final byte[] responseBody;
    private final HttpHeaders responseHeaders;

    public UnknownContentTypeException(Type targetType, MediaType contentType, int statusCode, String statusText, HttpHeaders responseHeaders, byte[] responseBody) {
        this(targetType, contentType, HttpStatusCode.valueOf(statusCode), statusText, responseHeaders, responseBody);
    }

    public UnknownContentTypeException(Type targetType, MediaType contentType, HttpStatusCode statusCode, String statusText, HttpHeaders responseHeaders, byte[] responseBody) {
        super("Could not extract response: no suitable HttpMessageConverter found for response type [" + targetType + "] and content type [" + contentType + "]");
        this.targetType = targetType;
        this.contentType = contentType;
        this.statusCode = statusCode;
        this.statusText = statusText;
        this.responseHeaders = responseHeaders;
        this.responseBody = responseBody;
    }

    public Type getTargetType() {
        return this.targetType;
    }

    public MediaType getContentType() {
        return this.contentType;
    }

    public HttpStatusCode getStatusCode() {
        return this.statusCode;
    }

    @Deprecated(since = "6.0")
    public int getRawStatusCode() {
        return this.statusCode.value();
    }

    public String getStatusText() {
        return this.statusText;
    }

    @Nullable
    public HttpHeaders getResponseHeaders() {
        return this.responseHeaders;
    }

    public byte[] getResponseBody() {
        return this.responseBody;
    }

    public String getResponseBodyAsString() {
        return new String(this.responseBody, this.contentType.getCharset() != null ? this.contentType.getCharset() : StandardCharsets.UTF_8);
    }
}

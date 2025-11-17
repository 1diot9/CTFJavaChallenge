package org.springframework.web;

import java.util.Collections;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/HttpMediaTypeNotSupportedException.class */
public class HttpMediaTypeNotSupportedException extends HttpMediaTypeException {
    private static final String PARSE_ERROR_DETAIL_CODE = ErrorResponse.getDefaultDetailMessageCode(HttpMediaTypeNotSupportedException.class, "parseError");

    @Nullable
    private final MediaType contentType;

    @Nullable
    private final HttpMethod httpMethod;

    public HttpMediaTypeNotSupportedException(String message) {
        this(message, (List<MediaType>) Collections.emptyList());
    }

    public HttpMediaTypeNotSupportedException(String message, List<MediaType> mediaTypes) {
        super(message, mediaTypes, PARSE_ERROR_DETAIL_CODE, null);
        this.contentType = null;
        this.httpMethod = null;
        getBody().setDetail("Could not parse Content-Type.");
    }

    public HttpMediaTypeNotSupportedException(@Nullable MediaType contentType, List<MediaType> mediaTypes) {
        this(contentType, mediaTypes, null);
    }

    public HttpMediaTypeNotSupportedException(@Nullable MediaType contentType, List<MediaType> mediaTypes, @Nullable HttpMethod httpMethod) {
        this(contentType, mediaTypes, httpMethod, "Content-Type " + (contentType != null ? "'" + contentType + "' " : "") + "is not supported");
    }

    public HttpMediaTypeNotSupportedException(@Nullable MediaType contentType, List<MediaType> supportedMediaTypes, @Nullable HttpMethod httpMethod, String message) {
        super(message, supportedMediaTypes, null, new Object[]{contentType, supportedMediaTypes});
        this.contentType = contentType;
        this.httpMethod = httpMethod;
        getBody().setDetail("Content-Type '" + this.contentType + "' is not supported.");
    }

    @Nullable
    public MediaType getContentType() {
        return this.contentType;
    }

    @Override // org.springframework.web.ErrorResponse
    public HttpStatusCode getStatusCode() {
        return HttpStatus.UNSUPPORTED_MEDIA_TYPE;
    }

    @Override // org.springframework.web.ErrorResponse
    public HttpHeaders getHeaders() {
        if (CollectionUtils.isEmpty(getSupportedMediaTypes())) {
            return HttpHeaders.EMPTY;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(getSupportedMediaTypes());
        if (HttpMethod.PATCH.equals(this.httpMethod)) {
            headers.setAcceptPatch(getSupportedMediaTypes());
        }
        return headers;
    }
}

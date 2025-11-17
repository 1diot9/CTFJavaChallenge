package org.springframework.web.server;

import java.util.Collections;
import java.util.List;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.web.ErrorResponse;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/server/UnsupportedMediaTypeStatusException.class */
public class UnsupportedMediaTypeStatusException extends ResponseStatusException {
    private static final String PARSE_ERROR_DETAIL_CODE = ErrorResponse.getDefaultDetailMessageCode(UnsupportedMediaTypeStatusException.class, "parseError");

    @Nullable
    private final MediaType contentType;
    private final List<MediaType> supportedMediaTypes;

    @Nullable
    private final ResolvableType bodyType;

    @Nullable
    private final HttpMethod method;

    public UnsupportedMediaTypeStatusException(@Nullable String reason) {
        this(reason, (List<MediaType>) Collections.emptyList());
    }

    public UnsupportedMediaTypeStatusException(@Nullable String reason, List<MediaType> supportedTypes) {
        super(HttpStatus.UNSUPPORTED_MEDIA_TYPE, reason, null, PARSE_ERROR_DETAIL_CODE, null);
        this.contentType = null;
        this.supportedMediaTypes = Collections.unmodifiableList(supportedTypes);
        this.bodyType = null;
        this.method = null;
        setDetail("Could not parse Content-Type.");
    }

    public UnsupportedMediaTypeStatusException(@Nullable MediaType contentType, List<MediaType> supportedTypes) {
        this(contentType, supportedTypes, null, null);
    }

    public UnsupportedMediaTypeStatusException(@Nullable MediaType contentType, List<MediaType> supportedTypes, @Nullable ResolvableType bodyType) {
        this(contentType, supportedTypes, bodyType, null);
    }

    public UnsupportedMediaTypeStatusException(@Nullable MediaType contentType, List<MediaType> supportedTypes, @Nullable HttpMethod method) {
        this(contentType, supportedTypes, null, method);
    }

    public UnsupportedMediaTypeStatusException(@Nullable MediaType contentType, List<MediaType> supportedTypes, @Nullable ResolvableType bodyType, @Nullable HttpMethod method) {
        super(HttpStatus.UNSUPPORTED_MEDIA_TYPE, initMessage(contentType, bodyType), null, null, new Object[]{contentType, supportedTypes});
        this.contentType = contentType;
        this.supportedMediaTypes = Collections.unmodifiableList(supportedTypes);
        this.bodyType = bodyType;
        this.method = method;
        setDetail(contentType != null ? "Content-Type '" + contentType + "' is not supported." : null);
    }

    private static String initMessage(@Nullable MediaType contentType, @Nullable ResolvableType bodyType) {
        return "Content type '" + (contentType != null ? contentType : "") + "' not supported" + (bodyType != null ? " for bodyType=" + bodyType : "");
    }

    @Nullable
    public MediaType getContentType() {
        return this.contentType;
    }

    public List<MediaType> getSupportedMediaTypes() {
        return this.supportedMediaTypes;
    }

    @Nullable
    public ResolvableType getBodyType() {
        return this.bodyType;
    }

    @Override // org.springframework.web.server.ResponseStatusException, org.springframework.web.ErrorResponseException, org.springframework.web.ErrorResponse
    public HttpHeaders getHeaders() {
        if (CollectionUtils.isEmpty(this.supportedMediaTypes)) {
            return HttpHeaders.EMPTY;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(this.supportedMediaTypes);
        if (this.method == HttpMethod.PATCH) {
            headers.setAcceptPatch(this.supportedMediaTypes);
        }
        return headers;
    }

    @Override // org.springframework.web.server.ResponseStatusException
    @Deprecated(since = "6.0")
    public HttpHeaders getResponseHeaders() {
        return getHeaders();
    }
}

package org.springframework.web.server;

import java.util.Collections;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.web.ErrorResponse;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/server/NotAcceptableStatusException.class */
public class NotAcceptableStatusException extends ResponseStatusException {
    private static final String PARSE_ERROR_DETAIL_CODE = ErrorResponse.getDefaultDetailMessageCode(NotAcceptableStatusException.class, "parseError");
    private final List<MediaType> supportedMediaTypes;

    public NotAcceptableStatusException(String reason) {
        super(HttpStatus.NOT_ACCEPTABLE, reason, null, PARSE_ERROR_DETAIL_CODE, null);
        this.supportedMediaTypes = Collections.emptyList();
        setDetail("Could not parse Accept header.");
    }

    public NotAcceptableStatusException(List<MediaType> mediaTypes) {
        super(HttpStatus.NOT_ACCEPTABLE, "Could not find acceptable representation", null, null, new Object[]{mediaTypes});
        this.supportedMediaTypes = Collections.unmodifiableList(mediaTypes);
        setDetail("Acceptable representations: " + mediaTypes + ".");
    }

    @Override // org.springframework.web.server.ResponseStatusException, org.springframework.web.ErrorResponseException, org.springframework.web.ErrorResponse
    public HttpHeaders getHeaders() {
        if (CollectionUtils.isEmpty(this.supportedMediaTypes)) {
            return HttpHeaders.EMPTY;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(this.supportedMediaTypes);
        return headers;
    }

    @Override // org.springframework.web.server.ResponseStatusException
    @Deprecated(since = "6.0")
    public HttpHeaders getResponseHeaders() {
        return getHeaders();
    }

    public List<MediaType> getSupportedMediaTypes() {
        return this.supportedMediaTypes;
    }
}

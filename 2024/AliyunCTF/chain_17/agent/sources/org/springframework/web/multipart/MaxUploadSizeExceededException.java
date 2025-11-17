package org.springframework.web.multipart;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.lang.Nullable;
import org.springframework.web.ErrorResponse;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/multipart/MaxUploadSizeExceededException.class */
public class MaxUploadSizeExceededException extends MultipartException implements ErrorResponse {
    private static final ProblemDetail body = ProblemDetail.forStatusAndDetail(HttpStatus.PAYLOAD_TOO_LARGE, "Maximum upload size exceeded");
    private final long maxUploadSize;

    public MaxUploadSizeExceededException(long maxUploadSize) {
        this(maxUploadSize, null);
    }

    public MaxUploadSizeExceededException(long maxUploadSize, @Nullable Throwable ex) {
        super("Maximum upload size " + (maxUploadSize >= 0 ? "of " + maxUploadSize + " bytes " : "") + "exceeded", ex);
        this.maxUploadSize = maxUploadSize;
    }

    public long getMaxUploadSize() {
        return this.maxUploadSize;
    }

    @Override // org.springframework.web.ErrorResponse
    public HttpStatusCode getStatusCode() {
        return HttpStatus.PAYLOAD_TOO_LARGE;
    }

    @Override // org.springframework.web.ErrorResponse
    public ProblemDetail getBody() {
        return body;
    }
}

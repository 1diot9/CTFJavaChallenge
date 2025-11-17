package org.springframework.web.context.request.async;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/context/request/async/AsyncRequestTimeoutException.class */
public class AsyncRequestTimeoutException extends RuntimeException implements ErrorResponse {
    @Override // org.springframework.web.ErrorResponse
    public HttpStatusCode getStatusCode() {
        return HttpStatus.SERVICE_UNAVAILABLE;
    }

    @Override // org.springframework.web.ErrorResponse
    public ProblemDetail getBody() {
        return ProblemDetail.forStatus(getStatusCode());
    }
}

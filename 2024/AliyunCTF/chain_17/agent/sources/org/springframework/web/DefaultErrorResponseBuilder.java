package org.springframework.web;

import java.net.URI;
import java.util.function.Consumer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.ErrorResponse;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/DefaultErrorResponseBuilder.class */
public final class DefaultErrorResponseBuilder implements ErrorResponse.Builder {
    private final Throwable exception;
    private final HttpStatusCode statusCode;

    @Nullable
    private HttpHeaders headers;
    private final ProblemDetail problemDetail;
    private String typeMessageCode;
    private String titleMessageCode;
    private String detailMessageCode;

    @Nullable
    private Object[] detailMessageArguments;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultErrorResponseBuilder(Throwable ex, ProblemDetail problemDetail) {
        Assert.notNull(ex, "Throwable is required");
        this.exception = ex;
        this.statusCode = HttpStatusCode.valueOf(problemDetail.getStatus());
        this.problemDetail = problemDetail;
        this.typeMessageCode = ErrorResponse.getDefaultTypeMessageCode(ex.getClass());
        this.titleMessageCode = ErrorResponse.getDefaultTitleMessageCode(ex.getClass());
        this.detailMessageCode = ErrorResponse.getDefaultDetailMessageCode(ex.getClass(), null);
    }

    @Override // org.springframework.web.ErrorResponse.Builder
    public ErrorResponse.Builder header(String headerName, String... headerValues) {
        this.headers = this.headers != null ? this.headers : new HttpHeaders();
        for (String headerValue : headerValues) {
            this.headers.add(headerName, headerValue);
        }
        return this;
    }

    @Override // org.springframework.web.ErrorResponse.Builder
    public ErrorResponse.Builder headers(Consumer<HttpHeaders> headersConsumer) {
        return this;
    }

    @Override // org.springframework.web.ErrorResponse.Builder
    public ErrorResponse.Builder type(URI type) {
        this.problemDetail.setType(type);
        return this;
    }

    @Override // org.springframework.web.ErrorResponse.Builder
    public ErrorResponse.Builder typeMessageCode(String messageCode) {
        this.typeMessageCode = messageCode;
        return this;
    }

    @Override // org.springframework.web.ErrorResponse.Builder
    public ErrorResponse.Builder title(@Nullable String title) {
        this.problemDetail.setTitle(title);
        return this;
    }

    @Override // org.springframework.web.ErrorResponse.Builder
    public ErrorResponse.Builder titleMessageCode(String messageCode) {
        Assert.notNull(messageCode, "`titleMessageCode` is required");
        this.titleMessageCode = messageCode;
        return this;
    }

    @Override // org.springframework.web.ErrorResponse.Builder
    public ErrorResponse.Builder instance(@Nullable URI instance) {
        this.problemDetail.setInstance(instance);
        return this;
    }

    @Override // org.springframework.web.ErrorResponse.Builder
    public ErrorResponse.Builder detail(String detail) {
        this.problemDetail.setDetail(detail);
        return this;
    }

    @Override // org.springframework.web.ErrorResponse.Builder
    public ErrorResponse.Builder detailMessageCode(String messageCode) {
        Assert.notNull(messageCode, "`detailMessageCode` is required");
        this.detailMessageCode = messageCode;
        return this;
    }

    @Override // org.springframework.web.ErrorResponse.Builder
    public ErrorResponse.Builder detailMessageArguments(Object... messageArguments) {
        this.detailMessageArguments = messageArguments;
        return this;
    }

    @Override // org.springframework.web.ErrorResponse.Builder
    public ErrorResponse.Builder property(String name, @Nullable Object value) {
        this.problemDetail.setProperty(name, value);
        return this;
    }

    @Override // org.springframework.web.ErrorResponse.Builder
    public ErrorResponse build() {
        return new SimpleErrorResponse(this.exception, this.statusCode, this.headers, this.problemDetail, this.typeMessageCode, this.titleMessageCode, this.detailMessageCode, this.detailMessageArguments);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/DefaultErrorResponseBuilder$SimpleErrorResponse.class */
    private static class SimpleErrorResponse implements ErrorResponse {
        private final Throwable exception;
        private final HttpStatusCode statusCode;
        private final HttpHeaders headers;
        private final ProblemDetail problemDetail;
        private final String typeMessageCode;
        private final String titleMessageCode;
        private final String detailMessageCode;

        @Nullable
        private final Object[] detailMessageArguments;

        SimpleErrorResponse(Throwable ex, HttpStatusCode statusCode, @Nullable HttpHeaders headers, ProblemDetail problemDetail, String typeMessageCode, String titleMessageCode, String detailMessageCode, @Nullable Object[] detailMessageArguments) {
            this.exception = ex;
            this.statusCode = statusCode;
            this.headers = headers != null ? headers : HttpHeaders.EMPTY;
            this.problemDetail = problemDetail;
            this.typeMessageCode = typeMessageCode;
            this.titleMessageCode = titleMessageCode;
            this.detailMessageCode = detailMessageCode;
            this.detailMessageArguments = detailMessageArguments;
        }

        @Override // org.springframework.web.ErrorResponse
        public HttpStatusCode getStatusCode() {
            return this.statusCode;
        }

        @Override // org.springframework.web.ErrorResponse
        public HttpHeaders getHeaders() {
            return this.headers;
        }

        @Override // org.springframework.web.ErrorResponse
        public ProblemDetail getBody() {
            return this.problemDetail;
        }

        @Override // org.springframework.web.ErrorResponse
        public String getTypeMessageCode() {
            return this.typeMessageCode;
        }

        @Override // org.springframework.web.ErrorResponse
        public String getTitleMessageCode() {
            return this.titleMessageCode;
        }

        @Override // org.springframework.web.ErrorResponse
        public String getDetailMessageCode() {
            return this.detailMessageCode;
        }

        @Override // org.springframework.web.ErrorResponse
        public Object[] getDetailMessageArguments() {
            return this.detailMessageArguments;
        }

        public String toString() {
            return "ErrorResponse{status=" + this.statusCode + ", headers=" + this.headers + ", body=" + this.problemDetail + ", exception=" + this.exception + "}";
        }
    }
}

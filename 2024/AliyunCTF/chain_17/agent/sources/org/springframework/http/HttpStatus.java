package org.springframework.http;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.filters.RateLimitFilter;
import org.apache.catalina.servlets.WebdavStatus;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/HttpStatus.class */
public enum HttpStatus implements HttpStatusCode {
    CONTINUE(100, Series.INFORMATIONAL, "Continue"),
    SWITCHING_PROTOCOLS(101, Series.INFORMATIONAL, "Switching Protocols"),
    PROCESSING(102, Series.INFORMATIONAL, "Processing"),
    EARLY_HINTS(103, Series.INFORMATIONAL, "Early Hints"),
    CHECKPOINT(103, Series.INFORMATIONAL, "Checkpoint"),
    OK(200, Series.SUCCESSFUL, "OK"),
    CREATED(201, Series.SUCCESSFUL, "Created"),
    ACCEPTED(202, Series.SUCCESSFUL, "Accepted"),
    NON_AUTHORITATIVE_INFORMATION(203, Series.SUCCESSFUL, "Non-Authoritative Information"),
    NO_CONTENT(204, Series.SUCCESSFUL, "No Content"),
    RESET_CONTENT(205, Series.SUCCESSFUL, "Reset Content"),
    PARTIAL_CONTENT(206, Series.SUCCESSFUL, "Partial Content"),
    MULTI_STATUS(WebdavStatus.SC_MULTI_STATUS, Series.SUCCESSFUL, "Multi-Status"),
    ALREADY_REPORTED(208, Series.SUCCESSFUL, "Already Reported"),
    IM_USED(226, Series.SUCCESSFUL, "IM Used"),
    MULTIPLE_CHOICES(300, Series.REDIRECTION, "Multiple Choices"),
    MOVED_PERMANENTLY(301, Series.REDIRECTION, "Moved Permanently"),
    FOUND(302, Series.REDIRECTION, "Found"),
    MOVED_TEMPORARILY(302, Series.REDIRECTION, "Moved Temporarily"),
    SEE_OTHER(303, Series.REDIRECTION, "See Other"),
    NOT_MODIFIED(304, Series.REDIRECTION, "Not Modified"),
    USE_PROXY(305, Series.REDIRECTION, "Use Proxy"),
    TEMPORARY_REDIRECT(307, Series.REDIRECTION, "Temporary Redirect"),
    PERMANENT_REDIRECT(cn.hutool.http.HttpStatus.HTTP_PERMANENT_REDIRECT, Series.REDIRECTION, "Permanent Redirect"),
    BAD_REQUEST(400, Series.CLIENT_ERROR, "Bad Request"),
    UNAUTHORIZED(401, Series.CLIENT_ERROR, "Unauthorized"),
    PAYMENT_REQUIRED(402, Series.CLIENT_ERROR, "Payment Required"),
    FORBIDDEN(403, Series.CLIENT_ERROR, "Forbidden"),
    NOT_FOUND(404, Series.CLIENT_ERROR, "Not Found"),
    METHOD_NOT_ALLOWED(405, Series.CLIENT_ERROR, "Method Not Allowed"),
    NOT_ACCEPTABLE(406, Series.CLIENT_ERROR, "Not Acceptable"),
    PROXY_AUTHENTICATION_REQUIRED(407, Series.CLIENT_ERROR, "Proxy Authentication Required"),
    REQUEST_TIMEOUT(408, Series.CLIENT_ERROR, "Request Timeout"),
    CONFLICT(409, Series.CLIENT_ERROR, "Conflict"),
    GONE(410, Series.CLIENT_ERROR, "Gone"),
    LENGTH_REQUIRED(411, Series.CLIENT_ERROR, "Length Required"),
    PRECONDITION_FAILED(412, Series.CLIENT_ERROR, "Precondition Failed"),
    PAYLOAD_TOO_LARGE(413, Series.CLIENT_ERROR, "Payload Too Large"),
    REQUEST_ENTITY_TOO_LARGE(413, Series.CLIENT_ERROR, "Request Entity Too Large"),
    URI_TOO_LONG(414, Series.CLIENT_ERROR, "URI Too Long"),
    REQUEST_URI_TOO_LONG(414, Series.CLIENT_ERROR, "Request-URI Too Long"),
    UNSUPPORTED_MEDIA_TYPE(415, Series.CLIENT_ERROR, "Unsupported Media Type"),
    REQUESTED_RANGE_NOT_SATISFIABLE(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE, Series.CLIENT_ERROR, "Requested range not satisfiable"),
    EXPECTATION_FAILED(HttpServletResponse.SC_EXPECTATION_FAILED, Series.CLIENT_ERROR, "Expectation Failed"),
    I_AM_A_TEAPOT(WebdavStatus.SC_UNPROCESSABLE_ENTITY, Series.CLIENT_ERROR, "I'm a teapot"),
    INSUFFICIENT_SPACE_ON_RESOURCE(WebdavStatus.SC_INSUFFICIENT_SPACE_ON_RESOURCE, Series.CLIENT_ERROR, "Insufficient Space On Resource"),
    METHOD_FAILURE(WebdavStatus.SC_METHOD_FAILURE, Series.CLIENT_ERROR, "Method Failure"),
    DESTINATION_LOCKED(421, Series.CLIENT_ERROR, "Destination Locked"),
    UNPROCESSABLE_ENTITY(422, Series.CLIENT_ERROR, "Unprocessable Entity"),
    LOCKED(WebdavStatus.SC_LOCKED, Series.CLIENT_ERROR, "Locked"),
    FAILED_DEPENDENCY(424, Series.CLIENT_ERROR, "Failed Dependency"),
    TOO_EARLY(425, Series.CLIENT_ERROR, "Too Early"),
    UPGRADE_REQUIRED(426, Series.CLIENT_ERROR, "Upgrade Required"),
    PRECONDITION_REQUIRED(428, Series.CLIENT_ERROR, "Precondition Required"),
    TOO_MANY_REQUESTS(RateLimitFilter.DEFAULT_STATUS_CODE, Series.CLIENT_ERROR, "Too Many Requests"),
    REQUEST_HEADER_FIELDS_TOO_LARGE(431, Series.CLIENT_ERROR, "Request Header Fields Too Large"),
    UNAVAILABLE_FOR_LEGAL_REASONS(451, Series.CLIENT_ERROR, "Unavailable For Legal Reasons"),
    INTERNAL_SERVER_ERROR(500, Series.SERVER_ERROR, "Internal Server Error"),
    NOT_IMPLEMENTED(501, Series.SERVER_ERROR, "Not Implemented"),
    BAD_GATEWAY(502, Series.SERVER_ERROR, "Bad Gateway"),
    SERVICE_UNAVAILABLE(503, Series.SERVER_ERROR, "Service Unavailable"),
    GATEWAY_TIMEOUT(504, Series.SERVER_ERROR, "Gateway Timeout"),
    HTTP_VERSION_NOT_SUPPORTED(505, Series.SERVER_ERROR, "HTTP Version not supported"),
    VARIANT_ALSO_NEGOTIATES(506, Series.SERVER_ERROR, "Variant Also Negotiates"),
    INSUFFICIENT_STORAGE(507, Series.SERVER_ERROR, "Insufficient Storage"),
    LOOP_DETECTED(508, Series.SERVER_ERROR, "Loop Detected"),
    BANDWIDTH_LIMIT_EXCEEDED(509, Series.SERVER_ERROR, "Bandwidth Limit Exceeded"),
    NOT_EXTENDED(510, Series.SERVER_ERROR, "Not Extended"),
    NETWORK_AUTHENTICATION_REQUIRED(511, Series.SERVER_ERROR, "Network Authentication Required");

    private static final HttpStatus[] VALUES = values();
    private final int value;
    private final Series series;
    private final String reasonPhrase;

    HttpStatus(int value, Series series, String reasonPhrase) {
        this.value = value;
        this.series = series;
        this.reasonPhrase = reasonPhrase;
    }

    @Override // org.springframework.http.HttpStatusCode
    public int value() {
        return this.value;
    }

    public Series series() {
        return this.series;
    }

    public String getReasonPhrase() {
        return this.reasonPhrase;
    }

    @Override // org.springframework.http.HttpStatusCode
    public boolean is1xxInformational() {
        return series() == Series.INFORMATIONAL;
    }

    @Override // org.springframework.http.HttpStatusCode
    public boolean is2xxSuccessful() {
        return series() == Series.SUCCESSFUL;
    }

    @Override // org.springframework.http.HttpStatusCode
    public boolean is3xxRedirection() {
        return series() == Series.REDIRECTION;
    }

    @Override // org.springframework.http.HttpStatusCode
    public boolean is4xxClientError() {
        return series() == Series.CLIENT_ERROR;
    }

    @Override // org.springframework.http.HttpStatusCode
    public boolean is5xxServerError() {
        return series() == Series.SERVER_ERROR;
    }

    @Override // org.springframework.http.HttpStatusCode
    public boolean isError() {
        return is4xxClientError() || is5xxServerError();
    }

    @Override // java.lang.Enum
    public String toString() {
        return this.value + " " + name();
    }

    public static HttpStatus valueOf(int statusCode) {
        HttpStatus status = resolve(statusCode);
        if (status == null) {
            throw new IllegalArgumentException("No matching constant for [" + statusCode + "]");
        }
        return status;
    }

    @Nullable
    public static HttpStatus resolve(int statusCode) {
        for (HttpStatus status : VALUES) {
            if (status.value == statusCode) {
                return status;
            }
        }
        return null;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/HttpStatus$Series.class */
    public enum Series {
        INFORMATIONAL(1),
        SUCCESSFUL(2),
        REDIRECTION(3),
        CLIENT_ERROR(4),
        SERVER_ERROR(5);

        private final int value;

        Series(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        @Deprecated
        public static Series valueOf(HttpStatus status) {
            return status.series;
        }

        public static Series valueOf(int statusCode) {
            Series series = resolve(statusCode);
            if (series == null) {
                throw new IllegalArgumentException("No matching constant for [" + statusCode + "]");
            }
            return series;
        }

        @Nullable
        public static Series resolve(int statusCode) {
            int seriesCode = statusCode / 100;
            for (Series series : values()) {
                if (series.value == seriesCode) {
                    return series;
                }
            }
            return null;
        }
    }
}

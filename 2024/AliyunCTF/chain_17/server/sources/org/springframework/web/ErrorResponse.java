package org.springframework.web;

import java.net.URI;
import java.util.Locale;
import java.util.function.Consumer;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/ErrorResponse.class */
public interface ErrorResponse {
    HttpStatusCode getStatusCode();

    ProblemDetail getBody();

    default HttpHeaders getHeaders() {
        return HttpHeaders.EMPTY;
    }

    default String getTypeMessageCode() {
        return getDefaultTypeMessageCode(getClass());
    }

    default String getTitleMessageCode() {
        return getDefaultTitleMessageCode(getClass());
    }

    default String getDetailMessageCode() {
        return getDefaultDetailMessageCode(getClass(), null);
    }

    @Nullable
    default Object[] getDetailMessageArguments() {
        return null;
    }

    @Nullable
    default Object[] getDetailMessageArguments(MessageSource messageSource, Locale locale) {
        return getDetailMessageArguments();
    }

    default ProblemDetail updateAndGetBody(@Nullable MessageSource messageSource, Locale locale) {
        if (messageSource != null) {
            String type = messageSource.getMessage(getTypeMessageCode(), null, null, locale);
            if (type != null) {
                getBody().setType(URI.create(type));
            }
            Object[] arguments = getDetailMessageArguments(messageSource, locale);
            String detail = messageSource.getMessage(getDetailMessageCode(), arguments, null, locale);
            if (detail != null) {
                getBody().setDetail(detail);
            }
            String title = messageSource.getMessage(getTitleMessageCode(), null, null, locale);
            if (title != null) {
                getBody().setTitle(title);
            }
        }
        return getBody();
    }

    static String getDefaultTypeMessageCode(Class<?> exceptionType) {
        return "problemDetail.type." + exceptionType.getName();
    }

    static String getDefaultTitleMessageCode(Class<?> exceptionType) {
        return "problemDetail.title." + exceptionType.getName();
    }

    static String getDefaultDetailMessageCode(Class<?> exceptionType, @Nullable String suffix) {
        return "problemDetail." + exceptionType.getName() + (suffix != null ? "." + suffix : "");
    }

    static ErrorResponse create(Throwable ex, HttpStatusCode statusCode, String detail) {
        return builder(ex, statusCode, detail).build();
    }

    static Builder builder(Throwable ex, HttpStatusCode statusCode, String detail) {
        return builder(ex, ProblemDetail.forStatusAndDetail(statusCode, detail));
    }

    static Builder builder(Throwable ex, ProblemDetail problemDetail) {
        return new DefaultErrorResponseBuilder(ex, problemDetail);
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/ErrorResponse$Builder.class */
    public interface Builder {
        Builder header(String headerName, String... headerValues);

        Builder headers(Consumer<HttpHeaders> headersConsumer);

        Builder type(URI type);

        Builder typeMessageCode(String messageCode);

        Builder title(@Nullable String title);

        Builder titleMessageCode(String messageCode);

        Builder instance(@Nullable URI instance);

        Builder detail(String detail);

        Builder detailMessageCode(String messageCode);

        Builder detailMessageArguments(Object... messageArguments);

        Builder property(String name, @Nullable Object value);

        ErrorResponse build();

        default ErrorResponse build(@Nullable MessageSource messageSource, Locale locale) {
            ErrorResponse response = build();
            response.updateAndGetBody(messageSource, locale);
            return response;
        }
    }
}

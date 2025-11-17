package org.springframework.web.client;

import java.nio.charset.Charset;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/client/HttpClientErrorException.class */
public class HttpClientErrorException extends HttpStatusCodeException {
    private static final long serialVersionUID = 5177019431887513952L;

    public HttpClientErrorException(HttpStatusCode statusCode) {
        super(statusCode);
    }

    public HttpClientErrorException(HttpStatusCode statusCode, String statusText) {
        super(statusCode, statusText);
    }

    public HttpClientErrorException(HttpStatusCode statusCode, String statusText, @Nullable byte[] body, @Nullable Charset responseCharset) {
        super(statusCode, statusText, body, responseCharset);
    }

    public HttpClientErrorException(HttpStatusCode statusCode, String statusText, @Nullable HttpHeaders headers, @Nullable byte[] body, @Nullable Charset responseCharset) {
        super(statusCode, statusText, headers, body, responseCharset);
    }

    public HttpClientErrorException(String message, HttpStatusCode statusCode, String statusText, @Nullable HttpHeaders headers, @Nullable byte[] body, @Nullable Charset responseCharset) {
        super(message, statusCode, statusText, headers, body, responseCharset);
    }

    public static HttpClientErrorException create(HttpStatusCode statusCode, String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
        return create(null, statusCode, statusText, headers, body, charset);
    }

    public static HttpClientErrorException create(@Nullable String message, HttpStatusCode statusCode, String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
        if (statusCode instanceof HttpStatus) {
            HttpStatus status = (HttpStatus) statusCode;
            switch (status) {
                case BAD_REQUEST:
                    if (message != null) {
                        return new BadRequest(message, statusText, headers, body, charset);
                    }
                    return new BadRequest(statusText, headers, body, charset);
                case UNAUTHORIZED:
                    if (message != null) {
                        return new Unauthorized(message, statusText, headers, body, charset);
                    }
                    return new Unauthorized(statusText, headers, body, charset);
                case FORBIDDEN:
                    if (message != null) {
                        return new Forbidden(message, statusText, headers, body, charset);
                    }
                    return new Forbidden(statusText, headers, body, charset);
                case NOT_FOUND:
                    if (message != null) {
                        return new NotFound(message, statusText, headers, body, charset);
                    }
                    return new NotFound(statusText, headers, body, charset);
                case METHOD_NOT_ALLOWED:
                    if (message != null) {
                        return new MethodNotAllowed(message, statusText, headers, body, charset);
                    }
                    return new MethodNotAllowed(statusText, headers, body, charset);
                case NOT_ACCEPTABLE:
                    if (message != null) {
                        return new NotAcceptable(message, statusText, headers, body, charset);
                    }
                    return new NotAcceptable(statusText, headers, body, charset);
                case CONFLICT:
                    if (message != null) {
                        return new Conflict(message, statusText, headers, body, charset);
                    }
                    return new Conflict(statusText, headers, body, charset);
                case GONE:
                    if (message != null) {
                        return new Gone(message, statusText, headers, body, charset);
                    }
                    return new Gone(statusText, headers, body, charset);
                case UNSUPPORTED_MEDIA_TYPE:
                    if (message != null) {
                        return new UnsupportedMediaType(message, statusText, headers, body, charset);
                    }
                    return new UnsupportedMediaType(statusText, headers, body, charset);
                case TOO_MANY_REQUESTS:
                    if (message != null) {
                        return new TooManyRequests(message, statusText, headers, body, charset);
                    }
                    return new TooManyRequests(statusText, headers, body, charset);
                case UNPROCESSABLE_ENTITY:
                    if (message != null) {
                        return new UnprocessableEntity(message, statusText, headers, body, charset);
                    }
                    return new UnprocessableEntity(statusText, headers, body, charset);
                default:
                    if (message != null) {
                        return new HttpClientErrorException(message, statusCode, statusText, headers, body, charset);
                    }
                    return new HttpClientErrorException(statusCode, statusText, headers, body, charset);
            }
        }
        if (message != null) {
            return new HttpClientErrorException(message, statusCode, statusText, headers, body, charset);
        }
        return new HttpClientErrorException(statusCode, statusText, headers, body, charset);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/client/HttpClientErrorException$BadRequest.class */
    public static final class BadRequest extends HttpClientErrorException {
        private BadRequest(String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
            super(HttpStatus.BAD_REQUEST, statusText, headers, body, charset);
        }

        private BadRequest(String message, String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
            super(message, HttpStatus.BAD_REQUEST, statusText, headers, body, charset);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/client/HttpClientErrorException$Unauthorized.class */
    public static final class Unauthorized extends HttpClientErrorException {
        private Unauthorized(String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
            super(HttpStatus.UNAUTHORIZED, statusText, headers, body, charset);
        }

        private Unauthorized(String message, String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
            super(message, HttpStatus.UNAUTHORIZED, statusText, headers, body, charset);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/client/HttpClientErrorException$Forbidden.class */
    public static final class Forbidden extends HttpClientErrorException {
        private Forbidden(String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
            super(HttpStatus.FORBIDDEN, statusText, headers, body, charset);
        }

        private Forbidden(String message, String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
            super(message, HttpStatus.FORBIDDEN, statusText, headers, body, charset);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/client/HttpClientErrorException$NotFound.class */
    public static final class NotFound extends HttpClientErrorException {
        private NotFound(String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
            super(HttpStatus.NOT_FOUND, statusText, headers, body, charset);
        }

        private NotFound(String message, String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
            super(message, HttpStatus.NOT_FOUND, statusText, headers, body, charset);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/client/HttpClientErrorException$MethodNotAllowed.class */
    public static final class MethodNotAllowed extends HttpClientErrorException {
        private MethodNotAllowed(String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
            super(HttpStatus.METHOD_NOT_ALLOWED, statusText, headers, body, charset);
        }

        private MethodNotAllowed(String message, String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
            super(message, HttpStatus.METHOD_NOT_ALLOWED, statusText, headers, body, charset);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/client/HttpClientErrorException$NotAcceptable.class */
    public static final class NotAcceptable extends HttpClientErrorException {
        private NotAcceptable(String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
            super(HttpStatus.NOT_ACCEPTABLE, statusText, headers, body, charset);
        }

        private NotAcceptable(String message, String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
            super(message, HttpStatus.NOT_ACCEPTABLE, statusText, headers, body, charset);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/client/HttpClientErrorException$Conflict.class */
    public static final class Conflict extends HttpClientErrorException {
        private Conflict(String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
            super(HttpStatus.CONFLICT, statusText, headers, body, charset);
        }

        private Conflict(String message, String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
            super(message, HttpStatus.CONFLICT, statusText, headers, body, charset);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/client/HttpClientErrorException$Gone.class */
    public static final class Gone extends HttpClientErrorException {
        private Gone(String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
            super(HttpStatus.GONE, statusText, headers, body, charset);
        }

        private Gone(String message, String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
            super(message, HttpStatus.GONE, statusText, headers, body, charset);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/client/HttpClientErrorException$UnsupportedMediaType.class */
    public static final class UnsupportedMediaType extends HttpClientErrorException {
        private UnsupportedMediaType(String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
            super(HttpStatus.UNSUPPORTED_MEDIA_TYPE, statusText, headers, body, charset);
        }

        private UnsupportedMediaType(String message, String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
            super(message, HttpStatus.UNSUPPORTED_MEDIA_TYPE, statusText, headers, body, charset);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/client/HttpClientErrorException$UnprocessableEntity.class */
    public static final class UnprocessableEntity extends HttpClientErrorException {
        private UnprocessableEntity(String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
            super(HttpStatus.UNPROCESSABLE_ENTITY, statusText, headers, body, charset);
        }

        private UnprocessableEntity(String message, String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
            super(message, HttpStatus.UNPROCESSABLE_ENTITY, statusText, headers, body, charset);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/client/HttpClientErrorException$TooManyRequests.class */
    public static final class TooManyRequests extends HttpClientErrorException {
        private TooManyRequests(String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
            super(HttpStatus.TOO_MANY_REQUESTS, statusText, headers, body, charset);
        }

        private TooManyRequests(String message, String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
            super(message, HttpStatus.TOO_MANY_REQUESTS, statusText, headers, body, charset);
        }
    }
}

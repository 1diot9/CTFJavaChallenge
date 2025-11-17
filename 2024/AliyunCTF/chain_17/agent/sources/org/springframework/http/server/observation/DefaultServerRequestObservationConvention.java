package org.springframework.http.server.observation;

import io.micrometer.common.KeyValue;
import io.micrometer.common.KeyValues;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.observation.ServerHttpObservationDocumentation;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/observation/DefaultServerRequestObservationConvention.class */
public class DefaultServerRequestObservationConvention implements ServerRequestObservationConvention {
    private static final String DEFAULT_NAME = "http.server.requests";
    private static final KeyValue METHOD_UNKNOWN = KeyValue.of(ServerHttpObservationDocumentation.LowCardinalityKeyNames.METHOD, "UNKNOWN");
    private static final KeyValue STATUS_UNKNOWN = KeyValue.of(ServerHttpObservationDocumentation.LowCardinalityKeyNames.STATUS, "UNKNOWN");
    private static final KeyValue HTTP_OUTCOME_SUCCESS = KeyValue.of(ServerHttpObservationDocumentation.LowCardinalityKeyNames.OUTCOME, "SUCCESS");
    private static final KeyValue HTTP_OUTCOME_UNKNOWN = KeyValue.of(ServerHttpObservationDocumentation.LowCardinalityKeyNames.OUTCOME, "UNKNOWN");
    private static final KeyValue URI_UNKNOWN = KeyValue.of(ServerHttpObservationDocumentation.LowCardinalityKeyNames.URI, "UNKNOWN");
    private static final KeyValue URI_ROOT = KeyValue.of(ServerHttpObservationDocumentation.LowCardinalityKeyNames.URI, "root");
    private static final KeyValue URI_NOT_FOUND = KeyValue.of(ServerHttpObservationDocumentation.LowCardinalityKeyNames.URI, "NOT_FOUND");
    private static final KeyValue URI_REDIRECTION = KeyValue.of(ServerHttpObservationDocumentation.LowCardinalityKeyNames.URI, "REDIRECTION");
    private static final KeyValue EXCEPTION_NONE = KeyValue.of(ServerHttpObservationDocumentation.LowCardinalityKeyNames.EXCEPTION, "none");
    private static final KeyValue HTTP_URL_UNKNOWN = KeyValue.of(ServerHttpObservationDocumentation.HighCardinalityKeyNames.HTTP_URL, "UNKNOWN");
    private static final Set<String> HTTP_METHODS = (Set) Stream.of((Object[]) HttpMethod.values()).map((v0) -> {
        return v0.name();
    }).collect(Collectors.toUnmodifiableSet());
    private final String name;

    public DefaultServerRequestObservationConvention() {
        this(DEFAULT_NAME);
    }

    public DefaultServerRequestObservationConvention(String name) {
        this.name = name;
    }

    @Override // io.micrometer.observation.ObservationConvention
    public String getName() {
        return this.name;
    }

    @Override // io.micrometer.observation.ObservationConvention
    public String getContextualName(ServerRequestObservationContext context) {
        String httpMethod = context.getCarrier().getMethod().toLowerCase();
        if (context.getPathPattern() != null) {
            return "http " + httpMethod + " " + context.getPathPattern();
        }
        return "http " + httpMethod;
    }

    @Override // io.micrometer.observation.ObservationConvention
    public KeyValues getLowCardinalityKeyValues(ServerRequestObservationContext context) {
        return KeyValues.of(exception(context), method(context), outcome(context), status(context), uri(context));
    }

    @Override // io.micrometer.observation.ObservationConvention
    public KeyValues getHighCardinalityKeyValues(ServerRequestObservationContext context) {
        return KeyValues.of(httpUrl(context));
    }

    protected KeyValue method(ServerRequestObservationContext context) {
        if (context.getCarrier() != null) {
            String httpMethod = context.getCarrier().getMethod();
            if (HTTP_METHODS.contains(httpMethod)) {
                return KeyValue.of(ServerHttpObservationDocumentation.LowCardinalityKeyNames.METHOD, httpMethod);
            }
        }
        return METHOD_UNKNOWN;
    }

    protected KeyValue status(ServerRequestObservationContext context) {
        if (context.getResponse() != null) {
            return KeyValue.of(ServerHttpObservationDocumentation.LowCardinalityKeyNames.STATUS, Integer.toString(context.getResponse().getStatus()));
        }
        return STATUS_UNKNOWN;
    }

    protected KeyValue uri(ServerRequestObservationContext context) {
        HttpStatus status;
        if (context.getCarrier() != null) {
            String pattern = context.getPathPattern();
            if (pattern != null) {
                if (pattern.isEmpty()) {
                    return URI_ROOT;
                }
                return KeyValue.of(ServerHttpObservationDocumentation.LowCardinalityKeyNames.URI, pattern);
            }
            if (context.getResponse() != null && (status = HttpStatus.resolve(context.getResponse().getStatus())) != null) {
                if (status.is3xxRedirection()) {
                    return URI_REDIRECTION;
                }
                if (status == HttpStatus.NOT_FOUND) {
                    return URI_NOT_FOUND;
                }
            }
        }
        return URI_UNKNOWN;
    }

    protected KeyValue exception(ServerRequestObservationContext context) {
        Throwable error = context.getError();
        if (error != null) {
            String simpleName = error.getClass().getSimpleName();
            return KeyValue.of(ServerHttpObservationDocumentation.LowCardinalityKeyNames.EXCEPTION, StringUtils.hasText(simpleName) ? simpleName : error.getClass().getName());
        }
        return EXCEPTION_NONE;
    }

    protected KeyValue outcome(ServerRequestObservationContext context) {
        if (context.getResponse() != null) {
            HttpStatusCode statusCode = HttpStatusCode.valueOf(context.getResponse().getStatus());
            return HttpOutcome.forStatus(statusCode);
        }
        return HTTP_OUTCOME_UNKNOWN;
    }

    protected KeyValue httpUrl(ServerRequestObservationContext context) {
        if (context.getCarrier() != null) {
            return KeyValue.of(ServerHttpObservationDocumentation.HighCardinalityKeyNames.HTTP_URL, context.getCarrier().getRequestURI());
        }
        return HTTP_URL_UNKNOWN;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/observation/DefaultServerRequestObservationConvention$HttpOutcome.class */
    public static class HttpOutcome {
        HttpOutcome() {
        }

        static KeyValue forStatus(HttpStatusCode statusCode) {
            if (statusCode.is2xxSuccessful()) {
                return DefaultServerRequestObservationConvention.HTTP_OUTCOME_SUCCESS;
            }
            if (statusCode instanceof HttpStatus) {
                HttpStatus status = (HttpStatus) statusCode;
                return KeyValue.of(ServerHttpObservationDocumentation.LowCardinalityKeyNames.OUTCOME, status.series().name());
            }
            return DefaultServerRequestObservationConvention.HTTP_OUTCOME_UNKNOWN;
        }
    }
}

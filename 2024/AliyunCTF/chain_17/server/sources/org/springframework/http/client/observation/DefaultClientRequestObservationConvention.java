package org.springframework.http.client.observation;

import io.micrometer.common.KeyValue;
import io.micrometer.common.KeyValues;
import java.io.IOException;
import java.util.regex.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.observation.ClientHttpObservationDocumentation;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/observation/DefaultClientRequestObservationConvention.class */
public class DefaultClientRequestObservationConvention implements ClientRequestObservationConvention {
    private static final String DEFAULT_NAME = "http.client.requests";
    private static final Pattern PATTERN_BEFORE_PATH = Pattern.compile("^https?://[^/]+/");
    private static final KeyValue URI_NONE = KeyValue.of(ClientHttpObservationDocumentation.LowCardinalityKeyNames.URI, "none");
    private static final KeyValue METHOD_NONE = KeyValue.of(ClientHttpObservationDocumentation.LowCardinalityKeyNames.METHOD, "none");
    private static final KeyValue STATUS_IO_ERROR = KeyValue.of(ClientHttpObservationDocumentation.LowCardinalityKeyNames.STATUS, "IO_ERROR");
    private static final KeyValue STATUS_CLIENT_ERROR = KeyValue.of(ClientHttpObservationDocumentation.LowCardinalityKeyNames.STATUS, "CLIENT_ERROR");
    private static final KeyValue HTTP_OUTCOME_SUCCESS = KeyValue.of(ClientHttpObservationDocumentation.LowCardinalityKeyNames.OUTCOME, "SUCCESS");
    private static final KeyValue HTTP_OUTCOME_UNKNOWN = KeyValue.of(ClientHttpObservationDocumentation.LowCardinalityKeyNames.OUTCOME, "UNKNOWN");
    private static final KeyValue CLIENT_NAME_NONE = KeyValue.of(ClientHttpObservationDocumentation.LowCardinalityKeyNames.CLIENT_NAME, "none");
    private static final KeyValue EXCEPTION_NONE = KeyValue.of(ClientHttpObservationDocumentation.LowCardinalityKeyNames.EXCEPTION, "none");
    private static final KeyValue HTTP_URL_NONE = KeyValue.of(ClientHttpObservationDocumentation.HighCardinalityKeyNames.HTTP_URL, "none");
    private final String name;

    public DefaultClientRequestObservationConvention() {
        this(DEFAULT_NAME);
    }

    public DefaultClientRequestObservationConvention(String name) {
        this.name = name;
    }

    @Override // io.micrometer.observation.ObservationConvention
    public String getName() {
        return this.name;
    }

    @Override // io.micrometer.observation.ObservationConvention
    public String getContextualName(ClientRequestObservationContext context) {
        return "http " + context.getCarrier().getMethod().name().toLowerCase();
    }

    @Override // io.micrometer.observation.ObservationConvention
    public KeyValues getLowCardinalityKeyValues(ClientRequestObservationContext context) {
        return KeyValues.of(clientName(context), exception(context), method(context), outcome(context), status(context), uri(context));
    }

    protected KeyValue uri(ClientRequestObservationContext context) {
        if (context.getUriTemplate() != null) {
            return KeyValue.of(ClientHttpObservationDocumentation.LowCardinalityKeyNames.URI, extractPath(context.getUriTemplate()));
        }
        return URI_NONE;
    }

    private static String extractPath(String uriTemplate) {
        String path = PATTERN_BEFORE_PATH.matcher(uriTemplate).replaceFirst("");
        return path.startsWith("/") ? path : "/" + path;
    }

    protected KeyValue method(ClientRequestObservationContext context) {
        if (context.getCarrier() != null) {
            return KeyValue.of(ClientHttpObservationDocumentation.LowCardinalityKeyNames.METHOD, context.getCarrier().getMethod().name());
        }
        return METHOD_NONE;
    }

    protected KeyValue status(ClientRequestObservationContext context) {
        ClientHttpResponse response = context.getResponse();
        if (response == null) {
            return STATUS_CLIENT_ERROR;
        }
        try {
            return KeyValue.of(ClientHttpObservationDocumentation.LowCardinalityKeyNames.STATUS, String.valueOf(response.getStatusCode().value()));
        } catch (IOException e) {
            return STATUS_IO_ERROR;
        }
    }

    protected KeyValue clientName(ClientRequestObservationContext context) {
        if (context.getCarrier() != null && context.getCarrier().getURI().getHost() != null) {
            return KeyValue.of(ClientHttpObservationDocumentation.LowCardinalityKeyNames.CLIENT_NAME, context.getCarrier().getURI().getHost());
        }
        return CLIENT_NAME_NONE;
    }

    protected KeyValue exception(ClientRequestObservationContext context) {
        Throwable error = context.getError();
        if (error != null) {
            String simpleName = error.getClass().getSimpleName();
            return KeyValue.of(ClientHttpObservationDocumentation.LowCardinalityKeyNames.EXCEPTION, StringUtils.hasText(simpleName) ? simpleName : error.getClass().getName());
        }
        return EXCEPTION_NONE;
    }

    protected KeyValue outcome(ClientRequestObservationContext context) {
        if (context.getResponse() != null) {
            try {
                return HttpOutcome.forStatus(context.getResponse().getStatusCode());
            } catch (IOException e) {
            }
        }
        return HTTP_OUTCOME_UNKNOWN;
    }

    @Override // io.micrometer.observation.ObservationConvention
    public KeyValues getHighCardinalityKeyValues(ClientRequestObservationContext context) {
        return KeyValues.of(requestUri(context));
    }

    protected KeyValue requestUri(ClientRequestObservationContext context) {
        if (context.getCarrier() != null) {
            return KeyValue.of(ClientHttpObservationDocumentation.HighCardinalityKeyNames.HTTP_URL, context.getCarrier().getURI().toASCIIString());
        }
        return HTTP_URL_NONE;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/observation/DefaultClientRequestObservationConvention$HttpOutcome.class */
    public static class HttpOutcome {
        HttpOutcome() {
        }

        static KeyValue forStatus(HttpStatusCode statusCode) {
            if (statusCode.is2xxSuccessful()) {
                return DefaultClientRequestObservationConvention.HTTP_OUTCOME_SUCCESS;
            }
            if (statusCode instanceof HttpStatus) {
                HttpStatus status = (HttpStatus) statusCode;
                return KeyValue.of(ClientHttpObservationDocumentation.LowCardinalityKeyNames.OUTCOME, status.series().name());
            }
            return DefaultClientRequestObservationConvention.HTTP_OUTCOME_UNKNOWN;
        }
    }
}

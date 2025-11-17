package org.springframework.web.client;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.function.Function;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.util.MultiValueMap;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/client/RestClientResponseException.class */
public class RestClientResponseException extends RestClientException {
    private static final long serialVersionUID = -8803556342728481792L;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final HttpStatusCode statusCode;
    private final String statusText;
    private final byte[] responseBody;

    @Nullable
    private final HttpHeaders responseHeaders;

    @Nullable
    private final String responseCharset;

    @Nullable
    private transient Function<ResolvableType, ?> bodyConvertFunction;

    public RestClientResponseException(String message, int statusCode, String statusText, @Nullable HttpHeaders headers, @Nullable byte[] responseBody, @Nullable Charset responseCharset) {
        this(message, HttpStatusCode.valueOf(statusCode), statusText, headers, responseBody, responseCharset);
    }

    public RestClientResponseException(String message, HttpStatusCode statusCode, String statusText, @Nullable HttpHeaders headers, @Nullable byte[] responseBody, @Nullable Charset responseCharset) {
        super(message);
        this.statusCode = statusCode;
        this.statusText = statusText;
        this.responseHeaders = copyHeaders(headers);
        this.responseBody = responseBody != null ? responseBody : new byte[0];
        this.responseCharset = responseCharset != null ? responseCharset.name() : null;
    }

    @Nullable
    private static HttpHeaders copyHeaders(@Nullable HttpHeaders headers) {
        if (headers != null) {
            MultiValueMap<String, String> result = CollectionUtils.toMultiValueMap(new LinkedCaseInsensitiveMap(headers.size(), Locale.ENGLISH));
            headers.forEach((name, values) -> {
                values.forEach(value -> {
                    result.add(name, value);
                });
            });
            return HttpHeaders.readOnlyHttpHeaders(result);
        }
        return null;
    }

    public HttpStatusCode getStatusCode() {
        return this.statusCode;
    }

    @Deprecated(since = "6.0")
    public int getRawStatusCode() {
        return this.statusCode.value();
    }

    public String getStatusText() {
        return this.statusText;
    }

    @Nullable
    public HttpHeaders getResponseHeaders() {
        return this.responseHeaders;
    }

    public byte[] getResponseBodyAsByteArray() {
        return this.responseBody;
    }

    public String getResponseBodyAsString() {
        return getResponseBodyAsString(DEFAULT_CHARSET);
    }

    public String getResponseBodyAsString(Charset fallbackCharset) {
        if (this.responseCharset == null) {
            return new String(this.responseBody, fallbackCharset);
        }
        try {
            return new String(this.responseBody, this.responseCharset);
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Nullable
    public <E> E getResponseBodyAs(Class<E> cls) {
        return (E) getResponseBodyAs(ResolvableType.forClass(cls));
    }

    @Nullable
    public <E> E getResponseBodyAs(ParameterizedTypeReference<E> parameterizedTypeReference) {
        return (E) getResponseBodyAs(ResolvableType.forType(parameterizedTypeReference.getType()));
    }

    @Nullable
    private <E> E getResponseBodyAs(ResolvableType resolvableType) {
        Assert.state(this.bodyConvertFunction != null, "Function to convert body not set");
        return (E) this.bodyConvertFunction.apply(resolvableType);
    }

    public void setBodyConvertFunction(Function<ResolvableType, ?> bodyConvertFunction) {
        this.bodyConvertFunction = bodyConvertFunction;
    }
}

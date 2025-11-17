package org.springframework.web.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import org.springframework.core.ResolvableType;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestClient;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/client/StatusHandler.class */
public final class StatusHandler {
    private final ResponsePredicate predicate;
    private final RestClient.ResponseSpec.ErrorHandler errorHandler;

    /* JADX INFO: Access modifiers changed from: private */
    @FunctionalInterface
    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/client/StatusHandler$ResponsePredicate.class */
    public interface ResponsePredicate {
        boolean test(ClientHttpResponse response) throws IOException;
    }

    private StatusHandler(ResponsePredicate predicate, RestClient.ResponseSpec.ErrorHandler errorHandler) {
        this.predicate = predicate;
        this.errorHandler = errorHandler;
    }

    public static StatusHandler of(Predicate<HttpStatusCode> predicate, RestClient.ResponseSpec.ErrorHandler errorHandler) {
        Assert.notNull(predicate, "Predicate must not be null");
        Assert.notNull(errorHandler, "ErrorHandler must not be null");
        return new StatusHandler(response -> {
            return predicate.test(response.getStatusCode());
        }, errorHandler);
    }

    public static StatusHandler fromErrorHandler(ResponseErrorHandler errorHandler) {
        Assert.notNull(errorHandler, "ResponseErrorHandler must not be null");
        Objects.requireNonNull(errorHandler);
        return new StatusHandler(errorHandler::hasError, (request, response) -> {
            errorHandler.handleError(request.getURI(), request.getMethod(), response);
        });
    }

    public static StatusHandler defaultHandler(List<HttpMessageConverter<?>> messageConverters) {
        return new StatusHandler(response -> {
            return response.getStatusCode().isError();
        }, (request, response2) -> {
            RestClientResponseException ex;
            HttpStatusCode statusCode = response2.getStatusCode();
            String statusText = response2.getStatusText();
            HttpHeaders headers = response2.getHeaders();
            byte[] body = RestClientUtils.getBody(response2);
            Charset charset = RestClientUtils.getCharset(response2);
            String message = getErrorMessage(statusCode.value(), statusText, body, charset);
            if (statusCode.is4xxClientError()) {
                ex = HttpClientErrorException.create(message, statusCode, statusText, headers, body, charset);
            } else if (statusCode.is5xxServerError()) {
                ex = HttpServerErrorException.create(message, statusCode, statusText, headers, body, charset);
            } else {
                ex = new UnknownHttpStatusCodeException(message, statusCode.value(), statusText, headers, body, charset);
            }
            if (!CollectionUtils.isEmpty(messageConverters)) {
                ex.setBodyConvertFunction(initBodyConvertFunction(response2, body, messageConverters));
            }
            throw ex;
        });
    }

    private static Function<ResolvableType, ?> initBodyConvertFunction(ClientHttpResponse response, byte[] body, List<HttpMessageConverter<?>> messageConverters) {
        Assert.state(!CollectionUtils.isEmpty(messageConverters), "Expected message converters");
        return resolvableType -> {
            try {
                HttpMessageConverterExtractor<?> extractor = new HttpMessageConverterExtractor<>(resolvableType.getType(), (List<HttpMessageConverter<?>>) messageConverters);
                return extractor.extractData(new ClientHttpResponseDecorator(response) { // from class: org.springframework.web.client.StatusHandler.1
                    @Override // org.springframework.web.client.ClientHttpResponseDecorator, org.springframework.http.HttpInputMessage
                    public InputStream getBody() {
                        return new ByteArrayInputStream(body);
                    }
                });
            } catch (IOException ex) {
                throw new RestClientException("Error while extracting response for type [" + resolvableType + "]", ex);
            }
        };
    }

    private static String getErrorMessage(int rawStatusCode, String statusText, @Nullable byte[] responseBody, @Nullable Charset charset) {
        String preface = rawStatusCode + " " + statusText + ": ";
        if (ObjectUtils.isEmpty(responseBody)) {
            return preface + "[no body]";
        }
        String bodyText = new String(responseBody, charset != null ? charset : StandardCharsets.UTF_8);
        return preface + LogFormatUtils.formatValue(bodyText, -1, true);
    }

    public boolean test(ClientHttpResponse response) throws IOException {
        return this.predicate.test(response);
    }

    public void handle(HttpRequest request, ClientHttpResponse response) throws IOException {
        this.errorHandler.handle(request, response);
    }
}

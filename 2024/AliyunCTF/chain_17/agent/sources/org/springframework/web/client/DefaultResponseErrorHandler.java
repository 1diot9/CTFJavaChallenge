package org.springframework.web.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import org.springframework.core.ResolvableType;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ObjectUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/client/DefaultResponseErrorHandler.class */
public class DefaultResponseErrorHandler implements ResponseErrorHandler {

    @Nullable
    private List<HttpMessageConverter<?>> messageConverters;

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setMessageConverters(List<HttpMessageConverter<?>> converters) {
        this.messageConverters = Collections.unmodifiableList(converters);
    }

    @Override // org.springframework.web.client.ResponseErrorHandler
    public boolean hasError(ClientHttpResponse response) throws IOException {
        HttpStatusCode statusCode = response.getStatusCode();
        return hasError(statusCode);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean hasError(HttpStatusCode statusCode) {
        return statusCode.isError();
    }

    @Deprecated
    protected boolean hasError(int statusCode) {
        HttpStatus.Series series = HttpStatus.Series.resolve(statusCode);
        return series == HttpStatus.Series.CLIENT_ERROR || series == HttpStatus.Series.SERVER_ERROR;
    }

    @Override // org.springframework.web.client.ResponseErrorHandler
    public void handleError(ClientHttpResponse response) throws IOException {
        HttpStatusCode statusCode = response.getStatusCode();
        handleError(response, statusCode);
    }

    private String getErrorMessage(int rawStatusCode, String statusText, @Nullable byte[] responseBody, @Nullable Charset charset) {
        String preface = rawStatusCode + " " + statusText + ": ";
        if (ObjectUtils.isEmpty(responseBody)) {
            return preface + "[no body]";
        }
        String bodyText = new String(responseBody, charset != null ? charset : StandardCharsets.UTF_8);
        return preface + LogFormatUtils.formatValue(bodyText, -1, true);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void handleError(ClientHttpResponse response, HttpStatusCode statusCode) throws IOException {
        RestClientResponseException ex;
        String statusText = response.getStatusText();
        HttpHeaders headers = response.getHeaders();
        byte[] body = getResponseBody(response);
        Charset charset = getCharset(response);
        String message = getErrorMessage(statusCode.value(), statusText, body, charset);
        if (statusCode.is4xxClientError()) {
            ex = HttpClientErrorException.create(message, statusCode, statusText, headers, body, charset);
        } else if (statusCode.is5xxServerError()) {
            ex = HttpServerErrorException.create(message, statusCode, statusText, headers, body, charset);
        } else {
            ex = new UnknownHttpStatusCodeException(message, statusCode.value(), statusText, headers, body, charset);
        }
        if (!CollectionUtils.isEmpty(this.messageConverters)) {
            ex.setBodyConvertFunction(initBodyConvertFunction(response, body));
        }
        throw ex;
    }

    protected Function<ResolvableType, ?> initBodyConvertFunction(ClientHttpResponse response, byte[] body) {
        Assert.state(!CollectionUtils.isEmpty(this.messageConverters), "Expected message converters");
        return resolvableType -> {
            try {
                HttpMessageConverterExtractor<?> extractor = new HttpMessageConverterExtractor<>(resolvableType.getType(), this.messageConverters);
                return extractor.extractData(new ClientHttpResponseDecorator(response) { // from class: org.springframework.web.client.DefaultResponseErrorHandler.1
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

    protected byte[] getResponseBody(ClientHttpResponse response) {
        try {
            return FileCopyUtils.copyToByteArray(response.getBody());
        } catch (IOException e) {
            return new byte[0];
        }
    }

    @Nullable
    protected Charset getCharset(ClientHttpResponse response) {
        HttpHeaders headers = response.getHeaders();
        MediaType contentType = headers.getContentType();
        if (contentType != null) {
            return contentType.getCharset();
        }
        return null;
    }
}

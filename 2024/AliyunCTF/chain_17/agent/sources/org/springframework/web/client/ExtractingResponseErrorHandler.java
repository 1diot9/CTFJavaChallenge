package org.springframework.web.client;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/client/ExtractingResponseErrorHandler.class */
public class ExtractingResponseErrorHandler extends DefaultResponseErrorHandler {
    private List<HttpMessageConverter<?>> messageConverters;
    private final Map<HttpStatusCode, Class<? extends RestClientException>> statusMapping;
    private final Map<HttpStatus.Series, Class<? extends RestClientException>> seriesMapping;

    public ExtractingResponseErrorHandler() {
        this.messageConverters = Collections.emptyList();
        this.statusMapping = new LinkedHashMap();
        this.seriesMapping = new LinkedHashMap();
    }

    public ExtractingResponseErrorHandler(List<HttpMessageConverter<?>> messageConverters) {
        this.messageConverters = Collections.emptyList();
        this.statusMapping = new LinkedHashMap();
        this.seriesMapping = new LinkedHashMap();
        this.messageConverters = messageConverters;
    }

    @Override // org.springframework.web.client.DefaultResponseErrorHandler
    public void setMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
        this.messageConverters = messageConverters;
    }

    public void setStatusMapping(Map<HttpStatusCode, Class<? extends RestClientException>> statusMapping) {
        if (!CollectionUtils.isEmpty(statusMapping)) {
            this.statusMapping.putAll(statusMapping);
        }
    }

    public void setSeriesMapping(Map<HttpStatus.Series, Class<? extends RestClientException>> seriesMapping) {
        if (!CollectionUtils.isEmpty(seriesMapping)) {
            this.seriesMapping.putAll(seriesMapping);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.client.DefaultResponseErrorHandler
    public boolean hasError(HttpStatusCode statusCode) {
        if (this.statusMapping.containsKey(statusCode)) {
            return this.statusMapping.get(statusCode) != null;
        }
        HttpStatus.Series series = HttpStatus.Series.resolve(statusCode.value());
        if (this.seriesMapping.containsKey(series)) {
            return this.seriesMapping.get(series) != null;
        }
        return super.hasError(statusCode);
    }

    @Override // org.springframework.web.client.DefaultResponseErrorHandler
    public void handleError(ClientHttpResponse response, HttpStatusCode statusCode) throws IOException {
        if (this.statusMapping.containsKey(statusCode)) {
            extract(this.statusMapping.get(statusCode), response);
        }
        HttpStatus.Series series = HttpStatus.Series.resolve(statusCode.value());
        if (this.seriesMapping.containsKey(series)) {
            extract(this.seriesMapping.get(series), response);
        } else {
            super.handleError(response, statusCode);
        }
    }

    private void extract(@Nullable Class<? extends RestClientException> exceptionClass, ClientHttpResponse response) throws IOException {
        if (exceptionClass == null) {
            return;
        }
        HttpMessageConverterExtractor<? extends RestClientException> extractor = new HttpMessageConverterExtractor<>(exceptionClass, this.messageConverters);
        RestClientException exception = (RestClientException) extractor.extractData(response);
        if (exception != null) {
            throw exception;
        }
    }
}

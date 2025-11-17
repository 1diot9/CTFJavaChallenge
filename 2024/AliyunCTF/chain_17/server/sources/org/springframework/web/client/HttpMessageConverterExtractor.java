package org.springframework.web.client;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.ResolvableType;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/client/HttpMessageConverterExtractor.class */
public class HttpMessageConverterExtractor<T> implements ResponseExtractor<T> {
    private final Type responseType;

    @Nullable
    private final Class<T> responseClass;
    private final List<HttpMessageConverter<?>> messageConverters;
    private final Log logger;

    public HttpMessageConverterExtractor(Class<T> responseType, List<HttpMessageConverter<?>> messageConverters) {
        this((Type) responseType, messageConverters);
    }

    public HttpMessageConverterExtractor(Type responseType, List<HttpMessageConverter<?>> messageConverters) {
        this(responseType, messageConverters, LogFactory.getLog((Class<?>) HttpMessageConverterExtractor.class));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public HttpMessageConverterExtractor(Type responseType, List<HttpMessageConverter<?>> messageConverters, Log logger) {
        Class cls;
        Assert.notNull(responseType, "'responseType' must not be null");
        Assert.notEmpty(messageConverters, "'messageConverters' must not be empty");
        Assert.noNullElements(messageConverters, "'messageConverters' must not contain null elements");
        this.responseType = responseType;
        if (responseType instanceof Class) {
            Class clazz = (Class) responseType;
            cls = clazz;
        } else {
            cls = null;
        }
        this.responseClass = cls;
        this.messageConverters = messageConverters;
        this.logger = logger;
    }

    @Override // org.springframework.web.client.ResponseExtractor
    public T extractData(ClientHttpResponse clientHttpResponse) throws IOException {
        IntrospectingClientHttpResponse introspectingClientHttpResponse = new IntrospectingClientHttpResponse(clientHttpResponse);
        if (!introspectingClientHttpResponse.hasMessageBody() || introspectingClientHttpResponse.hasEmptyMessageBody()) {
            return null;
        }
        MediaType contentType = getContentType(introspectingClientHttpResponse);
        try {
            for (HttpMessageConverter<?> httpMessageConverter : this.messageConverters) {
                if (httpMessageConverter instanceof GenericHttpMessageConverter) {
                    GenericHttpMessageConverter genericHttpMessageConverter = (GenericHttpMessageConverter) httpMessageConverter;
                    if (genericHttpMessageConverter.canRead(this.responseType, null, contentType)) {
                        if (this.logger.isDebugEnabled()) {
                            this.logger.debug("Reading to [" + ResolvableType.forType(this.responseType) + "]");
                        }
                        return (T) genericHttpMessageConverter.read(this.responseType, null, introspectingClientHttpResponse);
                    }
                }
                if (this.responseClass != null && httpMessageConverter.canRead(this.responseClass, contentType)) {
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug("Reading to [" + this.responseClass.getName() + "] as \"" + contentType + "\"");
                    }
                    return (T) httpMessageConverter.read2(this.responseClass, introspectingClientHttpResponse);
                }
            }
            throw new UnknownContentTypeException(this.responseType, contentType, introspectingClientHttpResponse.getStatusCode(), introspectingClientHttpResponse.getStatusText(), introspectingClientHttpResponse.getHeaders(), getResponseBody(introspectingClientHttpResponse));
        } catch (IOException | HttpMessageNotReadableException e) {
            throw new RestClientException("Error while extracting response for type [" + this.responseType + "] and content type [" + contentType + "]", e);
        }
    }

    protected MediaType getContentType(ClientHttpResponse response) {
        MediaType contentType = response.getHeaders().getContentType();
        if (contentType == null) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("No content-type, using 'application/octet-stream'");
            }
            contentType = MediaType.APPLICATION_OCTET_STREAM;
        }
        return contentType;
    }

    private static byte[] getResponseBody(ClientHttpResponse response) {
        try {
            return FileCopyUtils.copyToByteArray(response.getBody());
        } catch (IOException e) {
            return new byte[0];
        }
    }
}

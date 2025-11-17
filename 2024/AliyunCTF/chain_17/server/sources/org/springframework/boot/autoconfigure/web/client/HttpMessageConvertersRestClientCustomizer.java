package org.springframework.boot.autoconfigure.web.client;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClient;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/client/HttpMessageConvertersRestClientCustomizer.class */
public class HttpMessageConvertersRestClientCustomizer implements RestClientCustomizer {
    private final Iterable<? extends HttpMessageConverter<?>> messageConverters;

    public HttpMessageConvertersRestClientCustomizer(HttpMessageConverter<?>... messageConverters) {
        Assert.notNull(messageConverters, "MessageConverters must not be null");
        this.messageConverters = Arrays.asList(messageConverters);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public HttpMessageConvertersRestClientCustomizer(HttpMessageConverters messageConverters) {
        this.messageConverters = messageConverters;
    }

    @Override // org.springframework.boot.web.client.RestClientCustomizer
    public void customize(RestClient.Builder restClientBuilder) {
        restClientBuilder.messageConverters(this::configureMessageConverters);
    }

    private void configureMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
        if (this.messageConverters != null) {
            messageConverters.clear();
            Iterable<? extends HttpMessageConverter<?>> iterable = this.messageConverters;
            Objects.requireNonNull(messageConverters);
            iterable.forEach((v1) -> {
                r1.add(v1);
            });
        }
    }
}

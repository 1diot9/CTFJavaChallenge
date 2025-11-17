package org.springframework.boot.autoconfigure.web.client;

import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.boot.web.client.RestTemplateRequestCustomizer;
import org.springframework.util.ObjectUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/client/RestTemplateBuilderConfigurer.class */
public final class RestTemplateBuilderConfigurer {
    private HttpMessageConverters httpMessageConverters;
    private List<RestTemplateCustomizer> restTemplateCustomizers;
    private List<RestTemplateRequestCustomizer<?>> restTemplateRequestCustomizers;

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setHttpMessageConverters(HttpMessageConverters httpMessageConverters) {
        this.httpMessageConverters = httpMessageConverters;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setRestTemplateCustomizers(List<RestTemplateCustomizer> restTemplateCustomizers) {
        this.restTemplateCustomizers = restTemplateCustomizers;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setRestTemplateRequestCustomizers(List<RestTemplateRequestCustomizer<?>> restTemplateRequestCustomizers) {
        this.restTemplateRequestCustomizers = restTemplateRequestCustomizers;
    }

    public RestTemplateBuilder configure(RestTemplateBuilder builder) {
        if (this.httpMessageConverters != null) {
            builder = builder.messageConverters(this.httpMessageConverters.getConverters());
        }
        return addCustomizers(addCustomizers(builder, this.restTemplateCustomizers, (v0, v1) -> {
            return v0.customizers(v1);
        }), this.restTemplateRequestCustomizers, (v0, v1) -> {
            return v0.requestCustomizers(v1);
        });
    }

    private <T> RestTemplateBuilder addCustomizers(RestTemplateBuilder builder, List<T> customizers, BiFunction<RestTemplateBuilder, Collection<T>, RestTemplateBuilder> method) {
        if (!ObjectUtils.isEmpty(customizers)) {
            return method.apply(builder, customizers);
        }
        return builder;
    }
}

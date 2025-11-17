package org.springframework.boot.autoconfigure.web.client;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.boot.web.client.RestTemplateRequestCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.client.RestTemplate;

@AutoConfiguration(after = {HttpMessageConvertersAutoConfiguration.class})
@ConditionalOnClass({RestTemplate.class})
@Conditional({NotReactiveWebApplicationCondition.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/client/RestTemplateAutoConfiguration.class */
public class RestTemplateAutoConfiguration {
    @Bean
    @Lazy
    public RestTemplateBuilderConfigurer restTemplateBuilderConfigurer(ObjectProvider<HttpMessageConverters> messageConverters, ObjectProvider<RestTemplateCustomizer> restTemplateCustomizers, ObjectProvider<RestTemplateRequestCustomizer<?>> restTemplateRequestCustomizers) {
        RestTemplateBuilderConfigurer configurer = new RestTemplateBuilderConfigurer();
        configurer.setHttpMessageConverters(messageConverters.getIfUnique());
        configurer.setRestTemplateCustomizers(restTemplateCustomizers.orderedStream().toList());
        configurer.setRestTemplateRequestCustomizers(restTemplateRequestCustomizers.orderedStream().toList());
        return configurer;
    }

    @ConditionalOnMissingBean
    @Bean
    @Lazy
    public RestTemplateBuilder restTemplateBuilder(RestTemplateBuilderConfigurer restTemplateBuilderConfigurer) {
        RestTemplateBuilder builder = new RestTemplateBuilder(new RestTemplateCustomizer[0]);
        return restTemplateBuilderConfigurer.configure(builder);
    }
}

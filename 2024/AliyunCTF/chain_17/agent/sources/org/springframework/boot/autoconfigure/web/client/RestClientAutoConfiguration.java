package org.springframework.boot.autoconfigure.web.client;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.ssl.SslAutoConfiguration;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;
import org.springframework.web.client.RestClient;

@AutoConfiguration(after = {HttpMessageConvertersAutoConfiguration.class, SslAutoConfiguration.class})
@ConditionalOnClass({RestClient.class})
@Conditional({NotReactiveWebApplicationCondition.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/client/RestClientAutoConfiguration.class */
public class RestClientAutoConfiguration {
    @ConditionalOnMissingBean
    @Bean
    @Order(Integer.MAX_VALUE)
    HttpMessageConvertersRestClientCustomizer httpMessageConvertersRestClientCustomizer(ObjectProvider<HttpMessageConverters> messageConverters) {
        return new HttpMessageConvertersRestClientCustomizer(messageConverters.getIfUnique());
    }

    @ConditionalOnMissingBean({RestClientSsl.class})
    @ConditionalOnBean({SslBundles.class})
    @Bean
    AutoConfiguredRestClientSsl restClientSsl(SslBundles sslBundles) {
        return new AutoConfiguredRestClientSsl(sslBundles);
    }

    @ConditionalOnMissingBean
    @Bean
    RestClientBuilderConfigurer restClientBuilderConfigurer(ObjectProvider<RestClientCustomizer> customizerProvider) {
        RestClientBuilderConfigurer configurer = new RestClientBuilderConfigurer();
        configurer.setRestClientCustomizers(customizerProvider.orderedStream().toList());
        return configurer;
    }

    @ConditionalOnMissingBean
    @Scope("prototype")
    @Bean
    RestClient.Builder restClientBuilder(RestClientBuilderConfigurer restClientBuilderConfigurer) {
        RestClient.Builder builder = RestClient.builder().requestFactory(ClientHttpRequestFactories.get(ClientHttpRequestFactorySettings.DEFAULTS));
        return restClientBuilderConfigurer.configure(builder);
    }
}

package org.springframework.boot.autoconfigure.web.reactive.function.client;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.codec.CodecsAutoConfiguration;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.web.codec.CodecCustomizer;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;
import org.springframework.web.reactive.function.client.WebClient;

@AutoConfiguration(after = {CodecsAutoConfiguration.class, ClientHttpConnectorAutoConfiguration.class})
@ConditionalOnClass({WebClient.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/reactive/function/client/WebClientAutoConfiguration.class */
public class WebClientAutoConfiguration {
    @ConditionalOnMissingBean
    @Scope("prototype")
    @Bean
    public WebClient.Builder webClientBuilder(ObjectProvider<WebClientCustomizer> customizerProvider) {
        WebClient.Builder builder = WebClient.builder();
        customizerProvider.orderedStream().forEach(customizer -> {
            customizer.customize(builder);
        });
        return builder;
    }

    @ConditionalOnMissingBean({WebClientSsl.class})
    @ConditionalOnBean({SslBundles.class})
    @Bean
    AutoConfiguredWebClientSsl webClientSsl(ClientHttpConnectorFactory<?> clientHttpConnectorFactory, SslBundles sslBundles) {
        return new AutoConfiguredWebClientSsl(clientHttpConnectorFactory, sslBundles);
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnBean({CodecCustomizer.class})
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/reactive/function/client/WebClientAutoConfiguration$WebClientCodecsConfiguration.class */
    protected static class WebClientCodecsConfiguration {
        protected WebClientCodecsConfiguration() {
        }

        @ConditionalOnMissingBean
        @Bean
        @Order(0)
        public WebClientCodecCustomizer exchangeStrategiesCustomizer(ObjectProvider<CodecCustomizer> codecCustomizers) {
            return new WebClientCodecCustomizer(codecCustomizers.orderedStream().toList());
        }
    }
}

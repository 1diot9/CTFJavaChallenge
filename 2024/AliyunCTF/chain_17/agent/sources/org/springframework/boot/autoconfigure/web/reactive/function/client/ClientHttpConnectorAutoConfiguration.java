package org.springframework.boot.autoconfigure.web.reactive.function.client;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.ssl.SslAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.function.client.ClientHttpConnectorFactoryConfiguration;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

@AutoConfiguration
@ConditionalOnClass({WebClient.class})
@AutoConfigureAfter({SslAutoConfiguration.class})
@Import({ClientHttpConnectorFactoryConfiguration.ReactorNetty.class, ClientHttpConnectorFactoryConfiguration.HttpClient5.class, ClientHttpConnectorFactoryConfiguration.JdkClient.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/reactive/function/client/ClientHttpConnectorAutoConfiguration.class */
public class ClientHttpConnectorAutoConfiguration {
    /* JADX WARN: Type inference failed for: r0v1, types: [org.springframework.http.client.reactive.ClientHttpConnector] */
    @ConditionalOnMissingBean({ClientHttpConnector.class})
    @Bean
    @Lazy
    ClientHttpConnector webClientHttpConnector(ClientHttpConnectorFactory<?> clientHttpConnectorFactory) {
        return clientHttpConnectorFactory.createClientHttpConnector();
    }

    @ConditionalOnBean({ClientHttpConnector.class})
    @Bean
    @Lazy
    @Order(0)
    public WebClientCustomizer webClientHttpConnectorCustomizer(ClientHttpConnector clientHttpConnector) {
        return builder -> {
            builder.clientConnector(clientHttpConnector);
        };
    }
}

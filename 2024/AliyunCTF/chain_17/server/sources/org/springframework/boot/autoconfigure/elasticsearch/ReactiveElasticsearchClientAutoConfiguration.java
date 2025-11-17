package org.springframework.boot.autoconfigure.elasticsearch;

import co.elastic.clients.transport.ElasticsearchTransport;
import org.elasticsearch.client.RestClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchClientConfigurations;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchClient;
import reactor.core.publisher.Mono;

@EnableConfigurationProperties({ElasticsearchProperties.class})
@AutoConfiguration(after = {ElasticsearchClientAutoConfiguration.class})
@ConditionalOnClass({ReactiveElasticsearchClient.class, ElasticsearchTransport.class, Mono.class})
@ConditionalOnBean({RestClient.class})
@Import({ElasticsearchClientConfigurations.JsonpMapperConfiguration.class, ElasticsearchClientConfigurations.ElasticsearchTransportConfiguration.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/elasticsearch/ReactiveElasticsearchClientAutoConfiguration.class */
public class ReactiveElasticsearchClientAutoConfiguration {
    @ConditionalOnMissingBean
    @ConditionalOnBean({ElasticsearchTransport.class})
    @Bean
    ReactiveElasticsearchClient reactiveElasticsearchClient(ElasticsearchTransport transport) {
        return new ReactiveElasticsearchClient(transport);
    }
}

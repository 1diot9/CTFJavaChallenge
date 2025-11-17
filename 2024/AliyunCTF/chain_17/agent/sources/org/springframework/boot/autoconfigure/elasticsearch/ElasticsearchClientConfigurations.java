package org.springframework.boot.autoconfigure.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.JsonpMapper;
import co.elastic.clients.json.SimpleJsonpMapper;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.json.jsonb.JsonbJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientOptions;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.json.bind.Jsonb;
import jakarta.json.spi.JsonProvider;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/elasticsearch/ElasticsearchClientConfigurations.class */
class ElasticsearchClientConfigurations {
    ElasticsearchClientConfigurations() {
    }

    @Import({JacksonJsonpMapperConfiguration.class, JsonbJsonpMapperConfiguration.class, SimpleJsonpMapperConfiguration.class})
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/elasticsearch/ElasticsearchClientConfigurations$JsonpMapperConfiguration.class */
    static class JsonpMapperConfiguration {
        JsonpMapperConfiguration() {
        }
    }

    @ConditionalOnMissingBean({JsonpMapper.class})
    @ConditionalOnClass({ObjectMapper.class})
    @Configuration(proxyBeanMethods = false)
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/elasticsearch/ElasticsearchClientConfigurations$JacksonJsonpMapperConfiguration.class */
    static class JacksonJsonpMapperConfiguration {
        JacksonJsonpMapperConfiguration() {
        }

        @Bean
        JacksonJsonpMapper jacksonJsonpMapper() {
            return new JacksonJsonpMapper();
        }
    }

    @ConditionalOnMissingBean({JsonpMapper.class})
    @ConditionalOnBean({Jsonb.class})
    @Configuration(proxyBeanMethods = false)
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/elasticsearch/ElasticsearchClientConfigurations$JsonbJsonpMapperConfiguration.class */
    static class JsonbJsonpMapperConfiguration {
        JsonbJsonpMapperConfiguration() {
        }

        @Bean
        JsonbJsonpMapper jsonbJsonpMapper(Jsonb jsonb) {
            return new JsonbJsonpMapper(JsonProvider.provider(), jsonb);
        }
    }

    @ConditionalOnMissingBean({JsonpMapper.class})
    @Configuration(proxyBeanMethods = false)
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/elasticsearch/ElasticsearchClientConfigurations$SimpleJsonpMapperConfiguration.class */
    static class SimpleJsonpMapperConfiguration {
        SimpleJsonpMapperConfiguration() {
        }

        @Bean
        SimpleJsonpMapper simpleJsonpMapper() {
            return new SimpleJsonpMapper();
        }
    }

    @ConditionalOnMissingBean({ElasticsearchTransport.class})
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/elasticsearch/ElasticsearchClientConfigurations$ElasticsearchTransportConfiguration.class */
    static class ElasticsearchTransportConfiguration {
        ElasticsearchTransportConfiguration() {
        }

        @Bean
        RestClientTransport restClientTransport(RestClient restClient, JsonpMapper jsonMapper, ObjectProvider<RestClientOptions> restClientOptions) {
            return new RestClientTransport(restClient, jsonMapper, restClientOptions.getIfAvailable());
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnBean({ElasticsearchTransport.class})
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/elasticsearch/ElasticsearchClientConfigurations$ElasticsearchClientConfiguration.class */
    static class ElasticsearchClientConfiguration {
        ElasticsearchClientConfiguration() {
        }

        @ConditionalOnMissingBean
        @Bean
        ElasticsearchClient elasticsearchClient(ElasticsearchTransport transport) {
            return new ElasticsearchClient(transport);
        }
    }
}

package org.springframework.boot.autoconfigure.data.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import java.util.Collections;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScanner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchClient;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchCustomConversions;
import org.springframework.data.elasticsearch.core.convert.MappingElasticsearchConverter;
import org.springframework.data.elasticsearch.core.mapping.SimpleElasticsearchMappingContext;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/elasticsearch/ElasticsearchDataConfiguration.class */
abstract class ElasticsearchDataConfiguration {
    ElasticsearchDataConfiguration() {
    }

    @Configuration(proxyBeanMethods = false)
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/elasticsearch/ElasticsearchDataConfiguration$BaseConfiguration.class */
    static class BaseConfiguration {
        BaseConfiguration() {
        }

        @ConditionalOnMissingBean
        @Bean
        ElasticsearchCustomConversions elasticsearchCustomConversions() {
            return new ElasticsearchCustomConversions(Collections.emptyList());
        }

        @ConditionalOnMissingBean
        @Bean
        SimpleElasticsearchMappingContext elasticsearchMappingContext(ApplicationContext applicationContext, ElasticsearchCustomConversions elasticsearchCustomConversions) throws ClassNotFoundException {
            SimpleElasticsearchMappingContext mappingContext = new SimpleElasticsearchMappingContext();
            mappingContext.setInitialEntitySet(new EntityScanner(applicationContext).scan(Document.class));
            mappingContext.setSimpleTypeHolder(elasticsearchCustomConversions.getSimpleTypeHolder());
            return mappingContext;
        }

        @ConditionalOnMissingBean
        @Bean
        ElasticsearchConverter elasticsearchConverter(SimpleElasticsearchMappingContext mappingContext, ElasticsearchCustomConversions elasticsearchCustomConversions) {
            MappingElasticsearchConverter converter = new MappingElasticsearchConverter(mappingContext);
            converter.setConversions(elasticsearchCustomConversions);
            return converter;
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({ElasticsearchClient.class})
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/elasticsearch/ElasticsearchDataConfiguration$JavaClientConfiguration.class */
    static class JavaClientConfiguration {
        JavaClientConfiguration() {
        }

        @ConditionalOnMissingBean(value = {ElasticsearchOperations.class}, name = {"elasticsearchTemplate"})
        @ConditionalOnBean({ElasticsearchClient.class})
        @Bean
        ElasticsearchTemplate elasticsearchTemplate(ElasticsearchClient client, ElasticsearchConverter converter) {
            return new ElasticsearchTemplate(client, converter);
        }
    }

    @Configuration(proxyBeanMethods = false)
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/elasticsearch/ElasticsearchDataConfiguration$ReactiveRestClientConfiguration.class */
    static class ReactiveRestClientConfiguration {
        ReactiveRestClientConfiguration() {
        }

        @ConditionalOnMissingBean(value = {ReactiveElasticsearchOperations.class}, name = {"reactiveElasticsearchTemplate"})
        @ConditionalOnBean({ReactiveElasticsearchClient.class})
        @Bean
        ReactiveElasticsearchTemplate reactiveElasticsearchTemplate(ReactiveElasticsearchClient client, ElasticsearchConverter converter) {
            return new ReactiveElasticsearchTemplate(client, converter);
        }
    }
}

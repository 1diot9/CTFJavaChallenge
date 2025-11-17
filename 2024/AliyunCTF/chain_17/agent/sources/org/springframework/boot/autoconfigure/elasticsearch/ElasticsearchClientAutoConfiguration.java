package org.springframework.boot.autoconfigure.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import org.elasticsearch.client.RestClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchClientConfigurations;
import org.springframework.boot.autoconfigure.jsonb.JsonbAutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration(after = {JsonbAutoConfiguration.class, ElasticsearchRestClientAutoConfiguration.class})
@ConditionalOnClass({ElasticsearchClient.class})
@ConditionalOnBean({RestClient.class})
@Import({ElasticsearchClientConfigurations.JsonpMapperConfiguration.class, ElasticsearchClientConfigurations.ElasticsearchTransportConfiguration.class, ElasticsearchClientConfigurations.ElasticsearchClientConfiguration.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/elasticsearch/ElasticsearchClientAutoConfiguration.class */
public class ElasticsearchClientAutoConfiguration {
}

package org.springframework.boot.autoconfigure.data.elasticsearch;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataConfiguration;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchClientAutoConfiguration;
import org.springframework.boot.autoconfigure.elasticsearch.ReactiveElasticsearchClientAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;

@AutoConfiguration(after = {ElasticsearchClientAutoConfiguration.class, ReactiveElasticsearchClientAutoConfiguration.class})
@ConditionalOnClass({ElasticsearchTemplate.class})
@Import({ElasticsearchDataConfiguration.BaseConfiguration.class, ElasticsearchDataConfiguration.JavaClientConfiguration.class, ElasticsearchDataConfiguration.ReactiveRestClientConfiguration.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/elasticsearch/ElasticsearchDataAutoConfiguration.class */
public class ElasticsearchDataAutoConfiguration {
}

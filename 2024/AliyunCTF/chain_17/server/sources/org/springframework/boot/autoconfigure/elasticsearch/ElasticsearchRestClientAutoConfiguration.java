package org.springframework.boot.autoconfigure.elasticsearch;

import org.elasticsearch.client.RestClientBuilder;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientConfigurations;
import org.springframework.boot.autoconfigure.ssl.SslAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@EnableConfigurationProperties({ElasticsearchProperties.class})
@AutoConfiguration(after = {SslAutoConfiguration.class})
@ConditionalOnClass({RestClientBuilder.class})
@Import({ElasticsearchRestClientConfigurations.RestClientBuilderConfiguration.class, ElasticsearchRestClientConfigurations.RestClientConfiguration.class, ElasticsearchRestClientConfigurations.RestClientSnifferConfiguration.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/elasticsearch/ElasticsearchRestClientAutoConfiguration.class */
public class ElasticsearchRestClientAutoConfiguration {
}

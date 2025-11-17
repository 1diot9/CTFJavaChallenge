package org.springframework.boot.autoconfigure.data.elasticsearch;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.elasticsearch.repository.support.ElasticsearchRepositoryFactoryBean;

@AutoConfiguration
@ConditionalOnClass({ElasticsearchRepository.class})
@ConditionalOnMissingBean({ElasticsearchRepositoryFactoryBean.class})
@ConditionalOnProperty(prefix = "spring.data.elasticsearch.repositories", name = {"enabled"}, havingValue = "true", matchIfMissing = true)
@Import({ElasticsearchRepositoriesRegistrar.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/elasticsearch/ElasticsearchRepositoriesAutoConfiguration.class */
public class ElasticsearchRepositoriesAutoConfiguration {
}

package org.springframework.boot.autoconfigure.data.couchbase;

import com.couchbase.client.java.Cluster;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.ConditionalOnRepositoryType;
import org.springframework.boot.autoconfigure.data.RepositoryType;
import org.springframework.context.annotation.Import;
import org.springframework.data.couchbase.repository.ReactiveCouchbaseRepository;
import org.springframework.data.couchbase.repository.config.ReactiveRepositoryOperationsMapping;
import org.springframework.data.couchbase.repository.support.ReactiveCouchbaseRepositoryFactoryBean;
import reactor.core.publisher.Flux;

@AutoConfiguration(after = {CouchbaseReactiveDataAutoConfiguration.class})
@ConditionalOnClass({Cluster.class, ReactiveCouchbaseRepository.class, Flux.class})
@ConditionalOnMissingBean({ReactiveCouchbaseRepositoryFactoryBean.class})
@ConditionalOnRepositoryType(store = "couchbase", type = RepositoryType.REACTIVE)
@ConditionalOnBean({ReactiveRepositoryOperationsMapping.class})
@Import({CouchbaseReactiveRepositoriesRegistrar.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/couchbase/CouchbaseReactiveRepositoriesAutoConfiguration.class */
public class CouchbaseReactiveRepositoriesAutoConfiguration {
}

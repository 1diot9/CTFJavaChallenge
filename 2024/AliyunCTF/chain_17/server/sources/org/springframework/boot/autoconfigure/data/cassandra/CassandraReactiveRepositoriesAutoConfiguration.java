package org.springframework.boot.autoconfigure.data.cassandra;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.ConditionalOnRepositoryType;
import org.springframework.boot.autoconfigure.data.RepositoryType;
import org.springframework.context.annotation.Import;
import org.springframework.data.cassandra.ReactiveSession;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.data.cassandra.repository.support.ReactiveCassandraRepositoryFactoryBean;

@AutoConfiguration(after = {CassandraReactiveDataAutoConfiguration.class})
@ConditionalOnClass({ReactiveSession.class, ReactiveCassandraRepository.class})
@ConditionalOnMissingBean({ReactiveCassandraRepositoryFactoryBean.class})
@ConditionalOnRepositoryType(store = "cassandra", type = RepositoryType.REACTIVE)
@Import({CassandraReactiveRepositoriesRegistrar.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/cassandra/CassandraReactiveRepositoriesAutoConfiguration.class */
public class CassandraReactiveRepositoriesAutoConfiguration {
}

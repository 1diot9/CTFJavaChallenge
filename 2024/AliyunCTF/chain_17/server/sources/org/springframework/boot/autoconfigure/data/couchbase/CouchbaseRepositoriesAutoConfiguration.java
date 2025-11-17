package org.springframework.boot.autoconfigure.data.couchbase;

import com.couchbase.client.java.Bucket;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.ConditionalOnRepositoryType;
import org.springframework.boot.autoconfigure.data.RepositoryType;
import org.springframework.context.annotation.Import;
import org.springframework.data.couchbase.repository.CouchbaseRepository;
import org.springframework.data.couchbase.repository.config.RepositoryOperationsMapping;
import org.springframework.data.couchbase.repository.support.CouchbaseRepositoryFactoryBean;

@AutoConfiguration
@ConditionalOnClass({Bucket.class, CouchbaseRepository.class})
@ConditionalOnMissingBean({CouchbaseRepositoryFactoryBean.class})
@ConditionalOnRepositoryType(store = "couchbase", type = RepositoryType.IMPERATIVE)
@ConditionalOnBean({RepositoryOperationsMapping.class})
@Import({CouchbaseRepositoriesRegistrar.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/couchbase/CouchbaseRepositoriesAutoConfiguration.class */
public class CouchbaseRepositoriesAutoConfiguration {
}

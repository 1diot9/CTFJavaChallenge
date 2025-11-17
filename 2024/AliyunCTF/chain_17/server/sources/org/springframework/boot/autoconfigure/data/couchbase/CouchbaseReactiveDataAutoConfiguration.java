package org.springframework.boot.autoconfigure.data.couchbase;

import com.couchbase.client.java.Cluster;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Import;
import org.springframework.data.couchbase.repository.ReactiveCouchbaseRepository;
import reactor.core.publisher.Flux;

@AutoConfiguration(after = {CouchbaseDataAutoConfiguration.class})
@ConditionalOnClass({Cluster.class, ReactiveCouchbaseRepository.class, Flux.class})
@Import({CouchbaseReactiveDataConfiguration.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/couchbase/CouchbaseReactiveDataAutoConfiguration.class */
public class CouchbaseReactiveDataAutoConfiguration {
}

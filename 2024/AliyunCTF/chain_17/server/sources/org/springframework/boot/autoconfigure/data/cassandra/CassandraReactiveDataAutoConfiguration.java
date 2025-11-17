package org.springframework.boot.autoconfigure.data.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.cassandra.ReactiveSession;
import org.springframework.data.cassandra.ReactiveSessionFactory;
import org.springframework.data.cassandra.core.ReactiveCassandraOperations;
import org.springframework.data.cassandra.core.ReactiveCassandraTemplate;
import org.springframework.data.cassandra.core.convert.CassandraConverter;
import org.springframework.data.cassandra.core.cql.session.DefaultBridgedReactiveSession;
import org.springframework.data.cassandra.core.cql.session.DefaultReactiveSessionFactory;
import reactor.core.publisher.Flux;

@AutoConfiguration(after = {CassandraDataAutoConfiguration.class})
@ConditionalOnClass({CqlSession.class, ReactiveCassandraTemplate.class, Flux.class})
@ConditionalOnBean({CqlSession.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/cassandra/CassandraReactiveDataAutoConfiguration.class */
public class CassandraReactiveDataAutoConfiguration {
    @ConditionalOnMissingBean
    @Bean
    public ReactiveSession reactiveCassandraSession(CqlSession session) {
        return new DefaultBridgedReactiveSession(session);
    }

    @ConditionalOnMissingBean
    @Bean
    public ReactiveSessionFactory reactiveCassandraSessionFactory(ReactiveSession reactiveCassandraSession) {
        return new DefaultReactiveSessionFactory(reactiveCassandraSession);
    }

    @ConditionalOnMissingBean({ReactiveCassandraOperations.class})
    @Bean
    public ReactiveCassandraTemplate reactiveCassandraTemplate(ReactiveSession reactiveCassandraSession, CassandraConverter converter) {
        return new ReactiveCassandraTemplate(reactiveCassandraSession, converter);
    }
}

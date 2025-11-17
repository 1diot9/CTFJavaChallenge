package org.springframework.boot.autoconfigure.data.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScanPackages;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.cassandra.CassandraManagedTypes;
import org.springframework.data.cassandra.SessionFactory;
import org.springframework.data.cassandra.config.CassandraEntityClassScanner;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.config.SessionFactoryFactoryBean;
import org.springframework.data.cassandra.core.CassandraAdminOperations;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.core.convert.CassandraConverter;
import org.springframework.data.cassandra.core.convert.CassandraCustomConversions;
import org.springframework.data.cassandra.core.convert.MappingCassandraConverter;
import org.springframework.data.cassandra.core.mapping.CassandraMappingContext;
import org.springframework.data.cassandra.core.mapping.SimpleUserTypeResolver;

@AutoConfiguration(after = {CassandraAutoConfiguration.class})
@ConditionalOnClass({CqlSession.class, CassandraAdminOperations.class})
@ConditionalOnBean({CqlSession.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/cassandra/CassandraDataAutoConfiguration.class */
public class CassandraDataAutoConfiguration {
    private final CqlSession session;

    public CassandraDataAutoConfiguration(CqlSession session) {
        this.session = session;
    }

    @ConditionalOnMissingBean
    @Bean
    public static CassandraManagedTypes cassandraManagedTypes(BeanFactory beanFactory) throws ClassNotFoundException {
        List<String> packages = EntityScanPackages.get(beanFactory).getPackageNames();
        if (packages.isEmpty() && AutoConfigurationPackages.has(beanFactory)) {
            packages = AutoConfigurationPackages.get(beanFactory);
        }
        if (!packages.isEmpty()) {
            return CassandraManagedTypes.fromIterable(CassandraEntityClassScanner.scan(packages));
        }
        return CassandraManagedTypes.empty();
    }

    @ConditionalOnMissingBean
    @Bean
    public CassandraMappingContext cassandraMappingContext(CassandraManagedTypes cassandraManagedTypes, CassandraCustomConversions conversions) {
        CassandraMappingContext context = new CassandraMappingContext();
        context.setManagedTypes(cassandraManagedTypes);
        context.setSimpleTypeHolder(conversions.getSimpleTypeHolder());
        return context;
    }

    @ConditionalOnMissingBean
    @Bean
    public CassandraConverter cassandraConverter(CassandraMappingContext mapping, CassandraCustomConversions conversions) {
        MappingCassandraConverter converter = new MappingCassandraConverter(mapping);
        converter.setCodecRegistry(this.session.getContext().getCodecRegistry());
        converter.setCustomConversions(conversions);
        converter.setUserTypeResolver(new SimpleUserTypeResolver(this.session));
        return converter;
    }

    @ConditionalOnMissingBean({SessionFactory.class})
    @Bean
    public SessionFactoryFactoryBean cassandraSessionFactory(Environment environment, CassandraConverter converter) {
        SessionFactoryFactoryBean session = new SessionFactoryFactoryBean();
        session.setSession(this.session);
        session.setConverter(converter);
        Binder binder = Binder.get(environment);
        BindResult bind = binder.bind("spring.cassandra.schema-action", SchemaAction.class);
        Objects.requireNonNull(session);
        bind.ifBound(session::setSchemaAction);
        return session;
    }

    @ConditionalOnMissingBean({CassandraOperations.class})
    @Bean
    public CassandraTemplate cassandraTemplate(SessionFactory sessionFactory, CassandraConverter converter) {
        return new CassandraTemplate(sessionFactory, converter);
    }

    @ConditionalOnMissingBean
    @Bean
    public CassandraCustomConversions cassandraCustomConversions() {
        return new CassandraCustomConversions(Collections.emptyList());
    }
}

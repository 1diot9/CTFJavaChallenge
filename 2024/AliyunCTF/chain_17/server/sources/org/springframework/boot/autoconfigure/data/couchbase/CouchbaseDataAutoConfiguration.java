package org.springframework.boot.autoconfigure.data.couchbase;

import com.couchbase.client.java.Bucket;
import jakarta.validation.Validator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.couchbase.core.mapping.event.ValidatingCouchbaseEventListener;
import org.springframework.data.couchbase.repository.CouchbaseRepository;

@EnableConfigurationProperties({CouchbaseDataProperties.class})
@AutoConfiguration(after = {CouchbaseAutoConfiguration.class, ValidationAutoConfiguration.class})
@ConditionalOnClass({Bucket.class, CouchbaseRepository.class})
@Import({CouchbaseDataConfiguration.class, CouchbaseClientFactoryConfiguration.class, CouchbaseClientFactoryDependentConfiguration.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/couchbase/CouchbaseDataAutoConfiguration.class */
public class CouchbaseDataAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({Validator.class})
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/couchbase/CouchbaseDataAutoConfiguration$ValidationConfiguration.class */
    public static class ValidationConfiguration {
        @Bean
        @ConditionalOnSingleCandidate(Validator.class)
        public ValidatingCouchbaseEventListener validationEventListener(Validator validator) {
            return new ValidatingCouchbaseEventListener(validator);
        }
    }
}

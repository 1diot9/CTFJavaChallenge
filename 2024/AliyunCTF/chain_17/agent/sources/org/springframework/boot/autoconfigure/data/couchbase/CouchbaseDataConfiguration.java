package org.springframework.boot.autoconfigure.data.couchbase;

import java.util.Collections;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScanner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.couchbase.core.convert.CouchbaseCustomConversions;
import org.springframework.data.couchbase.core.convert.MappingCouchbaseConverter;
import org.springframework.data.couchbase.core.convert.translation.JacksonTranslationService;
import org.springframework.data.couchbase.core.convert.translation.TranslationService;
import org.springframework.data.couchbase.core.mapping.CouchbaseMappingContext;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.mapping.model.FieldNamingStrategy;

@Configuration(proxyBeanMethods = false)
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/couchbase/CouchbaseDataConfiguration.class */
class CouchbaseDataConfiguration {
    CouchbaseDataConfiguration() {
    }

    @ConditionalOnMissingBean
    @Bean
    MappingCouchbaseConverter couchbaseMappingConverter(CouchbaseDataProperties properties, CouchbaseMappingContext couchbaseMappingContext, CouchbaseCustomConversions couchbaseCustomConversions) {
        MappingCouchbaseConverter converter = new MappingCouchbaseConverter(couchbaseMappingContext, properties.getTypeKey());
        converter.setCustomConversions(couchbaseCustomConversions);
        return converter;
    }

    @ConditionalOnMissingBean
    @Bean
    TranslationService couchbaseTranslationService() {
        return new JacksonTranslationService();
    }

    @ConditionalOnMissingBean(name = {"couchbaseMappingContext"})
    @Bean(name = {"couchbaseMappingContext"})
    CouchbaseMappingContext couchbaseMappingContext(CouchbaseDataProperties properties, ApplicationContext applicationContext, CouchbaseCustomConversions couchbaseCustomConversions) throws ClassNotFoundException {
        CouchbaseMappingContext mappingContext = new CouchbaseMappingContext();
        mappingContext.setInitialEntitySet(new EntityScanner(applicationContext).scan(Document.class));
        mappingContext.setSimpleTypeHolder(couchbaseCustomConversions.getSimpleTypeHolder());
        Class<?> fieldNamingStrategy = properties.getFieldNamingStrategy();
        if (fieldNamingStrategy != null) {
            mappingContext.setFieldNamingStrategy((FieldNamingStrategy) BeanUtils.instantiateClass(fieldNamingStrategy));
        }
        mappingContext.setAutoIndexCreation(properties.isAutoIndex());
        return mappingContext;
    }

    @ConditionalOnMissingBean(name = {"couchbaseCustomConversions"})
    @Bean(name = {"couchbaseCustomConversions"})
    CouchbaseCustomConversions couchbaseCustomConversions() {
        return new CouchbaseCustomConversions(Collections.emptyList());
    }
}

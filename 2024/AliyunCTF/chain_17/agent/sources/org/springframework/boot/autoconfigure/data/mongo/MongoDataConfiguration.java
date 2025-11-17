package org.springframework.boot.autoconfigure.data.mongo;

import java.util.Collections;
import java.util.Objects;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScanner;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mapping.model.FieldNamingStrategy;
import org.springframework.data.mongodb.MongoManagedTypes;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

@Configuration(proxyBeanMethods = false)
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/mongo/MongoDataConfiguration.class */
class MongoDataConfiguration {
    MongoDataConfiguration() {
    }

    @ConditionalOnMissingBean
    @Bean
    static MongoManagedTypes mongoManagedTypes(ApplicationContext applicationContext) throws ClassNotFoundException {
        return MongoManagedTypes.fromIterable(new EntityScanner(applicationContext).scan(Document.class));
    }

    @ConditionalOnMissingBean
    @Bean
    MongoMappingContext mongoMappingContext(MongoProperties properties, MongoCustomConversions conversions, MongoManagedTypes managedTypes) {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        MongoMappingContext context = new MongoMappingContext();
        PropertyMapper.Source from = map.from((PropertyMapper) properties.isAutoIndexCreation());
        Objects.requireNonNull(context);
        from.to((v1) -> {
            r1.setAutoIndexCreation(v1);
        });
        context.setManagedTypes(managedTypes);
        Class<?> strategyClass = properties.getFieldNamingStrategy();
        if (strategyClass != null) {
            context.setFieldNamingStrategy((FieldNamingStrategy) BeanUtils.instantiateClass(strategyClass));
        }
        context.setSimpleTypeHolder(conversions.getSimpleTypeHolder());
        return context;
    }

    @ConditionalOnMissingBean
    @Bean
    MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(Collections.emptyList());
    }
}

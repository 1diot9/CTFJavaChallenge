package org.springframework.boot.autoconfigure.session;

import java.util.Objects;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.session.ReactiveSessionRepository;
import org.springframework.session.config.ReactiveSessionRepositoryCustomizer;
import org.springframework.session.data.mongo.ReactiveMongoSessionRepository;
import org.springframework.session.data.mongo.config.annotation.web.reactive.ReactiveMongoWebSessionConfiguration;

@EnableConfigurationProperties({MongoSessionProperties.class})
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ReactiveMongoOperations.class, ReactiveMongoSessionRepository.class})
@ConditionalOnMissingBean({ReactiveSessionRepository.class})
@ConditionalOnBean({ReactiveMongoOperations.class})
@Import({ReactiveMongoWebSessionConfiguration.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/session/MongoReactiveSessionConfiguration.class */
class MongoReactiveSessionConfiguration {
    MongoReactiveSessionConfiguration() {
    }

    @Bean
    ReactiveSessionRepositoryCustomizer<ReactiveMongoSessionRepository> springBootSessionRepositoryCustomizer(SessionProperties sessionProperties, MongoSessionProperties mongoSessionProperties, ServerProperties serverProperties) {
        return sessionRepository -> {
            PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            PropertyMapper.Source from = map.from((PropertyMapper) sessionProperties.determineTimeout(() -> {
                return serverProperties.getReactive().getSession().getTimeout();
            }));
            Objects.requireNonNull(sessionRepository);
            from.to(sessionRepository::setDefaultMaxInactiveInterval);
            Objects.requireNonNull(mongoSessionProperties);
            PropertyMapper.Source from2 = map.from(mongoSessionProperties::getCollectionName);
            Objects.requireNonNull(sessionRepository);
            from2.to(sessionRepository::setCollectionName);
        };
    }
}

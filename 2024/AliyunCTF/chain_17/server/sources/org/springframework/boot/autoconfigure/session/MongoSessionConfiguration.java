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
import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.session.SessionRepository;
import org.springframework.session.config.SessionRepositoryCustomizer;
import org.springframework.session.data.mongo.MongoIndexedSessionRepository;
import org.springframework.session.data.mongo.config.annotation.web.http.MongoHttpSessionConfiguration;

@EnableConfigurationProperties({MongoSessionProperties.class})
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({MongoOperations.class, MongoIndexedSessionRepository.class})
@ConditionalOnMissingBean({SessionRepository.class})
@ConditionalOnBean({MongoOperations.class})
@Import({MongoHttpSessionConfiguration.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/session/MongoSessionConfiguration.class */
class MongoSessionConfiguration {
    MongoSessionConfiguration() {
    }

    @Bean
    @Order(Integer.MIN_VALUE)
    SessionRepositoryCustomizer<MongoIndexedSessionRepository> springBootSessionRepositoryCustomizer(SessionProperties sessionProperties, MongoSessionProperties mongoSessionProperties, ServerProperties serverProperties) {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        return sessionRepository -> {
            PropertyMapper.Source from = map.from((PropertyMapper) sessionProperties.determineTimeout(() -> {
                return serverProperties.getServlet().getSession().getTimeout();
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

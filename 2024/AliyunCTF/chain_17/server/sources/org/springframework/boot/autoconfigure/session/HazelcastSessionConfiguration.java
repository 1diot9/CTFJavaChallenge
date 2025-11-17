package org.springframework.boot.autoconfigure.session;

import com.hazelcast.core.HazelcastInstance;
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
import org.springframework.session.SessionRepository;
import org.springframework.session.config.SessionRepositoryCustomizer;
import org.springframework.session.hazelcast.HazelcastIndexedSessionRepository;
import org.springframework.session.hazelcast.config.annotation.web.http.HazelcastHttpSessionConfiguration;

@EnableConfigurationProperties({HazelcastSessionProperties.class})
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({HazelcastIndexedSessionRepository.class})
@ConditionalOnMissingBean({SessionRepository.class})
@ConditionalOnBean({HazelcastInstance.class})
@Import({HazelcastHttpSessionConfiguration.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/session/HazelcastSessionConfiguration.class */
class HazelcastSessionConfiguration {
    HazelcastSessionConfiguration() {
    }

    @Bean
    @Order(Integer.MIN_VALUE)
    SessionRepositoryCustomizer<HazelcastIndexedSessionRepository> springBootSessionRepositoryCustomizer(SessionProperties sessionProperties, HazelcastSessionProperties hazelcastSessionProperties, ServerProperties serverProperties) {
        return sessionRepository -> {
            PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            PropertyMapper.Source from = map.from((PropertyMapper) sessionProperties.determineTimeout(() -> {
                return serverProperties.getServlet().getSession().getTimeout();
            }));
            Objects.requireNonNull(sessionRepository);
            from.to(sessionRepository::setDefaultMaxInactiveInterval);
            Objects.requireNonNull(hazelcastSessionProperties);
            PropertyMapper.Source from2 = map.from(hazelcastSessionProperties::getMapName);
            Objects.requireNonNull(sessionRepository);
            from2.to(sessionRepository::setSessionMapName);
            Objects.requireNonNull(hazelcastSessionProperties);
            PropertyMapper.Source from3 = map.from(hazelcastSessionProperties::getFlushMode);
            Objects.requireNonNull(sessionRepository);
            from3.to(sessionRepository::setFlushMode);
            Objects.requireNonNull(hazelcastSessionProperties);
            PropertyMapper.Source from4 = map.from(hazelcastSessionProperties::getSaveMode);
            Objects.requireNonNull(sessionRepository);
            from4.to(sessionRepository::setSaveMode);
        };
    }
}

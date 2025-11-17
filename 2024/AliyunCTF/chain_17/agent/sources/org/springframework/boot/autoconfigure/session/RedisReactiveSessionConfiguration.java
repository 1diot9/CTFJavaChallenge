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
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.session.ReactiveSessionRepository;
import org.springframework.session.config.ReactiveSessionRepositoryCustomizer;
import org.springframework.session.data.redis.ReactiveRedisSessionRepository;
import org.springframework.session.data.redis.config.annotation.web.server.RedisWebSessionConfiguration;

@EnableConfigurationProperties({RedisSessionProperties.class})
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ReactiveRedisConnectionFactory.class, ReactiveRedisSessionRepository.class})
@ConditionalOnMissingBean({ReactiveSessionRepository.class})
@ConditionalOnBean({ReactiveRedisConnectionFactory.class})
@Import({RedisWebSessionConfiguration.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/session/RedisReactiveSessionConfiguration.class */
class RedisReactiveSessionConfiguration {
    RedisReactiveSessionConfiguration() {
    }

    @Bean
    ReactiveSessionRepositoryCustomizer<ReactiveRedisSessionRepository> springBootSessionRepositoryCustomizer(SessionProperties sessionProperties, RedisSessionProperties redisSessionProperties, ServerProperties serverProperties) {
        return sessionRepository -> {
            PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            PropertyMapper.Source from = map.from((PropertyMapper) sessionProperties.determineTimeout(() -> {
                return serverProperties.getReactive().getSession().getTimeout();
            }));
            Objects.requireNonNull(sessionRepository);
            from.to(sessionRepository::setDefaultMaxInactiveInterval);
            Objects.requireNonNull(redisSessionProperties);
            PropertyMapper.Source from2 = map.from(redisSessionProperties::getNamespace);
            Objects.requireNonNull(sessionRepository);
            from2.to(sessionRepository::setRedisKeyNamespace);
            Objects.requireNonNull(redisSessionProperties);
            PropertyMapper.Source from3 = map.from(redisSessionProperties::getSaveMode);
            Objects.requireNonNull(sessionRepository);
            from3.to(sessionRepository::setSaveMode);
        };
    }
}

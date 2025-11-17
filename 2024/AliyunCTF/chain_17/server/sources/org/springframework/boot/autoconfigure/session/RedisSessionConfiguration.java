package org.springframework.boot.autoconfigure.session;

import java.util.Objects;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.context.properties.source.InvalidConfigurationPropertyValueException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.session.SessionRepository;
import org.springframework.session.config.SessionRepositoryCustomizer;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.session.data.redis.RedisSessionRepository;
import org.springframework.session.data.redis.config.ConfigureNotifyKeyspaceEventsAction;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration;
import org.springframework.session.data.redis.config.annotation.web.http.RedisIndexedHttpSessionConfiguration;

@EnableConfigurationProperties({RedisSessionProperties.class})
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({RedisTemplate.class, RedisIndexedSessionRepository.class})
@ConditionalOnMissingBean({SessionRepository.class})
@ConditionalOnBean({RedisConnectionFactory.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/session/RedisSessionConfiguration.class */
class RedisSessionConfiguration {
    RedisSessionConfiguration() {
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(prefix = "spring.session.redis", name = {"repository-type"}, havingValue = "default", matchIfMissing = true)
    @Import({RedisHttpSessionConfiguration.class})
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/session/RedisSessionConfiguration$DefaultRedisSessionConfiguration.class */
    static class DefaultRedisSessionConfiguration {
        DefaultRedisSessionConfiguration() {
        }

        @Bean
        @Order(Integer.MIN_VALUE)
        SessionRepositoryCustomizer<RedisSessionRepository> springBootSessionRepositoryCustomizer(SessionProperties sessionProperties, RedisSessionProperties redisSessionProperties, ServerProperties serverProperties) {
            String cleanupCron = redisSessionProperties.getCleanupCron();
            if (cleanupCron != null) {
                throw new InvalidConfigurationPropertyValueException("spring.session.redis.cleanup-cron", cleanupCron, "Cron-based cleanup is only supported when spring.session.redis.repository-type is set to indexed.");
            }
            return sessionRepository -> {
                PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
                PropertyMapper.Source from = map.from((PropertyMapper) sessionProperties.determineTimeout(() -> {
                    return serverProperties.getServlet().getSession().getTimeout();
                }));
                Objects.requireNonNull(sessionRepository);
                from.to(sessionRepository::setDefaultMaxInactiveInterval);
                Objects.requireNonNull(redisSessionProperties);
                PropertyMapper.Source from2 = map.from(redisSessionProperties::getNamespace);
                Objects.requireNonNull(sessionRepository);
                from2.to(sessionRepository::setRedisKeyNamespace);
                Objects.requireNonNull(redisSessionProperties);
                PropertyMapper.Source from3 = map.from(redisSessionProperties::getFlushMode);
                Objects.requireNonNull(sessionRepository);
                from3.to(sessionRepository::setFlushMode);
                Objects.requireNonNull(redisSessionProperties);
                PropertyMapper.Source from4 = map.from(redisSessionProperties::getSaveMode);
                Objects.requireNonNull(sessionRepository);
                from4.to(sessionRepository::setSaveMode);
            };
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(prefix = "spring.session.redis", name = {"repository-type"}, havingValue = "indexed")
    @Import({RedisIndexedHttpSessionConfiguration.class})
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/session/RedisSessionConfiguration$IndexedRedisSessionConfiguration.class */
    static class IndexedRedisSessionConfiguration {
        IndexedRedisSessionConfiguration() {
        }

        @ConditionalOnMissingBean
        @Bean
        ConfigureRedisAction configureRedisAction(RedisSessionProperties redisSessionProperties) {
            switch (redisSessionProperties.getConfigureAction()) {
                case NOTIFY_KEYSPACE_EVENTS:
                    return new ConfigureNotifyKeyspaceEventsAction();
                case NONE:
                    return ConfigureRedisAction.NO_OP;
                default:
                    throw new IncompatibleClassChangeError();
            }
        }

        @Bean
        @Order(Integer.MIN_VALUE)
        SessionRepositoryCustomizer<RedisIndexedSessionRepository> springBootSessionRepositoryCustomizer(SessionProperties sessionProperties, RedisSessionProperties redisSessionProperties, ServerProperties serverProperties) {
            return sessionRepository -> {
                PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
                PropertyMapper.Source from = map.from((PropertyMapper) sessionProperties.determineTimeout(() -> {
                    return serverProperties.getServlet().getSession().getTimeout();
                }));
                Objects.requireNonNull(sessionRepository);
                from.to(sessionRepository::setDefaultMaxInactiveInterval);
                Objects.requireNonNull(redisSessionProperties);
                PropertyMapper.Source from2 = map.from(redisSessionProperties::getNamespace);
                Objects.requireNonNull(sessionRepository);
                from2.to(sessionRepository::setRedisKeyNamespace);
                Objects.requireNonNull(redisSessionProperties);
                PropertyMapper.Source from3 = map.from(redisSessionProperties::getFlushMode);
                Objects.requireNonNull(sessionRepository);
                from3.to(sessionRepository::setFlushMode);
                Objects.requireNonNull(redisSessionProperties);
                PropertyMapper.Source from4 = map.from(redisSessionProperties::getSaveMode);
                Objects.requireNonNull(sessionRepository);
                from4.to(sessionRepository::setSaveMode);
                Objects.requireNonNull(redisSessionProperties);
                PropertyMapper.Source from5 = map.from(redisSessionProperties::getCleanupCron);
                Objects.requireNonNull(sessionRepository);
                from5.to(sessionRepository::setCleanupCron);
            };
        }
    }
}

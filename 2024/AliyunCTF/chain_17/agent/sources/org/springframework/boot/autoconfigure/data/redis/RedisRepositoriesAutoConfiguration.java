package org.springframework.boot.autoconfigure.data.redis;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.repository.support.RedisRepositoryFactoryBean;

@AutoConfiguration(after = {RedisAutoConfiguration.class})
@ConditionalOnClass({EnableRedisRepositories.class})
@ConditionalOnMissingBean({RedisRepositoryFactoryBean.class})
@ConditionalOnBean({RedisConnectionFactory.class})
@ConditionalOnProperty(prefix = "spring.data.redis.repositories", name = {"enabled"}, havingValue = "true", matchIfMissing = true)
@Import({RedisRepositoriesRegistrar.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/redis/RedisRepositoriesAutoConfiguration.class */
public class RedisRepositoriesAutoConfiguration {
}

package org.springframework.boot.autoconfigure.data.redis;

import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/redis/JedisClientConfigurationBuilderCustomizer.class */
public interface JedisClientConfigurationBuilderCustomizer {
    void customize(JedisClientConfiguration.JedisClientConfigurationBuilder clientConfigurationBuilder);
}

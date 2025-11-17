package org.springframework.boot.autoconfigure.data.redis;

import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/redis/LettuceClientConfigurationBuilderCustomizer.class */
public interface LettuceClientConfigurationBuilderCustomizer {
    void customize(LettuceClientConfiguration.LettuceClientConfigurationBuilder clientConfigurationBuilder);
}

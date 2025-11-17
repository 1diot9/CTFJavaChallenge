package org.springframework.boot.autoconfigure.hazelcast;

import com.hazelcast.config.Config;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/hazelcast/HazelcastConfigCustomizer.class */
public interface HazelcastConfigCustomizer {
    void customize(Config config);
}

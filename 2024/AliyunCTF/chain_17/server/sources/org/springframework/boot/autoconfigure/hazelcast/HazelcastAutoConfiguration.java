package org.springframework.boot.autoconfigure.hazelcast;

import com.hazelcast.core.HazelcastInstance;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@EnableConfigurationProperties({HazelcastProperties.class})
@AutoConfiguration
@ConditionalOnClass({HazelcastInstance.class})
@Import({HazelcastClientConfiguration.class, HazelcastServerConfiguration.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/hazelcast/HazelcastAutoConfiguration.class */
public class HazelcastAutoConfiguration {
}

package org.springframework.boot.autoconfigure.jms.artemis;

import org.apache.activemq.artemis.core.config.Configuration;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jms/artemis/ArtemisConfigurationCustomizer.class */
public interface ArtemisConfigurationCustomizer {
    void customize(Configuration configuration);
}

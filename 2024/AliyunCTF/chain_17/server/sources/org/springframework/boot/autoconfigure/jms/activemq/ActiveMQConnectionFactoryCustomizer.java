package org.springframework.boot.autoconfigure.jms.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jms/activemq/ActiveMQConnectionFactoryCustomizer.class */
public interface ActiveMQConnectionFactoryCustomizer {
    void customize(ActiveMQConnectionFactory factory);
}

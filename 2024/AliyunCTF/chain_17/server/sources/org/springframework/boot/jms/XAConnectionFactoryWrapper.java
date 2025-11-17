package org.springframework.boot.jms;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.XAConnectionFactory;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/jms/XAConnectionFactoryWrapper.class */
public interface XAConnectionFactoryWrapper {
    ConnectionFactory wrapConnectionFactory(XAConnectionFactory connectionFactory) throws Exception;
}

package org.springframework.boot.autoconfigure.jms.activemq;

import org.springframework.boot.autoconfigure.service.connection.ConnectionDetails;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jms/activemq/ActiveMQConnectionDetails.class */
public interface ActiveMQConnectionDetails extends ConnectionDetails {
    String getBrokerUrl();

    String getUser();

    String getPassword();
}

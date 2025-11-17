package org.springframework.boot.autoconfigure.pulsar;

import org.springframework.boot.autoconfigure.service.connection.ConnectionDetails;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/pulsar/PulsarConnectionDetails.class */
public interface PulsarConnectionDetails extends ConnectionDetails {
    String getBrokerUrl();

    String getAdminUrl();
}

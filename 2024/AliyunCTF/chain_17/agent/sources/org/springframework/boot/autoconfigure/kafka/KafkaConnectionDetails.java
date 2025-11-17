package org.springframework.boot.autoconfigure.kafka;

import java.util.List;
import org.springframework.boot.autoconfigure.service.connection.ConnectionDetails;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/kafka/KafkaConnectionDetails.class */
public interface KafkaConnectionDetails extends ConnectionDetails {
    List<String> getBootstrapServers();

    default List<String> getConsumerBootstrapServers() {
        return getBootstrapServers();
    }

    default List<String> getProducerBootstrapServers() {
        return getBootstrapServers();
    }

    default List<String> getAdminBootstrapServers() {
        return getBootstrapServers();
    }

    default List<String> getStreamsBootstrapServers() {
        return getBootstrapServers();
    }
}

package org.springframework.boot.autoconfigure.kafka;

import java.util.List;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/kafka/PropertiesKafkaConnectionDetails.class */
class PropertiesKafkaConnectionDetails implements KafkaConnectionDetails {
    private final KafkaProperties properties;

    /* JADX INFO: Access modifiers changed from: package-private */
    public PropertiesKafkaConnectionDetails(KafkaProperties properties) {
        this.properties = properties;
    }

    @Override // org.springframework.boot.autoconfigure.kafka.KafkaConnectionDetails
    public List<String> getBootstrapServers() {
        return this.properties.getBootstrapServers();
    }

    @Override // org.springframework.boot.autoconfigure.kafka.KafkaConnectionDetails
    public List<String> getConsumerBootstrapServers() {
        return getServers(this.properties.getConsumer().getBootstrapServers());
    }

    @Override // org.springframework.boot.autoconfigure.kafka.KafkaConnectionDetails
    public List<String> getProducerBootstrapServers() {
        return getServers(this.properties.getProducer().getBootstrapServers());
    }

    @Override // org.springframework.boot.autoconfigure.kafka.KafkaConnectionDetails
    public List<String> getStreamsBootstrapServers() {
        return getServers(this.properties.getStreams().getBootstrapServers());
    }

    private List<String> getServers(List<String> servers) {
        return servers != null ? servers : getBootstrapServers();
    }
}

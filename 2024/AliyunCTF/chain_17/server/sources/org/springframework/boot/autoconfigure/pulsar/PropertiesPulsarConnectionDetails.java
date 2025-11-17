package org.springframework.boot.autoconfigure.pulsar;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/pulsar/PropertiesPulsarConnectionDetails.class */
class PropertiesPulsarConnectionDetails implements PulsarConnectionDetails {
    private final PulsarProperties pulsarProperties;

    /* JADX INFO: Access modifiers changed from: package-private */
    public PropertiesPulsarConnectionDetails(PulsarProperties pulsarProperties) {
        this.pulsarProperties = pulsarProperties;
    }

    @Override // org.springframework.boot.autoconfigure.pulsar.PulsarConnectionDetails
    public String getBrokerUrl() {
        return this.pulsarProperties.getClient().getServiceUrl();
    }

    @Override // org.springframework.boot.autoconfigure.pulsar.PulsarConnectionDetails
    public String getAdminUrl() {
        return this.pulsarProperties.getAdmin().getServiceUrl();
    }
}

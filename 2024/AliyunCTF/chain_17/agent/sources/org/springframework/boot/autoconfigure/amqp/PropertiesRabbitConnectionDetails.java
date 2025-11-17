package org.springframework.boot.autoconfigure.amqp;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.autoconfigure.amqp.RabbitConnectionDetails;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/amqp/PropertiesRabbitConnectionDetails.class */
class PropertiesRabbitConnectionDetails implements RabbitConnectionDetails {
    private final RabbitProperties properties;

    /* JADX INFO: Access modifiers changed from: package-private */
    public PropertiesRabbitConnectionDetails(RabbitProperties properties) {
        this.properties = properties;
    }

    @Override // org.springframework.boot.autoconfigure.amqp.RabbitConnectionDetails
    public String getUsername() {
        return this.properties.determineUsername();
    }

    @Override // org.springframework.boot.autoconfigure.amqp.RabbitConnectionDetails
    public String getPassword() {
        return this.properties.determinePassword();
    }

    @Override // org.springframework.boot.autoconfigure.amqp.RabbitConnectionDetails
    public String getVirtualHost() {
        return this.properties.determineVirtualHost();
    }

    @Override // org.springframework.boot.autoconfigure.amqp.RabbitConnectionDetails
    public List<RabbitConnectionDetails.Address> getAddresses() {
        List<RabbitConnectionDetails.Address> addresses = new ArrayList<>();
        for (String address : this.properties.determineAddresses().split(",")) {
            int portSeparatorIndex = address.lastIndexOf(58);
            String host = address.substring(0, portSeparatorIndex);
            String port = address.substring(portSeparatorIndex + 1);
            addresses.add(new RabbitConnectionDetails.Address(host, Integer.parseInt(port)));
        }
        return addresses;
    }
}

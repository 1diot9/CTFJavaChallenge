package org.springframework.boot.autoconfigure.amqp;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import org.springframework.boot.autoconfigure.service.connection.ConnectionDetails;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/amqp/RabbitConnectionDetails.class */
public interface RabbitConnectionDetails extends ConnectionDetails {
    List<Address> getAddresses();

    default String getUsername() {
        return null;
    }

    default String getPassword() {
        return null;
    }

    default String getVirtualHost() {
        return null;
    }

    default Address getFirstAddress() {
        List<Address> addresses = getAddresses();
        Assert.state(!addresses.isEmpty(), "Address list is empty");
        return addresses.get(0);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/amqp/RabbitConnectionDetails$Address.class */
    public static final class Address extends Record {
        private final String host;
        private final int port;

        public Address(String host, int port) {
            this.host = host;
            this.port = port;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Address.class), Address.class, "host;port", "FIELD:Lorg/springframework/boot/autoconfigure/amqp/RabbitConnectionDetails$Address;->host:Ljava/lang/String;", "FIELD:Lorg/springframework/boot/autoconfigure/amqp/RabbitConnectionDetails$Address;->port:I").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Address.class), Address.class, "host;port", "FIELD:Lorg/springframework/boot/autoconfigure/amqp/RabbitConnectionDetails$Address;->host:Ljava/lang/String;", "FIELD:Lorg/springframework/boot/autoconfigure/amqp/RabbitConnectionDetails$Address;->port:I").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Address.class, Object.class), Address.class, "host;port", "FIELD:Lorg/springframework/boot/autoconfigure/amqp/RabbitConnectionDetails$Address;->host:Ljava/lang/String;", "FIELD:Lorg/springframework/boot/autoconfigure/amqp/RabbitConnectionDetails$Address;->port:I").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public String host() {
            return this.host;
        }

        public int port() {
            return this.port;
        }
    }
}

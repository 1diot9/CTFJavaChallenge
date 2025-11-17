package org.springframework.boot.autoconfigure.cassandra;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import org.springframework.boot.autoconfigure.service.connection.ConnectionDetails;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/cassandra/CassandraConnectionDetails.class */
public interface CassandraConnectionDetails extends ConnectionDetails {
    List<Node> getContactPoints();

    String getLocalDatacenter();

    default String getUsername() {
        return null;
    }

    default String getPassword() {
        return null;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/cassandra/CassandraConnectionDetails$Node.class */
    public static final class Node extends Record {
        private final String host;
        private final int port;

        public Node(String host, int port) {
            this.host = host;
            this.port = port;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Node.class), Node.class, "host;port", "FIELD:Lorg/springframework/boot/autoconfigure/cassandra/CassandraConnectionDetails$Node;->host:Ljava/lang/String;", "FIELD:Lorg/springframework/boot/autoconfigure/cassandra/CassandraConnectionDetails$Node;->port:I").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Node.class), Node.class, "host;port", "FIELD:Lorg/springframework/boot/autoconfigure/cassandra/CassandraConnectionDetails$Node;->host:Ljava/lang/String;", "FIELD:Lorg/springframework/boot/autoconfigure/cassandra/CassandraConnectionDetails$Node;->port:I").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Node.class, Object.class), Node.class, "host;port", "FIELD:Lorg/springframework/boot/autoconfigure/cassandra/CassandraConnectionDetails$Node;->host:Ljava/lang/String;", "FIELD:Lorg/springframework/boot/autoconfigure/cassandra/CassandraConnectionDetails$Node;->port:I").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public String host() {
            return this.host;
        }

        public int port() {
            return this.port;
        }
    }
}

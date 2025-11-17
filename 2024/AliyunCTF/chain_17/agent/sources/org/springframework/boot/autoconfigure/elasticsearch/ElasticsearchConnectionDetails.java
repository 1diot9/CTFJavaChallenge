package org.springframework.boot.autoconfigure.elasticsearch;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import org.springframework.boot.autoconfigure.service.connection.ConnectionDetails;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/elasticsearch/ElasticsearchConnectionDetails.class */
public interface ElasticsearchConnectionDetails extends ConnectionDetails {
    List<Node> getNodes();

    default String getUsername() {
        return null;
    }

    default String getPassword() {
        return null;
    }

    default String getPathPrefix() {
        return null;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/elasticsearch/ElasticsearchConnectionDetails$Node.class */
    public static final class Node extends Record {
        private final String hostname;
        private final int port;
        private final Protocol protocol;
        private final String username;
        private final String password;

        public Node(String hostname, int port, Protocol protocol, String username, String password) {
            this.hostname = hostname;
            this.port = port;
            this.protocol = protocol;
            this.username = username;
            this.password = password;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Node.class), Node.class, "hostname;port;protocol;username;password", "FIELD:Lorg/springframework/boot/autoconfigure/elasticsearch/ElasticsearchConnectionDetails$Node;->hostname:Ljava/lang/String;", "FIELD:Lorg/springframework/boot/autoconfigure/elasticsearch/ElasticsearchConnectionDetails$Node;->port:I", "FIELD:Lorg/springframework/boot/autoconfigure/elasticsearch/ElasticsearchConnectionDetails$Node;->protocol:Lorg/springframework/boot/autoconfigure/elasticsearch/ElasticsearchConnectionDetails$Node$Protocol;", "FIELD:Lorg/springframework/boot/autoconfigure/elasticsearch/ElasticsearchConnectionDetails$Node;->username:Ljava/lang/String;", "FIELD:Lorg/springframework/boot/autoconfigure/elasticsearch/ElasticsearchConnectionDetails$Node;->password:Ljava/lang/String;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Node.class), Node.class, "hostname;port;protocol;username;password", "FIELD:Lorg/springframework/boot/autoconfigure/elasticsearch/ElasticsearchConnectionDetails$Node;->hostname:Ljava/lang/String;", "FIELD:Lorg/springframework/boot/autoconfigure/elasticsearch/ElasticsearchConnectionDetails$Node;->port:I", "FIELD:Lorg/springframework/boot/autoconfigure/elasticsearch/ElasticsearchConnectionDetails$Node;->protocol:Lorg/springframework/boot/autoconfigure/elasticsearch/ElasticsearchConnectionDetails$Node$Protocol;", "FIELD:Lorg/springframework/boot/autoconfigure/elasticsearch/ElasticsearchConnectionDetails$Node;->username:Ljava/lang/String;", "FIELD:Lorg/springframework/boot/autoconfigure/elasticsearch/ElasticsearchConnectionDetails$Node;->password:Ljava/lang/String;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Node.class, Object.class), Node.class, "hostname;port;protocol;username;password", "FIELD:Lorg/springframework/boot/autoconfigure/elasticsearch/ElasticsearchConnectionDetails$Node;->hostname:Ljava/lang/String;", "FIELD:Lorg/springframework/boot/autoconfigure/elasticsearch/ElasticsearchConnectionDetails$Node;->port:I", "FIELD:Lorg/springframework/boot/autoconfigure/elasticsearch/ElasticsearchConnectionDetails$Node;->protocol:Lorg/springframework/boot/autoconfigure/elasticsearch/ElasticsearchConnectionDetails$Node$Protocol;", "FIELD:Lorg/springframework/boot/autoconfigure/elasticsearch/ElasticsearchConnectionDetails$Node;->username:Ljava/lang/String;", "FIELD:Lorg/springframework/boot/autoconfigure/elasticsearch/ElasticsearchConnectionDetails$Node;->password:Ljava/lang/String;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public String hostname() {
            return this.hostname;
        }

        public int port() {
            return this.port;
        }

        public Protocol protocol() {
            return this.protocol;
        }

        public String username() {
            return this.username;
        }

        public String password() {
            return this.password;
        }

        public Node(String host, int port, Protocol protocol) {
            this(host, port, protocol, null, null);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public URI toUri() {
            try {
                return new URI(this.protocol.getScheme(), userInfo(), this.hostname, this.port, null, null, null);
            } catch (URISyntaxException ex) {
                throw new IllegalStateException("Can't construct URI", ex);
            }
        }

        private String userInfo() {
            if (this.username == null) {
                return null;
            }
            return this.password != null ? this.username + ":" + this.password : this.username;
        }

        /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/elasticsearch/ElasticsearchConnectionDetails$Node$Protocol.class */
        public enum Protocol {
            HTTP("http"),
            HTTPS("https");

            private final String scheme;

            Protocol(String scheme) {
                this.scheme = scheme;
            }

            /* JADX INFO: Access modifiers changed from: package-private */
            public String getScheme() {
                return this.scheme;
            }

            /* JADX INFO: Access modifiers changed from: package-private */
            public static Protocol forScheme(String scheme) {
                for (Protocol protocol : values()) {
                    if (protocol.scheme.equals(scheme)) {
                        return protocol;
                    }
                }
                throw new IllegalArgumentException("Unknown scheme '" + scheme + "'");
            }
        }
    }
}

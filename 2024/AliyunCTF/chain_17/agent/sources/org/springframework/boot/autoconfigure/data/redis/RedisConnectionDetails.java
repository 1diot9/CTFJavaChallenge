package org.springframework.boot.autoconfigure.data.redis;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import org.springframework.boot.autoconfigure.service.connection.ConnectionDetails;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/redis/RedisConnectionDetails.class */
public interface RedisConnectionDetails extends ConnectionDetails {

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/redis/RedisConnectionDetails$Cluster.class */
    public interface Cluster {
        List<Node> getNodes();
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/redis/RedisConnectionDetails$Sentinel.class */
    public interface Sentinel {
        int getDatabase();

        String getMaster();

        List<Node> getNodes();

        String getUsername();

        String getPassword();
    }

    default String getUsername() {
        return null;
    }

    default String getPassword() {
        return null;
    }

    default Standalone getStandalone() {
        return null;
    }

    default Sentinel getSentinel() {
        return null;
    }

    default Cluster getCluster() {
        return null;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/redis/RedisConnectionDetails$Standalone.class */
    public interface Standalone {
        String getHost();

        int getPort();

        default int getDatabase() {
            return 0;
        }

        static Standalone of(String host, int port) {
            return of(host, port, 0);
        }

        static Standalone of(final String host, final int port, final int database) {
            Assert.hasLength(host, "Host must not be empty");
            return new Standalone() { // from class: org.springframework.boot.autoconfigure.data.redis.RedisConnectionDetails.Standalone.1
                @Override // org.springframework.boot.autoconfigure.data.redis.RedisConnectionDetails.Standalone
                public String getHost() {
                    return host;
                }

                @Override // org.springframework.boot.autoconfigure.data.redis.RedisConnectionDetails.Standalone
                public int getPort() {
                    return port;
                }

                @Override // org.springframework.boot.autoconfigure.data.redis.RedisConnectionDetails.Standalone
                public int getDatabase() {
                    return database;
                }
            };
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/redis/RedisConnectionDetails$Node.class */
    public static final class Node extends Record {
        private final String host;
        private final int port;

        public Node(String host, int port) {
            this.host = host;
            this.port = port;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Node.class), Node.class, "host;port", "FIELD:Lorg/springframework/boot/autoconfigure/data/redis/RedisConnectionDetails$Node;->host:Ljava/lang/String;", "FIELD:Lorg/springframework/boot/autoconfigure/data/redis/RedisConnectionDetails$Node;->port:I").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Node.class), Node.class, "host;port", "FIELD:Lorg/springframework/boot/autoconfigure/data/redis/RedisConnectionDetails$Node;->host:Ljava/lang/String;", "FIELD:Lorg/springframework/boot/autoconfigure/data/redis/RedisConnectionDetails$Node;->port:I").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Node.class, Object.class), Node.class, "host;port", "FIELD:Lorg/springframework/boot/autoconfigure/data/redis/RedisConnectionDetails$Node;->host:Ljava/lang/String;", "FIELD:Lorg/springframework/boot/autoconfigure/data/redis/RedisConnectionDetails$Node;->port:I").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public String host() {
            return this.host;
        }

        public int port() {
            return this.port;
        }
    }
}

package org.springframework.boot.autoconfigure.data.redis;

import java.util.List;
import java.util.stream.Stream;
import org.springframework.boot.autoconfigure.data.redis.RedisConnectionConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisConnectionDetails;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/redis/PropertiesRedisConnectionDetails.class */
class PropertiesRedisConnectionDetails implements RedisConnectionDetails {
    private final RedisProperties properties;

    /* JADX INFO: Access modifiers changed from: package-private */
    public PropertiesRedisConnectionDetails(RedisProperties properties) {
        this.properties = properties;
    }

    @Override // org.springframework.boot.autoconfigure.data.redis.RedisConnectionDetails
    public String getUsername() {
        if (this.properties.getUrl() != null) {
            RedisConnectionConfiguration.ConnectionInfo connectionInfo = connectionInfo(this.properties.getUrl());
            return connectionInfo.getUsername();
        }
        return this.properties.getUsername();
    }

    @Override // org.springframework.boot.autoconfigure.data.redis.RedisConnectionDetails
    public String getPassword() {
        if (this.properties.getUrl() != null) {
            RedisConnectionConfiguration.ConnectionInfo connectionInfo = connectionInfo(this.properties.getUrl());
            return connectionInfo.getPassword();
        }
        return this.properties.getPassword();
    }

    @Override // org.springframework.boot.autoconfigure.data.redis.RedisConnectionDetails
    public RedisConnectionDetails.Standalone getStandalone() {
        if (this.properties.getUrl() != null) {
            RedisConnectionConfiguration.ConnectionInfo connectionInfo = connectionInfo(this.properties.getUrl());
            return RedisConnectionDetails.Standalone.of(connectionInfo.getUri().getHost(), connectionInfo.getUri().getPort(), this.properties.getDatabase());
        }
        return RedisConnectionDetails.Standalone.of(this.properties.getHost(), this.properties.getPort(), this.properties.getDatabase());
    }

    private RedisConnectionConfiguration.ConnectionInfo connectionInfo(String url) {
        if (url != null) {
            return RedisConnectionConfiguration.parseUrl(url);
        }
        return null;
    }

    @Override // org.springframework.boot.autoconfigure.data.redis.RedisConnectionDetails
    public RedisConnectionDetails.Sentinel getSentinel() {
        final RedisProperties.Sentinel sentinel = this.properties.getSentinel();
        if (sentinel == null) {
            return null;
        }
        return new RedisConnectionDetails.Sentinel() { // from class: org.springframework.boot.autoconfigure.data.redis.PropertiesRedisConnectionDetails.1
            @Override // org.springframework.boot.autoconfigure.data.redis.RedisConnectionDetails.Sentinel
            public int getDatabase() {
                return PropertiesRedisConnectionDetails.this.properties.getDatabase();
            }

            @Override // org.springframework.boot.autoconfigure.data.redis.RedisConnectionDetails.Sentinel
            public String getMaster() {
                return sentinel.getMaster();
            }

            @Override // org.springframework.boot.autoconfigure.data.redis.RedisConnectionDetails.Sentinel
            public List<RedisConnectionDetails.Node> getNodes() {
                Stream<String> stream = sentinel.getNodes().stream();
                PropertiesRedisConnectionDetails propertiesRedisConnectionDetails = PropertiesRedisConnectionDetails.this;
                return stream.map(propertiesRedisConnectionDetails::asNode).toList();
            }

            @Override // org.springframework.boot.autoconfigure.data.redis.RedisConnectionDetails.Sentinel
            public String getUsername() {
                return sentinel.getUsername();
            }

            @Override // org.springframework.boot.autoconfigure.data.redis.RedisConnectionDetails.Sentinel
            public String getPassword() {
                return sentinel.getPassword();
            }
        };
    }

    @Override // org.springframework.boot.autoconfigure.data.redis.RedisConnectionDetails
    public RedisConnectionDetails.Cluster getCluster() {
        RedisProperties.Cluster cluster = this.properties.getCluster();
        List<RedisConnectionDetails.Node> nodes = cluster != null ? cluster.getNodes().stream().map(this::asNode).toList() : null;
        if (nodes != null) {
            return () -> {
                return nodes;
            };
        }
        return null;
    }

    private RedisConnectionDetails.Node asNode(String node) {
        String[] components = node.split(":");
        return new RedisConnectionDetails.Node(components[0], Integer.parseInt(components[1]));
    }
}

package org.springframework.boot.autoconfigure.data.redis;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.data.redis.RedisConnectionDetails;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.util.ClassUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/redis/RedisConnectionConfiguration.class */
abstract class RedisConnectionConfiguration {
    private static final boolean COMMONS_POOL2_AVAILABLE = ClassUtils.isPresent("org.apache.commons.pool2.ObjectPool", RedisConnectionConfiguration.class.getClassLoader());
    private final RedisProperties properties;
    private final RedisStandaloneConfiguration standaloneConfiguration;
    private final RedisSentinelConfiguration sentinelConfiguration;
    private final RedisClusterConfiguration clusterConfiguration;
    private final RedisConnectionDetails connectionDetails;
    private final SslBundles sslBundles;

    /* JADX INFO: Access modifiers changed from: protected */
    public RedisConnectionConfiguration(RedisProperties properties, RedisConnectionDetails connectionDetails, ObjectProvider<RedisStandaloneConfiguration> standaloneConfigurationProvider, ObjectProvider<RedisSentinelConfiguration> sentinelConfigurationProvider, ObjectProvider<RedisClusterConfiguration> clusterConfigurationProvider, ObjectProvider<SslBundles> sslBundles) {
        this.properties = properties;
        this.standaloneConfiguration = standaloneConfigurationProvider.getIfAvailable();
        this.sentinelConfiguration = sentinelConfigurationProvider.getIfAvailable();
        this.clusterConfiguration = clusterConfigurationProvider.getIfAvailable();
        this.connectionDetails = connectionDetails;
        this.sslBundles = sslBundles.getIfAvailable();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final RedisStandaloneConfiguration getStandaloneConfig() {
        if (this.standaloneConfiguration != null) {
            return this.standaloneConfiguration;
        }
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(this.connectionDetails.getStandalone().getHost());
        config.setPort(this.connectionDetails.getStandalone().getPort());
        config.setUsername(this.connectionDetails.getUsername());
        config.setPassword(RedisPassword.of(this.connectionDetails.getPassword()));
        config.setDatabase(this.connectionDetails.getStandalone().getDatabase());
        return config;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final RedisSentinelConfiguration getSentinelConfig() {
        if (this.sentinelConfiguration != null) {
            return this.sentinelConfiguration;
        }
        if (this.connectionDetails.getSentinel() != null) {
            RedisSentinelConfiguration config = new RedisSentinelConfiguration();
            config.master(this.connectionDetails.getSentinel().getMaster());
            config.setSentinels(createSentinels(this.connectionDetails.getSentinel()));
            config.setUsername(this.connectionDetails.getUsername());
            String password = this.connectionDetails.getPassword();
            if (password != null) {
                config.setPassword(RedisPassword.of(password));
            }
            config.setSentinelUsername(this.connectionDetails.getSentinel().getUsername());
            String sentinelPassword = this.connectionDetails.getSentinel().getPassword();
            if (sentinelPassword != null) {
                config.setSentinelPassword(RedisPassword.of(sentinelPassword));
            }
            config.setDatabase(this.connectionDetails.getSentinel().getDatabase());
            return config;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final RedisClusterConfiguration getClusterConfiguration() {
        if (this.clusterConfiguration != null) {
            return this.clusterConfiguration;
        }
        RedisProperties.Cluster clusterProperties = this.properties.getCluster();
        if (this.connectionDetails.getCluster() != null) {
            RedisClusterConfiguration config = new RedisClusterConfiguration(getNodes(this.connectionDetails.getCluster()));
            if (clusterProperties != null && clusterProperties.getMaxRedirects() != null) {
                config.setMaxRedirects(clusterProperties.getMaxRedirects().intValue());
            }
            config.setUsername(this.connectionDetails.getUsername());
            String password = this.connectionDetails.getPassword();
            if (password != null) {
                config.setPassword(RedisPassword.of(password));
            }
            return config;
        }
        return null;
    }

    private List<String> getNodes(RedisConnectionDetails.Cluster cluster) {
        return cluster.getNodes().stream().map(node -> {
            return "%s:%d".formatted(node.host(), Integer.valueOf(node.port()));
        }).toList();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final RedisProperties getProperties() {
        return this.properties;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public SslBundles getSslBundles() {
        return this.sslBundles;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isSslEnabled() {
        return getProperties().getSsl().isEnabled();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isPoolEnabled(RedisProperties.Pool pool) {
        Boolean enabled = pool.getEnabled();
        return enabled != null ? enabled.booleanValue() : COMMONS_POOL2_AVAILABLE;
    }

    private List<RedisNode> createSentinels(RedisConnectionDetails.Sentinel sentinel) {
        List<RedisNode> nodes = new ArrayList<>();
        for (RedisConnectionDetails.Node node : sentinel.getNodes()) {
            nodes.add(new RedisNode(node.host(), node.port()));
        }
        return nodes;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final boolean urlUsesSsl() {
        return parseUrl(this.properties.getUrl()).isUseSsl();
    }

    protected final RedisConnectionDetails getConnectionDetails() {
        return this.connectionDetails;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ConnectionInfo parseUrl(String url) {
        try {
            URI uri = new URI(url);
            String scheme = uri.getScheme();
            if (!"redis".equals(scheme) && !"rediss".equals(scheme)) {
                throw new RedisUrlSyntaxException(url);
            }
            boolean useSsl = "rediss".equals(scheme);
            String username = null;
            String password = null;
            if (uri.getUserInfo() != null) {
                String candidate = uri.getUserInfo();
                int index = candidate.indexOf(58);
                if (index >= 0) {
                    username = candidate.substring(0, index);
                    password = candidate.substring(index + 1);
                } else {
                    password = candidate;
                }
            }
            return new ConnectionInfo(uri, useSsl, username, password);
        } catch (URISyntaxException ex) {
            throw new RedisUrlSyntaxException(url, ex);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/redis/RedisConnectionConfiguration$ConnectionInfo.class */
    public static class ConnectionInfo {
        private final URI uri;
        private final boolean useSsl;
        private final String username;
        private final String password;

        ConnectionInfo(URI uri, boolean useSsl, String username, String password) {
            this.uri = uri;
            this.useSsl = useSsl;
            this.username = username;
            this.password = password;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public URI getUri() {
            return this.uri;
        }

        boolean isUseSsl() {
            return this.useSsl;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public String getUsername() {
            return this.username;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public String getPassword() {
            return this.password;
        }
    }
}

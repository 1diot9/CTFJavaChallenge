package org.springframework.boot.autoconfigure.elasticsearch;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("spring.elasticsearch")
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/elasticsearch/ElasticsearchProperties.class */
public class ElasticsearchProperties {
    private String username;
    private String password;
    private String pathPrefix;
    private List<String> uris = new ArrayList(Collections.singletonList("http://localhost:9200"));
    private Duration connectionTimeout = Duration.ofSeconds(1);
    private Duration socketTimeout = Duration.ofSeconds(30);
    private boolean socketKeepAlive = false;
    private final Restclient restclient = new Restclient();

    public List<String> getUris() {
        return this.uris;
    }

    public void setUris(List<String> uris) {
        this.uris = uris;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Duration getConnectionTimeout() {
        return this.connectionTimeout;
    }

    public void setConnectionTimeout(Duration connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public Duration getSocketTimeout() {
        return this.socketTimeout;
    }

    public void setSocketTimeout(Duration socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public boolean isSocketKeepAlive() {
        return this.socketKeepAlive;
    }

    public void setSocketKeepAlive(boolean socketKeepAlive) {
        this.socketKeepAlive = socketKeepAlive;
    }

    public String getPathPrefix() {
        return this.pathPrefix;
    }

    public void setPathPrefix(String pathPrefix) {
        this.pathPrefix = pathPrefix;
    }

    public Restclient getRestclient() {
        return this.restclient;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/elasticsearch/ElasticsearchProperties$Restclient.class */
    public static class Restclient {
        private final Sniffer sniffer = new Sniffer();
        private final Ssl ssl = new Ssl();

        public Sniffer getSniffer() {
            return this.sniffer;
        }

        public Ssl getSsl() {
            return this.ssl;
        }

        /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/elasticsearch/ElasticsearchProperties$Restclient$Sniffer.class */
        public static class Sniffer {
            private Duration interval = Duration.ofMinutes(5);
            private Duration delayAfterFailure = Duration.ofMinutes(1);

            public Duration getInterval() {
                return this.interval;
            }

            public void setInterval(Duration interval) {
                this.interval = interval;
            }

            public Duration getDelayAfterFailure() {
                return this.delayAfterFailure;
            }

            public void setDelayAfterFailure(Duration delayAfterFailure) {
                this.delayAfterFailure = delayAfterFailure;
            }
        }

        /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/elasticsearch/ElasticsearchProperties$Restclient$Ssl.class */
        public static class Ssl {
            private String bundle;

            public String getBundle() {
                return this.bundle;
            }

            public void setBundle(String bundle) {
                this.bundle = bundle;
            }
        }
    }
}

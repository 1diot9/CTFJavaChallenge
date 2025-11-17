package org.springframework.boot.autoconfigure.elasticsearch;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.sniff.Sniffer;
import org.elasticsearch.client.sniff.SnifferBuilder;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchConnectionDetails;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.ssl.SslOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/elasticsearch/ElasticsearchRestClientConfigurations.class */
class ElasticsearchRestClientConfigurations {
    ElasticsearchRestClientConfigurations() {
    }

    @ConditionalOnMissingBean({RestClientBuilder.class})
    @Configuration(proxyBeanMethods = false)
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/elasticsearch/ElasticsearchRestClientConfigurations$RestClientBuilderConfiguration.class */
    static class RestClientBuilderConfiguration {
        private final ElasticsearchProperties properties;

        RestClientBuilderConfiguration(ElasticsearchProperties properties) {
            this.properties = properties;
        }

        @ConditionalOnMissingBean({ElasticsearchConnectionDetails.class})
        @Bean
        PropertiesElasticsearchConnectionDetails elasticsearchConnectionDetails() {
            return new PropertiesElasticsearchConnectionDetails(this.properties);
        }

        @Bean
        RestClientBuilderCustomizer defaultRestClientBuilderCustomizer(ElasticsearchConnectionDetails connectionDetails) {
            return new DefaultRestClientBuilderCustomizer(this.properties, connectionDetails);
        }

        @Bean
        RestClientBuilder elasticsearchRestClientBuilder(ElasticsearchConnectionDetails connectionDetails, ObjectProvider<RestClientBuilderCustomizer> builderCustomizers, ObjectProvider<SslBundles> sslBundles) {
            RestClientBuilder builder = RestClient.builder((HttpHost[]) connectionDetails.getNodes().stream().map(node -> {
                return new HttpHost(node.hostname(), node.port(), node.protocol().getScheme());
            }).toArray(x$0 -> {
                return new HttpHost[x$0];
            }));
            builder.setHttpClientConfigCallback(httpClientBuilder -> {
                builderCustomizers.orderedStream().forEach(customizer -> {
                    customizer.customize(httpClientBuilder);
                });
                String sslBundleName = this.properties.getRestclient().getSsl().getBundle();
                if (StringUtils.hasText(sslBundleName)) {
                    configureSsl(httpClientBuilder, ((SslBundles) sslBundles.getObject()).getBundle(sslBundleName));
                }
                return httpClientBuilder;
            });
            builder.setRequestConfigCallback(requestConfigBuilder -> {
                builderCustomizers.orderedStream().forEach(customizer -> {
                    customizer.customize(requestConfigBuilder);
                });
                return requestConfigBuilder;
            });
            String pathPrefix = connectionDetails.getPathPrefix();
            if (pathPrefix != null) {
                builder.setPathPrefix(pathPrefix);
            }
            builderCustomizers.orderedStream().forEach(customizer -> {
                customizer.customize(builder);
            });
            return builder;
        }

        private void configureSsl(HttpAsyncClientBuilder httpClientBuilder, SslBundle sslBundle) {
            SSLContext sslcontext = sslBundle.createSslContext();
            SslOptions sslOptions = sslBundle.getOptions();
            httpClientBuilder.setSSLStrategy(new SSLIOSessionStrategy(sslcontext, sslOptions.getEnabledProtocols(), sslOptions.getCiphers(), (HostnameVerifier) null));
        }
    }

    @ConditionalOnMissingBean({RestClient.class})
    @Configuration(proxyBeanMethods = false)
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/elasticsearch/ElasticsearchRestClientConfigurations$RestClientConfiguration.class */
    static class RestClientConfiguration {
        RestClientConfiguration() {
        }

        @Bean
        RestClient elasticsearchRestClient(RestClientBuilder restClientBuilder) {
            return restClientBuilder.build();
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({Sniffer.class})
    @ConditionalOnSingleCandidate(RestClient.class)
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/elasticsearch/ElasticsearchRestClientConfigurations$RestClientSnifferConfiguration.class */
    static class RestClientSnifferConfiguration {
        RestClientSnifferConfiguration() {
        }

        @ConditionalOnMissingBean
        @Bean
        Sniffer elasticsearchSniffer(RestClient client, ElasticsearchProperties properties) {
            SnifferBuilder builder = Sniffer.builder(client);
            PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            Duration interval = properties.getRestclient().getSniffer().getInterval();
            PropertyMapper.Source<Integer> asInt = map.from((PropertyMapper) interval).asInt((v0) -> {
                return v0.toMillis();
            });
            Objects.requireNonNull(builder);
            asInt.to((v1) -> {
                r1.setSniffIntervalMillis(v1);
            });
            Duration delayAfterFailure = properties.getRestclient().getSniffer().getDelayAfterFailure();
            PropertyMapper.Source<Integer> asInt2 = map.from((PropertyMapper) delayAfterFailure).asInt((v0) -> {
                return v0.toMillis();
            });
            Objects.requireNonNull(builder);
            asInt2.to((v1) -> {
                r1.setSniffAfterFailureDelayMillis(v1);
            });
            return builder.build();
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/elasticsearch/ElasticsearchRestClientConfigurations$DefaultRestClientBuilderCustomizer.class */
    static class DefaultRestClientBuilderCustomizer implements RestClientBuilderCustomizer {
        private static final PropertyMapper map = PropertyMapper.get();
        private final ElasticsearchProperties properties;
        private final ElasticsearchConnectionDetails connectionDetails;

        DefaultRestClientBuilderCustomizer(ElasticsearchProperties properties, ElasticsearchConnectionDetails connectionDetails) {
            this.properties = properties;
            this.connectionDetails = connectionDetails;
        }

        @Override // org.springframework.boot.autoconfigure.elasticsearch.RestClientBuilderCustomizer
        public void customize(RestClientBuilder builder) {
        }

        @Override // org.springframework.boot.autoconfigure.elasticsearch.RestClientBuilderCustomizer
        public void customize(HttpAsyncClientBuilder builder) {
            builder.setDefaultCredentialsProvider(new ConnectionDetailsCredentialsProvider(this.connectionDetails));
            PropertyMapper propertyMapper = map;
            ElasticsearchProperties elasticsearchProperties = this.properties;
            Objects.requireNonNull(elasticsearchProperties);
            propertyMapper.from(elasticsearchProperties::isSocketKeepAlive).to(keepAlive -> {
                builder.setDefaultIOReactorConfig(IOReactorConfig.custom().setSoKeepAlive(keepAlive.booleanValue()).build());
            });
        }

        @Override // org.springframework.boot.autoconfigure.elasticsearch.RestClientBuilderCustomizer
        public void customize(RequestConfig.Builder builder) {
            PropertyMapper propertyMapper = map;
            ElasticsearchProperties elasticsearchProperties = this.properties;
            Objects.requireNonNull(elasticsearchProperties);
            PropertyMapper.Source<Integer> asInt = propertyMapper.from(elasticsearchProperties::getConnectionTimeout).whenNonNull().asInt((v0) -> {
                return v0.toMillis();
            });
            Objects.requireNonNull(builder);
            asInt.to((v1) -> {
                r1.setConnectTimeout(v1);
            });
            PropertyMapper propertyMapper2 = map;
            ElasticsearchProperties elasticsearchProperties2 = this.properties;
            Objects.requireNonNull(elasticsearchProperties2);
            PropertyMapper.Source<Integer> asInt2 = propertyMapper2.from(elasticsearchProperties2::getSocketTimeout).whenNonNull().asInt((v0) -> {
                return v0.toMillis();
            });
            Objects.requireNonNull(builder);
            asInt2.to((v1) -> {
                r1.setSocketTimeout(v1);
            });
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/elasticsearch/ElasticsearchRestClientConfigurations$ConnectionDetailsCredentialsProvider.class */
    private static class ConnectionDetailsCredentialsProvider extends BasicCredentialsProvider {
        ConnectionDetailsCredentialsProvider(ElasticsearchConnectionDetails connectionDetails) {
            String username = connectionDetails.getUsername();
            if (StringUtils.hasText(username)) {
                Credentials credentials = new UsernamePasswordCredentials(username, connectionDetails.getPassword());
                setCredentials(AuthScope.ANY, credentials);
            }
            Stream<URI> uris = getUris(connectionDetails);
            uris.filter(this::hasUserInfo).forEach(this::addUserInfoCredentials);
        }

        private Stream<URI> getUris(ElasticsearchConnectionDetails connectionDetails) {
            return connectionDetails.getNodes().stream().map((v0) -> {
                return v0.toUri();
            });
        }

        private boolean hasUserInfo(URI uri) {
            return uri != null && StringUtils.hasLength(uri.getUserInfo());
        }

        private void addUserInfoCredentials(URI uri) {
            AuthScope authScope = new AuthScope(uri.getHost(), uri.getPort());
            Credentials credentials = createUserInfoCredentials(uri.getUserInfo());
            setCredentials(authScope, credentials);
        }

        private Credentials createUserInfoCredentials(String userInfo) {
            int delimiter = userInfo.indexOf(":");
            if (delimiter == -1) {
                return new UsernamePasswordCredentials(userInfo, null);
            }
            String username = userInfo.substring(0, delimiter);
            String password = userInfo.substring(delimiter + 1);
            return new UsernamePasswordCredentials(username, password);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/elasticsearch/ElasticsearchRestClientConfigurations$PropertiesElasticsearchConnectionDetails.class */
    static class PropertiesElasticsearchConnectionDetails implements ElasticsearchConnectionDetails {
        private final ElasticsearchProperties properties;

        PropertiesElasticsearchConnectionDetails(ElasticsearchProperties properties) {
            this.properties = properties;
        }

        @Override // org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchConnectionDetails
        public List<ElasticsearchConnectionDetails.Node> getNodes() {
            return this.properties.getUris().stream().map(this::createNode).toList();
        }

        @Override // org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchConnectionDetails
        public String getUsername() {
            return this.properties.getUsername();
        }

        @Override // org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchConnectionDetails
        public String getPassword() {
            return this.properties.getPassword();
        }

        @Override // org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchConnectionDetails
        public String getPathPrefix() {
            return this.properties.getPathPrefix();
        }

        private ElasticsearchConnectionDetails.Node createNode(String uri) {
            if (!uri.startsWith("http://") && !uri.startsWith("https://")) {
                uri = "http://" + uri;
            }
            return createNode(URI.create(uri));
        }

        private ElasticsearchConnectionDetails.Node createNode(URI uri) {
            String userInfo = uri.getUserInfo();
            ElasticsearchConnectionDetails.Node.Protocol protocol = ElasticsearchConnectionDetails.Node.Protocol.forScheme(uri.getScheme());
            if (!StringUtils.hasLength(userInfo)) {
                return new ElasticsearchConnectionDetails.Node(uri.getHost(), uri.getPort(), protocol, null, null);
            }
            int separatorIndex = userInfo.indexOf(58);
            if (separatorIndex == -1) {
                return new ElasticsearchConnectionDetails.Node(uri.getHost(), uri.getPort(), protocol, userInfo, null);
            }
            String[] components = userInfo.split(":");
            return new ElasticsearchConnectionDetails.Node(uri.getHost(), uri.getPort(), protocol, components[0], components.length > 1 ? components[1] : "");
        }
    }
}

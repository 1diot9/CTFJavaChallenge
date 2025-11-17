package org.springframework.boot.autoconfigure.web.reactive.function.client;

import io.netty.handler.ssl.SslContextBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.net.ssl.SSLException;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslManagerBundle;
import org.springframework.boot.ssl.SslOptions;
import org.springframework.http.client.ReactorResourceFactory;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.function.ThrowingConsumer;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.SslProvider;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/reactive/function/client/ReactorClientHttpConnectorFactory.class */
class ReactorClientHttpConnectorFactory implements ClientHttpConnectorFactory<ReactorClientHttpConnector> {
    private final ReactorResourceFactory reactorResourceFactory;
    private final Supplier<Stream<ReactorNettyHttpClientMapper>> mappers;

    ReactorClientHttpConnectorFactory(ReactorResourceFactory reactorResourceFactory) {
        this(reactorResourceFactory, Stream::empty);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ReactorClientHttpConnectorFactory(ReactorResourceFactory reactorResourceFactory, Supplier<Stream<ReactorNettyHttpClientMapper>> mappers) {
        this.reactorResourceFactory = reactorResourceFactory;
        this.mappers = mappers;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.boot.autoconfigure.web.reactive.function.client.ClientHttpConnectorFactory
    public ReactorClientHttpConnector createClientHttpConnector(SslBundle sslBundle) {
        List<ReactorNettyHttpClientMapper> mappers = (List) this.mappers.get().collect(Collectors.toCollection(ArrayList::new));
        if (sslBundle != null) {
            mappers.add(new SslConfigurer(sslBundle));
        }
        ReactorResourceFactory reactorResourceFactory = this.reactorResourceFactory;
        ReactorNettyHttpClientMapper of = ReactorNettyHttpClientMapper.of(mappers);
        Objects.requireNonNull(of);
        return new ReactorClientHttpConnector(reactorResourceFactory, of::configure);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/reactive/function/client/ReactorClientHttpConnectorFactory$SslConfigurer.class */
    public static class SslConfigurer implements ReactorNettyHttpClientMapper {
        private final SslBundle sslBundle;

        SslConfigurer(SslBundle sslBundle) {
            this.sslBundle = sslBundle;
        }

        @Override // org.springframework.boot.autoconfigure.web.reactive.function.client.ReactorNettyHttpClientMapper
        public HttpClient configure(HttpClient httpClient) {
            return httpClient.secure(ThrowingConsumer.of(this::customizeSsl).throwing((v1, v2) -> {
                return new IllegalStateException(v1, v2);
            }));
        }

        private void customizeSsl(SslProvider.SslContextSpec spec) throws SSLException {
            SslOptions options = this.sslBundle.getOptions();
            SslManagerBundle managers = this.sslBundle.getManagers();
            SslContextBuilder builder = SslContextBuilder.forClient().keyManager(managers.getKeyManagerFactory()).trustManager(managers.getTrustManagerFactory()).ciphers(SslOptions.asSet(options.getCiphers())).protocols(options.getEnabledProtocols());
            spec.sslContext(builder.build());
        }
    }
}

package org.springframework.boot.web.client;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import okhttp3.OkHttpClient;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.DefaultHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.io.SocketConfig;
import org.eclipse.jetty.client.transport.HttpClientTransportDynamic;
import org.eclipse.jetty.io.ClientConnectionFactory;
import org.eclipse.jetty.io.ClientConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslOptions;
import org.springframework.http.client.AbstractClientHttpRequestFactoryWrapper;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.http.client.JettyClientHttpRequestFactory;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/client/ClientHttpRequestFactories.class */
public final class ClientHttpRequestFactories {
    static final String APACHE_HTTP_CLIENT_CLASS = "org.apache.hc.client5.http.impl.classic.HttpClients";
    private static final boolean APACHE_HTTP_CLIENT_PRESENT = ClassUtils.isPresent(APACHE_HTTP_CLIENT_CLASS, null);
    static final String OKHTTP_CLIENT_CLASS = "okhttp3.OkHttpClient";
    private static final boolean OKHTTP_CLIENT_PRESENT = ClassUtils.isPresent(OKHTTP_CLIENT_CLASS, null);
    static final String JETTY_CLIENT_CLASS = "org.eclipse.jetty.client.HttpClient";
    private static final boolean JETTY_CLIENT_PRESENT = ClassUtils.isPresent(JETTY_CLIENT_CLASS, null);

    private ClientHttpRequestFactories() {
    }

    public static ClientHttpRequestFactory get(ClientHttpRequestFactorySettings settings) {
        Assert.notNull(settings, "Settings must not be null");
        if (APACHE_HTTP_CLIENT_PRESENT) {
            return HttpComponents.get(settings);
        }
        if (JETTY_CLIENT_PRESENT) {
            return Jetty.get(settings);
        }
        if (OKHTTP_CLIENT_PRESENT) {
            return OkHttp.get(settings);
        }
        return Simple.get(settings);
    }

    public static <T extends ClientHttpRequestFactory> T get(Class<T> cls, ClientHttpRequestFactorySettings clientHttpRequestFactorySettings) {
        Assert.notNull(clientHttpRequestFactorySettings, "Settings must not be null");
        if (cls == ClientHttpRequestFactory.class) {
            return (T) get(clientHttpRequestFactorySettings);
        }
        if (cls == HttpComponentsClientHttpRequestFactory.class) {
            return HttpComponents.get(clientHttpRequestFactorySettings);
        }
        if (cls == JettyClientHttpRequestFactory.class) {
            return Jetty.get(clientHttpRequestFactorySettings);
        }
        if (cls == JdkClientHttpRequestFactory.class) {
            return Jdk.get(clientHttpRequestFactorySettings);
        }
        if (cls == SimpleClientHttpRequestFactory.class) {
            return Simple.get(clientHttpRequestFactorySettings);
        }
        if (cls == OkHttp3ClientHttpRequestFactory.class) {
            return OkHttp.get(clientHttpRequestFactorySettings);
        }
        return (T) get(() -> {
            return createRequestFactory(cls);
        }, clientHttpRequestFactorySettings);
    }

    public static <T extends ClientHttpRequestFactory> T get(Supplier<T> supplier, ClientHttpRequestFactorySettings clientHttpRequestFactorySettings) {
        return (T) Reflective.get(supplier, clientHttpRequestFactorySettings);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static <T extends ClientHttpRequestFactory> T createRequestFactory(Class<T> requestFactory) {
        try {
            Constructor<T> constructor = requestFactory.getDeclaredConstructor(new Class[0]);
            constructor.setAccessible(true);
            return constructor.newInstance(new Object[0]);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/client/ClientHttpRequestFactories$HttpComponents.class */
    public static class HttpComponents {
        HttpComponents() {
        }

        static HttpComponentsClientHttpRequestFactory get(ClientHttpRequestFactorySettings settings) {
            HttpComponentsClientHttpRequestFactory requestFactory = createRequestFactory(settings.readTimeout(), settings.sslBundle());
            PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            Objects.requireNonNull(settings);
            PropertyMapper.Source<Integer> asInt = map.from(settings::connectTimeout).asInt((v0) -> {
                return v0.toMillis();
            });
            Objects.requireNonNull(requestFactory);
            asInt.to((v1) -> {
                r1.setConnectTimeout(v1);
            });
            return requestFactory;
        }

        private static HttpComponentsClientHttpRequestFactory createRequestFactory(Duration readTimeout, SslBundle sslBundle) {
            return new HttpComponentsClientHttpRequestFactory(createHttpClient(readTimeout, sslBundle));
        }

        private static HttpClient createHttpClient(Duration readTimeout, SslBundle sslBundle) {
            PoolingHttpClientConnectionManagerBuilder connectionManagerBuilder = PoolingHttpClientConnectionManagerBuilder.create();
            if (readTimeout != null) {
                SocketConfig socketConfig = SocketConfig.custom().setSoTimeout((int) readTimeout.toMillis(), TimeUnit.MILLISECONDS).build();
                connectionManagerBuilder.setDefaultSocketConfig(socketConfig);
            }
            if (sslBundle != null) {
                SslOptions options = sslBundle.getOptions();
                SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslBundle.createSslContext(), options.getEnabledProtocols(), options.getCiphers(), new DefaultHostnameVerifier());
                connectionManagerBuilder.setSSLSocketFactory(socketFactory);
            }
            PoolingHttpClientConnectionManager connectionManager = connectionManagerBuilder.useSystemProperties().build();
            return HttpClientBuilder.create().useSystemProperties().setConnectionManager(connectionManager).build();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Deprecated(since = "3.2.0", forRemoval = true)
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/client/ClientHttpRequestFactories$OkHttp.class */
    public static class OkHttp {
        OkHttp() {
        }

        static OkHttp3ClientHttpRequestFactory get(ClientHttpRequestFactorySettings settings) {
            OkHttp3ClientHttpRequestFactory requestFactory = createRequestFactory(settings.sslBundle());
            PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            Objects.requireNonNull(settings);
            PropertyMapper.Source<Integer> asInt = map.from(settings::connectTimeout).asInt((v0) -> {
                return v0.toMillis();
            });
            Objects.requireNonNull(requestFactory);
            asInt.to((v1) -> {
                r1.setConnectTimeout(v1);
            });
            Objects.requireNonNull(settings);
            PropertyMapper.Source<Integer> asInt2 = map.from(settings::readTimeout).asInt((v0) -> {
                return v0.toMillis();
            });
            Objects.requireNonNull(requestFactory);
            asInt2.to((v1) -> {
                r1.setReadTimeout(v1);
            });
            return requestFactory;
        }

        private static OkHttp3ClientHttpRequestFactory createRequestFactory(SslBundle sslBundle) {
            if (sslBundle != null) {
                Assert.state(!sslBundle.getOptions().isSpecified(), "SSL Options cannot be specified with OkHttp");
                SSLSocketFactory socketFactory = sslBundle.createSslContext().getSocketFactory();
                TrustManager[] trustManagers = sslBundle.getManagers().getTrustManagers();
                Assert.state(trustManagers.length == 1, "Trust material must be provided in the SSL bundle for OkHttp3ClientHttpRequestFactory");
                OkHttpClient client = new OkHttpClient.Builder().sslSocketFactory(socketFactory, (X509TrustManager) trustManagers[0]).build();
                return new OkHttp3ClientHttpRequestFactory(client);
            }
            return new OkHttp3ClientHttpRequestFactory();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/client/ClientHttpRequestFactories$Jetty.class */
    public static class Jetty {
        Jetty() {
        }

        static JettyClientHttpRequestFactory get(ClientHttpRequestFactorySettings settings) {
            JettyClientHttpRequestFactory requestFactory = createRequestFactory(settings.sslBundle());
            PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            Objects.requireNonNull(settings);
            PropertyMapper.Source<Integer> asInt = map.from(settings::connectTimeout).asInt((v0) -> {
                return v0.toMillis();
            });
            Objects.requireNonNull(requestFactory);
            asInt.to((v1) -> {
                r1.setConnectTimeout(v1);
            });
            Objects.requireNonNull(settings);
            PropertyMapper.Source<Integer> asInt2 = map.from(settings::readTimeout).asInt((v0) -> {
                return v0.toMillis();
            });
            Objects.requireNonNull(requestFactory);
            asInt2.to((v1) -> {
                r1.setReadTimeout(v1);
            });
            return requestFactory;
        }

        private static JettyClientHttpRequestFactory createRequestFactory(SslBundle sslBundle) {
            if (sslBundle != null) {
                SSLContext sslContext = sslBundle.createSslContext();
                SslContextFactory.Client sslContextFactory = new SslContextFactory.Client();
                sslContextFactory.setSslContext(sslContext);
                ClientConnector connector = new ClientConnector();
                connector.setSslContextFactory(sslContextFactory);
                org.eclipse.jetty.client.HttpClient httpClient = new org.eclipse.jetty.client.HttpClient(new HttpClientTransportDynamic(connector, new ClientConnectionFactory.Info[0]));
                return new JettyClientHttpRequestFactory(httpClient);
            }
            return new JettyClientHttpRequestFactory();
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/client/ClientHttpRequestFactories$Jdk.class */
    static class Jdk {
        Jdk() {
        }

        static JdkClientHttpRequestFactory get(ClientHttpRequestFactorySettings settings) {
            java.net.http.HttpClient httpClient = createHttpClient(settings.connectTimeout(), settings.sslBundle());
            JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
            PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            Objects.requireNonNull(settings);
            PropertyMapper.Source from = map.from(settings::readTimeout);
            Objects.requireNonNull(requestFactory);
            from.to(requestFactory::setReadTimeout);
            return requestFactory;
        }

        private static java.net.http.HttpClient createHttpClient(Duration connectTimeout, SslBundle sslBundle) {
            HttpClient.Builder builder = java.net.http.HttpClient.newBuilder();
            if (connectTimeout != null) {
                builder.connectTimeout(connectTimeout);
            }
            if (sslBundle != null) {
                builder.sslContext(sslBundle.createSslContext());
            }
            return builder.build();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/client/ClientHttpRequestFactories$Simple.class */
    public static class Simple {
        Simple() {
        }

        static SimpleClientHttpRequestFactory get(ClientHttpRequestFactorySettings settings) {
            SslBundle sslBundle = settings.sslBundle();
            SimpleClientHttpRequestFactory requestFactory = sslBundle != null ? new SimpleClientHttpsRequestFactory(sslBundle) : new SimpleClientHttpRequestFactory();
            Assert.state(sslBundle == null || !sslBundle.getOptions().isSpecified(), "SSL Options cannot be specified with Java connections");
            PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            Objects.requireNonNull(settings);
            PropertyMapper.Source<Integer> asInt = map.from(settings::readTimeout).asInt((v0) -> {
                return v0.toMillis();
            });
            Objects.requireNonNull(requestFactory);
            asInt.to((v1) -> {
                r1.setReadTimeout(v1);
            });
            Objects.requireNonNull(settings);
            PropertyMapper.Source<Integer> asInt2 = map.from(settings::connectTimeout).asInt((v0) -> {
                return v0.toMillis();
            });
            Objects.requireNonNull(requestFactory);
            asInt2.to((v1) -> {
                r1.setConnectTimeout(v1);
            });
            return requestFactory;
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/client/ClientHttpRequestFactories$Simple$SimpleClientHttpsRequestFactory.class */
        public static class SimpleClientHttpsRequestFactory extends SimpleClientHttpRequestFactory {
            private SslBundle sslBundle;

            SimpleClientHttpsRequestFactory(SslBundle sslBundle) {
                this.sslBundle = sslBundle;
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // org.springframework.http.client.SimpleClientHttpRequestFactory
            public void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
                super.prepareConnection(connection, httpMethod);
                if (this.sslBundle != null && (connection instanceof HttpsURLConnection)) {
                    HttpsURLConnection secureConnection = (HttpsURLConnection) connection;
                    SSLSocketFactory socketFactory = this.sslBundle.createSslContext().getSocketFactory();
                    secureConnection.setSSLSocketFactory(socketFactory);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/client/ClientHttpRequestFactories$Reflective.class */
    public static class Reflective {
        Reflective() {
        }

        static <T extends ClientHttpRequestFactory> T get(Supplier<T> requestFactorySupplier, ClientHttpRequestFactorySettings settings) {
            T requestFactory = requestFactorySupplier.get();
            configure(requestFactory, settings);
            return requestFactory;
        }

        private static void configure(ClientHttpRequestFactory requestFactory, ClientHttpRequestFactorySettings settings) {
            ClientHttpRequestFactory unwrapped = unwrapRequestFactoryIfNecessary(requestFactory);
            PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            Objects.requireNonNull(settings);
            map.from(settings::connectTimeout).to(connectTimeout -> {
                setConnectTimeout(unwrapped, connectTimeout);
            });
            Objects.requireNonNull(settings);
            map.from(settings::readTimeout).to(readTimeout -> {
                setReadTimeout(unwrapped, readTimeout);
            });
        }

        private static ClientHttpRequestFactory unwrapRequestFactoryIfNecessary(ClientHttpRequestFactory requestFactory) {
            if (!(requestFactory instanceof AbstractClientHttpRequestFactoryWrapper)) {
                return requestFactory;
            }
            Field field = ReflectionUtils.findField(AbstractClientHttpRequestFactoryWrapper.class, "requestFactory");
            ReflectionUtils.makeAccessible(field);
            ClientHttpRequestFactory clientHttpRequestFactory = requestFactory;
            while (true) {
                ClientHttpRequestFactory unwrappedRequestFactory = clientHttpRequestFactory;
                if (unwrappedRequestFactory instanceof AbstractClientHttpRequestFactoryWrapper) {
                    clientHttpRequestFactory = (ClientHttpRequestFactory) ReflectionUtils.getField(field, unwrappedRequestFactory);
                } else {
                    return unwrappedRequestFactory;
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static void setConnectTimeout(ClientHttpRequestFactory factory, Duration connectTimeout) {
            Method method = findMethod(factory, "setConnectTimeout", Integer.TYPE);
            int timeout = Math.toIntExact(connectTimeout.toMillis());
            invoke(factory, method, Integer.valueOf(timeout));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static void setReadTimeout(ClientHttpRequestFactory factory, Duration readTimeout) {
            Method method = findMethod(factory, "setReadTimeout", Integer.TYPE);
            int timeout = Math.toIntExact(readTimeout.toMillis());
            invoke(factory, method, Integer.valueOf(timeout));
        }

        private static Method findMethod(ClientHttpRequestFactory requestFactory, String methodName, Class<?>... parameters) {
            Method method = ReflectionUtils.findMethod(requestFactory.getClass(), methodName, parameters);
            Assert.state(method != null, (Supplier<String>) () -> {
                return "Request factory %s does not have a suitable %s method".formatted(requestFactory.getClass().getName(), methodName);
            });
            Assert.state(!method.isAnnotationPresent(Deprecated.class), (Supplier<String>) () -> {
                return "Request factory %s has the %s method marked as deprecated".formatted(requestFactory.getClass().getName(), methodName);
            });
            return method;
        }

        private static void invoke(ClientHttpRequestFactory requestFactory, Method method, Object... parameters) {
            ReflectionUtils.invokeMethod(method, requestFactory, parameters);
        }
    }
}

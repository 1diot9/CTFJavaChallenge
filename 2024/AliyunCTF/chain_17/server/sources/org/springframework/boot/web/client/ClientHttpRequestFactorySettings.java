package org.springframework.boot.web.client;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.time.Duration;
import org.springframework.boot.ssl.SslBundle;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/client/ClientHttpRequestFactorySettings.class */
public final class ClientHttpRequestFactorySettings extends Record {
    private final Duration connectTimeout;
    private final Duration readTimeout;
    private final SslBundle sslBundle;
    public static final ClientHttpRequestFactorySettings DEFAULTS = new ClientHttpRequestFactorySettings(null, null, null, null);

    public ClientHttpRequestFactorySettings(Duration connectTimeout, Duration readTimeout, SslBundle sslBundle) {
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
        this.sslBundle = sslBundle;
    }

    @Override // java.lang.Record
    public final String toString() {
        return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, ClientHttpRequestFactorySettings.class), ClientHttpRequestFactorySettings.class, "connectTimeout;readTimeout;sslBundle", "FIELD:Lorg/springframework/boot/web/client/ClientHttpRequestFactorySettings;->connectTimeout:Ljava/time/Duration;", "FIELD:Lorg/springframework/boot/web/client/ClientHttpRequestFactorySettings;->readTimeout:Ljava/time/Duration;", "FIELD:Lorg/springframework/boot/web/client/ClientHttpRequestFactorySettings;->sslBundle:Lorg/springframework/boot/ssl/SslBundle;").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final int hashCode() {
        return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, ClientHttpRequestFactorySettings.class), ClientHttpRequestFactorySettings.class, "connectTimeout;readTimeout;sslBundle", "FIELD:Lorg/springframework/boot/web/client/ClientHttpRequestFactorySettings;->connectTimeout:Ljava/time/Duration;", "FIELD:Lorg/springframework/boot/web/client/ClientHttpRequestFactorySettings;->readTimeout:Ljava/time/Duration;", "FIELD:Lorg/springframework/boot/web/client/ClientHttpRequestFactorySettings;->sslBundle:Lorg/springframework/boot/ssl/SslBundle;").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final boolean equals(Object o) {
        return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, ClientHttpRequestFactorySettings.class, Object.class), ClientHttpRequestFactorySettings.class, "connectTimeout;readTimeout;sslBundle", "FIELD:Lorg/springframework/boot/web/client/ClientHttpRequestFactorySettings;->connectTimeout:Ljava/time/Duration;", "FIELD:Lorg/springframework/boot/web/client/ClientHttpRequestFactorySettings;->readTimeout:Ljava/time/Duration;", "FIELD:Lorg/springframework/boot/web/client/ClientHttpRequestFactorySettings;->sslBundle:Lorg/springframework/boot/ssl/SslBundle;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
    }

    public Duration connectTimeout() {
        return this.connectTimeout;
    }

    public Duration readTimeout() {
        return this.readTimeout;
    }

    public SslBundle sslBundle() {
        return this.sslBundle;
    }

    @Deprecated(since = "3.2.0", forRemoval = true)
    public ClientHttpRequestFactorySettings(Duration connectTimeout, Duration readTimeout, Boolean bufferRequestBody) {
        this(connectTimeout, readTimeout, (SslBundle) null);
    }

    @Deprecated(since = "3.2.0", forRemoval = true)
    public ClientHttpRequestFactorySettings(Duration connectTimeout, Duration readTimeout, Boolean bufferRequestBody, SslBundle sslBundle) {
        this(connectTimeout, readTimeout, sslBundle);
    }

    public ClientHttpRequestFactorySettings withConnectTimeout(Duration connectTimeout) {
        return new ClientHttpRequestFactorySettings(connectTimeout, this.readTimeout, this.sslBundle);
    }

    public ClientHttpRequestFactorySettings withReadTimeout(Duration readTimeout) {
        return new ClientHttpRequestFactorySettings(this.connectTimeout, readTimeout, this.sslBundle);
    }

    @Deprecated(since = "3.2.0", forRemoval = true)
    public ClientHttpRequestFactorySettings withBufferRequestBody(Boolean bufferRequestBody) {
        return this;
    }

    public ClientHttpRequestFactorySettings withSslBundle(SslBundle sslBundle) {
        return new ClientHttpRequestFactorySettings(this.connectTimeout, this.readTimeout, sslBundle);
    }

    @Deprecated(since = "3.2.0", forRemoval = true)
    public Boolean bufferRequestBody() {
        return null;
    }
}

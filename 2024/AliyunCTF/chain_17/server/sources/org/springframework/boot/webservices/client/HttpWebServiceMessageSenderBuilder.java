package org.springframework.boot.webservices.client;

import java.time.Duration;
import java.util.function.Function;
import java.util.function.Supplier;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.util.Assert;
import org.springframework.ws.transport.WebServiceMessageSender;
import org.springframework.ws.transport.http.ClientHttpRequestMessageSender;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/webservices/client/HttpWebServiceMessageSenderBuilder.class */
public class HttpWebServiceMessageSenderBuilder {
    private Duration connectTimeout;
    private Duration readTimeout;
    private SslBundle sslBundle;
    private Function<ClientHttpRequestFactorySettings, ClientHttpRequestFactory> requestFactory;

    public HttpWebServiceMessageSenderBuilder setConnectTimeout(Duration connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public HttpWebServiceMessageSenderBuilder setReadTimeout(Duration readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    public HttpWebServiceMessageSenderBuilder sslBundle(SslBundle sslBundle) {
        this.sslBundle = sslBundle;
        return this;
    }

    public HttpWebServiceMessageSenderBuilder requestFactory(Supplier<ClientHttpRequestFactory> requestFactorySupplier) {
        Assert.notNull(requestFactorySupplier, "RequestFactorySupplier must not be null");
        this.requestFactory = settings -> {
            return ClientHttpRequestFactories.get(requestFactorySupplier, settings);
        };
        return this;
    }

    public HttpWebServiceMessageSenderBuilder requestFactory(Function<ClientHttpRequestFactorySettings, ClientHttpRequestFactory> requestFactoryFunction) {
        Assert.notNull(requestFactoryFunction, "RequestFactoryFunction must not be null");
        this.requestFactory = requestFactoryFunction;
        return this;
    }

    public WebServiceMessageSender build() {
        return new ClientHttpRequestMessageSender(getRequestFactory());
    }

    private ClientHttpRequestFactory getRequestFactory() {
        ClientHttpRequestFactorySettings settings = new ClientHttpRequestFactorySettings(this.connectTimeout, this.readTimeout, this.sslBundle);
        return this.requestFactory != null ? this.requestFactory.apply(settings) : ClientHttpRequestFactories.get(settings);
    }
}

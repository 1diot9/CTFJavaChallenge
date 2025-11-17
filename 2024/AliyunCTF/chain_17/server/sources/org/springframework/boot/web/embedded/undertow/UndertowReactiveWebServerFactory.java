package org.springframework.boot.web.embedded.undertow;

import io.undertow.Undertow;
import java.io.File;
import java.util.Collection;
import java.util.List;
import org.springframework.boot.web.reactive.server.AbstractReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServer;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.UndertowHttpHandlerAdapter;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/undertow/UndertowReactiveWebServerFactory.class */
public class UndertowReactiveWebServerFactory extends AbstractReactiveWebServerFactory implements ConfigurableUndertowWebServerFactory {
    private final UndertowWebServerFactoryDelegate delegate;

    public UndertowReactiveWebServerFactory() {
        this.delegate = new UndertowWebServerFactoryDelegate();
    }

    public UndertowReactiveWebServerFactory(int port) {
        super(port);
        this.delegate = new UndertowWebServerFactoryDelegate();
    }

    @Override // org.springframework.boot.web.embedded.undertow.ConfigurableUndertowWebServerFactory
    public void setBuilderCustomizers(Collection<? extends UndertowBuilderCustomizer> customizers) {
        this.delegate.setBuilderCustomizers(customizers);
    }

    @Override // org.springframework.boot.web.embedded.undertow.ConfigurableUndertowWebServerFactory
    public void addBuilderCustomizers(UndertowBuilderCustomizer... customizers) {
        this.delegate.addBuilderCustomizers(customizers);
    }

    public Collection<UndertowBuilderCustomizer> getBuilderCustomizers() {
        return this.delegate.getBuilderCustomizers();
    }

    @Override // org.springframework.boot.web.embedded.undertow.ConfigurableUndertowWebServerFactory
    public void setBufferSize(Integer bufferSize) {
        this.delegate.setBufferSize(bufferSize);
    }

    @Override // org.springframework.boot.web.embedded.undertow.ConfigurableUndertowWebServerFactory
    public void setIoThreads(Integer ioThreads) {
        this.delegate.setIoThreads(ioThreads);
    }

    @Override // org.springframework.boot.web.embedded.undertow.ConfigurableUndertowWebServerFactory
    public void setWorkerThreads(Integer workerThreads) {
        this.delegate.setWorkerThreads(workerThreads);
    }

    @Override // org.springframework.boot.web.embedded.undertow.ConfigurableUndertowWebServerFactory
    public void setUseDirectBuffers(Boolean directBuffers) {
        this.delegate.setUseDirectBuffers(directBuffers);
    }

    @Override // org.springframework.boot.web.embedded.undertow.ConfigurableUndertowWebServerFactory
    public void setUseForwardHeaders(boolean useForwardHeaders) {
        this.delegate.setUseForwardHeaders(useForwardHeaders);
    }

    protected final boolean isUseForwardHeaders() {
        return this.delegate.isUseForwardHeaders();
    }

    @Override // org.springframework.boot.web.embedded.undertow.ConfigurableUndertowWebServerFactory
    public void setAccessLogDirectory(File accessLogDirectory) {
        this.delegate.setAccessLogDirectory(accessLogDirectory);
    }

    @Override // org.springframework.boot.web.embedded.undertow.ConfigurableUndertowWebServerFactory
    public void setAccessLogPattern(String accessLogPattern) {
        this.delegate.setAccessLogPattern(accessLogPattern);
    }

    @Override // org.springframework.boot.web.embedded.undertow.ConfigurableUndertowWebServerFactory
    public void setAccessLogPrefix(String accessLogPrefix) {
        this.delegate.setAccessLogPrefix(accessLogPrefix);
    }

    @Override // org.springframework.boot.web.embedded.undertow.ConfigurableUndertowWebServerFactory
    public void setAccessLogSuffix(String accessLogSuffix) {
        this.delegate.setAccessLogSuffix(accessLogSuffix);
    }

    public boolean isAccessLogEnabled() {
        return this.delegate.isAccessLogEnabled();
    }

    @Override // org.springframework.boot.web.embedded.undertow.ConfigurableUndertowWebServerFactory
    public void setAccessLogEnabled(boolean accessLogEnabled) {
        this.delegate.setAccessLogEnabled(accessLogEnabled);
    }

    @Override // org.springframework.boot.web.embedded.undertow.ConfigurableUndertowWebServerFactory
    public void setAccessLogRotate(boolean accessLogRotate) {
        this.delegate.setAccessLogRotate(accessLogRotate);
    }

    @Override // org.springframework.boot.web.reactive.server.ReactiveWebServerFactory
    public WebServer getWebServer(HttpHandler httpHandler) {
        Undertow.Builder builder = this.delegate.createBuilder(this, () -> {
            return this.getSslBundle();
        });
        List<HttpHandlerFactory> httpHandlerFactories = this.delegate.createHttpHandlerFactories(this, next -> {
            return new UndertowHttpHandlerAdapter(httpHandler);
        });
        return new UndertowWebServer(builder, httpHandlerFactories, getPort() >= 0);
    }
}

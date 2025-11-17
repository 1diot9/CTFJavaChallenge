package org.springframework.http.client.reactive;

import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import reactor.netty5.http.HttpResources;
import reactor.netty5.resources.ConnectionProvider;
import reactor.netty5.resources.LoopResources;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/reactive/ReactorNetty2ResourceFactory.class */
public class ReactorNetty2ResourceFactory implements InitializingBean, DisposableBean {

    @Nullable
    private Consumer<HttpResources> globalResourcesConsumer;

    @Nullable
    private ConnectionProvider connectionProvider;

    @Nullable
    private LoopResources loopResources;
    private boolean useGlobalResources = true;
    private Supplier<ConnectionProvider> connectionProviderSupplier = () -> {
        return ConnectionProvider.create("webflux", 500);
    };
    private Supplier<LoopResources> loopResourcesSupplier = () -> {
        return LoopResources.create("webflux-http");
    };
    private boolean manageConnectionProvider = false;
    private boolean manageLoopResources = false;
    private Duration shutdownQuietPeriod = Duration.ofSeconds(LoopResources.DEFAULT_SHUTDOWN_QUIET_PERIOD);
    private Duration shutdownTimeout = Duration.ofSeconds(LoopResources.DEFAULT_SHUTDOWN_TIMEOUT);

    public void setUseGlobalResources(boolean useGlobalResources) {
        this.useGlobalResources = useGlobalResources;
    }

    public boolean isUseGlobalResources() {
        return this.useGlobalResources;
    }

    public void addGlobalResourcesConsumer(Consumer<HttpResources> consumer) {
        this.useGlobalResources = true;
        this.globalResourcesConsumer = this.globalResourcesConsumer != null ? this.globalResourcesConsumer.andThen(consumer) : consumer;
    }

    public void setConnectionProviderSupplier(Supplier<ConnectionProvider> supplier) {
        this.connectionProviderSupplier = supplier;
    }

    public void setConnectionProvider(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public ConnectionProvider getConnectionProvider() {
        Assert.state(this.connectionProvider != null, "ConnectionProvider not initialized yet");
        return this.connectionProvider;
    }

    public void setLoopResourcesSupplier(Supplier<LoopResources> supplier) {
        this.loopResourcesSupplier = supplier;
    }

    public void setLoopResources(LoopResources loopResources) {
        this.loopResources = loopResources;
    }

    public LoopResources getLoopResources() {
        Assert.state(this.loopResources != null, "LoopResources not initialized yet");
        return this.loopResources;
    }

    public void setShutdownQuietPeriod(Duration shutdownQuietPeriod) {
        Assert.notNull(shutdownQuietPeriod, "shutdownQuietPeriod should not be null");
        this.shutdownQuietPeriod = shutdownQuietPeriod;
    }

    public void setShutdownTimeout(Duration shutdownTimeout) {
        Assert.notNull(shutdownTimeout, "shutdownTimeout should not be null");
        this.shutdownTimeout = shutdownTimeout;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        if (this.useGlobalResources) {
            Assert.isTrue(this.loopResources == null && this.connectionProvider == null, "'useGlobalResources' is mutually exclusive with explicitly configured resources");
            HttpResources httpResources = HttpResources.get();
            if (this.globalResourcesConsumer != null) {
                this.globalResourcesConsumer.accept(httpResources);
            }
            this.connectionProvider = httpResources;
            this.loopResources = httpResources;
            return;
        }
        if (this.loopResources == null) {
            this.manageLoopResources = true;
            this.loopResources = this.loopResourcesSupplier.get();
        }
        if (this.connectionProvider == null) {
            this.manageConnectionProvider = true;
            this.connectionProvider = this.connectionProviderSupplier.get();
        }
    }

    @Override // org.springframework.beans.factory.DisposableBean
    public void destroy() {
        if (this.useGlobalResources) {
            HttpResources.disposeLoopsAndConnectionsLater(this.shutdownQuietPeriod, this.shutdownTimeout).block();
            return;
        }
        try {
            ConnectionProvider provider = this.connectionProvider;
            if (provider != null && this.manageConnectionProvider) {
                provider.disposeLater().block();
            }
        } catch (Throwable th) {
        }
        try {
            LoopResources resources = this.loopResources;
            if (resources != null && this.manageLoopResources) {
                resources.disposeLater(this.shutdownQuietPeriod, this.shutdownTimeout).block();
            }
        } catch (Throwable th2) {
        }
    }
}

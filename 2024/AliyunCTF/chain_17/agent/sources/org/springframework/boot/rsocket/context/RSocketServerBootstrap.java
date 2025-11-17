package org.springframework.boot.rsocket.context;

import io.rsocket.SocketAcceptor;
import org.springframework.boot.rsocket.server.RSocketServer;
import org.springframework.boot.rsocket.server.RSocketServerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.SmartLifecycle;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/rsocket/context/RSocketServerBootstrap.class */
public class RSocketServerBootstrap implements ApplicationEventPublisherAware, SmartLifecycle {
    private final RSocketServer server;
    private ApplicationEventPublisher eventPublisher;

    public RSocketServerBootstrap(RSocketServerFactory serverFactory, SocketAcceptor socketAcceptor) {
        Assert.notNull(serverFactory, "ServerFactory must not be null");
        this.server = serverFactory.create(socketAcceptor);
    }

    @Override // org.springframework.context.ApplicationEventPublisherAware
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.eventPublisher = applicationEventPublisher;
    }

    @Override // org.springframework.context.Lifecycle
    public void start() {
        this.server.start();
        this.eventPublisher.publishEvent((ApplicationEvent) new RSocketServerInitializedEvent(this.server));
    }

    @Override // org.springframework.context.Lifecycle
    public void stop() {
        this.server.stop();
    }

    @Override // org.springframework.context.Lifecycle
    public boolean isRunning() {
        RSocketServer server = this.server;
        return (server == null || server.address() == null) ? false : true;
    }
}

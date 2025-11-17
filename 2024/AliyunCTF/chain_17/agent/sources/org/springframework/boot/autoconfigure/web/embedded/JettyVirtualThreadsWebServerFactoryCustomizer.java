package org.springframework.boot.autoconfigure.web.embedded;

import org.eclipse.jetty.util.VirtualThreads;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.embedded.jetty.ConfigurableJettyWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.core.Ordered;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/embedded/JettyVirtualThreadsWebServerFactoryCustomizer.class */
public class JettyVirtualThreadsWebServerFactoryCustomizer implements WebServerFactoryCustomizer<ConfigurableJettyWebServerFactory>, Ordered {
    private final ServerProperties serverProperties;

    public JettyVirtualThreadsWebServerFactoryCustomizer(ServerProperties serverProperties) {
        this.serverProperties = serverProperties;
    }

    @Override // org.springframework.boot.web.server.WebServerFactoryCustomizer
    public void customize(ConfigurableJettyWebServerFactory factory) {
        Assert.state(VirtualThreads.areSupported(), "Virtual threads are not supported");
        QueuedThreadPool threadPool = JettyThreadPool.create(this.serverProperties.getJetty().getThreads());
        threadPool.setVirtualThreadsExecutor(VirtualThreads.getDefaultVirtualThreadsExecutor());
        factory.setThreadPool(threadPool);
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return 1;
    }
}

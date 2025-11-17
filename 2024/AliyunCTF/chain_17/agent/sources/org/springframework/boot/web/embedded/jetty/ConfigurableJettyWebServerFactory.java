package org.springframework.boot.web.embedded.jetty;

import org.eclipse.jetty.util.thread.ThreadPool;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/jetty/ConfigurableJettyWebServerFactory.class */
public interface ConfigurableJettyWebServerFactory extends ConfigurableWebServerFactory {
    void setAcceptors(int acceptors);

    void setThreadPool(ThreadPool threadPool);

    void setSelectors(int selectors);

    void setUseForwardHeaders(boolean useForwardHeaders);

    void addServerCustomizers(JettyServerCustomizer... customizers);

    void setMaxConnections(int maxConnections);
}

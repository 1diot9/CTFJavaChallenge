package org.springframework.boot.web.reactive.server;

import org.springframework.boot.web.server.AbstractConfigurableWebServerFactory;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/reactive/server/AbstractReactiveWebServerFactory.class */
public abstract class AbstractReactiveWebServerFactory extends AbstractConfigurableWebServerFactory implements ConfigurableReactiveWebServerFactory {
    public AbstractReactiveWebServerFactory() {
    }

    public AbstractReactiveWebServerFactory(int port) {
        super(port);
    }
}

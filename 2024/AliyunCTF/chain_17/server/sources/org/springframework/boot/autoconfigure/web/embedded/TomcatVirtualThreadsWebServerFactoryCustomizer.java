package org.springframework.boot.autoconfigure.web.embedded;

import org.apache.tomcat.util.threads.VirtualThreadExecutor;
import org.springframework.boot.web.embedded.tomcat.ConfigurableTomcatWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.core.Ordered;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/embedded/TomcatVirtualThreadsWebServerFactoryCustomizer.class */
public class TomcatVirtualThreadsWebServerFactoryCustomizer implements WebServerFactoryCustomizer<ConfigurableTomcatWebServerFactory>, Ordered {
    @Override // org.springframework.boot.web.server.WebServerFactoryCustomizer
    public void customize(ConfigurableTomcatWebServerFactory factory) {
        factory.addProtocolHandlerCustomizers(protocolHandler -> {
            protocolHandler.setExecutor(new VirtualThreadExecutor("tomcat-handler-"));
        });
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return 1;
    }
}

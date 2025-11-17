package org.springframework.boot.web.embedded.tomcat;

import org.apache.catalina.connector.Connector;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/tomcat/TomcatConnectorCustomizer.class */
public interface TomcatConnectorCustomizer {
    void customize(Connector connector);
}

package org.springframework.boot.web.embedded.tomcat;

import org.apache.catalina.Context;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/tomcat/TomcatContextCustomizer.class */
public interface TomcatContextCustomizer {
    void customize(Context context);
}

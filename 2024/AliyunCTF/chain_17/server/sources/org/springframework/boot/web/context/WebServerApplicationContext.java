package org.springframework.boot.web.context;

import org.springframework.boot.web.server.WebServer;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ObjectUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/context/WebServerApplicationContext.class */
public interface WebServerApplicationContext extends ApplicationContext {
    WebServer getWebServer();

    String getServerNamespace();

    static boolean hasServerNamespace(ApplicationContext context, String serverNamespace) {
        if (context instanceof WebServerApplicationContext) {
            WebServerApplicationContext webServerApplicationContext = (WebServerApplicationContext) context;
            if (ObjectUtils.nullSafeEquals(webServerApplicationContext.getServerNamespace(), serverNamespace)) {
                return true;
            }
        }
        return false;
    }

    static String getServerNamespace(ApplicationContext context) {
        if (!(context instanceof WebServerApplicationContext)) {
            return null;
        }
        WebServerApplicationContext configurableContext = (WebServerApplicationContext) context;
        return configurableContext.getServerNamespace();
    }
}

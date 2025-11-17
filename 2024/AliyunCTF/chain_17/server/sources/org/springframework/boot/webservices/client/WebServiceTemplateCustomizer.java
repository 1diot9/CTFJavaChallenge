package org.springframework.boot.webservices.client;

import org.springframework.ws.client.core.WebServiceTemplate;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/webservices/client/WebServiceTemplateCustomizer.class */
public interface WebServiceTemplateCustomizer {
    void customize(WebServiceTemplate webServiceTemplate);
}

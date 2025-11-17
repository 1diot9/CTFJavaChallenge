package org.springframework.boot.autoconfigure;

import java.util.EventListener;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/AutoConfigurationImportListener.class */
public interface AutoConfigurationImportListener extends EventListener {
    void onAutoConfigurationImportEvent(AutoConfigurationImportEvent event);
}

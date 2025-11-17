package org.springframework.boot.autoconfigure;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/AutoConfigurationImportFilter.class */
public interface AutoConfigurationImportFilter {
    boolean[] match(String[] autoConfigurationClasses, AutoConfigurationMetadata autoConfigurationMetadata);
}

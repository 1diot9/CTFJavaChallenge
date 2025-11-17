package org.springframework.boot.autoconfigure.gson;

import com.google.gson.GsonBuilder;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/gson/GsonBuilderCustomizer.class */
public interface GsonBuilderCustomizer {
    void customize(GsonBuilder gsonBuilder);
}

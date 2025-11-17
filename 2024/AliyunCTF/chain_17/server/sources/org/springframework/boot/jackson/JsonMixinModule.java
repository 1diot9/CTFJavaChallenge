package org.springframework.boot.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/jackson/JsonMixinModule.class */
public class JsonMixinModule extends SimpleModule {
    public void registerEntries(JsonMixinModuleEntries entries, ClassLoader classLoader) {
        entries.doWithEntry(classLoader, this::setMixInAnnotation);
    }
}

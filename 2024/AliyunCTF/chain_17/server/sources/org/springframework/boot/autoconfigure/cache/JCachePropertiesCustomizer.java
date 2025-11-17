package org.springframework.boot.autoconfigure.cache;

import java.util.Properties;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/cache/JCachePropertiesCustomizer.class */
public interface JCachePropertiesCustomizer {
    void customize(CacheProperties cacheProperties, Properties properties);
}

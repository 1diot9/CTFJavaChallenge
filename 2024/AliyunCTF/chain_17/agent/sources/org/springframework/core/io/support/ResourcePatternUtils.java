package org.springframework.core.io.support;

import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.Nullable;
import org.springframework.util.ResourceUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/support/ResourcePatternUtils.class */
public abstract class ResourcePatternUtils {
    public static boolean isUrl(@Nullable String resourceLocation) {
        return resourceLocation != null && (resourceLocation.startsWith(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX) || ResourceUtils.isUrl(resourceLocation));
    }

    public static ResourcePatternResolver getResourcePatternResolver(@Nullable ResourceLoader resourceLoader) {
        if (resourceLoader instanceof ResourcePatternResolver) {
            ResourcePatternResolver resolver = (ResourcePatternResolver) resourceLoader;
            return resolver;
        }
        if (resourceLoader != null) {
            return new PathMatchingResourcePatternResolver(resourceLoader);
        }
        return new PathMatchingResourcePatternResolver();
    }
}

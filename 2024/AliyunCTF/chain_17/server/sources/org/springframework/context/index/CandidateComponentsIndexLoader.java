package org.springframework.context.index;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.SpringProperties;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.ConcurrentReferenceHashMap;

@Deprecated(since = "6.1", forRemoval = true)
/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/index/CandidateComponentsIndexLoader.class */
public final class CandidateComponentsIndexLoader {
    public static final String COMPONENTS_RESOURCE_LOCATION = "META-INF/spring.components";
    public static final String IGNORE_INDEX = "spring.index.ignore";
    private static final boolean shouldIgnoreIndex = SpringProperties.getFlag(IGNORE_INDEX);
    private static final Log logger = LogFactory.getLog((Class<?>) CandidateComponentsIndexLoader.class);
    private static final ConcurrentMap<ClassLoader, CandidateComponentsIndex> cache = new ConcurrentReferenceHashMap();

    private CandidateComponentsIndexLoader() {
    }

    @Nullable
    public static CandidateComponentsIndex loadIndex(@Nullable ClassLoader classLoader) {
        ClassLoader classLoaderToUse = classLoader;
        if (classLoaderToUse == null) {
            classLoaderToUse = CandidateComponentsIndexLoader.class.getClassLoader();
        }
        return cache.computeIfAbsent(classLoaderToUse, CandidateComponentsIndexLoader::doLoadIndex);
    }

    @Nullable
    private static CandidateComponentsIndex doLoadIndex(ClassLoader classLoader) {
        if (shouldIgnoreIndex) {
            return null;
        }
        try {
            Enumeration<URL> urls = classLoader.getResources(COMPONENTS_RESOURCE_LOCATION);
            if (!urls.hasMoreElements()) {
                return null;
            }
            List<Properties> result = new ArrayList<>();
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                Properties properties = PropertiesLoaderUtils.loadProperties(new UrlResource(url));
                result.add(properties);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Loaded " + result.size() + " index(es)");
            }
            int totalCount = result.stream().mapToInt((v0) -> {
                return v0.size();
            }).sum();
            if (totalCount > 0) {
                return new CandidateComponentsIndex(result);
            }
            return null;
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to load indexes from location [META-INF/spring.components]", ex);
        }
    }
}

package org.springframework.core.type.classreading;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/type/classreading/CachingMetadataReaderFactory.class */
public class CachingMetadataReaderFactory extends SimpleMetadataReaderFactory {
    public static final int DEFAULT_CACHE_LIMIT = 256;

    @Nullable
    private Map<Resource, MetadataReader> metadataReaderCache;

    public CachingMetadataReaderFactory() {
        setCacheLimit(256);
    }

    public CachingMetadataReaderFactory(@Nullable ClassLoader classLoader) {
        super(classLoader);
        setCacheLimit(256);
    }

    public CachingMetadataReaderFactory(@Nullable ResourceLoader resourceLoader) {
        super(resourceLoader);
        if (resourceLoader instanceof DefaultResourceLoader) {
            DefaultResourceLoader defaultResourceLoader = (DefaultResourceLoader) resourceLoader;
            this.metadataReaderCache = defaultResourceLoader.getResourceCache(MetadataReader.class);
        } else {
            setCacheLimit(256);
        }
    }

    public void setCacheLimit(int cacheLimit) {
        if (cacheLimit <= 0) {
            this.metadataReaderCache = null;
            return;
        }
        Map<Resource, MetadataReader> map = this.metadataReaderCache;
        if (map instanceof LocalResourceCache) {
            LocalResourceCache localResourceCache = (LocalResourceCache) map;
            localResourceCache.setCacheLimit(cacheLimit);
        } else {
            this.metadataReaderCache = new LocalResourceCache(cacheLimit);
        }
    }

    public int getCacheLimit() {
        Map<Resource, MetadataReader> map = this.metadataReaderCache;
        if (!(map instanceof LocalResourceCache)) {
            return this.metadataReaderCache != null ? Integer.MAX_VALUE : 0;
        }
        LocalResourceCache localResourceCache = (LocalResourceCache) map;
        return localResourceCache.getCacheLimit();
    }

    @Override // org.springframework.core.type.classreading.SimpleMetadataReaderFactory, org.springframework.core.type.classreading.MetadataReaderFactory
    public MetadataReader getMetadataReader(Resource resource) throws IOException {
        MetadataReader metadataReader;
        if (this.metadataReaderCache instanceof ConcurrentMap) {
            MetadataReader metadataReader2 = this.metadataReaderCache.get(resource);
            if (metadataReader2 == null) {
                metadataReader2 = super.getMetadataReader(resource);
                this.metadataReaderCache.put(resource, metadataReader2);
            }
            return metadataReader2;
        }
        if (this.metadataReaderCache != null) {
            synchronized (this.metadataReaderCache) {
                MetadataReader metadataReader3 = this.metadataReaderCache.get(resource);
                if (metadataReader3 == null) {
                    metadataReader3 = super.getMetadataReader(resource);
                    this.metadataReaderCache.put(resource, metadataReader3);
                }
                metadataReader = metadataReader3;
            }
            return metadataReader;
        }
        return super.getMetadataReader(resource);
    }

    public void clearCache() {
        if (this.metadataReaderCache instanceof LocalResourceCache) {
            synchronized (this.metadataReaderCache) {
                this.metadataReaderCache.clear();
            }
        } else if (this.metadataReaderCache != null) {
            setCacheLimit(256);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/type/classreading/CachingMetadataReaderFactory$LocalResourceCache.class */
    public static class LocalResourceCache extends LinkedHashMap<Resource, MetadataReader> {
        private volatile int cacheLimit;

        public LocalResourceCache(int cacheLimit) {
            super(cacheLimit, 0.75f, true);
            this.cacheLimit = cacheLimit;
        }

        public void setCacheLimit(int cacheLimit) {
            this.cacheLimit = cacheLimit;
        }

        public int getCacheLimit() {
            return this.cacheLimit;
        }

        @Override // java.util.LinkedHashMap
        protected boolean removeEldestEntry(Map.Entry<Resource, MetadataReader> eldest) {
            return size() > this.cacheLimit;
        }
    }
}

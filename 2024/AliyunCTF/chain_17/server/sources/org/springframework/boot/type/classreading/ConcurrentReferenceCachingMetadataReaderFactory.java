package org.springframework.boot.type.classreading;

import java.io.IOException;
import java.util.Map;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.util.ConcurrentReferenceHashMap;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/type/classreading/ConcurrentReferenceCachingMetadataReaderFactory.class */
public class ConcurrentReferenceCachingMetadataReaderFactory extends SimpleMetadataReaderFactory {
    private final Map<Resource, MetadataReader> cache;

    public ConcurrentReferenceCachingMetadataReaderFactory() {
        this.cache = new ConcurrentReferenceHashMap();
    }

    public ConcurrentReferenceCachingMetadataReaderFactory(ResourceLoader resourceLoader) {
        super(resourceLoader);
        this.cache = new ConcurrentReferenceHashMap();
    }

    public ConcurrentReferenceCachingMetadataReaderFactory(ClassLoader classLoader) {
        super(classLoader);
        this.cache = new ConcurrentReferenceHashMap();
    }

    @Override // org.springframework.core.type.classreading.SimpleMetadataReaderFactory, org.springframework.core.type.classreading.MetadataReaderFactory
    public MetadataReader getMetadataReader(Resource resource) throws IOException {
        MetadataReader metadataReader = this.cache.get(resource);
        if (metadataReader == null) {
            metadataReader = createMetadataReader(resource);
            this.cache.put(resource, metadataReader);
        }
        return metadataReader;
    }

    protected MetadataReader createMetadataReader(Resource resource) throws IOException {
        return super.getMetadataReader(resource);
    }

    public void clearCache() {
        this.cache.clear();
    }
}

package org.springframework.core.type.classreading;

import java.io.IOException;
import org.springframework.core.io.Resource;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/type/classreading/MetadataReaderFactory.class */
public interface MetadataReaderFactory {
    MetadataReader getMetadataReader(String className) throws IOException;

    MetadataReader getMetadataReader(Resource resource) throws IOException;
}

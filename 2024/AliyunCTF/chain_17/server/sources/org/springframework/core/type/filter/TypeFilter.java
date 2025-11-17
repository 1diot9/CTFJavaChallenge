package org.springframework.core.type.filter;

import java.io.IOException;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/type/filter/TypeFilter.class */
public interface TypeFilter {
    boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException;
}

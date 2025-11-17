package org.springframework.core.type.filter;

import java.io.IOException;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/type/filter/AbstractClassTestingTypeFilter.class */
public abstract class AbstractClassTestingTypeFilter implements TypeFilter {
    protected abstract boolean match(ClassMetadata metadata);

    @Override // org.springframework.core.type.filter.TypeFilter
    public final boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
        return match(metadataReader.getClassMetadata());
    }
}

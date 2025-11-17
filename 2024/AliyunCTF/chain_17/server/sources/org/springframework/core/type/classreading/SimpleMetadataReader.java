package org.springframework.core.type.classreading;

import java.io.IOException;
import java.io.InputStream;
import org.springframework.asm.ClassReader;
import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/type/classreading/SimpleMetadataReader.class */
final class SimpleMetadataReader implements MetadataReader {
    private static final int PARSING_OPTIONS = 7;
    private final Resource resource;
    private final AnnotationMetadata annotationMetadata;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SimpleMetadataReader(Resource resource, @Nullable ClassLoader classLoader) throws IOException {
        SimpleAnnotationMetadataReadingVisitor visitor = new SimpleAnnotationMetadataReadingVisitor(classLoader);
        getClassReader(resource).accept(visitor, 7);
        this.resource = resource;
        this.annotationMetadata = visitor.getMetadata();
    }

    private static ClassReader getClassReader(Resource resource) throws IOException {
        InputStream is = resource.getInputStream();
        try {
            try {
                ClassReader classReader = new ClassReader(is);
                if (is != null) {
                    is.close();
                }
                return classReader;
            } catch (IllegalArgumentException ex) {
                throw new ClassFormatException("ASM ClassReader failed to parse class file - probably due to a new Java class file version that is not supported yet. Consider compiling with a lower '-target' or upgrade your framework version. Affected class: " + resource, ex);
            }
        } catch (Throwable th) {
            if (is != null) {
                try {
                    is.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    @Override // org.springframework.core.type.classreading.MetadataReader
    public Resource getResource() {
        return this.resource;
    }

    @Override // org.springframework.core.type.classreading.MetadataReader
    public ClassMetadata getClassMetadata() {
        return this.annotationMetadata;
    }

    @Override // org.springframework.core.type.classreading.MetadataReader
    public AnnotationMetadata getAnnotationMetadata() {
        return this.annotationMetadata;
    }
}

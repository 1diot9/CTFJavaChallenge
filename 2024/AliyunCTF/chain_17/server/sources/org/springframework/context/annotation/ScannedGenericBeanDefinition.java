package org.springframework.context.annotation;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/ScannedGenericBeanDefinition.class */
public class ScannedGenericBeanDefinition extends GenericBeanDefinition implements AnnotatedBeanDefinition {
    private final AnnotationMetadata metadata;

    public ScannedGenericBeanDefinition(MetadataReader metadataReader) {
        Assert.notNull(metadataReader, "MetadataReader must not be null");
        this.metadata = metadataReader.getAnnotationMetadata();
        setBeanClassName(this.metadata.getClassName());
        setResource(metadataReader.getResource());
    }

    @Override // org.springframework.beans.factory.annotation.AnnotatedBeanDefinition
    public final AnnotationMetadata getMetadata() {
        return this.metadata;
    }

    @Override // org.springframework.beans.factory.annotation.AnnotatedBeanDefinition
    @Nullable
    public MethodMetadata getFactoryMethodMetadata() {
        return null;
    }
}

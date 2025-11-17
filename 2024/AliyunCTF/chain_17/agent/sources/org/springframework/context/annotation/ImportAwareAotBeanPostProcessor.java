package org.springframework.context.annotation;

import java.io.IOException;
import java.util.Map;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/ImportAwareAotBeanPostProcessor.class */
public final class ImportAwareAotBeanPostProcessor implements BeanPostProcessor, PriorityOrdered {
    private final MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory();
    private final Map<String, String> importsMapping;

    public ImportAwareAotBeanPostProcessor(Map<String, String> importsMapping) {
        this.importsMapping = Map.copyOf(importsMapping);
    }

    @Override // org.springframework.beans.factory.config.BeanPostProcessor
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        if (bean instanceof ImportAware) {
            ImportAware importAware = (ImportAware) bean;
            setAnnotationMetadata(importAware);
        }
        return bean;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return Integer.MAX_VALUE;
    }

    private void setAnnotationMetadata(ImportAware instance) {
        String importingClass = getImportingClassFor(instance);
        if (importingClass == null) {
            return;
        }
        try {
            MetadataReader metadataReader = this.metadataReaderFactory.getMetadataReader(importingClass);
            instance.setImportMetadata(metadataReader.getAnnotationMetadata());
        } catch (IOException ex) {
            throw new IllegalStateException(String.format("Failed to read metadata for '%s'", importingClass), ex);
        }
    }

    @Nullable
    private String getImportingClassFor(ImportAware instance) {
        String target = ClassUtils.getUserClass(instance).getName();
        return this.importsMapping.get(target);
    }
}

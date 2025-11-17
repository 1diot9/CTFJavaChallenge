package org.springframework.boot.autoconfigure;

import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.boot.context.annotation.ImportCandidates;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/AutoConfigurationExcludeFilter.class */
public class AutoConfigurationExcludeFilter implements TypeFilter, BeanClassLoaderAware {
    private ClassLoader beanClassLoader;
    private volatile List<String> autoConfigurations;

    @Override // org.springframework.beans.factory.BeanClassLoaderAware
    public void setBeanClassLoader(ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader;
    }

    @Override // org.springframework.core.type.filter.TypeFilter
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
        return isConfiguration(metadataReader) && isAutoConfiguration(metadataReader);
    }

    private boolean isConfiguration(MetadataReader metadataReader) {
        return metadataReader.getAnnotationMetadata().isAnnotated(Configuration.class.getName());
    }

    private boolean isAutoConfiguration(MetadataReader metadataReader) {
        boolean annotatedWithAutoConfiguration = metadataReader.getAnnotationMetadata().isAnnotated(AutoConfiguration.class.getName());
        return annotatedWithAutoConfiguration || getAutoConfigurations().contains(metadataReader.getClassMetadata().getClassName());
    }

    protected List<String> getAutoConfigurations() {
        if (this.autoConfigurations == null) {
            this.autoConfigurations = ImportCandidates.load(AutoConfiguration.class, this.beanClassLoader).getCandidates();
        }
        return this.autoConfigurations;
    }
}

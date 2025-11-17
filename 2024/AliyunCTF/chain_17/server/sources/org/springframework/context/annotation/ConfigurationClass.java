package org.springframework.context.annotation;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.parsing.Location;
import org.springframework.beans.factory.parsing.Problem;
import org.springframework.beans.factory.parsing.ProblemReporter;
import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.core.io.DescriptiveResource;
import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/ConfigurationClass.class */
public final class ConfigurationClass {
    private final AnnotationMetadata metadata;
    private final Resource resource;

    @Nullable
    private String beanName;
    private final Set<ConfigurationClass> importedBy = new LinkedHashSet(1);
    private final Set<BeanMethod> beanMethods = new LinkedHashSet();
    private final Map<String, Class<? extends BeanDefinitionReader>> importedResources = new LinkedHashMap();
    private final Map<ImportBeanDefinitionRegistrar, AnnotationMetadata> importBeanDefinitionRegistrars = new LinkedHashMap();
    final Set<String> skippedBeanMethods = new HashSet();

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConfigurationClass(MetadataReader metadataReader, String beanName) {
        Assert.notNull(beanName, "Bean name must not be null");
        this.metadata = metadataReader.getAnnotationMetadata();
        this.resource = metadataReader.getResource();
        this.beanName = beanName;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConfigurationClass(MetadataReader metadataReader, @Nullable ConfigurationClass importedBy) {
        this.metadata = metadataReader.getAnnotationMetadata();
        this.resource = metadataReader.getResource();
        this.importedBy.add(importedBy);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConfigurationClass(Class<?> clazz, String beanName) {
        Assert.notNull(beanName, "Bean name must not be null");
        this.metadata = AnnotationMetadata.introspect(clazz);
        this.resource = new DescriptiveResource(clazz.getName());
        this.beanName = beanName;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConfigurationClass(Class<?> clazz, @Nullable ConfigurationClass importedBy) {
        this.metadata = AnnotationMetadata.introspect(clazz);
        this.resource = new DescriptiveResource(clazz.getName());
        this.importedBy.add(importedBy);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConfigurationClass(AnnotationMetadata metadata, String beanName) {
        Assert.notNull(beanName, "Bean name must not be null");
        this.metadata = metadata;
        this.resource = new DescriptiveResource(metadata.getClassName());
        this.beanName = beanName;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AnnotationMetadata getMetadata() {
        return this.metadata;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Resource getResource() {
        return this.resource;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getSimpleName() {
        return ClassUtils.getShortName(getMetadata().getClassName());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Nullable
    public String getBeanName() {
        return this.beanName;
    }

    public boolean isImported() {
        return !this.importedBy.isEmpty();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void mergeImportedBy(ConfigurationClass otherConfigClass) {
        this.importedBy.addAll(otherConfigClass.importedBy);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Set<ConfigurationClass> getImportedBy() {
        return this.importedBy;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addBeanMethod(BeanMethod method) {
        this.beanMethods.add(method);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Set<BeanMethod> getBeanMethods() {
        return this.beanMethods;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addImportedResource(String importedResource, Class<? extends BeanDefinitionReader> readerClass) {
        this.importedResources.put(importedResource, readerClass);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addImportBeanDefinitionRegistrar(ImportBeanDefinitionRegistrar registrar, AnnotationMetadata importingClassMetadata) {
        this.importBeanDefinitionRegistrars.put(registrar, importingClassMetadata);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Map<ImportBeanDefinitionRegistrar, AnnotationMetadata> getImportBeanDefinitionRegistrars() {
        return this.importBeanDefinitionRegistrars;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Map<String, Class<? extends BeanDefinitionReader>> getImportedResources() {
        return this.importedResources;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void validate(ProblemReporter problemReporter) {
        Map<String, Object> attributes = this.metadata.getAnnotationAttributes(Configuration.class.getName());
        if (attributes != null && ((Boolean) attributes.get("proxyBeanMethods")).booleanValue()) {
            if (this.metadata.isFinal()) {
                problemReporter.error(new FinalConfigurationProblem());
            }
            for (BeanMethod beanMethod : this.beanMethods) {
                beanMethod.validate(problemReporter);
            }
        }
        if (attributes != null && ((Boolean) attributes.get("enforceUniqueMethods")).booleanValue()) {
            Map<String, MethodMetadata> beanMethodsByName = new LinkedHashMap<>();
            for (BeanMethod beanMethod2 : this.beanMethods) {
                MethodMetadata current = beanMethod2.getMetadata();
                MethodMetadata existing = beanMethodsByName.put(current.getMethodName(), current);
                if (existing != null && existing.getDeclaringClassName().equals(current.getDeclaringClassName())) {
                    problemReporter.error(new BeanMethodOverloadingProblem(existing.getMethodName()));
                }
            }
        }
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof ConfigurationClass) {
                ConfigurationClass that = (ConfigurationClass) other;
                if (getMetadata().getClassName().equals(that.getMetadata().getClassName())) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return getMetadata().getClassName().hashCode();
    }

    public String toString() {
        return "ConfigurationClass: beanName '" + this.beanName + "', " + this.resource;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/ConfigurationClass$FinalConfigurationProblem.class */
    private class FinalConfigurationProblem extends Problem {
        FinalConfigurationProblem() {
            super(String.format("@Configuration class '%s' may not be final. Remove the final modifier to continue.", ConfigurationClass.this.getSimpleName()), new Location(ConfigurationClass.this.getResource(), ConfigurationClass.this.getMetadata()));
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/ConfigurationClass$BeanMethodOverloadingProblem.class */
    private class BeanMethodOverloadingProblem extends Problem {
        BeanMethodOverloadingProblem(String methodName) {
            super(String.format("@Configuration class '%s' contains overloaded @Bean methods with name '%s'. Use unique method names for separate bean definitions (with individual conditions etc) or switch '@Configuration.enforceUniqueMethods' to 'false'.", ConfigurationClass.this.getSimpleName(), methodName), new Location(ConfigurationClass.this.getResource(), ConfigurationClass.this.getMetadata()));
        }
    }
}

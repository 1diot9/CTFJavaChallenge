package org.springframework.context.annotation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.index.CandidateComponentsIndex;
import org.springframework.context.index.CandidateComponentsIndexLoader;
import org.springframework.core.SpringProperties;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.ClassFormatException;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Indexed;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/ClassPathScanningCandidateComponentProvider.class */
public class ClassPathScanningCandidateComponentProvider implements EnvironmentCapable, ResourceLoaderAware {
    static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";
    public static final String IGNORE_CLASSFORMAT_PROPERTY_NAME = "spring.classformat.ignore";
    private static final boolean shouldIgnoreClassFormatException = SpringProperties.getFlag(IGNORE_CLASSFORMAT_PROPERTY_NAME);
    protected final Log logger;
    private String resourcePattern;
    private final List<TypeFilter> includeFilters;
    private final List<TypeFilter> excludeFilters;

    @Nullable
    private Environment environment;

    @Nullable
    private ConditionEvaluator conditionEvaluator;

    @Nullable
    private ResourcePatternResolver resourcePatternResolver;

    @Nullable
    private MetadataReaderFactory metadataReaderFactory;

    @Nullable
    private CandidateComponentsIndex componentsIndex;

    /* JADX INFO: Access modifiers changed from: protected */
    public ClassPathScanningCandidateComponentProvider() {
        this.logger = LogFactory.getLog(getClass());
        this.resourcePattern = DEFAULT_RESOURCE_PATTERN;
        this.includeFilters = new ArrayList();
        this.excludeFilters = new ArrayList();
    }

    public ClassPathScanningCandidateComponentProvider(boolean useDefaultFilters) {
        this(useDefaultFilters, new StandardEnvironment());
    }

    public ClassPathScanningCandidateComponentProvider(boolean useDefaultFilters, Environment environment) {
        this.logger = LogFactory.getLog(getClass());
        this.resourcePattern = DEFAULT_RESOURCE_PATTERN;
        this.includeFilters = new ArrayList();
        this.excludeFilters = new ArrayList();
        if (useDefaultFilters) {
            registerDefaultFilters();
        }
        setEnvironment(environment);
        setResourceLoader(null);
    }

    public void setResourcePattern(String resourcePattern) {
        Assert.notNull(resourcePattern, "'resourcePattern' must not be null");
        this.resourcePattern = resourcePattern;
    }

    public void addIncludeFilter(TypeFilter includeFilter) {
        this.includeFilters.add(includeFilter);
    }

    public void addExcludeFilter(TypeFilter excludeFilter) {
        this.excludeFilters.add(0, excludeFilter);
    }

    public void resetFilters(boolean useDefaultFilters) {
        this.includeFilters.clear();
        this.excludeFilters.clear();
        if (useDefaultFilters) {
            registerDefaultFilters();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void registerDefaultFilters() {
        this.includeFilters.add(new AnnotationTypeFilter(Component.class));
        ClassLoader cl = ClassPathScanningCandidateComponentProvider.class.getClassLoader();
        try {
            this.includeFilters.add(new AnnotationTypeFilter(ClassUtils.forName("jakarta.annotation.ManagedBean", cl), false));
            this.logger.trace("JSR-250 'jakarta.annotation.ManagedBean' found and supported for component scanning");
        } catch (ClassNotFoundException e) {
        }
        try {
            this.includeFilters.add(new AnnotationTypeFilter(ClassUtils.forName("javax.annotation.ManagedBean", cl), false));
            this.logger.trace("JSR-250 'javax.annotation.ManagedBean' found and supported for component scanning");
        } catch (ClassNotFoundException e2) {
        }
        try {
            this.includeFilters.add(new AnnotationTypeFilter(ClassUtils.forName("jakarta.inject.Named", cl), false));
            this.logger.trace("JSR-330 'jakarta.inject.Named' annotation found and supported for component scanning");
        } catch (ClassNotFoundException e3) {
        }
        try {
            this.includeFilters.add(new AnnotationTypeFilter(ClassUtils.forName("javax.inject.Named", cl), false));
            this.logger.trace("JSR-330 'javax.inject.Named' annotation found and supported for component scanning");
        } catch (ClassNotFoundException e4) {
        }
    }

    public void setEnvironment(Environment environment) {
        Assert.notNull(environment, "Environment must not be null");
        this.environment = environment;
        this.conditionEvaluator = null;
    }

    @Override // org.springframework.core.env.EnvironmentCapable
    public final Environment getEnvironment() {
        if (this.environment == null) {
            this.environment = new StandardEnvironment();
        }
        return this.environment;
    }

    @Nullable
    protected BeanDefinitionRegistry getRegistry() {
        return null;
    }

    @Override // org.springframework.context.ResourceLoaderAware
    public void setResourceLoader(@Nullable ResourceLoader resourceLoader) {
        this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
        this.metadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);
        this.componentsIndex = CandidateComponentsIndexLoader.loadIndex(this.resourcePatternResolver.getClassLoader());
    }

    public final ResourceLoader getResourceLoader() {
        return getResourcePatternResolver();
    }

    private ResourcePatternResolver getResourcePatternResolver() {
        if (this.resourcePatternResolver == null) {
            this.resourcePatternResolver = new PathMatchingResourcePatternResolver();
        }
        return this.resourcePatternResolver;
    }

    public void setMetadataReaderFactory(MetadataReaderFactory metadataReaderFactory) {
        this.metadataReaderFactory = metadataReaderFactory;
    }

    public final MetadataReaderFactory getMetadataReaderFactory() {
        if (this.metadataReaderFactory == null) {
            this.metadataReaderFactory = new CachingMetadataReaderFactory();
        }
        return this.metadataReaderFactory;
    }

    public Set<BeanDefinition> findCandidateComponents(String basePackage) {
        if (this.componentsIndex != null && indexSupportsIncludeFilters()) {
            return addCandidateComponentsFromIndex(this.componentsIndex, basePackage);
        }
        return scanCandidateComponents(basePackage);
    }

    private boolean indexSupportsIncludeFilters() {
        for (TypeFilter includeFilter : this.includeFilters) {
            if (!indexSupportsIncludeFilter(includeFilter)) {
                return false;
            }
        }
        return true;
    }

    private boolean indexSupportsIncludeFilter(TypeFilter filter) {
        if (filter instanceof AnnotationTypeFilter) {
            AnnotationTypeFilter annotationTypeFilter = (AnnotationTypeFilter) filter;
            Class<? extends Annotation> annotationType = annotationTypeFilter.getAnnotationType();
            return AnnotationUtils.isAnnotationDeclaredLocally(Indexed.class, annotationType) || annotationType.getName().startsWith("jakarta.") || annotationType.getName().startsWith("javax.");
        }
        if (filter instanceof AssignableTypeFilter) {
            AssignableTypeFilter assignableTypeFilter = (AssignableTypeFilter) filter;
            Class<?> target = assignableTypeFilter.getTargetType();
            return AnnotationUtils.isAnnotationDeclaredLocally(Indexed.class, target);
        }
        return false;
    }

    @Nullable
    private String extractStereotype(TypeFilter filter) {
        if (filter instanceof AnnotationTypeFilter) {
            AnnotationTypeFilter annotationTypeFilter = (AnnotationTypeFilter) filter;
            return annotationTypeFilter.getAnnotationType().getName();
        }
        if (filter instanceof AssignableTypeFilter) {
            AssignableTypeFilter assignableTypeFilter = (AssignableTypeFilter) filter;
            return assignableTypeFilter.getTargetType().getName();
        }
        return null;
    }

    private Set<BeanDefinition> addCandidateComponentsFromIndex(CandidateComponentsIndex index, String basePackage) {
        Set<BeanDefinition> candidates = new LinkedHashSet<>();
        try {
            Set<String> types = new HashSet<>();
            for (TypeFilter filter : this.includeFilters) {
                String stereotype = extractStereotype(filter);
                if (stereotype == null) {
                    throw new IllegalArgumentException("Failed to extract stereotype from " + filter);
                }
                types.addAll(index.getCandidateTypes(basePackage, stereotype));
            }
            boolean traceEnabled = this.logger.isTraceEnabled();
            boolean debugEnabled = this.logger.isDebugEnabled();
            for (String type : types) {
                MetadataReader metadataReader = getMetadataReaderFactory().getMetadataReader(type);
                if (isCandidateComponent(metadataReader)) {
                    ScannedGenericBeanDefinition sbd = new ScannedGenericBeanDefinition(metadataReader);
                    sbd.setSource(metadataReader.getResource());
                    if (isCandidateComponent(sbd)) {
                        if (debugEnabled) {
                            this.logger.debug("Using candidate component class from index: " + type);
                        }
                        candidates.add(sbd);
                    } else if (debugEnabled) {
                        this.logger.debug("Ignored because not a concrete top-level class: " + type);
                    }
                } else if (traceEnabled) {
                    this.logger.trace("Ignored because matching an exclude filter: " + type);
                }
            }
            return candidates;
        } catch (IOException ex) {
            throw new BeanDefinitionStoreException("I/O failure during classpath scanning", ex);
        }
    }

    private Set<BeanDefinition> scanCandidateComponents(String basePackage) {
        Set<BeanDefinition> candidates = new LinkedHashSet<>();
        try {
            String packageSearchPath = "classpath*:" + resolveBasePackage(basePackage) + "/" + this.resourcePattern;
            Resource[] resources = getResourcePatternResolver().getResources(packageSearchPath);
            boolean traceEnabled = this.logger.isTraceEnabled();
            boolean debugEnabled = this.logger.isDebugEnabled();
            for (Resource resource : resources) {
                String filename = resource.getFilename();
                if (filename == null || !filename.contains(ClassUtils.CGLIB_CLASS_SEPARATOR)) {
                    if (traceEnabled) {
                        this.logger.trace("Scanning " + resource);
                    }
                    try {
                        try {
                            MetadataReader metadataReader = getMetadataReaderFactory().getMetadataReader(resource);
                            if (isCandidateComponent(metadataReader)) {
                                ScannedGenericBeanDefinition sbd = new ScannedGenericBeanDefinition(metadataReader);
                                sbd.setSource(resource);
                                if (isCandidateComponent(sbd)) {
                                    if (debugEnabled) {
                                        this.logger.debug("Identified candidate component class: " + resource);
                                    }
                                    candidates.add(sbd);
                                } else if (debugEnabled) {
                                    this.logger.debug("Ignored because not a concrete top-level class: " + resource);
                                }
                            } else if (traceEnabled) {
                                this.logger.trace("Ignored because not matching any filter: " + resource);
                            }
                        } catch (ClassFormatException ex) {
                            if (shouldIgnoreClassFormatException) {
                                if (debugEnabled) {
                                    this.logger.debug("Ignored incompatible class format in " + resource + ": " + ex.getMessage());
                                }
                            } else {
                                throw new BeanDefinitionStoreException("Incompatible class format in " + resource + ": set system property 'spring.classformat.ignore' to 'true' if you mean to ignore such files during classpath scanning", ex);
                            }
                        } catch (Throwable ex2) {
                            throw new BeanDefinitionStoreException("Failed to read candidate component class: " + resource, ex2);
                        }
                    } catch (FileNotFoundException ex3) {
                        if (traceEnabled) {
                            this.logger.trace("Ignored non-readable " + resource + ": " + ex3.getMessage());
                        }
                    }
                }
            }
            return candidates;
        } catch (IOException ex4) {
            throw new BeanDefinitionStoreException("I/O failure during classpath scanning", ex4);
        }
    }

    protected String resolveBasePackage(String basePackage) {
        return ClassUtils.convertClassNameToResourcePath(getEnvironment().resolveRequiredPlaceholders(basePackage));
    }

    protected boolean isCandidateComponent(MetadataReader metadataReader) throws IOException {
        for (TypeFilter tf : this.excludeFilters) {
            if (tf.match(metadataReader, getMetadataReaderFactory())) {
                return false;
            }
        }
        for (TypeFilter tf2 : this.includeFilters) {
            if (tf2.match(metadataReader, getMetadataReaderFactory())) {
                return isConditionMatch(metadataReader);
            }
        }
        return false;
    }

    private boolean isConditionMatch(MetadataReader metadataReader) {
        if (this.conditionEvaluator == null) {
            this.conditionEvaluator = new ConditionEvaluator(getRegistry(), this.environment, this.resourcePatternResolver);
        }
        return !this.conditionEvaluator.shouldSkip(metadataReader.getAnnotationMetadata());
    }

    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        AnnotationMetadata metadata = beanDefinition.getMetadata();
        return metadata.isIndependent() && (metadata.isConcrete() || (metadata.isAbstract() && metadata.hasAnnotatedMethods(Lookup.class.getName())));
    }

    public void clearCache() {
        MetadataReaderFactory metadataReaderFactory = this.metadataReaderFactory;
        if (metadataReaderFactory instanceof CachingMetadataReaderFactory) {
            CachingMetadataReaderFactory cmrf = (CachingMetadataReaderFactory) metadataReaderFactory;
            cmrf.clearCache();
        }
    }
}

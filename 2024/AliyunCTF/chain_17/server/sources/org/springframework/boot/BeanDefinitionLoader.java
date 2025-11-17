package org.springframework.boot;

import groovy.lang.Closure;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.groovy.GroovyBeanDefinitionReader;
import org.springframework.beans.factory.support.AbstractBeanDefinitionReader;
import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.filter.AbstractTypeHierarchyTraversingFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.GroovyWebApplicationContext;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/BeanDefinitionLoader.class */
public class BeanDefinitionLoader {
    private static final Pattern GROOVY_CLOSURE_PATTERN = Pattern.compile(".*\\$_.*closure.*");
    private final Object[] sources;
    private final AnnotatedBeanDefinitionReader annotatedReader;
    private final AbstractBeanDefinitionReader xmlReader;
    private final BeanDefinitionReader groovyReader;
    private final ClassPathBeanDefinitionScanner scanner;
    private ResourceLoader resourceLoader;

    /* JADX INFO: Access modifiers changed from: protected */
    @FunctionalInterface
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/BeanDefinitionLoader$GroovyBeanDefinitionSource.class */
    public interface GroovyBeanDefinitionSource {
        Closure<?> getBeans();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BeanDefinitionLoader(BeanDefinitionRegistry registry, Object... sources) {
        Assert.notNull(registry, "Registry must not be null");
        Assert.notEmpty(sources, "Sources must not be empty");
        this.sources = sources;
        this.annotatedReader = new AnnotatedBeanDefinitionReader(registry);
        this.xmlReader = new XmlBeanDefinitionReader(registry);
        this.groovyReader = isGroovyPresent() ? new GroovyBeanDefinitionReader(registry) : null;
        this.scanner = new ClassPathBeanDefinitionScanner(registry);
        this.scanner.addExcludeFilter(new ClassExcludeFilter(sources));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setBeanNameGenerator(BeanNameGenerator beanNameGenerator) {
        this.annotatedReader.setBeanNameGenerator(beanNameGenerator);
        this.scanner.setBeanNameGenerator(beanNameGenerator);
        this.xmlReader.setBeanNameGenerator(beanNameGenerator);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
        this.scanner.setResourceLoader(resourceLoader);
        this.xmlReader.setResourceLoader(resourceLoader);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setEnvironment(ConfigurableEnvironment environment) {
        this.annotatedReader.setEnvironment(environment);
        this.scanner.setEnvironment(environment);
        this.xmlReader.setEnvironment(environment);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void load() {
        for (Object source : this.sources) {
            load(source);
        }
    }

    private void load(Object source) {
        Assert.notNull(source, "Source must not be null");
        if (source instanceof Class) {
            Class<?> clazz = (Class) source;
            load(clazz);
            return;
        }
        if (source instanceof Resource) {
            Resource resource = (Resource) source;
            load(resource);
        } else if (source instanceof Package) {
            Package pack = (Package) source;
            load(pack);
        } else {
            if (source instanceof CharSequence) {
                CharSequence sequence = (CharSequence) source;
                load(sequence);
                return;
            }
            throw new IllegalArgumentException("Invalid source type " + source.getClass());
        }
    }

    private void load(Class<?> source) {
        if (isGroovyPresent() && GroovyBeanDefinitionSource.class.isAssignableFrom(source)) {
            GroovyBeanDefinitionSource loader = (GroovyBeanDefinitionSource) BeanUtils.instantiateClass(source, GroovyBeanDefinitionSource.class);
            ((GroovyBeanDefinitionReader) this.groovyReader).beans(loader.getBeans());
        }
        if (isEligible(source)) {
            this.annotatedReader.register(source);
        }
    }

    private void load(Resource source) {
        if (source.getFilename().endsWith(GroovyWebApplicationContext.DEFAULT_CONFIG_LOCATION_SUFFIX)) {
            if (this.groovyReader == null) {
                throw new BeanDefinitionStoreException("Cannot load Groovy beans without Groovy on classpath");
            }
            this.groovyReader.loadBeanDefinitions(source);
            return;
        }
        this.xmlReader.loadBeanDefinitions(source);
    }

    private void load(Package source) {
        this.scanner.scan(source.getName());
    }

    private void load(CharSequence source) {
        String resolvedSource = this.scanner.getEnvironment().resolvePlaceholders(source.toString());
        try {
            load(ClassUtils.forName(resolvedSource, null));
        } catch (ClassNotFoundException | IllegalArgumentException e) {
            if (loadAsResources(resolvedSource)) {
                return;
            }
            Package packageResource = findPackage(resolvedSource);
            if (packageResource != null) {
                load(packageResource);
                return;
            }
            throw new IllegalArgumentException("Invalid source '" + resolvedSource + "'");
        }
    }

    private boolean loadAsResources(String resolvedSource) {
        boolean foundCandidate = false;
        Resource[] resources = findResources(resolvedSource);
        for (Resource resource : resources) {
            if (isLoadCandidate(resource)) {
                foundCandidate = true;
                load(resource);
            }
        }
        return foundCandidate;
    }

    private boolean isGroovyPresent() {
        return ClassUtils.isPresent("groovy.lang.MetaClass", null);
    }

    private Resource[] findResources(String source) {
        ResourceLoader loader = this.resourceLoader != null ? this.resourceLoader : new PathMatchingResourcePatternResolver();
        try {
            if (loader instanceof ResourcePatternResolver) {
                ResourcePatternResolver resolver = (ResourcePatternResolver) loader;
                return resolver.getResources(source);
            }
            return new Resource[]{loader.getResource(source)};
        } catch (IOException e) {
            throw new IllegalStateException("Error reading source '" + source + "'");
        }
    }

    private boolean isLoadCandidate(Resource resource) {
        if (resource == null || !resource.exists()) {
            return false;
        }
        if (resource instanceof ClassPathResource) {
            ClassPathResource classPathResource = (ClassPathResource) resource;
            String path = classPathResource.getPath();
            if (path.indexOf(46) == -1) {
                try {
                    return getClass().getClassLoader().getDefinedPackage(path) == null;
                } catch (Exception e) {
                    return true;
                }
            }
            return true;
        }
        return true;
    }

    private Package findPackage(CharSequence source) {
        Package pkg = getClass().getClassLoader().getDefinedPackage(source.toString());
        if (pkg != null) {
            return pkg;
        }
        try {
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(getClass().getClassLoader());
            Resource[] resources = resolver.getResources(ClassUtils.convertClassNameToResourcePath(source.toString()) + "/*.class");
            if (0 < resources.length) {
                Resource resource = resources[0];
                String className = StringUtils.stripFilenameExtension(resource.getFilename());
                load(Class.forName(source + "." + className));
            }
        } catch (Exception e) {
        }
        return getClass().getClassLoader().getDefinedPackage(source.toString());
    }

    private boolean isEligible(Class<?> type) {
        return (type.isAnonymousClass() || isGroovyClosure(type) || hasNoConstructors(type)) ? false : true;
    }

    private boolean isGroovyClosure(Class<?> type) {
        return GROOVY_CLOSURE_PATTERN.matcher(type.getName()).matches();
    }

    private boolean hasNoConstructors(Class<?> type) {
        Constructor<?>[] constructors = type.getDeclaredConstructors();
        return ObjectUtils.isEmpty((Object[]) constructors);
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/BeanDefinitionLoader$ClassExcludeFilter.class */
    private static class ClassExcludeFilter extends AbstractTypeHierarchyTraversingFilter {
        private final Set<String> classNames;

        ClassExcludeFilter(Object... sources) {
            super(false, false);
            this.classNames = new HashSet();
            for (Object source : sources) {
                if (source instanceof Class) {
                    this.classNames.add(((Class) source).getName());
                }
            }
        }

        @Override // org.springframework.core.type.filter.AbstractTypeHierarchyTraversingFilter
        protected boolean matchClassName(String className) {
            return this.classNames.contains(className);
        }
    }
}

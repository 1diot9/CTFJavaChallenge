package org.springframework.context.annotation;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.filter.AbstractTypeHierarchyTraversingFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/ComponentScanAnnotationParser.class */
class ComponentScanAnnotationParser {
    private final Environment environment;
    private final ResourceLoader resourceLoader;
    private final BeanNameGenerator beanNameGenerator;
    private final BeanDefinitionRegistry registry;

    public ComponentScanAnnotationParser(Environment environment, ResourceLoader resourceLoader, BeanNameGenerator beanNameGenerator, BeanDefinitionRegistry registry) {
        this.environment = environment;
        this.resourceLoader = resourceLoader;
        this.beanNameGenerator = beanNameGenerator;
        this.registry = registry;
    }

    public Set<BeanDefinitionHolder> parse(AnnotationAttributes componentScan, final String declaringClass) {
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(this.registry, componentScan.getBoolean("useDefaultFilters"), this.environment, this.resourceLoader);
        Class<? extends BeanNameGenerator> generatorClass = componentScan.getClass("nameGenerator");
        boolean useInheritedGenerator = BeanNameGenerator.class == generatorClass;
        scanner.setBeanNameGenerator(useInheritedGenerator ? this.beanNameGenerator : (BeanNameGenerator) BeanUtils.instantiateClass(generatorClass));
        ScopedProxyMode scopedProxyMode = (ScopedProxyMode) componentScan.getEnum("scopedProxy");
        if (scopedProxyMode != ScopedProxyMode.DEFAULT) {
            scanner.setScopedProxyMode(scopedProxyMode);
        } else {
            Class<? extends ScopeMetadataResolver> resolverClass = componentScan.getClass("scopeResolver");
            scanner.setScopeMetadataResolver((ScopeMetadataResolver) BeanUtils.instantiateClass(resolverClass));
        }
        scanner.setResourcePattern(componentScan.getString("resourcePattern"));
        for (AnnotationAttributes includeFilterAttributes : componentScan.getAnnotationArray("includeFilters")) {
            List<TypeFilter> typeFilters = TypeFilterUtils.createTypeFiltersFor(includeFilterAttributes, this.environment, this.resourceLoader, this.registry);
            for (TypeFilter typeFilter : typeFilters) {
                scanner.addIncludeFilter(typeFilter);
            }
        }
        for (AnnotationAttributes excludeFilterAttributes : componentScan.getAnnotationArray("excludeFilters")) {
            for (TypeFilter typeFilter2 : TypeFilterUtils.createTypeFiltersFor(excludeFilterAttributes, this.environment, this.resourceLoader, this.registry)) {
                scanner.addExcludeFilter(typeFilter2);
            }
        }
        boolean lazyInit = componentScan.getBoolean("lazyInit");
        if (lazyInit) {
            scanner.getBeanDefinitionDefaults().setLazyInit(true);
        }
        Set<String> basePackages = new LinkedHashSet<>();
        String[] basePackagesArray = componentScan.getStringArray("basePackages");
        for (String pkg : basePackagesArray) {
            String[] tokenized = StringUtils.tokenizeToStringArray(this.environment.resolvePlaceholders(pkg), ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
            Collections.addAll(basePackages, tokenized);
        }
        for (Class<?> clazz : componentScan.getClassArray("basePackageClasses")) {
            basePackages.add(ClassUtils.getPackageName(clazz));
        }
        if (basePackages.isEmpty()) {
            basePackages.add(ClassUtils.getPackageName(declaringClass));
        }
        scanner.addExcludeFilter(new AbstractTypeHierarchyTraversingFilter(false, false) { // from class: org.springframework.context.annotation.ComponentScanAnnotationParser.1
            @Override // org.springframework.core.type.filter.AbstractTypeHierarchyTraversingFilter
            protected boolean matchClassName(String className) {
                return declaringClass.equals(className);
            }
        });
        return scanner.doScan(StringUtils.toStringArray(basePackages));
    }
}

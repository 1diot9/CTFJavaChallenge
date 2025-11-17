package org.springframework.context.annotation;

import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionDefaults;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.PatternMatchUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/ClassPathBeanDefinitionScanner.class */
public class ClassPathBeanDefinitionScanner extends ClassPathScanningCandidateComponentProvider {
    private final BeanDefinitionRegistry registry;
    private BeanDefinitionDefaults beanDefinitionDefaults;

    @Nullable
    private String[] autowireCandidatePatterns;
    private BeanNameGenerator beanNameGenerator;
    private ScopeMetadataResolver scopeMetadataResolver;
    private boolean includeAnnotationConfig;

    public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry) {
        this(registry, true);
    }

    public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters) {
        this(registry, useDefaultFilters, getOrCreateEnvironment(registry));
    }

    /* JADX WARN: Illegal instructions before constructor call */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public ClassPathBeanDefinitionScanner(org.springframework.beans.factory.support.BeanDefinitionRegistry r7, boolean r8, org.springframework.core.env.Environment r9) {
        /*
            r6 = this;
            r0 = r6
            r1 = r7
            r2 = r8
            r3 = r9
            r4 = r7
            boolean r4 = r4 instanceof org.springframework.core.io.ResourceLoader
            if (r4 == 0) goto L16
            r4 = r7
            org.springframework.core.io.ResourceLoader r4 = (org.springframework.core.io.ResourceLoader) r4
            r10 = r4
            r4 = r10
            goto L17
        L16:
            r4 = 0
        L17:
            r0.<init>(r1, r2, r3, r4)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.context.annotation.ClassPathBeanDefinitionScanner.<init>(org.springframework.beans.factory.support.BeanDefinitionRegistry, boolean, org.springframework.core.env.Environment):void");
    }

    public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters, Environment environment, @Nullable ResourceLoader resourceLoader) {
        this.beanDefinitionDefaults = new BeanDefinitionDefaults();
        this.beanNameGenerator = AnnotationBeanNameGenerator.INSTANCE;
        this.scopeMetadataResolver = new AnnotationScopeMetadataResolver();
        this.includeAnnotationConfig = true;
        Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
        this.registry = registry;
        if (useDefaultFilters) {
            registerDefaultFilters();
        }
        setEnvironment(environment);
        setResourceLoader(resourceLoader);
    }

    @Override // org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
    public final BeanDefinitionRegistry getRegistry() {
        return this.registry;
    }

    public void setBeanDefinitionDefaults(@Nullable BeanDefinitionDefaults beanDefinitionDefaults) {
        this.beanDefinitionDefaults = beanDefinitionDefaults != null ? beanDefinitionDefaults : new BeanDefinitionDefaults();
    }

    public BeanDefinitionDefaults getBeanDefinitionDefaults() {
        return this.beanDefinitionDefaults;
    }

    public void setAutowireCandidatePatterns(@Nullable String... autowireCandidatePatterns) {
        this.autowireCandidatePatterns = autowireCandidatePatterns;
    }

    public void setBeanNameGenerator(@Nullable BeanNameGenerator beanNameGenerator) {
        this.beanNameGenerator = beanNameGenerator != null ? beanNameGenerator : AnnotationBeanNameGenerator.INSTANCE;
    }

    public void setScopeMetadataResolver(@Nullable ScopeMetadataResolver scopeMetadataResolver) {
        this.scopeMetadataResolver = scopeMetadataResolver != null ? scopeMetadataResolver : new AnnotationScopeMetadataResolver();
    }

    public void setScopedProxyMode(ScopedProxyMode scopedProxyMode) {
        this.scopeMetadataResolver = new AnnotationScopeMetadataResolver(scopedProxyMode);
    }

    public void setIncludeAnnotationConfig(boolean includeAnnotationConfig) {
        this.includeAnnotationConfig = includeAnnotationConfig;
    }

    public int scan(String... basePackages) {
        int beanCountAtScanStart = this.registry.getBeanDefinitionCount();
        doScan(basePackages);
        if (this.includeAnnotationConfig) {
            AnnotationConfigUtils.registerAnnotationConfigProcessors(this.registry);
        }
        return this.registry.getBeanDefinitionCount() - beanCountAtScanStart;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Assert.notEmpty(basePackages, "At least one base package must be specified");
        Set<BeanDefinitionHolder> beanDefinitions = new LinkedHashSet<>();
        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidates = findCandidateComponents(basePackage);
            for (BeanDefinition candidate : candidates) {
                ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(candidate);
                candidate.setScope(scopeMetadata.getScopeName());
                String beanName = this.beanNameGenerator.generateBeanName(candidate, this.registry);
                if (candidate instanceof AbstractBeanDefinition) {
                    AbstractBeanDefinition abstractBeanDefinition = (AbstractBeanDefinition) candidate;
                    postProcessBeanDefinition(abstractBeanDefinition, beanName);
                }
                if (candidate instanceof AnnotatedBeanDefinition) {
                    AnnotatedBeanDefinition annotatedBeanDefinition = (AnnotatedBeanDefinition) candidate;
                    AnnotationConfigUtils.processCommonDefinitionAnnotations(annotatedBeanDefinition);
                }
                if (checkCandidate(beanName, candidate)) {
                    BeanDefinitionHolder definitionHolder = AnnotationConfigUtils.applyScopedProxyMode(scopeMetadata, new BeanDefinitionHolder(candidate, beanName), this.registry);
                    beanDefinitions.add(definitionHolder);
                    registerBeanDefinition(definitionHolder, this.registry);
                }
            }
        }
        return beanDefinitions;
    }

    protected void postProcessBeanDefinition(AbstractBeanDefinition beanDefinition, String beanName) {
        beanDefinition.applyDefaults(this.beanDefinitionDefaults);
        if (this.autowireCandidatePatterns != null) {
            beanDefinition.setAutowireCandidate(PatternMatchUtils.simpleMatch(this.autowireCandidatePatterns, beanName));
        }
    }

    protected void registerBeanDefinition(BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry) {
        BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, registry);
    }

    protected boolean checkCandidate(String beanName, BeanDefinition beanDefinition) throws IllegalStateException {
        if (!this.registry.containsBeanDefinition(beanName)) {
            return true;
        }
        BeanDefinition existingDef = this.registry.getBeanDefinition(beanName);
        BeanDefinition originatingDef = existingDef.getOriginatingBeanDefinition();
        if (originatingDef != null) {
            existingDef = originatingDef;
        }
        if ((!(existingDef instanceof ScannedGenericBeanDefinition) && (this.registry.isBeanDefinitionOverridable(beanName) || ObjectUtils.nullSafeEquals(beanDefinition.getBeanClassName(), existingDef.getBeanClassName()))) || isCompatible(beanDefinition, existingDef)) {
            return false;
        }
        throw new ConflictingBeanDefinitionException("Annotation-specified bean name '" + beanName + "' for bean class [" + beanDefinition.getBeanClassName() + "] conflicts with existing, non-compatible bean definition of same name and class [" + existingDef.getBeanClassName() + "]");
    }

    protected boolean isCompatible(BeanDefinition newDef, BeanDefinition existingDef) {
        return (newDef.getSource() != null && newDef.getSource().equals(existingDef.getSource())) || newDef.equals(existingDef);
    }

    private static Environment getOrCreateEnvironment(BeanDefinitionRegistry registry) {
        Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
        if (registry instanceof EnvironmentCapable) {
            EnvironmentCapable environmentCapable = (EnvironmentCapable) registry;
            return environmentCapable.getEnvironment();
        }
        return new StandardEnvironment();
    }
}

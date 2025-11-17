package org.springframework.context.annotation;

import ch.qos.logback.core.CoreConstants;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.lang.model.element.Modifier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.autoproxy.AutoProxyUtils;
import org.springframework.aot.generate.GeneratedMethod;
import org.springframework.aot.generate.GenerationContext;
import org.springframework.aot.hint.ExecutableMode;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.ResourceHints;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.TypeReference;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor;
import org.springframework.beans.factory.aot.BeanFactoryInitializationCode;
import org.springframework.beans.factory.aot.BeanRegistrationAotContribution;
import org.springframework.beans.factory.aot.BeanRegistrationAotProcessor;
import org.springframework.beans.factory.aot.BeanRegistrationCode;
import org.springframework.beans.factory.aot.BeanRegistrationCodeFragments;
import org.springframework.beans.factory.aot.BeanRegistrationCodeFragmentsDecorator;
import org.springframework.beans.factory.aot.InstanceSupplierCodeGenerator;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.beans.factory.parsing.FailFastProblemReporter;
import org.springframework.beans.factory.parsing.PassThroughSourceExtractor;
import org.springframework.beans.factory.parsing.ProblemReporter;
import org.springframework.beans.factory.parsing.SourceExtractor;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RegisteredBean;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationStartupAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ConfigurationClassEnhancer;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PropertySourceDescriptor;
import org.springframework.core.io.support.PropertySourceProcessor;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.metrics.ApplicationStartup;
import org.springframework.core.metrics.StartupStep;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.javapoet.CodeBlock;
import org.springframework.javapoet.MethodSpec;
import org.springframework.javapoet.ParameterizedTypeName;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/ConfigurationClassPostProcessor.class */
public class ConfigurationClassPostProcessor implements BeanDefinitionRegistryPostProcessor, BeanRegistrationAotProcessor, BeanFactoryInitializationAotProcessor, PriorityOrdered, ResourceLoaderAware, ApplicationStartupAware, BeanClassLoaderAware, EnvironmentAware {
    public static final AnnotationBeanNameGenerator IMPORT_BEAN_NAME_GENERATOR = FullyQualifiedAnnotationBeanNameGenerator.INSTANCE;
    private static final String IMPORT_REGISTRY_BEAN_NAME = ConfigurationClassPostProcessor.class.getName() + ".importRegistry";

    @Nullable
    private Environment environment;

    @Nullable
    private ConfigurationClassBeanDefinitionReader reader;

    @Nullable
    private List<PropertySourceDescriptor> propertySourceDescriptors;
    private final Log logger = LogFactory.getLog(getClass());
    private SourceExtractor sourceExtractor = new PassThroughSourceExtractor();
    private ProblemReporter problemReporter = new FailFastProblemReporter();
    private ResourceLoader resourceLoader = new DefaultResourceLoader();

    @Nullable
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();
    private MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory();
    private boolean setMetadataReaderFactoryCalled = false;
    private final Set<Integer> registriesPostProcessed = new HashSet();
    private final Set<Integer> factoriesPostProcessed = new HashSet();
    private boolean localBeanNameGeneratorSet = false;
    private BeanNameGenerator componentScanBeanNameGenerator = AnnotationBeanNameGenerator.INSTANCE;
    private BeanNameGenerator importBeanNameGenerator = IMPORT_BEAN_NAME_GENERATOR;
    private ApplicationStartup applicationStartup = ApplicationStartup.DEFAULT;

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return Integer.MAX_VALUE;
    }

    public void setSourceExtractor(@Nullable SourceExtractor sourceExtractor) {
        this.sourceExtractor = sourceExtractor != null ? sourceExtractor : new PassThroughSourceExtractor();
    }

    public void setProblemReporter(@Nullable ProblemReporter problemReporter) {
        this.problemReporter = problemReporter != null ? problemReporter : new FailFastProblemReporter();
    }

    public void setMetadataReaderFactory(MetadataReaderFactory metadataReaderFactory) {
        Assert.notNull(metadataReaderFactory, "MetadataReaderFactory must not be null");
        this.metadataReaderFactory = metadataReaderFactory;
        this.setMetadataReaderFactoryCalled = true;
    }

    public void setBeanNameGenerator(BeanNameGenerator beanNameGenerator) {
        Assert.notNull(beanNameGenerator, "BeanNameGenerator must not be null");
        this.localBeanNameGeneratorSet = true;
        this.componentScanBeanNameGenerator = beanNameGenerator;
        this.importBeanNameGenerator = beanNameGenerator;
    }

    @Override // org.springframework.context.EnvironmentAware
    public void setEnvironment(Environment environment) {
        Assert.notNull(environment, "Environment must not be null");
        this.environment = environment;
    }

    @Override // org.springframework.context.ResourceLoaderAware
    public void setResourceLoader(ResourceLoader resourceLoader) {
        Assert.notNull(resourceLoader, "ResourceLoader must not be null");
        this.resourceLoader = resourceLoader;
        if (!this.setMetadataReaderFactoryCalled) {
            this.metadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);
        }
    }

    @Override // org.springframework.beans.factory.BeanClassLoaderAware
    public void setBeanClassLoader(ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader;
        if (!this.setMetadataReaderFactoryCalled) {
            this.metadataReaderFactory = new CachingMetadataReaderFactory(beanClassLoader);
        }
    }

    @Override // org.springframework.context.ApplicationStartupAware
    public void setApplicationStartup(ApplicationStartup applicationStartup) {
        this.applicationStartup = applicationStartup;
    }

    @Override // org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
        int registryId = System.identityHashCode(registry);
        if (this.registriesPostProcessed.contains(Integer.valueOf(registryId))) {
            throw new IllegalStateException("postProcessBeanDefinitionRegistry already called on this post-processor against " + registry);
        }
        if (this.factoriesPostProcessed.contains(Integer.valueOf(registryId))) {
            throw new IllegalStateException("postProcessBeanFactory already called on this post-processor against " + registry);
        }
        this.registriesPostProcessed.add(Integer.valueOf(registryId));
        processConfigBeanDefinitions(registry);
    }

    @Override // org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor, org.springframework.beans.factory.config.BeanFactoryPostProcessor
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        int factoryId = System.identityHashCode(beanFactory);
        if (this.factoriesPostProcessed.contains(Integer.valueOf(factoryId))) {
            throw new IllegalStateException("postProcessBeanFactory already called on this post-processor against " + beanFactory);
        }
        this.factoriesPostProcessed.add(Integer.valueOf(factoryId));
        if (!this.registriesPostProcessed.contains(Integer.valueOf(factoryId))) {
            processConfigBeanDefinitions((BeanDefinitionRegistry) beanFactory);
        }
        enhanceConfigurationClasses(beanFactory);
        beanFactory.addBeanPostProcessor(new ImportAwareBeanPostProcessor(beanFactory));
    }

    @Override // org.springframework.beans.factory.aot.BeanRegistrationAotProcessor
    @Nullable
    public BeanRegistrationAotContribution processAheadOfTime(RegisteredBean registeredBean) {
        Object configClassAttr = registeredBean.getMergedBeanDefinition().getAttribute(ConfigurationClassUtils.CONFIGURATION_CLASS_ATTRIBUTE);
        if ("full".equals(configClassAttr)) {
            return BeanRegistrationAotContribution.withCustomCodeFragments(codeFragments -> {
                return new ConfigurationClassProxyBeanRegistrationCodeFragments(codeFragments, registeredBean);
            });
        }
        return null;
    }

    @Override // org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor
    @Nullable
    public BeanFactoryInitializationAotContribution processAheadOfTime(ConfigurableListableBeanFactory beanFactory) {
        boolean hasPropertySourceDescriptors = !CollectionUtils.isEmpty(this.propertySourceDescriptors);
        boolean hasImportRegistry = beanFactory.containsBean(IMPORT_REGISTRY_BEAN_NAME);
        if (hasPropertySourceDescriptors || hasImportRegistry) {
            return (generationContext, code) -> {
                if (hasPropertySourceDescriptors) {
                    new PropertySourcesAotContribution(this.propertySourceDescriptors, this::resolvePropertySourceLocation).applyTo(generationContext, code);
                }
                if (hasImportRegistry) {
                    new ImportAwareAotContribution(beanFactory).applyTo(generationContext, code);
                }
            };
        }
        return null;
    }

    @Nullable
    private Resource resolvePropertySourceLocation(String location) {
        try {
            String resolvedLocation = this.environment != null ? this.environment.resolveRequiredPlaceholders(location) : location;
            return this.resourceLoader.getResource(resolvedLocation);
        } catch (Exception e) {
            return null;
        }
    }

    public void processConfigBeanDefinitions(BeanDefinitionRegistry registry) {
        BeanNameGenerator generator;
        List<BeanDefinitionHolder> configCandidates = new ArrayList<>();
        String[] candidateNames = registry.getBeanDefinitionNames();
        for (String beanName : candidateNames) {
            BeanDefinition beanDef = registry.getBeanDefinition(beanName);
            if (beanDef.getAttribute(ConfigurationClassUtils.CONFIGURATION_CLASS_ATTRIBUTE) != null) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Bean definition has already been processed as a configuration class: " + beanDef);
                }
            } else if (ConfigurationClassUtils.checkConfigurationClassCandidate(beanDef, this.metadataReaderFactory)) {
                configCandidates.add(new BeanDefinitionHolder(beanDef, beanName));
            }
        }
        if (configCandidates.isEmpty()) {
            return;
        }
        configCandidates.sort((bd1, bd2) -> {
            int i1 = ConfigurationClassUtils.getOrder(bd1.getBeanDefinition());
            int i2 = ConfigurationClassUtils.getOrder(bd2.getBeanDefinition());
            return Integer.compare(i1, i2);
        });
        SingletonBeanRegistry sbr = null;
        if (registry instanceof SingletonBeanRegistry) {
            SingletonBeanRegistry _sbr = (SingletonBeanRegistry) registry;
            sbr = _sbr;
            if (!this.localBeanNameGeneratorSet && (generator = (BeanNameGenerator) sbr.getSingleton(AnnotationConfigUtils.CONFIGURATION_BEAN_NAME_GENERATOR)) != null) {
                this.componentScanBeanNameGenerator = generator;
                this.importBeanNameGenerator = generator;
            }
        }
        if (this.environment == null) {
            this.environment = new StandardEnvironment();
        }
        ConfigurationClassParser parser = new ConfigurationClassParser(this.metadataReaderFactory, this.problemReporter, this.environment, this.resourceLoader, this.componentScanBeanNameGenerator, registry);
        Set<BeanDefinitionHolder> candidates = new LinkedHashSet<>(configCandidates);
        Set<ConfigurationClass> alreadyParsed = new HashSet<>(configCandidates.size());
        do {
            StartupStep processConfig = this.applicationStartup.start("spring.context.config-classes.parse");
            parser.parse(candidates);
            parser.validate();
            Set<ConfigurationClass> configClasses = new LinkedHashSet<>(parser.getConfigurationClasses());
            configClasses.removeAll(alreadyParsed);
            if (this.reader == null) {
                this.reader = new ConfigurationClassBeanDefinitionReader(registry, this.sourceExtractor, this.resourceLoader, this.environment, this.importBeanNameGenerator, parser.getImportRegistry());
            }
            this.reader.loadBeanDefinitions(configClasses);
            alreadyParsed.addAll(configClasses);
            processConfig.tag("classCount", () -> {
                return String.valueOf(configClasses.size());
            }).end();
            candidates.clear();
            if (registry.getBeanDefinitionCount() > candidateNames.length) {
                String[] newCandidateNames = registry.getBeanDefinitionNames();
                Set<String> oldCandidateNames = Set.of((Object[]) candidateNames);
                Set<String> alreadyParsedClasses = new HashSet<>();
                for (ConfigurationClass configurationClass : alreadyParsed) {
                    alreadyParsedClasses.add(configurationClass.getMetadata().getClassName());
                }
                for (String candidateName : newCandidateNames) {
                    if (!oldCandidateNames.contains(candidateName)) {
                        BeanDefinition bd = registry.getBeanDefinition(candidateName);
                        if (ConfigurationClassUtils.checkConfigurationClassCandidate(bd, this.metadataReaderFactory) && !alreadyParsedClasses.contains(bd.getBeanClassName())) {
                            candidates.add(new BeanDefinitionHolder(bd, candidateName));
                        }
                    }
                }
                candidateNames = newCandidateNames;
            }
        } while (!candidates.isEmpty());
        if (sbr != null && !sbr.containsSingleton(IMPORT_REGISTRY_BEAN_NAME)) {
            sbr.registerSingleton(IMPORT_REGISTRY_BEAN_NAME, parser.getImportRegistry());
        }
        this.propertySourceDescriptors = parser.getPropertySourceDescriptors();
        MetadataReaderFactory metadataReaderFactory = this.metadataReaderFactory;
        if (metadataReaderFactory instanceof CachingMetadataReaderFactory) {
            CachingMetadataReaderFactory cachingMetadataReaderFactory = (CachingMetadataReaderFactory) metadataReaderFactory;
            cachingMetadataReaderFactory.clearCache();
        }
    }

    public void enhanceConfigurationClasses(ConfigurableListableBeanFactory beanFactory) {
        StartupStep enhanceConfigClasses = this.applicationStartup.start("spring.context.config-classes.enhance");
        Map<String, AbstractBeanDefinition> configBeanDefs = new LinkedHashMap<>();
        for (String beanName : beanFactory.getBeanDefinitionNames()) {
            BeanDefinition beanDef = beanFactory.getBeanDefinition(beanName);
            Object configClassAttr = beanDef.getAttribute(ConfigurationClassUtils.CONFIGURATION_CLASS_ATTRIBUTE);
            AnnotationMetadata annotationMetadata = null;
            MethodMetadata methodMetadata = null;
            if (beanDef instanceof AnnotatedBeanDefinition) {
                AnnotatedBeanDefinition annotatedBeanDefinition = (AnnotatedBeanDefinition) beanDef;
                annotationMetadata = annotatedBeanDefinition.getMetadata();
                methodMetadata = annotatedBeanDefinition.getFactoryMethodMetadata();
            }
            if ((configClassAttr != null || methodMetadata != null) && (beanDef instanceof AbstractBeanDefinition)) {
                AbstractBeanDefinition abd = (AbstractBeanDefinition) beanDef;
                if (!abd.hasBeanClass()) {
                    boolean liteConfigurationCandidateWithoutBeanMethods = (!"lite".equals(configClassAttr) || annotationMetadata == null || ConfigurationClassUtils.hasBeanMethods(annotationMetadata)) ? false : true;
                    if (!liteConfigurationCandidateWithoutBeanMethods) {
                        try {
                            abd.resolveBeanClass(this.beanClassLoader);
                        } catch (Throwable ex) {
                            throw new IllegalStateException("Cannot load configuration class: " + beanDef.getBeanClassName(), ex);
                        }
                    }
                }
            }
            if ("full".equals(configClassAttr)) {
                if (!(beanDef instanceof AbstractBeanDefinition)) {
                    throw new BeanDefinitionStoreException("Cannot enhance @Configuration bean definition '" + beanName + "' since it is not stored in an AbstractBeanDefinition subclass");
                }
                AbstractBeanDefinition abd2 = (AbstractBeanDefinition) beanDef;
                if (this.logger.isWarnEnabled() && beanFactory.containsSingleton(beanName)) {
                    this.logger.warn("Cannot enhance @Configuration bean definition '" + beanName + "' since its singleton instance has been created too early. The typical cause is a non-static @Bean method with a BeanDefinitionRegistryPostProcessor return type: Consider declaring such methods as 'static' and/or marking the containing configuration class as 'proxyBeanMethods=false'.");
                }
                configBeanDefs.put(beanName, abd2);
            }
        }
        if (configBeanDefs.isEmpty()) {
            enhanceConfigClasses.end();
            return;
        }
        ConfigurationClassEnhancer enhancer = new ConfigurationClassEnhancer();
        for (Map.Entry<String, AbstractBeanDefinition> entry : configBeanDefs.entrySet()) {
            AbstractBeanDefinition beanDef2 = entry.getValue();
            beanDef2.setAttribute(AutoProxyUtils.PRESERVE_TARGET_CLASS_ATTRIBUTE, Boolean.TRUE);
            Class<?> configClass = beanDef2.getBeanClass();
            Class<?> enhancedClass = enhancer.enhance(configClass, this.beanClassLoader);
            if (configClass != enhancedClass) {
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace(String.format("Replacing bean definition '%s' existing class '%s' with enhanced class '%s'", entry.getKey(), configClass.getName(), enhancedClass.getName()));
                }
                beanDef2.setBeanClass(enhancedClass);
            }
        }
        enhanceConfigClasses.tag("classCount", () -> {
            return String.valueOf(configBeanDefs.keySet().size());
        }).end();
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/ConfigurationClassPostProcessor$ImportAwareBeanPostProcessor.class */
    private static class ImportAwareBeanPostProcessor implements InstantiationAwareBeanPostProcessor {
        private final BeanFactory beanFactory;

        public ImportAwareBeanPostProcessor(BeanFactory beanFactory) {
            this.beanFactory = beanFactory;
        }

        @Override // org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor
        public PropertyValues postProcessProperties(@Nullable PropertyValues pvs, Object bean, String beanName) {
            if (bean instanceof ConfigurationClassEnhancer.EnhancedConfiguration) {
                ConfigurationClassEnhancer.EnhancedConfiguration enhancedConfiguration = (ConfigurationClassEnhancer.EnhancedConfiguration) bean;
                enhancedConfiguration.setBeanFactory(this.beanFactory);
            }
            return pvs;
        }

        @Override // org.springframework.beans.factory.config.BeanPostProcessor
        public Object postProcessBeforeInitialization(Object bean, String beanName) {
            if (bean instanceof ImportAware) {
                ImportAware importAware = (ImportAware) bean;
                ImportRegistry ir = (ImportRegistry) this.beanFactory.getBean(ConfigurationClassPostProcessor.IMPORT_REGISTRY_BEAN_NAME, ImportRegistry.class);
                AnnotationMetadata importingClass = ir.getImportingClassFor(ClassUtils.getUserClass(bean).getName());
                if (importingClass != null) {
                    importAware.setImportMetadata(importingClass);
                }
            }
            return bean;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/ConfigurationClassPostProcessor$ImportAwareAotContribution.class */
    private static class ImportAwareAotContribution implements BeanFactoryInitializationAotContribution {
        private static final String BEAN_FACTORY_VARIABLE = "beanFactory";
        private static final ParameterizedTypeName STRING_STRING_MAP = ParameterizedTypeName.get((Class<?>) Map.class, String.class, String.class);
        private static final String MAPPINGS_VARIABLE = "mappings";
        private static final String BEAN_DEFINITION_VARIABLE = "beanDefinition";
        private static final String BEAN_NAME = "org.springframework.context.annotation.internalImportAwareAotProcessor";
        private final ConfigurableListableBeanFactory beanFactory;

        public ImportAwareAotContribution(ConfigurableListableBeanFactory beanFactory) {
            this.beanFactory = beanFactory;
        }

        @Override // org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution
        public void applyTo(GenerationContext generationContext, BeanFactoryInitializationCode beanFactoryInitializationCode) {
            Map<String, String> mappings = buildImportAwareMappings();
            if (!mappings.isEmpty()) {
                GeneratedMethod generatedMethod = beanFactoryInitializationCode.getMethods().add("addImportAwareBeanPostProcessors", method -> {
                    generateAddPostProcessorMethod(method, mappings);
                });
                beanFactoryInitializationCode.addInitializer(generatedMethod.toMethodReference());
                ResourceHints hints = generationContext.getRuntimeHints().resources();
                mappings.forEach((target, from) -> {
                    hints.registerType(TypeReference.of(from));
                });
            }
        }

        private void generateAddPostProcessorMethod(MethodSpec.Builder method, Map<String, String> mappings) {
            method.addJavadoc("Add ImportAwareBeanPostProcessor to support ImportAware beans.", new Object[0]);
            method.addModifiers(Modifier.PRIVATE);
            method.addParameter(DefaultListableBeanFactory.class, "beanFactory", new Modifier[0]);
            method.addCode(generateAddPostProcessorCode(mappings));
        }

        private CodeBlock generateAddPostProcessorCode(Map<String, String> mappings) {
            CodeBlock.Builder code = CodeBlock.builder();
            code.addStatement("$T $L = new $T<>()", STRING_STRING_MAP, MAPPINGS_VARIABLE, HashMap.class);
            mappings.forEach((type, from) -> {
                code.addStatement("$L.put($S, $S)", MAPPINGS_VARIABLE, type, from);
            });
            code.addStatement("$T $L = new $T($T.class)", RootBeanDefinition.class, "beanDefinition", RootBeanDefinition.class, ImportAwareAotBeanPostProcessor.class);
            code.addStatement("$L.setRole($T.ROLE_INFRASTRUCTURE)", "beanDefinition", BeanDefinition.class);
            code.addStatement("$L.setInstanceSupplier(() -> new $T($L))", "beanDefinition", ImportAwareAotBeanPostProcessor.class, MAPPINGS_VARIABLE);
            code.addStatement("$L.registerBeanDefinition($S, $L)", "beanFactory", BEAN_NAME, "beanDefinition");
            return code.build();
        }

        private Map<String, String> buildImportAwareMappings() {
            String target;
            AnnotationMetadata from;
            ImportRegistry importRegistry = (ImportRegistry) this.beanFactory.getBean(ConfigurationClassPostProcessor.IMPORT_REGISTRY_BEAN_NAME, ImportRegistry.class);
            Map<String, String> mappings = new LinkedHashMap<>();
            for (String name : this.beanFactory.getBeanDefinitionNames()) {
                Class<?> beanType = this.beanFactory.getType(name);
                if (beanType != null && ImportAware.class.isAssignableFrom(beanType) && (from = importRegistry.getImportingClassFor((target = ClassUtils.getUserClass(beanType).getName()))) != null) {
                    mappings.put(target, from.getClassName());
                }
            }
            return mappings;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/ConfigurationClassPostProcessor$PropertySourcesAotContribution.class */
    private static class PropertySourcesAotContribution implements BeanFactoryInitializationAotContribution {
        private static final String ENVIRONMENT_VARIABLE = "environment";
        private static final String RESOURCE_LOADER_VARIABLE = "resourceLoader";
        private final Log logger = LogFactory.getLog(getClass());
        private final List<PropertySourceDescriptor> descriptors;
        private final Function<String, Resource> resourceResolver;

        PropertySourcesAotContribution(List<PropertySourceDescriptor> descriptors, Function<String, Resource> resourceResolver) {
            this.descriptors = descriptors;
            this.resourceResolver = resourceResolver;
        }

        @Override // org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution
        public void applyTo(GenerationContext generationContext, BeanFactoryInitializationCode beanFactoryInitializationCode) {
            registerRuntimeHints(generationContext.getRuntimeHints());
            GeneratedMethod generatedMethod = beanFactoryInitializationCode.getMethods().add("processPropertySources", this::generateAddPropertySourceProcessorMethod);
            beanFactoryInitializationCode.addInitializer(generatedMethod.toMethodReference());
        }

        private void registerRuntimeHints(RuntimeHints hints) {
            for (PropertySourceDescriptor descriptor : this.descriptors) {
                Class<?> factoryClass = descriptor.propertySourceFactory();
                if (factoryClass != null) {
                    hints.reflection().registerType(factoryClass, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);
                }
                for (String location : descriptor.locations()) {
                    if (location.startsWith(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX) || (location.startsWith("classpath:") && (location.contains("*") || location.contains(CoreConstants.NA)))) {
                        if (this.logger.isWarnEnabled()) {
                            this.logger.warn("Runtime hint registration is not supported for the 'classpath*:' prefix or wildcards in @PropertySource locations. Please manually register a resource hint for each property source location represented by '%s'.".formatted(location));
                        }
                    } else {
                        Resource resource = this.resourceResolver.apply(location);
                        if (resource instanceof ClassPathResource) {
                            ClassPathResource classPathResource = (ClassPathResource) resource;
                            if (classPathResource.exists()) {
                                hints.resources().registerPattern(classPathResource.getPath());
                            }
                        }
                    }
                }
            }
        }

        private void generateAddPropertySourceProcessorMethod(MethodSpec.Builder method) {
            method.addJavadoc("Apply known @PropertySources to the environment.", new Object[0]);
            method.addModifiers(Modifier.PRIVATE);
            method.addParameter(ConfigurableEnvironment.class, "environment", new Modifier[0]);
            method.addParameter(ResourceLoader.class, RESOURCE_LOADER_VARIABLE, new Modifier[0]);
            method.addCode(generateAddPropertySourceProcessorCode());
        }

        private CodeBlock generateAddPropertySourceProcessorCode() {
            CodeBlock.Builder code = CodeBlock.builder();
            code.addStatement("$T $L = new $T($L, $L)", PropertySourceProcessor.class, "processor", PropertySourceProcessor.class, "environment", RESOURCE_LOADER_VARIABLE);
            code.beginControlFlow("try", new Object[0]);
            for (PropertySourceDescriptor descriptor : this.descriptors) {
                code.addStatement("$L.processPropertySource($L)", "processor", generatePropertySourceDescriptorCode(descriptor));
            }
            code.nextControlFlow("catch ($T ex)", IOException.class);
            code.addStatement("throw new $T(ex)", UncheckedIOException.class);
            code.endControlFlow();
            return code.build();
        }

        private CodeBlock generatePropertySourceDescriptorCode(PropertySourceDescriptor descriptor) {
            CodeBlock.Builder code = CodeBlock.builder();
            code.add("new $T(", PropertySourceDescriptor.class);
            CodeBlock values = (CodeBlock) descriptor.locations().stream().map(value -> {
                return CodeBlock.of("$S", value);
            }).collect(CodeBlock.joining(", "));
            if (descriptor.name() == null && descriptor.propertySourceFactory() == null && descriptor.encoding() == null && !descriptor.ignoreResourceNotFound()) {
                code.add("$L)", values);
            } else {
                List<CodeBlock> arguments = new ArrayList<>();
                arguments.add(CodeBlock.of("$T.of($L)", List.class, values));
                arguments.add(CodeBlock.of("$L", Boolean.valueOf(descriptor.ignoreResourceNotFound())));
                arguments.add(handleNull(descriptor.name(), () -> {
                    return CodeBlock.of("$S", descriptor.name());
                }));
                arguments.add(handleNull(descriptor.propertySourceFactory(), () -> {
                    return CodeBlock.of("$T.class", descriptor.propertySourceFactory());
                }));
                arguments.add(handleNull(descriptor.encoding(), () -> {
                    return CodeBlock.of("$S", descriptor.encoding());
                }));
                code.add(CodeBlock.join(arguments, ", "));
                code.add(")", new Object[0]);
            }
            return code.build();
        }

        private CodeBlock handleNull(@Nullable Object value, Supplier<CodeBlock> nonNull) {
            if (value == null) {
                return CodeBlock.of("null", new Object[0]);
            }
            return nonNull.get();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/ConfigurationClassPostProcessor$ConfigurationClassProxyBeanRegistrationCodeFragments.class */
    public static class ConfigurationClassProxyBeanRegistrationCodeFragments extends BeanRegistrationCodeFragmentsDecorator {
        private final RegisteredBean registeredBean;
        private final Class<?> proxyClass;

        public ConfigurationClassProxyBeanRegistrationCodeFragments(BeanRegistrationCodeFragments codeFragments, RegisteredBean registeredBean) {
            super(codeFragments);
            this.registeredBean = registeredBean;
            this.proxyClass = registeredBean.getBeanType().toClass();
        }

        @Override // org.springframework.beans.factory.aot.BeanRegistrationCodeFragmentsDecorator, org.springframework.beans.factory.aot.BeanRegistrationCodeFragments
        public CodeBlock generateSetBeanDefinitionPropertiesCode(GenerationContext generationContext, BeanRegistrationCode beanRegistrationCode, RootBeanDefinition beanDefinition, Predicate<String> attributeFilter) {
            CodeBlock.Builder code = CodeBlock.builder();
            code.add(super.generateSetBeanDefinitionPropertiesCode(generationContext, beanRegistrationCode, beanDefinition, attributeFilter));
            code.addStatement("$T.initializeConfigurationClass($T.class)", ConfigurationClassUtils.class, ClassUtils.getUserClass(this.proxyClass));
            return code.build();
        }

        @Override // org.springframework.beans.factory.aot.BeanRegistrationCodeFragmentsDecorator, org.springframework.beans.factory.aot.BeanRegistrationCodeFragments
        public CodeBlock generateInstanceSupplierCode(GenerationContext generationContext, BeanRegistrationCode beanRegistrationCode, boolean allowDirectSupplierShortcut) {
            Executable executableToUse = proxyExecutable(generationContext.getRuntimeHints(), this.registeredBean.resolveConstructorOrFactoryMethod());
            return new InstanceSupplierCodeGenerator(generationContext, beanRegistrationCode.getClassName(), beanRegistrationCode.getMethods(), allowDirectSupplierShortcut).generateCode(this.registeredBean, executableToUse);
        }

        private Executable proxyExecutable(RuntimeHints runtimeHints, Executable userExecutable) {
            if (userExecutable instanceof Constructor) {
                Constructor<?> userConstructor = (Constructor) userExecutable;
                try {
                    runtimeHints.reflection().registerConstructor(userConstructor, ExecutableMode.INTROSPECT);
                    return this.proxyClass.getConstructor(userExecutable.getParameterTypes());
                } catch (NoSuchMethodException ex) {
                    throw new IllegalStateException("No matching constructor found on proxy " + this.proxyClass, ex);
                }
            }
            return userExecutable;
        }
    }
}

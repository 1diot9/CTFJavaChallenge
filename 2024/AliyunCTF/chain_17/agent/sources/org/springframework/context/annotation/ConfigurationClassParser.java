package org.springframework.context.annotation;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Predicate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.parsing.Location;
import org.springframework.beans.factory.parsing.Problem;
import org.springframework.beans.factory.parsing.ProblemReporter;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PropertySourceDescriptor;
import org.springframework.core.io.support.PropertySourceProcessor;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/ConfigurationClassParser.class */
public class ConfigurationClassParser {
    private static final Predicate<String> DEFAULT_EXCLUSION_FILTER = className -> {
        return className.startsWith("java.lang.annotation.") || className.startsWith("org.springframework.stereotype.");
    };
    private static final Comparator<DeferredImportSelectorHolder> DEFERRED_IMPORT_COMPARATOR = (o1, o2) -> {
        return AnnotationAwareOrderComparator.INSTANCE.compare(o1.getImportSelector(), o2.getImportSelector());
    };
    private final MetadataReaderFactory metadataReaderFactory;
    private final ProblemReporter problemReporter;
    private final Environment environment;
    private final ResourceLoader resourceLoader;

    @Nullable
    private final PropertySourceRegistry propertySourceRegistry;
    private final BeanDefinitionRegistry registry;
    private final ComponentScanAnnotationParser componentScanParser;
    private final ConditionEvaluator conditionEvaluator;
    private final Log logger = LogFactory.getLog(getClass());
    private final Map<ConfigurationClass, ConfigurationClass> configurationClasses = new LinkedHashMap();
    private final Map<String, ConfigurationClass> knownSuperclasses = new HashMap();
    private final ImportStack importStack = new ImportStack();
    private final DeferredImportSelectorHandler deferredImportSelectorHandler = new DeferredImportSelectorHandler();
    private final SourceClass objectSourceClass = new SourceClass(Object.class);

    public ConfigurationClassParser(MetadataReaderFactory metadataReaderFactory, ProblemReporter problemReporter, Environment environment, ResourceLoader resourceLoader, BeanNameGenerator componentScanBeanNameGenerator, BeanDefinitionRegistry registry) {
        PropertySourceRegistry propertySourceRegistry;
        this.metadataReaderFactory = metadataReaderFactory;
        this.problemReporter = problemReporter;
        this.environment = environment;
        this.resourceLoader = resourceLoader;
        Environment environment2 = this.environment;
        if (environment2 instanceof ConfigurableEnvironment) {
            ConfigurableEnvironment ce = (ConfigurableEnvironment) environment2;
            propertySourceRegistry = new PropertySourceRegistry(new PropertySourceProcessor(ce, this.resourceLoader));
        } else {
            propertySourceRegistry = null;
        }
        this.propertySourceRegistry = propertySourceRegistry;
        this.registry = registry;
        this.componentScanParser = new ComponentScanAnnotationParser(environment, resourceLoader, componentScanBeanNameGenerator, registry);
        this.conditionEvaluator = new ConditionEvaluator(registry, environment, resourceLoader);
    }

    public void parse(Set<BeanDefinitionHolder> configCandidates) {
        for (BeanDefinitionHolder holder : configCandidates) {
            BeanDefinition bd = holder.getBeanDefinition();
            try {
                if (bd instanceof AnnotatedBeanDefinition) {
                    AnnotatedBeanDefinition annotatedBeanDef = (AnnotatedBeanDefinition) bd;
                    parse(annotatedBeanDef.getMetadata(), holder.getBeanName());
                } else {
                    if (bd instanceof AbstractBeanDefinition) {
                        AbstractBeanDefinition abstractBeanDef = (AbstractBeanDefinition) bd;
                        if (abstractBeanDef.hasBeanClass()) {
                            parse(abstractBeanDef.getBeanClass(), holder.getBeanName());
                        }
                    }
                    parse(bd.getBeanClassName(), holder.getBeanName());
                }
            } catch (BeanDefinitionStoreException ex) {
                throw ex;
            } catch (Throwable ex2) {
                throw new BeanDefinitionStoreException("Failed to parse configuration class [" + bd.getBeanClassName() + "]", ex2);
            }
        }
        this.deferredImportSelectorHandler.process();
    }

    protected final void parse(@Nullable String className, String beanName) throws IOException {
        Assert.notNull(className, "No bean class name for configuration class bean definition");
        MetadataReader reader = this.metadataReaderFactory.getMetadataReader(className);
        processConfigurationClass(new ConfigurationClass(reader, beanName), DEFAULT_EXCLUSION_FILTER);
    }

    protected final void parse(Class<?> clazz, String beanName) throws IOException {
        processConfigurationClass(new ConfigurationClass(clazz, beanName), DEFAULT_EXCLUSION_FILTER);
    }

    protected final void parse(AnnotationMetadata metadata, String beanName) throws IOException {
        processConfigurationClass(new ConfigurationClass(metadata, beanName), DEFAULT_EXCLUSION_FILTER);
    }

    public void validate() {
        for (ConfigurationClass configClass : this.configurationClasses.keySet()) {
            configClass.validate(this.problemReporter);
        }
    }

    public Set<ConfigurationClass> getConfigurationClasses() {
        return this.configurationClasses.keySet();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public List<PropertySourceDescriptor> getPropertySourceDescriptors() {
        return this.propertySourceRegistry != null ? this.propertySourceRegistry.getDescriptors() : Collections.emptyList();
    }

    protected void processConfigurationClass(ConfigurationClass configClass, Predicate<String> filter) throws IOException {
        if (this.conditionEvaluator.shouldSkip(configClass.getMetadata(), ConfigurationCondition.ConfigurationPhase.PARSE_CONFIGURATION)) {
            return;
        }
        ConfigurationClass existingClass = this.configurationClasses.get(configClass);
        if (existingClass != null) {
            if (configClass.isImported()) {
                if (existingClass.isImported()) {
                    existingClass.mergeImportedBy(configClass);
                    return;
                }
                return;
            } else {
                this.configurationClasses.remove(configClass);
                Collection<ConfigurationClass> values = this.knownSuperclasses.values();
                Objects.requireNonNull(configClass);
                values.removeIf((v1) -> {
                    return r1.equals(v1);
                });
            }
        }
        SourceClass sourceClass = null;
        try {
            sourceClass = asSourceClass(configClass, filter);
            do {
                sourceClass = doProcessConfigurationClass(configClass, sourceClass, filter);
            } while (sourceClass != null);
            this.configurationClasses.put(configClass, configClass);
        } catch (IOException ex) {
            throw new BeanDefinitionStoreException("I/O failure while processing configuration class [" + sourceClass + "]", ex);
        }
    }

    @Nullable
    protected final SourceClass doProcessConfigurationClass(ConfigurationClass configClass, SourceClass sourceClass, Predicate<String> filter) throws IOException {
        String superclass;
        if (configClass.getMetadata().isAnnotated(Component.class.getName())) {
            processMemberClasses(configClass, sourceClass, filter);
        }
        for (AnnotationAttributes propertySource : AnnotationConfigUtils.attributesForRepeatable(sourceClass.getMetadata(), (Class<? extends Annotation>) PropertySource.class, (Class<? extends Annotation>) PropertySources.class, true)) {
            if (this.propertySourceRegistry != null) {
                this.propertySourceRegistry.processPropertySource(propertySource);
            } else {
                this.logger.info("Ignoring @PropertySource annotation on [" + sourceClass.getMetadata().getClassName() + "]. Reason: Environment must implement ConfigurableEnvironment");
            }
        }
        Set<AnnotationAttributes> componentScans = AnnotationConfigUtils.attributesForRepeatable(sourceClass.getMetadata(), (Class<? extends Annotation>) ComponentScan.class, (Class<? extends Annotation>) ComponentScans.class, (Predicate<MergedAnnotation<? extends Annotation>>) (v0) -> {
            return v0.isDirectlyPresent();
        });
        if (componentScans.isEmpty()) {
            componentScans = AnnotationConfigUtils.attributesForRepeatable(sourceClass.getMetadata(), (Class<? extends Annotation>) ComponentScan.class, (Class<? extends Annotation>) ComponentScans.class, (Predicate<MergedAnnotation<? extends Annotation>>) (v0) -> {
                return v0.isMetaPresent();
            });
        }
        if (!componentScans.isEmpty() && !this.conditionEvaluator.shouldSkip(sourceClass.getMetadata(), ConfigurationCondition.ConfigurationPhase.REGISTER_BEAN)) {
            for (AnnotationAttributes componentScan : componentScans) {
                Set<BeanDefinitionHolder> scannedBeanDefinitions = this.componentScanParser.parse(componentScan, sourceClass.getMetadata().getClassName());
                for (BeanDefinitionHolder holder : scannedBeanDefinitions) {
                    BeanDefinition bdCand = holder.getBeanDefinition().getOriginatingBeanDefinition();
                    if (bdCand == null) {
                        bdCand = holder.getBeanDefinition();
                    }
                    if (ConfigurationClassUtils.checkConfigurationClassCandidate(bdCand, this.metadataReaderFactory)) {
                        parse(bdCand.getBeanClassName(), holder.getBeanName());
                    }
                }
            }
        }
        processImports(configClass, sourceClass, getImports(sourceClass), filter, true);
        AnnotationAttributes importResource = AnnotationConfigUtils.attributesFor(sourceClass.getMetadata(), (Class<?>) ImportResource.class);
        if (importResource != null) {
            String[] resources = importResource.getStringArray("locations");
            Class<? extends BeanDefinitionReader> readerClass = importResource.getClass("reader");
            for (String resource : resources) {
                String resolvedResource = this.environment.resolveRequiredPlaceholders(resource);
                configClass.addImportedResource(resolvedResource, readerClass);
            }
        }
        Set<MethodMetadata> beanMethods = retrieveBeanMethodMetadata(sourceClass);
        for (MethodMetadata methodMetadata : beanMethods) {
            configClass.addBeanMethod(new BeanMethod(methodMetadata, configClass));
        }
        processInterfaces(configClass, sourceClass);
        if (sourceClass.getMetadata().hasSuperClass() && (superclass = sourceClass.getMetadata().getSuperClassName()) != null && !superclass.startsWith("java") && !this.knownSuperclasses.containsKey(superclass)) {
            this.knownSuperclasses.put(superclass, configClass);
            return sourceClass.getSuperClass();
        }
        return null;
    }

    private void processMemberClasses(ConfigurationClass configClass, SourceClass sourceClass, Predicate<String> filter) throws IOException {
        Collection<SourceClass> memberClasses = sourceClass.getMemberClasses();
        if (!memberClasses.isEmpty()) {
            List<SourceClass> candidates = new ArrayList<>(memberClasses.size());
            for (SourceClass memberClass : memberClasses) {
                if (ConfigurationClassUtils.isConfigurationCandidate(memberClass.getMetadata()) && !memberClass.getMetadata().getClassName().equals(configClass.getMetadata().getClassName())) {
                    candidates.add(memberClass);
                }
            }
            OrderComparator.sort(candidates);
            for (SourceClass candidate : candidates) {
                if (this.importStack.contains(configClass)) {
                    this.problemReporter.error(new CircularImportProblem(configClass, this.importStack));
                } else {
                    this.importStack.push(configClass);
                    try {
                        processConfigurationClass(candidate.asConfigClass(configClass), filter);
                        this.importStack.pop();
                    } catch (Throwable th) {
                        this.importStack.pop();
                        throw th;
                    }
                }
            }
        }
    }

    private void processInterfaces(ConfigurationClass configClass, SourceClass sourceClass) throws IOException {
        for (SourceClass ifc : sourceClass.getInterfaces()) {
            Set<MethodMetadata> beanMethods = retrieveBeanMethodMetadata(ifc);
            for (MethodMetadata methodMetadata : beanMethods) {
                if (!methodMetadata.isAbstract()) {
                    configClass.addBeanMethod(new BeanMethod(methodMetadata, configClass));
                }
            }
            processInterfaces(configClass, ifc);
        }
    }

    private Set<MethodMetadata> retrieveBeanMethodMetadata(SourceClass sourceClass) {
        AnnotationMetadata original = sourceClass.getMetadata();
        Set<MethodMetadata> beanMethods = original.getAnnotatedMethods(Bean.class.getName());
        if (beanMethods.size() > 1 && (original instanceof StandardAnnotationMetadata)) {
            try {
                AnnotationMetadata asm = this.metadataReaderFactory.getMetadataReader(original.getClassName()).getAnnotationMetadata();
                Set<MethodMetadata> asmMethods = asm.getAnnotatedMethods(Bean.class.getName());
                if (asmMethods.size() >= beanMethods.size()) {
                    Set<MethodMetadata> candidateMethods = new LinkedHashSet<>(beanMethods);
                    Set<MethodMetadata> selectedMethods = new LinkedHashSet<>(asmMethods.size());
                    for (MethodMetadata asmMethod : asmMethods) {
                        Iterator<MethodMetadata> it = candidateMethods.iterator();
                        while (true) {
                            if (it.hasNext()) {
                                MethodMetadata beanMethod = it.next();
                                if (beanMethod.getMethodName().equals(asmMethod.getMethodName())) {
                                    selectedMethods.add(beanMethod);
                                    it.remove();
                                    break;
                                }
                            }
                        }
                    }
                    if (selectedMethods.size() == beanMethods.size()) {
                        beanMethods = selectedMethods;
                    }
                }
            } catch (IOException ex) {
                this.logger.debug("Failed to read class file via ASM for determining @Bean method order", ex);
            }
        }
        return beanMethods;
    }

    private Set<SourceClass> getImports(SourceClass sourceClass) throws IOException {
        Set<SourceClass> imports = new LinkedHashSet<>();
        Set<SourceClass> visited = new LinkedHashSet<>();
        collectImports(sourceClass, imports, visited);
        return imports;
    }

    private void collectImports(SourceClass sourceClass, Set<SourceClass> imports, Set<SourceClass> visited) throws IOException {
        if (visited.add(sourceClass)) {
            for (SourceClass annotation : sourceClass.getAnnotations()) {
                String annName = annotation.getMetadata().getClassName();
                if (!annName.equals(Import.class.getName())) {
                    collectImports(annotation, imports, visited);
                }
            }
            imports.addAll(sourceClass.getAnnotationAttributes(Import.class.getName(), "value"));
        }
    }

    private void processImports(ConfigurationClass configClass, SourceClass currentSourceClass, Collection<SourceClass> importCandidates, Predicate<String> exclusionFilter, boolean checkForCircularImports) {
        if (importCandidates.isEmpty()) {
            return;
        }
        if (checkForCircularImports && isChainedImportOnStack(configClass)) {
            this.problemReporter.error(new CircularImportProblem(configClass, this.importStack));
            return;
        }
        this.importStack.push(configClass);
        try {
            try {
                try {
                    for (SourceClass candidate : importCandidates) {
                        if (candidate.isAssignable(ImportSelector.class)) {
                            Class<?> candidateClass = candidate.loadClass();
                            ImportSelector selector = (ImportSelector) ParserStrategyUtils.instantiateClass(candidateClass, ImportSelector.class, this.environment, this.resourceLoader, this.registry);
                            Predicate<String> selectorFilter = selector.getExclusionFilter();
                            if (selectorFilter != null) {
                                exclusionFilter = exclusionFilter.or(selectorFilter);
                            }
                            if (selector instanceof DeferredImportSelector) {
                                DeferredImportSelector deferredImportSelector = (DeferredImportSelector) selector;
                                this.deferredImportSelectorHandler.handle(configClass, deferredImportSelector);
                            } else {
                                String[] importClassNames = selector.selectImports(currentSourceClass.getMetadata());
                                Collection<SourceClass> importSourceClasses = asSourceClasses(importClassNames, exclusionFilter);
                                processImports(configClass, currentSourceClass, importSourceClasses, exclusionFilter, false);
                            }
                        } else if (candidate.isAssignable(ImportBeanDefinitionRegistrar.class)) {
                            Class<?> candidateClass2 = candidate.loadClass();
                            ImportBeanDefinitionRegistrar registrar = (ImportBeanDefinitionRegistrar) ParserStrategyUtils.instantiateClass(candidateClass2, ImportBeanDefinitionRegistrar.class, this.environment, this.resourceLoader, this.registry);
                            configClass.addImportBeanDefinitionRegistrar(registrar, currentSourceClass.getMetadata());
                        } else {
                            this.importStack.registerImport(currentSourceClass.getMetadata(), candidate.getMetadata().getClassName());
                            processConfigurationClass(candidate.asConfigClass(configClass), exclusionFilter);
                        }
                    }
                } catch (Throwable ex) {
                    throw new BeanDefinitionStoreException("Failed to process import candidates for configuration class [" + configClass.getMetadata().getClassName() + "]: " + ex.getMessage(), ex);
                }
            } catch (BeanDefinitionStoreException ex2) {
                throw ex2;
            }
        } finally {
            this.importStack.pop();
        }
    }

    private boolean isChainedImportOnStack(ConfigurationClass configClass) {
        if (this.importStack.contains(configClass)) {
            String configClassName = configClass.getMetadata().getClassName();
            AnnotationMetadata importingClassFor = this.importStack.getImportingClassFor(configClassName);
            while (true) {
                AnnotationMetadata importingClass = importingClassFor;
                if (importingClass != null) {
                    if (configClassName.equals(importingClass.getClassName())) {
                        return true;
                    }
                    importingClassFor = this.importStack.getImportingClassFor(importingClass.getClassName());
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ImportRegistry getImportRegistry() {
        return this.importStack;
    }

    private SourceClass asSourceClass(ConfigurationClass configurationClass, Predicate<String> filter) throws IOException {
        AnnotationMetadata metadata = configurationClass.getMetadata();
        if (metadata instanceof StandardAnnotationMetadata) {
            StandardAnnotationMetadata standardAnnotationMetadata = (StandardAnnotationMetadata) metadata;
            return asSourceClass(standardAnnotationMetadata.getIntrospectedClass(), filter);
        }
        return asSourceClass(metadata.getClassName(), filter);
    }

    SourceClass asSourceClass(@Nullable Class<?> classType, Predicate<String> filter) throws IOException {
        if (classType == null || filter.test(classType.getName())) {
            return this.objectSourceClass;
        }
        try {
            for (Annotation ann : classType.getDeclaredAnnotations()) {
                AnnotationUtils.validateAnnotation(ann);
            }
            return new SourceClass(classType);
        } catch (Throwable th) {
            return asSourceClass(classType.getName(), filter);
        }
    }

    private Collection<SourceClass> asSourceClasses(String[] classNames, Predicate<String> filter) throws IOException {
        List<SourceClass> annotatedClasses = new ArrayList<>(classNames.length);
        for (String className : classNames) {
            annotatedClasses.add(asSourceClass(className, filter));
        }
        return annotatedClasses;
    }

    SourceClass asSourceClass(@Nullable String className, Predicate<String> filter) throws IOException {
        if (className == null || filter.test(className)) {
            return this.objectSourceClass;
        }
        if (className.startsWith("java")) {
            try {
                return new SourceClass(ClassUtils.forName(className, this.resourceLoader.getClassLoader()));
            } catch (ClassNotFoundException ex) {
                throw new IOException("Failed to load class [" + className + "]", ex);
            }
        }
        return new SourceClass(this.metadataReaderFactory.getMetadataReader(className));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/ConfigurationClassParser$ImportStack.class */
    public static class ImportStack extends ArrayDeque<ConfigurationClass> implements ImportRegistry {
        private final MultiValueMap<String, AnnotationMetadata> imports = new LinkedMultiValueMap();

        private ImportStack() {
        }

        public void registerImport(AnnotationMetadata importingClass, String importedClass) {
            this.imports.add(importedClass, importingClass);
        }

        @Override // org.springframework.context.annotation.ImportRegistry
        @Nullable
        public AnnotationMetadata getImportingClassFor(String importedClass) {
            return (AnnotationMetadata) CollectionUtils.lastElement((List) this.imports.get(importedClass));
        }

        @Override // org.springframework.context.annotation.ImportRegistry
        public void removeImportingClass(String importingClass) {
            Iterator<AnnotationMetadata> it = this.imports.values().iterator();
            while (it.hasNext()) {
                List<AnnotationMetadata> list = (List) it.next();
                Iterator<AnnotationMetadata> iterator = list.iterator();
                while (true) {
                    if (!iterator.hasNext()) {
                        break;
                    } else if (iterator.next().getClassName().equals(importingClass)) {
                        iterator.remove();
                        break;
                    }
                }
            }
        }

        @Override // java.util.AbstractCollection
        public String toString() {
            StringJoiner joiner = new StringJoiner("->", "[", "]");
            Iterator<ConfigurationClass> it = iterator();
            while (it.hasNext()) {
                ConfigurationClass configurationClass = it.next();
                joiner.add(configurationClass.getSimpleName());
            }
            return joiner.toString();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/ConfigurationClassParser$DeferredImportSelectorHandler.class */
    public class DeferredImportSelectorHandler {

        @Nullable
        private List<DeferredImportSelectorHolder> deferredImportSelectors = new ArrayList();

        private DeferredImportSelectorHandler() {
        }

        public void handle(ConfigurationClass configClass, DeferredImportSelector importSelector) {
            DeferredImportSelectorHolder holder = new DeferredImportSelectorHolder(configClass, importSelector);
            if (this.deferredImportSelectors == null) {
                DeferredImportSelectorGroupingHandler handler = new DeferredImportSelectorGroupingHandler();
                handler.register(holder);
                handler.processGroupImports();
                return;
            }
            this.deferredImportSelectors.add(holder);
        }

        public void process() {
            List<DeferredImportSelectorHolder> deferredImports = this.deferredImportSelectors;
            this.deferredImportSelectors = null;
            if (deferredImports != null) {
                try {
                    DeferredImportSelectorGroupingHandler handler = new DeferredImportSelectorGroupingHandler();
                    deferredImports.sort(ConfigurationClassParser.DEFERRED_IMPORT_COMPARATOR);
                    Objects.requireNonNull(handler);
                    deferredImports.forEach(handler::register);
                    handler.processGroupImports();
                } finally {
                    this.deferredImportSelectors = new ArrayList();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/ConfigurationClassParser$DeferredImportSelectorGroupingHandler.class */
    public class DeferredImportSelectorGroupingHandler {
        private final Map<Object, DeferredImportSelectorGrouping> groupings = new LinkedHashMap();
        private final Map<AnnotationMetadata, ConfigurationClass> configurationClasses = new HashMap();

        private DeferredImportSelectorGroupingHandler() {
        }

        public void register(DeferredImportSelectorHolder deferredImport) {
            Class<? extends DeferredImportSelector.Group> group = deferredImport.getImportSelector().getImportGroup();
            DeferredImportSelectorGrouping grouping = this.groupings.computeIfAbsent(group != null ? group : deferredImport, key -> {
                return new DeferredImportSelectorGrouping(createGroup(group));
            });
            grouping.add(deferredImport);
            this.configurationClasses.put(deferredImport.getConfigurationClass().getMetadata(), deferredImport.getConfigurationClass());
        }

        public void processGroupImports() {
            for (DeferredImportSelectorGrouping grouping : this.groupings.values()) {
                Predicate<String> exclusionFilter = grouping.getCandidateFilter();
                grouping.getImports().forEach(entry -> {
                    ConfigurationClass configurationClass = this.configurationClasses.get(entry.getMetadata());
                    try {
                        ConfigurationClassParser.this.processImports(configurationClass, ConfigurationClassParser.this.asSourceClass(configurationClass, (Predicate<String>) exclusionFilter), Collections.singleton(ConfigurationClassParser.this.asSourceClass(entry.getImportClassName(), (Predicate<String>) exclusionFilter)), exclusionFilter, false);
                    } catch (BeanDefinitionStoreException ex) {
                        throw ex;
                    } catch (Throwable ex2) {
                        throw new BeanDefinitionStoreException("Failed to process import candidates for configuration class [" + configurationClass.getMetadata().getClassName() + "]", ex2);
                    }
                });
            }
        }

        private DeferredImportSelector.Group createGroup(@Nullable Class<? extends DeferredImportSelector.Group> type) {
            Class<? extends DeferredImportSelector.Group> effectiveType = type != null ? type : DefaultDeferredImportSelectorGroup.class;
            return (DeferredImportSelector.Group) ParserStrategyUtils.instantiateClass(effectiveType, DeferredImportSelector.Group.class, ConfigurationClassParser.this.environment, ConfigurationClassParser.this.resourceLoader, ConfigurationClassParser.this.registry);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/ConfigurationClassParser$DeferredImportSelectorHolder.class */
    public static class DeferredImportSelectorHolder {
        private final ConfigurationClass configurationClass;
        private final DeferredImportSelector importSelector;

        public DeferredImportSelectorHolder(ConfigurationClass configClass, DeferredImportSelector selector) {
            this.configurationClass = configClass;
            this.importSelector = selector;
        }

        public ConfigurationClass getConfigurationClass() {
            return this.configurationClass;
        }

        public DeferredImportSelector getImportSelector() {
            return this.importSelector;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/ConfigurationClassParser$DeferredImportSelectorGrouping.class */
    public static class DeferredImportSelectorGrouping {
        private final DeferredImportSelector.Group group;
        private final List<DeferredImportSelectorHolder> deferredImports = new ArrayList();

        DeferredImportSelectorGrouping(DeferredImportSelector.Group group) {
            this.group = group;
        }

        public void add(DeferredImportSelectorHolder deferredImport) {
            this.deferredImports.add(deferredImport);
        }

        public Iterable<DeferredImportSelector.Group.Entry> getImports() {
            for (DeferredImportSelectorHolder deferredImport : this.deferredImports) {
                this.group.process(deferredImport.getConfigurationClass().getMetadata(), deferredImport.getImportSelector());
            }
            return this.group.selectImports();
        }

        public Predicate<String> getCandidateFilter() {
            Predicate<String> mergedFilter = ConfigurationClassParser.DEFAULT_EXCLUSION_FILTER;
            for (DeferredImportSelectorHolder deferredImport : this.deferredImports) {
                Predicate<String> selectorFilter = deferredImport.getImportSelector().getExclusionFilter();
                if (selectorFilter != null) {
                    mergedFilter = mergedFilter.or(selectorFilter);
                }
            }
            return mergedFilter;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/ConfigurationClassParser$DefaultDeferredImportSelectorGroup.class */
    private static class DefaultDeferredImportSelectorGroup implements DeferredImportSelector.Group {
        private final List<DeferredImportSelector.Group.Entry> imports = new ArrayList();

        private DefaultDeferredImportSelectorGroup() {
        }

        @Override // org.springframework.context.annotation.DeferredImportSelector.Group
        public void process(AnnotationMetadata metadata, DeferredImportSelector selector) {
            for (String importClassName : selector.selectImports(metadata)) {
                this.imports.add(new DeferredImportSelector.Group.Entry(metadata, importClassName));
            }
        }

        @Override // org.springframework.context.annotation.DeferredImportSelector.Group
        public Iterable<DeferredImportSelector.Group.Entry> selectImports() {
            return this.imports;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/ConfigurationClassParser$SourceClass.class */
    public class SourceClass implements Ordered {
        private final Object source;
        private final AnnotationMetadata metadata;

        public SourceClass(Object source) {
            this.source = source;
            if (source instanceof Class) {
                Class<?> sourceClass = (Class) source;
                this.metadata = AnnotationMetadata.introspect(sourceClass);
            } else {
                this.metadata = ((MetadataReader) source).getAnnotationMetadata();
            }
        }

        public final AnnotationMetadata getMetadata() {
            return this.metadata;
        }

        @Override // org.springframework.core.Ordered
        public int getOrder() {
            Integer order = ConfigurationClassUtils.getOrder(this.metadata);
            if (order != null) {
                return order.intValue();
            }
            return Integer.MAX_VALUE;
        }

        public Class<?> loadClass() throws ClassNotFoundException {
            Object obj = this.source;
            if (obj instanceof Class) {
                Class<?> sourceClass = (Class) obj;
                return sourceClass;
            }
            String className = ((MetadataReader) this.source).getClassMetadata().getClassName();
            return ClassUtils.forName(className, ConfigurationClassParser.this.resourceLoader.getClassLoader());
        }

        public boolean isAssignable(Class<?> clazz) throws IOException {
            Object obj = this.source;
            if (obj instanceof Class) {
                Class<?> sourceClass = (Class) obj;
                return clazz.isAssignableFrom(sourceClass);
            }
            return new AssignableTypeFilter(clazz).match((MetadataReader) this.source, ConfigurationClassParser.this.metadataReaderFactory);
        }

        public ConfigurationClass asConfigClass(ConfigurationClass importedBy) {
            Object obj = this.source;
            if (obj instanceof Class) {
                Class<?> sourceClass = (Class) obj;
                return new ConfigurationClass(sourceClass, importedBy);
            }
            return new ConfigurationClass((MetadataReader) this.source, importedBy);
        }

        public Collection<SourceClass> getMemberClasses() throws IOException {
            Object sourceToProcess = this.source;
            if (sourceToProcess instanceof Class) {
                Class<?> sourceClass = (Class) sourceToProcess;
                try {
                    Class<?>[] declaredClasses = sourceClass.getDeclaredClasses();
                    List<SourceClass> members = new ArrayList<>(declaredClasses.length);
                    for (Class<?> declaredClass : declaredClasses) {
                        members.add(ConfigurationClassParser.this.asSourceClass(declaredClass, ConfigurationClassParser.DEFAULT_EXCLUSION_FILTER));
                    }
                    return members;
                } catch (NoClassDefFoundError e) {
                    sourceToProcess = ConfigurationClassParser.this.metadataReaderFactory.getMetadataReader(sourceClass.getName());
                }
            }
            MetadataReader sourceReader = (MetadataReader) sourceToProcess;
            String[] memberClassNames = sourceReader.getClassMetadata().getMemberClassNames();
            List<SourceClass> members2 = new ArrayList<>(memberClassNames.length);
            for (String memberClassName : memberClassNames) {
                try {
                    members2.add(ConfigurationClassParser.this.asSourceClass(memberClassName, ConfigurationClassParser.DEFAULT_EXCLUSION_FILTER));
                } catch (IOException e2) {
                    if (ConfigurationClassParser.this.logger.isDebugEnabled()) {
                        ConfigurationClassParser.this.logger.debug("Failed to resolve member class [" + memberClassName + "] - not considering it as a configuration class candidate");
                    }
                }
            }
            return members2;
        }

        public SourceClass getSuperClass() throws IOException {
            Object obj = this.source;
            if (obj instanceof Class) {
                Class<?> sourceClass = (Class) obj;
                return ConfigurationClassParser.this.asSourceClass(sourceClass.getSuperclass(), ConfigurationClassParser.DEFAULT_EXCLUSION_FILTER);
            }
            return ConfigurationClassParser.this.asSourceClass(((MetadataReader) this.source).getClassMetadata().getSuperClassName(), ConfigurationClassParser.DEFAULT_EXCLUSION_FILTER);
        }

        public Set<SourceClass> getInterfaces() throws IOException {
            Set<SourceClass> result = new LinkedHashSet<>();
            Object obj = this.source;
            if (obj instanceof Class) {
                Class<?> sourceClass = (Class) obj;
                for (Class<?> ifcClass : sourceClass.getInterfaces()) {
                    result.add(ConfigurationClassParser.this.asSourceClass(ifcClass, ConfigurationClassParser.DEFAULT_EXCLUSION_FILTER));
                }
            } else {
                for (String className : this.metadata.getInterfaceNames()) {
                    result.add(ConfigurationClassParser.this.asSourceClass(className, ConfigurationClassParser.DEFAULT_EXCLUSION_FILTER));
                }
            }
            return result;
        }

        public Set<SourceClass> getAnnotations() {
            Set<SourceClass> result = new LinkedHashSet<>();
            Object obj = this.source;
            if (obj instanceof Class) {
                Class<?> sourceClass = (Class) obj;
                for (Annotation ann : sourceClass.getDeclaredAnnotations()) {
                    Class<?> annType = ann.annotationType();
                    if (!annType.getName().startsWith("java")) {
                        try {
                            result.add(ConfigurationClassParser.this.asSourceClass(annType, ConfigurationClassParser.DEFAULT_EXCLUSION_FILTER));
                        } catch (Throwable th) {
                        }
                    }
                }
            } else {
                for (String className : this.metadata.getAnnotationTypes()) {
                    if (!className.startsWith("java")) {
                        try {
                            result.add(getRelated(className));
                        } catch (Throwable th2) {
                        }
                    }
                }
            }
            return result;
        }

        public Collection<SourceClass> getAnnotationAttributes(String annType, String attribute) throws IOException {
            Map<String, Object> annotationAttributes = this.metadata.getAnnotationAttributes(annType, true);
            if (annotationAttributes == null || !annotationAttributes.containsKey(attribute)) {
                return Collections.emptySet();
            }
            String[] classNames = (String[]) annotationAttributes.get(attribute);
            Set<SourceClass> result = new LinkedHashSet<>();
            for (String className : classNames) {
                result.add(getRelated(className));
            }
            return result;
        }

        private SourceClass getRelated(String className) throws IOException {
            Object obj = this.source;
            if (obj instanceof Class) {
                Class<?> sourceClass = (Class) obj;
                try {
                    Class<?> clazz = ClassUtils.forName(className, sourceClass.getClassLoader());
                    return ConfigurationClassParser.this.asSourceClass(clazz, ConfigurationClassParser.DEFAULT_EXCLUSION_FILTER);
                } catch (ClassNotFoundException ex) {
                    if (className.startsWith("java")) {
                        throw new IOException("Failed to load class [" + className + "]", ex);
                    }
                    return new SourceClass(ConfigurationClassParser.this.metadataReaderFactory.getMetadataReader(className));
                }
            }
            return ConfigurationClassParser.this.asSourceClass(className, ConfigurationClassParser.DEFAULT_EXCLUSION_FILTER);
        }

        public boolean equals(@Nullable Object other) {
            if (this != other) {
                if (other instanceof SourceClass) {
                    SourceClass that = (SourceClass) other;
                    if (this.metadata.getClassName().equals(that.metadata.getClassName())) {
                    }
                }
                return false;
            }
            return true;
        }

        public int hashCode() {
            return this.metadata.getClassName().hashCode();
        }

        public String toString() {
            return this.metadata.getClassName();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/ConfigurationClassParser$CircularImportProblem.class */
    public static class CircularImportProblem extends Problem {
        public CircularImportProblem(ConfigurationClass attemptedImport, Deque<ConfigurationClass> importStack) {
            super(String.format("A circular @Import has been detected: Illegal attempt by @Configuration class '%s' to import class '%s' as '%s' is already present in the current import stack %s", importStack.element().getSimpleName(), attemptedImport.getSimpleName(), attemptedImport.getSimpleName(), importStack), new Location(importStack.element().getResource(), attemptedImport.getMetadata()));
        }
    }
}

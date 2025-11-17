package org.springframework.boot.logging.logback;

import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.joran.spi.ElementSelector;
import ch.qos.logback.core.joran.spi.RuleStore;
import ch.qos.logback.core.joran.util.PropertySetter;
import ch.qos.logback.core.joran.util.beans.BeanDescription;
import ch.qos.logback.core.model.ComponentModel;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.ModelUtil;
import ch.qos.logback.core.model.processor.DefaultProcessor;
import ch.qos.logback.core.model.processor.ModelInterpretationContext;
import ch.qos.logback.core.spi.ContextAware;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.util.AggregationType;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.springframework.aot.generate.GenerationContext;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.SerializationHints;
import org.springframework.aot.hint.TypeReference;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution;
import org.springframework.beans.factory.aot.BeanFactoryInitializationCode;
import org.springframework.boot.logging.LoggingInitializationContext;
import org.springframework.core.CollectionFactory;
import org.springframework.core.NativeDetector;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.function.SingletonSupplier;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/logging/logback/SpringBootJoranConfigurator.class */
class SpringBootJoranConfigurator extends JoranConfigurator {
    private final LoggingInitializationContext initializationContext;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SpringBootJoranConfigurator(LoggingInitializationContext initializationContext) {
        this.initializationContext = initializationContext;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.classic.joran.JoranConfigurator, ch.qos.logback.core.joran.JoranConfiguratorBase, ch.qos.logback.core.joran.GenericXMLConfigurator
    public void sanityCheck(Model topModel) {
        super.sanityCheck(topModel);
        performCheck(new SpringProfileIfNestedWithinSecondPhaseElementSanityChecker(), topModel);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.classic.joran.JoranConfigurator, ch.qos.logback.core.joran.JoranConfiguratorBase, ch.qos.logback.core.joran.GenericXMLConfigurator
    public void addModelHandlerAssociations(DefaultProcessor defaultProcessor) {
        defaultProcessor.addHandler(SpringPropertyModel.class, (handlerContext, handlerMic) -> {
            return new SpringPropertyModelHandler(this.context, this.initializationContext.getEnvironment());
        });
        defaultProcessor.addHandler(SpringProfileModel.class, (handlerContext2, handlerMic2) -> {
            return new SpringProfileModelHandler(this.context, this.initializationContext.getEnvironment());
        });
        super.addModelHandlerAssociations(defaultProcessor);
    }

    @Override // ch.qos.logback.classic.joran.JoranConfigurator, ch.qos.logback.core.joran.JoranConfiguratorBase, ch.qos.logback.core.joran.GenericXMLConfigurator
    public void addElementSelectorAndActionAssociations(RuleStore ruleStore) {
        super.addElementSelectorAndActionAssociations(ruleStore);
        ruleStore.addRule(new ElementSelector("configuration/springProperty"), SpringPropertyAction::new);
        ruleStore.addRule(new ElementSelector("*/springProfile"), SpringProfileAction::new);
        ruleStore.addTransparentPathPart("springProfile");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean configureUsingAotGeneratedArtifacts() {
        if (!new PatternRules(getContext()).load()) {
            return false;
        }
        Model model = new ModelReader().read();
        processModel(model);
        registerSafeConfiguration(model);
        return true;
    }

    @Override // ch.qos.logback.core.joran.GenericXMLConfigurator
    public void processModel(Model model) {
        super.processModel(model);
        if (!NativeDetector.inNativeImage() && isAotProcessingInProgress()) {
            getContext().putObject(BeanFactoryInitializationAotContribution.class.getName(), new LogbackConfigurationAotContribution(model, getModelInterpretationContext(), getContext()));
        }
    }

    private boolean isAotProcessingInProgress() {
        return Boolean.getBoolean("spring.aot.processing");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/logging/logback/SpringBootJoranConfigurator$LogbackConfigurationAotContribution.class */
    public static final class LogbackConfigurationAotContribution implements BeanFactoryInitializationAotContribution {
        private final ModelWriter modelWriter;
        private final PatternRules patternRules;

        private LogbackConfigurationAotContribution(Model model, ModelInterpretationContext interpretationContext, Context context) {
            this.modelWriter = new ModelWriter(model, interpretationContext);
            this.patternRules = new PatternRules(context);
        }

        @Override // org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution
        public void applyTo(GenerationContext generationContext, BeanFactoryInitializationCode beanFactoryInitializationCode) {
            this.modelWriter.writeTo(generationContext);
            this.patternRules.save(generationContext);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/logging/logback/SpringBootJoranConfigurator$ModelWriter.class */
    private static final class ModelWriter {
        private static final String MODEL_RESOURCE_LOCATION = "META-INF/spring/logback-model";
        private final Model model;
        private final ModelInterpretationContext modelInterpretationContext;

        private ModelWriter(Model model, ModelInterpretationContext modelInterpretationContext) {
            this.model = model;
            this.modelInterpretationContext = modelInterpretationContext;
        }

        private void writeTo(GenerationContext generationContext) {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            try {
                ObjectOutputStream output = new ObjectOutputStream(bytes);
                try {
                    output.writeObject(this.model);
                    output.close();
                    Resource modelResource = new ByteArrayResource(bytes.toByteArray());
                    generationContext.getGeneratedFiles().addResourceFile(MODEL_RESOURCE_LOCATION, modelResource);
                    generationContext.getRuntimeHints().resources().registerPattern(MODEL_RESOURCE_LOCATION);
                    SerializationHints serializationHints = generationContext.getRuntimeHints().serialization();
                    Set<Class<? extends Serializable>> serializationTypes = serializationTypes(this.model);
                    Objects.requireNonNull(serializationHints);
                    serializationTypes.forEach(serializationHints::registerType);
                    reflectionTypes(this.model).forEach(type -> {
                        generationContext.getRuntimeHints().reflection().registerType(TypeReference.of(type), MemberCategory.INTROSPECT_PUBLIC_METHODS, MemberCategory.INVOKE_PUBLIC_METHODS, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);
                    });
                } finally {
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        private Set<Class<? extends Serializable>> serializationTypes(Model model) {
            HashSet hashSet = new HashSet();
            Class<?> candidate = model.getClass();
            while (Model.class.isAssignableFrom(candidate)) {
                if (hashSet.add(candidate)) {
                    ReflectionUtils.doWithFields(candidate, field -> {
                        if (Modifier.isStatic(field.getModifiers())) {
                            return;
                        }
                        ReflectionUtils.makeAccessible(field);
                        Object value = field.get(model);
                        if (value != null) {
                            Class<?> fieldType = value.getClass();
                            if (Serializable.class.isAssignableFrom(fieldType)) {
                                hashSet.add(fieldType);
                            }
                        }
                    });
                    candidate = candidate.getSuperclass();
                }
            }
            for (Model submodel : model.getSubModels()) {
                hashSet.addAll(serializationTypes(submodel));
            }
            return hashSet;
        }

        private Set<String> reflectionTypes(Model model) {
            return reflectionTypes(model, () -> {
                return null;
            });
        }

        private Set<String> reflectionTypes(Model model, Supplier<Object> parent) {
            Set<String> reflectionTypes = new HashSet<>();
            Class<?> componentType = determineType(model, parent);
            if (componentType != null) {
                processComponent(componentType, reflectionTypes);
            }
            Supplier<Object> componentSupplier = SingletonSupplier.ofNullable(() -> {
                return instantiate(componentType);
            });
            for (Model submodel : model.getSubModels()) {
                reflectionTypes.addAll(reflectionTypes(submodel, componentSupplier));
            }
            return reflectionTypes;
        }

        private Class<?> determineType(Model model, Supplier<Object> parentSupplier) {
            String str;
            if (model instanceof ComponentModel) {
                ComponentModel componentModel = (ComponentModel) model;
                str = componentModel.getClassName();
            } else {
                str = null;
            }
            String className = str;
            if (className != null) {
                return loadImportType(className);
            }
            String tag = model.getTag();
            if (tag != null) {
                String className2 = this.modelInterpretationContext.getDefaultNestedComponentRegistry().findDefaultComponentTypeByTag(tag);
                if (className2 != null) {
                    return loadImportType(className2);
                }
                return inferTypeFromParent(parentSupplier, tag);
            }
            return null;
        }

        private Class<?> loadImportType(String className) {
            return loadComponentType(this.modelInterpretationContext.getImport(className));
        }

        private Class<?> inferTypeFromParent(Supplier<Object> parentSupplier, String tag) {
            Object parent = parentSupplier.get();
            if (parent != null) {
                try {
                    PropertySetter propertySetter = new PropertySetter(this.modelInterpretationContext.getBeanDescriptionCache(), parent);
                    Class<?> typeFromPropertySetter = propertySetter.getClassNameViaImplicitRules(tag, AggregationType.AS_COMPLEX_PROPERTY, this.modelInterpretationContext.getDefaultNestedComponentRegistry());
                    return typeFromPropertySetter;
                } catch (Exception e) {
                    return null;
                }
            }
            return null;
        }

        private Class<?> loadComponentType(String componentType) {
            try {
                return ClassUtils.forName(this.modelInterpretationContext.subst(componentType), getClass().getClassLoader());
            } catch (Throwable ex) {
                throw new RuntimeException("Failed to load component type '" + componentType + "'", ex);
            }
        }

        private Object instantiate(Class<?> type) {
            try {
                return type.getConstructor(new Class[0]).newInstance(new Object[0]);
            } catch (Exception e) {
                return null;
            }
        }

        private void processComponent(Class<?> componentType, Set<String> reflectionTypes) {
            BeanDescription beanDescription = this.modelInterpretationContext.getBeanDescriptionCache().getBeanDescription(componentType);
            reflectionTypes.addAll(parameterTypesNames(beanDescription.getPropertyNameToAdder().values()));
            reflectionTypes.addAll(parameterTypesNames(beanDescription.getPropertyNameToSetter().values()));
            reflectionTypes.add(componentType.getCanonicalName());
        }

        private Collection<String> parameterTypesNames(Collection<Method> methods) {
            return methods.stream().filter(method -> {
                return (method.getDeclaringClass().equals(ContextAware.class) || method.getDeclaringClass().equals(ContextAwareBase.class)) ? false : true;
            }).map((v0) -> {
                return v0.getParameterTypes();
            }).flatMap((v0) -> {
                return Stream.of(v0);
            }).filter(type -> {
                return (type.isPrimitive() || type.equals(String.class)) ? false : true;
            }).map(type2 -> {
                return type2.isArray() ? type2.getComponentType() : type2;
            }).map((v0) -> {
                return v0.getName();
            }).toList();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/logging/logback/SpringBootJoranConfigurator$ModelReader.class */
    private static final class ModelReader {
        private ModelReader() {
        }

        private Model read() {
            try {
                InputStream modelInput = getClass().getClassLoader().getResourceAsStream("META-INF/spring/logback-model");
                try {
                    ObjectInputStream input = new ObjectInputStream(modelInput);
                    try {
                        Model model = (Model) input.readObject();
                        ModelUtil.resetForReuse(model);
                        input.close();
                        if (modelInput != null) {
                            modelInput.close();
                        }
                        return model;
                    } catch (Throwable th) {
                        try {
                            input.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                        throw th;
                    }
                } finally {
                }
            } catch (Exception ex) {
                throw new RuntimeException("Failed to load model from 'META-INF/spring/logback-model'", ex);
            }
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/logging/logback/SpringBootJoranConfigurator$PatternRules.class */
    private static final class PatternRules {
        private static final String RESOURCE_LOCATION = "META-INF/spring/logback-pattern-rules";
        private final Context context;

        private PatternRules(Context context) {
            this.context = context;
        }

        private boolean load() {
            try {
                ClassPathResource resource = new ClassPathResource(RESOURCE_LOCATION);
                if (!resource.exists()) {
                    return false;
                }
                Properties properties = PropertiesLoaderUtils.loadProperties(resource);
                Map<String, String> patternRuleRegistry = getRegistryMap();
                for (String word : properties.stringPropertyNames()) {
                    patternRuleRegistry.put(word, properties.getProperty(word));
                }
                return true;
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

        private Map<String, String> getRegistryMap() {
            Map<String, String> patternRuleRegistry = (Map) this.context.getObject(CoreConstants.PATTERN_RULE_REGISTRY);
            if (patternRuleRegistry == null) {
                patternRuleRegistry = new HashMap();
                this.context.putObject(CoreConstants.PATTERN_RULE_REGISTRY, patternRuleRegistry);
            }
            return patternRuleRegistry;
        }

        private void save(GenerationContext generationContext) {
            Map<String, String> registryMap = getRegistryMap();
            generationContext.getGeneratedFiles().addResourceFile(RESOURCE_LOCATION, () -> {
                return asInputStream(registryMap);
            });
            generationContext.getRuntimeHints().resources().registerPattern(RESOURCE_LOCATION);
            for (String ruleClassName : registryMap.values()) {
                generationContext.getRuntimeHints().reflection().registerType(TypeReference.of(ruleClassName), MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);
            }
        }

        private InputStream asInputStream(Map<String, String> patternRuleRegistry) {
            Properties properties = CollectionFactory.createSortedProperties(true);
            Objects.requireNonNull(properties);
            patternRuleRegistry.forEach(properties::setProperty);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            try {
                properties.store(bytes, "");
                return new ByteArrayInputStream(bytes.toByteArray());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}

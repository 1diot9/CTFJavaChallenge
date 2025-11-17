package org.springframework.http.converter.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.cfg.DatatypeFeature;
import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.function.Consumer;
import java.util.function.Function;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.KotlinDetector;
import org.springframework.http.ProblemDetail;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.StaxUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/converter/json/Jackson2ObjectMapperBuilder.class */
public class Jackson2ObjectMapperBuilder {
    private static final boolean jackson2XmlPresent = ClassUtils.isPresent("com.fasterxml.jackson.dataformat.xml.XmlMapper", Jackson2ObjectMapperBuilder.class.getClassLoader());

    @Nullable
    private JsonFactory factory;

    @Nullable
    private DateFormat dateFormat;

    @Nullable
    private Locale locale;

    @Nullable
    private TimeZone timeZone;

    @Nullable
    private AnnotationIntrospector annotationIntrospector;

    @Nullable
    private PropertyNamingStrategy propertyNamingStrategy;

    @Nullable
    private TypeResolverBuilder<?> defaultTyping;

    @Nullable
    private JsonInclude.Value serializationInclusion;

    @Nullable
    private FilterProvider filters;

    @Nullable
    private List<Module> modules;

    @Nullable
    private Class<? extends Module>[] moduleClasses;

    @Nullable
    private HandlerInstantiator handlerInstantiator;

    @Nullable
    private ApplicationContext applicationContext;

    @Nullable
    private Boolean defaultUseWrapper;

    @Nullable
    private Consumer<ObjectMapper> configurer;
    private final Map<Class<?>, Class<?>> mixIns = new LinkedHashMap();
    private final Map<Class<?>, JsonSerializer<?>> serializers = new LinkedHashMap();
    private final Map<Class<?>, JsonDeserializer<?>> deserializers = new LinkedHashMap();
    private final Map<PropertyAccessor, JsonAutoDetect.Visibility> visibilities = new LinkedHashMap();
    private final Map<Object, Boolean> features = new LinkedHashMap();
    private boolean createXmlMapper = false;
    private boolean findModulesViaServiceLoader = false;
    private boolean findWellKnownModules = true;
    private ClassLoader moduleClassLoader = getClass().getClassLoader();

    public Jackson2ObjectMapperBuilder createXmlMapper(boolean createXmlMapper) {
        this.createXmlMapper = createXmlMapper;
        return this;
    }

    public Jackson2ObjectMapperBuilder factory(JsonFactory factory) {
        this.factory = factory;
        return this;
    }

    public Jackson2ObjectMapperBuilder dateFormat(DateFormat dateFormat) {
        this.dateFormat = dateFormat;
        return this;
    }

    public Jackson2ObjectMapperBuilder simpleDateFormat(String format) {
        this.dateFormat = new SimpleDateFormat(format);
        return this;
    }

    public Jackson2ObjectMapperBuilder locale(Locale locale) {
        this.locale = locale;
        return this;
    }

    public Jackson2ObjectMapperBuilder locale(String localeString) {
        this.locale = StringUtils.parseLocale(localeString);
        return this;
    }

    public Jackson2ObjectMapperBuilder timeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
        return this;
    }

    public Jackson2ObjectMapperBuilder timeZone(String timeZoneString) {
        this.timeZone = StringUtils.parseTimeZoneString(timeZoneString);
        return this;
    }

    public Jackson2ObjectMapperBuilder annotationIntrospector(AnnotationIntrospector annotationIntrospector) {
        this.annotationIntrospector = annotationIntrospector;
        return this;
    }

    public Jackson2ObjectMapperBuilder annotationIntrospector(Function<AnnotationIntrospector, AnnotationIntrospector> pairingFunction) {
        this.annotationIntrospector = pairingFunction.apply(this.annotationIntrospector);
        return this;
    }

    public Jackson2ObjectMapperBuilder propertyNamingStrategy(PropertyNamingStrategy propertyNamingStrategy) {
        this.propertyNamingStrategy = propertyNamingStrategy;
        return this;
    }

    public Jackson2ObjectMapperBuilder defaultTyping(TypeResolverBuilder<?> typeResolverBuilder) {
        this.defaultTyping = typeResolverBuilder;
        return this;
    }

    public Jackson2ObjectMapperBuilder serializationInclusion(JsonInclude.Include inclusion) {
        return serializationInclusion(JsonInclude.Value.construct(inclusion, inclusion));
    }

    public Jackson2ObjectMapperBuilder serializationInclusion(JsonInclude.Value serializationInclusion) {
        this.serializationInclusion = serializationInclusion;
        return this;
    }

    public Jackson2ObjectMapperBuilder filters(FilterProvider filters) {
        this.filters = filters;
        return this;
    }

    public Jackson2ObjectMapperBuilder mixIn(Class<?> target, Class<?> mixinSource) {
        this.mixIns.put(target, mixinSource);
        return this;
    }

    public Jackson2ObjectMapperBuilder mixIns(Map<Class<?>, Class<?>> mixIns) {
        this.mixIns.putAll(mixIns);
        return this;
    }

    public Jackson2ObjectMapperBuilder serializers(JsonSerializer<?>... serializers) {
        for (JsonSerializer<?> serializer : serializers) {
            Class<?> handledType = serializer.handledType();
            if (handledType == null || handledType == Object.class) {
                throw new IllegalArgumentException("Unknown handled type in " + serializer.getClass().getName());
            }
            this.serializers.put(serializer.handledType(), serializer);
        }
        return this;
    }

    public Jackson2ObjectMapperBuilder serializerByType(Class<?> type, JsonSerializer<?> serializer) {
        this.serializers.put(type, serializer);
        return this;
    }

    public Jackson2ObjectMapperBuilder serializersByType(Map<Class<?>, JsonSerializer<?>> serializers) {
        this.serializers.putAll(serializers);
        return this;
    }

    public Jackson2ObjectMapperBuilder deserializers(JsonDeserializer<?>... deserializers) {
        for (JsonDeserializer<?> deserializer : deserializers) {
            Class<?> handledType = deserializer.handledType();
            if (handledType == null || handledType == Object.class) {
                throw new IllegalArgumentException("Unknown handled type in " + deserializer.getClass().getName());
            }
            this.deserializers.put(deserializer.handledType(), deserializer);
        }
        return this;
    }

    public Jackson2ObjectMapperBuilder deserializerByType(Class<?> type, JsonDeserializer<?> deserializer) {
        this.deserializers.put(type, deserializer);
        return this;
    }

    public Jackson2ObjectMapperBuilder deserializersByType(Map<Class<?>, JsonDeserializer<?>> deserializers) {
        this.deserializers.putAll(deserializers);
        return this;
    }

    public Jackson2ObjectMapperBuilder autoDetectFields(boolean autoDetectFields) {
        this.features.put(MapperFeature.AUTO_DETECT_FIELDS, Boolean.valueOf(autoDetectFields));
        return this;
    }

    public Jackson2ObjectMapperBuilder autoDetectGettersSetters(boolean autoDetectGettersSetters) {
        this.features.put(MapperFeature.AUTO_DETECT_GETTERS, Boolean.valueOf(autoDetectGettersSetters));
        this.features.put(MapperFeature.AUTO_DETECT_SETTERS, Boolean.valueOf(autoDetectGettersSetters));
        this.features.put(MapperFeature.AUTO_DETECT_IS_GETTERS, Boolean.valueOf(autoDetectGettersSetters));
        return this;
    }

    public Jackson2ObjectMapperBuilder defaultViewInclusion(boolean defaultViewInclusion) {
        this.features.put(MapperFeature.DEFAULT_VIEW_INCLUSION, Boolean.valueOf(defaultViewInclusion));
        return this;
    }

    public Jackson2ObjectMapperBuilder failOnUnknownProperties(boolean failOnUnknownProperties) {
        this.features.put(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, Boolean.valueOf(failOnUnknownProperties));
        return this;
    }

    public Jackson2ObjectMapperBuilder failOnEmptyBeans(boolean failOnEmptyBeans) {
        this.features.put(SerializationFeature.FAIL_ON_EMPTY_BEANS, Boolean.valueOf(failOnEmptyBeans));
        return this;
    }

    public Jackson2ObjectMapperBuilder indentOutput(boolean indentOutput) {
        this.features.put(SerializationFeature.INDENT_OUTPUT, Boolean.valueOf(indentOutput));
        return this;
    }

    public Jackson2ObjectMapperBuilder defaultUseWrapper(boolean defaultUseWrapper) {
        this.defaultUseWrapper = Boolean.valueOf(defaultUseWrapper);
        return this;
    }

    public Jackson2ObjectMapperBuilder visibility(PropertyAccessor accessor, JsonAutoDetect.Visibility visibility) {
        this.visibilities.put(accessor, visibility);
        return this;
    }

    public Jackson2ObjectMapperBuilder featuresToEnable(Object... featuresToEnable) {
        for (Object feature : featuresToEnable) {
            this.features.put(feature, Boolean.TRUE);
        }
        return this;
    }

    public Jackson2ObjectMapperBuilder featuresToDisable(Object... featuresToDisable) {
        for (Object feature : featuresToDisable) {
            this.features.put(feature, Boolean.FALSE);
        }
        return this;
    }

    public Jackson2ObjectMapperBuilder modules(Module... modules) {
        return modules(Arrays.asList(modules));
    }

    public Jackson2ObjectMapperBuilder modules(List<Module> modules) {
        this.modules = new ArrayList(modules);
        this.findModulesViaServiceLoader = false;
        this.findWellKnownModules = false;
        return this;
    }

    public Jackson2ObjectMapperBuilder modules(Consumer<List<Module>> consumer) {
        this.modules = this.modules != null ? this.modules : new ArrayList<>();
        this.findModulesViaServiceLoader = false;
        this.findWellKnownModules = false;
        consumer.accept(this.modules);
        return this;
    }

    public Jackson2ObjectMapperBuilder modulesToInstall(Module... modules) {
        this.modules = new ArrayList(Arrays.asList(modules));
        this.findWellKnownModules = true;
        return this;
    }

    public Jackson2ObjectMapperBuilder modulesToInstall(Consumer<List<Module>> consumer) {
        this.modules = this.modules != null ? this.modules : new ArrayList<>();
        this.findWellKnownModules = true;
        consumer.accept(this.modules);
        return this;
    }

    @SafeVarargs
    public final Jackson2ObjectMapperBuilder modulesToInstall(Class<? extends Module>... modules) {
        this.moduleClasses = modules;
        this.findWellKnownModules = true;
        return this;
    }

    public Jackson2ObjectMapperBuilder findModulesViaServiceLoader(boolean findModules) {
        this.findModulesViaServiceLoader = findModules;
        return this;
    }

    public Jackson2ObjectMapperBuilder moduleClassLoader(ClassLoader moduleClassLoader) {
        this.moduleClassLoader = moduleClassLoader;
        return this;
    }

    public Jackson2ObjectMapperBuilder handlerInstantiator(HandlerInstantiator handlerInstantiator) {
        this.handlerInstantiator = handlerInstantiator;
        return this;
    }

    public Jackson2ObjectMapperBuilder applicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        return this;
    }

    public Jackson2ObjectMapperBuilder postConfigurer(Consumer<ObjectMapper> configurer) {
        this.configurer = this.configurer != null ? this.configurer.andThen(configurer) : configurer;
        return this;
    }

    public <T extends ObjectMapper> T build() {
        ObjectMapper objectMapper;
        ObjectMapper create;
        if (this.createXmlMapper) {
            if (this.defaultUseWrapper != null) {
                create = new XmlObjectMapperInitializer().create(this.defaultUseWrapper.booleanValue(), this.factory);
            } else {
                create = new XmlObjectMapperInitializer().create(this.factory);
            }
            objectMapper = create;
        } else {
            objectMapper = this.factory != null ? new ObjectMapper(this.factory) : new ObjectMapper();
        }
        configure(objectMapper);
        return (T) objectMapper;
    }

    public void configure(ObjectMapper objectMapper) {
        Assert.notNull(objectMapper, "ObjectMapper must not be null");
        MultiValueMap<Object, Module> modulesToRegister = new LinkedMultiValueMap<>();
        if (this.findModulesViaServiceLoader) {
            ObjectMapper.findModules(this.moduleClassLoader).forEach(module -> {
                registerModule(module, modulesToRegister);
            });
        } else if (this.findWellKnownModules) {
            registerWellKnownModulesIfAvailable(modulesToRegister);
        }
        if (this.modules != null) {
            this.modules.forEach(module2 -> {
                registerModule(module2, modulesToRegister);
            });
        }
        if (this.moduleClasses != null) {
            for (Class<? extends Module> moduleClass : this.moduleClasses) {
                registerModule((Module) BeanUtils.instantiateClass(moduleClass), modulesToRegister);
            }
        }
        List<Module> modules = new ArrayList<>();
        Iterator<Module> it = modulesToRegister.values().iterator();
        while (it.hasNext()) {
            List<Module> nestedModules = (List) it.next();
            modules.addAll(nestedModules);
        }
        objectMapper.registerModules(modules);
        if (this.dateFormat != null) {
            objectMapper.setDateFormat(this.dateFormat);
        }
        if (this.locale != null) {
            objectMapper.setLocale(this.locale);
        }
        if (this.timeZone != null) {
            objectMapper.setTimeZone(this.timeZone);
        }
        if (this.annotationIntrospector != null) {
            objectMapper.setAnnotationIntrospector(this.annotationIntrospector);
        }
        if (this.propertyNamingStrategy != null) {
            objectMapper.setPropertyNamingStrategy(this.propertyNamingStrategy);
        }
        if (this.defaultTyping != null) {
            objectMapper.setDefaultTyping(this.defaultTyping);
        }
        if (this.serializationInclusion != null) {
            objectMapper.setDefaultPropertyInclusion(this.serializationInclusion);
        }
        if (this.filters != null) {
            objectMapper.setFilterProvider(this.filters);
        }
        if (jackson2XmlPresent) {
            objectMapper.addMixIn(ProblemDetail.class, ProblemDetailJacksonXmlMixin.class);
        } else {
            objectMapper.addMixIn(ProblemDetail.class, ProblemDetailJacksonMixin.class);
        }
        Map<Class<?>, Class<?>> map = this.mixIns;
        Objects.requireNonNull(objectMapper);
        map.forEach(objectMapper::addMixIn);
        if (!this.serializers.isEmpty() || !this.deserializers.isEmpty()) {
            SimpleModule module3 = new SimpleModule();
            addSerializers(module3);
            addDeserializers(module3);
            objectMapper.registerModule(module3);
        }
        Map<PropertyAccessor, JsonAutoDetect.Visibility> map2 = this.visibilities;
        Objects.requireNonNull(objectMapper);
        map2.forEach(objectMapper::setVisibility);
        customizeDefaultFeatures(objectMapper);
        this.features.forEach((feature, enabled) -> {
            configureFeature(objectMapper, feature, enabled.booleanValue());
        });
        if (this.handlerInstantiator != null) {
            objectMapper.setHandlerInstantiator(this.handlerInstantiator);
        } else if (this.applicationContext != null) {
            objectMapper.setHandlerInstantiator(new SpringHandlerInstantiator(this.applicationContext.getAutowireCapableBeanFactory()));
        }
        if (this.configurer != null) {
            this.configurer.accept(objectMapper);
        }
    }

    private void registerModule(Module module, MultiValueMap<Object, Module> modulesToRegister) {
        if (module.getTypeId() == null) {
            modulesToRegister.add(SimpleModule.class.getName(), module);
        } else {
            modulesToRegister.set(module.getTypeId(), module);
        }
    }

    private void customizeDefaultFeatures(ObjectMapper objectMapper) {
        if (!this.features.containsKey(MapperFeature.DEFAULT_VIEW_INCLUSION)) {
            configureFeature(objectMapper, MapperFeature.DEFAULT_VIEW_INCLUSION, false);
        }
        if (!this.features.containsKey(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)) {
            configureFeature(objectMapper, DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        }
    }

    private <T> void addSerializers(SimpleModule module) {
        this.serializers.forEach((type, serializer) -> {
            module.addSerializer(type, serializer);
        });
    }

    private <T> void addDeserializers(SimpleModule module) {
        this.deserializers.forEach((type, deserializer) -> {
            module.addDeserializer(type, deserializer);
        });
    }

    private void configureFeature(ObjectMapper objectMapper, Object feature, boolean enabled) {
        if (feature instanceof JsonParser.Feature) {
            JsonParser.Feature jsonParserFeature = (JsonParser.Feature) feature;
            objectMapper.configure(jsonParserFeature, enabled);
            return;
        }
        if (feature instanceof JsonGenerator.Feature) {
            JsonGenerator.Feature jsonGeneratorFeature = (JsonGenerator.Feature) feature;
            objectMapper.configure(jsonGeneratorFeature, enabled);
            return;
        }
        if (feature instanceof SerializationFeature) {
            SerializationFeature serializationFeature = (SerializationFeature) feature;
            objectMapper.configure(serializationFeature, enabled);
            return;
        }
        if (feature instanceof DeserializationFeature) {
            DeserializationFeature deserializationFeature = (DeserializationFeature) feature;
            objectMapper.configure(deserializationFeature, enabled);
        } else if (feature instanceof DatatypeFeature) {
            DatatypeFeature datatypeFeature = (DatatypeFeature) feature;
            objectMapper.configure(datatypeFeature, enabled);
        } else {
            if (feature instanceof MapperFeature) {
                MapperFeature mapperFeature = (MapperFeature) feature;
                objectMapper.configure(mapperFeature, enabled);
                return;
            }
            throw new IllegalArgumentException("Unknown feature class: " + feature.getClass().getName());
        }
    }

    private void registerWellKnownModulesIfAvailable(MultiValueMap<Object, Module> modulesToRegister) {
        try {
            Module jdk8Module = (Module) BeanUtils.instantiateClass(ClassUtils.forName("com.fasterxml.jackson.datatype.jdk8.Jdk8Module", this.moduleClassLoader));
            modulesToRegister.set(jdk8Module.getTypeId(), jdk8Module);
        } catch (ClassNotFoundException e) {
        }
        try {
            Module parameterNamesModule = (Module) BeanUtils.instantiateClass(ClassUtils.forName("com.fasterxml.jackson.module.paramnames.ParameterNamesModule", this.moduleClassLoader));
            modulesToRegister.set(parameterNamesModule.getTypeId(), parameterNamesModule);
        } catch (ClassNotFoundException e2) {
        }
        try {
            Module javaTimeModule = (Module) BeanUtils.instantiateClass(ClassUtils.forName("com.fasterxml.jackson.datatype.jsr310.JavaTimeModule", this.moduleClassLoader));
            modulesToRegister.set(javaTimeModule.getTypeId(), javaTimeModule);
        } catch (ClassNotFoundException e3) {
        }
        if (KotlinDetector.isKotlinPresent()) {
            try {
                Module kotlinModule = (Module) BeanUtils.instantiateClass(ClassUtils.forName("com.fasterxml.jackson.module.kotlin.KotlinModule", this.moduleClassLoader));
                modulesToRegister.set(kotlinModule.getTypeId(), kotlinModule);
            } catch (ClassNotFoundException e4) {
            }
        }
    }

    public static Jackson2ObjectMapperBuilder json() {
        return new Jackson2ObjectMapperBuilder();
    }

    public static Jackson2ObjectMapperBuilder xml() {
        return new Jackson2ObjectMapperBuilder().createXmlMapper(true);
    }

    public static Jackson2ObjectMapperBuilder smile() {
        return new Jackson2ObjectMapperBuilder().factory(new SmileFactoryInitializer().create());
    }

    public static Jackson2ObjectMapperBuilder cbor() {
        return new Jackson2ObjectMapperBuilder().factory(new CborFactoryInitializer().create());
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/converter/json/Jackson2ObjectMapperBuilder$XmlObjectMapperInitializer.class */
    private static class XmlObjectMapperInitializer {
        private XmlObjectMapperInitializer() {
        }

        public ObjectMapper create(@Nullable JsonFactory factory) {
            if (factory != null) {
                return new XmlMapper((XmlFactory) factory);
            }
            return new XmlMapper(StaxUtils.createDefensiveInputFactory());
        }

        public ObjectMapper create(boolean defaultUseWrapper, @Nullable JsonFactory factory) {
            JacksonXmlModule module = new JacksonXmlModule();
            module.setDefaultUseWrapper(defaultUseWrapper);
            if (factory != null) {
                return new XmlMapper((XmlFactory) factory, module);
            }
            return new XmlMapper(new XmlFactory(StaxUtils.createDefensiveInputFactory()), module);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/converter/json/Jackson2ObjectMapperBuilder$SmileFactoryInitializer.class */
    private static class SmileFactoryInitializer {
        private SmileFactoryInitializer() {
        }

        public JsonFactory create() {
            return new SmileFactory();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/converter/json/Jackson2ObjectMapperBuilder$CborFactoryInitializer.class */
    private static class CborFactoryInitializer {
        private CborFactoryInitializer() {
        }

        public JsonFactory create() {
            return new CBORFactory();
        }
    }
}

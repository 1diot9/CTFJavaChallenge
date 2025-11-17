package com.fasterxml.jackson.databind.cfg;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.core.StreamWriteFeature;
import com.fasterxml.jackson.core.TokenStreamFactory;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.cfg.MapperBuilder;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.introspect.AccessorNamingStrategy;
import com.fasterxml.jackson.databind.introspect.DefaultAccessorNamingStrategy;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.SubtypeResolver;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.SerializerFactory;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ServiceLoader;
import java.util.TimeZone;
import java.util.function.Consumer;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/cfg/MapperBuilder.class */
public abstract class MapperBuilder<M extends ObjectMapper, B extends MapperBuilder<M, B>> {
    protected final M _mapper;

    /* JADX INFO: Access modifiers changed from: protected */
    public MapperBuilder(M mapper) {
        this._mapper = mapper;
    }

    public M build() {
        return this._mapper;
    }

    public boolean isEnabled(MapperFeature f) {
        return this._mapper.isEnabled(f);
    }

    public boolean isEnabled(DeserializationFeature f) {
        return this._mapper.isEnabled(f);
    }

    public boolean isEnabled(SerializationFeature f) {
        return this._mapper.isEnabled(f);
    }

    public boolean isEnabled(JsonParser.Feature f) {
        return this._mapper.isEnabled(f);
    }

    public boolean isEnabled(JsonGenerator.Feature f) {
        return this._mapper.isEnabled(f);
    }

    public TokenStreamFactory streamFactory() {
        return this._mapper.tokenStreamFactory();
    }

    public B enable(MapperFeature... features) {
        this._mapper.enable(features);
        return _this();
    }

    public B disable(MapperFeature... features) {
        this._mapper.disable(features);
        return _this();
    }

    public B configure(MapperFeature feature, boolean state) {
        this._mapper.configure(feature, state);
        return _this();
    }

    public B enable(SerializationFeature... features) {
        for (SerializationFeature f : features) {
            this._mapper.enable(f);
        }
        return _this();
    }

    public B disable(SerializationFeature... features) {
        for (SerializationFeature f : features) {
            this._mapper.disable(f);
        }
        return _this();
    }

    public B configure(SerializationFeature feature, boolean state) {
        this._mapper.configure(feature, state);
        return _this();
    }

    public B enable(DeserializationFeature... features) {
        for (DeserializationFeature f : features) {
            this._mapper.enable(f);
        }
        return _this();
    }

    public B disable(DeserializationFeature... features) {
        for (DeserializationFeature f : features) {
            this._mapper.disable(f);
        }
        return _this();
    }

    public B configure(DeserializationFeature feature, boolean state) {
        this._mapper.configure(feature, state);
        return _this();
    }

    public B enable(DatatypeFeature... features) {
        for (DatatypeFeature f : features) {
            this._mapper.configure(f, true);
        }
        return _this();
    }

    public B disable(DatatypeFeature... features) {
        for (DatatypeFeature f : features) {
            this._mapper.configure(f, false);
        }
        return _this();
    }

    public B configure(DatatypeFeature feature, boolean state) {
        this._mapper.configure(feature, state);
        return _this();
    }

    public B enable(JsonParser.Feature... features) {
        this._mapper.enable(features);
        return _this();
    }

    public B disable(JsonParser.Feature... features) {
        this._mapper.disable(features);
        return _this();
    }

    public B configure(JsonParser.Feature feature, boolean state) {
        this._mapper.configure(feature, state);
        return _this();
    }

    public B enable(JsonGenerator.Feature... features) {
        this._mapper.enable(features);
        return _this();
    }

    public B disable(JsonGenerator.Feature... features) {
        this._mapper.disable(features);
        return _this();
    }

    public B configure(JsonGenerator.Feature feature, boolean state) {
        this._mapper.configure(feature, state);
        return _this();
    }

    public B enable(StreamReadFeature... features) {
        for (StreamReadFeature f : features) {
            this._mapper.enable(f.mappedFeature());
        }
        return _this();
    }

    public B disable(StreamReadFeature... features) {
        for (StreamReadFeature f : features) {
            this._mapper.disable(f.mappedFeature());
        }
        return _this();
    }

    public B configure(StreamReadFeature feature, boolean state) {
        this._mapper.configure(feature.mappedFeature(), state);
        return _this();
    }

    public B enable(StreamWriteFeature... features) {
        for (StreamWriteFeature f : features) {
            this._mapper.enable(f.mappedFeature());
        }
        return _this();
    }

    public B disable(StreamWriteFeature... features) {
        for (StreamWriteFeature f : features) {
            this._mapper.disable(f.mappedFeature());
        }
        return _this();
    }

    public B configure(StreamWriteFeature feature, boolean state) {
        this._mapper.configure(feature.mappedFeature(), state);
        return _this();
    }

    public B withConfigOverride(Class<?> forType, Consumer<MutableConfigOverride> handler) {
        handler.accept(this._mapper.configOverride(forType));
        return _this();
    }

    public B withCoercionConfig(LogicalType forType, Consumer<MutableCoercionConfig> handler) {
        handler.accept(this._mapper.coercionConfigFor(forType));
        return _this();
    }

    public B withCoercionConfig(Class<?> forType, Consumer<MutableCoercionConfig> handler) {
        handler.accept(this._mapper.coercionConfigFor(forType));
        return _this();
    }

    public B withCoercionConfigDefaults(Consumer<MutableCoercionConfig> handler) {
        handler.accept(this._mapper.coercionConfigDefaults());
        return _this();
    }

    public B addModule(Module module) {
        this._mapper.registerModule(module);
        return _this();
    }

    public B addModules(Module... modules) {
        for (Module module : modules) {
            addModule(module);
        }
        return _this();
    }

    public B addModules(Iterable<? extends Module> modules) {
        for (Module module : modules) {
            addModule(module);
        }
        return _this();
    }

    public static List<Module> findModules() {
        return findModules(null);
    }

    public static List<Module> findModules(ClassLoader classLoader) {
        ArrayList<Module> modules = new ArrayList<>();
        ServiceLoader<Module> loader = secureGetServiceLoader(Module.class, classLoader);
        Iterator<Module> it = loader.iterator();
        while (it.hasNext()) {
            Module module = it.next();
            modules.add(module);
        }
        return modules;
    }

    private static <T> ServiceLoader<T> secureGetServiceLoader(final Class<T> clazz, final ClassLoader classLoader) {
        SecurityManager sm = System.getSecurityManager();
        if (sm == null) {
            return classLoader == null ? ServiceLoader.load(clazz) : ServiceLoader.load(clazz, classLoader);
        }
        return (ServiceLoader) AccessController.doPrivileged(new PrivilegedAction<ServiceLoader<T>>() { // from class: com.fasterxml.jackson.databind.cfg.MapperBuilder.1
            @Override // java.security.PrivilegedAction
            public ServiceLoader<T> run() {
                return classLoader == null ? ServiceLoader.load(clazz) : ServiceLoader.load(clazz, classLoader);
            }
        });
    }

    public B findAndAddModules() {
        return addModules(findModules());
    }

    public B annotationIntrospector(AnnotationIntrospector intr) {
        this._mapper.setAnnotationIntrospector(intr);
        return _this();
    }

    public B defaultAttributes(ContextAttributes attrs) {
        this._mapper.setDefaultAttributes(attrs);
        return _this();
    }

    public B typeFactory(TypeFactory f) {
        this._mapper.setTypeFactory(f);
        return _this();
    }

    public B subtypeResolver(SubtypeResolver r) {
        this._mapper.setSubtypeResolver(r);
        return _this();
    }

    public B visibility(VisibilityChecker<?> vc) {
        this._mapper.setVisibility(vc);
        return _this();
    }

    public B visibility(PropertyAccessor forMethod, JsonAutoDetect.Visibility visibility) {
        this._mapper.setVisibility(forMethod, visibility);
        return _this();
    }

    public B handlerInstantiator(HandlerInstantiator hi) {
        this._mapper.setHandlerInstantiator(hi);
        return _this();
    }

    public B propertyNamingStrategy(PropertyNamingStrategy s) {
        this._mapper.setPropertyNamingStrategy(s);
        return _this();
    }

    public B accessorNaming(AccessorNamingStrategy.Provider s) {
        if (s == null) {
            s = new DefaultAccessorNamingStrategy.Provider();
        }
        this._mapper.setAccessorNaming(s);
        return _this();
    }

    public B serializerFactory(SerializerFactory f) {
        this._mapper.setSerializerFactory(f);
        return _this();
    }

    public B filterProvider(FilterProvider prov) {
        this._mapper.setFilterProvider(prov);
        return _this();
    }

    public B defaultPrettyPrinter(PrettyPrinter pp) {
        this._mapper.setDefaultPrettyPrinter(pp);
        return _this();
    }

    public B injectableValues(InjectableValues v) {
        this._mapper.setInjectableValues(v);
        return _this();
    }

    public B nodeFactory(JsonNodeFactory f) {
        this._mapper.setNodeFactory(f);
        return _this();
    }

    public B constructorDetector(ConstructorDetector cd) {
        this._mapper.setConstructorDetector(cd);
        return _this();
    }

    public B addHandler(DeserializationProblemHandler h) {
        this._mapper.addHandler(h);
        return _this();
    }

    public B clearProblemHandlers() {
        this._mapper.clearProblemHandlers();
        return _this();
    }

    public B defaultSetterInfo(JsonSetter.Value v) {
        this._mapper.setDefaultSetterInfo(v);
        return _this();
    }

    public B defaultMergeable(Boolean b) {
        this._mapper.setDefaultMergeable(b);
        return _this();
    }

    public B defaultLeniency(Boolean b) {
        this._mapper.setDefaultLeniency(b);
        return _this();
    }

    public B defaultDateFormat(DateFormat df) {
        this._mapper.setDateFormat(df);
        return _this();
    }

    public B defaultTimeZone(TimeZone tz) {
        this._mapper.setTimeZone(tz);
        return _this();
    }

    public B defaultLocale(Locale locale) {
        this._mapper.setLocale(locale);
        return _this();
    }

    public B defaultBase64Variant(Base64Variant v) {
        this._mapper.setBase64Variant(v);
        return _this();
    }

    public B serializationInclusion(JsonInclude.Include incl) {
        this._mapper.setSerializationInclusion(incl);
        return _this();
    }

    public B defaultPropertyInclusion(JsonInclude.Value incl) {
        this._mapper.setDefaultPropertyInclusion(incl);
        return _this();
    }

    public B addMixIn(Class<?> target, Class<?> mixinSource) {
        this._mapper.addMixIn(target, mixinSource);
        return _this();
    }

    public B removeMixIn(Class<?> target) {
        this._mapper.addMixIn(target, null);
        return _this();
    }

    public B registerSubtypes(Class<?>... subtypes) {
        this._mapper.registerSubtypes(subtypes);
        return _this();
    }

    public B registerSubtypes(NamedType... subtypes) {
        this._mapper.registerSubtypes(subtypes);
        return _this();
    }

    public B registerSubtypes(Collection<Class<?>> subtypes) {
        this._mapper.registerSubtypes(subtypes);
        return _this();
    }

    public B polymorphicTypeValidator(PolymorphicTypeValidator ptv) {
        this._mapper.setPolymorphicTypeValidator(ptv);
        return _this();
    }

    public B activateDefaultTyping(PolymorphicTypeValidator subtypeValidator) {
        this._mapper.activateDefaultTyping(subtypeValidator);
        return _this();
    }

    public B activateDefaultTyping(PolymorphicTypeValidator subtypeValidator, ObjectMapper.DefaultTyping dti) {
        this._mapper.activateDefaultTyping(subtypeValidator, dti);
        return _this();
    }

    public B activateDefaultTyping(PolymorphicTypeValidator subtypeValidator, ObjectMapper.DefaultTyping applicability, JsonTypeInfo.As includeAs) {
        this._mapper.activateDefaultTyping(subtypeValidator, applicability, includeAs);
        return _this();
    }

    public B activateDefaultTypingAsProperty(PolymorphicTypeValidator subtypeValidator, ObjectMapper.DefaultTyping applicability, String propertyName) {
        this._mapper.activateDefaultTypingAsProperty(subtypeValidator, applicability, propertyName);
        return _this();
    }

    public B deactivateDefaultTyping() {
        this._mapper.deactivateDefaultTyping();
        return _this();
    }

    public B setDefaultTyping(TypeResolverBuilder<?> typer) {
        this._mapper.setDefaultTyping(typer);
        return _this();
    }

    protected final B _this() {
        return this;
    }
}

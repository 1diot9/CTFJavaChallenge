package org.springframework.boot.context.properties.bind;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.source.ConfigurationProperty;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.context.properties.source.ConfigurationPropertyState;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/bind/Binder.class */
public class Binder {
    private static final Set<Class<?>> NON_BEAN_CLASSES = Collections.unmodifiableSet(new HashSet(Arrays.asList(Object.class, Class.class)));
    private final Iterable<ConfigurationPropertySource> sources;
    private final PlaceholdersResolver placeholdersResolver;
    private final BindConverter bindConverter;
    private final BindHandler defaultBindHandler;
    private final Map<BindMethod, List<DataObjectBinder>> dataObjectBinders;

    public Binder(ConfigurationPropertySource... sources) {
        this(sources != null ? Arrays.asList(sources) : null, null, null, null);
    }

    public Binder(Iterable<ConfigurationPropertySource> sources) {
        this(sources, null, null, null);
    }

    public Binder(Iterable<ConfigurationPropertySource> sources, PlaceholdersResolver placeholdersResolver) {
        this(sources, placeholdersResolver, null, null);
    }

    public Binder(Iterable<ConfigurationPropertySource> sources, PlaceholdersResolver placeholdersResolver, ConversionService conversionService) {
        this(sources, placeholdersResolver, conversionService, null);
    }

    public Binder(Iterable<ConfigurationPropertySource> sources, PlaceholdersResolver placeholdersResolver, ConversionService conversionService, Consumer<PropertyEditorRegistry> propertyEditorInitializer) {
        this(sources, placeholdersResolver, conversionService, propertyEditorInitializer, null);
    }

    public Binder(Iterable<ConfigurationPropertySource> sources, PlaceholdersResolver placeholdersResolver, ConversionService conversionService, Consumer<PropertyEditorRegistry> propertyEditorInitializer, BindHandler defaultBindHandler) {
        this(sources, placeholdersResolver, conversionService, propertyEditorInitializer, defaultBindHandler, (BindConstructorProvider) null);
    }

    public Binder(Iterable<ConfigurationPropertySource> sources, PlaceholdersResolver placeholdersResolver, ConversionService conversionService, Consumer<PropertyEditorRegistry> propertyEditorInitializer, BindHandler defaultBindHandler, BindConstructorProvider constructorProvider) {
        this(sources, placeholdersResolver, (List<ConversionService>) (conversionService != null ? Collections.singletonList(conversionService) : (List) null), propertyEditorInitializer, defaultBindHandler, constructorProvider);
    }

    public Binder(Iterable<ConfigurationPropertySource> sources, PlaceholdersResolver placeholdersResolver, List<ConversionService> conversionServices, Consumer<PropertyEditorRegistry> propertyEditorInitializer, BindHandler defaultBindHandler, BindConstructorProvider constructorProvider) {
        Assert.notNull(sources, "Sources must not be null");
        for (ConfigurationPropertySource source : sources) {
            Assert.notNull(source, "Sources must not contain null elements");
        }
        this.sources = sources;
        this.placeholdersResolver = placeholdersResolver != null ? placeholdersResolver : PlaceholdersResolver.NONE;
        this.bindConverter = BindConverter.get(conversionServices, propertyEditorInitializer);
        this.defaultBindHandler = defaultBindHandler != null ? defaultBindHandler : BindHandler.DEFAULT;
        ValueObjectBinder valueObjectBinder = new ValueObjectBinder(constructorProvider == null ? BindConstructorProvider.DEFAULT : constructorProvider);
        JavaBeanBinder javaBeanBinder = JavaBeanBinder.INSTANCE;
        Map<BindMethod, List<DataObjectBinder>> dataObjectBinders = new HashMap<>();
        dataObjectBinders.put(BindMethod.VALUE_OBJECT, List.of(valueObjectBinder));
        dataObjectBinders.put(BindMethod.JAVA_BEAN, List.of(javaBeanBinder));
        dataObjectBinders.put(null, List.of(valueObjectBinder, javaBeanBinder));
        this.dataObjectBinders = Collections.unmodifiableMap(dataObjectBinders);
    }

    public <T> BindResult<T> bind(String name, Class<T> target) {
        return bind(name, Bindable.of(target));
    }

    public <T> BindResult<T> bind(String name, Bindable<T> target) {
        return bind(ConfigurationPropertyName.of(name), target, (BindHandler) null);
    }

    public <T> BindResult<T> bind(ConfigurationPropertyName name, Bindable<T> target) {
        return bind(name, target, (BindHandler) null);
    }

    public <T> BindResult<T> bind(String name, Bindable<T> target, BindHandler handler) {
        return bind(ConfigurationPropertyName.of(name), target, handler);
    }

    public <T> BindResult<T> bind(ConfigurationPropertyName name, Bindable<T> target, BindHandler handler) {
        return BindResult.of(bind(name, target, handler, false));
    }

    public <T> T bindOrCreate(String str, Class<T> cls) {
        return (T) bindOrCreate(str, Bindable.of(cls));
    }

    public <T> T bindOrCreate(String str, Bindable<T> bindable) {
        return (T) bindOrCreate(ConfigurationPropertyName.of(str), bindable, (BindHandler) null);
    }

    public <T> T bindOrCreate(String str, Bindable<T> bindable, BindHandler bindHandler) {
        return (T) bindOrCreate(ConfigurationPropertyName.of(str), bindable, bindHandler);
    }

    public <T> T bindOrCreate(ConfigurationPropertyName configurationPropertyName, Bindable<T> bindable, BindHandler bindHandler) {
        return (T) bind(configurationPropertyName, bindable, bindHandler, true);
    }

    private <T> T bind(ConfigurationPropertyName configurationPropertyName, Bindable<T> bindable, BindHandler bindHandler, boolean z) {
        Assert.notNull(configurationPropertyName, "Name must not be null");
        Assert.notNull(bindable, "Target must not be null");
        return (T) bind(configurationPropertyName, bindable, bindHandler != null ? bindHandler : this.defaultBindHandler, new Context(), false, z);
    }

    private <T> T bind(ConfigurationPropertyName configurationPropertyName, Bindable<T> bindable, BindHandler bindHandler, Context context, boolean z, boolean z2) {
        try {
            Bindable<T> onStart = bindHandler.onStart(configurationPropertyName, bindable, context);
            if (onStart == null) {
                return (T) handleBindResult(configurationPropertyName, bindable, bindHandler, context, null, z2);
            }
            return (T) handleBindResult(configurationPropertyName, onStart, bindHandler, context, bindObject(configurationPropertyName, onStart, bindHandler, context, z), z2);
        } catch (Exception e) {
            return (T) handleBindError(configurationPropertyName, bindable, bindHandler, context, e);
        }
    }

    private <T> T handleBindResult(ConfigurationPropertyName configurationPropertyName, Bindable<T> bindable, BindHandler bindHandler, Context context, Object obj, boolean z) throws Exception {
        if (obj != null) {
            obj = context.getConverter().convert(bindHandler.onSuccess(configurationPropertyName, bindable, context, obj), bindable);
        }
        if (obj == null && z) {
            obj = context.getConverter().convert(bindHandler.onCreate(configurationPropertyName, bindable, context, fromDataObjectBinders(bindable.getBindMethod(), dataObjectBinder -> {
                return dataObjectBinder.create(bindable, context);
            })), bindable);
            if (obj == null) {
                IllegalStateException illegalStateException = new IllegalStateException("Unable to create instance for " + bindable.getType());
                this.dataObjectBinders.get(bindable.getBindMethod()).forEach(dataObjectBinder2 -> {
                    dataObjectBinder2.onUnableToCreateInstance(bindable, context, illegalStateException);
                });
                throw illegalStateException;
            }
        }
        bindHandler.onFinish(configurationPropertyName, bindable, context, obj);
        return (T) context.getConverter().convert(obj, bindable);
    }

    private <T> T handleBindError(ConfigurationPropertyName configurationPropertyName, Bindable<T> bindable, BindHandler bindHandler, Context context, Exception exc) {
        try {
            return (T) context.getConverter().convert(bindHandler.onFailure(configurationPropertyName, bindable, context, exc), bindable);
        } catch (Exception e) {
            if (e instanceof BindException) {
                throw ((BindException) e);
            }
            throw new BindException(configurationPropertyName, bindable, context.getConfigurationProperty(), e);
        }
    }

    private <T> Object bindObject(ConfigurationPropertyName name, Bindable<T> target, BindHandler handler, Context context, boolean allowRecursiveBinding) {
        ConfigurationProperty property = findProperty(name, target, context);
        if (property == null && context.depth != 0 && containsNoDescendantOf(context.getSources(), name)) {
            return null;
        }
        AggregateBinder<?> aggregateBinder = getAggregateBinder(target, context);
        if (aggregateBinder != null) {
            return bindAggregate(name, target, handler, context, aggregateBinder);
        }
        if (property != null) {
            try {
                return bindProperty(target, context, property);
            } catch (ConverterNotFoundException ex) {
                Object instance = bindDataObject(name, target, handler, context, allowRecursiveBinding);
                if (instance != null) {
                    return instance;
                }
                throw ex;
            }
        }
        return bindDataObject(name, target, handler, context, allowRecursiveBinding);
    }

    private AggregateBinder<?> getAggregateBinder(Bindable<?> target, Context context) {
        Class<?> resolvedType = target.getType().resolve(Object.class);
        if (Map.class.isAssignableFrom(resolvedType)) {
            return new MapBinder(context);
        }
        if (Collection.class.isAssignableFrom(resolvedType)) {
            return new CollectionBinder(context);
        }
        if (target.getType().isArray()) {
            return new ArrayBinder(context);
        }
        return null;
    }

    private <T> Object bindAggregate(ConfigurationPropertyName name, Bindable<T> target, BindHandler handler, Context context, AggregateBinder<?> aggregateBinder) {
        AggregateElementBinder elementBinder = (itemName, itemTarget, source) -> {
            boolean allowRecursiveBinding = aggregateBinder.isAllowRecursiveBinding(source);
            Supplier<?> supplier = () -> {
                return bind(itemName, itemTarget, handler, context, allowRecursiveBinding, false);
            };
            return context.withSource(source, supplier);
        };
        return context.withIncreasedDepth(() -> {
            return aggregateBinder.bind(name, target, elementBinder);
        });
    }

    private <T> ConfigurationProperty findProperty(ConfigurationPropertyName name, Bindable<T> target, Context context) {
        if (name.isEmpty() || target.hasBindRestriction(Bindable.BindRestriction.NO_DIRECT_PROPERTY)) {
            return null;
        }
        for (ConfigurationPropertySource source : context.getSources()) {
            ConfigurationProperty property = source.getConfigurationProperty(name);
            if (property != null) {
                return property;
            }
        }
        return null;
    }

    private <T> Object bindProperty(Bindable<T> target, Context context, ConfigurationProperty property) {
        context.setConfigurationProperty(property);
        Object result = property.getValue();
        return context.getConverter().convert(this.placeholdersResolver.resolvePlaceholders(result), target);
    }

    private Object bindDataObject(ConfigurationPropertyName name, Bindable<?> target, BindHandler handler, Context context, boolean allowRecursiveBinding) {
        if (isUnbindableBean(name, target, context)) {
            return null;
        }
        Class<?> type = target.getType().resolve(Object.class);
        BindMethod bindMethod = target.getBindMethod();
        if (!allowRecursiveBinding && context.isBindingDataObject(type)) {
            return null;
        }
        DataObjectPropertyBinder propertyBinder = (propertyName, propertyTarget) -> {
            return bind(name.append(propertyName), propertyTarget, handler, context, false, false);
        };
        return context.withDataObject(type, () -> {
            return fromDataObjectBinders(bindMethod, dataObjectBinder -> {
                return dataObjectBinder.bind(name, target, context, propertyBinder);
            });
        });
    }

    private Object fromDataObjectBinders(BindMethod bindMethod, Function<DataObjectBinder, Object> operation) {
        return this.dataObjectBinders.get(bindMethod).stream().map(operation).filter(Objects::nonNull).findFirst().orElse(null);
    }

    private boolean isUnbindableBean(ConfigurationPropertyName name, Bindable<?> target, Context context) {
        for (ConfigurationPropertySource source : context.getSources()) {
            if (source.containsDescendantOf(name) == ConfigurationPropertyState.PRESENT) {
                return false;
            }
        }
        Class<?> resolved = target.getType().resolve(Object.class);
        if (resolved.isPrimitive() || NON_BEAN_CLASSES.contains(resolved)) {
            return true;
        }
        return resolved.getName().startsWith("java.");
    }

    private boolean containsNoDescendantOf(Iterable<ConfigurationPropertySource> sources, ConfigurationPropertyName name) {
        for (ConfigurationPropertySource source : sources) {
            if (source.containsDescendantOf(name) != ConfigurationPropertyState.ABSENT) {
                return false;
            }
        }
        return true;
    }

    public static Binder get(Environment environment) {
        return get(environment, null);
    }

    public static Binder get(Environment environment, BindHandler defaultBindHandler) {
        Iterable<ConfigurationPropertySource> sources = ConfigurationPropertySources.get(environment);
        PropertySourcesPlaceholdersResolver placeholdersResolver = new PropertySourcesPlaceholdersResolver(environment);
        return new Binder(sources, placeholdersResolver, null, null, defaultBindHandler);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/bind/Binder$Context.class */
    public final class Context implements BindContext {
        private int depth;
        private int sourcePushCount;
        private ConfigurationProperty configurationProperty;
        private final List<ConfigurationPropertySource> source = Arrays.asList((ConfigurationPropertySource) null);
        private final Deque<Class<?>> dataObjectBindings = new ArrayDeque();
        private final Deque<Class<?>> constructorBindings = new ArrayDeque();

        Context() {
        }

        private void increaseDepth() {
            this.depth++;
        }

        private void decreaseDepth() {
            this.depth--;
        }

        private <T> T withSource(ConfigurationPropertySource source, Supplier<T> supplier) {
            if (source == null) {
                return supplier.get();
            }
            this.source.set(0, source);
            this.sourcePushCount++;
            try {
                T t = supplier.get();
                this.sourcePushCount--;
                return t;
            } catch (Throwable th) {
                this.sourcePushCount--;
                throw th;
            }
        }

        private <T> T withDataObject(Class<?> cls, Supplier<T> supplier) {
            this.dataObjectBindings.push(cls);
            try {
                T t = (T) withIncreasedDepth(supplier);
                this.dataObjectBindings.pop();
                return t;
            } catch (Throwable th) {
                this.dataObjectBindings.pop();
                throw th;
            }
        }

        private boolean isBindingDataObject(Class<?> type) {
            return this.dataObjectBindings.contains(type);
        }

        private <T> T withIncreasedDepth(Supplier<T> supplier) {
            increaseDepth();
            try {
                return supplier.get();
            } finally {
                decreaseDepth();
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void setConfigurationProperty(ConfigurationProperty configurationProperty) {
            this.configurationProperty = configurationProperty;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void clearConfigurationProperty() {
            this.configurationProperty = null;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void pushConstructorBoundTypes(Class<?> value) {
            this.constructorBindings.push(value);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public boolean isNestedConstructorBinding() {
            return !this.constructorBindings.isEmpty();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void popConstructorBoundTypes() {
            this.constructorBindings.pop();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public PlaceholdersResolver getPlaceholdersResolver() {
            return Binder.this.placeholdersResolver;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public BindConverter getConverter() {
            return Binder.this.bindConverter;
        }

        @Override // org.springframework.boot.context.properties.bind.BindContext
        public Binder getBinder() {
            return Binder.this;
        }

        @Override // org.springframework.boot.context.properties.bind.BindContext
        public int getDepth() {
            return this.depth;
        }

        @Override // org.springframework.boot.context.properties.bind.BindContext
        public Iterable<ConfigurationPropertySource> getSources() {
            if (this.sourcePushCount > 0) {
                return this.source;
            }
            return Binder.this.sources;
        }

        @Override // org.springframework.boot.context.properties.bind.BindContext
        public ConfigurationProperty getConfigurationProperty() {
            return this.configurationProperty;
        }
    }
}

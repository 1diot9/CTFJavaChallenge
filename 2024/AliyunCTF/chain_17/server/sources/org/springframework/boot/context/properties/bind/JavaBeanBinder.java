package org.springframework.boot.context.properties.bind;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import java.beans.Introspector;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.ConfigurationPropertyState;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/bind/JavaBeanBinder.class */
public class JavaBeanBinder implements DataObjectBinder {
    static final JavaBeanBinder INSTANCE = new JavaBeanBinder();

    JavaBeanBinder() {
    }

    @Override // org.springframework.boot.context.properties.bind.DataObjectBinder
    public <T> T bind(ConfigurationPropertyName name, Bindable<T> target, Binder.Context context, DataObjectPropertyBinder propertyBinder) {
        boolean hasKnownBindableProperties = target.getValue() != null && hasKnownBindableProperties(name, context);
        Bean<T> bean = Bean.get(target, hasKnownBindableProperties);
        if (bean == null) {
            return null;
        }
        BeanSupplier<T> beanSupplier = bean.getSupplier(target);
        boolean bound = bind(propertyBinder, bean, beanSupplier, context);
        if (bound) {
            return beanSupplier.get();
        }
        return null;
    }

    @Override // org.springframework.boot.context.properties.bind.DataObjectBinder
    public <T> T create(Bindable<T> bindable, Binder.Context context) {
        Class<?> resolve = bindable.getType().resolve();
        if (resolve != null) {
            return (T) BeanUtils.instantiateClass(resolve);
        }
        return null;
    }

    private boolean hasKnownBindableProperties(ConfigurationPropertyName name, Binder.Context context) {
        for (ConfigurationPropertySource source : context.getSources()) {
            if (source.containsDescendantOf(name) == ConfigurationPropertyState.PRESENT) {
                return true;
            }
        }
        return false;
    }

    private <T> boolean bind(DataObjectPropertyBinder propertyBinder, Bean<T> bean, BeanSupplier<T> beanSupplier, Binder.Context context) {
        boolean bound = false;
        for (BeanProperty beanProperty : bean.getProperties().values()) {
            bound |= bind(beanSupplier, propertyBinder, beanProperty);
            context.clearConfigurationProperty();
        }
        return bound;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private <T> boolean bind(BeanSupplier<T> beanSupplier, DataObjectPropertyBinder propertyBinder, BeanProperty property) {
        String propertyName = property.getName();
        ResolvableType type = property.getType();
        Supplier<Object> value = property.getValue(beanSupplier);
        Annotation[] annotations = property.getAnnotations();
        Object bound = propertyBinder.bindProperty(propertyName, Bindable.of(type).withSuppliedValue(value).withAnnotations(annotations));
        if (bound == null) {
            return false;
        }
        if (property.isSettable()) {
            property.setValue(beanSupplier, bound);
            return true;
        }
        if (value == null || !bound.equals(value.get())) {
            throw new IllegalStateException("No setter found for property: " + property.getName());
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/bind/JavaBeanBinder$BeanProperties.class */
    public static class BeanProperties {
        private final Map<String, BeanProperty> properties = new LinkedHashMap();
        private final ResolvableType type;
        private final Class<?> resolvedType;

        BeanProperties(ResolvableType type, Class<?> resolvedType) {
            this.type = type;
            this.resolvedType = resolvedType;
            addProperties(resolvedType);
        }

        private void addProperties(Class<?> type) {
            while (type != null && !Object.class.equals(type)) {
                Method[] declaredMethods = (Method[]) getSorted(type, this::getDeclaredMethods, (v0) -> {
                    return v0.getName();
                });
                Field[] declaredFields = (Field[]) getSorted(type, (v0) -> {
                    return v0.getDeclaredFields();
                }, (v0) -> {
                    return v0.getName();
                });
                addProperties(declaredMethods, declaredFields);
                type = type.getSuperclass();
            }
        }

        private Method[] getDeclaredMethods(Class<?> type) {
            Method[] methods = type.getDeclaredMethods();
            Set<Method> result = new LinkedHashSet<>(methods.length);
            for (Method method : methods) {
                result.add(BridgeMethodResolver.findBridgedMethod(method));
            }
            return (Method[]) result.toArray(new Method[0]);
        }

        private <S, E> E[] getSorted(S source, Function<S, E[]> elements, Function<E, String> name) {
            E[] result = elements.apply(source);
            Arrays.sort(result, Comparator.comparing(name));
            return result;
        }

        protected void addProperties(Method[] declaredMethods, Field[] declaredFields) {
            for (int i = 0; i < declaredMethods.length; i++) {
                if (!isCandidate(declaredMethods[i])) {
                    declaredMethods[i] = null;
                }
            }
            for (Method method : declaredMethods) {
                addMethodIfPossible(method, BeanUtil.PREFIX_GETTER_IS, 0, (v0, v1) -> {
                    v0.addGetter(v1);
                });
            }
            for (Method method2 : declaredMethods) {
                addMethodIfPossible(method2, BeanUtil.PREFIX_GETTER_GET, 0, (v0, v1) -> {
                    v0.addGetter(v1);
                });
            }
            for (Method method3 : declaredMethods) {
                addMethodIfPossible(method3, "set", 1, (v0, v1) -> {
                    v0.addSetter(v1);
                });
            }
            for (Field field : declaredFields) {
                addField(field);
            }
        }

        private boolean isCandidate(Method method) {
            int modifiers = method.getModifiers();
            return (Modifier.isPrivate(modifiers) || Modifier.isProtected(modifiers) || Modifier.isAbstract(modifiers) || Modifier.isStatic(modifiers) || method.isBridge() || Object.class.equals(method.getDeclaringClass()) || Class.class.equals(method.getDeclaringClass()) || method.getName().indexOf(36) != -1) ? false : true;
        }

        private void addMethodIfPossible(Method method, String prefix, int parameterCount, BiConsumer<BeanProperty, Method> consumer) {
            if (method != null && method.getParameterCount() == parameterCount && method.getName().startsWith(prefix) && method.getName().length() > prefix.length()) {
                String propertyName = Introspector.decapitalize(method.getName().substring(prefix.length()));
                consumer.accept(this.properties.computeIfAbsent(propertyName, this::getBeanProperty), method);
            }
        }

        private BeanProperty getBeanProperty(String name) {
            return new BeanProperty(name, this.type);
        }

        private void addField(Field field) {
            BeanProperty property = this.properties.get(field.getName());
            if (property != null) {
                property.addField(field);
            }
        }

        protected final ResolvableType getType() {
            return this.type;
        }

        protected final Class<?> getResolvedType() {
            return this.resolvedType;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public final Map<String, BeanProperty> getProperties() {
            return this.properties;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static BeanProperties of(Bindable<?> bindable) {
            ResolvableType type = bindable.getType();
            Class<?> resolvedType = type.resolve(Object.class);
            return new BeanProperties(type, resolvedType);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/bind/JavaBeanBinder$Bean.class */
    static class Bean<T> extends BeanProperties {
        private static Bean<?> cached;

        Bean(ResolvableType type, Class<?> resolvedType) {
            super(type, resolvedType);
        }

        BeanSupplier<T> getSupplier(Bindable<T> target) {
            return new BeanSupplier<>(() -> {
                Object instance = null;
                if (target.getValue() != null) {
                    instance = target.getValue().get();
                }
                if (instance == null) {
                    instance = BeanUtils.instantiateClass(getResolvedType());
                }
                return instance;
            });
        }

        static <T> Bean<T> get(Bindable<T> bindable, boolean z) {
            ResolvableType type = bindable.getType();
            Class<?> resolve = type.resolve(Object.class);
            Supplier<T> value = bindable.getValue();
            T t = null;
            if (z && value != null) {
                t = value.get();
                resolve = t != null ? t.getClass() : resolve;
            }
            if (t == null && !isInstantiable(resolve)) {
                return null;
            }
            Bean<?> bean = cached;
            if (bean == null || !bean.isOfType(type, resolve)) {
                bean = new Bean<>(type, resolve);
                cached = bean;
            }
            return (Bean<T>) bean;
        }

        private static boolean isInstantiable(Class<?> type) {
            if (type.isInterface()) {
                return false;
            }
            try {
                type.getDeclaredConstructor(new Class[0]);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        private boolean isOfType(ResolvableType type, Class<?> resolvedType) {
            if (getType().hasGenerics() || type.hasGenerics()) {
                return getType().equals(type);
            }
            return getResolvedType() != null && getResolvedType().equals(resolvedType);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/bind/JavaBeanBinder$BeanSupplier.class */
    public static class BeanSupplier<T> implements Supplier<T> {
        private final Supplier<T> factory;
        private T instance;

        BeanSupplier(Supplier<T> factory) {
            this.factory = factory;
        }

        @Override // java.util.function.Supplier
        public T get() {
            if (this.instance == null) {
                this.instance = this.factory.get();
            }
            return this.instance;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/bind/JavaBeanBinder$BeanProperty.class */
    public static class BeanProperty {
        private final String name;
        private final ResolvableType declaringClassType;
        private Method getter;
        private Method setter;
        private Field field;

        BeanProperty(String name, ResolvableType declaringClassType) {
            this.name = DataObjectPropertyName.toDashedForm(name);
            this.declaringClassType = declaringClassType;
        }

        void addGetter(Method getter) {
            if (this.getter == null || this.getter.getName().startsWith(BeanUtil.PREFIX_GETTER_IS)) {
                this.getter = getter;
            }
        }

        void addSetter(Method setter) {
            if (this.setter == null || isBetterSetter(setter)) {
                this.setter = setter;
            }
        }

        private boolean isBetterSetter(Method setter) {
            return this.getter != null && this.getter.getReturnType().equals(setter.getParameterTypes()[0]);
        }

        void addField(Field field) {
            if (this.field == null) {
                this.field = field;
            }
        }

        String getName() {
            return this.name;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public ResolvableType getType() {
            if (this.setter != null) {
                MethodParameter methodParameter = new MethodParameter(this.setter, 0);
                return ResolvableType.forMethodParameter(methodParameter, this.declaringClassType);
            }
            MethodParameter methodParameter2 = new MethodParameter(this.getter, -1);
            return ResolvableType.forMethodParameter(methodParameter2, this.declaringClassType);
        }

        Annotation[] getAnnotations() {
            try {
                if (this.field != null) {
                    return this.field.getDeclaredAnnotations();
                }
                return null;
            } catch (Exception e) {
                return null;
            }
        }

        Supplier<Object> getValue(Supplier<?> instance) {
            if (this.getter == null) {
                return null;
            }
            return () -> {
                try {
                    this.getter.setAccessible(true);
                    return this.getter.invoke(instance.get(), new Object[0]);
                } catch (Exception ex) {
                    if (isUninitializedKotlinProperty(ex)) {
                        return null;
                    }
                    throw new IllegalStateException("Unable to get value for property " + this.name, ex);
                }
            };
        }

        private boolean isUninitializedKotlinProperty(Exception ex) {
            if (ex instanceof InvocationTargetException) {
                InvocationTargetException invocationTargetException = (InvocationTargetException) ex;
                if ("kotlin.UninitializedPropertyAccessException".equals(invocationTargetException.getTargetException().getClass().getName())) {
                    return true;
                }
            }
            return false;
        }

        boolean isSettable() {
            return this.setter != null;
        }

        void setValue(Supplier<?> instance, Object value) {
            try {
                this.setter.setAccessible(true);
                this.setter.invoke(instance.get(), value);
            } catch (Exception ex) {
                throw new IllegalStateException("Unable to set value for property " + this.name, ex);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Method getGetter() {
            return this.getter;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Method getSetter() {
            return this.setter;
        }
    }
}

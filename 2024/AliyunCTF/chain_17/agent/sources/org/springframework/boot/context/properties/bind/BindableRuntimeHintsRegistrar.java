package org.springframework.boot.context.properties.bind;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import kotlin.jvm.JvmClassMappingKt;
import kotlin.reflect.KClass;
import org.springframework.aot.hint.ExecutableMode;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.ReflectionHints;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.boot.context.properties.bind.JavaBeanBinder;
import org.springframework.core.KotlinDetector;
import org.springframework.core.KotlinReflectionParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.PrioritizedParameterNameDiscoverer;
import org.springframework.core.ResolvableType;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/bind/BindableRuntimeHintsRegistrar.class */
public class BindableRuntimeHintsRegistrar implements RuntimeHintsRegistrar {
    private final Bindable<?>[] bindables;

    /* JADX INFO: Access modifiers changed from: protected */
    public BindableRuntimeHintsRegistrar(Class<?>... types) {
        this((Bindable<?>[]) Stream.of((Object[]) types).map(Bindable::of).toArray(x$0 -> {
            return new Bindable[x$0];
        }));
    }

    protected BindableRuntimeHintsRegistrar(Bindable<?>... bindables) {
        this.bindables = bindables;
    }

    @Override // org.springframework.aot.hint.RuntimeHintsRegistrar
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        registerHints(hints);
    }

    public void registerHints(RuntimeHints hints) {
        Set<Class<?>> compiledWithoutParameters = new HashSet<>();
        for (Bindable<?> bindable : this.bindables) {
            new Processor(this, bindable, compiledWithoutParameters).process(hints.reflection());
        }
        if (!compiledWithoutParameters.isEmpty()) {
            throw new MissingParametersCompilerArgumentException(compiledWithoutParameters);
        }
    }

    public static BindableRuntimeHintsRegistrar forTypes(Iterable<Class<?>> types) {
        Assert.notNull(types, "Types must not be null");
        return forTypes((Class<?>[]) StreamSupport.stream(types.spliterator(), false).toArray(x$0 -> {
            return new Class[x$0];
        }));
    }

    public static BindableRuntimeHintsRegistrar forTypes(Class<?>... types) {
        return new BindableRuntimeHintsRegistrar(types);
    }

    public static BindableRuntimeHintsRegistrar forBindables(Iterable<Bindable<?>> bindables) {
        Assert.notNull(bindables, "Bindables must not be null");
        return forBindables((Bindable<?>[]) StreamSupport.stream(bindables.spliterator(), false).toArray(x$0 -> {
            return new Bindable[x$0];
        }));
    }

    public static BindableRuntimeHintsRegistrar forBindables(Bindable<?>... bindables) {
        return new BindableRuntimeHintsRegistrar(bindables);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/bind/BindableRuntimeHintsRegistrar$Processor.class */
    public final class Processor {
        private static final ParameterNameDiscoverer parameterNameDiscoverer;
        private final Class<?> type;
        private final Constructor<?> bindConstructor;
        private final JavaBeanBinder.BeanProperties bean;
        private final Set<Class<?>> seen;
        private final Set<Class<?>> compiledWithoutParameters;

        static {
            PrioritizedParameterNameDiscoverer discoverer = new PrioritizedParameterNameDiscoverer();
            if (KotlinDetector.isKotlinReflectPresent()) {
                discoverer.addDiscoverer(new KotlinReflectionParameterNameDiscoverer());
            }
            discoverer.addDiscoverer(new StandardReflectionParameterNameDiscoverer());
            parameterNameDiscoverer = discoverer;
        }

        Processor(final BindableRuntimeHintsRegistrar this$0, Bindable<?> bindable, Set<Class<?>> compiledWithoutParameters) {
            this(bindable, false, new HashSet(), compiledWithoutParameters);
        }

        private Processor(Bindable<?> bindable, boolean nestedType, Set<Class<?>> seen, Set<Class<?>> compiledWithoutParameters) {
            Constructor<?> constructor;
            this.type = bindable.getType().getRawClass();
            if (bindable.getBindMethod() != BindMethod.JAVA_BEAN) {
                constructor = BindConstructorProvider.DEFAULT.getBindConstructor(bindable.getType().resolve(), nestedType);
            } else {
                constructor = null;
            }
            this.bindConstructor = constructor;
            this.bean = JavaBeanBinder.BeanProperties.of(bindable);
            this.seen = seen;
            this.compiledWithoutParameters = compiledWithoutParameters;
        }

        void process(ReflectionHints hints) {
            if (this.seen.contains(this.type)) {
                return;
            }
            this.seen.add(this.type);
            handleConstructor(hints);
            if (this.bindConstructor != null) {
                handleValueObjectProperties(hints);
            } else if (this.bean != null && !this.bean.getProperties().isEmpty()) {
                handleJavaBeanProperties(hints);
            }
        }

        private void handleConstructor(ReflectionHints hints) {
            if (this.bindConstructor != null) {
                verifyParameterNamesAreAvailable();
                if (KotlinDetector.isKotlinType(this.bindConstructor.getDeclaringClass())) {
                    KotlinDelegate.handleConstructor(hints, this.bindConstructor);
                    return;
                } else {
                    hints.registerConstructor(this.bindConstructor, ExecutableMode.INVOKE);
                    return;
                }
            }
            Arrays.stream(this.type.getDeclaredConstructors()).filter(this::hasNoParameters).findFirst().ifPresent(constructor -> {
                hints.registerConstructor(constructor, ExecutableMode.INVOKE);
            });
        }

        private void verifyParameterNamesAreAvailable() {
            String[] parameterNames = parameterNameDiscoverer.getParameterNames(this.bindConstructor);
            if (parameterNames == null) {
                this.compiledWithoutParameters.add(this.bindConstructor.getDeclaringClass());
            }
        }

        private boolean hasNoParameters(Constructor<?> candidate) {
            return candidate.getParameterCount() == 0;
        }

        private void handleValueObjectProperties(ReflectionHints hints) {
            for (int i = 0; i < this.bindConstructor.getParameterCount(); i++) {
                String propertyName = this.bindConstructor.getParameters()[i].getName();
                ResolvableType propertyType = ResolvableType.forConstructorParameter(this.bindConstructor, i);
                handleProperty(hints, propertyName, propertyType);
            }
        }

        private void handleJavaBeanProperties(ReflectionHints hints) {
            Map<String, JavaBeanBinder.BeanProperty> properties = this.bean.getProperties();
            properties.forEach((name, property) -> {
                Method getter = property.getGetter();
                if (getter != null) {
                    hints.registerMethod(getter, ExecutableMode.INVOKE);
                }
                Method setter = property.getSetter();
                if (setter != null) {
                    hints.registerMethod(setter, ExecutableMode.INVOKE);
                }
                handleProperty(hints, name, property.getType());
            });
        }

        private void handleProperty(ReflectionHints hints, String propertyName, ResolvableType propertyType) {
            Class<?> propertyClass = propertyType.resolve();
            if (propertyClass == null || propertyClass.equals(this.type)) {
                return;
            }
            Class<?> componentType = getComponentClass(propertyType);
            if (componentType != null) {
                if (!isJavaType(componentType)) {
                    processNested(componentType, hints);
                }
            } else if (isNestedType(propertyName, propertyClass)) {
                processNested(propertyClass, hints);
            }
        }

        private void processNested(Class<?> type, ReflectionHints hints) {
            new Processor(Bindable.of(type), true, this.seen, this.compiledWithoutParameters).process(hints);
        }

        private Class<?> getComponentClass(ResolvableType type) {
            ResolvableType componentType = getComponentType(type);
            if (componentType == null) {
                return null;
            }
            if (isContainer(componentType)) {
                return getComponentClass(componentType);
            }
            return componentType.toClass();
        }

        private ResolvableType getComponentType(ResolvableType type) {
            if (type.isArray()) {
                return type.getComponentType();
            }
            if (isCollection(type)) {
                return type.asCollection().getGeneric(new int[0]);
            }
            if (isMap(type)) {
                return type.asMap().getGeneric(1);
            }
            return null;
        }

        private boolean isContainer(ResolvableType type) {
            return type.isArray() || isCollection(type) || isMap(type);
        }

        private boolean isCollection(ResolvableType type) {
            return Collection.class.isAssignableFrom(type.toClass());
        }

        private boolean isMap(ResolvableType type) {
            return Map.class.isAssignableFrom(type.toClass());
        }

        private boolean isNestedType(String propertyName, Class<?> propertyType) {
            Class<?> declaringClass = propertyType.getDeclaringClass();
            if (declaringClass != null && isNested(declaringClass, this.type)) {
                return true;
            }
            Field field = ReflectionUtils.findField(this.type, propertyName);
            return field != null && MergedAnnotations.from(field).isPresent(Nested.class);
        }

        private static boolean isNested(Class<?> type, Class<?> candidate) {
            if (type.isAssignableFrom(candidate)) {
                return true;
            }
            return candidate.getDeclaringClass() != null && isNested(type, candidate.getDeclaringClass());
        }

        private boolean isJavaType(Class<?> candidate) {
            return candidate.getPackageName().startsWith("java.");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/bind/BindableRuntimeHintsRegistrar$KotlinDelegate.class */
    public static final class KotlinDelegate {
        private KotlinDelegate() {
        }

        static void handleConstructor(ReflectionHints hints, Constructor<?> constructor) {
            KClass<?> kClass = JvmClassMappingKt.getKotlinClass(constructor.getDeclaringClass());
            if (kClass.isData()) {
                hints.registerType(constructor.getDeclaringClass(), MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);
            } else {
                hints.registerConstructor(constructor, ExecutableMode.INVOKE);
            }
        }
    }
}

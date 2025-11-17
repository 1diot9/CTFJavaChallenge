package org.springframework.boot.util;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/util/Instantiator.class */
public class Instantiator<T> {
    private static final Comparator<Constructor<?>> CONSTRUCTOR_COMPARATOR = Comparator.comparingInt((v0) -> {
        return v0.getParameterCount();
    }).reversed();
    private static final FailureHandler throwingFailureHandler = (type, implementationName, failure) -> {
        throw new IllegalArgumentException("Unable to instantiate " + implementationName + " [" + type.getName() + "]", failure);
    };
    private final Class<?> type;
    private final Map<Class<?>, Function<Class<?>, Object>> availableParameters;
    private final FailureHandler failureHandler;

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/util/Instantiator$AvailableParameters.class */
    public interface AvailableParameters {
        void add(Class<?> type, Object instance);

        void add(Class<?> type, Function<Class<?>, Object> factory);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/util/Instantiator$FailureHandler.class */
    public interface FailureHandler {
        void handleFailure(Class<?> type, String implementationName, Throwable failure);
    }

    public Instantiator(Class<?> type, Consumer<AvailableParameters> availableParameters) {
        this(type, availableParameters, throwingFailureHandler);
    }

    public Instantiator(Class<?> type, Consumer<AvailableParameters> availableParameters, FailureHandler failureHandler) {
        this.type = type;
        this.availableParameters = getAvailableParameters(availableParameters);
        this.failureHandler = failureHandler;
    }

    private Map<Class<?>, Function<Class<?>, Object>> getAvailableParameters(Consumer<AvailableParameters> availableParameters) {
        final Map<Class<?>, Function<Class<?>, Object>> result = new LinkedHashMap<>();
        availableParameters.accept(new AvailableParameters() { // from class: org.springframework.boot.util.Instantiator.1
            @Override // org.springframework.boot.util.Instantiator.AvailableParameters
            public void add(Class<?> type, Object instance) {
                result.put(type, factoryType -> {
                    return instance;
                });
            }

            @Override // org.springframework.boot.util.Instantiator.AvailableParameters
            public void add(Class<?> type, Function<Class<?>, Object> factory) {
                result.put(type, factory);
            }
        });
        return Collections.unmodifiableMap(result);
    }

    public List<T> instantiate(Collection<String> names) {
        return instantiate((ClassLoader) null, names);
    }

    public List<T> instantiate(ClassLoader classLoader, Collection<String> names) {
        Assert.notNull(names, "Names must not be null");
        return instantiate(names.stream().map(name -> {
            return TypeSupplier.forName(classLoader, name);
        }));
    }

    public List<T> instantiateTypes(Collection<Class<?>> types) {
        Assert.notNull(types, "Types must not be null");
        return instantiate(types.stream().map(TypeSupplier::forType));
    }

    private List<T> instantiate(Stream<TypeSupplier> typeSuppliers) {
        return typeSuppliers.map(this::instantiate).sorted(AnnotationAwareOrderComparator.INSTANCE).toList();
    }

    private T instantiate(TypeSupplier typeSupplier) {
        try {
            Class<?> type = typeSupplier.get();
            Assert.isAssignable(this.type, type);
            return instantiate(type);
        } catch (Throwable ex) {
            this.failureHandler.handleFailure(this.type, typeSupplier.getName(), ex);
            return null;
        }
    }

    private T instantiate(Class<?> cls) throws Exception {
        Constructor<?>[] declaredConstructors = cls.getDeclaredConstructors();
        Arrays.sort(declaredConstructors, CONSTRUCTOR_COMPARATOR);
        for (Constructor<?> constructor : declaredConstructors) {
            Object[] args = getArgs(constructor.getParameterTypes());
            if (args != null) {
                ReflectionUtils.makeAccessible(constructor);
                return (T) constructor.newInstance(args);
            }
        }
        throw new IllegalAccessException("Class [" + cls.getName() + "] has no suitable constructor");
    }

    private Object[] getArgs(Class<?>[] parameterTypes) {
        Object[] args = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            Function<Class<?>, Object> parameter = getAvailableParameter(parameterTypes[i]);
            if (parameter == null) {
                return null;
            }
            args[i] = parameter.apply(this.type);
        }
        return args;
    }

    private Function<Class<?>, Object> getAvailableParameter(Class<?> parameterType) {
        for (Map.Entry<Class<?>, Function<Class<?>, Object>> entry : this.availableParameters.entrySet()) {
            if (entry.getKey().isAssignableFrom(parameterType)) {
                return entry.getValue();
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/util/Instantiator$TypeSupplier.class */
    public interface TypeSupplier {
        String getName();

        Class<?> get() throws ClassNotFoundException;

        static TypeSupplier forName(final ClassLoader classLoader, final String name) {
            return new TypeSupplier() { // from class: org.springframework.boot.util.Instantiator.TypeSupplier.1
                @Override // org.springframework.boot.util.Instantiator.TypeSupplier
                public String getName() {
                    return name;
                }

                @Override // org.springframework.boot.util.Instantiator.TypeSupplier
                public Class<?> get() throws ClassNotFoundException {
                    return ClassUtils.forName(name, classLoader);
                }
            };
        }

        static TypeSupplier forType(final Class<?> type) {
            return new TypeSupplier() { // from class: org.springframework.boot.util.Instantiator.TypeSupplier.2
                @Override // org.springframework.boot.util.Instantiator.TypeSupplier
                public String getName() {
                    return type.getName();
                }

                @Override // org.springframework.boot.util.Instantiator.TypeSupplier
                public Class<?> get() throws ClassNotFoundException {
                    return type;
                }
            };
        }
    }
}

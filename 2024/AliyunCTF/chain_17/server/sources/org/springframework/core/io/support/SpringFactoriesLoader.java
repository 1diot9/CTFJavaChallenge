package org.springframework.core.io.support;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import kotlin.jvm.JvmClassMappingKt;
import kotlin.reflect.KFunction;
import kotlin.reflect.KParameter;
import kotlin.reflect.full.KClasses;
import kotlin.reflect.jvm.KCallablesJvm;
import kotlin.reflect.jvm.ReflectJvmMapping;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.KotlinDetector;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.io.UrlResource;
import org.springframework.core.log.LogMessage;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/support/SpringFactoriesLoader.class */
public class SpringFactoriesLoader {
    public static final String FACTORIES_RESOURCE_LOCATION = "META-INF/spring.factories";
    private static final FailureHandler THROWING_FAILURE_HANDLER = FailureHandler.throwing();
    private static final Log logger = LogFactory.getLog((Class<?>) SpringFactoriesLoader.class);
    static final Map<ClassLoader, Map<String, SpringFactoriesLoader>> cache = new ConcurrentReferenceHashMap();

    @Nullable
    private final ClassLoader classLoader;
    private final Map<String, List<String>> factories;

    /* JADX INFO: Access modifiers changed from: protected */
    public SpringFactoriesLoader(@Nullable ClassLoader classLoader, Map<String, List<String>> factories) {
        this.classLoader = classLoader;
        this.factories = factories;
    }

    public <T> List<T> load(Class<T> factoryType) {
        return load(factoryType, null, null);
    }

    public <T> List<T> load(Class<T> factoryType, @Nullable ArgumentResolver argumentResolver) {
        return load(factoryType, argumentResolver, null);
    }

    public <T> List<T> load(Class<T> factoryType, @Nullable FailureHandler failureHandler) {
        return load(factoryType, null, failureHandler);
    }

    public <T> List<T> load(Class<T> factoryType, @Nullable ArgumentResolver argumentResolver, @Nullable FailureHandler failureHandler) {
        Assert.notNull(factoryType, "'factoryType' must not be null");
        List<String> implementationNames = loadFactoryNames(factoryType);
        logger.trace(LogMessage.format("Loaded [%s] names: %s", factoryType.getName(), implementationNames));
        ArrayList arrayList = new ArrayList(implementationNames.size());
        FailureHandler failureHandlerToUse = failureHandler != null ? failureHandler : THROWING_FAILURE_HANDLER;
        for (String implementationName : implementationNames) {
            Object instantiateFactory = instantiateFactory(implementationName, factoryType, argumentResolver, failureHandlerToUse);
            if (instantiateFactory != null) {
                arrayList.add(instantiateFactory);
            }
        }
        AnnotationAwareOrderComparator.sort(arrayList);
        return arrayList;
    }

    private List<String> loadFactoryNames(Class<?> factoryType) {
        return this.factories.getOrDefault(factoryType.getName(), Collections.emptyList());
    }

    @Nullable
    protected <T> T instantiateFactory(String implementationName, Class<T> type, @Nullable ArgumentResolver argumentResolver, FailureHandler failureHandler) {
        try {
            Class<?> factoryImplementationClass = ClassUtils.forName(implementationName, this.classLoader);
            Assert.isTrue(type.isAssignableFrom(factoryImplementationClass), (Supplier<String>) () -> {
                return "Class [%s] is not assignable to factory type [%s]".formatted(implementationName, type.getName());
            });
            FactoryInstantiator<T> factoryInstantiator = FactoryInstantiator.forClass(factoryImplementationClass);
            return factoryInstantiator.instantiate(argumentResolver);
        } catch (Throwable ex) {
            failureHandler.handleFailure(type, implementationName, ex);
            return null;
        }
    }

    public static <T> List<T> loadFactories(Class<T> factoryType, @Nullable ClassLoader classLoader) {
        return forDefaultResourceLocation(classLoader).load(factoryType);
    }

    @Deprecated(since = "6.0")
    public static List<String> loadFactoryNames(Class<?> factoryType, @Nullable ClassLoader classLoader) {
        return forDefaultResourceLocation(classLoader).loadFactoryNames(factoryType);
    }

    public static SpringFactoriesLoader forDefaultResourceLocation() {
        return forDefaultResourceLocation(null);
    }

    public static SpringFactoriesLoader forDefaultResourceLocation(@Nullable ClassLoader classLoader) {
        return forResourceLocation(FACTORIES_RESOURCE_LOCATION, classLoader);
    }

    public static SpringFactoriesLoader forResourceLocation(String resourceLocation) {
        return forResourceLocation(resourceLocation, null);
    }

    public static SpringFactoriesLoader forResourceLocation(String resourceLocation, @Nullable ClassLoader classLoader) {
        Assert.hasText(resourceLocation, "'resourceLocation' must not be empty");
        ClassLoader resourceClassLoader = classLoader != null ? classLoader : SpringFactoriesLoader.class.getClassLoader();
        Map<String, SpringFactoriesLoader> loaders = cache.computeIfAbsent(resourceClassLoader, key -> {
            return new ConcurrentReferenceHashMap();
        });
        return loaders.computeIfAbsent(resourceLocation, key2 -> {
            return new SpringFactoriesLoader(classLoader, loadFactoriesResource(resourceClassLoader, resourceLocation));
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static Map<String, List<String>> loadFactoriesResource(ClassLoader classLoader, String resourceLocation) {
        Map<String, List<String>> result = new LinkedHashMap<>();
        try {
            Enumeration<URL> urls = classLoader.getResources(resourceLocation);
            while (urls.hasMoreElements()) {
                UrlResource resource = new UrlResource(urls.nextElement());
                Properties properties = PropertiesLoaderUtils.loadProperties(resource);
                properties.forEach((name, value) -> {
                    String[] factoryImplementationNames = StringUtils.commaDelimitedListToStringArray((String) value);
                    List<String> implementations = (List) result.computeIfAbsent(((String) name).trim(), key -> {
                        return new ArrayList(factoryImplementationNames.length);
                    });
                    Stream map = Arrays.stream(factoryImplementationNames).map((v0) -> {
                        return v0.trim();
                    });
                    Objects.requireNonNull(implementations);
                    map.forEach((v1) -> {
                        r1.add(v1);
                    });
                });
            }
            result.replaceAll(SpringFactoriesLoader::toDistinctUnmodifiableList);
            return Collections.unmodifiableMap(result);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Unable to load factories from location [" + resourceLocation + "]", ex);
        }
    }

    private static List<String> toDistinctUnmodifiableList(String factoryType, List<String> implementations) {
        return implementations.stream().distinct().toList();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/support/SpringFactoriesLoader$FactoryInstantiator.class */
    public static final class FactoryInstantiator<T> {
        private final Constructor<T> constructor;

        private FactoryInstantiator(Constructor<T> constructor) {
            ReflectionUtils.makeAccessible((Constructor<?>) constructor);
            this.constructor = constructor;
        }

        T instantiate(@Nullable ArgumentResolver argumentResolver) throws Exception {
            Object[] resolveArgs = resolveArgs(argumentResolver);
            if (isKotlinType(this.constructor.getDeclaringClass())) {
                return (T) KotlinDelegate.instantiate(this.constructor, resolveArgs);
            }
            return this.constructor.newInstance(resolveArgs);
        }

        private Object[] resolveArgs(@Nullable ArgumentResolver argumentResolver) {
            Class<?>[] types = this.constructor.getParameterTypes();
            if (argumentResolver != null) {
                Stream stream = Arrays.stream(types);
                Objects.requireNonNull(argumentResolver);
                return stream.map(argumentResolver::resolve).toArray();
            }
            return new Object[types.length];
        }

        static <T> FactoryInstantiator<T> forClass(Class<?> factoryImplementationClass) {
            Constructor<?> constructor = findConstructor(factoryImplementationClass);
            Assert.state(constructor != null, (Supplier<String>) () -> {
                return "Class [%s] has no suitable constructor".formatted(factoryImplementationClass.getName());
            });
            return new FactoryInstantiator<>(constructor);
        }

        @Nullable
        private static Constructor<?> findConstructor(Class<?> factoryImplementationClass) {
            Constructor<?> constructor = findPrimaryKotlinConstructor(factoryImplementationClass);
            Constructor<?> constructor2 = constructor != null ? constructor : findSingleConstructor(factoryImplementationClass.getConstructors());
            Constructor<?> constructor3 = constructor2 != null ? constructor2 : findSingleConstructor(factoryImplementationClass.getDeclaredConstructors());
            return constructor3 != null ? constructor3 : findDeclaredConstructor(factoryImplementationClass);
        }

        @Nullable
        private static Constructor<?> findPrimaryKotlinConstructor(Class<?> factoryImplementationClass) {
            if (isKotlinType(factoryImplementationClass)) {
                return KotlinDelegate.findPrimaryConstructor(factoryImplementationClass);
            }
            return null;
        }

        private static boolean isKotlinType(Class<?> factoryImplementationClass) {
            return KotlinDetector.isKotlinReflectPresent() && KotlinDetector.isKotlinType(factoryImplementationClass);
        }

        @Nullable
        private static Constructor<?> findSingleConstructor(Constructor<?>[] constructors) {
            if (constructors.length == 1) {
                return constructors[0];
            }
            return null;
        }

        @Nullable
        private static Constructor<?> findDeclaredConstructor(Class<?> factoryImplementationClass) {
            try {
                return factoryImplementationClass.getDeclaredConstructor(new Class[0]);
            } catch (NoSuchMethodException e) {
                return null;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/support/SpringFactoriesLoader$KotlinDelegate.class */
    public static class KotlinDelegate {
        private KotlinDelegate() {
        }

        @Nullable
        static <T> Constructor<T> findPrimaryConstructor(Class<T> clazz) {
            try {
                KFunction<T> primaryConstructor = KClasses.getPrimaryConstructor(JvmClassMappingKt.getKotlinClass(clazz));
                if (primaryConstructor != null) {
                    Constructor<T> constructor = ReflectJvmMapping.getJavaConstructor(primaryConstructor);
                    Assert.state(constructor != null, (Supplier<String>) () -> {
                        return "Failed to find Java constructor for Kotlin primary constructor: " + clazz.getName();
                    });
                    return constructor;
                }
                return null;
            } catch (UnsupportedOperationException e) {
                return null;
            }
        }

        static <T> T instantiate(Constructor<T> constructor, Object[] objArr) throws Exception {
            KFunction kotlinFunction = ReflectJvmMapping.getKotlinFunction(constructor);
            if (kotlinFunction == null) {
                return constructor.newInstance(objArr);
            }
            makeAccessible(constructor, kotlinFunction);
            return (T) instantiate(kotlinFunction, convertArgs(objArr, kotlinFunction.getParameters()));
        }

        private static <T> void makeAccessible(Constructor<T> constructor, KFunction<T> kotlinConstructor) {
            if (!Modifier.isPublic(constructor.getModifiers()) || !Modifier.isPublic(constructor.getDeclaringClass().getModifiers())) {
                KCallablesJvm.setAccessible(kotlinConstructor, true);
            }
        }

        private static Map<KParameter, Object> convertArgs(Object[] args, List<KParameter> parameters) {
            Map<KParameter, Object> result = CollectionUtils.newHashMap(parameters.size());
            Assert.isTrue(args.length <= parameters.size(), "Number of provided arguments should be less than or equal to the number of constructor parameters");
            for (int i = 0; i < args.length; i++) {
                if (!parameters.get(i).isOptional() || args[i] != null) {
                    result.put(parameters.get(i), args[i]);
                }
            }
            return result;
        }

        private static <T> T instantiate(KFunction<T> kFunction, Map<KParameter, Object> map) {
            return (T) kFunction.callBy(map);
        }
    }

    @FunctionalInterface
    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/support/SpringFactoriesLoader$ArgumentResolver.class */
    public interface ArgumentResolver {
        @Nullable
        <T> T resolve(Class<T> type);

        default <T> ArgumentResolver and(Class<T> type, T value) {
            return and(of(type, value));
        }

        default <T> ArgumentResolver andSupplied(Class<T> type, Supplier<T> valueSupplier) {
            return and(ofSupplied(type, valueSupplier));
        }

        default ArgumentResolver and(ArgumentResolver argumentResolver) {
            return from(type -> {
                Object resolved = resolve(type);
                return resolved != null ? resolved : argumentResolver.resolve(type);
            });
        }

        static ArgumentResolver none() {
            return from(type -> {
                return null;
            });
        }

        static <T> ArgumentResolver of(Class<T> type, T value) {
            return ofSupplied(type, () -> {
                return value;
            });
        }

        static <T> ArgumentResolver ofSupplied(Class<T> type, Supplier<T> valueSupplier) {
            return from(candidateType -> {
                if (candidateType.equals(type)) {
                    return valueSupplier.get();
                }
                return null;
            });
        }

        static ArgumentResolver from(final Function<Class<?>, Object> function) {
            return new ArgumentResolver() { // from class: org.springframework.core.io.support.SpringFactoriesLoader.ArgumentResolver.1
                @Override // org.springframework.core.io.support.SpringFactoriesLoader.ArgumentResolver
                public <T> T resolve(Class<T> cls) {
                    return (T) function.apply(cls);
                }
            };
        }
    }

    @FunctionalInterface
    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/support/SpringFactoriesLoader$FailureHandler.class */
    public interface FailureHandler {
        void handleFailure(Class<?> factoryType, String factoryImplementationName, Throwable failure);

        static FailureHandler throwing() {
            return throwing(IllegalArgumentException::new);
        }

        static FailureHandler throwing(BiFunction<String, Throwable, ? extends RuntimeException> exceptionFactory) {
            return handleMessage((messageSupplier, failure) -> {
                throw ((RuntimeException) exceptionFactory.apply((String) messageSupplier.get(), failure));
            });
        }

        static FailureHandler logging(Log logger) {
            return handleMessage((messageSupplier, failure) -> {
                logger.trace(LogMessage.of(messageSupplier), failure);
            });
        }

        static FailureHandler handleMessage(BiConsumer<Supplier<String>, Throwable> messageHandler) {
            return (factoryType, factoryImplementationName, failure) -> {
                Supplier<String> messageSupplier = () -> {
                    return "Unable to instantiate factory class [%s] for factory type [%s]".formatted(factoryImplementationName, factoryType.getName());
                };
                messageHandler.accept(messageSupplier, failure);
            };
        }
    }
}

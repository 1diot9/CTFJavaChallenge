package org.springframework.boot.autoconfigure.service.connection;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/service/connection/ConnectionDetailsFactories.class */
public class ConnectionDetailsFactories {
    private static final Log logger = LogFactory.getLog((Class<?>) ConnectionDetailsFactories.class);
    private final List<Registration<?, ?>> registrations;

    public ConnectionDetailsFactories() {
        this(SpringFactoriesLoader.forDefaultResourceLocation(ConnectionDetailsFactory.class.getClassLoader()));
    }

    ConnectionDetailsFactories(SpringFactoriesLoader loader) {
        this.registrations = new ArrayList();
        List<ConnectionDetailsFactory> factories = loader.load(ConnectionDetailsFactory.class, SpringFactoriesLoader.FailureHandler.logging(logger));
        Stream filter = factories.stream().map(Registration::get).filter((v0) -> {
            return Objects.nonNull(v0);
        });
        List<Registration<?, ?>> list = this.registrations;
        Objects.requireNonNull(list);
        filter.forEach((v1) -> {
            r1.add(v1);
        });
    }

    public <S> Map<Class<?>, ConnectionDetails> getConnectionDetails(S source, boolean required) throws ConnectionDetailsFactoryNotFoundException, ConnectionDetailsNotFoundException {
        List<Registration<S, ?>> registrations = getRegistrations(source, required);
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        for (Registration<S, ?> registration : registrations) {
            Object connectionDetails = registration.factory().getConnectionDetails(source);
            if (connectionDetails != null) {
                Class<?> connectionDetailsType = registration.connectionDetailsType();
                ConnectionDetails previous = (ConnectionDetails) linkedHashMap.put(connectionDetailsType, connectionDetails);
                Assert.state(previous == null, (Supplier<String>) () -> {
                    return "Duplicate connection details supplied for %s".formatted(connectionDetailsType.getName());
                });
            }
        }
        if (required && linkedHashMap.isEmpty()) {
            throw new ConnectionDetailsNotFoundException(source);
        }
        return Map.copyOf(linkedHashMap);
    }

    <S> List<Registration<S, ?>> getRegistrations(S source, boolean required) {
        Class<?> cls = source.getClass();
        List<Registration<S, ?>> result = new ArrayList<>();
        for (Registration<?, ?> candidate : this.registrations) {
            if (candidate.sourceType().isAssignableFrom(cls)) {
                result.add(candidate);
            }
        }
        if (required && result.isEmpty()) {
            throw new ConnectionDetailsFactoryNotFoundException(source);
        }
        result.sort(Comparator.comparing((v0) -> {
            return v0.factory();
        }, AnnotationAwareOrderComparator.INSTANCE));
        return List.copyOf(result);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/service/connection/ConnectionDetailsFactories$Registration.class */
    public static final class Registration<S, D extends ConnectionDetails> extends Record {
        private final Class<S> sourceType;
        private final Class<D> connectionDetailsType;
        private final ConnectionDetailsFactory<S, D> factory;

        Registration(Class<S> sourceType, Class<D> connectionDetailsType, ConnectionDetailsFactory<S, D> factory) {
            this.sourceType = sourceType;
            this.connectionDetailsType = connectionDetailsType;
            this.factory = factory;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Registration.class), Registration.class, "sourceType;connectionDetailsType;factory", "FIELD:Lorg/springframework/boot/autoconfigure/service/connection/ConnectionDetailsFactories$Registration;->sourceType:Ljava/lang/Class;", "FIELD:Lorg/springframework/boot/autoconfigure/service/connection/ConnectionDetailsFactories$Registration;->connectionDetailsType:Ljava/lang/Class;", "FIELD:Lorg/springframework/boot/autoconfigure/service/connection/ConnectionDetailsFactories$Registration;->factory:Lorg/springframework/boot/autoconfigure/service/connection/ConnectionDetailsFactory;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Registration.class), Registration.class, "sourceType;connectionDetailsType;factory", "FIELD:Lorg/springframework/boot/autoconfigure/service/connection/ConnectionDetailsFactories$Registration;->sourceType:Ljava/lang/Class;", "FIELD:Lorg/springframework/boot/autoconfigure/service/connection/ConnectionDetailsFactories$Registration;->connectionDetailsType:Ljava/lang/Class;", "FIELD:Lorg/springframework/boot/autoconfigure/service/connection/ConnectionDetailsFactories$Registration;->factory:Lorg/springframework/boot/autoconfigure/service/connection/ConnectionDetailsFactory;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Registration.class, Object.class), Registration.class, "sourceType;connectionDetailsType;factory", "FIELD:Lorg/springframework/boot/autoconfigure/service/connection/ConnectionDetailsFactories$Registration;->sourceType:Ljava/lang/Class;", "FIELD:Lorg/springframework/boot/autoconfigure/service/connection/ConnectionDetailsFactories$Registration;->connectionDetailsType:Ljava/lang/Class;", "FIELD:Lorg/springframework/boot/autoconfigure/service/connection/ConnectionDetailsFactories$Registration;->factory:Lorg/springframework/boot/autoconfigure/service/connection/ConnectionDetailsFactory;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public Class<S> sourceType() {
            return this.sourceType;
        }

        public Class<D> connectionDetailsType() {
            return this.connectionDetailsType;
        }

        public ConnectionDetailsFactory<S, D> factory() {
            return this.factory;
        }

        private static <S, D extends ConnectionDetails> Registration<S, D> get(ConnectionDetailsFactory<S, D> factory) {
            ResolvableType type = ResolvableType.forClass(ConnectionDetailsFactory.class, factory.getClass());
            if (!type.hasUnresolvableGenerics()) {
                Class<?>[] generics = type.resolveGenerics();
                return new Registration<>(generics[0], generics[1], factory);
            }
            return null;
        }
    }
}

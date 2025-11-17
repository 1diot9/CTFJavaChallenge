package org.apache.logging.log4j.util;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;

/* loaded from: server.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/util/ServiceLoaderUtil.class */
public final class ServiceLoaderUtil {
    private static final int MAX_BROKEN_SERVICES = 8;

    private ServiceLoaderUtil() {
    }

    public static <T> Stream<T> loadServices(final Class<T> serviceType, final MethodHandles.Lookup lookup) {
        return loadServices(serviceType, lookup, false);
    }

    public static <T> Stream<T> loadServices(final Class<T> serviceType, final MethodHandles.Lookup lookup, final boolean useTccl) {
        return loadServices(serviceType, lookup, useTccl, true);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <T> Stream<T> loadServices(final Class<T> serviceType, final MethodHandles.Lookup lookup, final boolean useTccl, final boolean verbose) {
        ClassLoader contextClassLoader;
        ClassLoader classLoader = lookup.lookupClass().getClassLoader();
        Stream<T> services = loadClassloaderServices(serviceType, lookup, classLoader, verbose);
        if (useTccl && (contextClassLoader = LoaderUtil.getThreadContextClassLoader()) != classLoader) {
            services = Stream.concat(services, loadClassloaderServices(serviceType, lookup, contextClassLoader, verbose));
        }
        if (OsgiServiceLocator.isAvailable()) {
            services = Stream.concat(services, OsgiServiceLocator.loadServices(serviceType, lookup, verbose));
        }
        Set<Class<?>> classes = new HashSet<>();
        return services.filter(service -> {
            return classes.add(service.getClass());
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <T> Stream<T> loadClassloaderServices(final Class<T> serviceType, final MethodHandles.Lookup lookup, final ClassLoader classLoader, final boolean verbose) {
        return StreamSupport.stream(new ServiceLoaderSpliterator(serviceType, lookup, classLoader, verbose), false);
    }

    static <T> Iterable<T> callServiceLoader(final MethodHandles.Lookup lookup, final Class<T> serviceType, final ClassLoader classLoader, final boolean verbose) {
        ServiceLoader<T> serviceLoader;
        try {
            MethodHandle loadHandle = lookup.findStatic(ServiceLoader.class, "load", MethodType.methodType(ServiceLoader.class, Class.class, ClassLoader.class));
            CallSite callSite = LambdaMetafactory.metafactory(lookup, "run", MethodType.methodType(PrivilegedAction.class, Class.class, ClassLoader.class), MethodType.methodType(Object.class), loadHandle, MethodType.methodType(ServiceLoader.class));
            PrivilegedAction<ServiceLoader<T>> action = (PrivilegedAction) callSite.getTarget().bindTo(serviceType).bindTo(classLoader).invoke();
            if (System.getSecurityManager() == null) {
                serviceLoader = action.run();
            } else {
                MethodHandle privilegedHandle = lookup.findStatic(AccessController.class, "doPrivileged", MethodType.methodType((Class<?>) Object.class, (Class<?>) PrivilegedAction.class));
                serviceLoader = (ServiceLoader) privilegedHandle.invoke(action);
            }
            return serviceLoader;
        } catch (Throwable e) {
            if (verbose) {
                StatusLogger.getLogger().error("Unable to load services for service {}", serviceType, e);
            }
            return Collections.emptyList();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/util/ServiceLoaderUtil$ServiceLoaderSpliterator.class */
    public static class ServiceLoaderSpliterator<S> implements Spliterator<S> {
        private final Iterator<S> serviceIterator;
        private final Logger logger;
        private final String serviceName;

        public ServiceLoaderSpliterator(final Class<S> serviceType, final MethodHandles.Lookup lookup, final ClassLoader classLoader, final boolean verbose) {
            this.serviceIterator = ServiceLoaderUtil.callServiceLoader(lookup, serviceType, classLoader, verbose).iterator();
            this.logger = verbose ? StatusLogger.getLogger() : null;
            this.serviceName = serviceType.toString();
        }

        @Override // java.util.Spliterator
        public boolean tryAdvance(Consumer<? super S> consumer) {
            int i = 8;
            while (true) {
                int i2 = i;
                i--;
                if (i2 > 0) {
                    try {
                    } catch (LinkageError | ServiceConfigurationError e) {
                        if (this.logger != null) {
                            this.logger.warn("Unable to load service class for service {}", this.serviceName, e);
                        }
                    } catch (Throwable th) {
                        if (this.logger != null) {
                            this.logger.warn("Unable to load service class for service {}", this.serviceName, th);
                        }
                        throw th;
                    }
                    if (this.serviceIterator.hasNext()) {
                        consumer.accept(this.serviceIterator.next());
                        return true;
                    }
                    continue;
                } else {
                    return false;
                }
            }
        }

        @Override // java.util.Spliterator
        public Spliterator<S> trySplit() {
            return null;
        }

        @Override // java.util.Spliterator
        public long estimateSize() {
            return Long.MAX_VALUE;
        }

        @Override // java.util.Spliterator
        public int characteristics() {
            return 1280;
        }
    }
}

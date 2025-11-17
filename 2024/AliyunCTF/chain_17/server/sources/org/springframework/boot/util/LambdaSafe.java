package org.springframework.boot.util;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.ResolvableType;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/util/LambdaSafe.class */
public final class LambdaSafe {
    private static final Method CLASS_GET_MODULE = ReflectionUtils.findMethod(Class.class, "getModule");
    private static final Method MODULE_GET_NAME;

    static {
        MODULE_GET_NAME = CLASS_GET_MODULE != null ? ReflectionUtils.findMethod(CLASS_GET_MODULE.getReturnType(), "getName") : null;
    }

    private LambdaSafe() {
    }

    public static <C, A> Callback<C, A> callback(Class<C> callbackType, C callbackInstance, A argument, Object... additionalArguments) {
        Assert.notNull(callbackType, "CallbackType must not be null");
        Assert.notNull(callbackInstance, "CallbackInstance must not be null");
        return new Callback<>(callbackType, callbackInstance, argument, additionalArguments);
    }

    public static <C, A> Callbacks<C, A> callbacks(Class<C> callbackType, Collection<? extends C> callbackInstances, A argument, Object... additionalArguments) {
        Assert.notNull(callbackType, "CallbackType must not be null");
        Assert.notNull(callbackInstances, "CallbackInstances must not be null");
        return new Callbacks<>(callbackType, callbackInstances, argument, additionalArguments);
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/util/LambdaSafe$LambdaSafeCallback.class */
    protected static abstract class LambdaSafeCallback<C, A, SELF extends LambdaSafeCallback<C, A, SELF>> {
        private final Class<C> callbackType;
        private final A argument;
        private final Object[] additionalArguments;
        private Log logger;
        private Filter<C, A> filter = new GenericTypeFilter();

        LambdaSafeCallback(Class<C> callbackType, A argument, Object[] additionalArguments) {
            this.callbackType = callbackType;
            this.argument = argument;
            this.additionalArguments = additionalArguments;
            this.logger = LogFactory.getLog((Class<?>) callbackType);
        }

        public SELF withLogger(Class<?> loggerSource) {
            return withLogger(LogFactory.getLog(loggerSource));
        }

        public SELF withLogger(Log logger) {
            Assert.notNull(logger, "Logger must not be null");
            this.logger = logger;
            return self();
        }

        SELF withFilter(Filter<C, A> filter) {
            Assert.notNull(filter, "Filter must not be null");
            this.filter = filter;
            return self();
        }

        protected final <R> InvocationResult<R> invoke(C callbackInstance, Supplier<R> supplier) {
            if (this.filter.match(this.callbackType, callbackInstance, this.argument, this.additionalArguments)) {
                try {
                    return InvocationResult.of(supplier.get());
                } catch (ClassCastException ex) {
                    if (!isLambdaGenericProblem(ex)) {
                        throw ex;
                    }
                    logNonMatchingType(callbackInstance, ex);
                }
            }
            return InvocationResult.noResult();
        }

        private boolean isLambdaGenericProblem(ClassCastException ex) {
            return ex.getMessage() == null || startsWithArgumentClassName(ex.getMessage());
        }

        private boolean startsWithArgumentClassName(String message) {
            Predicate<Object> startsWith = argument -> {
                return startsWithArgumentClassName(message, argument);
            };
            return startsWith.test(this.argument) || Stream.of(this.additionalArguments).anyMatch(startsWith);
        }

        private boolean startsWithArgumentClassName(String message, Object argument) {
            if (argument == null) {
                return false;
            }
            Class<?> argumentType = argument.getClass();
            if (message.startsWith(argumentType.getName()) || message.startsWith(argumentType.toString())) {
                return true;
            }
            int moduleSeparatorIndex = message.indexOf(47);
            if (moduleSeparatorIndex != -1 && message.startsWith(argumentType.getName(), moduleSeparatorIndex + 1)) {
                return true;
            }
            if (LambdaSafe.CLASS_GET_MODULE != null) {
                Object module = ReflectionUtils.invokeMethod(LambdaSafe.CLASS_GET_MODULE, argumentType);
                Object moduleName = ReflectionUtils.invokeMethod(LambdaSafe.MODULE_GET_NAME, module);
                return message.startsWith(moduleName + "/" + argumentType.getName());
            }
            return false;
        }

        private void logNonMatchingType(C callback, ClassCastException ex) {
            if (this.logger.isDebugEnabled()) {
                Class<?> expectedType = ResolvableType.forClass(this.callbackType).resolveGeneric(new int[0]);
                String expectedTypeName = expectedType != null ? ClassUtils.getShortName(expectedType) + " type" : "type";
                String message = "Non-matching " + expectedTypeName + " for callback " + ClassUtils.getShortName((Class<?>) this.callbackType) + ": " + callback;
                this.logger.debug(message, ex);
            }
        }

        private SELF self() {
            return this;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/util/LambdaSafe$Callback.class */
    public static final class Callback<C, A> extends LambdaSafeCallback<C, A, Callback<C, A>> {
        private final C callbackInstance;

        @Override // org.springframework.boot.util.LambdaSafe.LambdaSafeCallback
        public /* bridge */ /* synthetic */ LambdaSafeCallback withLogger(Log logger) {
            return super.withLogger(logger);
        }

        @Override // org.springframework.boot.util.LambdaSafe.LambdaSafeCallback
        public /* bridge */ /* synthetic */ LambdaSafeCallback withLogger(Class loggerSource) {
            return super.withLogger((Class<?>) loggerSource);
        }

        private Callback(Class<C> callbackType, C callbackInstance, A argument, Object[] additionalArguments) {
            super(callbackType, argument, additionalArguments);
            this.callbackInstance = callbackInstance;
        }

        public void invoke(Consumer<C> invoker) {
            invoke(this.callbackInstance, () -> {
                invoker.accept(this.callbackInstance);
                return null;
            });
        }

        public <R> InvocationResult<R> invokeAnd(Function<C, R> invoker) {
            return invoke(this.callbackInstance, () -> {
                return invoker.apply(this.callbackInstance);
            });
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/util/LambdaSafe$Callbacks.class */
    public static final class Callbacks<C, A> extends LambdaSafeCallback<C, A, Callbacks<C, A>> {
        private final Collection<? extends C> callbackInstances;

        @Override // org.springframework.boot.util.LambdaSafe.LambdaSafeCallback
        public /* bridge */ /* synthetic */ LambdaSafeCallback withLogger(Log logger) {
            return super.withLogger(logger);
        }

        @Override // org.springframework.boot.util.LambdaSafe.LambdaSafeCallback
        public /* bridge */ /* synthetic */ LambdaSafeCallback withLogger(Class loggerSource) {
            return super.withLogger((Class<?>) loggerSource);
        }

        private Callbacks(Class<C> callbackType, Collection<? extends C> callbackInstances, A argument, Object[] additionalArguments) {
            super(callbackType, argument, additionalArguments);
            this.callbackInstances = callbackInstances;
        }

        public void invoke(Consumer<C> invoker) {
            this.callbackInstances.forEach(obj -> {
                invoke(obj, () -> {
                    invoker.accept(obj);
                    return null;
                });
            });
        }

        public <R> Stream<R> invokeAnd(Function<C, R> invoker) {
            return this.callbackInstances.stream().map(obj -> {
                return invoke(obj, () -> {
                    return invoker.apply(obj);
                });
            }).filter((v0) -> {
                return v0.hasResult();
            }).map((v0) -> {
                return v0.get();
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @FunctionalInterface
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/util/LambdaSafe$Filter.class */
    public interface Filter<C, A> {
        boolean match(Class<C> callbackType, C callbackInstance, A argument, Object[] additionalArguments);

        static <C, A> Filter<C, A> allowAll() {
            return (callbackType, callbackInstance, argument, additionalArguments) -> {
                return true;
            };
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/util/LambdaSafe$GenericTypeFilter.class */
    private static final class GenericTypeFilter<C, A> implements Filter<C, A> {
        private GenericTypeFilter() {
        }

        @Override // org.springframework.boot.util.LambdaSafe.Filter
        public boolean match(Class<C> callbackType, C callbackInstance, A argument, Object[] additionalArguments) {
            ResolvableType type = ResolvableType.forClass(callbackType, callbackInstance.getClass());
            if (type.getGenerics().length == 1 && type.resolveGeneric(new int[0]) != null) {
                return type.resolveGeneric(new int[0]).isInstance(argument);
            }
            return true;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/util/LambdaSafe$InvocationResult.class */
    public static final class InvocationResult<R> {
        private static final InvocationResult<?> NONE = new InvocationResult<>(null);
        private final R value;

        private InvocationResult(R value) {
            this.value = value;
        }

        public boolean hasResult() {
            return this != NONE;
        }

        public R get() {
            return this.value;
        }

        public R get(R fallback) {
            return this != NONE ? this.value : fallback;
        }

        public static <R> InvocationResult<R> of(R value) {
            return new InvocationResult<>(value);
        }

        public static <R> InvocationResult<R> noResult() {
            return (InvocationResult<R>) NONE;
        }
    }
}

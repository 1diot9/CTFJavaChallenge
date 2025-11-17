package io.micrometer.observation;

import io.micrometer.common.KeyValue;
import io.micrometer.common.KeyValues;
import io.micrometer.common.lang.NonNull;
import io.micrometer.common.lang.Nullable;
import io.micrometer.common.util.internal.logging.InternalLoggerFactory;
import io.micrometer.observation.NoopObservation;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.springframework.beans.PropertyAccessor;

/* loaded from: server.jar:BOOT-INF/lib/micrometer-observation-1.12.2.jar:io/micrometer/observation/Observation.class */
public interface Observation extends ObservationView {
    public static final Observation NOOP = new NoopObservation();

    @FunctionalInterface
    /* loaded from: server.jar:BOOT-INF/lib/micrometer-observation-1.12.2.jar:io/micrometer/observation/Observation$CheckedCallable.class */
    public interface CheckedCallable<T, E extends Throwable> {
        T call() throws Throwable;
    }

    @FunctionalInterface
    /* loaded from: server.jar:BOOT-INF/lib/micrometer-observation-1.12.2.jar:io/micrometer/observation/Observation$CheckedFunction.class */
    public interface CheckedFunction<T, R, E extends Throwable> {
        @Nullable
        R apply(T t) throws Throwable;
    }

    @FunctionalInterface
    /* loaded from: server.jar:BOOT-INF/lib/micrometer-observation-1.12.2.jar:io/micrometer/observation/Observation$CheckedRunnable.class */
    public interface CheckedRunnable<E extends Throwable> {
        void run() throws Throwable;
    }

    Observation contextualName(@Nullable String str);

    Observation parentObservation(@Nullable Observation observation);

    Observation lowCardinalityKeyValue(KeyValue keyValue);

    Observation highCardinalityKeyValue(KeyValue keyValue);

    Observation observationConvention(ObservationConvention<?> observationConvention);

    Observation error(Throwable th);

    Observation event(Event event);

    Observation start();

    Context getContext();

    void stop();

    Scope openScope();

    static Observation start(String name, @Nullable ObservationRegistry registry) {
        return start(name, Context::new, registry);
    }

    static <T extends Context> Observation start(String name, Supplier<T> contextSupplier, @Nullable ObservationRegistry registry) {
        return createNotStarted(name, contextSupplier, registry).start();
    }

    static Observation createNotStarted(String name, @Nullable ObservationRegistry registry) {
        return createNotStarted(name, Context::new, registry);
    }

    static <T extends Context> Observation createNotStarted(String name, Supplier<T> contextSupplier, @Nullable ObservationRegistry registry) {
        if (registry == null || registry.isNoop()) {
            return NOOP;
        }
        Context context = contextSupplier.get();
        context.setParentFromCurrentObservation(registry);
        if (!registry.observationConfig().isObservationEnabled(name, context)) {
            return NOOP;
        }
        return new SimpleObservation(name, registry, context);
    }

    static <T extends Context> Observation createNotStarted(@Nullable ObservationConvention<T> customConvention, ObservationConvention<T> defaultConvention, Supplier<T> contextSupplier, @Nullable ObservationRegistry registry) {
        ObservationConvention<T> convention;
        if (registry == null || registry.isNoop()) {
            return NOOP;
        }
        T context = contextSupplier.get();
        context.setParentFromCurrentObservation(registry);
        if (customConvention != null) {
            convention = customConvention;
        } else {
            convention = registry.observationConfig().getObservationConvention(context, defaultConvention);
        }
        if (!registry.observationConfig().isObservationEnabled(convention.getName(), context)) {
            return NOOP;
        }
        return new SimpleObservation((ObservationConvention<? extends Context>) convention, registry, (Context) context);
    }

    static Observation start(ObservationConvention<Context> observationConvention, ObservationRegistry registry) {
        return start(observationConvention, Context::new, registry);
    }

    static <T extends Context> Observation start(ObservationConvention<T> observationConvention, Supplier<T> contextSupplier, ObservationRegistry registry) {
        return createNotStarted(observationConvention, contextSupplier, registry).start();
    }

    static <T extends Context> Observation start(@Nullable ObservationConvention<T> customConvention, ObservationConvention<T> defaultConvention, Supplier<T> contextSupplier, ObservationRegistry registry) {
        return createNotStarted(customConvention, defaultConvention, contextSupplier, registry).start();
    }

    static Observation createNotStarted(ObservationConvention<Context> observationConvention, ObservationRegistry registry) {
        return createNotStarted(observationConvention, Context::new, registry);
    }

    static <T extends Context> Observation createNotStarted(ObservationConvention<T> observationConvention, Supplier<T> contextSupplier, ObservationRegistry registry) {
        if (registry == null || registry.isNoop() || observationConvention == NoopObservationConvention.INSTANCE) {
            return NOOP;
        }
        T context = contextSupplier.get();
        context.setParentFromCurrentObservation(registry);
        if (!registry.observationConfig().isObservationEnabled(observationConvention.getName(), context)) {
            return NOOP;
        }
        return new SimpleObservation((ObservationConvention<? extends Context>) observationConvention, registry, (Context) context);
    }

    default Observation lowCardinalityKeyValue(String key, String value) {
        return lowCardinalityKeyValue(KeyValue.of(key, value));
    }

    default Observation lowCardinalityKeyValues(KeyValues keyValues) {
        Iterator<KeyValue> it = keyValues.iterator();
        while (it.hasNext()) {
            KeyValue keyValue = it.next();
            lowCardinalityKeyValue(keyValue);
        }
        return this;
    }

    default Observation highCardinalityKeyValue(String key, String value) {
        return highCardinalityKeyValue(KeyValue.of(key, value));
    }

    default Observation highCardinalityKeyValues(KeyValues keyValues) {
        Iterator<KeyValue> it = keyValues.iterator();
        while (it.hasNext()) {
            KeyValue keyValue = it.next();
            highCardinalityKeyValue(keyValue);
        }
        return this;
    }

    default boolean isNoop() {
        return this == NOOP;
    }

    @Override // io.micrometer.observation.ObservationView
    default ContextView getContextView() {
        return getContext();
    }

    default void observe(Runnable runnable) {
        start();
        try {
            try {
                Scope scope = openScope();
                try {
                    runnable.run();
                    if (scope != null) {
                        scope.close();
                    }
                } catch (Throwable th) {
                    if (scope != null) {
                        try {
                            scope.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            } catch (Throwable error) {
                error(error);
                throw error;
            }
        } finally {
            stop();
        }
    }

    default Runnable wrap(Runnable runnable) {
        return () -> {
            observe(runnable);
        };
    }

    default <E extends Throwable> void observeChecked(CheckedRunnable<E> checkedRunnable) throws Throwable {
        start();
        try {
            try {
                Scope scope = openScope();
                try {
                    checkedRunnable.run();
                    if (scope != null) {
                        scope.close();
                    }
                } catch (Throwable th) {
                    if (scope != null) {
                        try {
                            scope.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            } catch (Throwable error) {
                error(error);
                throw error;
            }
        } finally {
            stop();
        }
    }

    default <E extends Throwable> CheckedRunnable<E> wrapChecked(CheckedRunnable<E> checkedRunnable) throws Throwable {
        return () -> {
            observeChecked(checkedRunnable);
        };
    }

    @Nullable
    default <T> T observe(Supplier<T> supplier) {
        start();
        try {
            try {
                Scope scope = openScope();
                try {
                    T t = supplier.get();
                    if (scope != null) {
                        scope.close();
                    }
                    return t;
                } catch (Throwable th) {
                    if (scope != null) {
                        try {
                            scope.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            } catch (Throwable error) {
                error(error);
                throw error;
            }
        } finally {
            stop();
        }
    }

    default <T> Supplier<T> wrap(Supplier<T> supplier) {
        return () -> {
            return observe(supplier);
        };
    }

    @Nullable
    default <T, E extends Throwable> T observeChecked(CheckedCallable<T, E> checkedCallable) throws Throwable {
        start();
        try {
            try {
                Scope scope = openScope();
                try {
                    T call = checkedCallable.call();
                    if (scope != null) {
                        scope.close();
                    }
                    return call;
                } catch (Throwable th) {
                    if (scope != null) {
                        try {
                            scope.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            } catch (Throwable error) {
                error(error);
                throw error;
            }
        } finally {
            stop();
        }
    }

    default <T, E extends Throwable> CheckedCallable<T, E> wrapChecked(CheckedCallable<T, E> checkedCallable) throws Throwable {
        return () -> {
            return observeChecked(checkedCallable);
        };
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Nullable
    @Deprecated
    default <C extends Context, T> T observeWithContext(Function<C, T> function) {
        InternalLoggerFactory.getInstance((Class<?>) Observation.class).warn("This method is deprecated. Please migrate to observation.observe(...)");
        start();
        try {
            try {
                Scope openScope = openScope();
                try {
                    T t = (T) function.apply(getContext());
                    if (openScope != null) {
                        openScope.close();
                    }
                    return t;
                } catch (Throwable th) {
                    if (openScope != null) {
                        try {
                            openScope.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                error(th3);
                throw th3;
            }
        } finally {
            stop();
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Nullable
    @Deprecated
    default <C extends Context, T, E extends Throwable> T observeCheckedWithContext(CheckedFunction<C, T, E> checkedFunction) throws Throwable {
        InternalLoggerFactory.getInstance((Class<?>) Observation.class).warn("This method is deprecated. Please migrate to observation.observeChecked(...)");
        start();
        try {
            try {
                Scope openScope = openScope();
                try {
                    T t = (T) checkedFunction.apply(getContext());
                    if (openScope != null) {
                        openScope.close();
                    }
                    return t;
                } catch (Throwable th) {
                    if (openScope != null) {
                        try {
                            openScope.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                error(th3);
                throw th3;
            }
        } finally {
            stop();
        }
    }

    default void scoped(Runnable runnable) {
        try {
            Scope scope = openScope();
            try {
                runnable.run();
                if (scope != null) {
                    scope.close();
                }
            } finally {
            }
        } catch (Exception exception) {
            error(exception);
            throw exception;
        }
    }

    default <E extends Throwable> void scopedChecked(CheckedRunnable<E> checkedRunnable) throws Throwable {
        try {
            Scope scope = openScope();
            try {
                checkedRunnable.run();
                if (scope != null) {
                    scope.close();
                }
            } finally {
            }
        } catch (Throwable throwable) {
            error(throwable);
            throw throwable;
        }
    }

    default <T> T scoped(Supplier<T> supplier) {
        try {
            Scope scope = openScope();
            try {
                T t = supplier.get();
                if (scope != null) {
                    scope.close();
                }
                return t;
            } finally {
            }
        } catch (Exception exception) {
            error(exception);
            throw exception;
        }
    }

    default <T, E extends Throwable> T scopedChecked(CheckedCallable<T, E> checkedCallable) throws Throwable {
        try {
            Scope scope = openScope();
            try {
                T call = checkedCallable.call();
                if (scope != null) {
                    scope.close();
                }
                return call;
            } finally {
            }
        } catch (Throwable error) {
            error(error);
            throw error;
        }
    }

    static void tryScoped(@Nullable Observation parent, Runnable action) {
        if (parent != null) {
            parent.scoped(action);
        } else {
            action.run();
        }
    }

    static <E extends Throwable> void tryScopedChecked(@Nullable Observation parent, CheckedRunnable<E> checkedRunnable) throws Throwable {
        if (parent != null) {
            parent.scopedChecked(checkedRunnable);
        } else {
            checkedRunnable.run();
        }
    }

    static <T> T tryScoped(@Nullable Observation observation, Supplier<T> supplier) {
        if (observation != null) {
            return (T) observation.scoped(supplier);
        }
        return supplier.get();
    }

    static <T, E extends Throwable> T tryScopedChecked(@Nullable Observation observation, CheckedCallable<T, E> checkedCallable) throws Throwable {
        if (observation != null) {
            return (T) observation.scopedChecked(checkedCallable);
        }
        return checkedCallable.call();
    }

    /* loaded from: server.jar:BOOT-INF/lib/micrometer-observation-1.12.2.jar:io/micrometer/observation/Observation$Scope.class */
    public interface Scope extends AutoCloseable {
        public static final Scope NOOP = NoopObservation.NoopScope.INSTANCE;

        Observation getCurrentObservation();

        @Override // java.lang.AutoCloseable
        void close();

        void reset();

        void makeCurrent();

        @Nullable
        default Scope getPreviousObservationScope() {
            return null;
        }

        default boolean isNoop() {
            return this == NOOP;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/micrometer-observation-1.12.2.jar:io/micrometer/observation/Observation$Context.class */
    public static class Context implements ContextView {
        private String name;

        @Nullable
        private String contextualName;

        @Nullable
        private Throwable error;

        @Nullable
        private ObservationView parentObservation;
        private final Map<Object, Object> map = new ConcurrentHashMap();
        private final Map<String, KeyValue> lowCardinalityKeyValues = new LinkedHashMap();
        private final Map<String, KeyValue> highCardinalityKeyValues = new LinkedHashMap();

        @Override // io.micrometer.observation.Observation.ContextView
        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override // io.micrometer.observation.Observation.ContextView
        public String getContextualName() {
            return this.contextualName;
        }

        public void setContextualName(@Nullable String contextualName) {
            this.contextualName = contextualName;
        }

        @Override // io.micrometer.observation.Observation.ContextView
        @Nullable
        public ObservationView getParentObservation() {
            return this.parentObservation;
        }

        public void setParentObservation(@Nullable ObservationView parentObservation) {
            this.parentObservation = parentObservation;
        }

        void setParentFromCurrentObservation(ObservationRegistry registry) {
            Observation currentObservation;
            if (this.parentObservation == null && (currentObservation = registry.getCurrentObservation()) != null) {
                setParentObservation(currentObservation);
            }
        }

        @Override // io.micrometer.observation.Observation.ContextView
        @Nullable
        public Throwable getError() {
            return this.error;
        }

        public void setError(Throwable error) {
            this.error = error;
        }

        public <T> Context put(Object key, T object) {
            this.map.put(key, object);
            return this;
        }

        @Override // io.micrometer.observation.Observation.ContextView
        @Nullable
        public <T> T get(Object obj) {
            return (T) this.map.get(obj);
        }

        public Object remove(Object key) {
            return this.map.remove(key);
        }

        @Override // io.micrometer.observation.Observation.ContextView
        @NonNull
        public <T> T getRequired(Object obj) {
            T t = (T) this.map.get(obj);
            if (t == null) {
                throw new IllegalArgumentException("Context does not have an entry for key [" + obj + "]");
            }
            return t;
        }

        @Override // io.micrometer.observation.Observation.ContextView
        public boolean containsKey(Object key) {
            return this.map.containsKey(key);
        }

        @Override // io.micrometer.observation.Observation.ContextView
        public <T> T getOrDefault(Object obj, T t) {
            return (T) this.map.getOrDefault(obj, t);
        }

        public <T> T computeIfAbsent(Object obj, Function<Object, ? extends T> function) {
            return (T) this.map.computeIfAbsent(obj, function);
        }

        public void clear() {
            this.map.clear();
        }

        public Context addLowCardinalityKeyValue(KeyValue keyValue) {
            this.lowCardinalityKeyValues.put(keyValue.getKey(), keyValue);
            return this;
        }

        public Context addHighCardinalityKeyValue(KeyValue keyValue) {
            this.highCardinalityKeyValues.put(keyValue.getKey(), keyValue);
            return this;
        }

        public Context removeLowCardinalityKeyValue(String keyName) {
            this.lowCardinalityKeyValues.remove(keyName);
            return this;
        }

        public Context removeHighCardinalityKeyValue(String keyName) {
            this.highCardinalityKeyValues.remove(keyName);
            return this;
        }

        public Context addLowCardinalityKeyValues(KeyValues keyValues) {
            Iterator<KeyValue> it = keyValues.iterator();
            while (it.hasNext()) {
                KeyValue keyValue = it.next();
                addLowCardinalityKeyValue(keyValue);
            }
            return this;
        }

        public Context addHighCardinalityKeyValues(KeyValues keyValues) {
            Iterator<KeyValue> it = keyValues.iterator();
            while (it.hasNext()) {
                KeyValue keyValue = it.next();
                addHighCardinalityKeyValue(keyValue);
            }
            return this;
        }

        public Context removeLowCardinalityKeyValues(String... keyNames) {
            for (String keyName : keyNames) {
                removeLowCardinalityKeyValue(keyName);
            }
            return this;
        }

        public Context removeHighCardinalityKeyValues(String... keyNames) {
            for (String keyName : keyNames) {
                removeHighCardinalityKeyValue(keyName);
            }
            return this;
        }

        @Override // io.micrometer.observation.Observation.ContextView
        @NonNull
        public KeyValues getLowCardinalityKeyValues() {
            return KeyValues.of(this.lowCardinalityKeyValues.values());
        }

        @Override // io.micrometer.observation.Observation.ContextView
        @NonNull
        public KeyValues getHighCardinalityKeyValues() {
            return KeyValues.of(this.highCardinalityKeyValues.values());
        }

        @Override // io.micrometer.observation.Observation.ContextView
        public KeyValue getLowCardinalityKeyValue(String key) {
            return this.lowCardinalityKeyValues.get(key);
        }

        @Override // io.micrometer.observation.Observation.ContextView
        public KeyValue getHighCardinalityKeyValue(String key) {
            return this.highCardinalityKeyValues.get(key);
        }

        @Override // io.micrometer.observation.Observation.ContextView
        @NonNull
        public KeyValues getAllKeyValues() {
            return getLowCardinalityKeyValues().and(getHighCardinalityKeyValues());
        }

        public String toString() {
            return "name='" + this.name + "', contextualName='" + this.contextualName + "', error='" + this.error + "', lowCardinalityKeyValues=" + toString(getLowCardinalityKeyValues()) + ", highCardinalityKeyValues=" + toString(getHighCardinalityKeyValues()) + ", map=" + toString(this.map) + ", parentObservation=" + this.parentObservation;
        }

        private String toString(KeyValues keyValues) {
            return (String) keyValues.stream().map(keyValue -> {
                return String.format("%s='%s'", keyValue.getKey(), keyValue.getValue());
            }).collect(Collectors.joining(", ", PropertyAccessor.PROPERTY_KEY_PREFIX, "]"));
        }

        private String toString(Map<Object, Object> map) {
            return (String) map.entrySet().stream().map(entry -> {
                return String.format("%s='%s'", entry.getKey(), entry.getValue());
            }).collect(Collectors.joining(", ", PropertyAccessor.PROPERTY_KEY_PREFIX, "]"));
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/micrometer-observation-1.12.2.jar:io/micrometer/observation/Observation$Event.class */
    public interface Event {
        String getName();

        static Event of(String name, String contextualName) {
            return new SimpleEvent(name, contextualName);
        }

        static Event of(String name, String contextualName, long wallTime) {
            return new SimpleEvent(name, contextualName, wallTime);
        }

        static Event of(String name) {
            return of(name, name);
        }

        default long getWallTime() {
            return 0L;
        }

        default String getContextualName() {
            return getName();
        }

        default Event format(Object... dynamicEntriesForContextualName) {
            return of(getName(), String.format(getContextualName(), dynamicEntriesForContextualName));
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/micrometer-observation-1.12.2.jar:io/micrometer/observation/Observation$ContextView.class */
    public interface ContextView {
        String getName();

        @Nullable
        String getContextualName();

        @Nullable
        ObservationView getParentObservation();

        @Nullable
        Throwable getError();

        @Nullable
        <T> T get(Object obj);

        @NonNull
        <T> T getRequired(Object obj);

        boolean containsKey(Object obj);

        <T> T getOrDefault(Object obj, T t);

        KeyValues getLowCardinalityKeyValues();

        @NonNull
        KeyValues getHighCardinalityKeyValues();

        @Nullable
        KeyValue getLowCardinalityKeyValue(String str);

        @Nullable
        KeyValue getHighCardinalityKeyValue(String str);

        @NonNull
        KeyValues getAllKeyValues();

        default <T> T getOrDefault(Object obj, Supplier<T> supplier) {
            T t = (T) get(obj);
            return t != null ? t : supplier.get();
        }
    }
}

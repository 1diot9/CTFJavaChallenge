package io.micrometer.observation;

import io.micrometer.common.KeyValue;
import io.micrometer.common.lang.Nullable;
import io.micrometer.common.util.StringUtils;
import io.micrometer.common.util.internal.logging.InternalLogger;
import io.micrometer.common.util.internal.logging.InternalLoggerFactory;
import io.micrometer.observation.Observation;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/micrometer-observation-1.12.2.jar:io/micrometer/observation/SimpleObservation.class */
public class SimpleObservation implements Observation {
    final ObservationRegistry registry;
    private final Observation.Context context;

    @Nullable
    private ObservationConvention convention;
    private final Deque<ObservationHandler> handlers;
    private final Collection<ObservationFilter> filters;
    final Map<Thread, Observation.Scope> lastScope = new ConcurrentHashMap();

    /* JADX INFO: Access modifiers changed from: package-private */
    public SimpleObservation(@Nullable String name, ObservationRegistry registry, Observation.Context context) {
        this.registry = registry;
        this.context = context;
        this.context.setName(name);
        this.convention = getConventionFromConfig(registry, context);
        this.handlers = getHandlersFromConfig(registry, context);
        this.filters = registry.observationConfig().getObservationFilters();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SimpleObservation(ObservationConvention<? extends Observation.Context> convention, ObservationRegistry registry, Observation.Context context) {
        this.registry = registry;
        this.context = context;
        this.handlers = getHandlersFromConfig(registry, context);
        this.filters = registry.observationConfig().getObservationFilters();
        if (convention.supportsContext(context)) {
            this.convention = convention;
            return;
        }
        throw new IllegalStateException("Convention [" + convention + "] doesn't support context [" + context + "]");
    }

    @Nullable
    private static ObservationConvention getConventionFromConfig(ObservationRegistry registry, Observation.Context context) {
        for (ObservationConvention<?> convention : registry.observationConfig().getObservationConventions()) {
            if (convention.supportsContext(context)) {
                return convention;
            }
        }
        return null;
    }

    private static Deque<ObservationHandler> getHandlersFromConfig(ObservationRegistry registry, Observation.Context context) {
        Collection<ObservationHandler<?>> handlers = registry.observationConfig().getObservationHandlers();
        Deque<ObservationHandler> deque = new ArrayDeque<>(handlers.size());
        for (ObservationHandler handler : handlers) {
            if (handler.supportsContext(context)) {
                deque.add(handler);
            }
        }
        return deque;
    }

    @Override // io.micrometer.observation.Observation
    public Observation contextualName(@Nullable String contextualName) {
        this.context.setContextualName(contextualName);
        return this;
    }

    @Override // io.micrometer.observation.Observation
    public Observation parentObservation(@Nullable Observation parentObservation) {
        this.context.setParentObservation(parentObservation);
        return this;
    }

    @Override // io.micrometer.observation.Observation
    public Observation lowCardinalityKeyValue(KeyValue keyValue) {
        this.context.addLowCardinalityKeyValue(keyValue);
        return this;
    }

    @Override // io.micrometer.observation.Observation
    public Observation highCardinalityKeyValue(KeyValue keyValue) {
        this.context.addHighCardinalityKeyValue(keyValue);
        return this;
    }

    @Override // io.micrometer.observation.Observation
    public Observation observationConvention(ObservationConvention<?> convention) {
        if (convention.supportsContext(this.context)) {
            this.convention = convention;
        }
        return this;
    }

    @Override // io.micrometer.observation.Observation
    public Observation error(Throwable error) {
        this.context.setError(error);
        notifyOnError();
        return this;
    }

    @Override // io.micrometer.observation.Observation
    public Observation event(Observation.Event event) {
        notifyOnEvent(event);
        return this;
    }

    @Override // io.micrometer.observation.Observation
    public Observation start() {
        if (this.convention != null) {
            this.context.addLowCardinalityKeyValues(this.convention.getLowCardinalityKeyValues(this.context));
            this.context.addHighCardinalityKeyValues(this.convention.getHighCardinalityKeyValues(this.context));
            String newName = this.convention.getName();
            if (StringUtils.isNotBlank(newName)) {
                this.context.setName(newName);
            }
        }
        notifyOnObservationStarted();
        return this;
    }

    @Override // io.micrometer.observation.Observation
    public Observation.Context getContext() {
        return this.context;
    }

    @Override // io.micrometer.observation.Observation
    public void stop() {
        if (this.convention != null) {
            this.context.addLowCardinalityKeyValues(this.convention.getLowCardinalityKeyValues(this.context));
            this.context.addHighCardinalityKeyValues(this.convention.getHighCardinalityKeyValues(this.context));
            String newContextualName = this.convention.getContextualName(this.context);
            if (StringUtils.isNotBlank(newContextualName)) {
                this.context.setContextualName(newContextualName);
            }
        }
        Observation.Context modifiedContext = this.context;
        for (ObservationFilter filter : this.filters) {
            modifiedContext = filter.map(modifiedContext);
        }
        notifyOnObservationStopped(modifiedContext);
    }

    @Override // io.micrometer.observation.Observation
    public Observation.Scope openScope() {
        Observation.Scope scope = new SimpleScope(this.registry, this);
        notifyOnScopeOpened();
        this.lastScope.put(Thread.currentThread(), scope);
        return scope;
    }

    @Override // io.micrometer.observation.ObservationView
    @Nullable
    public Observation.Scope getEnclosingScope() {
        return this.lastScope.get(Thread.currentThread());
    }

    public String toString() {
        return "{name=" + this.context.getName() + "(" + this.context.getContextualName() + "), error=" + this.context.getError() + ", context=" + this.context + '}';
    }

    void notifyOnObservationStarted() {
        for (ObservationHandler handler : this.handlers) {
            handler.onStart(this.context);
        }
    }

    void notifyOnError() {
        for (ObservationHandler handler : this.handlers) {
            handler.onError(this.context);
        }
    }

    void notifyOnEvent(Observation.Event event) {
        for (ObservationHandler handler : this.handlers) {
            handler.onEvent(event, this.context);
        }
    }

    void notifyOnScopeOpened() {
        for (ObservationHandler handler : this.handlers) {
            handler.onScopeOpened(this.context);
        }
    }

    void notifyOnScopeClosed() {
        Iterator<ObservationHandler> iterator = this.handlers.descendingIterator();
        while (iterator.hasNext()) {
            ObservationHandler handler = iterator.next();
            handler.onScopeClosed(this.context);
        }
    }

    void notifyOnScopeMakeCurrent() {
        for (ObservationHandler handler : this.handlers) {
            handler.onScopeOpened(this.context);
        }
    }

    void notifyOnScopeReset() {
        for (ObservationHandler handler : this.handlers) {
            handler.onScopeReset(this.context);
        }
    }

    void notifyOnObservationStopped(Observation.Context context) {
        this.handlers.descendingIterator().forEachRemaining(handler -> {
            handler.onStop(context);
        });
    }

    @Override // io.micrometer.observation.ObservationView
    public ObservationRegistry getObservationRegistry() {
        return this.registry;
    }

    /* loaded from: server.jar:BOOT-INF/lib/micrometer-observation-1.12.2.jar:io/micrometer/observation/SimpleObservation$SimpleScope.class */
    static class SimpleScope implements Observation.Scope {
        private static final InternalLogger log = InternalLoggerFactory.getInstance((Class<?>) SimpleScope.class);
        final ObservationRegistry registry;
        private final Observation currentObservation;

        @Nullable
        final Observation.Scope previousObservationScope;

        /* JADX INFO: Access modifiers changed from: package-private */
        public SimpleScope(ObservationRegistry registry, Observation current) {
            this.registry = registry;
            this.currentObservation = current;
            this.previousObservationScope = registry.getCurrentObservationScope();
            this.registry.setCurrentObservationScope(this);
        }

        @Override // io.micrometer.observation.Observation.Scope
        public Observation getCurrentObservation() {
            return this.currentObservation;
        }

        @Override // io.micrometer.observation.Observation.Scope, java.lang.AutoCloseable
        public void close() {
            if (this.currentObservation instanceof SimpleObservation) {
                SimpleObservation observation = (SimpleObservation) this.currentObservation;
                SimpleScope lastScopeForThisObservation = getLastScope(this);
                if (lastScopeForThisObservation != null) {
                    observation.lastScope.put(Thread.currentThread(), lastScopeForThisObservation);
                } else {
                    observation.lastScope.remove(Thread.currentThread());
                }
                observation.notifyOnScopeClosed();
            } else if (this.currentObservation != null && !this.currentObservation.isNoop()) {
                log.debug("Custom observation type was used in combination with SimpleScope - that's unexpected");
            } else {
                log.trace("NoOp observation used with SimpleScope");
            }
            this.registry.setCurrentObservationScope(this.previousObservationScope);
        }

        @Nullable
        private SimpleScope getLastScope(SimpleScope simpleScope) {
            SimpleScope scope = simpleScope;
            do {
                scope = (SimpleScope) scope.previousObservationScope;
                if (scope == null) {
                    break;
                }
            } while (!this.currentObservation.equals(scope.currentObservation));
            return scope;
        }

        @Override // io.micrometer.observation.Observation.Scope
        public void reset() {
            SimpleScope scope = this;
            if (scope.currentObservation instanceof SimpleObservation) {
                SimpleObservation simpleObservation = (SimpleObservation) scope.currentObservation;
                do {
                    simpleObservation.notifyOnScopeReset();
                    scope = (SimpleScope) scope.previousObservationScope;
                } while (scope != null);
            }
            this.registry.setCurrentObservationScope(null);
        }

        @Override // io.micrometer.observation.Observation.Scope
        public void makeCurrent() {
            SimpleScope scope = this;
            do {
                if (scope.currentObservation instanceof SimpleObservation) {
                    ((SimpleObservation) scope.currentObservation).notifyOnScopeReset();
                }
                scope = (SimpleScope) scope.previousObservationScope;
            } while (scope != null);
            Deque<SimpleScope> scopes = new ArrayDeque<>();
            SimpleScope scope2 = this;
            do {
                scopes.addFirst(scope2);
                scope2 = (SimpleScope) scope2.previousObservationScope;
            } while (scope2 != null);
            for (SimpleScope simpleScope : scopes) {
                if (simpleScope.currentObservation instanceof SimpleObservation) {
                    ((SimpleObservation) simpleScope.currentObservation).notifyOnScopeMakeCurrent();
                }
            }
            this.registry.setCurrentObservationScope(this);
        }

        @Override // io.micrometer.observation.Observation.Scope
        @Nullable
        public Observation.Scope getPreviousObservationScope() {
            return this.previousObservationScope;
        }
    }
}

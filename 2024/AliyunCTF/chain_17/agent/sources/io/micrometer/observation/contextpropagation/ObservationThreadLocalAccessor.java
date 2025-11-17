package io.micrometer.observation.contextpropagation;

import io.micrometer.common.util.internal.logging.InternalLogger;
import io.micrometer.common.util.internal.logging.InternalLoggerFactory;
import io.micrometer.context.ContextRegistry;
import io.micrometer.context.ThreadLocalAccessor;
import io.micrometer.observation.NullObservation;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;

/* loaded from: agent.jar:BOOT-INF/lib/micrometer-observation-1.12.2.jar:io/micrometer/observation/contextpropagation/ObservationThreadLocalAccessor.class */
public class ObservationThreadLocalAccessor implements ThreadLocalAccessor<Observation> {
    private static final InternalLogger log;
    public static final String KEY = "micrometer.observation";
    private ObservationRegistry observationRegistry;
    private static ObservationThreadLocalAccessor instance;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !ObservationThreadLocalAccessor.class.desiredAssertionStatus();
        log = InternalLoggerFactory.getInstance((Class<?>) ObservationThreadLocalAccessor.class);
    }

    public ObservationThreadLocalAccessor() {
        this.observationRegistry = ObservationRegistry.create();
        instance = this;
    }

    public ObservationThreadLocalAccessor(ObservationRegistry observationRegistry) {
        this.observationRegistry = ObservationRegistry.create();
        this.observationRegistry = observationRegistry;
    }

    public void setObservationRegistry(ObservationRegistry observationRegistry) {
        this.observationRegistry = observationRegistry;
    }

    public ObservationRegistry getObservationRegistry() {
        return this.observationRegistry;
    }

    public static ObservationThreadLocalAccessor getInstance() {
        if (instance == null) {
            ContextRegistry.getInstance();
        }
        return instance;
    }

    public Object key() {
        return KEY;
    }

    /* renamed from: getValue, reason: merged with bridge method [inline-methods] */
    public Observation m818getValue() {
        return this.observationRegistry.getCurrentObservation();
    }

    public void setValue(Observation value) {
        Observation.Scope scope = value.openScope();
        if (log.isTraceEnabled()) {
            log.trace("Called setValue(...) for Observation <{}> and opened scope <{}>", value, scope);
        }
    }

    public void setValue() {
        Observation currentObservation = this.observationRegistry.getCurrentObservation();
        if (currentObservation == null) {
            return;
        }
        if (log.isTraceEnabled()) {
            log.trace("Calling setValue(), currentObservation <{}> but we will open a NullObservation", currentObservation);
        }
        ObservationRegistry registryAttachedToCurrentObservation = currentObservation.getObservationRegistry();
        Observation.Scope scope = new NullObservation(registryAttachedToCurrentObservation).start().openScope();
        if (log.isTraceEnabled()) {
            log.trace("Created the NullObservation scope <{}>", scope);
        }
    }

    private void closeCurrentScope() {
        Observation.Scope scope = this.observationRegistry.getCurrentObservationScope();
        if (log.isTraceEnabled()) {
            log.trace("Closing current scope <{}>", scope);
        }
        if (scope != null) {
            scope.close();
        }
        if (log.isTraceEnabled()) {
            log.trace("After closing scope, current one is <{}>", this.observationRegistry.getCurrentObservationScope());
        }
    }

    public void restore() {
        if (log.isTraceEnabled()) {
            log.trace("Calling restore()");
        }
        closeCurrentScope();
    }

    public void restore(Observation value) {
        Observation.Scope scope = this.observationRegistry.getCurrentObservationScope();
        if (log.isTraceEnabled()) {
            log.trace("Calling restore(...) with Observation <{}> and scope <{}>", value, scope);
        }
        if (scope == null) {
            log.warn("There is no current scope in thread local. This situation should not happen");
            assertFalse("There is no current scope in thread local. This situation should not happen");
        }
        Observation.Scope previousObservationScope = scope != null ? scope.getPreviousObservationScope() : null;
        if (previousObservationScope == null || value != previousObservationScope.getCurrentObservation()) {
            Observation previousObservation = previousObservationScope != null ? previousObservationScope.getCurrentObservation() : null;
            String msg = "Observation <" + value + "> to which we're restoring is not the same as the one set as this scope's parent observation <" + previousObservation + ">. Most likely a manually created Observation has a scope opened that was never closed. This may lead to thread polluting and memory leaks.";
            log.warn(msg);
            assertFalse(msg);
        }
        closeCurrentScope();
    }

    void assertFalse(String msg) {
        if (!$assertionsDisabled) {
            throw new AssertionError(msg);
        }
    }

    @Deprecated
    public void reset() {
        if (log.isTraceEnabled()) {
            log.trace("Calling reset()");
        }
        super.reset();
    }
}

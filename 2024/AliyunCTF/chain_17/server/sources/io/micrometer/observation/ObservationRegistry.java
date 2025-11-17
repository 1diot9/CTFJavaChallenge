package io.micrometer.observation;

import io.micrometer.common.lang.Nullable;
import io.micrometer.observation.Observation;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/* loaded from: server.jar:BOOT-INF/lib/micrometer-observation-1.12.2.jar:io/micrometer/observation/ObservationRegistry.class */
public interface ObservationRegistry {
    public static final ObservationRegistry NOOP = new NoopObservationRegistry();

    @Nullable
    Observation getCurrentObservation();

    @Nullable
    Observation.Scope getCurrentObservationScope();

    void setCurrentObservationScope(@Nullable Observation.Scope scope);

    ObservationConfig observationConfig();

    static ObservationRegistry create() {
        return new SimpleObservationRegistry();
    }

    default boolean isNoop() {
        return this == NOOP;
    }

    /* loaded from: server.jar:BOOT-INF/lib/micrometer-observation-1.12.2.jar:io/micrometer/observation/ObservationRegistry$ObservationConfig.class */
    public static class ObservationConfig {
        private final List<ObservationHandler<?>> observationHandlers = new CopyOnWriteArrayList();
        private final List<ObservationPredicate> observationPredicates = new CopyOnWriteArrayList();
        private final List<ObservationConvention<?>> observationConventions = new CopyOnWriteArrayList();
        private final List<ObservationFilter> observationFilters = new CopyOnWriteArrayList();

        public ObservationConfig observationHandler(ObservationHandler<?> handler) {
            this.observationHandlers.add(handler);
            return this;
        }

        public ObservationConfig observationPredicate(ObservationPredicate predicate) {
            this.observationPredicates.add(predicate);
            return this;
        }

        public ObservationConfig observationFilter(ObservationFilter observationFilter) {
            this.observationFilters.add(observationFilter);
            return this;
        }

        public ObservationConfig observationConvention(GlobalObservationConvention<?> observationConvention) {
            this.observationConventions.add(observationConvention);
            return this;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public <T extends Observation.Context> ObservationConvention<T> getObservationConvention(T context, ObservationConvention<T> defaultConvention) {
            Iterator<ObservationConvention<?>> it = this.observationConventions.iterator();
            while (it.hasNext()) {
                ObservationConvention<T> observationConvention = (ObservationConvention) it.next();
                if (observationConvention.supportsContext(context)) {
                    return observationConvention;
                }
            }
            return (ObservationConvention) Objects.requireNonNull(defaultConvention, "Default ObservationConvention must not be null");
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public boolean isObservationEnabled(String name, @Nullable Observation.Context context) {
            for (ObservationPredicate predicate : this.observationPredicates) {
                if (!predicate.test(name, context)) {
                    return false;
                }
            }
            return true;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Collection<ObservationHandler<?>> getObservationHandlers() {
            return this.observationHandlers;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Collection<ObservationFilter> getObservationFilters() {
            return this.observationFilters;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Collection<ObservationConvention<?>> getObservationConventions() {
            return this.observationConventions;
        }
    }
}

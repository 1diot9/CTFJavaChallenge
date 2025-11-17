package io.micrometer.observation.docs;

import io.micrometer.common.docs.KeyName;
import io.micrometer.common.lang.Nullable;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationConvention;
import io.micrometer.observation.ObservationRegistry;
import java.util.Objects;
import java.util.function.Supplier;

/* loaded from: server.jar:BOOT-INF/lib/micrometer-observation-1.12.2.jar:io/micrometer/observation/docs/ObservationDocumentation.class */
public interface ObservationDocumentation {
    public static final KeyName[] EMPTY = new KeyName[0];
    public static final Observation.Event[] EMPTY_EVENT_NAMES = new Observation.Event[0];

    @Nullable
    default String getName() {
        return null;
    }

    @Nullable
    default Class<? extends ObservationConvention<? extends Observation.Context>> getDefaultConvention() {
        return null;
    }

    @Nullable
    default String getContextualName() {
        return null;
    }

    default KeyName[] getLowCardinalityKeyNames() {
        return EMPTY;
    }

    default KeyName[] getHighCardinalityKeyNames() {
        return EMPTY;
    }

    default Observation.Event[] getEvents() {
        return EMPTY_EVENT_NAMES;
    }

    default String getPrefix() {
        return "";
    }

    default Observation observation(ObservationRegistry registry) {
        return observation(registry, Observation.Context::new);
    }

    default Observation observation(ObservationRegistry registry, Supplier<Observation.Context> contextSupplier) {
        Observation observation = Observation.createNotStarted(getName(), contextSupplier, registry);
        if (getContextualName() != null) {
            observation.contextualName(getContextualName());
        }
        return observation;
    }

    default <T extends Observation.Context> Observation observation(@Nullable ObservationConvention<T> customConvention, ObservationConvention<T> defaultConvention, Supplier<T> contextSupplier, ObservationRegistry registry) {
        if (getDefaultConvention() == null) {
            throw new IllegalStateException("You've decided to use convention based naming yet this observation [" + getClass() + "] has not defined any default convention");
        }
        if (!getDefaultConvention().isAssignableFrom(((ObservationConvention) Objects.requireNonNull(defaultConvention, "You have not provided a default convention in the Observation factory method")).getClass())) {
            throw new IllegalArgumentException("Observation [" + getClass() + "] defined default convention to be of type [" + getDefaultConvention() + "] but you have provided an incompatible one of type [" + defaultConvention.getClass() + "]");
        }
        Observation observation = Observation.createNotStarted(customConvention, defaultConvention, contextSupplier, registry);
        if (getName() != null) {
            observation.getContext().setName(getName());
        }
        if (getContextualName() != null) {
            observation.contextualName(getContextualName());
        }
        return observation;
    }

    default Observation start(ObservationRegistry registry) {
        return start(registry, Observation.Context::new);
    }

    default Observation start(ObservationRegistry registry, Supplier<Observation.Context> contextSupplier) {
        return observation(registry, contextSupplier).start();
    }

    default <T extends Observation.Context> Observation start(@Nullable ObservationConvention<T> customConvention, ObservationConvention<T> defaultConvention, Supplier<T> contextSupplier, ObservationRegistry registry) {
        return observation(customConvention, defaultConvention, contextSupplier, registry).start();
    }
}

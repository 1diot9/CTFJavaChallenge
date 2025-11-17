package io.micrometer.observation.aop;

import io.micrometer.common.KeyValues;
import io.micrometer.common.docs.KeyName;
import io.micrometer.common.lang.Nullable;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationConvention;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.annotation.Observed;
import io.micrometer.observation.aop.ObservedAspect;
import io.micrometer.observation.docs.ObservationDocumentation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/micrometer-observation-1.12.2.jar:io/micrometer/observation/aop/ObservedAspectObservationDocumentation.class */
public enum ObservedAspectObservationDocumentation implements ObservationDocumentation {
    DEFAULT;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/micrometer-observation-1.12.2.jar:io/micrometer/observation/aop/ObservedAspectObservationDocumentation$ObservedAspectLowCardinalityKeyName.class */
    public enum ObservedAspectLowCardinalityKeyName implements KeyName {
        CLASS_NAME { // from class: io.micrometer.observation.aop.ObservedAspectObservationDocumentation.ObservedAspectLowCardinalityKeyName.1
            @Override // io.micrometer.common.docs.KeyName
            public String asString() {
                return "class";
            }
        },
        METHOD_NAME { // from class: io.micrometer.observation.aop.ObservedAspectObservationDocumentation.ObservedAspectLowCardinalityKeyName.2
            @Override // io.micrometer.common.docs.KeyName
            public String asString() {
                return "method";
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Observation of(ProceedingJoinPoint pjp, Observed observed, ObservationRegistry registry, @Nullable ObservationConvention<ObservedAspect.ObservedAspectContext> observationConvention) {
        String name = observed.name().isEmpty() ? "method.observed" : observed.name();
        Signature signature = pjp.getStaticPart().getSignature();
        String contextualName = observed.contextualName().isEmpty() ? signature.getDeclaringType().getSimpleName() + "#" + signature.getName() : observed.contextualName();
        Observation observation = Observation.createNotStarted(name, () -> {
            return new ObservedAspect.ObservedAspectContext(pjp);
        }, registry).contextualName(contextualName).lowCardinalityKeyValue(ObservedAspectLowCardinalityKeyName.CLASS_NAME.asString(), signature.getDeclaringTypeName()).lowCardinalityKeyValue(ObservedAspectLowCardinalityKeyName.METHOD_NAME.asString(), signature.getName()).lowCardinalityKeyValues(KeyValues.of(observed.lowCardinalityKeyValues()));
        if (observationConvention != null) {
            observation.observationConvention(observationConvention);
        }
        return observation;
    }

    @Override // io.micrometer.observation.docs.ObservationDocumentation
    public String getName() {
        return "%s";
    }

    @Override // io.micrometer.observation.docs.ObservationDocumentation
    public String getContextualName() {
        return "%s";
    }

    @Override // io.micrometer.observation.docs.ObservationDocumentation
    public KeyName[] getLowCardinalityKeyNames() {
        return ObservedAspectLowCardinalityKeyName.values();
    }
}

package org.springframework.scheduling.support;

import io.micrometer.common.KeyValue;
import io.micrometer.common.KeyValues;
import org.springframework.scheduling.support.ScheduledTaskObservationDocumentation;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/support/DefaultScheduledTaskObservationConvention.class */
public class DefaultScheduledTaskObservationConvention implements ScheduledTaskObservationConvention {
    private static final String DEFAULT_NAME = "tasks.scheduled.execution";
    private static final KeyValue EXCEPTION_NONE = KeyValue.of(ScheduledTaskObservationDocumentation.LowCardinalityKeyNames.EXCEPTION, "none");
    private static final KeyValue OUTCOME_SUCCESS = KeyValue.of(ScheduledTaskObservationDocumentation.LowCardinalityKeyNames.OUTCOME, "SUCCESS");
    private static final KeyValue OUTCOME_ERROR = KeyValue.of(ScheduledTaskObservationDocumentation.LowCardinalityKeyNames.OUTCOME, "ERROR");
    private static final KeyValue OUTCOME_UNKNOWN = KeyValue.of(ScheduledTaskObservationDocumentation.LowCardinalityKeyNames.OUTCOME, "UNKNOWN");
    private static final KeyValue CODE_NAMESPACE_ANONYMOUS = KeyValue.of(ScheduledTaskObservationDocumentation.LowCardinalityKeyNames.CODE_NAMESPACE, "ANONYMOUS");

    @Override // io.micrometer.observation.ObservationConvention
    public String getName() {
        return DEFAULT_NAME;
    }

    @Override // io.micrometer.observation.ObservationConvention
    public String getContextualName(ScheduledTaskObservationContext context) {
        return "task " + StringUtils.uncapitalize(context.getTargetClass().getSimpleName()) + "." + context.getMethod().getName();
    }

    @Override // io.micrometer.observation.ObservationConvention
    public KeyValues getLowCardinalityKeyValues(ScheduledTaskObservationContext context) {
        return KeyValues.of(codeFunction(context), codeNamespace(context), exception(context), outcome(context));
    }

    protected KeyValue codeFunction(ScheduledTaskObservationContext context) {
        return KeyValue.of(ScheduledTaskObservationDocumentation.LowCardinalityKeyNames.CODE_FUNCTION, context.getMethod().getName());
    }

    protected KeyValue codeNamespace(ScheduledTaskObservationContext context) {
        if (context.getTargetClass().getCanonicalName() != null) {
            return KeyValue.of(ScheduledTaskObservationDocumentation.LowCardinalityKeyNames.CODE_NAMESPACE, context.getTargetClass().getCanonicalName());
        }
        return CODE_NAMESPACE_ANONYMOUS;
    }

    protected KeyValue exception(ScheduledTaskObservationContext context) {
        if (context.getError() != null) {
            return KeyValue.of(ScheduledTaskObservationDocumentation.LowCardinalityKeyNames.EXCEPTION, context.getError().getClass().getSimpleName());
        }
        return EXCEPTION_NONE;
    }

    protected KeyValue outcome(ScheduledTaskObservationContext context) {
        if (context.getError() != null) {
            return OUTCOME_ERROR;
        }
        if (!context.isComplete()) {
            return OUTCOME_UNKNOWN;
        }
        return OUTCOME_SUCCESS;
    }
}

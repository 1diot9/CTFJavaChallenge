package org.springframework.scheduling.support;

import io.micrometer.common.docs.KeyName;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationConvention;
import io.micrometer.observation.docs.ObservationDocumentation;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/support/ScheduledTaskObservationDocumentation.class */
public enum ScheduledTaskObservationDocumentation implements ObservationDocumentation {
    TASKS_SCHEDULED_EXECUTION { // from class: org.springframework.scheduling.support.ScheduledTaskObservationDocumentation.1
        @Override // io.micrometer.observation.docs.ObservationDocumentation
        public Class<? extends ObservationConvention<? extends Observation.Context>> getDefaultConvention() {
            return DefaultScheduledTaskObservationConvention.class;
        }

        @Override // io.micrometer.observation.docs.ObservationDocumentation
        public KeyName[] getLowCardinalityKeyNames() {
            return LowCardinalityKeyNames.values();
        }

        @Override // io.micrometer.observation.docs.ObservationDocumentation
        public KeyName[] getHighCardinalityKeyNames() {
            return new KeyName[0];
        }
    };

    /* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/support/ScheduledTaskObservationDocumentation$LowCardinalityKeyNames.class */
    public enum LowCardinalityKeyNames implements KeyName {
        CODE_FUNCTION { // from class: org.springframework.scheduling.support.ScheduledTaskObservationDocumentation.LowCardinalityKeyNames.1
            @Override // io.micrometer.common.docs.KeyName
            public String asString() {
                return "code.function";
            }
        },
        CODE_NAMESPACE { // from class: org.springframework.scheduling.support.ScheduledTaskObservationDocumentation.LowCardinalityKeyNames.2
            @Override // io.micrometer.common.docs.KeyName
            public String asString() {
                return "code.namespace";
            }
        },
        EXCEPTION { // from class: org.springframework.scheduling.support.ScheduledTaskObservationDocumentation.LowCardinalityKeyNames.3
            @Override // io.micrometer.common.docs.KeyName
            public String asString() {
                return SimpleMappingExceptionResolver.DEFAULT_EXCEPTION_ATTRIBUTE;
            }
        },
        OUTCOME { // from class: org.springframework.scheduling.support.ScheduledTaskObservationDocumentation.LowCardinalityKeyNames.4
            @Override // io.micrometer.common.docs.KeyName
            public String asString() {
                return "outcome";
            }
        }
    }
}

package org.springframework.boot.autoconfigure.web.client;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.NoneNestedConditions;
import org.springframework.context.annotation.ConfigurationCondition;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/client/NotReactiveWebApplicationCondition.class */
class NotReactiveWebApplicationCondition extends NoneNestedConditions {
    NotReactiveWebApplicationCondition() {
        super(ConfigurationCondition.ConfigurationPhase.PARSE_CONFIGURATION);
    }

    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/client/NotReactiveWebApplicationCondition$ReactiveWebApplication.class */
    private static final class ReactiveWebApplication {
        private ReactiveWebApplication() {
        }
    }
}

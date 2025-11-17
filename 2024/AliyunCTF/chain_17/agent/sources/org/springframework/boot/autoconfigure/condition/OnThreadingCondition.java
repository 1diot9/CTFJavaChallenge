package org.springframework.boot.autoconfigure.condition;

import java.lang.annotation.Annotation;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.thread.Threading;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/condition/OnThreadingCondition.class */
class OnThreadingCondition extends SpringBootCondition {
    OnThreadingCondition() {
    }

    @Override // org.springframework.boot.autoconfigure.condition.SpringBootCondition
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Map<String, Object> attributes = metadata.getAnnotationAttributes(ConditionalOnThreading.class.getName());
        Threading threading = (Threading) attributes.get("value");
        return getMatchOutcome(context.getEnvironment(), threading);
    }

    private ConditionOutcome getMatchOutcome(Environment environment, Threading threading) {
        String name = threading.name();
        ConditionMessage.Builder message = ConditionMessage.forCondition((Class<? extends Annotation>) ConditionalOnThreading.class, new Object[0]);
        if (threading.isActive(environment)) {
            return ConditionOutcome.match(message.foundExactly(name));
        }
        return ConditionOutcome.noMatch(message.didNotFind(name).atAll());
    }
}

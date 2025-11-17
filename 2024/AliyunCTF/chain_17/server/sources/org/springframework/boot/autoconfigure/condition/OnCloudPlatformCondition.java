package org.springframework.boot.autoconfigure.condition;

import java.lang.annotation.Annotation;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.cloud.CloudPlatform;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/condition/OnCloudPlatformCondition.class */
class OnCloudPlatformCondition extends SpringBootCondition {
    OnCloudPlatformCondition() {
    }

    @Override // org.springframework.boot.autoconfigure.condition.SpringBootCondition
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Map<String, Object> attributes = metadata.getAnnotationAttributes(ConditionalOnCloudPlatform.class.getName());
        CloudPlatform cloudPlatform = (CloudPlatform) attributes.get("value");
        return getMatchOutcome(context.getEnvironment(), cloudPlatform);
    }

    private ConditionOutcome getMatchOutcome(Environment environment, CloudPlatform cloudPlatform) {
        String name = cloudPlatform.name();
        ConditionMessage.Builder message = ConditionMessage.forCondition((Class<? extends Annotation>) ConditionalOnCloudPlatform.class, new Object[0]);
        if (cloudPlatform.isActive(environment)) {
            return ConditionOutcome.match(message.foundExactly(name));
        }
        return ConditionOutcome.noMatch(message.didNotFind(name).atAll());
    }
}

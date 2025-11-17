package org.springframework.context.annotation;

import java.util.List;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.MultiValueMap;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/ProfileCondition.class */
class ProfileCondition implements Condition {
    ProfileCondition() {
    }

    @Override // org.springframework.context.annotation.Condition
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        MultiValueMap<String, Object> attrs = metadata.getAllAnnotationAttributes(Profile.class.getName());
        if (attrs != null) {
            for (Object value : (List) attrs.get("value")) {
                if (context.getEnvironment().matchesProfiles((String[]) value)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
}

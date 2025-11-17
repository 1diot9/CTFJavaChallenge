package org.springframework.boot.autoconfigure.cache;

import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.context.properties.bind.BindException;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/cache/CacheCondition.class */
class CacheCondition extends SpringBootCondition {
    CacheCondition() {
    }

    @Override // org.springframework.boot.autoconfigure.condition.SpringBootCondition
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        BindResult<CacheType> specified;
        String sourceClass = "";
        if (metadata instanceof ClassMetadata) {
            ClassMetadata classMetadata = (ClassMetadata) metadata;
            sourceClass = classMetadata.getClassName();
        }
        ConditionMessage.Builder message = ConditionMessage.forCondition("Cache", sourceClass);
        Environment environment = context.getEnvironment();
        try {
            specified = Binder.get(environment).bind("spring.cache.type", CacheType.class);
        } catch (BindException e) {
        }
        if (!specified.isBound()) {
            return ConditionOutcome.match(message.because("automatic cache type"));
        }
        CacheType required = CacheConfigurations.getType(((AnnotationMetadata) metadata).getClassName());
        if (specified.get() == required) {
            return ConditionOutcome.match(message.because(specified.get() + " cache type"));
        }
        return ConditionOutcome.noMatch(message.because("unknown cache type"));
    }
}

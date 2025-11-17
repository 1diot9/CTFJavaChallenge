package org.springframework.boot.autoconfigure.condition;

import java.lang.annotation.Annotation;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnJava;
import org.springframework.boot.context.logging.LoggingApplicationListener;
import org.springframework.boot.system.JavaVersion;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotatedTypeMetadata;

@Order(LoggingApplicationListener.DEFAULT_ORDER)
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/condition/OnJavaCondition.class */
class OnJavaCondition extends SpringBootCondition {
    private static final JavaVersion JVM_VERSION = JavaVersion.getJavaVersion();

    OnJavaCondition() {
    }

    @Override // org.springframework.boot.autoconfigure.condition.SpringBootCondition
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Map<String, Object> attributes = metadata.getAnnotationAttributes(ConditionalOnJava.class.getName());
        ConditionalOnJava.Range range = (ConditionalOnJava.Range) attributes.get("range");
        JavaVersion version = (JavaVersion) attributes.get("value");
        return getMatchOutcome(range, JVM_VERSION, version);
    }

    protected ConditionOutcome getMatchOutcome(ConditionalOnJava.Range range, JavaVersion runningVersion, JavaVersion version) {
        boolean match = isWithin(runningVersion, range, version);
        String expected = String.format(range != ConditionalOnJava.Range.EQUAL_OR_NEWER ? "(older than %s)" : "(%s or newer)", version);
        ConditionMessage message = ConditionMessage.forCondition((Class<? extends Annotation>) ConditionalOnJava.class, expected).foundExactly(runningVersion);
        return new ConditionOutcome(match, message);
    }

    private boolean isWithin(JavaVersion runningVersion, ConditionalOnJava.Range range, JavaVersion version) {
        if (range == ConditionalOnJava.Range.EQUAL_OR_NEWER) {
            return runningVersion.isEqualOrNewerThan(version);
        }
        if (range == ConditionalOnJava.Range.OLDER_THAN) {
            return runningVersion.isOlderThan(version);
        }
        throw new IllegalStateException("Unknown range " + range);
    }
}

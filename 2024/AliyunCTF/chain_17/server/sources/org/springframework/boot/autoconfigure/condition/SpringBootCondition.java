package org.springframework.boot.autoconfigure.condition;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/condition/SpringBootCondition.class */
public abstract class SpringBootCondition implements Condition {
    private final Log logger = LogFactory.getLog(getClass());

    public abstract ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata);

    @Override // org.springframework.context.annotation.Condition
    public final boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String classOrMethodName = getClassOrMethodName(metadata);
        try {
            ConditionOutcome outcome = getMatchOutcome(context, metadata);
            logOutcome(classOrMethodName, outcome);
            recordEvaluation(context, classOrMethodName, outcome);
            return outcome.isMatch();
        } catch (NoClassDefFoundError ex) {
            throw new IllegalStateException("Could not evaluate condition on " + classOrMethodName + " due to " + ex.getMessage() + " not found. Make sure your own configuration does not rely on that class. This can also happen if you are @ComponentScanning a springframework package (e.g. if you put a @ComponentScan in the default package by mistake)", ex);
        } catch (RuntimeException ex2) {
            throw new IllegalStateException("Error processing condition on " + getName(metadata), ex2);
        }
    }

    private String getName(AnnotatedTypeMetadata metadata) {
        if (metadata instanceof AnnotationMetadata) {
            AnnotationMetadata annotationMetadata = (AnnotationMetadata) metadata;
            return annotationMetadata.getClassName();
        }
        if (metadata instanceof MethodMetadata) {
            MethodMetadata methodMetadata = (MethodMetadata) metadata;
            return methodMetadata.getDeclaringClassName() + "." + methodMetadata.getMethodName();
        }
        return metadata.toString();
    }

    private static String getClassOrMethodName(AnnotatedTypeMetadata metadata) {
        if (metadata instanceof ClassMetadata) {
            ClassMetadata classMetadata = (ClassMetadata) metadata;
            return classMetadata.getClassName();
        }
        MethodMetadata methodMetadata = (MethodMetadata) metadata;
        return methodMetadata.getDeclaringClassName() + "#" + methodMetadata.getMethodName();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void logOutcome(String classOrMethodName, ConditionOutcome outcome) {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(getLogMessage(classOrMethodName, outcome));
        }
    }

    private StringBuilder getLogMessage(String classOrMethodName, ConditionOutcome outcome) {
        StringBuilder message = new StringBuilder();
        message.append("Condition ");
        message.append(ClassUtils.getShortName(getClass()));
        message.append(" on ");
        message.append(classOrMethodName);
        message.append(outcome.isMatch() ? " matched" : " did not match");
        if (StringUtils.hasLength(outcome.getMessage())) {
            message.append(" due to ");
            message.append(outcome.getMessage());
        }
        return message;
    }

    private void recordEvaluation(ConditionContext context, String classOrMethodName, ConditionOutcome outcome) {
        if (context.getBeanFactory() != null) {
            ConditionEvaluationReport.get(context.getBeanFactory()).recordConditionEvaluation(classOrMethodName, this, outcome);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final boolean anyMatches(ConditionContext context, AnnotatedTypeMetadata metadata, Condition... conditions) {
        for (Condition condition : conditions) {
            if (matches(context, metadata, condition)) {
                return true;
            }
        }
        return false;
    }

    protected final boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata, Condition condition) {
        if (condition instanceof SpringBootCondition) {
            SpringBootCondition springBootCondition = (SpringBootCondition) condition;
            return springBootCondition.getMatchOutcome(context, metadata).isMatch();
        }
        return condition.matches(context, metadata);
    }
}

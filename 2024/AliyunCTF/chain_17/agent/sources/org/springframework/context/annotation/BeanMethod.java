package org.springframework.context.annotation;

import org.springframework.beans.factory.parsing.Problem;
import org.springframework.beans.factory.parsing.ProblemReporter;
import org.springframework.core.type.MethodMetadata;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/BeanMethod.class */
final class BeanMethod extends ConfigurationMethod {
    /* JADX INFO: Access modifiers changed from: package-private */
    public BeanMethod(MethodMetadata metadata, ConfigurationClass configurationClass) {
        super(metadata, configurationClass);
    }

    @Override // org.springframework.context.annotation.ConfigurationMethod
    public void validate(ProblemReporter problemReporter) {
        if ("void".equals(getMetadata().getReturnTypeName())) {
            problemReporter.error(new VoidDeclaredMethodError());
        }
        if (!getMetadata().isStatic() && this.configurationClass.getMetadata().isAnnotated(Configuration.class.getName()) && !getMetadata().isOverridable()) {
            problemReporter.error(new NonOverridableMethodError());
        }
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof BeanMethod) {
                BeanMethod that = (BeanMethod) other;
                if (this.metadata.equals(that.metadata)) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.metadata.hashCode();
    }

    @Override // org.springframework.context.annotation.ConfigurationMethod
    public String toString() {
        return "BeanMethod: " + this.metadata;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/BeanMethod$VoidDeclaredMethodError.class */
    private class VoidDeclaredMethodError extends Problem {
        VoidDeclaredMethodError() {
            super("@Bean method '%s' must not be declared as void; change the method's return type or its annotation.".formatted(BeanMethod.this.getMetadata().getMethodName()), BeanMethod.this.getResourceLocation());
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/BeanMethod$NonOverridableMethodError.class */
    private class NonOverridableMethodError extends Problem {
        NonOverridableMethodError() {
            super("@Bean method '%s' must not be private or final; change the method's modifiers to continue.".formatted(BeanMethod.this.getMetadata().getMethodName()), BeanMethod.this.getResourceLocation());
        }
    }
}

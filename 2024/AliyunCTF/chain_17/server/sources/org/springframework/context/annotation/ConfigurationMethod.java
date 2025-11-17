package org.springframework.context.annotation;

import org.springframework.beans.factory.parsing.Location;
import org.springframework.beans.factory.parsing.ProblemReporter;
import org.springframework.core.type.MethodMetadata;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/ConfigurationMethod.class */
public abstract class ConfigurationMethod {
    protected final MethodMetadata metadata;
    protected final ConfigurationClass configurationClass;

    public ConfigurationMethod(MethodMetadata metadata, ConfigurationClass configurationClass) {
        this.metadata = metadata;
        this.configurationClass = configurationClass;
    }

    public MethodMetadata getMetadata() {
        return this.metadata;
    }

    public ConfigurationClass getConfigurationClass() {
        return this.configurationClass;
    }

    public Location getResourceLocation() {
        return new Location(this.configurationClass.getResource(), this.metadata);
    }

    void validate(ProblemReporter problemReporter) {
    }

    public String toString() {
        return String.format("[%s:name=%s,declaringClass=%s]", getClass().getSimpleName(), getMetadata().getMethodName(), getMetadata().getDeclaringClassName());
    }
}

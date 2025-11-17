package io.micrometer.common.annotation;

import java.lang.annotation.Annotation;

/* loaded from: agent.jar:BOOT-INF/lib/micrometer-commons-1.12.2.jar:io/micrometer/common/annotation/AnnotatedParameter.class */
class AnnotatedParameter {
    final int parameterIndex;
    final Annotation annotation;
    final Object argument;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AnnotatedParameter(int parameterIndex, Annotation annotation, Object argument) {
        this.parameterIndex = parameterIndex;
        this.annotation = annotation;
        this.argument = argument;
    }
}

package io.micrometer.common.annotation;

/* loaded from: agent.jar:BOOT-INF/lib/micrometer-commons-1.12.2.jar:io/micrometer/common/annotation/NoOpValueResolver.class */
public class NoOpValueResolver implements ValueResolver {
    @Override // io.micrometer.common.annotation.ValueResolver
    public String resolve(Object parameter) {
        return null;
    }
}

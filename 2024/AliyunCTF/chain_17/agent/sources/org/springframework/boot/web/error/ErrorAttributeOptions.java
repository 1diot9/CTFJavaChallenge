package org.springframework.boot.web.error;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/error/ErrorAttributeOptions.class */
public final class ErrorAttributeOptions {
    private final Set<Include> includes;

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/error/ErrorAttributeOptions$Include.class */
    public enum Include {
        EXCEPTION,
        STACK_TRACE,
        MESSAGE,
        BINDING_ERRORS
    }

    private ErrorAttributeOptions(Set<Include> includes) {
        this.includes = includes;
    }

    public boolean isIncluded(Include include) {
        return this.includes.contains(include);
    }

    public Set<Include> getIncludes() {
        return this.includes;
    }

    public ErrorAttributeOptions including(Include... includes) {
        EnumSet<Include> updated = copyIncludes();
        updated.addAll(Arrays.asList(includes));
        return new ErrorAttributeOptions(Collections.unmodifiableSet(updated));
    }

    public ErrorAttributeOptions excluding(Include... excludes) {
        EnumSet<Include> updated = copyIncludes();
        Stream stream = Arrays.stream(excludes);
        Objects.requireNonNull(updated);
        stream.forEach((v1) -> {
            r1.remove(v1);
        });
        return new ErrorAttributeOptions(Collections.unmodifiableSet(updated));
    }

    private EnumSet<Include> copyIncludes() {
        return this.includes.isEmpty() ? EnumSet.noneOf(Include.class) : EnumSet.copyOf((Collection) this.includes);
    }

    public static ErrorAttributeOptions defaults() {
        return of(new Include[0]);
    }

    public static ErrorAttributeOptions of(Include... includes) {
        return of(Arrays.asList(includes));
    }

    public static ErrorAttributeOptions of(Collection<Include> includes) {
        return new ErrorAttributeOptions(includes.isEmpty() ? Collections.emptySet() : Collections.unmodifiableSet(EnumSet.copyOf((Collection) includes)));
    }
}

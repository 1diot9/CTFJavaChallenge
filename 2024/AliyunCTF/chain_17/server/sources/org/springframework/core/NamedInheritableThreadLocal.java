package org.springframework.core;

import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/NamedInheritableThreadLocal.class */
public class NamedInheritableThreadLocal<T> extends InheritableThreadLocal<T> {
    private final String name;

    public NamedInheritableThreadLocal(String name) {
        Assert.hasText(name, "Name must not be empty");
        this.name = name;
    }

    public String toString() {
        return this.name;
    }
}

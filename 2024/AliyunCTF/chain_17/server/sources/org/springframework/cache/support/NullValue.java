package org.springframework.cache.support;

import java.io.Serializable;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/cache/support/NullValue.class */
public final class NullValue implements Serializable {
    public static final Object INSTANCE = new NullValue();
    private static final long serialVersionUID = 1;

    private NullValue() {
    }

    private Object readResolve() {
        return INSTANCE;
    }

    public boolean equals(@Nullable Object other) {
        return this == other || other == null;
    }

    public int hashCode() {
        return NullValue.class.hashCode();
    }

    public String toString() {
        return "null";
    }
}

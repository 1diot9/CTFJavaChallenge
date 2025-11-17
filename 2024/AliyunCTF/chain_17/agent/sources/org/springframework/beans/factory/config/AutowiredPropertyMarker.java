package org.springframework.beans.factory.config;

import java.io.Serializable;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/config/AutowiredPropertyMarker.class */
public final class AutowiredPropertyMarker implements Serializable {
    public static final Object INSTANCE = new AutowiredPropertyMarker();

    private AutowiredPropertyMarker() {
    }

    private Object readResolve() {
        return INSTANCE;
    }

    public boolean equals(@Nullable Object other) {
        return this == other;
    }

    public int hashCode() {
        return AutowiredPropertyMarker.class.hashCode();
    }

    public String toString() {
        return "(autowired)";
    }
}

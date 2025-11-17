package org.springframework.beans.factory.support;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/MethodOverrides.class */
public class MethodOverrides {
    private final Set<MethodOverride> overrides = new CopyOnWriteArraySet();

    public MethodOverrides() {
    }

    public MethodOverrides(MethodOverrides other) {
        addOverrides(other);
    }

    public void addOverrides(@Nullable MethodOverrides other) {
        if (other != null) {
            this.overrides.addAll(other.overrides);
        }
    }

    public void addOverride(MethodOverride override) {
        this.overrides.add(override);
    }

    public Set<MethodOverride> getOverrides() {
        return this.overrides;
    }

    public boolean isEmpty() {
        return this.overrides.isEmpty();
    }

    @Nullable
    public MethodOverride getOverride(Method method) {
        MethodOverride match = null;
        for (MethodOverride candidate : this.overrides) {
            if (candidate.matches(method)) {
                match = candidate;
            }
        }
        return match;
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof MethodOverrides) {
                MethodOverrides that = (MethodOverrides) other;
                if (this.overrides.equals(that.overrides)) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.overrides.hashCode();
    }
}

package org.springframework.aop.support;

import java.io.Serializable;
import org.springframework.aop.ClassFilter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/support/RootClassFilter.class */
public class RootClassFilter implements ClassFilter, Serializable {
    private final Class<?> clazz;

    public RootClassFilter(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        this.clazz = clazz;
    }

    @Override // org.springframework.aop.ClassFilter
    public boolean matches(Class<?> candidate) {
        return this.clazz.isAssignableFrom(candidate);
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof RootClassFilter) {
                RootClassFilter that = (RootClassFilter) other;
                if (this.clazz.equals(that.clazz)) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.clazz.hashCode();
    }

    public String toString() {
        return getClass().getName() + ": " + this.clazz.getName();
    }
}

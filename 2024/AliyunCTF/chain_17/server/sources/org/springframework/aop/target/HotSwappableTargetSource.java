package org.springframework.aop.target;

import java.io.Serializable;
import org.springframework.aop.TargetSource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/target/HotSwappableTargetSource.class */
public class HotSwappableTargetSource implements TargetSource, Serializable {
    private static final long serialVersionUID = 7497929212653839187L;
    private Object target;

    public HotSwappableTargetSource(Object initialTarget) {
        Assert.notNull(initialTarget, "Target object must not be null");
        this.target = initialTarget;
    }

    @Override // org.springframework.aop.TargetSource, org.springframework.aop.TargetClassAware
    public synchronized Class<?> getTargetClass() {
        return this.target.getClass();
    }

    @Override // org.springframework.aop.TargetSource
    public synchronized Object getTarget() {
        return this.target;
    }

    public synchronized Object swap(Object newTarget) throws IllegalArgumentException {
        Assert.notNull(newTarget, "Target object must not be null");
        Object old = this.target;
        this.target = newTarget;
        return old;
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof HotSwappableTargetSource) {
                HotSwappableTargetSource that = (HotSwappableTargetSource) other;
                if (this.target.equals(that.target)) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return HotSwappableTargetSource.class.hashCode();
    }

    public String toString() {
        return "HotSwappableTargetSource for target: " + this.target;
    }
}

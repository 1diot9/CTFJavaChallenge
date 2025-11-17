package org.springframework.beans.factory.support;

import java.lang.reflect.Method;
import org.springframework.beans.BeanMetadataElement;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/MethodOverride.class */
public abstract class MethodOverride implements BeanMetadataElement {
    private final String methodName;
    private boolean overloaded = true;

    @Nullable
    private Object source;

    public abstract boolean matches(Method method);

    /* JADX INFO: Access modifiers changed from: protected */
    public MethodOverride(String methodName) {
        Assert.notNull(methodName, "Method name must not be null");
        this.methodName = methodName;
    }

    public String getMethodName() {
        return this.methodName;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setOverloaded(boolean overloaded) {
        this.overloaded = overloaded;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isOverloaded() {
        return this.overloaded;
    }

    public void setSource(@Nullable Object source) {
        this.source = source;
    }

    @Override // org.springframework.beans.BeanMetadataElement
    @Nullable
    public Object getSource() {
        return this.source;
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof MethodOverride) {
                MethodOverride that = (MethodOverride) other;
                if (!ObjectUtils.nullSafeEquals(this.methodName, that.methodName) || !ObjectUtils.nullSafeEquals(this.source, that.source)) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return ObjectUtils.nullSafeHash(this.methodName, this.source);
    }
}

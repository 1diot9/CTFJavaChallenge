package org.springframework.beans;

import java.beans.PropertyChangeEvent;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/PropertyAccessException.class */
public abstract class PropertyAccessException extends BeansException {

    @Nullable
    private final PropertyChangeEvent propertyChangeEvent;

    public abstract String getErrorCode();

    public PropertyAccessException(PropertyChangeEvent propertyChangeEvent, String msg, @Nullable Throwable cause) {
        super(msg, cause);
        this.propertyChangeEvent = propertyChangeEvent;
    }

    public PropertyAccessException(String msg, @Nullable Throwable cause) {
        super(msg, cause);
        this.propertyChangeEvent = null;
    }

    @Nullable
    public PropertyChangeEvent getPropertyChangeEvent() {
        return this.propertyChangeEvent;
    }

    @Nullable
    public String getPropertyName() {
        if (this.propertyChangeEvent != null) {
            return this.propertyChangeEvent.getPropertyName();
        }
        return null;
    }

    @Nullable
    public Object getValue() {
        if (this.propertyChangeEvent != null) {
            return this.propertyChangeEvent.getNewValue();
        }
        return null;
    }
}

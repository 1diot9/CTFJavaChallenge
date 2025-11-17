package org.springframework.validation;

import java.io.Serializable;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.ConfigurablePropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/validation/BeanPropertyBindingResult.class */
public class BeanPropertyBindingResult extends AbstractPropertyBindingResult implements Serializable {

    @Nullable
    private final Object target;
    private final boolean autoGrowNestedPaths;
    private final int autoGrowCollectionLimit;

    @Nullable
    private transient BeanWrapper beanWrapper;

    public BeanPropertyBindingResult(@Nullable Object target, String objectName) {
        this(target, objectName, true, Integer.MAX_VALUE);
    }

    public BeanPropertyBindingResult(@Nullable Object target, String objectName, boolean autoGrowNestedPaths, int autoGrowCollectionLimit) {
        super(objectName);
        this.target = target;
        this.autoGrowNestedPaths = autoGrowNestedPaths;
        this.autoGrowCollectionLimit = autoGrowCollectionLimit;
    }

    @Override // org.springframework.validation.AbstractBindingResult, org.springframework.validation.BindingResult
    @Nullable
    public final Object getTarget() {
        return this.target;
    }

    @Override // org.springframework.validation.AbstractPropertyBindingResult
    public final ConfigurablePropertyAccessor getPropertyAccessor() {
        if (this.beanWrapper == null) {
            this.beanWrapper = createBeanWrapper();
            this.beanWrapper.setExtractOldValueForEditor(true);
            this.beanWrapper.setAutoGrowNestedPaths(this.autoGrowNestedPaths);
            this.beanWrapper.setAutoGrowCollectionLimit(this.autoGrowCollectionLimit);
        }
        return this.beanWrapper;
    }

    protected BeanWrapper createBeanWrapper() {
        if (this.target == null) {
            throw new IllegalStateException("Cannot access properties on null bean instance '" + getObjectName() + "'");
        }
        return PropertyAccessorFactory.forBeanPropertyAccess(this.target);
    }
}

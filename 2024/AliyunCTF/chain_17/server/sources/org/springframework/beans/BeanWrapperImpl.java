package org.springframework.beans;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.AbstractNestablePropertyAccessor;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/BeanWrapperImpl.class */
public class BeanWrapperImpl extends AbstractNestablePropertyAccessor implements BeanWrapper {

    @Nullable
    private CachedIntrospectionResults cachedIntrospectionResults;

    public BeanWrapperImpl() {
        this(true);
    }

    public BeanWrapperImpl(boolean registerDefaultEditors) {
        super(registerDefaultEditors);
    }

    public BeanWrapperImpl(Object object) {
        super(object);
    }

    public BeanWrapperImpl(Class<?> clazz) {
        super(clazz);
    }

    public BeanWrapperImpl(Object object, String nestedPath, Object rootObject) {
        super(object, nestedPath, rootObject);
    }

    private BeanWrapperImpl(Object object, String nestedPath, BeanWrapperImpl parent) {
        super(object, nestedPath, (AbstractNestablePropertyAccessor) parent);
    }

    public void setBeanInstance(Object object) {
        this.wrappedObject = object;
        this.rootObject = object;
        this.typeConverterDelegate = new TypeConverterDelegate(this, this.wrappedObject);
        setIntrospectionClass(object.getClass());
    }

    @Override // org.springframework.beans.AbstractNestablePropertyAccessor
    public void setWrappedInstance(Object object, @Nullable String nestedPath, @Nullable Object rootObject) {
        super.setWrappedInstance(object, nestedPath, rootObject);
        setIntrospectionClass(getWrappedClass());
    }

    protected void setIntrospectionClass(Class<?> clazz) {
        if (this.cachedIntrospectionResults != null && this.cachedIntrospectionResults.getBeanClass() != clazz) {
            this.cachedIntrospectionResults = null;
        }
    }

    private CachedIntrospectionResults getCachedIntrospectionResults() {
        if (this.cachedIntrospectionResults == null) {
            this.cachedIntrospectionResults = CachedIntrospectionResults.forClass(getWrappedClass());
        }
        return this.cachedIntrospectionResults;
    }

    @Nullable
    public Object convertForProperty(@Nullable Object value, String propertyName) throws TypeMismatchException {
        CachedIntrospectionResults cachedIntrospectionResults = getCachedIntrospectionResults();
        PropertyDescriptor pd = cachedIntrospectionResults.getPropertyDescriptor(propertyName);
        if (pd == null) {
            throw new InvalidPropertyException(getRootClass(), getNestedPath() + propertyName, "No property '" + propertyName + "' found");
        }
        TypeDescriptor td = ((GenericTypeAwarePropertyDescriptor) pd).getTypeDescriptor();
        return convertForProperty(propertyName, null, value, td);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.beans.AbstractNestablePropertyAccessor
    @Nullable
    public BeanPropertyHandler getLocalPropertyHandler(String propertyName) {
        PropertyDescriptor pd = getCachedIntrospectionResults().getPropertyDescriptor(propertyName);
        if (pd != null) {
            return new BeanPropertyHandler((GenericTypeAwarePropertyDescriptor) pd);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.beans.AbstractNestablePropertyAccessor
    public BeanWrapperImpl newNestedPropertyAccessor(Object object, String nestedPath) {
        return new BeanWrapperImpl(object, nestedPath, this);
    }

    @Override // org.springframework.beans.AbstractNestablePropertyAccessor
    protected NotWritablePropertyException createNotWritablePropertyException(String propertyName) {
        PropertyMatches matches = PropertyMatches.forProperty(propertyName, getRootClass());
        throw new NotWritablePropertyException(getRootClass(), getNestedPath() + propertyName, matches.buildErrorMessage(), matches.getPossibleMatches());
    }

    @Override // org.springframework.beans.BeanWrapper
    public PropertyDescriptor[] getPropertyDescriptors() {
        return getCachedIntrospectionResults().getPropertyDescriptors();
    }

    @Override // org.springframework.beans.BeanWrapper
    public PropertyDescriptor getPropertyDescriptor(String propertyName) throws InvalidPropertyException {
        BeanWrapperImpl nestedBw = (BeanWrapperImpl) getPropertyAccessorForPropertyPath(propertyName);
        String finalPath = getFinalPath(nestedBw, propertyName);
        PropertyDescriptor pd = nestedBw.getCachedIntrospectionResults().getPropertyDescriptor(finalPath);
        if (pd == null) {
            throw new InvalidPropertyException(getRootClass(), getNestedPath() + propertyName, "No property '" + propertyName + "' found");
        }
        return pd;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/BeanWrapperImpl$BeanPropertyHandler.class */
    public class BeanPropertyHandler extends AbstractNestablePropertyAccessor.PropertyHandler {
        private final GenericTypeAwarePropertyDescriptor pd;

        public BeanPropertyHandler(GenericTypeAwarePropertyDescriptor pd) {
            super(pd.getPropertyType(), pd.getReadMethod() != null, pd.getWriteMethod() != null);
            this.pd = pd;
        }

        @Override // org.springframework.beans.AbstractNestablePropertyAccessor.PropertyHandler
        public TypeDescriptor toTypeDescriptor() {
            return this.pd.getTypeDescriptor();
        }

        @Override // org.springframework.beans.AbstractNestablePropertyAccessor.PropertyHandler
        public ResolvableType getResolvableType() {
            return this.pd.getReadMethodType();
        }

        @Override // org.springframework.beans.AbstractNestablePropertyAccessor.PropertyHandler
        public TypeDescriptor getMapValueType(int nestingLevel) {
            return new TypeDescriptor(this.pd.getReadMethodType().getNested(nestingLevel).asMap().getGeneric(1), null, this.pd.getTypeDescriptor().getAnnotations());
        }

        @Override // org.springframework.beans.AbstractNestablePropertyAccessor.PropertyHandler
        public TypeDescriptor getCollectionType(int nestingLevel) {
            return new TypeDescriptor(this.pd.getReadMethodType().getNested(nestingLevel).asCollection().getGeneric(new int[0]), null, this.pd.getTypeDescriptor().getAnnotations());
        }

        @Override // org.springframework.beans.AbstractNestablePropertyAccessor.PropertyHandler
        @Nullable
        public TypeDescriptor nested(int level) {
            return this.pd.getTypeDescriptor().nested(level);
        }

        @Override // org.springframework.beans.AbstractNestablePropertyAccessor.PropertyHandler
        @Nullable
        public Object getValue() throws Exception {
            Method readMethod = this.pd.getReadMethod();
            Assert.state(readMethod != null, "No read method available");
            ReflectionUtils.makeAccessible(readMethod);
            return readMethod.invoke(BeanWrapperImpl.this.getWrappedInstance(), (Object[]) null);
        }

        @Override // org.springframework.beans.AbstractNestablePropertyAccessor.PropertyHandler
        public void setValue(@Nullable Object value) throws Exception {
            Method writeMethod = this.pd.getWriteMethodForActualAccess();
            ReflectionUtils.makeAccessible(writeMethod);
            writeMethod.invoke(BeanWrapperImpl.this.getWrappedInstance(), value);
        }

        @Override // org.springframework.beans.AbstractNestablePropertyAccessor.PropertyHandler
        public boolean setValueFallbackIfPossible(@Nullable Object value) {
            Method writeMethod = this.pd.getWriteMethodFallback(value != null ? value.getClass() : null);
            if (writeMethod != null) {
                ReflectionUtils.makeAccessible(writeMethod);
                try {
                    writeMethod.invoke(BeanWrapperImpl.this.getWrappedInstance(), value);
                    return true;
                } catch (Exception ex) {
                    LogFactory.getLog((Class<?>) BeanPropertyHandler.class).debug("Write method fallback failed", ex);
                    return false;
                }
            }
            return false;
        }
    }
}

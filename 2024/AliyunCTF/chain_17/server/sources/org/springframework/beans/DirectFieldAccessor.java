package org.springframework.beans;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.AbstractNestablePropertyAccessor;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/DirectFieldAccessor.class */
public class DirectFieldAccessor extends AbstractNestablePropertyAccessor {
    private final Map<String, FieldPropertyHandler> fieldMap;

    public DirectFieldAccessor(Object object) {
        super(object);
        this.fieldMap = new HashMap();
    }

    protected DirectFieldAccessor(Object object, String nestedPath, DirectFieldAccessor parent) {
        super(object, nestedPath, (AbstractNestablePropertyAccessor) parent);
        this.fieldMap = new HashMap();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.beans.AbstractNestablePropertyAccessor
    @Nullable
    public FieldPropertyHandler getLocalPropertyHandler(String propertyName) {
        Field field;
        FieldPropertyHandler propertyHandler = this.fieldMap.get(propertyName);
        if (propertyHandler == null && (field = ReflectionUtils.findField(getWrappedClass(), propertyName)) != null) {
            propertyHandler = new FieldPropertyHandler(field);
            this.fieldMap.put(propertyName, propertyHandler);
        }
        return propertyHandler;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.beans.AbstractNestablePropertyAccessor
    public DirectFieldAccessor newNestedPropertyAccessor(Object object, String nestedPath) {
        return new DirectFieldAccessor(object, nestedPath, this);
    }

    @Override // org.springframework.beans.AbstractNestablePropertyAccessor
    protected NotWritablePropertyException createNotWritablePropertyException(String propertyName) {
        PropertyMatches matches = PropertyMatches.forField(propertyName, getRootClass());
        throw new NotWritablePropertyException(getRootClass(), getNestedPath() + propertyName, matches.buildErrorMessage(), matches.getPossibleMatches());
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/DirectFieldAccessor$FieldPropertyHandler.class */
    public class FieldPropertyHandler extends AbstractNestablePropertyAccessor.PropertyHandler {
        private final Field field;
        private final ResolvableType resolvableType;

        public FieldPropertyHandler(Field field) {
            super(field.getType(), true, true);
            this.field = field;
            this.resolvableType = ResolvableType.forField(this.field);
        }

        @Override // org.springframework.beans.AbstractNestablePropertyAccessor.PropertyHandler
        public TypeDescriptor toTypeDescriptor() {
            return new TypeDescriptor(this.resolvableType, this.field.getType(), this.field.getAnnotations());
        }

        @Override // org.springframework.beans.AbstractNestablePropertyAccessor.PropertyHandler
        public ResolvableType getResolvableType() {
            return this.resolvableType;
        }

        @Override // org.springframework.beans.AbstractNestablePropertyAccessor.PropertyHandler
        public TypeDescriptor getMapValueType(int nestingLevel) {
            return new TypeDescriptor(this.resolvableType.getNested(nestingLevel).asMap().getGeneric(1), null, this.field.getAnnotations());
        }

        @Override // org.springframework.beans.AbstractNestablePropertyAccessor.PropertyHandler
        public TypeDescriptor getCollectionType(int nestingLevel) {
            return new TypeDescriptor(this.resolvableType.getNested(nestingLevel).asCollection().getGeneric(new int[0]), null, this.field.getAnnotations());
        }

        @Override // org.springframework.beans.AbstractNestablePropertyAccessor.PropertyHandler
        @Nullable
        public TypeDescriptor nested(int level) {
            return TypeDescriptor.nested(this.field, level);
        }

        @Override // org.springframework.beans.AbstractNestablePropertyAccessor.PropertyHandler
        @Nullable
        public Object getValue() throws Exception {
            try {
                ReflectionUtils.makeAccessible(this.field);
                return this.field.get(DirectFieldAccessor.this.getWrappedInstance());
            } catch (IllegalAccessException ex) {
                throw new InvalidPropertyException(DirectFieldAccessor.this.getWrappedClass(), this.field.getName(), "Field is not accessible", ex);
            }
        }

        @Override // org.springframework.beans.AbstractNestablePropertyAccessor.PropertyHandler
        public void setValue(@Nullable Object value) throws Exception {
            try {
                ReflectionUtils.makeAccessible(this.field);
                this.field.set(DirectFieldAccessor.this.getWrappedInstance(), value);
            } catch (IllegalAccessException ex) {
                throw new InvalidPropertyException(DirectFieldAccessor.this.getWrappedClass(), this.field.getName(), "Field is not accessible", ex);
            }
        }
    }
}

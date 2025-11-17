package org.springframework.beans;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.Property;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/GenericTypeAwarePropertyDescriptor.class */
public final class GenericTypeAwarePropertyDescriptor extends PropertyDescriptor {
    private final Class<?> beanClass;

    @Nullable
    private final Method readMethod;

    @Nullable
    private final Method writeMethod;

    @Nullable
    private Set<Method> ambiguousWriteMethods;
    private volatile boolean ambiguousWriteMethodsLogged;

    @Nullable
    private MethodParameter writeMethodParameter;

    @Nullable
    private volatile ResolvableType writeMethodType;

    @Nullable
    private ResolvableType readMethodType;

    @Nullable
    private volatile TypeDescriptor typeDescriptor;

    @Nullable
    private Class<?> propertyType;

    @Nullable
    private final Class<?> propertyEditorClass;

    public GenericTypeAwarePropertyDescriptor(Class<?> beanClass, String propertyName, @Nullable Method readMethod, @Nullable Method writeMethod, @Nullable Class<?> propertyEditorClass) throws IntrospectionException {
        super(propertyName, (Method) null, (Method) null);
        Method candidate;
        this.beanClass = beanClass;
        Method readMethodToUse = readMethod != null ? BridgeMethodResolver.findBridgedMethod(readMethod) : null;
        Method writeMethodToUse = writeMethod != null ? BridgeMethodResolver.findBridgedMethod(writeMethod) : null;
        if (writeMethodToUse == null && readMethodToUse != null && (candidate = ClassUtils.getMethodIfAvailable(this.beanClass, "set" + StringUtils.capitalize(getName()), (Class[]) null)) != null && candidate.getParameterCount() == 1) {
            writeMethodToUse = candidate;
        }
        this.readMethod = readMethodToUse;
        this.writeMethod = writeMethodToUse;
        if (this.writeMethod != null) {
            if (this.readMethod == null) {
                Set<Method> ambiguousCandidates = new HashSet<>();
                for (Method method : beanClass.getMethods()) {
                    if (method.getName().equals(writeMethodToUse.getName()) && !method.equals(writeMethodToUse) && !method.isBridge() && method.getParameterCount() == writeMethodToUse.getParameterCount()) {
                        ambiguousCandidates.add(method);
                    }
                }
                if (!ambiguousCandidates.isEmpty()) {
                    this.ambiguousWriteMethods = ambiguousCandidates;
                }
            }
            this.writeMethodParameter = new MethodParameter(this.writeMethod, 0).withContainingClass(this.beanClass);
        }
        if (this.readMethod != null) {
            this.readMethodType = ResolvableType.forMethodReturnType(this.readMethod, this.beanClass);
            this.propertyType = this.readMethodType.resolve(this.readMethod.getReturnType());
        } else if (this.writeMethodParameter != null) {
            this.propertyType = this.writeMethodParameter.getParameterType();
        }
        this.propertyEditorClass = propertyEditorClass;
    }

    public Class<?> getBeanClass() {
        return this.beanClass;
    }

    @Nullable
    public Method getReadMethod() {
        return this.readMethod;
    }

    @Nullable
    public Method getWriteMethod() {
        return this.writeMethod;
    }

    public Method getWriteMethodForActualAccess() {
        Assert.state(this.writeMethod != null, "No write method available");
        if (this.ambiguousWriteMethods != null && !this.ambiguousWriteMethodsLogged) {
            this.ambiguousWriteMethodsLogged = true;
            LogFactory.getLog((Class<?>) GenericTypeAwarePropertyDescriptor.class).debug("Non-unique JavaBean property '" + getName() + "' being accessed! Ambiguous write methods found next to actually used [" + this.writeMethod + "]: " + this.ambiguousWriteMethods);
        }
        return this.writeMethod;
    }

    @Nullable
    public Method getWriteMethodFallback(@Nullable Class<?> valueType) {
        if (this.ambiguousWriteMethods != null) {
            for (Method method : this.ambiguousWriteMethods) {
                Class<?> paramType = method.getParameterTypes()[0];
                if (valueType != null) {
                    if (paramType.isAssignableFrom(valueType)) {
                        return method;
                    }
                } else if (!paramType.isPrimitive()) {
                    return method;
                }
            }
            return null;
        }
        return null;
    }

    public MethodParameter getWriteMethodParameter() {
        Assert.state(this.writeMethodParameter != null, "No write method available");
        return this.writeMethodParameter;
    }

    public ResolvableType getWriteMethodType() {
        ResolvableType writeMethodType = this.writeMethodType;
        if (writeMethodType == null) {
            writeMethodType = ResolvableType.forMethodParameter(getWriteMethodParameter());
            this.writeMethodType = writeMethodType;
        }
        return writeMethodType;
    }

    public ResolvableType getReadMethodType() {
        Assert.state(this.readMethodType != null, "No read method available");
        return this.readMethodType;
    }

    public TypeDescriptor getTypeDescriptor() {
        TypeDescriptor typeDescriptor = this.typeDescriptor;
        if (typeDescriptor == null) {
            Property property = new Property(getBeanClass(), getReadMethod(), getWriteMethod(), getName());
            typeDescriptor = new TypeDescriptor(property);
            this.typeDescriptor = typeDescriptor;
        }
        return typeDescriptor;
    }

    @Nullable
    public Class<?> getPropertyType() {
        return this.propertyType;
    }

    @Nullable
    public Class<?> getPropertyEditorClass() {
        return this.propertyEditorClass;
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof GenericTypeAwarePropertyDescriptor) {
                GenericTypeAwarePropertyDescriptor that = (GenericTypeAwarePropertyDescriptor) other;
                if (!getBeanClass().equals(that.getBeanClass()) || !PropertyDescriptorUtils.equals(this, that)) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return Objects.hash(getBeanClass(), getReadMethod(), getWriteMethod());
    }
}

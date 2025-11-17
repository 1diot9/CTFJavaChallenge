package org.springframework.beans.factory.support;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import org.springframework.util.ClassUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/MethodDescriptor.class */
final class MethodDescriptor extends Record {
    private final Class<?> declaringClass;
    private final String methodName;
    private final Class<?>[] parameterTypes;

    MethodDescriptor(Class<?> declaringClass, String methodName, Class<?>... parameterTypes) {
        this.declaringClass = declaringClass;
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
    }

    @Override // java.lang.Record
    public final String toString() {
        return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, MethodDescriptor.class), MethodDescriptor.class, "declaringClass;methodName;parameterTypes", "FIELD:Lorg/springframework/beans/factory/support/MethodDescriptor;->declaringClass:Ljava/lang/Class;", "FIELD:Lorg/springframework/beans/factory/support/MethodDescriptor;->methodName:Ljava/lang/String;", "FIELD:Lorg/springframework/beans/factory/support/MethodDescriptor;->parameterTypes:[Ljava/lang/Class;").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final int hashCode() {
        return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, MethodDescriptor.class), MethodDescriptor.class, "declaringClass;methodName;parameterTypes", "FIELD:Lorg/springframework/beans/factory/support/MethodDescriptor;->declaringClass:Ljava/lang/Class;", "FIELD:Lorg/springframework/beans/factory/support/MethodDescriptor;->methodName:Ljava/lang/String;", "FIELD:Lorg/springframework/beans/factory/support/MethodDescriptor;->parameterTypes:[Ljava/lang/Class;").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final boolean equals(Object o) {
        return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, MethodDescriptor.class, Object.class), MethodDescriptor.class, "declaringClass;methodName;parameterTypes", "FIELD:Lorg/springframework/beans/factory/support/MethodDescriptor;->declaringClass:Ljava/lang/Class;", "FIELD:Lorg/springframework/beans/factory/support/MethodDescriptor;->methodName:Ljava/lang/String;", "FIELD:Lorg/springframework/beans/factory/support/MethodDescriptor;->parameterTypes:[Ljava/lang/Class;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
    }

    public Class<?> declaringClass() {
        return this.declaringClass;
    }

    public String methodName() {
        return this.methodName;
    }

    public Class<?>[] parameterTypes() {
        return this.parameterTypes;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static MethodDescriptor create(String beanName, Class<?> beanClass, String methodName) {
        try {
            Class<?> declaringClass = beanClass;
            String methodNameToUse = methodName;
            int indexOfDot = methodName.lastIndexOf(46);
            if (indexOfDot > 0) {
                String className = methodName.substring(0, indexOfDot);
                methodNameToUse = methodName.substring(indexOfDot + 1);
                if (!beanClass.getName().equals(className)) {
                    declaringClass = ClassUtils.forName(className, beanClass.getClassLoader());
                }
            }
            return new MethodDescriptor(declaringClass, methodNameToUse, new Class[0]);
        } catch (Exception | LinkageError ex) {
            throw new BeanDefinitionValidationException("Could not create MethodDescriptor for method '%s' on bean with name '%s': %s".formatted(methodName, beanName, ex.getMessage()));
        }
    }
}

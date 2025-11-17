package org.springframework.beans.factory.aot;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/aot/BeanRegistrationKey.class */
public final class BeanRegistrationKey extends Record {
    private final String beanName;
    private final Class<?> beanClass;

    /* JADX INFO: Access modifiers changed from: package-private */
    public BeanRegistrationKey(String beanName, Class<?> beanClass) {
        this.beanName = beanName;
        this.beanClass = beanClass;
    }

    @Override // java.lang.Record
    public final String toString() {
        return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, BeanRegistrationKey.class), BeanRegistrationKey.class, "beanName;beanClass", "FIELD:Lorg/springframework/beans/factory/aot/BeanRegistrationKey;->beanName:Ljava/lang/String;", "FIELD:Lorg/springframework/beans/factory/aot/BeanRegistrationKey;->beanClass:Ljava/lang/Class;").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final int hashCode() {
        return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, BeanRegistrationKey.class), BeanRegistrationKey.class, "beanName;beanClass", "FIELD:Lorg/springframework/beans/factory/aot/BeanRegistrationKey;->beanName:Ljava/lang/String;", "FIELD:Lorg/springframework/beans/factory/aot/BeanRegistrationKey;->beanClass:Ljava/lang/Class;").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final boolean equals(Object o) {
        return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, BeanRegistrationKey.class, Object.class), BeanRegistrationKey.class, "beanName;beanClass", "FIELD:Lorg/springframework/beans/factory/aot/BeanRegistrationKey;->beanName:Ljava/lang/String;", "FIELD:Lorg/springframework/beans/factory/aot/BeanRegistrationKey;->beanClass:Ljava/lang/Class;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
    }

    public String beanName() {
        return this.beanName;
    }

    public Class<?> beanClass() {
        return this.beanClass;
    }
}

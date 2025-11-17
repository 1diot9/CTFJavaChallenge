package org.springframework.boot.context.properties.bind;

import java.lang.reflect.Constructor;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/bind/BindConstructorProvider.class */
public interface BindConstructorProvider {
    public static final BindConstructorProvider DEFAULT = new DefaultBindConstructorProvider();

    Constructor<?> getBindConstructor(Bindable<?> bindable, boolean isNestedConstructorBinding);

    default Constructor<?> getBindConstructor(Class<?> type, boolean isNestedConstructorBinding) {
        return getBindConstructor(Bindable.of(type), isNestedConstructorBinding);
    }
}

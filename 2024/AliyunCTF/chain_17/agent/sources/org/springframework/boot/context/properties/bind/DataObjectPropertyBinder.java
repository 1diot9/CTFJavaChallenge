package org.springframework.boot.context.properties.bind;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/bind/DataObjectPropertyBinder.class */
public interface DataObjectPropertyBinder {
    Object bindProperty(String propertyName, Bindable<?> target);
}

package org.springframework.beans;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/SimpleTypeConverter.class */
public class SimpleTypeConverter extends TypeConverterSupport {
    public SimpleTypeConverter() {
        this.typeConverterDelegate = new TypeConverterDelegate(this);
        registerDefaultEditors();
    }
}

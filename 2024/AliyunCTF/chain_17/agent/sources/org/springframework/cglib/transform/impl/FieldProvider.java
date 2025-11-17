package org.springframework.cglib.transform.impl;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/transform/impl/FieldProvider.class */
public interface FieldProvider {
    String[] getFieldNames();

    Class[] getFieldTypes();

    void setField(int index, Object value);

    Object getField(int index);

    void setField(String name, Object value);

    Object getField(String name);
}

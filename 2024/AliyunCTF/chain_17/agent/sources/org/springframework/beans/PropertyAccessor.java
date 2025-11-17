package org.springframework.beans;

import java.util.Map;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/PropertyAccessor.class */
public interface PropertyAccessor {
    public static final String NESTED_PROPERTY_SEPARATOR = ".";
    public static final char NESTED_PROPERTY_SEPARATOR_CHAR = '.';
    public static final String PROPERTY_KEY_PREFIX = "[";
    public static final char PROPERTY_KEY_PREFIX_CHAR = '[';
    public static final String PROPERTY_KEY_SUFFIX = "]";
    public static final char PROPERTY_KEY_SUFFIX_CHAR = ']';

    boolean isReadableProperty(String propertyName);

    boolean isWritableProperty(String propertyName);

    @Nullable
    Class<?> getPropertyType(String propertyName) throws BeansException;

    @Nullable
    TypeDescriptor getPropertyTypeDescriptor(String propertyName) throws BeansException;

    @Nullable
    Object getPropertyValue(String propertyName) throws BeansException;

    void setPropertyValue(String propertyName, @Nullable Object value) throws BeansException;

    void setPropertyValue(PropertyValue pv) throws BeansException;

    void setPropertyValues(Map<?, ?> map) throws BeansException;

    void setPropertyValues(PropertyValues pvs) throws BeansException;

    void setPropertyValues(PropertyValues pvs, boolean ignoreUnknown) throws BeansException;

    void setPropertyValues(PropertyValues pvs, boolean ignoreUnknown, boolean ignoreInvalid) throws BeansException;
}

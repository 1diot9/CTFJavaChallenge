package org.springframework.beans;

import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import java.util.Collection;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/SimpleBeanInfoFactory.class */
class SimpleBeanInfoFactory implements BeanInfoFactory, Ordered {
    @Override // org.springframework.beans.BeanInfoFactory
    @NonNull
    public BeanInfo getBeanInfo(final Class<?> beanClass) throws IntrospectionException {
        final Collection<? extends PropertyDescriptor> pds = PropertyDescriptorUtils.determineBasicProperties(beanClass);
        return new SimpleBeanInfo() { // from class: org.springframework.beans.SimpleBeanInfoFactory.1
            public BeanDescriptor getBeanDescriptor() {
                return new BeanDescriptor(beanClass);
            }

            public PropertyDescriptor[] getPropertyDescriptors() {
                return (PropertyDescriptor[]) pds.toArray(PropertyDescriptorUtils.EMPTY_PROPERTY_DESCRIPTOR_ARRAY);
            }
        };
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return 2147483646;
    }
}

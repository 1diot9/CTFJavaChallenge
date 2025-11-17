package org.springframework.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import org.springframework.core.Ordered;
import org.springframework.core.SpringProperties;
import org.springframework.lang.NonNull;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/StandardBeanInfoFactory.class */
public class StandardBeanInfoFactory implements BeanInfoFactory, Ordered {
    public static final String IGNORE_BEANINFO_PROPERTY_NAME = "spring.beaninfo.ignore";
    private static final boolean shouldIntrospectorIgnoreBeaninfoClasses = SpringProperties.getFlag(IGNORE_BEANINFO_PROPERTY_NAME);

    @Override // org.springframework.beans.BeanInfoFactory
    @NonNull
    public BeanInfo getBeanInfo(Class<?> beanClass) throws IntrospectionException {
        BeanInfo beanInfo;
        if (shouldIntrospectorIgnoreBeaninfoClasses) {
            beanInfo = Introspector.getBeanInfo(beanClass, 3);
        } else {
            beanInfo = Introspector.getBeanInfo(beanClass);
        }
        BeanInfo beanInfo2 = beanInfo;
        Class<?> classToFlush = beanClass;
        do {
            Introspector.flushFromCaches(classToFlush);
            classToFlush = classToFlush.getSuperclass();
            if (classToFlush == null) {
                break;
            }
        } while (classToFlush != Object.class);
        return beanInfo2;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return Integer.MAX_VALUE;
    }
}

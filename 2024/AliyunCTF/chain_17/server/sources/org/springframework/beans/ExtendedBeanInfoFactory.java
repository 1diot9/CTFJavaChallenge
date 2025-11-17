package org.springframework.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.lang.reflect.Method;
import org.springframework.lang.NonNull;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/ExtendedBeanInfoFactory.class */
public class ExtendedBeanInfoFactory extends StandardBeanInfoFactory {
    @Override // org.springframework.beans.StandardBeanInfoFactory, org.springframework.beans.BeanInfoFactory
    @NonNull
    public BeanInfo getBeanInfo(Class<?> beanClass) throws IntrospectionException {
        BeanInfo beanInfo = super.getBeanInfo(beanClass);
        return supports(beanClass) ? new ExtendedBeanInfo(beanInfo) : beanInfo;
    }

    private boolean supports(Class<?> beanClass) {
        for (Method method : beanClass.getMethods()) {
            if (ExtendedBeanInfo.isCandidateWriteMethod(method)) {
                return true;
            }
        }
        return false;
    }
}

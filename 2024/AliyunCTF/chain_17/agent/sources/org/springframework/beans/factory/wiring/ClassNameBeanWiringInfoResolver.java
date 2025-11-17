package org.springframework.beans.factory.wiring;

import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/wiring/ClassNameBeanWiringInfoResolver.class */
public class ClassNameBeanWiringInfoResolver implements BeanWiringInfoResolver {
    @Override // org.springframework.beans.factory.wiring.BeanWiringInfoResolver
    public BeanWiringInfo resolveWiringInfo(Object beanInstance) {
        Assert.notNull(beanInstance, "Bean instance must not be null");
        return new BeanWiringInfo(ClassUtils.getUserClass(beanInstance).getName(), true);
    }
}

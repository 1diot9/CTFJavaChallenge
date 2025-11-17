package org.springframework.beans.factory.annotation;

import org.springframework.beans.factory.wiring.BeanWiringInfo;
import org.springframework.beans.factory.wiring.BeanWiringInfoResolver;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/annotation/AnnotationBeanWiringInfoResolver.class */
public class AnnotationBeanWiringInfoResolver implements BeanWiringInfoResolver {
    @Override // org.springframework.beans.factory.wiring.BeanWiringInfoResolver
    @Nullable
    public BeanWiringInfo resolveWiringInfo(Object beanInstance) {
        Assert.notNull(beanInstance, "Bean instance must not be null");
        Configurable annotation = (Configurable) beanInstance.getClass().getAnnotation(Configurable.class);
        if (annotation != null) {
            return buildWiringInfo(beanInstance, annotation);
        }
        return null;
    }

    protected BeanWiringInfo buildWiringInfo(Object beanInstance, Configurable annotation) {
        if (!Autowire.NO.equals(annotation.autowire())) {
            return new BeanWiringInfo(annotation.autowire().value(), annotation.dependencyCheck());
        }
        if (!annotation.value().isEmpty()) {
            return new BeanWiringInfo(annotation.value(), false);
        }
        return new BeanWiringInfo(getDefaultBeanName(beanInstance), true);
    }

    protected String getDefaultBeanName(Object beanInstance) {
        return ClassUtils.getUserClass(beanInstance).getName();
    }
}

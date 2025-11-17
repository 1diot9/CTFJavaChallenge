package org.springframework.aop.framework.autoproxy;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Conventions;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/framework/autoproxy/AutoProxyUtils.class */
public abstract class AutoProxyUtils {
    public static final String PRESERVE_TARGET_CLASS_ATTRIBUTE = Conventions.getQualifiedAttributeName(AutoProxyUtils.class, "preserveTargetClass");
    public static final String ORIGINAL_TARGET_CLASS_ATTRIBUTE = Conventions.getQualifiedAttributeName(AutoProxyUtils.class, "originalTargetClass");

    public static boolean shouldProxyTargetClass(ConfigurableListableBeanFactory beanFactory, @Nullable String beanName) {
        if (beanName != null && beanFactory.containsBeanDefinition(beanName)) {
            BeanDefinition bd = beanFactory.getBeanDefinition(beanName);
            return Boolean.TRUE.equals(bd.getAttribute(PRESERVE_TARGET_CLASS_ATTRIBUTE));
        }
        return false;
    }

    @Nullable
    public static Class<?> determineTargetClass(ConfigurableListableBeanFactory beanFactory, @Nullable String beanName) {
        if (beanName == null) {
            return null;
        }
        if (beanFactory.containsBeanDefinition(beanName)) {
            BeanDefinition bd = beanFactory.getMergedBeanDefinition(beanName);
            Class<?> targetClass = (Class) bd.getAttribute(ORIGINAL_TARGET_CLASS_ATTRIBUTE);
            if (targetClass != null) {
                return targetClass;
            }
        }
        return beanFactory.getType(beanName);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void exposeTargetClass(ConfigurableListableBeanFactory beanFactory, @Nullable String beanName, Class<?> targetClass) {
        if (beanName != null && beanFactory.containsBeanDefinition(beanName)) {
            beanFactory.getMergedBeanDefinition(beanName).setAttribute(ORIGINAL_TARGET_CLASS_ATTRIBUTE, targetClass);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isOriginalInstance(String beanName, Class<?> beanClass) {
        return StringUtils.hasLength(beanName) && beanName.length() == beanClass.getName().length() + AutowireCapableBeanFactory.ORIGINAL_INSTANCE_SUFFIX.length() && beanName.startsWith(beanClass.getName()) && beanName.endsWith(AutowireCapableBeanFactory.ORIGINAL_INSTANCE_SUFFIX);
    }
}

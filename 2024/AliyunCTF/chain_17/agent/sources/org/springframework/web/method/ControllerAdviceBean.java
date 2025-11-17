package org.springframework.web.method;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/method/ControllerAdviceBean.class */
public class ControllerAdviceBean implements Ordered {
    private final Object beanOrName;
    private final boolean isSingleton;

    @Nullable
    private Object resolvedBean;

    @Nullable
    private final Class<?> beanType;
    private final HandlerTypePredicate beanTypePredicate;

    @Nullable
    private final BeanFactory beanFactory;

    @Nullable
    private Integer order;

    public ControllerAdviceBean(Object bean) {
        Assert.notNull(bean, "Bean must not be null");
        this.beanOrName = bean;
        this.isSingleton = true;
        this.resolvedBean = bean;
        this.beanType = ClassUtils.getUserClass(bean.getClass());
        this.beanTypePredicate = createBeanTypePredicate(this.beanType);
        this.beanFactory = null;
    }

    public ControllerAdviceBean(String beanName, BeanFactory beanFactory) {
        this(beanName, beanFactory, null);
    }

    public ControllerAdviceBean(String beanName, BeanFactory beanFactory, @Nullable ControllerAdvice controllerAdvice) {
        Assert.hasText(beanName, "Bean name must contain text");
        Assert.notNull(beanFactory, "BeanFactory must not be null");
        Assert.isTrue(beanFactory.containsBean(beanName), (Supplier<String>) () -> {
            return "BeanFactory [" + beanFactory + "] does not contain specified controller advice bean '" + beanName + "'";
        });
        this.beanOrName = beanName;
        this.isSingleton = beanFactory.isSingleton(beanName);
        this.beanType = getBeanType(beanName, beanFactory);
        this.beanTypePredicate = controllerAdvice != null ? createBeanTypePredicate(controllerAdvice) : createBeanTypePredicate(this.beanType);
        this.beanFactory = beanFactory;
    }

    /* JADX WARN: Removed duplicated region for block: B:14:0x005b  */
    /* JADX WARN: Removed duplicated region for block: B:15:0x0070  */
    @Override // org.springframework.core.Ordered
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public int getOrder() {
        /*
            Method dump skipped, instructions count: 238
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.web.method.ControllerAdviceBean.getOrder():int");
    }

    @Nullable
    public Class<?> getBeanType() {
        return this.beanType;
    }

    public Object resolveBean() {
        if (this.resolvedBean == null) {
            Object resolvedBean = obtainBeanFactory().getBean((String) this.beanOrName);
            if (!this.isSingleton) {
                return resolvedBean;
            }
            this.resolvedBean = resolvedBean;
        }
        return this.resolvedBean;
    }

    private BeanFactory obtainBeanFactory() {
        Assert.state(this.beanFactory != null, "No BeanFactory set");
        return this.beanFactory;
    }

    public boolean isApplicableToBeanType(@Nullable Class<?> beanType) {
        return this.beanTypePredicate.test(beanType);
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof ControllerAdviceBean) {
                ControllerAdviceBean that = (ControllerAdviceBean) other;
                if (!this.beanOrName.equals(that.beanOrName) || this.beanFactory != that.beanFactory) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.beanOrName.hashCode();
    }

    public String toString() {
        return this.beanOrName.toString();
    }

    public static List<ControllerAdviceBean> findAnnotatedBeans(ApplicationContext context) {
        ControllerAdvice controllerAdvice;
        ListableBeanFactory beanFactory = context;
        if (context instanceof ConfigurableApplicationContext) {
            ConfigurableApplicationContext cac = (ConfigurableApplicationContext) context;
            beanFactory = cac.getBeanFactory();
        }
        List<ControllerAdviceBean> adviceBeans = new ArrayList<>();
        for (String name : BeanFactoryUtils.beanNamesForTypeIncludingAncestors(beanFactory, (Class<?>) Object.class)) {
            if (!ScopedProxyUtils.isScopedTarget(name) && (controllerAdvice = (ControllerAdvice) beanFactory.findAnnotationOnBean(name, ControllerAdvice.class)) != null) {
                adviceBeans.add(new ControllerAdviceBean(name, beanFactory, controllerAdvice));
            }
        }
        OrderComparator.sort(adviceBeans);
        return adviceBeans;
    }

    @Nullable
    private static Class<?> getBeanType(String beanName, BeanFactory beanFactory) {
        Class<?> beanType = beanFactory.getType(beanName);
        if (beanType != null) {
            return ClassUtils.getUserClass(beanType);
        }
        return null;
    }

    private static HandlerTypePredicate createBeanTypePredicate(@Nullable Class<?> beanType) {
        ControllerAdvice controllerAdvice = beanType != null ? (ControllerAdvice) AnnotatedElementUtils.findMergedAnnotation(beanType, ControllerAdvice.class) : null;
        return createBeanTypePredicate(controllerAdvice);
    }

    private static HandlerTypePredicate createBeanTypePredicate(@Nullable ControllerAdvice controllerAdvice) {
        if (controllerAdvice != null) {
            return HandlerTypePredicate.builder().basePackage(controllerAdvice.basePackages()).basePackageClass(controllerAdvice.basePackageClasses()).assignableType(controllerAdvice.assignableTypes()).annotation(controllerAdvice.annotations()).build();
        }
        return HandlerTypePredicate.forAnyHandlerType();
    }
}

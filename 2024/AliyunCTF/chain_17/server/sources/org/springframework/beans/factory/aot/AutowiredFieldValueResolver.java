package org.springframework.beans.factory.aot;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;
import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.beans.factory.aot.AutowiredElementResolver;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.RegisteredBean;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.function.ThrowingConsumer;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/aot/AutowiredFieldValueResolver.class */
public final class AutowiredFieldValueResolver extends AutowiredElementResolver {
    private final String fieldName;
    private final boolean required;

    @Nullable
    private final String shortcut;

    private AutowiredFieldValueResolver(String fieldName, boolean required, @Nullable String shortcut) {
        Assert.hasText(fieldName, "'fieldName' must not be empty");
        this.fieldName = fieldName;
        this.required = required;
        this.shortcut = shortcut;
    }

    public static AutowiredFieldValueResolver forField(String fieldName) {
        return new AutowiredFieldValueResolver(fieldName, false, null);
    }

    public static AutowiredFieldValueResolver forRequiredField(String fieldName) {
        return new AutowiredFieldValueResolver(fieldName, true, null);
    }

    public AutowiredFieldValueResolver withShortcut(String beanName) {
        return new AutowiredFieldValueResolver(this.fieldName, this.required, beanName);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public <T> void resolve(RegisteredBean registeredBean, ThrowingConsumer<T> action) {
        Assert.notNull(registeredBean, "'registeredBean' must not be null");
        Assert.notNull(action, "'action' must not be null");
        Object resolve = resolve(registeredBean);
        if (resolve != null) {
            action.accept(resolve);
        }
    }

    @Nullable
    public <T> T resolve(RegisteredBean registeredBean, Class<T> cls) {
        T t = (T) resolveObject(registeredBean);
        Assert.isInstanceOf(cls, t);
        return t;
    }

    @Nullable
    public <T> T resolve(RegisteredBean registeredBean) {
        return (T) resolveObject(registeredBean);
    }

    @Nullable
    public Object resolveObject(RegisteredBean registeredBean) {
        Assert.notNull(registeredBean, "'registeredBean' must not be null");
        return resolveValue(registeredBean, getField(registeredBean));
    }

    public void resolveAndSet(RegisteredBean registeredBean, Object instance) {
        Assert.notNull(registeredBean, "'registeredBean' must not be null");
        Assert.notNull(instance, "'instance' must not be null");
        Field field = getField(registeredBean);
        Object resolved = resolveValue(registeredBean, field);
        if (resolved != null) {
            ReflectionUtils.makeAccessible(field);
            ReflectionUtils.setField(field, instance, resolved);
        }
    }

    @Nullable
    private Object resolveValue(RegisteredBean registeredBean, Field field) {
        String beanName = registeredBean.getBeanName();
        Class<?> beanClass = registeredBean.getBeanClass();
        ConfigurableBeanFactory beanFactory = registeredBean.getBeanFactory();
        DependencyDescriptor descriptor = new DependencyDescriptor(field, this.required);
        descriptor.setContainingClass(beanClass);
        if (this.shortcut != null) {
            descriptor = new AutowiredElementResolver.ShortcutDependencyDescriptor(descriptor, this.shortcut);
        }
        Set<String> autowiredBeanNames = new LinkedHashSet<>(1);
        TypeConverter typeConverter = beanFactory.getTypeConverter();
        try {
            Assert.isInstanceOf(AutowireCapableBeanFactory.class, beanFactory);
            Object value = ((AutowireCapableBeanFactory) beanFactory).resolveDependency(descriptor, beanName, autowiredBeanNames, typeConverter);
            registerDependentBeans(beanFactory, beanName, autowiredBeanNames);
            return value;
        } catch (BeansException ex) {
            throw new UnsatisfiedDependencyException((String) null, beanName, new InjectionPoint(field), ex);
        }
    }

    private Field getField(RegisteredBean registeredBean) {
        Field field = ReflectionUtils.findField(registeredBean.getBeanClass(), this.fieldName);
        Assert.notNull(field, (Supplier<String>) () -> {
            return "No field '" + this.fieldName + "' found on " + registeredBean.getBeanClass().getName();
        });
        return field;
    }
}

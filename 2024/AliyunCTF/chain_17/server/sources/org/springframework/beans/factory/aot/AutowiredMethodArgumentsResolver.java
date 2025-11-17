package org.springframework.beans.factory.aot;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.beans.factory.aot.AutowiredElementResolver;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.RegisteredBean;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.function.ThrowingConsumer;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/aot/AutowiredMethodArgumentsResolver.class */
public final class AutowiredMethodArgumentsResolver extends AutowiredElementResolver {
    private final String methodName;
    private final Class<?>[] parameterTypes;
    private final boolean required;

    @Nullable
    private final String[] shortcuts;

    private AutowiredMethodArgumentsResolver(String methodName, Class<?>[] parameterTypes, boolean required, @Nullable String[] shortcuts) {
        Assert.hasText(methodName, "'methodName' must not be empty");
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
        this.required = required;
        this.shortcuts = shortcuts;
    }

    public static AutowiredMethodArgumentsResolver forMethod(String methodName, Class<?>... parameterTypes) {
        return new AutowiredMethodArgumentsResolver(methodName, parameterTypes, false, null);
    }

    public static AutowiredMethodArgumentsResolver forRequiredMethod(String methodName, Class<?>... parameterTypes) {
        return new AutowiredMethodArgumentsResolver(methodName, parameterTypes, true, null);
    }

    public AutowiredMethodArgumentsResolver withShortcut(String... beanNames) {
        return new AutowiredMethodArgumentsResolver(this.methodName, this.parameterTypes, this.required, beanNames);
    }

    public void resolve(RegisteredBean registeredBean, ThrowingConsumer<AutowiredArguments> action) {
        Assert.notNull(registeredBean, "'registeredBean' must not be null");
        Assert.notNull(action, "'action' must not be null");
        AutowiredArguments resolved = resolve(registeredBean);
        if (resolved != null) {
            action.accept(resolved);
        }
    }

    @Nullable
    public AutowiredArguments resolve(RegisteredBean registeredBean) {
        Assert.notNull(registeredBean, "'registeredBean' must not be null");
        return resolveArguments(registeredBean, getMethod(registeredBean));
    }

    public void resolveAndInvoke(RegisteredBean registeredBean, Object instance) {
        Assert.notNull(registeredBean, "'registeredBean' must not be null");
        Assert.notNull(instance, "'instance' must not be null");
        Method method = getMethod(registeredBean);
        AutowiredArguments resolved = resolveArguments(registeredBean, method);
        if (resolved != null) {
            ReflectionUtils.makeAccessible(method);
            ReflectionUtils.invokeMethod(method, instance, resolved.toArray());
        }
    }

    @Nullable
    private AutowiredArguments resolveArguments(RegisteredBean registeredBean, Method method) {
        String beanName = registeredBean.getBeanName();
        Class<?> beanClass = registeredBean.getBeanClass();
        ConfigurableBeanFactory beanFactory = registeredBean.getBeanFactory();
        Assert.isInstanceOf(AutowireCapableBeanFactory.class, beanFactory);
        AutowireCapableBeanFactory autowireCapableBeanFactory = (AutowireCapableBeanFactory) beanFactory;
        int argumentCount = method.getParameterCount();
        Object[] arguments = new Object[argumentCount];
        Set<String> autowiredBeanNames = new LinkedHashSet<>(argumentCount);
        TypeConverter typeConverter = beanFactory.getTypeConverter();
        for (int i = 0; i < argumentCount; i++) {
            MethodParameter parameter = new MethodParameter(method, i);
            DependencyDescriptor descriptor = new DependencyDescriptor(parameter, this.required);
            descriptor.setContainingClass(beanClass);
            String shortcut = this.shortcuts != null ? this.shortcuts[i] : null;
            if (shortcut != null) {
                descriptor = new AutowiredElementResolver.ShortcutDependencyDescriptor(descriptor, shortcut);
            }
            try {
                Object argument = autowireCapableBeanFactory.resolveDependency(descriptor, beanName, autowiredBeanNames, typeConverter);
                if (argument == null && !this.required) {
                    return null;
                }
                arguments[i] = argument;
            } catch (BeansException ex) {
                throw new UnsatisfiedDependencyException((String) null, beanName, new InjectionPoint(parameter), ex);
            }
        }
        registerDependentBeans(beanFactory, beanName, autowiredBeanNames);
        return AutowiredArguments.of(arguments);
    }

    private Method getMethod(RegisteredBean registeredBean) {
        Method method = ReflectionUtils.findMethod(registeredBean.getBeanClass(), this.methodName, this.parameterTypes);
        Assert.notNull(method, (Supplier<String>) () -> {
            return "Method '%s' with parameter types [%s] declared on %s could not be found.".formatted(this.methodName, toCommaSeparatedNames(this.parameterTypes), registeredBean.getBeanClass().getName());
        });
        return method;
    }

    private String toCommaSeparatedNames(Class<?>... parameterTypes) {
        return (String) Arrays.stream(parameterTypes).map((v0) -> {
            return v0.getName();
        }).collect(Collectors.joining(", "));
    }
}

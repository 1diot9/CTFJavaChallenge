package org.springframework.context.annotation;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.RegisteredBean;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/ResourceElementResolver.class */
public abstract class ResourceElementResolver {
    private final String name;
    private final boolean defaultName;

    public abstract void resolveAndSet(RegisteredBean registeredBean, Object instance);

    abstract DependencyDescriptor createDependencyDescriptor(RegisteredBean registeredBean);

    abstract Class<?> getLookupType(RegisteredBean registeredBean);

    abstract AnnotatedElement getAnnotatedElement(RegisteredBean registeredBean);

    ResourceElementResolver(String name, boolean defaultName) {
        this.name = name;
        this.defaultName = defaultName;
    }

    public static ResourceElementResolver forField(String fieldName) {
        return new ResourceFieldResolver(fieldName, true, fieldName);
    }

    public static ResourceElementResolver forField(String fieldName, String resourceName) {
        return new ResourceFieldResolver(resourceName, false, fieldName);
    }

    public static ResourceElementResolver forMethod(String methodName, Class<?> parameterType) {
        return new ResourceMethodResolver(defaultResourceNameForMethod(methodName), true, methodName, parameterType);
    }

    public static ResourceElementResolver forMethod(String methodName, Class<?> parameterType, String resourceName) {
        return new ResourceMethodResolver(resourceName, false, methodName, parameterType);
    }

    private static String defaultResourceNameForMethod(String methodName) {
        if (methodName.startsWith("set") && methodName.length() > 3) {
            return StringUtils.uncapitalizeAsProperty(methodName.substring(3));
        }
        return methodName;
    }

    @Nullable
    public <T> T resolve(RegisteredBean registeredBean) {
        Assert.notNull(registeredBean, "'registeredBean' must not be null");
        return isLazyLookup(registeredBean) ? (T) buildLazyResourceProxy(registeredBean) : (T) resolveValue(registeredBean);
    }

    boolean isLazyLookup(RegisteredBean registeredBean) {
        AnnotatedElement ae = getAnnotatedElement(registeredBean);
        Lazy lazy = (Lazy) ae.getAnnotation(Lazy.class);
        return lazy != null && lazy.value();
    }

    private Object buildLazyResourceProxy(final RegisteredBean registeredBean) {
        final Class<?> lookupType = getLookupType(registeredBean);
        TargetSource ts = new TargetSource() { // from class: org.springframework.context.annotation.ResourceElementResolver.1
            @Override // org.springframework.aop.TargetSource, org.springframework.aop.TargetClassAware
            public Class<?> getTargetClass() {
                return lookupType;
            }

            @Override // org.springframework.aop.TargetSource
            public Object getTarget() {
                return ResourceElementResolver.this.resolveValue(registeredBean);
            }
        };
        ProxyFactory pf = new ProxyFactory();
        pf.setTargetSource(ts);
        if (lookupType.isInterface()) {
            pf.addInterface(lookupType);
        }
        return pf.getProxy(registeredBean.getBeanFactory().getBeanClassLoader());
    }

    private Object resolveValue(RegisteredBean registeredBean) {
        Object resource;
        Set<String> autowiredBeanNames;
        ConfigurableListableBeanFactory factory = registeredBean.getBeanFactory();
        DependencyDescriptor descriptor = createDependencyDescriptor(registeredBean);
        if (this.defaultName && !factory.containsBean(this.name)) {
            autowiredBeanNames = new LinkedHashSet();
            resource = factory.resolveDependency(descriptor, registeredBean.getBeanName(), autowiredBeanNames, null);
            if (resource == null) {
                throw new NoSuchBeanDefinitionException(descriptor.getDependencyType(), "No resolvable resource object");
            }
        } else {
            resource = factory.resolveBeanByName(this.name, descriptor);
            autowiredBeanNames = Collections.singleton(this.name);
        }
        for (String autowiredBeanName : autowiredBeanNames) {
            if (factory.containsBean(autowiredBeanName)) {
                factory.registerDependentBean(autowiredBeanName, registeredBean.getBeanName());
            }
        }
        return resource;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/ResourceElementResolver$ResourceFieldResolver.class */
    private static final class ResourceFieldResolver extends ResourceElementResolver {
        private final String fieldName;

        public ResourceFieldResolver(String name, boolean defaultName, String fieldName) {
            super(name, defaultName);
            this.fieldName = fieldName;
        }

        @Override // org.springframework.context.annotation.ResourceElementResolver
        public void resolveAndSet(RegisteredBean registeredBean, Object instance) {
            Assert.notNull(registeredBean, "'registeredBean' must not be null");
            Assert.notNull(instance, "'instance' must not be null");
            Field field = getField(registeredBean);
            Object resolved = resolve(registeredBean);
            ReflectionUtils.makeAccessible(field);
            ReflectionUtils.setField(field, instance, resolved);
        }

        @Override // org.springframework.context.annotation.ResourceElementResolver
        protected DependencyDescriptor createDependencyDescriptor(RegisteredBean registeredBean) {
            Field field = getField(registeredBean);
            return new LookupDependencyDescriptor(field, field.getType(), isLazyLookup(registeredBean));
        }

        @Override // org.springframework.context.annotation.ResourceElementResolver
        protected Class<?> getLookupType(RegisteredBean registeredBean) {
            return getField(registeredBean).getType();
        }

        @Override // org.springframework.context.annotation.ResourceElementResolver
        protected AnnotatedElement getAnnotatedElement(RegisteredBean registeredBean) {
            return getField(registeredBean);
        }

        private Field getField(RegisteredBean registeredBean) {
            Field field = ReflectionUtils.findField(registeredBean.getBeanClass(), this.fieldName);
            Assert.notNull(field, (Supplier<String>) () -> {
                return "No field '" + this.fieldName + "' found on " + registeredBean.getBeanClass().getName();
            });
            return field;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/ResourceElementResolver$ResourceMethodResolver.class */
    private static final class ResourceMethodResolver extends ResourceElementResolver {
        private final String methodName;
        private final Class<?> lookupType;

        private ResourceMethodResolver(String name, boolean defaultName, String methodName, Class<?> lookupType) {
            super(name, defaultName);
            this.methodName = methodName;
            this.lookupType = lookupType;
        }

        @Override // org.springframework.context.annotation.ResourceElementResolver
        public void resolveAndSet(RegisteredBean registeredBean, Object instance) {
            Assert.notNull(registeredBean, "'registeredBean' must not be null");
            Assert.notNull(instance, "'instance' must not be null");
            Method method = getMethod(registeredBean);
            Object resolved = resolve(registeredBean);
            ReflectionUtils.makeAccessible(method);
            ReflectionUtils.invokeMethod(method, instance, resolved);
        }

        @Override // org.springframework.context.annotation.ResourceElementResolver
        protected DependencyDescriptor createDependencyDescriptor(RegisteredBean registeredBean) {
            return new LookupDependencyDescriptor(getMethod(registeredBean), this.lookupType, isLazyLookup(registeredBean));
        }

        @Override // org.springframework.context.annotation.ResourceElementResolver
        protected Class<?> getLookupType(RegisteredBean bean) {
            return this.lookupType;
        }

        @Override // org.springframework.context.annotation.ResourceElementResolver
        protected AnnotatedElement getAnnotatedElement(RegisteredBean registeredBean) {
            return getMethod(registeredBean);
        }

        private Method getMethod(RegisteredBean registeredBean) {
            Method method = ReflectionUtils.findMethod(registeredBean.getBeanClass(), this.methodName, this.lookupType);
            Assert.notNull(method, (Supplier<String>) () -> {
                return "Method '%s' with parameter type '%s' declared on %s could not be found.".formatted(this.methodName, this.lookupType.getName(), registeredBean.getBeanClass().getName());
            });
            return method;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/ResourceElementResolver$LookupDependencyDescriptor.class */
    static class LookupDependencyDescriptor extends DependencyDescriptor {
        private final Class<?> lookupType;
        private final boolean lazyLookup;

        public LookupDependencyDescriptor(Field field, Class<?> lookupType, boolean lazyLookup) {
            super(field, true);
            this.lookupType = lookupType;
            this.lazyLookup = lazyLookup;
        }

        public LookupDependencyDescriptor(Method method, Class<?> lookupType, boolean lazyLookup) {
            super(new MethodParameter(method, 0), true);
            this.lookupType = lookupType;
            this.lazyLookup = lazyLookup;
        }

        @Override // org.springframework.beans.factory.config.DependencyDescriptor
        public Class<?> getDependencyType() {
            return this.lookupType;
        }

        @Override // org.springframework.beans.factory.config.DependencyDescriptor
        public boolean supportsLazyResolution() {
            return !this.lazyLookup;
        }
    }
}

package org.springframework.beans.factory.support;

import java.lang.reflect.Executable;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.core.ResolvableType;
import org.springframework.core.style.ToStringCreator;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/RegisteredBean.class */
public final class RegisteredBean {
    private final ConfigurableListableBeanFactory beanFactory;
    private final Supplier<String> beanName;
    private final boolean generatedBeanName;
    private final Supplier<RootBeanDefinition> mergedBeanDefinition;

    @Nullable
    private final RegisteredBean parent;

    private RegisteredBean(ConfigurableListableBeanFactory beanFactory, Supplier<String> beanName, boolean generatedBeanName, Supplier<RootBeanDefinition> mergedBeanDefinition, @Nullable RegisteredBean parent) {
        this.beanFactory = beanFactory;
        this.beanName = beanName;
        this.generatedBeanName = generatedBeanName;
        this.mergedBeanDefinition = mergedBeanDefinition;
        this.parent = parent;
    }

    public static RegisteredBean of(ConfigurableListableBeanFactory beanFactory, String beanName) {
        Assert.notNull(beanFactory, "'beanFactory' must not be null");
        Assert.hasLength(beanName, "'beanName' must not be empty");
        return new RegisteredBean(beanFactory, () -> {
            return beanName;
        }, false, () -> {
            return (RootBeanDefinition) beanFactory.getMergedBeanDefinition(beanName);
        }, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static RegisteredBean of(ConfigurableListableBeanFactory beanFactory, String beanName, RootBeanDefinition mbd) {
        return new RegisteredBean(beanFactory, () -> {
            return beanName;
        }, false, () -> {
            return mbd;
        }, null);
    }

    public static RegisteredBean ofInnerBean(RegisteredBean parent, BeanDefinitionHolder innerBean) {
        Assert.notNull(innerBean, "'innerBean' must not be null");
        return ofInnerBean(parent, innerBean.getBeanName(), innerBean.getBeanDefinition());
    }

    public static RegisteredBean ofInnerBean(RegisteredBean parent, BeanDefinition innerBeanDefinition) {
        return ofInnerBean(parent, null, innerBeanDefinition);
    }

    public static RegisteredBean ofInnerBean(RegisteredBean parent, @Nullable String innerBeanName, BeanDefinition innerBeanDefinition) {
        Supplier<String> supplier;
        Assert.notNull(parent, "'parent' must not be null");
        Assert.notNull(innerBeanDefinition, "'innerBeanDefinition' must not be null");
        InnerBeanResolver resolver = new InnerBeanResolver(parent, innerBeanName, innerBeanDefinition);
        if (StringUtils.hasLength(innerBeanName)) {
            supplier = () -> {
                return innerBeanName;
            };
        } else {
            Objects.requireNonNull(resolver);
            supplier = resolver::resolveBeanName;
        }
        Supplier<String> beanName = supplier;
        ConfigurableListableBeanFactory beanFactory = parent.getBeanFactory();
        boolean z = innerBeanName == null;
        Objects.requireNonNull(resolver);
        return new RegisteredBean(beanFactory, beanName, z, resolver::resolveMergedBeanDefinition, parent);
    }

    public String getBeanName() {
        return this.beanName.get();
    }

    public boolean isGeneratedBeanName() {
        return this.generatedBeanName;
    }

    public ConfigurableListableBeanFactory getBeanFactory() {
        return this.beanFactory;
    }

    public Class<?> getBeanClass() {
        return ClassUtils.getUserClass(getBeanType().toClass());
    }

    public ResolvableType getBeanType() {
        return getMergedBeanDefinition().getResolvableType();
    }

    public RootBeanDefinition getMergedBeanDefinition() {
        return this.mergedBeanDefinition.get();
    }

    public boolean isInnerBean() {
        return this.parent != null;
    }

    @Nullable
    public RegisteredBean getParent() {
        return this.parent;
    }

    public Executable resolveConstructorOrFactoryMethod() {
        return new ConstructorResolver((AbstractAutowireCapableBeanFactory) getBeanFactory()).resolveConstructorOrFactoryMethod(getBeanName(), getMergedBeanDefinition());
    }

    @Nullable
    public Object resolveAutowiredArgument(DependencyDescriptor descriptor, TypeConverter typeConverter, Set<String> autowiredBeanNames) {
        return new ConstructorResolver((AbstractAutowireCapableBeanFactory) getBeanFactory()).resolveAutowiredArgument(descriptor, descriptor.getDependencyType(), getBeanName(), autowiredBeanNames, typeConverter, true);
    }

    public String toString() {
        return new ToStringCreator(this).append("beanName", getBeanName()).append("mergedBeanDefinition", getMergedBeanDefinition()).toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/RegisteredBean$InnerBeanResolver.class */
    public static class InnerBeanResolver {
        private final RegisteredBean parent;

        @Nullable
        private final String innerBeanName;
        private final BeanDefinition innerBeanDefinition;

        @Nullable
        private volatile String resolvedBeanName;

        InnerBeanResolver(RegisteredBean parent, @Nullable String innerBeanName, BeanDefinition innerBeanDefinition) {
            Assert.isInstanceOf(AbstractAutowireCapableBeanFactory.class, parent.getBeanFactory());
            this.parent = parent;
            this.innerBeanName = innerBeanName;
            this.innerBeanDefinition = innerBeanDefinition;
        }

        String resolveBeanName() {
            String resolvedBeanName = this.resolvedBeanName;
            if (resolvedBeanName != null) {
                return resolvedBeanName;
            }
            String resolvedBeanName2 = (String) resolveInnerBean((beanName, mergedBeanDefinition) -> {
                return beanName;
            });
            this.resolvedBeanName = resolvedBeanName2;
            return resolvedBeanName2;
        }

        RootBeanDefinition resolveMergedBeanDefinition() {
            return (RootBeanDefinition) resolveInnerBean((beanName, mergedBeanDefinition) -> {
                return mergedBeanDefinition;
            });
        }

        private <T> T resolveInnerBean(BiFunction<String, RootBeanDefinition, T> biFunction) {
            return (T) new BeanDefinitionValueResolver((AbstractAutowireCapableBeanFactory) this.parent.getBeanFactory(), this.parent.getBeanName(), this.parent.getMergedBeanDefinition()).resolveInnerBean(this.innerBeanName, this.innerBeanDefinition, biFunction);
        }
    }
}

package org.springframework.beans.factory.support;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.cglib.core.ClassLoaderAwareGeneratorStrategy;
import org.springframework.cglib.core.SpringNamingPolicy;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.CallbackFilter;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.Factory;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.cglib.proxy.NoOp;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/CglibSubclassingInstantiationStrategy.class */
public class CglibSubclassingInstantiationStrategy extends SimpleInstantiationStrategy {
    private static final int PASSTHROUGH = 0;
    private static final int LOOKUP_OVERRIDE = 1;
    private static final int METHOD_REPLACER = 2;

    @Override // org.springframework.beans.factory.support.SimpleInstantiationStrategy
    protected Object instantiateWithMethodInjection(RootBeanDefinition bd, @Nullable String beanName, BeanFactory owner) {
        return instantiateWithMethodInjection(bd, beanName, owner, null, new Object[0]);
    }

    @Override // org.springframework.beans.factory.support.SimpleInstantiationStrategy
    protected Object instantiateWithMethodInjection(RootBeanDefinition bd, @Nullable String beanName, BeanFactory owner, @Nullable Constructor<?> ctor, Object... args) {
        return new CglibSubclassCreator(bd, owner).instantiate(ctor, args);
    }

    @Override // org.springframework.beans.factory.support.InstantiationStrategy
    public Class<?> getActualBeanClass(RootBeanDefinition bd, @Nullable String beanName, BeanFactory owner) {
        if (!bd.hasMethodOverrides()) {
            return super.getActualBeanClass(bd, beanName, owner);
        }
        return new CglibSubclassCreator(bd, owner).createEnhancedSubclass(bd);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/CglibSubclassingInstantiationStrategy$CglibSubclassCreator.class */
    public static class CglibSubclassCreator {
        private static final Class<?>[] CALLBACK_TYPES = {NoOp.class, LookupOverrideMethodInterceptor.class, ReplaceOverrideMethodInterceptor.class};
        private final RootBeanDefinition beanDefinition;
        private final BeanFactory owner;

        CglibSubclassCreator(RootBeanDefinition beanDefinition, BeanFactory owner) {
            this.beanDefinition = beanDefinition;
            this.owner = owner;
        }

        public Object instantiate(@Nullable Constructor<?> ctor, Object... args) {
            Object instance;
            Class<?> subclass = createEnhancedSubclass(this.beanDefinition);
            if (ctor == null) {
                instance = BeanUtils.instantiateClass(subclass);
            } else {
                try {
                    Constructor<?> enhancedSubclassConstructor = subclass.getConstructor(ctor.getParameterTypes());
                    instance = enhancedSubclassConstructor.newInstance(args);
                } catch (Exception ex) {
                    throw new BeanInstantiationException(this.beanDefinition.getBeanClass(), "Failed to invoke constructor for CGLIB enhanced subclass [" + subclass.getName() + "]", ex);
                }
            }
            Factory factory = (Factory) instance;
            factory.setCallbacks(new Callback[]{NoOp.INSTANCE, new LookupOverrideMethodInterceptor(this.beanDefinition, this.owner), new ReplaceOverrideMethodInterceptor(this.beanDefinition, this.owner)});
            return instance;
        }

        public Class<?> createEnhancedSubclass(RootBeanDefinition beanDefinition) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(beanDefinition.getBeanClass());
            enhancer.setNamingPolicy(SpringNamingPolicy.INSTANCE);
            enhancer.setAttemptLoad(true);
            BeanFactory beanFactory = this.owner;
            if (beanFactory instanceof ConfigurableBeanFactory) {
                ConfigurableBeanFactory cbf = (ConfigurableBeanFactory) beanFactory;
                ClassLoader cl = cbf.getBeanClassLoader();
                enhancer.setStrategy(new ClassLoaderAwareGeneratorStrategy(cl));
            }
            enhancer.setCallbackFilter(new MethodOverrideCallbackFilter(beanDefinition));
            enhancer.setCallbackTypes(CALLBACK_TYPES);
            return enhancer.createClass();
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/CglibSubclassingInstantiationStrategy$CglibIdentitySupport.class */
    private static class CglibIdentitySupport {
        private final RootBeanDefinition beanDefinition;

        public CglibIdentitySupport(RootBeanDefinition beanDefinition) {
            this.beanDefinition = beanDefinition;
        }

        public RootBeanDefinition getBeanDefinition() {
            return this.beanDefinition;
        }

        public boolean equals(@Nullable Object other) {
            return other != null && getClass() == other.getClass() && this.beanDefinition.equals(((CglibIdentitySupport) other).beanDefinition);
        }

        public int hashCode() {
            return this.beanDefinition.hashCode();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/CglibSubclassingInstantiationStrategy$MethodOverrideCallbackFilter.class */
    public static class MethodOverrideCallbackFilter extends CglibIdentitySupport implements CallbackFilter {
        private static final Log logger = LogFactory.getLog((Class<?>) MethodOverrideCallbackFilter.class);

        public MethodOverrideCallbackFilter(RootBeanDefinition beanDefinition) {
            super(beanDefinition);
        }

        @Override // org.springframework.cglib.proxy.CallbackFilter
        public int accept(Method method) {
            MethodOverride methodOverride = getBeanDefinition().getMethodOverrides().getOverride(method);
            if (logger.isTraceEnabled()) {
                logger.trace("MethodOverride for " + method + ": " + methodOverride);
            }
            if (methodOverride == null) {
                return 0;
            }
            if (methodOverride instanceof LookupOverride) {
                return 1;
            }
            if (methodOverride instanceof ReplaceOverride) {
                return 2;
            }
            throw new UnsupportedOperationException("Unexpected MethodOverride subclass: " + methodOverride.getClass().getName());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/CglibSubclassingInstantiationStrategy$LookupOverrideMethodInterceptor.class */
    public static class LookupOverrideMethodInterceptor extends CglibIdentitySupport implements MethodInterceptor {
        private final BeanFactory owner;

        public LookupOverrideMethodInterceptor(RootBeanDefinition beanDefinition, BeanFactory owner) {
            super(beanDefinition);
            this.owner = owner;
        }

        @Override // org.springframework.cglib.proxy.MethodInterceptor
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy mp) throws Throwable {
            LookupOverride lo = (LookupOverride) getBeanDefinition().getMethodOverrides().getOverride(method);
            Assert.state(lo != null, "LookupOverride not found");
            Object[] argsToUse = args.length > 0 ? args : null;
            if (StringUtils.hasText(lo.getBeanName())) {
                Object bean = argsToUse != null ? this.owner.getBean(lo.getBeanName(), argsToUse) : this.owner.getBean(lo.getBeanName());
                if (bean.equals(null)) {
                    return null;
                }
                return bean;
            }
            ResolvableType genericReturnType = ResolvableType.forMethodReturnType(method);
            return argsToUse != null ? this.owner.getBeanProvider(genericReturnType).getObject(argsToUse) : this.owner.getBeanProvider(genericReturnType).getObject();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/CglibSubclassingInstantiationStrategy$ReplaceOverrideMethodInterceptor.class */
    public static class ReplaceOverrideMethodInterceptor extends CglibIdentitySupport implements MethodInterceptor {
        private final BeanFactory owner;

        public ReplaceOverrideMethodInterceptor(RootBeanDefinition beanDefinition, BeanFactory owner) {
            super(beanDefinition);
            this.owner = owner;
        }

        @Override // org.springframework.cglib.proxy.MethodInterceptor
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy mp) throws Throwable {
            ReplaceOverride ro = (ReplaceOverride) getBeanDefinition().getMethodOverrides().getOverride(method);
            Assert.state(ro != null, "ReplaceOverride not found");
            MethodReplacer mr = (MethodReplacer) this.owner.getBean(ro.getMethodReplacerBeanName(), MethodReplacer.class);
            return mr.reimplement(obj, method, args);
        }
    }
}

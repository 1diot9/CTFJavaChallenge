package org.springframework.jndi;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import javax.naming.Context;
import javax.naming.NamingException;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jndi/JndiObjectFactoryBean.class */
public class JndiObjectFactoryBean extends JndiObjectLocator implements FactoryBean<Object>, BeanFactoryAware, BeanClassLoaderAware {

    @Nullable
    private Class<?>[] proxyInterfaces;

    @Nullable
    private Object defaultObject;

    @Nullable
    private ConfigurableBeanFactory beanFactory;

    @Nullable
    private Object jndiObject;
    private boolean lookupOnStartup = true;
    private boolean cache = true;
    private boolean exposeAccessContext = false;

    @Nullable
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

    public void setProxyInterface(Class<?> proxyInterface) {
        this.proxyInterfaces = new Class[]{proxyInterface};
    }

    public void setProxyInterfaces(Class<?>... proxyInterfaces) {
        this.proxyInterfaces = proxyInterfaces;
    }

    public void setLookupOnStartup(boolean lookupOnStartup) {
        this.lookupOnStartup = lookupOnStartup;
    }

    public void setCache(boolean cache) {
        this.cache = cache;
    }

    public void setExposeAccessContext(boolean exposeAccessContext) {
        this.exposeAccessContext = exposeAccessContext;
    }

    public void setDefaultObject(Object defaultObject) {
        this.defaultObject = defaultObject;
    }

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) {
        if (beanFactory instanceof ConfigurableBeanFactory) {
            ConfigurableBeanFactory cbf = (ConfigurableBeanFactory) beanFactory;
            this.beanFactory = cbf;
        }
    }

    @Override // org.springframework.beans.factory.BeanClassLoaderAware
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    @Override // org.springframework.jndi.JndiObjectLocator, org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws IllegalArgumentException, NamingException {
        super.afterPropertiesSet();
        if (this.proxyInterfaces != null || !this.lookupOnStartup || !this.cache || this.exposeAccessContext) {
            if (this.defaultObject != null) {
                throw new IllegalArgumentException("'defaultObject' is not supported in combination with 'proxyInterface'");
            }
            this.jndiObject = JndiObjectProxyFactory.createJndiObjectProxy(this);
            return;
        }
        if (this.defaultObject != null && getExpectedType() != null && !getExpectedType().isInstance(this.defaultObject)) {
            TypeConverter converter = this.beanFactory != null ? this.beanFactory.getTypeConverter() : new SimpleTypeConverter();
            try {
                this.defaultObject = converter.convertIfNecessary(this.defaultObject, getExpectedType());
            } catch (TypeMismatchException ex) {
                throw new IllegalArgumentException("Default object [" + this.defaultObject + "] of type [" + this.defaultObject.getClass().getName() + "] is not of expected type [" + getExpectedType().getName() + "] and cannot be converted either", ex);
            }
        }
        this.jndiObject = lookupWithFallback();
    }

    protected Object lookupWithFallback() throws NamingException {
        ClassLoader originalClassLoader = ClassUtils.overrideThreadContextClassLoader(this.beanClassLoader);
        try {
            try {
                Object lookup = lookup();
                if (originalClassLoader != null) {
                    Thread.currentThread().setContextClassLoader(originalClassLoader);
                }
                return lookup;
            } catch (TypeMismatchNamingException e) {
                throw e;
            } catch (NamingException e2) {
                if (this.defaultObject != null) {
                    if (this.logger.isTraceEnabled()) {
                        this.logger.trace("JNDI lookup failed - returning specified default object instead", e2);
                    } else if (this.logger.isDebugEnabled()) {
                        this.logger.debug("JNDI lookup failed - returning specified default object instead: " + e2);
                    }
                    Object obj = this.defaultObject;
                    if (originalClassLoader != null) {
                        Thread.currentThread().setContextClassLoader(originalClassLoader);
                    }
                    return obj;
                }
                throw e2;
            }
        } catch (Throwable th) {
            if (originalClassLoader != null) {
                Thread.currentThread().setContextClassLoader(originalClassLoader);
            }
            throw th;
        }
    }

    @Override // org.springframework.beans.factory.FactoryBean
    @Nullable
    public Object getObject() {
        return this.jndiObject;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public Class<?> getObjectType() {
        if (this.proxyInterfaces != null) {
            if (this.proxyInterfaces.length == 1) {
                return this.proxyInterfaces[0];
            }
            if (this.proxyInterfaces.length > 1) {
                return createCompositeInterface(this.proxyInterfaces);
            }
        }
        if (this.jndiObject != null) {
            return this.jndiObject.getClass();
        }
        return getExpectedType();
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public boolean isSingleton() {
        return true;
    }

    protected Class<?> createCompositeInterface(Class<?>[] interfaces) {
        return ClassUtils.createCompositeInterface(interfaces, this.beanClassLoader);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jndi/JndiObjectFactoryBean$JndiObjectProxyFactory.class */
    private static class JndiObjectProxyFactory {
        private JndiObjectProxyFactory() {
        }

        private static Object createJndiObjectProxy(JndiObjectFactoryBean jof) throws NamingException {
            JndiObjectTargetSource targetSource = new JndiObjectTargetSource();
            targetSource.setJndiTemplate(jof.getJndiTemplate());
            String jndiName = jof.getJndiName();
            Assert.state(jndiName != null, "No JNDI name specified");
            targetSource.setJndiName(jndiName);
            targetSource.setExpectedType(jof.getExpectedType());
            targetSource.setResourceRef(jof.isResourceRef());
            targetSource.setLookupOnStartup(jof.lookupOnStartup);
            targetSource.setCache(jof.cache);
            targetSource.afterPropertiesSet();
            ProxyFactory proxyFactory = new ProxyFactory();
            if (jof.proxyInterfaces != null) {
                proxyFactory.setInterfaces(jof.proxyInterfaces);
            } else {
                Class<?> targetClass = targetSource.getTargetClass();
                if (targetClass == null) {
                    throw new IllegalStateException("Cannot deactivate 'lookupOnStartup' without specifying a 'proxyInterface' or 'expectedType'");
                }
                Class<?>[] ifcs = ClassUtils.getAllInterfacesForClass(targetClass, jof.beanClassLoader);
                for (Class<?> ifc : ifcs) {
                    if (Modifier.isPublic(ifc.getModifiers())) {
                        proxyFactory.addInterface(ifc);
                    }
                }
            }
            if (jof.exposeAccessContext) {
                proxyFactory.addAdvice(new JndiContextExposingInterceptor(jof.getJndiTemplate()));
            }
            proxyFactory.setTargetSource(targetSource);
            return proxyFactory.getProxy(jof.beanClassLoader);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jndi/JndiObjectFactoryBean$JndiContextExposingInterceptor.class */
    public static class JndiContextExposingInterceptor implements MethodInterceptor {
        private final JndiTemplate jndiTemplate;

        public JndiContextExposingInterceptor(JndiTemplate jndiTemplate) {
            this.jndiTemplate = jndiTemplate;
        }

        @Override // org.aopalliance.intercept.MethodInterceptor
        @Nullable
        public Object invoke(MethodInvocation invocation) throws Throwable {
            Context ctx = isEligible(invocation.getMethod()) ? this.jndiTemplate.getContext() : null;
            try {
                Object proceed = invocation.proceed();
                this.jndiTemplate.releaseContext(ctx);
                return proceed;
            } catch (Throwable th) {
                this.jndiTemplate.releaseContext(ctx);
                throw th;
            }
        }

        protected boolean isEligible(Method method) {
            return Object.class != method.getDeclaringClass();
        }
    }
}

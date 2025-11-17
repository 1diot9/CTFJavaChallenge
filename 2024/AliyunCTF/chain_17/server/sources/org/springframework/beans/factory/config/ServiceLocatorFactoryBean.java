package org.springframework.beans.factory.config;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Properties;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/config/ServiceLocatorFactoryBean.class */
public class ServiceLocatorFactoryBean implements FactoryBean<Object>, BeanFactoryAware, InitializingBean {

    @Nullable
    private Class<?> serviceLocatorInterface;

    @Nullable
    private Constructor<Exception> serviceLocatorExceptionConstructor;

    @Nullable
    private Properties serviceMappings;

    @Nullable
    private ListableBeanFactory beanFactory;

    @Nullable
    private Object proxy;

    public void setServiceLocatorInterface(Class<?> interfaceType) {
        this.serviceLocatorInterface = interfaceType;
    }

    public void setServiceLocatorExceptionClass(Class<? extends Exception> serviceLocatorExceptionClass) {
        this.serviceLocatorExceptionConstructor = determineServiceLocatorExceptionConstructor(serviceLocatorExceptionClass);
    }

    public void setServiceMappings(Properties serviceMappings) {
        this.serviceMappings = serviceMappings;
    }

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (!(beanFactory instanceof ListableBeanFactory)) {
            throw new FatalBeanException("ServiceLocatorFactoryBean needs to run in a BeanFactory that is a ListableBeanFactory");
        }
        ListableBeanFactory lbf = (ListableBeanFactory) beanFactory;
        this.beanFactory = lbf;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        if (this.serviceLocatorInterface == null) {
            throw new IllegalArgumentException("Property 'serviceLocatorInterface' is required");
        }
        this.proxy = Proxy.newProxyInstance(this.serviceLocatorInterface.getClassLoader(), new Class[]{this.serviceLocatorInterface}, new ServiceLocatorInvocationHandler());
    }

    protected Constructor<Exception> determineServiceLocatorExceptionConstructor(Class<? extends Exception> exceptionClass) {
        try {
            return exceptionClass.getConstructor(String.class, Throwable.class);
        } catch (NoSuchMethodException e) {
            try {
                return exceptionClass.getConstructor(Throwable.class);
            } catch (NoSuchMethodException e2) {
                try {
                    return exceptionClass.getConstructor(String.class);
                } catch (NoSuchMethodException e3) {
                    throw new IllegalArgumentException("Service locator exception [" + exceptionClass.getName() + "] neither has a (String, Throwable) constructor nor a (String) constructor");
                }
            }
        }
    }

    protected Exception createServiceLocatorException(Constructor<Exception> exceptionConstructor, BeansException cause) {
        Class<?>[] paramTypes = exceptionConstructor.getParameterTypes();
        Object[] args = new Object[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i++) {
            if (String.class == paramTypes[i]) {
                args[i] = cause.getMessage();
            } else if (paramTypes[i].isInstance(cause)) {
                args[i] = cause;
            }
        }
        return (Exception) BeanUtils.instantiateClass(exceptionConstructor, args);
    }

    @Override // org.springframework.beans.factory.FactoryBean
    @Nullable
    public Object getObject() {
        return this.proxy;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public Class<?> getObjectType() {
        return this.serviceLocatorInterface;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public boolean isSingleton() {
        return true;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/config/ServiceLocatorFactoryBean$ServiceLocatorInvocationHandler.class */
    private class ServiceLocatorInvocationHandler implements InvocationHandler {
        private ServiceLocatorInvocationHandler() {
        }

        @Override // java.lang.reflect.InvocationHandler
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (ReflectionUtils.isEqualsMethod(method)) {
                return Boolean.valueOf(proxy == args[0]);
            }
            if (ReflectionUtils.isHashCodeMethod(method)) {
                return Integer.valueOf(System.identityHashCode(proxy));
            }
            if (ReflectionUtils.isToStringMethod(method)) {
                return "Service locator: " + ServiceLocatorFactoryBean.this.serviceLocatorInterface;
            }
            return invokeServiceLocatorMethod(method, args);
        }

        private Object invokeServiceLocatorMethod(Method method, Object[] args) throws Exception {
            Class<?> serviceLocatorMethodReturnType = getServiceLocatorMethodReturnType(method);
            try {
                String beanName = tryGetBeanName(args);
                Assert.state(ServiceLocatorFactoryBean.this.beanFactory != null, "No BeanFactory available");
                if (StringUtils.hasLength(beanName)) {
                    return ServiceLocatorFactoryBean.this.beanFactory.getBean(beanName, serviceLocatorMethodReturnType);
                }
                return ServiceLocatorFactoryBean.this.beanFactory.getBean(serviceLocatorMethodReturnType);
            } catch (BeansException ex) {
                if (ServiceLocatorFactoryBean.this.serviceLocatorExceptionConstructor != null) {
                    throw ServiceLocatorFactoryBean.this.createServiceLocatorException(ServiceLocatorFactoryBean.this.serviceLocatorExceptionConstructor, ex);
                }
                throw ex;
            }
        }

        private String tryGetBeanName(@Nullable Object[] args) {
            String mappedName;
            String beanName = "";
            if (args != null && args.length == 1 && args[0] != null) {
                beanName = args[0].toString();
            }
            if (ServiceLocatorFactoryBean.this.serviceMappings != null && (mappedName = ServiceLocatorFactoryBean.this.serviceMappings.getProperty(beanName)) != null) {
                beanName = mappedName;
            }
            return beanName;
        }

        private Class<?> getServiceLocatorMethodReturnType(Method method) throws NoSuchMethodException {
            Assert.state(ServiceLocatorFactoryBean.this.serviceLocatorInterface != null, "No service locator interface specified");
            Class<?>[] paramTypes = method.getParameterTypes();
            Method interfaceMethod = ServiceLocatorFactoryBean.this.serviceLocatorInterface.getMethod(method.getName(), paramTypes);
            Class<?> serviceLocatorReturnType = interfaceMethod.getReturnType();
            if (paramTypes.length > 1 || Void.TYPE == serviceLocatorReturnType) {
                throw new UnsupportedOperationException("May only call methods with signature '<type> xxx()' or '<type> xxx(<idtype> id)' on factory interface, but tried to call: " + interfaceMethod);
            }
            return serviceLocatorReturnType;
        }
    }
}

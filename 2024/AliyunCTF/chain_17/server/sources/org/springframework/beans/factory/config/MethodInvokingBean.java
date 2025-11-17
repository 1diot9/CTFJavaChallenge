package org.springframework.beans.factory.config;

import java.lang.reflect.InvocationTargetException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.support.ArgumentConvertingMethodInvoker;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/config/MethodInvokingBean.class */
public class MethodInvokingBean extends ArgumentConvertingMethodInvoker implements BeanClassLoaderAware, BeanFactoryAware, InitializingBean {

    @Nullable
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

    @Nullable
    private ConfigurableBeanFactory beanFactory;

    @Override // org.springframework.beans.factory.BeanClassLoaderAware
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    @Override // org.springframework.util.MethodInvoker
    protected Class<?> resolveClassName(String className) throws ClassNotFoundException {
        return ClassUtils.forName(className, this.beanClassLoader);
    }

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) {
        if (beanFactory instanceof ConfigurableBeanFactory) {
            ConfigurableBeanFactory cbf = (ConfigurableBeanFactory) beanFactory;
            this.beanFactory = cbf;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.beans.support.ArgumentConvertingMethodInvoker
    public TypeConverter getDefaultTypeConverter() {
        if (this.beanFactory != null) {
            return this.beanFactory.getTypeConverter();
        }
        return super.getDefaultTypeConverter();
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws Exception {
        prepare();
        invokeWithTargetException();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public Object invokeWithTargetException() throws Exception {
        try {
            return invoke();
        } catch (InvocationTargetException ex) {
            Throwable targetException = ex.getTargetException();
            if (targetException instanceof Exception) {
                Exception exception = (Exception) targetException;
                throw exception;
            }
            Throwable targetException2 = ex.getTargetException();
            if (targetException2 instanceof Error) {
                Error error = (Error) targetException2;
                throw error;
            }
            throw ex;
        }
    }
}

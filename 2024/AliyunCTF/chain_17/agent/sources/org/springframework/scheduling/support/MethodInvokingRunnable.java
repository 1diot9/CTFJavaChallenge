package org.springframework.scheduling.support;

import java.lang.reflect.InvocationTargetException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.support.ArgumentConvertingMethodInvoker;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/support/MethodInvokingRunnable.class */
public class MethodInvokingRunnable extends ArgumentConvertingMethodInvoker implements Runnable, BeanClassLoaderAware, InitializingBean {
    protected final Log logger = LogFactory.getLog(getClass());

    @Nullable
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

    @Override // org.springframework.beans.factory.BeanClassLoaderAware
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    @Override // org.springframework.util.MethodInvoker
    protected Class<?> resolveClassName(String className) throws ClassNotFoundException {
        return ClassUtils.forName(className, this.beanClassLoader);
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws ClassNotFoundException, NoSuchMethodException {
        prepare();
    }

    @Override // java.lang.Runnable
    public void run() {
        try {
            invoke();
        } catch (InvocationTargetException ex) {
            this.logger.error(getInvocationFailureMessage(), ex.getTargetException());
        } catch (Throwable ex2) {
            this.logger.error(getInvocationFailureMessage(), ex2);
        }
    }

    protected String getInvocationFailureMessage() {
        return "Invocation of method '" + getTargetMethod() + "' on target class [" + getTargetClass() + "] failed";
    }
}

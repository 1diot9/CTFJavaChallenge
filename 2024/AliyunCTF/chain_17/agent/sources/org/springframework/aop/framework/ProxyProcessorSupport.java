package org.springframework.aop.framework;

import java.io.Closeable;
import org.springframework.beans.factory.Aware;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/framework/ProxyProcessorSupport.class */
public class ProxyProcessorSupport extends ProxyConfig implements Ordered, BeanClassLoaderAware, AopInfrastructureBean {
    private int order = Integer.MAX_VALUE;

    @Nullable
    private ClassLoader proxyClassLoader = ClassUtils.getDefaultClassLoader();
    private boolean classLoaderConfigured = false;

    public void setOrder(int order) {
        this.order = order;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return this.order;
    }

    public void setProxyClassLoader(@Nullable ClassLoader classLoader) {
        this.proxyClassLoader = classLoader;
        this.classLoaderConfigured = classLoader != null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public ClassLoader getProxyClassLoader() {
        return this.proxyClassLoader;
    }

    @Override // org.springframework.beans.factory.BeanClassLoaderAware
    public void setBeanClassLoader(ClassLoader classLoader) {
        if (!this.classLoaderConfigured) {
            this.proxyClassLoader = classLoader;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void evaluateProxyInterfaces(Class<?> beanClass, ProxyFactory proxyFactory) {
        Class<?>[] targetInterfaces = ClassUtils.getAllInterfacesForClass(beanClass, getProxyClassLoader());
        boolean hasReasonableProxyInterface = false;
        int length = targetInterfaces.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            Class<?> ifc = targetInterfaces[i];
            if (isConfigurationCallbackInterface(ifc) || isInternalLanguageInterface(ifc) || ifc.getMethods().length <= 0) {
                i++;
            } else {
                hasReasonableProxyInterface = true;
                break;
            }
        }
        if (hasReasonableProxyInterface) {
            for (Class<?> cls : targetInterfaces) {
                proxyFactory.addInterface(cls);
            }
            return;
        }
        proxyFactory.setProxyTargetClass(true);
    }

    protected boolean isConfigurationCallbackInterface(Class<?> ifc) {
        return InitializingBean.class == ifc || DisposableBean.class == ifc || Closeable.class == ifc || AutoCloseable.class == ifc || ObjectUtils.containsElement(ifc.getInterfaces(), Aware.class);
    }

    protected boolean isInternalLanguageInterface(Class<?> ifc) {
        return ifc.getName().equals("groovy.lang.GroovyObject") || ifc.getName().endsWith(".cglib.proxy.Factory") || ifc.getName().endsWith(".bytebuddy.MockAccess");
    }
}

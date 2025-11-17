package org.springframework.aop.framework;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.Interceptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.Advisor;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.adapter.AdvisorAdapterRegistry;
import org.springframework.aop.framework.adapter.GlobalAdvisorAdapterRegistry;
import org.springframework.aop.framework.adapter.UnknownAdviceTypeException;
import org.springframework.aop.target.SingletonTargetSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.FactoryBeanNotInitializedException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/framework/ProxyFactoryBean.class */
public class ProxyFactoryBean extends ProxyCreatorSupport implements FactoryBean<Object>, BeanClassLoaderAware, BeanFactoryAware {
    public static final String GLOBAL_SUFFIX = "*";

    @Nullable
    private String[] interceptorNames;

    @Nullable
    private String targetName;

    @Nullable
    private transient BeanFactory beanFactory;

    @Nullable
    private Object singletonInstance;
    protected final Log logger = LogFactory.getLog(getClass());
    private boolean autodetectInterfaces = true;
    private boolean singleton = true;
    private AdvisorAdapterRegistry advisorAdapterRegistry = GlobalAdvisorAdapterRegistry.getInstance();
    private boolean freezeProxy = false;

    @Nullable
    private transient ClassLoader proxyClassLoader = ClassUtils.getDefaultClassLoader();
    private transient boolean classLoaderConfigured = false;
    private boolean advisorChainInitialized = false;

    public void setProxyInterfaces(Class<?>[] proxyInterfaces) throws ClassNotFoundException {
        setInterfaces(proxyInterfaces);
    }

    public void setInterceptorNames(String... interceptorNames) {
        this.interceptorNames = interceptorNames;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public void setAutodetectInterfaces(boolean autodetectInterfaces) {
        this.autodetectInterfaces = autodetectInterfaces;
    }

    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    public void setAdvisorAdapterRegistry(AdvisorAdapterRegistry advisorAdapterRegistry) {
        this.advisorAdapterRegistry = advisorAdapterRegistry;
    }

    @Override // org.springframework.aop.framework.ProxyConfig
    public void setFrozen(boolean frozen) {
        this.freezeProxy = frozen;
    }

    public void setProxyClassLoader(@Nullable ClassLoader classLoader) {
        this.proxyClassLoader = classLoader;
        this.classLoaderConfigured = classLoader != null;
    }

    @Override // org.springframework.beans.factory.BeanClassLoaderAware
    public void setBeanClassLoader(ClassLoader classLoader) {
        if (!this.classLoaderConfigured) {
            this.proxyClassLoader = classLoader;
        }
    }

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        checkInterceptorNames();
    }

    @Override // org.springframework.beans.factory.FactoryBean
    @Nullable
    public Object getObject() throws BeansException {
        initializeAdvisorChain();
        if (isSingleton()) {
            return getSingletonInstance();
        }
        if (this.targetName == null) {
            this.logger.info("Using non-singleton proxies with singleton targets is often undesirable. Enable prototype proxies by setting the 'targetName' property.");
        }
        return newPrototypeInstance();
    }

    @Override // org.springframework.beans.factory.FactoryBean
    @Nullable
    public Class<?> getObjectType() {
        synchronized (this) {
            if (this.singletonInstance != null) {
                return this.singletonInstance.getClass();
            }
            try {
                return createAopProxy().getProxyClass(this.proxyClassLoader);
            } catch (AopConfigException ex) {
                if (getTargetClass() == null) {
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug("Failed to determine early proxy class: " + ex.getMessage());
                        return null;
                    }
                    return null;
                }
                throw ex;
            }
        }
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public boolean isSingleton() {
        return this.singleton;
    }

    private synchronized Object getSingletonInstance() {
        if (this.singletonInstance == null) {
            this.targetSource = freshTargetSource();
            if (this.autodetectInterfaces && getProxiedInterfaces().length == 0 && !isProxyTargetClass()) {
                Class<?> targetClass = getTargetClass();
                if (targetClass == null) {
                    throw new FactoryBeanNotInitializedException("Cannot determine target class for proxy");
                }
                setInterfaces(ClassUtils.getAllInterfacesForClass(targetClass, this.proxyClassLoader));
            }
            super.setFrozen(this.freezeProxy);
            this.singletonInstance = getProxy(createAopProxy());
        }
        return this.singletonInstance;
    }

    private synchronized Object newPrototypeInstance() {
        Class<?> targetClass;
        ProxyCreatorSupport copy = new ProxyCreatorSupport(getAopProxyFactory());
        TargetSource targetSource = freshTargetSource();
        copy.copyConfigurationFrom(this, targetSource, freshAdvisorChain());
        if (this.autodetectInterfaces && getProxiedInterfaces().length == 0 && !isProxyTargetClass() && (targetClass = targetSource.getTargetClass()) != null) {
            copy.setInterfaces(ClassUtils.getAllInterfacesForClass(targetClass, this.proxyClassLoader));
        }
        copy.setFrozen(this.freezeProxy);
        return getProxy(copy.createAopProxy());
    }

    protected Object getProxy(AopProxy aopProxy) {
        return aopProxy.getProxy(this.proxyClassLoader);
    }

    private void checkInterceptorNames() {
        if (!ObjectUtils.isEmpty((Object[]) this.interceptorNames)) {
            String finalName = this.interceptorNames[this.interceptorNames.length - 1];
            if (this.targetName == null && this.targetSource == EMPTY_TARGET_SOURCE && !finalName.endsWith("*") && !isNamedBeanAnAdvisorOrAdvice(finalName)) {
                this.targetName = finalName;
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Bean with name '" + finalName + "' concluding interceptor chain is not an advisor class: treating it as a target or TargetSource");
                }
                this.interceptorNames = (String[]) Arrays.copyOf(this.interceptorNames, this.interceptorNames.length - 1);
            }
        }
    }

    private boolean isNamedBeanAnAdvisorOrAdvice(String beanName) {
        Assert.state(this.beanFactory != null, "No BeanFactory set");
        Class<?> namedBeanClass = this.beanFactory.getType(beanName);
        if (namedBeanClass != null) {
            return Advisor.class.isAssignableFrom(namedBeanClass) || Advice.class.isAssignableFrom(namedBeanClass);
        }
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Could not determine type of bean with name '" + beanName + "' - assuming it is neither an Advisor nor an Advice");
            return false;
        }
        return false;
    }

    private synchronized void initializeAdvisorChain() throws AopConfigException, BeansException {
        Object advice;
        if (!this.advisorChainInitialized && !ObjectUtils.isEmpty((Object[]) this.interceptorNames)) {
            if (this.beanFactory == null) {
                throw new IllegalStateException("No BeanFactory available anymore (probably due to serialization) - cannot resolve interceptor names " + Arrays.toString(this.interceptorNames));
            }
            if (this.interceptorNames[this.interceptorNames.length - 1].endsWith("*") && this.targetName == null && this.targetSource == EMPTY_TARGET_SOURCE) {
                throw new AopConfigException("Target required after globals");
            }
            for (String name : this.interceptorNames) {
                if (name.endsWith("*")) {
                    BeanFactory beanFactory = this.beanFactory;
                    if (!(beanFactory instanceof ListableBeanFactory)) {
                        throw new AopConfigException("Can only use global advisors or interceptors with a ListableBeanFactory");
                    }
                    ListableBeanFactory lbf = (ListableBeanFactory) beanFactory;
                    addGlobalAdvisors(lbf, name.substring(0, name.length() - "*".length()));
                } else {
                    if (this.singleton || this.beanFactory.isSingleton(name)) {
                        advice = this.beanFactory.getBean(name);
                    } else {
                        advice = new PrototypePlaceholderAdvisor(name);
                    }
                    addAdvisorOnChainCreation(advice);
                }
            }
            this.advisorChainInitialized = true;
        }
    }

    private List<Advisor> freshAdvisorChain() {
        Advisor[] advisors = getAdvisors();
        List<Advisor> freshAdvisors = new ArrayList<>(advisors.length);
        for (Advisor advisor : advisors) {
            if (advisor instanceof PrototypePlaceholderAdvisor) {
                PrototypePlaceholderAdvisor ppa = (PrototypePlaceholderAdvisor) advisor;
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Refreshing bean named '" + ppa.getBeanName() + "'");
                }
                if (this.beanFactory == null) {
                    throw new IllegalStateException("No BeanFactory available anymore (probably due to serialization) - cannot resolve prototype advisor '" + ppa.getBeanName() + "'");
                }
                Object bean = this.beanFactory.getBean(ppa.getBeanName());
                Advisor refreshedAdvisor = namedBeanToAdvisor(bean);
                freshAdvisors.add(refreshedAdvisor);
            } else {
                freshAdvisors.add(advisor);
            }
        }
        return freshAdvisors;
    }

    private void addGlobalAdvisors(ListableBeanFactory beanFactory, String prefix) {
        String[] globalAdvisorNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(beanFactory, (Class<?>) Advisor.class);
        String[] globalInterceptorNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(beanFactory, (Class<?>) Interceptor.class);
        if (globalAdvisorNames.length > 0 || globalInterceptorNames.length > 0) {
            List<Object> beans = new ArrayList<>(globalAdvisorNames.length + globalInterceptorNames.length);
            for (String name : globalAdvisorNames) {
                if (name.startsWith(prefix)) {
                    beans.add(beanFactory.getBean(name));
                }
            }
            for (String name2 : globalInterceptorNames) {
                if (name2.startsWith(prefix)) {
                    beans.add(beanFactory.getBean(name2));
                }
            }
            AnnotationAwareOrderComparator.sort((List<?>) beans);
            for (Object bean : beans) {
                addAdvisorOnChainCreation(bean);
            }
        }
    }

    private void addAdvisorOnChainCreation(Object next) {
        addAdvisor(namedBeanToAdvisor(next));
    }

    private TargetSource freshTargetSource() {
        if (this.targetName == null) {
            return this.targetSource;
        }
        if (this.beanFactory == null) {
            throw new IllegalStateException("No BeanFactory available anymore (probably due to serialization) - cannot resolve target with name '" + this.targetName + "'");
        }
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Refreshing target with name '" + this.targetName + "'");
        }
        Object target = this.beanFactory.getBean(this.targetName);
        if (!(target instanceof TargetSource)) {
            return new SingletonTargetSource(target);
        }
        TargetSource targetSource = (TargetSource) target;
        return targetSource;
    }

    private Advisor namedBeanToAdvisor(Object next) {
        try {
            return this.advisorAdapterRegistry.wrap(next);
        } catch (UnknownAdviceTypeException ex) {
            throw new AopConfigException("Unknown advisor type " + next.getClass() + "; can only include Advisor or Advice type beans in interceptorNames chain except for last entry which may also be target instance or TargetSource", ex);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.aop.framework.ProxyCreatorSupport, org.springframework.aop.framework.AdvisedSupport
    public void adviceChanged() {
        super.adviceChanged();
        if (this.singleton) {
            this.logger.debug("Advice has changed; re-caching singleton instance");
            synchronized (this) {
                this.singletonInstance = null;
            }
        }
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        this.proxyClassLoader = ClassUtils.getDefaultClassLoader();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/framework/ProxyFactoryBean$PrototypePlaceholderAdvisor.class */
    public static class PrototypePlaceholderAdvisor implements Advisor, Serializable {
        private final String beanName;
        private final String message;

        public PrototypePlaceholderAdvisor(String beanName) {
            this.beanName = beanName;
            this.message = "Placeholder for prototype Advisor/Advice with bean name '" + beanName + "'";
        }

        public String getBeanName() {
            return this.beanName;
        }

        @Override // org.springframework.aop.Advisor
        public Advice getAdvice() {
            throw new UnsupportedOperationException("Cannot invoke methods: " + this.message);
        }

        public String toString() {
            return this.message;
        }
    }
}

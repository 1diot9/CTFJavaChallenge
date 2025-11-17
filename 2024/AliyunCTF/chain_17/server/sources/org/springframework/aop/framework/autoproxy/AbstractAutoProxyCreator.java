package org.springframework.aop.framework.autoproxy;

import java.lang.reflect.Constructor;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.aopalliance.aop.Advice;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.Advisor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.framework.ProxyProcessorSupport;
import org.springframework.aop.framework.adapter.AdvisorAdapterRegistry;
import org.springframework.aop.framework.adapter.GlobalAdvisorAdapterRegistry;
import org.springframework.aop.target.EmptyTargetSource;
import org.springframework.aop.target.SingletonTargetSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import org.springframework.core.SmartClassLoader;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/framework/autoproxy/AbstractAutoProxyCreator.class */
public abstract class AbstractAutoProxyCreator extends ProxyProcessorSupport implements SmartInstantiationAwareBeanPostProcessor, BeanFactoryAware {

    @Nullable
    protected static final Object[] DO_NOT_PROXY = null;
    protected static final Object[] PROXY_WITHOUT_ADDITIONAL_INTERCEPTORS = new Object[0];

    @Nullable
    private TargetSourceCreator[] customTargetSourceCreators;

    @Nullable
    private BeanFactory beanFactory;
    protected final Log logger = LogFactory.getLog(getClass());
    private AdvisorAdapterRegistry advisorAdapterRegistry = GlobalAdvisorAdapterRegistry.getInstance();
    private boolean freezeProxy = false;
    private String[] interceptorNames = new String[0];
    private boolean applyCommonInterceptorsFirst = true;
    private final Set<String> targetSourcedBeans = Collections.newSetFromMap(new ConcurrentHashMap(16));
    private final Map<Object, Object> earlyBeanReferences = new ConcurrentHashMap(16);
    private final Map<Object, Class<?>> proxyTypes = new ConcurrentHashMap(16);
    private final Map<Object, Boolean> advisedBeans = new ConcurrentHashMap(256);

    @Nullable
    protected abstract Object[] getAdvicesAndAdvisorsForBean(Class<?> beanClass, String beanName, @Nullable TargetSource customTargetSource) throws BeansException;

    @Override // org.springframework.aop.framework.ProxyConfig
    public void setFrozen(boolean frozen) {
        this.freezeProxy = frozen;
    }

    @Override // org.springframework.aop.framework.ProxyConfig
    public boolean isFrozen() {
        return this.freezeProxy;
    }

    public void setAdvisorAdapterRegistry(AdvisorAdapterRegistry advisorAdapterRegistry) {
        this.advisorAdapterRegistry = advisorAdapterRegistry;
    }

    public void setCustomTargetSourceCreators(TargetSourceCreator... targetSourceCreators) {
        this.customTargetSourceCreators = targetSourceCreators;
    }

    public void setInterceptorNames(String... interceptorNames) {
        this.interceptorNames = interceptorNames;
    }

    public void setApplyCommonInterceptorsFirst(boolean applyCommonInterceptorsFirst) {
        this.applyCommonInterceptorsFirst = applyCommonInterceptorsFirst;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public BeanFactory getBeanFactory() {
        return this.beanFactory;
    }

    @Override // org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor
    @Nullable
    public Class<?> predictBeanType(Class<?> beanClass, String beanName) {
        if (this.proxyTypes.isEmpty()) {
            return null;
        }
        Object cacheKey = getCacheKey(beanClass, beanName);
        return this.proxyTypes.get(cacheKey);
    }

    @Override // org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor
    public Class<?> determineBeanType(Class<?> beanClass, String beanName) {
        Object cacheKey = getCacheKey(beanClass, beanName);
        Class<?> proxyType = this.proxyTypes.get(cacheKey);
        if (proxyType == null) {
            TargetSource targetSource = getCustomTargetSource(beanClass, beanName);
            if (targetSource != null) {
                if (StringUtils.hasLength(beanName)) {
                    this.targetSourcedBeans.add(beanName);
                }
            } else {
                targetSource = EmptyTargetSource.forClass(beanClass);
            }
            Object[] specificInterceptors = getAdvicesAndAdvisorsForBean(beanClass, beanName, targetSource);
            if (specificInterceptors != DO_NOT_PROXY) {
                this.advisedBeans.put(cacheKey, Boolean.TRUE);
                proxyType = createProxyClass(beanClass, beanName, specificInterceptors, targetSource);
                this.proxyTypes.put(cacheKey, proxyType);
            }
        }
        return proxyType != null ? proxyType : beanClass;
    }

    @Override // org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor
    @Nullable
    public Constructor<?>[] determineCandidateConstructors(Class<?> beanClass, String beanName) {
        return null;
    }

    @Override // org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor
    public Object getEarlyBeanReference(Object bean, String beanName) {
        Object cacheKey = getCacheKey(bean.getClass(), beanName);
        this.earlyBeanReferences.put(cacheKey, bean);
        return wrapIfNecessary(bean, beanName, cacheKey);
    }

    @Override // org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) {
        Object cacheKey = getCacheKey(beanClass, beanName);
        if (!StringUtils.hasLength(beanName) || !this.targetSourcedBeans.contains(beanName)) {
            if (this.advisedBeans.containsKey(cacheKey)) {
                return null;
            }
            if (isInfrastructureClass(beanClass) || shouldSkip(beanClass, beanName)) {
                this.advisedBeans.put(cacheKey, Boolean.FALSE);
                return null;
            }
        }
        TargetSource targetSource = getCustomTargetSource(beanClass, beanName);
        if (targetSource != null) {
            if (StringUtils.hasLength(beanName)) {
                this.targetSourcedBeans.add(beanName);
            }
            Object[] specificInterceptors = getAdvicesAndAdvisorsForBean(beanClass, beanName, targetSource);
            Object proxy = createProxy(beanClass, beanName, specificInterceptors, targetSource);
            this.proxyTypes.put(cacheKey, proxy.getClass());
            return proxy;
        }
        return null;
    }

    @Override // org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor
    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) {
        return pvs;
    }

    @Override // org.springframework.beans.factory.config.BeanPostProcessor
    public Object postProcessAfterInitialization(@Nullable Object bean, String beanName) {
        if (bean != null) {
            Object cacheKey = getCacheKey(bean.getClass(), beanName);
            if (this.earlyBeanReferences.remove(cacheKey) != bean) {
                return wrapIfNecessary(bean, beanName, cacheKey);
            }
        }
        return bean;
    }

    protected Object getCacheKey(Class<?> beanClass, @Nullable String beanName) {
        if (StringUtils.hasLength(beanName)) {
            return FactoryBean.class.isAssignableFrom(beanClass) ? "&" + beanName : beanName;
        }
        return beanClass;
    }

    protected Object wrapIfNecessary(Object bean, String beanName, Object cacheKey) {
        if (StringUtils.hasLength(beanName) && this.targetSourcedBeans.contains(beanName)) {
            return bean;
        }
        if (Boolean.FALSE.equals(this.advisedBeans.get(cacheKey))) {
            return bean;
        }
        if (isInfrastructureClass(bean.getClass()) || shouldSkip(bean.getClass(), beanName)) {
            this.advisedBeans.put(cacheKey, Boolean.FALSE);
            return bean;
        }
        Object[] specificInterceptors = getAdvicesAndAdvisorsForBean(bean.getClass(), beanName, null);
        if (specificInterceptors != DO_NOT_PROXY) {
            this.advisedBeans.put(cacheKey, Boolean.TRUE);
            Object proxy = createProxy(bean.getClass(), beanName, specificInterceptors, new SingletonTargetSource(bean));
            this.proxyTypes.put(cacheKey, proxy.getClass());
            return proxy;
        }
        this.advisedBeans.put(cacheKey, Boolean.FALSE);
        return bean;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isInfrastructureClass(Class<?> beanClass) {
        boolean retVal = Advice.class.isAssignableFrom(beanClass) || Pointcut.class.isAssignableFrom(beanClass) || Advisor.class.isAssignableFrom(beanClass) || AopInfrastructureBean.class.isAssignableFrom(beanClass);
        if (retVal && this.logger.isTraceEnabled()) {
            this.logger.trace("Did not attempt to auto-proxy infrastructure class [" + beanClass.getName() + "]");
        }
        return retVal;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean shouldSkip(Class<?> beanClass, String beanName) {
        return AutoProxyUtils.isOriginalInstance(beanName, beanClass);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public TargetSource getCustomTargetSource(Class<?> beanClass, String beanName) {
        if (this.customTargetSourceCreators != null && this.beanFactory != null && this.beanFactory.containsBean(beanName)) {
            for (TargetSourceCreator tsc : this.customTargetSourceCreators) {
                TargetSource ts = tsc.getTargetSource(beanClass, beanName);
                if (ts != null) {
                    if (this.logger.isTraceEnabled()) {
                        this.logger.trace("TargetSourceCreator [" + tsc + "] found custom TargetSource for bean with name '" + beanName + "'");
                    }
                    return ts;
                }
            }
            return null;
        }
        return null;
    }

    protected Object createProxy(Class<?> beanClass, @Nullable String beanName, @Nullable Object[] specificInterceptors, TargetSource targetSource) {
        return buildProxy(beanClass, beanName, specificInterceptors, targetSource, false);
    }

    private Class<?> createProxyClass(Class<?> beanClass, @Nullable String beanName, @Nullable Object[] specificInterceptors, TargetSource targetSource) {
        return (Class) buildProxy(beanClass, beanName, specificInterceptors, targetSource, true);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r12v0 */
    private Object buildProxy(Class<?> cls, @Nullable String str, @Nullable Object[] objArr, TargetSource targetSource, boolean z) {
        BeanFactory beanFactory = this.beanFactory;
        if (beanFactory instanceof ConfigurableListableBeanFactory) {
            AutoProxyUtils.exposeTargetClass((ConfigurableListableBeanFactory) beanFactory, str, cls);
        }
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.copyFrom(this);
        if (proxyFactory.isProxyTargetClass()) {
            if (Proxy.isProxyClass(cls) || ClassUtils.isLambdaClass(cls)) {
                for (Class<?> cls2 : cls.getInterfaces()) {
                    proxyFactory.addInterface(cls2);
                }
            }
        } else if (shouldProxyTargetClass(cls, str)) {
            proxyFactory.setProxyTargetClass(true);
        } else {
            evaluateProxyInterfaces(cls, proxyFactory);
        }
        proxyFactory.addAdvisors(buildAdvisors(str, objArr));
        proxyFactory.setTargetSource(targetSource);
        customizeProxyFactory(proxyFactory);
        proxyFactory.setFrozen(this.freezeProxy);
        if (advisorsPreFiltered()) {
            proxyFactory.setPreFiltered(true);
        }
        ?? proxyClassLoader = getProxyClassLoader();
        boolean z2 = proxyClassLoader instanceof SmartClassLoader;
        ClassLoader classLoader = proxyClassLoader;
        if (z2) {
            SmartClassLoader smartClassLoader = (SmartClassLoader) proxyClassLoader;
            classLoader = proxyClassLoader;
            if (proxyClassLoader != cls.getClassLoader()) {
                classLoader = smartClassLoader.getOriginalClassLoader();
            }
        }
        return z ? proxyFactory.getProxyClass(classLoader) : proxyFactory.getProxy(classLoader);
    }

    protected boolean shouldProxyTargetClass(Class<?> beanClass, @Nullable String beanName) {
        BeanFactory beanFactory = this.beanFactory;
        if (beanFactory instanceof ConfigurableListableBeanFactory) {
            ConfigurableListableBeanFactory clbf = (ConfigurableListableBeanFactory) beanFactory;
            if (AutoProxyUtils.shouldProxyTargetClass(clbf, beanName)) {
                return true;
            }
        }
        return false;
    }

    protected boolean advisorsPreFiltered() {
        return false;
    }

    protected Advisor[] buildAdvisors(@Nullable String beanName, @Nullable Object[] specificInterceptors) {
        Advisor[] commonInterceptors = resolveInterceptorNames();
        List<Object> allInterceptors = new ArrayList<>();
        if (specificInterceptors != null) {
            if (specificInterceptors.length > 0) {
                allInterceptors.addAll(Arrays.asList(specificInterceptors));
            }
            if (commonInterceptors.length > 0) {
                if (this.applyCommonInterceptorsFirst) {
                    allInterceptors.addAll(0, Arrays.asList(commonInterceptors));
                } else {
                    allInterceptors.addAll(Arrays.asList(commonInterceptors));
                }
            }
        }
        if (this.logger.isTraceEnabled()) {
            int nrOfCommonInterceptors = commonInterceptors.length;
            int nrOfSpecificInterceptors = specificInterceptors != null ? specificInterceptors.length : 0;
            this.logger.trace("Creating implicit proxy for bean '" + beanName + "' with " + nrOfCommonInterceptors + " common interceptors and " + nrOfSpecificInterceptors + " specific interceptors");
        }
        Advisor[] advisors = new Advisor[allInterceptors.size()];
        for (int i = 0; i < allInterceptors.size(); i++) {
            advisors[i] = this.advisorAdapterRegistry.wrap(allInterceptors.get(i));
        }
        return advisors;
    }

    private Advisor[] resolveInterceptorNames() {
        ConfigurableBeanFactory configurableBeanFactory;
        BeanFactory bf = this.beanFactory;
        if (bf instanceof ConfigurableBeanFactory) {
            ConfigurableBeanFactory _cbf = (ConfigurableBeanFactory) bf;
            configurableBeanFactory = _cbf;
        } else {
            configurableBeanFactory = null;
        }
        ConfigurableBeanFactory cbf = configurableBeanFactory;
        List<Advisor> advisors = new ArrayList<>();
        for (String beanName : this.interceptorNames) {
            if (cbf == null || !cbf.isCurrentlyInCreation(beanName)) {
                Assert.state(bf != null, "BeanFactory required for resolving interceptor names");
                Object next = bf.getBean(beanName);
                advisors.add(this.advisorAdapterRegistry.wrap(next));
            }
        }
        return (Advisor[]) advisors.toArray(new Advisor[0]);
    }

    protected void customizeProxyFactory(ProxyFactory proxyFactory) {
    }
}

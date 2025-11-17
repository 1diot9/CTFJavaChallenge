package org.springframework.scripting.support;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;
import org.springframework.asm.Type;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanCurrentlyInCreationException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionValidationException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.cglib.core.Signature;
import org.springframework.cglib.proxy.InterfaceMaker;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.Conventions;
import org.springframework.core.Ordered;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.Nullable;
import org.springframework.scripting.ScriptFactory;
import org.springframework.scripting.ScriptSource;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scripting/support/ScriptFactoryPostProcessor.class */
public class ScriptFactoryPostProcessor implements SmartInstantiationAwareBeanPostProcessor, BeanClassLoaderAware, BeanFactoryAware, ResourceLoaderAware, DisposableBean, Ordered {
    public static final String INLINE_SCRIPT_PREFIX = "inline:";
    public static final String REFRESH_CHECK_DELAY_ATTRIBUTE = Conventions.getQualifiedAttributeName(ScriptFactoryPostProcessor.class, "refreshCheckDelay");
    public static final String PROXY_TARGET_CLASS_ATTRIBUTE = Conventions.getQualifiedAttributeName(ScriptFactoryPostProcessor.class, "proxyTargetClass");
    public static final String LANGUAGE_ATTRIBUTE = Conventions.getQualifiedAttributeName(ScriptFactoryPostProcessor.class, "language");
    private static final String SCRIPT_FACTORY_NAME_PREFIX = "scriptFactory.";
    private static final String SCRIPTED_OBJECT_NAME_PREFIX = "scriptedObject.";

    @Nullable
    private ConfigurableBeanFactory beanFactory;
    protected final Log logger = LogFactory.getLog(getClass());
    private long defaultRefreshCheckDelay = -1;
    private boolean defaultProxyTargetClass = false;

    @Nullable
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();
    private ResourceLoader resourceLoader = new DefaultResourceLoader();
    final DefaultListableBeanFactory scriptBeanFactory = new DefaultListableBeanFactory();
    private final Map<String, ScriptSource> scriptSourceCache = new ConcurrentHashMap();

    public void setDefaultRefreshCheckDelay(long defaultRefreshCheckDelay) {
        this.defaultRefreshCheckDelay = defaultRefreshCheckDelay;
    }

    public void setDefaultProxyTargetClass(boolean defaultProxyTargetClass) {
        this.defaultProxyTargetClass = defaultProxyTargetClass;
    }

    @Override // org.springframework.beans.factory.BeanClassLoaderAware
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) {
        if (!(beanFactory instanceof ConfigurableBeanFactory)) {
            throw new IllegalStateException("ScriptFactoryPostProcessor doesn't work with non-ConfigurableBeanFactory: " + beanFactory.getClass());
        }
        ConfigurableBeanFactory cbf = (ConfigurableBeanFactory) beanFactory;
        this.beanFactory = cbf;
        this.scriptBeanFactory.setParentBeanFactory(this.beanFactory);
        this.scriptBeanFactory.copyConfigurationFrom(this.beanFactory);
        this.scriptBeanFactory.getBeanPostProcessors().removeIf(beanPostProcessor -> {
            return beanPostProcessor instanceof AopInfrastructureBean;
        });
    }

    @Override // org.springframework.context.ResourceLoaderAware
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return Integer.MIN_VALUE;
    }

    @Override // org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor
    @Nullable
    public Class<?> predictBeanType(Class<?> beanClass, String beanName) {
        if (!ScriptFactory.class.isAssignableFrom(beanClass)) {
            return null;
        }
        Assert.state(this.beanFactory != null, "No BeanFactory set");
        BeanDefinition bd = this.beanFactory.getMergedBeanDefinition(beanName);
        try {
            String scriptFactoryBeanName = "scriptFactory." + beanName;
            String scriptedObjectBeanName = "scriptedObject." + beanName;
            prepareScriptBeans(bd, scriptFactoryBeanName, scriptedObjectBeanName);
            ScriptFactory scriptFactory = (ScriptFactory) this.scriptBeanFactory.getBean(scriptFactoryBeanName, ScriptFactory.class);
            ScriptSource scriptSource = getScriptSource(scriptFactoryBeanName, scriptFactory.getScriptSourceLocator());
            Class<?>[] interfaces = scriptFactory.getScriptInterfaces();
            Class<?> scriptedType = scriptFactory.getScriptedObjectType(scriptSource);
            if (scriptedType != null) {
                return scriptedType;
            }
            if (!ObjectUtils.isEmpty((Object[]) interfaces)) {
                return interfaces.length == 1 ? interfaces[0] : createCompositeInterface(interfaces);
            }
            if (bd.isSingleton()) {
                return this.scriptBeanFactory.getBean(scriptedObjectBeanName).getClass();
            }
            return null;
        } catch (Exception ex) {
            if (ex instanceof BeanCreationException) {
                BeanCreationException bce = (BeanCreationException) ex;
                if (bce.getMostSpecificCause() instanceof BeanCurrentlyInCreationException) {
                    if (this.logger.isTraceEnabled()) {
                        this.logger.trace("Could not determine scripted object type for bean '" + beanName + "': " + ex.getMessage());
                        return null;
                    }
                    return null;
                }
            }
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Could not determine scripted object type for bean '" + beanName + "'", ex);
                return null;
            }
            return null;
        }
    }

    @Override // org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor
    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) {
        return pvs;
    }

    @Override // org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) {
        if (!ScriptFactory.class.isAssignableFrom(beanClass)) {
            return null;
        }
        Assert.state(this.beanFactory != null, "No BeanFactory set");
        BeanDefinition bd = this.beanFactory.getMergedBeanDefinition(beanName);
        String scriptFactoryBeanName = "scriptFactory." + beanName;
        String scriptedObjectBeanName = "scriptedObject." + beanName;
        prepareScriptBeans(bd, scriptFactoryBeanName, scriptedObjectBeanName);
        ScriptFactory scriptFactory = (ScriptFactory) this.scriptBeanFactory.getBean(scriptFactoryBeanName, ScriptFactory.class);
        ScriptSource scriptSource = getScriptSource(scriptFactoryBeanName, scriptFactory.getScriptSourceLocator());
        boolean isFactoryBean = false;
        try {
            Class<?> scriptedObjectType = scriptFactory.getScriptedObjectType(scriptSource);
            if (scriptedObjectType != null) {
                isFactoryBean = FactoryBean.class.isAssignableFrom(scriptedObjectType);
            }
            long refreshCheckDelay = resolveRefreshCheckDelay(bd);
            if (refreshCheckDelay >= 0) {
                Class<?>[] interfaces = scriptFactory.getScriptInterfaces();
                RefreshableScriptTargetSource ts = new RefreshableScriptTargetSource(this.scriptBeanFactory, scriptedObjectBeanName, scriptFactory, scriptSource, isFactoryBean);
                boolean proxyTargetClass = resolveProxyTargetClass(bd);
                String language = (String) bd.getAttribute(LANGUAGE_ATTRIBUTE);
                if (proxyTargetClass && (language == null || !language.equals("groovy"))) {
                    throw new BeanDefinitionValidationException("Cannot use proxyTargetClass=true with script beans where language is not 'groovy': '" + language + "'");
                }
                ts.setRefreshCheckDelay(refreshCheckDelay);
                return createRefreshableProxy(ts, interfaces, proxyTargetClass);
            }
            if (isFactoryBean) {
                scriptedObjectBeanName = "&" + scriptedObjectBeanName;
            }
            return this.scriptBeanFactory.getBean(scriptedObjectBeanName);
        } catch (Exception ex) {
            throw new BeanCreationException(beanName, "Could not determine scripted object type for " + scriptFactory, ex);
        }
    }

    protected void prepareScriptBeans(BeanDefinition bd, String scriptFactoryBeanName, String scriptedObjectBeanName) {
        synchronized (this.scriptBeanFactory) {
            if (!this.scriptBeanFactory.containsBeanDefinition(scriptedObjectBeanName)) {
                this.scriptBeanFactory.registerBeanDefinition(scriptFactoryBeanName, createScriptFactoryBeanDefinition(bd));
                ScriptFactory scriptFactory = (ScriptFactory) this.scriptBeanFactory.getBean(scriptFactoryBeanName, ScriptFactory.class);
                ScriptSource scriptSource = getScriptSource(scriptFactoryBeanName, scriptFactory.getScriptSourceLocator());
                Class<?>[] interfaces = scriptFactory.getScriptInterfaces();
                Class<?>[] scriptedInterfaces = interfaces;
                if (scriptFactory.requiresConfigInterface() && !bd.getPropertyValues().isEmpty()) {
                    Class<?> configInterface = createConfigInterface(bd, interfaces);
                    scriptedInterfaces = (Class[]) ObjectUtils.addObjectToArray(interfaces, configInterface);
                }
                BeanDefinition objectBd = createScriptedObjectBeanDefinition(bd, scriptFactoryBeanName, scriptSource, scriptedInterfaces);
                long refreshCheckDelay = resolveRefreshCheckDelay(bd);
                if (refreshCheckDelay >= 0) {
                    objectBd.setScope("prototype");
                }
                this.scriptBeanFactory.registerBeanDefinition(scriptedObjectBeanName, objectBd);
            }
        }
    }

    protected long resolveRefreshCheckDelay(BeanDefinition beanDefinition) {
        long refreshCheckDelay = this.defaultRefreshCheckDelay;
        Object attributeValue = beanDefinition.getAttribute(REFRESH_CHECK_DELAY_ATTRIBUTE);
        if (attributeValue instanceof Number) {
            Number number = (Number) attributeValue;
            refreshCheckDelay = number.longValue();
        } else if (attributeValue instanceof String) {
            String text = (String) attributeValue;
            refreshCheckDelay = Long.parseLong(text);
        } else if (attributeValue != null) {
            throw new BeanDefinitionStoreException("Invalid refresh check delay attribute [" + REFRESH_CHECK_DELAY_ATTRIBUTE + "] with value '" + attributeValue + "': needs to be of type Number or String");
        }
        return refreshCheckDelay;
    }

    protected boolean resolveProxyTargetClass(BeanDefinition beanDefinition) {
        boolean proxyTargetClass = this.defaultProxyTargetClass;
        Object attributeValue = beanDefinition.getAttribute(PROXY_TARGET_CLASS_ATTRIBUTE);
        if (attributeValue instanceof Boolean) {
            Boolean boo = (Boolean) attributeValue;
            proxyTargetClass = boo.booleanValue();
        } else if (attributeValue instanceof String) {
            String text = (String) attributeValue;
            proxyTargetClass = Boolean.parseBoolean(text);
        } else if (attributeValue != null) {
            throw new BeanDefinitionStoreException("Invalid proxy target class attribute [" + PROXY_TARGET_CLASS_ATTRIBUTE + "] with value '" + attributeValue + "': needs to be of type Boolean or String");
        }
        return proxyTargetClass;
    }

    protected BeanDefinition createScriptFactoryBeanDefinition(BeanDefinition bd) {
        GenericBeanDefinition scriptBd = new GenericBeanDefinition();
        scriptBd.setBeanClassName(bd.getBeanClassName());
        scriptBd.getConstructorArgumentValues().addArgumentValues(bd.getConstructorArgumentValues());
        return scriptBd;
    }

    protected ScriptSource getScriptSource(String beanName, String scriptSourceLocator) {
        return this.scriptSourceCache.computeIfAbsent(beanName, key -> {
            return convertToScriptSource(beanName, scriptSourceLocator, this.resourceLoader);
        });
    }

    protected ScriptSource convertToScriptSource(String beanName, String scriptSourceLocator, ResourceLoader resourceLoader) {
        if (scriptSourceLocator.startsWith(INLINE_SCRIPT_PREFIX)) {
            return new StaticScriptSource(scriptSourceLocator.substring(INLINE_SCRIPT_PREFIX.length()), beanName);
        }
        return new ResourceScriptSource(resourceLoader.getResource(scriptSourceLocator));
    }

    protected Class<?> createConfigInterface(BeanDefinition bd, @Nullable Class<?>[] interfaces) {
        InterfaceMaker maker = new InterfaceMaker();
        PropertyValue[] pvs = bd.getPropertyValues().getPropertyValues();
        for (PropertyValue pv : pvs) {
            String propertyName = pv.getName();
            Class<?> propertyType = BeanUtils.findPropertyType(propertyName, interfaces);
            String setterName = "set" + StringUtils.capitalize(propertyName);
            Signature signature = new Signature(setterName, Type.VOID_TYPE, new Type[]{Type.getType(propertyType)});
            maker.add(signature, new Type[0]);
        }
        if (StringUtils.hasText(bd.getInitMethodName())) {
            Signature signature2 = new Signature(bd.getInitMethodName(), Type.VOID_TYPE, new Type[0]);
            maker.add(signature2, new Type[0]);
        }
        if (StringUtils.hasText(bd.getDestroyMethodName())) {
            Signature signature3 = new Signature(bd.getDestroyMethodName(), Type.VOID_TYPE, new Type[0]);
            maker.add(signature3, new Type[0]);
        }
        return maker.create();
    }

    protected Class<?> createCompositeInterface(Class<?>[] interfaces) {
        return ClassUtils.createCompositeInterface(interfaces, this.beanClassLoader);
    }

    protected BeanDefinition createScriptedObjectBeanDefinition(BeanDefinition bd, String scriptFactoryBeanName, ScriptSource scriptSource, @Nullable Class<?>[] interfaces) {
        GenericBeanDefinition objectBd = new GenericBeanDefinition(bd);
        objectBd.setFactoryBeanName(scriptFactoryBeanName);
        objectBd.setFactoryMethodName("getScriptedObject");
        objectBd.getConstructorArgumentValues().clear();
        objectBd.getConstructorArgumentValues().addIndexedArgumentValue(0, scriptSource);
        objectBd.getConstructorArgumentValues().addIndexedArgumentValue(1, interfaces);
        return objectBd;
    }

    protected Object createRefreshableProxy(TargetSource ts, @Nullable Class<?>[] interfaces, boolean proxyTargetClass) {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTargetSource(ts);
        ClassLoader classLoader = this.beanClassLoader;
        if (interfaces != null) {
            proxyFactory.setInterfaces(interfaces);
        } else {
            Class<?> targetClass = ts.getTargetClass();
            if (targetClass != null) {
                proxyFactory.setInterfaces(ClassUtils.getAllInterfacesForClass(targetClass, this.beanClassLoader));
            }
        }
        if (proxyTargetClass) {
            classLoader = null;
            proxyFactory.setProxyTargetClass(true);
        }
        DelegatingIntroductionInterceptor introduction = new DelegatingIntroductionInterceptor(ts);
        introduction.suppressInterface(TargetSource.class);
        proxyFactory.addAdvice(introduction);
        return proxyFactory.getProxy(classLoader);
    }

    @Override // org.springframework.beans.factory.DisposableBean
    public void destroy() {
        this.scriptBeanFactory.destroySingletons();
    }
}

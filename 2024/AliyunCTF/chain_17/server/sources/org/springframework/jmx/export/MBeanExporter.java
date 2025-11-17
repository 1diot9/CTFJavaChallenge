package org.springframework.jmx.export;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.management.DynamicMBean;
import javax.management.JMException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.StandardMBean;
import javax.management.modelmbean.ModelMBean;
import javax.management.modelmbean.ModelMBeanInfo;
import javax.management.modelmbean.RequiredModelMBean;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.target.LazyInitTargetSource;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.CannotLoadBeanClassException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.jmx.export.assembler.AutodetectCapableMBeanInfoAssembler;
import org.springframework.jmx.export.assembler.MBeanInfoAssembler;
import org.springframework.jmx.export.assembler.SimpleReflectiveMBeanInfoAssembler;
import org.springframework.jmx.export.naming.KeyNamingStrategy;
import org.springframework.jmx.export.naming.ObjectNamingStrategy;
import org.springframework.jmx.export.naming.SelfNaming;
import org.springframework.jmx.export.notification.ModelMBeanNotificationPublisher;
import org.springframework.jmx.export.notification.NotificationPublisherAware;
import org.springframework.jmx.support.JmxUtils;
import org.springframework.jmx.support.MBeanRegistrationSupport;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jmx/export/MBeanExporter.class */
public class MBeanExporter extends MBeanRegistrationSupport implements MBeanExportOperations, BeanClassLoaderAware, BeanFactoryAware, InitializingBean, SmartInitializingSingleton, DisposableBean {

    @Deprecated(since = "6.1")
    public static final int AUTODETECT_NONE = 0;

    @Deprecated(since = "6.1")
    public static final int AUTODETECT_MBEAN = 1;

    @Deprecated(since = "6.1")
    public static final int AUTODETECT_ASSEMBLER = 2;

    @Deprecated(since = "6.1")
    public static final int AUTODETECT_ALL = 3;
    private static final String WILDCARD = "*";
    private static final String MR_TYPE_OBJECT_REFERENCE = "ObjectReference";
    private static final Map<String, Integer> constants = Map.of("AUTODETECT_NONE", 0, "AUTODETECT_MBEAN", 1, "AUTODETECT_ASSEMBLER", 2, "AUTODETECT_ALL", 3);

    @Nullable
    private Map<String, Object> beans;

    @Nullable
    Integer autodetectMode;

    @Nullable
    private MBeanExporterListener[] listeners;

    @Nullable
    private NotificationListenerBean[] notificationListeners;

    @Nullable
    private ListableBeanFactory beanFactory;
    private boolean allowEagerInit = false;
    private MBeanInfoAssembler assembler = new SimpleReflectiveMBeanInfoAssembler();
    private ObjectNamingStrategy namingStrategy = new KeyNamingStrategy();
    private boolean ensureUniqueRuntimeObjectNames = true;
    private boolean exposeManagedResourceClassLoader = true;
    private final Set<String> excludedBeans = new HashSet();
    private final Map<NotificationListenerBean, ObjectName[]> registeredNotificationListeners = new LinkedHashMap();

    @Nullable
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

    /* JADX INFO: Access modifiers changed from: private */
    @FunctionalInterface
    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jmx/export/MBeanExporter$AutodetectCallback.class */
    public interface AutodetectCallback {
        boolean include(Class<?> beanClass, String beanName);
    }

    public void setBeans(Map<String, Object> beans) {
        this.beans = beans;
    }

    public void setAutodetect(boolean autodetect) {
        this.autodetectMode = Integer.valueOf(autodetect ? 3 : 0);
    }

    @Deprecated(since = "6.1")
    public void setAutodetectModeName(String constantName) {
        Assert.hasText(constantName, "'constantName' must not be null or blank");
        Integer mode = constants.get(constantName);
        Assert.notNull(mode, "Only autodetect constants allowed");
        this.autodetectMode = mode;
    }

    @Deprecated(since = "6.1")
    public void setAutodetectMode(int autodetectMode) {
        Assert.isTrue(constants.containsValue(Integer.valueOf(autodetectMode)), "Only values of autodetect constants allowed");
        this.autodetectMode = Integer.valueOf(autodetectMode);
    }

    public void setAllowEagerInit(boolean allowEagerInit) {
        this.allowEagerInit = allowEagerInit;
    }

    public void setAssembler(MBeanInfoAssembler assembler) {
        this.assembler = assembler;
    }

    public void setNamingStrategy(ObjectNamingStrategy namingStrategy) {
        this.namingStrategy = namingStrategy;
    }

    public void setEnsureUniqueRuntimeObjectNames(boolean ensureUniqueRuntimeObjectNames) {
        this.ensureUniqueRuntimeObjectNames = ensureUniqueRuntimeObjectNames;
    }

    public void setExposeManagedResourceClassLoader(boolean exposeManagedResourceClassLoader) {
        this.exposeManagedResourceClassLoader = exposeManagedResourceClassLoader;
    }

    public void setExcludedBeans(String... excludedBeans) {
        this.excludedBeans.clear();
        Collections.addAll(this.excludedBeans, excludedBeans);
    }

    public void addExcludedBean(String excludedBean) {
        Assert.notNull(excludedBean, "ExcludedBean must not be null");
        this.excludedBeans.add(excludedBean);
    }

    public void setListeners(MBeanExporterListener... listeners) {
        this.listeners = listeners;
    }

    public void setNotificationListeners(NotificationListenerBean... notificationListeners) {
        this.notificationListeners = notificationListeners;
    }

    public void setNotificationListenerMappings(Map<?, ? extends NotificationListener> listeners) {
        Assert.notNull(listeners, "'listeners' must not be null");
        List<NotificationListenerBean> notificationListeners = new ArrayList<>(listeners.size());
        listeners.forEach((key, listener) -> {
            NotificationListenerBean bean = new NotificationListenerBean(listener);
            if (key != null && !"*".equals(key)) {
                bean.setMappedObjectName(key);
            }
            notificationListeners.add(bean);
        });
        this.notificationListeners = (NotificationListenerBean[]) notificationListeners.toArray(new NotificationListenerBean[0]);
    }

    @Override // org.springframework.beans.factory.BeanClassLoaderAware
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) {
        if (beanFactory instanceof ListableBeanFactory) {
            ListableBeanFactory lbf = (ListableBeanFactory) beanFactory;
            this.beanFactory = lbf;
        } else {
            this.logger.debug("MBeanExporter not running in a ListableBeanFactory: auto-detection of MBeans not available.");
        }
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        if (this.server == null) {
            this.server = JmxUtils.locateMBeanServer();
        }
    }

    @Override // org.springframework.beans.factory.SmartInitializingSingleton
    public void afterSingletonsInstantiated() {
        try {
            this.logger.debug("Registering beans for JMX exposure on startup");
            registerBeans();
            registerNotificationListeners();
        } catch (RuntimeException ex) {
            unregisterNotificationListeners();
            unregisterBeans();
            throw ex;
        }
    }

    @Override // org.springframework.beans.factory.DisposableBean
    public void destroy() {
        this.logger.debug("Unregistering JMX-exposed beans on shutdown");
        unregisterNotificationListeners();
        unregisterBeans();
    }

    @Override // org.springframework.jmx.export.MBeanExportOperations
    public ObjectName registerManagedResource(Object managedResource) throws MBeanExportException {
        Assert.notNull(managedResource, "Managed resource must not be null");
        try {
            ObjectName objectName = getObjectName(managedResource, null);
            if (this.ensureUniqueRuntimeObjectNames) {
                objectName = JmxUtils.appendIdentityToObjectName(objectName, managedResource);
            }
            registerManagedResource(managedResource, objectName);
            return objectName;
        } catch (Throwable ex) {
            throw new MBeanExportException("Unable to generate ObjectName for MBean [" + managedResource + "]", ex);
        }
    }

    @Override // org.springframework.jmx.export.MBeanExportOperations
    public void registerManagedResource(Object managedResource, ObjectName objectName) throws MBeanExportException {
        Assert.notNull(managedResource, "Managed resource must not be null");
        Assert.notNull(objectName, "ObjectName must not be null");
        try {
            if (isMBean(managedResource.getClass())) {
                doRegister(managedResource, objectName);
            } else {
                ModelMBean mbean = createAndConfigureMBean(managedResource, managedResource.getClass().getName());
                doRegister(mbean, objectName);
                injectNotificationPublisherIfNecessary(managedResource, mbean, objectName);
            }
        } catch (JMException ex) {
            throw new UnableToRegisterMBeanException("Unable to register MBean [" + managedResource + "] with object name [" + objectName + "]", ex);
        }
    }

    @Override // org.springframework.jmx.export.MBeanExportOperations
    public void unregisterManagedResource(ObjectName objectName) {
        Assert.notNull(objectName, "ObjectName must not be null");
        doUnregister(objectName);
    }

    protected void registerBeans() {
        if (this.beans == null) {
            this.beans = new HashMap();
            if (this.autodetectMode == null) {
                this.autodetectMode = 3;
            }
        }
        int mode = this.autodetectMode != null ? this.autodetectMode.intValue() : 0;
        if (mode != 0) {
            if (this.beanFactory == null) {
                throw new MBeanExportException("Cannot autodetect MBeans if not running in a BeanFactory");
            }
            if (mode == 1 || mode == 3) {
                this.logger.debug("Auto-detecting user-defined JMX MBeans");
                autodetect(this.beans, (beanClass, beanName) -> {
                    return isMBean(beanClass);
                });
            }
            if (mode == 2 || mode == 3) {
                MBeanInfoAssembler mBeanInfoAssembler = this.assembler;
                if (mBeanInfoAssembler instanceof AutodetectCapableMBeanInfoAssembler) {
                    AutodetectCapableMBeanInfoAssembler autodetectCapableAssembler = (AutodetectCapableMBeanInfoAssembler) mBeanInfoAssembler;
                    Map<String, Object> map = this.beans;
                    Objects.requireNonNull(autodetectCapableAssembler);
                    autodetect(map, autodetectCapableAssembler::includeBean);
                }
            }
        }
        if (!this.beans.isEmpty()) {
            this.beans.forEach((beanName2, instance) -> {
                registerBeanNameOrInstance(instance, beanName2);
            });
        }
    }

    protected boolean isBeanDefinitionLazyInit(ListableBeanFactory beanFactory, String beanName) {
        if (beanFactory instanceof ConfigurableListableBeanFactory) {
            ConfigurableListableBeanFactory clbf = (ConfigurableListableBeanFactory) beanFactory;
            if (beanFactory.containsBeanDefinition(beanName) && clbf.getBeanDefinition(beanName).isLazyInit()) {
                return true;
            }
        }
        return false;
    }

    protected ObjectName registerBeanNameOrInstance(Object mapValue, String beanKey) throws MBeanExportException {
        try {
            if (mapValue instanceof String) {
                String beanName = (String) mapValue;
                if (this.beanFactory == null) {
                    throw new MBeanExportException("Cannot resolve bean names if not running in a BeanFactory");
                }
                if (isBeanDefinitionLazyInit(this.beanFactory, beanName)) {
                    ObjectName objectName = registerLazyInit(beanName, beanKey);
                    replaceNotificationListenerBeanNameKeysIfNecessary(beanName, objectName);
                    return objectName;
                }
                Object bean = this.beanFactory.getBean(beanName);
                ObjectName objectName2 = registerBeanInstance(bean, beanKey);
                replaceNotificationListenerBeanNameKeysIfNecessary(beanName, objectName2);
                return objectName2;
            }
            if (this.beanFactory != null) {
                Map<String, ?> beansOfSameType = this.beanFactory.getBeansOfType(mapValue.getClass(), false, this.allowEagerInit);
                for (Map.Entry<String, ?> entry : beansOfSameType.entrySet()) {
                    if (entry.getValue() == mapValue) {
                        String beanName2 = entry.getKey();
                        ObjectName objectName3 = registerBeanInstance(mapValue, beanKey);
                        replaceNotificationListenerBeanNameKeysIfNecessary(beanName2, objectName3);
                        return objectName3;
                    }
                }
            }
            return registerBeanInstance(mapValue, beanKey);
        } catch (Throwable ex) {
            throw new UnableToRegisterMBeanException("Unable to register MBean [" + mapValue + "] with key '" + beanKey + "'", ex);
        }
    }

    private void replaceNotificationListenerBeanNameKeysIfNecessary(String beanName, ObjectName objectName) {
        if (this.notificationListeners != null) {
            for (NotificationListenerBean notificationListener : this.notificationListeners) {
                notificationListener.replaceObjectName(beanName, objectName);
            }
        }
    }

    private ObjectName registerBeanInstance(Object bean, String beanKey) throws JMException {
        ObjectName objectName = getObjectName(bean, beanKey);
        Object mbeanToExpose = null;
        if (isMBean(bean.getClass())) {
            mbeanToExpose = bean;
        } else {
            DynamicMBean adaptedBean = adaptMBeanIfPossible(bean);
            if (adaptedBean != null) {
                mbeanToExpose = adaptedBean;
            }
        }
        if (mbeanToExpose != null) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Located MBean '" + beanKey + "': registering with JMX server as MBean [" + objectName + "]");
            }
            doRegister(mbeanToExpose, objectName);
        } else {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Located managed bean '" + beanKey + "': registering with JMX server as MBean [" + objectName + "]");
            }
            ModelMBean mbean = createAndConfigureMBean(bean, beanKey);
            doRegister(mbean, objectName);
            injectNotificationPublisherIfNecessary(bean, mbean, objectName);
        }
        return objectName;
    }

    private ObjectName registerLazyInit(String beanName, String beanKey) throws JMException {
        Assert.state(this.beanFactory != null, "No BeanFactory set");
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setProxyTargetClass(true);
        proxyFactory.setFrozen(true);
        if (isMBean(this.beanFactory.getType(beanName))) {
            LazyInitTargetSource targetSource = new LazyInitTargetSource();
            targetSource.setTargetBeanName(beanName);
            targetSource.setBeanFactory(this.beanFactory);
            proxyFactory.setTargetSource(targetSource);
            Object proxy = proxyFactory.getProxy(this.beanClassLoader);
            ObjectName objectName = getObjectName(proxy, beanKey);
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Located MBean '" + beanKey + "': registering with JMX server as lazy-init MBean [" + objectName + "]");
            }
            doRegister(proxy, objectName);
            return objectName;
        }
        NotificationPublisherAwareLazyTargetSource targetSource2 = new NotificationPublisherAwareLazyTargetSource();
        targetSource2.setTargetBeanName(beanName);
        targetSource2.setBeanFactory(this.beanFactory);
        proxyFactory.setTargetSource(targetSource2);
        Object proxy2 = proxyFactory.getProxy(this.beanClassLoader);
        ObjectName objectName2 = getObjectName(proxy2, beanKey);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Located simple bean '" + beanKey + "': registering with JMX server as lazy-init MBean [" + objectName2 + "]");
        }
        ModelMBean mbean = createAndConfigureMBean(proxy2, beanKey);
        targetSource2.setModelMBean(mbean);
        targetSource2.setObjectName(objectName2);
        doRegister(mbean, objectName2);
        return objectName2;
    }

    protected ObjectName getObjectName(Object bean, @Nullable String beanKey) throws MalformedObjectNameException {
        if (bean instanceof SelfNaming) {
            SelfNaming selfNaming = (SelfNaming) bean;
            return selfNaming.getObjectName();
        }
        return this.namingStrategy.getObjectName(bean, beanKey);
    }

    protected boolean isMBean(@Nullable Class<?> beanClass) {
        return JmxUtils.isMBean(beanClass);
    }

    @Nullable
    protected DynamicMBean adaptMBeanIfPossible(Object bean) throws JMException {
        Class<?> targetClass = AopUtils.getTargetClass(bean);
        if (targetClass != bean.getClass()) {
            Class<?> ifc = JmxUtils.getMXBeanInterface(targetClass);
            if (ifc != null) {
                if (!ifc.isInstance(bean)) {
                    throw new NotCompliantMBeanException("Managed bean [" + bean + "] has a target class with an MXBean interface but does not expose it in the proxy");
                }
                return new StandardMBean(bean, ifc, true);
            }
            Class<?> ifc2 = JmxUtils.getMBeanInterface(targetClass);
            if (ifc2 != null) {
                if (!ifc2.isInstance(bean)) {
                    throw new NotCompliantMBeanException("Managed bean [" + bean + "] has a target class with an MBean interface but does not expose it in the proxy");
                }
                return new StandardMBean(bean, ifc2);
            }
            return null;
        }
        return null;
    }

    protected ModelMBean createAndConfigureMBean(Object managedResource, String beanKey) throws MBeanExportException {
        try {
            ModelMBean mbean = createModelMBean();
            mbean.setModelMBeanInfo(getMBeanInfo(managedResource, beanKey));
            mbean.setManagedResource(managedResource, MR_TYPE_OBJECT_REFERENCE);
            return mbean;
        } catch (Throwable ex) {
            throw new MBeanExportException("Could not create ModelMBean for managed resource [" + managedResource + "] with key '" + beanKey + "'", ex);
        }
    }

    protected ModelMBean createModelMBean() throws MBeanException {
        return this.exposeManagedResourceClassLoader ? new SpringModelMBean() : new RequiredModelMBean();
    }

    private ModelMBeanInfo getMBeanInfo(Object managedBean, String beanKey) throws JMException {
        ModelMBeanInfo info = this.assembler.getMBeanInfo(managedBean, beanKey);
        if (this.logger.isInfoEnabled() && ObjectUtils.isEmpty((Object[]) info.getAttributes()) && ObjectUtils.isEmpty((Object[]) info.getOperations())) {
            this.logger.info("Bean with key '" + beanKey + "' has been registered as an MBean but has no exposed attributes or operations");
        }
        return info;
    }

    private void autodetect(Map<String, Object> beans, AutodetectCallback callback) {
        Assert.state(this.beanFactory != null, "No BeanFactory set");
        Set<String> beanNames = new LinkedHashSet<>(this.beanFactory.getBeanDefinitionCount());
        Collections.addAll(beanNames, this.beanFactory.getBeanDefinitionNames());
        ListableBeanFactory listableBeanFactory = this.beanFactory;
        if (listableBeanFactory instanceof ConfigurableBeanFactory) {
            ConfigurableBeanFactory cbf = (ConfigurableBeanFactory) listableBeanFactory;
            Collections.addAll(beanNames, cbf.getSingletonNames());
        }
        for (String beanName : beanNames) {
            if (!isExcluded(beanName) && !isBeanDefinitionAbstract(this.beanFactory, beanName)) {
                try {
                    Class<?> beanClass = this.beanFactory.getType(beanName);
                    if (beanClass != null && callback.include(beanClass, beanName)) {
                        boolean lazyInit = isBeanDefinitionLazyInit(this.beanFactory, beanName);
                        Object beanInstance = null;
                        if (!lazyInit) {
                            beanInstance = this.beanFactory.getBean(beanName);
                            if (!beanClass.isInstance(beanInstance)) {
                            }
                        }
                        if (!ScopedProxyUtils.isScopedTarget(beanName) && !beans.containsValue(beanName) && (beanInstance == null || !CollectionUtils.containsInstance(beans.values(), beanInstance))) {
                            beans.put(beanName, beanInstance != null ? beanInstance : beanName);
                            if (this.logger.isDebugEnabled()) {
                                this.logger.debug("Bean with name '" + beanName + "' has been autodetected for JMX exposure");
                            }
                        } else if (this.logger.isTraceEnabled()) {
                            this.logger.trace("Bean with name '" + beanName + "' is already registered for JMX exposure");
                        }
                    }
                } catch (CannotLoadBeanClassException ex) {
                    if (this.allowEagerInit) {
                        throw ex;
                    }
                }
            }
        }
    }

    private boolean isExcluded(String beanName) {
        return this.excludedBeans.contains(beanName) || (beanName.startsWith(BeanFactory.FACTORY_BEAN_PREFIX) && this.excludedBeans.contains(beanName.substring(BeanFactory.FACTORY_BEAN_PREFIX.length())));
    }

    private boolean isBeanDefinitionAbstract(ListableBeanFactory beanFactory, String beanName) {
        if (beanFactory instanceof ConfigurableListableBeanFactory) {
            ConfigurableListableBeanFactory clbf = (ConfigurableListableBeanFactory) beanFactory;
            if (beanFactory.containsBeanDefinition(beanName) && clbf.getBeanDefinition(beanName).isAbstract()) {
                return true;
            }
        }
        return false;
    }

    private void injectNotificationPublisherIfNecessary(Object managedResource, @Nullable ModelMBean modelMBean, @Nullable ObjectName objectName) {
        if (managedResource instanceof NotificationPublisherAware) {
            NotificationPublisherAware npa = (NotificationPublisherAware) managedResource;
            if (modelMBean != null && objectName != null) {
                npa.setNotificationPublisher(new ModelMBeanNotificationPublisher(modelMBean, objectName, managedResource));
            }
        }
    }

    private void registerNotificationListeners() throws MBeanExportException {
        if (this.notificationListeners != null) {
            Assert.state(this.server != null, "No MBeanServer available");
            for (NotificationListenerBean bean : this.notificationListeners) {
                try {
                    ObjectName[] mappedObjectNames = bean.getResolvedObjectNames();
                    if (mappedObjectNames == null) {
                        mappedObjectNames = getRegisteredObjectNames();
                    }
                    if (this.registeredNotificationListeners.put(bean, mappedObjectNames) == null) {
                        for (ObjectName mappedObjectName : mappedObjectNames) {
                            this.server.addNotificationListener(mappedObjectName, bean.getNotificationListener(), bean.getNotificationFilter(), bean.getHandback());
                        }
                    }
                } catch (Throwable ex) {
                    throw new MBeanExportException("Unable to register NotificationListener", ex);
                }
            }
        }
    }

    private void unregisterNotificationListeners() {
        if (this.server != null) {
            this.registeredNotificationListeners.forEach((bean, mappedObjectNames) -> {
                for (ObjectName mappedObjectName : mappedObjectNames) {
                    try {
                        this.server.removeNotificationListener(mappedObjectName, bean.getNotificationListener(), bean.getNotificationFilter(), bean.getHandback());
                    } catch (Throwable ex) {
                        if (this.logger.isDebugEnabled()) {
                            this.logger.debug("Unable to unregister NotificationListener", ex);
                        }
                    }
                }
            });
        }
        this.registeredNotificationListeners.clear();
    }

    @Override // org.springframework.jmx.support.MBeanRegistrationSupport
    protected void onRegister(ObjectName objectName) {
        notifyListenersOfRegistration(objectName);
    }

    @Override // org.springframework.jmx.support.MBeanRegistrationSupport
    protected void onUnregister(ObjectName objectName) {
        notifyListenersOfUnregistration(objectName);
    }

    private void notifyListenersOfRegistration(ObjectName objectName) {
        if (this.listeners != null) {
            for (MBeanExporterListener listener : this.listeners) {
                listener.mbeanRegistered(objectName);
            }
        }
    }

    private void notifyListenersOfUnregistration(ObjectName objectName) {
        if (this.listeners != null) {
            for (MBeanExporterListener listener : this.listeners) {
                listener.mbeanUnregistered(objectName);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jmx/export/MBeanExporter$NotificationPublisherAwareLazyTargetSource.class */
    public class NotificationPublisherAwareLazyTargetSource extends LazyInitTargetSource {

        @Nullable
        private ModelMBean modelMBean;

        @Nullable
        private ObjectName objectName;

        private NotificationPublisherAwareLazyTargetSource() {
        }

        public void setModelMBean(ModelMBean modelMBean) {
            this.modelMBean = modelMBean;
        }

        public void setObjectName(ObjectName objectName) {
            this.objectName = objectName;
        }

        @Override // org.springframework.aop.target.LazyInitTargetSource, org.springframework.aop.TargetSource
        @Nullable
        public Object getTarget() {
            try {
                return super.getTarget();
            } catch (RuntimeException ex) {
                if (this.logger.isInfoEnabled()) {
                    this.logger.info("Failed to retrieve target for JMX-exposed bean [" + this.objectName + "]: " + ex);
                }
                throw ex;
            }
        }

        @Override // org.springframework.aop.target.LazyInitTargetSource
        protected void postProcessTargetObject(Object targetObject) {
            MBeanExporter.this.injectNotificationPublisherIfNecessary(targetObject, this.modelMBean, this.objectName);
        }
    }
}

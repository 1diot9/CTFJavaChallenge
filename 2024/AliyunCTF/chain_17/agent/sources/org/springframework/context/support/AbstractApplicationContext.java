package org.springframework.context.support;

import java.io.IOException;
import java.lang.Thread;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.CachedIntrospectionResults;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.support.ResourceEditorRegistrar;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ApplicationStartupAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.HierarchicalMessageSource;
import org.springframework.context.LifecycleProcessor;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.context.expression.StandardBeanExpressionResolver;
import org.springframework.context.weaving.LoadTimeWeaverAware;
import org.springframework.context.weaving.LoadTimeWeaverAwareProcessor;
import org.springframework.core.NativeDetector;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.metrics.ApplicationStartup;
import org.springframework.core.metrics.StartupStep;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/support/AbstractApplicationContext.class */
public abstract class AbstractApplicationContext extends DefaultResourceLoader implements ConfigurableApplicationContext {
    public static final String LIFECYCLE_PROCESSOR_BEAN_NAME = "lifecycleProcessor";
    public static final String MESSAGE_SOURCE_BEAN_NAME = "messageSource";
    public static final String APPLICATION_EVENT_MULTICASTER_BEAN_NAME = "applicationEventMulticaster";
    protected final Log logger;
    private String id;
    private String displayName;

    @Nullable
    private ApplicationContext parent;

    @Nullable
    private ConfigurableEnvironment environment;
    private final List<BeanFactoryPostProcessor> beanFactoryPostProcessors;
    private long startupDate;
    private final AtomicBoolean active;
    private final AtomicBoolean closed;
    private final Lock startupShutdownLock;

    @Nullable
    private volatile Thread startupShutdownThread;

    @Nullable
    private Thread shutdownHook;
    private final ResourcePatternResolver resourcePatternResolver;

    @Nullable
    private LifecycleProcessor lifecycleProcessor;

    @Nullable
    private MessageSource messageSource;

    @Nullable
    private ApplicationEventMulticaster applicationEventMulticaster;
    private ApplicationStartup applicationStartup;
    private final Set<ApplicationListener<?>> applicationListeners;

    @Nullable
    private Set<ApplicationListener<?>> earlyApplicationListeners;

    @Nullable
    private Set<ApplicationEvent> earlyApplicationEvents;

    protected abstract void refreshBeanFactory() throws BeansException, IllegalStateException;

    protected abstract void closeBeanFactory();

    @Override // org.springframework.context.ConfigurableApplicationContext
    public abstract ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException;

    static {
        ContextClosedEvent.class.getName();
    }

    public AbstractApplicationContext() {
        this.logger = LogFactory.getLog(getClass());
        this.id = ObjectUtils.identityToString(this);
        this.displayName = ObjectUtils.identityToString(this);
        this.beanFactoryPostProcessors = new ArrayList();
        this.active = new AtomicBoolean();
        this.closed = new AtomicBoolean();
        this.startupShutdownLock = new ReentrantLock();
        this.applicationStartup = ApplicationStartup.DEFAULT;
        this.applicationListeners = new LinkedHashSet();
        this.resourcePatternResolver = getResourcePatternResolver();
    }

    public AbstractApplicationContext(@Nullable ApplicationContext parent) {
        this();
        setParent(parent);
    }

    @Override // org.springframework.context.ConfigurableApplicationContext
    public void setId(String id) {
        this.id = id;
    }

    @Override // org.springframework.context.ApplicationContext
    public String getId() {
        return this.id;
    }

    @Override // org.springframework.context.ApplicationContext
    public String getApplicationName() {
        return "";
    }

    public void setDisplayName(String displayName) {
        Assert.hasLength(displayName, "Display name must not be empty");
        this.displayName = displayName;
    }

    @Override // org.springframework.context.ApplicationContext
    public String getDisplayName() {
        return this.displayName;
    }

    @Override // org.springframework.context.ApplicationContext
    @Nullable
    public ApplicationContext getParent() {
        return this.parent;
    }

    public void setEnvironment(ConfigurableEnvironment environment) {
        this.environment = environment;
    }

    @Override // org.springframework.context.ConfigurableApplicationContext, org.springframework.core.env.EnvironmentCapable
    public ConfigurableEnvironment getEnvironment() {
        if (this.environment == null) {
            this.environment = createEnvironment();
        }
        return this.environment;
    }

    protected ConfigurableEnvironment createEnvironment() {
        return new StandardEnvironment();
    }

    @Override // org.springframework.context.ApplicationContext
    public AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException {
        return getBeanFactory();
    }

    @Override // org.springframework.context.ApplicationContext
    public long getStartupDate() {
        return this.startupDate;
    }

    @Override // org.springframework.context.ApplicationEventPublisher
    public void publishEvent(ApplicationEvent event) {
        publishEvent(event, null);
    }

    @Override // org.springframework.context.ApplicationEventPublisher
    public void publishEvent(Object event) {
        publishEvent(event, null);
    }

    protected void publishEvent(Object event, @Nullable ResolvableType typeHint) {
        ApplicationEvent applicationEvent;
        Assert.notNull(event, "Event must not be null");
        ResolvableType eventType = null;
        if (event instanceof ApplicationEvent) {
            ApplicationEvent applEvent = (ApplicationEvent) event;
            applicationEvent = applEvent;
            eventType = typeHint;
        } else {
            ResolvableType payloadType = null;
            if (typeHint != null && ApplicationEvent.class.isAssignableFrom(typeHint.toClass())) {
                eventType = typeHint;
            } else {
                payloadType = typeHint;
            }
            applicationEvent = new PayloadApplicationEvent(this, event, payloadType);
        }
        if (eventType == null) {
            eventType = ResolvableType.forInstance(applicationEvent);
            if (typeHint == null) {
                typeHint = eventType;
            }
        }
        if (this.earlyApplicationEvents != null) {
            this.earlyApplicationEvents.add(applicationEvent);
        } else if (this.applicationEventMulticaster != null) {
            this.applicationEventMulticaster.multicastEvent(applicationEvent, eventType);
        }
        if (this.parent != null) {
            ApplicationContext applicationContext = this.parent;
            if (applicationContext instanceof AbstractApplicationContext) {
                AbstractApplicationContext abstractApplicationContext = (AbstractApplicationContext) applicationContext;
                abstractApplicationContext.publishEvent(event, typeHint);
            } else {
                this.parent.publishEvent(event);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ApplicationEventMulticaster getApplicationEventMulticaster() throws IllegalStateException {
        if (this.applicationEventMulticaster == null) {
            throw new IllegalStateException("ApplicationEventMulticaster not initialized - call 'refresh' before multicasting events via the context: " + this);
        }
        return this.applicationEventMulticaster;
    }

    @Override // org.springframework.context.ConfigurableApplicationContext
    public void setApplicationStartup(ApplicationStartup applicationStartup) {
        Assert.notNull(applicationStartup, "ApplicationStartup must not be null");
        this.applicationStartup = applicationStartup;
    }

    @Override // org.springframework.context.ConfigurableApplicationContext
    public ApplicationStartup getApplicationStartup() {
        return this.applicationStartup;
    }

    LifecycleProcessor getLifecycleProcessor() throws IllegalStateException {
        if (this.lifecycleProcessor == null) {
            throw new IllegalStateException("LifecycleProcessor not initialized - call 'refresh' before invoking lifecycle methods via the context: " + this);
        }
        return this.lifecycleProcessor;
    }

    protected ResourcePatternResolver getResourcePatternResolver() {
        return new PathMatchingResourcePatternResolver(this);
    }

    @Override // org.springframework.context.ConfigurableApplicationContext
    public void setParent(@Nullable ApplicationContext parent) {
        this.parent = parent;
        if (parent != null) {
            Environment parentEnvironment = parent.getEnvironment();
            if (parentEnvironment instanceof ConfigurableEnvironment) {
                ConfigurableEnvironment configurableEnvironment = (ConfigurableEnvironment) parentEnvironment;
                getEnvironment().merge(configurableEnvironment);
            }
        }
    }

    @Override // org.springframework.context.ConfigurableApplicationContext
    public void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor) {
        Assert.notNull(postProcessor, "BeanFactoryPostProcessor must not be null");
        this.beanFactoryPostProcessors.add(postProcessor);
    }

    public List<BeanFactoryPostProcessor> getBeanFactoryPostProcessors() {
        return this.beanFactoryPostProcessors;
    }

    @Override // org.springframework.context.ConfigurableApplicationContext
    public void addApplicationListener(ApplicationListener<?> listener) {
        Assert.notNull(listener, "ApplicationListener must not be null");
        if (this.applicationEventMulticaster != null) {
            this.applicationEventMulticaster.addApplicationListener(listener);
        }
        this.applicationListeners.add(listener);
    }

    @Override // org.springframework.context.ConfigurableApplicationContext
    public void removeApplicationListener(ApplicationListener<?> listener) {
        Assert.notNull(listener, "ApplicationListener must not be null");
        if (this.applicationEventMulticaster != null) {
            this.applicationEventMulticaster.removeApplicationListener(listener);
        }
        this.applicationListeners.remove(listener);
    }

    public Collection<ApplicationListener<?>> getApplicationListeners() {
        return this.applicationListeners;
    }

    public void refresh() throws BeansException, IllegalStateException {
        this.startupShutdownLock.lock();
        try {
            this.startupShutdownThread = Thread.currentThread();
            StartupStep contextRefresh = this.applicationStartup.start("spring.context.refresh");
            prepareRefresh();
            ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();
            prepareBeanFactory(beanFactory);
            try {
                try {
                    postProcessBeanFactory(beanFactory);
                    StartupStep beanPostProcess = this.applicationStartup.start("spring.context.beans.post-process");
                    invokeBeanFactoryPostProcessors(beanFactory);
                    registerBeanPostProcessors(beanFactory);
                    beanPostProcess.end();
                    initMessageSource();
                    initApplicationEventMulticaster();
                    onRefresh();
                    registerListeners();
                    finishBeanFactoryInitialization(beanFactory);
                    finishRefresh();
                    contextRefresh.end();
                } catch (Throwable th) {
                    contextRefresh.end();
                    throw th;
                }
            } catch (Error | RuntimeException ex) {
                if (this.logger.isWarnEnabled()) {
                    this.logger.warn("Exception encountered during context initialization - cancelling refresh attempt: " + ex);
                }
                destroyBeans();
                cancelRefresh(ex);
                throw ex;
            }
        } finally {
            this.startupShutdownThread = null;
            this.startupShutdownLock.unlock();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void prepareRefresh() {
        this.startupDate = System.currentTimeMillis();
        this.closed.set(false);
        this.active.set(true);
        if (this.logger.isDebugEnabled()) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Refreshing " + this);
            } else {
                this.logger.debug("Refreshing " + getDisplayName());
            }
        }
        initPropertySources();
        getEnvironment().validateRequiredProperties();
        if (this.earlyApplicationListeners == null) {
            this.earlyApplicationListeners = new LinkedHashSet(this.applicationListeners);
        } else {
            this.applicationListeners.clear();
            this.applicationListeners.addAll(this.earlyApplicationListeners);
        }
        this.earlyApplicationEvents = new LinkedHashSet();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void initPropertySources() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ConfigurableListableBeanFactory obtainFreshBeanFactory() {
        refreshBeanFactory();
        return getBeanFactory();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        beanFactory.setBeanClassLoader(getClassLoader());
        beanFactory.setBeanExpressionResolver(new StandardBeanExpressionResolver(beanFactory.getBeanClassLoader()));
        beanFactory.addPropertyEditorRegistrar(new ResourceEditorRegistrar(this, getEnvironment()));
        beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));
        beanFactory.ignoreDependencyInterface(EnvironmentAware.class);
        beanFactory.ignoreDependencyInterface(EmbeddedValueResolverAware.class);
        beanFactory.ignoreDependencyInterface(ResourceLoaderAware.class);
        beanFactory.ignoreDependencyInterface(ApplicationEventPublisherAware.class);
        beanFactory.ignoreDependencyInterface(MessageSourceAware.class);
        beanFactory.ignoreDependencyInterface(ApplicationContextAware.class);
        beanFactory.ignoreDependencyInterface(ApplicationStartupAware.class);
        beanFactory.registerResolvableDependency(BeanFactory.class, beanFactory);
        beanFactory.registerResolvableDependency(ResourceLoader.class, this);
        beanFactory.registerResolvableDependency(ApplicationEventPublisher.class, this);
        beanFactory.registerResolvableDependency(ApplicationContext.class, this);
        beanFactory.addBeanPostProcessor(new ApplicationListenerDetector(this));
        if (!NativeDetector.inNativeImage() && beanFactory.containsBean(ConfigurableApplicationContext.LOAD_TIME_WEAVER_BEAN_NAME)) {
            beanFactory.addBeanPostProcessor(new LoadTimeWeaverAwareProcessor(beanFactory));
            beanFactory.setTempClassLoader(new ContextTypeMatchClassLoader(beanFactory.getBeanClassLoader()));
        }
        if (!beanFactory.containsLocalBean("environment")) {
            beanFactory.registerSingleton("environment", getEnvironment());
        }
        if (!beanFactory.containsLocalBean("systemProperties")) {
            beanFactory.registerSingleton("systemProperties", getEnvironment().getSystemProperties());
        }
        if (!beanFactory.containsLocalBean("systemEnvironment")) {
            beanFactory.registerSingleton("systemEnvironment", getEnvironment().getSystemEnvironment());
        }
        if (!beanFactory.containsLocalBean(ConfigurableApplicationContext.APPLICATION_STARTUP_BEAN_NAME)) {
            beanFactory.registerSingleton(ConfigurableApplicationContext.APPLICATION_STARTUP_BEAN_NAME, getApplicationStartup());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) {
        PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(beanFactory, getBeanFactoryPostProcessors());
        if (!NativeDetector.inNativeImage() && beanFactory.getTempClassLoader() == null && beanFactory.containsBean(ConfigurableApplicationContext.LOAD_TIME_WEAVER_BEAN_NAME)) {
            beanFactory.addBeanPostProcessor(new LoadTimeWeaverAwareProcessor(beanFactory));
            beanFactory.setTempClassLoader(new ContextTypeMatchClassLoader(beanFactory.getBeanClassLoader()));
        }
    }

    protected void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory) {
        PostProcessorRegistrationDelegate.registerBeanPostProcessors(beanFactory, this);
    }

    protected void initMessageSource() {
        ConfigurableListableBeanFactory beanFactory = getBeanFactory();
        if (beanFactory.containsLocalBean(MESSAGE_SOURCE_BEAN_NAME)) {
            this.messageSource = (MessageSource) beanFactory.getBean(MESSAGE_SOURCE_BEAN_NAME, MessageSource.class);
            if (this.parent != null) {
                MessageSource messageSource = this.messageSource;
                if (messageSource instanceof HierarchicalMessageSource) {
                    HierarchicalMessageSource hms = (HierarchicalMessageSource) messageSource;
                    if (hms.getParentMessageSource() == null) {
                        hms.setParentMessageSource(getInternalParentMessageSource());
                    }
                }
            }
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Using MessageSource [" + this.messageSource + "]");
                return;
            }
            return;
        }
        DelegatingMessageSource dms = new DelegatingMessageSource();
        dms.setParentMessageSource(getInternalParentMessageSource());
        this.messageSource = dms;
        beanFactory.registerSingleton(MESSAGE_SOURCE_BEAN_NAME, this.messageSource);
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("No 'messageSource' bean, using [" + this.messageSource + "]");
        }
    }

    protected void initApplicationEventMulticaster() {
        ConfigurableListableBeanFactory beanFactory = getBeanFactory();
        if (beanFactory.containsLocalBean(APPLICATION_EVENT_MULTICASTER_BEAN_NAME)) {
            this.applicationEventMulticaster = (ApplicationEventMulticaster) beanFactory.getBean(APPLICATION_EVENT_MULTICASTER_BEAN_NAME, ApplicationEventMulticaster.class);
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Using ApplicationEventMulticaster [" + this.applicationEventMulticaster + "]");
                return;
            }
            return;
        }
        this.applicationEventMulticaster = new SimpleApplicationEventMulticaster(beanFactory);
        beanFactory.registerSingleton(APPLICATION_EVENT_MULTICASTER_BEAN_NAME, this.applicationEventMulticaster);
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("No 'applicationEventMulticaster' bean, using [" + this.applicationEventMulticaster.getClass().getSimpleName() + "]");
        }
    }

    protected void initLifecycleProcessor() {
        ConfigurableListableBeanFactory beanFactory = getBeanFactory();
        if (beanFactory.containsLocalBean(LIFECYCLE_PROCESSOR_BEAN_NAME)) {
            this.lifecycleProcessor = (LifecycleProcessor) beanFactory.getBean(LIFECYCLE_PROCESSOR_BEAN_NAME, LifecycleProcessor.class);
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Using LifecycleProcessor [" + this.lifecycleProcessor + "]");
                return;
            }
            return;
        }
        DefaultLifecycleProcessor defaultProcessor = new DefaultLifecycleProcessor();
        defaultProcessor.setBeanFactory(beanFactory);
        this.lifecycleProcessor = defaultProcessor;
        beanFactory.registerSingleton(LIFECYCLE_PROCESSOR_BEAN_NAME, this.lifecycleProcessor);
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("No 'lifecycleProcessor' bean, using [" + this.lifecycleProcessor.getClass().getSimpleName() + "]");
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onRefresh() throws BeansException {
    }

    protected void registerListeners() {
        for (ApplicationListener<?> listener : getApplicationListeners()) {
            getApplicationEventMulticaster().addApplicationListener(listener);
        }
        String[] listenerBeanNames = getBeanNamesForType(ApplicationListener.class, true, false);
        for (String listenerBeanName : listenerBeanNames) {
            getApplicationEventMulticaster().addApplicationListenerBean(listenerBeanName);
        }
        Set<ApplicationEvent> earlyEventsToProcess = this.earlyApplicationEvents;
        this.earlyApplicationEvents = null;
        if (!CollectionUtils.isEmpty(earlyEventsToProcess)) {
            for (ApplicationEvent earlyEvent : earlyEventsToProcess) {
                getApplicationEventMulticaster().multicastEvent(earlyEvent);
            }
        }
    }

    protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
        if (beanFactory.containsBean(ConfigurableApplicationContext.CONVERSION_SERVICE_BEAN_NAME) && beanFactory.isTypeMatch(ConfigurableApplicationContext.CONVERSION_SERVICE_BEAN_NAME, ConversionService.class)) {
            beanFactory.setConversionService((ConversionService) beanFactory.getBean(ConfigurableApplicationContext.CONVERSION_SERVICE_BEAN_NAME, ConversionService.class));
        }
        if (!beanFactory.hasEmbeddedValueResolver()) {
            beanFactory.addEmbeddedValueResolver(strVal -> {
                return getEnvironment().resolvePlaceholders(strVal);
            });
        }
        String[] weaverAwareNames = beanFactory.getBeanNamesForType(LoadTimeWeaverAware.class, false, false);
        for (String weaverAwareName : weaverAwareNames) {
            getBean(weaverAwareName);
        }
        beanFactory.setTempClassLoader(null);
        beanFactory.freezeConfiguration();
        beanFactory.preInstantiateSingletons();
    }

    protected void finishRefresh() {
        resetCommonCaches();
        clearResourceCaches();
        initLifecycleProcessor();
        getLifecycleProcessor().onRefresh();
        publishEvent((ApplicationEvent) new ContextRefreshedEvent(this));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void cancelRefresh(Throwable ex) {
        this.active.set(false);
        resetCommonCaches();
    }

    protected void resetCommonCaches() {
        ReflectionUtils.clearCache();
        AnnotationUtils.clearCache();
        ResolvableType.clearCache();
        CachedIntrospectionResults.clearClassLoader(getClassLoader());
    }

    @Override // org.springframework.context.ConfigurableApplicationContext
    public void registerShutdownHook() {
        if (this.shutdownHook == null) {
            this.shutdownHook = new Thread(ConfigurableApplicationContext.SHUTDOWN_HOOK_THREAD_NAME) { // from class: org.springframework.context.support.AbstractApplicationContext.1
                @Override // java.lang.Thread, java.lang.Runnable
                public void run() {
                    if (AbstractApplicationContext.this.isStartupShutdownThreadStuck()) {
                        AbstractApplicationContext.this.active.set(false);
                        return;
                    }
                    AbstractApplicationContext.this.startupShutdownLock.lock();
                    try {
                        AbstractApplicationContext.this.doClose();
                    } finally {
                        AbstractApplicationContext.this.startupShutdownLock.unlock();
                    }
                }
            };
            Runtime.getRuntime().addShutdownHook(this.shutdownHook);
        }
    }

    private boolean isStartupShutdownThreadStuck() {
        Thread activeThread = this.startupShutdownThread;
        if (activeThread != null && activeThread.getState() == Thread.State.WAITING) {
            activeThread.interrupt();
            try {
                Thread.sleep(1L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            if (activeThread.getState() == Thread.State.WAITING) {
                return true;
            }
            return false;
        }
        return false;
    }

    @Override // org.springframework.context.ConfigurableApplicationContext, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        if (isStartupShutdownThreadStuck()) {
            this.active.set(false);
            return;
        }
        this.startupShutdownLock.lock();
        try {
            this.startupShutdownThread = Thread.currentThread();
            doClose();
            if (this.shutdownHook != null) {
                try {
                    Runtime.getRuntime().removeShutdownHook(this.shutdownHook);
                } catch (IllegalStateException e) {
                }
            }
        } finally {
            this.startupShutdownThread = null;
            this.startupShutdownLock.unlock();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void doClose() {
        if (this.active.get() && this.closed.compareAndSet(false, true)) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Closing " + this);
            }
            try {
                publishEvent((ApplicationEvent) new ContextClosedEvent(this));
            } catch (Throwable ex) {
                this.logger.warn("Exception thrown from ApplicationListener handling ContextClosedEvent", ex);
            }
            if (this.lifecycleProcessor != null) {
                try {
                    this.lifecycleProcessor.onClose();
                } catch (Throwable ex2) {
                    this.logger.warn("Exception thrown from LifecycleProcessor on context close", ex2);
                }
            }
            destroyBeans();
            closeBeanFactory();
            onClose();
            resetCommonCaches();
            if (this.earlyApplicationListeners != null) {
                this.applicationListeners.clear();
                this.applicationListeners.addAll(this.earlyApplicationListeners);
            }
            this.applicationEventMulticaster = null;
            this.messageSource = null;
            this.lifecycleProcessor = null;
            this.active.set(false);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void destroyBeans() {
        getBeanFactory().destroySingletons();
    }

    protected void onClose() {
    }

    @Override // org.springframework.context.ConfigurableApplicationContext
    public boolean isActive() {
        return this.active.get();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void assertBeanFactoryActive() {
        if (!this.active.get()) {
            if (this.closed.get()) {
                throw new IllegalStateException(getDisplayName() + " has been closed already");
            }
            throw new IllegalStateException(getDisplayName() + " has not been refreshed yet");
        }
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public Object getBean(String name) throws BeansException {
        assertBeanFactoryActive();
        return getBeanFactory().getBean(name);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public <T> T getBean(String str, Class<T> cls) throws BeansException {
        assertBeanFactoryActive();
        return (T) getBeanFactory().getBean(str, cls);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public Object getBean(String name, Object... args) throws BeansException {
        assertBeanFactoryActive();
        return getBeanFactory().getBean(name, args);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public <T> T getBean(Class<T> cls) throws BeansException {
        assertBeanFactoryActive();
        return (T) getBeanFactory().getBean(cls);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public <T> T getBean(Class<T> cls, Object... objArr) throws BeansException {
        assertBeanFactoryActive();
        return (T) getBeanFactory().getBean(cls, objArr);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType) {
        assertBeanFactoryActive();
        return getBeanFactory().getBeanProvider(requiredType);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public <T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType) {
        assertBeanFactoryActive();
        return getBeanFactory().getBeanProvider(requiredType);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public boolean containsBean(String name) {
        return getBeanFactory().containsBean(name);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        assertBeanFactoryActive();
        return getBeanFactory().isSingleton(name);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
        assertBeanFactoryActive();
        return getBeanFactory().isPrototype(name);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException {
        assertBeanFactoryActive();
        return getBeanFactory().isTypeMatch(name, typeToMatch);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException {
        assertBeanFactoryActive();
        return getBeanFactory().isTypeMatch(name, typeToMatch);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    @Nullable
    public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        assertBeanFactoryActive();
        return getBeanFactory().getType(name);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    @Nullable
    public Class<?> getType(String name, boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException {
        assertBeanFactoryActive();
        return getBeanFactory().getType(name, allowFactoryBeanInit);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public String[] getAliases(String name) {
        return getBeanFactory().getAliases(name);
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory, org.springframework.beans.factory.support.BeanDefinitionRegistry
    public boolean containsBeanDefinition(String beanName) {
        return getBeanFactory().containsBeanDefinition(beanName);
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory, org.springframework.beans.factory.support.BeanDefinitionRegistry
    public int getBeanDefinitionCount() {
        return getBeanFactory().getBeanDefinitionCount();
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory, org.springframework.beans.factory.support.BeanDefinitionRegistry
    public String[] getBeanDefinitionNames() {
        return getBeanFactory().getBeanDefinitionNames();
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType, boolean allowEagerInit) {
        assertBeanFactoryActive();
        return getBeanFactory().getBeanProvider(requiredType, allowEagerInit);
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public <T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType, boolean allowEagerInit) {
        assertBeanFactoryActive();
        return getBeanFactory().getBeanProvider(requiredType, allowEagerInit);
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public String[] getBeanNamesForType(ResolvableType type) {
        assertBeanFactoryActive();
        return getBeanFactory().getBeanNamesForType(type);
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public String[] getBeanNamesForType(ResolvableType type, boolean includeNonSingletons, boolean allowEagerInit) {
        assertBeanFactoryActive();
        return getBeanFactory().getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public String[] getBeanNamesForType(@Nullable Class<?> type) {
        assertBeanFactoryActive();
        return getBeanFactory().getBeanNamesForType(type);
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public String[] getBeanNamesForType(@Nullable Class<?> type, boolean includeNonSingletons, boolean allowEagerInit) {
        assertBeanFactoryActive();
        return getBeanFactory().getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public <T> Map<String, T> getBeansOfType(@Nullable Class<T> type) throws BeansException {
        assertBeanFactoryActive();
        return getBeanFactory().getBeansOfType(type);
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public <T> Map<String, T> getBeansOfType(@Nullable Class<T> type, boolean includeNonSingletons, boolean allowEagerInit) throws BeansException {
        assertBeanFactoryActive();
        return getBeanFactory().getBeansOfType(type, includeNonSingletons, allowEagerInit);
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType) {
        assertBeanFactoryActive();
        return getBeanFactory().getBeanNamesForAnnotation(annotationType);
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) throws BeansException {
        assertBeanFactoryActive();
        return getBeanFactory().getBeansWithAnnotation(annotationType);
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    @Nullable
    public <A extends Annotation> A findAnnotationOnBean(String str, Class<A> cls) throws NoSuchBeanDefinitionException {
        assertBeanFactoryActive();
        return (A) getBeanFactory().findAnnotationOnBean(str, cls);
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    @Nullable
    public <A extends Annotation> A findAnnotationOnBean(String str, Class<A> cls, boolean z) throws NoSuchBeanDefinitionException {
        assertBeanFactoryActive();
        return (A) getBeanFactory().findAnnotationOnBean(str, cls, z);
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public <A extends Annotation> Set<A> findAllAnnotationsOnBean(String beanName, Class<A> annotationType, boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException {
        assertBeanFactoryActive();
        return getBeanFactory().findAllAnnotationsOnBean(beanName, annotationType, allowFactoryBeanInit);
    }

    @Override // org.springframework.beans.factory.HierarchicalBeanFactory
    @Nullable
    public BeanFactory getParentBeanFactory() {
        return getParent();
    }

    @Override // org.springframework.beans.factory.HierarchicalBeanFactory
    public boolean containsLocalBean(String name) {
        return getBeanFactory().containsLocalBean(name);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public BeanFactory getInternalParentBeanFactory() {
        ApplicationContext parent = getParent();
        if (!(parent instanceof ConfigurableApplicationContext)) {
            return getParent();
        }
        ConfigurableApplicationContext cac = (ConfigurableApplicationContext) parent;
        return cac.getBeanFactory();
    }

    @Override // org.springframework.context.MessageSource
    public String getMessage(String code, @Nullable Object[] args, @Nullable String defaultMessage, Locale locale) {
        return getMessageSource().getMessage(code, args, defaultMessage, locale);
    }

    @Override // org.springframework.context.MessageSource
    public String getMessage(String code, @Nullable Object[] args, Locale locale) throws NoSuchMessageException {
        return getMessageSource().getMessage(code, args, locale);
    }

    @Override // org.springframework.context.MessageSource
    public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
        return getMessageSource().getMessage(resolvable, locale);
    }

    private MessageSource getMessageSource() throws IllegalStateException {
        if (this.messageSource == null) {
            throw new IllegalStateException("MessageSource not initialized - call 'refresh' before accessing messages via the context: " + this);
        }
        return this.messageSource;
    }

    @Nullable
    protected MessageSource getInternalParentMessageSource() {
        ApplicationContext parent = getParent();
        if (!(parent instanceof AbstractApplicationContext)) {
            return getParent();
        }
        AbstractApplicationContext abstractApplicationContext = (AbstractApplicationContext) parent;
        return abstractApplicationContext.messageSource;
    }

    @Override // org.springframework.core.io.support.ResourcePatternResolver
    public Resource[] getResources(String locationPattern) throws IOException {
        return this.resourcePatternResolver.getResources(locationPattern);
    }

    @Override // org.springframework.context.Lifecycle
    public void start() {
        getLifecycleProcessor().start();
        publishEvent((ApplicationEvent) new ContextStartedEvent(this));
    }

    @Override // org.springframework.context.Lifecycle
    public void stop() {
        getLifecycleProcessor().stop();
        publishEvent((ApplicationEvent) new ContextStoppedEvent(this));
    }

    @Override // org.springframework.context.Lifecycle
    public boolean isRunning() {
        return this.lifecycleProcessor != null && this.lifecycleProcessor.isRunning();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(getDisplayName());
        sb.append(", started on ").append(new Date(getStartupDate()));
        ApplicationContext parent = getParent();
        if (parent != null) {
            sb.append(", parent: ").append(parent.getDisplayName());
        }
        return sb.toString();
    }
}

package org.springframework.boot;

import java.lang.StackWalker;
import java.lang.management.ManagementFactory;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.coyote.http11.Constants;
import org.crac.management.CRaCMXBean;
import org.springframework.aot.AotDetector;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader;
import org.springframework.boot.Banner;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.BindableRuntimeHintsRegistrar;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.aot.AotApplicationContextInitializer;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.env.CommandLinePropertySource;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.core.metrics.ApplicationStartup;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.function.ThrowingConsumer;
import org.springframework.util.function.ThrowingSupplier;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/SpringApplication.class */
public class SpringApplication {
    public static final String BANNER_LOCATION_PROPERTY_VALUE = "banner.txt";
    public static final String BANNER_LOCATION_PROPERTY = "spring.banner.location";
    private static final String SYSTEM_PROPERTY_JAVA_AWT_HEADLESS = "java.awt.headless";
    private static final Log logger = LogFactory.getLog((Class<?>) SpringApplication.class);
    static final SpringApplicationShutdownHook shutdownHook = new SpringApplicationShutdownHook();
    private static final ThreadLocal<SpringApplicationHook> applicationHook = new ThreadLocal<>();
    private final Set<Class<?>> primarySources;
    private Set<String> sources;
    private Class<?> mainApplicationClass;
    private Banner.Mode bannerMode;
    private boolean logStartupInfo;
    private boolean addCommandLineProperties;
    private boolean addConversionService;
    private Banner banner;
    private ResourceLoader resourceLoader;
    private BeanNameGenerator beanNameGenerator;
    private ConfigurableEnvironment environment;
    private WebApplicationType webApplicationType;
    private boolean headless;
    private boolean registerShutdownHook;
    private List<ApplicationContextInitializer<?>> initializers;
    private List<ApplicationListener<?>> listeners;
    private Map<String, Object> defaultProperties;
    private final List<BootstrapRegistryInitializer> bootstrapRegistryInitializers;
    private Set<String> additionalProfiles;
    private boolean allowBeanDefinitionOverriding;
    private boolean allowCircularReferences;
    private boolean isCustomEnvironment;
    private boolean lazyInitialization;
    private String environmentPrefix;
    private ApplicationContextFactory applicationContextFactory;
    private ApplicationStartup applicationStartup;
    private boolean keepAlive;

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/SpringApplication$Running.class */
    public interface Running {
        ConfigurableApplicationContext getApplicationContext();
    }

    public SpringApplication(Class<?>... primarySources) {
        this(null, primarySources);
    }

    public SpringApplication(ResourceLoader resourceLoader, Class<?>... primarySources) {
        this.sources = new LinkedHashSet();
        this.bannerMode = Banner.Mode.CONSOLE;
        this.logStartupInfo = true;
        this.addCommandLineProperties = true;
        this.addConversionService = true;
        this.headless = true;
        this.registerShutdownHook = true;
        this.additionalProfiles = Collections.emptySet();
        this.isCustomEnvironment = false;
        this.lazyInitialization = false;
        this.applicationContextFactory = ApplicationContextFactory.DEFAULT;
        this.applicationStartup = ApplicationStartup.DEFAULT;
        this.resourceLoader = resourceLoader;
        Assert.notNull(primarySources, "PrimarySources must not be null");
        this.primarySources = new LinkedHashSet(Arrays.asList(primarySources));
        this.webApplicationType = WebApplicationType.deduceFromClasspath();
        this.bootstrapRegistryInitializers = new ArrayList(getSpringFactoriesInstances(BootstrapRegistryInitializer.class));
        setInitializers(getSpringFactoriesInstances(ApplicationContextInitializer.class));
        setListeners(getSpringFactoriesInstances(ApplicationListener.class));
        this.mainApplicationClass = deduceMainApplicationClass();
    }

    private Class<?> deduceMainApplicationClass() {
        return (Class) ((Optional) StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).walk(this::findMainClass)).orElse(null);
    }

    private Optional<Class<?>> findMainClass(Stream<StackWalker.StackFrame> stack) {
        return stack.filter(frame -> {
            return Objects.equals(frame.getMethodName(), "main");
        }).findFirst().map((v0) -> {
            return v0.getDeclaringClass();
        });
    }

    public ConfigurableApplicationContext run(String... args) {
        Startup startup = Startup.create();
        if (this.registerShutdownHook) {
            shutdownHook.enableShutdownHookAddition();
        }
        DefaultBootstrapContext bootstrapContext = createBootstrapContext();
        ConfigurableApplicationContext context = null;
        configureHeadlessProperty();
        SpringApplicationRunListeners listeners = getRunListeners(args);
        listeners.starting(bootstrapContext, this.mainApplicationClass);
        try {
            ApplicationArguments applicationArguments = new DefaultApplicationArguments(args);
            ConfigurableEnvironment environment = prepareEnvironment(listeners, bootstrapContext, applicationArguments);
            Banner printedBanner = printBanner(environment);
            context = createApplicationContext();
            context.setApplicationStartup(this.applicationStartup);
            prepareContext(bootstrapContext, context, environment, listeners, applicationArguments, printedBanner);
            refreshContext(context);
            afterRefresh(context, applicationArguments);
            startup.started();
            if (this.logStartupInfo) {
                new StartupInfoLogger(this.mainApplicationClass).logStarted(getApplicationLog(), startup);
            }
            listeners.started(context, startup.timeTakenToStarted());
            callRunners(context, applicationArguments);
            try {
                if (context.isRunning()) {
                    listeners.ready(context, startup.ready());
                }
                return context;
            } catch (Throwable ex) {
                throw handleRunFailure(context, ex, null);
            }
        } catch (Throwable ex2) {
            throw handleRunFailure(context, ex2, listeners);
        }
    }

    private DefaultBootstrapContext createBootstrapContext() {
        DefaultBootstrapContext bootstrapContext = new DefaultBootstrapContext();
        this.bootstrapRegistryInitializers.forEach(initializer -> {
            initializer.initialize(bootstrapContext);
        });
        return bootstrapContext;
    }

    private ConfigurableEnvironment prepareEnvironment(SpringApplicationRunListeners listeners, DefaultBootstrapContext bootstrapContext, ApplicationArguments applicationArguments) {
        ConfigurableEnvironment environment = getOrCreateEnvironment();
        configureEnvironment(environment, applicationArguments.getSourceArgs());
        ConfigurationPropertySources.attach(environment);
        listeners.environmentPrepared(bootstrapContext, environment);
        DefaultPropertiesPropertySource.moveToEnd(environment);
        Assert.state(!environment.containsProperty("spring.main.environment-prefix"), "Environment prefix cannot be set via properties.");
        bindToSpringApplication(environment);
        if (!this.isCustomEnvironment) {
            EnvironmentConverter environmentConverter = new EnvironmentConverter(getClassLoader());
            environment = environmentConverter.convertEnvironmentIfNecessary(environment, deduceEnvironmentClass());
        }
        ConfigurationPropertySources.attach(environment);
        return environment;
    }

    private Class<? extends ConfigurableEnvironment> deduceEnvironmentClass() {
        Class<? extends ConfigurableEnvironment> environmentType = this.applicationContextFactory.getEnvironmentType(this.webApplicationType);
        if (environmentType == null && this.applicationContextFactory != ApplicationContextFactory.DEFAULT) {
            environmentType = ApplicationContextFactory.DEFAULT.getEnvironmentType(this.webApplicationType);
        }
        if (environmentType == null) {
            return ApplicationEnvironment.class;
        }
        return environmentType;
    }

    private void prepareContext(DefaultBootstrapContext bootstrapContext, ConfigurableApplicationContext context, ConfigurableEnvironment environment, SpringApplicationRunListeners listeners, ApplicationArguments applicationArguments, Banner printedBanner) {
        context.setEnvironment(environment);
        postProcessApplicationContext(context);
        addAotGeneratedInitializerIfNecessary(this.initializers);
        applyInitializers(context);
        listeners.contextPrepared(context);
        bootstrapContext.close(context);
        if (this.logStartupInfo) {
            logStartupInfo(context.getParent() == null);
            logStartupProfileInfo(context);
        }
        SingletonBeanRegistry beanFactory = context.getBeanFactory();
        beanFactory.registerSingleton("springApplicationArguments", applicationArguments);
        if (printedBanner != null) {
            beanFactory.registerSingleton("springBootBanner", printedBanner);
        }
        if (beanFactory instanceof AbstractAutowireCapableBeanFactory) {
            AbstractAutowireCapableBeanFactory autowireCapableBeanFactory = (AbstractAutowireCapableBeanFactory) beanFactory;
            autowireCapableBeanFactory.setAllowCircularReferences(this.allowCircularReferences);
            if (beanFactory instanceof DefaultListableBeanFactory) {
                DefaultListableBeanFactory listableBeanFactory = (DefaultListableBeanFactory) beanFactory;
                listableBeanFactory.setAllowBeanDefinitionOverriding(this.allowBeanDefinitionOverriding);
            }
        }
        if (this.lazyInitialization) {
            context.addBeanFactoryPostProcessor(new LazyInitializationBeanFactoryPostProcessor());
        }
        if (this.keepAlive) {
            context.addApplicationListener(new KeepAlive());
        }
        context.addBeanFactoryPostProcessor(new PropertySourceOrderingBeanFactoryPostProcessor(context));
        if (!AotDetector.useGeneratedArtifacts()) {
            Set<Object> sources = getAllSources();
            Assert.notEmpty(sources, "Sources must not be empty");
            load(context, sources.toArray(new Object[0]));
        }
        listeners.contextLoaded(context);
    }

    private void addAotGeneratedInitializerIfNecessary(List<ApplicationContextInitializer<?>> initializers) {
        if (AotDetector.useGeneratedArtifacts()) {
            Stream<ApplicationContextInitializer<?>> stream = initializers.stream();
            Class<AotApplicationContextInitializer> cls = AotApplicationContextInitializer.class;
            Objects.requireNonNull(AotApplicationContextInitializer.class);
            List<ApplicationContextInitializer<?>> aotInitializers = new ArrayList<>(stream.filter((v1) -> {
                return r3.isInstance(v1);
            }).toList());
            if (aotInitializers.isEmpty()) {
                String initializerClassName = this.mainApplicationClass.getName() + "__ApplicationContextInitializer";
                Assert.state(ClassUtils.isPresent(initializerClassName, getClassLoader()), "You are starting the application with AOT mode enabled but AOT processing hasn't happened. Please build your application with enabled AOT processing first, or remove the system property 'spring.aot.enabled' to run the application in regular mode");
                aotInitializers.add(AotApplicationContextInitializer.forInitializerClasses(initializerClassName));
            }
            initializers.removeAll(aotInitializers);
            initializers.addAll(0, aotInitializers);
        }
    }

    private void refreshContext(ConfigurableApplicationContext context) {
        if (this.registerShutdownHook) {
            shutdownHook.registerApplicationContext(context);
        }
        refresh(context);
    }

    private void configureHeadlessProperty() {
        System.setProperty(SYSTEM_PROPERTY_JAVA_AWT_HEADLESS, System.getProperty(SYSTEM_PROPERTY_JAVA_AWT_HEADLESS, Boolean.toString(this.headless)));
    }

    private SpringApplicationRunListeners getRunListeners(String[] args) {
        SpringFactoriesLoader.ArgumentResolver argumentResolver = SpringFactoriesLoader.ArgumentResolver.of(SpringApplication.class, this);
        List<SpringApplicationRunListener> listeners = getSpringFactoriesInstances(SpringApplicationRunListener.class, argumentResolver.and(String[].class, args));
        SpringApplicationHook hook = applicationHook.get();
        SpringApplicationRunListener hookListener = hook != null ? hook.getRunListener(this) : null;
        if (hookListener != null) {
            listeners = new ArrayList<>(listeners);
            listeners.add(hookListener);
        }
        return new SpringApplicationRunListeners(logger, listeners, this.applicationStartup);
    }

    private <T> List<T> getSpringFactoriesInstances(Class<T> type) {
        return getSpringFactoriesInstances(type, null);
    }

    private <T> List<T> getSpringFactoriesInstances(Class<T> type, SpringFactoriesLoader.ArgumentResolver argumentResolver) {
        return SpringFactoriesLoader.forDefaultResourceLocation(getClassLoader()).load(type, argumentResolver);
    }

    private ConfigurableEnvironment getOrCreateEnvironment() {
        if (this.environment != null) {
            return this.environment;
        }
        ConfigurableEnvironment environment = this.applicationContextFactory.createEnvironment(this.webApplicationType);
        if (environment == null && this.applicationContextFactory != ApplicationContextFactory.DEFAULT) {
            environment = ApplicationContextFactory.DEFAULT.createEnvironment(this.webApplicationType);
        }
        return environment != null ? environment : new ApplicationEnvironment();
    }

    protected void configureEnvironment(ConfigurableEnvironment environment, String[] args) {
        if (this.addConversionService) {
            environment.setConversionService(new ApplicationConversionService());
        }
        configurePropertySources(environment, args);
        configureProfiles(environment, args);
    }

    protected void configurePropertySources(ConfigurableEnvironment environment, String[] args) {
        MutablePropertySources sources = environment.getPropertySources();
        if (!CollectionUtils.isEmpty(this.defaultProperties)) {
            DefaultPropertiesPropertySource.addOrMerge(this.defaultProperties, sources);
        }
        if (this.addCommandLineProperties && args.length > 0) {
            if (sources.contains(CommandLinePropertySource.COMMAND_LINE_PROPERTY_SOURCE_NAME)) {
                PropertySource<?> source = sources.get(CommandLinePropertySource.COMMAND_LINE_PROPERTY_SOURCE_NAME);
                CompositePropertySource composite = new CompositePropertySource(CommandLinePropertySource.COMMAND_LINE_PROPERTY_SOURCE_NAME);
                composite.addPropertySource(new SimpleCommandLinePropertySource("springApplicationCommandLineArgs", args));
                composite.addPropertySource(source);
                sources.replace(CommandLinePropertySource.COMMAND_LINE_PROPERTY_SOURCE_NAME, composite);
                return;
            }
            sources.addFirst(new SimpleCommandLinePropertySource(args));
        }
    }

    protected void configureProfiles(ConfigurableEnvironment environment, String[] args) {
    }

    protected void bindToSpringApplication(ConfigurableEnvironment environment) {
        try {
            Binder.get(environment).bind("spring.main", Bindable.ofInstance(this));
        } catch (Exception ex) {
            throw new IllegalStateException("Cannot bind to SpringApplication", ex);
        }
    }

    private Banner printBanner(ConfigurableEnvironment environment) {
        if (this.bannerMode == Banner.Mode.OFF) {
            return null;
        }
        ResourceLoader resourceLoader = this.resourceLoader != null ? this.resourceLoader : new DefaultResourceLoader(null);
        SpringApplicationBannerPrinter bannerPrinter = new SpringApplicationBannerPrinter(resourceLoader, this.banner);
        if (this.bannerMode == Banner.Mode.LOG) {
            return bannerPrinter.print(environment, this.mainApplicationClass, logger);
        }
        return bannerPrinter.print(environment, this.mainApplicationClass, System.out);
    }

    protected ConfigurableApplicationContext createApplicationContext() {
        return this.applicationContextFactory.create(this.webApplicationType);
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected void postProcessApplicationContext(ConfigurableApplicationContext context) {
        if (this.beanNameGenerator != null) {
            context.getBeanFactory().registerSingleton(AnnotationConfigUtils.CONFIGURATION_BEAN_NAME_GENERATOR, this.beanNameGenerator);
        }
        if (this.resourceLoader != null) {
            if (context instanceof GenericApplicationContext) {
                GenericApplicationContext genericApplicationContext = (GenericApplicationContext) context;
                genericApplicationContext.setResourceLoader(this.resourceLoader);
            }
            if (context instanceof DefaultResourceLoader) {
                DefaultResourceLoader defaultResourceLoader = (DefaultResourceLoader) context;
                defaultResourceLoader.setClassLoader(this.resourceLoader.getClassLoader());
            }
        }
        if (this.addConversionService) {
            context.getBeanFactory().setConversionService(context.getEnvironment().getConversionService());
        }
    }

    protected void applyInitializers(ConfigurableApplicationContext context) {
        for (ApplicationContextInitializer initializer : getInitializers()) {
            Class<?> requiredType = GenericTypeResolver.resolveTypeArgument(initializer.getClass(), ApplicationContextInitializer.class);
            Assert.isInstanceOf(requiredType, context, "Unable to call initializer.");
            initializer.initialize(context);
        }
    }

    protected void logStartupInfo(boolean isRoot) {
        if (isRoot) {
            new StartupInfoLogger(this.mainApplicationClass).logStarting(getApplicationLog());
        }
    }

    protected void logStartupProfileInfo(ConfigurableApplicationContext context) {
        Log log = getApplicationLog();
        if (log.isInfoEnabled()) {
            List<String> activeProfiles = quoteProfiles(context.getEnvironment().getActiveProfiles());
            if (ObjectUtils.isEmpty(activeProfiles)) {
                List<String> defaultProfiles = quoteProfiles(context.getEnvironment().getDefaultProfiles());
                Object[] objArr = new Object[2];
                objArr[0] = Integer.valueOf(defaultProfiles.size());
                objArr[1] = defaultProfiles.size() <= 1 ? DefaultBeanDefinitionDocumentReader.PROFILE_ATTRIBUTE : "profiles";
                String message = String.format("%s default %s: ", objArr);
                log.info("No active profile set, falling back to " + message + StringUtils.collectionToDelimitedString(defaultProfiles, ", "));
                return;
            }
            String message2 = activeProfiles.size() == 1 ? "1 profile is active: " : activeProfiles.size() + " profiles are active: ";
            log.info("The following " + message2 + StringUtils.collectionToDelimitedString(activeProfiles, ", "));
        }
    }

    private List<String> quoteProfiles(String[] profiles) {
        return Arrays.stream(profiles).map(profile -> {
            return "\"" + profile + "\"";
        }).toList();
    }

    protected Log getApplicationLog() {
        if (this.mainApplicationClass == null) {
            return logger;
        }
        return LogFactory.getLog(this.mainApplicationClass);
    }

    protected void load(ApplicationContext context, Object[] sources) {
        if (logger.isDebugEnabled()) {
            logger.debug("Loading source " + StringUtils.arrayToCommaDelimitedString(sources));
        }
        BeanDefinitionLoader loader = createBeanDefinitionLoader(getBeanDefinitionRegistry(context), sources);
        if (this.beanNameGenerator != null) {
            loader.setBeanNameGenerator(this.beanNameGenerator);
        }
        if (this.resourceLoader != null) {
            loader.setResourceLoader(this.resourceLoader);
        }
        if (this.environment != null) {
            loader.setEnvironment(this.environment);
        }
        loader.load();
    }

    public ResourceLoader getResourceLoader() {
        return this.resourceLoader;
    }

    public ClassLoader getClassLoader() {
        if (this.resourceLoader != null) {
            return this.resourceLoader.getClassLoader();
        }
        return ClassUtils.getDefaultClassLoader();
    }

    private BeanDefinitionRegistry getBeanDefinitionRegistry(ApplicationContext context) {
        if (context instanceof BeanDefinitionRegistry) {
            BeanDefinitionRegistry registry = (BeanDefinitionRegistry) context;
            return registry;
        }
        if (context instanceof AbstractApplicationContext) {
            AbstractApplicationContext abstractApplicationContext = (AbstractApplicationContext) context;
            return (BeanDefinitionRegistry) abstractApplicationContext.getBeanFactory();
        }
        throw new IllegalStateException("Could not locate BeanDefinitionRegistry");
    }

    protected BeanDefinitionLoader createBeanDefinitionLoader(BeanDefinitionRegistry registry, Object[] sources) {
        return new BeanDefinitionLoader(registry, sources);
    }

    protected void refresh(ConfigurableApplicationContext applicationContext) {
        applicationContext.refresh();
    }

    protected void afterRefresh(ConfigurableApplicationContext context, ApplicationArguments args) {
    }

    private void callRunners(ConfigurableApplicationContext context, ApplicationArguments args) {
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        String[] beanNames = beanFactory.getBeanNamesForType(Runner.class);
        Map<Runner, String> instancesToBeanNames = new IdentityHashMap<>();
        for (String beanName : beanNames) {
            instancesToBeanNames.put((Runner) beanFactory.getBean(beanName, Runner.class), beanName);
        }
        Comparator<Object> comparator = getOrderComparator(beanFactory).withSourceProvider(new FactoryAwareOrderSourceProvider(beanFactory, instancesToBeanNames));
        instancesToBeanNames.keySet().stream().sorted(comparator).forEach(runner -> {
            callRunner(runner, args);
        });
    }

    private OrderComparator getOrderComparator(ConfigurableListableBeanFactory beanFactory) {
        Comparator<?> comparator;
        if (beanFactory instanceof DefaultListableBeanFactory) {
            DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) beanFactory;
            comparator = defaultListableBeanFactory.getDependencyComparator();
        } else {
            comparator = null;
        }
        Comparator<?> dependencyComparator = comparator;
        if (!(dependencyComparator instanceof OrderComparator)) {
            return AnnotationAwareOrderComparator.INSTANCE;
        }
        OrderComparator orderComparator = (OrderComparator) dependencyComparator;
        return orderComparator;
    }

    private void callRunner(Runner runner, ApplicationArguments args) {
        if (runner instanceof ApplicationRunner) {
            callRunner(ApplicationRunner.class, runner, applicationRunner -> {
                applicationRunner.run(args);
            });
        }
        if (runner instanceof CommandLineRunner) {
            callRunner(CommandLineRunner.class, runner, commandLineRunner -> {
                commandLineRunner.run(args.getSourceArgs());
            });
        }
    }

    private <R extends Runner> void callRunner(Class<R> type, Runner runner, ThrowingConsumer<R> call) {
        call.throwing((message, ex) -> {
            return new IllegalStateException("Failed to execute " + ClassUtils.getShortName((Class<?>) type), ex);
        }).accept(runner);
    }

    /* JADX WARN: Finally extract failed */
    private RuntimeException handleRunFailure(ConfigurableApplicationContext context, Throwable exception, SpringApplicationRunListeners listeners) {
        if (exception instanceof AbandonedRunException) {
            AbandonedRunException abandonedRunException = (AbandonedRunException) exception;
            return abandonedRunException;
        }
        try {
            try {
                handleExitCode(context, exception);
                if (listeners != null) {
                    listeners.failed(context, exception);
                }
                reportFailure(getExceptionReporters(context), exception);
                if (context != null) {
                    context.close();
                    shutdownHook.deregisterFailedApplicationContext(context);
                }
            } catch (Throwable th) {
                reportFailure(getExceptionReporters(context), exception);
                if (context != null) {
                    context.close();
                    shutdownHook.deregisterFailedApplicationContext(context);
                }
                throw th;
            }
        } catch (Exception ex) {
            logger.warn("Unable to close ApplicationContext", ex);
        }
        if (!(exception instanceof RuntimeException)) {
            return new IllegalStateException(exception);
        }
        RuntimeException runtimeException = (RuntimeException) exception;
        return runtimeException;
    }

    private Collection<SpringBootExceptionReporter> getExceptionReporters(ConfigurableApplicationContext context) {
        try {
            SpringFactoriesLoader.ArgumentResolver argumentResolver = SpringFactoriesLoader.ArgumentResolver.of(ConfigurableApplicationContext.class, context);
            return getSpringFactoriesInstances(SpringBootExceptionReporter.class, argumentResolver);
        } catch (Throwable th) {
            return Collections.emptyList();
        }
    }

    private void reportFailure(Collection<SpringBootExceptionReporter> exceptionReporters, Throwable failure) {
        try {
            for (SpringBootExceptionReporter reporter : exceptionReporters) {
                if (reporter.reportException(failure)) {
                    registerLoggedException(failure);
                    return;
                }
            }
        } catch (Throwable th) {
        }
        if (logger.isErrorEnabled()) {
            logger.error("Application run failed", failure);
            registerLoggedException(failure);
        }
    }

    protected void registerLoggedException(Throwable exception) {
        SpringBootExceptionHandler handler = getSpringBootExceptionHandler();
        if (handler != null) {
            handler.registerLoggedException(exception);
        }
    }

    private void handleExitCode(ConfigurableApplicationContext context, Throwable exception) {
        int exitCode = getExitCodeFromException(context, exception);
        if (exitCode != 0) {
            if (context != null) {
                context.publishEvent((ApplicationEvent) new ExitCodeEvent(context, exitCode));
            }
            SpringBootExceptionHandler handler = getSpringBootExceptionHandler();
            if (handler != null) {
                handler.registerExitCode(exitCode);
            }
        }
    }

    private int getExitCodeFromException(ConfigurableApplicationContext context, Throwable exception) {
        int exitCode = getExitCodeFromMappedException(context, exception);
        if (exitCode == 0) {
            exitCode = getExitCodeFromExitCodeGeneratorException(exception);
        }
        return exitCode;
    }

    private int getExitCodeFromMappedException(ConfigurableApplicationContext context, Throwable exception) {
        if (context == null || !context.isActive()) {
            return 0;
        }
        ExitCodeGenerators generators = new ExitCodeGenerators();
        Collection<ExitCodeExceptionMapper> beans = context.getBeansOfType(ExitCodeExceptionMapper.class).values();
        generators.addAll(exception, beans);
        return generators.getExitCode();
    }

    /* JADX WARN: Multi-variable type inference failed */
    private int getExitCodeFromExitCodeGeneratorException(Throwable exception) {
        if (exception == 0) {
            return 0;
        }
        if (exception instanceof ExitCodeGenerator) {
            ExitCodeGenerator generator = (ExitCodeGenerator) exception;
            return generator.getExitCode();
        }
        return getExitCodeFromExitCodeGeneratorException(exception.getCause());
    }

    SpringBootExceptionHandler getSpringBootExceptionHandler() {
        if (isMainThread(Thread.currentThread())) {
            return SpringBootExceptionHandler.forCurrentThread();
        }
        return null;
    }

    private boolean isMainThread(Thread currentThread) {
        return ("main".equals(currentThread.getName()) || "restartedMain".equals(currentThread.getName())) && "main".equals(currentThread.getThreadGroup().getName());
    }

    public Class<?> getMainApplicationClass() {
        return this.mainApplicationClass;
    }

    public void setMainApplicationClass(Class<?> mainApplicationClass) {
        this.mainApplicationClass = mainApplicationClass;
    }

    public WebApplicationType getWebApplicationType() {
        return this.webApplicationType;
    }

    public void setWebApplicationType(WebApplicationType webApplicationType) {
        Assert.notNull(webApplicationType, "WebApplicationType must not be null");
        this.webApplicationType = webApplicationType;
    }

    public void setAllowBeanDefinitionOverriding(boolean allowBeanDefinitionOverriding) {
        this.allowBeanDefinitionOverriding = allowBeanDefinitionOverriding;
    }

    public void setAllowCircularReferences(boolean allowCircularReferences) {
        this.allowCircularReferences = allowCircularReferences;
    }

    public void setLazyInitialization(boolean lazyInitialization) {
        this.lazyInitialization = lazyInitialization;
    }

    public void setHeadless(boolean headless) {
        this.headless = headless;
    }

    public void setRegisterShutdownHook(boolean registerShutdownHook) {
        this.registerShutdownHook = registerShutdownHook;
    }

    public void setBanner(Banner banner) {
        this.banner = banner;
    }

    public void setBannerMode(Banner.Mode bannerMode) {
        this.bannerMode = bannerMode;
    }

    public void setLogStartupInfo(boolean logStartupInfo) {
        this.logStartupInfo = logStartupInfo;
    }

    public void setAddCommandLineProperties(boolean addCommandLineProperties) {
        this.addCommandLineProperties = addCommandLineProperties;
    }

    public void setAddConversionService(boolean addConversionService) {
        this.addConversionService = addConversionService;
    }

    public void addBootstrapRegistryInitializer(BootstrapRegistryInitializer bootstrapRegistryInitializer) {
        Assert.notNull(bootstrapRegistryInitializer, "BootstrapRegistryInitializer must not be null");
        this.bootstrapRegistryInitializers.addAll(Arrays.asList(bootstrapRegistryInitializer));
    }

    public void setDefaultProperties(Map<String, Object> defaultProperties) {
        this.defaultProperties = defaultProperties;
    }

    public void setDefaultProperties(Properties defaultProperties) {
        this.defaultProperties = new HashMap();
        Iterator it = Collections.list(defaultProperties.propertyNames()).iterator();
        while (it.hasNext()) {
            Object key = it.next();
            this.defaultProperties.put((String) key, defaultProperties.get(key));
        }
    }

    public void setAdditionalProfiles(String... profiles) {
        this.additionalProfiles = Collections.unmodifiableSet(new LinkedHashSet(Arrays.asList(profiles)));
    }

    public Set<String> getAdditionalProfiles() {
        return this.additionalProfiles;
    }

    public void setBeanNameGenerator(BeanNameGenerator beanNameGenerator) {
        this.beanNameGenerator = beanNameGenerator;
    }

    public void setEnvironment(ConfigurableEnvironment environment) {
        this.isCustomEnvironment = true;
        this.environment = environment;
    }

    public void addPrimarySources(Collection<Class<?>> additionalPrimarySources) {
        this.primarySources.addAll(additionalPrimarySources);
    }

    public Set<String> getSources() {
        return this.sources;
    }

    public void setSources(Set<String> sources) {
        Assert.notNull(sources, "Sources must not be null");
        this.sources = new LinkedHashSet(sources);
    }

    public Set<Object> getAllSources() {
        Set<Object> allSources = new LinkedHashSet<>();
        if (!CollectionUtils.isEmpty(this.primarySources)) {
            allSources.addAll(this.primarySources);
        }
        if (!CollectionUtils.isEmpty(this.sources)) {
            allSources.addAll(this.sources);
        }
        return Collections.unmodifiableSet(allSources);
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        Assert.notNull(resourceLoader, "ResourceLoader must not be null");
        this.resourceLoader = resourceLoader;
    }

    public String getEnvironmentPrefix() {
        return this.environmentPrefix;
    }

    public void setEnvironmentPrefix(String environmentPrefix) {
        this.environmentPrefix = environmentPrefix;
    }

    public void setApplicationContextFactory(ApplicationContextFactory applicationContextFactory) {
        this.applicationContextFactory = applicationContextFactory != null ? applicationContextFactory : ApplicationContextFactory.DEFAULT;
    }

    public void setInitializers(Collection<? extends ApplicationContextInitializer<?>> initializers) {
        this.initializers = new ArrayList(initializers);
    }

    public void addInitializers(ApplicationContextInitializer<?>... initializers) {
        this.initializers.addAll(Arrays.asList(initializers));
    }

    public Set<ApplicationContextInitializer<?>> getInitializers() {
        return asUnmodifiableOrderedSet(this.initializers);
    }

    public void setListeners(Collection<? extends ApplicationListener<?>> listeners) {
        this.listeners = new ArrayList(listeners);
    }

    public void addListeners(ApplicationListener<?>... listeners) {
        this.listeners.addAll(Arrays.asList(listeners));
    }

    public Set<ApplicationListener<?>> getListeners() {
        return asUnmodifiableOrderedSet(this.listeners);
    }

    public void setApplicationStartup(ApplicationStartup applicationStartup) {
        this.applicationStartup = applicationStartup != null ? applicationStartup : ApplicationStartup.DEFAULT;
    }

    public ApplicationStartup getApplicationStartup() {
        return this.applicationStartup;
    }

    public boolean isKeepAlive() {
        return this.keepAlive;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public static SpringApplicationShutdownHandlers getShutdownHandlers() {
        return shutdownHook.getHandlers();
    }

    public static ConfigurableApplicationContext run(Class<?> primarySource, String... args) {
        return run((Class<?>[]) new Class[]{primarySource}, args);
    }

    public static ConfigurableApplicationContext run(Class<?>[] primarySources, String[] args) {
        return new SpringApplication(primarySources).run(args);
    }

    public static void main(String[] args) throws Exception {
        run((Class<?>[]) new Class[0], args);
    }

    /* JADX WARN: Finally extract failed */
    public static int exit(ApplicationContext context, ExitCodeGenerator... exitCodeGenerators) {
        Assert.notNull(context, "Context must not be null");
        int exitCode = 0;
        try {
            try {
                ExitCodeGenerators generators = new ExitCodeGenerators();
                Collection<ExitCodeGenerator> beans = context.getBeansOfType(ExitCodeGenerator.class).values();
                generators.addAll(exitCodeGenerators);
                generators.addAll(beans);
                exitCode = generators.getExitCode();
                if (exitCode != 0) {
                    context.publishEvent((ApplicationEvent) new ExitCodeEvent(context, exitCode));
                }
                close(context);
            } catch (Throwable th) {
                close(context);
                throw th;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            exitCode = exitCode != 0 ? exitCode : 1;
        }
        return exitCode;
    }

    public static Augmented from(ThrowingConsumer<String[]> main) {
        Assert.notNull(main, "Main must not be null");
        return new Augmented(main, Collections.emptySet());
    }

    public static void withHook(SpringApplicationHook hook, Runnable action) {
        withHook(hook, () -> {
            action.run();
            return null;
        });
    }

    public static <T> T withHook(SpringApplicationHook hook, ThrowingSupplier<T> action) {
        applicationHook.set(hook);
        try {
            T t = action.get();
            applicationHook.remove();
            return t;
        } catch (Throwable th) {
            applicationHook.remove();
            throw th;
        }
    }

    private static void close(ApplicationContext context) {
        if (context instanceof ConfigurableApplicationContext) {
            ConfigurableApplicationContext closable = (ConfigurableApplicationContext) context;
            closable.close();
        }
    }

    private static <E> Set<E> asUnmodifiableOrderedSet(Collection<E> elements) {
        List<E> list = new ArrayList<>(elements);
        list.sort(AnnotationAwareOrderComparator.INSTANCE);
        return new LinkedHashSet(list);
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/SpringApplication$Augmented.class */
    public static class Augmented {
        private final ThrowingConsumer<String[]> main;
        private final Set<Class<?>> sources;

        Augmented(ThrowingConsumer<String[]> main, Set<Class<?>> sources) {
            this.main = main;
            this.sources = Set.copyOf(sources);
        }

        public Augmented with(Class<?>... sources) {
            LinkedHashSet<Class<?>> merged = new LinkedHashSet<>(this.sources);
            merged.addAll(Arrays.asList(sources));
            return new Augmented(this.main, merged);
        }

        public Running run(String... args) {
            RunListener runListener = new RunListener();
            SpringApplicationHook hook = new SingleUseSpringApplicationHook(springApplication -> {
                springApplication.addPrimarySources(this.sources);
                return runListener;
            });
            SpringApplication.withHook(hook, () -> {
                this.main.accept(args);
            });
            return runListener;
        }

        /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/SpringApplication$Augmented$RunListener.class */
        private static final class RunListener implements SpringApplicationRunListener, Running {
            private final List<ConfigurableApplicationContext> contexts = Collections.synchronizedList(new ArrayList());

            private RunListener() {
            }

            @Override // org.springframework.boot.SpringApplicationRunListener
            public void contextLoaded(ConfigurableApplicationContext context) {
                this.contexts.add(context);
            }

            @Override // org.springframework.boot.SpringApplication.Running
            public ConfigurableApplicationContext getApplicationContext() {
                List<ConfigurableApplicationContext> rootContexts = this.contexts.stream().filter(context -> {
                    return context.getParent() == null;
                }).toList();
                Assert.state(!rootContexts.isEmpty(), "No root application context located");
                Assert.state(rootContexts.size() == 1, "No unique root application context located");
                return rootContexts.get(0);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/SpringApplication$PropertySourceOrderingBeanFactoryPostProcessor.class */
    public static class PropertySourceOrderingBeanFactoryPostProcessor implements BeanFactoryPostProcessor, Ordered {
        private final ConfigurableApplicationContext context;

        PropertySourceOrderingBeanFactoryPostProcessor(ConfigurableApplicationContext context) {
            this.context = context;
        }

        @Override // org.springframework.core.Ordered
        public int getOrder() {
            return Integer.MIN_VALUE;
        }

        @Override // org.springframework.beans.factory.config.BeanFactoryPostProcessor
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
            DefaultPropertiesPropertySource.moveToEnd(this.context.getEnvironment());
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/SpringApplication$SpringApplicationRuntimeHints.class */
    static class SpringApplicationRuntimeHints extends BindableRuntimeHintsRegistrar {
        SpringApplicationRuntimeHints() {
            super((Class<?>[]) new Class[]{SpringApplication.class});
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/SpringApplication$AbandonedRunException.class */
    public static class AbandonedRunException extends RuntimeException {
        private final ConfigurableApplicationContext applicationContext;

        public AbandonedRunException() {
            this(null);
        }

        public AbandonedRunException(ConfigurableApplicationContext applicationContext) {
            this.applicationContext = applicationContext;
        }

        public ConfigurableApplicationContext getApplicationContext() {
            return this.applicationContext;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/SpringApplication$SingleUseSpringApplicationHook.class */
    private static final class SingleUseSpringApplicationHook implements SpringApplicationHook {
        private final AtomicBoolean used = new AtomicBoolean();
        private final SpringApplicationHook delegate;

        private SingleUseSpringApplicationHook(SpringApplicationHook delegate) {
            this.delegate = delegate;
        }

        @Override // org.springframework.boot.SpringApplicationHook
        public SpringApplicationRunListener getRunListener(SpringApplication springApplication) {
            if (this.used.compareAndSet(false, true)) {
                return this.delegate.getRunListener(springApplication);
            }
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/SpringApplication$KeepAlive.class */
    public static final class KeepAlive implements ApplicationListener<ApplicationContextEvent> {
        private final AtomicReference<Thread> thread = new AtomicReference<>();

        private KeepAlive() {
        }

        @Override // org.springframework.context.ApplicationListener
        public void onApplicationEvent(ApplicationContextEvent event) {
            if (event instanceof ContextRefreshedEvent) {
                startKeepAliveThread();
            } else if (event instanceof ContextClosedEvent) {
                stopKeepAliveThread();
            }
        }

        private void startKeepAliveThread() {
            Thread thread = new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(Long.MAX_VALUE);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            });
            if (this.thread.compareAndSet(null, thread)) {
                thread.setDaemon(false);
                thread.setName(Constants.KEEP_ALIVE_HEADER_VALUE_TOKEN);
                thread.start();
            }
        }

        private void stopKeepAliveThread() {
            Thread thread = this.thread.getAndSet(null);
            if (thread == null) {
                return;
            }
            thread.interrupt();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/SpringApplication$Startup.class */
    public static abstract class Startup {
        private Duration timeTakenToStarted;

        protected abstract long startTime();

        /* JADX INFO: Access modifiers changed from: protected */
        public abstract Long processUptime();

        /* JADX INFO: Access modifiers changed from: protected */
        public abstract String action();

        Startup() {
        }

        final Duration started() {
            long now = System.currentTimeMillis();
            this.timeTakenToStarted = Duration.ofMillis(now - startTime());
            return this.timeTakenToStarted;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Duration timeTakenToStarted() {
            return this.timeTakenToStarted;
        }

        private Duration ready() {
            long now = System.currentTimeMillis();
            return Duration.ofMillis(now - startTime());
        }

        static Startup create() {
            ClassLoader classLoader = Startup.class.getClassLoader();
            return (ClassUtils.isPresent("jdk.crac.management.CRaCMXBean", classLoader) && ClassUtils.isPresent("org.crac.management.CRaCMXBean", classLoader)) ? new CoordinatedRestoreAtCheckpointStartup() : new StandardStartup();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/SpringApplication$StandardStartup.class */
    public static final class StandardStartup extends Startup {
        private final Long startTime = Long.valueOf(System.currentTimeMillis());

        private StandardStartup() {
        }

        @Override // org.springframework.boot.SpringApplication.Startup
        protected long startTime() {
            return this.startTime.longValue();
        }

        @Override // org.springframework.boot.SpringApplication.Startup
        protected Long processUptime() {
            try {
                return Long.valueOf(ManagementFactory.getRuntimeMXBean().getUptime());
            } catch (Throwable th) {
                return null;
            }
        }

        @Override // org.springframework.boot.SpringApplication.Startup
        protected String action() {
            return "Started";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/SpringApplication$CoordinatedRestoreAtCheckpointStartup.class */
    public static final class CoordinatedRestoreAtCheckpointStartup extends Startup {
        private final StandardStartup fallback = new StandardStartup();

        private CoordinatedRestoreAtCheckpointStartup() {
        }

        @Override // org.springframework.boot.SpringApplication.Startup
        protected Long processUptime() {
            long uptime = CRaCMXBean.getCRaCMXBean().getUptimeSinceRestore();
            return Long.valueOf(uptime >= 0 ? uptime : this.fallback.processUptime().longValue());
        }

        @Override // org.springframework.boot.SpringApplication.Startup
        protected String action() {
            return restoreTime() >= 0 ? "Restored" : this.fallback.action();
        }

        private long restoreTime() {
            return CRaCMXBean.getCRaCMXBean().getRestoreTime();
        }

        @Override // org.springframework.boot.SpringApplication.Startup
        protected long startTime() {
            long restoreTime = restoreTime();
            return restoreTime >= 0 ? restoreTime : this.fallback.startTime();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/SpringApplication$FactoryAwareOrderSourceProvider.class */
    public class FactoryAwareOrderSourceProvider implements OrderComparator.OrderSourceProvider {
        private final ConfigurableBeanFactory beanFactory;
        private final Map<?, String> instancesToBeanNames;

        FactoryAwareOrderSourceProvider(ConfigurableBeanFactory beanFactory, Map<?, String> instancesToBeanNames) {
            this.beanFactory = beanFactory;
            this.instancesToBeanNames = instancesToBeanNames;
        }

        @Override // org.springframework.core.OrderComparator.OrderSourceProvider
        public Object getOrderSource(Object obj) {
            String beanName = this.instancesToBeanNames.get(obj);
            if (beanName != null) {
                return getOrderSource(beanName, obj.getClass());
            }
            return null;
        }

        private Object getOrderSource(String beanName, Class<?> instanceType) {
            try {
                RootBeanDefinition beanDefinition = (RootBeanDefinition) this.beanFactory.getMergedBeanDefinition(beanName);
                Method factoryMethod = beanDefinition.getResolvedFactoryMethod();
                Class<?> targetType = beanDefinition.getTargetType();
                return Stream.of((Object[]) new GenericDeclaration[]{factoryMethod, targetType != instanceType ? targetType : null}).filter((v0) -> {
                    return Objects.nonNull(v0);
                }).toArray();
            } catch (NoSuchBeanDefinitionException e) {
                return null;
            }
        }
    }
}

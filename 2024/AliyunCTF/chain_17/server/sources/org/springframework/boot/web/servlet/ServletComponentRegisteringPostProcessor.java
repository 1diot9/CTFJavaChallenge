package org.springframework.boot.web.servlet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.springframework.aot.generate.GenerationContext;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.TypeReference;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor;
import org.springframework.beans.factory.aot.BeanFactoryInitializationCode;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.web.servlet.WebListenerHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.web.context.WebApplicationContext;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/servlet/ServletComponentRegisteringPostProcessor.class */
class ServletComponentRegisteringPostProcessor implements BeanFactoryPostProcessor, ApplicationContextAware, BeanFactoryInitializationAotProcessor {
    private static final List<ServletComponentHandler> HANDLERS;
    private final Set<String> packagesToScan;
    private ApplicationContext applicationContext;

    static {
        List<ServletComponentHandler> servletComponentHandlers = new ArrayList<>();
        servletComponentHandlers.add(new WebServletHandler());
        servletComponentHandlers.add(new WebFilterHandler());
        servletComponentHandlers.add(new WebListenerHandler());
        HANDLERS = Collections.unmodifiableList(servletComponentHandlers);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ServletComponentRegisteringPostProcessor(Set<String> packagesToScan) {
        this.packagesToScan = packagesToScan;
    }

    @Override // org.springframework.beans.factory.config.BeanFactoryPostProcessor
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (isRunningInEmbeddedWebServer()) {
            ClassPathScanningCandidateComponentProvider componentProvider = createComponentProvider();
            for (String packageToScan : this.packagesToScan) {
                scanPackage(componentProvider, packageToScan);
            }
        }
    }

    private void scanPackage(ClassPathScanningCandidateComponentProvider componentProvider, String packageToScan) {
        for (BeanDefinition candidate : componentProvider.findCandidateComponents(packageToScan)) {
            if (candidate instanceof AnnotatedBeanDefinition) {
                AnnotatedBeanDefinition annotatedBeanDefinition = (AnnotatedBeanDefinition) candidate;
                for (ServletComponentHandler handler : HANDLERS) {
                    handler.handle(annotatedBeanDefinition, (BeanDefinitionRegistry) this.applicationContext);
                }
            }
        }
    }

    private boolean isRunningInEmbeddedWebServer() {
        ApplicationContext applicationContext = this.applicationContext;
        if (applicationContext instanceof WebApplicationContext) {
            WebApplicationContext webApplicationContext = (WebApplicationContext) applicationContext;
            if (webApplicationContext.getServletContext() == null) {
                return true;
            }
        }
        return false;
    }

    private ClassPathScanningCandidateComponentProvider createComponentProvider() {
        ClassPathScanningCandidateComponentProvider componentProvider = new ClassPathScanningCandidateComponentProvider(false);
        componentProvider.setEnvironment(this.applicationContext.getEnvironment());
        componentProvider.setResourceLoader(this.applicationContext);
        for (ServletComponentHandler handler : HANDLERS) {
            componentProvider.addIncludeFilter(handler.getTypeFilter());
        }
        return componentProvider;
    }

    Set<String> getPackagesToScan() {
        return Collections.unmodifiableSet(this.packagesToScan);
    }

    @Override // org.springframework.context.ApplicationContextAware
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override // org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor
    public BeanFactoryInitializationAotContribution processAheadOfTime(final ConfigurableListableBeanFactory beanFactory) {
        return new BeanFactoryInitializationAotContribution() { // from class: org.springframework.boot.web.servlet.ServletComponentRegisteringPostProcessor.1
            @Override // org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution
            public void applyTo(GenerationContext generationContext, BeanFactoryInitializationCode beanFactoryInitializationCode) {
                for (String beanName : beanFactory.getBeanDefinitionNames()) {
                    BeanDefinition definition = beanFactory.getBeanDefinition(beanName);
                    if (Objects.equals(definition.getBeanClassName(), WebListenerHandler.ServletComponentWebListenerRegistrar.class.getName())) {
                        String listenerClassName = (String) definition.getConstructorArgumentValues().getArgumentValue(0, String.class).getValue();
                        generationContext.getRuntimeHints().reflection().registerType(TypeReference.of(listenerClassName), MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);
                    }
                }
            }
        };
    }
}

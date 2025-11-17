package org.springframework.context.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionValueResolver;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.metrics.ApplicationStartup;
import org.springframework.core.metrics.StartupStep;
import org.springframework.lang.Nullable;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/support/PostProcessorRegistrationDelegate.class */
public final class PostProcessorRegistrationDelegate {
    private PostProcessorRegistrationDelegate() {
    }

    public static void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory, List<BeanFactoryPostProcessor> beanFactoryPostProcessors) {
        Set<String> processedBeans = new HashSet<>();
        if (beanFactory instanceof BeanDefinitionRegistry) {
            BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
            List<BeanFactoryPostProcessor> regularPostProcessors = new ArrayList<>();
            List<BeanDefinitionRegistryPostProcessor> registryProcessors = new ArrayList<>();
            for (BeanFactoryPostProcessor postProcessor : beanFactoryPostProcessors) {
                if (postProcessor instanceof BeanDefinitionRegistryPostProcessor) {
                    BeanDefinitionRegistryPostProcessor registryProcessor = (BeanDefinitionRegistryPostProcessor) postProcessor;
                    registryProcessor.postProcessBeanDefinitionRegistry(registry);
                    registryProcessors.add(registryProcessor);
                } else {
                    regularPostProcessors.add(postProcessor);
                }
            }
            List<BeanDefinitionRegistryPostProcessor> currentRegistryProcessors = new ArrayList<>();
            String[] postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
            for (String ppName : postProcessorNames) {
                if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
                    currentRegistryProcessors.add((BeanDefinitionRegistryPostProcessor) beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
                    processedBeans.add(ppName);
                }
            }
            sortPostProcessors(currentRegistryProcessors, beanFactory);
            registryProcessors.addAll(currentRegistryProcessors);
            invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry, beanFactory.getApplicationStartup());
            currentRegistryProcessors.clear();
            String[] postProcessorNames2 = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
            for (String ppName2 : postProcessorNames2) {
                if (!processedBeans.contains(ppName2) && beanFactory.isTypeMatch(ppName2, Ordered.class)) {
                    currentRegistryProcessors.add((BeanDefinitionRegistryPostProcessor) beanFactory.getBean(ppName2, BeanDefinitionRegistryPostProcessor.class));
                    processedBeans.add(ppName2);
                }
            }
            sortPostProcessors(currentRegistryProcessors, beanFactory);
            registryProcessors.addAll(currentRegistryProcessors);
            invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry, beanFactory.getApplicationStartup());
            currentRegistryProcessors.clear();
            boolean reiterate = true;
            while (reiterate) {
                reiterate = false;
                String[] postProcessorNames3 = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
                for (String ppName3 : postProcessorNames3) {
                    if (!processedBeans.contains(ppName3)) {
                        currentRegistryProcessors.add((BeanDefinitionRegistryPostProcessor) beanFactory.getBean(ppName3, BeanDefinitionRegistryPostProcessor.class));
                        processedBeans.add(ppName3);
                        reiterate = true;
                    }
                }
                sortPostProcessors(currentRegistryProcessors, beanFactory);
                registryProcessors.addAll(currentRegistryProcessors);
                invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry, beanFactory.getApplicationStartup());
                currentRegistryProcessors.clear();
            }
            invokeBeanFactoryPostProcessors(registryProcessors, beanFactory);
            invokeBeanFactoryPostProcessors(regularPostProcessors, beanFactory);
        } else {
            invokeBeanFactoryPostProcessors(beanFactoryPostProcessors, beanFactory);
        }
        String[] postProcessorNames4 = beanFactory.getBeanNamesForType(BeanFactoryPostProcessor.class, true, false);
        List<BeanFactoryPostProcessor> priorityOrderedPostProcessors = new ArrayList<>();
        List<String> orderedPostProcessorNames = new ArrayList<>();
        List<String> nonOrderedPostProcessorNames = new ArrayList<>();
        for (String ppName4 : postProcessorNames4) {
            if (!processedBeans.contains(ppName4)) {
                if (beanFactory.isTypeMatch(ppName4, PriorityOrdered.class)) {
                    priorityOrderedPostProcessors.add((BeanFactoryPostProcessor) beanFactory.getBean(ppName4, BeanFactoryPostProcessor.class));
                } else if (beanFactory.isTypeMatch(ppName4, Ordered.class)) {
                    orderedPostProcessorNames.add(ppName4);
                } else {
                    nonOrderedPostProcessorNames.add(ppName4);
                }
            }
        }
        sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
        invokeBeanFactoryPostProcessors(priorityOrderedPostProcessors, beanFactory);
        List<BeanFactoryPostProcessor> orderedPostProcessors = new ArrayList<>(orderedPostProcessorNames.size());
        for (String postProcessorName : orderedPostProcessorNames) {
            orderedPostProcessors.add((BeanFactoryPostProcessor) beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
        }
        sortPostProcessors(orderedPostProcessors, beanFactory);
        invokeBeanFactoryPostProcessors(orderedPostProcessors, beanFactory);
        List<BeanFactoryPostProcessor> nonOrderedPostProcessors = new ArrayList<>(nonOrderedPostProcessorNames.size());
        for (String postProcessorName2 : nonOrderedPostProcessorNames) {
            nonOrderedPostProcessors.add((BeanFactoryPostProcessor) beanFactory.getBean(postProcessorName2, BeanFactoryPostProcessor.class));
        }
        invokeBeanFactoryPostProcessors(nonOrderedPostProcessors, beanFactory);
        beanFactory.clearMetadataCache();
    }

    public static void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory, AbstractApplicationContext applicationContext) {
        String[] postProcessorNames = beanFactory.getBeanNamesForType(BeanPostProcessor.class, true, false);
        int beanProcessorTargetCount = beanFactory.getBeanPostProcessorCount() + 1 + postProcessorNames.length;
        beanFactory.addBeanPostProcessor(new BeanPostProcessorChecker(beanFactory, postProcessorNames, beanProcessorTargetCount));
        List<BeanPostProcessor> priorityOrderedPostProcessors = new ArrayList<>();
        List<BeanPostProcessor> internalPostProcessors = new ArrayList<>();
        List<String> orderedPostProcessorNames = new ArrayList<>();
        List<String> nonOrderedPostProcessorNames = new ArrayList<>();
        for (String ppName : postProcessorNames) {
            if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
                BeanPostProcessor pp = (BeanPostProcessor) beanFactory.getBean(ppName, BeanPostProcessor.class);
                priorityOrderedPostProcessors.add(pp);
                if (pp instanceof MergedBeanDefinitionPostProcessor) {
                    internalPostProcessors.add(pp);
                }
            } else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
                orderedPostProcessorNames.add(ppName);
            } else {
                nonOrderedPostProcessorNames.add(ppName);
            }
        }
        sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
        registerBeanPostProcessors(beanFactory, priorityOrderedPostProcessors);
        List<BeanPostProcessor> orderedPostProcessors = new ArrayList<>(orderedPostProcessorNames.size());
        for (String ppName2 : orderedPostProcessorNames) {
            BeanPostProcessor pp2 = (BeanPostProcessor) beanFactory.getBean(ppName2, BeanPostProcessor.class);
            orderedPostProcessors.add(pp2);
            if (pp2 instanceof MergedBeanDefinitionPostProcessor) {
                internalPostProcessors.add(pp2);
            }
        }
        sortPostProcessors(orderedPostProcessors, beanFactory);
        registerBeanPostProcessors(beanFactory, orderedPostProcessors);
        List<BeanPostProcessor> nonOrderedPostProcessors = new ArrayList<>(nonOrderedPostProcessorNames.size());
        for (String ppName3 : nonOrderedPostProcessorNames) {
            BeanPostProcessor pp3 = (BeanPostProcessor) beanFactory.getBean(ppName3, BeanPostProcessor.class);
            nonOrderedPostProcessors.add(pp3);
            if (pp3 instanceof MergedBeanDefinitionPostProcessor) {
                internalPostProcessors.add(pp3);
            }
        }
        registerBeanPostProcessors(beanFactory, nonOrderedPostProcessors);
        sortPostProcessors(internalPostProcessors, beanFactory);
        registerBeanPostProcessors(beanFactory, internalPostProcessors);
        beanFactory.addBeanPostProcessor(new ApplicationListenerDetector(applicationContext));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <T extends BeanPostProcessor> List<T> loadBeanPostProcessors(ConfigurableListableBeanFactory beanFactory, Class<T> beanPostProcessorType) {
        String[] postProcessorNames = beanFactory.getBeanNamesForType((Class<?>) beanPostProcessorType, true, false);
        ArrayList arrayList = new ArrayList();
        for (String ppName : postProcessorNames) {
            arrayList.add((BeanPostProcessor) beanFactory.getBean(ppName, beanPostProcessorType));
        }
        sortPostProcessors(arrayList, beanFactory);
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void invokeMergedBeanDefinitionPostProcessors(DefaultListableBeanFactory beanFactory) {
        new MergedBeanDefinitionPostProcessorInvoker(beanFactory).invokeMergedBeanDefinitionPostProcessors();
    }

    private static void sortPostProcessors(List<?> postProcessors, ConfigurableListableBeanFactory beanFactory) {
        if (postProcessors.size() <= 1) {
            return;
        }
        Comparator<Object> comparatorToUse = null;
        if (beanFactory instanceof DefaultListableBeanFactory) {
            DefaultListableBeanFactory dlbf = (DefaultListableBeanFactory) beanFactory;
            comparatorToUse = dlbf.getDependencyComparator();
        }
        if (comparatorToUse == null) {
            comparatorToUse = OrderComparator.INSTANCE;
        }
        postProcessors.sort(comparatorToUse);
    }

    private static void invokeBeanDefinitionRegistryPostProcessors(Collection<? extends BeanDefinitionRegistryPostProcessor> postProcessors, BeanDefinitionRegistry registry, ApplicationStartup applicationStartup) {
        for (BeanDefinitionRegistryPostProcessor postProcessor : postProcessors) {
            StartupStep start = applicationStartup.start("spring.context.beandef-registry.post-process");
            Objects.requireNonNull(postProcessor);
            StartupStep postProcessBeanDefRegistry = start.tag("postProcessor", postProcessor::toString);
            postProcessor.postProcessBeanDefinitionRegistry(registry);
            postProcessBeanDefRegistry.end();
        }
    }

    private static void invokeBeanFactoryPostProcessors(Collection<? extends BeanFactoryPostProcessor> postProcessors, ConfigurableListableBeanFactory beanFactory) {
        for (BeanFactoryPostProcessor postProcessor : postProcessors) {
            StartupStep start = beanFactory.getApplicationStartup().start("spring.context.bean-factory.post-process");
            Objects.requireNonNull(postProcessor);
            StartupStep postProcessBeanFactory = start.tag("postProcessor", postProcessor::toString);
            postProcessor.postProcessBeanFactory(beanFactory);
            postProcessBeanFactory.end();
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory, List<? extends BeanPostProcessor> postProcessors) {
        if (beanFactory instanceof AbstractBeanFactory) {
            AbstractBeanFactory abstractBeanFactory = (AbstractBeanFactory) beanFactory;
            abstractBeanFactory.addBeanPostProcessors(postProcessors);
        } else {
            for (BeanPostProcessor postProcessor : postProcessors) {
                beanFactory.addBeanPostProcessor(postProcessor);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/support/PostProcessorRegistrationDelegate$BeanPostProcessorChecker.class */
    public static final class BeanPostProcessorChecker implements BeanPostProcessor {
        private static final Log logger = LogFactory.getLog((Class<?>) BeanPostProcessorChecker.class);
        private final ConfigurableListableBeanFactory beanFactory;
        private final String[] postProcessorNames;
        private final int beanPostProcessorTargetCount;

        public BeanPostProcessorChecker(ConfigurableListableBeanFactory beanFactory, String[] postProcessorNames, int beanPostProcessorTargetCount) {
            this.beanFactory = beanFactory;
            this.postProcessorNames = postProcessorNames;
            this.beanPostProcessorTargetCount = beanPostProcessorTargetCount;
        }

        @Override // org.springframework.beans.factory.config.BeanPostProcessor
        public Object postProcessBeforeInitialization(Object bean, String beanName) {
            return bean;
        }

        @Override // org.springframework.beans.factory.config.BeanPostProcessor
        public Object postProcessAfterInitialization(Object bean, String beanName) {
            if (!(bean instanceof BeanPostProcessor) && !isInfrastructureBean(beanName) && this.beanFactory.getBeanPostProcessorCount() < this.beanPostProcessorTargetCount && logger.isWarnEnabled()) {
                Set<String> bppsInCreation = new LinkedHashSet<>(2);
                for (String bppName : this.postProcessorNames) {
                    if (this.beanFactory.isCurrentlyInCreation(bppName)) {
                        bppsInCreation.add(bppName);
                    }
                }
                if (bppsInCreation.size() == 1) {
                    String bppName2 = bppsInCreation.iterator().next();
                    if (this.beanFactory.containsBeanDefinition(bppName2) && beanName.equals(this.beanFactory.getBeanDefinition(bppName2).getFactoryBeanName())) {
                        logger.warn("Bean '" + beanName + "' of type [" + bean.getClass().getName() + "] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying). The currently created BeanPostProcessor " + bppsInCreation + " is declared through a non-static factory method on that class; consider declaring it as static instead.");
                        return bean;
                    }
                }
                logger.warn("Bean '" + beanName + "' of type [" + bean.getClass().getName() + "] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying). Is this bean getting eagerly injected into a currently created BeanPostProcessor " + bppsInCreation + "? Check the corresponding BeanPostProcessor declaration and its dependencies.");
            }
            return bean;
        }

        private boolean isInfrastructureBean(@Nullable String beanName) {
            if (beanName != null && this.beanFactory.containsBeanDefinition(beanName)) {
                BeanDefinition bd = this.beanFactory.getBeanDefinition(beanName);
                return bd.getRole() == 2;
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/support/PostProcessorRegistrationDelegate$MergedBeanDefinitionPostProcessorInvoker.class */
    public static final class MergedBeanDefinitionPostProcessorInvoker {
        private final DefaultListableBeanFactory beanFactory;

        private MergedBeanDefinitionPostProcessorInvoker(DefaultListableBeanFactory beanFactory) {
            this.beanFactory = beanFactory;
        }

        private void invokeMergedBeanDefinitionPostProcessors() {
            List<MergedBeanDefinitionPostProcessor> postProcessors = PostProcessorRegistrationDelegate.loadBeanPostProcessors(this.beanFactory, MergedBeanDefinitionPostProcessor.class);
            for (String beanName : this.beanFactory.getBeanDefinitionNames()) {
                RootBeanDefinition bd = (RootBeanDefinition) this.beanFactory.getMergedBeanDefinition(beanName);
                Class<?> beanType = resolveBeanType(bd);
                postProcessRootBeanDefinition(postProcessors, beanName, beanType, bd);
                bd.markAsPostProcessed();
            }
            PostProcessorRegistrationDelegate.registerBeanPostProcessors(this.beanFactory, postProcessors);
        }

        private void postProcessRootBeanDefinition(List<MergedBeanDefinitionPostProcessor> postProcessors, String beanName, Class<?> beanType, RootBeanDefinition bd) {
            BeanDefinitionValueResolver valueResolver = new BeanDefinitionValueResolver(this.beanFactory, beanName, bd);
            postProcessors.forEach(postProcessor -> {
                postProcessor.postProcessMergedBeanDefinition(bd, beanType, beanName);
            });
            for (PropertyValue propertyValue : bd.getPropertyValues().getPropertyValueList()) {
                postProcessValue(postProcessors, valueResolver, propertyValue.getValue());
            }
            for (ConstructorArgumentValues.ValueHolder valueHolder : bd.getConstructorArgumentValues().getIndexedArgumentValues().values()) {
                postProcessValue(postProcessors, valueResolver, valueHolder.getValue());
            }
            for (ConstructorArgumentValues.ValueHolder valueHolder2 : bd.getConstructorArgumentValues().getGenericArgumentValues()) {
                postProcessValue(postProcessors, valueResolver, valueHolder2.getValue());
            }
        }

        private void postProcessValue(List<MergedBeanDefinitionPostProcessor> postProcessors, BeanDefinitionValueResolver valueResolver, @Nullable Object value) {
            if (value instanceof BeanDefinitionHolder) {
                BeanDefinitionHolder bdh = (BeanDefinitionHolder) value;
                BeanDefinition beanDefinition = bdh.getBeanDefinition();
                if (beanDefinition instanceof AbstractBeanDefinition) {
                    AbstractBeanDefinition innerBd = (AbstractBeanDefinition) beanDefinition;
                    Class<?> innerBeanType = resolveBeanType(innerBd);
                    resolveInnerBeanDefinition(valueResolver, innerBd, (innerBeanName, innerBeanDefinition) -> {
                        postProcessRootBeanDefinition(postProcessors, innerBeanName, innerBeanType, innerBeanDefinition);
                    });
                    return;
                }
            }
            if (value instanceof AbstractBeanDefinition) {
                AbstractBeanDefinition innerBd2 = (AbstractBeanDefinition) value;
                Class<?> innerBeanType2 = resolveBeanType(innerBd2);
                resolveInnerBeanDefinition(valueResolver, innerBd2, (innerBeanName2, innerBeanDefinition2) -> {
                    postProcessRootBeanDefinition(postProcessors, innerBeanName2, innerBeanType2, innerBeanDefinition2);
                });
            } else if (value instanceof TypedStringValue) {
                TypedStringValue typedStringValue = (TypedStringValue) value;
                resolveTypeStringValue(typedStringValue);
            }
        }

        private void resolveInnerBeanDefinition(BeanDefinitionValueResolver valueResolver, BeanDefinition innerBeanDefinition, BiConsumer<String, RootBeanDefinition> resolver) {
            valueResolver.resolveInnerBean(null, innerBeanDefinition, (name, rbd) -> {
                resolver.accept(name, rbd);
                return Void.class;
            });
        }

        private void resolveTypeStringValue(TypedStringValue typedStringValue) {
            try {
                typedStringValue.resolveTargetType(this.beanFactory.getBeanClassLoader());
            } catch (ClassNotFoundException e) {
            }
        }

        private Class<?> resolveBeanType(AbstractBeanDefinition bd) {
            if (!bd.hasBeanClass()) {
                try {
                    bd.resolveBeanClass(this.beanFactory.getBeanClassLoader());
                } catch (ClassNotFoundException e) {
                }
            }
            return bd.getResolvableType().toClass();
        }
    }
}

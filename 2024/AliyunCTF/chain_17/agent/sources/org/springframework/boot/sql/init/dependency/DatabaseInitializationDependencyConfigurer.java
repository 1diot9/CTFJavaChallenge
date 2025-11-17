package org.springframework.boot.sql.init.dependency;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.aot.AotDetector;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/sql/init/dependency/DatabaseInitializationDependencyConfigurer.class */
public class DatabaseInitializationDependencyConfigurer implements ImportBeanDefinitionRegistrar {
    @Override // org.springframework.context.annotation.ImportBeanDefinitionRegistrar
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        String name = DependsOnDatabaseInitializationPostProcessor.class.getName();
        if (!registry.containsBeanDefinition(name)) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition((Class<?>) DependsOnDatabaseInitializationPostProcessor.class);
            registry.registerBeanDefinition(name, builder.getBeanDefinition());
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/sql/init/dependency/DatabaseInitializationDependencyConfigurer$DependsOnDatabaseInitializationPostProcessor.class */
    static class DependsOnDatabaseInitializationPostProcessor implements BeanFactoryPostProcessor, EnvironmentAware, Ordered {
        private Environment environment;

        DependsOnDatabaseInitializationPostProcessor() {
        }

        @Override // org.springframework.context.EnvironmentAware
        public void setEnvironment(Environment environment) {
            this.environment = environment;
        }

        @Override // org.springframework.core.Ordered
        public int getOrder() {
            return 0;
        }

        @Override // org.springframework.beans.factory.config.BeanFactoryPostProcessor
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
            if (AotDetector.useGeneratedArtifacts()) {
                return;
            }
            InitializerBeanNames initializerBeanNames = detectInitializerBeanNames(beanFactory);
            if (initializerBeanNames.isEmpty()) {
                return;
            }
            Set<String> previousInitializerBeanNamesBatch = null;
            for (Set<String> initializerBeanNamesBatch : initializerBeanNames.batchedBeanNames()) {
                for (String initializerBeanName : initializerBeanNamesBatch) {
                    BeanDefinition beanDefinition = getBeanDefinition(initializerBeanName, beanFactory);
                    beanDefinition.setDependsOn(merge(beanDefinition.getDependsOn(), previousInitializerBeanNamesBatch));
                }
                previousInitializerBeanNamesBatch = initializerBeanNamesBatch;
            }
            for (String dependsOnInitializationBeanNames : detectDependsOnInitializationBeanNames(beanFactory)) {
                BeanDefinition beanDefinition2 = getBeanDefinition(dependsOnInitializationBeanNames, beanFactory);
                beanDefinition2.setDependsOn(merge(beanDefinition2.getDependsOn(), initializerBeanNames.beanNames()));
            }
        }

        private String[] merge(String[] source, Set<String> additional) {
            if (CollectionUtils.isEmpty(additional)) {
                return source;
            }
            Set<String> result = new LinkedHashSet<>((Collection<? extends String>) (source != null ? Arrays.asList(source) : Collections.emptySet()));
            result.addAll(additional);
            return StringUtils.toStringArray(result);
        }

        private InitializerBeanNames detectInitializerBeanNames(ConfigurableListableBeanFactory beanFactory) {
            List<DatabaseInitializerDetector> detectors = getDetectors(beanFactory, DatabaseInitializerDetector.class);
            InitializerBeanNames initializerBeanNames = new InitializerBeanNames();
            for (DatabaseInitializerDetector detector : detectors) {
                for (String beanName : detector.detect(beanFactory)) {
                    BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
                    beanDefinition.setAttribute(DatabaseInitializerDetector.class.getName(), detector.getClass().getName());
                    initializerBeanNames.detected(detector, beanName);
                }
            }
            Iterator<DatabaseInitializerDetector> it = detectors.iterator();
            while (it.hasNext()) {
                it.next().detectionComplete(beanFactory, initializerBeanNames.beanNames());
            }
            return initializerBeanNames;
        }

        private Collection<String> detectDependsOnInitializationBeanNames(ConfigurableListableBeanFactory beanFactory) {
            List<DependsOnDatabaseInitializationDetector> detectors = getDetectors(beanFactory, DependsOnDatabaseInitializationDetector.class);
            Set<String> beanNames = new HashSet<>();
            for (DependsOnDatabaseInitializationDetector detector : detectors) {
                beanNames.addAll(detector.detect(beanFactory));
            }
            return beanNames;
        }

        private <T> List<T> getDetectors(ConfigurableListableBeanFactory beanFactory, Class<T> type) {
            SpringFactoriesLoader.ArgumentResolver argumentResolver = SpringFactoriesLoader.ArgumentResolver.of(Environment.class, this.environment);
            return SpringFactoriesLoader.forDefaultResourceLocation(beanFactory.getBeanClassLoader()).load(type, argumentResolver);
        }

        private static BeanDefinition getBeanDefinition(String beanName, ConfigurableListableBeanFactory beanFactory) {
            try {
                return beanFactory.getBeanDefinition(beanName);
            } catch (NoSuchBeanDefinitionException ex) {
                BeanFactory parentBeanFactory = beanFactory.getParentBeanFactory();
                if (parentBeanFactory instanceof ConfigurableListableBeanFactory) {
                    ConfigurableListableBeanFactory configurableBeanFactory = (ConfigurableListableBeanFactory) parentBeanFactory;
                    return getBeanDefinition(beanName, configurableBeanFactory);
                }
                throw ex;
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/sql/init/dependency/DatabaseInitializationDependencyConfigurer$DependsOnDatabaseInitializationPostProcessor$InitializerBeanNames.class */
        public static class InitializerBeanNames {
            private final Map<DatabaseInitializerDetector, Set<String>> byDetectorBeanNames = new LinkedHashMap();
            private final Set<String> beanNames = new LinkedHashSet();

            InitializerBeanNames() {
            }

            private void detected(DatabaseInitializerDetector detector, String beanName) {
                this.byDetectorBeanNames.computeIfAbsent(detector, key -> {
                    return new LinkedHashSet();
                }).add(beanName);
                this.beanNames.add(beanName);
            }

            private boolean isEmpty() {
                return this.beanNames.isEmpty();
            }

            private Iterable<Set<String>> batchedBeanNames() {
                return this.byDetectorBeanNames.values();
            }

            private Set<String> beanNames() {
                return Collections.unmodifiableSet(this.beanNames);
            }
        }
    }
}

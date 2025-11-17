package org.springframework.boot.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/ConfigurationWarningsApplicationContextInitializer.class */
public class ConfigurationWarningsApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    private static final Log logger = LogFactory.getLog((Class<?>) ConfigurationWarningsApplicationContextInitializer.class);

    /* JADX INFO: Access modifiers changed from: protected */
    @FunctionalInterface
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/ConfigurationWarningsApplicationContextInitializer$Check.class */
    public interface Check {
        String getWarning(BeanDefinitionRegistry registry);
    }

    @Override // org.springframework.context.ApplicationContextInitializer
    public void initialize(ConfigurableApplicationContext context) {
        context.addBeanFactoryPostProcessor(new ConfigurationWarningsPostProcessor(getChecks()));
    }

    protected Check[] getChecks() {
        return new Check[]{new ComponentScanPackageCheck()};
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/ConfigurationWarningsApplicationContextInitializer$ConfigurationWarningsPostProcessor.class */
    protected static final class ConfigurationWarningsPostProcessor implements PriorityOrdered, BeanDefinitionRegistryPostProcessor {
        private final Check[] checks;

        public ConfigurationWarningsPostProcessor(Check[] checks) {
            this.checks = checks;
        }

        @Override // org.springframework.core.Ordered
        public int getOrder() {
            return 2147483646;
        }

        @Override // org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor, org.springframework.beans.factory.config.BeanFactoryPostProcessor
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        }

        @Override // org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
        public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
            for (Check check : this.checks) {
                String message = check.getWarning(registry);
                if (StringUtils.hasLength(message)) {
                    warn(message);
                }
            }
        }

        private void warn(String message) {
            if (ConfigurationWarningsApplicationContextInitializer.logger.isWarnEnabled()) {
                ConfigurationWarningsApplicationContextInitializer.logger.warn(String.format("%n%n** WARNING ** : %s%n%n", message));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/ConfigurationWarningsApplicationContextInitializer$ComponentScanPackageCheck.class */
    public static class ComponentScanPackageCheck implements Check {
        private static final Set<String> PROBLEM_PACKAGES;

        protected ComponentScanPackageCheck() {
        }

        static {
            Set<String> packages = new HashSet<>();
            packages.add("org.springframework");
            packages.add("org");
            PROBLEM_PACKAGES = Collections.unmodifiableSet(packages);
        }

        @Override // org.springframework.boot.context.ConfigurationWarningsApplicationContextInitializer.Check
        public String getWarning(BeanDefinitionRegistry registry) {
            Set<String> scannedPackages = getComponentScanningPackages(registry);
            List<String> problematicPackages = getProblematicPackages(scannedPackages);
            if (problematicPackages.isEmpty()) {
                return null;
            }
            return "Your ApplicationContext is unlikely to start due to a @ComponentScan of " + StringUtils.collectionToDelimitedString(problematicPackages, ", ") + ".";
        }

        protected Set<String> getComponentScanningPackages(BeanDefinitionRegistry registry) {
            Set<String> packages = new LinkedHashSet<>();
            String[] names = registry.getBeanDefinitionNames();
            for (String name : names) {
                BeanDefinition definition = registry.getBeanDefinition(name);
                if (definition instanceof AnnotatedBeanDefinition) {
                    AnnotatedBeanDefinition annotatedDefinition = (AnnotatedBeanDefinition) definition;
                    addComponentScanningPackages(packages, annotatedDefinition.getMetadata());
                }
            }
            return packages;
        }

        private void addComponentScanningPackages(Set<String> packages, AnnotationMetadata metadata) {
            AnnotationAttributes attributes = AnnotationAttributes.fromMap(metadata.getAnnotationAttributes(ComponentScan.class.getName(), true));
            if (attributes != null) {
                addPackages(packages, attributes.getStringArray("value"));
                addPackages(packages, attributes.getStringArray("basePackages"));
                addClasses(packages, attributes.getStringArray("basePackageClasses"));
                if (packages.isEmpty()) {
                    packages.add(ClassUtils.getPackageName(metadata.getClassName()));
                }
            }
        }

        private void addPackages(Set<String> packages, String[] values) {
            if (values != null) {
                Collections.addAll(packages, values);
            }
        }

        private void addClasses(Set<String> packages, String[] values) {
            if (values != null) {
                for (String value : values) {
                    packages.add(ClassUtils.getPackageName(value));
                }
            }
        }

        private List<String> getProblematicPackages(Set<String> scannedPackages) {
            List<String> problematicPackages = new ArrayList<>();
            for (String scannedPackage : scannedPackages) {
                if (isProblematicPackage(scannedPackage)) {
                    problematicPackages.add(getDisplayName(scannedPackage));
                }
            }
            return problematicPackages;
        }

        private boolean isProblematicPackage(String scannedPackage) {
            if (scannedPackage == null || scannedPackage.isEmpty()) {
                return true;
            }
            return PROBLEM_PACKAGES.contains(scannedPackage);
        }

        private String getDisplayName(String scannedPackage) {
            if (scannedPackage == null || scannedPackage.isEmpty()) {
                return "the default package";
            }
            return "'" + scannedPackage + "'";
        }
    }
}

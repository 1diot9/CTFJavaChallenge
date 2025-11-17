package org.springframework.boot.autoconfigure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.context.annotation.DeterminableImports;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/AutoConfigurationPackages.class */
public abstract class AutoConfigurationPackages {
    private static final Log logger = LogFactory.getLog((Class<?>) AutoConfigurationPackages.class);
    private static final String BEAN = AutoConfigurationPackages.class.getName();

    public static boolean has(BeanFactory beanFactory) {
        return beanFactory.containsBean(BEAN) && !get(beanFactory).isEmpty();
    }

    public static List<String> get(BeanFactory beanFactory) {
        try {
            return ((BasePackages) beanFactory.getBean(BEAN, BasePackages.class)).get();
        } catch (NoSuchBeanDefinitionException e) {
            throw new IllegalStateException("Unable to retrieve @EnableAutoConfiguration base packages");
        }
    }

    public static void register(BeanDefinitionRegistry registry, String... packageNames) {
        if (registry.containsBeanDefinition(BEAN)) {
            addBasePackages(registry.getBeanDefinition(BEAN), packageNames);
            return;
        }
        RootBeanDefinition beanDefinition = new RootBeanDefinition((Class<?>) BasePackages.class);
        beanDefinition.setRole(2);
        addBasePackages(beanDefinition, packageNames);
        registry.registerBeanDefinition(BEAN, beanDefinition);
    }

    private static void addBasePackages(BeanDefinition beanDefinition, String[] additionalBasePackages) {
        ConstructorArgumentValues constructorArgumentValues = beanDefinition.getConstructorArgumentValues();
        if (constructorArgumentValues.hasIndexedArgumentValue(0)) {
            String[] existingPackages = (String[]) constructorArgumentValues.getIndexedArgumentValue(0, String[].class).getValue();
            constructorArgumentValues.addIndexedArgumentValue(0, Stream.concat(Stream.of((Object[]) existingPackages), Stream.of((Object[]) additionalBasePackages)).distinct().toArray(x$0 -> {
                return new String[x$0];
            }));
        } else {
            constructorArgumentValues.addIndexedArgumentValue(0, additionalBasePackages);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/AutoConfigurationPackages$Registrar.class */
    static class Registrar implements ImportBeanDefinitionRegistrar, DeterminableImports {
        Registrar() {
        }

        @Override // org.springframework.context.annotation.ImportBeanDefinitionRegistrar
        public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
            AutoConfigurationPackages.register(registry, (String[]) new PackageImports(metadata).getPackageNames().toArray(new String[0]));
        }

        @Override // org.springframework.boot.context.annotation.DeterminableImports
        public Set<Object> determineImports(AnnotationMetadata metadata) {
            return Collections.singleton(new PackageImports(metadata));
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/AutoConfigurationPackages$PackageImports.class */
    private static final class PackageImports {
        private final List<String> packageNames;

        PackageImports(AnnotationMetadata metadata) {
            AnnotationAttributes attributes = AnnotationAttributes.fromMap(metadata.getAnnotationAttributes(AutoConfigurationPackage.class.getName(), false));
            List<String> packageNames = new ArrayList<>(Arrays.asList(attributes.getStringArray("basePackages")));
            for (Class<?> basePackageClass : attributes.getClassArray("basePackageClasses")) {
                packageNames.add(basePackageClass.getPackage().getName());
            }
            if (packageNames.isEmpty()) {
                packageNames.add(ClassUtils.getPackageName(metadata.getClassName()));
            }
            this.packageNames = Collections.unmodifiableList(packageNames);
        }

        List<String> getPackageNames() {
            return this.packageNames;
        }

        public boolean equals(Object obj) {
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            return this.packageNames.equals(((PackageImports) obj).packageNames);
        }

        public int hashCode() {
            return this.packageNames.hashCode();
        }

        public String toString() {
            return "Package Imports " + this.packageNames;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/AutoConfigurationPackages$BasePackages.class */
    public static final class BasePackages {
        private final List<String> packages;
        private boolean loggedBasePackageInfo;

        BasePackages(String... names) {
            List<String> packages = new ArrayList<>();
            for (String name : names) {
                if (StringUtils.hasText(name)) {
                    packages.add(name);
                }
            }
            this.packages = packages;
        }

        List<String> get() {
            if (!this.loggedBasePackageInfo) {
                if (this.packages.isEmpty()) {
                    if (AutoConfigurationPackages.logger.isWarnEnabled()) {
                        AutoConfigurationPackages.logger.warn("@EnableAutoConfiguration was declared on a class in the default package. Automatic @Repository and @Entity scanning is not enabled.");
                    }
                } else if (AutoConfigurationPackages.logger.isDebugEnabled()) {
                    String packageNames = StringUtils.collectionToCommaDelimitedString(this.packages);
                    AutoConfigurationPackages.logger.debug("@EnableAutoConfiguration was declared on a class in the package '" + packageNames + "'. Automatic @Repository and @Entity scanning is enabled.");
                }
                this.loggedBasePackageInfo = true;
            }
            return this.packages;
        }
    }
}

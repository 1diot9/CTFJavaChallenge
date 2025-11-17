package org.springframework.boot.autoconfigure.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/domain/EntityScanPackages.class */
public class EntityScanPackages {
    private static final String BEAN = EntityScanPackages.class.getName();
    private static final EntityScanPackages NONE = new EntityScanPackages(new String[0]);
    private final List<String> packageNames;

    EntityScanPackages(String... packageNames) {
        List<String> packages = new ArrayList<>();
        for (String name : packageNames) {
            if (StringUtils.hasText(name)) {
                packages.add(name);
            }
        }
        this.packageNames = Collections.unmodifiableList(packages);
    }

    public List<String> getPackageNames() {
        return this.packageNames;
    }

    public static EntityScanPackages get(BeanFactory beanFactory) {
        try {
            return (EntityScanPackages) beanFactory.getBean(BEAN, EntityScanPackages.class);
        } catch (NoSuchBeanDefinitionException e) {
            return NONE;
        }
    }

    public static void register(BeanDefinitionRegistry registry, String... packageNames) {
        Assert.notNull(registry, "Registry must not be null");
        Assert.notNull(packageNames, "PackageNames must not be null");
        register(registry, Arrays.asList(packageNames));
    }

    public static void register(BeanDefinitionRegistry registry, Collection<String> packageNames) {
        Assert.notNull(registry, "Registry must not be null");
        Assert.notNull(packageNames, "PackageNames must not be null");
        if (registry.containsBeanDefinition(BEAN)) {
            EntityScanPackagesBeanDefinition beanDefinition = (EntityScanPackagesBeanDefinition) registry.getBeanDefinition(BEAN);
            beanDefinition.addPackageNames(packageNames);
        } else {
            registry.registerBeanDefinition(BEAN, new EntityScanPackagesBeanDefinition(packageNames));
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/domain/EntityScanPackages$Registrar.class */
    static class Registrar implements ImportBeanDefinitionRegistrar {
        private final Environment environment;

        Registrar(Environment environment) {
            this.environment = environment;
        }

        @Override // org.springframework.context.annotation.ImportBeanDefinitionRegistrar
        public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
            EntityScanPackages.register(registry, getPackagesToScan(metadata));
        }

        private Set<String> getPackagesToScan(AnnotationMetadata metadata) {
            AnnotationAttributes attributes = AnnotationAttributes.fromMap(metadata.getAnnotationAttributes(EntityScan.class.getName()));
            Set<String> packagesToScan = new LinkedHashSet<>();
            for (String basePackage : attributes.getStringArray("basePackages")) {
                String[] tokenized = StringUtils.tokenizeToStringArray(this.environment.resolvePlaceholders(basePackage), ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
                Collections.addAll(packagesToScan, tokenized);
            }
            for (Class<?> basePackageClass : attributes.getClassArray("basePackageClasses")) {
                packagesToScan.add(this.environment.resolvePlaceholders(ClassUtils.getPackageName(basePackageClass)));
            }
            if (packagesToScan.isEmpty()) {
                String packageName = ClassUtils.getPackageName(metadata.getClassName());
                Assert.state(StringUtils.hasLength(packageName), "@EntityScan cannot be used with the default package");
                return Collections.singleton(packageName);
            }
            return packagesToScan;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/domain/EntityScanPackages$EntityScanPackagesBeanDefinition.class */
    public static class EntityScanPackagesBeanDefinition extends GenericBeanDefinition {
        private final Set<String> packageNames = new LinkedHashSet();

        EntityScanPackagesBeanDefinition(Collection<String> packageNames) {
            setBeanClass(EntityScanPackages.class);
            setRole(2);
            addPackageNames(packageNames);
        }

        private void addPackageNames(Collection<String> additionalPackageNames) {
            this.packageNames.addAll(additionalPackageNames);
            getConstructorArgumentValues().addIndexedArgumentValue(0, StringUtils.toStringArray(this.packageNames));
        }
    }
}

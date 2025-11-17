package org.springframework.boot.autoconfigure;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/AbstractDependsOnBeanFactoryPostProcessor.class */
public abstract class AbstractDependsOnBeanFactoryPostProcessor implements BeanFactoryPostProcessor, Ordered {
    private final Class<?> beanClass;
    private final Class<? extends FactoryBean<?>> factoryBeanClass;
    private final Function<ListableBeanFactory, Set<String>> dependsOn;

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractDependsOnBeanFactoryPostProcessor(Class<?> beanClass, Class<? extends FactoryBean<?>> factoryBeanClass, String... dependsOn) {
        this.beanClass = beanClass;
        this.factoryBeanClass = factoryBeanClass;
        this.dependsOn = beanFactory -> {
            return new HashSet(Arrays.asList(dependsOn));
        };
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractDependsOnBeanFactoryPostProcessor(Class<?> beanClass, Class<? extends FactoryBean<?>> factoryBeanClass, Class<?>... dependencyTypes) {
        this.beanClass = beanClass;
        this.factoryBeanClass = factoryBeanClass;
        this.dependsOn = beanFactory -> {
            return (Set) Arrays.stream(dependencyTypes).flatMap(dependencyType -> {
                return getBeanNames(beanFactory, dependencyType).stream();
            }).collect(Collectors.toSet());
        };
    }

    protected AbstractDependsOnBeanFactoryPostProcessor(Class<?> beanClass, String... dependsOn) {
        this(beanClass, (Class<? extends FactoryBean<?>>) null, dependsOn);
    }

    protected AbstractDependsOnBeanFactoryPostProcessor(Class<?> beanClass, Class<?>... dependencyTypes) {
        this(beanClass, (Class<? extends FactoryBean<?>>) null, dependencyTypes);
    }

    @Override // org.springframework.beans.factory.config.BeanFactoryPostProcessor
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        for (String beanName : getBeanNames(beanFactory)) {
            BeanDefinition definition = getBeanDefinition(beanName, beanFactory);
            String[] dependencies = definition.getDependsOn();
            for (String dependencyName : this.dependsOn.apply(beanFactory)) {
                dependencies = StringUtils.addStringToArray(dependencies, dependencyName);
            }
            definition.setDependsOn(dependencies);
        }
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return 0;
    }

    private Set<String> getBeanNames(ListableBeanFactory beanFactory) {
        Set<String> names = getBeanNames(beanFactory, this.beanClass);
        if (this.factoryBeanClass != null) {
            names.addAll(getBeanNames(beanFactory, this.factoryBeanClass));
        }
        return names;
    }

    private static Set<String> getBeanNames(ListableBeanFactory beanFactory, Class<?> beanClass) {
        String[] names = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(beanFactory, beanClass, true, false);
        return (Set) Arrays.stream(names).map(BeanFactoryUtils::transformedBeanName).collect(Collectors.toSet());
    }

    private static BeanDefinition getBeanDefinition(String beanName, ConfigurableListableBeanFactory beanFactory) {
        try {
            return beanFactory.getBeanDefinition(beanName);
        } catch (NoSuchBeanDefinitionException ex) {
            BeanFactory parentBeanFactory = beanFactory.getParentBeanFactory();
            if (parentBeanFactory instanceof ConfigurableListableBeanFactory) {
                ConfigurableListableBeanFactory listableBeanFactory = (ConfigurableListableBeanFactory) parentBeanFactory;
                return getBeanDefinition(beanName, listableBeanFactory);
            }
            throw ex;
        }
    }
}

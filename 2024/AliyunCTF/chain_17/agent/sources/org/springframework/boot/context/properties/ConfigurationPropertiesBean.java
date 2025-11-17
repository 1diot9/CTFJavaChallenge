package org.springframework.boot.context.properties;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.context.properties.bind.BindConstructorProvider;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/ConfigurationPropertiesBean.class */
public final class ConfigurationPropertiesBean {
    private static final org.springframework.boot.context.properties.bind.BindMethod JAVA_BEAN_BIND_METHOD = org.springframework.boot.context.properties.bind.BindMethod.JAVA_BEAN;
    private static final org.springframework.boot.context.properties.bind.BindMethod VALUE_OBJECT_BIND_METHOD = org.springframework.boot.context.properties.bind.BindMethod.VALUE_OBJECT;
    private final String name;
    private final Object instance;
    private final Bindable<?> bindTarget;

    private ConfigurationPropertiesBean(String name, Object instance, Bindable<?> bindTarget) {
        this.name = name;
        this.instance = instance;
        this.bindTarget = bindTarget;
    }

    public String getName() {
        return this.name;
    }

    public Object getInstance() {
        return this.instance;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Class<?> getType() {
        return this.bindTarget.getType().resolve();
    }

    @Deprecated(since = "3.0.8", forRemoval = true)
    public BindMethod getBindMethod() {
        return BindMethod.from(this.bindTarget.getBindMethod());
    }

    public ConfigurationProperties getAnnotation() {
        return (ConfigurationProperties) this.bindTarget.getAnnotation(ConfigurationProperties.class);
    }

    public Bindable<?> asBindTarget() {
        return this.bindTarget;
    }

    public static Map<String, ConfigurationPropertiesBean> getAll(ApplicationContext applicationContext) {
        Assert.notNull(applicationContext, "ApplicationContext must not be null");
        if (applicationContext instanceof ConfigurableApplicationContext) {
            ConfigurableApplicationContext configurableContext = (ConfigurableApplicationContext) applicationContext;
            return getAll(configurableContext);
        }
        Map<String, ConfigurationPropertiesBean> propertiesBeans = new LinkedHashMap<>();
        applicationContext.getBeansWithAnnotation(ConfigurationProperties.class).forEach((name, instance) -> {
            ConfigurationPropertiesBean propertiesBean = get(applicationContext, instance, name);
            if (propertiesBean != null) {
                propertiesBeans.put(name, propertiesBean);
            }
        });
        return propertiesBeans;
    }

    private static Map<String, ConfigurationPropertiesBean> getAll(ConfigurableApplicationContext applicationContext) {
        Map<String, ConfigurationPropertiesBean> propertiesBeans = new LinkedHashMap<>();
        ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
        Iterator<String> beanNames = beanFactory.getBeanNamesIterator();
        while (beanNames.hasNext()) {
            String beanName = beanNames.next();
            if (isConfigurationPropertiesBean(beanFactory, beanName)) {
                try {
                    Object bean = beanFactory.getBean(beanName);
                    ConfigurationPropertiesBean propertiesBean = get(applicationContext, bean, beanName);
                    if (propertiesBean != null) {
                        propertiesBeans.put(beanName, propertiesBean);
                    }
                } catch (Exception e) {
                }
            }
        }
        return propertiesBeans;
    }

    private static boolean isConfigurationPropertiesBean(ConfigurableListableBeanFactory beanFactory, String beanName) {
        try {
            if (beanFactory.getBeanDefinition(beanName).isAbstract()) {
                return false;
            }
            if (beanFactory.findAnnotationOnBean(beanName, ConfigurationProperties.class) != null) {
                return true;
            }
            Method factoryMethod = findFactoryMethod(beanFactory, beanName);
            return findMergedAnnotation(factoryMethod, ConfigurationProperties.class).isPresent();
        } catch (NoSuchBeanDefinitionException e) {
            return false;
        }
    }

    public static ConfigurationPropertiesBean get(ApplicationContext applicationContext, Object bean, String beanName) {
        Method factoryMethod = findFactoryMethod(applicationContext, beanName);
        Bindable<Object> bindTarget = createBindTarget(bean, bean.getClass(), factoryMethod);
        if (bindTarget == null) {
            return null;
        }
        Bindable<Object> bindTarget2 = bindTarget.withBindMethod(BindMethodAttribute.get(applicationContext, beanName));
        if (bindTarget2.getBindMethod() == null && factoryMethod != null) {
            bindTarget2 = bindTarget2.withBindMethod(JAVA_BEAN_BIND_METHOD);
        }
        if (bindTarget2.getBindMethod() != VALUE_OBJECT_BIND_METHOD) {
            bindTarget2 = bindTarget2.withExistingValue(bean);
        }
        return create(beanName, bean, bindTarget2);
    }

    private static Method findFactoryMethod(ApplicationContext applicationContext, String beanName) {
        if (applicationContext instanceof ConfigurableApplicationContext) {
            ConfigurableApplicationContext configurableContext = (ConfigurableApplicationContext) applicationContext;
            return findFactoryMethod(configurableContext, beanName);
        }
        return null;
    }

    private static Method findFactoryMethod(ConfigurableApplicationContext applicationContext, String beanName) {
        return findFactoryMethod(applicationContext.getBeanFactory(), beanName);
    }

    private static Method findFactoryMethod(ConfigurableListableBeanFactory beanFactory, String beanName) {
        if (beanFactory.containsBeanDefinition(beanName)) {
            BeanDefinition beanDefinition = beanFactory.getMergedBeanDefinition(beanName);
            if (beanDefinition instanceof RootBeanDefinition) {
                RootBeanDefinition rootBeanDefinition = (RootBeanDefinition) beanDefinition;
                return rootBeanDefinition.getResolvedFactoryMethod();
            }
            return null;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ConfigurationPropertiesBean forValueObject(Class<?> beanType, String beanName) {
        Bindable<Object> bindTarget = createBindTarget(null, beanType, null);
        Assert.state(bindTarget != null && deduceBindMethod(bindTarget) == VALUE_OBJECT_BIND_METHOD, (Supplier<String>) () -> {
            return "Bean '" + beanName + "' is not a @ConfigurationProperties value object";
        });
        return create(beanName, null, bindTarget.withBindMethod(VALUE_OBJECT_BIND_METHOD));
    }

    private static Bindable<Object> createBindTarget(Object bean, Class<?> beanType, Method factoryMethod) {
        ResolvableType type = factoryMethod != null ? ResolvableType.forMethodReturnType(factoryMethod) : ResolvableType.forClass(beanType);
        Annotation[] annotations = findAnnotations(bean, beanType, factoryMethod);
        if (annotations != null) {
            return Bindable.of(type).withAnnotations(annotations);
        }
        return null;
    }

    private static Annotation[] findAnnotations(Object instance, Class<?> type, Method factory) {
        ConfigurationProperties annotation = (ConfigurationProperties) findAnnotation(instance, type, factory, ConfigurationProperties.class);
        if (annotation == null) {
            return null;
        }
        Validated validated = (Validated) findAnnotation(instance, type, factory, Validated.class);
        return validated != null ? new Annotation[]{annotation, validated} : new Annotation[]{annotation};
    }

    private static <A extends Annotation> A findAnnotation(Object instance, Class<?> type, Method factory, Class<A> annotationType) {
        MergedAnnotation<A> annotation = MergedAnnotation.missing();
        if (factory != null) {
            annotation = findMergedAnnotation(factory, annotationType);
        }
        if (!annotation.isPresent()) {
            annotation = findMergedAnnotation(type, annotationType);
        }
        if (!annotation.isPresent() && AopUtils.isAopProxy(instance)) {
            annotation = MergedAnnotations.from(AopUtils.getTargetClass(instance), MergedAnnotations.SearchStrategy.TYPE_HIERARCHY).get(annotationType);
        }
        if (annotation.isPresent()) {
            return annotation.synthesize();
        }
        return null;
    }

    private static <A extends Annotation> MergedAnnotation<A> findMergedAnnotation(AnnotatedElement element, Class<A> annotationType) {
        return element != null ? MergedAnnotations.from(element, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY).get(annotationType) : MergedAnnotation.missing();
    }

    private static ConfigurationPropertiesBean create(String name, Object instance, Bindable<Object> bindTarget) {
        if (bindTarget != null) {
            return new ConfigurationPropertiesBean(name, instance, bindTarget);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static org.springframework.boot.context.properties.bind.BindMethod deduceBindMethod(Class<?> type) {
        return deduceBindMethod(BindConstructorProvider.DEFAULT.getBindConstructor(type, false));
    }

    static org.springframework.boot.context.properties.bind.BindMethod deduceBindMethod(Bindable<Object> bindable) {
        return deduceBindMethod(BindConstructorProvider.DEFAULT.getBindConstructor((Bindable<?>) bindable, false));
    }

    private static org.springframework.boot.context.properties.bind.BindMethod deduceBindMethod(Constructor<?> bindConstructor) {
        return bindConstructor != null ? VALUE_OBJECT_BIND_METHOD : JAVA_BEAN_BIND_METHOD;
    }

    @Deprecated(since = "3.0.8", forRemoval = true)
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/ConfigurationPropertiesBean$BindMethod.class */
    public enum BindMethod {
        JAVA_BEAN,
        VALUE_OBJECT;

        static BindMethod from(org.springframework.boot.context.properties.bind.BindMethod bindMethod) {
            if (bindMethod == null) {
                return null;
            }
            switch (bindMethod) {
                case VALUE_OBJECT:
                    return VALUE_OBJECT;
                case JAVA_BEAN:
                    return JAVA_BEAN;
                default:
                    throw new IncompatibleClassChangeError();
            }
        }
    }
}

package org.springframework.boot.jackson;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import org.springframework.aot.generate.GenerationContext;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.ReflectionHints;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor;
import org.springframework.beans.factory.aot.BeanFactoryInitializationCode;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/jackson/JsonComponentModule.class */
public class JsonComponentModule extends SimpleModule implements BeanFactoryAware, InitializingBean {
    private BeanFactory beanFactory;

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        registerJsonComponents();
    }

    public void registerJsonComponents() {
        BeanFactory beanFactory = this.beanFactory;
        while (true) {
            BeanFactory beanFactory2 = beanFactory;
            if (beanFactory2 != null) {
                if (beanFactory2 instanceof ListableBeanFactory) {
                    ListableBeanFactory listableBeanFactory = (ListableBeanFactory) beanFactory2;
                    addJsonBeans(listableBeanFactory);
                }
                if (beanFactory2 instanceof HierarchicalBeanFactory) {
                    HierarchicalBeanFactory hierarchicalBeanFactory = (HierarchicalBeanFactory) beanFactory2;
                    beanFactory = hierarchicalBeanFactory.getParentBeanFactory();
                } else {
                    beanFactory = null;
                }
            } else {
                return;
            }
        }
    }

    private void addJsonBeans(ListableBeanFactory beanFactory) {
        Map<String, Object> beans = beanFactory.getBeansWithAnnotation(JsonComponent.class);
        for (Object bean : beans.values()) {
            addJsonBean(bean);
        }
    }

    private void addJsonBean(Object bean) {
        MergedAnnotation<JsonComponent> annotation = MergedAnnotations.from(bean.getClass(), MergedAnnotations.SearchStrategy.TYPE_HIERARCHY).get(JsonComponent.class);
        Class<?>[] types = annotation.getClassArray("type");
        JsonComponent.Scope scope = (JsonComponent.Scope) annotation.getEnum("scope", JsonComponent.Scope.class);
        addJsonBean(bean, types, scope);
    }

    private void addJsonBean(Object bean, Class<?>[] types, JsonComponent.Scope scope) {
        if (bean instanceof JsonSerializer) {
            addJsonSerializerBean((JsonSerializer) bean, scope, types);
        } else if (bean instanceof JsonDeserializer) {
            addJsonDeserializerBean((JsonDeserializer) bean, types);
        } else if (bean instanceof KeyDeserializer) {
            addKeyDeserializerBean((KeyDeserializer) bean, types);
        }
        for (Class<?> innerClass : bean.getClass().getDeclaredClasses()) {
            if (isSuitableInnerClass(innerClass)) {
                Object innerInstance = BeanUtils.instantiateClass(innerClass);
                addJsonBean(innerInstance, types, scope);
            }
        }
    }

    private static boolean isSuitableInnerClass(Class<?> innerClass) {
        return !Modifier.isAbstract(innerClass.getModifiers()) && (JsonSerializer.class.isAssignableFrom(innerClass) || JsonDeserializer.class.isAssignableFrom(innerClass) || KeyDeserializer.class.isAssignableFrom(innerClass));
    }

    private <T> void addJsonSerializerBean(JsonSerializer<T> serializer, JsonComponent.Scope scope, Class<?>[] types) {
        addBeanToModule(serializer, ResolvableType.forClass(JsonSerializer.class, serializer.getClass()).resolveGeneric(new int[0]), types, scope == JsonComponent.Scope.VALUES ? this::addSerializer : this::addKeySerializer);
    }

    private <T> void addJsonDeserializerBean(JsonDeserializer<T> deserializer, Class<?>[] types) {
        addBeanToModule(deserializer, ResolvableType.forClass(JsonDeserializer.class, deserializer.getClass()).resolveGeneric(new int[0]), types, this::addDeserializer);
    }

    private void addKeyDeserializerBean(KeyDeserializer deserializer, Class<?>[] types) {
        Assert.notEmpty(types, "Type must be specified for KeyDeserializer");
        addBeanToModule(deserializer, Object.class, types, this::addKeyDeserializer);
    }

    private <E, T> void addBeanToModule(E element, Class<T> baseType, Class<?>[] types, BiConsumer<Class<T>, E> consumer) {
        if (ObjectUtils.isEmpty((Object[]) types)) {
            consumer.accept(baseType, element);
            return;
        }
        for (Class<?> type : types) {
            Assert.isAssignable(baseType, type);
            consumer.accept(type, element);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/jackson/JsonComponentModule$JsonComponentBeanFactoryInitializationAotProcessor.class */
    static class JsonComponentBeanFactoryInitializationAotProcessor implements BeanFactoryInitializationAotProcessor {
        JsonComponentBeanFactoryInitializationAotProcessor() {
        }

        @Override // org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor
        public BeanFactoryInitializationAotContribution processAheadOfTime(ConfigurableListableBeanFactory beanFactory) {
            String[] jsonComponents = beanFactory.getBeanNamesForAnnotation(JsonComponent.class);
            Map<Class<?>, List<Class<?>>> innerComponents = new HashMap<>();
            for (String jsonComponent : jsonComponents) {
                Class<?> type = beanFactory.getType(jsonComponent, true);
                for (Class<?> declaredClass : type.getDeclaredClasses()) {
                    if (JsonComponentModule.isSuitableInnerClass(declaredClass)) {
                        innerComponents.computeIfAbsent(type, t -> {
                            return new ArrayList();
                        }).add(declaredClass);
                    }
                }
            }
            if (innerComponents.isEmpty()) {
                return null;
            }
            return new JsonComponentAotContribution(innerComponents);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/jackson/JsonComponentModule$JsonComponentAotContribution.class */
    private static final class JsonComponentAotContribution implements BeanFactoryInitializationAotContribution {
        private final Map<Class<?>, List<Class<?>>> innerComponents;

        private JsonComponentAotContribution(Map<Class<?>, List<Class<?>>> innerComponents) {
            this.innerComponents = innerComponents;
        }

        @Override // org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution
        public void applyTo(GenerationContext generationContext, BeanFactoryInitializationCode beanFactoryInitializationCode) {
            ReflectionHints reflection = generationContext.getRuntimeHints().reflection();
            this.innerComponents.forEach((outer, inners) -> {
                reflection.registerType((Class<?>) outer, MemberCategory.DECLARED_CLASSES);
                inners.forEach(inner -> {
                    reflection.registerType((Class<?>) inner, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);
                });
            });
        }
    }
}

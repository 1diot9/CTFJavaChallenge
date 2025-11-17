package org.springframework.boot.jackson;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/jackson/JsonMixinModuleEntries.class */
public final class JsonMixinModuleEntries {
    private final Map<Object, Object> entries;

    private JsonMixinModuleEntries(Builder builder) {
        this.entries = new LinkedHashMap(builder.entries);
    }

    public static JsonMixinModuleEntries create(Consumer<Builder> mixins) {
        Builder builder = new Builder();
        mixins.accept(builder);
        return builder.build();
    }

    public static JsonMixinModuleEntries scan(ApplicationContext context, Collection<String> basePackages) {
        return create(builder -> {
            if (ObjectUtils.isEmpty(basePackages)) {
                return;
            }
            JsonMixinComponentScanner scanner = new JsonMixinComponentScanner();
            scanner.setEnvironment(context.getEnvironment());
            scanner.setResourceLoader(context);
            Iterator it = basePackages.iterator();
            while (it.hasNext()) {
                String basePackage = (String) it.next();
                if (StringUtils.hasText(basePackage)) {
                    for (BeanDefinition candidate : scanner.findCandidateComponents(basePackage)) {
                        Class<?> mixinClass = ClassUtils.resolveClassName(candidate.getBeanClassName(), context.getClassLoader());
                        registerMixinClass(builder, mixinClass);
                    }
                }
            }
        });
    }

    private static void registerMixinClass(Builder builder, Class<?> mixinClass) {
        MergedAnnotation<JsonMixin> annotation = MergedAnnotations.from(mixinClass, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY).get(JsonMixin.class);
        for (Class<?> targetType : annotation.getClassArray("type")) {
            builder.and(targetType, mixinClass);
        }
    }

    public void doWithEntry(ClassLoader classLoader, BiConsumer<Class<?>, Class<?>> action) {
        this.entries.forEach((type, mixin) -> {
            action.accept(resolveClassNameIfNecessary(type, classLoader), resolveClassNameIfNecessary(mixin, classLoader));
        });
    }

    private Class<?> resolveClassNameIfNecessary(Object type, ClassLoader classLoader) {
        if (!(type instanceof Class)) {
            return ClassUtils.resolveClassName((String) type, classLoader);
        }
        Class<?> clazz = (Class) type;
        return clazz;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/jackson/JsonMixinModuleEntries$Builder.class */
    public static class Builder {
        private final Map<Object, Object> entries = new LinkedHashMap();

        Builder() {
        }

        public Builder and(String typeClassName, String mixinClassName) {
            this.entries.put(typeClassName, mixinClassName);
            return this;
        }

        public Builder and(Class<?> type, Class<?> mixinClass) {
            this.entries.put(type, mixinClass);
            return this;
        }

        JsonMixinModuleEntries build() {
            return new JsonMixinModuleEntries(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/jackson/JsonMixinModuleEntries$JsonMixinComponentScanner.class */
    public static class JsonMixinComponentScanner extends ClassPathScanningCandidateComponentProvider {
        JsonMixinComponentScanner() {
            addIncludeFilter(new AnnotationTypeFilter(JsonMixin.class));
        }

        @Override // org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
        protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
            return true;
        }
    }
}

package org.springframework.boot.autoconfigure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.boot.context.annotation.Configurations;
import org.springframework.core.Ordered;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.util.ClassUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/AutoConfigurations.class */
public class AutoConfigurations extends Configurations implements Ordered {
    private static final AutoConfigurationSorter SORTER = new AutoConfigurationSorter(new SimpleMetadataReaderFactory(), null);
    private static final Ordered ORDER = new AutoConfigurationImportSelector();

    @Override // org.springframework.boot.context.annotation.Configurations
    protected /* bridge */ /* synthetic */ Configurations merge(Set mergedClasses) {
        return merge((Set<Class<?>>) mergedClasses);
    }

    protected AutoConfigurations(Collection<Class<?>> classes) {
        super(classes);
    }

    @Override // org.springframework.boot.context.annotation.Configurations
    protected Collection<Class<?>> sort(Collection<Class<?>> classes) {
        List<String> names = classes.stream().map((v0) -> {
            return v0.getName();
        }).toList();
        List<String> sorted = SORTER.getInPriorityOrder(names);
        return (Collection) sorted.stream().map(className -> {
            return ClassUtils.resolveClassName(className, null);
        }).collect(Collectors.toCollection(ArrayList::new));
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return ORDER.getOrder();
    }

    @Override // org.springframework.boot.context.annotation.Configurations
    protected AutoConfigurations merge(Set<Class<?>> mergedClasses) {
        return new AutoConfigurations(mergedClasses);
    }

    public static AutoConfigurations of(Class<?>... classes) {
        return new AutoConfigurations(Arrays.asList(classes));
    }
}

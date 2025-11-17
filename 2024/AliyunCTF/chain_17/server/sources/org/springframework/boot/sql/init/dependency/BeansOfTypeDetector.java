package org.springframework.boot.sql.init.dependency;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/sql/init/dependency/BeansOfTypeDetector.class */
class BeansOfTypeDetector {
    private final Set<Class<?>> types;

    /* JADX INFO: Access modifiers changed from: package-private */
    public BeansOfTypeDetector(Set<Class<?>> types) {
        this.types = types;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Set<String> detect(ListableBeanFactory beanFactory) {
        Set<String> beanNames = new HashSet<>();
        for (Class<?> type : this.types) {
            try {
                String[] names = beanFactory.getBeanNamesForType(type, true, false);
                Stream map = Arrays.stream(names).map(BeanFactoryUtils::transformedBeanName);
                Objects.requireNonNull(beanNames);
                map.forEach((v1) -> {
                    r1.add(v1);
                });
            } catch (Throwable th) {
            }
        }
        return beanNames;
    }
}

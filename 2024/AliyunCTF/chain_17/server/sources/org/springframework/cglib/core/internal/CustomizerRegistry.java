package org.springframework.cglib.core.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.cglib.core.Customizer;
import org.springframework.cglib.core.KeyFactoryCustomizer;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/core/internal/CustomizerRegistry.class */
public class CustomizerRegistry {
    private final Class[] customizerTypes;
    private Map<Class, List<KeyFactoryCustomizer>> customizers = new HashMap();

    public CustomizerRegistry(Class[] customizerTypes) {
        this.customizerTypes = customizerTypes;
    }

    public void add(KeyFactoryCustomizer customizer) {
        Class<?> cls = customizer.getClass();
        for (Class type : this.customizerTypes) {
            if (type.isAssignableFrom(cls)) {
                List<KeyFactoryCustomizer> list = this.customizers.computeIfAbsent(type, k -> {
                    return new ArrayList();
                });
                list.add(customizer);
            }
        }
    }

    public <T> List<T> get(Class<T> klass) {
        List<T> list = (List) this.customizers.get(klass);
        if (list == null) {
            return Collections.emptyList();
        }
        return list;
    }

    @Deprecated
    public static CustomizerRegistry singleton(Customizer customizer) {
        CustomizerRegistry registry = new CustomizerRegistry(new Class[]{Customizer.class});
        registry.add(customizer);
        return registry;
    }
}

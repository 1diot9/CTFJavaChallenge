package org.springframework.util;

import java.util.List;
import java.util.Map;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/MultiValueMap.class */
public interface MultiValueMap<K, V> extends Map<K, List<V>> {
    @Nullable
    V getFirst(K key);

    void add(K key, @Nullable V value);

    void addAll(K key, List<? extends V> values);

    void addAll(MultiValueMap<K, V> values);

    void set(K key, @Nullable V value);

    void setAll(Map<K, V> values);

    Map<K, V> toSingleValueMap();

    default void addIfAbsent(K key, @Nullable V value) {
        if (!containsKey(key)) {
            add(key, value);
        }
    }
}

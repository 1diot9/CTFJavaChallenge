package cn.hutool.core.map;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import java.util.Map;
import java.util.function.Consumer;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/map/TreeEntry.class */
public interface TreeEntry<K, V> extends Map.Entry<K, V> {
    @Override // java.util.Map.Entry
    boolean equals(Object obj);

    @Override // java.util.Map.Entry
    int hashCode();

    int getWeight();

    TreeEntry<K, V> getRoot();

    TreeEntry<K, V> getDeclaredParent();

    TreeEntry<K, V> getParent(K k);

    void forEachChild(boolean z, Consumer<TreeEntry<K, V>> consumer);

    Map<K, TreeEntry<K, V>> getDeclaredChildren();

    Map<K, TreeEntry<K, V>> getChildren();

    TreeEntry<K, V> getChild(K k);

    default boolean hasParent() {
        return ObjectUtil.isNotNull(getDeclaredParent());
    }

    default boolean containsParent(K key) {
        return ObjectUtil.isNotNull(getParent(key));
    }

    default boolean hasChildren() {
        return CollUtil.isNotEmpty((Map<?, ?>) getDeclaredChildren());
    }

    default boolean containsChild(K key) {
        return ObjectUtil.isNotNull(getChild(key));
    }
}

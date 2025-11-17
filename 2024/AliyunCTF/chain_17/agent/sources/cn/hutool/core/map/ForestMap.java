package cn.hutool.core.map;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.util.ObjectUtil;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/map/ForestMap.class */
public interface ForestMap<K, V> extends Map<K, TreeEntry<K, V>> {
    @Override // java.util.Map
    TreeEntry<K, V> remove(Object obj);

    @Override // java.util.Map
    void clear();

    TreeEntry<K, V> putNode(K k, V v);

    void putLinkedNodes(K k, K k2, V v);

    void linkNodes(K k, K k2, BiConsumer<TreeEntry<K, V>, TreeEntry<K, V>> biConsumer);

    void unlinkNode(K k, K k2);

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.Map
    /* bridge */ /* synthetic */ default Object put(Object obj, Object obj2) {
        return put((ForestMap<K, V>) obj, (TreeEntry<ForestMap<K, V>, V>) obj2);
    }

    default TreeEntry<K, V> put(K key, TreeEntry<K, V> node) {
        return putNode(key, node.getValue());
    }

    @Override // java.util.Map
    default void putAll(Map<? extends K, ? extends TreeEntry<K, V>> treeEntryMap) {
        if (CollUtil.isEmpty(treeEntryMap)) {
            return;
        }
        treeEntryMap.forEach((k, v) -> {
            if (v.hasParent()) {
                TreeEntry<K, V> parent = v.getDeclaredParent();
                putLinkedNodes(parent.getKey(), parent.getValue(), v.getKey(), v.getValue());
            } else {
                putNode(v.getKey(), v.getValue());
            }
        });
    }

    default <C extends Collection<V>> void putAllNode(C values, Function<V, K> keyGenerator, Function<V, K> parentKeyGenerator, boolean ignoreNullNode) {
        if (CollUtil.isEmpty((Collection<?>) values)) {
            return;
        }
        values.forEach(v -> {
            Object apply = keyGenerator.apply(v);
            Object apply2 = parentKeyGenerator.apply(v);
            boolean hasKey = ObjectUtil.isNotNull(apply);
            boolean hasParentKey = ObjectUtil.isNotNull(apply2);
            if (!ignoreNullNode || (hasKey && hasParentKey)) {
                linkNodes(apply2, apply);
                ((TreeEntry) get(apply)).setValue(v);
            } else {
                if (!hasKey && !hasParentKey) {
                    return;
                }
                if (hasKey) {
                    putNode(apply, v);
                } else {
                    putNode(apply2, null);
                }
            }
        });
    }

    default void putLinkedNodes(K parentKey, V parentValue, K childKey, V childValue) {
        putNode(parentKey, parentValue);
        putNode(childKey, childValue);
        linkNodes(parentKey, childKey);
    }

    default void linkNodes(K parentKey, K childKey) {
        linkNodes(parentKey, childKey, null);
    }

    default Set<TreeEntry<K, V>> getTreeNodes(K key) {
        TreeEntry<K, V> target = (TreeEntry) get(key);
        if (ObjectUtil.isNull(target)) {
            return Collections.emptySet();
        }
        Set<TreeEntry<K, V>> results = CollUtil.newLinkedHashSet(target.getRoot());
        CollUtil.addAll((Collection) results, (Iterable) target.getRoot().getChildren().values());
        return results;
    }

    default TreeEntry<K, V> getRootNode(K key) {
        return (TreeEntry) Opt.ofNullable(get(key)).map((v0) -> {
            return v0.getRoot();
        }).orElse(null);
    }

    default TreeEntry<K, V> getDeclaredParentNode(K key) {
        return (TreeEntry) Opt.ofNullable(get(key)).map((v0) -> {
            return v0.getDeclaredParent();
        }).orElse(null);
    }

    default TreeEntry<K, V> getParentNode(K key, K parentKey) {
        return (TreeEntry) Opt.ofNullable(get(key)).map(t -> {
            return t.getParent(parentKey);
        }).orElse(null);
    }

    default boolean containsParentNode(K key, K parentKey) {
        return ((Boolean) Opt.ofNullable(get(key)).map(m -> {
            return Boolean.valueOf(m.containsParent(parentKey));
        }).orElse(false)).booleanValue();
    }

    default V getNodeValue(K k) {
        return (V) Opt.ofNullable(get(k)).map((v0) -> {
            return v0.getValue();
        }).get();
    }

    default boolean containsChildNode(K parentKey, K childKey) {
        return ((Boolean) Opt.ofNullable(get(parentKey)).map(m -> {
            return Boolean.valueOf(m.containsChild(childKey));
        }).orElse(false)).booleanValue();
    }

    default Collection<TreeEntry<K, V>> getDeclaredChildNodes(K key) {
        return (Collection) Opt.ofNullable(get(key)).map((v0) -> {
            return v0.getDeclaredChildren();
        }).map((v0) -> {
            return v0.values();
        }).orElseGet(Collections::emptyList);
    }

    default Collection<TreeEntry<K, V>> getChildNodes(K key) {
        return (Collection) Opt.ofNullable(get(key)).map((v0) -> {
            return v0.getChildren();
        }).map((v0) -> {
            return v0.values();
        }).orElseGet(Collections::emptyList);
    }
}

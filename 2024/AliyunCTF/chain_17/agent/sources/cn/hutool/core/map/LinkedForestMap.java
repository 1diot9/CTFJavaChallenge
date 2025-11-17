package cn.hutool.core.map;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/map/LinkedForestMap.class */
public class LinkedForestMap<K, V> implements ForestMap<K, V> {
    private final Map<K, TreeEntryNode<K, V>> nodes = new LinkedHashMap();
    private final boolean allowOverrideParent;

    /* JADX WARN: Multi-variable type inference failed */
    @Override // cn.hutool.core.map.ForestMap
    public /* bridge */ /* synthetic */ TreeEntry putNode(Object obj, Object obj2) {
        return putNode((LinkedForestMap<K, V>) obj, obj2);
    }

    public LinkedForestMap(boolean allowOverrideParent) {
        this.allowOverrideParent = allowOverrideParent;
    }

    @Override // java.util.Map
    public int size() {
        return this.nodes.size();
    }

    @Override // java.util.Map
    public boolean isEmpty() {
        return this.nodes.isEmpty();
    }

    @Override // java.util.Map
    public boolean containsKey(Object key) {
        return this.nodes.containsKey(key);
    }

    @Override // java.util.Map
    public boolean containsValue(Object value) {
        return this.nodes.containsValue(value);
    }

    @Override // java.util.Map
    public TreeEntry<K, V> get(Object key) {
        return this.nodes.get(key);
    }

    @Override // cn.hutool.core.map.ForestMap, java.util.Map
    public TreeEntry<K, V> remove(Object key) {
        TreeEntryNode<K, V> target = this.nodes.remove(key);
        if (ObjectUtil.isNull(target)) {
            return null;
        }
        if (target.hasParent()) {
            TreeEntryNode<K, V> parent = target.getDeclaredParent();
            Map<K, TreeEntry<K, V>> targetChildren = target.getChildren();
            parent.removeDeclaredChild(target.getKey());
            target.clear();
            targetChildren.forEach((k, c) -> {
                parent.addChild((TreeEntryNode) c);
            });
        }
        return target;
    }

    @Override // cn.hutool.core.map.ForestMap, java.util.Map
    public void clear() {
        this.nodes.values().forEach((v0) -> {
            v0.clear();
        });
        this.nodes.clear();
    }

    @Override // java.util.Map
    public Set<K> keySet() {
        return this.nodes.keySet();
    }

    @Override // java.util.Map
    public Collection<TreeEntry<K, V>> values() {
        return new ArrayList(this.nodes.values());
    }

    @Override // java.util.Map
    public Set<Map.Entry<K, TreeEntry<K, V>>> entrySet() {
        return (Set) this.nodes.entrySet().stream().map(this::wrap).collect(Collectors.toSet());
    }

    private Map.Entry<K, TreeEntry<K, V>> wrap(Map.Entry<K, TreeEntryNode<K, V>> nodeEntry) {
        return new EntryNodeWrapper(nodeEntry.getValue());
    }

    @Override // cn.hutool.core.map.ForestMap
    public TreeEntryNode<K, V> putNode(K key, V value) {
        TreeEntryNode<K, V> target = this.nodes.get(key);
        if (ObjectUtil.isNotNull(target)) {
            V oldVal = target.getValue();
            target.setValue(value);
            return target.copy(oldVal);
        }
        this.nodes.put(key, new TreeEntryNode<>(null, key, value));
        return null;
    }

    @Override // cn.hutool.core.map.ForestMap
    public void putLinkedNodes(K parentKey, V parentValue, K childKey, V childValue) {
        linkNodes(parentKey, childKey, (parent, child) -> {
            parent.setValue(parentValue);
            child.setValue(childValue);
        });
    }

    @Override // cn.hutool.core.map.ForestMap
    public void putLinkedNodes(K parentKey, K childKey, V childValue) {
        linkNodes(parentKey, childKey, (parent, child) -> {
            child.setValue(childValue);
        });
    }

    @Override // cn.hutool.core.map.ForestMap
    public void linkNodes(K parentKey, K childKey, BiConsumer<TreeEntry<K, V>, TreeEntry<K, V>> consumer) {
        BiConsumer<TreeEntry<K, V>, TreeEntry<K, V>> consumer2 = (BiConsumer) ObjectUtil.defaultIfNull(consumer, (parent, child) -> {
        });
        TreeEntryNode<K, V> parentNode = this.nodes.computeIfAbsent(parentKey, t -> {
            return new TreeEntryNode(null, t);
        });
        TreeEntryNode<K, V> childNode = this.nodes.get(childKey);
        if (ObjectUtil.isNull(childNode)) {
            TreeEntryNode<K, V> childNode2 = new TreeEntryNode<>(parentNode, childKey);
            consumer2.accept(parentNode, childNode2);
            this.nodes.put(childKey, childNode2);
        } else {
            if (ObjectUtil.equals(parentNode, childNode.getDeclaredParent())) {
                consumer2.accept(parentNode, childNode);
                return;
            }
            if (false == childNode.hasParent()) {
                parentNode.addChild(childNode);
            } else if (this.allowOverrideParent) {
                childNode.getDeclaredParent().removeDeclaredChild(childNode.getKey());
                parentNode.addChild(childNode);
            } else {
                throw new IllegalArgumentException(StrUtil.format("[{}] has been used as child of [{}], can not be overwrite as child of [{}]", childNode.getKey(), childNode.getDeclaredParent().getKey(), parentKey));
            }
            consumer2.accept(parentNode, childNode);
        }
    }

    @Override // cn.hutool.core.map.ForestMap
    public void unlinkNode(K parentKey, K childKey) {
        TreeEntryNode<K, V> childNode = this.nodes.get(childKey);
        if (!ObjectUtil.isNull(childNode) && childNode.hasParent()) {
            childNode.getDeclaredParent().removeDeclaredChild(childNode.getKey());
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/map/LinkedForestMap$TreeEntryNode.class */
    public static class TreeEntryNode<K, V> implements TreeEntry<K, V> {
        private TreeEntryNode<K, V> root;
        private TreeEntryNode<K, V> parent;
        private int weight;
        private final Map<K, TreeEntryNode<K, V>> children;
        private final K key;
        private V value;

        /* JADX WARN: Multi-variable type inference failed */
        @Override // cn.hutool.core.map.TreeEntry
        public /* bridge */ /* synthetic */ TreeEntry getChild(Object obj) {
            return getChild((TreeEntryNode<K, V>) obj);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // cn.hutool.core.map.TreeEntry
        public /* bridge */ /* synthetic */ TreeEntry getParent(Object obj) {
            return getParent((TreeEntryNode<K, V>) obj);
        }

        public TreeEntryNode(TreeEntryNode<K, V> parent, K key) {
            this(parent, key, null);
        }

        public TreeEntryNode(TreeEntryNode<K, V> parent, K key, V value) {
            this.parent = parent;
            this.key = key;
            this.value = value;
            this.children = new LinkedHashMap();
            if (ObjectUtil.isNull(parent)) {
                this.root = this;
                this.weight = 0;
            } else {
                parent.addChild(this);
                this.weight = parent.weight + 1;
                this.root = parent.root;
            }
        }

        @Override // java.util.Map.Entry
        public K getKey() {
            return this.key;
        }

        @Override // cn.hutool.core.map.TreeEntry
        public int getWeight() {
            return this.weight;
        }

        @Override // java.util.Map.Entry
        public V getValue() {
            return this.value;
        }

        @Override // java.util.Map.Entry
        public V setValue(V value) {
            V oldVal = getValue();
            this.value = value;
            return oldVal;
        }

        TreeEntryNode<K, V> traverseParentNodes(boolean includeCurrent, Consumer<TreeEntryNode<K, V>> consumer, Predicate<TreeEntryNode<K, V>> breakTraverse) {
            TreeEntryNode<K, V> curr;
            Predicate<TreeEntryNode<K, V>> breakTraverse2 = (Predicate) ObjectUtil.defaultIfNull(breakTraverse, (Function<Predicate<TreeEntryNode<K, V>>, ? extends Predicate<TreeEntryNode<K, V>>>) a -> {
                return n -> {
                    return false;
                };
            });
            TreeEntryNode<K, V> treeEntryNode = includeCurrent ? this : this.parent;
            while (true) {
                curr = treeEntryNode;
                if (!ObjectUtil.isNotNull(curr)) {
                    break;
                }
                consumer.accept(curr);
                if (breakTraverse2.test(curr)) {
                    break;
                }
                treeEntryNode = curr.parent;
            }
            return curr;
        }

        public boolean isRoot() {
            return getRoot() == this;
        }

        @Override // cn.hutool.core.map.TreeEntry
        public TreeEntryNode<K, V> getRoot() {
            if (ObjectUtil.isNotNull(this.root)) {
                return this.root;
            }
            this.root = traverseParentNodes(true, p -> {
            }, p2 -> {
                return !p2.hasParent();
            });
            return this.root;
        }

        @Override // cn.hutool.core.map.TreeEntry
        public TreeEntryNode<K, V> getDeclaredParent() {
            return this.parent;
        }

        @Override // cn.hutool.core.map.TreeEntry
        public TreeEntryNode<K, V> getParent(K key) {
            return traverseParentNodes(false, p -> {
            }, p2 -> {
                return p2.equalsKey(key);
            });
        }

        @Override // cn.hutool.core.map.TreeEntry
        public void forEachChild(boolean includeSelf, Consumer<TreeEntry<K, V>> nodeConsumer) {
            traverseChildNodes(includeSelf, (index, child) -> {
                nodeConsumer.accept(child);
            }, null);
        }

        public boolean equalsKey(K key) {
            return ObjectUtil.equal(getKey(), key);
        }

        TreeEntryNode<K, V> traverseChildNodes(boolean includeCurrent, BiConsumer<Integer, TreeEntryNode<K, V>> consumer, BiPredicate<Integer, TreeEntryNode<K, V>> breakTraverse) {
            BiPredicate<Integer, TreeEntryNode<K, V>> breakTraverse2 = (BiPredicate) ObjectUtil.defaultIfNull(breakTraverse, (i, n) -> {
                return false;
            });
            Deque<List<TreeEntryNode<K, V>>> keyNodeDeque = CollUtil.newLinkedList(CollUtil.newArrayList(this));
            boolean needProcess = includeCurrent;
            int index = includeCurrent ? 0 : 1;
            TreeEntryNode<K, V> lastNode = null;
            while (!keyNodeDeque.isEmpty()) {
                List<TreeEntryNode<K, V>> curr = keyNodeDeque.removeFirst();
                List<TreeEntryNode<K, V>> next = new ArrayList<>();
                for (TreeEntryNode<K, V> node : curr) {
                    if (needProcess) {
                        consumer.accept(Integer.valueOf(index), node);
                        if (breakTraverse2.test(Integer.valueOf(index), node)) {
                            return node;
                        }
                    } else {
                        needProcess = true;
                    }
                    CollUtil.addAll((Collection) next, (Iterable) node.children.values());
                }
                if (!next.isEmpty()) {
                    keyNodeDeque.addLast(next);
                }
                lastNode = (TreeEntryNode) CollUtil.getLast(next);
                index++;
            }
            return lastNode;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void addChild(TreeEntryNode<K, V> child) {
            if (containsChild(child.key)) {
                return;
            }
            traverseParentNodes(true, s -> {
                Assert.notEquals(s.key, child.key, "circular reference between [{}] and [{}]!", s.key, this.key);
            }, null);
            child.parent = this;
            child.traverseChildNodes(true, (i, c) -> {
                c.root = getRoot();
                c.weight = i.intValue() + getWeight() + 1;
            }, null);
            this.children.put(child.key, child);
        }

        void removeDeclaredChild(K key) {
            TreeEntryNode<K, V> child = this.children.get(key);
            if (ObjectUtil.isNull(child)) {
                return;
            }
            this.children.remove(key);
            child.parent = null;
            child.traverseChildNodes(true, (i, c) -> {
                c.root = child;
                c.weight = i.intValue();
            }, null);
        }

        @Override // cn.hutool.core.map.TreeEntry
        public TreeEntryNode<K, V> getChild(K key) {
            return traverseChildNodes(false, (i, c) -> {
            }, (i2, c2) -> {
                return c2.equalsKey(key);
            });
        }

        @Override // cn.hutool.core.map.TreeEntry
        public Map<K, TreeEntry<K, V>> getDeclaredChildren() {
            return new LinkedHashMap(this.children);
        }

        @Override // cn.hutool.core.map.TreeEntry
        public Map<K, TreeEntry<K, V>> getChildren() {
            Map<K, TreeEntry<K, V>> childrenMap = new LinkedHashMap<>();
            traverseChildNodes(false, (i, c) -> {
            }, null);
            return childrenMap;
        }

        void clear() {
            this.root = null;
            this.children.clear();
            this.parent = null;
        }

        @Override // cn.hutool.core.map.TreeEntry, java.util.Map.Entry
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass().equals(o.getClass()) || ClassUtil.isAssignable(getClass(), o.getClass())) {
                return false;
            }
            TreeEntry<?, ?> treeEntry = (TreeEntry) o;
            return ObjectUtil.equals(getKey(), treeEntry.getKey());
        }

        @Override // cn.hutool.core.map.TreeEntry, java.util.Map.Entry
        public int hashCode() {
            return Objects.hash(getKey());
        }

        TreeEntryNode<K, V> copy(V v) {
            TreeEntryNode<K, V> treeEntryNode = new TreeEntryNode<>(this.parent, this.key, ObjectUtil.defaultIfNull(v, this.value));
            treeEntryNode.children.putAll(this.children);
            return treeEntryNode;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/map/LinkedForestMap$EntryNodeWrapper.class */
    public static class EntryNodeWrapper<K, V, N extends TreeEntry<K, V>> implements Map.Entry<K, TreeEntry<K, V>> {
        private final N entryNode;

        EntryNodeWrapper(N entryNode) {
            this.entryNode = entryNode;
        }

        @Override // java.util.Map.Entry
        public K getKey() {
            return (K) this.entryNode.getKey();
        }

        @Override // java.util.Map.Entry
        public TreeEntry<K, V> getValue() {
            return this.entryNode;
        }

        @Override // java.util.Map.Entry
        public TreeEntry<K, V> setValue(TreeEntry<K, V> value) {
            throw new UnsupportedOperationException();
        }
    }
}

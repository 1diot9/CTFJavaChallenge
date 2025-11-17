package cn.hutool.core.lang.tree;

import java.util.Map;
import java.util.Objects;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/tree/TreeNode.class */
public class TreeNode<T> implements Node<T> {
    private static final long serialVersionUID = 1;
    private T id;
    private T parentId;
    private CharSequence name;
    private Comparable<?> weight;
    private Map<String, Object> extra;

    @Override // cn.hutool.core.lang.tree.Node
    public /* bridge */ /* synthetic */ Node setWeight(Comparable comparable) {
        return setWeight((Comparable<?>) comparable);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // cn.hutool.core.lang.tree.Node
    public /* bridge */ /* synthetic */ Node setParentId(Object obj) {
        return setParentId((TreeNode<T>) obj);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // cn.hutool.core.lang.tree.Node
    public /* bridge */ /* synthetic */ Node setId(Object obj) {
        return setId((TreeNode<T>) obj);
    }

    public TreeNode() {
        this.weight = 0;
    }

    public TreeNode(T id, T parentId, String name, Comparable<?> weight) {
        this.weight = 0;
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        if (weight != null) {
            this.weight = weight;
        }
    }

    @Override // cn.hutool.core.lang.tree.Node
    public T getId() {
        return this.id;
    }

    @Override // cn.hutool.core.lang.tree.Node
    public TreeNode<T> setId(T id) {
        this.id = id;
        return this;
    }

    @Override // cn.hutool.core.lang.tree.Node
    public T getParentId() {
        return this.parentId;
    }

    @Override // cn.hutool.core.lang.tree.Node
    public TreeNode<T> setParentId(T parentId) {
        this.parentId = parentId;
        return this;
    }

    @Override // cn.hutool.core.lang.tree.Node
    public CharSequence getName() {
        return this.name;
    }

    @Override // cn.hutool.core.lang.tree.Node
    public TreeNode<T> setName(CharSequence name) {
        this.name = name;
        return this;
    }

    @Override // cn.hutool.core.lang.tree.Node
    public Comparable<?> getWeight() {
        return this.weight;
    }

    @Override // cn.hutool.core.lang.tree.Node
    public TreeNode<T> setWeight(Comparable<?> weight) {
        this.weight = weight;
        return this;
    }

    public Map<String, Object> getExtra() {
        return this.extra;
    }

    public TreeNode<T> setExtra(Map<String, Object> extra) {
        this.extra = extra;
        return this;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TreeNode<?> treeNode = (TreeNode) o;
        return Objects.equals(this.id, treeNode.id);
    }

    public int hashCode() {
        return Objects.hash(this.id);
    }
}

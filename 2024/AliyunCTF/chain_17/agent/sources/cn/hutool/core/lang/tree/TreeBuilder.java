package cn.hutool.core.lang.tree;

import cn.hutool.core.builder.Builder;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.tree.parser.NodeParser;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/tree/TreeBuilder.class */
public class TreeBuilder<E> implements Builder<Tree<E>> {
    private static final long serialVersionUID = 1;
    private final Tree<E> root;
    private final Map<E, Tree<E>> idTreeMap;
    private boolean isBuild;

    public static <T> TreeBuilder<T> of(T rootId) {
        return of(rootId, null);
    }

    public static <T> TreeBuilder<T> of(T rootId, TreeNodeConfig config) {
        return new TreeBuilder<>(rootId, config);
    }

    public TreeBuilder(E rootId, TreeNodeConfig config) {
        this.root = new Tree<>(config);
        this.root.setId((Tree<E>) rootId);
        this.idTreeMap = new LinkedHashMap();
    }

    public TreeBuilder<E> setId(E id) {
        this.root.setId((Tree<E>) id);
        return this;
    }

    public TreeBuilder<E> setParentId(E parentId) {
        this.root.setParentId((Tree<E>) parentId);
        return this;
    }

    public TreeBuilder<E> setName(CharSequence name) {
        this.root.setName(name);
        return this;
    }

    public TreeBuilder<E> setWeight(Comparable<?> weight) {
        this.root.setWeight(weight);
        return this;
    }

    public TreeBuilder<E> putExtra(String key, Object value) {
        Assert.notEmpty(key, "Key must be not empty !", new Object[0]);
        this.root.put(key, value);
        return this;
    }

    public TreeBuilder<E> append(Map<E, Tree<E>> map) {
        checkBuilt();
        this.idTreeMap.putAll(map);
        return this;
    }

    public TreeBuilder<E> append(Iterable<Tree<E>> trees) {
        checkBuilt();
        for (Tree<E> tree : trees) {
            this.idTreeMap.put(tree.getId(), tree);
        }
        return this;
    }

    public <T> TreeBuilder<E> append(List<T> list, NodeParser<T, E> nodeParser) {
        return append(list, null, nodeParser);
    }

    public <T> TreeBuilder<E> append(List<T> list, E rootId, NodeParser<T, E> nodeParser) {
        checkBuilt();
        TreeNodeConfig config = this.root.getConfig();
        Map<E, Tree<E>> map = new LinkedHashMap<>(list.size(), 1.0f);
        for (T t : list) {
            Tree<E> node = new Tree<>(config);
            nodeParser.parse(t, node);
            if (null != rootId && false == rootId.getClass().equals(node.getId().getClass())) {
                throw new IllegalArgumentException("rootId type is node.getId().getClass()!");
            }
            map.put(node.getId(), node);
        }
        return append(map);
    }

    public TreeBuilder<E> reset() {
        this.idTreeMap.clear();
        this.root.setChildren(null);
        this.isBuild = false;
        return this;
    }

    @Override // cn.hutool.core.builder.Builder
    public Tree<E> build() {
        checkBuilt();
        buildFromMap();
        cutTree();
        this.isBuild = true;
        this.idTreeMap.clear();
        return this.root;
    }

    public List<Tree<E>> buildList() {
        if (this.isBuild) {
            return this.root.getChildren();
        }
        return build().getChildren();
    }

    private void buildFromMap() {
        if (MapUtil.isEmpty(this.idTreeMap)) {
            return;
        }
        Map sortByValue = MapUtil.sortByValue(this.idTreeMap, false);
        for (Tree<E> tree : sortByValue.values()) {
            if (null != tree) {
                E parentId = tree.getParentId();
                if (ObjectUtil.equals(this.root.getId(), parentId)) {
                    this.root.addChildren(tree);
                } else {
                    Tree tree2 = (Tree) sortByValue.get(parentId);
                    if (null != tree2) {
                        tree2.addChildren(tree);
                    }
                }
            }
        }
    }

    private void cutTree() {
        TreeNodeConfig config = this.root.getConfig();
        Integer deep = config.getDeep();
        if (null == deep || deep.intValue() < 0) {
            return;
        }
        cutTree(this.root, 0, deep.intValue());
    }

    private void cutTree(Tree<E> tree, int currentDepp, int maxDeep) {
        if (null == tree) {
            return;
        }
        if (currentDepp == maxDeep) {
            tree.setChildren(null);
            return;
        }
        List<Tree<E>> children = tree.getChildren();
        if (CollUtil.isNotEmpty((Collection<?>) children)) {
            for (Tree<E> child : children) {
                cutTree(child, currentDepp + 1, maxDeep);
            }
        }
    }

    private void checkBuilt() {
        Assert.isFalse(this.isBuild, "Current tree has been built.", new Object[0]);
    }
}

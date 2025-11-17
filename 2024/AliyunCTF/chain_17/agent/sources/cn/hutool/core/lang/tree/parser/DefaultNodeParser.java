package cn.hutool.core.lang.tree.parser;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.map.MapUtil;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/tree/parser/DefaultNodeParser.class */
public class DefaultNodeParser<T> implements NodeParser<TreeNode<T>, T> {
    @Override // cn.hutool.core.lang.tree.parser.NodeParser
    public void parse(TreeNode<T> treeNode, Tree<T> tree) {
        tree.setId((Tree<T>) treeNode.getId());
        tree.setParentId((Tree<T>) treeNode.getParentId());
        tree.setWeight(treeNode.getWeight());
        tree.setName(treeNode.getName());
        Map<String, Object> extra = treeNode.getExtra();
        if (MapUtil.isNotEmpty(extra)) {
            tree.getClass();
            extra.forEach(tree::putExtra);
        }
    }
}

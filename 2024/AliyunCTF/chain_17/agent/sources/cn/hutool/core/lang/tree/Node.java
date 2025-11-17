package cn.hutool.core.lang.tree;

import cn.hutool.core.comparator.CompareUtil;
import java.io.Serializable;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/tree/Node.class */
public interface Node<T> extends Comparable<Node<T>>, Serializable {
    T getId();

    Node<T> setId(T t);

    T getParentId();

    Node<T> setParentId(T t);

    CharSequence getName();

    Node<T> setName(CharSequence charSequence);

    Comparable<?> getWeight();

    Node<T> setWeight(Comparable<?> comparable);

    @Override // java.lang.Comparable
    default int compareTo(Node node) {
        if (null == node) {
            return 1;
        }
        Comparable weight = getWeight();
        Comparable weightOther = node.getWeight();
        return CompareUtil.compare(weight, weightOther);
    }
}

package cn.hutool.core.collection;

import cn.hutool.core.lang.Assert;
import java.util.NoSuchElementException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/collection/NodeListIter.class */
public class NodeListIter implements ResettableIter<Node> {
    private final NodeList nodeList;
    private int index = 0;

    public NodeListIter(NodeList nodeList) {
        this.nodeList = (NodeList) Assert.notNull(nodeList, "NodeList must not be null.", new Object[0]);
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        return this.nodeList != null && this.index < this.nodeList.getLength();
    }

    @Override // java.util.Iterator
    public Node next() {
        if (this.nodeList != null && this.index < this.nodeList.getLength()) {
            NodeList nodeList = this.nodeList;
            int i = this.index;
            this.index = i + 1;
            return nodeList.item(i);
        }
        throw new NoSuchElementException("underlying nodeList has no more elements");
    }

    @Override // java.util.Iterator
    public void remove() {
        throw new UnsupportedOperationException("remove() method not supported for a NodeListIterator.");
    }

    @Override // cn.hutool.core.collection.ResettableIter
    public void reset() {
        this.index = 0;
    }
}

package cn.hutool.core.collection;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/collection/PartitionIter.class */
public class PartitionIter<T> implements IterableIter<List<T>>, Serializable {
    private static final long serialVersionUID = 1;
    protected final Iterator<T> iterator;
    protected final int partitionSize;

    public PartitionIter(Iterator<T> iterator, int partitionSize) {
        this.iterator = iterator;
        this.partitionSize = partitionSize;
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        return this.iterator.hasNext();
    }

    @Override // java.util.Iterator
    public List<T> next() {
        List<T> list = new ArrayList<>(this.partitionSize);
        for (int i = 0; i < this.partitionSize && false != this.iterator.hasNext(); i++) {
            list.add(this.iterator.next());
        }
        return list;
    }
}

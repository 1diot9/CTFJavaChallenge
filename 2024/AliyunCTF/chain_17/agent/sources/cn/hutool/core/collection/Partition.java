package cn.hutool.core.collection;

import cn.hutool.core.lang.Assert;
import java.util.AbstractList;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/collection/Partition.class */
public class Partition<T> extends AbstractList<List<T>> {
    protected final List<T> list;
    protected final int size;

    public Partition(List<T> list, int size) {
        this.list = (List) Assert.notNull(list);
        this.size = Math.min(list.size(), size);
    }

    @Override // java.util.AbstractList, java.util.List
    public List<T> get(int index) {
        int start = index * this.size;
        int end = Math.min(start + this.size, this.list.size());
        return this.list.subList(start, end);
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public int size() {
        int size = this.size;
        if (0 == size) {
            return 0;
        }
        int total = this.list.size();
        return ((total + size) - 1) / size;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean isEmpty() {
        return this.list.isEmpty();
    }
}

package cn.hutool.core.collection;

import cn.hutool.core.lang.Assert;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/collection/AvgPartition.class */
public class AvgPartition<T> extends Partition<T> {
    final int limit;
    final int remainder;

    public AvgPartition(List<T> list, int limit) {
        super(list, list.size() / (limit <= 0 ? 1 : limit));
        Assert.isTrue(limit > 0, "Partition limit must be > 0", new Object[0]);
        this.limit = limit;
        this.remainder = list.size() % limit;
    }

    @Override // cn.hutool.core.collection.Partition, java.util.AbstractList, java.util.List
    public List<T> get(int index) {
        int size = this.size;
        int remainder = this.remainder;
        int start = (index * size) + Math.min(index, remainder);
        int end = start + size;
        if (index + 1 <= remainder) {
            end++;
        }
        return this.list.subList(start, end);
    }

    @Override // cn.hutool.core.collection.Partition, java.util.AbstractCollection, java.util.Collection, java.util.List
    public int size() {
        return this.limit;
    }
}

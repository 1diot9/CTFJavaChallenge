package cn.hutool.core.collection;

import java.util.List;
import java.util.RandomAccess;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/collection/RandomAccessPartition.class */
public class RandomAccessPartition<T> extends Partition<T> implements RandomAccess {
    public RandomAccessPartition(List<T> list, int size) {
        super(list, size);
    }
}

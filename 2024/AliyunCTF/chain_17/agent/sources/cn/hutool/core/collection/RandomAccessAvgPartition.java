package cn.hutool.core.collection;

import java.util.List;
import java.util.RandomAccess;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/collection/RandomAccessAvgPartition.class */
public class RandomAccessAvgPartition<T> extends AvgPartition<T> implements RandomAccess {
    public RandomAccessAvgPartition(List<T> list, int limit) {
        super(list, limit);
    }
}

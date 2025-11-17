package org.springframework.core.io.buffer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/buffer/LimitedDataBufferList.class */
public class LimitedDataBufferList extends ArrayList<DataBuffer> {
    private final int maxByteCount;
    private int byteCount;

    public LimitedDataBufferList(int maxByteCount) {
        this.maxByteCount = maxByteCount;
    }

    @Override // java.util.ArrayList, java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean add(DataBuffer buffer) {
        updateCount(buffer.readableByteCount());
        return super.add((LimitedDataBufferList) buffer);
    }

    @Override // java.util.ArrayList, java.util.AbstractList, java.util.List
    public void add(int index, DataBuffer buffer) {
        super.add(index, (int) buffer);
        updateCount(buffer.readableByteCount());
    }

    @Override // java.util.ArrayList, java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean addAll(Collection<? extends DataBuffer> collection) {
        boolean result = super.addAll(collection);
        collection.forEach(buffer -> {
            updateCount(buffer.readableByteCount());
        });
        return result;
    }

    @Override // java.util.ArrayList, java.util.AbstractList, java.util.List
    public boolean addAll(int index, Collection<? extends DataBuffer> collection) {
        boolean result = super.addAll(index, collection);
        collection.forEach(buffer -> {
            updateCount(buffer.readableByteCount());
        });
        return result;
    }

    private void updateCount(int bytesToAdd) {
        if (this.maxByteCount < 0) {
            return;
        }
        if (bytesToAdd > Integer.MAX_VALUE - this.byteCount) {
            raiseLimitException();
            return;
        }
        this.byteCount += bytesToAdd;
        if (this.byteCount > this.maxByteCount) {
            raiseLimitException();
        }
    }

    private void raiseLimitException() {
        throw new DataBufferLimitException("Exceeded limit on max bytes to buffer : " + this.maxByteCount);
    }

    @Override // java.util.ArrayList, java.util.AbstractList, java.util.List
    public DataBuffer remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.ArrayList, java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.ArrayList, java.util.AbstractList
    protected void removeRange(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.ArrayList, java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.ArrayList, java.util.Collection
    public boolean removeIf(Predicate<? super DataBuffer> filter) {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.ArrayList, java.util.AbstractList, java.util.List
    public DataBuffer set(int index, DataBuffer element) {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.ArrayList, java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.util.List
    public void clear() {
        this.byteCount = 0;
        super.clear();
    }

    public void releaseAndClear() {
        forEach(buf -> {
            try {
                DataBufferUtils.release(buf);
            } catch (Throwable th) {
            }
        });
        clear();
    }
}

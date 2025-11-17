package com.fasterxml.jackson.databind.util;

import java.lang.reflect.Array;
import java.util.List;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/ObjectBuffer.class */
public final class ObjectBuffer {
    private static final int SMALL_CHUNK = 16384;
    private static final int MAX_CHUNK = 262144;
    private LinkedNode<Object[]> _head;
    private LinkedNode<Object[]> _tail;
    private int _size;
    private Object[] _freeBuffer;

    public Object[] resetAndStart() {
        _reset();
        if (this._freeBuffer == null) {
            Object[] objArr = new Object[12];
            this._freeBuffer = objArr;
            return objArr;
        }
        return this._freeBuffer;
    }

    public Object[] resetAndStart(Object[] base, int count) {
        _reset();
        if (this._freeBuffer == null || this._freeBuffer.length < count) {
            this._freeBuffer = new Object[Math.max(12, count)];
        }
        System.arraycopy(base, 0, this._freeBuffer, 0, count);
        return this._freeBuffer;
    }

    public Object[] appendCompletedChunk(Object[] fullChunk) {
        LinkedNode<Object[]> next = new LinkedNode<>(fullChunk, null);
        if (this._head == null) {
            this._tail = next;
            this._head = next;
        } else {
            this._tail.linkNext(next);
            this._tail = next;
        }
        int len = fullChunk.length;
        this._size += len;
        if (len < 16384) {
            len += len;
        } else if (len < 262144) {
            len += len >> 2;
        }
        return new Object[len];
    }

    public Object[] completeAndClearBuffer(Object[] lastChunk, int lastChunkEntries) {
        int totalSize = lastChunkEntries + this._size;
        Object[] result = new Object[totalSize];
        _copyTo(result, totalSize, lastChunk, lastChunkEntries);
        _reset();
        return result;
    }

    public <T> T[] completeAndClearBuffer(Object[] objArr, int i, Class<T> cls) {
        int i2 = i + this._size;
        T[] tArr = (T[]) ((Object[]) Array.newInstance((Class<?>) cls, i2));
        _copyTo(tArr, i2, objArr, i);
        _reset();
        return tArr;
    }

    public void completeAndClearBuffer(Object[] lastChunk, int lastChunkEntries, List<Object> resultList) {
        LinkedNode<Object[]> linkedNode = this._head;
        while (true) {
            LinkedNode<Object[]> n = linkedNode;
            if (n == null) {
                break;
            }
            Object[] curr = n.value();
            for (Object obj : curr) {
                resultList.add(obj);
            }
            linkedNode = n.next();
        }
        for (int i = 0; i < lastChunkEntries; i++) {
            resultList.add(lastChunk[i]);
        }
        _reset();
    }

    public int initialCapacity() {
        if (this._freeBuffer == null) {
            return 0;
        }
        return this._freeBuffer.length;
    }

    public int bufferedSize() {
        return this._size;
    }

    protected void _reset() {
        if (this._tail != null) {
            this._freeBuffer = this._tail.value();
        }
        this._tail = null;
        this._head = null;
        this._size = 0;
    }

    protected final void _copyTo(Object resultArray, int totalSize, Object[] lastChunk, int lastChunkEntries) {
        int ptr = 0;
        LinkedNode<Object[]> linkedNode = this._head;
        while (true) {
            LinkedNode<Object[]> n = linkedNode;
            if (n == null) {
                break;
            }
            Object[] curr = n.value();
            int len = curr.length;
            System.arraycopy(curr, 0, resultArray, ptr, len);
            ptr += len;
            linkedNode = n.next();
        }
        System.arraycopy(lastChunk, 0, resultArray, ptr, lastChunkEntries);
        int ptr2 = ptr + lastChunkEntries;
        if (ptr2 != totalSize) {
            throw new IllegalStateException("Should have gotten " + totalSize + " entries, got " + ptr2);
        }
    }
}

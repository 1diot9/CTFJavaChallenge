package com.fasterxml.jackson.databind.util;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/PrimitiveArrayBuilder.class */
public abstract class PrimitiveArrayBuilder<T> {
    static final int INITIAL_CHUNK_SIZE = 12;
    static final int SMALL_CHUNK_SIZE = 16384;
    static final int MAX_CHUNK_SIZE = 262144;
    protected T _freeBuffer;
    protected Node<T> _bufferHead;
    protected Node<T> _bufferTail;
    protected int _bufferedEntryCount;

    protected abstract T _constructArray(int i);

    public int bufferedSize() {
        return this._bufferedEntryCount;
    }

    public T resetAndStart() {
        _reset();
        return this._freeBuffer == null ? _constructArray(12) : this._freeBuffer;
    }

    public final T appendCompletedChunk(T fullChunk, int fullChunkLength) {
        int nextLen;
        Node<T> next = new Node<>(fullChunk, fullChunkLength);
        if (this._bufferHead == null) {
            this._bufferTail = next;
            this._bufferHead = next;
        } else {
            this._bufferTail.linkNext(next);
            this._bufferTail = next;
        }
        this._bufferedEntryCount += fullChunkLength;
        if (fullChunkLength < 16384) {
            nextLen = fullChunkLength + fullChunkLength;
        } else {
            nextLen = fullChunkLength + (fullChunkLength >> 2);
        }
        return _constructArray(nextLen);
    }

    public T completeAndClearBuffer(T lastChunk, int lastChunkEntries) {
        int totalSize = lastChunkEntries + this._bufferedEntryCount;
        T resultArray = _constructArray(totalSize);
        int ptr = 0;
        Node<T> node = this._bufferHead;
        while (true) {
            Node<T> n = node;
            if (n == null) {
                break;
            }
            ptr = n.copyData(resultArray, ptr);
            node = n.next();
        }
        System.arraycopy(lastChunk, 0, resultArray, ptr, lastChunkEntries);
        int ptr2 = ptr + lastChunkEntries;
        if (ptr2 != totalSize) {
            throw new IllegalStateException("Should have gotten " + totalSize + " entries, got " + ptr2);
        }
        return resultArray;
    }

    protected void _reset() {
        if (this._bufferTail != null) {
            this._freeBuffer = this._bufferTail.getData();
        }
        this._bufferTail = null;
        this._bufferHead = null;
        this._bufferedEntryCount = 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/PrimitiveArrayBuilder$Node.class */
    public static final class Node<T> {
        final T _data;
        final int _dataLength;
        Node<T> _next;

        public Node(T data, int dataLen) {
            this._data = data;
            this._dataLength = dataLen;
        }

        public T getData() {
            return this._data;
        }

        public int copyData(T dst, int ptr) {
            System.arraycopy(this._data, 0, dst, ptr, this._dataLength);
            return ptr + this._dataLength;
        }

        public Node<T> next() {
            return this._next;
        }

        public void linkNext(Node<T> next) {
            if (this._next != null) {
                throw new IllegalStateException();
            }
            this._next = next;
        }
    }
}

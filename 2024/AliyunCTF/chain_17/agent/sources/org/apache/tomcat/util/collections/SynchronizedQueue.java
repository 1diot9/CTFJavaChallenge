package org.apache.tomcat.util.collections;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/collections/SynchronizedQueue.class */
public class SynchronizedQueue<T> {
    public static final int DEFAULT_SIZE = 128;
    private Object[] queue;
    private int size;
    private int insert;
    private int remove;

    public SynchronizedQueue() {
        this(128);
    }

    public SynchronizedQueue(int initialSize) {
        this.insert = 0;
        this.remove = 0;
        this.queue = new Object[initialSize];
        this.size = initialSize;
    }

    public synchronized boolean offer(T t) {
        Object[] objArr = this.queue;
        int i = this.insert;
        this.insert = i + 1;
        objArr[i] = t;
        if (this.insert == this.size) {
            this.insert = 0;
        }
        if (this.insert == this.remove) {
            expand();
            return true;
        }
        return true;
    }

    public synchronized T poll() {
        if (this.insert == this.remove) {
            return null;
        }
        T t = (T) this.queue[this.remove];
        this.queue[this.remove] = null;
        this.remove++;
        if (this.remove == this.size) {
            this.remove = 0;
        }
        return t;
    }

    private void expand() {
        int newSize = this.size * 2;
        Object[] newQueue = new Object[newSize];
        System.arraycopy(this.queue, this.insert, newQueue, 0, this.size - this.insert);
        System.arraycopy(this.queue, 0, newQueue, this.size - this.insert, this.insert);
        this.insert = this.size;
        this.remove = 0;
        this.queue = newQueue;
        this.size = newSize;
    }

    public synchronized int size() {
        int result = this.insert - this.remove;
        if (result < 0) {
            result += this.size;
        }
        return result;
    }

    public synchronized void clear() {
        this.queue = new Object[this.size];
        this.insert = 0;
        this.remove = 0;
    }
}

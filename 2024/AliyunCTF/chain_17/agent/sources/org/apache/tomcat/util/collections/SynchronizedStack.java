package org.apache.tomcat.util.collections;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/collections/SynchronizedStack.class */
public class SynchronizedStack<T> {
    public static final int DEFAULT_SIZE = 128;
    private static final int DEFAULT_LIMIT = -1;
    private int size;
    private final int limit;
    private int index;
    private Object[] stack;

    public SynchronizedStack() {
        this(128, -1);
    }

    public SynchronizedStack(int size, int limit) {
        this.index = -1;
        if (limit > -1 && size > limit) {
            this.size = limit;
        } else {
            this.size = size;
        }
        this.limit = limit;
        this.stack = new Object[this.size];
    }

    public synchronized boolean push(T obj) {
        this.index++;
        if (this.index == this.size) {
            if (this.limit == -1 || this.size < this.limit) {
                expand();
            } else {
                this.index--;
                return false;
            }
        }
        this.stack[this.index] = obj;
        return true;
    }

    public synchronized T pop() {
        if (this.index == -1) {
            return null;
        }
        T t = (T) this.stack[this.index];
        Object[] objArr = this.stack;
        int i = this.index;
        this.index = i - 1;
        objArr[i] = null;
        return t;
    }

    public synchronized void clear() {
        if (this.index > -1) {
            for (int i = 0; i < this.index + 1; i++) {
                this.stack[i] = null;
            }
        }
        this.index = -1;
    }

    private void expand() {
        int newSize = this.size * 2;
        if (this.limit != -1 && newSize > this.limit) {
            newSize = this.limit;
        }
        Object[] newStack = new Object[newSize];
        System.arraycopy(this.stack, 0, newStack, 0, this.size);
        this.stack = newStack;
        this.size = newSize;
    }
}

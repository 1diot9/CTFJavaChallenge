package com.fasterxml.jackson.databind.util;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/LinkedNode.class */
public final class LinkedNode<T> {
    private final T value;
    private LinkedNode<T> next;

    public LinkedNode(T value, LinkedNode<T> next) {
        this.value = value;
        this.next = next;
    }

    public void linkNext(LinkedNode<T> n) {
        if (this.next != null) {
            throw new IllegalStateException();
        }
        this.next = n;
    }

    public LinkedNode<T> next() {
        return this.next;
    }

    public T value() {
        return this.value;
    }

    public static <ST> boolean contains(LinkedNode<ST> node, ST value) {
        while (node != null) {
            if (node.value() == value) {
                return true;
            }
            node = node.next();
        }
        return false;
    }
}

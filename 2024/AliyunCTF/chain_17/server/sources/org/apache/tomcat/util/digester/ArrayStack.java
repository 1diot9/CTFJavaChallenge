package org.apache.tomcat.util.digester;

import java.util.ArrayList;
import java.util.EmptyStackException;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/digester/ArrayStack.class */
public class ArrayStack<E> extends ArrayList<E> {
    private static final long serialVersionUID = 2130079159931574599L;

    public ArrayStack() {
    }

    public ArrayStack(int initialSize) {
        super(initialSize);
    }

    public boolean empty() {
        return isEmpty();
    }

    public E peek() throws EmptyStackException {
        int n = size();
        if (n <= 0) {
            throw new EmptyStackException();
        }
        return get(n - 1);
    }

    public E peek(int n) throws EmptyStackException {
        int m = (size() - n) - 1;
        if (m < 0) {
            throw new EmptyStackException();
        }
        return get(m);
    }

    public E pop() throws EmptyStackException {
        int n = size();
        if (n <= 0) {
            throw new EmptyStackException();
        }
        return remove(n - 1);
    }

    public E push(E item) {
        add(item);
        return item;
    }
}

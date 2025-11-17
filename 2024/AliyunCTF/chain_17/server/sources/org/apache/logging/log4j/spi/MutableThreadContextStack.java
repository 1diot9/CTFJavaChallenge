package org.apache.logging.log4j.spi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.util.StringBuilderFormattable;

/* loaded from: server.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/spi/MutableThreadContextStack.class */
public class MutableThreadContextStack implements ThreadContextStack, StringBuilderFormattable {
    private static final long serialVersionUID = 50505011;
    private final List<String> list;
    private boolean frozen;

    public MutableThreadContextStack() {
        this(new ArrayList());
    }

    public MutableThreadContextStack(final List<String> list) {
        this.list = new ArrayList(list);
    }

    private MutableThreadContextStack(final MutableThreadContextStack stack) {
        this.list = new ArrayList(stack.list);
    }

    private void checkInvariants() {
        if (this.frozen) {
            throw new UnsupportedOperationException("context stack has been frozen");
        }
    }

    @Override // org.apache.logging.log4j.ThreadContext.ContextStack
    public String pop() {
        checkInvariants();
        if (this.list.isEmpty()) {
            return null;
        }
        int last = this.list.size() - 1;
        String result = this.list.remove(last);
        return result;
    }

    @Override // org.apache.logging.log4j.ThreadContext.ContextStack
    public String peek() {
        if (this.list.isEmpty()) {
            return null;
        }
        int last = this.list.size() - 1;
        return this.list.get(last);
    }

    @Override // org.apache.logging.log4j.ThreadContext.ContextStack
    public void push(final String message) {
        checkInvariants();
        this.list.add(message);
    }

    @Override // org.apache.logging.log4j.ThreadContext.ContextStack
    public int getDepth() {
        return this.list.size();
    }

    @Override // org.apache.logging.log4j.ThreadContext.ContextStack
    public List<String> asList() {
        return this.list;
    }

    @Override // org.apache.logging.log4j.ThreadContext.ContextStack
    public void trim(final int depth) {
        checkInvariants();
        if (depth < 0) {
            throw new IllegalArgumentException("Maximum stack depth cannot be negative");
        }
        if (this.list == null) {
            return;
        }
        List<String> copy = new ArrayList<>(this.list.size());
        int count = Math.min(depth, this.list.size());
        for (int i = 0; i < count; i++) {
            copy.add(this.list.get(i));
        }
        this.list.clear();
        this.list.addAll(copy);
    }

    @Override // org.apache.logging.log4j.ThreadContext.ContextStack
    public ThreadContextStack copy() {
        return new MutableThreadContextStack(this);
    }

    @Override // java.util.Collection
    public void clear() {
        checkInvariants();
        this.list.clear();
    }

    @Override // java.util.Collection
    public int size() {
        return this.list.size();
    }

    @Override // java.util.Collection
    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    @Override // java.util.Collection
    public boolean contains(final Object o) {
        return this.list.contains(o);
    }

    @Override // java.util.Collection, java.lang.Iterable
    public Iterator<String> iterator() {
        return this.list.iterator();
    }

    @Override // java.util.Collection
    public Object[] toArray() {
        return this.list.toArray();
    }

    @Override // java.util.Collection
    public <T> T[] toArray(T[] tArr) {
        return (T[]) this.list.toArray(tArr);
    }

    @Override // java.util.Collection
    public boolean add(final String s) {
        checkInvariants();
        return this.list.add(s);
    }

    @Override // java.util.Collection
    public boolean remove(final Object o) {
        checkInvariants();
        return this.list.remove(o);
    }

    @Override // java.util.Collection
    public boolean containsAll(final Collection<?> objects) {
        return this.list.containsAll(objects);
    }

    @Override // java.util.Collection
    public boolean addAll(final Collection<? extends String> strings) {
        checkInvariants();
        return this.list.addAll(strings);
    }

    @Override // java.util.Collection
    public boolean removeAll(final Collection<?> objects) {
        checkInvariants();
        return this.list.removeAll(objects);
    }

    @Override // java.util.Collection
    public boolean retainAll(final Collection<?> objects) {
        checkInvariants();
        return this.list.retainAll(objects);
    }

    public String toString() {
        return String.valueOf(this.list);
    }

    @Override // org.apache.logging.log4j.util.StringBuilderFormattable
    public void formatTo(final StringBuilder buffer) {
        buffer.append('[');
        for (int i = 0; i < this.list.size(); i++) {
            if (i > 0) {
                buffer.append(',').append(' ');
            }
            buffer.append(this.list.get(i));
        }
        buffer.append(']');
    }

    @Override // java.util.Collection
    public int hashCode() {
        return 31 + Objects.hashCode(this.list);
    }

    @Override // java.util.Collection
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof ThreadContextStack)) {
            return false;
        }
        ThreadContextStack other = (ThreadContextStack) obj;
        List<String> otherAsList = other.asList();
        return Objects.equals(this.list, otherAsList);
    }

    @Override // org.apache.logging.log4j.ThreadContext.ContextStack
    public ThreadContext.ContextStack getImmutableStackOrNull() {
        return copy();
    }

    public void freeze() {
        this.frozen = true;
    }

    public boolean isFrozen() {
        return this.frozen;
    }
}

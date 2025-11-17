package com.fasterxml.jackson.databind.util.internal;

import com.fasterxml.jackson.databind.util.internal.Linked;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/internal/LinkedDeque.class */
final class LinkedDeque<E extends Linked<E>> extends AbstractCollection<E> implements Deque<E> {
    E first;
    E last;

    void linkFirst(E e) {
        E f = this.first;
        this.first = e;
        if (f == null) {
            this.last = e;
        } else {
            f.setPrevious(e);
            e.setNext(f);
        }
    }

    void linkLast(E e) {
        E l = this.last;
        this.last = e;
        if (l == null) {
            this.first = e;
        } else {
            l.setNext(e);
            e.setPrevious(l);
        }
    }

    E unlinkFirst() {
        E e = this.first;
        E e2 = (E) e.getNext();
        e.setNext(null);
        this.first = e2;
        if (e2 == null) {
            this.last = null;
        } else {
            e2.setPrevious(null);
        }
        return e;
    }

    E unlinkLast() {
        E e = this.last;
        E e2 = (E) e.getPrevious();
        e.setPrevious(null);
        this.last = e2;
        if (e2 == null) {
            this.first = null;
        } else {
            e2.setNext(null);
        }
        return e;
    }

    void unlink(E e) {
        E e2 = (E) e.getPrevious();
        E e3 = (E) e.getNext();
        if (e2 == null) {
            this.first = e3;
        } else {
            e2.setNext(e3);
            e.setPrevious(null);
        }
        if (e3 == null) {
            this.last = e2;
        } else {
            e3.setPrevious(e2);
            e.setNext(null);
        }
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public boolean isEmpty() {
        return this.first == null;
    }

    void checkNotEmpty() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Deque
    public int size() {
        int size = 0;
        Linked linked = this.first;
        while (true) {
            Linked linked2 = linked;
            if (linked2 != null) {
                size++;
                linked = linked2.getNext();
            } else {
                return size;
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v6, types: [com.fasterxml.jackson.databind.util.internal.Linked] */
    @Override // java.util.AbstractCollection, java.util.Collection
    public void clear() {
        E e = this.first;
        while (true) {
            E e2 = e;
            if (e2 != null) {
                ?? next = e2.getNext();
                e2.setPrevious(null);
                e2.setNext(null);
                e = next;
            } else {
                this.last = null;
                this.first = null;
                return;
            }
        }
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Deque
    public boolean contains(Object o) {
        return (o instanceof Linked) && contains((Linked<?>) o);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean contains(Linked<?> e) {
        return (e.getPrevious() == null && e.getNext() == null && e != this.first) ? false : true;
    }

    public void moveToFront(E e) {
        if (e != this.first) {
            unlink(e);
            linkFirst(e);
        }
    }

    public void moveToBack(E e) {
        if (e != this.last) {
            unlink(e);
            linkLast(e);
        }
    }

    @Override // java.util.Deque, java.util.Queue
    public E peek() {
        return peekFirst();
    }

    @Override // java.util.Deque
    public E peekFirst() {
        return this.first;
    }

    @Override // java.util.Deque
    public E peekLast() {
        return this.last;
    }

    @Override // java.util.Deque
    public E getFirst() {
        checkNotEmpty();
        return peekFirst();
    }

    @Override // java.util.Deque
    public E getLast() {
        checkNotEmpty();
        return peekLast();
    }

    @Override // java.util.Deque, java.util.Queue
    public E element() {
        return getFirst();
    }

    @Override // java.util.Deque, java.util.Queue
    public boolean offer(E e) {
        return offerLast((LinkedDeque<E>) e);
    }

    @Override // java.util.Deque
    public boolean offerFirst(E e) {
        if (contains((Linked<?>) e)) {
            return false;
        }
        linkFirst(e);
        return true;
    }

    @Override // java.util.Deque
    public boolean offerLast(E e) {
        if (contains((Linked<?>) e)) {
            return false;
        }
        linkLast(e);
        return true;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Deque, java.util.Queue
    public boolean add(E e) {
        return offerLast((LinkedDeque<E>) e);
    }

    @Override // java.util.Deque
    public void addFirst(E e) {
        if (!offerFirst((LinkedDeque<E>) e)) {
            throw new IllegalArgumentException();
        }
    }

    @Override // java.util.Deque
    public void addLast(E e) {
        if (!offerLast((LinkedDeque<E>) e)) {
            throw new IllegalArgumentException();
        }
    }

    @Override // java.util.Deque, java.util.Queue
    public E poll() {
        return pollFirst();
    }

    @Override // java.util.Deque
    public E pollFirst() {
        if (isEmpty()) {
            return null;
        }
        return unlinkFirst();
    }

    @Override // java.util.Deque
    public E pollLast() {
        if (isEmpty()) {
            return null;
        }
        return unlinkLast();
    }

    @Override // java.util.Deque, java.util.Queue
    public E remove() {
        return removeFirst();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Deque
    public boolean remove(Object o) {
        return (o instanceof Linked) && remove((LinkedDeque<E>) o);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean remove(E e) {
        if (contains((Linked<?>) e)) {
            unlink(e);
            return true;
        }
        return false;
    }

    @Override // java.util.Deque
    public E removeFirst() {
        checkNotEmpty();
        return pollFirst();
    }

    @Override // java.util.Deque
    public boolean removeFirstOccurrence(Object o) {
        return remove(o);
    }

    @Override // java.util.Deque
    public E removeLast() {
        checkNotEmpty();
        return pollLast();
    }

    @Override // java.util.Deque
    public boolean removeLastOccurrence(Object o) {
        return remove(o);
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        for (Object o : c) {
            modified |= remove(o);
        }
        return modified;
    }

    @Override // java.util.Deque
    public void push(E e) {
        addFirst((LinkedDeque<E>) e);
    }

    @Override // java.util.Deque
    public E pop() {
        return removeFirst();
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Deque
    public Iterator<E> iterator() {
        return new LinkedDeque<E>.AbstractLinkedIterator(this.first) { // from class: com.fasterxml.jackson.databind.util.internal.LinkedDeque.1
            @Override // com.fasterxml.jackson.databind.util.internal.LinkedDeque.AbstractLinkedIterator
            E computeNext() {
                return (E) this.cursor.getNext();
            }
        };
    }

    @Override // java.util.Deque
    public Iterator<E> descendingIterator() {
        return new LinkedDeque<E>.AbstractLinkedIterator(this.last) { // from class: com.fasterxml.jackson.databind.util.internal.LinkedDeque.2
            @Override // com.fasterxml.jackson.databind.util.internal.LinkedDeque.AbstractLinkedIterator
            E computeNext() {
                return (E) this.cursor.getPrevious();
            }
        };
    }

    /* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/internal/LinkedDeque$AbstractLinkedIterator.class */
    abstract class AbstractLinkedIterator implements Iterator<E> {
        E cursor;

        abstract E computeNext();

        AbstractLinkedIterator(E start) {
            this.cursor = start;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.cursor != null;
        }

        @Override // java.util.Iterator
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            E e = this.cursor;
            this.cursor = (E) computeNext();
            return e;
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

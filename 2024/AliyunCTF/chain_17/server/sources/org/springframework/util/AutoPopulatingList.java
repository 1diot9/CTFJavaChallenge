package org.springframework.util;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/AutoPopulatingList.class */
public class AutoPopulatingList<E> implements List<E>, Serializable {
    private final List<E> backingList;
    private final ElementFactory<E> elementFactory;

    @FunctionalInterface
    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/AutoPopulatingList$ElementFactory.class */
    public interface ElementFactory<E> {
        E createElement(int index) throws ElementInstantiationException;
    }

    public AutoPopulatingList(Class<? extends E> elementClass) {
        this(new ArrayList(), elementClass);
    }

    public AutoPopulatingList(List<E> backingList, Class<? extends E> elementClass) {
        this(backingList, new ReflectiveElementFactory(elementClass));
    }

    public AutoPopulatingList(ElementFactory<E> elementFactory) {
        this(new ArrayList(), elementFactory);
    }

    public AutoPopulatingList(List<E> backingList, ElementFactory<E> elementFactory) {
        Assert.notNull(backingList, "Backing List must not be null");
        Assert.notNull(elementFactory, "Element factory must not be null");
        this.backingList = backingList;
        this.elementFactory = elementFactory;
    }

    @Override // java.util.List
    public void add(int index, E element) {
        this.backingList.add(index, element);
    }

    @Override // java.util.List, java.util.Collection
    public boolean add(E o) {
        return this.backingList.add(o);
    }

    @Override // java.util.List, java.util.Collection
    public boolean addAll(Collection<? extends E> c) {
        return this.backingList.addAll(c);
    }

    @Override // java.util.List
    public boolean addAll(int index, Collection<? extends E> c) {
        return this.backingList.addAll(index, c);
    }

    @Override // java.util.List, java.util.Collection
    public void clear() {
        this.backingList.clear();
    }

    @Override // java.util.List, java.util.Collection
    public boolean contains(Object o) {
        return this.backingList.contains(o);
    }

    @Override // java.util.List, java.util.Collection
    public boolean containsAll(Collection<?> c) {
        return this.backingList.containsAll(c);
    }

    @Override // java.util.List
    public E get(int index) {
        E element;
        int backingListSize = this.backingList.size();
        if (index < backingListSize) {
            element = this.backingList.get(index);
            if (element == null) {
                element = this.elementFactory.createElement(index);
                this.backingList.set(index, element);
            }
        } else {
            for (int x = backingListSize; x < index; x++) {
                this.backingList.add(null);
            }
            element = this.elementFactory.createElement(index);
            this.backingList.add(element);
        }
        return element;
    }

    @Override // java.util.List
    public int indexOf(Object o) {
        return this.backingList.indexOf(o);
    }

    @Override // java.util.List, java.util.Collection
    public boolean isEmpty() {
        return this.backingList.isEmpty();
    }

    @Override // java.util.List, java.util.Collection, java.lang.Iterable
    public Iterator<E> iterator() {
        return this.backingList.iterator();
    }

    @Override // java.util.List
    public int lastIndexOf(Object o) {
        return this.backingList.lastIndexOf(o);
    }

    @Override // java.util.List
    public ListIterator<E> listIterator() {
        return this.backingList.listIterator();
    }

    @Override // java.util.List
    public ListIterator<E> listIterator(int index) {
        return this.backingList.listIterator(index);
    }

    @Override // java.util.List
    public E remove(int index) {
        return this.backingList.remove(index);
    }

    @Override // java.util.List, java.util.Collection
    public boolean remove(Object o) {
        return this.backingList.remove(o);
    }

    @Override // java.util.List, java.util.Collection
    public boolean removeAll(Collection<?> c) {
        return this.backingList.removeAll(c);
    }

    @Override // java.util.List, java.util.Collection
    public boolean retainAll(Collection<?> c) {
        return this.backingList.retainAll(c);
    }

    @Override // java.util.List
    public E set(int index, E element) {
        return this.backingList.set(index, element);
    }

    @Override // java.util.List, java.util.Collection
    public int size() {
        return this.backingList.size();
    }

    @Override // java.util.List
    public List<E> subList(int fromIndex, int toIndex) {
        return this.backingList.subList(fromIndex, toIndex);
    }

    @Override // java.util.List, java.util.Collection
    public Object[] toArray() {
        return this.backingList.toArray();
    }

    @Override // java.util.List, java.util.Collection
    public <T> T[] toArray(T[] tArr) {
        return (T[]) this.backingList.toArray(tArr);
    }

    @Override // java.util.List, java.util.Collection
    public boolean equals(@Nullable Object other) {
        return this.backingList.equals(other);
    }

    @Override // java.util.List, java.util.Collection
    public int hashCode() {
        return this.backingList.hashCode();
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/AutoPopulatingList$ElementInstantiationException.class */
    public static class ElementInstantiationException extends RuntimeException {
        public ElementInstantiationException(String msg) {
            super(msg);
        }

        public ElementInstantiationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/AutoPopulatingList$ReflectiveElementFactory.class */
    private static class ReflectiveElementFactory<E> implements ElementFactory<E>, Serializable {
        private final Class<? extends E> elementClass;

        public ReflectiveElementFactory(Class<? extends E> elementClass) {
            Assert.notNull(elementClass, "Element class must not be null");
            Assert.isTrue(!elementClass.isInterface(), "Element class must not be an interface type");
            Assert.isTrue(!Modifier.isAbstract(elementClass.getModifiers()), "Element class cannot be an abstract class");
            this.elementClass = elementClass;
        }

        @Override // org.springframework.util.AutoPopulatingList.ElementFactory
        public E createElement(int i) {
            try {
                return (E) ReflectionUtils.accessibleConstructor(this.elementClass, new Class[0]).newInstance(new Object[0]);
            } catch (IllegalAccessException e) {
                throw new ElementInstantiationException("Could not access element constructor: " + this.elementClass.getName(), e);
            } catch (InstantiationException e2) {
                throw new ElementInstantiationException("Unable to instantiate element class: " + this.elementClass.getName(), e2);
            } catch (NoSuchMethodException e3) {
                throw new ElementInstantiationException("No default constructor on element class: " + this.elementClass.getName(), e3);
            } catch (InvocationTargetException e4) {
                throw new ElementInstantiationException("Failed to invoke element constructor: " + this.elementClass.getName(), e4.getTargetException());
            }
        }
    }
}

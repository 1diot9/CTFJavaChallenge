package org.yaml.snakeyaml.util;

import java.util.AbstractList;
import java.util.Collections;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/util/ArrayUtils.class */
public class ArrayUtils {
    private ArrayUtils() {
    }

    public static <E> List<E> toUnmodifiableList(E[] elements) {
        return elements.length == 0 ? Collections.emptyList() : new UnmodifiableArrayList(elements);
    }

    public static <E> List<E> toUnmodifiableCompositeList(E[] array1, E[] array2) {
        List<E> result;
        if (array1.length == 0) {
            result = toUnmodifiableList(array2);
        } else if (array2.length == 0) {
            result = toUnmodifiableList(array1);
        } else {
            result = new CompositeUnmodifiableArrayList<>(array1, array2);
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/util/ArrayUtils$UnmodifiableArrayList.class */
    public static class UnmodifiableArrayList<E> extends AbstractList<E> {
        private final E[] array;

        UnmodifiableArrayList(E[] array) {
            this.array = array;
        }

        @Override // java.util.AbstractList, java.util.List
        public E get(int index) {
            if (index >= this.array.length) {
                throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size());
            }
            return this.array[index];
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return this.array.length;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/util/ArrayUtils$CompositeUnmodifiableArrayList.class */
    private static class CompositeUnmodifiableArrayList<E> extends AbstractList<E> {
        private final E[] array1;
        private final E[] array2;

        CompositeUnmodifiableArrayList(E[] array1, E[] array2) {
            this.array1 = array1;
            this.array2 = array2;
        }

        @Override // java.util.AbstractList, java.util.List
        public E get(int index) {
            E element;
            if (index < this.array1.length) {
                element = this.array1[index];
            } else if (index - this.array1.length < this.array2.length) {
                element = this.array2[index - this.array1.length];
            } else {
                throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size());
            }
            return element;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return this.array1.length + this.array2.length;
        }
    }
}

package io.micrometer.common;

import io.micrometer.common.lang.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/* loaded from: agent.jar:BOOT-INF/lib/micrometer-commons-1.12.2.jar:io/micrometer/common/KeyValues.class */
public final class KeyValues implements Iterable<KeyValue> {
    private static final KeyValues EMPTY = new KeyValues(new KeyValue[0]);
    private final KeyValue[] keyValues;
    private int last;

    private KeyValues(KeyValue[] keyValues) {
        this.keyValues = keyValues;
        Arrays.sort(this.keyValues);
        dedup();
    }

    private void dedup() {
        int n = this.keyValues.length;
        if (n == 0 || n == 1) {
            this.last = n;
            return;
        }
        int j = 0;
        for (int i = 0; i < n - 1; i++) {
            if (!this.keyValues[i].getKey().equals(this.keyValues[i + 1].getKey())) {
                int i2 = j;
                j++;
                this.keyValues[i2] = this.keyValues[i];
            }
        }
        this.keyValues[j] = this.keyValues[n - 1];
        this.last = j + 1;
    }

    public KeyValues and(String key, String value) {
        return and(KeyValue.of(key, value));
    }

    public KeyValues and(@Nullable String... keyValues) {
        if (blankVarargs(keyValues)) {
            return this;
        }
        return and(of(keyValues));
    }

    public KeyValues and(@Nullable KeyValue... keyValues) {
        if (blankVarargs(keyValues)) {
            return this;
        }
        KeyValue[] newKeyValues = new KeyValue[this.last + keyValues.length];
        System.arraycopy(this.keyValues, 0, newKeyValues, 0, this.last);
        System.arraycopy(keyValues, 0, newKeyValues, this.last, keyValues.length);
        return new KeyValues(newKeyValues);
    }

    public <E> KeyValues and(@Nullable Iterable<E> elements, Function<E, String> keyExtractor, Function<E, String> valueExtractor) {
        if (elements == null || !elements.iterator().hasNext()) {
            return this;
        }
        Function<E, KeyValue> mapper = element -> {
            return KeyValue.of(element, (Function<Object, String>) keyExtractor, (Function<Object, String>) valueExtractor);
        };
        Iterable<KeyValue> keyValues = () -> {
            return StreamSupport.stream(elements.spliterator(), false).map(mapper).iterator();
        };
        return and(keyValues);
    }

    public KeyValues and(@Nullable Iterable<? extends KeyValue> keyValues) {
        if (keyValues == null || keyValues == EMPTY || !keyValues.iterator().hasNext()) {
            return this;
        }
        if (this.keyValues.length == 0) {
            return of(keyValues);
        }
        return and(of(keyValues).keyValues);
    }

    @Override // java.lang.Iterable
    public Iterator<KeyValue> iterator() {
        return new ArrayIterator();
    }

    /* loaded from: agent.jar:BOOT-INF/lib/micrometer-commons-1.12.2.jar:io/micrometer/common/KeyValues$ArrayIterator.class */
    private class ArrayIterator implements Iterator<KeyValue> {
        private int currentIndex;

        private ArrayIterator() {
            this.currentIndex = 0;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.currentIndex < KeyValues.this.last;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Iterator
        public KeyValue next() {
            KeyValue[] keyValueArr = KeyValues.this.keyValues;
            int i = this.currentIndex;
            this.currentIndex = i + 1;
            return keyValueArr[i];
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException("cannot remove items from key values");
        }
    }

    @Override // java.lang.Iterable
    public Spliterator<KeyValue> spliterator() {
        return Spliterators.spliterator(this.keyValues, 0, this.last, 1301);
    }

    public Stream<KeyValue> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    public int hashCode() {
        int result = 1;
        for (int i = 0; i < this.last; i++) {
            result = (31 * result) + this.keyValues[i].hashCode();
        }
        return result;
    }

    public boolean equals(@Nullable Object obj) {
        return this == obj || (obj != null && getClass() == obj.getClass() && keyValuesEqual((KeyValues) obj));
    }

    private boolean keyValuesEqual(KeyValues obj) {
        if (this.keyValues == obj.keyValues) {
            return true;
        }
        if (this.last != obj.last) {
            return false;
        }
        for (int i = 0; i < this.last; i++) {
            if (!this.keyValues[i].equals(obj.keyValues[i])) {
                return false;
            }
        }
        return true;
    }

    public static KeyValues concat(@Nullable Iterable<? extends KeyValue> keyValues, @Nullable Iterable<? extends KeyValue> otherKeyValues) {
        return of(keyValues).and(otherKeyValues);
    }

    public static KeyValues concat(@Nullable Iterable<? extends KeyValue> keyValues, @Nullable String... otherKeyValues) {
        return of(keyValues).and(otherKeyValues);
    }

    public static <E> KeyValues of(@Nullable Iterable<E> elements, Function<E, String> keyExtractor, Function<E, String> valueExtractor) {
        return empty().and(elements, keyExtractor, valueExtractor);
    }

    public static KeyValues of(@Nullable Iterable<? extends KeyValue> keyValues) {
        if (keyValues == null || keyValues == EMPTY || !keyValues.iterator().hasNext()) {
            return empty();
        }
        if (keyValues instanceof KeyValues) {
            return (KeyValues) keyValues;
        }
        if (keyValues instanceof Collection) {
            Collection<? extends KeyValue> keyValuesCollection = (Collection) keyValues;
            return new KeyValues((KeyValue[]) keyValuesCollection.toArray(new KeyValue[0]));
        }
        return new KeyValues((KeyValue[]) StreamSupport.stream(keyValues.spliterator(), false).toArray(x$0 -> {
            return new KeyValue[x$0];
        }));
    }

    public static KeyValues of(String key, String value) {
        return new KeyValues(new KeyValue[]{KeyValue.of(key, value)});
    }

    public static KeyValues of(@Nullable String... keyValues) {
        if (blankVarargs(keyValues)) {
            return empty();
        }
        if (keyValues.length % 2 == 1) {
            throw new IllegalArgumentException("size must be even, it is a set of key=value pairs");
        }
        KeyValue[] keyValueArray = new KeyValue[keyValues.length / 2];
        for (int i = 0; i < keyValues.length; i += 2) {
            keyValueArray[i / 2] = KeyValue.of(keyValues[i], keyValues[i + 1]);
        }
        return new KeyValues(keyValueArray);
    }

    private static boolean blankVarargs(@Nullable Object[] args) {
        return args == null || args.length == 0 || (args.length == 1 && args[0] == null);
    }

    public static KeyValues of(@Nullable KeyValue... keyValues) {
        return empty().and(keyValues);
    }

    public static KeyValues empty() {
        return EMPTY;
    }

    public String toString() {
        return (String) stream().map((v0) -> {
            return v0.toString();
        }).collect(Collectors.joining(",", "[", "]"));
    }
}

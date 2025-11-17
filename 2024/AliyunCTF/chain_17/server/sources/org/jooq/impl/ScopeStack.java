package org.jooq.impl;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ScopeStack.class */
final class ScopeStack<K, V> implements Iterable<V> {
    private int scopeLevel;
    private Map<K, List<V>> stack;
    private final ObjIntFunction<K, V> constructor;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ScopeStack() {
        this((ObjIntFunction) null);
    }

    ScopeStack(V defaultValue) {
        this((part, scopeLevel) -> {
            return defaultValue;
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ScopeStack(ObjIntFunction<K, V> constructor) {
        this.scopeLevel = -1;
        this.constructor = constructor;
    }

    private final Map<K, List<V>> stack() {
        if (this.stack == null) {
            this.stack = new LinkedHashMap();
        }
        return this.stack;
    }

    private final void trim() {
        int l = this.scopeLevel + 1;
        if (l >= 0) {
            Iterator<Map.Entry<K, List<V>>> it = stack().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<K, List<V>> entry = it.next();
                List<V> list = entry.getValue();
                while (true) {
                    int size = list.size();
                    if (size > l || (size > 0 && list.get(size - 1) == null)) {
                        list.remove(size - 1);
                    }
                }
                if (list.isEmpty()) {
                    it.remove();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean isEmpty() {
        return !iterator().hasNext();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Iterable<Value<V>> valueIterable() {
        return () -> {
            return new ScopeStackIterator(Value::lastOf, e -> {
                return true;
            });
        };
    }

    @Override // java.lang.Iterable
    public final Iterator<V> iterator() {
        return iterable(e -> {
            return true;
        }).iterator();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Iterable<V> iterableAtScopeLevel() {
        return () -> {
            return new ScopeStackIterator(list -> {
                if (list.size() == this.scopeLevel + 1) {
                    return list.get(this.scopeLevel);
                }
                return null;
            }, e -> {
                return true;
            });
        };
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Iterable<V> iterable(Predicate<? super V> filter) {
        return () -> {
            return new ScopeStackIterator(list -> {
                return list.get(list.size() - 1);
            }, filter);
        };
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ScopeStack$Value.class */
    static final class Value<V> extends Record {
        private final int scopeLevel;
        private final V value;

        /* JADX INFO: Access modifiers changed from: package-private */
        public Value(int scopeLevel, V value) {
            this.scopeLevel = scopeLevel;
            this.value = value;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Value.class), Value.class, "scopeLevel;value", "FIELD:Lorg/jooq/impl/ScopeStack$Value;->scopeLevel:I", "FIELD:Lorg/jooq/impl/ScopeStack$Value;->value:Ljava/lang/Object;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Value.class), Value.class, "scopeLevel;value", "FIELD:Lorg/jooq/impl/ScopeStack$Value;->scopeLevel:I", "FIELD:Lorg/jooq/impl/ScopeStack$Value;->value:Ljava/lang/Object;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Value.class, Object.class), Value.class, "scopeLevel;value", "FIELD:Lorg/jooq/impl/ScopeStack$Value;->scopeLevel:I", "FIELD:Lorg/jooq/impl/ScopeStack$Value;->value:Ljava/lang/Object;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public int scopeLevel() {
            return this.scopeLevel;
        }

        public V value() {
            return this.value;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static <V> Value<V> of(int scopeLevel, V value) {
            if (value == null) {
                return null;
            }
            return new Value<>(scopeLevel, value);
        }

        static <V> Value<V> lastOf(List<V> list) {
            int size = list.size();
            V value = list.get(size - 1);
            return of(size - 1, value);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ScopeStack$ScopeStackIterator.class */
    private final class ScopeStackIterator<U> implements Iterator<U> {
        final Iterator<List<V>> it;
        final java.util.function.Function<List<V>, U> valueExtractor;
        final Predicate<? super U> filter;
        U next;

        ScopeStackIterator(java.util.function.Function<List<V>, U> valueExtractor, Predicate<? super U> filter) {
            this.it = ScopeStack.this.stack().values().iterator();
            this.valueExtractor = valueExtractor;
            this.filter = filter;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return move() != null;
        }

        @Override // java.util.Iterator
        public U next() {
            if (this.next == null) {
                return move();
            }
            U result = this.next;
            this.next = null;
            return result;
        }

        private U move() {
            while (this.it.hasNext()) {
                List<V> next = this.it.next();
                if (!next.isEmpty()) {
                    U apply = this.valueExtractor.apply(next);
                    this.next = apply;
                    if (apply != null && this.filter.test(this.next)) {
                        break;
                    }
                }
                this.next = null;
            }
            return this.next;
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException("remove");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void setAll(V value) {
        for (K key : stack().keySet()) {
            set(key, value);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void set(K key, V value) {
        set0(list(key), value);
    }

    private final V get0(List<V> list) {
        int i;
        if (list == null || (i = list.size()) == 0) {
            return null;
        }
        return list.get(i - 1);
    }

    private final V getCurrentScope0(List<V> list) {
        int i;
        if (list == null || (i = list.size()) == 0 || this.scopeLevel >= i) {
            return null;
        }
        return list.get(i - 1);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final V get(K key) {
        return get0(listOrNull(key));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final V getCurrentScope(K key) {
        return getCurrentScope0(listOrNull(key));
    }

    final <T extends Throwable> V getOrThrow(K key, Supplier<T> exception) throws Throwable {
        V result = get(key);
        if (result == null) {
            throw exception.get();
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final V getOrCreate(K key) {
        List<V> list = list(key);
        V result = get0(list);
        return result != null ? result : create0(key, list);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final V create(K key) {
        return create0(key, list(key));
    }

    private final V create0(K key, List<V> list) {
        V result = this.constructor.apply(key, this.scopeLevel);
        set0(list, result);
        return result;
    }

    private void set0(List<V> list, V value) {
        int l = this.scopeLevel + 1;
        int size = list.size();
        if (size < l) {
            list.addAll(Collections.nCopies(l - size, null));
        }
        list.set(this.scopeLevel, value);
    }

    private List<V> listOrNull(K key) {
        return stack().get(key);
    }

    private List<V> list(K key) {
        return stack().computeIfAbsent(key, k -> {
            return new ArrayList();
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean inScope() {
        return this.scopeLevel > -1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int scopeLevel() {
        return this.scopeLevel;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void scopeStart() {
        this.scopeLevel++;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void scopeEnd() {
        this.scopeLevel--;
        trim();
    }

    public String toString() {
        return stack().toString();
    }
}

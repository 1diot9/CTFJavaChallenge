package com.fasterxml.jackson.databind.ser.impl;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.util.LRUMap;
import com.fasterxml.jackson.databind.util.TypeKey;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/ser/impl/ReadOnlyClassToSerializerMap.class */
public final class ReadOnlyClassToSerializerMap {
    private final Bucket[] _buckets;
    private final int _size;
    private final int _mask;

    public ReadOnlyClassToSerializerMap(LRUMap<TypeKey, JsonSerializer<Object>> src) {
        this._size = findSize(src.size());
        this._mask = this._size - 1;
        Bucket[] buckets = new Bucket[this._size];
        src.contents((key, value) -> {
            int index = key.hashCode() & this._mask;
            buckets[index] = new Bucket(buckets[index], key, value);
        });
        this._buckets = buckets;
    }

    private static final int findSize(int size) {
        int needed = size <= 64 ? size + size : size + (size >> 2);
        int i = 8;
        while (true) {
            int result = i;
            if (result < needed) {
                i = result + result;
            } else {
                return result;
            }
        }
    }

    public static ReadOnlyClassToSerializerMap from(LRUMap<TypeKey, JsonSerializer<Object>> src) {
        return new ReadOnlyClassToSerializerMap(src);
    }

    public int size() {
        return this._size;
    }

    public JsonSerializer<Object> typedValueSerializer(JavaType type) {
        Bucket bucket = this._buckets[TypeKey.typedHash(type) & this._mask];
        if (bucket == null) {
            return null;
        }
        if (bucket.matchesTyped(type)) {
            return bucket.value;
        }
        do {
            Bucket bucket2 = bucket.next;
            bucket = bucket2;
            if (bucket2 == null) {
                return null;
            }
        } while (!bucket.matchesTyped(type));
        return bucket.value;
    }

    public JsonSerializer<Object> typedValueSerializer(Class<?> type) {
        Bucket bucket = this._buckets[TypeKey.typedHash(type) & this._mask];
        if (bucket == null) {
            return null;
        }
        if (bucket.matchesTyped(type)) {
            return bucket.value;
        }
        do {
            Bucket bucket2 = bucket.next;
            bucket = bucket2;
            if (bucket2 == null) {
                return null;
            }
        } while (!bucket.matchesTyped(type));
        return bucket.value;
    }

    public JsonSerializer<Object> untypedValueSerializer(JavaType type) {
        Bucket bucket = this._buckets[TypeKey.untypedHash(type) & this._mask];
        if (bucket == null) {
            return null;
        }
        if (bucket.matchesUntyped(type)) {
            return bucket.value;
        }
        do {
            Bucket bucket2 = bucket.next;
            bucket = bucket2;
            if (bucket2 == null) {
                return null;
            }
        } while (!bucket.matchesUntyped(type));
        return bucket.value;
    }

    public JsonSerializer<Object> untypedValueSerializer(Class<?> type) {
        Bucket bucket = this._buckets[TypeKey.untypedHash(type) & this._mask];
        if (bucket == null) {
            return null;
        }
        if (bucket.matchesUntyped(type)) {
            return bucket.value;
        }
        do {
            Bucket bucket2 = bucket.next;
            bucket = bucket2;
            if (bucket2 == null) {
                return null;
            }
        } while (!bucket.matchesUntyped(type));
        return bucket.value;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/ser/impl/ReadOnlyClassToSerializerMap$Bucket.class */
    public static final class Bucket {
        public final JsonSerializer<Object> value;
        public final Bucket next;
        protected final Class<?> _class;
        protected final JavaType _type;
        protected final boolean _isTyped;

        public Bucket(Bucket next, TypeKey key, JsonSerializer<Object> value) {
            this.next = next;
            this.value = value;
            this._isTyped = key.isTyped();
            this._class = key.getRawType();
            this._type = key.getType();
        }

        public boolean matchesTyped(Class<?> key) {
            return this._class == key && this._isTyped;
        }

        public boolean matchesUntyped(Class<?> key) {
            return this._class == key && !this._isTyped;
        }

        public boolean matchesTyped(JavaType key) {
            return this._isTyped && key.equals(this._type);
        }

        public boolean matchesUntyped(JavaType key) {
            return !this._isTyped && key.equals(this._type);
        }
    }
}

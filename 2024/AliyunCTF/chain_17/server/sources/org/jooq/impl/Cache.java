package org.jooq.impl;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.util.Map;
import java.util.function.Supplier;
import org.jooq.Configuration;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Cache.class */
public final class Cache {
    private static final Object NULL = new Object();

    Cache() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    public static final <V> V run(Configuration configuration, Supplier<V> operation, CacheType type, Supplier<?> key) {
        if (configuration == null) {
            configuration = new DefaultConfiguration();
        }
        if (!type.category.predicate.test(configuration.settings())) {
            return operation.get();
        }
        Object cacheOrNull = configuration.data(type);
        if (cacheOrNull == null) {
            synchronized (type) {
                cacheOrNull = configuration.data(type);
                if (cacheOrNull == null) {
                    Object defaultIfNull = StringUtils.defaultIfNull(configuration.cacheProvider().provide(new DefaultCacheContext(configuration, type)), NULL);
                    cacheOrNull = defaultIfNull;
                    configuration.data(type, defaultIfNull);
                }
            }
        }
        if (cacheOrNull == NULL) {
            return operation.get();
        }
        Map<Object, Object> cache = (Map) cacheOrNull;
        Object k = key.get();
        V v = cache.get(k);
        if (v == null) {
            synchronized (cache) {
                v = cache.get(k);
                if (v == null) {
                    V v2 = operation.get();
                    v = v2;
                    cache.put(k, v2 == null ? NULL : v);
                }
            }
        }
        if (v == NULL) {
            return null;
        }
        return v;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Object key(Object key1, Object key2) {
        return new Key2(key1, key2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Cache$Key2.class */
    public static final class Key2 extends Record implements Serializable {
        private final Object key1;
        private final Object key2;

        private Key2(Object key1, Object key2) {
            this.key1 = key1;
            this.key2 = key2;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Key2.class), Key2.class, "key1;key2", "FIELD:Lorg/jooq/impl/Cache$Key2;->key1:Ljava/lang/Object;", "FIELD:Lorg/jooq/impl/Cache$Key2;->key2:Ljava/lang/Object;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Key2.class), Key2.class, "key1;key2", "FIELD:Lorg/jooq/impl/Cache$Key2;->key1:Ljava/lang/Object;", "FIELD:Lorg/jooq/impl/Cache$Key2;->key2:Ljava/lang/Object;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Key2.class, Object.class), Key2.class, "key1;key2", "FIELD:Lorg/jooq/impl/Cache$Key2;->key1:Ljava/lang/Object;", "FIELD:Lorg/jooq/impl/Cache$Key2;->key2:Ljava/lang/Object;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public Object key1() {
            return this.key1;
        }

        public Object key2() {
            return this.key2;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Object key(Object key1, Object key2, Object key3) {
        return new Key3(key1, key2, key3);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Cache$Key3.class */
    public static final class Key3 extends Record implements Serializable {
        private final Object key1;
        private final Object key2;
        private final Object key3;

        private Key3(Object key1, Object key2, Object key3) {
            this.key1 = key1;
            this.key2 = key2;
            this.key3 = key3;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Key3.class), Key3.class, "key1;key2;key3", "FIELD:Lorg/jooq/impl/Cache$Key3;->key1:Ljava/lang/Object;", "FIELD:Lorg/jooq/impl/Cache$Key3;->key2:Ljava/lang/Object;", "FIELD:Lorg/jooq/impl/Cache$Key3;->key3:Ljava/lang/Object;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Key3.class), Key3.class, "key1;key2;key3", "FIELD:Lorg/jooq/impl/Cache$Key3;->key1:Ljava/lang/Object;", "FIELD:Lorg/jooq/impl/Cache$Key3;->key2:Ljava/lang/Object;", "FIELD:Lorg/jooq/impl/Cache$Key3;->key3:Ljava/lang/Object;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Key3.class, Object.class), Key3.class, "key1;key2;key3", "FIELD:Lorg/jooq/impl/Cache$Key3;->key1:Ljava/lang/Object;", "FIELD:Lorg/jooq/impl/Cache$Key3;->key2:Ljava/lang/Object;", "FIELD:Lorg/jooq/impl/Cache$Key3;->key3:Ljava/lang/Object;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public Object key1() {
            return this.key1;
        }

        public Object key2() {
            return this.key2;
        }

        public Object key3() {
            return this.key3;
        }
    }
}

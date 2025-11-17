package org.springframework.cglib.core.internal;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/core/internal/LoadingCache.class */
public class LoadingCache<K, KK, V> {
    protected final ConcurrentMap<KK, Object> map = new ConcurrentHashMap();
    protected final Function<K, V> loader;
    protected final Function<K, KK> keyMapper;
    public static final Function IDENTITY = key -> {
        return key;
    };

    public LoadingCache(Function<K, KK> keyMapper, Function<K, V> loader) {
        this.keyMapper = keyMapper;
        this.loader = loader;
    }

    public static <K> Function<K, K> identity() {
        return IDENTITY;
    }

    public V get(K k) {
        KK apply = this.keyMapper.apply(k);
        V v = (V) this.map.get(apply);
        if (v != null && !(v instanceof FutureTask)) {
            return v;
        }
        return createEntry(k, apply, v);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v24, types: [V, java.lang.Object] */
    protected V createEntry(K k, KK kk, Object obj) {
        FutureTask futureTask;
        boolean z = false;
        if (obj != null) {
            futureTask = (FutureTask) obj;
        } else {
            futureTask = new FutureTask(() -> {
                return this.loader.apply(k);
            });
            V v = (V) this.map.putIfAbsent(kk, futureTask);
            if (v == 0) {
                z = true;
                futureTask.run();
            } else if (v instanceof FutureTask) {
                futureTask = (FutureTask) v;
            } else {
                return v;
            }
        }
        try {
            ?? r0 = futureTask.get();
            if (z) {
                this.map.put(kk, r0);
            }
            return r0;
        } catch (InterruptedException e) {
            throw new IllegalStateException("Interrupted while loading cache item", e);
        } catch (ExecutionException e2) {
            Throwable cause = e2.getCause();
            if (cause instanceof RuntimeException) {
                throw ((RuntimeException) cause);
            }
            throw new IllegalStateException("Unable to load cache item", cause);
        }
    }
}

package cn.hutool.core.lang.intern;

import cn.hutool.core.map.WeakConcurrentMap;
import java.lang.ref.WeakReference;
import java.util.function.Function;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/intern/WeakInterner.class */
public class WeakInterner<T> implements Interner<T> {
    private final WeakConcurrentMap<T, WeakReference<T>> cache = new WeakConcurrentMap<>();

    @Override // cn.hutool.core.lang.intern.Interner
    public T intern(T sample) {
        T val;
        if (sample == null) {
            return null;
        }
        do {
            val = this.cache.computeIfAbsent((WeakConcurrentMap<T, WeakReference<T>>) sample, (Function<? super WeakConcurrentMap<T, WeakReference<T>>, ? extends WeakReference<T>>) WeakReference::new).get();
        } while (val == null);
        return val;
    }
}

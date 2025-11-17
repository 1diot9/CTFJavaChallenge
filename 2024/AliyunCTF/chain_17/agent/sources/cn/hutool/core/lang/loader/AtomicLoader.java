package cn.hutool.core.lang.loader;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicReference;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/loader/AtomicLoader.class */
public abstract class AtomicLoader<T> implements Loader<T>, Serializable {
    private static final long serialVersionUID = 1;
    private final AtomicReference<T> reference = new AtomicReference<>();

    protected abstract T init();

    @Override // cn.hutool.core.lang.loader.Loader
    public T get() {
        T result = this.reference.get();
        if (result == null) {
            result = init();
            if (false == this.reference.compareAndSet(null, result)) {
                result = this.reference.get();
            }
        }
        return result;
    }
}

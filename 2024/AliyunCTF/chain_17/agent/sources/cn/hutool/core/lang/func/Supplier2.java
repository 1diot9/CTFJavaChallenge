package cn.hutool.core.lang.func;

import java.util.function.Supplier;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/func/Supplier2.class */
public interface Supplier2<T, P1, P2> {
    T get(P1 p1, P2 p2);

    default Supplier<T> toSupplier(P1 p1, P2 p2) {
        return () -> {
            return get(p1, p2);
        };
    }
}

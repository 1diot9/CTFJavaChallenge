package cn.hutool.core.lang.func;

import java.util.function.Supplier;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/func/Supplier3.class */
public interface Supplier3<T, P1, P2, P3> {
    T get(P1 p1, P2 p2, P3 p3);

    default Supplier<T> toSupplier(P1 p1, P2 p2, P3 p3) {
        return () -> {
            return get(p1, p2, p3);
        };
    }
}

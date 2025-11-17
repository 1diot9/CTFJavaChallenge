package cn.hutool.core.lang.func;

import java.util.function.Supplier;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/func/Supplier1.class */
public interface Supplier1<T, P1> {
    T get(P1 p1);

    default Supplier<T> toSupplier(P1 p1) {
        return () -> {
            return get(p1);
        };
    }
}

package cn.hutool.core.lang.func;

import java.io.Serializable;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/func/VoidFunc1.class */
public interface VoidFunc1<P> extends Serializable {
    void call(P p) throws Exception;

    default void callWithRuntimeException(P parameter) {
        try {
            call(parameter);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

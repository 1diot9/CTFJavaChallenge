package cn.hutool.core.lang.func;

import java.io.Serializable;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/func/VoidFunc.class */
public interface VoidFunc<P> extends Serializable {
    void call(P... pArr) throws Exception;

    default void callWithRuntimeException(P... parameters) {
        try {
            call(parameters);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

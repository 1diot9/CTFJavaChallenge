package cn.hutool.core.lang.func;

import java.io.Serializable;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/func/VoidFunc0.class */
public interface VoidFunc0 extends Serializable {
    void call() throws Exception;

    default void callWithRuntimeException() {
        try {
            call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

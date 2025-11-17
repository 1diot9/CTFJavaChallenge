package cn.hutool.core.lang.func;

import java.io.Serializable;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/func/Func0.class */
public interface Func0<R> extends Serializable {
    R call() throws Exception;

    default R callWithRuntimeException() {
        try {
            return call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

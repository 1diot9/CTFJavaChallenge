package cn.hutool.core.lang.func;

import java.io.Serializable;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/func/Func1.class */
public interface Func1<P, R> extends Serializable {
    R call(P p) throws Exception;

    default R callWithRuntimeException(P parameter) {
        try {
            return call(parameter);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

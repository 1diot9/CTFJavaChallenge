package cn.hutool.core.lang.func;

import java.io.Serializable;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/func/Func.class */
public interface Func<P, R> extends Serializable {
    R call(P... pArr) throws Exception;

    default R callWithRuntimeException(P... parameters) {
        try {
            return call(parameters);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

package cn.hutool.core.bean.copier;

import java.lang.reflect.Type;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/bean/copier/ValueProvider.class */
public interface ValueProvider<T> {
    Object value(T t, Type type);

    boolean containsKey(T t);
}

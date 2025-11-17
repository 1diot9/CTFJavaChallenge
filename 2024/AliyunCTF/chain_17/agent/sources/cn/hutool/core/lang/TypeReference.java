package cn.hutool.core.lang;

import cn.hutool.core.util.TypeUtil;
import java.lang.reflect.Type;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/TypeReference.class */
public abstract class TypeReference<T> implements Type {
    private final Type type = TypeUtil.getTypeArgument(getClass());

    public Type getType() {
        return this.type;
    }

    public String toString() {
        return this.type.toString();
    }
}

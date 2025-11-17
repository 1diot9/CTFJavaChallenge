package cn.hutool.core.lang;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/ParameterizedTypeImpl.class */
public class ParameterizedTypeImpl implements ParameterizedType, Serializable {
    private static final long serialVersionUID = 1;
    private final Type[] actualTypeArguments;
    private final Type ownerType;
    private final Type rawType;

    public ParameterizedTypeImpl(Type[] actualTypeArguments, Type ownerType, Type rawType) {
        this.actualTypeArguments = actualTypeArguments;
        this.ownerType = ownerType;
        this.rawType = rawType;
    }

    @Override // java.lang.reflect.ParameterizedType
    public Type[] getActualTypeArguments() {
        return this.actualTypeArguments;
    }

    @Override // java.lang.reflect.ParameterizedType
    public Type getOwnerType() {
        return this.ownerType;
    }

    @Override // java.lang.reflect.ParameterizedType
    public Type getRawType() {
        return this.rawType;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        Type useOwner = this.ownerType;
        Class<?> raw = (Class) this.rawType;
        if (useOwner == null) {
            buf.append(raw.getName());
        } else {
            if (useOwner instanceof Class) {
                buf.append(((Class) useOwner).getName());
            } else {
                buf.append(useOwner.toString());
            }
            buf.append('.').append(raw.getSimpleName());
        }
        appendAllTo(buf.append('<'), ", ", this.actualTypeArguments).append('>');
        return buf.toString();
    }

    private static StringBuilder appendAllTo(StringBuilder buf, String sep, Type... types) {
        String strUtil;
        if (ArrayUtil.isNotEmpty((Object[]) types)) {
            boolean isFirst = true;
            for (Type type : types) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    buf.append(sep);
                }
                if (type instanceof Class) {
                    strUtil = ((Class) type).getName();
                } else {
                    strUtil = StrUtil.toString(type);
                }
                String typeStr = strUtil;
                buf.append(typeStr);
            }
        }
        return buf;
    }
}

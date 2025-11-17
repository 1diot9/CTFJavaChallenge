package cn.hutool.core.convert;

import java.lang.reflect.Type;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/convert/TypeConverter.class */
public interface TypeConverter {
    Object convert(Type type, Object obj);
}

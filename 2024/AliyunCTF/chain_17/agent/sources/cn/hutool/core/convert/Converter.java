package cn.hutool.core.convert;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/convert/Converter.class */
public interface Converter<T> {
    T convert(Object obj, T t) throws IllegalArgumentException;

    default T convertWithCheck(Object value, T defaultValue, boolean quietly) {
        try {
            return convert(value, defaultValue);
        } catch (Exception e) {
            if (quietly) {
                return defaultValue;
            }
            throw e;
        }
    }
}

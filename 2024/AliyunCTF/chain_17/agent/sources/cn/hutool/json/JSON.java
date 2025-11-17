package cn.hutool.json;

import cn.hutool.core.lang.TypeReference;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/json/JSON.class */
public interface JSON extends Cloneable, Serializable {
    JSONConfig getConfig();

    Object getByPath(String str);

    void putByPath(String str, Object obj);

    <T> T getByPath(String str, Class<T> cls);

    Writer write(Writer writer, int i, int i2) throws JSONException;

    default String toStringPretty() throws JSONException {
        return toJSONString(4);
    }

    default String toJSONString(int indentFactor) throws JSONException {
        String obj;
        StringWriter sw = new StringWriter();
        synchronized (sw.getBuffer()) {
            obj = write(sw, indentFactor, 0).toString();
        }
        return obj;
    }

    default Writer write(Writer writer) throws JSONException {
        return write(writer, 0, 0);
    }

    default <T> T toBean(Class<T> cls) {
        return (T) toBean((Type) cls);
    }

    default <T> T toBean(TypeReference<T> typeReference) {
        return (T) toBean(typeReference.getType());
    }

    default <T> T toBean(Type type) {
        return (T) JSONConverter.jsonConvert(type, this, getConfig());
    }

    @Deprecated
    default <T> T toBean(Type type, boolean z) {
        return (T) JSONConverter.jsonConvert(type, this, JSONConfig.create().setIgnoreError(z));
    }
}

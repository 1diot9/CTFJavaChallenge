package cn.hutool.core.util;

import cn.hutool.core.io.FastByteArrayOutputStream;
import cn.hutool.core.io.IoUtil;
import java.io.ByteArrayInputStream;
import java.io.Serializable;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/util/SerializeUtil.class */
public class SerializeUtil {
    public static <T> T clone(T t) {
        if (false == (t instanceof Serializable)) {
            return null;
        }
        return (T) deserialize(serialize(t));
    }

    public static <T> byte[] serialize(T obj) {
        if (false == (obj instanceof Serializable)) {
            return null;
        }
        FastByteArrayOutputStream byteOut = new FastByteArrayOutputStream();
        IoUtil.writeObjects(byteOut, false, (Serializable) obj);
        return byteOut.toByteArray();
    }

    public static <T> T deserialize(byte[] bArr) {
        return (T) IoUtil.readObj(new ByteArrayInputStream(bArr));
    }
}

package org.apache.logging.log4j.util;

import java.lang.reflect.Method;
import org.apache.logging.log4j.LoggingException;

/* loaded from: server.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/util/Base64Util.class */
public final class Base64Util {
    private static Method encodeMethod;
    private static Object encoder;

    static {
        encodeMethod = null;
        encoder = null;
        try {
            Class<?> clazz = LoaderUtil.loadClass("java.util.Base64");
            Class<?> encoderClazz = LoaderUtil.loadClass("java.util.Base64$Encoder");
            Method method = clazz.getMethod("getEncoder", new Class[0]);
            encoder = method.invoke(null, new Object[0]);
            encodeMethod = encoderClazz.getMethod("encodeToString", byte[].class);
        } catch (Exception e) {
            try {
                Class<?> clazz2 = LoaderUtil.loadClass("javax.xml.bind.DataTypeConverter");
                encodeMethod = clazz2.getMethod("printBase64Binary", new Class[0]);
            } catch (Exception ex2) {
                LowLevelLogUtil.logException("Unable to create a Base64 Encoder", ex2);
            }
        }
    }

    private Base64Util() {
    }

    public static String encode(final String str) {
        if (str == null) {
            return null;
        }
        byte[] data = str.getBytes();
        if (encodeMethod != null) {
            try {
                return (String) encodeMethod.invoke(encoder, data);
            } catch (Exception ex) {
                throw new LoggingException("Unable to encode String", ex);
            }
        }
        throw new LoggingException("No Encoder, unable to encode string");
    }
}

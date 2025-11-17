package org.apache.tomcat.util.buf;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/buf/ByteBufferUtils.class */
public class ByteBufferUtils {
    private static final StringManager sm = StringManager.getManager((Class<?>) ByteBufferUtils.class);
    private static final Log log = LogFactory.getLog((Class<?>) ByteBufferUtils.class);
    private static final Object unsafe;
    private static final Method cleanerMethod;
    private static final Method cleanMethod;
    private static final Method invokeCleanerMethod;

    static {
        Object unsafeLocal;
        Method invokeCleanerMethodLocal;
        ByteBuffer tempBuffer = ByteBuffer.allocateDirect(0);
        try {
            Class<?> clazz = Class.forName("sun.misc.Unsafe");
            Field theUnsafe = clazz.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            unsafeLocal = theUnsafe.get(null);
            invokeCleanerMethodLocal = clazz.getMethod("invokeCleaner", ByteBuffer.class);
            invokeCleanerMethodLocal.invoke(unsafeLocal, tempBuffer);
        } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | NoSuchFieldException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
            log.warn(sm.getString("byteBufferUtils.cleaner"), e);
            unsafeLocal = null;
            invokeCleanerMethodLocal = null;
        }
        cleanerMethod = null;
        cleanMethod = null;
        unsafe = unsafeLocal;
        invokeCleanerMethod = invokeCleanerMethodLocal;
    }

    private ByteBufferUtils() {
    }

    public static ByteBuffer expand(ByteBuffer in, int newSize) {
        ByteBuffer out;
        if (in.capacity() >= newSize) {
            return in;
        }
        boolean direct = false;
        if (in.isDirect()) {
            out = ByteBuffer.allocateDirect(newSize);
            direct = true;
        } else {
            out = ByteBuffer.allocate(newSize);
        }
        in.flip();
        out.put(in);
        if (direct) {
            cleanDirectBuffer(in);
        }
        return out;
    }

    public static void cleanDirectBuffer(ByteBuffer buf) {
        if (cleanMethod != null) {
            try {
                cleanMethod.invoke(cleanerMethod.invoke(buf, new Object[0]), new Object[0]);
                return;
            } catch (IllegalAccessException | IllegalArgumentException | SecurityException | InvocationTargetException e) {
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("byteBufferUtils.cleaner"), e);
                    return;
                }
                return;
            }
        }
        if (invokeCleanerMethod != null) {
            try {
                invokeCleanerMethod.invoke(unsafe, buf);
            } catch (IllegalAccessException | IllegalArgumentException | SecurityException | InvocationTargetException e2) {
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("byteBufferUtils.cleaner"), e2);
                }
            }
        }
    }
}

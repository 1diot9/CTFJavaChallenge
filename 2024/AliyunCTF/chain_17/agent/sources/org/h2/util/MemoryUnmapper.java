package org.h2.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import org.h2.engine.SysProperties;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/MemoryUnmapper.class */
public final class MemoryUnmapper {
    private static final boolean ENABLED;
    private static final Object UNSAFE;
    private static final Method INVOKE_CLEANER;

    static {
        boolean z = SysProperties.NIO_CLEANER_HACK;
        Object obj = null;
        Method method = null;
        if (z) {
            try {
                Class<?> cls = Class.forName("sun.misc.Unsafe");
                Field declaredField = cls.getDeclaredField("theUnsafe");
                declaredField.setAccessible(true);
                obj = declaredField.get(null);
                method = cls.getMethod("invokeCleaner", ByteBuffer.class);
            } catch (ReflectiveOperationException e) {
                obj = null;
            } catch (Throwable th) {
                z = false;
                obj = null;
            }
        }
        ENABLED = z;
        UNSAFE = obj;
        INVOKE_CLEANER = method;
    }

    public static boolean unmap(ByteBuffer byteBuffer) {
        if (!ENABLED) {
            return false;
        }
        try {
            if (INVOKE_CLEANER != null) {
                INVOKE_CLEANER.invoke(UNSAFE, byteBuffer);
                return true;
            }
            Method method = byteBuffer.getClass().getMethod("cleaner", new Class[0]);
            method.setAccessible(true);
            Object invoke = method.invoke(byteBuffer, new Object[0]);
            if (invoke != null) {
                invoke.getClass().getMethod("clean", new Class[0]).invoke(invoke, new Object[0]);
                return true;
            }
            return true;
        } catch (Throwable th) {
            return false;
        }
    }

    private MemoryUnmapper() {
    }
}

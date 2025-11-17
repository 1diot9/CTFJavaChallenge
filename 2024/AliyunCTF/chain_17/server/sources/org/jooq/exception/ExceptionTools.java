package org.jooq.exception;

import org.jetbrains.annotations.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/exception/ExceptionTools.class */
public final class ExceptionTools {
    private static int maxCauseLookups = 256;

    @Nullable
    public static <T extends Throwable> T getCause(Throwable th, Class<? extends T> cls) {
        Throwable cause = th.getCause();
        for (int i = 0; i < maxCauseLookups && cause != null; i++) {
            if (cls.isInstance(cause)) {
                return (T) cause;
            }
            Throwable th2 = cause;
            cause = cause.getCause();
            if (th2 == cause) {
                return null;
            }
        }
        return null;
    }

    public static void sneakyThrow(Throwable throwable) {
        sneakyThrow0(throwable);
    }

    private static <E extends Throwable> void sneakyThrow0(Throwable throwable) throws Throwable {
        throw throwable;
    }

    private ExceptionTools() {
    }
}

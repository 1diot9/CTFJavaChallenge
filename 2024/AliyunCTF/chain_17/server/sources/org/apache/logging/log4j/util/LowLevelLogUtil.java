package org.apache.logging.log4j.util;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Objects;

/* loaded from: server.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/util/LowLevelLogUtil.class */
final class LowLevelLogUtil {
    private static PrintWriter writer = new PrintWriter((OutputStream) System.err, true);

    public static void log(final String message) {
        if (message != null) {
            writer.println(message);
        }
    }

    public static void logException(final Throwable exception) {
        if (exception != null) {
            exception.printStackTrace(writer);
        }
    }

    public static void logException(final String message, final Throwable exception) {
        log(message);
        logException(exception);
    }

    public static void setOutputStream(final OutputStream out) {
        writer = new PrintWriter((OutputStream) Objects.requireNonNull(out), true);
    }

    public static void setWriter(final Writer writer2) {
        writer = new PrintWriter((Writer) Objects.requireNonNull(writer2), true);
    }

    private LowLevelLogUtil() {
    }
}

package org.jooq;

import org.jooq.tools.JooqLogger;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/CLIUtil.class */
final class CLIUtil {
    CLIUtil() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void main(String url, Runnable runnable) {
        JooqLogger.initSimpleFormatter();
        try {
            runnable.run();
        } catch (NoClassDefFoundError e) {
            throw new RuntimeException("A class definition could not be found when running the CLI utility.\n\nThis is mostly due to a missing dependency. Make sure you have added the right dependencies\nas according to the manual for {url}\n".replace("{url}", url.replace("/latest/", "/3.19/")), e);
        }
    }
}

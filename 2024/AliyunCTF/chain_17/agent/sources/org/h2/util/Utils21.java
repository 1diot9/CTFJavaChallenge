package org.h2.util;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/Utils21.class */
public final class Utils21 {
    public static Thread newVirtualThread(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.setDaemon(true);
        return thread;
    }

    private Utils21() {
    }
}

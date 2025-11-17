package org.h2.engine;

import java.util.WeakHashMap;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/engine/OnExitDatabaseCloser.class */
public class OnExitDatabaseCloser extends Thread {
    private static final WeakHashMap<Database, Void> DATABASES = new WeakHashMap<>();
    private static final Thread INSTANCE = new OnExitDatabaseCloser();
    private static boolean registered;
    private static boolean terminated;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static synchronized void register(Database database) {
        if (terminated) {
            return;
        }
        DATABASES.put(database, null);
        if (!registered) {
            registered = true;
            try {
                Runtime.getRuntime().addShutdownHook(INSTANCE);
            } catch (IllegalStateException e) {
            } catch (SecurityException e2) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static synchronized void unregister(Database database) {
        if (terminated) {
            return;
        }
        DATABASES.remove(database);
        if (DATABASES.isEmpty() && registered) {
            try {
                Runtime.getRuntime().removeShutdownHook(INSTANCE);
            } catch (IllegalStateException e) {
            } catch (SecurityException e2) {
            }
            registered = false;
        }
    }

    private static void onShutdown() {
        synchronized (OnExitDatabaseCloser.class) {
            terminated = true;
        }
        RuntimeException runtimeException = null;
        for (Database database : DATABASES.keySet()) {
            try {
                database.onShutdown();
            } catch (RuntimeException e) {
                try {
                    database.getTrace(2).error(e, "could not close the database");
                } catch (Throwable th) {
                    e.addSuppressed(th);
                    if (runtimeException == null) {
                        runtimeException = e;
                    } else {
                        runtimeException.addSuppressed(e);
                    }
                }
            }
        }
        if (runtimeException != null) {
            throw runtimeException;
        }
    }

    private OnExitDatabaseCloser() {
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        onShutdown();
    }
}

package org.h2.engine;

import java.lang.ref.WeakReference;
import org.h2.message.Trace;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/engine/DelayedDatabaseCloser.class */
public class DelayedDatabaseCloser extends Thread {
    private final Trace trace;
    private volatile WeakReference<Database> databaseRef;
    private int delayInMillis;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DelayedDatabaseCloser(Database database, int i) {
        this.databaseRef = new WeakReference<>(database);
        this.delayInMillis = i;
        this.trace = database.getTrace(2);
        setName("H2 Close Delay " + database.getShortName());
        setDaemon(true);
        start();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void reset() {
        this.databaseRef = null;
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        Database database;
        while (this.delayInMillis > 0) {
            try {
                Thread.sleep(100);
                this.delayInMillis -= 100;
            } catch (Exception e) {
            }
            WeakReference<Database> weakReference = this.databaseRef;
            if (weakReference == null || weakReference.get() == null) {
                return;
            }
        }
        WeakReference<Database> weakReference2 = this.databaseRef;
        if (weakReference2 != null && (database = weakReference2.get()) != null) {
            try {
                database.close();
            } catch (RuntimeException e2) {
                try {
                    this.trace.error(e2, "could not close the database");
                } catch (Throwable th) {
                    e2.addSuppressed(th);
                    throw e2;
                }
            }
        }
    }
}

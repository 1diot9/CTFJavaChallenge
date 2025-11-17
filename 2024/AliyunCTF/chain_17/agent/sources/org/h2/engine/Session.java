package org.h2.engine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.locks.ReentrantLock;
import org.h2.command.CommandInterface;
import org.h2.jdbc.meta.DatabaseMeta;
import org.h2.message.Trace;
import org.h2.result.ResultInterface;
import org.h2.store.DataHandler;
import org.h2.util.NetworkConnectionInfo;
import org.h2.util.TimeZoneProvider;
import org.h2.util.Utils;
import org.h2.value.ValueLob;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/engine/Session.class */
public abstract class Session implements CastDataProvider, AutoCloseable {
    private final ReentrantLock lock = new ReentrantLock();
    private ArrayList<String> sessionState;
    boolean sessionStateChanged;
    private boolean sessionStateUpdating;
    volatile StaticSettings staticSettings;

    public abstract ArrayList<String> getClusterServers();

    public abstract CommandInterface prepareCommand(String str, int i);

    @Override // java.lang.AutoCloseable
    public abstract void close();

    public abstract Trace getTrace();

    public abstract boolean isClosed();

    public abstract DataHandler getDataHandler();

    public abstract boolean hasPendingTransaction();

    public abstract void cancel();

    public abstract boolean getAutoCommit();

    public abstract void setAutoCommit(boolean z);

    public abstract ValueLob addTemporaryLob(ValueLob valueLob);

    public abstract boolean isRemote();

    public abstract void setCurrentSchemaName(String str);

    public abstract String getCurrentSchemaName();

    public abstract void setNetworkConnectionInfo(NetworkConnectionInfo networkConnectionInfo);

    public abstract IsolationLevel getIsolationLevel();

    public abstract void setIsolationLevel(IsolationLevel isolationLevel);

    public abstract StaticSettings getStaticSettings();

    public abstract DynamicSettings getDynamicSettings();

    public abstract DatabaseMeta getDatabaseMeta();

    public abstract boolean isOldInformationSchema();

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/engine/Session$StaticSettings.class */
    public static final class StaticSettings {
        public final boolean databaseToUpper;
        public final boolean databaseToLower;
        public final boolean caseInsensitiveIdentifiers;

        public StaticSettings(boolean z, boolean z2, boolean z3) {
            this.databaseToUpper = z;
            this.databaseToLower = z2;
            this.caseInsensitiveIdentifiers = z3;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/engine/Session$DynamicSettings.class */
    public static final class DynamicSettings {
        public final Mode mode;
        public final TimeZoneProvider timeZone;

        public DynamicSettings(Mode mode, TimeZoneProvider timeZoneProvider) {
            this.mode = mode;
            this.timeZone = timeZoneProvider;
        }
    }

    public final void lock() {
        this.lock.lock();
    }

    public final void unlock() {
        this.lock.unlock();
    }

    public final boolean isLockedByCurrentThread() {
        return this.lock.isHeldByCurrentThread();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void recreateSessionState() {
        if (this.sessionState != null && !this.sessionState.isEmpty()) {
            this.sessionStateUpdating = true;
            try {
                Iterator<String> it = this.sessionState.iterator();
                while (it.hasNext()) {
                    prepareCommand(it.next(), Integer.MAX_VALUE).executeUpdate(null);
                }
            } finally {
                this.sessionStateUpdating = false;
                this.sessionStateChanged = false;
            }
        }
    }

    public void readSessionState() {
        if (!this.sessionStateChanged || this.sessionStateUpdating) {
            return;
        }
        this.sessionStateChanged = false;
        this.sessionState = Utils.newSmallArrayList();
        ResultInterface executeQuery = prepareCommand(!isOldInformationSchema() ? "SELECT STATE_COMMAND FROM INFORMATION_SCHEMA.SESSION_STATE" : "SELECT SQL FROM INFORMATION_SCHEMA.SESSION_STATE", Integer.MAX_VALUE).executeQuery(0L, false);
        while (executeQuery.next()) {
            this.sessionState.add(executeQuery.currentRow()[0].getString());
        }
    }

    public Session setThreadLocalSession() {
        return null;
    }

    public void resetThreadLocalSession(Session session) {
    }
}

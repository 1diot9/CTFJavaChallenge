package org.h2.api;

import java.sql.SQLException;
import java.util.EventListener;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/api/DatabaseEventListener.class */
public interface DatabaseEventListener extends EventListener {
    public static final int STATE_SCAN_FILE = 0;
    public static final int STATE_CREATE_INDEX = 1;
    public static final int STATE_RECOVER = 2;
    public static final int STATE_BACKUP_FILE = 3;
    public static final int STATE_RECONNECTED = 4;
    public static final int STATE_STATEMENT_START = 5;
    public static final int STATE_STATEMENT_END = 6;
    public static final int STATE_STATEMENT_PROGRESS = 7;

    default void init(String str) {
    }

    default void opened() {
    }

    default void exceptionThrown(SQLException sQLException, String str) {
    }

    default void setProgress(int i, String str, long j, long j2) {
    }

    default void closingDatabase() {
    }
}

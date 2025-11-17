package org.h2.api;

import java.sql.Connection;
import java.sql.SQLException;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/api/Trigger.class */
public interface Trigger {
    public static final int INSERT = 1;
    public static final int UPDATE = 2;
    public static final int DELETE = 4;
    public static final int SELECT = 8;

    void fire(Connection connection, Object[] objArr, Object[] objArr2) throws SQLException;

    default void init(Connection connection, String str, String str2, String str3, boolean z, int i) throws SQLException {
    }

    default void close() throws SQLException {
    }

    default void remove() throws SQLException {
    }
}

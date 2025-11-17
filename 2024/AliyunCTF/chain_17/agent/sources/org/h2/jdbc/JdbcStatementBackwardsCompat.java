package org.h2.jdbc;

import java.sql.SQLException;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/jdbc/JdbcStatementBackwardsCompat.class */
public interface JdbcStatementBackwardsCompat {
    String enquoteIdentifier(String str, boolean z) throws SQLException;

    boolean isSimpleIdentifier(String str) throws SQLException;
}

package org.h2.jdbc;

import java.sql.SQLException;
import java.sql.Savepoint;
import org.h2.api.ErrorCode;
import org.h2.message.DbException;
import org.h2.message.Trace;
import org.h2.message.TraceObject;
import org.h2.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/jdbc/JdbcSavepoint.class */
public final class JdbcSavepoint extends TraceObject implements Savepoint {
    private static final String SYSTEM_SAVEPOINT_PREFIX = "SYSTEM_SAVEPOINT_";
    private final int savepointId;
    private final String name;
    private JdbcConnection conn;

    /* JADX INFO: Access modifiers changed from: package-private */
    public JdbcSavepoint(JdbcConnection jdbcConnection, int i, String str, Trace trace, int i2) {
        setTrace(trace, 6, i2);
        this.conn = jdbcConnection;
        this.savepointId = i;
        this.name = str;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void release() {
        this.conn = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getName(String str, int i) {
        if (str != null) {
            return StringUtils.quoteJavaString(str);
        }
        return SYSTEM_SAVEPOINT_PREFIX + i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void rollback() {
        checkValid();
        this.conn.prepareCommand("ROLLBACK TO SAVEPOINT " + getName(this.name, this.savepointId), Integer.MAX_VALUE).executeUpdate(null);
    }

    private void checkValid() {
        if (this.conn == null) {
            throw DbException.get(ErrorCode.SAVEPOINT_IS_INVALID_1, getName(this.name, this.savepointId));
        }
    }

    @Override // java.sql.Savepoint
    public int getSavepointId() throws SQLException {
        try {
            debugCodeCall("getSavepointId");
            checkValid();
            if (this.name != null) {
                throw DbException.get(ErrorCode.SAVEPOINT_IS_NAMED);
            }
            return this.savepointId;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Savepoint
    public String getSavepointName() throws SQLException {
        try {
            debugCodeCall("getSavepointName");
            checkValid();
            if (this.name == null) {
                throw DbException.get(ErrorCode.SAVEPOINT_IS_UNNAMED);
            }
            return this.name;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    public String toString() {
        return getTraceObjectName() + ": id=" + this.savepointId + " name=" + this.name;
    }
}

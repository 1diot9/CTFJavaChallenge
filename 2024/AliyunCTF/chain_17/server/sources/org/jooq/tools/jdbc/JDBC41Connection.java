package org.jooq.tools.jdbc;

import java.sql.SQLException;
import java.util.concurrent.Executor;

@Deprecated
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/tools/jdbc/JDBC41Connection.class */
public abstract class JDBC41Connection {
    public void setSchema(String s) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getSchema() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void abort(Executor executor) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getNetworkTimeout() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

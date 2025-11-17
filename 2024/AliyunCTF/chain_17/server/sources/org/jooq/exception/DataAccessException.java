package org.jooq.exception;

import io.r2dbc.spi.R2dbcException;
import java.sql.SQLException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.tools.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/exception/DataAccessException.class */
public class DataAccessException extends RuntimeException {
    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    @NotNull
    public String sqlState() {
        SQLException s = (SQLException) getCause(SQLException.class);
        if (s != null) {
            return (String) StringUtils.defaultIfNull(s.getSQLState(), "00000");
        }
        R2dbcException r = (R2dbcException) getCause(R2dbcException.class);
        if (r != null) {
            return (String) StringUtils.defaultIfNull(r.getSqlState(), "00000");
        }
        return "00000";
    }

    @NotNull
    public SQLStateClass sqlStateClass() {
        SQLException s = (SQLException) getCause(SQLException.class);
        if (s != null) {
            return sqlStateClass(s);
        }
        R2dbcException r = (R2dbcException) getCause(R2dbcException.class);
        if (r != null) {
            return sqlStateClass(r);
        }
        return SQLStateClass.NONE;
    }

    @NotNull
    public static SQLStateClass sqlStateClass(SQLException e) {
        if (e.getSQLState() != null) {
            return SQLStateClass.fromCode(e.getSQLState());
        }
        if (e.getSQLState() == null && causePrefix(e, "org.sqlite")) {
            return SQLStateClass.fromSQLiteVendorCode(e.getErrorCode());
        }
        if (e.getSQLState() == null && causePrefix(e, "io.trino")) {
            return SQLStateClass.fromTrinoVendorCode(e.getErrorCode());
        }
        return SQLStateClass.NONE;
    }

    private static final boolean causePrefix(SQLException e, String prefix) {
        return e.getClass().getName().startsWith(prefix) || (e.getCause() != null && e.getCause().getClass().getName().startsWith(prefix));
    }

    @NotNull
    public static SQLStateClass sqlStateClass(R2dbcException e) {
        if (e.getSqlState() != null) {
            return SQLStateClass.fromCode(e.getSqlState());
        }
        return SQLStateClass.NONE;
    }

    @NotNull
    public SQLStateSubclass sqlStateSubclass() {
        return SQLStateSubclass.fromCode(sqlState());
    }

    @Override // java.lang.Throwable
    public StackTraceElement[] getStackTrace() {
        return super.getStackTrace();
    }

    @Nullable
    public <T extends Throwable> T getCause(Class<? extends T> cls) {
        return (T) ExceptionTools.getCause(this, cls);
    }
}

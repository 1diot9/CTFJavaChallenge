package io.r2dbc.spi;

/* loaded from: server.jar:BOOT-INF/lib/r2dbc-spi-1.0.0.RELEASE.jar:io/r2dbc/spi/R2dbcException.class */
public abstract class R2dbcException extends RuntimeException {
    private final int errorCode;

    @Nullable
    private final String sqlState;

    @Nullable
    private final String sql;

    public R2dbcException() {
        this((String) null);
    }

    public R2dbcException(@Nullable String reason) {
        this(reason, (String) null);
    }

    public R2dbcException(@Nullable String reason, @Nullable String sqlState) {
        this(reason, sqlState, 0);
    }

    public R2dbcException(@Nullable String reason, @Nullable String sqlState, int errorCode) {
        this(reason, sqlState, errorCode, null, null);
    }

    public R2dbcException(@Nullable String reason, @Nullable String sqlState, int errorCode, @Nullable String sql) {
        this(reason, sqlState, errorCode, sql, null);
    }

    public R2dbcException(@Nullable String reason, @Nullable String sqlState, int errorCode, @Nullable String sql, @Nullable Throwable cause) {
        super(reason, cause);
        this.sqlState = sqlState;
        this.errorCode = errorCode;
        this.sql = sql;
    }

    public R2dbcException(@Nullable String reason, @Nullable String sqlState, int errorCode, @Nullable Throwable cause) {
        this(reason, sqlState, errorCode, null, cause);
    }

    public R2dbcException(@Nullable String reason, @Nullable String sqlState, @Nullable Throwable cause) {
        this(reason, sqlState, 0, cause);
    }

    public R2dbcException(@Nullable String reason, @Nullable Throwable cause) {
        this(reason, (String) null, cause);
    }

    public R2dbcException(@Nullable Throwable cause) {
        this((String) null, cause);
    }

    public final int getErrorCode() {
        return this.errorCode;
    }

    @Nullable
    public final String getSqlState() {
        return this.sqlState;
    }

    @Nullable
    public final String getSql() {
        return this.sql;
    }

    @Override // java.lang.Throwable
    public String toString() {
        StringBuilder builder = new StringBuilder(32);
        builder.append(getClass().getName());
        if (getErrorCode() != 0 || ((getSqlState() != null && !getSqlState().isEmpty()) || getMessage() != null)) {
            builder.append(":");
        }
        if (getErrorCode() != 0) {
            builder.append(" [").append(getErrorCode()).append("]");
        }
        if (getSqlState() != null && !getSqlState().isEmpty()) {
            builder.append(" [").append(getSqlState()).append("]");
        }
        if (getMessage() != null) {
            builder.append(" ").append(getMessage());
        }
        return builder.toString();
    }
}

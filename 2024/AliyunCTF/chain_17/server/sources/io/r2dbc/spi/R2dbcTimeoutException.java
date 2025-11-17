package io.r2dbc.spi;

/* loaded from: server.jar:BOOT-INF/lib/r2dbc-spi-1.0.0.RELEASE.jar:io/r2dbc/spi/R2dbcTimeoutException.class */
public class R2dbcTimeoutException extends R2dbcTransientException {
    public R2dbcTimeoutException() {
    }

    public R2dbcTimeoutException(@Nullable String reason) {
        super(reason);
    }

    public R2dbcTimeoutException(@Nullable String reason, @Nullable String sqlState) {
        super(reason, sqlState);
    }

    public R2dbcTimeoutException(@Nullable String reason, @Nullable String sqlState, int errorCode) {
        super(reason, sqlState, errorCode);
    }

    public R2dbcTimeoutException(@Nullable String reason, @Nullable String sqlState, int errorCode, @Nullable String sql) {
        super(reason, sqlState, errorCode, sql);
    }

    public R2dbcTimeoutException(@Nullable String reason, @Nullable String sqlState, int errorCode, @Nullable String sql, @Nullable Throwable cause) {
        super(reason, sqlState, errorCode, sql, cause);
    }

    public R2dbcTimeoutException(@Nullable String reason, @Nullable String sqlState, int errorCode, @Nullable Throwable cause) {
        super(reason, sqlState, errorCode, cause);
    }

    public R2dbcTimeoutException(@Nullable String reason, @Nullable String sqlState, @Nullable Throwable cause) {
        super(reason, sqlState, cause);
    }

    public R2dbcTimeoutException(@Nullable String reason, @Nullable Throwable cause) {
        super(reason, cause);
    }

    public R2dbcTimeoutException(@Nullable Throwable cause) {
        super(cause);
    }
}

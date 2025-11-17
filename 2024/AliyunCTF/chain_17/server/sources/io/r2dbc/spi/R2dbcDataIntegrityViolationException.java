package io.r2dbc.spi;

/* loaded from: server.jar:BOOT-INF/lib/r2dbc-spi-1.0.0.RELEASE.jar:io/r2dbc/spi/R2dbcDataIntegrityViolationException.class */
public class R2dbcDataIntegrityViolationException extends R2dbcNonTransientException {
    public R2dbcDataIntegrityViolationException() {
    }

    public R2dbcDataIntegrityViolationException(@Nullable String reason) {
        super(reason);
    }

    public R2dbcDataIntegrityViolationException(@Nullable String reason, @Nullable String sqlState) {
        super(reason, sqlState);
    }

    public R2dbcDataIntegrityViolationException(@Nullable String reason, @Nullable String sqlState, int errorCode) {
        super(reason, sqlState, errorCode);
    }

    public R2dbcDataIntegrityViolationException(@Nullable String reason, @Nullable String sqlState, int errorCode, @Nullable String sql) {
        super(reason, sqlState, errorCode, sql);
    }

    public R2dbcDataIntegrityViolationException(@Nullable String reason, @Nullable String sqlState, int errorCode, @Nullable String sql, @Nullable Throwable cause) {
        super(reason, sqlState, errorCode, sql, cause);
    }

    public R2dbcDataIntegrityViolationException(@Nullable String reason, @Nullable String sqlState, int errorCode, @Nullable Throwable cause) {
        super(reason, sqlState, errorCode, cause);
    }

    public R2dbcDataIntegrityViolationException(@Nullable String reason, @Nullable String sqlState, @Nullable Throwable cause) {
        super(reason, sqlState, cause);
    }

    public R2dbcDataIntegrityViolationException(@Nullable String reason, @Nullable Throwable cause) {
        super(reason, cause);
    }

    public R2dbcDataIntegrityViolationException(@Nullable Throwable cause) {
        super(cause);
    }
}

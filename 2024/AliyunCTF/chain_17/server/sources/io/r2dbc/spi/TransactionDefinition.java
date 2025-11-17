package io.r2dbc.spi;

import java.time.Duration;

/* loaded from: server.jar:BOOT-INF/lib/r2dbc-spi-1.0.0.RELEASE.jar:io/r2dbc/spi/TransactionDefinition.class */
public interface TransactionDefinition {
    public static final Option<IsolationLevel> ISOLATION_LEVEL = Option.valueOf("isolationLevel");
    public static final Option<Boolean> READ_ONLY = Option.valueOf("readOnly");
    public static final Option<String> NAME = Option.valueOf("name");
    public static final Option<Duration> LOCK_WAIT_TIMEOUT = Option.valueOf("lockWaitTimeout");

    @Nullable
    <T> T getAttribute(Option<T> option);
}

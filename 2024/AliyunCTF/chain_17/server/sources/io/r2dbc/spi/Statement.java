package io.r2dbc.spi;

import org.reactivestreams.Publisher;

/* loaded from: server.jar:BOOT-INF/lib/r2dbc-spi-1.0.0.RELEASE.jar:io/r2dbc/spi/Statement.class */
public interface Statement {
    Statement add();

    Statement bind(int i, Object obj);

    Statement bind(String str, Object obj);

    Statement bindNull(int i, Class<?> cls);

    Statement bindNull(String str, Class<?> cls);

    Publisher<? extends Result> execute();

    default Statement returnGeneratedValues(String... columns) {
        Assert.requireNonNull(columns, "columns must not be null");
        return this;
    }

    default Statement fetchSize(int rows) {
        return this;
    }
}

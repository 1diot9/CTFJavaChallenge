package org.jooq.tools.r2dbc;

import io.r2dbc.spi.Result;
import io.r2dbc.spi.Statement;
import org.reactivestreams.Publisher;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/tools/r2dbc/DefaultStatement.class */
public class DefaultStatement implements Statement {
    private final Statement delegate;

    public DefaultStatement(Statement delegate) {
        this.delegate = delegate;
    }

    public Statement getDelegate() {
        return this.delegate;
    }

    @Override // io.r2dbc.spi.Statement
    public Statement add() {
        return getDelegate().add();
    }

    @Override // io.r2dbc.spi.Statement
    public Statement bind(int index, Object value) {
        return getDelegate().bind(index, value);
    }

    @Override // io.r2dbc.spi.Statement
    public Statement bind(String name, Object value) {
        return getDelegate().bind(name, value);
    }

    @Override // io.r2dbc.spi.Statement
    public Statement bindNull(int index, Class<?> type) {
        return getDelegate().bindNull(index, type);
    }

    @Override // io.r2dbc.spi.Statement
    public Statement bindNull(String name, Class<?> type) {
        return getDelegate().bindNull(name, type);
    }

    @Override // io.r2dbc.spi.Statement
    public Publisher<? extends Result> execute() {
        return getDelegate().execute();
    }

    @Override // io.r2dbc.spi.Statement
    public Statement returnGeneratedValues(String... columns) {
        return getDelegate().returnGeneratedValues(columns);
    }

    @Override // io.r2dbc.spi.Statement
    public Statement fetchSize(int rows) {
        return getDelegate().fetchSize(rows);
    }
}

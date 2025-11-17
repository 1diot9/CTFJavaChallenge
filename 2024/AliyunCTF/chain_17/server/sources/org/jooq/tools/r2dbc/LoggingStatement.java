package org.jooq.tools.r2dbc;

import io.r2dbc.spi.Result;
import io.r2dbc.spi.Statement;
import org.jooq.tools.JooqLogger;
import org.reactivestreams.Publisher;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/tools/r2dbc/LoggingStatement.class */
public class LoggingStatement extends DefaultStatement {
    private static final JooqLogger log = JooqLogger.getLogger((Class<?>) LoggingStatement.class);

    public LoggingStatement(Statement delegate) {
        super(delegate);
    }

    @Override // org.jooq.tools.r2dbc.DefaultStatement, io.r2dbc.spi.Statement
    public Statement add() {
        if (log.isDebugEnabled()) {
            log.debug("Statement::add");
        }
        getDelegate().add();
        return this;
    }

    @Override // org.jooq.tools.r2dbc.DefaultStatement, io.r2dbc.spi.Statement
    public Statement bind(int index, Object value) {
        if (log.isTraceEnabled()) {
            log.trace("Statement::bind", "index = " + index + ", value = " + String.valueOf(value));
        }
        getDelegate().bind(index, value);
        return this;
    }

    @Override // org.jooq.tools.r2dbc.DefaultStatement, io.r2dbc.spi.Statement
    public Statement bind(String name, Object value) {
        if (log.isTraceEnabled()) {
            log.trace("Statement::bind", "name = " + name + ", value = " + String.valueOf(value));
        }
        getDelegate().bind(name, value);
        return this;
    }

    @Override // org.jooq.tools.r2dbc.DefaultStatement, io.r2dbc.spi.Statement
    public Statement bindNull(int index, Class<?> type) {
        if (log.isTraceEnabled()) {
            log.trace("Statement::bindNull", "index = " + index + ", type = " + String.valueOf(type));
        }
        getDelegate().bindNull(index, type);
        return this;
    }

    @Override // org.jooq.tools.r2dbc.DefaultStatement, io.r2dbc.spi.Statement
    public Statement bindNull(String name, Class<?> type) {
        if (log.isTraceEnabled()) {
            log.trace("Statement::bindNull", "name = " + name + ", type = " + String.valueOf(type));
        }
        getDelegate().bindNull(name, type);
        return this;
    }

    @Override // org.jooq.tools.r2dbc.DefaultStatement, io.r2dbc.spi.Statement
    public Publisher<? extends Result> execute() {
        return s -> {
            if (log.isDebugEnabled()) {
                log.debug("Statement::execute");
            }
            getDelegate().execute().subscribe(s);
        };
    }
}

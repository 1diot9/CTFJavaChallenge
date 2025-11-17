package org.jooq.tools.r2dbc;

import io.r2dbc.spi.Batch;
import io.r2dbc.spi.Result;
import org.jooq.tools.JooqLogger;
import org.reactivestreams.Publisher;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/tools/r2dbc/LoggingBatch.class */
public class LoggingBatch implements Batch {
    private static final JooqLogger log = JooqLogger.getLogger((Class<?>) LoggingBatch.class);
    private final Batch delegate;

    public LoggingBatch(Batch delegate) {
        this.delegate = delegate;
    }

    public Batch getDelegate() {
        return this.delegate;
    }

    @Override // io.r2dbc.spi.Batch
    public Batch add(String sql) {
        if (log.isDebugEnabled()) {
            log.debug("Batch::add", sql);
        }
        getDelegate().add(sql);
        return this;
    }

    @Override // io.r2dbc.spi.Batch
    public Publisher<? extends Result> execute() {
        return s -> {
            if (log.isDebugEnabled()) {
                log.debug("Batch::execute");
            }
            getDelegate().execute().subscribe(s);
        };
    }
}

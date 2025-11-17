package org.jooq.tools;

import org.jooq.ExecuteContext;
import org.jooq.ExecuteListener;
import org.springframework.http.HttpHeaders;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/tools/StopWatchListener.class */
public class StopWatchListener implements ExecuteListener {
    private final StopWatch watch = new StopWatch();

    @Override // org.jooq.ExecuteListener
    public void start(ExecuteContext ctx) {
        this.watch.splitTrace("Initialising");
    }

    @Override // org.jooq.ExecuteListener
    public void renderStart(ExecuteContext ctx) {
        this.watch.splitTrace("Rendering query");
    }

    @Override // org.jooq.ExecuteListener
    public void renderEnd(ExecuteContext ctx) {
        this.watch.splitTrace("Query rendered");
    }

    @Override // org.jooq.ExecuteListener
    public void prepareStart(ExecuteContext ctx) {
        this.watch.splitTrace("Preparing statement");
    }

    @Override // org.jooq.ExecuteListener
    public void prepareEnd(ExecuteContext ctx) {
        this.watch.splitTrace("Statement prepared");
    }

    @Override // org.jooq.ExecuteListener
    public void bindStart(ExecuteContext ctx) {
        this.watch.splitTrace("Binding variables");
    }

    @Override // org.jooq.ExecuteListener
    public void bindEnd(ExecuteContext ctx) {
        this.watch.splitTrace("Variables bound");
    }

    @Override // org.jooq.ExecuteListener
    public void executeStart(ExecuteContext ctx) {
        this.watch.splitTrace("Executing query");
    }

    @Override // org.jooq.ExecuteListener
    public void executeEnd(ExecuteContext ctx) {
        this.watch.splitDebug("Query executed");
    }

    @Override // org.jooq.ExecuteListener
    public void outStart(ExecuteContext ctx) {
        this.watch.splitDebug("Fetching out values");
    }

    @Override // org.jooq.ExecuteListener
    public void outEnd(ExecuteContext ctx) {
        this.watch.splitDebug("Out values fetched");
    }

    @Override // org.jooq.ExecuteListener
    public void fetchStart(ExecuteContext ctx) {
        this.watch.splitTrace("Fetching results");
    }

    @Override // org.jooq.ExecuteListener
    public void resultStart(ExecuteContext ctx) {
        this.watch.splitTrace("Fetching result");
    }

    @Override // org.jooq.ExecuteListener
    public void recordStart(ExecuteContext ctx) {
        this.watch.splitTrace("Fetching record");
    }

    @Override // org.jooq.ExecuteListener
    public void recordEnd(ExecuteContext ctx) {
        this.watch.splitTrace("Record fetched");
    }

    @Override // org.jooq.ExecuteListener
    public void resultEnd(ExecuteContext ctx) {
        this.watch.splitTrace("Result fetched");
    }

    @Override // org.jooq.ExecuteListener
    public void fetchEnd(ExecuteContext ctx) {
        this.watch.splitTrace("Results fetched");
    }

    @Override // org.jooq.ExecuteListener
    public void end(ExecuteContext ctx) {
        this.watch.splitDebug("Finishing");
    }

    @Override // org.jooq.ExecuteListener
    public void exception(ExecuteContext ctx) {
        this.watch.splitDebug("Exception");
    }

    @Override // org.jooq.ExecuteListener
    public void warning(ExecuteContext ctx) {
        this.watch.splitDebug(HttpHeaders.WARNING);
    }
}

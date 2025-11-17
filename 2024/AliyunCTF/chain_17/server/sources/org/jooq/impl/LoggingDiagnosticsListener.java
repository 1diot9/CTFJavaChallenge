package org.jooq.impl;

import java.util.stream.Collectors;
import org.jooq.DiagnosticsContext;
import org.jooq.DiagnosticsListener;
import org.jooq.tools.JooqLogger;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/LoggingDiagnosticsListener.class */
public class LoggingDiagnosticsListener implements DiagnosticsListener {
    private static final JooqLogger log = JooqLogger.getLogger((Class<?>) LoggingDiagnosticsListener.class);

    private void log(String text, DiagnosticsContext ctx) {
        log(text, ctx, null);
    }

    private void log(String text, DiagnosticsContext ctx, String additionalContext) {
        String text2;
        if (log.isInfoEnabled()) {
            if (additionalContext != null) {
                text = text + "\n" + additionalContext;
            }
            if (ctx.actualStatement().equals(ctx.normalisedStatement())) {
                text2 = text + "\nStatement: " + ctx.actualStatement();
            } else {
                text2 = text + "\nActual statement    : " + ctx.actualStatement() + "\nNormalised statement: " + ctx.normalisedStatement();
            }
            log.info("Diagnostics", text2);
        }
    }

    @Override // org.jooq.DiagnosticsListener
    public void duplicateStatements(DiagnosticsContext ctx) {
        log("Duplicate statements were encountered. Why is it bad? See: https://www.jooq.org/doc/latest/manual/sql-execution/diagnostics/diagnostics-duplicate-statements/\n", ctx, "Recent statements include: " + String.valueOf(ctx.duplicateStatements().stream().limit(5L).map(s -> {
            return "\n  " + s;
        }).collect(Collectors.toList())));
    }

    @Override // org.jooq.DiagnosticsListener
    public void repeatedStatements(DiagnosticsContext ctx) {
        log("Repeated statements were encountered. Why is it bad? See: https://www.jooq.org/doc/latest/manual/sql-execution/diagnostics/diagnostics-repeated-statements/\n", ctx, "Recent statements include: " + String.valueOf(ctx.repeatedStatements().stream().limit(5L).map(s -> {
            return "\n  " + s;
        }).collect(Collectors.toList())));
    }

    @Override // org.jooq.DiagnosticsListener
    public void tooManyColumnsFetched(DiagnosticsContext ctx) {
        log("Too many columns were fetched and never read. Why is it bad? See: https://www.jooq.org/doc/latest/manual/sql-execution/diagnostics/diagnostics-too-many-columns/\n", ctx, "Fetched columns : " + String.valueOf(ctx.resultSetFetchedColumnNames()) + "\nConsumed columns: " + String.valueOf(ctx.resultSetConsumedColumnNames()));
    }

    @Override // org.jooq.DiagnosticsListener
    public void tooManyRowsFetched(DiagnosticsContext ctx) {
        log("Too many rows were fetched and never read. Why is it bad? See: https://www.jooq.org/doc/latest/manual/sql-execution/diagnostics/diagnostics-too-many-rows/\n", ctx, "Fetched rows : " + ctx.resultSetFetchedRows() + "\nConsumed rows: " + ctx.resultSetConsumedRows());
    }
}

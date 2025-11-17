package org.jooq.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import org.jooq.Configuration;
import org.jooq.ExecuteContext;
import org.jooq.ExecuteListener;
import org.jooq.ExecuteType;
import org.jooq.Field;
import org.jooq.Param;
import org.jooq.Parameter;
import org.jooq.QueryPart;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.Routine;
import org.jooq.VisitContext;
import org.jooq.VisitListener;
import org.jooq.impl.DSL;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/tools/LoggerListener.class */
public class LoggerListener implements ExecuteListener {
    private static final JooqLogger log = JooqLogger.getLogger((Class<?>) LoggerListener.class);
    private static final String BUFFER = "org.jooq.tools.LoggerListener.BUFFER";
    private static final String DO_BUFFER = "org.jooq.tools.LoggerListener.DO_BUFFER";
    private static final String BATCH_SIZE = "org.jooq.tools.LoggerListener.BATCH_SIZE";
    private static final int maxLength = 2000;

    @Override // org.jooq.ExecuteListener
    public void renderEnd(ExecuteContext ctx) {
        if (log.isDebugEnabled()) {
            Configuration configuration = ctx.configuration();
            String newline = Boolean.TRUE.equals(configuration.settings().isRenderFormatted()) ? "\n" : "";
            if (!log.isTraceEnabled()) {
                configuration = configuration.deriveAppending(new BindValueAbbreviator());
            }
            ctx.batchSQL();
            if (ctx.query() != null) {
                log.debug("Executing query", newline + ctx.sql());
                String inlined = DSL.using(configuration).renderInlined(ctx.query());
                if (!ctx.sql().equals(inlined)) {
                    log.debug("-> with bind values", newline + inlined);
                    return;
                }
                return;
            }
            if (ctx.routine() != null) {
                log.debug("Calling routine", newline + ctx.sql());
                String inlined2 = DSL.using(configuration).renderInlined(ctx.routine());
                if (!ctx.sql().equals(inlined2)) {
                    log.debug("-> with bind values", newline + inlined2);
                    return;
                }
                return;
            }
            if (!StringUtils.isBlank(ctx.sql())) {
                if (ctx.type() == ExecuteType.BATCH) {
                    log.debug("Executing batch query", newline + ctx.sql());
                } else {
                    log.debug("Executing query", newline + ctx.sql());
                }
            }
        }
    }

    @Override // org.jooq.ExecuteListener
    public void bindEnd(ExecuteContext ctx) {
        if (ctx.type() == ExecuteType.BATCH && log.isDebugEnabled()) {
            ctx.data().compute(BATCH_SIZE, (k, v) -> {
                return Integer.valueOf(v == null ? 1 : ((Integer) v).intValue() + 1);
            });
        }
    }

    @Override // org.jooq.ExecuteListener
    public void executeStart(ExecuteContext ctx) {
        if (ctx.type() == ExecuteType.BATCH && log.isDebugEnabled()) {
            log.debug("Batch size", ctx.data().getOrDefault(BATCH_SIZE, Integer.valueOf(ctx.batchSQL().length)));
        }
    }

    @Override // org.jooq.ExecuteListener
    public void recordEnd(ExecuteContext ctx) {
        if (ctx.recordLevel() > 0) {
            return;
        }
        if (log.isTraceEnabled() && ctx.record() != null) {
            logMultiline("Record fetched", ctx.record().toString(), Level.FINER);
        }
        if (log.isDebugEnabled() && ctx.record() != null && !Boolean.FALSE.equals(ctx.data(DO_BUFFER))) {
            Result<Record> buffer = (Result) ctx.data(BUFFER);
            if (buffer == null) {
                Result<Record> newResult = ctx.dsl().newResult(ctx.record().fields());
                buffer = newResult;
                ctx.data(BUFFER, newResult);
            }
            if (buffer.size() < maxRows()) {
                buffer.add(ctx.record());
            }
        }
    }

    @Override // org.jooq.ExecuteListener
    public void resultStart(ExecuteContext ctx) {
        ctx.data(DO_BUFFER, false);
    }

    @Override // org.jooq.ExecuteListener
    public void resultEnd(ExecuteContext ctx) {
        if (ctx.resultLevel() <= 0 && ctx.result() != null && log.isDebugEnabled()) {
            log(ctx.configuration(), ctx.result());
            log.debug("Fetched row(s)", Integer.valueOf(ctx.result().size()));
        }
    }

    @Override // org.jooq.ExecuteListener
    public void fetchEnd(ExecuteContext ctx) {
        Result<Record> buffer = (Result) ctx.data(BUFFER);
        if (buffer != null && !buffer.isEmpty() && log.isDebugEnabled()) {
            log(ctx.configuration(), buffer);
            log.debug("Fetched row(s)", buffer.size() + (buffer.size() < maxRows() ? "" : " (or more)"));
        }
    }

    private void log(Configuration configuration, Result<?> result) {
        logMultiline("Fetched result", result.format(configuration.formattingProvider().txtFormat().maxRows(maxRows()).maxColWidth(maxColWidth())), Level.FINE);
    }

    private int maxRows() {
        if (log.isTraceEnabled()) {
            return 500;
        }
        if (log.isDebugEnabled()) {
            return 5;
        }
        return 0;
    }

    private int maxColWidth() {
        if (log.isTraceEnabled()) {
            return 500;
        }
        if (log.isDebugEnabled()) {
            return 50;
        }
        return 0;
    }

    @Override // org.jooq.ExecuteListener
    public void executeEnd(ExecuteContext ctx) {
        if (ctx.rows() >= 0 && log.isDebugEnabled()) {
            log.debug("Affected row(s)", Integer.valueOf(ctx.rows()));
        }
    }

    @Override // org.jooq.ExecuteListener
    public void outEnd(ExecuteContext ctx) {
        if (ctx.routine() != null && log.isDebugEnabled()) {
            logMultiline("Fetched OUT parameters", String.valueOf(StringUtils.defaultIfNull(record(ctx.configuration(), ctx.routine()), "N/A")), Level.FINE);
        }
    }

    @Override // org.jooq.ExecuteListener
    public void exception(ExecuteContext ctx) {
        if (log.isDebugEnabled() && ctx.configuration().data("org.jooq.tools.LoggerListener.exception.mute") == null) {
            log.debug((Object) "Exception", (Throwable) ctx.exception());
        }
    }

    private Record record(Configuration configuration, Routine<?> routine) {
        Record result = null;
        List<Field<?>> fields = new ArrayList<>(1 + routine.getOutParameters().size());
        Parameter<?> returnParam = routine.getReturnParameter();
        if (returnParam != null) {
            fields.add(DSL.field(DSL.name(returnParam.getName()), returnParam.getDataType()));
        }
        for (Parameter<?> param : routine.getOutParameters()) {
            fields.add(DSL.field(DSL.name(param.getName()), param.getDataType()));
        }
        if (fields.size() > 0) {
            result = DSL.using(configuration).newRecord((Field<?>[]) fields.toArray(new Field[0]));
            int i = 0;
            if (returnParam != null) {
                i = 0 + 1;
                result.setValue(fields.get(0), routine.getValue(returnParam));
            }
            Iterator<Parameter<?>> it = routine.getOutParameters().iterator();
            while (it.hasNext()) {
                int i2 = i;
                i++;
                result.setValue(fields.get(i2), routine.getValue(it.next()));
            }
            result.changed(false);
        }
        return result;
    }

    private void logMultiline(String comment, String message, Level level) {
        for (String line : message.split("\n")) {
            if (level == Level.FINE) {
                log.debug(comment, line);
            } else {
                log.trace(comment, line);
            }
            comment = "";
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/tools/LoggerListener$BindValueAbbreviator.class */
    private static class BindValueAbbreviator implements VisitListener {
        private boolean anyAbbreviations = false;

        private BindValueAbbreviator() {
        }

        @Override // org.jooq.VisitListener
        public void visitStart(VisitContext context) {
            if (context.renderContext() != null) {
                QueryPart part = context.queryPart();
                if (part instanceof Param) {
                    Param<?> param = (Param) part;
                    Object value = param.getValue();
                    if ((value instanceof String) && ((String) value).length() > LoggerListener.maxLength) {
                        this.anyAbbreviations = true;
                        context.queryPart(DSL.val(StringUtils.abbreviate((String) value, LoggerListener.maxLength)));
                    } else if ((value instanceof byte[]) && ((byte[]) value).length > LoggerListener.maxLength) {
                        this.anyAbbreviations = true;
                        context.queryPart(DSL.val(Arrays.copyOf((byte[]) value, LoggerListener.maxLength)));
                    }
                }
            }
        }

        @Override // org.jooq.VisitListener
        public void visitEnd(VisitContext context) {
            if (this.anyAbbreviations && context.queryPartsLength() == 1) {
                context.renderContext().sql(" -- Bind values may have been abbreviated for DEBUG logging. Use TRACE logging for very large bind variables.");
            }
        }
    }
}

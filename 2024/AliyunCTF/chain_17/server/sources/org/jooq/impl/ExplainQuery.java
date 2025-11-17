package org.jooq.impl;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jooq.DSLContext;
import org.jooq.Explain;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.StatementType;
import org.springframework.web.servlet.tags.form.TextareaTag;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ExplainQuery.class */
final class ExplainQuery {
    ExplainQuery() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Explain explain(DSLContext ctx, Query query) {
        Result<Record> result;
        switch (ctx.family()) {
            case H2:
                result = ctx.fetch("{explain analyze} {0}", query);
                break;
            case HSQLDB:
                result = ctx.configuration().deriveSettings(s -> {
                    return s.withStatementType(StatementType.STATIC_STATEMENT);
                }).dsl().fetch("{explain plan for} {0}", query);
                break;
            default:
                result = ctx.fetch("{explain} {0}", query);
                break;
        }
        double cost = Double.NaN;
        double rows = Double.NaN;
        switch (ctx.family()) {
            case MARIADB:
            case MYSQL:
                rows = ((Double) ((Record) result.get(0)).get(TextareaTag.ROWS_ATTRIBUTE, Double.TYPE)).doubleValue();
                break;
            case POSTGRES:
            case YUGABYTEDB:
                Matcher matcher = Pattern.compile(".*\\bcost=\\d+\\.\\d+\\.\\.(\\d+\\.\\d+)\\s+rows=(\\d+).*").matcher((CharSequence) ((Record) result.get(0)).get(0, String.class));
                cost = Double.parseDouble(matcher.replaceAll("$1"));
                rows = Double.parseDouble(matcher.replaceAll("$2"));
                break;
        }
        return new ExplainImpl(rows, cost, result.format());
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ExplainQuery$ExplainImpl.class */
    private static final class ExplainImpl extends Record implements Explain {
        private final double rows;
        private final double cost;
        private final String plan;

        private ExplainImpl(double rows, double cost, String plan) {
            this.rows = rows;
            this.cost = cost;
            this.plan = plan;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, ExplainImpl.class), ExplainImpl.class, "rows;cost;plan", "FIELD:Lorg/jooq/impl/ExplainQuery$ExplainImpl;->rows:D", "FIELD:Lorg/jooq/impl/ExplainQuery$ExplainImpl;->cost:D", "FIELD:Lorg/jooq/impl/ExplainQuery$ExplainImpl;->plan:Ljava/lang/String;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, ExplainImpl.class, Object.class), ExplainImpl.class, "rows;cost;plan", "FIELD:Lorg/jooq/impl/ExplainQuery$ExplainImpl;->rows:D", "FIELD:Lorg/jooq/impl/ExplainQuery$ExplainImpl;->cost:D", "FIELD:Lorg/jooq/impl/ExplainQuery$ExplainImpl;->plan:Ljava/lang/String;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        @Override // org.jooq.Explain
        public double rows() {
            return this.rows;
        }

        @Override // org.jooq.Explain
        public double cost() {
            return this.cost;
        }

        @Override // org.jooq.Explain
        public String plan() {
            return this.plan;
        }

        @Override // java.lang.Record
        public String toString() {
            return String.format("Explain [cost=%.2f, rows=%.2f]\n\n", Double.valueOf(this.cost), Double.valueOf(this.rows)) + this.plan;
        }
    }
}

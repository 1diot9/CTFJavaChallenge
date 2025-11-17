package org.jooq.impl;

import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.DatePart;
import org.jooq.Field;
import org.jooq.Keyword;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Extract.class */
public final class Extract extends AbstractField<Integer> implements QOM.Extract {
    private final Field<?> field;
    private final DatePart datePart;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Extract(Field<?> field, DatePart datePart) {
        super(Names.N_EXTRACT, SQLDataType.INTEGER);
        this.field = field;
        this.datePart = datePart;
    }

    /* JADX WARN: Type inference failed for: r0v11, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v18, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v25, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v44, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v49, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v54, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v59, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v6, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v64, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v71, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v76, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v81, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v86, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v91, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v96, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case SQLITE:
                switch (this.datePart) {
                    case YEAR:
                        ctx.visit(DSL.function(Names.N_STRFTIME, SQLDataType.VARCHAR, (Field<?>[]) new Field[]{DSL.inline("%Y"), this.field}).cast(SQLDataType.INTEGER));
                        return;
                    case MONTH:
                        ctx.visit(DSL.function(Names.N_STRFTIME, SQLDataType.VARCHAR, (Field<?>[]) new Field[]{DSL.inline("%m"), this.field}).cast(SQLDataType.INTEGER));
                        return;
                    case DAY:
                        ctx.visit(DSL.function(Names.N_STRFTIME, SQLDataType.VARCHAR, (Field<?>[]) new Field[]{DSL.inline("%d"), this.field}).cast(SQLDataType.INTEGER));
                        return;
                    case HOUR:
                        ctx.visit(DSL.function(Names.N_STRFTIME, SQLDataType.VARCHAR, (Field<?>[]) new Field[]{DSL.inline("%H"), this.field}).cast(SQLDataType.INTEGER));
                        return;
                    case MINUTE:
                        ctx.visit(DSL.function(Names.N_STRFTIME, SQLDataType.VARCHAR, (Field<?>[]) new Field[]{DSL.inline("%M"), this.field}).cast(SQLDataType.INTEGER));
                        return;
                    case SECOND:
                        ctx.visit(DSL.function(Names.N_STRFTIME, SQLDataType.VARCHAR, (Field<?>[]) new Field[]{DSL.inline("%S"), this.field}).cast(SQLDataType.INTEGER));
                        return;
                    case EPOCH:
                        ctx.visit(DSL.function(Names.N_STRFTIME, SQLDataType.VARCHAR, (Field<?>[]) new Field[]{DSL.inline("%s"), this.field}).cast(SQLDataType.INTEGER));
                        return;
                    case ISO_DAY_OF_WEEK:
                        ctx.visit((Field<?>) dowSun0ToISO(DSL.function(DSL.systemName("strftime"), SQLDataType.INTEGER, (Field<?>[]) new Field[]{DSL.inline("%w"), this.field}).cast(SQLDataType.INTEGER)));
                        return;
                    case DAY_OF_WEEK:
                        ctx.visit(DSL.function(Names.N_STRFTIME, SQLDataType.VARCHAR, (Field<?>[]) new Field[]{DSL.inline("%w"), this.field}).cast(SQLDataType.INTEGER).plus(DSL.one()));
                        return;
                    case DAY_OF_YEAR:
                        ctx.visit(DSL.function(Names.N_STRFTIME, SQLDataType.VARCHAR, (Field<?>[]) new Field[]{DSL.inline("%j"), this.field}).cast(SQLDataType.INTEGER));
                        return;
                }
            case DERBY:
                switch (this.datePart) {
                    case YEAR:
                        ctx.visit(Keywords.K_YEAR).sql('(').visit(this.field).sql(')');
                        return;
                    case MONTH:
                        ctx.visit(Keywords.K_MONTH).sql('(').visit(this.field).sql(')');
                        return;
                    case DAY:
                        ctx.visit(Keywords.K_DAY).sql('(').visit(this.field).sql(')');
                        return;
                    case HOUR:
                        ctx.visit(Keywords.K_HOUR).sql('(').visit(this.field).sql(')');
                        return;
                    case MINUTE:
                        ctx.visit(Keywords.K_MINUTE).sql('(').visit(this.field).sql(')');
                        return;
                    case SECOND:
                        ctx.visit(Keywords.K_SECOND).sql('(').visit(this.field).sql(')');
                        return;
                }
            case MARIADB:
            case MYSQL:
                switch (this.datePart) {
                    case EPOCH:
                        ctx.visit(DSL.keyword("unix_timestamp")).sql('(').visit(this.field).sql(')');
                        return;
                    case ISO_DAY_OF_WEEK:
                        ctx.visit(Names.N_WEEKDAY).sql('(').visit(this.field).sql(") + 1");
                        return;
                    case DAY_OF_WEEK:
                        ctx.visit(Names.N_DAYOFWEEK).sql('(').visit(this.field).sql(')');
                        return;
                    case DAY_OF_YEAR:
                        ctx.visit(Names.N_DAYOFYEAR).sql('(').visit(this.field).sql(')');
                        return;
                    case QUARTER:
                        ctx.visit(this.datePart.toName()).sql('(').visit(this.field).sql(')');
                        return;
                }
            case POSTGRES:
            case YUGABYTEDB:
                switch (this.datePart) {
                    case ISO_DAY_OF_WEEK:
                        acceptNativeFunction(ctx, DSL.keyword("isodow"));
                        return;
                    case DAY_OF_WEEK:
                        ctx.sql('(');
                        acceptNativeFunction(ctx, DSL.keyword("dow"));
                        ctx.sql(" + 1)");
                        return;
                    case DAY_OF_YEAR:
                        acceptNativeFunction(ctx, DSL.keyword("doy"));
                        return;
                    case QUARTER:
                    case CENTURY:
                    case DECADE:
                    case MILLENNIUM:
                    case TIMEZONE:
                        acceptNativeFunction(ctx);
                        return;
                    case MILLISECOND:
                        acceptNativeFunction(ctx, DSL.keyword("milliseconds"));
                        return;
                    case MICROSECOND:
                        acceptNativeFunction(ctx, DSL.keyword("microseconds"));
                        return;
                }
            case HSQLDB:
                switch (this.datePart) {
                    case EPOCH:
                        ctx.visit(DSL.keyword("unix_timestamp")).sql('(').visit(this.field).sql(')');
                        return;
                    case ISO_DAY_OF_WEEK:
                        ctx.visit((Field<?>) dowSun1ToISO(DSL.field("{extract}({day_of_week from} {0})", (DataType) SQLDataType.INTEGER, this.field)));
                        return;
                    case QUARTER:
                    case WEEK:
                        ctx.visit(this.datePart.toName()).sql('(').visit(this.field).sql(')');
                        return;
                }
            case H2:
                switch (this.datePart) {
                    case QUARTER:
                        ctx.visit(this.datePart.toName()).sql('(').visit(this.field).sql(')');
                        return;
                    case WEEK:
                        ctx.visit(DSL.keyword("iso_week")).sql('(').visit(this.field).sql(')');
                        return;
                }
        }
        acceptDefaultEmulation(ctx);
    }

    private static final Field<Integer> dowISOToSun1(Field<Integer> dow) {
        return Internal.iadd(dow.mod(DSL.inline(7)), DSL.one());
    }

    private static final Field<Integer> dowSun1ToISO(Field<Integer> dow) {
        return Internal.iadd(Internal.iadd(dow, DSL.inline(5)).mod(DSL.inline(7)), DSL.one());
    }

    private static final Field<Integer> dowSun0ToISO(Field<Integer> dow) {
        return Internal.iadd(Internal.iadd(dow, DSL.inline(6)).mod(DSL.inline(7)), DSL.one());
    }

    private final void acceptDefaultEmulation(Context<?> ctx) {
        switch (this.datePart) {
            case QUARTER:
                ctx.visit(DSL.floor(Internal.idiv(Internal.iadd(DSL.month(this.field), DSL.inline(2)), DSL.inline(3))));
                return;
            case MILLISECOND:
            case MICROSECOND:
            default:
                acceptNativeFunction(ctx);
                return;
            case CENTURY:
                ctx.visit(DSL.floor(Internal.idiv(Internal.imul(DSL.sign(DSL.year(this.field)), Internal.iadd(DSL.abs(DSL.year(this.field)), DSL.inline(99))), DSL.inline(100))));
                return;
            case DECADE:
                ctx.visit(DSL.floor(Internal.idiv(DSL.year(this.field), DSL.inline(10))));
                return;
            case MILLENNIUM:
                ctx.visit(DSL.floor(Internal.idiv(Internal.imul(DSL.sign(DSL.year(this.field)), Internal.iadd(DSL.abs(DSL.year(this.field)), DSL.inline(999))), DSL.inline(1000))));
                return;
            case TIMEZONE:
                ctx.visit(Internal.iadd(Internal.imul(DSL.extract(this.field, DatePart.TIMEZONE_HOUR), DSL.inline(3600)), Internal.imul(DSL.extract(this.field, DatePart.TIMEZONE_MINUTE), DSL.inline(60))));
                return;
        }
    }

    private final void acceptNativeFunction(Context<?> ctx) {
        acceptNativeFunction(ctx, this.datePart.toKeyword());
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    private final void acceptNativeFunction(Context<?> ctx, Keyword keyword) {
        ctx.visit(Names.N_EXTRACT).sql('(').visit(keyword).sql(' ').visit(Keywords.K_FROM).sql(' ').visit(this.field).sql(')');
    }

    @Override // org.jooq.impl.QOM.Extract
    public final Field<?> $field() {
        return this.field;
    }

    @Override // org.jooq.impl.QOM.Extract
    public final DatePart $datePart() {
        return this.datePart;
    }
}

package org.jooq.impl;

import java.util.Set;
import org.jooq.Clause;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function3;
import org.jooq.Name;
import org.jooq.Record1;
import org.jooq.SQLDialect;
import org.jooq.Select;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.TableOptions;
import org.jooq.conf.ParamType;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/GenerateSeries.class */
public final class GenerateSeries extends AbstractTable<Record1<Integer>> implements AutoAlias<Table<Record1<Integer>>>, QOM.GenerateSeries<Integer> {
    private static final Set<SQLDialect> EMULATE_WITH_RECURSIVE = SQLDialect.supportedUntil(SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.SQLITE, SQLDialect.TRINO);
    private static final Set<SQLDialect> EMULATE_SYSTEM_RANGE = SQLDialect.supportedBy(SQLDialect.H2);
    private final Field<Integer> from;
    private final Field<Integer> to;
    private final Field<Integer> step;
    private final Name name;

    @Override // org.jooq.impl.AutoAlias
    public /* bridge */ /* synthetic */ Table<Record1<Integer>> autoAlias(Context context, Table<Record1<Integer>> table) {
        return autoAlias2((Context<?>) context, table);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public GenerateSeries(Field<Integer> from, Field<Integer> to) {
        this(from, to, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public GenerateSeries(Field<Integer> from, Field<Integer> to, Field<Integer> step) {
        this(from, to, step, Names.N_GENERATE_SERIES);
    }

    GenerateSeries(Field<Integer> from, Field<Integer> to, Field<Integer> step, Name name) {
        super(TableOptions.expression(), name);
        this.from = from;
        this.to = to;
        this.step = step;
        this.name = name;
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table, org.jooq.SelectField
    public Table<Record1<Integer>> as(Name alias) {
        return new TableAlias(new GenerateSeries(this.from, this.to, this.step, alias), alias);
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table
    public Table<Record1<Integer>> as(Name alias, Name... fieldAliases) {
        return new TableAlias(new GenerateSeries(this.from, this.to, this.step, alias), alias, fieldAliases);
    }

    /* JADX WARN: Type inference failed for: r0v16, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v25, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v36, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v7, types: [org.jooq.Context] */
    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (EMULATE_WITH_RECURSIVE.contains(ctx.dialect())) {
            Field<Integer> f = DSL.field(this.name, SQLDataType.INTEGER);
            Tools.visitSubquery(ctx, DSL.withRecursive(this.name, this.name).as(DSL.select(this.from).unionAll((Select) DSL.select(Internal.iadd(f, this.step == null ? DSL.inline(1) : this.step)).from(this.name).where(f.lt(this.to)))).select(f).from(this.name), 1);
            return;
        }
        if (EMULATE_SYSTEM_RANGE.contains(ctx.dialect())) {
            if (this.step == null) {
                ctx.visit(Names.N_SYSTEM_RANGE).sql('(').visit((Field<?>) this.from).sql(", ").visit((Field<?>) this.to).sql(')');
                return;
            }
            ctx.visit(Names.N_SYSTEM_RANGE).sql('(').visit((Field<?>) this.from).sql(", ").visit((Field<?>) this.to).sql(", ");
            ctx.paramType(ParamType.INLINED, c -> {
                c.visit((Field<?>) this.step);
            });
            ctx.sql(')');
            return;
        }
        if (this.step == null) {
            ctx.visit(Names.N_GENERATE_SERIES).sql('(').visit((Field<?>) this.from).sql(", ").visit((Field<?>) this.to).sql(')');
        } else {
            ctx.visit(Names.N_GENERATE_SERIES).sql('(').visit((Field<?>) this.from).sql(", ").visit((Field<?>) this.to).sql(", ").visit((Field<?>) this.step).sql(')');
        }
    }

    @Override // org.jooq.RecordQualifier
    public final Class<? extends Record1<Integer>> getRecordType() {
        return RecordImpl1.class;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractTable
    public final FieldsImpl<Record1<Integer>> fields0() {
        return new FieldsImpl<>((SelectField<?>[]) new SelectField[]{DSL.field(DSL.name(this.name, Names.N_GENERATE_SERIES), Integer.class)});
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return null;
    }

    /* renamed from: autoAlias, reason: avoid collision after fix types in other method */
    public final Table<Record1<Integer>> autoAlias2(Context<?> ctx, Table<Record1<Integer>> t) {
        if (EMULATE_WITH_RECURSIVE.contains(ctx.dialect())) {
            return t.as(this.name);
        }
        if (EMULATE_SYSTEM_RANGE.contains(ctx.dialect())) {
            return t.as(this.name, this.name);
        }
        return null;
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final Field<Integer> $arg1() {
        return this.from;
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final Field<Integer> $arg2() {
        return this.to;
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final Field<Integer> $arg3() {
        return this.step;
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final Function3<? super Field<Integer>, ? super Field<Integer>, ? super Field<Integer>, ? extends QOM.GenerateSeries<Integer>> $constructor() {
        return (f1, f2, f3) -> {
            return new GenerateSeries(f1, f2, f3, this.name);
        };
    }
}

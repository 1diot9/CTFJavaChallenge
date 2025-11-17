package org.jooq.impl;

import java.util.Set;
import org.jooq.Context;
import org.jooq.DMLQuery;
import org.jooq.Delete;
import org.jooq.Insert;
import org.jooq.Name;
import org.jooq.QueryPart;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.Table;
import org.jooq.TableOptions;
import org.jooq.Update;
import org.jooq.impl.QOM;
import org.jooq.impl.Tools;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DataChangeDeltaTable.class */
public final class DataChangeDeltaTable<R extends Record> extends AbstractTable<R> implements AutoAlias<Table<R>>, QOM.DataChangeDeltaTable<R> {
    private final Set<SQLDialect> EMULATE_USING_CTE;
    private final QOM.ResultOption resultOption;
    private final DMLQuery<R> query;
    private final Table<R> table;
    private final Name alias;

    @Override // org.jooq.impl.AutoAlias
    public /* bridge */ /* synthetic */ QueryPart autoAlias(Context context, QueryPart queryPart) {
        return autoAlias((Context<?>) context, (Table) queryPart);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DataChangeDeltaTable(QOM.ResultOption resultOption, DMLQuery<R> query) {
        this(resultOption, query, table(query));
    }

    private DataChangeDeltaTable(QOM.ResultOption resultOption, DMLQuery<R> query, Table<R> table) {
        this(resultOption, query, table, table.getUnqualifiedName());
    }

    private DataChangeDeltaTable(QOM.ResultOption resultOption, DMLQuery<R> query, Table<R> table, Name alias) {
        super(TableOptions.expression(), alias);
        this.EMULATE_USING_CTE = SQLDialect.supportedBy(SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB);
        this.resultOption = resultOption;
        this.query = query;
        this.table = table;
        this.alias = alias;
    }

    private static final <R extends Record> Table<R> table(DMLQuery<R> dMLQuery) {
        if ((dMLQuery instanceof Insert) || (dMLQuery instanceof Update) || (dMLQuery instanceof Delete)) {
            return (Table<R>) Tools.abstractDMLQuery(dMLQuery).table();
        }
        if (dMLQuery instanceof MergeImpl) {
            return ((MergeImpl) dMLQuery).table();
        }
        throw new IllegalStateException("Unsupported query type: " + String.valueOf(dMLQuery));
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (this.EMULATE_USING_CTE.contains(ctx.dialect())) {
            acceptCte(ctx);
        } else {
            acceptNative(ctx);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private final void acceptCte(Context<?> ctx) {
        TopLevelCte cte = (TopLevelCte) ctx.data(Tools.SimpleDataKey.DATA_TOP_LEVEL_CTE);
        if (cte != null) {
            cte.add((TopLevelCte) DSL.name(this.alias).as(DSL.resultQuery("{0}", CustomQueryPart.of(c2 -> {
                c2.visit(this.query).formatSeparator().visit(Keywords.K_RETURNING).sql(' ').visit(new SelectFieldList(this.table.fields()));
            }))));
        }
        ctx.visit(DSL.table(this.alias));
    }

    /* JADX WARN: Type inference failed for: r0v8, types: [org.jooq.Context] */
    private final void acceptNative(Context<?> ctx) {
        switch (this.resultOption) {
            case FINAL:
                ctx.visit(Keywords.K_FINAL);
                break;
            case OLD:
                ctx.visit(Keywords.K_OLD);
                break;
            case NEW:
                ctx.visit(Keywords.K_NEW);
                break;
            default:
                throw new IllegalStateException("Unsupported result option: " + String.valueOf(this.resultOption));
        }
        ctx.sql(' ').visit(Keywords.K_TABLE).sqlIndentStart(" (");
        Tools.increment(ctx.data(), Tools.SimpleDataKey.DATA_RENDERING_DATA_CHANGE_DELTA_TABLE, () -> {
            ctx.visit(this.query).sqlIndentEnd(')');
        });
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table, org.jooq.SelectField
    public Table<R> as(Name as) {
        return new TableAlias(new DataChangeDeltaTable(this.resultOption, this.query, this.table, as), as);
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table
    public Table<R> as(Name as, Name... fieldAliases) {
        return new TableAlias(new DataChangeDeltaTable(this.resultOption, this.query, this.table, as), as, fieldAliases);
    }

    public final Table<R> autoAlias(Context<?> ctx, Table<R> t) {
        return t.as(this.alias);
    }

    @Override // org.jooq.RecordQualifier
    public final Class<? extends R> getRecordType() {
        return this.table.getRecordType();
    }

    @Override // org.jooq.impl.AbstractTable
    final FieldsImpl<R> fields0() {
        return ((AbstractRow) this.table.as(this.alias).fieldsRow()).fields;
    }

    @Override // org.jooq.impl.QOM.DataChangeDeltaTable
    public final QOM.ResultOption $resultOption() {
        return this.resultOption;
    }

    @Override // org.jooq.impl.QOM.DataChangeDeltaTable
    public final DMLQuery<R> $query() {
        return this.query;
    }
}

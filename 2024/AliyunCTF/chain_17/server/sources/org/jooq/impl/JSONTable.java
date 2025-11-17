package org.jooq.impl;

import java.util.Set;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.JSONB;
import org.jooq.JSONTableColumnPathStep;
import org.jooq.Name;
import org.jooq.QueryPart;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.SelectSelectStep;
import org.jooq.TableOptions;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/JSONTable.class */
public final class JSONTable extends AbstractTable<Record> implements JSONTableColumnPathStep, QOM.UNotYetImplemented {
    private static final Set<SQLDialect> REQUIRES_COLUMN_PATH = SQLDialect.supportedBy(SQLDialect.MARIADB, SQLDialect.MYSQL);
    private final Field<String> path;
    private final Field<?> json;
    private final QueryPartList<JSONTableColumn> columns;
    private final boolean hasOrdinality;
    private transient FieldsImpl<Record> fields;

    @Override // org.jooq.JSONTableColumnsFirstStep
    public /* bridge */ /* synthetic */ JSONTableColumnPathStep column(Field field, DataType dataType) {
        return column((Field<?>) field, (DataType<?>) dataType);
    }

    @Override // org.jooq.JSONTableColumnsFirstStep
    public /* bridge */ /* synthetic */ JSONTableColumnPathStep column(Name name, DataType dataType) {
        return column(name, (DataType<?>) dataType);
    }

    @Override // org.jooq.JSONTableColumnsFirstStep
    public /* bridge */ /* synthetic */ JSONTableColumnPathStep column(String str, DataType dataType) {
        return column(str, (DataType<?>) dataType);
    }

    @Override // org.jooq.JSONTableColumnsFirstStep
    public /* bridge */ /* synthetic */ JSONTableColumnPathStep column(Field field) {
        return column((Field<?>) field);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public JSONTable(Field<?> json, Field<String> path) {
        this(json, path, null, false);
    }

    private JSONTable(Field<?> json, Field<String> path, QueryPartList<JSONTableColumn> columns, boolean hasOrdinality) {
        super(TableOptions.expression(), Names.N_JSON_TABLE);
        this.json = json;
        this.path = path;
        this.columns = columns == null ? new QueryPartList<>() : columns;
        this.hasOrdinality = hasOrdinality;
    }

    @Override // org.jooq.JSONTableColumnsFirstStep
    public final JSONTable column(String name) {
        return column(DSL.name(name));
    }

    @Override // org.jooq.JSONTableColumnsFirstStep
    public final JSONTable column(Name name) {
        return column(DSL.field(name));
    }

    @Override // org.jooq.JSONTableColumnsFirstStep
    public final JSONTable column(Field<?> name) {
        return column(name, name.getDataType());
    }

    @Override // org.jooq.JSONTableColumnsFirstStep
    public final JSONTable column(String name, DataType<?> type) {
        return column(DSL.name(name), type);
    }

    @Override // org.jooq.JSONTableColumnsFirstStep
    public final JSONTable column(Name name, DataType<?> type) {
        return column(DSL.field(name), type);
    }

    @Override // org.jooq.JSONTableColumnsFirstStep
    public final JSONTable column(Field<?> name, DataType<?> type) {
        QueryPartList<JSONTableColumn> c = new QueryPartList<>(this.columns);
        c.add((QueryPartList<JSONTableColumn>) new JSONTableColumn(name, type, false, null));
        return new JSONTable(this.json, this.path, c, this.hasOrdinality);
    }

    @Override // org.jooq.JSONTableColumnForOrdinalityStep
    public final JSONTable forOrdinality() {
        return path0(true, null);
    }

    @Override // org.jooq.JSONTableColumnPathStep
    public final JSONTable path(String p) {
        return path0(false, p);
    }

    private final JSONTable path0(boolean forOrdinality, String p) {
        QueryPartList<JSONTableColumn> c = new QueryPartList<>(this.columns);
        int i = c.size() - 1;
        JSONTableColumn last = c.get(i);
        c.set(i, (int) new JSONTableColumn(last.field, last.type, forOrdinality, p));
        return new JSONTable(this.json, this.path, c, this.hasOrdinality || forOrdinality);
    }

    @Override // org.jooq.RecordQualifier
    public final Class<? extends Record> getRecordType() {
        return RecordImplN.class;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractTable
    public final FieldsImpl<Record> fields0() {
        if (this.fields == null) {
            this.fields = new FieldsImpl<>(Tools.map(this.columns, c -> {
                return c.field.getDataType() == c.type ? c.field : DSL.field(c.field.getQualifiedName(), c.type);
            }));
        }
        return this.fields;
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case POSTGRES:
            case YUGABYTEDB:
                acceptPostgres(ctx);
                return;
            default:
                acceptStandard(ctx);
                return;
        }
    }

    private final void acceptPostgres(Context<?> ctx) {
        String str;
        SelectSelectStep<Record> select = DSL.select(Tools.map(this.columns, col -> {
            if (col.forOrdinality) {
                return DSL.field("o").as(col.field);
            }
            QueryPart[] queryPartArr = new QueryPart[2];
            queryPartArr[0] = col.path != null ? DSL.val(col.path) : DSL.inline("$." + col.field.getName());
            queryPartArr[1] = DSL.keyword(col.type.getCastTypeName(ctx.configuration()));
            return DSL.field("cast((jsonb_path_query_first(j, cast({0} as jsonpath))->>0) as {1})", queryPartArr).as(col.field);
        }));
        if (this.hasOrdinality) {
            str = "jsonb_path_query({0}, cast({1} as jsonpath)) {with} {ordinality} {as} t(j, o)";
        } else {
            str = "jsonb_path_query({0}, cast({1} as jsonpath)) {as} t(j)";
        }
        QueryPart[] queryPartArr = new QueryPart[2];
        queryPartArr[0] = this.json.getType() == JSONB.class ? this.json : this.json.cast(SQLDataType.JSONB);
        queryPartArr[1] = this.path;
        Tools.visitSubquery(ctx, select.from(str, queryPartArr), 1);
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v4, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v9, types: [org.jooq.Context] */
    private final void acceptStandard(Context<?> ctx) {
        ctx.visit(Keywords.K_JSON_TABLE).sqlIndentStart('(');
        ctx.visit(this.json).sql(',').formatSeparator();
        acceptJSONPath(ctx);
        ctx.formatSeparator().visit(Keywords.K_COLUMNS).sql(" (").visit(this.columns).sql(')');
        ctx.sqlIndentEnd(')');
    }

    private final void acceptJSONPath(Context<?> ctx) {
        ctx.visit((Field<?>) this.path);
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public boolean declaresTables() {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/JSONTable$JSONTableColumn.class */
    public static class JSONTableColumn extends AbstractQueryPart implements QOM.UNotYetImplemented {
        final Field<?> field;
        final DataType<?> type;
        final boolean forOrdinality;
        final String path;

        JSONTableColumn(Field<?> field, DataType<?> type, boolean forOrdinality, String path) {
            this.field = field;
            this.type = type;
            this.forOrdinality = forOrdinality;
            this.path = path;
        }

        /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
        /* JADX WARN: Type inference failed for: r0v20, types: [org.jooq.Context] */
        /* JADX WARN: Type inference failed for: r0v27, types: [org.jooq.Context] */
        @Override // org.jooq.QueryPartInternal
        public final void accept(Context<?> ctx) {
            String str;
            ctx.qualify(false, c -> {
                c.visit(this.field);
            }).sql(' ');
            if (this.forOrdinality) {
                ctx.visit(Keywords.K_FOR).sql(' ').visit(Keywords.K_ORDINALITY);
            } else {
                Tools.toSQLDDLTypeDeclaration(ctx, this.type);
            }
            if (this.path != null) {
                str = this.path;
            } else if (!this.forOrdinality && JSONTable.REQUIRES_COLUMN_PATH.contains(ctx.dialect())) {
                str = "$." + this.field.getName();
            } else {
                str = null;
            }
            String p = str;
            if (p != null) {
                ctx.sql(' ').visit(Keywords.K_PATH).sql(' ').visit((Field<?>) DSL.inline(p));
            }
        }
    }
}

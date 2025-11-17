package org.jooq.impl;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.jooq.Clause;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.CreateViewAsStep;
import org.jooq.CreateViewFinalStep;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Function6;
import org.jooq.Name;
import org.jooq.QueryPart;
import org.jooq.Record;
import org.jooq.ResultQuery;
import org.jooq.SQL;
import org.jooq.SQLDialect;
import org.jooq.Select;
import org.jooq.Table;
import org.jooq.conf.ParamType;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/CreateViewImpl.class */
public final class CreateViewImpl<R extends Record> extends AbstractDDLQuery implements QOM.CreateView<R>, CreateViewAsStep<R>, CreateViewFinalStep {
    final Table<?> view;
    final QueryPartListView<? extends Field<?>> fields;
    final boolean orReplace;
    final boolean materialized;
    final boolean ifNotExists;
    ResultQuery<? extends R> query;
    private static final Clause[] CLAUSES = {Clause.CREATE_VIEW};
    private static final Set<SQLDialect> NO_SUPPORT_IF_NOT_EXISTS = SQLDialect.supportedUntil(SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB);
    private static final Set<SQLDialect> NO_SUPPORT_COLUMN_RENAME = SQLDialect.supportedBy(SQLDialect.TRINO);
    private static final Set<SQLDialect> NO_SUPPORT_COLUMN_RENAME_MVIEW = SQLDialect.supportedBy(SQLDialect.H2);
    private transient Select<?> parsed;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CreateViewImpl(Configuration configuration, Table<?> view, Collection<? extends Field<?>> fields, boolean orReplace, boolean materialized, boolean ifNotExists) {
        this(configuration, view, fields, orReplace, materialized, ifNotExists, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CreateViewImpl(Configuration configuration, Table<?> view, Collection<? extends Field<?>> fields, boolean orReplace, boolean materialized, boolean ifNotExists, ResultQuery<? extends R> query) {
        super(configuration);
        this.view = view;
        this.fields = new QueryPartList(fields);
        this.orReplace = orReplace;
        this.materialized = materialized;
        this.ifNotExists = ifNotExists;
        this.query = query;
    }

    @Override // org.jooq.CreateViewAsStep
    public final CreateViewImpl<R> as(ResultQuery<? extends R> query) {
        this.query = query;
        return this;
    }

    @Override // org.jooq.CreateViewAsStep
    public final CreateViewImpl<R> as(String query, QueryPart... parts) {
        return as((ResultQuery) DSL.resultQuery(query, parts));
    }

    @Override // org.jooq.CreateViewAsStep
    public final CreateViewImpl<R> as(String query, Object... bindings) {
        return as((ResultQuery) DSL.resultQuery(query, bindings));
    }

    @Override // org.jooq.CreateViewAsStep
    public final CreateViewImpl<R> as(String query) {
        return as((ResultQuery) DSL.resultQuery(query));
    }

    @Override // org.jooq.CreateViewAsStep
    public final CreateViewImpl<R> as(SQL query) {
        return as((ResultQuery) DSL.resultQuery(query));
    }

    private final boolean supportsIfNotExists(Context<?> ctx) {
        return !NO_SUPPORT_IF_NOT_EXISTS.contains(ctx.dialect());
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (this.ifNotExists && !supportsIfNotExists(ctx)) {
            Tools.tryCatch(ctx, DDLStatementType.CREATE_VIEW, c -> {
                accept0(c);
            });
        } else {
            accept0(ctx);
        }
    }

    private final void accept0(Context<?> ctx) {
        switch (ctx.family()) {
            default:
                acceptDefault(ctx);
                return;
        }
    }

    /* JADX WARN: Type inference failed for: r0v15, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v22, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v31, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v40, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v46, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v49, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v53, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v58, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v61, types: [org.jooq.Context] */
    private final void acceptDefault(Context<?> ctx) {
        QueryPart queryPart;
        List<? extends Field<?>> f = this.fields;
        boolean rename = f != null && f.size() > 0;
        boolean renameSupported = (this.materialized && !NO_SUPPORT_COLUMN_RENAME_MVIEW.contains(ctx.dialect())) || !(this.materialized || NO_SUPPORT_COLUMN_RENAME.contains(ctx.dialect()));
        ctx.start(Clause.CREATE_VIEW_NAME).visit((0 == 0 || !this.orReplace) ? Keywords.K_CREATE : Keywords.K_REPLACE);
        if (this.orReplace && 0 == 0) {
            ctx.sql(' ').visit(Keywords.K_OR);
            switch (ctx.family()) {
                case FIREBIRD:
                    ctx.sql(' ').visit(Keywords.K_ALTER);
                    break;
                default:
                    ctx.sql(' ').visit(Keywords.K_REPLACE);
                    break;
            }
        }
        if (this.materialized) {
            ctx.sql(' ').visit(Keywords.K_MATERIALIZED);
        }
        ctx.sql(' ').visit(Keywords.K_VIEW).sql(' ');
        if (this.ifNotExists && supportsIfNotExists(ctx)) {
            ctx.visit(Keywords.K_IF_NOT_EXISTS).sql(' ');
        }
        ctx.visit(this.view);
        if (rename && renameSupported) {
            ctx.sql('(').visit(QueryPartListView.wrap((List) f).qualify(false)).sql(')');
        }
        Context start = ctx.end(Clause.CREATE_VIEW_NAME).formatSeparator().visit(Keywords.K_AS).formatSeparator().start(Clause.CREATE_VIEW_AS);
        if (rename && !renameSupported) {
            queryPart = DSL.selectFrom(parsed().asTable(DSL.name("t"), (Name[]) Tools.map(f, (v0) -> {
                return v0.getUnqualifiedName();
            }, x$0 -> {
                return new Name[x$0];
            })));
        } else {
            queryPart = this.query;
        }
        start.visit(queryPart, ParamType.INLINED).end(Clause.CREATE_VIEW_AS);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Select<?> parsed() {
        if (this.parsed != null) {
            return this.parsed;
        }
        ResultQuery<? extends R> resultQuery = this.query;
        if (resultQuery instanceof Select) {
            Select s = (Select) resultQuery;
            this.parsed = s;
            return s;
        }
        DSLContext dsl = configuration().dsl();
        return dsl.parser().parseSelect(dsl.renderInlined(this.query));
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    @Override // org.jooq.impl.QOM.CreateView
    public final Table<?> $view() {
        return this.view;
    }

    @Override // org.jooq.impl.QOM.CreateView
    public final QOM.UnmodifiableList<? extends Field<?>> $fields() {
        return QOM.unmodifiable((List) this.fields);
    }

    @Override // org.jooq.impl.QOM.CreateView
    public final boolean $orReplace() {
        return this.orReplace;
    }

    @Override // org.jooq.impl.QOM.CreateView
    public final boolean $materialized() {
        return this.materialized;
    }

    @Override // org.jooq.impl.QOM.CreateView
    public final boolean $ifNotExists() {
        return this.ifNotExists;
    }

    @Override // org.jooq.impl.QOM.CreateView
    public final ResultQuery<? extends R> $query() {
        return this.query;
    }

    @Override // org.jooq.impl.QOM.CreateView
    public final QOM.CreateView<R> $view(Table<?> newValue) {
        return $constructor().apply(newValue, $fields(), Boolean.valueOf($orReplace()), Boolean.valueOf($materialized()), Boolean.valueOf($ifNotExists()), $query());
    }

    @Override // org.jooq.impl.QOM.CreateView
    public final QOM.CreateView<R> $fields(Collection<? extends Field<?>> newValue) {
        return $constructor().apply($view(), newValue, Boolean.valueOf($orReplace()), Boolean.valueOf($materialized()), Boolean.valueOf($ifNotExists()), $query());
    }

    @Override // org.jooq.impl.QOM.CreateView
    public final QOM.CreateView<R> $orReplace(boolean newValue) {
        return $constructor().apply($view(), $fields(), Boolean.valueOf(newValue), Boolean.valueOf($materialized()), Boolean.valueOf($ifNotExists()), $query());
    }

    @Override // org.jooq.impl.QOM.CreateView
    public final QOM.CreateView<R> $materialized(boolean newValue) {
        return $constructor().apply($view(), $fields(), Boolean.valueOf($orReplace()), Boolean.valueOf(newValue), Boolean.valueOf($ifNotExists()), $query());
    }

    @Override // org.jooq.impl.QOM.CreateView
    public final QOM.CreateView<R> $ifNotExists(boolean newValue) {
        return $constructor().apply($view(), $fields(), Boolean.valueOf($orReplace()), Boolean.valueOf($materialized()), Boolean.valueOf(newValue), $query());
    }

    @Override // org.jooq.impl.QOM.CreateView
    public final QOM.CreateView<R> $query(ResultQuery<? extends R> newValue) {
        return $constructor().apply($view(), $fields(), Boolean.valueOf($orReplace()), Boolean.valueOf($materialized()), Boolean.valueOf($ifNotExists()), newValue);
    }

    public final Function6<? super Table<?>, ? super Collection<? extends Field<?>>, ? super Boolean, ? super Boolean, ? super Boolean, ? super ResultQuery<? extends R>, ? extends QOM.CreateView<R>> $constructor() {
        return (a1, a2, a3, a4, a5, a6) -> {
            return new CreateViewImpl(configuration(), a1, a2, a3.booleanValue(), a4.booleanValue(), a5.booleanValue(), a6);
        };
    }
}

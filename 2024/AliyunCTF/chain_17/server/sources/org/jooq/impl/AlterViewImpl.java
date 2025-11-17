package org.jooq.impl;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.jooq.AlterViewFinalStep;
import org.jooq.AlterViewStep;
import org.jooq.Clause;
import org.jooq.Comment;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function7;
import org.jooq.Name;
import org.jooq.SQLDialect;
import org.jooq.Select;
import org.jooq.Table;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AlterViewImpl.class */
public final class AlterViewImpl extends AbstractDDLQuery implements QOM.AlterView, AlterViewStep, AlterViewFinalStep {
    final Table<?> view;
    final QueryPartListView<? extends Field<?>> fields;
    final boolean materialized;
    final boolean ifExists;
    Comment comment;
    Table<?> renameTo;
    Select<?> as;
    private static final Clause[] CLAUSES = {Clause.ALTER_VIEW};
    private static final Set<SQLDialect> NO_SUPPORT_RENAME_IF_EXISTS = SQLDialect.supportedUntil(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD);
    private static final Set<SQLDialect> NO_SUPPORT_IF_EXISTS = SQLDialect.supportedUntil(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD);
    private static final Set<SQLDialect> SUPPORT_ALTER_TABLE_RENAME = SQLDialect.supportedBy(SQLDialect.HSQLDB, SQLDialect.YUGABYTEDB);

    @Override // org.jooq.AlterViewStep
    public /* bridge */ /* synthetic */ AlterViewFinalStep as(Select select) {
        return as((Select<?>) select);
    }

    @Override // org.jooq.AlterViewStep
    public /* bridge */ /* synthetic */ AlterViewFinalStep renameTo(Table table) {
        return renameTo((Table<?>) table);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AlterViewImpl(Configuration configuration, Table<?> view, Collection<? extends Field<?>> fields, boolean materialized, boolean ifExists) {
        this(configuration, view, fields, materialized, ifExists, null, null, null);
    }

    AlterViewImpl(Configuration configuration, Table<?> view, boolean materialized, boolean ifExists) {
        this(configuration, view, null, materialized, ifExists);
    }

    AlterViewImpl(Configuration configuration, Table<?> view, Collection<? extends Field<?>> fields, boolean materialized, boolean ifExists, Comment comment, Table<?> renameTo, Select<?> as) {
        super(configuration);
        this.view = view;
        this.fields = new QueryPartList(fields);
        this.materialized = materialized;
        this.ifExists = ifExists;
        this.comment = comment;
        this.renameTo = renameTo;
        this.as = as;
    }

    @Override // org.jooq.AlterViewStep
    public final AlterViewImpl comment(String comment) {
        return comment(DSL.comment(comment));
    }

    @Override // org.jooq.AlterViewStep
    public final AlterViewImpl comment(Comment comment) {
        this.comment = comment;
        return this;
    }

    @Override // org.jooq.AlterViewStep
    public final AlterViewImpl renameTo(String renameTo) {
        return renameTo((Table<?>) DSL.table(DSL.name(renameTo)));
    }

    @Override // org.jooq.AlterViewStep
    public final AlterViewImpl renameTo(Name renameTo) {
        return renameTo((Table<?>) DSL.table(renameTo));
    }

    @Override // org.jooq.AlterViewStep
    public final AlterViewImpl renameTo(Table<?> renameTo) {
        this.renameTo = renameTo;
        return this;
    }

    @Override // org.jooq.AlterViewStep
    public final AlterViewImpl as(Select<?> as) {
        this.as = as;
        return this;
    }

    private final boolean supportsIfExists(Context<?> ctx) {
        return this.renameTo != null ? !NO_SUPPORT_RENAME_IF_EXISTS.contains(ctx.dialect()) : !NO_SUPPORT_IF_EXISTS.contains(ctx.dialect());
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (this.ifExists && !supportsIfExists(ctx)) {
            Tools.tryCatch(ctx, DDLStatementType.ALTER_VIEW, c -> {
                accept0(c);
            });
        } else {
            accept0(ctx);
        }
    }

    /* JADX WARN: Type inference failed for: r0v10, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v15, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v22, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v27, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v31, types: [org.jooq.Context] */
    private final void accept0(Context<?> ctx) {
        if (this.as != null) {
            switch (ctx.family()) {
                case CUBRID:
                case DERBY:
                case FIREBIRD:
                case H2:
                case HSQLDB:
                case IGNITE:
                case MARIADB:
                case MYSQL:
                case POSTGRES:
                case SQLITE:
                case YUGABYTEDB:
                    if (this.materialized) {
                        ctx.visit(DSL.begin(DSL.dropMaterializedView(this.view), DSL.createMaterializedView(this.view, (Field<?>[]) this.fields.toArray(Tools.EMPTY_FIELD)).as(this.as)));
                        return;
                    } else {
                        ctx.visit(DSL.begin(DSL.dropView(this.view), DSL.createView(this.view, (Field<?>[]) this.fields.toArray(Tools.EMPTY_FIELD)).as(this.as)));
                        return;
                    }
                default:
                    ctx.visit(Keywords.K_ALTER).sql(' ');
                    if (this.materialized) {
                        ctx.visit(Keywords.K_MATERIALIZED).sql(' ');
                    }
                    ctx.visit(Keywords.K_VIEW).sql(' ').visit(this.view);
                    if (!this.fields.isEmpty()) {
                        ctx.sql(" (").visit(QueryPartCollectionView.wrap(this.fields).qualify(false)).sql(')');
                    }
                    ctx.formatSeparator().visit(Keywords.K_AS).formatSeparator().visit(this.as);
                    return;
            }
        }
        if (this.comment != null) {
            ctx.visit((this.materialized ? DSL.commentOnMaterializedView(this.view) : DSL.commentOnView(this.view)).is(this.comment));
        } else {
            accept1(ctx);
        }
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v12, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v19, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v26, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v34, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v37, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v9, types: [org.jooq.Context] */
    private final void accept1(Context<?> ctx) {
        ctx.start(Clause.ALTER_VIEW_VIEW).visit(Keywords.K_ALTER).sql(' ');
        if (SUPPORT_ALTER_TABLE_RENAME.contains(ctx.dialect())) {
            ctx.visit(Keywords.K_TABLE).sql(' ');
        } else if (this.materialized) {
            ctx.visit(Keywords.K_MATERIALIZED).sql(' ').visit(Keywords.K_VIEW).sql(' ');
        } else {
            ctx.visit(Keywords.K_VIEW).sql(' ');
        }
        if (this.ifExists && supportsIfExists(ctx)) {
            ctx.sql(' ').visit(Keywords.K_IF_EXISTS);
        }
        ctx.sql(' ').visit(this.view).sql(' ').end(Clause.ALTER_VIEW_VIEW);
        if (this.renameTo != null) {
            ctx.start(Clause.ALTER_VIEW_RENAME).visit(Keywords.K_RENAME_TO).sql(' ').qualify(false, c -> {
                c.visit(this.renameTo);
            }).end(Clause.ALTER_VIEW_RENAME);
        }
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    @Override // org.jooq.impl.QOM.AlterView
    public final Table<?> $view() {
        return this.view;
    }

    @Override // org.jooq.impl.QOM.AlterView
    public final QOM.UnmodifiableList<? extends Field<?>> $fields() {
        return QOM.unmodifiable((List) this.fields);
    }

    @Override // org.jooq.impl.QOM.AlterView
    public final boolean $materialized() {
        return this.materialized;
    }

    @Override // org.jooq.impl.QOM.AlterView
    public final boolean $ifExists() {
        return this.ifExists;
    }

    @Override // org.jooq.impl.QOM.AlterView
    public final Comment $comment() {
        return this.comment;
    }

    @Override // org.jooq.impl.QOM.AlterView
    public final Table<?> $renameTo() {
        return this.renameTo;
    }

    @Override // org.jooq.impl.QOM.AlterView
    public final Select<?> $as() {
        return this.as;
    }

    @Override // org.jooq.impl.QOM.AlterView
    public final QOM.AlterView $view(Table<?> newValue) {
        return $constructor().apply(newValue, $fields(), Boolean.valueOf($materialized()), Boolean.valueOf($ifExists()), $comment(), $renameTo(), $as());
    }

    @Override // org.jooq.impl.QOM.AlterView
    public final QOM.AlterView $fields(Collection<? extends Field<?>> newValue) {
        return $constructor().apply($view(), newValue, Boolean.valueOf($materialized()), Boolean.valueOf($ifExists()), $comment(), $renameTo(), $as());
    }

    @Override // org.jooq.impl.QOM.AlterView
    public final QOM.AlterView $materialized(boolean newValue) {
        return $constructor().apply($view(), $fields(), Boolean.valueOf(newValue), Boolean.valueOf($ifExists()), $comment(), $renameTo(), $as());
    }

    @Override // org.jooq.impl.QOM.AlterView
    public final QOM.AlterView $ifExists(boolean newValue) {
        return $constructor().apply($view(), $fields(), Boolean.valueOf($materialized()), Boolean.valueOf(newValue), $comment(), $renameTo(), $as());
    }

    @Override // org.jooq.impl.QOM.AlterView
    public final QOM.AlterView $comment(Comment newValue) {
        return $constructor().apply($view(), $fields(), Boolean.valueOf($materialized()), Boolean.valueOf($ifExists()), newValue, $renameTo(), $as());
    }

    @Override // org.jooq.impl.QOM.AlterView
    public final QOM.AlterView $renameTo(Table<?> newValue) {
        return $constructor().apply($view(), $fields(), Boolean.valueOf($materialized()), Boolean.valueOf($ifExists()), $comment(), newValue, $as());
    }

    @Override // org.jooq.impl.QOM.AlterView
    public final QOM.AlterView $as(Select<?> newValue) {
        return $constructor().apply($view(), $fields(), Boolean.valueOf($materialized()), Boolean.valueOf($ifExists()), $comment(), $renameTo(), newValue);
    }

    public final Function7<? super Table<?>, ? super Collection<? extends Field<?>>, ? super Boolean, ? super Boolean, ? super Comment, ? super Table<?>, ? super Select<?>, ? extends QOM.AlterView> $constructor() {
        return (a1, a2, a3, a4, a5, a6, a7) -> {
            return new AlterViewImpl(configuration(), a1, a2, a3.booleanValue(), a4.booleanValue(), a5, a6, a7);
        };
    }
}

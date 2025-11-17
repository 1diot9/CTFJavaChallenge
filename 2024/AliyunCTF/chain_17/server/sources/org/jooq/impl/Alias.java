package org.jooq.impl;

import java.util.Set;
import java.util.function.Predicate;
import org.jooq.Clause;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.QueryPart;
import org.jooq.SQLDialect;
import org.jooq.Table;
import org.jooq.conf.RenderOptionalKeyword;
import org.jooq.impl.QOM;
import org.jooq.impl.Tools;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Alias.class */
public final class Alias<Q extends QueryPart> extends AbstractQueryPart implements QOM.UEmpty, SimpleCheckQueryPart {
    private static final Clause[] CLAUSES_TABLE_REFERENCE = {Clause.TABLE, Clause.TABLE_REFERENCE};
    private static final Clause[] CLAUSES_TABLE_ALIAS = {Clause.TABLE, Clause.TABLE_ALIAS};
    private static final Clause[] CLAUSES_FIELD_REFERENCE = {Clause.FIELD, Clause.FIELD_REFERENCE};
    private static final Clause[] CLAUSES_FIELD_ALIAS = {Clause.FIELD, Clause.FIELD_ALIAS};
    static final Set<SQLDialect> NO_SUPPORT_ALIASED_JOIN_TABLES = SQLDialect.supportedBy(SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.SQLITE);
    static final Set<SQLDialect> SUPPORT_AS_REQUIRED = SQLDialect.supportedBy(SQLDialect.DERBY, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB);
    static final Set<SQLDialect> SUPPORT_DERIVED_COLUMN_NAMES_SPECIAL1 = SQLDialect.supportedBy(SQLDialect.CUBRID, SQLDialect.FIREBIRD, SQLDialect.MYSQL);
    static final Set<SQLDialect> SUPPORT_DERIVED_COLUMN_NAMES_SPECIAL2 = SQLDialect.supportedUntil(SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.SQLITE);
    final Q wrapped;
    final Q wrapping;
    final Name alias;
    final Name[] fieldAliases;
    final Predicate<Context<?>> wrapInParentheses;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Alias(Q wrapped, Q wrapping, Name alias) {
        this(wrapped, wrapping, alias, null, c -> {
            return false;
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Alias(Q wrapped, Q wrapping, Name alias, Name[] fieldAliases, Predicate<Context<?>> wrapInParentheses) {
        this.wrapped = wrapped;
        this.wrapping = wrapping;
        this.alias = alias;
        this.fieldAliases = fieldAliases;
        this.wrapInParentheses = wrapInParentheses;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Q wrapped() {
        return this.wrapped;
    }

    @Override // org.jooq.impl.SimpleCheckQueryPart
    public final boolean isSimple(Context<?> ctx) {
        return ((this.wrapped instanceof Table) && !ctx.declareTables()) || ((this.wrapped instanceof Field) && !ctx.declareFields());
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (ctx.declareAliases() && (ctx.declareFields() || ctx.declareTables())) {
            boolean aliasedJoinTable = this.wrapped instanceof JoinTable;
            if (!aliasedJoinTable) {
                ctx.declareAliases(false);
            }
            acceptDeclareAliasStandard(ctx);
            if (!aliasedJoinTable) {
                ctx.declareAliases(true);
                return;
            }
            return;
        }
        ctx.qualify(false, c -> {
            c.visit(this.alias);
        });
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    private final void acceptDeclareAliasTSQL(Context<?> ctx) {
        ctx.visit(this.alias).sql(" = ");
        toSQLWrapped(ctx);
    }

    /* JADX WARN: Code restructure failed: missing block: B:59:0x0174, code lost:            r0.add((org.jooq.impl.SelectFieldList<org.jooq.Field<?>>) org.jooq.impl.DSL.field("null").as(r9.fieldAliases[r17]));        r17 = r17 + 1;     */
    /* JADX WARN: Removed duplicated region for block: B:57:0x0161  */
    /* JADX WARN: Type inference failed for: r0v52, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v69, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v82, types: [org.jooq.Context] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private final void acceptDeclareAliasStandard(org.jooq.Context<?> r10) {
        /*
            Method dump skipped, instructions count: 640
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jooq.impl.Alias.acceptDeclareAliasStandard(org.jooq.Context):void");
    }

    /* JADX WARN: Type inference failed for: r0v12, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v17, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v26, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v31, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v34, types: [org.jooq.Context] */
    final void toSQLAs(Context<?> ctx) {
        if (Boolean.TRUE.equals(ctx.data(Tools.BooleanDataKey.DATA_AS_REQUIRED))) {
            ctx.sql(' ').visit(Keywords.K_AS);
            return;
        }
        if (this.wrapped instanceof Field) {
            if (ctx.settings().getRenderOptionalAsKeywordForFieldAliases() == RenderOptionalKeyword.DEFAULT && SUPPORT_AS_REQUIRED.contains(ctx.dialect())) {
                ctx.sql(' ').visit(Keywords.K_AS);
                return;
            } else {
                if (ctx.settings().getRenderOptionalAsKeywordForFieldAliases() == RenderOptionalKeyword.ON) {
                    ctx.sql(' ').visit(Keywords.K_AS);
                    return;
                }
                return;
            }
        }
        if (ctx.settings().getRenderOptionalAsKeywordForTableAliases() == RenderOptionalKeyword.DEFAULT && SUPPORT_AS_REQUIRED.contains(ctx.dialect())) {
            ctx.sql(' ').visit(Keywords.K_AS);
        } else if (ctx.settings().getRenderOptionalAsKeywordForTableAliases() == RenderOptionalKeyword.ON) {
            ctx.sql(' ').visit(Keywords.K_AS);
        }
    }

    private final void toSQLWrapped(Context<?> ctx) {
        boolean wrap = this.wrapInParentheses.test(ctx);
        if (wrap) {
            ctx.data(Tools.BooleanDataKey.DATA_WRAP_DERIVED_TABLES_IN_PARENTHESES, false, c -> {
                toSQLWrapped(c, wrap);
            });
        } else {
            toSQLWrapped(ctx, wrap);
        }
    }

    private final void toSQLWrapped(Context<?> ctx, boolean wrap) {
        boolean nestedJoinTable = this.wrapped instanceof JoinTable;
        if (wrap) {
            if (nestedJoinTable) {
                ctx.sqlIndentStart('(');
            } else {
                ctx.sql('(');
            }
        }
        if (nestedJoinTable && NO_SUPPORT_ALIASED_JOIN_TABLES.contains(ctx.dialect())) {
            ctx.visit(DSL.select(DSL.asterisk()).from((Table) this.wrapped));
        } else {
            ctx.visit(this.wrapped);
        }
        if (wrap) {
            if (nestedJoinTable) {
                ctx.sqlIndentEnd(')');
            } else {
                ctx.sql(')');
            }
        }
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    private final void toSQLDerivedColumnList(Context<?> ctx) {
        ctx.sql(" (").visit(QueryPartListView.wrap(this.fieldAliases)).sql(')');
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        if (ctx.declareFields() || ctx.declareTables()) {
            if (this.wrapped instanceof Table) {
                return CLAUSES_TABLE_ALIAS;
            }
            return CLAUSES_FIELD_ALIAS;
        }
        if (this.wrapped instanceof Table) {
            return CLAUSES_TABLE_REFERENCE;
        }
        return CLAUSES_FIELD_REFERENCE;
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final boolean declaresFields() {
        return true;
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final boolean declaresTables() {
        return true;
    }
}

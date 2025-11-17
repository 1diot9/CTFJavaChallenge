package org.jooq.impl;

import java.util.Set;
import java.util.function.Predicate;
import org.jooq.Clause;
import org.jooq.Context;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.Select;
import org.jooq.Table;
import org.jooq.TableOptions;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DerivedTable.class */
public class DerivedTable<R extends Record> extends AbstractTable<R> implements QOM.DerivedTable<R> {
    static final Set<SQLDialect> NO_SUPPORT_CORRELATED_DERIVED_TABLE = SQLDialect.supportedUntil(SQLDialect.DERBY, SQLDialect.H2, SQLDialect.MARIADB);
    private final Lazy<Select<R>> query;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DerivedTable(Select<R> query) {
        this(Lazy.of(() -> {
            return query;
        }), Names.N_T);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DerivedTable(Lazy<Select<R>> query, Name name) {
        super(TableOptions.expression(), name);
        this.query = query;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Select<R> query() {
        return this.query.get();
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table, org.jooq.SelectField
    public final Table<R> as(Name alias) {
        return new TableAlias(this, alias, (Predicate<Context<?>>) c -> {
            return true;
        });
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table
    public final Table<R> as(Name alias, Name... fieldAliases) {
        return new TableAlias(this, alias, fieldAliases, c -> {
            return true;
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractTable
    public FieldsImpl<R> fields0() {
        return new FieldsImpl<>(query().getSelect());
    }

    @Override // org.jooq.RecordQualifier
    public final Class<? extends R> getRecordType() {
        return query().getRecordType();
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        Tools.visitSubquery(ctx, query(), 1, false);
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return null;
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final org.jooq.Function1<? super Select<R>, ? extends QOM.DerivedTable<R>> $constructor() {
        return t -> {
            return new DerivedTable(t);
        };
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final Select<R> $arg1() {
        return query();
    }
}

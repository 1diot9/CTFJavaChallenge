package org.jooq.impl;

import java.util.List;
import java.util.Set;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Name;
import org.jooq.QueryPart;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.Select;
import org.jooq.Table;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/WithOrdinalityTable.class */
public final class WithOrdinalityTable<R extends Record> extends AbstractTable<R> implements AutoAlias<Table<R>>, QOM.WithOrdinalityTable<R> {
    static final Set<SQLDialect> NO_SUPPORT_STANDARD = SQLDialect.supportedBy(SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.SQLITE);
    static final Set<SQLDialect> NO_SUPPORT_TVF = SQLDialect.supportedBy(SQLDialect.H2, SQLDialect.HSQLDB);
    static final Set<SQLDialect> NO_SUPPORT_TABLE_EXPRESSIONS = SQLDialect.supportedBy(SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB);
    final AbstractTable<?> delegate;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.jooq.impl.WithOrdinalityTable$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/WithOrdinalityTable$1.class */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    @Override // org.jooq.impl.AutoAlias
    public /* bridge */ /* synthetic */ QueryPart autoAlias(Context context, QueryPart queryPart) {
        return autoAlias((Context<?>) context, (Table) queryPart);
    }

    @Override // org.jooq.impl.QOM.WithOrdinalityTable
    public /* bridge */ /* synthetic */ QOM.WithOrdinalityTable $table(Table table) {
        return $table((Table<?>) table);
    }

    static {
        NO_SUPPORT_TVF.addAll(NO_SUPPORT_STANDARD);
        NO_SUPPORT_TABLE_EXPRESSIONS.addAll(NO_SUPPORT_TVF);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public WithOrdinalityTable(AbstractTable<?> delegate) {
        super(delegate.getOptions(), delegate.getQualifiedName(), delegate.getSchema());
        this.delegate = delegate;
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final boolean declaresTables() {
        return true;
    }

    @Override // org.jooq.RecordQualifier
    public final Class<? extends R> getRecordType() {
        return RecordImplN.class;
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table
    public final List<ForeignKey<R, ?>> getReferences() {
        return (List<ForeignKey<R, ?>>) this.delegate.getReferences();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractTable
    public final FieldsImpl<R> fields0() {
        FieldsImpl<R> r = new FieldsImpl<>(this.delegate.fields0().fields);
        for (int i = 0; i < r.fields.length; i++) {
            r.fields[i] = DSL.field(this.delegate.getUnqualifiedName().append(r.fields[i].getUnqualifiedName()), r.fields[i].getDataType());
        }
        r.add(DSL.field(Names.N_ORDINAL, SQLDataType.BIGINT));
        return r;
    }

    public final Table<R> autoAlias(Context<?> ctx, Table<R> t) {
        if (t != this && (t instanceof AutoAlias)) {
            return (Table) ((AutoAlias) t).autoAlias(ctx, t);
        }
        if (t instanceof QOM.Aliasable) {
            QOM.Aliasable<?> a = (QOM.Aliasable) t;
            Name alias = a.$alias();
            if (alias == null) {
                alias = ((Table) a.$aliased()).getUnqualifiedName();
            }
            Field<?>[] fields = t.fields();
            if (Tools.isEmpty(fields)) {
                return t.as(alias);
            }
            return t.as(DSL.table(alias), fields);
        }
        return null;
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if ((this.delegate instanceof ArrayTable) || (this.delegate instanceof ArrayOfValues)) {
            if (NO_SUPPORT_STANDARD.contains(ctx.dialect())) {
                acceptEmulation(ctx);
                return;
            } else {
                acceptStandard(ctx);
                return;
            }
        }
        if ((this.delegate instanceof FunctionTable) && NO_SUPPORT_TVF.contains(ctx.dialect())) {
            if (NO_SUPPORT_TVF.contains(ctx.dialect())) {
                acceptEmulation(ctx);
                return;
            } else {
                acceptStandard(ctx);
                return;
            }
        }
        if ((this.delegate instanceof TableImpl) && ((TableImpl) this.delegate).parameters != null) {
            if (NO_SUPPORT_TVF.contains(ctx.dialect())) {
                acceptEmulation(ctx);
                return;
            } else {
                acceptStandard(ctx);
                return;
            }
        }
        if (NO_SUPPORT_TABLE_EXPRESSIONS.contains(ctx.dialect())) {
            acceptEmulation(ctx);
        } else {
            acceptStandard(ctx);
        }
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    private final void acceptStandard(Context<?> ctx) {
        ctx.visit(this.delegate).sql(' ').visit(Keywords.K_WITH).sql(' ').visit(Keywords.K_ORDINALITY);
    }

    private final void acceptEmulation(Context<?> ctx) {
        switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
            default:
                Select<?> s = DSL.select(this.delegate.fields()).select(DSL.rowNumber().over().as(Names.N_ORDINAL)).from(this.delegate);
                Tools.visitSubquery(ctx, s, 1, true);
                return;
        }
    }

    @Override // org.jooq.impl.QOM.WithOrdinalityTable
    public final Table<?> $table() {
        return this.delegate;
    }

    @Override // org.jooq.impl.QOM.WithOrdinalityTable
    public final WithOrdinalityTable<?> $table(Table<?> newTable) {
        return new WithOrdinalityTable<>((AbstractTable) newTable);
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.impl.QOM.Aliasable
    public final Table<R> $aliased() {
        return new WithOrdinalityTable((AbstractTable) this.delegate.$aliased());
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.impl.QOM.Aliasable
    public final Name $alias() {
        return this.delegate.$alias();
    }
}

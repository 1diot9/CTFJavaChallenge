package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.TableOptions;
import org.jooq.exception.SQLDialectNotSupportedException;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/FunctionTable.class */
public final class FunctionTable<R extends Record> extends AbstractTable<R> implements QOM.UNotYetImplemented {
    private final Field<?> function;

    /* JADX INFO: Access modifiers changed from: package-private */
    public FunctionTable(Field<?> function) {
        super(TableOptions.function(), Names.N_FUNCTION);
        this.function = function;
    }

    @Override // org.jooq.RecordQualifier
    public final Class<? extends R> getRecordType() {
        return RecordImplN.class;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractTable
    public final FieldsImpl<R> fields0() {
        return new FieldsImpl<>((SelectField<?>[]) new SelectField[0]);
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table, org.jooq.SelectField
    public final Table<R> as(Name as) {
        return new TableAlias(new FunctionTable(this.function), as);
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table
    public final Table<R> as(Name as, Name... fieldAliases) {
        return new TableAlias(new FunctionTable(this.function), as, fieldAliases);
    }

    /* JADX WARN: Type inference failed for: r0v6, types: [org.jooq.Context] */
    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case HSQLDB:
                ctx.visit(Keywords.K_TABLE).sql('(').visit(this.function).sql(')');
                return;
            case POSTGRES:
            case YUGABYTEDB:
                ctx.visit(this.function);
                return;
            default:
                throw new SQLDialectNotSupportedException("FUNCTION TABLE is not supported for " + String.valueOf(ctx.dialect()));
        }
    }
}

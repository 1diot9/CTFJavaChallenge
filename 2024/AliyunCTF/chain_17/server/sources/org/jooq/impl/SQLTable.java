package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Record;
import org.jooq.SQL;
import org.jooq.SQLDialect;
import org.jooq.SelectField;
import org.jooq.TableOptions;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/SQLTable.class */
public final class SQLTable extends AbstractTable<Record> implements QOM.UEmptyTable<Record> {
    private final SQL delegate;

    /* renamed from: org.jooq.impl.SQLTable$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/SQLTable$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SQLTable(SQL delegate) {
        super(TableOptions.expression(), DSL.name(delegate.toString()));
        this.delegate = delegate;
    }

    @Override // org.jooq.RecordQualifier
    public final Class<? extends Record> getRecordType() {
        return RecordImplN.class;
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
            default:
                ctx.visit(this.delegate);
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractTable
    public final FieldsImpl<Record> fields0() {
        return new FieldsImpl<>((SelectField<?>[]) new SelectField[0]);
    }
}

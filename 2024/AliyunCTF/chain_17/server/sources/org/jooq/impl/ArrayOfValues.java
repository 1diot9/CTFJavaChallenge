package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.TableOptions;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ArrayOfValues.class */
public final class ArrayOfValues extends AbstractAutoAliasTable<Record> implements QOM.UNotYetImplemented {
    private final Field<?>[] array;
    private final FieldsImpl<Record> field;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ArrayOfValues(Field<?>[] array) {
        this(array, Names.N_ARRAY_TABLE);
    }

    ArrayOfValues(Field<?>[] array, Name alias) {
        this(array, alias, null);
    }

    ArrayOfValues(Field<?>[] array, Name alias, Name[] fieldAliases) {
        super(alias, ArrayTable.fieldAliases(fieldAliases));
        Class<?> arrayType = !Tools.isEmpty(array) ? array[0].getType() : Object.class;
        this.array = array;
        this.field = ArrayTable.init(arrayType, this.alias, this.fieldAliases[0]);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractAutoAliasTable
    /* renamed from: construct */
    public final AbstractAutoAliasTable<Record> construct2(Name newAlias, Name[] newFieldAliases) {
        return new ArrayOfValues(this.array, newAlias, newFieldAliases);
    }

    @Override // org.jooq.RecordQualifier
    public final Class<? extends Record> getRecordType() {
        return RecordImplN.class;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractTable
    public final FieldsImpl<Record> fields0() {
        return this.field;
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case CUBRID:
            case DERBY:
            case FIREBIRD:
            case IGNITE:
            case MARIADB:
            case MYSQL:
            case SQLITE:
                ctx.visit(new ArrayTableEmulation(this.array, this.fieldAliases));
                return;
            default:
                ctx.visit(new StandardUnnest());
                return;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ArrayOfValues$StandardUnnest.class */
    private class StandardUnnest extends DialectArrayTable {
        private StandardUnnest() {
            super();
        }

        /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
        @Override // org.jooq.QueryPartInternal
        public final void accept(Context<?> ctx) {
            ctx.visit(Keywords.K_UNNEST).sql('(').visit(Keywords.K_ARRAY).sql('[').visit(QueryPartListView.wrap(ArrayOfValues.this.array)).sql(']').sql(")");
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ArrayOfValues$DialectArrayTable.class */
    private abstract class DialectArrayTable extends AbstractTable<Record> implements QOM.UTransient {
        DialectArrayTable() {
            super(TableOptions.expression(), ArrayOfValues.this.alias);
        }

        @Override // org.jooq.RecordQualifier
        public final Class<? extends Record> getRecordType() {
            return RecordImplN.class;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.AbstractTable
        public final FieldsImpl<Record> fields0() {
            return ArrayOfValues.this.fields0();
        }
    }
}

package org.jooq.impl;

import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.Param;
import org.jooq.QueryPart;
import org.jooq.Record;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.TableOptions;
import org.jooq.exception.DataTypeException;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ArrayTable.class */
public final class ArrayTable extends AbstractAutoAliasTable<Record> implements QOM.UNotYetImplemented {
    private final Field<?> array;
    private final FieldsImpl<Record> field;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ArrayTable(Field<?> array) {
        this(array, Names.N_ARRAY_TABLE);
    }

    ArrayTable(Field<?> array, Name alias) {
        this(array, alias, (Name[]) null);
    }

    ArrayTable(Field<?> array, Name alias, Name[] fieldAliases) {
        this(array, alias, init(arrayType(array), alias, fieldAliases(fieldAliases)[0]));
    }

    private ArrayTable(Field<?> array, Name alias, FieldsImpl<Record> fields) {
        super(alias, (Name[]) Tools.map(fields.fields, (v0) -> {
            return v0.getUnqualifiedName();
        }, x$0 -> {
            return new Name[x$0];
        }));
        this.array = array;
        this.field = fields;
    }

    private static final Class<?> arrayType(Field<?> array) {
        Class<?> arrayType;
        if (array.getDataType().getType().isArray()) {
            arrayType = array.getDataType().getArrayComponentType();
        } else {
            arrayType = Object.class;
        }
        return arrayType;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Name[] fieldAliases(Name[] fieldAliases) {
        return Tools.isEmpty(fieldAliases) ? new Name[]{Names.N_COLUMN_VALUE} : fieldAliases;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final FieldsImpl<Record> init(Class<?> arrayType, Name alias, Name fieldAlias) {
        if (Record.class.isAssignableFrom(arrayType)) {
            try {
                return new FieldsImpl<>(Tools.map(((Record) arrayType.getDeclaredConstructor(new Class[0]).newInstance(new Object[0])).fields(), f -> {
                    return DSL.field(alias.append(f.getUnqualifiedName()), f.getDataType());
                }));
            } catch (Exception e) {
                throw new DataTypeException("Bad UDT Type : " + String.valueOf(arrayType), e);
            }
        }
        return new FieldsImpl<>((SelectField<?>[]) new SelectField[]{DSL.field(alias.unqualifiedName().append(fieldAlias.unqualifiedName()), DSL.getDataType(arrayType))});
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractAutoAliasTable
    /* renamed from: construct, reason: merged with bridge method [inline-methods] */
    public final AbstractAutoAliasTable<Record> construct2(Name newAlias, Name[] newFieldAliases) {
        return new ArrayTable(this.array, newAlias, newFieldAliases);
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
        ctx.visit(table(ctx.configuration()));
    }

    private final QueryPart table(Configuration configuration) {
        boolean isArray = this.array.getDataType().getType().isArray();
        switch (configuration.family()) {
            case CUBRID:
            case DERBY:
            case FIREBIRD:
            case IGNITE:
            case MARIADB:
            case MYSQL:
            case SQLITE:
                if (isArray && (this.array instanceof Param)) {
                    return emulateParam();
                }
                if (isArray && (this.array instanceof Array)) {
                    return emulateArray();
                }
                return DSL.table("{0}", this.array);
            default:
                return new StandardUnnest();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ArrayTable$StandardUnnest.class */
    public class StandardUnnest extends DialectArrayTable {
        private StandardUnnest() {
            super();
        }

        /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
        @Override // org.jooq.QueryPartInternal
        public final void accept(Context<?> ctx) {
            ctx.visit(Keywords.K_UNNEST).sql('(').visit(ArrayTable.this.array).sql(")");
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ArrayTable$DialectArrayTable.class */
    private abstract class DialectArrayTable extends AbstractTable<Record> implements AutoAlias<Table<Record>>, QOM.UTransient {
        @Override // org.jooq.impl.AutoAlias
        public /* bridge */ /* synthetic */ Table<Record> autoAlias(Context context, Table<Record> table) {
            return autoAlias2((Context<?>) context, table);
        }

        DialectArrayTable() {
            super(TableOptions.expression(), ArrayTable.this.alias);
        }

        @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
        public final boolean declaresTables() {
            return true;
        }

        @Override // org.jooq.RecordQualifier
        public final Class<? extends Record> getRecordType() {
            return RecordImplN.class;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.AbstractTable
        public final FieldsImpl<Record> fields0() {
            return ArrayTable.this.fields0();
        }

        /* renamed from: autoAlias, reason: avoid collision after fix types in other method */
        public final Table<Record> autoAlias2(Context<?> ctx, Table<Record> t) {
            return t.as(ArrayTable.this.alias, ArrayTable.this.fieldAliases);
        }
    }

    private final QueryPart emulateParam() {
        return new ArrayTableEmulation((Object[]) ((Param) this.array).getValue(), this.fieldAliases);
    }

    private final QueryPart emulateArray() {
        return new ArrayTableEmulation(((Array) this.array).fields.fields, this.fieldAliases);
    }
}

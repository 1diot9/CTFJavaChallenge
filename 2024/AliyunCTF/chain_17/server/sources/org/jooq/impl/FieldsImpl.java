package org.jooq.impl;

import java.sql.SQLWarning;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.Converter;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Fields;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.RecordType;
import org.jooq.Row;
import org.jooq.Select;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.QOM;
import org.jooq.tools.JooqLogger;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/FieldsImpl.class */
public final class FieldsImpl<R extends Record> extends AbstractQueryPart implements RecordType<R>, Mappable<R>, QOM.UTransient {
    Field<?>[] fields;
    private static final JooqLogger log = JooqLogger.getLogger((Class<?>) FieldsImpl.class);
    private static final FieldOrIndex<Field<?>> RETURN_FIELD = new FieldOrIndex<Field<?>>() { // from class: org.jooq.impl.FieldsImpl.1
        @Override // org.jooq.impl.FieldsImpl.FieldOrIndex
        public /* bridge */ /* synthetic */ Field<?> result(Field field, int i) {
            return result((Field<?>) field, i);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.FieldsImpl.FieldOrIndex
        public Field<?> result(Field<?> field, int index) {
            return field;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.FieldsImpl.FieldOrIndex
        public Field<?> resultNull() {
            return null;
        }
    };
    private static final FieldOrIndex<Integer> RETURN_INDEX = new FieldOrIndex<Integer>() { // from class: org.jooq.impl.FieldsImpl.2
        @Override // org.jooq.impl.FieldsImpl.FieldOrIndex
        public /* bridge */ /* synthetic */ Integer result(Field field, int i) {
            return result((Field<?>) field, i);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.FieldsImpl.FieldOrIndex
        public Integer result(Field<?> field, int index) {
            return Integer.valueOf(index);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.FieldsImpl.FieldOrIndex
        public Integer resultNull() {
            return -1;
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/FieldsImpl$FieldOrIndex.class */
    public interface FieldOrIndex<U> {
        U result(Field<?> field, int i);

        U resultNull();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public FieldsImpl(SelectField<?>... fields) {
        this.fields = (Field[]) Tools.map(fields, toField(), x$0 -> {
            return new Field[x$0];
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public FieldsImpl(Collection<? extends SelectField<?>> fields) {
        this.fields = (Field[]) Tools.map(fields, toField(), x$0 -> {
            return new Field[x$0];
        });
    }

    @Override // org.jooq.impl.Mappable
    public final RecordMapper<R, ?> mapper(int fieldIndex) {
        return r -> {
            return r.get(fieldIndex);
        };
    }

    @Override // org.jooq.impl.Mappable
    public final <U> RecordMapper<R, U> mapper(int fieldIndex, Configuration configuration, Class<? extends U> type) {
        return mapper(fieldIndex, Tools.converterOrFail(configuration, (Object) null, this.fields[safeIndex(fieldIndex)].getType(), type));
    }

    @Override // org.jooq.impl.Mappable
    public final <U> RecordMapper<R, U> mapper(int fieldIndex, Converter<?, ? extends U> converter) {
        return r -> {
            return r.get(fieldIndex, converter);
        };
    }

    @Override // org.jooq.impl.Mappable
    public final RecordMapper<R, Record> mapper(int[] fieldIndexes) {
        return mapper(fields(fieldIndexes));
    }

    @Override // org.jooq.impl.Mappable
    public final RecordMapper<R, ?> mapper(String fieldName) {
        return mapper(Tools.indexOrFail(this, fieldName));
    }

    @Override // org.jooq.impl.Mappable
    public final <U> RecordMapper<R, U> mapper(String fieldName, Configuration configuration, Class<? extends U> type) {
        return mapper(fieldName, Tools.converterOrFail(configuration, (Object) null, field(Tools.indexOrFail(this, fieldName)).getType(), type));
    }

    @Override // org.jooq.impl.Mappable
    public final <U> RecordMapper<R, U> mapper(String fieldName, Converter<?, ? extends U> converter) {
        return r -> {
            return r.get(fieldName, converter);
        };
    }

    @Override // org.jooq.impl.Mappable
    public final RecordMapper<R, Record> mapper(String[] fieldNames) {
        return mapper(fields(fieldNames));
    }

    @Override // org.jooq.impl.Mappable
    public final RecordMapper<R, ?> mapper(Name fieldName) {
        return mapper(Tools.indexOrFail(this, fieldName));
    }

    @Override // org.jooq.impl.Mappable
    public final <U> RecordMapper<R, U> mapper(Name fieldName, Configuration configuration, Class<? extends U> type) {
        return mapper(fieldName, Tools.converterOrFail(configuration, (Object) null, field(Tools.indexOrFail(this, fieldName)).getType(), type));
    }

    @Override // org.jooq.impl.Mappable
    public final <U> RecordMapper<R, U> mapper(Name fieldName, Converter<?, ? extends U> converter) {
        return r -> {
            return r.get(fieldName, converter);
        };
    }

    @Override // org.jooq.impl.Mappable
    public final RecordMapper<R, Record> mapper(Name[] fieldNames) {
        return mapper(fields(fieldNames));
    }

    @Override // org.jooq.impl.Mappable
    public final <T> RecordMapper<R, T> mapper(Field<T> field) {
        return mapper(Tools.indexOrFail((Fields) this, (Field<?>) field));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.Mappable
    public final <U> RecordMapper<R, U> mapper(Field<?> field, Configuration configuration, Class<? extends U> type) {
        return mapper(field, Tools.converterOrFail(configuration, (Object) null, (Class<Object>) field.getType(), type));
    }

    @Override // org.jooq.impl.Mappable
    public final <T, U> RecordMapper<R, U> mapper(Field<T> field, Converter<? super T, ? extends U> converter) {
        return mapper(Tools.indexOrFail((Fields) this, (Field<?>) field), converter);
    }

    @Override // org.jooq.impl.Mappable
    public final RecordMapper<R, Record> mapper(Field<?>[] f) {
        AbstractRow<?> row = Tools.row0(f == null ? Tools.EMPTY_FIELD : f);
        return r -> {
            return Tools.newRecord(false, AbstractRecord.class, row, r.configuration()).operate(x -> {
                for (Field<?> field : row.fields.fields) {
                    Tools.copyValue((AbstractRecord) x, field, r, field);
                }
                return x;
            });
        };
    }

    @Override // org.jooq.impl.Mappable
    public final <S extends Record> RecordMapper<R, S> mapper(Table<S> table) {
        return r -> {
            return r.into(table);
        };
    }

    @Override // org.jooq.impl.Mappable
    public final <E> RecordMapper<R, E> mapper(Configuration configuration, Class<? extends E> type) {
        return configuration.recordMapperProvider().provide(this, type);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Row fieldsRow0(FieldsTrait fields) {
        if (!(fields instanceof Select)) {
            return fields.fieldsRow();
        }
        Select<?> s = (Select) fields;
        return s.asTable("t").fieldsRow();
    }

    private static final ThrowingFunction<SelectField<?>, Field<?>, RuntimeException> toField() {
        return f -> {
            if (f instanceof Row) {
                Row r = (Row) f;
                return new RowAsField(r);
            }
            if (f instanceof Table) {
                Table<?> t = (Table) f;
                return new TableAsField(t);
            }
            return (Field) f;
        };
    }

    @Override // org.jooq.RecordType
    public final int size() {
        return this.fields.length;
    }

    @Override // org.jooq.Fields
    public final <T> Field<T> field(Field<T> field) {
        return (Field) field0((Field<?>) field, RETURN_FIELD);
    }

    private final <U> U field0(Field<?> field, FieldOrIndex<U> result) {
        String tName;
        if (field == null) {
            return result.resultNull();
        }
        for (int i = 0; i < this.fields.length; i++) {
            Field<?> f = this.fields[i];
            if (f == field) {
                return result.result(f, i);
            }
        }
        for (int i2 = 0; i2 < this.fields.length; i2++) {
            Field<?> f2 = this.fields[i2];
            if (f2.equals(field)) {
                return result.result(f2, i2);
            }
        }
        Field<?> columnOnlyMatch = null;
        Field<?> columnOnlyMatch2 = null;
        int columnOnlyIndexMatch = -1;
        Field<?> unaliased = null;
        Field<?> aliasMatch = null;
        Field<?> aliasMatch2 = null;
        int aliasIndexMatch = -1;
        String tableName = tableName(field);
        String fieldName = field.getName();
        for (int i3 = 0; i3 < this.fields.length; i3++) {
            Field<?> f3 = this.fields[i3];
            String fName = f3.getName();
            if (tableName != null && (tName = tableName(f3)) != null && tableName.equals(tName) && fName.equals(fieldName)) {
                return result.result(f3, i3);
            }
            if (fName.equals(fieldName)) {
                if (unaliased == null) {
                    unaliased = Tools.unaliasTable(field);
                }
                if (unaliased != null && unaliased.equals(Tools.unaliasTable(f3))) {
                    if (aliasMatch == null) {
                        aliasMatch = f3;
                        aliasIndexMatch = i3;
                    } else {
                        aliasMatch2 = f3;
                    }
                }
                if (columnOnlyMatch == null) {
                    columnOnlyMatch = f3;
                    columnOnlyIndexMatch = i3;
                } else {
                    columnOnlyMatch2 = f3;
                }
            }
        }
        if (aliasMatch2 != null && log.isInfoEnabled()) {
            log.info((Object) ("Ambiguous match found for " + String.valueOf(field) + ". Both " + String.valueOf(aliasMatch) + " and " + String.valueOf(aliasMatch2) + " match."), (Throwable) new SQLWarning());
        }
        if (aliasMatch != null) {
            return result.result(aliasMatch, aliasIndexMatch);
        }
        if (columnOnlyMatch2 != null && log.isInfoEnabled()) {
            log.info((Object) ("Ambiguous match found for " + String.valueOf(field) + ". Both " + String.valueOf(columnOnlyMatch) + " and " + String.valueOf(columnOnlyMatch2) + " match."), (Throwable) new SQLWarning());
        }
        return result.result(columnOnlyMatch, columnOnlyIndexMatch);
    }

    private final String tableName(Field<?> field) {
        if (field instanceof TableField) {
            TableField<?, ?> f = (TableField) field;
            Table<R> table = f.getTable();
            if (table != null) {
                return table.getName();
            }
            return null;
        }
        return null;
    }

    @Override // org.jooq.Fields
    public final Field<?> field(String fieldName) {
        return (Field) field0(fieldName, RETURN_FIELD);
    }

    private final <U> U field0(String fieldName, FieldOrIndex<U> result) {
        if (fieldName == null) {
            return result.resultNull();
        }
        Field<?> columnMatch = null;
        int indexMatch = -1;
        for (int i = 0; i < this.fields.length; i++) {
            Field<?> f = this.fields[i];
            if (f.getName().equals(fieldName)) {
                if (columnMatch == null) {
                    columnMatch = f;
                    indexMatch = i;
                } else {
                    log.info((Object) ("Ambiguous match found for " + fieldName + ". Both " + String.valueOf(columnMatch) + " and " + String.valueOf(f) + " match."), (Throwable) new SQLWarning());
                }
            }
        }
        return result.result(columnMatch, indexMatch);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Fields
    public final <T> Field<T> field(String str, Class<T> cls) {
        Field<?> field = field(str);
        if (field == null) {
            return null;
        }
        return (Field<T>) field.coerce(cls);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Fields
    public final <T> Field<T> field(String str, DataType<T> dataType) {
        Field<?> field = field(str);
        if (field == null) {
            return null;
        }
        return (Field<T>) field.coerce(dataType);
    }

    @Override // org.jooq.Fields
    public final Field<?> field(Name name) {
        return (Field) field0(name, RETURN_FIELD);
    }

    private final <U> U field0(Name name, FieldOrIndex<U> fieldOrIndex) {
        if (name == null) {
            return fieldOrIndex.resultNull();
        }
        return (U) field0(DSL.field(name), fieldOrIndex);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Fields
    public final <T> Field<T> field(Name name, Class<T> cls) {
        Field<?> field = field(name);
        if (field == null) {
            return null;
        }
        return (Field<T>) field.coerce(cls);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Fields
    public final <T> Field<T> field(Name name, DataType<T> dataType) {
        Field<?> field = field(name);
        if (field == null) {
            return null;
        }
        return (Field<T>) field.coerce(dataType);
    }

    @Override // org.jooq.Fields
    public final Field<?> field(int index) {
        if (index >= 0 && index < this.fields.length) {
            return this.fields[index];
        }
        return null;
    }

    final int safeIndex(int index) {
        if (index >= 0 && index < this.fields.length) {
            return index;
        }
        throw new IllegalArgumentException("No field at index " + index + " in Record type " + String.valueOf(this.fields));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Fields
    public final <T> Field<T> field(int i, Class<T> cls) {
        Field<?> field = field(i);
        if (field == null) {
            return null;
        }
        return (Field<T>) field.coerce(cls);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Fields
    public final <T> Field<T> field(int i, DataType<T> dataType) {
        Field<?> field = field(i);
        if (field == null) {
            return null;
        }
        return (Field<T>) field.coerce(dataType);
    }

    @Override // org.jooq.Fields
    public final Field<?>[] fields() {
        return this.fields;
    }

    @Override // org.jooq.Fields
    public final Row fieldsRow() {
        return new RowImplN(this.fields);
    }

    @Override // org.jooq.Fields
    public final Stream<Field<?>> fieldStream() {
        return Stream.of((Object[]) this.fields);
    }

    @Override // org.jooq.Fields
    public final Field<?>[] fields(Field<?>... f) {
        return (Field[]) Tools.map(f, i -> {
            return field(i);
        }, x$0 -> {
            return new Field[x$0];
        });
    }

    @Override // org.jooq.Fields
    public final Field<?>[] fields(String... f) {
        return (Field[]) Tools.map(f, i -> {
            return field(i);
        }, x$0 -> {
            return new Field[x$0];
        });
    }

    @Override // org.jooq.Fields
    public final Field<?>[] fields(Name... f) {
        return (Field[]) Tools.map(f, i -> {
            return field(i);
        }, x$0 -> {
            return new Field[x$0];
        });
    }

    @Override // org.jooq.Fields
    public final Field<?>[] fields(int... f) {
        return (Field[]) Tools.map(f, i -> {
            return field(i);
        }, x$0 -> {
            return new Field[x$0];
        });
    }

    @Override // org.jooq.Fields
    public final int indexOf(Field<?> field) {
        return ((Integer) field0(field, RETURN_INDEX)).intValue();
    }

    @Override // org.jooq.Fields
    public final int indexOf(String fieldName) {
        return ((Integer) field0(fieldName, RETURN_INDEX)).intValue();
    }

    @Override // org.jooq.Fields
    public final int indexOf(Name fieldName) {
        return ((Integer) field0(fieldName, RETURN_INDEX)).intValue();
    }

    @Override // org.jooq.Fields
    public final Class<?>[] types() {
        return (Class[]) Tools.map(this.fields, f -> {
            return f.getType();
        }, x$0 -> {
            return new Class[x$0];
        });
    }

    @Override // org.jooq.Fields
    public final Class<?> type(int fieldIndex) {
        if (fieldIndex < 0 || fieldIndex >= size()) {
            return null;
        }
        return field(fieldIndex).getType();
    }

    @Override // org.jooq.Fields
    public final Class<?> type(String fieldName) {
        return type(Tools.indexOrFail(this, fieldName));
    }

    @Override // org.jooq.Fields
    public final Class<?> type(Name fieldName) {
        return type(Tools.indexOrFail(this, fieldName));
    }

    @Override // org.jooq.Fields
    public final DataType<?>[] dataTypes() {
        return (DataType[]) Tools.map(this.fields, f -> {
            return f.getDataType();
        }, x$0 -> {
            return new DataType[x$0];
        });
    }

    @Override // org.jooq.Fields
    public final DataType<?> dataType(int fieldIndex) {
        if (fieldIndex < 0 || fieldIndex >= size()) {
            return null;
        }
        return field(fieldIndex).getDataType();
    }

    @Override // org.jooq.Fields
    public final DataType<?> dataType(String fieldName) {
        return dataType(Tools.indexOrFail(this, fieldName));
    }

    @Override // org.jooq.Fields
    public final DataType<?> dataType(Name fieldName) {
        return dataType(Tools.indexOrFail(this, fieldName));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int[] indexesOf(Field<?>... f) {
        int[] result = new int[f.length];
        for (int i = 0; i < f.length; i++) {
            result[i] = Tools.indexOrFail(this, f[i]);
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int[] indexesOf(String... fieldNames) {
        int[] result = new int[fieldNames.length];
        for (int i = 0; i < fieldNames.length; i++) {
            result[i] = Tools.indexOrFail(this, fieldNames[i]);
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int[] indexesOf(Name... fieldNames) {
        int[] result = new int[fieldNames.length];
        for (int i = 0; i < fieldNames.length; i++) {
            result[i] = Tools.indexOrFail(this, fieldNames[i]);
        }
        return result;
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        ctx.visit(QueryPartListView.wrap(this.fields));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void add(Field<?> f) {
        Field<?>[] result = new Field[this.fields.length + 1];
        System.arraycopy(this.fields, 0, result, 0, this.fields.length);
        result[this.fields.length] = f;
        this.fields = result;
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that instanceof FieldsImpl) {
            FieldsImpl<?> f = (FieldsImpl) that;
            return Arrays.equals(this.fields, f.fields);
        }
        return false;
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public int hashCode() {
        return Arrays.hashCode(this.fields);
    }
}

package org.jooq.impl;

import java.util.Collection;
import java.util.stream.Stream;
import org.jooq.Binding;
import org.jooq.Clause;
import org.jooq.Comment;
import org.jooq.Comparator;
import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.ContextConverter;
import org.jooq.Converter;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row;
import org.jooq.SelectField;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractRow.class */
public abstract class AbstractRow<R extends Record> extends AbstractQueryPart implements Row, SelectField<R> {
    private static final Clause[] CLAUSES = {Clause.FIELD_ROW};
    final FieldsImpl<R> fields;

    @Override // org.jooq.SelectField
    public /* bridge */ /* synthetic */ SelectField as(Field field) {
        return as((Field<?>) field);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractRow(SelectField<?>... fields) {
        this(new FieldsImpl(fields));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractRow(Collection<? extends SelectField<?>> fields) {
        this(new FieldsImpl(fields));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractRow(FieldsImpl<R> fields) {
        this.fields = fields;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final RowAsField<Row, R> rf() {
        return new RowAsField<>(this);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectField
    public final <U> SelectField<U> convert(Binding<R, U> binding) {
        return rf().convert((Binding) binding);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectField
    public final <U> SelectField<U> convert(Converter<R, U> converter) {
        return rf().convert((Converter) converter);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectField
    public final <U> SelectField<U> convert(Class<U> toType, java.util.function.Function<? super R, ? extends U> function, java.util.function.Function<? super U, ? extends R> function2) {
        return rf().convert((Class) toType, (java.util.function.Function) function, (java.util.function.Function) function2);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectField
    public final <U> SelectField<U> convertFrom(Class<U> toType, java.util.function.Function<? super R, ? extends U> function) {
        return rf().convertFrom((Class) toType, (java.util.function.Function) function);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectField
    public final <U> SelectField<U> convertFrom(java.util.function.Function<? super R, ? extends U> function) {
        return rf().convertFrom((java.util.function.Function) function);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectField
    public final <U> SelectField<U> convertTo(Class<U> toType, java.util.function.Function<? super U, ? extends R> function) {
        return rf().convertTo((Class) toType, (java.util.function.Function) function);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.SelectField
    public final <U> SelectField<U> convertTo(java.util.function.Function<? super U, ? extends R> function) {
        return rf().convertTo((java.util.function.Function) function);
    }

    @Override // org.jooq.SelectField
    public final Field<R> as(String str) {
        return (Field<R>) rf().as(str);
    }

    @Override // org.jooq.SelectField
    public final Field<R> as(Name alias) {
        return rf().as(alias);
    }

    @Override // org.jooq.SelectField
    public final Field<R> as(Field<?> field) {
        return (Field<R>) rf().as(field);
    }

    @Override // org.jooq.Typed
    public final ContextConverter<?, R> getConverter() {
        return (ContextConverter<?, R>) rf().getConverter();
    }

    @Override // org.jooq.Typed
    public final Binding<?, R> getBinding() {
        return (Binding<?, R>) rf().getBinding();
    }

    @Override // org.jooq.Typed
    public final Class<R> getType() {
        return (Class<R>) rf().getType();
    }

    @Override // org.jooq.Typed
    public final DataType<R> getDataType() {
        return (DataType<R>) rf().getDataType();
    }

    @Override // org.jooq.Typed
    public final DataType<R> getDataType(Configuration configuration) {
        return (DataType<R>) rf().getDataType(configuration);
    }

    @Override // org.jooq.Named
    public final String getName() {
        return rf().getName();
    }

    @Override // org.jooq.Named
    public final Name getQualifiedName() {
        return rf().getQualifiedName();
    }

    @Override // org.jooq.Named
    public final Name getUnqualifiedName() {
        return rf().getUnqualifiedName();
    }

    @Override // org.jooq.Named
    public final String getComment() {
        return rf().getComment();
    }

    @Override // org.jooq.Named
    public final Comment getCommentPart() {
        return rf().getCommentPart();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final AbstractRow convertTo(Row row) {
        int size = this.fields.size();
        for (int i = 0; i < size; i++) {
            if (Tools.isVal(this.fields.field(i)) && !Tools.isVal(row.field(i))) {
                Field<?>[] result = new Field[size];
                for (int i2 = 0; i2 < size; i2++) {
                    Field<?> f = this.fields.field(i2);
                    Val<?> v = Tools.extractVal(f);
                    if (v != null) {
                        result[i2] = v.convertTo(row.field(i2).getDataType());
                    } else {
                        result[i2] = f;
                    }
                }
                return Tools.row0(result);
            }
        }
        return this;
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        ctx.sql("(").visit(QueryPartListView.wrap(this.fields.fields)).sql(")");
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Condition compare(Row arg1, Comparator comparator, Row arg2) {
        switch (comparator) {
            case EQUALS:
                return new RowEq(arg1, arg2);
            case GREATER:
                return new RowGt(arg1, arg2);
            case GREATER_OR_EQUAL:
                return new RowGe(arg1, arg2);
            case LESS:
                return new RowLt(arg1, arg2);
            case LESS_OR_EQUAL:
                return new RowLe(arg1, arg2);
            case NOT_EQUALS:
                return new RowNe(arg1, arg2);
            case IS_DISTINCT_FROM:
                return new RowIsDistinctFrom(arg1, arg2, false);
            case IS_NOT_DISTINCT_FROM:
                return new RowIsDistinctFrom(arg1, arg2, true);
            default:
                throw new IllegalArgumentException("Comparator not supported: " + String.valueOf(comparator));
        }
    }

    final Condition compare(Comparator comparator, Row row) {
        return compare(this, comparator, row);
    }

    @Override // org.jooq.Row
    public final int size() {
        return this.fields.size();
    }

    @Override // org.jooq.Fields
    public final Row fieldsRow() {
        return this;
    }

    @Override // org.jooq.Fields
    public final Stream<Field<?>> fieldStream() {
        return Stream.of((Object[]) fields());
    }

    @Override // org.jooq.Fields
    public final <T> Field<T> field(Field<T> field) {
        return this.fields.field(field);
    }

    @Override // org.jooq.Fields
    @Deprecated
    public final Field<?> field(String name) {
        return this.fields.field(name);
    }

    @Override // org.jooq.Fields
    @Deprecated
    public final <T> Field<T> field(String name, Class<T> type) {
        return this.fields.field(name, type);
    }

    @Override // org.jooq.Fields
    @Deprecated
    public final <T> Field<T> field(String name, DataType<T> dataType) {
        return this.fields.field(name, dataType);
    }

    @Override // org.jooq.Fields
    @Deprecated
    public final Field<?> field(Name name) {
        return this.fields.field(name);
    }

    @Override // org.jooq.Fields
    @Deprecated
    public final <T> Field<T> field(Name name, Class<T> type) {
        return this.fields.field(name, type);
    }

    @Override // org.jooq.Fields
    @Deprecated
    public final <T> Field<T> field(Name name, DataType<T> dataType) {
        return this.fields.field(name, dataType);
    }

    @Override // org.jooq.Fields
    public final Field<?> field(int index) {
        return this.fields.field(index);
    }

    @Override // org.jooq.Fields
    public final <T> Field<T> field(int index, Class<T> type) {
        return this.fields.field(index, type);
    }

    @Override // org.jooq.Fields
    public final <T> Field<T> field(int index, DataType<T> dataType) {
        return this.fields.field(index, dataType);
    }

    @Override // org.jooq.Fields
    public final Field<?>[] fields() {
        return this.fields.fields();
    }

    @Override // org.jooq.Fields
    public final Field<?>[] fields(Field<?>... f) {
        return this.fields.fields(f);
    }

    @Override // org.jooq.Fields
    public final Field<?>[] fields(String... fieldNames) {
        return this.fields.fields(fieldNames);
    }

    @Override // org.jooq.Fields
    public final Field<?>[] fields(Name... fieldNames) {
        return this.fields.fields(fieldNames);
    }

    @Override // org.jooq.Fields
    public final Field<?>[] fields(int... fieldIndexes) {
        return this.fields.fields(fieldIndexes);
    }

    @Override // org.jooq.Fields
    public final int indexOf(Field<?> field) {
        return this.fields.indexOf(field);
    }

    @Override // org.jooq.Fields
    public final int indexOf(String fieldName) {
        return this.fields.indexOf(fieldName);
    }

    @Override // org.jooq.Fields
    public final int indexOf(Name fieldName) {
        return this.fields.indexOf(fieldName);
    }

    @Override // org.jooq.Fields
    public final Class<?>[] types() {
        return this.fields.types();
    }

    @Override // org.jooq.Fields
    public final Class<?> type(int fieldIndex) {
        return this.fields.type(fieldIndex);
    }

    @Override // org.jooq.Fields
    public final Class<?> type(String fieldName) {
        return this.fields.type(fieldName);
    }

    @Override // org.jooq.Fields
    public final Class<?> type(Name fieldName) {
        return this.fields.type(fieldName);
    }

    @Override // org.jooq.Fields
    public final DataType<?>[] dataTypes() {
        return this.fields.dataTypes();
    }

    @Override // org.jooq.Fields
    public final DataType<?> dataType(int fieldIndex) {
        return this.fields.dataType(fieldIndex);
    }

    @Override // org.jooq.Fields
    public final DataType<?> dataType(String fieldName) {
        return this.fields.dataType(fieldName);
    }

    @Override // org.jooq.Fields
    public final DataType<?> dataType(Name fieldName) {
        return this.fields.dataType(fieldName);
    }

    @Override // org.jooq.Row
    public final Condition isNull() {
        return new RowIsNull(this);
    }

    @Override // org.jooq.Row
    public final Condition isNotNull() {
        return new RowIsNotNull(this);
    }

    @Override // org.jooq.Named
    public final Name $name() {
        return Names.N_ROW;
    }

    @Override // org.jooq.Typed
    public final DataType<R> $dataType() {
        return getDataType();
    }

    @Override // org.jooq.Row
    public final QOM.UnmodifiableList<? extends Field<?>> $fields() {
        return QOM.unmodifiable(fields());
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public int hashCode() {
        return this.fields.hashCode();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that instanceof AbstractRow) {
            AbstractRow<?> r = (AbstractRow) that;
            return this.fields.equals(r.fields);
        }
        return super.equals(that);
    }
}

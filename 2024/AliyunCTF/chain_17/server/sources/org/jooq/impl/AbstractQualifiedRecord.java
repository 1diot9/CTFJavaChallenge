package org.jooq.impl;

import java.sql.SQLException;
import java.sql.SQLInput;
import java.sql.SQLOutput;
import org.jooq.Converter;
import org.jooq.ExecuteContext;
import org.jooq.Field;
import org.jooq.QualifiedRecord;
import org.jooq.Record;
import org.jooq.RecordQualifier;
import org.jooq.Row;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractQualifiedRecord.class */
abstract class AbstractQualifiedRecord<R extends QualifiedRecord<R>> extends AbstractRecord implements QualifiedRecord<R> {
    private final RecordQualifier<R> qualifier;

    @Override // org.jooq.impl.AbstractRecord, org.jooq.Record
    public /* bridge */ /* synthetic */ Record with(Field field, Object obj, Converter converter) {
        return with(field, (Field) obj, (Converter<? extends T, ? super Field>) converter);
    }

    @Override // org.jooq.impl.AbstractRecord, org.jooq.Record
    public /* bridge */ /* synthetic */ Record with(Field field, Object obj) {
        return with((Field<Field>) field, (Field) obj);
    }

    public AbstractQualifiedRecord(RecordQualifier<R> qualifier) {
        super((AbstractRow<?>) qualifier.fieldsRow());
        this.qualifier = qualifier;
    }

    @Override // org.jooq.QualifiedRecord
    public final RecordQualifier<R> getQualifier() {
        return this.qualifier;
    }

    @Override // org.jooq.impl.AbstractRecord, org.jooq.Record
    public final <T> R with(Field<T> field, T value) {
        return (R) super.with((Field<Field<T>>) field, (Field<T>) value);
    }

    @Override // org.jooq.impl.AbstractRecord, org.jooq.Record
    public final <T, U> R with(Field<T> field, U value, Converter<? extends T, ? super U> converter) {
        return (R) super.with((Field) field, (Field<T>) value, (Converter<? extends T, ? super Field<T>>) converter);
    }

    @Override // org.jooq.Fields
    public Row fieldsRow() {
        return this.fields;
    }

    @Override // org.jooq.Record
    public Row valuesRow() {
        return Tools.row0(Tools.fieldsArray(intoArray(), this.fields.fields.fields()));
    }

    @Override // java.sql.SQLData
    public final String getSQLTypeName() throws SQLException {
        ExecuteContext ctx = DefaultExecuteContext.localExecuteContext();
        String result = Tools.getMappedUDTName(ctx, this);
        return result;
    }

    @Override // java.sql.SQLData
    public final void readSQL(SQLInput stream, String typeName) throws SQLException {
        ExecuteContext ctx = DefaultExecuteContext.localExecuteContext();
        Field[] f = getQualifier().fields();
        for (int i = 0; i < f.length; i++) {
            Field field = f[i];
            DefaultBindingGetSQLInputContext out = new DefaultBindingGetSQLInputContext(ctx, stream);
            field.getBinding().get(out);
            set(i, (Field<?>) field, out.value());
        }
    }

    @Override // java.sql.SQLData
    public final void writeSQL(SQLOutput stream) throws SQLException {
        ExecuteContext ctx = DefaultExecuteContext.localExecuteContext();
        Field<?>[] f = getQualifier().fields();
        for (int i = 0; i < f.length; i++) {
            f[i].getBinding().set(new DefaultBindingSetSQLOutputContext(ctx, stream, get(i)));
        }
    }
}

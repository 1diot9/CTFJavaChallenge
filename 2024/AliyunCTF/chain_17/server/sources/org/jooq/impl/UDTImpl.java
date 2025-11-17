package org.jooq.impl;

import org.jetbrains.annotations.ApiStatus;
import org.jooq.Binding;
import org.jooq.Catalog;
import org.jooq.Clause;
import org.jooq.Context;
import org.jooq.Converter;
import org.jooq.DataType;
import org.jooq.Name;
import org.jooq.Named;
import org.jooq.Package;
import org.jooq.Row;
import org.jooq.Schema;
import org.jooq.SelectField;
import org.jooq.UDT;
import org.jooq.UDTField;
import org.jooq.UDTRecord;
import org.jooq.impl.QOM;

@ApiStatus.Internal
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/UDTImpl.class */
public class UDTImpl<R extends UDTRecord<R>> extends AbstractNamed implements UDT<R>, FieldsTrait, QOM.UNotYetImplemented {
    private final Schema schema;
    private final FieldsImpl<R> fields;
    private final Package pkg;
    private final boolean synthetic;
    private transient DataType<R> type;

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public /* bridge */ /* synthetic */ boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public /* bridge */ /* synthetic */ int hashCode() {
        return super.hashCode();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public /* bridge */ /* synthetic */ String toString() {
        return super.toString();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public /* bridge */ /* synthetic */ boolean generatesCast() {
        return super.generatesCast();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public /* bridge */ /* synthetic */ boolean declaresCTE() {
        return super.declaresCTE();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public /* bridge */ /* synthetic */ boolean declaresWindows() {
        return super.declaresWindows();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public /* bridge */ /* synthetic */ boolean declaresTables() {
        return super.declaresTables();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public /* bridge */ /* synthetic */ boolean declaresFields() {
        return super.declaresFields();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public /* bridge */ /* synthetic */ boolean rendersContent(Context context) {
        return super.rendersContent(context);
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    @Deprecated
    public /* bridge */ /* synthetic */ Clause[] clauses(Context context) {
        return super.clauses(context);
    }

    @Deprecated
    public UDTImpl(String name, Schema schema) {
        this(DSL.name(name), schema);
    }

    @Deprecated
    public UDTImpl(String name, Schema schema, Package pkg) {
        this(DSL.name(name), schema, pkg);
    }

    @Deprecated
    public UDTImpl(String name, Schema schema, Package pkg, boolean synthetic) {
        this(DSL.name(name), schema, pkg, synthetic);
    }

    public UDTImpl(Name name, Schema schema) {
        this(name, schema, (Package) null);
    }

    public UDTImpl(Name name, Schema schema, Package pkg) {
        this(name, schema, pkg, false);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public UDTImpl(Name name, Schema schema, Package r8, boolean synthetic) {
        super(qualify(r8 != 0 ? r8 : schema, name), CommentImpl.NO_COMMENT);
        this.fields = new FieldsImpl<>((SelectField<?>[]) new SelectField[0]);
        this.schema = schema;
        this.pkg = r8;
        this.synthetic = synthetic;
    }

    @Override // org.jooq.Qualified
    public final Catalog getCatalog() {
        if (getSchema() == null) {
            return null;
        }
        return getSchema().getCatalog();
    }

    @Override // org.jooq.Qualified
    public Schema getSchema() {
        return this.schema;
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.Named
    public Name getQualifiedName() {
        Named q = getPackage() != null ? getPackage() : getSchema();
        return q == null ? super.getQualifiedName() : q.getQualifiedName().append(getUnqualifiedName());
    }

    @Override // org.jooq.RecordQualifier
    public final Package getPackage() {
        return this.pkg;
    }

    @Override // org.jooq.Fields
    public final Row fieldsRow() {
        return Tools.row0(this.fields);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final FieldsImpl<R> fields0() {
        return this.fields;
    }

    @Override // org.jooq.RecordQualifier
    public Class<R> getRecordType() {
        throw new UnsupportedOperationException();
    }

    @Override // org.jooq.UDT
    public final boolean isSQLUsable() {
        return true;
    }

    @Override // org.jooq.UDT
    public final boolean isSynthetic() {
        return this.synthetic;
    }

    @Override // org.jooq.RecordQualifier
    public final R newRecord() {
        return (R) DSL.using(new DefaultConfiguration()).newRecord(this);
    }

    @Override // org.jooq.RecordQualifier, org.jooq.Typed
    public final DataType<R> getDataType() {
        if (this.type == null) {
            this.type = new UDTDataType(this);
        }
        return this.type;
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        QualifiedImpl.acceptMappedSchemaPrefix(ctx, getSchema());
        ctx.visit(DSL.name(getName()));
    }

    @Deprecated
    protected static final <R extends UDTRecord<R>, T> UDTField<R, T> createField(String name, DataType<T> type, UDT<R> udt) {
        return createField(DSL.name(name), type, udt, "", (Converter) null, (Binding) null);
    }

    @Deprecated
    protected static final <R extends UDTRecord<R>, T> UDTField<R, T> createField(String name, DataType<T> type, UDT<R> udt, String comment) {
        return createField(DSL.name(name), type, udt, comment, (Converter) null, (Binding) null);
    }

    @Deprecated
    protected static final <R extends UDTRecord<R>, T, U> UDTField<R, U> createField(String name, DataType<T> type, UDT<R> udt, String comment, Converter<T, U> converter) {
        return createField(DSL.name(name), type, udt, comment, converter, (Binding) null);
    }

    @Deprecated
    protected static final <R extends UDTRecord<R>, T, U> UDTField<R, U> createField(String name, DataType<T> type, UDT<R> udt, String comment, Binding<T, U> binding) {
        return createField(DSL.name(name), type, udt, comment, (Converter) null, binding);
    }

    @Deprecated
    protected static final <R extends UDTRecord<R>, T, X, U> UDTField<R, U> createField(String name, DataType<T> type, UDT<R> udt, String comment, Converter<X, U> converter, Binding<T, X> binding) {
        return createField(DSL.name(name), type, udt, comment, converter, binding);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static final <R extends UDTRecord<R>, T> UDTField<R, T> createField(Name name, DataType<T> type, UDT<R> udt) {
        return createField(name, type, udt, "", (Converter) null, (Binding) null);
    }

    protected static final <R extends UDTRecord<R>, T> UDTField<R, T> createField(Name name, DataType<T> type, UDT<R> udt, String comment) {
        return createField(name, type, udt, comment, (Converter) null, (Binding) null);
    }

    protected static final <R extends UDTRecord<R>, T, U> UDTField<R, U> createField(Name name, DataType<T> type, UDT<R> udt, String comment, Converter<T, U> converter) {
        return createField(name, type, udt, comment, converter, (Binding) null);
    }

    protected static final <R extends UDTRecord<R>, T, U> UDTField<R, U> createField(Name name, DataType<T> type, UDT<R> udt, String comment, Binding<T, U> binding) {
        return createField(name, type, udt, comment, (Converter) null, binding);
    }

    protected static final <R extends UDTRecord<R>, T, X, U> UDTField<R, U> createField(Name name, DataType<T> type, UDT<R> udt, String comment, Converter<X, U> converter, Binding<T, X> binding) {
        DataType<T> asConvertedDataType;
        Binding<? super T, U> newBinding = DefaultBinding.newBinding(converter, type, binding);
        if (converter == null && binding == null) {
            asConvertedDataType = type;
        } else {
            asConvertedDataType = type.asConvertedDataType(newBinding);
        }
        UDTFieldImpl<R, U> udtField = new UDTFieldImpl<>(name, asConvertedDataType, udt, DSL.comment(comment), newBinding);
        return udtField;
    }

    @Override // org.jooq.Qualified
    public final Schema $schema() {
        return this.schema;
    }
}

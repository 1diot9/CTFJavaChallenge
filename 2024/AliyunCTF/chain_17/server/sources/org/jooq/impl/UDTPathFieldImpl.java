package org.jooq.impl;

import org.jetbrains.annotations.ApiStatus;
import org.jooq.Binding;
import org.jooq.Catalog;
import org.jooq.Clause;
import org.jooq.Comment;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.Package;
import org.jooq.Record;
import org.jooq.RecordQualifier;
import org.jooq.Row;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UDT;
import org.jooq.UDTPathField;
import org.jooq.UDTRecord;
import org.jooq.impl.QOM;
import org.jooq.impl.Tools;
import org.jooq.tools.StringUtils;

@ApiStatus.Internal
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/UDTPathFieldImpl.class */
public class UDTPathFieldImpl<R extends Record, U extends UDTRecord<U>, T> extends AbstractField<T> implements UDTPathField<R, U, T>, SimpleQueryPart, TypedReference<T>, ScopeMappable, QOM.UEmpty {
    private final RecordQualifier<R> qualifier;
    private final UDT<U> udt;

    @Override // org.jooq.impl.AbstractField, org.jooq.Field, org.jooq.SelectField
    public /* bridge */ /* synthetic */ Field as(Name name) {
        return super.as(name);
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.impl.QOM.Aliasable
    public /* bridge */ /* synthetic */ Field $aliased() {
        return super.$aliased();
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.impl.QOM.Aliasable
    public /* bridge */ /* synthetic */ Name $alias() {
        return super.$alias();
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.Named
    public /* bridge */ /* synthetic */ Name getQualifiedName() {
        return super.getQualifiedName();
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
    public /* bridge */ /* synthetic */ boolean rendersContent(Context context) {
        return super.rendersContent(context);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public UDTPathFieldImpl(Name name, DataType<T> type, RecordQualifier<R> qualifier, UDT<U> udt, Comment comment, Binding<?, T> binding) {
        super(qualify(qualifier, name), type, comment, binding);
        this.qualifier = qualifier;
        this.udt = udt;
        qualifier.$name();
    }

    @Override // org.jooq.UDTPathField
    public final RecordQualifier<R> getQualifier() {
        return this.qualifier;
    }

    @Override // org.jooq.UDTPathField
    public final RecordQualifier<U> asQualifier() {
        return new UDTPathFieldImplAsQualifier();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/UDTPathFieldImpl$UDTPathFieldImplAsQualifier.class */
    public class UDTPathFieldImplAsQualifier extends AbstractNamed implements RecordQualifier<U>, FieldsTrait, QOM.UNotYetImplemented {
        UDTPathFieldImplAsQualifier() {
            super(UDTPathFieldImpl.this.getQualifiedName(), UDTPathFieldImpl.this.getCommentPart());
        }

        RecordQualifier<R> getQualifier() {
            return UDTPathFieldImpl.this.getQualifier();
        }

        @Override // org.jooq.Qualified
        public final Catalog getCatalog() {
            return null;
        }

        @Override // org.jooq.Qualified
        public final Schema getSchema() {
            return null;
        }

        @Override // org.jooq.Qualified
        public final Schema $schema() {
            return null;
        }

        @Override // org.jooq.Fields
        public final Row fieldsRow() {
            return UDTPathFieldImpl.this.getUDT().fieldsRow();
        }

        @Override // org.jooq.RecordQualifier
        public final Package getPackage() {
            return UDTPathFieldImpl.this.getUDT().getPackage();
        }

        @Override // org.jooq.RecordQualifier
        public final Class<? extends U> getRecordType() {
            return (Class<? extends U>) UDTPathFieldImpl.this.getUDT().getRecordType();
        }

        @Override // org.jooq.RecordQualifier, org.jooq.Typed
        public final DataType<U> getDataType() {
            return (DataType<U>) UDTPathFieldImpl.this.getUDT().getDataType();
        }

        @Override // org.jooq.RecordQualifier
        public final U newRecord() {
            return (U) UDTPathFieldImpl.this.getUDT().newRecord();
        }

        @Override // org.jooq.QueryPartInternal
        public final void accept(Context<?> ctx) {
            UDTPathFieldImpl.this.accept(ctx);
        }
    }

    @Override // org.jooq.UDTField
    public final UDT<U> getUDT() {
        return this.udt;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean parenthesised(Context<?> ctx) {
        return true;
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public boolean declaresFields() {
        return super.declaresFields();
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return null;
    }

    /* JADX WARN: Type inference failed for: r0v20, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v7, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        RecordQualifier<R> q = getQualifier();
        if (!Boolean.TRUE.equals(ctx.data(Tools.BooleanDataKey.DATA_STORE_ASSIGNMENT)) && (q instanceof UDTPathFieldImplAsQualifier) && (((UDTPathFieldImplAsQualifier) q).getQualifier() instanceof Table)) {
            ctx.sql('(').visit(q).sql(").").visit(getUnqualifiedName());
        } else if (q instanceof Table) {
            Table<?> t = (Table) q;
            TableFieldImpl.accept2(ctx, t, getUnqualifiedName());
        } else {
            ctx.visit(q).sql('.').visit(getUnqualifiedName());
        }
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public int hashCode() {
        if (getQualifier() instanceof Table) {
            return ((Schema) StringUtils.defaultIfNull(getQualifier().getSchema(), SchemaImpl.DEFAULT_SCHEMA.get())).getQualifiedName().append(getQualifier().getUnqualifiedName()).append(getUnqualifiedName()).hashCode();
        }
        return ((Schema) StringUtils.defaultIfNull(getQualifier().getSchema(), SchemaImpl.DEFAULT_SCHEMA.get())).getQualifiedName().append(getQualifier().getUnqualifiedName()).append(getUDT().getUnqualifiedName()).append(getUnqualifiedName()).hashCode();
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if ((getQualifier() instanceof Table) && (that instanceof TableField)) {
            TableField<?, ?> other = (TableField) that;
            return StringUtils.equals(getQualifier(), other.getTable()) && StringUtils.equals(getName(), other.getName());
        }
        if (!(that instanceof UDTPathField)) {
            return super.equals(that);
        }
        UDTPathField<?, ?, ?> other2 = (UDTPathField) that;
        return StringUtils.equals(getQualifier(), other2.getQualifier()) && StringUtils.equals(getUDT(), other2.getUDT()) && StringUtils.equals(getName(), other2.getName());
    }
}

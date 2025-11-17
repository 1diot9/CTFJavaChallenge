package org.jooq.impl;

import org.jooq.Record;
import org.jooq.Row;
import org.jooq.UDT;
import org.jooq.UDTRecord;

@org.jooq.Internal
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/UDTRecordImpl.class */
public class UDTRecordImpl<R extends UDTRecord<R>> extends AbstractQualifiedRecord<R> implements UDTRecord<R> {
    @Override // org.jooq.impl.AbstractQualifiedRecord, org.jooq.Record
    public /* bridge */ /* synthetic */ Row valuesRow() {
        return super.valuesRow();
    }

    @Override // org.jooq.impl.AbstractQualifiedRecord, org.jooq.Fields
    public /* bridge */ /* synthetic */ Row fieldsRow() {
        return super.fieldsRow();
    }

    @Override // org.jooq.impl.AbstractRecord, org.jooq.Record
    public /* bridge */ /* synthetic */ int compareTo(Record record) {
        return super.compareTo(record);
    }

    @Override // org.jooq.impl.AbstractRecord, org.jooq.Record
    public /* bridge */ /* synthetic */ Object into(Object obj) {
        return super.into((UDTRecordImpl<R>) obj);
    }

    @Override // org.jooq.impl.AbstractRecord, org.jooq.Record
    public /* bridge */ /* synthetic */ Record original() {
        return super.original();
    }

    @Override // org.jooq.impl.AbstractStore, org.jooq.Record
    public /* bridge */ /* synthetic */ boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override // org.jooq.impl.AbstractStore, org.jooq.Record
    public /* bridge */ /* synthetic */ int hashCode() {
        return super.hashCode();
    }

    public UDTRecordImpl(UDT<R> udt) {
        super(udt);
    }

    @Override // org.jooq.UDTRecord
    public final UDT<R> getUDT() {
        return (UDT) getQualifier();
    }

    @Override // org.jooq.impl.AbstractRecord
    public String toString() {
        return DSL.using(configuration()).renderInlined(DSL.inline(this));
    }
}

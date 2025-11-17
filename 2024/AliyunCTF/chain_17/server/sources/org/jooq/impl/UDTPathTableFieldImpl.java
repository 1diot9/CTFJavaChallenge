package org.jooq.impl;

import org.jetbrains.annotations.ApiStatus;
import org.jooq.Binding;
import org.jooq.Comment;
import org.jooq.DataType;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.RecordQualifier;
import org.jooq.Table;
import org.jooq.UDT;
import org.jooq.UDTPathTableField;
import org.jooq.UDTRecord;

@ApiStatus.Internal
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/UDTPathTableFieldImpl.class */
public class UDTPathTableFieldImpl<R extends Record, U extends UDTRecord<U>, T> extends UDTPathFieldImpl<R, U, T> implements UDTPathTableField<R, U, T> {
    public UDTPathTableFieldImpl(Name name, DataType<T> type, RecordQualifier<R> qualifier, UDT<U> udt, Comment comment, Binding<?, T> binding) {
        super(name, type, qualifier, udt, comment, binding);
    }

    @Override // org.jooq.TableField
    public final Table<R> getTable() {
        RecordQualifier<R> qualifier = getQualifier();
        if (!(qualifier instanceof Table)) {
            return null;
        }
        Table<R> t = (Table) qualifier;
        return t;
    }
}

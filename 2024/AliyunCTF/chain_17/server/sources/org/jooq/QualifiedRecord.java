package org.jooq;

import java.sql.SQLData;
import org.jetbrains.annotations.NotNull;
import org.jooq.QualifiedRecord;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/QualifiedRecord.class */
public interface QualifiedRecord<R extends QualifiedRecord<R>> extends Record, SQLData {
    @NotNull
    RecordQualifier<R> getQualifier();

    @Override // org.jooq.Record
    @NotNull
    <T> R with(Field<T> field, T t);

    @Override // org.jooq.Record
    @NotNull
    <T, U> R with(Field<T> field, U u, Converter<? extends T, ? super U> converter);

    @Override // org.jooq.Record
    @NotNull
    /* bridge */ /* synthetic */ default Record with(Field field, Object obj, Converter converter) {
        return with(field, (Field) obj, (Converter<? extends T, ? super Field>) converter);
    }

    @Override // org.jooq.Record
    @NotNull
    /* bridge */ /* synthetic */ default Record with(Field field, Object obj) {
        return with((Field<Field>) field, (Field) obj);
    }
}

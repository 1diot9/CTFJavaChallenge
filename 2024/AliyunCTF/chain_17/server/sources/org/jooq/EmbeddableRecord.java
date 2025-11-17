package org.jooq;

import org.jooq.EmbeddableRecord;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/EmbeddableRecord.class */
public interface EmbeddableRecord<R extends EmbeddableRecord<R>> extends Record {
    @Override // org.jooq.Record
    R original();

    @Override // org.jooq.Record
    <T> R with(Field<T> field, T t);

    @Override // org.jooq.Record
    <T, U> R with(Field<T> field, U u, Converter<? extends T, ? super U> converter);

    @Override // org.jooq.Record
    /* bridge */ /* synthetic */ default Record with(Field field, Object obj, Converter converter) {
        return with(field, (Field) obj, (Converter<? extends T, ? super Field>) converter);
    }

    @Override // org.jooq.Record
    /* bridge */ /* synthetic */ default Record with(Field field, Object obj) {
        return with((Field<Field>) field, (Field) obj);
    }
}

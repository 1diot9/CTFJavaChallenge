package org.jooq.impl;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.jooq.CharacterSet;
import org.jooq.Collation;
import org.jooq.Field;
import org.jooq.Generator;
import org.jooq.Nullability;
import org.jooq.Record;
import org.jooq.Row;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/RecordDataType.class */
public final class RecordDataType<R extends Record> extends DefaultDataType<R> {
    final AbstractRow<R> row;

    /* JADX INFO: Access modifiers changed from: package-private */
    public RecordDataType(Row row) {
        this(row, Tools.recordType(row.size()), "record");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RecordDataType(Row row, Class<R> recordType, String name) {
        super((SQLDialect) null, recordType, name, nullability(row));
        this.row = (AbstractRow) row;
    }

    static final Nullability nullability(Row row) {
        if (Tools.anyMatch(row.fields(), f -> {
            return f.getDataType().nullable();
        })) {
            return Nullability.NULL;
        }
        return Nullability.NOT_NULL;
    }

    RecordDataType(DefaultDataType<R> t, AbstractRow<R> row, Integer precision, Integer scale, Integer length, Nullability nullability, boolean readonly, Generator<?, ?, R> generatedAlwaysAs, QOM.GenerationOption generationOption, QOM.GenerationLocation generationLocation, Collation collation, CharacterSet characterSet, boolean identity, Field<R> defaultValue) {
        super(t, precision, scale, length, nullability, readonly, generatedAlwaysAs, generationOption, generationLocation, collation, characterSet, identity, defaultValue);
        this.row = row;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.DefaultDataType, org.jooq.impl.AbstractDataTypeX
    public DefaultDataType<R> construct(Integer newPrecision, Integer newScale, Integer newLength, Nullability newNullability, boolean newReadonly, Generator<?, ?, R> newGeneratedAlwaysAs, QOM.GenerationOption newGenerationOption, QOM.GenerationLocation newGenerationLocation, Collation newCollation, CharacterSet newCharacterSet, boolean newIdentity, Field<R> newDefaultValue) {
        return new RecordDataType(this, this.row, newPrecision, newScale, newLength, newNullability, newReadonly, newGeneratedAlwaysAs, newGenerationOption, newGenerationLocation, newCollation, newCharacterSet, newIdentity, newDefaultValue);
    }

    @Override // org.jooq.impl.DefaultDataType, org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final Row getRow() {
        return this.row;
    }

    @Override // org.jooq.impl.DefaultDataType, org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final Class<? extends R> getRecordType() {
        return (Class<? extends R>) getType();
    }

    @Override // org.jooq.impl.DefaultDataType, org.jooq.impl.AbstractDataType, org.jooq.DataType
    public R convert(Object obj) {
        if ((obj instanceof Record) && ((Record) obj).fieldsRow().equals(this.row)) {
            return (R) obj;
        }
        if ((obj instanceof Record) || (obj instanceof Map) || (obj instanceof List)) {
            return (R) Tools.newRecord(true, getRecordType(), this.row, Tools.CONFIG.get()).operate(r -> {
                if (obj instanceof Record) {
                    ((AbstractRecord) r).fromArray(((Record) obj).intoArray());
                } else if (obj instanceof Map) {
                    r.from(((Map) obj).entrySet().stream().sorted(Comparator.comparing((v0) -> {
                        return v0.getKey();
                    })).map((v0) -> {
                        return v0.getValue();
                    }).collect(Collectors.toList()));
                } else {
                    r.from(obj);
                }
                return r;
            });
        }
        return (R) super.convert(obj);
    }
}

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
import org.jooq.Result;
import org.jooq.Row;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/MultisetDataType.class */
public final class MultisetDataType<R extends Record> extends DefaultDataType<Result<R>> {
    final AbstractRow<R> row;
    final Class<? extends R> recordType;

    /* JADX INFO: Access modifiers changed from: package-private */
    public MultisetDataType(AbstractRow<R> row, Class<? extends R> recordType) {
        super((SQLDialect) null, Result.class, "multiset", "multiset");
        this.row = row;
        this.recordType = recordType != null ? recordType : Record.class;
    }

    MultisetDataType(DefaultDataType<Result<R>> t, AbstractRow<R> row, Class<? extends R> recordType, Integer precision, Integer scale, Integer length, Nullability nullability, boolean readonly, Generator<?, ?, Result<R>> generatedAlwaysAs, QOM.GenerationOption generationOption, QOM.GenerationLocation generationLocation, Collation collation, CharacterSet characterSet, boolean identity, Field<Result<R>> defaultValue) {
        super(t, precision, scale, length, nullability, readonly, generatedAlwaysAs, generationOption, generationLocation, collation, characterSet, identity, defaultValue);
        this.row = row;
        this.recordType = recordType;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.DefaultDataType, org.jooq.impl.AbstractDataTypeX
    public DefaultDataType<Result<R>> construct(Integer newPrecision, Integer newScale, Integer newLength, Nullability newNullability, boolean newReadonly, Generator<?, ?, Result<R>> newGeneratedAlwaysAs, QOM.GenerationOption newGenerationOption, QOM.GenerationLocation newGenerationLocation, Collation newCollation, CharacterSet newCharacterSet, boolean newIdentity, Field<Result<R>> newDefaultValue) {
        return new MultisetDataType(this, this.row, this.recordType, newPrecision, newScale, newLength, newNullability, newReadonly, newGeneratedAlwaysAs, newGenerationOption, newGenerationLocation, newCollation, newCharacterSet, newIdentity, newDefaultValue);
    }

    @Override // org.jooq.impl.DefaultDataType, org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final Row getRow() {
        return this.row;
    }

    @Override // org.jooq.impl.DefaultDataType, org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final Class<? extends R> getRecordType() {
        return this.recordType;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.DefaultDataType, org.jooq.impl.AbstractDataType, org.jooq.DataType
    public Result<R> convert(Object obj) {
        if ((obj instanceof Result) && ((Result) obj).fieldsRow().equals(this.row)) {
            return (Result) obj;
        }
        if (obj instanceof List) {
            ResultImpl resultImpl = new ResultImpl(Tools.CONFIG.get(), this.row);
            for (Object obj2 : (List) obj) {
                resultImpl.add((ResultImpl) Tools.newRecord(true, this.recordType, this.row, Tools.CONFIG.get()).operate(r -> {
                    if (obj2 instanceof Record) {
                        ((AbstractRecord) r).fromArray(((Record) obj2).intoArray());
                    } else if (obj2 instanceof Map) {
                        r.from(((Map) obj2).entrySet().stream().sorted(Comparator.comparing((v0) -> {
                            return v0.getKey();
                        })).map((v0) -> {
                            return v0.getValue();
                        }).collect(Collectors.toList()));
                    } else {
                        r.from(obj2);
                    }
                    return r;
                }));
            }
            return resultImpl;
        }
        if (obj == null) {
            return new ResultImpl(Tools.CONFIG.get(), this.row);
        }
        return (Result) super.convert(obj);
    }
}

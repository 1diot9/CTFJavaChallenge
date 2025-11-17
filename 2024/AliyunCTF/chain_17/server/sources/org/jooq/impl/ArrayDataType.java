package org.jooq.impl;

import org.jooq.CharacterSet;
import org.jooq.Collation;
import org.jooq.Configuration;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Generator;
import org.jooq.Nullability;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ArrayDataType.class */
public final class ArrayDataType<T> extends DefaultDataType<T[]> {
    final DataType<T> elementType;

    public ArrayDataType(DataType<T> elementType) {
        super((SQLDialect) null, elementType.getArrayType(), elementType.getTypeName() + " array", elementType.getCastTypeName() + " array");
        this.elementType = elementType;
    }

    ArrayDataType(AbstractDataType<T[]> t, DataType<T> elementType, Integer precision, Integer scale, Integer length, Nullability nullability, boolean readonly, Generator<?, ?, T[]> generatedAlwaysAs, QOM.GenerationOption generationOption, QOM.GenerationLocation generationLocation, Collation collation, CharacterSet characterSet, boolean identity, Field<T[]> defaultValue) {
        super(t, precision, scale, length, nullability, readonly, generatedAlwaysAs, generationOption, generationLocation, collation, characterSet, identity, defaultValue);
        this.elementType = elementType;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.DefaultDataType, org.jooq.impl.AbstractDataTypeX
    public DefaultDataType<T[]> construct(Integer newPrecision, Integer newScale, Integer newLength, Nullability newNullability, boolean newReadonly, Generator<?, ?, T[]> newGeneratedAlwaysAs, QOM.GenerationOption newGenerationOption, QOM.GenerationLocation newGenerationLocation, Collation newCollation, CharacterSet newCharacterSet, boolean newIdentity, Field<T[]> newDefaultValue) {
        return new ArrayDataType(this, (AbstractDataType) this.elementType, newPrecision, newScale, newLength, newNullability, newReadonly, newGeneratedAlwaysAs, newGenerationOption, newGenerationLocation, newCollation, newCharacterSet, newIdentity, newDefaultValue);
    }

    @Override // org.jooq.impl.DefaultDataType, org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final String getTypeName() {
        return getTypeName(Tools.CONFIG.get());
    }

    @Override // org.jooq.impl.DefaultDataType, org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final String getTypeName(Configuration configuration) {
        String typeName = this.elementType.getTypeName(configuration);
        return getArrayType(configuration, typeName);
    }

    @Override // org.jooq.impl.DefaultDataType, org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final String getCastTypeName() {
        return getCastTypeName(Tools.CONFIG.get());
    }

    @Override // org.jooq.impl.DefaultDataType, org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final String getCastTypeName(Configuration configuration) {
        String castTypeName = this.elementType.getCastTypeName(configuration);
        return getArrayType(configuration, castTypeName);
    }

    @Override // org.jooq.impl.DefaultDataType, org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final Class<?> getArrayComponentType() {
        return this.elementType.getType();
    }

    @Override // org.jooq.impl.DefaultDataType, org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final DataType<?> getArrayComponentDataType() {
        return this.elementType;
    }

    @Override // org.jooq.impl.DefaultDataType, org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final Class<?> getArrayBaseType() {
        return getArrayBaseDataType().getType();
    }

    @Override // org.jooq.impl.DefaultDataType, org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final DataType<?> getArrayBaseDataType() {
        DataType<?> dataType = this;
        while (true) {
            DataType<?> result = dataType;
            DataType<?> t = result.getArrayComponentDataType();
            if (t != null) {
                dataType = t;
            } else {
                return result;
            }
        }
    }

    private static String getArrayType(Configuration configuration, String dataType) {
        switch (configuration.family()) {
            case POSTGRES:
            case YUGABYTEDB:
                return dataType + "[]";
            case H2:
                return dataType + " array";
            default:
                return dataType + " array";
        }
    }
}

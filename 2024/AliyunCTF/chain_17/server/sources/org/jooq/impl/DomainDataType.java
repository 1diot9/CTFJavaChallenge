package org.jooq.impl;

import org.jooq.CharacterSet;
import org.jooq.Collation;
import org.jooq.Configuration;
import org.jooq.DataType;
import org.jooq.Domain;
import org.jooq.Field;
import org.jooq.Generator;
import org.jooq.Nullability;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DomainDataType.class */
public final class DomainDataType<T> extends DefaultDataType<T> {
    private final Domain<T> domain;
    private final DataType<T> baseType;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DomainDataType(Domain<T> domain, DataType<T> baseType) {
        super(null, baseType.getSQLDataType(), baseType.getType(), baseType.getBinding(), baseType.getQualifiedName(), baseType.getTypeName(), baseType.getCastTypeName(), baseType.precisionDefined() ? Integer.valueOf(baseType.precision()) : null, baseType.scaleDefined() ? Integer.valueOf(baseType.scale()) : null, baseType.lengthDefined() ? Integer.valueOf(baseType.length()) : null, baseType.nullability(), baseType.readonly(), baseType.generatedAlwaysAsGenerator(), baseType.generationOption(), baseType.generationLocation(), null, null, false, baseType.default_());
        this.domain = domain;
        this.baseType = baseType;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.DefaultDataType, org.jooq.impl.AbstractDataTypeX
    public DefaultDataType<T> construct(Integer newPrecision, Integer newScale, Integer newLength, Nullability newNullability, boolean newReadonly, Generator<?, ?, T> newGeneratedAlwaysAs, QOM.GenerationOption newGenerationOption, QOM.GenerationLocation newGenerationLocation, Collation newCollation, CharacterSet newCharacterSet, boolean newIdentity, Field<T> newDefaultValue) {
        return new DomainDataType(this.domain, ((AbstractDataTypeX) this.baseType).construct(newPrecision, newScale, newLength, newNullability, newReadonly, newGeneratedAlwaysAs, newGenerationOption, newGenerationLocation, newCollation, newCharacterSet, newIdentity, newDefaultValue));
    }

    @Override // org.jooq.impl.DefaultDataType, org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final Domain<T> getDomain() {
        return this.domain;
    }

    @Override // org.jooq.impl.DefaultDataType, org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final int getSQLType() {
        return this.baseType.getSQLType();
    }

    @Override // org.jooq.impl.DefaultDataType, org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final String getTypeName(Configuration configuration) {
        return this.baseType.getTypeName(configuration);
    }

    @Override // org.jooq.impl.DefaultDataType, org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final String getCastTypeName(Configuration configuration) {
        return this.baseType.getCastTypeName(configuration);
    }
}

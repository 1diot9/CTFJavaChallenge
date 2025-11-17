package org.jooq.impl;

import org.jooq.Binding;
import org.jooq.CharacterSet;
import org.jooq.Collation;
import org.jooq.Configuration;
import org.jooq.Converter;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Generator;
import org.jooq.Nullability;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
@Deprecated
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/LegacyConvertedDataType.class */
public final class LegacyConvertedDataType<T, U> extends DefaultDataType<U> {
    private final AbstractDataTypeX<T> delegate;

    /* JADX INFO: Access modifiers changed from: package-private */
    public LegacyConvertedDataType(AbstractDataTypeX<T> delegate, Binding<? super T, U> binding) {
        super((SQLDialect) null, binding.converter().toType(), binding, delegate.getQualifiedName(), delegate.getTypeName(), delegate.getCastTypeName(), delegate.precisionDefined() ? Integer.valueOf(delegate.precision()) : null, delegate.scaleDefined() ? Integer.valueOf(delegate.scale()) : null, delegate.lengthDefined() ? Integer.valueOf(delegate.length()) : null, delegate.nullability(), delegate.defaultValue());
        this.delegate = delegate;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.DefaultDataType, org.jooq.impl.AbstractDataTypeX
    public DefaultDataType<U> construct(Integer newPrecision, Integer newScale, Integer newLength, Nullability newNullability, boolean newReadonly, Generator<?, ?, U> newGeneratedAlwaysAs, QOM.GenerationOption newGenerationOption, QOM.GenerationLocation newGenerationLocation, Collation newCollation, CharacterSet newCharacterSet, boolean newIdentity, Field<U> newDefaultValue) {
        return new LegacyConvertedDataType(this.delegate.construct(newPrecision, newScale, newLength, newNullability, newReadonly, newGeneratedAlwaysAs, newGenerationOption, newGenerationLocation, newCollation, newCharacterSet, newIdentity, newDefaultValue), getBinding());
    }

    @Override // org.jooq.impl.DefaultDataType, org.jooq.impl.AbstractDataType, org.jooq.DataType
    public int getSQLType() {
        return this.delegate.getSQLType();
    }

    @Override // org.jooq.impl.DefaultDataType, org.jooq.impl.AbstractDataType, org.jooq.DataType
    public String getTypeName(Configuration configuration) {
        return this.delegate.getTypeName(configuration);
    }

    @Override // org.jooq.impl.DefaultDataType, org.jooq.impl.AbstractDataType, org.jooq.DataType
    public String getCastTypeName(Configuration configuration) {
        return this.delegate.getCastTypeName(configuration);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.DefaultDataType, org.jooq.impl.AbstractDataType, org.jooq.DataType
    public U convert(Object obj) {
        if (getConverter().toType().isInstance(obj)) {
            return obj;
        }
        return getConverter().from(this.delegate.convert(obj), Internal.converterContext());
    }

    @Override // org.jooq.impl.DefaultDataType, org.jooq.impl.AbstractDataType, org.jooq.DataType
    public <X> DataType<X> asConvertedDataType(Converter<? super U, X> converter) {
        return super.asConvertedDataType(new ChainedConverterBinding(getBinding(), converter));
    }
}

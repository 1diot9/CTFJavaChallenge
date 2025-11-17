package org.jooq.impl;

import java.util.List;
import java.util.Map;
import org.jooq.Binding;
import org.jooq.CharacterSet;
import org.jooq.Collation;
import org.jooq.Configuration;
import org.jooq.Converter;
import org.jooq.Converters;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Generator;
import org.jooq.Nullability;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.Row;
import org.jooq.SQLDialect;
import org.jooq.exception.DataTypeException;
import org.jooq.impl.DefaultBinding;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ConvertedDataType.class */
public final class ConvertedDataType<T, U> extends AbstractDataTypeX<U> {
    final AbstractDataTypeX<T> delegate;
    final Binding<? super T, U> binding;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConvertedDataType(AbstractDataTypeX<T> delegate, Binding<? super T, U> binding) {
        super(delegate.getQualifiedName(), delegate.getCommentPart());
        this.delegate = delegate;
        this.binding = binding;
        new LegacyConvertedDataType(delegate, binding);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractDataTypeX
    public AbstractDataTypeX<U> construct(Integer newPrecision, Integer newScale, Integer newLength, Nullability newNullability, boolean newReadonly, Generator<?, ?, U> newGeneratedAlwaysAs, QOM.GenerationOption newGenerationOption, QOM.GenerationLocation newGenerationLocation, Collation newCollation, CharacterSet newCharacterSet, boolean newIdentity, Field<U> newDefaultValue) {
        return (AbstractDataTypeX) this.delegate.construct(newPrecision, newScale, newLength, newNullability, newReadonly, newGeneratedAlwaysAs, newGenerationOption, newGenerationLocation, newCollation, newCharacterSet, newIdentity, newDefaultValue).asConvertedDataType(this.binding);
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final Row getRow() {
        return this.delegate.getRow();
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final Class<? extends Record> getRecordType() {
        return this.delegate.getRecordType();
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final boolean isRecord() {
        return this.delegate.isRecord();
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final boolean isMultiset() {
        return this.delegate.isMultiset();
    }

    @Override // org.jooq.DataType
    public final DataType<U> getSQLDataType() {
        return this.delegate.getSQLDataType();
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final DataType<U[]> getArrayDataType() {
        if (getBinding() instanceof DefaultBinding.InternalBinding) {
            return this.delegate.getArrayDataType().asConvertedDataType(Converters.forArrays(this.binding.converter()));
        }
        throw new DataTypeException("Cannot create array data types from custom data types with custom bindings.");
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final DataType<?> getArrayComponentDataType() {
        DataType<?> arrayComponentDataType = this.delegate.getArrayComponentDataType();
        if (arrayComponentDataType == null) {
            return null;
        }
        return arrayComponentDataType.asConvertedDataType(Converters.forArrayComponents(this.binding.converter()));
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final Class<?> getArrayComponentType() {
        DataType<?> d = getArrayComponentDataType();
        if (d == null) {
            return null;
        }
        return d.getType();
    }

    @Override // org.jooq.DataType
    public final DataType<U> getDataType(Configuration configuration) {
        return this.delegate.getDataType(configuration);
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final String getCastTypeName() {
        return this.delegate.getCastTypeName();
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final String getCastTypeName(Configuration configuration) {
        return this.delegate.getCastTypeName(configuration);
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final String getTypeName() {
        return this.delegate.getTypeName();
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final String getTypeName(Configuration configuration) {
        return this.delegate.getTypeName(configuration);
    }

    @Override // org.jooq.DataType
    public final Binding<?, U> getBinding() {
        return this.binding;
    }

    @Override // org.jooq.DataType
    public final Class<U> getType() {
        return this.binding.converter().toType();
    }

    @Override // org.jooq.DataType
    public final SQLDialect getDialect() {
        return this.delegate.getDialect();
    }

    @Override // org.jooq.DataType
    public final Nullability nullability() {
        return this.delegate.nullability();
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final boolean readonly() {
        return this.delegate.readonly();
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final Generator<?, ?, U> generatedAlwaysAsGenerator() {
        return this.delegate.generatedAlwaysAsGenerator();
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final QOM.GenerationOption generationOption() {
        return this.delegate.generationOption();
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final QOM.GenerationLocation generationLocation() {
        return this.delegate.generationLocation();
    }

    @Override // org.jooq.DataType
    public final Collation collation() {
        return this.delegate.collation();
    }

    @Override // org.jooq.DataType
    public final CharacterSet characterSet() {
        return this.delegate.characterSet();
    }

    @Override // org.jooq.DataType
    public final boolean identity() {
        return this.delegate.identity();
    }

    @Override // org.jooq.DataType
    public final Field<U> default_() {
        return this.delegate.default_();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractDataType
    public final String typeName0() {
        return this.delegate.typeName0();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractDataType
    public final String castTypePrefix0() {
        return this.delegate.castTypePrefix0();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractDataType
    public final String castTypeSuffix0() {
        return this.delegate.castTypeSuffix0();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractDataType
    public final String castTypeName0() {
        return this.delegate.castTypeName0();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractDataType
    public final Class<?> tType0() {
        return this.delegate.tType0();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractDataType
    public final Class<U> uType0() {
        return this.binding.converter().toType();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractDataType
    public final Integer precision0() {
        return this.delegate.precision0();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractDataType
    public final Integer scale0() {
        return this.delegate.scale0();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractDataType
    public final Integer length0() {
        return this.delegate.length0();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final U convert(Object obj) {
        if (getConverter().toType().isInstance(obj)) {
            return obj;
        }
        if (this.delegate.isMultiset() && !(obj instanceof Result)) {
            return obj;
        }
        if (this.delegate.isRecord() && !(obj instanceof Record) && !(obj instanceof List) && !(obj instanceof Map)) {
            return obj;
        }
        return getConverter().from(this.delegate.convert(obj), Internal.converterContext());
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final <X> DataType<X> asConvertedDataType(Converter<? super U, X> converter) {
        return super.asConvertedDataType(new ChainedConverterBinding(getBinding(), converter));
    }

    final DataType<T> delegate() {
        AbstractDataTypeX<T> abstractDataTypeX = this.delegate;
        if (!(abstractDataTypeX instanceof ConvertedDataType)) {
            return this.delegate;
        }
        ConvertedDataType c = (ConvertedDataType) abstractDataTypeX;
        return c.delegate();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final DataType<?> delegate(DataType<?> type) {
        if (!(type instanceof ConvertedDataType)) {
            return type;
        }
        ConvertedDataType<?, ?> c = (ConvertedDataType) type;
        return c.delegate();
    }
}

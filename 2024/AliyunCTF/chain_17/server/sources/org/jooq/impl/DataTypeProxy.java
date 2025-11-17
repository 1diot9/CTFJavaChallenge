package org.jooq.impl;

import java.util.IdentityHashMap;
import org.jooq.Binding;
import org.jooq.CharacterSet;
import org.jooq.Collation;
import org.jooq.Configuration;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Generator;
import org.jooq.Name;
import org.jooq.Nullability;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DataTypeProxy.class */
public final class DataTypeProxy<T> extends AbstractDataType<T> {
    private AbstractDataType<T> type;
    private final Integer overridePrecision;
    private final Integer overrideScale;
    private final Integer overrideLength;
    private final Nullability overrideNullability;
    private final Boolean overrideReadonly;
    private final Generator<?, ?, T> overrideGeneratedAlwaysAs;
    private final QOM.GenerationOption overrideGenerationOption;
    private final QOM.GenerationLocation overrideGenerationLocation;
    private final Collation overrideCollation;
    private final CharacterSet overrideCharacterSet;
    private final Boolean overrideIdentity;
    private final Field<T> overrideDefaultValue;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DataTypeProxy(AbstractDataType<T> type) {
        this(type, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    private DataTypeProxy(AbstractDataType<T> type, Integer overridePrecision, Integer overrideScale, Integer overrideLength, Nullability overrideNullability, Boolean overrideReadonly, Generator<?, ?, T> overrideGeneratedAlwaysAs, QOM.GenerationOption overrideGenerationOption, QOM.GenerationLocation overrideGenerationLocation, Collation overrideCollation, CharacterSet overrideCharacterSet, Boolean overrideIdentity, Field<T> overrideDefaultValue) {
        super(type.getQualifiedName(), type.getCommentPart());
        this.type = type;
        this.overridePrecision = overridePrecision;
        this.overrideScale = overrideScale;
        this.overrideLength = overrideLength;
        this.overrideNullability = overrideNullability;
        this.overrideReadonly = overrideReadonly;
        this.overrideGeneratedAlwaysAs = overrideGeneratedAlwaysAs;
        this.overrideGenerationOption = overrideGenerationOption;
        this.overrideGenerationLocation = overrideGenerationLocation;
        this.overrideCollation = overrideCollation;
        this.overrideCharacterSet = overrideCharacterSet;
        this.overrideIdentity = overrideIdentity;
        this.overrideDefaultValue = overrideDefaultValue;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final AbstractDataType<T> type() {
        return this.type;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void type(AbstractDataType<T> t) {
        if (t instanceof DataTypeProxy) {
            DataTypeProxy<T> p = (DataTypeProxy) t;
            IdentityHashMap<AbstractDataType<?>, AbstractDataType<?>> m = new IdentityHashMap<>();
            m.put(this, this);
            m.put(p, p);
            while (true) {
                AbstractDataType<T> type = p.type();
                if (!(type instanceof DataTypeProxy)) {
                    break;
                }
                DataTypeProxy<T> p2 = (DataTypeProxy) type;
                if (m.put(p2, p2) != null) {
                    return;
                } else {
                    p = p2;
                }
            }
        }
        this.type = t;
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.Named
    public final Name getQualifiedName() {
        return this.type.getQualifiedName();
    }

    @Override // org.jooq.DataType
    public final DataType<T> getSQLDataType() {
        return this.type.getSQLDataType();
    }

    @Override // org.jooq.DataType
    public final DataType<T> getDataType(Configuration configuration) {
        return this.type.getDataType(configuration);
    }

    @Override // org.jooq.DataType
    public final Binding<?, T> getBinding() {
        return this.type.getBinding();
    }

    @Override // org.jooq.DataType
    public final Class<T> getType() {
        return this.type.getType();
    }

    @Override // org.jooq.DataType
    public final SQLDialect getDialect() {
        return this.type.getDialect();
    }

    @Override // org.jooq.DataType
    public final Nullability nullability() {
        return (Nullability) StringUtils.defaultIfNull(this.overrideNullability, this.type.nullability());
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final DataType<T> nullability(Nullability n) {
        return new DataTypeProxy(this, this.overridePrecision, this.overrideScale, this.overrideLength, n, this.overrideReadonly, this.overrideGeneratedAlwaysAs, this.overrideGenerationOption, this.overrideGenerationLocation, this.overrideCollation, this.overrideCharacterSet, this.overrideIdentity, this.overrideDefaultValue);
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final boolean readonly() {
        return ((Boolean) StringUtils.defaultIfNull(this.overrideReadonly, Boolean.valueOf(this.type.readonly()))).booleanValue();
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final DataType<T> readonly(boolean r) {
        return new DataTypeProxy(this, this.overridePrecision, this.overrideScale, this.overrideLength, this.overrideNullability, Boolean.valueOf(r), this.overrideGeneratedAlwaysAs, this.overrideGenerationOption, this.overrideGenerationLocation, this.overrideCollation, this.overrideCharacterSet, this.overrideIdentity, this.overrideDefaultValue);
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final Generator<?, ?, T> generatedAlwaysAsGenerator() {
        return (Generator) StringUtils.defaultIfNull(this.overrideGeneratedAlwaysAs, this.type.generatedAlwaysAsGenerator());
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final DataType<T> generatedAlwaysAs(Generator<?, ?, T> g) {
        return new DataTypeProxy(this, this.overridePrecision, this.overrideScale, this.overrideLength, this.overrideNullability, this.overrideReadonly, g, this.overrideGenerationOption, this.overrideGenerationLocation, this.overrideCollation, this.overrideCharacterSet, this.overrideIdentity, this.overrideDefaultValue);
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final QOM.GenerationOption generationOption() {
        return (QOM.GenerationOption) StringUtils.defaultIfNull(this.overrideGenerationOption, this.type.generationOption());
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final DataType<T> generationOption(QOM.GenerationOption g) {
        return new DataTypeProxy(this, this.overridePrecision, this.overrideScale, this.overrideLength, this.overrideNullability, this.overrideReadonly, this.overrideGeneratedAlwaysAs, g, this.overrideGenerationLocation, this.overrideCollation, this.overrideCharacterSet, this.overrideIdentity, this.overrideDefaultValue);
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final QOM.GenerationLocation generationLocation() {
        return (QOM.GenerationLocation) StringUtils.defaultIfNull(this.overrideGenerationLocation, this.type.generationLocation());
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final DataType<T> generationLocation(QOM.GenerationLocation g) {
        return new DataTypeProxy(this, this.overridePrecision, this.overrideScale, this.overrideLength, this.overrideNullability, this.overrideReadonly, this.overrideGeneratedAlwaysAs, this.overrideGenerationOption, g, this.overrideCollation, this.overrideCharacterSet, this.overrideIdentity, this.overrideDefaultValue);
    }

    @Override // org.jooq.DataType
    public final Collation collation() {
        return (Collation) StringUtils.defaultIfNull(this.overrideCollation, this.type.collation());
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final DataType<T> collation(Collation c) {
        return new DataTypeProxy(this, this.overridePrecision, this.overrideScale, this.overrideLength, this.overrideNullability, this.overrideReadonly, this.overrideGeneratedAlwaysAs, this.overrideGenerationOption, this.overrideGenerationLocation, c, this.overrideCharacterSet, this.overrideIdentity, this.overrideDefaultValue);
    }

    @Override // org.jooq.DataType
    public final CharacterSet characterSet() {
        return (CharacterSet) StringUtils.defaultIfNull(this.overrideCharacterSet, this.type.characterSet());
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final DataType<T> characterSet(CharacterSet c) {
        return new DataTypeProxy(this, this.overridePrecision, this.overrideScale, this.overrideLength, this.overrideNullability, this.overrideReadonly, this.overrideGeneratedAlwaysAs, this.overrideGenerationOption, this.overrideGenerationLocation, this.overrideCollation, c, this.overrideIdentity, this.overrideDefaultValue);
    }

    @Override // org.jooq.DataType
    public final boolean identity() {
        return ((Boolean) StringUtils.defaultIfNull(this.overrideIdentity, Boolean.valueOf(this.type.identity()))).booleanValue();
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final DataType<T> identity(boolean i) {
        return new DataTypeProxy(this, this.overridePrecision, this.overrideScale, this.overrideLength, this.overrideNullability, this.overrideReadonly, this.overrideGeneratedAlwaysAs, this.overrideGenerationOption, this.overrideGenerationLocation, this.overrideCollation, this.overrideCharacterSet, Boolean.valueOf(i), this.overrideDefaultValue);
    }

    @Override // org.jooq.DataType
    public final Field<T> default_() {
        return (Field) StringUtils.defaultIfNull(this.overrideDefaultValue, this.type.default_());
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final DataType<T> default_(Field<T> d) {
        return new DataTypeProxy(this, this.overridePrecision, this.overrideScale, this.overrideLength, this.overrideNullability, this.overrideReadonly, this.overrideGeneratedAlwaysAs, this.overrideGenerationOption, this.overrideGenerationLocation, this.overrideCollation, this.overrideCharacterSet, this.overrideIdentity, d);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractDataType
    public final String typeName0() {
        return this.type.typeName0();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractDataType
    public final String castTypePrefix0() {
        return this.type.castTypePrefix0();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractDataType
    public final String castTypeSuffix0() {
        return this.type.castTypeSuffix0();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractDataType
    public final String castTypeName0() {
        return this.type.castTypeName0();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractDataType
    public final Class<?> tType0() {
        return this.type.tType0();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractDataType
    public final Class<T> uType0() {
        return this.type.uType0();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractDataType
    public final Integer precision0() {
        return (Integer) StringUtils.defaultIfNull(this.overridePrecision, this.type.precision0());
    }

    @Override // org.jooq.impl.AbstractDataType
    final AbstractDataType<T> precision1(Integer p, Integer s) {
        return new DataTypeProxy(this, p, s, this.overrideLength, this.overrideNullability, this.overrideReadonly, this.overrideGeneratedAlwaysAs, this.overrideGenerationOption, this.overrideGenerationLocation, this.overrideCollation, this.overrideCharacterSet, this.overrideIdentity, this.overrideDefaultValue);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractDataType
    public final Integer scale0() {
        return (Integer) StringUtils.defaultIfNull(this.overrideScale, this.type.scale0());
    }

    @Override // org.jooq.impl.AbstractDataType
    final AbstractDataType<T> scale1(Integer s) {
        return new DataTypeProxy(this, this.overridePrecision, s, this.overrideLength, this.overrideNullability, this.overrideReadonly, this.overrideGeneratedAlwaysAs, this.overrideGenerationOption, this.overrideGenerationLocation, this.overrideCollation, this.overrideCharacterSet, this.overrideIdentity, this.overrideDefaultValue);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractDataType
    public final Integer length0() {
        return (Integer) StringUtils.defaultIfNull(this.overrideLength, this.type.length0());
    }

    @Override // org.jooq.impl.AbstractDataType
    final AbstractDataType<T> length1(Integer l) {
        return new DataTypeProxy(this, this.overridePrecision, this.overrideScale, l, this.overrideNullability, this.overrideReadonly, this.overrideGeneratedAlwaysAs, this.overrideGenerationOption, this.overrideGenerationLocation, this.overrideCollation, this.overrideCharacterSet, this.overrideIdentity, this.overrideDefaultValue);
    }
}

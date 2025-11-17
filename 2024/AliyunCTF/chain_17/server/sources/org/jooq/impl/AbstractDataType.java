package org.jooq.impl;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import java.lang.invoke.SerializedLambda;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.temporal.Temporal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import org.jooq.Binding;
import org.jooq.CharacterSet;
import org.jooq.Collation;
import org.jooq.Comment;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.ContextConverter;
import org.jooq.Converter;
import org.jooq.DataType;
import org.jooq.Domain;
import org.jooq.EmbeddableRecord;
import org.jooq.EnumType;
import org.jooq.Field;
import org.jooq.Generator;
import org.jooq.GeneratorStatementType;
import org.jooq.Geography;
import org.jooq.Geometry;
import org.jooq.JSON;
import org.jooq.JSONB;
import org.jooq.Name;
import org.jooq.Nullability;
import org.jooq.QualifiedRecord;
import org.jooq.Record;
import org.jooq.Row;
import org.jooq.SQLDialect;
import org.jooq.Schema;
import org.jooq.XML;
import org.jooq.impl.QOM;
import org.jooq.types.Interval;
import org.jooq.types.UNumber;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractDataType.class */
public abstract class AbstractDataType<T> extends AbstractNamed implements DataType<T>, QOM.UEmpty {
    static final Set<SQLDialect> NO_SUPPORT_TIMESTAMP_PRECISION = SQLDialect.supportedBy(SQLDialect.DERBY, SQLDialect.FIREBIRD);

    @Override // org.jooq.DataType
    public abstract DataType<T> nullability(Nullability nullability);

    @Override // org.jooq.DataType
    public abstract boolean readonly();

    @Override // org.jooq.DataType
    public abstract DataType<T> readonly(boolean z);

    @Override // org.jooq.DataType
    public abstract DataType<T> generatedAlwaysAs(Generator<?, ?, T> generator);

    @Override // org.jooq.DataType
    public abstract Generator<?, ?, T> generatedAlwaysAsGenerator();

    @Override // org.jooq.DataType
    public abstract DataType<T> generationOption(QOM.GenerationOption generationOption);

    @Override // org.jooq.DataType
    public abstract QOM.GenerationOption generationOption();

    @Override // org.jooq.DataType
    public abstract DataType<T> generationLocation(QOM.GenerationLocation generationLocation);

    @Override // org.jooq.DataType
    public abstract QOM.GenerationLocation generationLocation();

    @Override // org.jooq.DataType
    public abstract DataType<T> collation(Collation collation);

    @Override // org.jooq.DataType
    public abstract DataType<T> characterSet(CharacterSet characterSet);

    @Override // org.jooq.DataType
    public abstract DataType<T> identity(boolean z);

    @Override // org.jooq.DataType
    public abstract DataType<T> default_(Field<T> field);

    abstract AbstractDataType<T> precision1(Integer num, Integer num2);

    abstract AbstractDataType<T> scale1(Integer num);

    abstract AbstractDataType<T> length1(Integer num);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract String typeName0();

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract String castTypePrefix0();

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract String castTypeSuffix0();

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract String castTypeName0();

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract Class<?> tType0();

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract Class<T> uType0();

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract Integer precision0();

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract Integer scale0();

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract Integer length0();

    private static /* synthetic */ Object $deserializeLambda$(SerializedLambda lambda) {
        String implMethodName = lambda.getImplMethodName();
        boolean z = -1;
        switch (implMethodName.hashCode()) {
            case 953566906:
                if (implMethodName.equals("lambda$lazyName$f03949e5$1")) {
                    z = false;
                    break;
                }
                break;
            case 1283463102:
                if (implMethodName.equals("lambda$generatedAlwaysAs$3d6458c1$1")) {
                    z = true;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
                if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("org/jooq/impl/LazySupplier") && lambda.getFunctionalInterfaceMethodName().equals(BeanUtil.PREFIX_GETTER_GET) && lambda.getFunctionalInterfaceMethodSignature().equals("()Ljava/lang/Object;") && lambda.getImplClass().equals("org/jooq/impl/AbstractDataType") && lambda.getImplMethodSignature().equals("(Lorg/jooq/EnumType;)Lorg/jooq/Name;")) {
                    EnumType enumType = (EnumType) lambda.getCapturedArg(0);
                    return () -> {
                        Objects.requireNonNull(enumType);
                        return lazyName(enumType::getSchema, () -> {
                            return DSL.name(enumType.getName());
                        });
                    };
                }
                break;
            case true:
                if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("org/jooq/Generator") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("org/jooq/impl/AbstractDataType") && lambda.getImplMethodSignature().equals("(Lorg/jooq/Field;Lorg/jooq/GeneratorContext;)Lorg/jooq/Field;")) {
                    Field field = (Field) lambda.getCapturedArg(0);
                    return t -> {
                        return field;
                    };
                }
                break;
        }
        throw new IllegalArgumentException("Invalid lambda deserialization");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractDataType(Name name, Comment comment) {
        super(name, comment);
    }

    @Override // org.jooq.DataType
    public final DataType<T> nullable(boolean n) {
        return nullability(Nullability.of(n));
    }

    @Override // org.jooq.DataType
    public final boolean nullable() {
        return nullability().nullable();
    }

    @Override // org.jooq.DataType
    public final DataType<T> null_() {
        return nullable(true);
    }

    @Override // org.jooq.DataType
    public final DataType<T> notNull() {
        return nullable(false);
    }

    @Override // org.jooq.DataType
    public final boolean readonlyInternal() {
        return readonlyInternal(Tools.CONFIG.get());
    }

    @Override // org.jooq.DataType
    public final boolean readonlyInternal(Configuration configuration) {
        return readonly() && !computedOnClientStored(configuration);
    }

    @Override // org.jooq.DataType
    public final boolean computed() {
        return generatedAlwaysAsGenerator() != null;
    }

    @Override // org.jooq.DataType
    public final boolean computedOnServer() {
        return computedOnServer(Tools.CONFIG.get());
    }

    @Override // org.jooq.DataType
    public final boolean computedOnServer(Configuration configuration) {
        return computed() && generationLocation(configuration) == QOM.GenerationLocation.SERVER;
    }

    @Override // org.jooq.DataType
    public final boolean computedOnClient() {
        return computedOnClient(Tools.CONFIG.get());
    }

    @Override // org.jooq.DataType
    public final boolean computedOnClient(Configuration configuration) {
        return computed() && generationLocation(configuration) == QOM.GenerationLocation.CLIENT;
    }

    @Override // org.jooq.DataType
    public final boolean computedOnClientStored() {
        return computedOnClientStored(Tools.CONFIG.get());
    }

    @Override // org.jooq.DataType
    public final boolean computedOnClientStored(Configuration configuration) {
        return computedOnClient(configuration) && generationOption(configuration) != QOM.GenerationOption.VIRTUAL && (generatedAlwaysAsGenerator().supports(GeneratorStatementType.INSERT) || generatedAlwaysAsGenerator().supports(GeneratorStatementType.UPDATE));
    }

    @Override // org.jooq.DataType
    public final boolean computedOnClientStoredOn(GeneratorStatementType statementType) {
        return computedOnClientStoredOn(statementType, Tools.CONFIG.get());
    }

    @Override // org.jooq.DataType
    public final boolean computedOnClientStoredOn(GeneratorStatementType statementType, Configuration configuration) {
        return computedOnClient(configuration) && generationOption(configuration) != QOM.GenerationOption.VIRTUAL && generatedAlwaysAsGenerator().supports(statementType);
    }

    @Override // org.jooq.DataType
    public final boolean computedOnClientVirtual() {
        return computedOnClientVirtual(Tools.CONFIG.get());
    }

    @Override // org.jooq.DataType
    public final boolean computedOnClientVirtual(Configuration configuration) {
        return computedOnClient(configuration) && generationOption(configuration) == QOM.GenerationOption.VIRTUAL && generatedAlwaysAsGenerator().supports(GeneratorStatementType.SELECT);
    }

    @Override // org.jooq.DataType
    public final DataType<T> generatedAlwaysAs(T g) {
        return generatedAlwaysAs((Field) Tools.field(g, this));
    }

    @Override // org.jooq.DataType
    public final DataType<T> generatedAlwaysAs(Field<T> generatedAlwaysAsValue) {
        return generatedAlwaysAs((Generator) t -> {
            return generatedAlwaysAsValue;
        });
    }

    @Override // org.jooq.DataType
    public final Field<T> generatedAlwaysAs() {
        Generator<?, ?, T> s = generatedAlwaysAsGenerator();
        if (s == null) {
            return null;
        }
        return (Field) s.apply(new DefaultGeneratorContext(Tools.CONFIG.get()));
    }

    @Override // org.jooq.DataType
    public final DataType<T> stored() {
        return generationOption(QOM.GenerationOption.STORED);
    }

    @Override // org.jooq.DataType
    public final DataType<T> virtual() {
        return generationOption(QOM.GenerationOption.VIRTUAL);
    }

    final QOM.GenerationOption generationOption(Configuration configuration) {
        if (Boolean.TRUE.equals(Tools.settings(configuration).isEmulateComputedColumns())) {
            return QOM.GenerationOption.STORED;
        }
        return generationOption();
    }

    final QOM.GenerationLocation generationLocation(Configuration configuration) {
        if (Boolean.TRUE.equals(Tools.settings(configuration).isEmulateComputedColumns())) {
            return QOM.GenerationLocation.CLIENT;
        }
        return generationLocation();
    }

    @Override // org.jooq.DataType
    public final DataType<T> defaultValue(T d) {
        return default_((AbstractDataType<T>) d);
    }

    @Override // org.jooq.DataType
    public final DataType<T> defaultValue(Field<T> d) {
        return default_((Field) d);
    }

    @Override // org.jooq.DataType
    public final Field<T> defaultValue() {
        return default_();
    }

    @Override // org.jooq.DataType
    public final DataType<T> default_(T d) {
        return default_((Field) Tools.field(d, this));
    }

    @Override // org.jooq.DataType
    public final boolean defaulted() {
        return defaultValue() != null;
    }

    @Override // org.jooq.DataType
    public final int precision() {
        Integer precision = precision0();
        if (precision == null) {
            return 0;
        }
        return precision.intValue();
    }

    @Override // org.jooq.DataType
    public final DataType<T> precision(int p) {
        return precision0(Integer.valueOf(p), Integer.valueOf(scale()));
    }

    @Override // org.jooq.DataType
    public final DataType<T> precision(int p, int s) {
        return precision0(Integer.valueOf(p), Integer.valueOf(s));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final AbstractDataType<T> precision0(Integer p, Integer s) {
        if (eq(precision0(), p) && eq(scale0(), s)) {
            return this;
        }
        if (isLob()) {
            return this;
        }
        return precision1(p, s);
    }

    @Override // org.jooq.DataType
    public final boolean hasPrecision() {
        Class<?> tType = tType0();
        return tType == BigInteger.class || tType == BigDecimal.class || tType == Timestamp.class || tType == Time.class || tType == LocalDateTime.class || tType == LocalTime.class || tType == OffsetDateTime.class || tType == OffsetTime.class || tType == Instant.class;
    }

    @Override // org.jooq.DataType
    public final boolean precisionDefined() {
        return precision0() != null && hasPrecision();
    }

    @Override // org.jooq.DataType
    public final int scale() {
        Integer scale = scale0();
        if (scale == null) {
            return 0;
        }
        return scale.intValue();
    }

    @Override // org.jooq.DataType
    public final DataType<T> scale(int s) {
        return scale0(Integer.valueOf(s));
    }

    final AbstractDataType<T> scale0(Integer s) {
        if (eq(scale0(), s)) {
            return this;
        }
        if (isLob()) {
            return this;
        }
        return scale1(s);
    }

    @Override // org.jooq.DataType
    public final boolean hasScale() {
        return tType0() == BigDecimal.class;
    }

    @Override // org.jooq.DataType
    public final boolean scaleDefined() {
        return scale0() != null && hasScale();
    }

    @Override // org.jooq.DataType
    public final DataType<T> length(int l) {
        return length0(Integer.valueOf(l));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final AbstractDataType<T> length0(Integer l) {
        if (eq(length0(), l)) {
            return this;
        }
        if (isLob()) {
            return this;
        }
        return length1(l);
    }

    @Override // org.jooq.DataType
    public final int length() {
        Integer length = length0();
        if (length == null) {
            return 0;
        }
        return length.intValue();
    }

    @Override // org.jooq.DataType
    public final boolean hasLength() {
        Class<?> tType = tType0();
        return (tType == byte[].class || tType == String.class) && !isLob();
    }

    @Override // org.jooq.DataType
    public final boolean lengthDefined() {
        return length0() != null && hasLength();
    }

    @Override // org.jooq.DataType
    public int getSQLType() {
        return getSQLType(DSL.using(getDialect()).configuration());
    }

    /* JADX WARN: Code restructure failed: missing block: B:27:0x0068, code lost:            return 91;     */
    @Override // org.jooq.DataType
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final int getSQLType(org.jooq.Configuration r4) {
        /*
            Method dump skipped, instructions count: 311
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jooq.impl.AbstractDataType.getSQLType(org.jooq.Configuration):int");
    }

    @Override // org.jooq.DataType
    public Domain<T> getDomain() {
        return null;
    }

    @Override // org.jooq.DataType
    public final ContextConverter<?, T> getConverter() {
        return ContextConverter.scoped(getBinding().converter());
    }

    @Override // org.jooq.DataType
    public String getTypeName() {
        return getTypeName0(Tools.CONFIG_UNQUOTED.get());
    }

    private final String getTypeName0(Configuration configuration) {
        if (isEnum() || isUDT()) {
            return renderedTypeName0(configuration);
        }
        return typeName0();
    }

    @Override // org.jooq.DataType
    public String getTypeName(Configuration configuration) {
        return ((AbstractDataType) getDataType(configuration)).getTypeName0(configuration);
    }

    @Override // org.jooq.DataType
    public String getCastTypeName() {
        return getCastTypeName0(Tools.CONFIG_UNQUOTED.get());
    }

    private final String getCastTypeName0(Configuration configuration) {
        SQLDialect dialect = configuration.dialect();
        if (isEnum() || isUDT()) {
            return renderedTypeName0(configuration);
        }
        if (lengthDefined() && length() > 0) {
            if (isBinary() && Tools.NO_SUPPORT_BINARY_TYPE_LENGTH.contains(dialect)) {
                return castTypeName0();
            }
            return castTypePrefix0() + "(" + length() + ")" + castTypeSuffix0();
        }
        if (precisionDefined() && (isTimestamp() || precision() > 0)) {
            if (isTimestamp() && NO_SUPPORT_TIMESTAMP_PRECISION.contains(dialect)) {
                return castTypePrefix0() + castTypeSuffix0();
            }
            if (scaleDefined() && scale() > 0) {
                return castTypePrefix0() + "(" + precision() + ", " + scale() + ")" + castTypeSuffix0();
            }
            return castTypePrefix0() + "(" + precision() + ")" + castTypeSuffix0();
        }
        return castTypeName0();
    }

    @Override // org.jooq.DataType
    public String getCastTypeName(Configuration configuration) {
        return ((AbstractDataType) getDataType(configuration)).getCastTypeName0(configuration);
    }

    private final String renderedTypeName0(Configuration configuration) {
        return configuration.dsl().render(this);
    }

    @Override // org.jooq.DataType
    public final Class<T[]> getArrayType() {
        return Internal.arrayType(getType());
    }

    @Override // org.jooq.DataType
    public DataType<T[]> getArrayDataType() {
        return new ArrayDataType(this);
    }

    @Override // org.jooq.DataType
    public final DataType<T[]> array() {
        return getArrayDataType();
    }

    @Override // org.jooq.DataType
    public Class<?> getArrayComponentType() {
        return null;
    }

    @Override // org.jooq.DataType
    public DataType<?> getArrayComponentDataType() {
        return null;
    }

    @Override // org.jooq.DataType
    public Class<?> getArrayBaseType() {
        return getType();
    }

    @Override // org.jooq.DataType
    public DataType<?> getArrayBaseDataType() {
        return this;
    }

    @Override // org.jooq.DataType
    public Row getRow() {
        return null;
    }

    @Override // org.jooq.DataType
    public Class<? extends Record> getRecordType() {
        return null;
    }

    @Override // org.jooq.DataType
    public final <E extends EnumType> DataType<E> asEnumDataType(Class<E> enumDataType) {
        EnumType enumType = Tools.enums(enumDataType)[0];
        return new DefaultDataType(getDialect(), (DataType) null, enumDataType, null, lazyName(enumType), enumType.getName(), enumType.getName(), precision0(), scale0(), length0(), nullability(), readonly(), generatedAlwaysAsGenerator(), generationOption(), generationLocation(), collation(), characterSet(), identity(), defaultValue());
    }

    static final <E extends EnumType> Name lazyName(E e) {
        return new LazyName(() -> {
            Objects.requireNonNull(e);
            return lazyName(e::getSchema, () -> {
                return DSL.name(e.getName());
            });
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Name lazyName(Supplier<Schema> schema, Supplier<Name> name) {
        Schema s = schema.get();
        return s == null ? name.get() : s.getQualifiedName().append(name.get());
    }

    @Override // org.jooq.DataType
    public <U> DataType<U> asConvertedDataType(Converter<? super T, U> converter) {
        return asConvertedDataType(DefaultBinding.newBinding(converter, this, null));
    }

    @Override // org.jooq.DataType
    public <U> DataType<U> asConvertedDataType(Binding<? super T, U> newBinding) {
        if (getBinding() == newBinding) {
            return this;
        }
        if (newBinding == null) {
            newBinding = DefaultBinding.binding(this);
        }
        return new ConvertedDataType((AbstractDataTypeX) this, newBinding);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DataType
    public T convert(Object obj) {
        if (obj == 0) {
            return null;
        }
        if (obj.getClass() == getType()) {
            return obj;
        }
        return (T) Convert.convert(obj, getType());
    }

    @Override // org.jooq.DataType
    public final T[] convert(Object... objArr) {
        return (T[]) Tools.map(objArr, o -> {
            return convert(o);
        }, l -> {
            return (Object[]) java.lang.reflect.Array.newInstance((Class<?>) getType(), l);
        });
    }

    @Override // org.jooq.DataType
    public final List<T> convert(Collection<?> objects) {
        return Tools.map(objects, o -> {
            return convert(o);
        });
    }

    @Override // org.jooq.DataType
    public final boolean isNumeric() {
        return Number.class.isAssignableFrom(tType0()) && !isInterval();
    }

    @Override // org.jooq.DataType
    public final boolean isInteger() {
        Class<?> tType = tType0();
        return UNumber.class.isAssignableFrom(tType) || Byte.class == tType || Short.class == tType || Integer.class == tType || Long.class == tType;
    }

    @Override // org.jooq.DataType
    public final boolean isFloat() {
        Class<?> tType = tType0();
        return Float.class == tType || Double.class == tType;
    }

    @Override // org.jooq.DataType
    public final boolean isBoolean() {
        return tType0() == Boolean.class;
    }

    @Override // org.jooq.DataType
    public final boolean isString() {
        return tType0() == String.class;
    }

    @Override // org.jooq.DataType
    public final boolean isNString() {
        AbstractDataType<T> t = (AbstractDataType) getSQLDataType();
        return t == SQLDataType.NCHAR || t == SQLDataType.NCLOB || t == SQLDataType.NVARCHAR || (SQLDataType.NCHAR == null && "nchar".equals(t.typeName0())) || ((SQLDataType.NCLOB == null && "nclob".equals(t.typeName0())) || (SQLDataType.NVARCHAR == null && "nvarchar".equals(t.typeName0())));
    }

    @Override // org.jooq.DataType
    public final boolean isDateTime() {
        Class<?> tType = tType0();
        return Date.class.isAssignableFrom(tType) || Temporal.class.isAssignableFrom(tType);
    }

    @Override // org.jooq.DataType
    public final boolean isDate() {
        Class<?> tType = tType0();
        return java.sql.Date.class.isAssignableFrom(tType) || LocalDate.class.isAssignableFrom(tType);
    }

    @Override // org.jooq.DataType
    public final boolean isTimestamp() {
        Class<?> tType = tType0();
        return Timestamp.class.isAssignableFrom(tType) || LocalDateTime.class.isAssignableFrom(tType);
    }

    @Override // org.jooq.DataType
    public final boolean isTime() {
        Class<?> tType = tType0();
        return Time.class.isAssignableFrom(tType) || LocalTime.class.isAssignableFrom(tType);
    }

    @Override // org.jooq.DataType
    public final boolean isTemporal() {
        return isDateTime() || isInterval();
    }

    @Override // org.jooq.DataType
    public final boolean isInterval() {
        return Interval.class.isAssignableFrom(tType0());
    }

    @Override // org.jooq.DataType
    public final boolean isLob() {
        AbstractDataType<T> t = (AbstractDataType) getSQLDataType();
        if (t == this) {
            return typeName0().endsWith("lob");
        }
        return t == SQLDataType.BLOB || t == SQLDataType.CLOB || t == SQLDataType.NCLOB || (SQLDataType.BLOB == null && "blob".equals(t.typeName0())) || ((SQLDataType.CLOB == null && "clob".equals(t.typeName0())) || (SQLDataType.NCLOB == null && "nclob".equals(t.typeName0())));
    }

    @Override // org.jooq.DataType
    public final boolean isBinary() {
        return tType0() == byte[].class;
    }

    @Override // org.jooq.DataType
    public final boolean isArray() {
        Class<?> tType = tType0();
        return !isBinary() && tType.isArray();
    }

    @Override // org.jooq.DataType
    public final boolean isAssociativeArray() {
        return false;
    }

    @Override // org.jooq.DataType
    public final boolean isEmbeddable() {
        return EmbeddableRecord.class.isAssignableFrom(tType0());
    }

    @Override // org.jooq.DataType
    public final boolean isUDT() {
        return QualifiedRecord.class.isAssignableFrom(tType0());
    }

    @Override // org.jooq.DataType
    public boolean isRecord() {
        return Record.class.isAssignableFrom(tType0());
    }

    @Override // org.jooq.DataType
    public boolean isMultiset() {
        return this instanceof MultisetDataType;
    }

    @Override // org.jooq.DataType
    public final boolean isEnum() {
        return EnumType.class.isAssignableFrom(tType0());
    }

    @Override // org.jooq.DataType
    public final boolean isJSON() {
        Class<?> tType = tType0();
        return tType == JSON.class || tType == JSONB.class;
    }

    @Override // org.jooq.DataType
    public final boolean isXML() {
        return tType0() == XML.class;
    }

    @Override // org.jooq.DataType
    public final boolean isSpatial() {
        Class<?> tType = tType0();
        return tType == Geometry.class || tType == Geography.class;
    }

    @Override // org.jooq.DataType
    public final boolean isUUID() {
        return tType0() == UUID.class;
    }

    @Override // org.jooq.DataType
    public final boolean isOther() {
        return getType() == Object.class;
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            default:
                Tools.visitMappedSchema(ctx, getQualifiedName());
                return;
        }
    }

    private static final boolean eq(Integer i1, Integer i2) {
        return i1 == i2 || !(i1 == null || i2 == null || i1.intValue() != i2.intValue());
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public String toString() {
        return getCastTypeName() + " /* " + getType().getName() + " */";
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public int hashCode() {
        int result = (31 * 1) + (getDialect() == null ? 0 : getDialect().hashCode());
        return (31 * ((31 * ((31 * ((31 * ((31 * ((31 * result) + length())) + precision())) + scale())) + getType().hashCode())) + (tType0() == null ? 0 : tType0().hashCode()))) + (typeName0() == null ? 0 : typeName0().hashCode());
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof AbstractDataType)) {
            return false;
        }
        AbstractDataType<?> other = (AbstractDataType) obj;
        if (getDialect() != other.getDialect() || !eq(length0(), other.length0()) || !eq(precision0(), other.precision0()) || !eq(scale0(), other.scale0()) || !getType().equals(other.getType())) {
            return false;
        }
        if (tType0() == null) {
            if (other.tType0() != null) {
                return false;
            }
        } else if (!tType0().equals(other.tType0())) {
            return false;
        }
        if (typeName0() == null) {
            if (other.typeName0() != null) {
                return false;
            }
            return true;
        }
        if (!typeName0().equals(other.typeName0())) {
            return false;
        }
        return true;
    }
}

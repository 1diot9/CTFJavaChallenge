package org.jooq.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLType;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.ApiStatus;
import org.jooq.Binding;
import org.jooq.CharacterSet;
import org.jooq.Clause;
import org.jooq.Collation;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.Converter;
import org.jooq.DataType;
import org.jooq.Domain;
import org.jooq.EnumType;
import org.jooq.Field;
import org.jooq.Generator;
import org.jooq.Name;
import org.jooq.Nullability;
import org.jooq.QualifiedRecord;
import org.jooq.Row;
import org.jooq.SQLDialect;
import org.jooq.exception.MappingException;
import org.jooq.exception.SQLDialectNotSupportedException;
import org.jooq.impl.DefaultBinding;
import org.jooq.impl.QOM;
import org.jooq.tools.JooqLogger;
import org.jooq.tools.reflect.Reflect;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.jooq.types.ULong;
import org.jooq.types.UShort;
import org.springframework.asm.Opcodes;
import org.springframework.util.ClassUtils;

@ApiStatus.Internal
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultDataType.class */
public class DefaultDataType<T> extends AbstractDataTypeX<T> {
    private static final Map<Class<?>, DefaultDataType<?>> SQL_DATATYPES_BY_TYPE;
    private final SQLDialect dialect;
    private final DataType<T> sqlDataType;
    private final Class<T> uType;
    private final Class<?> tType;
    private final Binding<?, T> binding;
    private final String castTypeName;
    private final String castTypePrefix;
    private final String castTypeSuffix;
    private final String typeName;
    private final Nullability nullability;
    private final boolean readonly;
    private final Generator<?, ?, T> generatedAlwaysAs;
    private final QOM.GenerationOption generationOption;
    private final QOM.GenerationLocation generationLocation;
    private final Collation collation;
    private final CharacterSet characterSet;
    private final boolean identity;
    private final Field<T> defaultValue;
    private final Integer precision;
    private final Integer scale;
    private final Integer length;
    private static final JooqLogger getDataType;
    private static final Set<SQLDialect> ENCODED_TIMESTAMP_PRECISION = SQLDialect.supportedBy(SQLDialect.HSQLDB, SQLDialect.MARIADB);
    private static final Set<SQLDialect> NO_SUPPORT_TIMESTAMP_PRECISION = SQLDialect.supportedBy(SQLDialect.FIREBIRD, SQLDialect.MYSQL, SQLDialect.SQLITE);
    private static final Set<SQLDialect> SUPPORT_POSTGRES_ARRAY_NOTATION = SQLDialect.supportedBy(SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB);
    private static final Set<SQLDialect> SUPPORT_HSQLDB_ARRAY_NOTATION = SQLDialect.supportedBy(SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB);
    private static final Set<SQLDialect> SUPPORT_TRINO_ARRAY_NOTATION = SQLDialect.supportedBy(SQLDialect.TRINO);
    private static final Pattern P_NORMALISE = Pattern.compile("\"|\\.|\\s|\\(\\w+(\\s*,\\s*\\w+)*\\)|(NOT\\s*NULL)?");
    private static final Pattern P_TYPE_NAME = Pattern.compile("\\([^)]*\\)");
    private static final Pattern P_PRECISION_SCALE = Pattern.compile("\\(\\s*(\\d+)(?:\\s*,\\s*(\\d+))?\\s*\\)");
    static final int LONG_PRECISION = String.valueOf(Long.MAX_VALUE).length();
    static final int INTEGER_PRECISION = String.valueOf(Integer.MAX_VALUE).length();
    static final int SHORT_PRECISION = String.valueOf(32767).length();
    static final int BYTE_PRECISION = String.valueOf(Opcodes.LAND).length();
    private static final Map<DataType<?>, DefaultDataType<?>>[] TYPES_BY_SQL_DATATYPE = new Map[SQLDialect.values().length];
    private static final Map<String, DefaultDataType<?>>[] TYPES_BY_NAME = new Map[SQLDialect.values().length];
    private static final Map<Class<?>, DefaultDataType<?>>[] TYPES_BY_TYPE = new Map[SQLDialect.values().length];

    @Override // org.jooq.impl.AbstractDataType, org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public /* bridge */ /* synthetic */ boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public /* bridge */ /* synthetic */ int hashCode() {
        return super.hashCode();
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public /* bridge */ /* synthetic */ String toString() {
        return super.toString();
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public /* bridge */ /* synthetic */ boolean isMultiset() {
        return super.isMultiset();
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public /* bridge */ /* synthetic */ boolean isRecord() {
        return super.isRecord();
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public /* bridge */ /* synthetic */ Object convert(Object obj) {
        return super.convert(obj);
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public /* bridge */ /* synthetic */ DataType asConvertedDataType(Binding binding) {
        return super.asConvertedDataType(binding);
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public /* bridge */ /* synthetic */ DataType asConvertedDataType(Converter converter) {
        return super.asConvertedDataType(converter);
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public /* bridge */ /* synthetic */ Class getRecordType() {
        return super.getRecordType();
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public /* bridge */ /* synthetic */ Row getRow() {
        return super.getRow();
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public /* bridge */ /* synthetic */ DataType getArrayBaseDataType() {
        return super.getArrayBaseDataType();
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public /* bridge */ /* synthetic */ Class getArrayBaseType() {
        return super.getArrayBaseType();
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public /* bridge */ /* synthetic */ DataType getArrayComponentDataType() {
        return super.getArrayComponentDataType();
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public /* bridge */ /* synthetic */ Class getArrayComponentType() {
        return super.getArrayComponentType();
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public /* bridge */ /* synthetic */ DataType getArrayDataType() {
        return super.getArrayDataType();
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public /* bridge */ /* synthetic */ String getCastTypeName(Configuration configuration) {
        return super.getCastTypeName(configuration);
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public /* bridge */ /* synthetic */ String getCastTypeName() {
        return super.getCastTypeName();
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public /* bridge */ /* synthetic */ String getTypeName(Configuration configuration) {
        return super.getTypeName(configuration);
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public /* bridge */ /* synthetic */ String getTypeName() {
        return super.getTypeName();
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public /* bridge */ /* synthetic */ Domain getDomain() {
        return super.getDomain();
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public /* bridge */ /* synthetic */ int getSQLType() {
        return super.getSQLType();
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.Named
    public /* bridge */ /* synthetic */ Name getQualifiedName() {
        return super.getQualifiedName();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public /* bridge */ /* synthetic */ boolean generatesCast() {
        return super.generatesCast();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public /* bridge */ /* synthetic */ boolean declaresCTE() {
        return super.declaresCTE();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public /* bridge */ /* synthetic */ boolean declaresWindows() {
        return super.declaresWindows();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public /* bridge */ /* synthetic */ boolean declaresTables() {
        return super.declaresTables();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public /* bridge */ /* synthetic */ boolean declaresFields() {
        return super.declaresFields();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public /* bridge */ /* synthetic */ boolean rendersContent(Context context) {
        return super.rendersContent(context);
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    @Deprecated
    public /* bridge */ /* synthetic */ Clause[] clauses(Context context) {
        return super.clauses(context);
    }

    static {
        for (SQLDialect dialect : SQLDialect.values()) {
            TYPES_BY_SQL_DATATYPE[dialect.ordinal()] = new LinkedHashMap();
            TYPES_BY_NAME[dialect.ordinal()] = new LinkedHashMap();
            TYPES_BY_TYPE[dialect.ordinal()] = new LinkedHashMap();
        }
        SQL_DATATYPES_BY_TYPE = new LinkedHashMap();
        try {
            Class.forName(SQLDataType.class.getName());
        } catch (Exception e) {
        }
        getDataType = JooqLogger.getLogger(DefaultDataType.class, "getDataType", 5);
    }

    public DefaultDataType(SQLDialect dialect, DataType<T> sqlDataType, String typeName) {
        this(dialect, sqlDataType, typeName, (String) null);
    }

    public DefaultDataType(SQLDialect dialect, DataType<T> sqlDataType, String typeName, String castTypeName) {
        this(dialect, sqlDataType, sqlDataType.getType(), sqlDataType.getQualifiedName(), typeName, castTypeName, sqlDataType.precisionDefined() ? Integer.valueOf(sqlDataType.precision()) : null, sqlDataType.scaleDefined() ? Integer.valueOf(sqlDataType.scale()) : null, sqlDataType.lengthDefined() ? Integer.valueOf(sqlDataType.length()) : null, sqlDataType.nullability(), sqlDataType.defaultValue());
    }

    public DefaultDataType(SQLDialect dialect, Class<T> type, String typeName) {
        this(dialect, (DataType) null, type, DSL.systemName(typeName), typeName, (String) null, (Integer) null, (Integer) null, (Integer) null, Nullability.DEFAULT, (Field) null);
    }

    public DefaultDataType(SQLDialect dialect, Class<T> type, String typeName, String castTypeName) {
        this(dialect, (DataType) null, type, DSL.systemName(typeName), typeName, castTypeName, (Integer) null, (Integer) null, (Integer) null, Nullability.DEFAULT, (Field) null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultDataType(SQLDialect dialect, Class<T> type, String typeName, Nullability nullability) {
        this(dialect, (DataType) null, type, DSL.systemName(typeName), typeName, typeName, (Integer) null, (Integer) null, (Integer) null, nullability, (Field) null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultDataType(SQLDialect dialect, Class<T> type, Name qualifiedTypeName) {
        this(dialect, (DataType) null, type, qualifiedTypeName, (String) null, (String) null, (Integer) null, (Integer) null, (Integer) null, Nullability.DEFAULT, (Field) null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultDataType(SQLDialect dialect, DataType<T> type, Name qualifiedTypeName) {
        this(dialect, type, type.getType(), qualifiedTypeName, (String) null, (String) null, (Integer) null, (Integer) null, (Integer) null, Nullability.DEFAULT, (Field) null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultDataType(SQLDialect dialect, Class<T> type, Binding<?, T> binding, Name qualifiedTypeName, String typeName, String castTypeName, Integer precision, Integer scale, Integer length, Nullability nullability, Field<T> defaultValue) {
        this(dialect, null, type, binding, qualifiedTypeName, typeName, castTypeName, precision, scale, length, nullability, defaultValue);
    }

    DefaultDataType(SQLDialect dialect, DataType<T> sqlDataType, Class<T> type, Name qualifiedTypeName, String typeName, String castTypeName, Integer precision, Integer scale, Integer length, Nullability nullability, Field<T> defaultValue) {
        this(dialect, sqlDataType, type, null, qualifiedTypeName, typeName, castTypeName, precision, scale, length, nullability, defaultValue);
    }

    DefaultDataType(SQLDialect dialect, DataType<T> sqlDataType, Class<T> type, Binding<?, T> binding, Name qualifiedTypeName, String typeName, String castTypeName, Integer precision, Integer scale, Integer length, Nullability nullability, Field<T> defaultValue) {
        this(dialect, sqlDataType, type, binding, qualifiedTypeName, typeName, castTypeName, precision, scale, length, nullability, false, null, QOM.GenerationOption.DEFAULT, QOM.GenerationLocation.SERVER, null, null, false, defaultValue);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultDataType(SQLDialect dialect, DataType<T> sqlDataType, Class<T> type, Binding<?, T> binding, Name qualifiedTypeName, String typeName, String castTypeName, Integer precision, Integer scale, Integer length, Nullability nullability, boolean readonly, Generator<?, ?, T> generatedAlwaysAs, QOM.GenerationOption generationOption, QOM.GenerationLocation generationLocation, Collation collation, CharacterSet characterSet, boolean identity, Field<T> defaultValue) {
        super(qualifiedTypeName, CommentImpl.NO_COMMENT);
        typeName = typeName == null ? qualifiedTypeName.toString() : typeName;
        this.dialect = dialect;
        this.sqlDataType = (dialect == null && sqlDataType == null) ? this : sqlDataType;
        this.uType = type;
        this.typeName = P_TYPE_NAME.matcher(typeName).replaceAll("").trim();
        this.castTypeName = castTypeName == null ? this.typeName : castTypeName;
        String[] split = P_TYPE_NAME.split(castTypeName == null ? typeName : castTypeName);
        this.castTypePrefix = split.length > 0 ? split[0] : "";
        this.castTypeSuffix = split.length > 1 ? split[1] : "";
        this.nullability = nullability == null ? Nullability.DEFAULT : nullability;
        this.readonly = readonly;
        this.generatedAlwaysAs = generatedAlwaysAs;
        this.generationOption = generationOption == null ? QOM.GenerationOption.DEFAULT : generationOption;
        this.generationLocation = generationLocation == null ? QOM.GenerationLocation.SERVER : generationLocation;
        this.collation = collation;
        this.characterSet = characterSet;
        this.identity = identity;
        this.defaultValue = defaultValue;
        this.precision = integerPrecision(type, precision);
        this.scale = scale;
        this.length = length;
        int ordinal = dialect == null ? SQLDialect.DEFAULT.ordinal() : dialect.family().ordinal();
        if (!TYPES_BY_NAME[ordinal].containsKey(typeName.toUpperCase())) {
            TYPES_BY_NAME[ordinal].putIfAbsent(normalise(typeName), this);
        }
        TYPES_BY_TYPE[ordinal].putIfAbsent(type, this);
        if (sqlDataType != null) {
            TYPES_BY_SQL_DATATYPE[ordinal].putIfAbsent(sqlDataType, this);
        }
        if (dialect == null) {
            SQL_DATATYPES_BY_TYPE.putIfAbsent(type, this);
        }
        this.binding = binding != null ? binding : DefaultBinding.binding(this);
        this.tType = this.binding.converter().fromType();
        if ((this instanceof BuiltInDataType) && !this.tType.isArray()) {
            getArrayDataType();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractDataTypeX
    public DefaultDataType<T> construct(Integer newPrecision, Integer newScale, Integer newLength, Nullability newNullability, boolean newReadonly, Generator<?, ?, T> newGeneratedAlwaysAs, QOM.GenerationOption newGenerationOption, QOM.GenerationLocation newGenerationLocation, Collation newCollation, CharacterSet newCharacterSet, boolean newIdentity, Field<T> newDefaultValue) {
        return new DefaultDataType<>(this, newPrecision, newScale, newLength, newNullability, newReadonly, newGeneratedAlwaysAs, newGenerationOption, newGenerationLocation, newCollation, newCharacterSet, newIdentity, newDefaultValue);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultDataType(AbstractDataType<T> t, Integer precision, Integer scale, Integer length, Nullability nullability, boolean readonly, Generator<?, ?, T> generatedAlwaysAs, QOM.GenerationOption generationOption, QOM.GenerationLocation generationLocation, Collation collation, CharacterSet characterSet, boolean identity, Field<T> defaultValue) {
        super(t.getQualifiedName(), CommentImpl.NO_COMMENT);
        Binding<?, T> binding;
        this.dialect = t.getDialect();
        this.sqlDataType = t.getSQLDataType();
        this.uType = t.uType0();
        this.tType = t.tType0();
        this.typeName = t.typeName0();
        this.castTypeName = t.castTypeName0();
        this.castTypePrefix = t.castTypePrefix0();
        this.castTypeSuffix = t.castTypeSuffix0();
        this.nullability = nullability;
        this.readonly = readonly;
        this.generatedAlwaysAs = generatedAlwaysAs;
        this.generationOption = generationOption;
        this.generationLocation = generationLocation;
        this.collation = collation;
        this.characterSet = characterSet;
        this.identity = identity;
        this.defaultValue = defaultValue;
        this.precision = integerPrecision(this.uType, precision);
        this.scale = scale;
        this.length = length;
        if (t.getBinding() instanceof DefaultBinding.InternalBinding) {
            binding = DefaultBinding.binding(this, t.getBinding().converter());
        } else {
            binding = t.getBinding();
        }
        this.binding = binding;
    }

    private static final Integer integerPrecision(Class<?> type, Integer precision) {
        if (precision == null) {
            if (type == Long.class || type == ULong.class) {
                precision = Integer.valueOf(LONG_PRECISION);
            } else if (type == Integer.class || type == UInteger.class) {
                precision = Integer.valueOf(INTEGER_PRECISION);
            } else if (type == Short.class || type == UShort.class) {
                precision = Integer.valueOf(SHORT_PRECISION);
            } else if (type == Byte.class || type == UByte.class) {
                precision = Integer.valueOf(BYTE_PRECISION);
            }
        }
        return precision;
    }

    @Override // org.jooq.DataType
    public final Nullability nullability() {
        return this.nullability;
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final boolean readonly() {
        return this.readonly;
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final Generator<?, ?, T> generatedAlwaysAsGenerator() {
        if (this.generatedAlwaysAs == null) {
            return null;
        }
        if (this.generatedAlwaysAs instanceof DefaultGenerator) {
            return this.generatedAlwaysAs;
        }
        return new DefaultGenerator(this.generatedAlwaysAs);
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final QOM.GenerationOption generationOption() {
        return this.generationOption;
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final QOM.GenerationLocation generationLocation() {
        return this.generationLocation;
    }

    @Override // org.jooq.DataType
    public final Collation collation() {
        return this.collation;
    }

    @Override // org.jooq.DataType
    public final CharacterSet characterSet() {
        return this.characterSet;
    }

    @Override // org.jooq.DataType
    public final boolean identity() {
        return this.identity;
    }

    @Override // org.jooq.DataType
    public final Field<T> default_() {
        return this.defaultValue;
    }

    @Override // org.jooq.impl.AbstractDataType
    final Integer precision0() {
        return this.precision;
    }

    @Override // org.jooq.impl.AbstractDataType
    final Integer scale0() {
        return this.scale;
    }

    @Override // org.jooq.impl.AbstractDataType
    final Integer length0() {
        return this.length;
    }

    @Override // org.jooq.DataType
    public final DataType<T> getSQLDataType() {
        return this.sqlDataType;
    }

    @Override // org.jooq.DataType
    public final DataType<T> getDataType(Configuration configuration) {
        if (getDialect() == null) {
            DefaultDataType dataType = TYPES_BY_SQL_DATATYPE[configuration.family().ordinal()].get(length0(null).precision0((Integer) null, null));
            if (dataType != null) {
                return dataType.construct(this.precision, this.scale, this.length, this.nullability, this.readonly, (Generator) this.generatedAlwaysAs, this.generationOption, this.generationLocation, this.collation, this.characterSet, this.identity, (Field) this.defaultValue);
            }
            return this;
        }
        if (getDialect().family() == configuration.family()) {
            return this;
        }
        if (getSQLDataType() == null) {
            return this;
        }
        return getSQLDataType().getDataType(configuration);
    }

    @Override // org.jooq.DataType
    public final Class<T> getType() {
        return this.uType;
    }

    @Override // org.jooq.DataType
    public final Binding<?, T> getBinding() {
        return this.binding;
    }

    @Override // org.jooq.impl.AbstractDataType
    final String typeName0() {
        return this.typeName;
    }

    @Override // org.jooq.impl.AbstractDataType
    final String castTypePrefix0() {
        return this.castTypePrefix;
    }

    @Override // org.jooq.impl.AbstractDataType
    final String castTypeSuffix0() {
        return this.castTypeSuffix;
    }

    @Override // org.jooq.impl.AbstractDataType
    final String castTypeName0() {
        return this.castTypeName;
    }

    @Override // org.jooq.impl.AbstractDataType
    final Class<?> tType0() {
        return this.tType;
    }

    @Override // org.jooq.impl.AbstractDataType
    final Class<T> uType0() {
        return this.uType;
    }

    @Override // org.jooq.DataType
    public final SQLDialect getDialect() {
        return this.dialect;
    }

    public static final DataType<Object> getDefaultDataType(String typeName) {
        return new DefaultDataType(SQLDialect.DEFAULT, Object.class, typeName, typeName);
    }

    public static final DataType<Object> getDefaultDataType(SQLDialect dialect, String typeName) {
        return new DefaultDataType(dialect, Object.class, typeName, typeName);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static final DataType<?> getDataType(SQLDialect dialect, String typeName) {
        if (dialect == null) {
            dialect = SQLDialect.DEFAULT;
        }
        SQLDialect family = dialect.family();
        int ordinal = family.ordinal();
        String upper = typeName.toUpperCase();
        DataType result = TYPES_BY_NAME[ordinal].get(upper);
        if (result == null) {
            Map<String, DefaultDataType<?>> map = TYPES_BY_NAME[ordinal];
            String normalised = normalise(typeName);
            result = map.get(normalised);
            if (result == null) {
                result = TYPES_BY_NAME[SQLDialect.DEFAULT.ordinal()].get(normalised);
                boolean arrayCheck = result == null || (result.isArray() && hasLengthPrecisionOrScale(result.getArrayComponentDataType()));
                if (result == null && "INT".equals(normalised)) {
                    result = TYPES_BY_NAME[SQLDialect.DEFAULT.ordinal()].get("INTEGER");
                } else if (arrayCheck && SUPPORT_POSTGRES_ARRAY_NOTATION.contains(dialect) && typeName.charAt(0) == '_') {
                    result = getDataType(dialect, typeName.substring(1)).getArrayDataType();
                } else if (arrayCheck && SUPPORT_POSTGRES_ARRAY_NOTATION.contains(dialect) && typeName.endsWith(ClassUtils.ARRAY_SUFFIX)) {
                    result = getDataType(dialect, typeName.substring(0, typeName.length() - 2)).getArrayDataType();
                } else if (arrayCheck && SUPPORT_HSQLDB_ARRAY_NOTATION.contains(dialect) && upper.endsWith(" ARRAY")) {
                    result = getDataType(dialect, typeName.substring(0, typeName.length() - 6)).getArrayDataType();
                } else if (arrayCheck && SUPPORT_HSQLDB_ARRAY_NOTATION.contains(dialect) && upper.equals("ARRAY")) {
                    result = SQLDataType.OTHER.getArrayDataType();
                } else if (arrayCheck && SUPPORT_TRINO_ARRAY_NOTATION.contains(dialect) && upper.startsWith("ARRAY(")) {
                    result = getDataType(dialect, typeName.substring(6, typeName.length() - 1)).getArrayDataType();
                }
                if (result == null) {
                    throw new SQLDialectNotSupportedException("Type " + typeName + " is not supported in dialect " + String.valueOf(dialect), false);
                }
            }
        }
        if (hasLengthPrecisionOrScale(result)) {
            Matcher m = P_PRECISION_SCALE.matcher(typeName);
            if (m.find()) {
                String g1 = m.group(1);
                String g2 = m.group(2);
                int i1 = Integer.parseInt(g1);
                int i2 = g2 != null ? Integer.parseInt(g2) : 0;
                result = patchPrecisionAndScale(dialect, i1, i2, result);
            }
        }
        return result;
    }

    private static final boolean hasLengthPrecisionOrScale(DataType<?> result) {
        return result.hasLength() || result.hasPrecision();
    }

    public static final DataType<?> getDataType(SQLDialect dialect, SQLType sqlType) {
        Integer i = sqlType.getVendorTypeNumber();
        return i == null ? SQLDataType.OTHER : getDataType(dialect, i.intValue());
    }

    public static final DataType<?> getDataType(SQLDialect dialect, int sqlType) {
        switch (sqlType) {
            case -16:
                return SQLDataType.LONGNVARCHAR;
            case -15:
                return SQLDataType.NCHAR;
            case -9:
                return SQLDataType.NVARCHAR;
            case -7:
                return SQLDataType.BIT;
            case -6:
                return SQLDataType.TINYINT;
            case -5:
                return SQLDataType.BIGINT;
            case -4:
                return SQLDataType.LONGVARBINARY;
            case -3:
                return SQLDataType.VARBINARY;
            case -2:
                return SQLDataType.BINARY;
            case -1:
                return SQLDataType.LONGVARCHAR;
            case 1:
                return SQLDataType.CHAR;
            case 2:
                return SQLDataType.NUMERIC;
            case 3:
                return SQLDataType.DECIMAL;
            case 4:
                return SQLDataType.INTEGER;
            case 5:
                return SQLDataType.SMALLINT;
            case 6:
                return SQLDataType.FLOAT;
            case 7:
                return SQLDataType.REAL;
            case 8:
                return SQLDataType.DOUBLE;
            case 12:
                return SQLDataType.VARCHAR;
            case 16:
                return SQLDataType.BOOLEAN;
            case 91:
                return SQLDataType.DATE;
            case 92:
                return SQLDataType.TIME;
            case 93:
                return SQLDataType.TIMESTAMP;
            case 2002:
                return SQLDataType.RECORD;
            case 2004:
                return SQLDataType.BLOB;
            case 2005:
                return SQLDataType.CLOB;
            case 2009:
                return SQLDataType.XML;
            case 2011:
                return SQLDataType.NCLOB;
            case 2012:
                return SQLDataType.RESULT;
            case 2013:
                return SQLDataType.TIMEWITHTIMEZONE;
            case 2014:
                return SQLDataType.TIMESTAMPWITHTIMEZONE;
            default:
                return SQLDataType.OTHER;
        }
    }

    public static final <T> DataType<T> getDataType(SQLDialect dialect, Class<T> type) {
        return getDataType(dialect, type, null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultDataType$DiscouragedStaticTypeRegistryUsage.class */
    public static final class DiscouragedStaticTypeRegistryUsage extends RuntimeException {
        private DiscouragedStaticTypeRegistryUsage() {
        }
    }

    public static final <T> DataType<T> getDataType(SQLDialect dialect, Class<T> type, DataType<T> fallbackDataType) {
        return check(getDataType0(dialect, type, fallbackDataType));
    }

    private static final <T> DataType<T> check(DataType<T> result) {
        if ((result instanceof ConvertedDataType) || (result instanceof LegacyConvertedDataType)) {
            getDataType.warn("Static type registry", "The deprecated static type registry was being accessed for a non-built-in data type: " + String.valueOf(result) + ". It is strongly recommended not looking up DataType<T> references from Class<T> references by relying on the internal static type registry. See https://github.com/jOOQ/jOOQ/issues/15286 for details.", new DiscouragedStaticTypeRegistryUsage());
        }
        if (result instanceof ArrayDataType) {
            ArrayDataType<?> a = (ArrayDataType) result;
            check(a.elementType);
        }
        return result;
    }

    private static final <T> DataType<T> getDataType0(SQLDialect sQLDialect, Class<T> cls, DataType<T> dataType) {
        Class wrapper = Reflect.wrapper(cls);
        if (byte[].class != wrapper && wrapper.isArray()) {
            return getDataType(sQLDialect, wrapper.getComponentType()).getArrayDataType();
        }
        DefaultDataType<?> defaultDataType = null;
        if (sQLDialect != null) {
            defaultDataType = TYPES_BY_TYPE[sQLDialect.family().ordinal()].get(wrapper);
        }
        if (defaultDataType == null) {
            try {
                if (QualifiedRecord.class.isAssignableFrom(wrapper)) {
                    return (DataType<T>) Tools.getRecordQualifier((Class<?>) wrapper).getDataType();
                }
                if (EnumType.class.isAssignableFrom(wrapper)) {
                    return (DataType<T>) SQLDataType.VARCHAR.asEnumDataType(wrapper);
                }
            } catch (Exception e) {
                throw new MappingException("Cannot create instance of " + String.valueOf(wrapper), e);
            }
        }
        if (defaultDataType == null) {
            if (SQL_DATATYPES_BY_TYPE.get(wrapper) != null) {
                return SQL_DATATYPES_BY_TYPE.get(wrapper);
            }
            if (dataType != null) {
                return dataType;
            }
            if (Date.class == wrapper) {
                return (DataType<T>) SQLDataType.TIMESTAMP;
            }
            throw new SQLDialectNotSupportedException("Type " + String.valueOf(wrapper) + " is not supported in dialect " + String.valueOf(sQLDialect));
        }
        return defaultDataType;
    }

    public static final String normalise(String typeName) {
        return P_NORMALISE.matcher(typeName.toUpperCase()).replaceAll("");
    }

    public static final DataType<?> getDataType(SQLDialect dialect, String t, int p, int s) throws SQLDialectNotSupportedException {
        return getDataType(dialect, t, p, s, true);
    }

    public static final DataType<?> getDataType(SQLDialect dialect, String t, int p, int s, boolean forceIntegerTypesOnZeroScaleDecimals) throws SQLDialectNotSupportedException {
        DataType<?> result = getDataType(dialect, t);
        boolean array = result.isArray();
        if (array) {
            result = result.getArrayComponentDataType();
        }
        if (forceIntegerTypesOnZeroScaleDecimals && result.getType() == BigDecimal.class) {
            result = getDataType(dialect, getNumericClass(p, s));
        }
        if (result.getSQLDataType() != null) {
            if (result.lengthDefined()) {
                result = result.getSQLDataType().length(result.length());
            } else if (result.scaleDefined()) {
                result = result.getSQLDataType().precision(result.precision(), result.scale());
            } else if (result.precisionDefined()) {
                result = result.getSQLDataType().precision(result.precision());
            } else {
                result = result.getSQLDataType();
            }
        }
        if ((!result.lengthDefined() && !result.precisionDefined() && !result.scaleDefined()) || p > 0 || s > 0) {
            result = patchPrecisionAndScale(dialect, p, s, result);
        }
        if (array) {
            result = result.getArrayDataType();
        }
        return result;
    }

    private static final DataType<?> patchPrecisionAndScale(SQLDialect dialect, int p, int s, DataType<?> result) {
        if (result.hasPrecision() && result.hasScale()) {
            result = result.precision(p, s);
        } else if (result.hasPrecision() && result.isDateTime()) {
            if (ENCODED_TIMESTAMP_PRECISION.contains(dialect)) {
                result = result.precision(decodeTimestampPrecision(p));
            } else if (!NO_SUPPORT_TIMESTAMP_PRECISION.contains(dialect)) {
                result = result.precision(s);
            }
        } else if (result.hasPrecision()) {
            result = result.precision(p);
        } else if (result.hasLength()) {
            result = result.length(p);
        }
        return result;
    }

    private static final int decodeTimestampPrecision(int precision) {
        return Math.max(0, precision - 20);
    }

    public static final Class<?> getType(SQLDialect dialect, String t, int p, int s) throws SQLDialectNotSupportedException {
        return getDataType(dialect, t, p, s).getType();
    }

    private static final Class<?> getNumericClass(int precision, int scale) {
        if (scale == 0 && precision != 0) {
            if (precision < BYTE_PRECISION) {
                return Byte.class;
            }
            if (precision < SHORT_PRECISION) {
                return Short.class;
            }
            if (precision < INTEGER_PRECISION) {
                return Integer.class;
            }
            if (precision < LONG_PRECISION) {
                return Long.class;
            }
            return BigInteger.class;
        }
        return BigDecimal.class;
    }

    static final Collection<Class<?>> types() {
        return Collections.unmodifiableCollection(SQL_DATATYPES_BY_TYPE.keySet());
    }

    static final Collection<DefaultDataType<?>> dataTypes() {
        return Collections.unmodifiableCollection(SQL_DATATYPES_BY_TYPE.values());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final DataType<?> set(DataType<?> d, Integer l, Integer p, Integer s) {
        if (l != null) {
            d = d.length(l.intValue());
        }
        if (p != null) {
            if (s != null) {
                d = d.precision(p.intValue(), s.intValue());
            } else {
                d = d.precision(p.intValue());
            }
        }
        return d;
    }
}

package org.jooq;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.Converters;
import org.jooq.exception.DataTypeException;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/DataType.class */
public interface DataType<T> extends Named {
    @Nullable
    DataType<T> getSQLDataType();

    @NotNull
    DataType<T> getDataType(Configuration configuration);

    int getSQLType();

    int getSQLType(Configuration configuration);

    @NotNull
    Binding<?, T> getBinding();

    @NotNull
    ContextConverter<?, T> getConverter();

    @NotNull
    Class<T> getType();

    @Nullable
    Domain<T> getDomain();

    @Nullable
    Row getRow();

    @Nullable
    Class<? extends Record> getRecordType();

    @NotNull
    Class<T[]> getArrayType();

    @NotNull
    DataType<T[]> getArrayDataType() throws DataTypeException;

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    DataType<T[]> array() throws DataTypeException;

    @Nullable
    Class<?> getArrayComponentType();

    @Nullable
    DataType<?> getArrayComponentDataType();

    @NotNull
    Class<?> getArrayBaseType();

    @NotNull
    DataType<?> getArrayBaseDataType();

    @NotNull
    <E extends EnumType> DataType<E> asEnumDataType(Class<E> cls);

    @NotNull
    <U> DataType<U> asConvertedDataType(Converter<? super T, U> converter);

    @NotNull
    <U> DataType<U> asConvertedDataType(Binding<? super T, U> binding);

    @NotNull
    String getTypeName();

    @NotNull
    String getTypeName(Configuration configuration);

    @NotNull
    String getCastTypeName();

    @NotNull
    String getCastTypeName(Configuration configuration);

    @Nullable
    SQLDialect getDialect();

    T convert(Object obj);

    T[] convert(Object... objArr);

    @NotNull
    List<T> convert(Collection<?> collection);

    @Support
    @NotNull
    DataType<T> nullability(Nullability nullability);

    @NotNull
    Nullability nullability();

    @Support
    @NotNull
    DataType<T> nullable(boolean z);

    boolean nullable();

    @Support
    @NotNull
    DataType<T> readonly(boolean z);

    boolean readonly();

    boolean readonlyInternal();

    boolean readonlyInternal(Configuration configuration);

    boolean computed();

    boolean computedOnServer();

    boolean computedOnServer(Configuration configuration);

    boolean computedOnClient();

    boolean computedOnClient(Configuration configuration);

    boolean computedOnClientStored();

    boolean computedOnClientStored(Configuration configuration);

    boolean computedOnClientStoredOn(GeneratorStatementType generatorStatementType);

    boolean computedOnClientStoredOn(GeneratorStatementType generatorStatementType, Configuration configuration);

    boolean computedOnClientVirtual();

    boolean computedOnClientVirtual(Configuration configuration);

    @Support({SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @NotNull
    DataType<T> generatedAlwaysAs(T t);

    @Support({SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @NotNull
    DataType<T> generatedAlwaysAs(Field<T> field);

    @NotNull
    DataType<T> generatedAlwaysAs(Generator<?, ?, T> generator);

    @Nullable
    Field<T> generatedAlwaysAs();

    @Nullable
    Generator<?, ?, T> generatedAlwaysAsGenerator();

    @Support({SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @NotNull
    DataType<T> stored();

    @Support({SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @NotNull
    DataType<T> virtual();

    @Support({SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @NotNull
    DataType<T> generationOption(QOM.GenerationOption generationOption);

    @Support
    @NotNull
    QOM.GenerationOption generationOption();

    @Support
    @NotNull
    DataType<T> generationLocation(QOM.GenerationLocation generationLocation);

    @Support
    @NotNull
    QOM.GenerationLocation generationLocation();

    @Support
    @NotNull
    DataType<T> null_();

    @Support
    @NotNull
    DataType<T> notNull();

    @Support({SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    DataType<T> collation(Collation collation);

    @Nullable
    Collation collation();

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL})
    @NotNull
    DataType<T> characterSet(CharacterSet characterSet);

    @Nullable
    CharacterSet characterSet();

    @Support({SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    DataType<T> identity(boolean z);

    boolean identity();

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    DataType<T> defaultValue(T t);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    DataType<T> defaultValue(Field<T> field);

    @Nullable
    Field<T> defaultValue();

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    DataType<T> default_(T t);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    DataType<T> default_(Field<T> field);

    @Nullable
    Field<T> default_();

    boolean defaulted();

    @Support
    @NotNull
    DataType<T> precision(int i);

    @Support
    @NotNull
    DataType<T> precision(int i, int i2);

    int precision();

    boolean hasPrecision();

    boolean precisionDefined();

    @Support
    @NotNull
    DataType<T> scale(int i);

    int scale();

    boolean hasScale();

    boolean scaleDefined();

    @Support
    @NotNull
    DataType<T> length(int i);

    int length();

    boolean hasLength();

    boolean lengthDefined();

    boolean isNumeric();

    boolean isInteger();

    boolean isFloat();

    boolean isBoolean();

    boolean isString();

    boolean isNString();

    boolean isDateTime();

    boolean isDate();

    boolean isTimestamp();

    boolean isTime();

    boolean isTemporal();

    boolean isInterval();

    boolean isBinary();

    boolean isLob();

    boolean isArray();

    boolean isAssociativeArray();

    boolean isEmbeddable();

    boolean isUDT();

    boolean isRecord();

    boolean isMultiset();

    boolean isEnum();

    boolean isJSON();

    boolean isXML();

    boolean isSpatial();

    boolean isUUID();

    boolean isOther();

    @NotNull
    default <U> DataType<U> asConvertedDataType(Class<U> toType, Function<? super T, ? extends U> from, Function<? super U, ? extends T> to) {
        return asConvertedDataType(Converter.of(getType(), toType, from, to));
    }

    @NotNull
    default <U> DataType<U> asConvertedDataTypeFrom(Class<U> toType, Function<? super T, ? extends U> from) {
        return asConvertedDataType(Converter.from(getType(), toType, from));
    }

    @NotNull
    default <U> DataType<U> asConvertedDataTypeFrom(Function<? super T, ? extends U> from) {
        return asConvertedDataType(Converter.from(getType(), Converters.UnknownType.class, from));
    }

    @NotNull
    default <U> DataType<U> asConvertedDataTypeTo(Class<U> toType, Function<? super U, ? extends T> to) {
        return asConvertedDataType(Converter.to(getType(), toType, to));
    }

    @NotNull
    default <U> DataType<U> asConvertedDataTypeTo(Function<? super U, ? extends T> to) {
        return asConvertedDataType(Converter.to(getType(), Converters.UnknownType.class, to));
    }
}

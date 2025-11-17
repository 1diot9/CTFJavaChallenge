package org.jooq.impl;

import java.io.Serializable;
import java.io.StringReader;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Struct;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import org.jooq.Attachable;
import org.jooq.Binding;
import org.jooq.BindingGetResultSetContext;
import org.jooq.BindingGetSQLInputContext;
import org.jooq.BindingGetStatementContext;
import org.jooq.BindingRegisterContext;
import org.jooq.BindingSQLContext;
import org.jooq.BindingScope;
import org.jooq.BindingSetSQLOutputContext;
import org.jooq.BindingSetStatementContext;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.ContextConverter;
import org.jooq.Converter;
import org.jooq.Converters;
import org.jooq.DataType;
import org.jooq.EnumType;
import org.jooq.ExecuteScope;
import org.jooq.Field;
import org.jooq.Geography;
import org.jooq.Geometry;
import org.jooq.JSON;
import org.jooq.JSONB;
import org.jooq.Param;
import org.jooq.QualifiedRecord;
import org.jooq.QueryPart;
import org.jooq.Record;
import org.jooq.RecordQualifier;
import org.jooq.RenderContext;
import org.jooq.Result;
import org.jooq.Row;
import org.jooq.RowId;
import org.jooq.SQLDialect;
import org.jooq.Schema;
import org.jooq.Scope;
import org.jooq.Source;
import org.jooq.UDTRecord;
import org.jooq.XML;
import org.jooq.conf.NestedCollectionEmulation;
import org.jooq.conf.ParamType;
import org.jooq.exception.ControlFlowSignal;
import org.jooq.exception.DataTypeException;
import org.jooq.exception.MappingException;
import org.jooq.exception.SQLDialectNotSupportedException;
import org.jooq.impl.R2DBC;
import org.jooq.tools.JooqLogger;
import org.jooq.tools.Longs;
import org.jooq.tools.StringUtils;
import org.jooq.tools.jdbc.JDBCUtils;
import org.jooq.tools.jdbc.MockArray;
import org.jooq.tools.reflect.Reflect;
import org.jooq.types.DayToSecond;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.jooq.types.ULong;
import org.jooq.types.UShort;
import org.jooq.types.YearToMonth;
import org.jooq.types.YearToSecond;
import org.jooq.util.postgres.PostgresUtils;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.util.ClassUtils;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultBinding.class */
public class DefaultBinding<T, U> implements Binding<T, U> {
    static final JooqLogger log = JooqLogger.getLogger((Class<?>) DefaultBinding.class);
    private static final Set<SQLDialect> REQUIRE_JDBC_DATE_LITERAL = SQLDialect.supportedBy(SQLDialect.MYSQL);
    private static final long PG_DATE_POSITIVE_INFINITY = 9223372036825200000L;
    private static final long PG_DATE_NEGATIVE_INFINITY = -9223372036832400000L;
    final Binding<T, U> delegate;

    private static /* synthetic */ Object $deserializeLambda$(SerializedLambda lambda) {
        String implMethodName = lambda.getImplMethodName();
        boolean z = -1;
        switch (implMethodName.hashCode()) {
            case 759112349:
                if (implMethodName.equals("lambda$binding$2c5dc1fb$10")) {
                    z = 2;
                    break;
                }
                break;
            case 759112350:
                if (implMethodName.equals("lambda$binding$2c5dc1fb$11")) {
                    z = true;
                    break;
                }
                break;
            case 759112351:
                if (implMethodName.equals("lambda$binding$2c5dc1fb$12")) {
                    z = false;
                    break;
                }
                break;
            case 759112352:
                if (implMethodName.equals("lambda$binding$2c5dc1fb$13")) {
                    z = 13;
                    break;
                }
                break;
            case 759112353:
                if (implMethodName.equals("lambda$binding$2c5dc1fb$14")) {
                    z = 11;
                    break;
                }
                break;
            case 1548508147:
                if (implMethodName.equals("lambda$binding$2c5dc1fb$1")) {
                    z = 12;
                    break;
                }
                break;
            case 1548508148:
                if (implMethodName.equals("lambda$binding$2c5dc1fb$2")) {
                    z = 7;
                    break;
                }
                break;
            case 1548508149:
                if (implMethodName.equals("lambda$binding$2c5dc1fb$3")) {
                    z = 8;
                    break;
                }
                break;
            case 1548508150:
                if (implMethodName.equals("lambda$binding$2c5dc1fb$4")) {
                    z = 9;
                    break;
                }
                break;
            case 1548508151:
                if (implMethodName.equals("lambda$binding$2c5dc1fb$5")) {
                    z = 10;
                    break;
                }
                break;
            case 1548508152:
                if (implMethodName.equals("lambda$binding$2c5dc1fb$6")) {
                    z = 3;
                    break;
                }
                break;
            case 1548508153:
                if (implMethodName.equals("lambda$binding$2c5dc1fb$7")) {
                    z = 4;
                    break;
                }
                break;
            case 1548508154:
                if (implMethodName.equals("lambda$binding$2c5dc1fb$8")) {
                    z = 5;
                    break;
                }
                break;
            case 1548508155:
                if (implMethodName.equals("lambda$binding$2c5dc1fb$9")) {
                    z = 6;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
                if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("java/util/function/BiFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("org/jooq/impl/DefaultBinding") && lambda.getImplMethodSignature().equals("(Lorg/jooq/types/ULong;Lorg/jooq/ConverterContext;)Ljava/math/BigInteger;")) {
                    return (t, x) -> {
                        return t.toBigInteger();
                    };
                }
                break;
            case true:
                if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("java/util/function/BiFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("org/jooq/impl/DefaultBinding") && lambda.getImplMethodSignature().equals("(Ljava/math/BigInteger;Lorg/jooq/ConverterContext;)Lorg/jooq/types/ULong;")) {
                    return (t2, x2) -> {
                        return ULong.valueOf(t2);
                    };
                }
                break;
            case true:
                if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("java/util/function/BiFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("org/jooq/impl/DefaultBinding") && lambda.getImplMethodSignature().equals("(Lorg/jooq/types/UInteger;Lorg/jooq/ConverterContext;)Ljava/lang/Long;")) {
                    return (t3, x3) -> {
                        return Long.valueOf(t3.longValue());
                    };
                }
                break;
            case true:
                if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("java/util/function/BiFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("org/jooq/impl/DefaultBinding") && lambda.getImplMethodSignature().equals("(Ljava/time/LocalTime;Lorg/jooq/ConverterContext;)Ljava/sql/Time;")) {
                    return (t4, x4) -> {
                        return Time.valueOf(t4);
                    };
                }
                break;
            case true:
                if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("java/util/function/BiFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("org/jooq/impl/DefaultBinding") && lambda.getImplMethodSignature().equals("(Ljava/lang/Short;Lorg/jooq/ConverterContext;)Lorg/jooq/types/UByte;")) {
                    return (t5, x5) -> {
                        return UByte.valueOf(t5.shortValue());
                    };
                }
                break;
            case true:
                if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("java/util/function/BiFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("org/jooq/impl/DefaultBinding") && lambda.getImplMethodSignature().equals("(Lorg/jooq/types/UByte;Lorg/jooq/ConverterContext;)Ljava/lang/Short;")) {
                    return (t6, x6) -> {
                        return Short.valueOf(t6.shortValue());
                    };
                }
                break;
            case true:
                if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("java/util/function/BiFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("org/jooq/impl/DefaultBinding") && lambda.getImplMethodSignature().equals("(Ljava/lang/Long;Lorg/jooq/ConverterContext;)Lorg/jooq/types/UInteger;")) {
                    return (t7, x7) -> {
                        return UInteger.valueOf(t7.longValue());
                    };
                }
                break;
            case true:
                if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("java/util/function/BiFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("org/jooq/impl/DefaultBinding") && lambda.getImplMethodSignature().equals("(Ljava/time/LocalDate;Lorg/jooq/ConverterContext;)Ljava/sql/Date;")) {
                    return (t8, x8) -> {
                        return Date.valueOf(t8);
                    };
                }
                break;
            case true:
                if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("java/util/function/BiFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("org/jooq/impl/DefaultBinding") && lambda.getImplMethodSignature().equals("(Ljava/sql/Timestamp;Lorg/jooq/ConverterContext;)Ljava/time/LocalDateTime;")) {
                    return (t9, x9) -> {
                        return t9.toLocalDateTime();
                    };
                }
                break;
            case true:
                if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("java/util/function/BiFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("org/jooq/impl/DefaultBinding") && lambda.getImplMethodSignature().equals("(Ljava/time/LocalDateTime;Lorg/jooq/ConverterContext;)Ljava/sql/Timestamp;")) {
                    return (t10, x10) -> {
                        return Timestamp.valueOf(t10);
                    };
                }
                break;
            case true:
                if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("java/util/function/BiFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("org/jooq/impl/DefaultBinding") && lambda.getImplMethodSignature().equals("(Ljava/sql/Time;Lorg/jooq/ConverterContext;)Ljava/time/LocalTime;")) {
                    return (t11, x11) -> {
                        return t11.toLocalTime();
                    };
                }
                break;
            case true:
                if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("java/util/function/BiFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("org/jooq/impl/DefaultBinding") && lambda.getImplMethodSignature().equals("(Lorg/jooq/types/UShort;Lorg/jooq/ConverterContext;)Ljava/lang/Integer;")) {
                    return (t12, x12) -> {
                        return Integer.valueOf(t12.intValue());
                    };
                }
                break;
            case true:
                if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("java/util/function/BiFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("org/jooq/impl/DefaultBinding") && lambda.getImplMethodSignature().equals("(Ljava/sql/Date;Lorg/jooq/ConverterContext;)Ljava/time/LocalDate;")) {
                    return (t13, x13) -> {
                        return t13.toLocalDate();
                    };
                }
                break;
            case true:
                if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("java/util/function/BiFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("org/jooq/impl/DefaultBinding") && lambda.getImplMethodSignature().equals("(Ljava/lang/Integer;Lorg/jooq/ConverterContext;)Lorg/jooq/types/UShort;")) {
                    return (t14, x14) -> {
                        return UShort.valueOf(t14.intValue());
                    };
                }
                break;
        }
        throw new IllegalArgumentException("Invalid lambda deserialization");
    }

    public static final <T, U> Binding<T, U> binding(Converter<T, U> converter) {
        return binding(DefaultDataType.getDataType(SQLDialect.DEFAULT, converter.fromType()), converter);
    }

    public static final <T> Binding<T, T> binding(DataType<T> dataType) {
        return binding(dataType, Converters.identity(dataType.getType()));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T, U> Binding<T, U> binding(DataType<T> dataType, Converter<T, U> converter) {
        Class<T> fromType = converter.fromType();
        if (fromType == BigDecimal.class) {
            return new DefaultBigDecimalBinding(dataType, converter);
        }
        if (fromType == BigInteger.class) {
            return new DefaultBigIntegerBinding(dataType, converter);
        }
        if (fromType == Blob.class) {
            return new DefaultBlobBinding(dataType, converter);
        }
        if (fromType == Boolean.class) {
            return new DefaultBooleanBinding(dataType, converter);
        }
        if (fromType == Byte.class || fromType == Byte.TYPE) {
            return new DefaultByteBinding(dataType, converter);
        }
        if (fromType == byte[].class) {
            return new DefaultBytesBinding(dataType, converter);
        }
        if (fromType == Clob.class) {
            return new DefaultClobBinding(dataType, converter);
        }
        if (fromType == Date.class) {
            return new DefaultDateBinding(dataType, converter);
        }
        if (fromType == DayToSecond.class) {
            return new DefaultDayToSecondBinding(dataType, converter);
        }
        if (fromType == Double.class || fromType == Double.TYPE) {
            return new DefaultDoubleBinding(dataType, converter);
        }
        if (fromType == Float.class || fromType == Float.TYPE) {
            return new DefaultFloatBinding(dataType, converter);
        }
        if (fromType == Geometry.class) {
            return new CommercialOnlyBinding(dataType, converter);
        }
        if (fromType == Geography.class) {
            return new CommercialOnlyBinding(dataType, converter);
        }
        if (fromType == Integer.class || fromType == Integer.TYPE) {
            return new DefaultIntegerBinding(dataType, converter);
        }
        if (fromType == JSON.class) {
            return new DefaultJSONBinding(dataType, converter);
        }
        if (fromType == JSONB.class) {
            return new DefaultJSONBBinding(dataType, converter);
        }
        if (fromType == XML.class) {
            return new DefaultXMLBinding(dataType, converter);
        }
        if (fromType == LocalDate.class) {
            return new DelegatingBinding(dataType, ContextConverter.ofNullable(Date.class, LocalDate.class, (BiFunction) ((Serializable) (t13, x13) -> {
                return t13.toLocalDate();
            }), (BiFunction) ((Serializable) (t8, x8) -> {
                return Date.valueOf(t8);
            })), (ContextConverter) converter, c -> {
                return new DefaultDateBinding(SQLDataType.DATE, c);
            });
        }
        if (fromType == LocalDateTime.class) {
            return new DelegatingBinding(dataType, ContextConverter.ofNullable(Timestamp.class, LocalDateTime.class, (BiFunction) ((Serializable) (t9, x9) -> {
                return t9.toLocalDateTime();
            }), (BiFunction) ((Serializable) (t10, x10) -> {
                return Timestamp.valueOf(t10);
            })), (ContextConverter) converter, c2 -> {
                return new DefaultTimestampBinding(SQLDataType.TIMESTAMP, c2);
            });
        }
        if (fromType == LocalTime.class) {
            return new DelegatingBinding(dataType, ContextConverter.ofNullable(Time.class, LocalTime.class, (BiFunction) ((Serializable) (t11, x11) -> {
                return t11.toLocalTime();
            }), (BiFunction) ((Serializable) (t4, x4) -> {
                return Time.valueOf(t4);
            })), (ContextConverter) converter, c3 -> {
                return new DefaultTimeBinding(SQLDataType.TIME, c3);
            });
        }
        if (fromType == Long.class || fromType == Long.TYPE) {
            return new DefaultLongBinding(dataType, converter);
        }
        if (fromType == OffsetDateTime.class) {
            return new DefaultOffsetDateTimeBinding(dataType, converter);
        }
        if (fromType == OffsetTime.class) {
            return new DefaultOffsetTimeBinding(dataType, converter);
        }
        if (fromType == Instant.class) {
            return new DefaultInstantBinding(dataType, converter);
        }
        if (fromType == RowId.class) {
            return new DefaultRowIdBinding(dataType, converter);
        }
        if (fromType == Short.class || fromType == Short.TYPE) {
            return new DefaultShortBinding(dataType, converter);
        }
        if (fromType == String.class) {
            if (dataType.isNString()) {
                return new DefaultNStringBinding(dataType, converter);
            }
            return new DefaultStringBinding(dataType, converter);
        }
        if (fromType == Time.class) {
            return new DefaultTimeBinding(dataType, converter);
        }
        if (fromType == Timestamp.class) {
            return new DefaultTimestampBinding(dataType, converter);
        }
        if (fromType == java.util.Date.class) {
            return new DefaultTimestampBinding(dataType, Converters.of(TimestampToJavaUtilDateConverter.INSTANCE, converter));
        }
        if (fromType == UByte.class) {
            return new DelegatingBinding(dataType, ContextConverter.ofNullable(Short.class, UByte.class, (BiFunction) ((Serializable) (t5, x5) -> {
                return UByte.valueOf(t5.shortValue());
            }), (BiFunction) ((Serializable) (t6, x6) -> {
                return Short.valueOf(t6.shortValue());
            })), (ContextConverter) converter, c4 -> {
                return new DefaultShortBinding(SQLDataType.SMALLINT, c4);
            });
        }
        if (fromType == UInteger.class) {
            return new DelegatingBinding(dataType, ContextConverter.ofNullable(Long.class, UInteger.class, (BiFunction) ((Serializable) (t7, x7) -> {
                return UInteger.valueOf(t7.longValue());
            }), (BiFunction) ((Serializable) (t3, x3) -> {
                return Long.valueOf(t3.longValue());
            })), (ContextConverter) converter, c5 -> {
                return new DefaultLongBinding(SQLDataType.BIGINT, c5);
            });
        }
        if (fromType == ULong.class) {
            return new DelegatingBinding(dataType, ContextConverter.ofNullable(BigInteger.class, ULong.class, (BiFunction) ((Serializable) (t2, x2) -> {
                return ULong.valueOf(t2);
            }), (BiFunction) ((Serializable) (t, x) -> {
                return t.toBigInteger();
            })), (ContextConverter) converter, c6 -> {
                return new DefaultBigIntegerBinding(SQLDataType.DECIMAL_INTEGER, c6);
            });
        }
        if (fromType == UShort.class) {
            return new DelegatingBinding(dataType, ContextConverter.ofNullable(Integer.class, UShort.class, (BiFunction) ((Serializable) (t14, x14) -> {
                return UShort.valueOf(t14.intValue());
            }), (BiFunction) ((Serializable) (t12, x12) -> {
                return Integer.valueOf(t12.intValue());
            })), (ContextConverter) converter, c7 -> {
                return new DefaultIntegerBinding(SQLDataType.INTEGER, c7);
            });
        }
        if (fromType == UUID.class) {
            return new DefaultUUIDBinding(dataType, converter);
        }
        if (fromType == YearToSecond.class) {
            return new DefaultYearToSecondBinding(dataType, converter);
        }
        if (fromType == YearToMonth.class) {
            return new DefaultYearToMonthBinding(dataType, converter);
        }
        if (fromType == Year.class) {
            return new DefaultYearBinding(dataType, converter);
        }
        if (fromType.isArray()) {
            return new DefaultArrayBinding(dataType, converter);
        }
        if (EnumType.class.isAssignableFrom(fromType)) {
            return new DefaultEnumTypeBinding(dataType, converter);
        }
        if (Record.class.isAssignableFrom(fromType)) {
            return new DefaultRecordBinding(dataType, converter);
        }
        if (Result.class.isAssignableFrom(fromType)) {
            return new DefaultResultBinding(dataType, converter);
        }
        return new DefaultOtherBinding(dataType, converter);
    }

    @Deprecated(forRemoval = true)
    public DefaultBinding(Converter<T, U> converter) {
        this.delegate = binding(converter);
    }

    public DefaultBinding(Binding<T, U> delegate) {
        this.delegate = delegate;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    public static final <T, X, U> Binding<T, U> newBinding(final Converter<X, U> converter, DataType<T> dataType, final Binding<T, X> binding) {
        Binding<?, T> binding2;
        if (converter == null && binding == 0) {
            binding2 = dataType.getBinding();
        } else if (converter == null) {
            binding2 = binding;
        } else if (binding == 0) {
            binding2 = binding(dataType, ContextConverter.scoped(converter));
        } else {
            binding2 = new Binding<T, U>() { // from class: org.jooq.impl.DefaultBinding.1
                final ContextConverter<T, U> theConverter;

                {
                    this.theConverter = Converters.of(Binding.this.converter(), converter);
                }

                @Override // org.jooq.Binding
                public Converter<T, U> converter() {
                    return this.theConverter;
                }

                @Override // org.jooq.Binding
                public void sql(BindingSQLContext<U> ctx) throws SQLException {
                    Binding.this.sql(ctx.convert(converter));
                }

                @Override // org.jooq.Binding
                public void register(BindingRegisterContext<U> ctx) throws SQLException {
                    Binding.this.register(ctx.convert(converter));
                }

                @Override // org.jooq.Binding
                public void set(BindingSetStatementContext<U> ctx) throws SQLException {
                    Binding.this.set(ctx.convert(converter));
                }

                @Override // org.jooq.Binding
                public void set(BindingSetSQLOutputContext<U> ctx) throws SQLException {
                    Binding.this.set(ctx.convert(converter));
                }

                @Override // org.jooq.Binding
                public void get(BindingGetResultSetContext<U> ctx) throws SQLException {
                    Binding.this.get(ctx.convert(converter));
                }

                @Override // org.jooq.Binding
                public void get(BindingGetStatementContext<U> ctx) throws SQLException {
                    Binding.this.get(ctx.convert(converter));
                }

                @Override // org.jooq.Binding
                public void get(BindingGetSQLInputContext<U> ctx) throws SQLException {
                    Binding.this.get(ctx.convert(converter));
                }
            };
        }
        return (Binding<T, U>) binding2;
    }

    static final Map<String, Class<?>> typeMap(Class<?> type, Scope scope) {
        return typeMap(type, scope, new HashMap());
    }

    static final Map<String, Class<?>> typeMap(Class<?> type, Scope scope, Map<String, Class<?>> result) {
        try {
            if (QualifiedRecord.class.isAssignableFrom(type)) {
                result.put(Tools.getMappedUDTName(scope, (Class<? extends QualifiedRecord<?>>) type), type);
                for (Field<?> field : Tools.getRecordQualifier(type).fields()) {
                    typeMap(field.getType(), scope, result);
                }
            }
            return result;
        } catch (Exception e) {
            throw new MappingException("Error while collecting type map", e);
        }
    }

    private static final long parse(Class<? extends java.util.Date> type, String date) throws SQLException {
        Long number = Longs.tryParse(date);
        if (number != null) {
            return number.longValue();
        }
        String date2 = StringUtils.replace(date, "T", " ");
        if (type == Timestamp.class) {
            return Timestamp.valueOf(date2).getTime();
        }
        if (type == Date.class) {
            return Date.valueOf(date2.split(" ")[0]).getTime();
        }
        if (type == Time.class) {
            return Time.valueOf(date2).getTime();
        }
        throw new SQLException("Could not parse date " + date2);
    }

    @Override // org.jooq.Binding
    public Converter<T, U> converter() {
        return this.delegate.converter();
    }

    @Override // org.jooq.Binding
    public void sql(BindingSQLContext<U> ctx) throws SQLException {
        this.delegate.sql(ctx);
    }

    @Override // org.jooq.Binding
    public void register(BindingRegisterContext<U> ctx) throws SQLException {
        this.delegate.register(ctx);
    }

    @Override // org.jooq.Binding
    public void set(BindingSetStatementContext<U> ctx) throws SQLException {
        this.delegate.set(ctx);
    }

    @Override // org.jooq.Binding
    public void set(BindingSetSQLOutputContext<U> ctx) throws SQLException {
        this.delegate.set(ctx);
    }

    @Override // org.jooq.Binding
    public void get(BindingGetResultSetContext<U> ctx) throws SQLException {
        this.delegate.get(ctx);
    }

    @Override // org.jooq.Binding
    public void get(BindingGetStatementContext<U> ctx) throws SQLException {
        this.delegate.get(ctx);
    }

    @Override // org.jooq.Binding
    public void get(BindingGetSQLInputContext<U> ctx) throws SQLException {
        this.delegate.get(ctx);
    }

    public String toString() {
        return this.delegate.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultBinding$InternalBinding.class */
    public static abstract class InternalBinding<T, U> implements Binding<T, U> {
        static final Set<SQLDialect> NEEDS_PRECISION_SCALE_ON_BIGDECIMAL = SQLDialect.supportedBy(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB);
        static final Set<SQLDialect> REQUIRES_JSON_CAST = SQLDialect.supportedBy(SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB);
        static final Set<SQLDialect> NO_SUPPORT_ENUM_CAST = SQLDialect.supportedBy(SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB);
        static final Set<SQLDialect> NO_SUPPORT_NVARCHAR = SQLDialect.supportedBy(SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB);
        final DataType<T> dataType;
        final ContextConverter<T, U> converter;
        final boolean attachable;

        abstract void set0(BindingSetStatementContext<U> bindingSetStatementContext, T t) throws SQLException;

        abstract void set0(BindingSetSQLOutputContext<U> bindingSetSQLOutputContext, T t) throws SQLException;

        abstract T get0(BindingGetResultSetContext<U> bindingGetResultSetContext) throws SQLException;

        abstract T get0(BindingGetStatementContext<U> bindingGetStatementContext) throws SQLException;

        abstract T get0(BindingGetSQLInputContext<U> bindingGetSQLInputContext) throws SQLException;

        abstract int sqltype(Statement statement, Configuration configuration) throws SQLException;

        InternalBinding(DataType<T> dataType, Converter<T, U> converter) {
            this.dataType = dataType;
            this.converter = ContextConverter.scoped(converter);
            this.attachable = Attachable.class.isAssignableFrom(converter.toType()) || !Modifier.isFinal(converter.toType().getModifiers());
        }

        @Override // org.jooq.Binding
        public final ContextConverter<T, U> converter() {
            return this.converter;
        }

        private final boolean shouldCast(BindingSQLContext<U> ctx, T converted) {
            if (ctx.render().paramType() == ParamType.INLINED) {
                if (converted == null) {
                    switch (ctx.family()) {
                        case DERBY:
                            return true;
                    }
                }
            } else if (!(converted instanceof EnumType)) {
                switch (ctx.family()) {
                    case DERBY:
                    case DUCKDB:
                    case FIREBIRD:
                    case H2:
                    case HSQLDB:
                    case IGNITE:
                    case CUBRID:
                    case POSTGRES:
                    case YUGABYTEDB:
                        return true;
                }
            }
            if (this.dataType.isInterval()) {
                switch (ctx.family()) {
                    case H2:
                    case HSQLDB:
                    case POSTGRES:
                    case YUGABYTEDB:
                        return true;
                }
            }
            if (this.dataType.isJSON() || this.dataType.isXML()) {
                switch (ctx.family()) {
                    case POSTGRES:
                    case YUGABYTEDB:
                    case TRINO:
                        return true;
                }
            }
            if (this.dataType.isUUID()) {
                switch (ctx.family()) {
                    case TRINO:
                        return true;
                }
            }
            if (this.dataType.getType() == OffsetDateTime.class || this.dataType.getType() == OffsetTime.class || this.dataType.getType() == Instant.class) {
                switch (ctx.family()) {
                    case TRINO:
                        return true;
                    default:
                        return false;
                }
            }
            return false;
        }

        /* JADX WARN: Multi-variable type inference failed */
        private final void sqlCast(BindingSQLContext<U> ctx, T t) throws SQLException {
            DataType<T> sqlDataType = this.dataType.getSQLDataType();
            SQLDialect family = ctx.family();
            if (t != 0 && this.dataType.getType() == BigDecimal.class && NEEDS_PRECISION_SCALE_ON_BIGDECIMAL.contains(ctx.dialect())) {
                int scale = ((BigDecimal) t).scale();
                int precision = ((BigDecimal) t).precision();
                if (scale >= precision) {
                    precision = scale + 1;
                }
                sqlCast(ctx, t, this.dataType, null, Integer.valueOf(precision), Integer.valueOf(scale));
                return;
            }
            if (SQLDataType.ROWID == sqlDataType) {
                sql(ctx, t);
                return;
            }
            if (SQLDataType.OTHER == sqlDataType) {
                if (t != 0) {
                    sqlCast(ctx, t, DefaultDataType.getDataType(family, t.getClass()), null, null, null);
                    return;
                } else {
                    ctx.render().sql(ctx.variable());
                    return;
                }
            }
            if (REQUIRES_JSON_CAST.contains(ctx.dialect()) && (sqlDataType == null || (!sqlDataType.isTemporal() && sqlDataType != SQLDataType.UUID && !sqlDataType.isXML() && !sqlDataType.isJSON()))) {
                sql(ctx, t);
            } else if (!NO_SUPPORT_ENUM_CAST.contains(ctx.dialect()) && this.dataType.isEnum()) {
                sqlCast(ctx, t, Tools.emulateEnumType(this.dataType), this.dataType.lengthDefined() ? Integer.valueOf(this.dataType.length()) : null, this.dataType.precisionDefined() ? Integer.valueOf(this.dataType.precision()) : null, this.dataType.scaleDefined() ? Integer.valueOf(this.dataType.scale()) : null);
            } else {
                sqlCast(ctx, t, this.dataType, this.dataType.lengthDefined() ? Integer.valueOf(this.dataType.length()) : null, this.dataType.precisionDefined() ? Integer.valueOf(this.dataType.precision()) : null, this.dataType.scaleDefined() ? Integer.valueOf(this.dataType.scale()) : null);
            }
        }

        private static final int getValueLength(String string) {
            if (string == null) {
                return 1;
            }
            int length = string.length();
            for (int i = 0; i < length; i++) {
                if (string.charAt(i) > 127) {
                    return Math.min(32672, 4 * length);
                }
            }
            return Math.min(32672, length);
        }

        private final void sqlCast(BindingSQLContext<U> ctx, T converted, DataType<?> t, Integer length, Integer precision, Integer scale) throws SQLException {
            switch (ctx.family()) {
                case TRINO:
                    if (t.isJSON()) {
                        ctx.render().visit(Names.N_JSON_PARSE).sql('(');
                        sql(ctx, converted);
                        ctx.render().sql(')');
                        return;
                    }
                    sqlCast0(ctx, converted, t, length, precision, scale);
                    return;
                default:
                    sqlCast0(ctx, converted, t, length, precision, scale);
                    return;
            }
        }

        private final void sqlCast0(BindingSQLContext<U> ctx, T converted, DataType<?> t, Integer length, Integer precision, Integer scale) throws SQLException {
            ctx.render().visit(Keywords.K_CAST).sql('(');
            sql(ctx, converted);
            ctx.render().sql(' ').visit(Keywords.K_AS).sql(' ').sql(DefaultDataType.set(t, length, precision, scale).getCastTypeName(ctx.configuration())).sql(')');
        }

        @Override // org.jooq.Binding
        public final void sql(BindingSQLContext<U> ctx) throws SQLException {
            T converted = converter().to(ctx.value(), ctx.converterContext());
            switch (ctx.render().castMode()) {
                case NEVER:
                    sql(ctx, converted);
                    return;
                case ALWAYS:
                    sqlCast(ctx, converted);
                    return;
                default:
                    if (shouldCast(ctx, converted)) {
                        sqlCast(ctx, converted);
                        return;
                    } else {
                        sql(ctx, converted);
                        return;
                    }
            }
        }

        private final void sql(BindingSQLContext<U> ctx, T value) throws SQLException {
            if (ctx.render().paramType() == ParamType.INLINED) {
                if (value == null) {
                    ctx.render().visit(Keywords.K_NULL);
                    return;
                } else {
                    sqlInline0(ctx, value);
                    return;
                }
            }
            sqlBind0(ctx, value);
        }

        static final String escape(Object val, Context<?> ctx) {
            String result = val.toString();
            if (Tools.needsBackslashEscaping(ctx.configuration())) {
                result = StringUtils.replace(result, "\\", "\\\\");
            }
            return StringUtils.replace(result, "'", "''");
        }

        @Override // org.jooq.Binding
        public final void register(BindingRegisterContext<U> ctx) throws SQLException {
            if (!Boolean.FALSE.equals(ctx.settings().isExecuteLogging()) && DefaultBinding.log.isTraceEnabled()) {
                DefaultBinding.log.trace("Registering variable " + ctx.index(), String.valueOf(this.dataType));
            }
            register0(ctx);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // org.jooq.Binding
        public final void set(BindingSetStatementContext<U> ctx) throws SQLException {
            Object obj = converter().to(ctx.value(), ctx.converterContext());
            if (!Boolean.FALSE.equals(ctx.settings().isExecuteLogging()) && DefaultBinding.log.isTraceEnabled()) {
                if (obj != null && obj.getClass().isArray() && obj.getClass() != byte[].class) {
                    DefaultBinding.log.trace("Binding variable " + ctx.index(), String.valueOf(Arrays.asList((Object[]) obj)) + " (" + String.valueOf(this.dataType) + ")");
                } else {
                    DefaultBinding.log.trace("Binding variable " + ctx.index(), String.valueOf(obj) + " (" + String.valueOf(this.dataType) + ")");
                }
            }
            if (obj == null) {
                setNull0(ctx);
            } else {
                set0(ctx, (BindingSetStatementContext<U>) obj);
            }
        }

        @Override // org.jooq.Binding
        public final void set(BindingSetSQLOutputContext<U> ctx) throws SQLException {
            T value = converter().to(ctx.value(), ctx.converterContext());
            if (value == null) {
                ctx.output().writeObject(null);
            } else {
                set0(ctx, (BindingSetSQLOutputContext<U>) value);
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // org.jooq.Binding
        public final void get(BindingGetResultSetContext<U> ctx) throws SQLException {
            U value = converter().from(get0(ctx), ctx.converterContext());
            if (this.attachable) {
                value = attach(value, ctx.configuration());
            }
            ctx.value(value);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // org.jooq.Binding
        public final void get(BindingGetStatementContext<U> ctx) throws SQLException {
            U value = converter().from(get0(ctx), ctx.converterContext());
            if (this.attachable) {
                value = attach(value, ctx.configuration());
            }
            ctx.value(value);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // org.jooq.Binding
        public final void get(BindingGetSQLInputContext<U> ctx) throws SQLException {
            U value = converter().from(get0(ctx), ctx.converterContext());
            if (this.attachable) {
                value = attach(value, ctx.configuration());
            }
            ctx.value(value);
        }

        private static final <U> U attach(U value, Configuration configuration) {
            if ((value instanceof Attachable) && Tools.attachRecords(configuration)) {
                ((Attachable) value).attach(configuration);
            }
            return value;
        }

        void setNull0(BindingSetStatementContext<U> ctx) throws SQLException {
            PreparedStatement statement = ctx.statement();
            if (statement instanceof R2DBC.R2DBCPreparedStatement) {
                R2DBC.R2DBCPreparedStatement s = (R2DBC.R2DBCPreparedStatement) statement;
                s.setNull(ctx.index(), (DataType<?>) this.dataType);
            } else {
                ctx.statement().setNull(ctx.index(), sqltype(ctx.statement(), ctx.configuration()));
            }
        }

        void register0(BindingRegisterContext<U> ctx) throws SQLException {
            ctx.statement().registerOutParameter(ctx.index(), sqltype(ctx.statement(), ctx.configuration()));
        }

        void sqlInline0(BindingSQLContext<U> ctx, T value) throws SQLException {
            sqlInline1(ctx, value);
        }

        final void sqlInline1(BindingSQLContext<U> ctx, Object value) throws SQLException {
            ctx.render().sql('\'').sql(escape(value, ctx.render()), true).sql('\'');
        }

        void sqlBind0(BindingSQLContext<U> ctx, T value) throws SQLException {
            ctx.render().sql(ctx.variable());
        }

        public String toString() {
            return "AbstractBinding [type=" + String.valueOf(this.dataType) + ", converter=" + String.valueOf(this.converter) + "]";
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultBinding$DelegatingBinding.class */
    public static final class DelegatingBinding<X, T, U> extends InternalBinding<X, U> {
        private final ContextConverter<T, X> delegatingConverter;
        private final InternalBinding<T, U> delegatingBinding;

        DelegatingBinding(DataType<X> originalDataType, ContextConverter<T, X> delegatingConverter, ContextConverter<X, U> originalConverter, java.util.function.Function<? super ContextConverter<T, U>, ? extends InternalBinding<T, U>> f) {
            super(originalDataType, originalConverter);
            this.delegatingConverter = delegatingConverter;
            this.delegatingBinding = f.apply(Converters.of(delegatingConverter, originalConverter));
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final void sqlInline0(BindingSQLContext<U> bindingSQLContext, X x) throws SQLException {
            this.delegatingBinding.sqlInline0(bindingSQLContext, this.delegatingConverter.to(x, bindingSQLContext.converterContext()));
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final void sqlBind0(BindingSQLContext<U> bindingSQLContext, X x) throws SQLException {
            this.delegatingBinding.sqlBind0(bindingSQLContext, this.delegatingConverter.to(x, bindingSQLContext.converterContext()));
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final void set0(BindingSetStatementContext<U> bindingSetStatementContext, X x) throws SQLException {
            this.delegatingBinding.set0(bindingSetStatementContext, (BindingSetStatementContext<U>) this.delegatingConverter.to(x, bindingSetStatementContext.converterContext()));
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final void setNull0(BindingSetStatementContext<U> ctx) throws SQLException {
            this.delegatingBinding.setNull0(ctx);
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final void set0(BindingSetSQLOutputContext<U> bindingSetSQLOutputContext, X x) throws SQLException {
            this.delegatingBinding.set0(bindingSetSQLOutputContext, (BindingSetSQLOutputContext<U>) this.delegatingConverter.to(x, bindingSetSQLOutputContext.converterContext()));
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final X get0(BindingGetResultSetContext<U> bindingGetResultSetContext) throws SQLException {
            return (X) this.delegatingConverter.from(this.delegatingBinding.get0(bindingGetResultSetContext), bindingGetResultSetContext.converterContext());
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final X get0(BindingGetStatementContext<U> bindingGetStatementContext) throws SQLException {
            return (X) this.delegatingConverter.from(this.delegatingBinding.get0(bindingGetStatementContext), bindingGetStatementContext.converterContext());
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final X get0(BindingGetSQLInputContext<U> bindingGetSQLInputContext) throws SQLException {
            return (X) this.delegatingConverter.from(this.delegatingBinding.get0(bindingGetSQLInputContext), bindingGetSQLInputContext.converterContext());
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final int sqltype(Statement statement, Configuration configuration) throws SQLException {
            return this.delegatingBinding.sqltype(statement, configuration);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultBinding$DefaultArrayBinding.class */
    public static final class DefaultArrayBinding<U> extends InternalBinding<Object[], U> {
        private static final Set<SQLDialect> REQUIRES_ARRAY_CAST = SQLDialect.supportedBy(SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB);

        DefaultArrayBinding(DataType<Object[]> dataType, Converter<Object[], U> converter) {
            super(dataType, converter);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void sqlInline0(BindingSQLContext<U> ctx, Object[] value) throws SQLException {
            DataType<T> dataType;
            String separator = "";
            if (REQUIRES_ARRAY_CAST.contains(ctx.dialect())) {
                if (this.dataType.getType() == Object[].class) {
                    dataType = DefaultDataType.getDataType(ctx.dialect(), deriveArrayTypeFromComponentType(value));
                } else {
                    dataType = this.dataType;
                }
                ctx.render().visit(DSL.cast((Field<?>) DSL.inline(PostgresUtils.toPGArrayString(value)), (DataType) dataType));
                return;
            }
            ctx.render().visit(Keywords.K_ARRAY);
            ctx.render().sql(1 != 0 ? '[' : '(');
            for (Object o : value) {
                ctx.render().sql(separator);
                DefaultBinding.binding(this.dataType.getArrayComponentDataType()).sql(new DefaultBindingSQLContext(ctx.configuration(), ctx.data(), ctx.render(), o));
                separator = ", ";
            }
            ctx.render().sql(1 != 0 ? ']' : ')');
        }

        private final Class<? extends Object[]> deriveArrayTypeFromComponentType(Object[] value) {
            for (Object o : value) {
                if (o != null) {
                    return Internal.arrayType(o.getClass());
                }
            }
            return String[].class;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void sqlBind0(BindingSQLContext<U> ctx, Object[] value) throws SQLException {
            Cast.renderCastIf(ctx.render(), c -> {
                super.sqlBind0(ctx, (BindingSQLContext) value);
            }, c2 -> {
                if (EnumType.class.isAssignableFrom(this.dataType.getType().getComponentType())) {
                    DefaultEnumTypeBinding.pgRenderEnumCast(ctx.render(), this.dataType.getType(), DefaultEnumTypeBinding.pgEnumValue(this.dataType.getType()));
                } else {
                    ctx.render().sql(this.dataType.getCastTypeName(ctx.render().configuration()));
                }
            }, () -> {
                return REQUIRES_ARRAY_CAST.contains(ctx.family());
            });
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetStatementContext<U> ctx, Object[] value) throws SQLException {
            switch (ctx.family()) {
                case H2:
                    ctx.statement().setObject(ctx.index(), value);
                    return;
                case HSQLDB:
                    Object[] a = value;
                    Class<T> type = this.dataType.getType();
                    if (type == UUID[].class) {
                        a = Convert.convertArray(a, (Class<?>) byte[][].class);
                        type = byte[][].class;
                    }
                    ctx.statement().setArray(ctx.index(), new MockArray(ctx.family(), a, type));
                    return;
                case IGNITE:
                case CUBRID:
                default:
                    throw new SQLDialectNotSupportedException("Cannot bind ARRAY types in dialect " + String.valueOf(ctx.family()));
                case POSTGRES:
                case YUGABYTEDB:
                    ctx.statement().setString(ctx.index(), PostgresUtils.toPGArrayString(value));
                    return;
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetSQLOutputContext<U> ctx, Object[] value) throws SQLException {
            ctx.output().writeArray(new MockArray(ctx.family(), value, this.dataType.getType()));
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final Object[] get0(BindingGetResultSetContext<U> ctx) throws SQLException {
            switch (ctx.family()) {
                case HSQLDB:
                    if (ctx.resultSet().getObject(ctx.index()) == null) {
                        return null;
                    }
                    return convertArray(ctx.resultSet().getArray(ctx.index()), (Class<? extends Object[]>) this.dataType.getType());
                case IGNITE:
                case CUBRID:
                default:
                    return convertArray(ctx.resultSet().getArray(ctx.index()), (Class<? extends Object[]>) this.dataType.getType());
                case POSTGRES:
                case YUGABYTEDB:
                    return (Object[]) pgGetArray(ctx, ctx.resultSet(), this.dataType, ctx.index());
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final Object[] get0(BindingGetStatementContext<U> ctx) throws SQLException {
            return convertArray(ctx.statement().getObject(ctx.index()), (Class<? extends Object[]>) this.dataType.getType());
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final Object[] get0(BindingGetSQLInputContext<U> ctx) throws SQLException {
            java.sql.Array array = ctx.input().readArray();
            if (array == null) {
                return null;
            }
            return (Object[]) array.getArray();
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final int sqltype(Statement statement, Configuration configuration) {
            return 2003;
        }

        /* JADX WARN: Multi-variable type inference failed */
        private static final <T> T pgGetArray(ExecuteScope executeScope, ResultSet resultSet, DataType<T> dataType, int i) throws SQLException {
            java.sql.Array array = null;
            try {
                array = resultSet.getArray(i);
                if (array == null) {
                    JDBCUtils.safeFree(array);
                    return null;
                }
                DataType<?> arrayComponentDataType = dataType.getArrayComponentDataType();
                try {
                    if (arrayComponentDataType.isBinary() || arrayComponentDataType.isUDT()) {
                        throw new ControlFlowSignal("GOTO the next array deserialisation strategy");
                    }
                    T t = (T) convertArray(array, (Class<? extends Object[]>) dataType.getType());
                    JDBCUtils.safeFree(array);
                    return t;
                } catch (Exception e) {
                    ArrayList arrayList = new ArrayList();
                    try {
                        ResultSet resultSet2 = array.getResultSet();
                        try {
                            Binding binding = DefaultBinding.binding(dataType.getArrayComponentDataType());
                            DefaultBindingGetResultSetContext defaultBindingGetResultSetContext = new DefaultBindingGetResultSetContext(executeScope.executeContext(), resultSet2, 2);
                            while (resultSet2.next()) {
                                binding.get(defaultBindingGetResultSetContext);
                                arrayList.add(defaultBindingGetResultSetContext.value());
                            }
                            if (resultSet2 != null) {
                                resultSet2.close();
                            }
                            T t2 = (T) convertArray((Object) arrayList.toArray(), (Class<? extends Object[]>) dataType.getType());
                            JDBCUtils.safeFree(array);
                            return t2;
                        } catch (Throwable th) {
                            if (resultSet2 != null) {
                                try {
                                    resultSet2.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                            }
                            throw th;
                        }
                    } catch (Exception e2) {
                        String str = null;
                        try {
                            str = resultSet.getString(i);
                        } catch (SQLException e3) {
                        }
                        DefaultBinding.log.error("Cannot parse array", str, e2);
                        JDBCUtils.safeFree(array);
                        return null;
                    }
                }
            } catch (Throwable th3) {
                JDBCUtils.safeFree(array);
                throw th3;
            }
        }

        private static final Object[] convertArray(Object array, Class<? extends Object[]> type) throws SQLException {
            if (array instanceof Object[]) {
                return (Object[]) Convert.convert(array, type);
            }
            if (array instanceof java.sql.Array) {
                java.sql.Array a = (java.sql.Array) array;
                return convertArray(a, type);
            }
            return null;
        }

        private static final Object[] convertArray(java.sql.Array array, Class<? extends Object[]> type) throws SQLException {
            if (array != null) {
                return (Object[]) Convert.convert(array.getArray(), type);
            }
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultBinding$DefaultBigDecimalBinding.class */
    public static final class DefaultBigDecimalBinding<U> extends InternalBinding<BigDecimal, U> {
        private static final Set<SQLDialect> BIND_AS_STRING = SQLDialect.supportedBy(SQLDialect.SQLITE);

        DefaultBigDecimalBinding(DataType<BigDecimal> dataType, Converter<BigDecimal, U> converter) {
            super(dataType, converter);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void sqlInline0(BindingSQLContext<U> ctx, BigDecimal value) {
            ctx.render().sql(value.toString());
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final void setNull0(BindingSetStatementContext<U> ctx) throws SQLException {
            super.setNull0(ctx);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetStatementContext<U> ctx, BigDecimal value) throws SQLException {
            if (BIND_AS_STRING.contains(ctx.dialect())) {
                ctx.statement().setString(ctx.index(), value.toString());
            } else {
                ctx.statement().setBigDecimal(ctx.index(), value);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetSQLOutputContext<U> ctx, BigDecimal value) throws SQLException {
            ctx.output().writeBigDecimal(value);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final BigDecimal get0(BindingGetResultSetContext<U> ctx) throws SQLException {
            if (ctx.family() == SQLDialect.SQLITE) {
                return (BigDecimal) Convert.convert(ctx.resultSet().getString(ctx.index()), BigDecimal.class);
            }
            return ctx.resultSet().getBigDecimal(ctx.index());
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final BigDecimal get0(BindingGetStatementContext<U> ctx) throws SQLException {
            return ctx.statement().getBigDecimal(ctx.index());
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final BigDecimal get0(BindingGetSQLInputContext<U> ctx) throws SQLException {
            return ctx.input().readBigDecimal();
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final int sqltype(Statement statement, Configuration configuration) {
            return 3;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultBinding$DefaultBigIntegerBinding.class */
    public static final class DefaultBigIntegerBinding<U> extends InternalBinding<BigInteger, U> {
        private static final Set<SQLDialect> BIND_AS_STRING = SQLDialect.supportedBy(SQLDialect.SQLITE);

        DefaultBigIntegerBinding(DataType<BigInteger> dataType, Converter<BigInteger, U> converter) {
            super(dataType, converter);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void sqlInline0(BindingSQLContext<U> ctx, BigInteger value) {
            ctx.render().sql(value.toString());
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final void setNull0(BindingSetStatementContext<U> ctx) throws SQLException {
            super.setNull0(ctx);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetStatementContext<U> ctx, BigInteger value) throws SQLException {
            if (BIND_AS_STRING.contains(ctx.dialect())) {
                ctx.statement().setString(ctx.index(), value.toString());
            } else {
                ctx.statement().setBigDecimal(ctx.index(), new BigDecimal(value));
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetSQLOutputContext<U> ctx, BigInteger value) throws SQLException {
            ctx.output().writeBigDecimal(new BigDecimal(value));
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final BigInteger get0(BindingGetResultSetContext<U> ctx) throws SQLException {
            if (ctx.family() == SQLDialect.SQLITE) {
                return (BigInteger) Convert.convert(ctx.resultSet().getString(ctx.index()), BigInteger.class);
            }
            BigDecimal b = ctx.resultSet().getBigDecimal(ctx.index());
            if (b == null) {
                return null;
            }
            return b.toBigInteger();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final BigInteger get0(BindingGetStatementContext<U> ctx) throws SQLException {
            BigDecimal d = ctx.statement().getBigDecimal(ctx.index());
            if (d == null) {
                return null;
            }
            return d.toBigInteger();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final BigInteger get0(BindingGetSQLInputContext<U> ctx) throws SQLException {
            BigDecimal d = ctx.input().readBigDecimal();
            if (d == null) {
                return null;
            }
            return d.toBigInteger();
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final int sqltype(Statement statement, Configuration configuration) {
            return 3;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultBinding$DefaultBlobBinding.class */
    public static final class DefaultBlobBinding<U> extends InternalBinding<Blob, U> {
        DefaultBlobBinding(DataType<Blob> dataType, Converter<Blob, U> converter) {
            super(dataType, converter);
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final void setNull0(BindingSetStatementContext<U> ctx) throws SQLException {
            super.setNull0(ctx);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetStatementContext<U> ctx, Blob value) throws SQLException {
            ctx.statement().setBlob(ctx.index(), value);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetSQLOutputContext<U> ctx, Blob value) throws SQLException {
            ctx.output().writeBlob(value);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final Blob get0(BindingGetResultSetContext<U> ctx) throws SQLException {
            return ctx.resultSet().getBlob(ctx.index());
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final Blob get0(BindingGetStatementContext<U> ctx) throws SQLException {
            return ctx.statement().getBlob(ctx.index());
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final Blob get0(BindingGetSQLInputContext<U> ctx) throws SQLException {
            return ctx.input().readBlob();
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final int sqltype(Statement statement, Configuration configuration) {
            switch (configuration.family()) {
                case POSTGRES:
                case YUGABYTEDB:
                    return -2;
                default:
                    return 2004;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultBinding$DefaultBooleanBinding.class */
    public static final class DefaultBooleanBinding<U> extends InternalBinding<Boolean, U> {
        static final Set<SQLDialect> BIND_AS_1_0 = SQLDialect.supportedUntil(SQLDialect.FIREBIRD, SQLDialect.SQLITE);

        DefaultBooleanBinding(DataType<Boolean> dataType, Converter<Boolean, U> converter) {
            super(dataType, converter);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void sqlInline0(BindingSQLContext<U> ctx, Boolean value) {
            if (BIND_AS_1_0.contains(ctx.dialect())) {
                ctx.render().sql(value.booleanValue() ? CustomBooleanEditor.VALUE_1 : CustomBooleanEditor.VALUE_0);
            } else {
                ctx.render().visit(value.booleanValue() ? Keywords.K_TRUE : Keywords.K_FALSE);
            }
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final void register0(BindingRegisterContext<U> ctx) throws SQLException {
            super.register0(ctx);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetStatementContext<U> ctx, Boolean value) throws SQLException {
            switch (ctx.family()) {
                default:
                    ctx.statement().setBoolean(ctx.index(), value.booleanValue());
                    return;
            }
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final void setNull0(BindingSetStatementContext<U> ctx) throws SQLException {
            super.setNull0(ctx);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetSQLOutputContext<U> ctx, Boolean value) throws SQLException {
            ctx.output().writeBoolean(value.booleanValue());
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final Boolean get0(BindingGetResultSetContext<U> ctx) throws SQLException {
            return JDBCUtils.wasNull(ctx.resultSet(), Boolean.valueOf(ctx.resultSet().getBoolean(ctx.index())));
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final Boolean get0(BindingGetStatementContext<U> ctx) throws SQLException {
            return JDBCUtils.wasNull(ctx.statement(), Boolean.valueOf(ctx.statement().getBoolean(ctx.index())));
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final Boolean get0(BindingGetSQLInputContext<U> ctx) throws SQLException {
            return JDBCUtils.wasNull(ctx.input(), Boolean.valueOf(ctx.input().readBoolean()));
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final int sqltype(Statement statement, Configuration configuration) throws SQLException {
            switch (configuration.family()) {
                default:
                    return 16;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultBinding$DefaultByteBinding.class */
    public static final class DefaultByteBinding<U> extends InternalBinding<Byte, U> {
        DefaultByteBinding(DataType<Byte> dataType, Converter<Byte, U> converter) {
            super(dataType, converter);
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final void setNull0(BindingSetStatementContext<U> ctx) throws SQLException {
            super.setNull0(ctx);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void sqlInline0(BindingSQLContext<U> ctx, Byte value) {
            ctx.render().sql((int) value.byteValue());
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetStatementContext<U> ctx, Byte value) throws SQLException {
            ctx.statement().setByte(ctx.index(), value.byteValue());
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetSQLOutputContext<U> ctx, Byte value) throws SQLException {
            ctx.output().writeByte(value.byteValue());
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final Byte get0(BindingGetResultSetContext<U> ctx) throws SQLException {
            return (Byte) JDBCUtils.wasNull(ctx.resultSet(), Byte.valueOf(ctx.resultSet().getByte(ctx.index())));
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final Byte get0(BindingGetStatementContext<U> ctx) throws SQLException {
            return (Byte) JDBCUtils.wasNull(ctx.statement(), Byte.valueOf(ctx.statement().getByte(ctx.index())));
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final Byte get0(BindingGetSQLInputContext<U> ctx) throws SQLException {
            return (Byte) JDBCUtils.wasNull(ctx.input(), Byte.valueOf(ctx.input().readByte()));
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final int sqltype(Statement statement, Configuration configuration) {
            return -6;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultBinding$DefaultBytesBinding.class */
    public static final class DefaultBytesBinding<U> extends InternalBinding<byte[], U> {
        private final BlobBinding blobs;

        DefaultBytesBinding(DataType<byte[]> dataType, Converter<byte[], U> converter) {
            super(dataType, converter);
            this.blobs = new BlobBinding();
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final void setNull0(BindingSetStatementContext<U> ctx) throws SQLException {
            switch (ctx.family()) {
                default:
                    super.setNull0(ctx);
                    return;
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void sqlInline0(BindingSQLContext<U> ctx, byte[] value) {
            switch (ctx.family()) {
                case DERBY:
                    ctx.render().visit(Keywords.K_CAST).sql("(X'").sql(Tools.convertBytesToHex(value)).sql("' ").visit(Keywords.K_AS).sql(' ').visit(SQLDataType.BLOB).sql(')');
                    return;
                case DUCKDB:
                case FIREBIRD:
                case IGNITE:
                case CUBRID:
                case TRINO:
                default:
                    ctx.render().sql("X'").sql(Tools.convertBytesToHex(value)).sql('\'');
                    return;
                case H2:
                case HSQLDB:
                case MARIADB:
                case MYSQL:
                case SQLITE:
                    ctx.render().sql("X'").sql(Tools.convertBytesToHex(value)).sql('\'');
                    return;
                case POSTGRES:
                case YUGABYTEDB:
                    Cast.renderCast(ctx.render(), c -> {
                        c.sql("E'").sql(PostgresUtils.toPGString(value)).sql("'");
                    }, c2 -> {
                        c2.visit(Names.N_BYTEA);
                    });
                    return;
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetStatementContext<U> ctx, byte[] value) throws SQLException {
            switch (ctx.family()) {
                case DUCKDB:
                case H2:
                    this.blobs.set(new DefaultBindingSetStatementContext(ctx.executeContext(), ctx.statement(), ctx.index(), value));
                    return;
                default:
                    ctx.statement().setBytes(ctx.index(), value);
                    return;
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetSQLOutputContext<U> ctx, byte[] value) throws SQLException {
            ctx.output().writeBytes(value);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final byte[] get0(BindingGetResultSetContext<U> ctx) throws SQLException {
            switch (ctx.family()) {
                case DUCKDB:
                case H2:
                    DefaultBindingGetResultSetContext<byte[]> x = new DefaultBindingGetResultSetContext<>(ctx.executeContext(), ctx.resultSet(), ctx.index());
                    this.blobs.get(x);
                    return x.value();
                default:
                    return ctx.resultSet().getBytes(ctx.index());
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final byte[] get0(BindingGetStatementContext<U> ctx) throws SQLException {
            switch (ctx.family()) {
                case DUCKDB:
                case H2:
                    DefaultBindingGetStatementContext<byte[]> x = new DefaultBindingGetStatementContext<>(ctx.executeContext(), ctx.statement(), ctx.index());
                    this.blobs.get(x);
                    return x.value();
                default:
                    return ctx.statement().getBytes(ctx.index());
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final byte[] get0(BindingGetSQLInputContext<U> ctx) throws SQLException {
            return ctx.input().readBytes();
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final int sqltype(Statement statement, Configuration configuration) {
            switch (configuration.family()) {
                case POSTGRES:
                case YUGABYTEDB:
                    return -2;
                default:
                    return 2004;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultBinding$DefaultClobBinding.class */
    public static final class DefaultClobBinding<U> extends InternalBinding<Clob, U> {
        DefaultClobBinding(DataType<Clob> dataType, Converter<Clob, U> converter) {
            super(dataType, converter);
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final void setNull0(BindingSetStatementContext<U> ctx) throws SQLException {
            super.setNull0(ctx);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetStatementContext<U> ctx, Clob value) throws SQLException {
            ctx.statement().setClob(ctx.index(), value);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetSQLOutputContext<U> ctx, Clob value) throws SQLException {
            ctx.output().writeClob(value);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final Clob get0(BindingGetResultSetContext<U> ctx) throws SQLException {
            return ctx.resultSet().getClob(ctx.index());
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final Clob get0(BindingGetStatementContext<U> ctx) throws SQLException {
            return ctx.statement().getClob(ctx.index());
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final Clob get0(BindingGetSQLInputContext<U> ctx) throws SQLException {
            return ctx.input().readClob();
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final int sqltype(Statement statement, Configuration configuration) {
            return 2005;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultBinding$DefaultDateBinding.class */
    public static final class DefaultDateBinding<U> extends InternalBinding<Date, U> {
        private static final Set<SQLDialect> INLINE_AS_STRING_LITERAL = SQLDialect.supportedBy(SQLDialect.SQLITE);

        DefaultDateBinding(DataType<Date> dataType, Converter<Date, U> converter) {
            super(dataType, converter);
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final void setNull0(BindingSetStatementContext<U> ctx) throws SQLException {
            switch (ctx.family()) {
                default:
                    super.setNull0(ctx);
                    return;
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void sqlInline0(BindingSQLContext<U> ctx, Date value) {
            if (INLINE_AS_STRING_LITERAL.contains(ctx.dialect())) {
                ctx.render().sql('\'').sql(escape(value, ctx.render())).sql('\'');
                return;
            }
            if (ctx.family() == SQLDialect.DERBY) {
                ctx.render().visit(Keywords.K_DATE).sql("('").sql(escape(value, ctx.render())).sql("')");
            } else if (DefaultBinding.REQUIRE_JDBC_DATE_LITERAL.contains(ctx.dialect())) {
                ctx.render().sql("{d '").sql(escape(value, ctx.render())).sql("'}");
            } else {
                ctx.render().visit(Keywords.K_DATE).sql(" '").sql(format(value, ctx.render())).sql('\'');
            }
        }

        private final String format(Date value, RenderContext render) {
            if (render.family() == SQLDialect.POSTGRES) {
                if (value.getTime() == DefaultBinding.PG_DATE_POSITIVE_INFINITY) {
                    return "infinity";
                }
                if (value.getTime() == DefaultBinding.PG_DATE_NEGATIVE_INFINITY) {
                    return "-infinity";
                }
            }
            if (value.getYear() + 1900 >= 10000) {
                return (value.getYear() + 1900) + "-" + StringUtils.leftPad((value.getMonth() + 1), 2, '0') + "-" + StringUtils.leftPad(value.getDate(), 2, '0');
            }
            return escape(value, render);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void sqlBind0(BindingSQLContext<U> ctx, Date value) throws SQLException {
            switch (ctx.family()) {
                default:
                    super.sqlBind0(ctx, (BindingSQLContext<U>) value);
                    return;
            }
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final void register0(BindingRegisterContext<U> ctx) throws SQLException {
            switch (ctx.family()) {
                default:
                    super.register0(ctx);
                    return;
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetStatementContext<U> ctx, Date value) throws SQLException {
            switch (ctx.family()) {
                case DUCKDB:
                case SQLITE:
                    ctx.statement().setString(ctx.index(), value.toString());
                    return;
                default:
                    ctx.statement().setDate(ctx.index(), value);
                    return;
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetSQLOutputContext<U> ctx, Date value) throws SQLException {
            switch (ctx.family()) {
                default:
                    ctx.output().writeDate(value);
                    return;
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final Date get0(BindingGetResultSetContext<U> ctx) throws SQLException {
            switch (ctx.family()) {
                case SQLITE:
                    String date = ctx.resultSet().getString(ctx.index());
                    if (date == null) {
                        return null;
                    }
                    return new Date(DefaultBinding.parse(Date.class, date));
                default:
                    return ctx.resultSet().getDate(ctx.index());
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final Date get0(BindingGetStatementContext<U> ctx) throws SQLException {
            switch (ctx.family()) {
                default:
                    return ctx.statement().getDate(ctx.index());
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final Date get0(BindingGetSQLInputContext<U> ctx) throws SQLException {
            switch (ctx.family()) {
                default:
                    return ctx.input().readDate();
            }
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final int sqltype(Statement statement, Configuration configuration) {
            switch (configuration.family()) {
                default:
                    return 91;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultBinding$DefaultDayToSecondBinding.class */
    public static final class DefaultDayToSecondBinding<U> extends InternalBinding<DayToSecond, U> {
        private static final Set<SQLDialect> REQUIRE_PG_INTERVAL = SQLDialect.supportedBy(SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB);
        private static final Set<SQLDialect> REQUIRE_STANDARD_INTERVAL = SQLDialect.supportedBy(SQLDialect.H2, SQLDialect.TRINO);

        DefaultDayToSecondBinding(DataType<DayToSecond> dataType, Converter<DayToSecond, U> converter) {
            super(dataType, converter);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public void sqlInline0(BindingSQLContext<U> ctx, DayToSecond value) throws SQLException {
            if (REQUIRE_PG_INTERVAL.contains(ctx.dialect())) {
                ctx.render().visit((Field<?>) DSL.inline(PostgresUtils.toPGInterval(value).toString()));
            } else if (ctx.family() == SQLDialect.TRINO) {
                ctx.render().sql(renderDTS(ctx, value, i -> {
                    return (String) Tools.apply(i.toString(), s -> {
                        return s.substring(0, s.length() - 1);
                    });
                }));
            } else {
                super.sqlInline0(ctx, (BindingSQLContext<U>) value);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetStatementContext<U> ctx, DayToSecond value) throws SQLException {
            if (REQUIRE_PG_INTERVAL.contains(ctx.dialect())) {
                ctx.statement().setString(ctx.index(), PostgresUtils.toPGInterval(value).toString());
            } else {
                ctx.statement().setString(ctx.index(), renderDTS(ctx, value));
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetSQLOutputContext<U> ctx, DayToSecond value) throws SQLException {
            ctx.output().writeString(renderDTS(ctx, value));
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final DayToSecond get0(BindingGetResultSetContext<U> ctx) throws SQLException {
            if (REQUIRE_PG_INTERVAL.contains(ctx.dialect())) {
                return PostgresUtils.toDayToSecond(ctx.resultSet().getString(ctx.index()));
            }
            return parseDTS(ctx, ctx.resultSet().getString(ctx.index()));
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final DayToSecond get0(BindingGetStatementContext<U> ctx) throws SQLException {
            if (REQUIRE_PG_INTERVAL.contains(ctx.dialect())) {
                return PostgresUtils.toDayToSecond(ctx.statement().getString(ctx.index()));
            }
            return parseDTS(ctx, ctx.statement().getString(ctx.index()));
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final DayToSecond get0(BindingGetSQLInputContext<U> ctx) throws SQLException {
            return parseDTS(ctx, ctx.input().readString());
        }

        private final DayToSecond parseDTS(Scope scope, String string) {
            if (string == null) {
                return null;
            }
            if (REQUIRE_STANDARD_INTERVAL.contains(scope.dialect()) && string.startsWith("INTERVAL")) {
                return (DayToSecond) ((Param) scope.dsl().parser().parseField(string)).getValue();
            }
            return DayToSecond.valueOf(string);
        }

        private final String renderDTS(Scope scope, DayToSecond dts) {
            return renderDTS(scope, dts, (v0) -> {
                return v0.toString();
            });
        }

        private final String renderDTS(Scope scope, DayToSecond dts, java.util.function.Function<? super DayToSecond, ? extends String> toString) {
            if (dts == null) {
                return null;
            }
            if (REQUIRE_STANDARD_INTERVAL.contains(scope.dialect())) {
                return "INTERVAL '" + toString.apply(dts) + "' DAY TO SECOND";
            }
            return toString.apply(dts);
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final int sqltype(Statement statement, Configuration configuration) {
            return 12;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultBinding$DefaultDoubleBinding.class */
    public static final class DefaultDoubleBinding<U> extends InternalBinding<Double, U> {
        static final Set<SQLDialect> REQUIRES_LITERAL_CAST = SQLDialect.supportedBy(SQLDialect.H2);

        DefaultDoubleBinding(DataType<Double> dataType, Converter<Double, U> converter) {
            super(dataType, converter);
        }

        static final Field<?> nan(BindingSQLContext<?> ctx, DataType<?> type) {
            switch (ctx.family()) {
                case FIREBIRD:
                    return DSL.log(DSL.inline(1), DSL.inline(1));
                case HSQLDB:
                    return DSL.inline(0.0d).div(DSL.field("0.0e0", type));
                default:
                    return DSL.inline("NaN").cast(type);
            }
        }

        static final Field<?> infinity(BindingSQLContext<?> ctx, DataType<?> type, boolean negative) {
            switch (ctx.family()) {
                case FIREBIRD:
                    return DSL.log(negative ? DSL.inline(0.5d) : DSL.inline(1.5d), DSL.inline(1));
                case HSQLDB:
                    return DSL.inline(negative ? -1.0d : 1.0d).div(DSL.field("0.0e0", type));
                default:
                    return DSL.inline(negative ? "-Infinity" : "Infinity").cast(type);
            }
        }

        static final Double fixInfinity(Scope scope, ThrowingSupplier<Double, SQLException> doubleSupplier, ThrowingSupplier<String, SQLException> stringSupplier) throws SQLException {
            return (Double) fixInfinity(scope, doubleSupplier, stringSupplier, () -> {
                return Double.valueOf(Double.POSITIVE_INFINITY);
            }, () -> {
                return Double.valueOf(Double.NEGATIVE_INFINITY);
            });
        }

        static final <T> T fixInfinity(Scope scope, ThrowingSupplier<T, SQLException> doubleSupplier, ThrowingSupplier<String, SQLException> stringSupplier, Supplier<T> positive, Supplier<T> negative) throws SQLException {
            try {
                return doubleSupplier.get();
            } catch (SQLException e) {
                throw e;
            }
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final void setNull0(BindingSetStatementContext<U> ctx) throws SQLException {
            super.setNull0(ctx);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void sqlInline0(BindingSQLContext<U> ctx, Double value) {
            if (value.isNaN()) {
                ctx.render().visit(nan(ctx, SQLDataType.DOUBLE));
                return;
            }
            if (value.doubleValue() == Double.POSITIVE_INFINITY) {
                ctx.render().visit(infinity(ctx, SQLDataType.DOUBLE, false));
                return;
            }
            if (value.doubleValue() == Double.NEGATIVE_INFINITY) {
                ctx.render().visit(infinity(ctx, SQLDataType.DOUBLE, true));
            } else if (REQUIRES_LITERAL_CAST.contains(ctx.dialect())) {
                ctx.render().visit(DSL.field(ctx.render().doubleFormat().format(value)).cast(SQLDataType.DOUBLE));
            } else {
                ctx.render().sql(value.doubleValue());
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetStatementContext<U> ctx, Double value) throws SQLException {
            ctx.statement().setDouble(ctx.index(), value.doubleValue());
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetSQLOutputContext<U> ctx, Double value) throws SQLException {
            ctx.output().writeDouble(value.doubleValue());
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final Double get0(BindingGetResultSetContext<U> ctx) throws SQLException {
            return (Double) JDBCUtils.wasNull(ctx.resultSet(), fixInfinity(ctx, () -> {
                return Double.valueOf(ctx.resultSet().getDouble(ctx.index()));
            }, () -> {
                return ctx.resultSet().getString(ctx.index());
            }));
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final Double get0(BindingGetStatementContext<U> ctx) throws SQLException {
            return (Double) JDBCUtils.wasNull(ctx.statement(), fixInfinity(ctx, () -> {
                return Double.valueOf(ctx.statement().getDouble(ctx.index()));
            }, () -> {
                return ctx.statement().getString(ctx.index());
            }));
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final Double get0(BindingGetSQLInputContext<U> ctx) throws SQLException {
            return (Double) JDBCUtils.wasNull(ctx.input(), fixInfinity(ctx, () -> {
                return Double.valueOf(ctx.input().readDouble());
            }, () -> {
                return ctx.input().readString();
            }));
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final int sqltype(Statement statement, Configuration configuration) {
            return 8;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultBinding$DefaultEnumTypeBinding.class */
    public static final class DefaultEnumTypeBinding<U> extends InternalBinding<EnumType, U> {
        private static final Set<SQLDialect> REQUIRE_ENUM_CAST = SQLDialect.supportedBy(SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB);

        DefaultEnumTypeBinding(DataType<EnumType> dataType, Converter<EnumType, U> converter) {
            super(dataType, converter);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void sqlInline0(BindingSQLContext<U> ctx, EnumType value) throws SQLException {
            EnumType enumValue = pgEnumValue(this.dataType.getType());
            Cast.renderCastIf(ctx.render(), c -> {
                DefaultBinding.binding(SQLDataType.VARCHAR).sql(new DefaultBindingSQLContext(ctx.configuration(), ctx.data(), ctx.render(), value.getLiteral()));
            }, c2 -> {
                pgRenderEnumCast(c2, this.dataType.getType(), enumValue);
            }, () -> {
                return REQUIRE_ENUM_CAST.contains(ctx.dialect()) && enumValue.getName() != null;
            });
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void sqlBind0(BindingSQLContext<U> ctx, EnumType value) throws SQLException {
            EnumType enumValue = pgEnumValue(this.dataType.getType());
            Cast.renderCastIf(ctx.render(), c -> {
                super.sqlBind0(ctx, (BindingSQLContext) value);
            }, c2 -> {
                pgRenderEnumCast(c2, this.dataType.getType(), enumValue);
            }, () -> {
                return REQUIRE_ENUM_CAST.contains(ctx.dialect()) && enumValue.getName() != null;
            });
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetStatementContext<U> ctx, EnumType value) throws SQLException {
            ctx.statement().setString(ctx.index(), value.getLiteral());
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetSQLOutputContext<U> ctx, EnumType value) throws SQLException {
            ctx.output().writeString(value.getLiteral());
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final EnumType get0(BindingGetResultSetContext<U> ctx) throws SQLException {
            return getEnumType(this.dataType.getType(), ctx.resultSet().getString(ctx.index()));
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final EnumType get0(BindingGetStatementContext<U> ctx) throws SQLException {
            return getEnumType(this.dataType.getType(), ctx.statement().getString(ctx.index()));
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final EnumType get0(BindingGetSQLInputContext<U> ctx) throws SQLException {
            return getEnumType(this.dataType.getType(), ctx.input().readString());
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final int sqltype(Statement statement, Configuration configuration) {
            return 12;
        }

        static final EnumType pgEnumValue(Class<?> type) {
            EnumType[] enums = Tools.enums(type.isArray() ? type.getComponentType() : type);
            if (enums == null || enums.length == 0) {
                throw new IllegalArgumentException("Not a valid EnumType : " + String.valueOf(type));
            }
            return enums[0];
        }

        static final void pgRenderEnumCast(Context<?> ctx, Class<?> type, EnumType value) {
            if (value.getName() != null) {
                Schema schema = DSL.using(ctx.configuration()).map(value.getSchema());
                if (schema != null && Boolean.TRUE.equals(ctx.configuration().settings().isRenderSchema())) {
                    ctx.visit(schema);
                    ctx.sql('.');
                }
                ctx.visit(DSL.name(value.getName()));
                if (type.isArray()) {
                    ctx.sql(ClassUtils.ARRAY_SUFFIX);
                }
            }
        }

        static final <E extends EnumType> E getEnumType(Class<? extends E> type, String literal) {
            try {
                return (E) Tools.findAny(Tools.enums(type), e -> {
                    return e.getLiteral().equals(literal);
                });
            } catch (Exception e2) {
                throw new DataTypeException("Unknown enum literal found : " + literal);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultBinding$DefaultFloatBinding.class */
    public static final class DefaultFloatBinding<U> extends InternalBinding<Float, U> {
        DefaultFloatBinding(DataType<Float> dataType, Converter<Float, U> converter) {
            super(dataType, converter);
        }

        static final Float fixInfinity(Scope scope, ThrowingSupplier<Float, SQLException> doubleSupplier, ThrowingSupplier<String, SQLException> stringSupplier) throws SQLException {
            return (Float) DefaultDoubleBinding.fixInfinity(scope, doubleSupplier, stringSupplier, () -> {
                return Float.valueOf(Float.POSITIVE_INFINITY);
            }, () -> {
                return Float.valueOf(Float.NEGATIVE_INFINITY);
            });
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final void setNull0(BindingSetStatementContext<U> ctx) throws SQLException {
            super.setNull0(ctx);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void sqlInline0(BindingSQLContext<U> ctx, Float value) {
            if (value.isNaN()) {
                ctx.render().visit(DefaultDoubleBinding.nan(ctx, SQLDataType.REAL));
                return;
            }
            if (value.floatValue() == Double.POSITIVE_INFINITY) {
                ctx.render().visit(DefaultDoubleBinding.infinity(ctx, SQLDataType.REAL, false));
                return;
            }
            if (value.floatValue() == Double.NEGATIVE_INFINITY) {
                ctx.render().visit(DefaultDoubleBinding.infinity(ctx, SQLDataType.REAL, true));
            } else if (DefaultDoubleBinding.REQUIRES_LITERAL_CAST.contains(ctx.dialect())) {
                ctx.render().visit(DSL.field(ctx.render().floatFormat().format(value)).cast(SQLDataType.REAL));
            } else {
                ctx.render().sql(value.floatValue());
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetStatementContext<U> ctx, Float value) throws SQLException {
            ctx.statement().setFloat(ctx.index(), value.floatValue());
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetSQLOutputContext<U> ctx, Float value) throws SQLException {
            ctx.output().writeFloat(value.floatValue());
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final Float get0(BindingGetResultSetContext<U> ctx) throws SQLException {
            return (Float) JDBCUtils.wasNull(ctx.resultSet(), fixInfinity(ctx, () -> {
                return Float.valueOf(ctx.resultSet().getFloat(ctx.index()));
            }, () -> {
                return ctx.resultSet().getString(ctx.index());
            }));
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final Float get0(BindingGetStatementContext<U> ctx) throws SQLException {
            return (Float) JDBCUtils.wasNull(ctx.statement(), fixInfinity(ctx, () -> {
                return Float.valueOf(ctx.statement().getFloat(ctx.index()));
            }, () -> {
                return ctx.statement().getString(ctx.index());
            }));
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final Float get0(BindingGetSQLInputContext<U> ctx) throws SQLException {
            return (Float) JDBCUtils.wasNull(ctx.input(), fixInfinity(ctx, () -> {
                return Float.valueOf(ctx.input().readFloat());
            }, () -> {
                return ctx.input().readString();
            }));
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final int sqltype(Statement statement, Configuration configuration) {
            return 6;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultBinding$DefaultIntegerBinding.class */
    public static final class DefaultIntegerBinding<U> extends InternalBinding<Integer, U> {
        DefaultIntegerBinding(DataType<Integer> dataType, Converter<Integer, U> converter) {
            super(dataType, converter);
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final void setNull0(BindingSetStatementContext<U> ctx) throws SQLException {
            super.setNull0(ctx);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void sqlInline0(BindingSQLContext<U> ctx, Integer value) {
            ctx.render().sql(value.intValue());
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetStatementContext<U> ctx, Integer value) throws SQLException {
            ctx.statement().setInt(ctx.index(), value.intValue());
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetSQLOutputContext<U> ctx, Integer value) throws SQLException {
            ctx.output().writeInt(value.intValue());
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final Integer get0(BindingGetResultSetContext<U> ctx) throws SQLException {
            return (Integer) JDBCUtils.wasNull(ctx.resultSet(), Integer.valueOf(ctx.resultSet().getInt(ctx.index())));
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final Integer get0(BindingGetStatementContext<U> ctx) throws SQLException {
            return (Integer) JDBCUtils.wasNull(ctx.statement(), Integer.valueOf(ctx.statement().getInt(ctx.index())));
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final Integer get0(BindingGetSQLInputContext<U> ctx) throws SQLException {
            return (Integer) JDBCUtils.wasNull(ctx.input(), Integer.valueOf(ctx.input().readInt()));
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final int sqltype(Statement statement, Configuration configuration) {
            return 4;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultBinding$DefaultLongBinding.class */
    public static final class DefaultLongBinding<U> extends InternalBinding<Long, U> {
        DefaultLongBinding(DataType<Long> dataType, Converter<Long, U> converter) {
            super(dataType, converter);
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final void setNull0(BindingSetStatementContext<U> ctx) throws SQLException {
            super.setNull0(ctx);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void sqlInline0(BindingSQLContext<U> ctx, Long value) {
            ctx.render().sql(value.longValue());
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetStatementContext<U> ctx, Long value) throws SQLException {
            ctx.statement().setLong(ctx.index(), value.longValue());
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetSQLOutputContext<U> ctx, Long value) throws SQLException {
            ctx.output().writeLong(value.longValue());
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final Long get0(BindingGetResultSetContext<U> ctx) throws SQLException {
            return (Long) JDBCUtils.wasNull(ctx.resultSet(), Long.valueOf(ctx.resultSet().getLong(ctx.index())));
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final Long get0(BindingGetStatementContext<U> ctx) throws SQLException {
            return (Long) JDBCUtils.wasNull(ctx.statement(), Long.valueOf(ctx.statement().getLong(ctx.index())));
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final Long get0(BindingGetSQLInputContext<U> ctx) throws SQLException {
            return (Long) JDBCUtils.wasNull(ctx.input(), Long.valueOf(ctx.input().readLong()));
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final int sqltype(Statement statement, Configuration configuration) {
            return -5;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultBinding$OffsetDateTimeParser.class */
    public static final class OffsetDateTimeParser {
        OffsetDateTimeParser() {
        }

        static final OffsetTime offsetTime(String string) {
            if (string == null) {
                return null;
            }
            int[] position = {0};
            return OffsetTime.of(parseLocalTime(string, position), parseOffset(string, position));
        }

        static final OffsetDateTime offsetDateTime(String string) {
            if (string == null) {
                return null;
            }
            int[] position = {0};
            LocalDate d = parseLocalDate(string, position);
            parseAnyChar(string, position, " T");
            LocalTime t = parseLocalTime(string, position);
            ZoneOffset offset = parseOffset(string, position);
            if (parseBCIf(string, position)) {
                return OffsetDateTime.of(d.withYear(1 - d.getYear()), t, offset);
            }
            return OffsetDateTime.of(d, t, offset);
        }

        static final LocalDate parseLocalDate(String string, int[] position) {
            int year = parseInt(string, position, 10);
            parseChar(string, position, '-');
            int month = parseInt(string, position, 2);
            parseChar(string, position, '-');
            int day = parseInt(string, position, 2);
            return LocalDate.of(year, month, day);
        }

        static final LocalTime parseLocalTime(String string, int[] position) {
            int hour = parseInt(string, position, 2);
            if (hour == 24) {
                hour %= 24;
            }
            parseChar(string, position, ':');
            int minute = parseInt(string, position, 2);
            int second = 0;
            int nano = 0;
            if (parseCharIf(string, position, ':')) {
                second = parseInt(string, position, 2);
                if (parseCharIf(string, position, '.')) {
                    nano = parseInt(string, position, 9, true);
                }
            }
            return LocalTime.of(hour, minute, second, nano);
        }

        /* JADX WARN: Code restructure failed: missing block: B:10:0x0036, code lost:            r0 = true;     */
        /* JADX WARN: Code restructure failed: missing block: B:11:0x003b, code lost:            r11 = r0;     */
        /* JADX WARN: Code restructure failed: missing block: B:12:0x003f, code lost:            if (r0 != false) goto L17;     */
        /* JADX WARN: Code restructure failed: missing block: B:14:0x0044, code lost:            if (r11 == false) goto L29;     */
        /* JADX WARN: Code restructure failed: missing block: B:15:0x0047, code lost:            r7 = parseInt(r5, r6, 1);     */
        /* JADX WARN: Code restructure failed: missing block: B:16:0x0058, code lost:            if (java.lang.Character.isDigit(r5.charAt(r6[0])) == false) goto L20;     */
        /* JADX WARN: Code restructure failed: missing block: B:17:0x005b, code lost:            r7 = (r7 * 10) + parseInt(r5, r6, 1);     */
        /* JADX WARN: Code restructure failed: missing block: B:19:0x006e, code lost:            if (parseCharIf(r5, r6, ':') == false) goto L23;     */
        /* JADX WARN: Code restructure failed: missing block: B:20:0x0071, code lost:            r8 = parseInt(r5, r6, 2);     */
        /* JADX WARN: Code restructure failed: missing block: B:22:0x007f, code lost:            if (parseCharIf(r5, r6, ':') == false) goto L26;     */
        /* JADX WARN: Code restructure failed: missing block: B:23:0x0082, code lost:            r9 = parseInt(r5, r6, 2);     */
        /* JADX WARN: Code restructure failed: missing block: B:25:0x008c, code lost:            if (r0 == false) goto L29;     */
        /* JADX WARN: Code restructure failed: missing block: B:26:0x008f, code lost:            r7 = -r7;        r8 = -r8;        r9 = -r9;     */
        /* JADX WARN: Code restructure failed: missing block: B:27:0x003a, code lost:            r0 = false;     */
        /* JADX WARN: Code restructure failed: missing block: B:2:0x000e, code lost:            if (parseCharIf(r5, r6, 'Z') == false) goto L4;     */
        /* JADX WARN: Code restructure failed: missing block: B:30:0x00a1, code lost:            return java.time.ZoneOffset.ofHoursMinutesSeconds(r7, r8, r9);     */
        /* JADX WARN: Code restructure failed: missing block: B:4:0x0018, code lost:            if (parseCharIf(r5, r6, ' ') == false) goto L31;     */
        /* JADX WARN: Code restructure failed: missing block: B:6:0x001e, code lost:            r0 = parseCharIf(r5, r6, '-');     */
        /* JADX WARN: Code restructure failed: missing block: B:7:0x0029, code lost:            if (r0 != false) goto L12;     */
        /* JADX WARN: Code restructure failed: missing block: B:9:0x0033, code lost:            if (parseCharIf(r5, r6, '+') == false) goto L12;     */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        private static final java.time.ZoneOffset parseOffset(java.lang.String r5, int[] r6) {
            /*
                r0 = 0
                r7 = r0
                r0 = 0
                r8 = r0
                r0 = 0
                r9 = r0
                r0 = r5
                r1 = r6
                r2 = 90
                boolean r0 = parseCharIf(r0, r1, r2)
                if (r0 != 0) goto L9a
            L11:
                r0 = r5
                r1 = r6
                r2 = 32
                boolean r0 = parseCharIf(r0, r1, r2)
                if (r0 == 0) goto L1e
                goto L11
            L1e:
                r0 = r5
                r1 = r6
                r2 = 45
                boolean r0 = parseCharIf(r0, r1, r2)
                r10 = r0
                r0 = r10
                if (r0 != 0) goto L3a
                r0 = r5
                r1 = r6
                r2 = 43
                boolean r0 = parseCharIf(r0, r1, r2)
                if (r0 == 0) goto L3a
                r0 = 1
                goto L3b
            L3a:
                r0 = 0
            L3b:
                r11 = r0
                r0 = r10
                if (r0 != 0) goto L47
                r0 = r11
                if (r0 == 0) goto L9a
            L47:
                r0 = r5
                r1 = r6
                r2 = 1
                int r0 = parseInt(r0, r1, r2)
                r7 = r0
                r0 = r5
                r1 = r6
                r2 = 0
                r1 = r1[r2]
                char r0 = r0.charAt(r1)
                boolean r0 = java.lang.Character.isDigit(r0)
                if (r0 == 0) goto L67
                r0 = r7
                r1 = 10
                int r0 = r0 * r1
                r1 = r5
                r2 = r6
                r3 = 1
                int r1 = parseInt(r1, r2, r3)
                int r0 = r0 + r1
                r7 = r0
            L67:
                r0 = r5
                r1 = r6
                r2 = 58
                boolean r0 = parseCharIf(r0, r1, r2)
                if (r0 == 0) goto L78
                r0 = r5
                r1 = r6
                r2 = 2
                int r0 = parseInt(r0, r1, r2)
                r8 = r0
            L78:
                r0 = r5
                r1 = r6
                r2 = 58
                boolean r0 = parseCharIf(r0, r1, r2)
                if (r0 == 0) goto L8a
                r0 = r5
                r1 = r6
                r2 = 2
                int r0 = parseInt(r0, r1, r2)
                r9 = r0
            L8a:
                r0 = r10
                if (r0 == 0) goto L9a
                r0 = r7
                int r0 = -r0
                r7 = r0
                r0 = r8
                int r0 = -r0
                r8 = r0
                r0 = r9
                int r0 = -r0
                r9 = r0
            L9a:
                r0 = r7
                r1 = r8
                r2 = r9
                java.time.ZoneOffset r0 = java.time.ZoneOffset.ofHoursMinutesSeconds(r0, r1, r2)
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: org.jooq.impl.DefaultBinding.OffsetDateTimeParser.parseOffset(java.lang.String, int[]):java.time.ZoneOffset");
        }

        private static final void parseAnyChar(String string, int[] position, String expected) {
            for (int i = 0; i < expected.length(); i++) {
                if (string.charAt(position[0]) == expected.charAt(i)) {
                    position[0] = position[0] + 1;
                    return;
                }
            }
            throw new IllegalArgumentException("Expected any of \"" + expected + "\" at position " + position[0] + " in " + string);
        }

        private static final boolean parseBCIf(String string, int[] position) {
            return parseCharIf(string, position, ' ') && parseCharIf(string, position, 'B') && parseCharIf(string, position, 'C');
        }

        private static final boolean parseCharIf(String string, int[] position, char expected) {
            boolean result = string.length() > position[0] && string.charAt(position[0]) == expected;
            if (result) {
                position[0] = position[0] + 1;
            }
            return result;
        }

        private static final void parseChar(String string, int[] position, char expected) {
            if (!parseCharIf(string, position, expected)) {
                throw new IllegalArgumentException("Expected '" + expected + "' at position " + position[0] + " in " + string);
            }
        }

        private static final int parseInt(String string, int[] position, int maxLength) {
            return parseInt(string, position, maxLength, false);
        }

        private static final int parseInt(String string, int[] position, int maxLength, boolean rightPad) {
            int result = 0;
            int pos = position[0];
            int length = 0;
            while (length < maxLength && pos + length < string.length()) {
                char c = string.charAt(pos + length);
                if (c != '+' || length != 0) {
                    int digit = c - '0';
                    if (digit < 0 || digit >= 10) {
                        break;
                    }
                    result = (result * 10) + digit;
                }
                length++;
            }
            if (rightPad && length < maxLength && result > 0) {
                for (int i = length; i < maxLength; i++) {
                    result *= 10;
                }
            }
            position[0] = pos + length;
            return result;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultBinding$DefaultOffsetDateTimeBinding.class */
    public static final class DefaultOffsetDateTimeBinding<U> extends InternalBinding<OffsetDateTime, U> {
        private static final DateTimeFormatter F_TIMESTAMPTZ = new DateTimeFormatterBuilder().appendValue(ChronoField.YEAR, 4, 10, SignStyle.NORMAL).appendLiteral('-').appendValue(ChronoField.MONTH_OF_YEAR, 2).appendLiteral('-').appendValue(ChronoField.DAY_OF_MONTH, 2).appendLiteral(' ').appendValue(ChronoField.HOUR_OF_DAY, 2).appendLiteral(':').appendValue(ChronoField.MINUTE_OF_HOUR, 2).appendLiteral(':').appendValue(ChronoField.SECOND_OF_MINUTE, 2).appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true).appendOffset("+HH:MM", "+00:00").toFormatter();
        private static final DateTimeFormatter ERA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.nnnnnnnnnZZZZZ G", Locale.US);

        DefaultOffsetDateTimeBinding(DataType<OffsetDateTime> dataType, Converter<OffsetDateTime, U> converter) {
            super(dataType, converter);
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final void setNull0(BindingSetStatementContext<U> ctx) throws SQLException {
            super.setNull0(ctx);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void sqlInline0(BindingSQLContext<U> ctx, OffsetDateTime value) {
            SQLDialect family = ctx.family();
            switch (family) {
                case HSQLDB:
                case TRINO:
                    ctx.render().visit(Keywords.K_TIMESTAMP).sql(" '").sql(escape(format(value, family), ctx.render())).sql('\'');
                    return;
                case SQLITE:
                    ctx.render().sql('\'').sql(escape(format(value, family), ctx.render())).sql('\'');
                    return;
                default:
                    ctx.render().visit(Keywords.K_TIMESTAMP_WITH_TIME_ZONE).sql(" '").sql(escape(format(value, family), ctx.render())).sql('\'');
                    return;
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetStatementContext<U> ctx, OffsetDateTime value) throws SQLException {
            SQLDialect family = ctx.family();
            if (!Boolean.FALSE.equals(ctx.settings().isBindOffsetDateTimeType())) {
                ctx.statement().setObject(ctx.index(), value);
            } else {
                ctx.statement().setString(ctx.index(), format(value, family));
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetSQLOutputContext<U> ctx, OffsetDateTime value) throws SQLException {
            if (!Boolean.FALSE.equals(ctx.settings().isBindOffsetDateTimeType())) {
                ctx.output().writeObject(value, JDBCType.TIMESTAMP_WITH_TIMEZONE);
                return;
            }
            throw new UnsupportedOperationException("Type " + String.valueOf(this.dataType) + " is not supported");
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final OffsetDateTime get0(BindingGetResultSetContext<U> ctx) throws SQLException {
            if (!Boolean.FALSE.equals(ctx.settings().isBindOffsetDateTimeType())) {
                return (OffsetDateTime) ctx.resultSet().getObject(ctx.index(), OffsetDateTime.class);
            }
            return OffsetDateTimeParser.offsetDateTime(ctx.resultSet().getString(ctx.index()));
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final OffsetDateTime get0(BindingGetStatementContext<U> ctx) throws SQLException {
            if (!Boolean.FALSE.equals(ctx.settings().isBindOffsetDateTimeType())) {
                return (OffsetDateTime) ctx.statement().getObject(ctx.index(), OffsetDateTime.class);
            }
            return OffsetDateTimeParser.offsetDateTime(ctx.statement().getString(ctx.index()));
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final OffsetDateTime get0(BindingGetSQLInputContext<U> ctx) throws SQLException {
            if (!Boolean.FALSE.equals(ctx.settings().isBindOffsetDateTimeType())) {
                return (OffsetDateTime) ctx.input().readObject(OffsetDateTime.class);
            }
            throw new UnsupportedOperationException("Type " + String.valueOf(this.dataType) + " is not supported");
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final int sqltype(Statement statement, Configuration configuration) {
            if (!Boolean.FALSE.equals(configuration.settings().isBindOffsetDateTimeType())) {
                return 2014;
            }
            return 12;
        }

        private static final String format(OffsetDateTime val, SQLDialect family) {
            if (family == SQLDialect.POSTGRES) {
                if (val.toEpochSecond() * 1000 == DefaultBinding.PG_DATE_POSITIVE_INFINITY) {
                    return "infinity";
                }
                if (val.toEpochSecond() * 1000 == DefaultBinding.PG_DATE_NEGATIVE_INFINITY) {
                    return "-infinity";
                }
            }
            if (family == SQLDialect.POSTGRES && val.getYear() <= 0) {
                return formatEra(val);
            }
            return val.format(F_TIMESTAMPTZ);
        }

        private static final String formatISO(OffsetDateTime val) {
            return val.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        }

        private static final String formatEra(OffsetDateTime val) {
            return val.format(ERA);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultBinding$DefaultOffsetTimeBinding.class */
    public static final class DefaultOffsetTimeBinding<U> extends InternalBinding<OffsetTime, U> {
        DefaultOffsetTimeBinding(DataType<OffsetTime> dataType, Converter<OffsetTime, U> converter) {
            super(dataType, converter);
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final void setNull0(BindingSetStatementContext<U> ctx) throws SQLException {
            super.setNull0(ctx);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void sqlInline0(BindingSQLContext<U> ctx, OffsetTime value) {
            switch (ctx.family()) {
                case HSQLDB:
                case TRINO:
                    ctx.render().visit(Keywords.K_TIME).sql(" '").sql(escape(format(value), ctx.render())).sql('\'');
                    return;
                case SQLITE:
                    ctx.render().sql('\'').sql(escape(format(value), ctx.render())).sql('\'');
                    return;
                default:
                    ctx.render().visit(Keywords.K_TIME_WITH_TIME_ZONE).sql(" '").sql(escape(format(value), ctx.render())).sql('\'');
                    return;
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetStatementContext<U> ctx, OffsetTime value) throws SQLException {
            if (Boolean.FALSE.equals(ctx.settings().isBindOffsetTimeType())) {
                String string = format(value);
                ctx.statement().setString(ctx.index(), string);
            } else {
                ctx.statement().setObject(ctx.index(), value);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetSQLOutputContext<U> ctx, OffsetTime value) throws SQLException {
            throw new UnsupportedOperationException("Type " + String.valueOf(this.dataType) + " is not supported");
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final OffsetTime get0(BindingGetResultSetContext<U> ctx) throws SQLException {
            if (!Boolean.FALSE.equals(ctx.settings().isBindOffsetTimeType())) {
                return (OffsetTime) ctx.resultSet().getObject(ctx.index(), OffsetTime.class);
            }
            return OffsetDateTimeParser.offsetTime(ctx.resultSet().getString(ctx.index()));
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final OffsetTime get0(BindingGetStatementContext<U> ctx) throws SQLException {
            if (!Boolean.FALSE.equals(ctx.settings().isBindOffsetTimeType())) {
                return (OffsetTime) ctx.statement().getObject(ctx.index(), OffsetTime.class);
            }
            return OffsetDateTimeParser.offsetTime(ctx.statement().getString(ctx.index()));
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final OffsetTime get0(BindingGetSQLInputContext<U> ctx) throws SQLException {
            throw new UnsupportedOperationException("Type " + String.valueOf(this.dataType) + " is not supported");
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final int sqltype(Statement statement, Configuration configuration) {
            if (!Boolean.FALSE.equals(configuration.settings().isBindOffsetTimeType())) {
                return 2013;
            }
            return 12;
        }

        private static final String format(OffsetTime val) {
            return StringUtils.replace(val.format(DateTimeFormatter.ISO_OFFSET_TIME), "Z", "+00:00");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultBinding$DefaultInstantBinding.class */
    public static final class DefaultInstantBinding<U> extends InternalBinding<Instant, U> {
        private static final ContextConverter<OffsetDateTime, Instant> CONVERTER = ContextConverter.ofNullable(OffsetDateTime.class, Instant.class, (BiFunction) ((Serializable) (t, x) -> {
            return t.toInstant();
        }), (BiFunction) ((Serializable) (i, x2) -> {
            return OffsetDateTime.ofInstant(i, ZoneOffset.UTC);
        }));
        private final DefaultOffsetDateTimeBinding<U> delegate;

        private static /* synthetic */ Object $deserializeLambda$(SerializedLambda lambda) {
            String implMethodName = lambda.getImplMethodName();
            boolean z = -1;
            switch (implMethodName.hashCode()) {
                case -1435945115:
                    if (implMethodName.equals("lambda$static$24b3668d$1")) {
                        z = false;
                        break;
                    }
                    break;
                case -1435945114:
                    if (implMethodName.equals("lambda$static$24b3668d$2")) {
                        z = true;
                        break;
                    }
                    break;
            }
            switch (z) {
                case false:
                    if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("java/util/function/BiFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("org/jooq/impl/DefaultBinding$DefaultInstantBinding") && lambda.getImplMethodSignature().equals("(Ljava/time/OffsetDateTime;Lorg/jooq/ConverterContext;)Ljava/time/Instant;")) {
                        return (t, x) -> {
                            return t.toInstant();
                        };
                    }
                    break;
                case true:
                    if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("java/util/function/BiFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("org/jooq/impl/DefaultBinding$DefaultInstantBinding") && lambda.getImplMethodSignature().equals("(Ljava/time/Instant;Lorg/jooq/ConverterContext;)Ljava/time/OffsetDateTime;")) {
                        return (i, x2) -> {
                            return OffsetDateTime.ofInstant(i, ZoneOffset.UTC);
                        };
                    }
                    break;
            }
            throw new IllegalArgumentException("Invalid lambda deserialization");
        }

        DefaultInstantBinding(DataType<Instant> dataType, Converter<Instant, U> converter) {
            super(dataType, converter);
            this.delegate = new DefaultOffsetDateTimeBinding<>(SQLDataType.OFFSETDATETIME, Converters.of(CONVERTER, converter()));
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final void setNull0(BindingSetStatementContext<U> ctx) throws SQLException {
            this.delegate.setNull0(ctx);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void sqlInline0(BindingSQLContext<U> ctx, Instant value) throws SQLException {
            this.delegate.sqlInline0((BindingSQLContext) ctx, CONVERTER.to(value, ctx.converterContext()));
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetStatementContext<U> ctx, Instant value) throws SQLException {
            this.delegate.set0((BindingSetStatementContext) ctx, CONVERTER.to(value, ctx.converterContext()));
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetSQLOutputContext<U> ctx, Instant value) throws SQLException {
            this.delegate.set0((BindingSetSQLOutputContext) ctx, CONVERTER.to(value, ctx.converterContext()));
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final Instant get0(BindingGetResultSetContext<U> ctx) throws SQLException {
            return CONVERTER.from(this.delegate.get0((BindingGetResultSetContext) ctx), ctx.converterContext());
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final Instant get0(BindingGetStatementContext<U> ctx) throws SQLException {
            return CONVERTER.from(this.delegate.get0((BindingGetStatementContext) ctx), ctx.converterContext());
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final Instant get0(BindingGetSQLInputContext<U> ctx) throws SQLException {
            return CONVERTER.from(this.delegate.get0((BindingGetSQLInputContext) ctx), ctx.converterContext());
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final int sqltype(Statement statement, Configuration configuration) throws SQLException {
            return this.delegate.sqltype(statement, configuration);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultBinding$CommercialOnlyBinding.class */
    public static final class CommercialOnlyBinding<U> extends InternalBinding<Object, U> {
        CommercialOnlyBinding(DataType<Object> dataType, Converter<Object, U> converter) {
            super(dataType, converter);
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final void set0(BindingSetStatementContext<U> ctx, Object value) throws SQLException {
            ctx.configuration().requireCommercial(() -> {
                return "The out of the box binding for " + this.dataType.getName() + " is available in the commercial jOOQ distribution only. Alternatively, you can implement your own custom binding.";
            });
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final void set0(BindingSetSQLOutputContext<U> ctx, Object value) throws SQLException {
            ctx.configuration().requireCommercial(() -> {
                return "The out of the box binding for " + this.dataType.getName() + " is available in the commercial jOOQ distribution only. Alternatively, you can implement your own custom binding.";
            });
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final Object get0(BindingGetResultSetContext<U> ctx) throws SQLException {
            ctx.configuration().requireCommercial(() -> {
                return "The out of the box binding for " + this.dataType.getName() + " is available in the commercial jOOQ distribution only. Alternatively, you can implement your own custom binding.";
            });
            return null;
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final Object get0(BindingGetStatementContext<U> ctx) throws SQLException {
            ctx.configuration().requireCommercial(() -> {
                return "The out of the box binding for " + this.dataType.getName() + " is available in the commercial jOOQ distribution only. Alternatively, you can implement your own custom binding.";
            });
            return null;
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final Object get0(BindingGetSQLInputContext<U> ctx) throws SQLException {
            ctx.configuration().requireCommercial(() -> {
                return "The out of the box binding for " + this.dataType.getName() + " is available in the commercial jOOQ distribution only. Alternatively, you can implement your own custom binding.";
            });
            return null;
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final int sqltype(Statement statement, Configuration configuration) throws SQLException {
            return 1111;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultBinding$DefaultOtherBinding.class */
    public static final class DefaultOtherBinding<U> extends InternalBinding<Object, U> {
        DefaultOtherBinding(DataType<Object> dataType, Converter<Object, U> converter) {
            super(dataType, converter);
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final void set0(BindingSetStatementContext<U> ctx, Object value) throws SQLException {
            InternalBinding b = (InternalBinding) DefaultBinding.binding(DefaultDataType.getDataType(ctx.dialect(), value.getClass()));
            if (b instanceof DefaultOtherBinding) {
                ctx.statement().setObject(ctx.index(), value);
            } else {
                b.set0(ctx, (BindingSetStatementContext<U>) b.dataType.convert(value));
            }
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final void setNull0(BindingSetStatementContext<U> ctx) throws SQLException {
            switch (ctx.family()) {
                default:
                    ctx.statement().setObject(ctx.index(), null);
                    return;
            }
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        void sqlInline0(BindingSQLContext<U> ctx, Object value) throws SQLException {
            Binding<?, ?> b = DefaultBinding.binding(DefaultDataType.getDataType(SQLDialect.DEFAULT, value.getClass(), SQLDataType.OTHER));
            if (b instanceof DefaultOtherBinding) {
                super.sqlInline0(ctx, value);
            } else if (b instanceof InternalBinding) {
                InternalBinding i = (InternalBinding) b;
                i.sqlInline0(ctx, value);
            } else {
                super.sqlInline0(ctx, value);
            }
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final void set0(BindingSetSQLOutputContext<U> ctx, Object value) throws SQLException {
            throw new DataTypeException("Type " + String.valueOf(this.dataType) + " is not supported");
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final Object get0(BindingGetResultSetContext<U> ctx) throws SQLException {
            return unlob(ctx.resultSet().getObject(ctx.index()));
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final Object get0(BindingGetStatementContext<U> ctx) throws SQLException {
            return unlob(ctx.statement().getObject(ctx.index()));
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final Object get0(BindingGetSQLInputContext<U> ctx) throws SQLException {
            return unlob(ctx.input().readObject());
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final int sqltype(Statement statement, Configuration configuration) {
            return 1111;
        }

        private static final Object unlob(Object object) throws SQLException {
            if (object instanceof Blob) {
                Blob blob = (Blob) object;
                try {
                    byte[] bytes = blob.getBytes(1L, Tools.asInt(blob.length()));
                    JDBCUtils.safeFree(blob);
                    return bytes;
                } catch (Throwable th) {
                    JDBCUtils.safeFree(blob);
                    throw th;
                }
            }
            if (object instanceof Clob) {
                Clob clob = (Clob) object;
                try {
                    String subString = clob.getSubString(1L, Tools.asInt(clob.length()));
                    JDBCUtils.safeFree(clob);
                    return subString;
                } catch (Throwable th2) {
                    JDBCUtils.safeFree(clob);
                    throw th2;
                }
            }
            return object;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultBinding$DefaultRowIdBinding.class */
    public static final class DefaultRowIdBinding<U> extends InternalBinding<RowId, U> {
        DefaultRowIdBinding(DataType<RowId> dataType, Converter<RowId, U> converter) {
            super(dataType, converter);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetStatementContext<U> ctx, RowId value) throws SQLException {
            ctx.statement().setObject(ctx.index(), value.value());
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetSQLOutputContext<U> ctx, RowId value) throws SQLException {
            throw new DataTypeException("Type " + String.valueOf(this.dataType) + " is not supported");
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final RowId get0(BindingGetResultSetContext<U> ctx) throws SQLException {
            return new RowIdImpl(ctx.resultSet().getObject(ctx.index()));
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final RowId get0(BindingGetStatementContext<U> ctx) throws SQLException {
            return new RowIdImpl(ctx.statement().getObject(ctx.index()));
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final RowId get0(BindingGetSQLInputContext<U> ctx) throws SQLException {
            throw new DataTypeException("Type " + String.valueOf(this.dataType) + " is not supported");
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final int sqltype(Statement statement, Configuration configuration) {
            return -8;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultBinding$DefaultRecordBinding.class */
    public static final class DefaultRecordBinding<U> extends InternalBinding<Record, U> {
        static final Set<SQLDialect> REQUIRE_RECORD_CAST = SQLDialect.supportedBy(SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB);

        DefaultRecordBinding(DataType<Record> dataType, Converter<Record, U> converter) {
            super(dataType, converter);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public void sqlBind0(BindingSQLContext<U> ctx, Record value) throws SQLException {
            Cast.renderCastIf(ctx.render(), c -> {
                super.sqlBind0(ctx, (BindingSQLContext) value);
            }, c2 -> {
                pgRenderRecordCast(ctx.render());
            }, () -> {
                return REQUIRE_RECORD_CAST.contains(ctx.dialect());
            });
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void sqlInline0(BindingSQLContext<U> ctx, Record value) throws SQLException {
            Cast.renderCastIf(ctx.render(), c -> {
                if (REQUIRE_RECORD_CAST.contains(ctx.dialect())) {
                    ctx.render().visit((Field<?>) DSL.inline(PostgresUtils.toPGString(value)));
                } else {
                    ctx.render().visit((Field<?>) new QualifiedRecordConstant((QualifiedRecord) value, Tools.getRecordQualifier((DataType<?>) this.dataType)));
                }
            }, c2 -> {
                pgRenderRecordCast(ctx.render());
            }, () -> {
                return REQUIRE_RECORD_CAST.contains(ctx.dialect());
            });
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final void register0(BindingRegisterContext<U> ctx) throws SQLException {
            super.register0(ctx);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetStatementContext<U> ctx, Record value) throws SQLException {
            if (REQUIRE_RECORD_CAST.contains(ctx.dialect()) && value != null) {
                ctx.statement().setString(ctx.index(), PostgresUtils.toPGString(value));
            } else {
                DefaultExecuteContext.localExecuteContext(ctx.executeContext(), () -> {
                    ctx.statement().setObject(ctx.index(), value);
                    return null;
                });
            }
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final void setNull0(BindingSetStatementContext<U> ctx) throws SQLException {
            super.setNull0(ctx);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetSQLOutputContext<U> ctx, Record value) throws SQLException {
            if (value instanceof QualifiedRecord) {
                QualifiedRecord<?> q = (QualifiedRecord) value;
                ctx.output().writeObject(q);
                return;
            }
            throw new UnsupportedOperationException("Type " + String.valueOf(this.dataType) + " is not supported");
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final Record get0(BindingGetResultSetContext<U> ctx) throws SQLException {
            switch (ctx.family()) {
                case POSTGRES:
                case YUGABYTEDB:
                    return pgNewRecord(ctx, this.dataType.getType(), (AbstractRow) this.dataType.getRow(), ctx.resultSet().getObject(ctx.index()));
                default:
                    if (UDTRecord.class.isAssignableFrom(this.dataType.getType())) {
                        return (Record) DefaultExecuteContext.localExecuteContext(ctx.executeContext(), () -> {
                            return (Record) ctx.resultSet().getObject(ctx.index(), DefaultBinding.typeMap(this.dataType.getType(), ctx));
                        });
                    }
                    return readMultiset(ctx, this.dataType);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final Record get0(BindingGetStatementContext<U> ctx) throws SQLException {
            switch (ctx.family()) {
                case POSTGRES:
                case YUGABYTEDB:
                    return pgNewRecord(ctx, this.dataType.getType(), (AbstractRow) this.dataType.getRow(), ctx.statement().getObject(ctx.index()));
                default:
                    return (Record) DefaultExecuteContext.localExecuteContext(ctx.executeContext(), () -> {
                        return (Record) ctx.statement().getObject(ctx.index(), DefaultBinding.typeMap(this.dataType.getType(), ctx));
                    });
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final Record get0(BindingGetSQLInputContext<U> ctx) throws SQLException {
            return (Record) ctx.input().readObject();
        }

        static final <R extends Record> R readMultiset(BindingGetResultSetContext<?> ctx, DataType<R> type) throws SQLException {
            Result<R> result;
            AbstractRow<R> row = (AbstractRow) type.getRow();
            if (row.size() == 1 && Tools.emulateMultiset(ctx.configuration()) != NestedCollectionEmulation.NATIVE) {
                result = new ResultImpl(ctx.configuration(), row);
                result.add(Tools.newRecord(true, type.getRecordType(), row, ctx.configuration()).operate(r -> {
                    DefaultBindingGetResultSetContext<?> c = new DefaultBindingGetResultSetContext<>(ctx.executeContext(), ctx.resultSet(), ctx.index());
                    r.field(0).getBinding().get(c);
                    r.fromArray(c.value());
                    return r;
                }));
            } else {
                result = DefaultResultBinding.readMultiset(ctx, row, type.getType(), s -> {
                    if (s == null || !(s.startsWith(PropertyAccessor.PROPERTY_KEY_PREFIX) || s.startsWith("{"))) {
                        return null;
                    }
                    return "[" + s + "]";
                }, s2 -> {
                    if (s2 == null || !s2.startsWith("<")) {
                        return null;
                    }
                    return "<result>" + s2 + "</result>";
                }, s3 -> {
                    if (!(s3 instanceof Struct)) {
                        return null;
                    }
                    Struct x = (Struct) s3;
                    return Arrays.asList(x);
                });
            }
            if (Tools.isEmpty((Collection<?>) result)) {
                return null;
            }
            return (R) result.get(0);
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final int sqltype(Statement statement, Configuration configuration) {
            return 2002;
        }

        final void pgRenderRecordCast(Context<?> ctx) {
            DataType<T> dataType = this.dataType;
            if (dataType instanceof UDTDataType) {
                UDTDataType<?> u = (UDTDataType) dataType;
                ctx.visit((QueryPart) StringUtils.defaultIfNull(Tools.getMappedUDT(ctx, u.udt), u.udt));
                return;
            }
            DataType<T> dataType2 = this.dataType;
            if (dataType2 instanceof TableDataType) {
                TableDataType<?> t = (TableDataType) dataType2;
                ctx.visit((QueryPart) StringUtils.defaultIfNull(Tools.getMappedTable(ctx, t.table), t.table));
            } else if (this.dataType.isUDT()) {
                RecordQualifier<?> q = Tools.getRecordQualifier((DataType<?>) this.dataType);
                ctx.visit((QueryPart) StringUtils.defaultIfNull(Tools.getMappedQualifier(ctx, q), q));
            } else {
                ctx.visit(this.dataType.getQualifiedName());
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static final <T, U> U pgFromString(BindingScope ctx, Field<U> field, String string) {
            ContextConverter<?, U> converter = field.getConverter();
            Class<?> type = Reflect.wrapper(converter.fromType());
            if (string == null) {
                return null;
            }
            if (type != Blob.class) {
                if (type == Boolean.class) {
                    return converter.from(Convert.convert(string, Boolean.class), ctx.converterContext());
                }
                if (type == BigInteger.class) {
                    return converter.from(new BigInteger(string), ctx.converterContext());
                }
                if (type == BigDecimal.class) {
                    return converter.from(new BigDecimal(string), ctx.converterContext());
                }
                if (type == Byte.class) {
                    return converter.from(Byte.valueOf(string), ctx.converterContext());
                }
                if (type == byte[].class) {
                    return converter.from(PostgresUtils.toBytes(string), ctx.converterContext());
                }
                if (type != Clob.class) {
                    if (type == Date.class) {
                        return converter.from(Date.valueOf(string), ctx.converterContext());
                    }
                    if (type == Double.class) {
                        return converter.from(Double.valueOf(string), ctx.converterContext());
                    }
                    if (type == Float.class) {
                        return converter.from(Float.valueOf(string), ctx.converterContext());
                    }
                    if (type == Integer.class) {
                        return converter.from(Integer.valueOf(string), ctx.converterContext());
                    }
                    if (type == Long.class) {
                        return converter.from(Long.valueOf(string), ctx.converterContext());
                    }
                    if (type == Short.class) {
                        return converter.from(Short.valueOf(string), ctx.converterContext());
                    }
                    if (type == String.class) {
                        return converter.from(string, ctx.converterContext());
                    }
                    if (type == Time.class) {
                        return converter.from(Time.valueOf(string), ctx.converterContext());
                    }
                    if (type == Timestamp.class) {
                        return converter.from(Timestamp.valueOf(Convert.patchIso8601Timestamp(string, false)), ctx.converterContext());
                    }
                    if (type == LocalTime.class) {
                        return converter.from(LocalTime.parse(string), ctx.converterContext());
                    }
                    if (type == LocalDate.class) {
                        return converter.from(LocalDate.parse(string), ctx.converterContext());
                    }
                    if (type == LocalDateTime.class) {
                        return converter.from(LocalDateTime.parse(Convert.patchIso8601Timestamp(string, true)), ctx.converterContext());
                    }
                    if (type == OffsetTime.class) {
                        return converter.from(OffsetDateTimeParser.offsetTime(string), ctx.converterContext());
                    }
                    if (type == OffsetDateTime.class) {
                        return converter.from(OffsetDateTimeParser.offsetDateTime(string), ctx.converterContext());
                    }
                    if (type == Instant.class) {
                        return converter.from(OffsetDateTimeParser.offsetDateTime(string).toInstant(), ctx.converterContext());
                    }
                    if (type == JSON.class) {
                        return converter.from(JSON.json(string), ctx.converterContext());
                    }
                    if (type == JSONB.class) {
                        return converter.from(JSONB.jsonb(string), ctx.converterContext());
                    }
                    if (type == UByte.class) {
                        return converter.from(UByte.valueOf(string), ctx.converterContext());
                    }
                    if (type == UShort.class) {
                        return converter.from(UShort.valueOf(string), ctx.converterContext());
                    }
                    if (type == UInteger.class) {
                        return converter.from(UInteger.valueOf(string), ctx.converterContext());
                    }
                    if (type == ULong.class) {
                        return converter.from(ULong.valueOf(string), ctx.converterContext());
                    }
                    if (type == UUID.class) {
                        return converter.from(UUID.fromString(string), ctx.converterContext());
                    }
                    if (type == XML.class) {
                        return converter.from(XML.xml(string), ctx.converterContext());
                    }
                    if (type == Year.class) {
                        return converter.from(Year.parse(string), ctx.converterContext());
                    }
                    if (type == YearToMonth.class) {
                        return converter.from(PostgresUtils.toYearToMonth(string), ctx.converterContext());
                    }
                    if (type == YearToSecond.class) {
                        return converter.from(PostgresUtils.toYearToSecond(string), ctx.converterContext());
                    }
                    if (type == DayToSecond.class) {
                        return converter.from(PostgresUtils.toDayToSecond(string), ctx.converterContext());
                    }
                    if (type.isArray()) {
                        return converter.from(pgNewArray(ctx, field, type, string), ctx.converterContext());
                    }
                    if (EnumType.class.isAssignableFrom(type)) {
                        return converter.from(DefaultEnumTypeBinding.getEnumType(type, string), ctx.converterContext());
                    }
                    if (Result.class.isAssignableFrom(type)) {
                        if (string.startsWith("<")) {
                            return converter.from(DefaultResultBinding.readMultisetXML(ctx, (AbstractRow) field.getDataType().getRow(), field.getDataType().getRecordType(), string), ctx.converterContext());
                        }
                        return converter.from(DefaultResultBinding.readMultisetJSON(ctx, (AbstractRow) field.getDataType().getRow(), field.getDataType().getRecordType(), string), ctx.converterContext());
                    }
                    if (Record.class.isAssignableFrom(type) && (!InternalRecord.class.isAssignableFrom(type) || type == converter.fromType())) {
                        return converter.from(pgNewRecord(ctx, type, (AbstractRow) field.getDataType().getRow(), string), ctx.converterContext());
                    }
                    if (type == Object.class) {
                        return converter.from(string, ctx.converterContext());
                    }
                    if (type != Reflect.wrapper(converter.toType())) {
                        return converter.from(pgFromString(ctx, DSL.field("converted_field", ConvertedDataType.delegate(field.getDataType())), string), ctx.converterContext());
                    }
                }
            }
            throw new UnsupportedOperationException("Class " + String.valueOf(type) + " is not supported");
        }

        static final Record pgNewRecord(BindingScope ctx, Class<?> type, AbstractRow<?> fields, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Record) {
                Record r = (Record) object;
                return r;
            }
            String s = object.toString();
            List<String> values = PostgresUtils.toPGObject(s);
            if (fields == null && Record.class.isAssignableFrom(type)) {
                fields = Tools.row0((Field<?>[]) Tools.fields(values.size(), SQLDataType.VARCHAR));
            }
            return Tools.newRecord(true, (Class) type, (AbstractRow) fields).operate(r2 -> {
                Row row = r2.fieldsRow();
                for (int i = 0; i < row.size(); i++) {
                    pgSetValue(ctx, r2, row.field(i), (String) values.get(i));
                }
                r2.changed(false);
                return r2;
            });
        }

        /* JADX WARN: Multi-variable type inference failed */
        private static final <T> void pgSetValue(BindingScope ctx, Record record, Field<T> field, String value) {
            record.set(field, pgFromString(ctx, field, value));
        }

        private static final Object[] pgNewArray(BindingScope ctx, Field<?> field, Class<?> type, String string) {
            if (string == null) {
                return null;
            }
            DataType<?> t = field.getDataType();
            try {
                return Tools.map(PostgresUtils.toPGArray(string), v -> {
                    return pgFromString(ctx, DSL.field("array_element", ConvertedDataType.delegate(t).getArrayComponentDataType()), v);
                }, size -> {
                    return (Object[]) java.lang.reflect.Array.newInstance(type.getComponentType(), size);
                });
            } catch (Exception e) {
                if (type.getComponentType().getSimpleName().equals("UnknownType")) {
                    throw new DataTypeException("Error while creating array for UnknownType. Please provide an explicit Class<U> type to your converter, see https://github.com/jOOQ/jOOQ/issues/11823", e);
                }
                throw new DataTypeException("Error while creating array", e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultBinding$DefaultResultBinding.class */
    public static final class DefaultResultBinding<U> extends InternalBinding<Result<?>, U> {
        DefaultResultBinding(DataType<Result<?>> dataType, Converter<Result<?>, U> converter) {
            super(dataType, converter);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetStatementContext<U> ctx, Result<?> value) throws SQLException {
            throw new UnsupportedOperationException("Cannot bind a value of type Result to a PreparedStatement");
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetSQLOutputContext<U> ctx, Result<?> value) throws SQLException {
            throw new UnsupportedOperationException("Cannot bind a value of type Result to a SQLOutput");
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final Result<?> get0(BindingGetResultSetContext<U> ctx) throws SQLException {
            Field<?> field = Tools.uncoerce(ctx.field());
            if (field.getDataType().isMultiset()) {
                return readMultiset(ctx, field.getDataType());
            }
            return ctx.configuration().dsl().fetch((ResultSet) Convert.convert(ctx.resultSet().getObject(ctx.index()), ResultSet.class));
        }

        static final <R extends Record> Result<R> readMultiset(BindingGetResultSetContext<?> ctx, DataType<Result<R>> type) throws SQLException {
            return readMultiset(ctx, (AbstractRow) type.getRow(), type.getRecordType(), java.util.function.Function.identity(), java.util.function.Function.identity(), java.util.function.Function.identity());
        }

        static final <R extends Record> Result<R> readMultiset(BindingGetResultSetContext<?> ctx, AbstractRow<R> row, Class<R> recordType, java.util.function.Function<String, String> jsonStringPatch, java.util.function.Function<String, String> xmlStringPatch, java.util.function.Function<Object, List<Struct>> nativePatch) throws SQLException {
            NestedCollectionEmulation emulation = Tools.emulateMultiset(ctx.configuration());
            switch (emulation) {
                case JSON:
                case JSONB:
                    if (emulation == NestedCollectionEmulation.JSONB && DefaultJSONBBinding.EMULATE_AS_BLOB.contains(ctx.dialect())) {
                        byte[] bytes = ctx.resultSet().getBytes(ctx.index());
                        return (Result) Tools.apply(jsonStringPatch.apply(bytes == null ? null : Source.of(bytes, ctx.configuration().charsetProvider().provide()).readString()), s -> {
                            return readMultisetJSON(ctx, row, recordType, s);
                        });
                    }
                    return (Result) Tools.apply(jsonStringPatch.apply(ctx.resultSet().getString(ctx.index())), s2 -> {
                        return readMultisetJSON(ctx, row, recordType, s2);
                    });
                case XML:
                    return (Result) Tools.apply(xmlStringPatch.apply(ctx.resultSet().getString(ctx.index())), s3 -> {
                        return readMultisetXML(ctx, row, recordType, s3);
                    });
                default:
                    throw new UnsupportedOperationException("Multiset emulation not yet supported: " + String.valueOf(emulation));
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static final <R extends Record> Result<R> readMultisetXML(Scope ctx, AbstractRow<R> row, Class<R> recordType, String s) {
            if (s.startsWith("<")) {
                return new XMLHandler(ctx.dsl(), row, recordType).read(s);
            }
            return readMultisetScalar(ctx, row, recordType, s);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static final <R extends Record> Result<R> readMultisetJSON(Scope ctx, AbstractRow<R> row, Class<R> recordType, String s) {
            if (s.startsWith("{") || s.startsWith(PropertyAccessor.PROPERTY_KEY_PREFIX)) {
                return new JSONReader(ctx.dsl(), row, recordType, true).read(new StringReader(DefaultJSONBinding.patchSnowflakeJSON(ctx, s)), true);
            }
            return readMultisetScalar(ctx, row, recordType, s);
        }

        static final <R extends Record> Result<R> readMultisetScalar(Scope ctx, AbstractRow<R> row, Class<R> recordType, String s) {
            Result<R> result = new ResultImpl<>(ctx.configuration(), row);
            result.add(Tools.newRecord(true, recordType, row, ctx.configuration()).operate(r -> {
                r.from(Arrays.asList(s));
                return r;
            }));
            return result;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final Result<?> get0(BindingGetStatementContext<U> ctx) throws SQLException {
            return ctx.configuration().dsl().fetch((ResultSet) Convert.convert(ctx.statement().getObject(ctx.index()), ResultSet.class));
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final Result<?> get0(BindingGetSQLInputContext<U> ctx) throws SQLException {
            throw new UnsupportedOperationException("Cannot get a value of type Result from a SQLInput");
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final int sqltype(Statement statement, Configuration configuration) {
            switch (configuration.family()) {
                case H2:
                    return -10;
                default:
                    return 1111;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultBinding$DefaultShortBinding.class */
    public static final class DefaultShortBinding<U> extends InternalBinding<Short, U> {
        DefaultShortBinding(DataType<Short> dataType, Converter<Short, U> converter) {
            super(dataType, converter);
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final void setNull0(BindingSetStatementContext<U> ctx) throws SQLException {
            super.setNull0(ctx);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void sqlInline0(BindingSQLContext<U> ctx, Short value) {
            ctx.render().sql((int) value.shortValue());
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetStatementContext<U> ctx, Short value) throws SQLException {
            ctx.statement().setShort(ctx.index(), value.shortValue());
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetSQLOutputContext<U> ctx, Short value) throws SQLException {
            ctx.output().writeShort(value.shortValue());
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final Short get0(BindingGetResultSetContext<U> ctx) throws SQLException {
            return (Short) JDBCUtils.wasNull(ctx.resultSet(), Short.valueOf(ctx.resultSet().getShort(ctx.index())));
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final Short get0(BindingGetStatementContext<U> ctx) throws SQLException {
            return (Short) JDBCUtils.wasNull(ctx.statement(), Short.valueOf(ctx.statement().getShort(ctx.index())));
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final Short get0(BindingGetSQLInputContext<U> ctx) throws SQLException {
            return (Short) JDBCUtils.wasNull(ctx.input(), Short.valueOf(ctx.input().readShort()));
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final int sqltype(Statement statement, Configuration configuration) {
            return 5;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultBinding$DefaultStringBinding.class */
    public static final class DefaultStringBinding<U> extends InternalBinding<String, U> {
        DefaultStringBinding(DataType<String> dataType, Converter<String, U> converter) {
            super(dataType, converter);
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final void setNull0(BindingSetStatementContext<U> ctx) throws SQLException {
            super.setNull0(ctx);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void sqlInline0(BindingSQLContext<U> ctx, String value) throws SQLException {
            if (ctx.family() == SQLDialect.DERBY) {
                sqlInlineWorkaround6516(ctx, value, 8192, "");
            } else {
                super.sqlInline0(ctx, (BindingSQLContext<U>) value);
            }
        }

        private final void sqlInlineWorkaround6516(BindingSQLContext<U> ctx, String value, int limit, String prefix) throws SQLException {
            int l = value.length();
            if (l > limit) {
                ctx.render().sql('(');
                int i = 0;
                while (true) {
                    int i2 = i;
                    if (i2 < l) {
                        if (i2 > 0) {
                            ctx.render().sql(" || ");
                        }
                        ctx.render().sql(prefix).sql("(");
                        super.sqlInline0(ctx, (BindingSQLContext<U>) value.substring(i2, Math.min(l, i2 + limit)));
                        ctx.render().sql(')');
                        i = i2 + limit;
                    } else {
                        ctx.render().sql(')');
                        return;
                    }
                }
            } else {
                super.sqlInline0(ctx, (BindingSQLContext<U>) value);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetStatementContext<U> ctx, String value) throws SQLException {
            ctx.statement().setString(ctx.index(), value);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetSQLOutputContext<U> ctx, String value) throws SQLException {
            ctx.output().writeString(value);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final String get0(BindingGetResultSetContext<U> ctx) throws SQLException {
            return ctx.resultSet().getString(ctx.index());
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final String get0(BindingGetStatementContext<U> ctx) throws SQLException {
            return ctx.statement().getString(ctx.index());
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final String get0(BindingGetSQLInputContext<U> ctx) throws SQLException {
            return ctx.input().readString();
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final int sqltype(Statement statement, Configuration configuration) {
            return 12;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultBinding$DefaultNStringBinding.class */
    public static final class DefaultNStringBinding<U> extends InternalBinding<String, U> {
        private final DefaultStringBinding<U> fallback;

        DefaultNStringBinding(DataType<String> dataType, Converter<String, U> converter) {
            super(dataType, converter);
            this.fallback = new DefaultStringBinding<>(dataType, converter);
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final void setNull0(BindingSetStatementContext<U> ctx) throws SQLException {
            super.setNull0(ctx);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public void sqlInline0(BindingSQLContext<U> ctx, String value) throws SQLException {
            if (NO_SUPPORT_NVARCHAR.contains(ctx.dialect())) {
                this.fallback.sqlInline0((BindingSQLContext) ctx, value);
            } else {
                ctx.render().sql('N');
                super.sqlInline0(ctx, (BindingSQLContext<U>) value);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetStatementContext<U> ctx, String value) throws SQLException {
            if (NO_SUPPORT_NVARCHAR.contains(ctx.dialect())) {
                this.fallback.set0((BindingSetStatementContext) ctx, value);
            } else {
                ctx.statement().setNString(ctx.index(), value);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetSQLOutputContext<U> ctx, String value) throws SQLException {
            if (NO_SUPPORT_NVARCHAR.contains(ctx.dialect())) {
                this.fallback.set0((BindingSetSQLOutputContext) ctx, value);
            }
            ctx.output().writeNString(value);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final String get0(BindingGetResultSetContext<U> ctx) throws SQLException {
            if (NO_SUPPORT_NVARCHAR.contains(ctx.dialect())) {
                return this.fallback.get0((BindingGetResultSetContext) ctx);
            }
            return ctx.resultSet().getNString(ctx.index());
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final String get0(BindingGetStatementContext<U> ctx) throws SQLException {
            if (NO_SUPPORT_NVARCHAR.contains(ctx.dialect())) {
                return this.fallback.get0((BindingGetStatementContext) ctx);
            }
            return ctx.statement().getNString(ctx.index());
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final String get0(BindingGetSQLInputContext<U> ctx) throws SQLException {
            if (NO_SUPPORT_NVARCHAR.contains(ctx.dialect())) {
                return this.fallback.get0((BindingGetSQLInputContext) ctx);
            }
            return ctx.input().readNString();
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final int sqltype(Statement statement, Configuration configuration) {
            if (NO_SUPPORT_NVARCHAR.contains(configuration.dialect())) {
                return this.fallback.sqltype(statement, configuration);
            }
            return -9;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultBinding$DefaultTimeBinding.class */
    public static final class DefaultTimeBinding<U> extends InternalBinding<Time, U> {
        DefaultTimeBinding(DataType<Time> dataType, Converter<Time, U> converter) {
            super(dataType, converter);
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final void setNull0(BindingSetStatementContext<U> ctx) throws SQLException {
            super.setNull0(ctx);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void sqlInline0(BindingSQLContext<U> ctx, Time value) {
            switch (ctx.family()) {
                case DERBY:
                    ctx.render().visit(Keywords.K_TIME).sql("('").sql(escape(value, ctx.render())).sql("')");
                    return;
                case SQLITE:
                    ctx.render().sql('\'').sql(new SimpleDateFormat("HH:mm:ss").format((java.util.Date) value)).sql('\'');
                    return;
                default:
                    if (DefaultBinding.REQUIRE_JDBC_DATE_LITERAL.contains(ctx.dialect())) {
                        ctx.render().sql("{t '").sql(escape(value, ctx.render())).sql("'}");
                        return;
                    } else {
                        ctx.render().visit(Keywords.K_TIME).sql(" '").sql(escape(value, ctx.render())).sql('\'');
                        return;
                    }
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetStatementContext<U> ctx, Time value) throws SQLException {
            if (ctx.family() == SQLDialect.SQLITE) {
                ctx.statement().setString(ctx.index(), value.toString());
            } else {
                ctx.statement().setTime(ctx.index(), value);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetSQLOutputContext<U> ctx, Time value) throws SQLException {
            ctx.output().writeTime(value);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final Time get0(BindingGetResultSetContext<U> ctx) throws SQLException {
            if (ctx.family() == SQLDialect.SQLITE) {
                String time = ctx.resultSet().getString(ctx.index());
                if (time == null) {
                    return null;
                }
                return new Time(DefaultBinding.parse(Time.class, time));
            }
            return ctx.resultSet().getTime(ctx.index());
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final Time get0(BindingGetStatementContext<U> ctx) throws SQLException {
            return ctx.statement().getTime(ctx.index());
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final Time get0(BindingGetSQLInputContext<U> ctx) throws SQLException {
            return ctx.input().readTime();
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final int sqltype(Statement statement, Configuration configuration) {
            return 92;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultBinding$DefaultTimestampBinding.class */
    public static final class DefaultTimestampBinding<U> extends InternalBinding<Timestamp, U> {
        private static final Set<SQLDialect> INLINE_AS_STRING_LITERAL = SQLDialect.supportedBy(SQLDialect.SQLITE);

        DefaultTimestampBinding(DataType<Timestamp> dataType, Converter<Timestamp, U> converter) {
            super(dataType, converter);
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final void setNull0(BindingSetStatementContext<U> ctx) throws SQLException {
            super.setNull0(ctx);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void sqlInline0(BindingSQLContext<U> ctx, Timestamp value) {
            if (INLINE_AS_STRING_LITERAL.contains(ctx.dialect())) {
                ctx.render().sql('\'').sql(escape(value, ctx.render())).sql('\'');
                return;
            }
            if (ctx.family() == SQLDialect.DERBY) {
                ctx.render().visit(Keywords.K_TIMESTAMP).sql("('").sql(escape(value, ctx.render())).sql("')");
                return;
            }
            if (ctx.family() == SQLDialect.CUBRID) {
                ctx.render().visit(Keywords.K_DATETIME).sql(" '").sql(escape(value, ctx.render())).sql('\'');
            } else if (DefaultBinding.REQUIRE_JDBC_DATE_LITERAL.contains(ctx.dialect())) {
                ctx.render().sql("{ts '").sql(escape(value, ctx.render())).sql("'}");
            } else {
                ctx.render().visit(Keywords.K_TIMESTAMP).sql(" '").sql(format(value, ctx.render())).sql('\'');
            }
        }

        private final String format(Timestamp value, RenderContext render) {
            if (render.family() == SQLDialect.POSTGRES) {
                if (value.getTime() == DefaultBinding.PG_DATE_POSITIVE_INFINITY) {
                    return "infinity";
                }
                if (value.getTime() == DefaultBinding.PG_DATE_NEGATIVE_INFINITY) {
                    return "-infinity";
                }
            }
            return escape(value, render);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetStatementContext<U> ctx, Timestamp value) throws SQLException {
            if (ctx.family() == SQLDialect.SQLITE) {
                ctx.statement().setString(ctx.index(), value.toString());
            } else {
                ctx.statement().setTimestamp(ctx.index(), value);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetSQLOutputContext<U> ctx, Timestamp value) throws SQLException {
            ctx.output().writeTimestamp(value);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final Timestamp get0(BindingGetResultSetContext<U> ctx) throws SQLException {
            if (ctx.family() == SQLDialect.SQLITE) {
                String timestamp = ctx.resultSet().getString(ctx.index());
                if (timestamp == null) {
                    return null;
                }
                return new Timestamp(DefaultBinding.parse(Timestamp.class, timestamp));
            }
            return ctx.resultSet().getTimestamp(ctx.index());
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final Timestamp get0(BindingGetStatementContext<U> ctx) throws SQLException {
            return ctx.statement().getTimestamp(ctx.index());
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final Timestamp get0(BindingGetSQLInputContext<U> ctx) throws SQLException {
            return ctx.input().readTimestamp();
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final int sqltype(Statement statement, Configuration configuration) {
            return 93;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultBinding$DefaultUUIDBinding.class */
    public static final class DefaultUUIDBinding<U> extends InternalBinding<UUID, U> {
        DefaultUUIDBinding(DataType<UUID> dataType, Converter<UUID, U> converter) {
            super(dataType, converter);
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final void setNull0(BindingSetStatementContext<U> ctx) throws SQLException {
            super.setNull0(ctx);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetStatementContext<U> ctx, UUID value) throws SQLException {
            switch (ctx.family()) {
                case H2:
                case POSTGRES:
                case YUGABYTEDB:
                    ctx.statement().setObject(ctx.index(), value);
                    return;
                default:
                    ctx.statement().setString(ctx.index(), value.toString());
                    return;
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetSQLOutputContext<U> ctx, UUID value) throws SQLException {
            ctx.output().writeString(value.toString());
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final UUID get0(BindingGetResultSetContext<U> ctx) throws SQLException {
            switch (ctx.family()) {
                case H2:
                case POSTGRES:
                case YUGABYTEDB:
                    Object o = ctx.resultSet().getObject(ctx.index());
                    if (o == null) {
                        return null;
                    }
                    if (o instanceof UUID) {
                        UUID u = (UUID) o;
                        return u;
                    }
                    return (UUID) Convert.convert(o.toString(), UUID.class);
                default:
                    return (UUID) Convert.convert(ctx.resultSet().getString(ctx.index()), UUID.class);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final UUID get0(BindingGetStatementContext<U> ctx) throws SQLException {
            switch (ctx.family()) {
                case H2:
                case POSTGRES:
                case YUGABYTEDB:
                    return (UUID) ctx.statement().getObject(ctx.index());
                default:
                    return (UUID) Convert.convert(ctx.statement().getString(ctx.index()), UUID.class);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final UUID get0(BindingGetSQLInputContext<U> ctx) throws SQLException {
            return (UUID) Convert.convert(ctx.input().readString(), UUID.class);
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final int sqltype(Statement statement, Configuration configuration) {
            switch (configuration.family()) {
                case POSTGRES:
                case YUGABYTEDB:
                    return 1111;
                default:
                    return 12;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultBinding$DefaultJSONBinding.class */
    public static final class DefaultJSONBinding<U> extends InternalBinding<JSON, U> {
        DefaultJSONBinding(DataType<JSON> dataType, Converter<JSON, U> converter) {
            super(dataType, converter);
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final void setNull0(BindingSetStatementContext<U> ctx) throws SQLException {
            super.setNull0(ctx);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public void sqlInline0(BindingSQLContext<U> ctx, JSON value) throws SQLException {
            super.sqlInline0(ctx, (BindingSQLContext<U>) value);
            if (ctx.family() == SQLDialect.H2 && value != null) {
                ctx.render().sql(' ').visit(Keywords.K_FORMAT).sql(' ').visit(Keywords.K_JSON);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public void sqlBind0(BindingSQLContext<U> ctx, JSON value) throws SQLException {
            super.sqlBind0(ctx, (BindingSQLContext<U>) value);
            if (ctx.family() == SQLDialect.H2 && value != null) {
                ctx.render().sql(' ').visit(Keywords.K_FORMAT).sql(' ').visit(Keywords.K_JSON);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetStatementContext<U> ctx, JSON value) throws SQLException {
            ctx.statement().setString(ctx.index(), value.data());
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetSQLOutputContext<U> ctx, JSON value) throws SQLException {
            ctx.output().writeString(value.data());
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final JSON get0(BindingGetResultSetContext<U> ctx) throws SQLException {
            String string = patchSnowflakeJSON(ctx, ctx.resultSet().getString(ctx.index()));
            if (string == null) {
                return null;
            }
            return JSON.valueOf(string);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final JSON get0(BindingGetStatementContext<U> ctx) throws SQLException {
            String string = patchSnowflakeJSON(ctx, ctx.statement().getString(ctx.index()));
            if (string == null) {
                return null;
            }
            return JSON.valueOf(string);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final JSON get0(BindingGetSQLInputContext<U> ctx) throws SQLException {
            String string = patchSnowflakeJSON(ctx, ctx.input().readString());
            if (string == null) {
                return null;
            }
            return JSON.valueOf(string);
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final int sqltype(Statement statement, Configuration configuration) {
            return 12;
        }

        static final String patchSnowflakeJSON(Scope ctx, String json) {
            return json;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultBinding$DefaultJSONBBinding.class */
    public static final class DefaultJSONBBinding<U> extends InternalBinding<JSONB, U> {
        static final Set<SQLDialect> EMULATE_AS_BLOB = SQLDialect.supportedBy(SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.SQLITE);

        DefaultJSONBBinding(DataType<JSONB> dataType, Converter<JSONB, U> converter) {
            super(dataType, converter);
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final void setNull0(BindingSetStatementContext<U> ctx) throws SQLException {
            super.setNull0(ctx);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public void sqlInline0(BindingSQLContext<U> ctx, JSONB value) throws SQLException {
            if (EMULATE_AS_BLOB.contains(ctx.dialect())) {
                bytes(ctx.configuration()).sqlInline0((BindingSQLContext) ctx, bytesConverter(ctx.configuration()).to(value, ctx.converterContext()));
                return;
            }
            super.sqlInline1(ctx, value.data());
            if (ctx.family() == SQLDialect.H2) {
                ctx.render().sql(' ').visit(Keywords.K_FORMAT).sql(' ').visit(Keywords.K_JSON);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public void sqlBind0(BindingSQLContext<U> ctx, JSONB value) throws SQLException {
            super.sqlBind0(ctx, (BindingSQLContext<U>) value);
            if (ctx.family() == SQLDialect.H2 && value != null) {
                ctx.render().sql(' ').visit(Keywords.K_FORMAT).sql(' ').visit(Keywords.K_JSON);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetStatementContext<U> ctx, JSONB value) throws SQLException {
            if (EMULATE_AS_BLOB.contains(ctx.dialect())) {
                bytes(ctx.configuration()).set0((BindingSetStatementContext) ctx, bytesConverter(ctx.configuration()).to(value, ctx.converterContext()));
            } else {
                ctx.statement().setString(ctx.index(), value.data());
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetSQLOutputContext<U> ctx, JSONB value) throws SQLException {
            if (EMULATE_AS_BLOB.contains(ctx.dialect())) {
                bytes(ctx.configuration()).set0((BindingSetSQLOutputContext) ctx, bytesConverter(ctx.configuration()).to(value, ctx.converterContext()));
            } else {
                ctx.output().writeString(value.data());
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final JSONB get0(BindingGetResultSetContext<U> ctx) throws SQLException {
            if (EMULATE_AS_BLOB.contains(ctx.dialect())) {
                return bytesConverter(ctx.configuration()).from(bytes(ctx.configuration()).get0((BindingGetResultSetContext) ctx), ctx.converterContext());
            }
            String string = DefaultJSONBinding.patchSnowflakeJSON(ctx, ctx.resultSet().getString(ctx.index()));
            if (string == null) {
                return null;
            }
            return JSONB.valueOf(string);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final JSONB get0(BindingGetStatementContext<U> ctx) throws SQLException {
            if (EMULATE_AS_BLOB.contains(ctx.dialect())) {
                return bytesConverter(ctx.configuration()).from(bytes(ctx.configuration()).get0((BindingGetStatementContext) ctx), ctx.converterContext());
            }
            String string = DefaultJSONBinding.patchSnowflakeJSON(ctx, ctx.statement().getString(ctx.index()));
            if (string == null) {
                return null;
            }
            return JSONB.valueOf(string);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final JSONB get0(BindingGetSQLInputContext<U> ctx) throws SQLException {
            if (EMULATE_AS_BLOB.contains(ctx.dialect())) {
                return bytesConverter(ctx.configuration()).from(bytes(ctx.configuration()).get0((BindingGetSQLInputContext) ctx), ctx.converterContext());
            }
            String string = DefaultJSONBinding.patchSnowflakeJSON(ctx, ctx.input().readString());
            if (string == null) {
                return null;
            }
            return JSONB.valueOf(string);
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final int sqltype(Statement statement, Configuration configuration) {
            if (EMULATE_AS_BLOB.contains(configuration.dialect())) {
                return bytes(configuration).sqltype(statement, configuration);
            }
            return 12;
        }

        private final ContextConverter<byte[], JSONB> bytesConverter(Configuration configuration) {
            return ContextConverter.ofNullable(byte[].class, JSONB.class, (t, x) -> {
                return JSONB.valueOf(new String(t, configuration.charsetProvider().provide()));
            }, (u, x2) -> {
                return u.toString().getBytes(configuration.charsetProvider().provide());
            });
        }

        private final DefaultBytesBinding<U> bytes(Configuration configuration) {
            return new DefaultBytesBinding<>(SQLDataType.BLOB, bytesConverter(configuration));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultBinding$DefaultXMLBinding.class */
    public static final class DefaultXMLBinding<U> extends InternalBinding<XML, U> {
        DefaultXMLBinding(DataType<XML> dataType, Converter<XML, U> converter) {
            super(dataType, converter);
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final void setNull0(BindingSetStatementContext<U> ctx) throws SQLException {
            super.setNull0(ctx);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetStatementContext<U> ctx, XML value) throws SQLException {
            ctx.statement().setString(ctx.index(), value.toString());
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetSQLOutputContext<U> ctx, XML value) throws SQLException {
            ctx.output().writeString(value.toString());
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final XML get0(BindingGetResultSetContext<U> ctx) throws SQLException {
            String string = ctx.resultSet().getString(ctx.index());
            if (string == null) {
                return null;
            }
            return XML.valueOf(string);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final XML get0(BindingGetStatementContext<U> ctx) throws SQLException {
            String string = ctx.statement().getString(ctx.index());
            if (string == null) {
                return null;
            }
            return XML.valueOf(string);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final XML get0(BindingGetSQLInputContext<U> ctx) throws SQLException {
            String string = ctx.input().readString();
            if (string == null) {
                return null;
            }
            return XML.valueOf(string);
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final int sqltype(Statement statement, Configuration configuration) {
            return 12;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultBinding$DefaultYearToSecondBinding.class */
    public static final class DefaultYearToSecondBinding<U> extends InternalBinding<YearToSecond, U> {
        private static final Set<SQLDialect> REQUIRE_PG_INTERVAL = SQLDialect.supportedBy(SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB);

        DefaultYearToSecondBinding(DataType<YearToSecond> dataType, Converter<YearToSecond, U> converter) {
            super(dataType, converter);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void sqlInline0(BindingSQLContext<U> ctx, YearToSecond value) throws SQLException {
            if (REQUIRE_PG_INTERVAL.contains(ctx.dialect())) {
                ctx.render().visit((Field<?>) DSL.inline(PostgresUtils.toPGInterval(value).toString()));
            } else {
                super.sqlInline0(ctx, (BindingSQLContext<U>) value);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetStatementContext<U> ctx, YearToSecond value) throws SQLException {
            if (REQUIRE_PG_INTERVAL.contains(ctx.dialect())) {
                ctx.statement().setString(ctx.index(), PostgresUtils.toPGInterval(value).toString());
            } else {
                ctx.statement().setString(ctx.index(), value.toString());
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetSQLOutputContext<U> ctx, YearToSecond value) throws SQLException {
            ctx.output().writeString(value.toString());
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final YearToSecond get0(BindingGetResultSetContext<U> ctx) throws SQLException {
            if (REQUIRE_PG_INTERVAL.contains(ctx.dialect())) {
                Object object = ctx.resultSet().getObject(ctx.index());
                if (object == null) {
                    return null;
                }
                return PostgresUtils.toYearToSecond(object);
            }
            String string = ctx.resultSet().getString(ctx.index());
            if (string == null) {
                return null;
            }
            return YearToSecond.valueOf(string);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final YearToSecond get0(BindingGetStatementContext<U> ctx) throws SQLException {
            if (REQUIRE_PG_INTERVAL.contains(ctx.dialect())) {
                Object object = ctx.statement().getObject(ctx.index());
                if (object == null) {
                    return null;
                }
                return PostgresUtils.toYearToSecond(object);
            }
            String string = ctx.statement().getString(ctx.index());
            if (string == null) {
                return null;
            }
            return YearToSecond.valueOf(string);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final YearToSecond get0(BindingGetSQLInputContext<U> ctx) throws SQLException {
            String string = ctx.input().readString();
            if (string == null) {
                return null;
            }
            return YearToSecond.valueOf(string);
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final int sqltype(Statement statement, Configuration configuration) {
            return 12;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultBinding$DefaultYearBinding.class */
    public static final class DefaultYearBinding<U> extends InternalBinding<Year, U> {
        DefaultYearBinding(DataType<Year> dataType, Converter<Year, U> converter) {
            super(dataType, converter);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void sqlInline0(BindingSQLContext<U> ctx, Year value) {
            ctx.render().sql(value.getValue());
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetStatementContext<U> ctx, Year value) throws SQLException {
            ctx.statement().setInt(ctx.index(), value.getValue());
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetSQLOutputContext<U> ctx, Year value) throws SQLException {
            ctx.output().writeInt(value.getValue());
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final Year get0(BindingGetResultSetContext<U> ctx) throws SQLException {
            return (Year) JDBCUtils.wasNull(ctx.resultSet(), Year.of(ctx.resultSet().getInt(ctx.index())));
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final Year get0(BindingGetStatementContext<U> ctx) throws SQLException {
            return (Year) JDBCUtils.wasNull(ctx.statement(), Year.of(ctx.statement().getInt(ctx.index())));
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final Year get0(BindingGetSQLInputContext<U> ctx) throws SQLException {
            return (Year) JDBCUtils.wasNull(ctx.input(), Year.of(ctx.input().readInt()));
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final int sqltype(Statement statement, Configuration configuration) {
            return 5;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultBinding$DefaultYearToMonthBinding.class */
    public static final class DefaultYearToMonthBinding<U> extends InternalBinding<YearToMonth, U> {
        private static final Set<SQLDialect> REQUIRE_PG_INTERVAL = SQLDialect.supportedBy(SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB);
        private static final Set<SQLDialect> REQUIRE_STANDARD_INTERVAL = SQLDialect.supportedBy(SQLDialect.H2, SQLDialect.TRINO);

        DefaultYearToMonthBinding(DataType<YearToMonth> dataType, Converter<YearToMonth, U> converter) {
            super(dataType, converter);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void sqlInline0(BindingSQLContext<U> ctx, YearToMonth value) throws SQLException {
            if (REQUIRE_PG_INTERVAL.contains(ctx.dialect())) {
                ctx.render().visit((Field<?>) DSL.inline(PostgresUtils.toPGInterval(value).toString()));
            } else if (ctx.family() == SQLDialect.TRINO) {
                ctx.render().sql(renderYTM(ctx, value));
            } else {
                super.sqlInline0(ctx, (BindingSQLContext<U>) value);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetStatementContext<U> ctx, YearToMonth value) throws SQLException {
            if (REQUIRE_PG_INTERVAL.contains(ctx.dialect())) {
                ctx.statement().setString(ctx.index(), PostgresUtils.toPGInterval(value).toString());
            } else {
                ctx.statement().setString(ctx.index(), renderYTM(ctx, value));
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final void set0(BindingSetSQLOutputContext<U> ctx, YearToMonth value) throws SQLException {
            ctx.output().writeString(renderYTM(ctx, value));
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final YearToMonth get0(BindingGetResultSetContext<U> ctx) throws SQLException {
            if (REQUIRE_PG_INTERVAL.contains(ctx.dialect())) {
                return PostgresUtils.toYearToMonth(ctx.resultSet().getString(ctx.index()));
            }
            return parseYTM(ctx, ctx.resultSet().getString(ctx.index()));
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final YearToMonth get0(BindingGetStatementContext<U> ctx) throws SQLException {
            if (REQUIRE_PG_INTERVAL.contains(ctx.dialect())) {
                return PostgresUtils.toYearToMonth(ctx.statement().getString(ctx.index()));
            }
            return parseYTM(ctx, ctx.statement().getString(ctx.index()));
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        public final YearToMonth get0(BindingGetSQLInputContext<U> ctx) throws SQLException {
            return parseYTM(ctx, ctx.input().readString());
        }

        private final YearToMonth parseYTM(Scope scope, String string) {
            if (string == null) {
                return null;
            }
            if (REQUIRE_STANDARD_INTERVAL.contains(scope.dialect()) && string.startsWith("INTERVAL")) {
                return (YearToMonth) ((Param) scope.dsl().parser().parseField(string)).getValue();
            }
            return YearToMonth.valueOf(string);
        }

        private final String renderYTM(Scope scope, YearToMonth ytm) {
            return renderYTM(scope, ytm, (v0) -> {
                return v0.toString();
            });
        }

        private final String renderYTM(Scope scope, YearToMonth ytm, java.util.function.Function<? super YearToMonth, ? extends String> toString) {
            if (ytm == null) {
                return null;
            }
            if (REQUIRE_STANDARD_INTERVAL.contains(scope.dialect())) {
                return "INTERVAL '" + toString.apply(ytm) + "' YEAR TO MONTH";
            }
            return toString.apply(ytm);
        }

        @Override // org.jooq.impl.DefaultBinding.InternalBinding
        final int sqltype(Statement statement, Configuration configuration) {
            return 12;
        }
    }
}

package org.jooq.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.util.UUID;
import org.jooq.Configuration;
import org.jooq.DataType;
import org.jooq.Geography;
import org.jooq.Geometry;
import org.jooq.JSON;
import org.jooq.JSONB;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.RowId;
import org.jooq.SQLDialect;
import org.jooq.XML;
import org.jooq.impl.SQLDataTypes;
import org.jooq.types.DayToSecond;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.jooq.types.ULong;
import org.jooq.types.UShort;
import org.jooq.types.YearToMonth;
import org.jooq.types.YearToSecond;
import org.jooq.util.cubrid.CUBRIDDataType;
import org.jooq.util.derby.DerbyDataType;
import org.jooq.util.firebird.FirebirdDataType;
import org.jooq.util.h2.H2DataType;
import org.jooq.util.hsqldb.HSQLDBDataType;
import org.jooq.util.ignite.IgniteDataType;
import org.jooq.util.mariadb.MariaDBDataType;
import org.jooq.util.mysql.MySQLDataType;
import org.jooq.util.postgres.PostgresDataType;
import org.jooq.util.sqlite.SQLiteDataType;
import org.jooq.util.yugabytedb.YugabyteDBDataType;
import org.springframework.cache.interceptor.CacheOperationExpressionEvaluator;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/SQLDataType.class */
public final class SQLDataType {
    public static final DataType<String> VARCHAR = new BuiltInDataType(String.class, "varchar(l)");
    public static final DataType<String> CHAR = new BuiltInDataType(String.class, "char(l)");
    public static final DataType<String> LONGVARCHAR = new BuiltInDataType(String.class, "longvarchar(l)");
    public static final DataType<String> CLOB = new BuiltInDataType(String.class, "clob");
    public static final DataType<String> NVARCHAR = new BuiltInDataType(String.class, "nvarchar(l)");
    public static final DataType<String> NCHAR = new BuiltInDataType(String.class, "nchar(l)");
    public static final DataType<String> LONGNVARCHAR = new BuiltInDataType(String.class, "longnvarchar(l)");
    public static final DataType<String> NCLOB = new BuiltInDataType(String.class, "nclob");
    public static final DataType<Boolean> BOOLEAN = new BuiltInDataType(Boolean.class, "boolean");
    public static final DataType<Boolean> BIT = new BuiltInDataType(Boolean.class, "bit");
    public static final DataType<Byte> TINYINT = new BuiltInDataType(Byte.class, "tinyint");
    public static final DataType<Short> SMALLINT = new BuiltInDataType(Short.class, "smallint");
    public static final DataType<Integer> INTEGER = new BuiltInDataType(Integer.class, "integer");
    public static final DataType<Long> BIGINT = new BuiltInDataType(Long.class, "bigint");
    public static final DataType<BigInteger> DECIMAL_INTEGER = new BuiltInDataType(BigInteger.class, "decimal_integer");
    public static final DataType<UByte> TINYINTUNSIGNED = new BuiltInDataType(UByte.class, "tinyint unsigned");
    public static final DataType<UShort> SMALLINTUNSIGNED = new BuiltInDataType(UShort.class, "smallint unsigned");
    public static final DataType<UInteger> INTEGERUNSIGNED = new BuiltInDataType(UInteger.class, "integer unsigned");
    public static final DataType<ULong> BIGINTUNSIGNED = new BuiltInDataType(ULong.class, "bigint unsigned");
    public static final DataType<Double> DOUBLE = new BuiltInDataType(Double.class, "double");
    public static final DataType<Double> FLOAT = new BuiltInDataType(Double.class, "float");
    public static final DataType<Float> REAL = new BuiltInDataType(Float.class, "real");
    public static final DataType<BigDecimal> NUMERIC = new BuiltInDataType(BigDecimal.class, "numeric(p, s)");
    public static final DataType<BigDecimal> DECIMAL = new BuiltInDataType(BigDecimal.class, "decimal(p, s)");
    public static final DataType<Date> DATE = new BuiltInDataType(Date.class, "date");
    public static final DataType<Timestamp> TIMESTAMP = new BuiltInDataType(Timestamp.class, "timestamp(p)");
    public static final DataType<Time> TIME = new BuiltInDataType(Time.class, "time(p)");
    public static final DataType<YearToSecond> INTERVAL = new BuiltInDataType(YearToSecond.class, "interval");
    public static final DataType<YearToMonth> INTERVALYEARTOMONTH = new BuiltInDataType(YearToMonth.class, "interval year to month");
    public static final DataType<DayToSecond> INTERVALDAYTOSECOND = new BuiltInDataType(DayToSecond.class, "interval day to second");
    public static final DataType<LocalDate> LOCALDATE = new BuiltInDataType(LocalDate.class, "date");
    public static final DataType<LocalTime> LOCALTIME = new BuiltInDataType(LocalTime.class, "time(p)");
    public static final DataType<LocalDateTime> LOCALDATETIME = new BuiltInDataType(LocalDateTime.class, "timestamp(p)");
    public static final DataType<OffsetTime> OFFSETTIME = new BuiltInDataType(OffsetTime.class, "time(p) with time zone");
    public static final DataType<OffsetDateTime> OFFSETDATETIME = new BuiltInDataType(OffsetDateTime.class, "timestamp(p) with time zone");
    public static final DataType<OffsetTime> TIMEWITHTIMEZONE = OFFSETTIME;
    public static final DataType<OffsetDateTime> TIMESTAMPWITHTIMEZONE = OFFSETDATETIME;
    public static final DataType<Instant> INSTANT = new BuiltInDataType(Instant.class, "instant");
    public static final DataType<Year> YEAR = new BuiltInDataType(Year.class, "year");
    public static final DataType<byte[]> BINARY = new BuiltInDataType(byte[].class, "binary(l)");
    public static final DataType<byte[]> VARBINARY = new BuiltInDataType(byte[].class, "varbinary(l)");
    public static final DataType<byte[]> LONGVARBINARY = new BuiltInDataType(byte[].class, "longvarbinary(l)");
    public static final DataType<byte[]> BLOB = new BuiltInDataType(byte[].class, "blob");
    public static final DataType<Object> OTHER = new BuiltInDataType(Object.class, "other");
    public static final DataType<RowId> ROWID = new BuiltInDataType(RowId.class, "rowid");
    public static final DataType<Record> RECORD = new BuiltInDataType(Record.class, "record");
    public static final DataType<Result<Record>> RESULT = new BuiltInDataType(Result.class, CacheOperationExpressionEvaluator.RESULT_VARIABLE);
    public static final DataType<UUID> UUID = new BuiltInDataType(UUID.class, "uuid");
    public static final DataType<JSON> JSON = new BuiltInDataType(JSON.class, "json");
    public static final DataType<JSONB> JSONB = new BuiltInDataType(JSONB.class, "jsonb");
    public static final DataType<XML> XML = new BuiltInDataType(XML.class, "xml");
    public static final DataType<Geography> GEOGRAPHY = new BuiltInDataType(Geography.class, "geography");
    public static final DataType<Geometry> GEOMETRY = new BuiltInDataType(Geometry.class, "geometry");

    static {
        try {
            Class.forName(CUBRIDDataType.class.getName());
            initJSR310Types(SQLDialect.CUBRID);
            Class.forName(DerbyDataType.class.getName());
            initJSR310Types(SQLDialect.DERBY);
            Class.forName(SQLDataTypes.DuckDBDataType.class.getName());
            initJSR310Types(SQLDialect.DUCKDB);
            Class.forName(FirebirdDataType.class.getName());
            initJSR310Types(SQLDialect.FIREBIRD);
            Class.forName(H2DataType.class.getName());
            initJSR310Types(SQLDialect.H2);
            Class.forName(HSQLDBDataType.class.getName());
            initJSR310Types(SQLDialect.HSQLDB);
            Class.forName(IgniteDataType.class.getName());
            initJSR310Types(SQLDialect.IGNITE);
            Class.forName(MariaDBDataType.class.getName());
            initJSR310Types(SQLDialect.MARIADB);
            Class.forName(MySQLDataType.class.getName());
            initJSR310Types(SQLDialect.MYSQL);
            Class.forName(PostgresDataType.class.getName());
            initJSR310Types(SQLDialect.POSTGRES);
            Class.forName(SQLiteDataType.class.getName());
            initJSR310Types(SQLDialect.SQLITE);
            Class.forName(SQLDataTypes.TrinoDataType.class.getName());
            initJSR310Types(SQLDialect.TRINO);
            Class.forName(YugabyteDBDataType.class.getName());
            initJSR310Types(SQLDialect.YUGABYTEDB);
        } catch (Exception e) {
        }
    }

    public static final DataType<String> VARCHAR(int length) {
        return VARCHAR.length(length);
    }

    public static final DataType<String> CHAR(int length) {
        return CHAR.length(length);
    }

    public static final DataType<String> LONGVARCHAR(int length) {
        return LONGVARCHAR.length(length);
    }

    public static final DataType<String> CLOB(int length) {
        return CLOB.length(length);
    }

    public static final DataType<String> NVARCHAR(int length) {
        return NVARCHAR.length(length);
    }

    public static final DataType<String> NCHAR(int length) {
        return NCHAR.length(length);
    }

    public static final DataType<String> LONGNVARCHAR(int length) {
        return LONGNVARCHAR.length(length);
    }

    public static final DataType<String> NCLOB(int length) {
        return NCLOB.length(length);
    }

    public static final DataType<BigInteger> DECIMAL_INTEGER(int precision) {
        return DECIMAL_INTEGER.precision(precision, 0);
    }

    public static final DataType<BigDecimal> NUMERIC(int precision) {
        return NUMERIC.precision(precision, 0);
    }

    public static final DataType<BigDecimal> NUMERIC(int precision, int scale) {
        return NUMERIC.precision(precision, scale);
    }

    public static final DataType<BigDecimal> DECIMAL(int precision) {
        return DECIMAL.precision(precision, 0);
    }

    public static final DataType<BigDecimal> DECIMAL(int precision, int scale) {
        return DECIMAL.precision(precision, scale);
    }

    public static final DataType<Timestamp> TIMESTAMP(int precision) {
        return TIMESTAMP.precision(precision);
    }

    public static final DataType<Time> TIME(int precision) {
        return TIME.precision(precision);
    }

    public static final DataType<LocalTime> LOCALTIME(int precision) {
        return LOCALTIME.precision(precision);
    }

    public static final DataType<LocalDateTime> LOCALDATETIME(int precision) {
        return LOCALDATETIME.precision(precision);
    }

    public static final DataType<OffsetTime> OFFSETTIME(int precision) {
        return OFFSETTIME.precision(precision);
    }

    public static final DataType<OffsetDateTime> OFFSETDATETIME(int precision) {
        return OFFSETDATETIME.precision(precision);
    }

    public static final DataType<OffsetTime> TIMEWITHTIMEZONE(int precision) {
        return TIMEWITHTIMEZONE.precision(precision);
    }

    public static final DataType<OffsetDateTime> TIMESTAMPWITHTIMEZONE(int precision) {
        return TIMESTAMPWITHTIMEZONE.precision(precision);
    }

    public static final DataType<Instant> INSTANT(int precision) {
        return INSTANT.precision(precision);
    }

    public static final DataType<byte[]> BINARY(int length) {
        return BINARY.length(length);
    }

    public static final DataType<byte[]> VARBINARY(int length) {
        return VARBINARY.length(length);
    }

    public static final DataType<byte[]> LONGVARBINARY(int length) {
        return LONGVARBINARY.length(length);
    }

    public static final DataType<byte[]> BLOB(int length) {
        return BLOB.length(length);
    }

    private static final void initJSR310Types(SQLDialect family) {
        Configuration configuration = new DefaultConfiguration(family);
        new BuiltInDataType(family, LOCALDATE, DATE.getTypeName(configuration), DATE.getCastTypeName(configuration));
        new BuiltInDataType(family, LOCALTIME, TIME.getTypeName(configuration), TIME.getCastTypeName(configuration));
        new BuiltInDataType(family, LOCALDATETIME, TIMESTAMP.getTypeName(configuration), TIMESTAMP.getCastTypeName(configuration));
    }

    private SQLDataType() {
    }
}

package org.jooq.util.ignite;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Year;
import java.util.UUID;
import org.jooq.DataType;
import org.jooq.SQLDialect;
import org.jooq.impl.BuiltInDataType;
import org.jooq.impl.SQLDataType;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.jooq.types.ULong;
import org.jooq.types.UShort;

@Deprecated(forRemoval = true, since = "3.11")
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/util/ignite/IgniteDataType.class */
public class IgniteDataType {
    private static final SQLDialect FAMILY = SQLDialect.IGNITE;
    public static final DataType<Byte> TINYINT = new BuiltInDataType(FAMILY, SQLDataType.TINYINT, "tinyint");
    public static final DataType<Short> SMALLINT = new BuiltInDataType(FAMILY, SQLDataType.SMALLINT, "smallint");
    public static final DataType<Integer> INT = new BuiltInDataType(FAMILY, SQLDataType.INTEGER, "int");
    public static final DataType<Integer> INTEGER = new BuiltInDataType(FAMILY, SQLDataType.INTEGER, "integer");
    public static final DataType<Boolean> BOOLEAN = new BuiltInDataType(FAMILY, SQLDataType.BOOLEAN, "boolean");
    public static final DataType<Long> BIGINT = new BuiltInDataType(FAMILY, SQLDataType.BIGINT, "bigint");
    public static final DataType<BigDecimal> DECIMAL = new BuiltInDataType(FAMILY, SQLDataType.DECIMAL, "decimal(p, s)");
    public static final DataType<BigDecimal> NUMERIC = new BuiltInDataType(FAMILY, SQLDataType.NUMERIC, "decimal(p, s)");
    public static final DataType<Double> DOUBLE = new BuiltInDataType(FAMILY, SQLDataType.DOUBLE, "double");
    public static final DataType<Double> FLOAT = new BuiltInDataType(FAMILY, SQLDataType.FLOAT, "double");
    public static final DataType<Float> REAL = new BuiltInDataType(FAMILY, SQLDataType.REAL, "real");
    public static final DataType<Time> TIME = new BuiltInDataType(FAMILY, SQLDataType.TIME, "time(p)");
    public static final DataType<Date> DATE = new BuiltInDataType(FAMILY, SQLDataType.DATE, "date");
    public static final DataType<Timestamp> TIMESTAMP = new BuiltInDataType(FAMILY, SQLDataType.TIMESTAMP, "timestamp(p)");
    public static final DataType<byte[]> BINARY = new BuiltInDataType(FAMILY, SQLDataType.BINARY, "binary(l)");
    public static final DataType<byte[]> VARBINARY = new BuiltInDataType(FAMILY, SQLDataType.VARBINARY, "binary(l)");
    public static final DataType<byte[]> LONGVARBINARY = new BuiltInDataType(FAMILY, SQLDataType.LONGVARBINARY, "binary(l)");
    public static final DataType<byte[]> BLOB = new BuiltInDataType(FAMILY, SQLDataType.BLOB, "binary");
    public static final DataType<Object> OTHER = new BuiltInDataType(FAMILY, SQLDataType.OTHER, "other");
    public static final DataType<String> VARCHAR = new BuiltInDataType(FAMILY, SQLDataType.VARCHAR, "varchar(l)");
    public static final DataType<String> CHAR = new BuiltInDataType(FAMILY, SQLDataType.CHAR, "char(l)");
    public static final DataType<String> CLOB = new BuiltInDataType(FAMILY, SQLDataType.CLOB, "varchar");
    protected static final DataType<BigInteger> __BIGINTEGER = new BuiltInDataType(FAMILY, SQLDataType.DECIMAL_INTEGER, "decimal(p, s)");
    protected static final DataType<UByte> __TINYINTUNSIGNED = new BuiltInDataType(FAMILY, SQLDataType.TINYINTUNSIGNED, "smallint");
    protected static final DataType<UShort> __SMALLINTUNSIGNED = new BuiltInDataType(FAMILY, SQLDataType.SMALLINTUNSIGNED, "int");
    protected static final DataType<UInteger> __INTEGERUNSIGNED = new BuiltInDataType(FAMILY, SQLDataType.INTEGERUNSIGNED, "bigint");
    protected static final DataType<ULong> __BIGINTUNSIGNED = new BuiltInDataType(FAMILY, SQLDataType.BIGINTUNSIGNED, "number(p, s)");
    protected static final DataType<Year> __YEAR = new BuiltInDataType(FAMILY, SQLDataType.YEAR, "smallint");
    public static final DataType<UUID> UUID = new BuiltInDataType(FAMILY, SQLDataType.UUID, "uuid");
}

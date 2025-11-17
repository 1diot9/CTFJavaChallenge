package org.h2.value;

import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.joran.JoranConstants;
import com.alibaba.com.caucho.hessian.io.Hessian2Constants;
import com.fasterxml.jackson.core.JsonFactory;
import java.sql.JDBCType;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLType;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.h2.api.ErrorCode;
import org.h2.api.H2Type;
import org.h2.api.IntervalQualifier;
import org.h2.engine.Mode;
import org.h2.message.DbException;
import org.h2.store.LobStorageFrontend;
import org.h2.util.DateTimeUtils;
import org.h2.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/DataType.class */
public class DataType {
    private static final HashMap<String, DataType> TYPES_BY_NAME = new HashMap<>(128);
    static final DataType[] TYPES_BY_VALUE_TYPE = new DataType[42];
    public int type;
    public int sqlType;
    public long minPrecision;
    public long maxPrecision;
    public int minScale;
    public int maxScale;
    public String prefix;
    public String suffix;
    public String params;
    public boolean caseSensitive;
    public boolean supportsPrecision;
    public boolean supportsScale;
    public long defaultPrecision;
    public int defaultScale;
    public boolean specialPrecisionScale;

    /* JADX WARN: Type inference failed for: r0v3, types: [org.h2.value.DataType, long] */
    /* JADX WARN: Type inference failed for: r1v51, types: [org.h2.value.DataType, long] */
    static {
        ?? dataType = new DataType();
        dataType.minPrecision = 1L;
        dataType.maxPrecision = 1L;
        1.defaultPrecision = dataType;
        add(0, 0, dataType, JoranConstants.NULL);
        add(1, 1, createString(true, true), "CHARACTER", "CHAR", "NCHAR", "NATIONAL CHARACTER", "NATIONAL CHAR");
        add(2, 12, createString(true, false), "CHARACTER VARYING", "VARCHAR", "CHAR VARYING", "NCHAR VARYING", "NATIONAL CHARACTER VARYING", "NATIONAL CHAR VARYING", "VARCHAR2", "NVARCHAR", "NVARCHAR2", "VARCHAR_CASESENSITIVE", "TID", "LONGVARCHAR", "LONGNVARCHAR", "TINYTEXT", "TEXT", "MEDIUMTEXT", "LONGTEXT", "NTEXT");
        add(3, 2005, createLob(true), "CHARACTER LARGE OBJECT", "CLOB", "CHAR LARGE OBJECT", "NCLOB", "NCHAR LARGE OBJECT", "NATIONAL CHARACTER LARGE OBJECT");
        add(4, 12, createString(false, false), "VARCHAR_IGNORECASE");
        add(5, -2, createBinary(true), "BINARY");
        add(6, -3, createBinary(false), "BINARY VARYING", "VARBINARY", "RAW", "BYTEA", "LONG RAW", "LONGVARBINARY");
        add(7, 2004, createLob(false), "BINARY LARGE OBJECT", "BLOB", "TINYBLOB", "MEDIUMBLOB", "LONGBLOB", "IMAGE");
        add(8, 16, createNumeric(1, 0), "BOOLEAN", "BIT", "BOOL");
        add(9, -6, createNumeric(8, 0), "TINYINT");
        add(10, 5, createNumeric(16, 0), "SMALLINT", "INT2");
        add(11, 4, createNumeric(32, 0), "INTEGER", "INT", "MEDIUMINT", "INT4", "SIGNED");
        add(12, -5, createNumeric(64, 0), "BIGINT", "INT8", "LONG");
        DataType dataType2 = new DataType();
        dataType2.minPrecision = 1L;
        dataType2.maxPrecision = 100000L;
        dataType2.defaultPrecision = 100000L;
        dataType2.defaultScale = 0;
        dataType2.maxScale = 100000;
        dataType2.minScale = 0;
        dataType2.params = "PRECISION,SCALE";
        dataType2.supportsPrecision = true;
        dataType2.supportsScale = true;
        add(13, 2, dataType2, "NUMERIC", "DECIMAL", "DEC");
        add(14, 7, createNumeric(24, 0), "REAL", "FLOAT4");
        add(15, 8, createNumeric(53, 0), "DOUBLE PRECISION", "DOUBLE", "FLOAT8");
        add(15, 6, createNumeric(53, 0), "FLOAT");
        DataType dataType3 = new DataType();
        dataType3.minPrecision = 1L;
        dataType3.maxPrecision = 100000L;
        dataType3.defaultPrecision = 100000L;
        dataType3.params = "PRECISION";
        dataType3.supportsPrecision = true;
        add(16, 2, dataType3, "DECFLOAT");
        add(17, 91, createDate(10, 10, "DATE", false, 0, 0), "DATE");
        add(18, 92, createDate(18, 8, "TIME", true, 0, 9), "TIME", "TIME WITHOUT TIME ZONE");
        add(19, 2013, createDate(24, 14, "TIME WITH TIME ZONE", true, 0, 9), "TIME WITH TIME ZONE");
        add(20, 93, createDate(29, 26, "TIMESTAMP", true, 6, 9), "TIMESTAMP", "TIMESTAMP WITHOUT TIME ZONE", "DATETIME", "DATETIME2", "SMALLDATETIME");
        add(21, 2014, createDate(35, 32, "TIMESTAMP WITH TIME ZONE", true, 6, 9), "TIMESTAMP WITH TIME ZONE");
        for (int i = 22; i <= 34; i++) {
            addInterval(i);
        }
        add(35, 2000, createBinary(false), "JAVA_OBJECT", "OBJECT", "OTHER");
        DataType createString = createString(false, false);
        createString.supportsPrecision = false;
        createString.params = "ELEMENT [,...]";
        add(36, 1111, createString, "ENUM");
        add(37, 1111, createGeometry(), "GEOMETRY");
        add(38, 1111, createString(true, false, "JSON '", "'"), JsonFactory.FORMAT_NAME_JSON);
        ?? dataType4 = new DataType();
        dataType4.suffix = "'";
        dataType4.prefix = "'";
        dataType4.minPrecision = 16L;
        dataType4.maxPrecision = 16L;
        16.defaultPrecision = dataType4;
        add(39, -2, dataType4, "UUID");
        DataType dataType5 = new DataType();
        dataType5.prefix = "ARRAY[";
        dataType5.suffix = "]";
        dataType5.params = "CARDINALITY";
        dataType5.supportsPrecision = true;
        dataType5.maxPrecision = 65536L;
        dataType5.defaultPrecision = 65536L;
        add(40, 2003, dataType5, "ARRAY");
        DataType dataType6 = new DataType();
        dataType6.prefix = "ROW(";
        dataType6.suffix = ")";
        dataType6.params = "NAME DATA_TYPE [,...]";
        add(41, 1111, dataType6, "ROW");
    }

    private static void addInterval(int i) {
        IntervalQualifier valueOf = IntervalQualifier.valueOf(i - 22);
        String intervalQualifier = valueOf.toString();
        DataType dataType = new DataType();
        dataType.prefix = "INTERVAL '";
        dataType.suffix = "' " + intervalQualifier;
        dataType.supportsPrecision = true;
        dataType.defaultPrecision = 2L;
        dataType.minPrecision = 1L;
        dataType.maxPrecision = 18L;
        if (valueOf.hasSeconds()) {
            dataType.supportsScale = true;
            dataType.defaultScale = 6;
            dataType.maxScale = 9;
            dataType.params = "PRECISION,SCALE";
        } else {
            dataType.params = "PRECISION";
        }
        add(i, 1111, dataType, ("INTERVAL " + intervalQualifier).intern());
    }

    private static void add(int i, int i2, DataType dataType, String... strArr) {
        dataType.type = i;
        dataType.sqlType = i2;
        if (TYPES_BY_VALUE_TYPE[i] == null) {
            TYPES_BY_VALUE_TYPE[i] = dataType;
        }
        for (String str : strArr) {
            TYPES_BY_NAME.put(str, dataType);
        }
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [org.h2.value.DataType, long] */
    /* JADX WARN: Type inference failed for: r3v1, types: [org.h2.value.DataType, long] */
    public static DataType createNumeric(int i, int i2) {
        ?? dataType = new DataType();
        ?? r3 = i;
        dataType.minPrecision = r3;
        dataType.maxPrecision = r3;
        r3.defaultPrecision = dataType;
        dataType.minScale = i2;
        dataType.maxScale = i2;
        dataType.defaultScale = i2;
        return dataType;
    }

    public static DataType createDate(int i, int i2, String str, boolean z, int i3, int i4) {
        DataType dataType = new DataType();
        dataType.prefix = str + " '";
        dataType.suffix = "'";
        dataType.maxPrecision = i;
        long j = i2;
        dataType.minPrecision = j;
        dataType.defaultPrecision = j;
        if (z) {
            dataType.params = "SCALE";
            dataType.supportsScale = true;
            dataType.maxScale = i4;
            dataType.defaultScale = i3;
        }
        return dataType;
    }

    private static DataType createString(boolean z, boolean z2) {
        return createString(z, z2, "'", "'");
    }

    private static DataType createBinary(boolean z) {
        return createString(false, z, "X'", "'");
    }

    private static DataType createString(boolean z, boolean z2, String str, String str2) {
        DataType dataType = new DataType();
        dataType.prefix = str;
        dataType.suffix = str2;
        dataType.params = "LENGTH";
        dataType.caseSensitive = z;
        dataType.supportsPrecision = true;
        dataType.minPrecision = 1L;
        dataType.maxPrecision = DateTimeUtils.NANOS_PER_SECOND;
        dataType.defaultPrecision = z2 ? 1L : DateTimeUtils.NANOS_PER_SECOND;
        return dataType;
    }

    private static DataType createLob(boolean z) {
        DataType createString = z ? createString(true, false) : createBinary(false);
        createString.maxPrecision = Long.MAX_VALUE;
        createString.defaultPrecision = Long.MAX_VALUE;
        return createString;
    }

    private static DataType createGeometry() {
        DataType dataType = new DataType();
        dataType.prefix = "'";
        dataType.suffix = "'";
        dataType.params = "TYPE,SRID";
        dataType.maxPrecision = Long.MAX_VALUE;
        dataType.defaultPrecision = Long.MAX_VALUE;
        return dataType;
    }

    public static DataType getDataType(int i) {
        if (i == -1) {
            throw DbException.get(ErrorCode.UNKNOWN_DATA_TYPE_1, CoreConstants.NA);
        }
        if (i >= 0 && i < 42) {
            return TYPES_BY_VALUE_TYPE[i];
        }
        return TYPES_BY_VALUE_TYPE[0];
    }

    public static int convertTypeToSQLType(TypeInfo typeInfo) {
        int valueType = typeInfo.getValueType();
        switch (valueType) {
            case 13:
                return typeInfo.getExtTypeInfo() != null ? 3 : 2;
            case 14:
            case 15:
                if (typeInfo.getDeclaredPrecision() >= 0) {
                    return 6;
                }
                break;
        }
        return getDataType(valueType).sqlType;
    }

    public static int convertSQLTypeToValueType(int i, String str) {
        switch (i) {
            case -2:
                if (str.equalsIgnoreCase("UUID")) {
                    return 39;
                }
                break;
            case 1111:
                DataType dataType = TYPES_BY_NAME.get(StringUtils.toUpperEnglish(str));
                if (dataType != null) {
                    return dataType.type;
                }
                break;
        }
        return convertSQLTypeToValueType(i);
    }

    public static int getValueTypeFromResultSet(ResultSetMetaData resultSetMetaData, int i) throws SQLException {
        return convertSQLTypeToValueType(resultSetMetaData.getColumnType(i), resultSetMetaData.getColumnTypeName(i));
    }

    public static boolean isBinaryColumn(ResultSetMetaData resultSetMetaData, int i) throws SQLException {
        switch (resultSetMetaData.getColumnType(i)) {
            case -4:
            case LobStorageFrontend.TABLE_RESULT /* -3 */:
            case 2000:
            case 2004:
                return true;
            case -2:
                if (!resultSetMetaData.getColumnTypeName(i).equals("UUID")) {
                    return true;
                }
                return false;
            default:
                return false;
        }
    }

    public static int convertSQLTypeToValueType(SQLType sQLType) {
        if (sQLType instanceof H2Type) {
            return sQLType.getVendorTypeNumber().intValue();
        }
        if (sQLType instanceof JDBCType) {
            return convertSQLTypeToValueType(sQLType.getVendorTypeNumber().intValue());
        }
        throw DbException.get(ErrorCode.UNKNOWN_DATA_TYPE_1, sQLType == null ? "<null>" : unknownSqlTypeToString(new StringBuilder(), sQLType).toString());
    }

    public static int convertSQLTypeToValueType(int i) {
        switch (i) {
            case Hessian2Constants.INT_DIRECT_MIN /* -16 */:
            case -9:
            case -1:
            case 12:
                return 2;
            case -15:
            case 1:
                return 1;
            case -7:
            case 16:
                return 8;
            case -6:
                return 9;
            case -5:
                return 12;
            case -4:
            case LobStorageFrontend.TABLE_RESULT /* -3 */:
                return 6;
            case -2:
                return 5;
            case 0:
                return 0;
            case 2:
            case 3:
                return 13;
            case 4:
                return 11;
            case 5:
                return 10;
            case 6:
            case 8:
                return 15;
            case 7:
                return 14;
            case 91:
                return 17;
            case 92:
                return 18;
            case 93:
                return 20;
            case 1111:
                return -1;
            case 2000:
                return 35;
            case 2003:
                return 40;
            case 2004:
                return 7;
            case 2005:
            case 2011:
                return 3;
            case 2013:
                return 19;
            case 2014:
                return 21;
            default:
                throw DbException.get(ErrorCode.UNKNOWN_DATA_TYPE_1, Integer.toString(i));
        }
    }

    public static String sqlTypeToString(SQLType sQLType) {
        if (sQLType == null) {
            return "null";
        }
        if (sQLType instanceof JDBCType) {
            return "JDBCType." + sQLType.getName();
        }
        if (sQLType instanceof H2Type) {
            return sQLType.toString();
        }
        return unknownSqlTypeToString(new StringBuilder("/* "), sQLType).append(" */ null").toString();
    }

    private static StringBuilder unknownSqlTypeToString(StringBuilder sb, SQLType sQLType) {
        return sb.append(StringUtils.quoteJavaString(sQLType.getVendor())).append('/').append(StringUtils.quoteJavaString(sQLType.getName())).append(" [").append(sQLType.getVendorTypeNumber()).append(']');
    }

    public static DataType getTypeByName(String str, Mode mode) {
        DataType dataType = mode.typeByNameMap.get(str);
        if (dataType == null) {
            dataType = TYPES_BY_NAME.get(str);
        }
        return dataType;
    }

    public static boolean isIndexable(TypeInfo typeInfo) {
        switch (typeInfo.getValueType()) {
            case -1:
            case 0:
            case 3:
            case 7:
                return false;
            case 40:
                return isIndexable((TypeInfo) typeInfo.getExtTypeInfo());
            case 41:
                Iterator<Map.Entry<String, TypeInfo>> it = ((ExtTypeInfoRow) typeInfo.getExtTypeInfo()).getFields().iterator();
                while (it.hasNext()) {
                    if (!isIndexable(it.next().getValue())) {
                        return false;
                    }
                }
                return true;
            default:
                return true;
        }
    }

    public static boolean areStableComparable(TypeInfo typeInfo, TypeInfo typeInfo2) {
        int valueType = typeInfo.getValueType();
        int valueType2 = typeInfo2.getValueType();
        switch (valueType) {
            case -1:
            case 0:
            case 3:
            case 7:
            case 41:
                return false;
            case 1:
            case 2:
            case 4:
            case 5:
            case 6:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            default:
                switch (valueType2) {
                    case -1:
                    case 0:
                    case 3:
                    case 7:
                    case 41:
                        return false;
                    default:
                        return true;
                }
            case 17:
            case 20:
                return valueType2 == 17 || valueType2 == 20;
            case 18:
            case 19:
            case 21:
                return valueType == valueType2;
            case 40:
                if (valueType2 == 40) {
                    return areStableComparable((TypeInfo) typeInfo.getExtTypeInfo(), (TypeInfo) typeInfo2.getExtTypeInfo());
                }
                return false;
        }
    }

    public static boolean isDateTimeType(int i) {
        return i >= 17 && i <= 21;
    }

    public static boolean isIntervalType(int i) {
        return i >= 22 && i <= 34;
    }

    public static boolean isYearMonthIntervalType(int i) {
        return i == 22 || i == 23 || i == 28;
    }

    public static boolean isLargeObject(int i) {
        return i == 7 || i == 3;
    }

    public static boolean isNumericType(int i) {
        return i >= 9 && i <= 16;
    }

    public static boolean isBinaryStringType(int i) {
        return i >= 5 && i <= 7;
    }

    public static boolean isCharacterStringType(int i) {
        return i >= 1 && i <= 4;
    }

    public static boolean isStringType(int i) {
        return i == 2 || i == 1 || i == 4;
    }

    public static boolean isBinaryStringOrSpecialBinaryType(int i) {
        switch (i) {
            case 5:
            case 6:
            case 7:
            case 35:
            case 37:
            case 38:
            case 39:
                return true;
            default:
                return false;
        }
    }

    public static boolean hasTotalOrdering(int i) {
        switch (i) {
            case 5:
            case 6:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 14:
            case 15:
            case 17:
            case 18:
            case 20:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 39:
                return true;
            case 7:
            case 13:
            case 16:
            case 19:
            case 21:
            case 38:
            default:
                return false;
        }
    }

    public static long addPrecision(long j, long j2) {
        long j3 = j + j2;
        if ((j | j2 | j3) < 0) {
            return Long.MAX_VALUE;
        }
        return j3;
    }

    public static Object getDefaultForPrimitiveType(Class<?> cls) {
        if (cls == Boolean.TYPE) {
            return Boolean.FALSE;
        }
        if (cls == Byte.TYPE) {
            return (byte) 0;
        }
        if (cls == Character.TYPE) {
            return (char) 0;
        }
        if (cls == Short.TYPE) {
            return (short) 0;
        }
        if (cls == Integer.TYPE) {
            return 0;
        }
        if (cls == Long.TYPE) {
            return 0L;
        }
        if (cls == Float.TYPE) {
            return Float.valueOf(0.0f);
        }
        if (cls == Double.TYPE) {
            return Double.valueOf(0.0d);
        }
        throw DbException.getInternalError("primitive=" + cls.toString());
    }
}

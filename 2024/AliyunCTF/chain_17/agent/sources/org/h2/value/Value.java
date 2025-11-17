package org.h2.value;

import ch.qos.logback.core.joran.JoranConstants;
import com.fasterxml.jackson.core.JsonFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.lang.ref.SoftReference;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.h2.api.ErrorCode;
import org.h2.api.IntervalQualifier;
import org.h2.engine.CastDataProvider;
import org.h2.engine.Mode;
import org.h2.message.DbException;
import org.h2.util.Bits;
import org.h2.util.DateTimeUtils;
import org.h2.util.HasSQL;
import org.h2.util.IntervalUtils;
import org.h2.util.JdbcUtils;
import org.h2.util.MathUtils;
import org.h2.util.StringUtils;
import org.h2.util.geometry.GeoJsonUtils;
import org.h2.util.json.JsonConstructorUtils;
import org.slf4j.Marker;
import org.springframework.aop.framework.autoproxy.target.QuickTargetSourceCreator;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/Value.class */
public abstract class Value extends VersionedValue<Value> implements HasSQL, Typed {
    public static final int UNKNOWN = -1;
    public static final int NULL = 0;
    public static final int CHAR = 1;
    public static final int VARCHAR = 2;
    public static final int CLOB = 3;
    public static final int VARCHAR_IGNORECASE = 4;
    public static final int BINARY = 5;
    public static final int VARBINARY = 6;
    public static final int BLOB = 7;
    public static final int BOOLEAN = 8;
    public static final int TINYINT = 9;
    public static final int SMALLINT = 10;
    public static final int INTEGER = 11;
    public static final int BIGINT = 12;
    public static final int NUMERIC = 13;
    public static final int REAL = 14;
    public static final int DOUBLE = 15;
    public static final int DECFLOAT = 16;
    public static final int DATE = 17;
    public static final int TIME = 18;
    public static final int TIME_TZ = 19;
    public static final int TIMESTAMP = 20;
    public static final int TIMESTAMP_TZ = 21;
    public static final int INTERVAL_YEAR = 22;
    public static final int INTERVAL_MONTH = 23;
    public static final int INTERVAL_DAY = 24;
    public static final int INTERVAL_HOUR = 25;
    public static final int INTERVAL_MINUTE = 26;
    public static final int INTERVAL_SECOND = 27;
    public static final int INTERVAL_YEAR_TO_MONTH = 28;
    public static final int INTERVAL_DAY_TO_HOUR = 29;
    public static final int INTERVAL_DAY_TO_MINUTE = 30;
    public static final int INTERVAL_DAY_TO_SECOND = 31;
    public static final int INTERVAL_HOUR_TO_MINUTE = 32;
    public static final int INTERVAL_HOUR_TO_SECOND = 33;
    public static final int INTERVAL_MINUTE_TO_SECOND = 34;
    public static final int JAVA_OBJECT = 35;
    public static final int ENUM = 36;
    public static final int GEOMETRY = 37;
    public static final int JSON = 38;
    public static final int UUID = 39;
    public static final int ARRAY = 40;
    public static final int ROW = 41;
    public static final int TYPE_COUNT = 42;
    static final int GROUP_NULL = 0;
    static final int GROUP_CHARACTER_STRING = 1;
    static final int GROUP_BINARY_STRING = 2;
    static final int GROUP_BOOLEAN = 3;
    static final int GROUP_NUMERIC = 4;
    static final int GROUP_DATETIME = 5;
    static final int GROUP_INTERVAL_YM = 6;
    static final int GROUP_INTERVAL_DT = 7;
    static final int GROUP_OTHER = 8;
    static final int GROUP_COLLECTION = 9;
    private static SoftReference<Value[]> softCache;
    static final int CONVERT_TO = 0;
    static final int CAST_TO = 1;
    static final int ASSIGN_TO = 2;
    static final byte[] GROUPS = {0, 1, 1, 1, 1, 2, 2, 2, 3, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 6, 6, 7, 7, 7, 7, 6, 7, 7, 7, 7, 7, 7, 8, 8, 8, 8, 8, 9, 9};
    private static final String[] NAMES = {"UNKNOWN", JoranConstants.NULL, "CHARACTER", "CHARACTER VARYING", "CHARACTER LARGE OBJECT", "VARCHAR_IGNORECASE", "BINARY", "BINARY VARYING", "BINARY LARGE OBJECT", "BOOLEAN", "TINYINT", "SMALLINT", "INTEGER", "BIGINT", "NUMERIC", "REAL", "DOUBLE PRECISION", "DECFLOAT", "DATE", "TIME", "TIME WITH TIME ZONE", "TIMESTAMP", "TIMESTAMP WITH TIME ZONE", "INTERVAL YEAR", "INTERVAL MONTH", "INTERVAL DAY", "INTERVAL HOUR", "INTERVAL MINUTE", "INTERVAL SECOND", "INTERVAL YEAR TO MONTH", "INTERVAL DAY TO HOUR", "INTERVAL DAY TO MINUTE", "INTERVAL DAY TO SECOND", "INTERVAL HOUR TO MINUTE", "INTERVAL HOUR TO SECOND", "INTERVAL MINUTE TO SECOND", "JAVA_OBJECT", "ENUM", "GEOMETRY", JsonFactory.FORMAT_NAME_JSON, "UUID", "ARRAY", "ROW"};
    public static final Value[] EMPTY_VALUES = new Value[0];
    static final BigDecimal MAX_LONG_DECIMAL = BigDecimal.valueOf(Long.MAX_VALUE);
    public static final BigDecimal MIN_LONG_DECIMAL = BigDecimal.valueOf(Long.MIN_VALUE);

    public abstract TypeInfo getType();

    public abstract int getValueType();

    public abstract int hashCode();

    public abstract boolean equals(Object obj);

    public abstract String getString();

    public abstract int compareTypeSafe(Value value, CompareMode compareMode, CastDataProvider castDataProvider);

    public static String getTypeName(int i) {
        return NAMES[i + 1];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void rangeCheck(long j, long j2, long j3) {
        if ((j | j2) < 0 || j2 > j3 - j) {
            if (j < 0 || j > j3) {
                throw DbException.getInvalidValueException("offset", Long.valueOf(j + 1));
            }
            throw DbException.getInvalidValueException("length", Long.valueOf(j2));
        }
    }

    public int getMemory() {
        return 24;
    }

    public static int getHigherOrder(int i, int i2) {
        if (i == i2) {
            if (i == -1) {
                throw DbException.get(ErrorCode.UNKNOWN_DATA_TYPE_1, "?, ?");
            }
            return i;
        }
        if (i < i2) {
            i = i2;
            i2 = i;
        }
        if (i == -1) {
            if (i2 == 0) {
                throw DbException.get(ErrorCode.UNKNOWN_DATA_TYPE_1, "?, NULL");
            }
            return i2;
        }
        if (i2 == -1) {
            if (i == 0) {
                throw DbException.get(ErrorCode.UNKNOWN_DATA_TYPE_1, "NULL, ?");
            }
            return i;
        }
        if (i2 == 0) {
            return i;
        }
        return getHigherOrderKnown(i, i2);
    }

    private static int getHigherOrderNonNull(int i, int i2) {
        if (i == i2) {
            return i;
        }
        if (i < i2) {
            i = i2;
            i2 = i;
        }
        return getHigherOrderKnown(i, i2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getHigherOrderKnown(int i, int i2) {
        byte b = GROUPS[i];
        byte b2 = GROUPS[i2];
        switch (b) {
            case 3:
                if (b2 == 2) {
                    throw getDataTypeCombinationException(8, i2);
                }
                break;
            case 4:
                return getHigherNumeric(i, i2, b2);
            case 5:
                return getHigherDateTime(i, i2, b2);
            case 6:
                return getHigherIntervalYearMonth(i, i2, b2);
            case 7:
                return getHigherIntervalDayTime(i, i2, b2);
            case 8:
                return getHigherOther(i, i2, b2);
        }
        return i;
    }

    private static int getHigherNumeric(int i, int i2, int i3) {
        if (i3 == 4) {
            switch (i) {
                case 14:
                    switch (i2) {
                        case 11:
                            return 15;
                        case 12:
                        case 13:
                            return 16;
                    }
                case 15:
                    switch (i2) {
                        case 12:
                        case 13:
                            return 16;
                    }
            }
        }
        if (i3 == 2) {
            throw getDataTypeCombinationException(i, i2);
        }
        return i;
    }

    private static int getHigherDateTime(int i, int i2, int i3) {
        if (i3 == 1) {
            return i;
        }
        if (i3 != 5) {
            throw getDataTypeCombinationException(i, i2);
        }
        switch (i) {
            case 18:
                if (i2 == 17) {
                    return 20;
                }
                break;
            case 19:
                if (i2 == 17) {
                    return 21;
                }
                break;
            case 20:
                if (i2 == 19) {
                    return 21;
                }
                break;
        }
        return i;
    }

    /* JADX WARN: Failed to find 'out' block for switch in B:2:0x0001. Please report as an issue. */
    private static int getHigherIntervalYearMonth(int i, int i2, int i3) {
        switch (i3) {
            case 6:
                if (i == 23 && i2 == 22) {
                    return 28;
                }
                break;
            case 1:
            case 4:
                return i;
            default:
                throw getDataTypeCombinationException(i, i2);
        }
    }

    private static int getHigherIntervalDayTime(int i, int i2, int i3) {
        switch (i3) {
            case 1:
            case 4:
                return i;
            case 7:
                switch (i) {
                    case 25:
                        return 29;
                    case 26:
                        if (i2 == 24) {
                            return 30;
                        }
                        return 32;
                    case 27:
                        if (i2 == 24) {
                            return 31;
                        }
                        if (i2 == 25) {
                            return 33;
                        }
                        return 34;
                    case 29:
                        if (i2 == 26) {
                            return 30;
                        }
                        if (i2 == 27) {
                            return 31;
                        }
                        break;
                    case 30:
                        if (i2 == 27) {
                            return 31;
                        }
                        break;
                    case 32:
                        switch (i2) {
                            case 24:
                            case 29:
                            case 30:
                                return 30;
                            case 27:
                                return 33;
                            case 31:
                                return 31;
                        }
                    case 33:
                        switch (i2) {
                            case 24:
                            case 29:
                            case 30:
                            case 31:
                                return 31;
                        }
                    case 34:
                        switch (i2) {
                            case 24:
                            case 29:
                            case 30:
                            case 31:
                                return 31;
                            case 25:
                            case 32:
                            case 33:
                                return 33;
                        }
                }
                return i;
            default:
                throw getDataTypeCombinationException(i, i2);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:29:0x00a6, code lost:            if (r4 == 35) goto L35;     */
    /* JADX WARN: Failed to find 'out' block for switch in B:27:0x007e. Please report as an issue. */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static int getHigherOther(int r3, int r4, int r5) {
        /*
            r0 = r3
            switch(r0) {
                case 35: goto L24;
                case 36: goto L2f;
                case 37: goto L45;
                case 38: goto L55;
                case 39: goto L7d;
                default: goto Lb2;
            }
        L24:
            r0 = r5
            r1 = 2
            if (r0 == r1) goto Lb2
            r0 = r3
            r1 = r4
            org.h2.message.DbException r0 = getDataTypeCombinationException(r0, r1)
            throw r0
        L2f:
            r0 = r5
            r1 = 1
            if (r0 == r1) goto Lb2
            r0 = r5
            r1 = 4
            if (r0 != r1) goto L3f
            r0 = r4
            r1 = 11
            if (r0 <= r1) goto Lb2
        L3f:
            r0 = r3
            r1 = r4
            org.h2.message.DbException r0 = getDataTypeCombinationException(r0, r1)
            throw r0
        L45:
            r0 = r5
            r1 = 1
            if (r0 == r1) goto Lb2
            r0 = r5
            r1 = 2
            if (r0 == r1) goto Lb2
            r0 = r3
            r1 = r4
            org.h2.message.DbException r0 = getDataTypeCombinationException(r0, r1)
            throw r0
        L55:
            r0 = r5
            switch(r0) {
                case 5: goto L74;
                case 6: goto L74;
                case 7: goto L74;
                case 8: goto L74;
                default: goto L7a;
            }
        L74:
            r0 = r3
            r1 = r4
            org.h2.message.DbException r0 = getDataTypeCombinationException(r0, r1)
            throw r0
        L7a:
            goto Lb2
        L7d:
            r0 = r5
            switch(r0) {
                case 1: goto La0;
                case 2: goto La0;
                case 8: goto La3;
                default: goto Lac;
            }
        La0:
            goto Lb2
        La3:
            r0 = r4
            r1 = 35
            if (r0 != r1) goto Lac
            goto Lb2
        Lac:
            r0 = r3
            r1 = r4
            org.h2.message.DbException r0 = getDataTypeCombinationException(r0, r1)
            throw r0
        Lb2:
            r0 = r3
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.value.Value.getHigherOther(int, int, int):int");
    }

    private static DbException getDataTypeCombinationException(int i, int i2) {
        return DbException.get(ErrorCode.DATA_CONVERSION_ERROR_1, getTypeName(i) + ", " + getTypeName(i2));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Code restructure failed: missing block: B:6:0x001c, code lost:            if (r0 == null) goto L8;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static org.h2.value.Value cache(org.h2.value.Value r4) {
        /*
            boolean r0 = org.h2.engine.SysProperties.OBJECT_CACHE
            if (r0 == 0) goto L5f
            r0 = r4
            int r0 = r0.hashCode()
            r5 = r0
            java.lang.ref.SoftReference<org.h2.value.Value[]> r0 = org.h2.value.Value.softCache
            if (r0 == 0) goto L1f
            java.lang.ref.SoftReference<org.h2.value.Value[]> r0 = org.h2.value.Value.softCache
            java.lang.Object r0 = r0.get()
            org.h2.value.Value[] r0 = (org.h2.value.Value[]) r0
            r1 = r0
            r6 = r1
            if (r0 != 0) goto L31
        L1f:
            int r0 = org.h2.engine.SysProperties.OBJECT_CACHE_SIZE
            org.h2.value.Value[] r0 = new org.h2.value.Value[r0]
            r6 = r0
            java.lang.ref.SoftReference r0 = new java.lang.ref.SoftReference
            r1 = r0
            r2 = r6
            r1.<init>(r2)
            org.h2.value.Value.softCache = r0
        L31:
            r0 = r5
            int r1 = org.h2.engine.SysProperties.OBJECT_CACHE_SIZE
            r2 = 1
            int r1 = r1 - r2
            r0 = r0 & r1
            r7 = r0
            r0 = r6
            r1 = r7
            r0 = r0[r1]
            r8 = r0
            r0 = r8
            if (r0 == 0) goto L5b
            r0 = r8
            int r0 = r0.getValueType()
            r1 = r4
            int r1 = r1.getValueType()
            if (r0 != r1) goto L5b
            r0 = r4
            r1 = r8
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L5b
            r0 = r8
            return r0
        L5b:
            r0 = r6
            r1 = r7
            r2 = r4
            r0[r1] = r2
        L5f:
            r0 = r4
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.value.Value.cache(org.h2.value.Value):org.h2.value.Value");
    }

    public static void clearCache() {
        softCache = null;
    }

    public Reader getReader() {
        return new StringReader(getString());
    }

    public Reader getReader(long j, long j2) {
        String string = getString();
        long j3 = j - 1;
        rangeCheck(j3, j2, string.length());
        int i = (int) j3;
        return new StringReader(string.substring(i, i + ((int) j2)));
    }

    public byte[] getBytes() {
        throw getDataConversionError(6);
    }

    public byte[] getBytesNoCopy() {
        return getBytes();
    }

    public InputStream getInputStream() {
        return new ByteArrayInputStream(getBytesNoCopy());
    }

    public InputStream getInputStream(long j, long j2) {
        byte[] bytesNoCopy = getBytesNoCopy();
        long j3 = j - 1;
        rangeCheck(j3, j2, bytesNoCopy.length);
        return new ByteArrayInputStream(bytesNoCopy, (int) j3, (int) j2);
    }

    public boolean getBoolean() {
        return convertToBoolean().getBoolean();
    }

    public byte getByte() {
        return convertToTinyint(null).getByte();
    }

    public short getShort() {
        return convertToSmallint(null).getShort();
    }

    public int getInt() {
        return convertToInt(null).getInt();
    }

    public long getLong() {
        return convertToBigint(null).getLong();
    }

    public BigDecimal getBigDecimal() {
        throw getDataConversionError(13);
    }

    public float getFloat() {
        throw getDataConversionError(14);
    }

    public double getDouble() {
        throw getDataConversionError(15);
    }

    public Value add(Value value) {
        throw getUnsupportedExceptionForOperation(Marker.ANY_NON_NULL_MARKER);
    }

    public int getSignum() {
        throw getUnsupportedExceptionForOperation("SIGNUM");
    }

    public Value negate() {
        throw getUnsupportedExceptionForOperation("NEG");
    }

    public Value subtract(Value value) {
        throw getUnsupportedExceptionForOperation("-");
    }

    public Value divide(Value value, TypeInfo typeInfo) {
        throw getUnsupportedExceptionForOperation("/");
    }

    public Value multiply(Value value) {
        throw getUnsupportedExceptionForOperation("*");
    }

    public Value modulus(Value value) {
        throw getUnsupportedExceptionForOperation(QuickTargetSourceCreator.PREFIX_THREAD_LOCAL);
    }

    public final Value convertTo(int i) {
        return convertTo(i, (CastDataProvider) null);
    }

    public final Value convertTo(TypeInfo typeInfo) {
        return convertTo(typeInfo, null, 0, null);
    }

    public final Value convertTo(int i, CastDataProvider castDataProvider) {
        switch (i) {
            case 40:
                return convertToAnyArray(castDataProvider);
            case 41:
                return convertToAnyRow();
            default:
                return convertTo(TypeInfo.getTypeInfo(i), castDataProvider, 0, null);
        }
    }

    public final Value convertTo(TypeInfo typeInfo, CastDataProvider castDataProvider) {
        return convertTo(typeInfo, castDataProvider, 0, null);
    }

    public final Value convertTo(TypeInfo typeInfo, CastDataProvider castDataProvider, Object obj) {
        return convertTo(typeInfo, castDataProvider, 0, obj);
    }

    public final ValueJson convertToAnyJson() {
        return this != ValueNull.INSTANCE ? convertToJson(TypeInfo.TYPE_JSON, 0, null) : ValueJson.NULL;
    }

    public final ValueArray convertToAnyArray(CastDataProvider castDataProvider) {
        return getValueType() == 40 ? (ValueArray) this : ValueArray.get(getType(), new Value[]{this}, castDataProvider);
    }

    public final ValueRow convertToAnyRow() {
        return getValueType() == 41 ? (ValueRow) this : ValueRow.get(new Value[]{this});
    }

    public final Value castTo(TypeInfo typeInfo, CastDataProvider castDataProvider) {
        return convertTo(typeInfo, castDataProvider, 1, null);
    }

    public final Value convertForAssignTo(TypeInfo typeInfo, CastDataProvider castDataProvider, Object obj) {
        return convertTo(typeInfo, castDataProvider, 2, obj);
    }

    private Value convertTo(TypeInfo typeInfo, CastDataProvider castDataProvider, int i, Object obj) {
        int valueType;
        int valueType2 = getValueType();
        if (valueType2 == 0 || (valueType2 == (valueType = typeInfo.getValueType()) && i == 0 && typeInfo.getExtTypeInfo() == null && valueType2 != 1)) {
            return this;
        }
        switch (valueType) {
            case 0:
                return ValueNull.INSTANCE;
            case 1:
                return convertToChar(typeInfo, castDataProvider, i, obj);
            case 2:
                return convertToVarchar(typeInfo, castDataProvider, i, obj);
            case 3:
                return convertToClob(typeInfo, i, obj);
            case 4:
                return convertToVarcharIgnoreCase(typeInfo, i, obj);
            case 5:
                return convertToBinary(typeInfo, i, obj);
            case 6:
                return convertToVarbinary(typeInfo, i, obj);
            case 7:
                return convertToBlob(typeInfo, i, obj);
            case 8:
                return convertToBoolean();
            case 9:
                return convertToTinyint(obj);
            case 10:
                return convertToSmallint(obj);
            case 11:
                return convertToInt(obj);
            case 12:
                return convertToBigint(obj);
            case 13:
                return convertToNumeric(typeInfo, castDataProvider, i, obj);
            case 14:
                return convertToReal();
            case 15:
                return convertToDouble();
            case 16:
                return convertToDecfloat(typeInfo, i);
            case 17:
                return convertToDate(castDataProvider);
            case 18:
                return convertToTime(typeInfo, castDataProvider, i);
            case 19:
                return convertToTimeTimeZone(typeInfo, castDataProvider, i);
            case 20:
                return convertToTimestamp(typeInfo, castDataProvider, i);
            case 21:
                return convertToTimestampTimeZone(typeInfo, castDataProvider, i);
            case 22:
            case 23:
            case 28:
                return convertToIntervalYearMonth(typeInfo, i, obj);
            case 24:
            case 25:
            case 26:
            case 27:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
                return convertToIntervalDayTime(typeInfo, i, obj);
            case 35:
                return convertToJavaObject(typeInfo, i, obj);
            case 36:
                return convertToEnum((ExtTypeInfoEnum) typeInfo.getExtTypeInfo(), castDataProvider);
            case 37:
                return convertToGeometry((ExtTypeInfoGeometry) typeInfo.getExtTypeInfo());
            case 38:
                return convertToJson(typeInfo, i, obj);
            case 39:
                return convertToUuid();
            case 40:
                return convertToArray(typeInfo, castDataProvider, i, obj);
            case 41:
                return convertToRow(typeInfo, castDataProvider, i, obj);
            default:
                throw getDataConversionError(valueType);
        }
    }

    public ValueChar convertToChar() {
        return convertToChar(TypeInfo.getTypeInfo(1), null, 0, null);
    }

    private ValueChar convertToChar(TypeInfo typeInfo, CastDataProvider castDataProvider, int i, Object obj) {
        int valueType = getValueType();
        switch (valueType) {
            case 7:
            case 35:
                throw getDataConversionError(typeInfo.getValueType());
            default:
                String string = getString();
                int length = string.length();
                int i2 = length;
                if (i == 0) {
                    while (i2 > 0 && string.charAt(i2 - 1) == ' ') {
                        i2--;
                    }
                } else {
                    int convertLongToInt = MathUtils.convertLongToInt(typeInfo.getPrecision());
                    if (castDataProvider == null || castDataProvider.getMode().charPadding == Mode.CharPadding.ALWAYS) {
                        if (i2 != convertLongToInt) {
                            if (i2 < convertLongToInt) {
                                return ValueChar.get(StringUtils.pad(string, convertLongToInt, null, true));
                            }
                            if (i == 1) {
                                i2 = convertLongToInt;
                            }
                            do {
                                i2--;
                                if (string.charAt(i2) != ' ') {
                                    throw getValueTooLongException(typeInfo, obj);
                                }
                            } while (i2 > convertLongToInt);
                        }
                    } else {
                        if (i == 1 && i2 > convertLongToInt) {
                            i2 = convertLongToInt;
                        }
                        while (i2 > 0 && string.charAt(i2 - 1) == ' ') {
                            i2--;
                        }
                        if (i == 2 && i2 > convertLongToInt) {
                            throw getValueTooLongException(typeInfo, obj);
                        }
                    }
                }
                if (length != i2) {
                    string = string.substring(0, i2);
                } else if (valueType == 1) {
                    return (ValueChar) this;
                }
                return ValueChar.get(string);
        }
    }

    private Value convertToVarchar(TypeInfo typeInfo, CastDataProvider castDataProvider, int i, Object obj) {
        int valueType = getValueType();
        switch (valueType) {
            case 7:
            case 35:
                throw getDataConversionError(typeInfo.getValueType());
            default:
                if (i != 0) {
                    String string = getString();
                    int convertLongToInt = MathUtils.convertLongToInt(typeInfo.getPrecision());
                    if (string.length() > convertLongToInt) {
                        if (i != 1) {
                            throw getValueTooLongException(typeInfo, obj);
                        }
                        return ValueVarchar.get(string.substring(0, convertLongToInt), castDataProvider);
                    }
                }
                return valueType == 2 ? this : ValueVarchar.get(getString(), castDataProvider);
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to find 'out' block for switch in B:2:0x0004. Please report as an issue. */
    /* JADX WARN: Removed duplicated region for block: B:6:0x00b1  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private org.h2.value.ValueClob convertToClob(org.h2.value.TypeInfo r6, int r7, java.lang.Object r8) {
        /*
            r5 = this;
            r0 = r5
            int r0 = r0.getValueType()
            switch(r0) {
                case 3: goto L28;
                case 7: goto L3a;
                case 35: goto L31;
                default: goto La4;
            }
        L28:
            r0 = r5
            org.h2.value.ValueClob r0 = (org.h2.value.ValueClob) r0
            r9 = r0
            goto Lad
        L31:
            r0 = r5
            r1 = r6
            int r1 = r1.getValueType()
            org.h2.message.DbException r0 = r0.getDataConversionError(r1)
            throw r0
        L3a:
            r0 = r5
            org.h2.value.ValueBlob r0 = (org.h2.value.ValueBlob) r0
            org.h2.value.lob.LobData r0 = r0.lobData
            r10 = r0
            r0 = r10
            boolean r0 = r0 instanceof org.h2.value.lob.LobDataInMemory
            if (r0 == 0) goto L81
            r0 = r10
            org.h2.value.lob.LobDataInMemory r0 = (org.h2.value.lob.LobDataInMemory) r0
            byte[] r0 = r0.getSmall()
            r11 = r0
            java.lang.String r0 = new java.lang.String
            r1 = r0
            r2 = r11
            java.nio.charset.Charset r3 = java.nio.charset.StandardCharsets.UTF_8
            r1.<init>(r2, r3)
            java.nio.charset.Charset r1 = java.nio.charset.StandardCharsets.UTF_8
            byte[] r0 = r0.getBytes(r1)
            r12 = r0
            r0 = r12
            r1 = r11
            boolean r0 = java.util.Arrays.equals(r0, r1)
            if (r0 == 0) goto L77
            r0 = r11
            r12 = r0
        L77:
            r0 = r12
            org.h2.value.ValueClob r0 = org.h2.value.ValueClob.createSmall(r0)
            r9 = r0
            goto Lad
        L81:
            r0 = r10
            boolean r0 = r0 instanceof org.h2.value.lob.LobDataDatabase
            if (r0 == 0) goto La4
            r0 = r10
            org.h2.store.DataHandler r0 = r0.getDataHandler()
            org.h2.store.LobStorageInterface r0 = r0.getLobStorage()
            r1 = r5
            java.io.Reader r1 = r1.getReader()
            r2 = -1
            org.h2.value.ValueClob r0 = r0.createClob(r1, r2)
            r9 = r0
            goto Lad
        La4:
            r0 = r5
            java.lang.String r0 = r0.getString()
            org.h2.value.ValueClob r0 = org.h2.value.ValueClob.createSmall(r0)
            r9 = r0
        Lad:
            r0 = r7
            if (r0 == 0) goto Ld9
            r0 = r7
            r1 = 1
            if (r0 != r1) goto Lc4
            r0 = r9
            r1 = r6
            long r1 = r1.getPrecision()
            org.h2.value.ValueClob r0 = r0.convertPrecision(r1)
            r9 = r0
            goto Ld9
        Lc4:
            r0 = r9
            long r0 = r0.charLength()
            r1 = r6
            long r1 = r1.getPrecision()
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 <= 0) goto Ld9
            r0 = r9
            r1 = r6
            r2 = r8
            org.h2.message.DbException r0 = r0.getValueTooLongException(r1, r2)
            throw r0
        Ld9:
            r0 = r9
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.value.Value.convertToClob(org.h2.value.TypeInfo, int, java.lang.Object):org.h2.value.ValueClob");
    }

    private Value convertToVarcharIgnoreCase(TypeInfo typeInfo, int i, Object obj) {
        int valueType = getValueType();
        switch (valueType) {
            case 7:
            case 35:
                throw getDataConversionError(typeInfo.getValueType());
            default:
                if (i != 0) {
                    String string = getString();
                    int convertLongToInt = MathUtils.convertLongToInt(typeInfo.getPrecision());
                    if (string.length() > convertLongToInt) {
                        if (i != 1) {
                            throw getValueTooLongException(typeInfo, obj);
                        }
                        return ValueVarcharIgnoreCase.get(string.substring(0, convertLongToInt));
                    }
                }
                return valueType == 4 ? this : ValueVarcharIgnoreCase.get(getString());
        }
    }

    private ValueBinary convertToBinary(TypeInfo typeInfo, int i, Object obj) {
        ValueBinary noCopy;
        byte[] bytesNoCopy;
        int length;
        int convertLongToInt;
        if (getValueType() == 5) {
            noCopy = (ValueBinary) this;
        } else {
            try {
                noCopy = ValueBinary.getNoCopy(getBytesNoCopy());
            } catch (DbException e) {
                if (e.getErrorCode() == 22018) {
                    throw getDataConversionError(5);
                }
                throw e;
            }
        }
        if (i != 0 && (length = (bytesNoCopy = noCopy.getBytesNoCopy()).length) != (convertLongToInt = MathUtils.convertLongToInt(typeInfo.getPrecision()))) {
            if (i == 2 && length > convertLongToInt) {
                throw noCopy.getValueTooLongException(typeInfo, obj);
            }
            noCopy = ValueBinary.getNoCopy(Arrays.copyOf(bytesNoCopy, convertLongToInt));
        }
        return noCopy;
    }

    private ValueVarbinary convertToVarbinary(TypeInfo typeInfo, int i, Object obj) {
        ValueVarbinary noCopy;
        if (getValueType() == 6) {
            noCopy = (ValueVarbinary) this;
        } else {
            noCopy = ValueVarbinary.getNoCopy(getBytesNoCopy());
        }
        if (i != 0) {
            byte[] bytesNoCopy = noCopy.getBytesNoCopy();
            int length = bytesNoCopy.length;
            int convertLongToInt = MathUtils.convertLongToInt(typeInfo.getPrecision());
            if (i == 1) {
                if (length > convertLongToInt) {
                    noCopy = ValueVarbinary.getNoCopy(Arrays.copyOf(bytesNoCopy, convertLongToInt));
                }
            } else if (length > convertLongToInt) {
                throw noCopy.getValueTooLongException(typeInfo, obj);
            }
        }
        return noCopy;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to find 'out' block for switch in B:2:0x0004. Please report as an issue. */
    /* JADX WARN: Removed duplicated region for block: B:6:0x0079  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private org.h2.value.ValueBlob convertToBlob(org.h2.value.TypeInfo r6, int r7, java.lang.Object r8) {
        /*
            r5 = this;
            r0 = r5
            int r0 = r0.getValueType()
            switch(r0) {
                case 3: goto L29;
                case 7: goto L20;
                default: goto L52;
            }
        L20:
            r0 = r5
            org.h2.value.ValueBlob r0 = (org.h2.value.ValueBlob) r0
            r9 = r0
            goto L75
        L29:
            r0 = r5
            org.h2.value.ValueLob r0 = (org.h2.value.ValueLob) r0
            org.h2.value.lob.LobData r0 = r0.lobData
            org.h2.store.DataHandler r0 = r0.getDataHandler()
            r10 = r0
            r0 = r10
            if (r0 == 0) goto L52
            r0 = r10
            org.h2.store.LobStorageInterface r0 = r0.getLobStorage()
            r1 = r5
            java.io.InputStream r1 = r1.getInputStream()
            r2 = -1
            org.h2.value.ValueBlob r0 = r0.createBlob(r1, r2)
            r9 = r0
            goto L75
        L52:
            r0 = r5
            byte[] r0 = r0.getBytesNoCopy()     // Catch: org.h2.message.DbException -> L5e
            org.h2.value.ValueBlob r0 = org.h2.value.ValueBlob.createSmall(r0)     // Catch: org.h2.message.DbException -> L5e
            r9 = r0
            goto L75
        L5e:
            r11 = move-exception
            r0 = r11
            int r0 = r0.getErrorCode()
            r1 = 22018(0x5602, float:3.0854E-41)
            if (r0 != r1) goto L72
            r0 = r5
            r1 = 7
            org.h2.message.DbException r0 = r0.getDataConversionError(r1)
            throw r0
        L72:
            r0 = r11
            throw r0
        L75:
            r0 = r7
            if (r0 == 0) goto La1
            r0 = r7
            r1 = 1
            if (r0 != r1) goto L8c
            r0 = r9
            r1 = r6
            long r1 = r1.getPrecision()
            org.h2.value.ValueBlob r0 = r0.convertPrecision(r1)
            r9 = r0
            goto La1
        L8c:
            r0 = r9
            long r0 = r0.octetLength()
            r1 = r6
            long r1 = r1.getPrecision()
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 <= 0) goto La1
            r0 = r9
            r1 = r6
            r2 = r8
            org.h2.message.DbException r0 = r0.getValueTooLongException(r1, r2)
            throw r0
        La1:
            r0 = r9
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.value.Value.convertToBlob(org.h2.value.TypeInfo, int, java.lang.Object):org.h2.value.ValueBlob");
    }

    public final ValueBoolean convertToBoolean() {
        switch (getValueType()) {
            case 0:
                throw DbException.getInternalError();
            case 1:
            case 2:
            case 4:
                return ValueBoolean.get(getBoolean());
            case 3:
            case 5:
            case 6:
            case 7:
            default:
                throw getDataConversionError(8);
            case 8:
                return (ValueBoolean) this;
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
                return ValueBoolean.get(getSignum() != 0);
        }
    }

    public final ValueTinyint convertToTinyint(Object obj) {
        switch (getValueType()) {
            case 0:
                throw DbException.getInternalError();
            case 1:
            case 2:
            case 4:
            case 8:
                return ValueTinyint.get(getByte());
            case 5:
            case 6:
                byte[] bytesNoCopy = getBytesNoCopy();
                if (bytesNoCopy.length == 1) {
                    return ValueTinyint.get(bytesNoCopy[0]);
                }
                break;
            case 9:
                return (ValueTinyint) this;
            case 10:
            case 11:
            case 36:
                return ValueTinyint.get(convertToByte(getInt(), obj));
            case 12:
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
                return ValueTinyint.get(convertToByte(getLong(), obj));
            case 13:
            case 16:
                return ValueTinyint.get(convertToByte(convertToLong(getBigDecimal(), obj), obj));
            case 14:
            case 15:
                return ValueTinyint.get(convertToByte(convertToLong(getDouble(), obj), obj));
        }
        throw getDataConversionError(9);
    }

    public final ValueSmallint convertToSmallint(Object obj) {
        switch (getValueType()) {
            case 0:
                throw DbException.getInternalError();
            case 1:
            case 2:
            case 4:
            case 8:
            case 9:
                return ValueSmallint.get(getShort());
            case 5:
            case 6:
                byte[] bytesNoCopy = getBytesNoCopy();
                if (bytesNoCopy.length == 2) {
                    return ValueSmallint.get((short) ((bytesNoCopy[0] << 8) + (bytesNoCopy[1] & 255)));
                }
                break;
            case 10:
                return (ValueSmallint) this;
            case 11:
            case 36:
                return ValueSmallint.get(convertToShort(getInt(), obj));
            case 12:
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
                return ValueSmallint.get(convertToShort(getLong(), obj));
            case 13:
            case 16:
                return ValueSmallint.get(convertToShort(convertToLong(getBigDecimal(), obj), obj));
            case 14:
            case 15:
                return ValueSmallint.get(convertToShort(convertToLong(getDouble(), obj), obj));
        }
        throw getDataConversionError(10);
    }

    public final ValueInteger convertToInt(Object obj) {
        switch (getValueType()) {
            case 0:
                throw DbException.getInternalError();
            case 1:
            case 2:
            case 4:
            case 8:
            case 9:
            case 10:
            case 36:
                return ValueInteger.get(getInt());
            case 5:
            case 6:
                byte[] bytesNoCopy = getBytesNoCopy();
                if (bytesNoCopy.length == 4) {
                    return ValueInteger.get(Bits.readInt(bytesNoCopy, 0));
                }
                break;
            case 11:
                return (ValueInteger) this;
            case 12:
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
                return ValueInteger.get(convertToInt(getLong(), obj));
            case 13:
            case 16:
                return ValueInteger.get(convertToInt(convertToLong(getBigDecimal(), obj), obj));
            case 14:
            case 15:
                return ValueInteger.get(convertToInt(convertToLong(getDouble(), obj), obj));
        }
        throw getDataConversionError(11);
    }

    public final ValueBigint convertToBigint(Object obj) {
        switch (getValueType()) {
            case 0:
                throw DbException.getInternalError();
            case 1:
            case 2:
            case 4:
            case 8:
            case 9:
            case 10:
            case 11:
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
            case 36:
                return ValueBigint.get(getLong());
            case 5:
            case 6:
                byte[] bytesNoCopy = getBytesNoCopy();
                if (bytesNoCopy.length == 8) {
                    return ValueBigint.get(Bits.readLong(bytesNoCopy, 0));
                }
                break;
            case 12:
                return (ValueBigint) this;
            case 13:
            case 16:
                return ValueBigint.get(convertToLong(getBigDecimal(), obj));
            case 14:
            case 15:
                return ValueBigint.get(convertToLong(getDouble(), obj));
        }
        throw getDataConversionError(12);
    }

    private ValueNumeric convertToNumeric(TypeInfo typeInfo, CastDataProvider castDataProvider, int i, Object obj) {
        ValueNumeric valueNumeric;
        switch (getValueType()) {
            case 0:
                throw DbException.getInternalError();
            case 8:
                valueNumeric = getBoolean() ? ValueNumeric.ONE : ValueNumeric.ZERO;
                break;
            case 13:
                valueNumeric = (ValueNumeric) this;
                break;
            default:
                BigDecimal bigDecimal = getBigDecimal();
                int scale = typeInfo.getScale();
                int scale2 = bigDecimal.scale();
                if (scale2 < 0 || scale2 > 100000 || (i != 0 && scale2 != scale && (scale2 >= scale || !castDataProvider.getMode().convertOnlyToSmallerScale))) {
                    bigDecimal = ValueNumeric.setScale(bigDecimal, scale);
                }
                if (i != 0 && bigDecimal.precision() > (typeInfo.getPrecision() - scale) + bigDecimal.scale()) {
                    throw getValueTooLongException(typeInfo, obj);
                }
                return ValueNumeric.get(bigDecimal);
        }
        if (i != 0) {
            int scale3 = typeInfo.getScale();
            BigDecimal bigDecimal2 = valueNumeric.getBigDecimal();
            int scale4 = bigDecimal2.scale();
            if (scale4 != scale3 && (scale4 >= scale3 || !castDataProvider.getMode().convertOnlyToSmallerScale)) {
                valueNumeric = ValueNumeric.get(ValueNumeric.setScale(bigDecimal2, scale3));
            }
            BigDecimal bigDecimal3 = valueNumeric.getBigDecimal();
            if (bigDecimal3.precision() > (typeInfo.getPrecision() - scale3) + bigDecimal3.scale()) {
                throw valueNumeric.getValueTooLongException(typeInfo, obj);
            }
        }
        return valueNumeric;
    }

    public final ValueReal convertToReal() {
        switch (getValueType()) {
            case 0:
                throw DbException.getInternalError();
            case 8:
                return getBoolean() ? ValueReal.ONE : ValueReal.ZERO;
            case 14:
                return (ValueReal) this;
            default:
                return ValueReal.get(getFloat());
        }
    }

    public final ValueDouble convertToDouble() {
        switch (getValueType()) {
            case 0:
                throw DbException.getInternalError();
            case 8:
                return getBoolean() ? ValueDouble.ONE : ValueDouble.ZERO;
            case 15:
                return (ValueDouble) this;
            default:
                return ValueDouble.get(getDouble());
        }
    }

    private ValueDecfloat convertToDecfloat(TypeInfo typeInfo, int i) {
        ValueDecfloat valueDecfloat;
        BigDecimal bigDecimal;
        int precision;
        int precision2;
        switch (getValueType()) {
            case 0:
                throw DbException.getInternalError();
            case 1:
            case 2:
            case 4:
                String trim = getString().trim();
                try {
                    valueDecfloat = ValueDecfloat.get(new BigDecimal(trim));
                    break;
                } catch (NumberFormatException e) {
                    boolean z = -1;
                    switch (trim.hashCode()) {
                        case -173313165:
                            if (trim.equals("+Infinity")) {
                                z = 2;
                                break;
                            }
                            break;
                        case 78043:
                            if (trim.equals("NaN")) {
                                z = 3;
                                break;
                            }
                            break;
                        case 1359056:
                            if (trim.equals("+NaN")) {
                                z = 5;
                                break;
                            }
                            break;
                        case 1418638:
                            if (trim.equals("-NaN")) {
                                z = 4;
                                break;
                            }
                            break;
                        case 237817416:
                            if (trim.equals("Infinity")) {
                                z = true;
                                break;
                            }
                            break;
                        case 506745205:
                            if (trim.equals("-Infinity")) {
                                z = false;
                                break;
                            }
                            break;
                    }
                    switch (z) {
                        case false:
                            return ValueDecfloat.NEGATIVE_INFINITY;
                        case true:
                        case true:
                            return ValueDecfloat.POSITIVE_INFINITY;
                        case true:
                        case true:
                        case true:
                            return ValueDecfloat.NAN;
                        default:
                            throw getDataConversionError(16);
                    }
                }
            case 3:
            case 5:
            case 6:
            case 7:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            default:
                try {
                    valueDecfloat = ValueDecfloat.get(getBigDecimal());
                    break;
                } catch (DbException e2) {
                    if (e2.getErrorCode() == 22018) {
                        throw getDataConversionError(16);
                    }
                    throw e2;
                }
            case 8:
                valueDecfloat = getBoolean() ? ValueDecfloat.ONE : ValueDecfloat.ZERO;
                break;
            case 14:
                float f = getFloat();
                if (!Float.isFinite(f)) {
                    if (f == Float.POSITIVE_INFINITY) {
                        return ValueDecfloat.POSITIVE_INFINITY;
                    }
                    if (f == Float.NEGATIVE_INFINITY) {
                        return ValueDecfloat.NEGATIVE_INFINITY;
                    }
                    return ValueDecfloat.NAN;
                }
                valueDecfloat = ValueDecfloat.get(new BigDecimal(Float.toString(f)));
                break;
            case 15:
                double d = getDouble();
                if (!Double.isFinite(d)) {
                    if (d == Double.POSITIVE_INFINITY) {
                        return ValueDecfloat.POSITIVE_INFINITY;
                    }
                    if (d == Double.NEGATIVE_INFINITY) {
                        return ValueDecfloat.NEGATIVE_INFINITY;
                    }
                    return ValueDecfloat.NAN;
                }
                valueDecfloat = ValueDecfloat.get(new BigDecimal(Double.toString(d)));
                break;
            case 16:
                valueDecfloat = (ValueDecfloat) this;
                if (valueDecfloat.value == null) {
                    return valueDecfloat;
                }
                break;
        }
        if (i != 0 && (precision = (bigDecimal = valueDecfloat.value).precision()) > (precision2 = (int) typeInfo.getPrecision())) {
            valueDecfloat = ValueDecfloat.get(bigDecimal.setScale((bigDecimal.scale() - precision) + precision2, RoundingMode.HALF_UP));
        }
        return valueDecfloat;
    }

    public final ValueDate convertToDate(CastDataProvider castDataProvider) {
        switch (getValueType()) {
            case 0:
                throw DbException.getInternalError();
            case 1:
            case 2:
            case 4:
                return ValueDate.parse(getString().trim());
            case 3:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 18:
            case 19:
            default:
                throw getDataConversionError(17);
            case 17:
                return (ValueDate) this;
            case 20:
                return ValueDate.fromDateValue(((ValueTimestamp) this).getDateValue());
            case 21:
                ValueTimestampTimeZone valueTimestampTimeZone = (ValueTimestampTimeZone) this;
                return ValueDate.fromDateValue(DateTimeUtils.dateValueFromLocalSeconds(DateTimeUtils.getEpochSeconds(valueTimestampTimeZone.getDateValue(), valueTimestampTimeZone.getTimeNanos(), valueTimestampTimeZone.getTimeZoneOffsetSeconds()) + castDataProvider.currentTimeZone().getTimeZoneOffsetUTC(r0)));
        }
    }

    private ValueTime convertToTime(TypeInfo typeInfo, CastDataProvider castDataProvider, int i) {
        ValueTime parse;
        int scale;
        switch (getValueType()) {
            case 1:
            case 2:
            case 4:
                parse = ValueTime.parse(getString().trim(), castDataProvider);
                break;
            case 3:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            default:
                throw getDataConversionError(18);
            case 18:
                parse = (ValueTime) this;
                break;
            case 19:
                parse = ValueTime.fromNanos(getLocalTimeNanos(castDataProvider));
                break;
            case 20:
                parse = ValueTime.fromNanos(((ValueTimestamp) this).getTimeNanos());
                break;
            case 21:
                ValueTimestampTimeZone valueTimestampTimeZone = (ValueTimestampTimeZone) this;
                long timeNanos = valueTimestampTimeZone.getTimeNanos();
                parse = ValueTime.fromNanos(DateTimeUtils.nanosFromLocalSeconds(DateTimeUtils.getEpochSeconds(valueTimestampTimeZone.getDateValue(), timeNanos, valueTimestampTimeZone.getTimeZoneOffsetSeconds()) + castDataProvider.currentTimeZone().getTimeZoneOffsetUTC(r0)) + (timeNanos % DateTimeUtils.NANOS_PER_SECOND));
                break;
        }
        if (i != 0 && (scale = typeInfo.getScale()) < 9) {
            long nanos = parse.getNanos();
            long convertScale = DateTimeUtils.convertScale(nanos, scale, DateTimeUtils.NANOS_PER_DAY);
            if (convertScale != nanos) {
                parse = ValueTime.fromNanos(convertScale);
            }
        }
        return parse;
    }

    private ValueTimeTimeZone convertToTimeTimeZone(TypeInfo typeInfo, CastDataProvider castDataProvider, int i) {
        ValueTimeTimeZone parse;
        int scale;
        switch (getValueType()) {
            case 1:
            case 2:
            case 4:
                parse = ValueTimeTimeZone.parse(getString().trim(), castDataProvider);
                break;
            case 3:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            default:
                throw getDataConversionError(19);
            case 18:
                parse = ValueTimeTimeZone.fromNanos(((ValueTime) this).getNanos(), castDataProvider.currentTimestamp().getTimeZoneOffsetSeconds());
                break;
            case 19:
                parse = (ValueTimeTimeZone) this;
                break;
            case 20:
                ValueTimestamp valueTimestamp = (ValueTimestamp) this;
                long timeNanos = valueTimestamp.getTimeNanos();
                parse = ValueTimeTimeZone.fromNanos(timeNanos, castDataProvider.currentTimeZone().getTimeZoneOffsetLocal(valueTimestamp.getDateValue(), timeNanos));
                break;
            case 21:
                ValueTimestampTimeZone valueTimestampTimeZone = (ValueTimestampTimeZone) this;
                parse = ValueTimeTimeZone.fromNanos(valueTimestampTimeZone.getTimeNanos(), valueTimestampTimeZone.getTimeZoneOffsetSeconds());
                break;
        }
        if (i != 0 && (scale = typeInfo.getScale()) < 9) {
            long nanos = parse.getNanos();
            long convertScale = DateTimeUtils.convertScale(nanos, scale, DateTimeUtils.NANOS_PER_DAY);
            if (convertScale != nanos) {
                parse = ValueTimeTimeZone.fromNanos(convertScale, parse.getTimeZoneOffsetSeconds());
            }
        }
        return parse;
    }

    private ValueTimestamp convertToTimestamp(TypeInfo typeInfo, CastDataProvider castDataProvider, int i) {
        ValueTimestamp parse;
        int scale;
        switch (getValueType()) {
            case 1:
            case 2:
            case 4:
                parse = ValueTimestamp.parse(getString().trim(), castDataProvider);
                break;
            case 3:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            default:
                throw getDataConversionError(20);
            case 17:
                return ValueTimestamp.fromDateValueAndNanos(((ValueDate) this).getDateValue(), 0L);
            case 18:
                parse = ValueTimestamp.fromDateValueAndNanos(castDataProvider.currentTimestamp().getDateValue(), ((ValueTime) this).getNanos());
                break;
            case 19:
                parse = ValueTimestamp.fromDateValueAndNanos(castDataProvider.currentTimestamp().getDateValue(), getLocalTimeNanos(castDataProvider));
                break;
            case 20:
                parse = (ValueTimestamp) this;
                break;
            case 21:
                ValueTimestampTimeZone valueTimestampTimeZone = (ValueTimestampTimeZone) this;
                long timeNanos = valueTimestampTimeZone.getTimeNanos();
                long epochSeconds = DateTimeUtils.getEpochSeconds(valueTimestampTimeZone.getDateValue(), timeNanos, valueTimestampTimeZone.getTimeZoneOffsetSeconds()) + castDataProvider.currentTimeZone().getTimeZoneOffsetUTC(r0);
                parse = ValueTimestamp.fromDateValueAndNanos(DateTimeUtils.dateValueFromLocalSeconds(epochSeconds), DateTimeUtils.nanosFromLocalSeconds(epochSeconds) + (timeNanos % DateTimeUtils.NANOS_PER_SECOND));
                break;
        }
        if (i != 0 && (scale = typeInfo.getScale()) < 9) {
            long dateValue = parse.getDateValue();
            long timeNanos2 = parse.getTimeNanos();
            long convertScale = DateTimeUtils.convertScale(timeNanos2, scale, dateValue == DateTimeUtils.MAX_DATE_VALUE ? DateTimeUtils.NANOS_PER_DAY : Long.MAX_VALUE);
            if (convertScale != timeNanos2) {
                if (convertScale >= DateTimeUtils.NANOS_PER_DAY) {
                    convertScale -= DateTimeUtils.NANOS_PER_DAY;
                    dateValue = DateTimeUtils.incrementDateValue(dateValue);
                }
                parse = ValueTimestamp.fromDateValueAndNanos(dateValue, convertScale);
            }
        }
        return parse;
    }

    private long getLocalTimeNanos(CastDataProvider castDataProvider) {
        return DateTimeUtils.normalizeNanosOfDay(((ValueTimeTimeZone) this).getNanos() + ((castDataProvider.currentTimestamp().getTimeZoneOffsetSeconds() - r0.getTimeZoneOffsetSeconds()) * DateTimeUtils.NANOS_PER_SECOND));
    }

    private ValueTimestampTimeZone convertToTimestampTimeZone(TypeInfo typeInfo, CastDataProvider castDataProvider, int i) {
        ValueTimestampTimeZone parse;
        int scale;
        switch (getValueType()) {
            case 1:
            case 2:
            case 4:
                parse = ValueTimestampTimeZone.parse(getString().trim(), castDataProvider);
                break;
            case 3:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            default:
                throw getDataConversionError(21);
            case 17:
                long dateValue = ((ValueDate) this).getDateValue();
                return ValueTimestampTimeZone.fromDateValueAndNanos(dateValue, 0L, castDataProvider.currentTimeZone().getTimeZoneOffsetLocal(dateValue, 0L));
            case 18:
                long dateValue2 = castDataProvider.currentTimestamp().getDateValue();
                long nanos = ((ValueTime) this).getNanos();
                parse = ValueTimestampTimeZone.fromDateValueAndNanos(dateValue2, nanos, castDataProvider.currentTimeZone().getTimeZoneOffsetLocal(dateValue2, nanos));
                break;
            case 19:
                ValueTimeTimeZone valueTimeTimeZone = (ValueTimeTimeZone) this;
                parse = ValueTimestampTimeZone.fromDateValueAndNanos(castDataProvider.currentTimestamp().getDateValue(), valueTimeTimeZone.getNanos(), valueTimeTimeZone.getTimeZoneOffsetSeconds());
                break;
            case 20:
                ValueTimestamp valueTimestamp = (ValueTimestamp) this;
                long dateValue3 = valueTimestamp.getDateValue();
                long timeNanos = valueTimestamp.getTimeNanos();
                parse = ValueTimestampTimeZone.fromDateValueAndNanos(dateValue3, timeNanos, castDataProvider.currentTimeZone().getTimeZoneOffsetLocal(dateValue3, timeNanos));
                break;
            case 21:
                parse = (ValueTimestampTimeZone) this;
                break;
        }
        if (i != 0 && (scale = typeInfo.getScale()) < 9) {
            long dateValue4 = parse.getDateValue();
            long timeNanos2 = parse.getTimeNanos();
            long convertScale = DateTimeUtils.convertScale(timeNanos2, scale, dateValue4 == DateTimeUtils.MAX_DATE_VALUE ? DateTimeUtils.NANOS_PER_DAY : Long.MAX_VALUE);
            if (convertScale != timeNanos2) {
                if (convertScale >= DateTimeUtils.NANOS_PER_DAY) {
                    convertScale -= DateTimeUtils.NANOS_PER_DAY;
                    dateValue4 = DateTimeUtils.incrementDateValue(dateValue4);
                }
                parse = ValueTimestampTimeZone.fromDateValueAndNanos(dateValue4, convertScale, parse.getTimeZoneOffsetSeconds());
            }
        }
        return parse;
    }

    private ValueInterval convertToIntervalYearMonth(TypeInfo typeInfo, int i, Object obj) {
        ValueInterval convertToIntervalYearMonth = convertToIntervalYearMonth(typeInfo.getValueType(), obj);
        if (i != 0 && !convertToIntervalYearMonth.checkPrecision(typeInfo.getPrecision())) {
            throw convertToIntervalYearMonth.getValueTooLongException(typeInfo, obj);
        }
        return convertToIntervalYearMonth;
    }

    private ValueInterval convertToIntervalYearMonth(int i, Object obj) {
        long convertToLong;
        switch (getValueType()) {
            case 1:
            case 2:
            case 4:
                String string = getString();
                try {
                    return (ValueInterval) IntervalUtils.parseFormattedInterval(IntervalQualifier.valueOf(i - 22), string).convertTo(i);
                } catch (Exception e) {
                    throw DbException.get(ErrorCode.INVALID_DATETIME_CONSTANT_2, e, "INTERVAL", string);
                }
            case 3:
            case 5:
            case 6:
            case 7:
            case 8:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 24:
            case 25:
            case 26:
            case 27:
            default:
                throw getDataConversionError(i);
            case 9:
            case 10:
            case 11:
                convertToLong = getInt();
                break;
            case 12:
                convertToLong = getLong();
                break;
            case 13:
            case 16:
                if (i == 28) {
                    return IntervalUtils.intervalFromAbsolute(IntervalQualifier.YEAR_TO_MONTH, getBigDecimal().multiply(BigDecimal.valueOf(12L)).setScale(0, RoundingMode.HALF_UP).toBigInteger());
                }
                convertToLong = convertToLong(getBigDecimal(), obj);
                break;
            case 14:
            case 15:
                if (i == 28) {
                    return IntervalUtils.intervalFromAbsolute(IntervalQualifier.YEAR_TO_MONTH, getBigDecimal().multiply(BigDecimal.valueOf(12L)).setScale(0, RoundingMode.HALF_UP).toBigInteger());
                }
                convertToLong = convertToLong(getDouble(), obj);
                break;
            case 22:
            case 23:
            case 28:
                return IntervalUtils.intervalFromAbsolute(IntervalQualifier.valueOf(i - 22), IntervalUtils.intervalToAbsolute((ValueInterval) this));
        }
        boolean z = false;
        if (convertToLong < 0) {
            z = true;
            convertToLong = -convertToLong;
        }
        return ValueInterval.from(IntervalQualifier.valueOf(i - 22), z, convertToLong, 0L);
    }

    private ValueInterval convertToIntervalDayTime(TypeInfo typeInfo, int i, Object obj) {
        ValueInterval convertToIntervalDayTime = convertToIntervalDayTime(typeInfo.getValueType(), obj);
        if (i != 0) {
            convertToIntervalDayTime = convertToIntervalDayTime.setPrecisionAndScale(typeInfo, obj);
        }
        return convertToIntervalDayTime;
    }

    private ValueInterval convertToIntervalDayTime(int i, Object obj) {
        long convertToLong;
        switch (getValueType()) {
            case 1:
            case 2:
            case 4:
                String string = getString();
                try {
                    return (ValueInterval) IntervalUtils.parseFormattedInterval(IntervalQualifier.valueOf(i - 22), string).convertTo(i);
                } catch (Exception e) {
                    throw DbException.get(ErrorCode.INVALID_DATETIME_CONSTANT_2, e, "INTERVAL", string);
                }
            case 3:
            case 5:
            case 6:
            case 7:
            case 8:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 28:
            default:
                throw getDataConversionError(i);
            case 9:
            case 10:
            case 11:
                convertToLong = getInt();
                break;
            case 12:
                convertToLong = getLong();
                break;
            case 13:
            case 16:
                if (i > 26) {
                    return convertToIntervalDayTime(getBigDecimal(), i);
                }
                convertToLong = convertToLong(getBigDecimal(), obj);
                break;
            case 14:
            case 15:
                if (i > 26) {
                    return convertToIntervalDayTime(getBigDecimal(), i);
                }
                convertToLong = convertToLong(getDouble(), obj);
                break;
            case 24:
            case 25:
            case 26:
            case 27:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
                return IntervalUtils.intervalFromAbsolute(IntervalQualifier.valueOf(i - 22), IntervalUtils.intervalToAbsolute((ValueInterval) this));
        }
        boolean z = false;
        if (convertToLong < 0) {
            z = true;
            convertToLong = -convertToLong;
        }
        return ValueInterval.from(IntervalQualifier.valueOf(i - 22), z, convertToLong, 0L);
    }

    private ValueInterval convertToIntervalDayTime(BigDecimal bigDecimal, int i) {
        long j;
        switch (i) {
            case 27:
                j = 1000000000;
                break;
            case 28:
            default:
                throw getDataConversionError(i);
            case 29:
            case 30:
            case 31:
                j = 86400000000000L;
                break;
            case 32:
            case 33:
                j = 3600000000000L;
                break;
            case 34:
                j = 60000000000L;
                break;
        }
        return IntervalUtils.intervalFromAbsolute(IntervalQualifier.valueOf(i - 22), bigDecimal.multiply(BigDecimal.valueOf(j)).setScale(0, RoundingMode.HALF_UP).toBigInteger());
    }

    public final ValueJavaObject convertToJavaObject(TypeInfo typeInfo, int i, Object obj) {
        ValueJavaObject noCopy;
        switch (getValueType()) {
            case 0:
                throw DbException.getInternalError();
            case 5:
            case 6:
            case 7:
                noCopy = ValueJavaObject.getNoCopy(getBytesNoCopy());
                break;
            case 35:
                noCopy = (ValueJavaObject) this;
                break;
            default:
                throw getDataConversionError(35);
        }
        if (i != 0 && noCopy.getBytesNoCopy().length > typeInfo.getPrecision()) {
            throw noCopy.getValueTooLongException(typeInfo, obj);
        }
        return noCopy;
    }

    public final ValueEnum convertToEnum(ExtTypeInfoEnum extTypeInfoEnum, CastDataProvider castDataProvider) {
        switch (getValueType()) {
            case 0:
                throw DbException.getInternalError();
            case 1:
            case 2:
            case 4:
                return extTypeInfoEnum.getValue(getString(), castDataProvider);
            case 3:
            case 5:
            case 6:
            case 7:
            case 8:
            case 14:
            case 15:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
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
            default:
                throw getDataConversionError(36);
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 16:
                return extTypeInfoEnum.getValue(getInt(), castDataProvider);
            case 36:
                ValueEnum valueEnum = (ValueEnum) this;
                if (extTypeInfoEnum.equals(valueEnum.getEnumerators())) {
                    return valueEnum;
                }
                return extTypeInfoEnum.getValue(valueEnum.getString(), castDataProvider);
        }
    }

    public final ValueGeometry convertToGeometry(ExtTypeInfoGeometry extTypeInfoGeometry) {
        ValueGeometry valueGeometry;
        Integer srid;
        switch (getValueType()) {
            case 0:
                throw DbException.getInternalError();
            case 1:
            case 2:
            case 3:
            case 4:
                valueGeometry = ValueGeometry.get(getString());
                break;
            case 5:
            case 6:
            case 7:
                valueGeometry = ValueGeometry.getFromEWKB(getBytesNoCopy());
                break;
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
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
            default:
                throw getDataConversionError(37);
            case 37:
                valueGeometry = (ValueGeometry) this;
                break;
            case 38:
                int i = 0;
                if (extTypeInfoGeometry != null && (srid = extTypeInfoGeometry.getSrid()) != null) {
                    i = srid.intValue();
                }
                try {
                    valueGeometry = ValueGeometry.get(GeoJsonUtils.geoJsonToEwkb(getBytesNoCopy(), i));
                    break;
                } catch (RuntimeException e) {
                    throw DbException.get(ErrorCode.DATA_CONVERSION_ERROR_1, getTraceSQL());
                }
                break;
        }
        if (extTypeInfoGeometry != null) {
            int type = extTypeInfoGeometry.getType();
            Integer srid2 = extTypeInfoGeometry.getSrid();
            if ((type != 0 && valueGeometry.getTypeAndDimensionSystem() != type) || (srid2 != null && valueGeometry.getSRID() != srid2.intValue())) {
                StringBuilder append = ExtTypeInfoGeometry.toSQL(new StringBuilder(), valueGeometry.getTypeAndDimensionSystem(), Integer.valueOf(valueGeometry.getSRID())).append(" -> ");
                extTypeInfoGeometry.getSQL(append, 3);
                throw DbException.get(ErrorCode.DATA_CONVERSION_ERROR_1, append.toString());
            }
        }
        return valueGeometry;
    }

    private ValueJson convertToJson(TypeInfo typeInfo, int i, Object obj) {
        ValueJson internal;
        switch (getValueType()) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 17:
            case 18:
            case 19:
            case 36:
            case 39:
                internal = ValueJson.get(getString());
                break;
            case 5:
            case 6:
            case 7:
                internal = ValueJson.fromJson(getBytesNoCopy());
                break;
            case 8:
                internal = ValueJson.get(getBoolean());
                break;
            case 9:
            case 10:
            case 11:
                internal = ValueJson.get(getInt());
                break;
            case 12:
                internal = ValueJson.get(getLong());
                break;
            case 13:
            case 14:
            case 15:
            case 16:
                internal = ValueJson.get(getBigDecimal());
                break;
            case 20:
                internal = ValueJson.get(((ValueTimestamp) this).getISOString());
                break;
            case 21:
                internal = ValueJson.get(((ValueTimestampTimeZone) this).getISOString());
                break;
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
            default:
                throw getDataConversionError(38);
            case 37:
                ValueGeometry valueGeometry = (ValueGeometry) this;
                internal = ValueJson.getInternal(GeoJsonUtils.ewkbToGeoJson(valueGeometry.getBytesNoCopy(), valueGeometry.getDimensionSystem()));
                break;
            case 38:
                internal = (ValueJson) this;
                break;
            case 40:
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byteArrayOutputStream.write(91);
                for (Value value : ((ValueArray) this).getList()) {
                    JsonConstructorUtils.jsonArrayAppend(byteArrayOutputStream, value, 0);
                }
                byteArrayOutputStream.write(93);
                internal = ValueJson.getInternal(byteArrayOutputStream.toByteArray());
                break;
        }
        if (i != 0 && internal.getBytesNoCopy().length > typeInfo.getPrecision()) {
            throw internal.getValueTooLongException(typeInfo, obj);
        }
        return internal;
    }

    public final ValueUuid convertToUuid() {
        switch (getValueType()) {
            case 0:
                throw DbException.getInternalError();
            case 1:
            case 2:
            case 4:
                return ValueUuid.get(getString());
            case 5:
            case 6:
                return ValueUuid.get(getBytesNoCopy());
            case 35:
                return JdbcUtils.deserializeUuid(getBytesNoCopy());
            case 39:
                return (ValueUuid) this;
            default:
                throw getDataConversionError(39);
        }
    }

    private ValueArray convertToArray(TypeInfo typeInfo, CastDataProvider castDataProvider, int i, Object obj) {
        Value[] valueArr;
        ValueArray valueArray;
        TypeInfo typeInfo2 = (TypeInfo) typeInfo.getExtTypeInfo();
        int valueType = getValueType();
        if (valueType == 40) {
            valueArray = (ValueArray) this;
        } else {
            switch (valueType) {
                case 3:
                    valueArr = new Value[]{ValueVarchar.get(getString())};
                    break;
                case 7:
                    valueArr = new Value[]{ValueVarbinary.get(getBytesNoCopy())};
                    break;
                default:
                    valueArr = new Value[]{this};
                    break;
            }
            valueArray = ValueArray.get(valueArr, castDataProvider);
        }
        if (typeInfo2 != null) {
            Value[] list = valueArray.getList();
            int length = list.length;
            int i2 = 0;
            while (true) {
                if (i2 < length) {
                    Value value = list[i2];
                    Value convertTo = value.convertTo(typeInfo2, castDataProvider, i, obj);
                    if (value == convertTo) {
                        i2++;
                    } else {
                        Value[] valueArr2 = new Value[length];
                        System.arraycopy(list, 0, valueArr2, 0, i2);
                        valueArr2[i2] = convertTo;
                        while (true) {
                            i2++;
                            if (i2 < length) {
                                valueArr2[i2] = list[i2].convertTo(typeInfo2, castDataProvider, i, obj);
                            } else {
                                valueArray = ValueArray.get(typeInfo2, valueArr2, castDataProvider);
                            }
                        }
                    }
                }
            }
        }
        if (i != 0) {
            Value[] list2 = valueArray.getList();
            int length2 = list2.length;
            if (i == 1) {
                int convertLongToInt = MathUtils.convertLongToInt(typeInfo.getPrecision());
                if (length2 > convertLongToInt) {
                    valueArray = ValueArray.get(valueArray.getComponentType(), (Value[]) Arrays.copyOf(list2, convertLongToInt), castDataProvider);
                }
            } else if (length2 > typeInfo.getPrecision()) {
                throw valueArray.getValueTooLongException(typeInfo, obj);
            }
        }
        return valueArray;
    }

    private Value convertToRow(TypeInfo typeInfo, CastDataProvider castDataProvider, int i, Object obj) {
        ValueRow valueRow;
        if (getValueType() == 41) {
            valueRow = (ValueRow) this;
        } else {
            valueRow = ValueRow.get(new Value[]{this});
        }
        ExtTypeInfoRow extTypeInfoRow = (ExtTypeInfoRow) typeInfo.getExtTypeInfo();
        if (extTypeInfoRow != null) {
            Value[] list = valueRow.getList();
            int length = list.length;
            Set<Map.Entry<String, TypeInfo>> fields = extTypeInfoRow.getFields();
            if (length != fields.size()) {
                throw getDataConversionError(typeInfo);
            }
            Iterator<Map.Entry<String, TypeInfo>> it = fields.iterator();
            int i2 = 0;
            while (true) {
                if (i2 >= length) {
                    break;
                }
                Value value = list[i2];
                TypeInfo value2 = it.next().getValue();
                Value convertTo = value.convertTo(value2, castDataProvider, i, obj);
                if (value == convertTo) {
                    i2++;
                } else {
                    Value[] valueArr = new Value[length];
                    System.arraycopy(list, 0, valueArr, 0, i2);
                    valueArr[i2] = convertTo;
                    while (true) {
                        i2++;
                        if (i2 >= length) {
                            break;
                        }
                        valueArr[i2] = list[i2].convertTo(value2, castDataProvider, i, obj);
                    }
                    valueRow = ValueRow.get(typeInfo, valueArr);
                }
            }
        }
        return valueRow;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final DbException getDataConversionError(int i) {
        throw DbException.get(ErrorCode.DATA_CONVERSION_ERROR_1, getTypeName(getValueType()) + " to " + getTypeName(i));
    }

    final DbException getDataConversionError(TypeInfo typeInfo) {
        throw DbException.get(ErrorCode.DATA_CONVERSION_ERROR_1, getTypeName(getValueType()) + " to " + typeInfo.getTraceSQL());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final DbException getValueTooLongException(TypeInfo typeInfo, Object obj) {
        StringBuilder sb = new StringBuilder();
        if (obj != null) {
            sb.append(obj).append(' ');
        }
        typeInfo.getSQL(sb, 3);
        return DbException.getValueTooLongException(sb.toString(), getTraceSQL(), getType().getPrecision());
    }

    public final int compareTo(Value value, CastDataProvider castDataProvider, CompareMode compareMode) {
        if (this == value) {
            return 0;
        }
        if (this == ValueNull.INSTANCE) {
            return -1;
        }
        if (value == ValueNull.INSTANCE) {
            return 1;
        }
        return compareToNotNullable(this, value, castDataProvider, compareMode);
    }

    private static int compareToNotNullable(Value value, Value value2, CastDataProvider castDataProvider, CompareMode compareMode) {
        int valueType = value.getValueType();
        int valueType2 = value2.getValueType();
        if (valueType != valueType2 || valueType == 36) {
            int higherOrderNonNull = getHigherOrderNonNull(valueType, valueType2);
            if (DataType.isNumericType(higherOrderNonNull)) {
                return compareNumeric(value, value2, valueType, valueType2, higherOrderNonNull);
            }
            if (higherOrderNonNull == 36) {
                ExtTypeInfoEnum enumeratorsForBinaryOperation = ExtTypeInfoEnum.getEnumeratorsForBinaryOperation(value, value2);
                return Integer.compare(value.convertToEnum(enumeratorsForBinaryOperation, castDataProvider).getInt(), value2.convertToEnum(enumeratorsForBinaryOperation, castDataProvider).getInt());
            }
            if (higherOrderNonNull <= 7) {
                if (higherOrderNonNull <= 3) {
                    if (valueType == 1 || valueType2 == 1) {
                        higherOrderNonNull = 1;
                    }
                } else if (higherOrderNonNull >= 5 && (valueType == 5 || valueType2 == 5)) {
                    higherOrderNonNull = 5;
                }
            }
            value = value.convertTo(higherOrderNonNull, castDataProvider);
            value2 = value2.convertTo(higherOrderNonNull, castDataProvider);
        }
        return value.compareTypeSafe(value2, compareMode, castDataProvider);
    }

    private static int compareNumeric(Value value, Value value2, int i, int i2, int i3) {
        if (DataType.isNumericType(i) && DataType.isNumericType(i2)) {
            switch (i3) {
                case 9:
                case 10:
                case 11:
                    return Integer.compare(value.getInt(), value2.getInt());
                case 12:
                    return Long.compare(value.getLong(), value2.getLong());
                case 13:
                    return value.getBigDecimal().compareTo(value2.getBigDecimal());
                case 14:
                    return Float.compare(value.getFloat(), value2.getFloat());
                case 15:
                    return Double.compare(value.getDouble(), value2.getDouble());
            }
        }
        return value.convertToDecfloat(null, 0).compareTypeSafe(value2.convertToDecfloat(null, 0), null, null);
    }

    public int compareWithNull(Value value, boolean z, CastDataProvider castDataProvider, CompareMode compareMode) {
        if (this == ValueNull.INSTANCE || value == ValueNull.INSTANCE) {
            return Integer.MIN_VALUE;
        }
        return compareToNotNullable(this, value, castDataProvider, compareMode);
    }

    public boolean containsNull() {
        return false;
    }

    public Value getValueWithFirstNull(Value value) {
        if (this != ValueNull.INSTANCE) {
            return value == ValueNull.INSTANCE ? ValueNull.INSTANCE : getValueWithFirstNullImpl(value);
        }
        if (value == ValueNull.INSTANCE) {
            return null;
        }
        return ValueNull.INSTANCE;
    }

    Value getValueWithFirstNullImpl(Value value) {
        return this;
    }

    private static byte convertToByte(long j, Object obj) {
        if (j > 127 || j < -128) {
            throw DbException.get(ErrorCode.NUMERIC_VALUE_OUT_OF_RANGE_2, Long.toString(j), getColumnName(obj));
        }
        return (byte) j;
    }

    private static short convertToShort(long j, Object obj) {
        if (j > 32767 || j < -32768) {
            throw DbException.get(ErrorCode.NUMERIC_VALUE_OUT_OF_RANGE_2, Long.toString(j), getColumnName(obj));
        }
        return (short) j;
    }

    public static int convertToInt(long j, Object obj) {
        if (j > 2147483647L || j < -2147483648L) {
            throw DbException.get(ErrorCode.NUMERIC_VALUE_OUT_OF_RANGE_2, Long.toString(j), getColumnName(obj));
        }
        return (int) j;
    }

    private static long convertToLong(double d, Object obj) {
        if (d > 9.223372036854776E18d || d < -9.223372036854776E18d) {
            throw DbException.get(ErrorCode.NUMERIC_VALUE_OUT_OF_RANGE_2, Double.toString(d), getColumnName(obj));
        }
        return Math.round(d);
    }

    private static long convertToLong(BigDecimal bigDecimal, Object obj) {
        if (bigDecimal.compareTo(MAX_LONG_DECIMAL) > 0 || bigDecimal.compareTo(MIN_LONG_DECIMAL) < 0) {
            throw DbException.get(ErrorCode.NUMERIC_VALUE_OUT_OF_RANGE_2, bigDecimal.toString(), getColumnName(obj));
        }
        return bigDecimal.setScale(0, RoundingMode.HALF_UP).longValue();
    }

    private static String getColumnName(Object obj) {
        return obj == null ? "" : obj.toString();
    }

    public String toString() {
        return getTraceSQL();
    }

    protected final DbException getUnsupportedExceptionForOperation(String str) {
        return DbException.getUnsupportedException(getTypeName(getValueType()) + ' ' + str);
    }

    public long charLength() {
        return getString().length();
    }

    public long octetLength() {
        return getBytesNoCopy().length;
    }

    public final boolean isTrue() {
        if (this != ValueNull.INSTANCE) {
            return getBoolean();
        }
        return false;
    }

    public final boolean isFalse() {
        return (this == ValueNull.INSTANCE || getBoolean()) ? false : true;
    }
}

package org.h2.value;

import ch.qos.logback.core.CoreConstants;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.h2.api.ErrorCode;
import org.h2.api.IntervalQualifier;
import org.h2.engine.Constants;
import org.h2.message.DbException;
import org.h2.util.DateTimeUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/TypeInfo.class */
public class TypeInfo extends ExtTypeInfo implements Typed {
    public static final TypeInfo TYPE_UNKNOWN = new TypeInfo(-1);
    public static final TypeInfo TYPE_NULL;
    public static final TypeInfo TYPE_CHAR;
    public static final TypeInfo TYPE_VARCHAR;
    public static final TypeInfo TYPE_VARCHAR_IGNORECASE;
    public static final TypeInfo TYPE_CLOB;
    public static final TypeInfo TYPE_BINARY;
    public static final TypeInfo TYPE_VARBINARY;
    public static final TypeInfo TYPE_BLOB;
    public static final TypeInfo TYPE_BOOLEAN;
    public static final TypeInfo TYPE_TINYINT;
    public static final TypeInfo TYPE_SMALLINT;
    public static final TypeInfo TYPE_INTEGER;
    public static final TypeInfo TYPE_BIGINT;
    public static final TypeInfo TYPE_NUMERIC_SCALE_0;
    public static final TypeInfo TYPE_NUMERIC_BIGINT;
    public static final TypeInfo TYPE_NUMERIC_FLOATING_POINT;
    public static final TypeInfo TYPE_REAL;
    public static final TypeInfo TYPE_DOUBLE;
    public static final TypeInfo TYPE_DECFLOAT;
    public static final TypeInfo TYPE_DECFLOAT_BIGINT;
    public static final TypeInfo TYPE_DATE;
    public static final TypeInfo TYPE_TIME;
    public static final TypeInfo TYPE_TIME_TZ;
    public static final TypeInfo TYPE_TIMESTAMP;
    public static final TypeInfo TYPE_TIMESTAMP_TZ;
    public static final TypeInfo TYPE_INTERVAL_DAY;
    public static final TypeInfo TYPE_INTERVAL_YEAR_TO_MONTH;
    public static final TypeInfo TYPE_INTERVAL_DAY_TO_SECOND;
    public static final TypeInfo TYPE_INTERVAL_HOUR_TO_SECOND;
    public static final TypeInfo TYPE_JAVA_OBJECT;
    public static final TypeInfo TYPE_ENUM_UNDEFINED;
    public static final TypeInfo TYPE_GEOMETRY;
    public static final TypeInfo TYPE_JSON;
    public static final TypeInfo TYPE_UUID;
    public static final TypeInfo TYPE_ARRAY_UNKNOWN;
    public static final TypeInfo TYPE_ROW_EMPTY;
    private static final TypeInfo[] TYPE_INFOS_BY_VALUE_TYPE;
    private final int valueType;
    private final long precision;
    private final int scale;
    private final ExtTypeInfo extTypeInfo;

    static {
        TypeInfo[] typeInfoArr = new TypeInfo[42];
        TypeInfo typeInfo = new TypeInfo(0);
        TYPE_NULL = typeInfo;
        typeInfoArr[0] = typeInfo;
        TypeInfo typeInfo2 = new TypeInfo(1, -1L);
        TYPE_CHAR = typeInfo2;
        typeInfoArr[1] = typeInfo2;
        TypeInfo typeInfo3 = new TypeInfo(2);
        TYPE_VARCHAR = typeInfo3;
        typeInfoArr[2] = typeInfo3;
        TypeInfo typeInfo4 = new TypeInfo(3);
        TYPE_CLOB = typeInfo4;
        typeInfoArr[3] = typeInfo4;
        TypeInfo typeInfo5 = new TypeInfo(4);
        TYPE_VARCHAR_IGNORECASE = typeInfo5;
        typeInfoArr[4] = typeInfo5;
        TypeInfo typeInfo6 = new TypeInfo(5, -1L);
        TYPE_BINARY = typeInfo6;
        typeInfoArr[5] = typeInfo6;
        TypeInfo typeInfo7 = new TypeInfo(6);
        TYPE_VARBINARY = typeInfo7;
        typeInfoArr[6] = typeInfo7;
        TypeInfo typeInfo8 = new TypeInfo(7);
        TYPE_BLOB = typeInfo8;
        typeInfoArr[7] = typeInfo8;
        TypeInfo typeInfo9 = new TypeInfo(8);
        TYPE_BOOLEAN = typeInfo9;
        typeInfoArr[8] = typeInfo9;
        TypeInfo typeInfo10 = new TypeInfo(9);
        TYPE_TINYINT = typeInfo10;
        typeInfoArr[9] = typeInfo10;
        TypeInfo typeInfo11 = new TypeInfo(10);
        TYPE_SMALLINT = typeInfo11;
        typeInfoArr[10] = typeInfo11;
        TypeInfo typeInfo12 = new TypeInfo(11);
        TYPE_INTEGER = typeInfo12;
        typeInfoArr[11] = typeInfo12;
        TypeInfo typeInfo13 = new TypeInfo(12);
        TYPE_BIGINT = typeInfo13;
        typeInfoArr[12] = typeInfo13;
        TYPE_NUMERIC_SCALE_0 = new TypeInfo(13, 100000L, 0, null);
        TYPE_NUMERIC_BIGINT = new TypeInfo(13, 19L, 0, null);
        TypeInfo typeInfo14 = new TypeInfo(13, 100000L, ErrorCode.GENERAL_ERROR_1, null);
        TYPE_NUMERIC_FLOATING_POINT = typeInfo14;
        typeInfoArr[13] = typeInfo14;
        TypeInfo typeInfo15 = new TypeInfo(14);
        TYPE_REAL = typeInfo15;
        typeInfoArr[14] = typeInfo15;
        TypeInfo typeInfo16 = new TypeInfo(15);
        TYPE_DOUBLE = typeInfo16;
        typeInfoArr[15] = typeInfo16;
        TypeInfo typeInfo17 = new TypeInfo(16);
        TYPE_DECFLOAT = typeInfo17;
        typeInfoArr[16] = typeInfo17;
        TYPE_DECFLOAT_BIGINT = new TypeInfo(16, 19L);
        TypeInfo typeInfo18 = new TypeInfo(17);
        TYPE_DATE = typeInfo18;
        typeInfoArr[17] = typeInfo18;
        TypeInfo typeInfo19 = new TypeInfo(18, 9);
        TYPE_TIME = typeInfo19;
        typeInfoArr[18] = typeInfo19;
        TypeInfo typeInfo20 = new TypeInfo(19, 9);
        TYPE_TIME_TZ = typeInfo20;
        typeInfoArr[19] = typeInfo20;
        TypeInfo typeInfo21 = new TypeInfo(20, 9);
        TYPE_TIMESTAMP = typeInfo21;
        typeInfoArr[20] = typeInfo21;
        TypeInfo typeInfo22 = new TypeInfo(21, 9);
        TYPE_TIMESTAMP_TZ = typeInfo22;
        typeInfoArr[21] = typeInfo22;
        for (int i = 22; i <= 34; i++) {
            typeInfoArr[i] = new TypeInfo(i, 18L, IntervalQualifier.valueOf(i - 22).hasSeconds() ? 9 : -1, null);
        }
        TYPE_INTERVAL_DAY = typeInfoArr[24];
        TYPE_INTERVAL_YEAR_TO_MONTH = typeInfoArr[28];
        TYPE_INTERVAL_DAY_TO_SECOND = typeInfoArr[31];
        TYPE_INTERVAL_HOUR_TO_SECOND = typeInfoArr[33];
        TypeInfo typeInfo23 = new TypeInfo(35);
        TYPE_JAVA_OBJECT = typeInfo23;
        typeInfoArr[35] = typeInfo23;
        TypeInfo typeInfo24 = new TypeInfo(36);
        TYPE_ENUM_UNDEFINED = typeInfo24;
        typeInfoArr[36] = typeInfo24;
        TypeInfo typeInfo25 = new TypeInfo(37);
        TYPE_GEOMETRY = typeInfo25;
        typeInfoArr[37] = typeInfo25;
        TypeInfo typeInfo26 = new TypeInfo(38);
        TYPE_JSON = typeInfo26;
        typeInfoArr[38] = typeInfo26;
        TypeInfo typeInfo27 = new TypeInfo(39);
        TYPE_UUID = typeInfo27;
        typeInfoArr[39] = typeInfo27;
        TypeInfo typeInfo28 = new TypeInfo(40);
        TYPE_ARRAY_UNKNOWN = typeInfo28;
        typeInfoArr[40] = typeInfo28;
        TypeInfo typeInfo29 = new TypeInfo(41, -1L, -1, new ExtTypeInfoRow((LinkedHashMap<String, TypeInfo>) new LinkedHashMap()));
        TYPE_ROW_EMPTY = typeInfo29;
        typeInfoArr[41] = typeInfo29;
        TYPE_INFOS_BY_VALUE_TYPE = typeInfoArr;
    }

    public static TypeInfo getTypeInfo(int i) {
        TypeInfo typeInfo;
        if (i == -1) {
            throw DbException.get(ErrorCode.UNKNOWN_DATA_TYPE_1, CoreConstants.NA);
        }
        if (i >= 0 && i < 42 && (typeInfo = TYPE_INFOS_BY_VALUE_TYPE[i]) != null) {
            return typeInfo;
        }
        return TYPE_NULL;
    }

    public static TypeInfo getTypeInfo(int i, long j, int i2, ExtTypeInfo extTypeInfo) {
        switch (i) {
            case -1:
                return TYPE_UNKNOWN;
            case 0:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 17:
            case 39:
                return TYPE_INFOS_BY_VALUE_TYPE[i];
            case 1:
                if (j < 1) {
                    return TYPE_CHAR;
                }
                if (j > DateTimeUtils.NANOS_PER_SECOND) {
                    j = 1000000000;
                }
                return new TypeInfo(1, j);
            case 2:
                if (j < 1 || j >= DateTimeUtils.NANOS_PER_SECOND) {
                    if (j != 0) {
                        return TYPE_VARCHAR;
                    }
                    j = 1;
                }
                return new TypeInfo(2, j);
            case 3:
                if (j < 1) {
                    return TYPE_CLOB;
                }
                return new TypeInfo(3, j);
            case 4:
                if (j < 1 || j >= DateTimeUtils.NANOS_PER_SECOND) {
                    if (j != 0) {
                        return TYPE_VARCHAR_IGNORECASE;
                    }
                    j = 1;
                }
                return new TypeInfo(4, j);
            case 5:
                if (j < 1) {
                    return TYPE_BINARY;
                }
                if (j > DateTimeUtils.NANOS_PER_SECOND) {
                    j = 1000000000;
                }
                return new TypeInfo(5, j);
            case 6:
                if (j < 1 || j >= DateTimeUtils.NANOS_PER_SECOND) {
                    if (j != 0) {
                        return TYPE_VARBINARY;
                    }
                    j = 1;
                }
                return new TypeInfo(6, j);
            case 7:
                if (j < 1) {
                    return TYPE_BLOB;
                }
                return new TypeInfo(7, j);
            case 13:
                if (j < 1) {
                    j = -1;
                } else if (j > 100000) {
                    j = 100000;
                }
                if (i2 < 0) {
                    i2 = -1;
                } else if (i2 > 100000) {
                    i2 = 100000;
                }
                return new TypeInfo(13, j, i2, extTypeInfo instanceof ExtTypeInfoNumeric ? extTypeInfo : null);
            case 14:
                if (j >= 1 && j <= 24) {
                    return new TypeInfo(14, j, -1, extTypeInfo);
                }
                return TYPE_REAL;
            case 15:
                if (j == 0 || (j >= 25 && j <= 53)) {
                    return new TypeInfo(15, j, -1, extTypeInfo);
                }
                return TYPE_DOUBLE;
            case 16:
                if (j < 1) {
                    j = -1;
                } else if (j >= 100000) {
                    return TYPE_DECFLOAT;
                }
                return new TypeInfo(16, j, -1, null);
            case 18:
                if (i2 < 0) {
                    i2 = -1;
                } else if (i2 >= 9) {
                    return TYPE_TIME;
                }
                return new TypeInfo(18, i2);
            case 19:
                if (i2 < 0) {
                    i2 = -1;
                } else if (i2 >= 9) {
                    return TYPE_TIME_TZ;
                }
                return new TypeInfo(19, i2);
            case 20:
                if (i2 < 0) {
                    i2 = -1;
                } else if (i2 >= 9) {
                    return TYPE_TIMESTAMP;
                }
                return new TypeInfo(20, i2);
            case 21:
                if (i2 < 0) {
                    i2 = -1;
                } else if (i2 >= 9) {
                    return TYPE_TIMESTAMP_TZ;
                }
                return new TypeInfo(21, i2);
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 28:
            case 29:
            case 30:
            case 32:
                if (j < 1) {
                    j = -1;
                } else if (j > 18) {
                    j = 18;
                }
                return new TypeInfo(i, j);
            case 27:
            case 31:
            case 33:
            case 34:
                if (j < 1) {
                    j = -1;
                } else if (j > 18) {
                    j = 18;
                }
                if (i2 < 0) {
                    i2 = -1;
                } else if (i2 > 9) {
                    i2 = 9;
                }
                return new TypeInfo(i, j, i2, null);
            case 35:
                if (j < 1) {
                    return TYPE_JAVA_OBJECT;
                }
                if (j > DateTimeUtils.NANOS_PER_SECOND) {
                    j = 1000000000;
                }
                return new TypeInfo(35, j);
            case 36:
                if (extTypeInfo instanceof ExtTypeInfoEnum) {
                    return ((ExtTypeInfoEnum) extTypeInfo).getType();
                }
                return TYPE_ENUM_UNDEFINED;
            case 37:
                if (extTypeInfo instanceof ExtTypeInfoGeometry) {
                    return new TypeInfo(37, -1L, -1, extTypeInfo);
                }
                return TYPE_GEOMETRY;
            case 38:
                if (j < 1) {
                    return TYPE_JSON;
                }
                if (j > DateTimeUtils.NANOS_PER_SECOND) {
                    j = 1000000000;
                }
                return new TypeInfo(38, j);
            case 40:
                if (!(extTypeInfo instanceof TypeInfo)) {
                    throw new IllegalArgumentException();
                }
                if (j < 0 || j >= 65536) {
                    j = -1;
                }
                return new TypeInfo(40, j, -1, extTypeInfo);
            case 41:
                if (!(extTypeInfo instanceof ExtTypeInfoRow)) {
                    throw new IllegalArgumentException();
                }
                return new TypeInfo(41, -1L, -1, extTypeInfo);
            default:
                return TYPE_NULL;
        }
    }

    public static TypeInfo getHigherType(Typed[] typedArr) {
        TypeInfo type;
        int length = typedArr.length;
        if (length == 0) {
            type = TYPE_NULL;
        } else {
            type = typedArr[0].getType();
            boolean z = false;
            boolean z2 = false;
            switch (type.getValueType()) {
                case -1:
                    z = true;
                    break;
                case 0:
                    z2 = true;
                    break;
            }
            for (int i = 1; i < length; i++) {
                TypeInfo type2 = typedArr[i].getType();
                switch (type2.getValueType()) {
                    case -1:
                        z = true;
                        break;
                    case 0:
                        z2 = true;
                        break;
                    default:
                        type = getHigherType(type, type2);
                        break;
                }
            }
            if (type.getValueType() <= 0 && z) {
                throw DbException.get(ErrorCode.UNKNOWN_DATA_TYPE_1, z2 ? "NULL, ?" : CoreConstants.NA);
            }
        }
        return type;
    }

    public static TypeInfo getHigherType(TypeInfo typeInfo, TypeInfo typeInfo2) {
        int higherOrderKnown;
        long max;
        int i;
        int valueType = typeInfo.getValueType();
        int valueType2 = typeInfo2.getValueType();
        if (valueType == valueType2) {
            if (valueType == -1) {
                throw DbException.get(ErrorCode.UNKNOWN_DATA_TYPE_1, "?, ?");
            }
            higherOrderKnown = valueType;
        } else {
            if (valueType < valueType2) {
                valueType = valueType2;
                valueType2 = valueType;
                typeInfo = typeInfo2;
                typeInfo2 = typeInfo;
            }
            if (valueType == -1) {
                if (valueType2 == 0) {
                    throw DbException.get(ErrorCode.UNKNOWN_DATA_TYPE_1, "?, NULL");
                }
                return typeInfo2;
            }
            if (valueType2 == -1) {
                if (valueType == 0) {
                    throw DbException.get(ErrorCode.UNKNOWN_DATA_TYPE_1, "NULL, ?");
                }
                return typeInfo;
            }
            if (valueType2 == 0) {
                return typeInfo;
            }
            higherOrderKnown = Value.getHigherOrderKnown(valueType, valueType2);
        }
        switch (higherOrderKnown) {
            case 13:
                TypeInfo numericType = typeInfo.toNumericType();
                TypeInfo numericType2 = typeInfo2.toNumericType();
                long precision = numericType.getPrecision();
                long precision2 = numericType2.getPrecision();
                int scale = numericType.getScale();
                int scale2 = numericType2.getScale();
                if (scale < scale2) {
                    precision += scale2 - scale;
                    i = scale2;
                } else {
                    precision2 += scale - scale2;
                    i = scale;
                }
                return getTypeInfo(13, Math.max(precision, precision2), i, null);
            case 14:
            case 15:
                max = -1;
                break;
            case 37:
                return getHigherGeometry(typeInfo, typeInfo2);
            case 40:
                return getHigherArray(typeInfo, typeInfo2, dimensions(typeInfo), dimensions(typeInfo2));
            case 41:
                return getHigherRow(typeInfo, typeInfo2);
            default:
                max = Math.max(typeInfo.getPrecision(), typeInfo2.getPrecision());
                break;
        }
        ExtTypeInfo extTypeInfo = typeInfo.extTypeInfo;
        return getTypeInfo(higherOrderKnown, max, Math.max(typeInfo.getScale(), typeInfo2.getScale()), (higherOrderKnown != valueType || extTypeInfo == null) ? higherOrderKnown == valueType2 ? typeInfo2.extTypeInfo : null : extTypeInfo);
    }

    private static TypeInfo getHigherGeometry(TypeInfo typeInfo, TypeInfo typeInfo2) {
        ExtTypeInfo extTypeInfo = typeInfo.getExtTypeInfo();
        ExtTypeInfo extTypeInfo2 = typeInfo2.getExtTypeInfo();
        if (extTypeInfo instanceof ExtTypeInfoGeometry) {
            if (!(extTypeInfo2 instanceof ExtTypeInfoGeometry)) {
                return typeInfo2.getValueType() == 37 ? TYPE_GEOMETRY : typeInfo;
            }
            ExtTypeInfoGeometry extTypeInfoGeometry = (ExtTypeInfoGeometry) extTypeInfo;
            ExtTypeInfoGeometry extTypeInfoGeometry2 = (ExtTypeInfoGeometry) extTypeInfo2;
            int type = extTypeInfoGeometry.getType();
            Integer srid = extTypeInfoGeometry.getSrid();
            int type2 = extTypeInfoGeometry2.getType();
            Integer srid2 = extTypeInfoGeometry2.getSrid();
            if (Objects.equals(srid, srid2)) {
                if (type == type2) {
                    return typeInfo;
                }
                if (srid == null) {
                    return TYPE_GEOMETRY;
                }
                type = 0;
            } else if (srid == null || srid2 == null) {
                if (type == 0 || type != type2) {
                    return TYPE_GEOMETRY;
                }
                srid = null;
            } else {
                throw DbException.get(ErrorCode.TYPES_ARE_NOT_COMPARABLE_2, typeInfo.getTraceSQL(), typeInfo2.getTraceSQL());
            }
            return new TypeInfo(37, -1L, -1, new ExtTypeInfoGeometry(type, srid));
        }
        if (extTypeInfo2 instanceof ExtTypeInfoGeometry) {
            return typeInfo.getValueType() == 37 ? TYPE_GEOMETRY : typeInfo2;
        }
        return TYPE_GEOMETRY;
    }

    private static int dimensions(TypeInfo typeInfo) {
        int i = 0;
        while (typeInfo.getValueType() == 40) {
            typeInfo = (TypeInfo) typeInfo.extTypeInfo;
            i++;
        }
        return i;
    }

    private static TypeInfo getHigherArray(TypeInfo typeInfo, TypeInfo typeInfo2, int i, int i2) {
        long max;
        if (i > i2) {
            i--;
            max = Math.max(typeInfo.getPrecision(), 1L);
            typeInfo = (TypeInfo) typeInfo.extTypeInfo;
        } else if (i < i2) {
            i2--;
            max = Math.max(1L, typeInfo2.getPrecision());
            typeInfo2 = (TypeInfo) typeInfo2.extTypeInfo;
        } else if (i > 0) {
            i--;
            i2--;
            max = Math.max(typeInfo.getPrecision(), typeInfo2.getPrecision());
            typeInfo = (TypeInfo) typeInfo.extTypeInfo;
            typeInfo2 = (TypeInfo) typeInfo2.extTypeInfo;
        } else {
            return getHigherType(typeInfo, typeInfo2);
        }
        return getTypeInfo(40, max, 0, getHigherArray(typeInfo, typeInfo2, i, i2));
    }

    private static TypeInfo getHigherRow(TypeInfo typeInfo, TypeInfo typeInfo2) {
        if (typeInfo.getValueType() != 41) {
            typeInfo = typeToRow(typeInfo);
        }
        if (typeInfo2.getValueType() != 41) {
            typeInfo2 = typeToRow(typeInfo2);
        }
        ExtTypeInfoRow extTypeInfoRow = (ExtTypeInfoRow) typeInfo.getExtTypeInfo();
        ExtTypeInfoRow extTypeInfoRow2 = (ExtTypeInfoRow) typeInfo2.getExtTypeInfo();
        if (extTypeInfoRow.equals(extTypeInfoRow2)) {
            return typeInfo;
        }
        Set<Map.Entry<String, TypeInfo>> fields = extTypeInfoRow.getFields();
        Set<Map.Entry<String, TypeInfo>> fields2 = extTypeInfoRow2.getFields();
        int size = fields.size();
        if (fields2.size() != size) {
            throw DbException.get(ErrorCode.COLUMN_COUNT_DOES_NOT_MATCH);
        }
        LinkedHashMap linkedHashMap = new LinkedHashMap((int) Math.ceil(size / 0.75d));
        Iterator<Map.Entry<String, TypeInfo>> it = fields2.iterator();
        for (Map.Entry<String, TypeInfo> entry : fields) {
            linkedHashMap.put(entry.getKey(), getHigherType(entry.getValue(), it.next().getValue()));
        }
        return getTypeInfo(41, 0L, 0, new ExtTypeInfoRow((LinkedHashMap<String, TypeInfo>) linkedHashMap));
    }

    private static TypeInfo typeToRow(TypeInfo typeInfo) {
        LinkedHashMap linkedHashMap = new LinkedHashMap(2);
        linkedHashMap.put("C1", typeInfo);
        return getTypeInfo(41, 0L, 0, new ExtTypeInfoRow((LinkedHashMap<String, TypeInfo>) linkedHashMap));
    }

    public static boolean areSameTypes(TypeInfo typeInfo, TypeInfo typeInfo2) {
        while (true) {
            int valueType = typeInfo.getValueType();
            if (valueType != typeInfo2.getValueType()) {
                return false;
            }
            ExtTypeInfo extTypeInfo = typeInfo.getExtTypeInfo();
            ExtTypeInfo extTypeInfo2 = typeInfo2.getExtTypeInfo();
            if (valueType != 40) {
                return Objects.equals(extTypeInfo, extTypeInfo2);
            }
            typeInfo = (TypeInfo) extTypeInfo;
            typeInfo2 = (TypeInfo) extTypeInfo2;
        }
    }

    public static void checkComparable(TypeInfo typeInfo, TypeInfo typeInfo2) {
        if (!areComparable(typeInfo, typeInfo2)) {
            throw DbException.get(ErrorCode.TYPES_ARE_NOT_COMPARABLE_2, typeInfo.getTraceSQL(), typeInfo2.getTraceSQL());
        }
    }

    private static boolean areComparable(TypeInfo typeInfo, TypeInfo typeInfo2) {
        TypeInfo unwrapRow = typeInfo.unwrapRow();
        TypeInfo typeInfo3 = unwrapRow;
        int valueType = unwrapRow.getValueType();
        TypeInfo unwrapRow2 = typeInfo2.unwrapRow();
        TypeInfo typeInfo4 = unwrapRow2;
        int valueType2 = unwrapRow2.getValueType();
        if (valueType > valueType2) {
            valueType = valueType2;
            valueType2 = valueType;
            typeInfo3 = typeInfo4;
            typeInfo4 = typeInfo3;
        }
        if (valueType <= 0) {
            return true;
        }
        if (valueType == valueType2) {
            switch (valueType) {
                case 40:
                    return areComparable((TypeInfo) typeInfo3.getExtTypeInfo(), (TypeInfo) typeInfo4.getExtTypeInfo());
                case 41:
                    Set<Map.Entry<String, TypeInfo>> fields = ((ExtTypeInfoRow) typeInfo3.getExtTypeInfo()).getFields();
                    Set<Map.Entry<String, TypeInfo>> fields2 = ((ExtTypeInfoRow) typeInfo4.getExtTypeInfo()).getFields();
                    if (fields2.size() != fields.size()) {
                        return false;
                    }
                    Iterator<Map.Entry<String, TypeInfo>> it = fields.iterator();
                    Iterator<Map.Entry<String, TypeInfo>> it2 = fields2.iterator();
                    while (it.hasNext()) {
                        if (!areComparable(it.next().getValue(), it2.next().getValue())) {
                            return false;
                        }
                    }
                    return true;
                default:
                    return true;
            }
        }
        byte b = Value.GROUPS[valueType];
        byte b2 = Value.GROUPS[valueType2];
        if (b == b2) {
            switch (b) {
                case 5:
                    return (valueType == 17 && (valueType2 == 18 || valueType2 == 19)) ? false : true;
                case 6:
                case 7:
                default:
                    return true;
                case 8:
                case 9:
                    return false;
            }
        }
        switch (b) {
            case 1:
                switch (b2) {
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                        return true;
                    case 8:
                        switch (valueType2) {
                            case 36:
                            case 37:
                            case 38:
                            case 39:
                                return true;
                            default:
                                return false;
                        }
                    default:
                        return false;
                }
            case 2:
                switch (valueType2) {
                    case 35:
                    case 37:
                    case 38:
                    case 39:
                        return true;
                    case 36:
                    default:
                        return false;
                }
            default:
                return false;
        }
    }

    public static boolean haveSameOrdering(TypeInfo typeInfo, TypeInfo typeInfo2) {
        TypeInfo unwrapRow = typeInfo.unwrapRow();
        TypeInfo typeInfo3 = unwrapRow;
        int valueType = unwrapRow.getValueType();
        TypeInfo unwrapRow2 = typeInfo2.unwrapRow();
        TypeInfo typeInfo4 = unwrapRow2;
        int valueType2 = unwrapRow2.getValueType();
        if (valueType > valueType2) {
            valueType = valueType2;
            valueType2 = valueType;
            typeInfo3 = typeInfo4;
            typeInfo4 = typeInfo3;
        }
        if (valueType <= 0) {
            return true;
        }
        if (valueType == valueType2) {
            switch (valueType) {
                case 40:
                    return haveSameOrdering((TypeInfo) typeInfo3.getExtTypeInfo(), (TypeInfo) typeInfo4.getExtTypeInfo());
                case 41:
                    Set<Map.Entry<String, TypeInfo>> fields = ((ExtTypeInfoRow) typeInfo3.getExtTypeInfo()).getFields();
                    Set<Map.Entry<String, TypeInfo>> fields2 = ((ExtTypeInfoRow) typeInfo4.getExtTypeInfo()).getFields();
                    if (fields2.size() != fields.size()) {
                        return false;
                    }
                    Iterator<Map.Entry<String, TypeInfo>> it = fields.iterator();
                    Iterator<Map.Entry<String, TypeInfo>> it2 = fields2.iterator();
                    while (it.hasNext()) {
                        if (!haveSameOrdering(it.next().getValue(), it2.next().getValue())) {
                            return false;
                        }
                    }
                    return true;
                default:
                    return true;
            }
        }
        byte b = Value.GROUPS[valueType];
        if (b == Value.GROUPS[valueType2]) {
            switch (b) {
                case 1:
                    return (valueType == 4) == (valueType2 == 4);
                case 2:
                case 3:
                case 4:
                case 6:
                case 7:
                default:
                    return true;
                case 5:
                    switch (valueType) {
                        case 17:
                            return valueType2 == 20 || valueType2 == 21;
                        case 18:
                        case 19:
                            return valueType2 == 18 || valueType2 == 19;
                        default:
                            return true;
                    }
                case 8:
                case 9:
                    return false;
            }
        }
        if (b == 2) {
            switch (valueType2) {
                case 35:
                case 37:
                case 38:
                case 39:
                    return true;
                case 36:
                default:
                    return false;
            }
        }
        return false;
    }

    private TypeInfo(int i) {
        this.valueType = i;
        this.precision = -1L;
        this.scale = -1;
        this.extTypeInfo = null;
    }

    private TypeInfo(int i, long j) {
        this.valueType = i;
        this.precision = j;
        this.scale = -1;
        this.extTypeInfo = null;
    }

    private TypeInfo(int i, int i2) {
        this.valueType = i;
        this.precision = -1L;
        this.scale = i2;
        this.extTypeInfo = null;
    }

    public TypeInfo(int i, long j, int i2, ExtTypeInfo extTypeInfo) {
        this.valueType = i;
        this.precision = j;
        this.scale = i2;
        this.extTypeInfo = extTypeInfo;
    }

    @Override // org.h2.value.Typed
    public TypeInfo getType() {
        return this;
    }

    public int getValueType() {
        return this.valueType;
    }

    public long getPrecision() {
        switch (this.valueType) {
            case -1:
                return -1L;
            case 0:
                return 1L;
            case 1:
            case 5:
                if (this.precision >= 0) {
                    return this.precision;
                }
                return 1L;
            case 2:
            case 4:
            case 6:
            case 35:
            case 36:
            case 37:
            case 38:
                return this.precision >= 0 ? this.precision : DateTimeUtils.NANOS_PER_SECOND;
            case 3:
            case 7:
                if (this.precision >= 0) {
                    return this.precision;
                }
                return Long.MAX_VALUE;
            case 8:
                return 1L;
            case 9:
                return 8L;
            case 10:
                return 16L;
            case 11:
                return 32L;
            case 12:
                return 64L;
            case 13:
                if (this.precision >= 0) {
                    return this.precision;
                }
                return 100000L;
            case 14:
                return 24L;
            case 15:
                return 53L;
            case 16:
                if (this.precision >= 0) {
                    return this.precision;
                }
                return 100000L;
            case 17:
                return 10L;
            case 18:
                if ((this.scale >= 0 ? this.scale : 0) == 0) {
                    return 8L;
                }
                return 9 + r6;
            case 19:
                if ((this.scale >= 0 ? this.scale : 0) == 0) {
                    return 14L;
                }
                return 15 + r6;
            case 20:
                if ((this.scale >= 0 ? this.scale : 6) == 0) {
                    return 19L;
                }
                return 20 + r6;
            case 21:
                if ((this.scale >= 0 ? this.scale : 6) == 0) {
                    return 25L;
                }
                return 26 + r6;
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
                if (this.precision >= 0) {
                    return this.precision;
                }
                return 2L;
            case 39:
                return 16L;
            case 40:
                if (this.precision >= 0) {
                    return this.precision;
                }
                return 65536L;
            case 41:
                return 2147483647L;
            default:
                return this.precision;
        }
    }

    public long getDeclaredPrecision() {
        return this.precision;
    }

    public int getScale() {
        switch (this.valueType) {
            case -1:
                return -1;
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 14:
            case 15:
            case 16:
            case 17:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 28:
            case 29:
            case 30:
            case 32:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
                return 0;
            case 13:
                if (this.scale >= 0) {
                    return this.scale;
                }
                return 0;
            case 18:
            case 19:
                if (this.scale >= 0) {
                    return this.scale;
                }
                return 0;
            case 20:
            case 21:
                if (this.scale >= 0) {
                    return this.scale;
                }
                return 6;
            case 27:
            case 31:
            case 33:
            case 34:
                if (this.scale >= 0) {
                    return this.scale;
                }
                return 6;
            default:
                return this.scale;
        }
    }

    public int getDeclaredScale() {
        return this.scale;
    }

    public int getDisplaySize() {
        switch (this.valueType) {
            case -1:
            default:
                return -1;
            case 0:
                return 4;
            case 1:
                if (this.precision >= 0) {
                    return (int) this.precision;
                }
                return 1;
            case 2:
            case 4:
            case 38:
                return this.precision >= 0 ? (int) this.precision : Constants.MAX_STRING_LENGTH;
            case 3:
                if (this.precision < 0 || this.precision > 2147483647L) {
                    return Integer.MAX_VALUE;
                }
                return (int) this.precision;
            case 5:
                if (this.precision >= 0) {
                    return ((int) this.precision) * 2;
                }
                return 2;
            case 6:
            case 35:
                if (this.precision >= 0) {
                    return ((int) this.precision) * 2;
                }
                return 2000000000;
            case 7:
                if (this.precision < 0 || this.precision > 1073741823) {
                    return Integer.MAX_VALUE;
                }
                return ((int) this.precision) * 2;
            case 8:
                return 5;
            case 9:
                return 4;
            case 10:
                return 6;
            case 11:
                return 11;
            case 12:
                return 20;
            case 13:
                if (this.precision >= 0) {
                    return ((int) this.precision) + 2;
                }
                return 100002;
            case 14:
                return 15;
            case 15:
                return 24;
            case 16:
                if (this.precision >= 0) {
                    return ((int) this.precision) + 12;
                }
                return 100012;
            case 17:
                return 10;
            case 18:
                int i = this.scale >= 0 ? this.scale : 0;
                if (i == 0) {
                    return 8;
                }
                return 9 + i;
            case 19:
                int i2 = this.scale >= 0 ? this.scale : 0;
                if (i2 == 0) {
                    return 14;
                }
                return 15 + i2;
            case 20:
                int i3 = this.scale >= 0 ? this.scale : 6;
                if (i3 == 0) {
                    return 19;
                }
                return 20 + i3;
            case 21:
                int i4 = this.scale >= 0 ? this.scale : 6;
                if (i4 == 0) {
                    return 25;
                }
                return 26 + i4;
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
                return ValueInterval.getDisplaySize(this.valueType, this.precision >= 0 ? (int) this.precision : 2, this.scale >= 0 ? this.scale : 6);
            case 36:
                return this.extTypeInfo != null ? (int) this.precision : Constants.MAX_STRING_LENGTH;
            case 37:
            case 40:
            case 41:
                return Integer.MAX_VALUE;
            case 39:
                return 36;
        }
    }

    public ExtTypeInfo getExtTypeInfo() {
        return this.extTypeInfo;
    }

    @Override // org.h2.util.HasSQL
    public StringBuilder getSQL(StringBuilder sb, int i) {
        switch (this.valueType) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 35:
            case 38:
                sb.append(Value.getTypeName(this.valueType));
                if (this.precision >= 0) {
                    sb.append('(').append(this.precision).append(')');
                    break;
                }
                break;
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 17:
            case 39:
            default:
                sb.append(Value.getTypeName(this.valueType));
                break;
            case 13:
                if (this.extTypeInfo != null) {
                    this.extTypeInfo.getSQL(sb, i);
                } else {
                    sb.append("NUMERIC");
                }
                boolean z = this.precision >= 0;
                boolean z2 = this.scale >= 0;
                if (z || z2) {
                    sb.append('(').append(z ? this.precision : 100000L);
                    if (z2) {
                        sb.append(", ").append(this.scale);
                    }
                    sb.append(')');
                    break;
                }
                break;
            case 14:
            case 15:
                if (this.precision < 0) {
                    sb.append(Value.getTypeName(this.valueType));
                    break;
                } else {
                    sb.append("FLOAT");
                    if (this.precision > 0) {
                        sb.append('(').append(this.precision).append(')');
                        break;
                    }
                }
                break;
            case 16:
                sb.append("DECFLOAT");
                if (this.precision >= 0) {
                    sb.append('(').append(this.precision).append(')');
                    break;
                }
                break;
            case 18:
            case 19:
                sb.append("TIME");
                if (this.scale >= 0) {
                    sb.append('(').append(this.scale).append(')');
                }
                if (this.valueType == 19) {
                    sb.append(" WITH TIME ZONE");
                    break;
                }
                break;
            case 20:
            case 21:
                sb.append("TIMESTAMP");
                if (this.scale >= 0) {
                    sb.append('(').append(this.scale).append(')');
                }
                if (this.valueType == 21) {
                    sb.append(" WITH TIME ZONE");
                    break;
                }
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
                IntervalQualifier.valueOf(this.valueType - 22).getTypeName(sb, (int) this.precision, this.scale, false);
                break;
            case 36:
                this.extTypeInfo.getSQL(sb.append("ENUM"), i);
                break;
            case 37:
                sb.append("GEOMETRY");
                if (this.extTypeInfo != null) {
                    this.extTypeInfo.getSQL(sb, i);
                    break;
                }
                break;
            case 40:
                if (this.extTypeInfo != null) {
                    this.extTypeInfo.getSQL(sb, i).append(' ');
                }
                sb.append("ARRAY");
                if (this.precision >= 0) {
                    sb.append('[').append(this.precision).append(']');
                    break;
                }
                break;
            case 41:
                sb.append("ROW");
                if (this.extTypeInfo != null) {
                    this.extTypeInfo.getSQL(sb, i);
                    break;
                }
                break;
        }
        return sb;
    }

    public int hashCode() {
        return (31 * ((31 * ((31 * ((31 * 1) + this.valueType)) + ((int) (this.precision ^ (this.precision >>> 32))))) + this.scale)) + (this.extTypeInfo == null ? 0 : this.extTypeInfo.hashCode());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != TypeInfo.class) {
            return false;
        }
        TypeInfo typeInfo = (TypeInfo) obj;
        return this.valueType == typeInfo.valueType && this.precision == typeInfo.precision && this.scale == typeInfo.scale && Objects.equals(this.extTypeInfo, typeInfo.extTypeInfo);
    }

    public TypeInfo toNumericType() {
        switch (this.valueType) {
            case 8:
            case 9:
            case 10:
            case 11:
                return getTypeInfo(13, getDecimalPrecision(), 0, null);
            case 12:
                return TYPE_NUMERIC_BIGINT;
            case 13:
                return this;
            case 14:
                return getTypeInfo(13, 85L, 46, null);
            case 15:
                return getTypeInfo(13, 634L, 325, null);
            default:
                return TYPE_NUMERIC_FLOATING_POINT;
        }
    }

    public TypeInfo toDecfloatType() {
        switch (this.valueType) {
            case 8:
            case 9:
            case 10:
            case 11:
                return getTypeInfo(16, getDecimalPrecision(), 0, null);
            case 12:
                return TYPE_DECFLOAT_BIGINT;
            case 13:
                return getTypeInfo(16, getPrecision(), 0, null);
            case 14:
                return getTypeInfo(16, 7L, 0, null);
            case 15:
                return getTypeInfo(16, 17L, 0, null);
            case 16:
                return this;
            default:
                return TYPE_DECFLOAT;
        }
    }

    public TypeInfo unwrapRow() {
        if (this.valueType == 41) {
            Set<Map.Entry<String, TypeInfo>> fields = ((ExtTypeInfoRow) this.extTypeInfo).getFields();
            if (fields.size() == 1) {
                return fields.iterator().next().getValue().unwrapRow();
            }
        }
        return this;
    }

    public long getDecimalPrecision() {
        switch (this.valueType) {
            case 9:
                return 3L;
            case 10:
                return 5L;
            case 11:
                return 10L;
            case 12:
                return 19L;
            case 13:
            default:
                return this.precision;
            case 14:
                return 7L;
            case 15:
                return 17L;
        }
    }

    public String getDeclaredTypeName() {
        switch (this.valueType) {
            case 13:
                return this.extTypeInfo != null ? "DECIMAL" : "NUMERIC";
            case 14:
            case 15:
                if (this.extTypeInfo != null) {
                    return "FLOAT";
                }
                break;
            case 36:
            case 37:
            case 41:
                return getSQL(0);
            case 40:
                return ((TypeInfo) this.extTypeInfo).getSQL(new StringBuilder(), 0).append(" ARRAY").toString();
        }
        return Value.getTypeName(this.valueType);
    }
}

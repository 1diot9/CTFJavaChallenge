package org.h2.value;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.ZonedDateTime;
import java.util.TimeZone;
import java.util.UUID;
import org.h2.api.IntervalQualifier;
import org.h2.engine.Session;
import org.h2.jdbc.JdbcResultSet;
import org.h2.message.DbException;
import org.h2.message.TraceObject;
import org.h2.util.IntervalUtils;
import org.h2.util.JSR310Utils;
import org.h2.util.JdbcUtils;
import org.h2.util.LegacyDateTimeUtils;
import org.h2.util.Utils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/ValueToObjectConverter2.class */
public final class ValueToObjectConverter2 extends TraceObject {
    public static TypeInfo classToType(Class<?> cls) {
        if (cls == null) {
            return TypeInfo.TYPE_NULL;
        }
        if (cls.isPrimitive()) {
            cls = Utils.getNonPrimitiveClass(cls);
        }
        if (cls == Void.class) {
            return TypeInfo.TYPE_NULL;
        }
        if (cls == String.class || cls == Character.class) {
            return TypeInfo.TYPE_VARCHAR;
        }
        if (cls == byte[].class) {
            return TypeInfo.TYPE_VARBINARY;
        }
        if (cls == Boolean.class) {
            return TypeInfo.TYPE_BOOLEAN;
        }
        if (cls == Byte.class) {
            return TypeInfo.TYPE_TINYINT;
        }
        if (cls == Short.class) {
            return TypeInfo.TYPE_SMALLINT;
        }
        if (cls == Integer.class) {
            return TypeInfo.TYPE_INTEGER;
        }
        if (cls == Long.class) {
            return TypeInfo.TYPE_BIGINT;
        }
        if (cls == Float.class) {
            return TypeInfo.TYPE_REAL;
        }
        if (cls == Double.class) {
            return TypeInfo.TYPE_DOUBLE;
        }
        if (cls == LocalDate.class) {
            return TypeInfo.TYPE_DATE;
        }
        if (cls == LocalTime.class) {
            return TypeInfo.TYPE_TIME;
        }
        if (cls == OffsetTime.class) {
            return TypeInfo.TYPE_TIME_TZ;
        }
        if (cls == LocalDateTime.class) {
            return TypeInfo.TYPE_TIMESTAMP;
        }
        if (cls == OffsetDateTime.class || cls == ZonedDateTime.class || cls == Instant.class) {
            return TypeInfo.TYPE_TIMESTAMP_TZ;
        }
        if (cls == Period.class) {
            return TypeInfo.TYPE_INTERVAL_YEAR_TO_MONTH;
        }
        if (cls == Duration.class) {
            return TypeInfo.TYPE_INTERVAL_DAY_TO_SECOND;
        }
        if (UUID.class == cls) {
            return TypeInfo.TYPE_UUID;
        }
        if (cls.isArray()) {
            return TypeInfo.getTypeInfo(40, 2147483647L, 0, classToType(cls.getComponentType()));
        }
        if (Clob.class.isAssignableFrom(cls) || Reader.class.isAssignableFrom(cls)) {
            return TypeInfo.TYPE_CLOB;
        }
        if (Blob.class.isAssignableFrom(cls) || InputStream.class.isAssignableFrom(cls)) {
            return TypeInfo.TYPE_BLOB;
        }
        if (BigDecimal.class.isAssignableFrom(cls)) {
            return TypeInfo.TYPE_NUMERIC_FLOATING_POINT;
        }
        if (ValueToObjectConverter.GEOMETRY_CLASS != null && ValueToObjectConverter.GEOMETRY_CLASS.isAssignableFrom(cls)) {
            return TypeInfo.TYPE_GEOMETRY;
        }
        if (Array.class.isAssignableFrom(cls)) {
            return TypeInfo.TYPE_ARRAY_UNKNOWN;
        }
        if (ResultSet.class.isAssignableFrom(cls)) {
            return TypeInfo.TYPE_ROW_EMPTY;
        }
        TypeInfo legacyClassToType = LegacyDateTimeUtils.legacyClassToType(cls);
        if (legacyClassToType != null) {
            return legacyClassToType;
        }
        return TypeInfo.TYPE_JAVA_OBJECT;
    }

    public static Value readValue(Session session, ResultSet resultSet, int i, int i2) {
        Value readValueOther;
        if (resultSet instanceof JdbcResultSet) {
            readValueOther = ValueToObjectConverter.readValue(session, (JdbcResultSet) resultSet, i);
        } else {
            try {
                readValueOther = readValueOther(session, resultSet, i, i2);
            } catch (SQLException e) {
                throw DbException.convert(e);
            }
        }
        return readValueOther;
    }

    private static Value readValueOther(Session session, ResultSet resultSet, int i, int i2) throws SQLException {
        Value parse;
        byte[] serialize;
        switch (i2) {
            case 0:
                parse = ValueNull.INSTANCE;
                break;
            case 1:
                String string = resultSet.getString(i);
                parse = string == null ? ValueNull.INSTANCE : ValueChar.get(string);
                break;
            case 2:
                String string2 = resultSet.getString(i);
                parse = string2 == null ? ValueNull.INSTANCE : ValueVarchar.get(string2, session);
                break;
            case 3:
                if (session == null) {
                    String string3 = resultSet.getString(i);
                    parse = string3 == null ? ValueNull.INSTANCE : ValueClob.createSmall(string3);
                    break;
                } else {
                    Reader characterStream = resultSet.getCharacterStream(i);
                    parse = characterStream == null ? ValueNull.INSTANCE : session.addTemporaryLob(session.getDataHandler().getLobStorage().createClob(new BufferedReader(characterStream), -1L));
                    break;
                }
            case 4:
                String string4 = resultSet.getString(i);
                parse = string4 == null ? ValueNull.INSTANCE : ValueVarcharIgnoreCase.get(string4);
                break;
            case 5:
                byte[] bytes = resultSet.getBytes(i);
                parse = bytes == null ? ValueNull.INSTANCE : ValueBinary.getNoCopy(bytes);
                break;
            case 6:
                byte[] bytes2 = resultSet.getBytes(i);
                parse = bytes2 == null ? ValueNull.INSTANCE : ValueVarbinary.getNoCopy(bytes2);
                break;
            case 7:
                if (session == null) {
                    byte[] bytes3 = resultSet.getBytes(i);
                    parse = bytes3 == null ? ValueNull.INSTANCE : ValueBlob.createSmall(bytes3);
                    break;
                } else {
                    InputStream binaryStream = resultSet.getBinaryStream(i);
                    parse = binaryStream == null ? ValueNull.INSTANCE : session.addTemporaryLob(session.getDataHandler().getLobStorage().createBlob(binaryStream, -1L));
                    break;
                }
            case 8:
                parse = resultSet.wasNull() ? ValueNull.INSTANCE : ValueBoolean.get(resultSet.getBoolean(i));
                break;
            case 9:
                parse = resultSet.wasNull() ? ValueNull.INSTANCE : ValueTinyint.get(resultSet.getByte(i));
                break;
            case 10:
                parse = resultSet.wasNull() ? ValueNull.INSTANCE : ValueSmallint.get(resultSet.getShort(i));
                break;
            case 11:
                parse = resultSet.wasNull() ? ValueNull.INSTANCE : ValueInteger.get(resultSet.getInt(i));
                break;
            case 12:
                parse = resultSet.wasNull() ? ValueNull.INSTANCE : ValueBigint.get(resultSet.getLong(i));
                break;
            case 13:
                BigDecimal bigDecimal = resultSet.getBigDecimal(i);
                parse = bigDecimal == null ? ValueNull.INSTANCE : ValueNumeric.getAnyScale(bigDecimal);
                break;
            case 14:
                parse = resultSet.wasNull() ? ValueNull.INSTANCE : ValueReal.get(resultSet.getFloat(i));
                break;
            case 15:
                parse = resultSet.wasNull() ? ValueNull.INSTANCE : ValueDouble.get(resultSet.getDouble(i));
                break;
            case 16:
                BigDecimal bigDecimal2 = resultSet.getBigDecimal(i);
                parse = bigDecimal2 == null ? ValueNull.INSTANCE : ValueDecfloat.get(bigDecimal2);
                break;
            case 17:
                try {
                    LocalDate localDate = (LocalDate) resultSet.getObject(i, LocalDate.class);
                    parse = localDate == null ? ValueNull.INSTANCE : JSR310Utils.localDateToValue(localDate);
                    break;
                } catch (SQLException e) {
                    Date date = resultSet.getDate(i);
                    parse = date == null ? ValueNull.INSTANCE : LegacyDateTimeUtils.fromDate(session, null, date);
                    break;
                }
            case 18:
                try {
                    LocalTime localTime = (LocalTime) resultSet.getObject(i, LocalTime.class);
                    parse = localTime == null ? ValueNull.INSTANCE : JSR310Utils.localTimeToValue(localTime);
                    break;
                } catch (SQLException e2) {
                    Time time = resultSet.getTime(i);
                    parse = time == null ? ValueNull.INSTANCE : LegacyDateTimeUtils.fromTime(session, null, time);
                    break;
                }
            case 19:
                try {
                    OffsetTime offsetTime = (OffsetTime) resultSet.getObject(i, OffsetTime.class);
                    parse = offsetTime == null ? ValueNull.INSTANCE : JSR310Utils.offsetTimeToValue(offsetTime);
                    break;
                } catch (SQLException e3) {
                    Object object = resultSet.getObject(i);
                    if (object == null) {
                        parse = ValueNull.INSTANCE;
                        break;
                    } else {
                        parse = ValueTimeTimeZone.parse(object.toString(), session);
                        break;
                    }
                }
            case 20:
                try {
                    LocalDateTime localDateTime = (LocalDateTime) resultSet.getObject(i, LocalDateTime.class);
                    parse = localDateTime == null ? ValueNull.INSTANCE : JSR310Utils.localDateTimeToValue(localDateTime);
                    break;
                } catch (SQLException e4) {
                    Timestamp timestamp = resultSet.getTimestamp(i);
                    parse = timestamp == null ? ValueNull.INSTANCE : LegacyDateTimeUtils.fromTimestamp(session, (TimeZone) null, timestamp);
                    break;
                }
            case 21:
                try {
                    OffsetDateTime offsetDateTime = (OffsetDateTime) resultSet.getObject(i, OffsetDateTime.class);
                    parse = offsetDateTime == null ? ValueNull.INSTANCE : JSR310Utils.offsetDateTimeToValue(offsetDateTime);
                    break;
                } catch (SQLException e5) {
                    Object object2 = resultSet.getObject(i);
                    if (object2 == null) {
                        parse = ValueNull.INSTANCE;
                        break;
                    } else if (object2 instanceof ZonedDateTime) {
                        parse = JSR310Utils.zonedDateTimeToValue((ZonedDateTime) object2);
                        break;
                    } else {
                        parse = ValueTimestampTimeZone.parse(object2.toString(), session);
                        break;
                    }
                }
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
                String string5 = resultSet.getString(i);
                parse = string5 == null ? ValueNull.INSTANCE : IntervalUtils.parseFormattedInterval(IntervalQualifier.valueOf(i2 - 22), string5);
                break;
            case 35:
                try {
                    serialize = resultSet.getBytes(i);
                } catch (SQLException e6) {
                    try {
                        Object object3 = resultSet.getObject(i);
                        serialize = object3 != null ? JdbcUtils.serialize(object3, session.getJavaObjectSerializer()) : null;
                    } catch (Exception e7) {
                        throw DbException.convert(e7);
                    }
                }
                parse = serialize == null ? ValueNull.INSTANCE : ValueJavaObject.getNoCopy(serialize);
                break;
            case 36:
                parse = resultSet.wasNull() ? ValueNull.INSTANCE : ValueInteger.get(resultSet.getInt(i));
                break;
            case 37:
                Object object4 = resultSet.getObject(i);
                parse = object4 == null ? ValueNull.INSTANCE : ValueGeometry.getFromGeometry(object4);
                break;
            case 38:
                Object object5 = resultSet.getObject(i);
                if (object5 == null) {
                    parse = ValueNull.INSTANCE;
                    break;
                } else {
                    Class<?> cls = object5.getClass();
                    if (cls == byte[].class) {
                        parse = ValueJson.fromJson((byte[]) object5);
                        break;
                    } else if (cls == String.class) {
                        parse = ValueJson.fromJson((String) object5);
                        break;
                    } else {
                        parse = ValueJson.fromJson(object5.toString());
                        break;
                    }
                }
            case 39:
                Object object6 = resultSet.getObject(i);
                if (object6 == null) {
                    parse = ValueNull.INSTANCE;
                    break;
                } else if (object6 instanceof UUID) {
                    parse = ValueUuid.get((UUID) object6);
                    break;
                } else if (object6 instanceof byte[]) {
                    parse = ValueUuid.get((byte[]) object6);
                    break;
                } else {
                    parse = ValueUuid.get((String) object6);
                    break;
                }
            case 40:
                Array array = resultSet.getArray(i);
                if (array == null) {
                    parse = ValueNull.INSTANCE;
                    break;
                } else {
                    Object[] objArr = (Object[]) array.getArray();
                    if (objArr == null) {
                        parse = ValueNull.INSTANCE;
                        break;
                    } else {
                        int length = objArr.length;
                        Value[] valueArr = new Value[length];
                        for (int i3 = 0; i3 < length; i3++) {
                            valueArr[i3] = ValueToObjectConverter.objectToValue(session, objArr[i3], 0);
                        }
                        parse = ValueArray.get(valueArr, session);
                        break;
                    }
                }
            case 41:
                Object object7 = resultSet.getObject(i);
                if (object7 == null) {
                    parse = ValueNull.INSTANCE;
                    break;
                } else if (object7 instanceof ResultSet) {
                    parse = ValueToObjectConverter.resultSetToValue(session, (ResultSet) object7);
                    break;
                } else {
                    Object[] objArr2 = (Object[]) object7;
                    int length2 = objArr2.length;
                    Value[] valueArr2 = new Value[length2];
                    for (int i4 = 0; i4 < length2; i4++) {
                        valueArr2[i4] = ValueToObjectConverter.objectToValue(session, objArr2[i4], 0);
                    }
                    parse = ValueRow.get(valueArr2);
                    break;
                }
            default:
                throw DbException.getInternalError("data type " + i2);
        }
        return parse;
    }

    private ValueToObjectConverter2() {
    }
}

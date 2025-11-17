package org.h2.value;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLXML;
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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import org.h2.api.ErrorCode;
import org.h2.api.Interval;
import org.h2.command.CommandInterface;
import org.h2.engine.Session;
import org.h2.jdbc.JdbcArray;
import org.h2.jdbc.JdbcBlob;
import org.h2.jdbc.JdbcClob;
import org.h2.jdbc.JdbcConnection;
import org.h2.jdbc.JdbcLob;
import org.h2.jdbc.JdbcResultSet;
import org.h2.jdbc.JdbcSQLXML;
import org.h2.jdbc.JdbcStatement;
import org.h2.message.DbException;
import org.h2.message.TraceObject;
import org.h2.result.ResultInterface;
import org.h2.util.JSR310Utils;
import org.h2.util.JdbcUtils;
import org.h2.util.LegacyDateTimeUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/ValueToObjectConverter.class */
public final class ValueToObjectConverter extends TraceObject {
    public static final Class<?> GEOMETRY_CLASS;
    private static final String GEOMETRY_CLASS_NAME = "org.locationtech.jts.geom.Geometry";

    static {
        Class<?> cls;
        try {
            cls = JdbcUtils.loadUserClass(GEOMETRY_CLASS_NAME);
        } catch (Exception e) {
            cls = null;
        }
        GEOMETRY_CLASS = cls;
    }

    public static Value objectToValue(Session session, Object obj, int i) {
        if (obj == null) {
            return ValueNull.INSTANCE;
        }
        if (i == 35) {
            return ValueJavaObject.getNoCopy(JdbcUtils.serialize(obj, session.getJavaObjectSerializer()));
        }
        if (obj instanceof Value) {
            Value value = (Value) obj;
            if (value instanceof ValueLob) {
                session.addTemporaryLob((ValueLob) value);
            }
            return value;
        }
        Class<?> cls = obj.getClass();
        if (cls == String.class) {
            return ValueVarchar.get((String) obj, session);
        }
        if (cls == Long.class) {
            return ValueBigint.get(((Long) obj).longValue());
        }
        if (cls == Integer.class) {
            return ValueInteger.get(((Integer) obj).intValue());
        }
        if (cls == Boolean.class) {
            return ValueBoolean.get(((Boolean) obj).booleanValue());
        }
        if (cls == Byte.class) {
            return ValueTinyint.get(((Byte) obj).byteValue());
        }
        if (cls == Short.class) {
            return ValueSmallint.get(((Short) obj).shortValue());
        }
        if (cls == Float.class) {
            return ValueReal.get(((Float) obj).floatValue());
        }
        if (cls == Double.class) {
            return ValueDouble.get(((Double) obj).doubleValue());
        }
        if (cls == byte[].class) {
            return ValueVarbinary.get((byte[]) obj);
        }
        if (cls == UUID.class) {
            return ValueUuid.get((UUID) obj);
        }
        if (cls == Character.class) {
            return ValueChar.get(((Character) obj).toString());
        }
        if (cls == LocalDate.class) {
            return JSR310Utils.localDateToValue((LocalDate) obj);
        }
        if (cls == LocalTime.class) {
            return JSR310Utils.localTimeToValue((LocalTime) obj);
        }
        if (cls == LocalDateTime.class) {
            return JSR310Utils.localDateTimeToValue((LocalDateTime) obj);
        }
        if (cls == Instant.class) {
            return JSR310Utils.instantToValue((Instant) obj);
        }
        if (cls == OffsetTime.class) {
            return JSR310Utils.offsetTimeToValue((OffsetTime) obj);
        }
        if (cls == OffsetDateTime.class) {
            return JSR310Utils.offsetDateTimeToValue((OffsetDateTime) obj);
        }
        if (cls == ZonedDateTime.class) {
            return JSR310Utils.zonedDateTimeToValue((ZonedDateTime) obj);
        }
        if (cls == Interval.class) {
            Interval interval = (Interval) obj;
            return ValueInterval.from(interval.getQualifier(), interval.isNegative(), interval.getLeading(), interval.getRemaining());
        }
        if (cls == Period.class) {
            return JSR310Utils.periodToValue((Period) obj);
        }
        if (cls == Duration.class) {
            return JSR310Utils.durationToValue((Duration) obj);
        }
        if (obj instanceof Object[]) {
            return arrayToValue(session, obj);
        }
        if (GEOMETRY_CLASS != null && GEOMETRY_CLASS.isAssignableFrom(cls)) {
            return ValueGeometry.getFromGeometry(obj);
        }
        if (obj instanceof BigInteger) {
            return ValueNumeric.get((BigInteger) obj);
        }
        if (obj instanceof BigDecimal) {
            return ValueNumeric.getAnyScale((BigDecimal) obj);
        }
        return otherToValue(session, obj);
    }

    private static Value otherToValue(Session session, Object obj) {
        ValueClob createClob;
        if (obj instanceof Array) {
            try {
                return arrayToValue(session, ((Array) obj).getArray());
            } catch (SQLException e) {
                throw DbException.convert(e);
            }
        }
        if (obj instanceof ResultSet) {
            return resultSetToValue(session, (ResultSet) obj);
        }
        if (obj instanceof Reader) {
            Reader reader = (Reader) obj;
            if (!(reader instanceof BufferedReader)) {
                reader = new BufferedReader(reader);
            }
            createClob = session.getDataHandler().getLobStorage().createClob(reader, -1L);
        } else if (obj instanceof Clob) {
            try {
                Clob clob = (Clob) obj;
                createClob = session.getDataHandler().getLobStorage().createClob(new BufferedReader(clob.getCharacterStream()), clob.length());
            } catch (SQLException e2) {
                throw DbException.convert(e2);
            }
        } else if (obj instanceof InputStream) {
            createClob = session.getDataHandler().getLobStorage().createBlob((InputStream) obj, -1L);
        } else if (obj instanceof Blob) {
            try {
                Blob blob = (Blob) obj;
                createClob = session.getDataHandler().getLobStorage().createBlob(blob.getBinaryStream(), blob.length());
            } catch (SQLException e3) {
                throw DbException.convert(e3);
            }
        } else if (obj instanceof SQLXML) {
            try {
                createClob = session.getDataHandler().getLobStorage().createClob(new BufferedReader(((SQLXML) obj).getCharacterStream()), -1L);
            } catch (SQLException e4) {
                throw DbException.convert(e4);
            }
        } else {
            Value legacyObjectToValue = LegacyDateTimeUtils.legacyObjectToValue(session, obj);
            if (legacyObjectToValue != null) {
                return legacyObjectToValue;
            }
            return ValueJavaObject.getNoCopy(JdbcUtils.serialize(obj, session.getJavaObjectSerializer()));
        }
        return session.addTemporaryLob(createClob);
    }

    private static Value arrayToValue(Session session, Object obj) {
        Object[] objArr = (Object[]) obj;
        int length = objArr.length;
        Value[] valueArr = new Value[length];
        for (int i = 0; i < length; i++) {
            valueArr[i] = objectToValue(session, objArr[i], -1);
        }
        return ValueArray.get(valueArr, session);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Value resultSetToValue(Session session, ResultSet resultSet) {
        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            LinkedHashMap<String, TypeInfo> readResultSetMeta = readResultSetMeta(session, metaData, columnCount);
            if (!resultSet.next()) {
                throw DbException.get(ErrorCode.DATA_CONVERSION_ERROR_1, "Empty ResultSet to ROW value");
            }
            Value[] valueArr = new Value[columnCount];
            Iterator<Map.Entry<String, TypeInfo>> it = readResultSetMeta.entrySet().iterator();
            for (int i = 0; i < columnCount; i++) {
                valueArr[i] = objectToValue(session, resultSet.getObject(i + 1), it.next().getValue().getValueType());
            }
            if (resultSet.next()) {
                throw DbException.get(ErrorCode.DATA_CONVERSION_ERROR_1, "Multi-row ResultSet to ROW value");
            }
            return ValueRow.get(new ExtTypeInfoRow(readResultSetMeta), valueArr);
        } catch (SQLException e) {
            throw DbException.convert(e);
        }
    }

    private static LinkedHashMap<String, TypeInfo> readResultSetMeta(Session session, ResultSetMetaData resultSetMetaData, int i) throws SQLException {
        TypeInfo typeInfo;
        LinkedHashMap<String, TypeInfo> linkedHashMap = new LinkedHashMap<>();
        for (int i2 = 0; i2 < i; i2++) {
            String columnLabel = resultSetMetaData.getColumnLabel(i2 + 1);
            String columnTypeName = resultSetMetaData.getColumnTypeName(i2 + 1);
            int convertSQLTypeToValueType = DataType.convertSQLTypeToValueType(resultSetMetaData.getColumnType(i2 + 1), columnTypeName);
            int precision = resultSetMetaData.getPrecision(i2 + 1);
            int scale = resultSetMetaData.getScale(i2 + 1);
            if (convertSQLTypeToValueType == 40 && columnTypeName.endsWith(" ARRAY")) {
                typeInfo = TypeInfo.getTypeInfo(40, -1L, 0, TypeInfo.getTypeInfo(DataType.getTypeByName(columnTypeName.substring(0, columnTypeName.length() - 6), session.getMode()).type));
            } else {
                typeInfo = TypeInfo.getTypeInfo(convertSQLTypeToValueType, precision, scale, null);
            }
            linkedHashMap.put(columnLabel, typeInfo);
        }
        return linkedHashMap;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static <T> T valueToObject(Class<T> cls, Value value, JdbcConnection jdbcConnection) {
        if (value == ValueNull.INSTANCE) {
            return null;
        }
        if (cls == BigDecimal.class) {
            return (T) value.getBigDecimal();
        }
        if (cls == BigInteger.class) {
            return (T) value.getBigDecimal().toBigInteger();
        }
        if (cls == String.class) {
            return (T) value.getString();
        }
        if (cls == Boolean.class) {
            return (T) Boolean.valueOf(value.getBoolean());
        }
        if (cls == Byte.class) {
            return (T) Byte.valueOf(value.getByte());
        }
        if (cls == Short.class) {
            return (T) Short.valueOf(value.getShort());
        }
        if (cls == Integer.class) {
            return (T) Integer.valueOf(value.getInt());
        }
        if (cls == Long.class) {
            return (T) Long.valueOf(value.getLong());
        }
        if (cls == Float.class) {
            return (T) Float.valueOf(value.getFloat());
        }
        if (cls == Double.class) {
            return (T) Double.valueOf(value.getDouble());
        }
        if (cls == UUID.class) {
            return (T) value.convertToUuid().getUuid();
        }
        if (cls == byte[].class) {
            return (T) value.getBytes();
        }
        if (cls == Character.class) {
            String string = value.getString();
            return (T) Character.valueOf(string.isEmpty() ? ' ' : string.charAt(0));
        }
        if (cls == Interval.class) {
            if (!(value instanceof ValueInterval)) {
                value = value.convertTo(TypeInfo.TYPE_INTERVAL_DAY_TO_SECOND);
            }
            ValueInterval valueInterval = (ValueInterval) value;
            return (T) new Interval(valueInterval.getQualifier(), false, valueInterval.getLeading(), valueInterval.getRemaining());
        }
        if (cls == LocalDate.class) {
            return (T) JSR310Utils.valueToLocalDate(value, jdbcConnection);
        }
        if (cls == LocalTime.class) {
            return (T) JSR310Utils.valueToLocalTime(value, jdbcConnection);
        }
        if (cls == LocalDateTime.class) {
            return (T) JSR310Utils.valueToLocalDateTime(value, jdbcConnection);
        }
        if (cls == OffsetTime.class) {
            return (T) JSR310Utils.valueToOffsetTime(value, jdbcConnection);
        }
        if (cls == OffsetDateTime.class) {
            return (T) JSR310Utils.valueToOffsetDateTime(value, jdbcConnection);
        }
        if (cls == ZonedDateTime.class) {
            return (T) JSR310Utils.valueToZonedDateTime(value, jdbcConnection);
        }
        if (cls == Instant.class) {
            return (T) JSR310Utils.valueToInstant(value, jdbcConnection);
        }
        if (cls == Period.class) {
            return (T) JSR310Utils.valueToPeriod(value);
        }
        if (cls == Duration.class) {
            return (T) JSR310Utils.valueToDuration(value);
        }
        if (cls.isArray()) {
            return (T) valueToArray(cls, value, jdbcConnection);
        }
        if (GEOMETRY_CLASS != null && GEOMETRY_CLASS.isAssignableFrom(cls)) {
            return (T) value.convertToGeometry(null).getGeometry();
        }
        return (T) valueToOther(cls, value, jdbcConnection);
    }

    private static Object valueToArray(Class<?> cls, Value value, JdbcConnection jdbcConnection) {
        Value[] list = ((ValueArray) value).getList();
        Class<?> componentType = cls.getComponentType();
        int length = list.length;
        Object[] objArr = (Object[]) java.lang.reflect.Array.newInstance(componentType, length);
        for (int i = 0; i < length; i++) {
            objArr[i] = valueToObject(componentType, list[i], jdbcConnection);
        }
        return objArr;
    }

    private static Object valueToOther(Class<?> cls, Value value, JdbcConnection jdbcConnection) {
        if (cls == Object.class) {
            return JdbcUtils.deserialize(value.convertToJavaObject(TypeInfo.TYPE_JAVA_OBJECT, 0, null).getBytesNoCopy(), jdbcConnection.getJavaObjectSerializer());
        }
        if (cls == InputStream.class) {
            return value.getInputStream();
        }
        if (cls == Reader.class) {
            return value.getReader();
        }
        if (cls == Array.class) {
            return new JdbcArray(jdbcConnection, value, getNextId(16));
        }
        if (cls == Blob.class) {
            return new JdbcBlob(jdbcConnection, value, JdbcLob.State.WITH_VALUE, getNextId(9));
        }
        if (cls == Clob.class) {
            return new JdbcClob(jdbcConnection, value, JdbcLob.State.WITH_VALUE, getNextId(10));
        }
        if (cls == SQLXML.class) {
            return new JdbcSQLXML(jdbcConnection, value, JdbcLob.State.WITH_VALUE, getNextId(17));
        }
        if (cls == ResultSet.class) {
            return new JdbcResultSet(jdbcConnection, (JdbcStatement) null, (CommandInterface) null, (ResultInterface) value.convertToAnyRow().getResult(), getNextId(4), true, false, false);
        }
        Object valueToLegacyType = LegacyDateTimeUtils.valueToLegacyType(cls, value, jdbcConnection);
        if (valueToLegacyType != null) {
            return valueToLegacyType;
        }
        if (value.getValueType() == 35) {
            Object deserialize = JdbcUtils.deserialize(value.getBytesNoCopy(), jdbcConnection.getJavaObjectSerializer());
            if (cls.isAssignableFrom(deserialize.getClass())) {
                return deserialize;
            }
        }
        throw DbException.getUnsupportedException("converting to class " + cls.getName());
    }

    public static Class<?> getDefaultClass(int i, boolean z) {
        switch (i) {
            case 0:
                return Void.class;
            case 1:
            case 2:
            case 4:
            case 36:
                return String.class;
            case 3:
                return Clob.class;
            case 5:
            case 6:
            case 38:
                return byte[].class;
            case 7:
                return Blob.class;
            case 8:
                return Boolean.class;
            case 9:
                if (z) {
                    return Integer.class;
                }
                return Byte.class;
            case 10:
                if (z) {
                    return Integer.class;
                }
                return Short.class;
            case 11:
                return Integer.class;
            case 12:
                return Long.class;
            case 13:
            case 16:
                return BigDecimal.class;
            case 14:
                return Float.class;
            case 15:
                return Double.class;
            case 17:
                return z ? Date.class : LocalDate.class;
            case 18:
                return z ? Time.class : LocalTime.class;
            case 19:
                return OffsetTime.class;
            case 20:
                return z ? Timestamp.class : LocalDateTime.class;
            case 21:
                return OffsetDateTime.class;
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
                return Interval.class;
            case 35:
                return z ? Object.class : byte[].class;
            case 37:
                Class<?> cls = GEOMETRY_CLASS;
                return cls != null ? cls : String.class;
            case 39:
                return UUID.class;
            case 40:
                if (z) {
                    return Array.class;
                }
                return Object[].class;
            case 41:
                if (z) {
                    return ResultSet.class;
                }
                return Object[].class;
            default:
                throw DbException.getUnsupportedException("data type " + i);
        }
    }

    public static Object valueToDefaultObject(Value value, JdbcConnection jdbcConnection, boolean z) {
        switch (value.getValueType()) {
            case 0:
                return null;
            case 1:
            case 2:
            case 4:
            case 36:
                return value.getString();
            case 3:
                return new JdbcClob(jdbcConnection, value, JdbcLob.State.WITH_VALUE, getNextId(10));
            case 5:
            case 6:
            case 38:
                return value.getBytes();
            case 7:
                return new JdbcBlob(jdbcConnection, value, JdbcLob.State.WITH_VALUE, getNextId(9));
            case 8:
                return Boolean.valueOf(value.getBoolean());
            case 9:
                if (z) {
                    return Integer.valueOf(value.getInt());
                }
                return Byte.valueOf(value.getByte());
            case 10:
                if (z) {
                    return Integer.valueOf(value.getInt());
                }
                return Short.valueOf(value.getShort());
            case 11:
                return Integer.valueOf(value.getInt());
            case 12:
                return Long.valueOf(value.getLong());
            case 13:
            case 16:
                return value.getBigDecimal();
            case 14:
                return Float.valueOf(value.getFloat());
            case 15:
                return Double.valueOf(value.getDouble());
            case 17:
                return z ? LegacyDateTimeUtils.toDate(jdbcConnection, null, value) : JSR310Utils.valueToLocalDate(value, null);
            case 18:
                return z ? LegacyDateTimeUtils.toTime(jdbcConnection, null, value) : JSR310Utils.valueToLocalTime(value, null);
            case 19:
                return JSR310Utils.valueToOffsetTime(value, null);
            case 20:
                return z ? LegacyDateTimeUtils.toTimestamp(jdbcConnection, null, value) : JSR310Utils.valueToLocalDateTime(value, null);
            case 21:
                return JSR310Utils.valueToOffsetDateTime(value, null);
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
                return ((ValueInterval) value).getInterval();
            case 35:
                return z ? JdbcUtils.deserialize(value.getBytesNoCopy(), jdbcConnection.getJavaObjectSerializer()) : value.getBytes();
            case 37:
                return GEOMETRY_CLASS != null ? ((ValueGeometry) value).getGeometry() : value.getString();
            case 39:
                return ((ValueUuid) value).getUuid();
            case 40:
                if (z) {
                    return new JdbcArray(jdbcConnection, value, getNextId(16));
                }
                return valueToDefaultArray(value, jdbcConnection, z);
            case 41:
                if (z) {
                    return new JdbcResultSet(jdbcConnection, (JdbcStatement) null, (CommandInterface) null, (ResultInterface) ((ValueRow) value).getResult(), getNextId(4), true, false, false);
                }
                return valueToDefaultArray(value, jdbcConnection, z);
            default:
                throw DbException.getUnsupportedException("data type " + value.getValueType());
        }
    }

    public static Object valueToDefaultArray(Value value, JdbcConnection jdbcConnection, boolean z) {
        Value[] list = ((ValueCollectionBase) value).getList();
        int length = list.length;
        Object[] objArr = new Object[length];
        for (int i = 0; i < length; i++) {
            objArr[i] = valueToDefaultObject(list[i], jdbcConnection, z);
        }
        return objArr;
    }

    public static Value readValue(Session session, JdbcResultSet jdbcResultSet, int i) {
        Value internal = jdbcResultSet.getInternal(i);
        switch (internal.getValueType()) {
            case 3:
                internal = session.addTemporaryLob(session.getDataHandler().getLobStorage().createClob(new BufferedReader(internal.getReader()), -1L));
                break;
            case 7:
                internal = session.addTemporaryLob(session.getDataHandler().getLobStorage().createBlob(internal.getInputStream(), -1L));
                break;
        }
        return internal;
    }

    private ValueToObjectConverter() {
    }
}

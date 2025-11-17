package org.jooq.impl;

import jakarta.xml.bind.JAXB;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Struct;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import org.jooq.ContextConverter;
import org.jooq.Converter;
import org.jooq.ConverterContext;
import org.jooq.EnumType;
import org.jooq.Field;
import org.jooq.JSON;
import org.jooq.JSONB;
import org.jooq.JSONFormat;
import org.jooq.Param;
import org.jooq.QualifiedRecord;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.XML;
import org.jooq.XMLFormat;
import org.jooq.exception.DataTypeException;
import org.jooq.tools.Ints;
import org.jooq.tools.JooqLogger;
import org.jooq.tools.Longs;
import org.jooq.tools.StringUtils;
import org.jooq.tools.jdbc.MockArray;
import org.jooq.tools.jdbc.MockResultSet;
import org.jooq.tools.json.ContainerFactory;
import org.jooq.tools.json.JSONParser;
import org.jooq.tools.json.ParseException;
import org.jooq.tools.reflect.Reflect;
import org.jooq.types.DayToSecond;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.jooq.types.ULong;
import org.jooq.types.UShort;
import org.jooq.types.Unsigned;
import org.jooq.types.YearToMonth;
import org.jooq.types.YearToSecond;
import org.jooq.util.postgres.PostgresUtils;
import org.jooq.util.xml.jaxb.InformationSchema;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Convert.class */
public final class Convert {
    static final Set<String> TRUE_VALUES;
    static final Set<String> FALSE_VALUES;
    static final Pattern P_FRACTIONAL_SECONDS;
    private static final JooqLogger log = JooqLogger.getLogger((Class<?>) Convert.class);
    private static final Pattern UUID_PATTERN = Pattern.compile("(\\p{XDigit}{8})-?(\\p{XDigit}{4})-?(\\p{XDigit}{4})-?(\\p{XDigit}{4})-?(\\p{XDigit}{12})");

    static {
        Set<String> trueValues = new HashSet<>();
        Set<String> falseValues = new HashSet<>();
        trueValues.add(CustomBooleanEditor.VALUE_1);
        trueValues.add("1.0");
        trueValues.add("y");
        trueValues.add("Y");
        trueValues.add(CustomBooleanEditor.VALUE_YES);
        trueValues.add("YES");
        trueValues.add("true");
        trueValues.add("TRUE");
        trueValues.add("t");
        trueValues.add("T");
        trueValues.add(CustomBooleanEditor.VALUE_ON);
        trueValues.add("ON");
        trueValues.add("enabled");
        trueValues.add("ENABLED");
        falseValues.add(CustomBooleanEditor.VALUE_0);
        falseValues.add("0.0");
        falseValues.add("n");
        falseValues.add("N");
        falseValues.add("no");
        falseValues.add("NO");
        falseValues.add("false");
        falseValues.add("FALSE");
        falseValues.add("f");
        falseValues.add("F");
        falseValues.add(CustomBooleanEditor.VALUE_OFF);
        falseValues.add("OFF");
        falseValues.add("disabled");
        falseValues.add("DISABLED");
        TRUE_VALUES = Collections.unmodifiableSet(trueValues);
        FALSE_VALUES = Collections.unmodifiableSet(falseValues);
        P_FRACTIONAL_SECONDS = Pattern.compile("^(\\d+:\\d+:\\d+)\\.\\d+$");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Convert$_JSON.class */
    public static final class _JSON {
        private static final Object JSON_MAPPER;
        private static final Method JSON_READ_METHOD;
        private static final Method JSON_WRITE_METHOD;

        private _JSON() {
        }

        static {
            Object jsonMapper = null;
            Method jsonReadMethod = null;
            Method jsonWriteMethod = null;
            try {
                Class<?> klass = Class.forName("com.fasterxml.jackson.databind.ObjectMapper");
                try {
                    Class<?> kotlin = Class.forName("com.fasterxml.jackson.module.kotlin.ExtensionsKt");
                    jsonMapper = kotlin.getMethod("jacksonObjectMapper", new Class[0]).invoke(kotlin, new Object[0]);
                    Convert.log.debug("Jackson kotlin module is available");
                } catch (Exception e) {
                    jsonMapper = klass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
                    Convert.log.debug("Jackson kotlin module is not available");
                }
                jsonReadMethod = klass.getMethod("readValue", String.class, Class.class);
                jsonWriteMethod = klass.getMethod("writeValueAsString", Object.class);
                Convert.log.debug("Jackson is available");
            } catch (Exception e1) {
                Convert.log.debug("Jackson not available", e1.getMessage());
                try {
                    Class<?> klass2 = Class.forName("com.google.gson.Gson");
                    jsonMapper = klass2.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
                    jsonReadMethod = klass2.getMethod("fromJson", String.class, Class.class);
                    jsonWriteMethod = klass2.getMethod("toJson", Object.class);
                    Convert.log.debug("Gson is available");
                } catch (Exception e2) {
                    Convert.log.debug("Gson not available", e2.getMessage());
                }
            }
            JSON_MAPPER = jsonMapper;
            JSON_READ_METHOD = jsonReadMethod;
            JSON_WRITE_METHOD = jsonWriteMethod;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Convert$_XML.class */
    public static final class _XML {
        private static final boolean JAXB_AVAILABLE;

        private _XML() {
        }

        static {
            boolean jaxbAvailable = false;
            try {
                JAXB.marshal(new InformationSchema(), new StringWriter());
                jaxbAvailable = true;
                Convert.log.debug("JAXB is available");
            } catch (Throwable t) {
                Convert.log.debug("JAXB not available", t.getMessage());
            }
            JAXB_AVAILABLE = jaxbAvailable;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Object[] convert(Object[] values, Field<?>[] fields) {
        if (values == null) {
            return null;
        }
        Object[] result = new Object[values.length];
        for (int i = 0; i < values.length; i++) {
            if (values[i] instanceof Field) {
                result[i] = values[i];
            } else {
                result[i] = convert(values[i], fields[i].getType());
            }
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Object[] convert(Object[] values, Class<?>[] types) {
        if (values == null) {
            return null;
        }
        Object[] result = new Object[values.length];
        for (int i = 0; i < values.length; i++) {
            if (values[i] instanceof Field) {
                result[i] = values[i];
            } else {
                result[i] = convert(values[i], types[i]);
            }
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    public static final <U> U[] convertArray(Object[] objArr, Converter<?, ? extends U> converter) throws DataTypeException {
        if (objArr == null) {
            return null;
        }
        Object[] convertArray = convertArray(objArr, converter.fromType());
        U[] uArr = (U[]) ((Object[]) java.lang.reflect.Array.newInstance(converter.toType(), objArr.length));
        for (int i = 0; i < convertArray.length; i++) {
            uArr[i] = convert(convertArray[i], converter);
        }
        return uArr;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Object[] convertArray(Object[] from, Class<?> toClass) throws DataTypeException {
        if (from == null) {
            return null;
        }
        if (!toClass.isArray()) {
            return convertArray(from, (Class<?>) Internal.arrayType(toClass));
        }
        if (toClass == from.getClass()) {
            return from;
        }
        Class<?> toComponentType = toClass.getComponentType();
        if (from.length == 0) {
            return Arrays.copyOf(from, from.length, toClass);
        }
        if (from[0] != null && from[0].getClass() == toComponentType) {
            return Arrays.copyOf(from, from.length, toClass);
        }
        Object[] result = (Object[]) java.lang.reflect.Array.newInstance(toComponentType, from.length);
        for (int i = 0; i < from.length; i++) {
            result[i] = convert(from[i], toComponentType);
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <U> U[] convertCollection(Collection collection, Class<? extends U[]> cls) {
        return (U[]) ((Object[]) new ConvertAll(cls).from(collection, Internal.converterContext()));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    public static final <U> U convert(Object obj, Converter<?, ? extends U> converter) throws DataTypeException {
        if (converter instanceof IdentityConverter) {
            return obj;
        }
        return (U) convert0(obj, converter);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static final <T, U> U convert0(Object obj, Converter<T, ? extends U> converter) throws DataTypeException {
        Class<T> fromType = converter.fromType();
        if (fromType == Object.class) {
            return (U) ContextConverter.scoped(converter).from(obj, Internal.converterContext());
        }
        return (U) ContextConverter.scoped(converter).from(new ConvertAll(fromType).from(obj, Internal.converterContext()), Internal.converterContext());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    public static final <T> T convert(Object obj, Class<? extends T> cls) throws DataTypeException {
        if (obj != 0 && obj.getClass() == cls) {
            return obj;
        }
        return (T) convert0(obj, new ConvertAll(cls));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> List<T> convert(Collection<?> collection, Class<? extends T> type) throws DataTypeException {
        return convert(collection, (Converter) new ConvertAll(type));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <U> List<U> convert(Collection<?> collection, Converter<?, ? extends U> converter) throws DataTypeException {
        return convert0(collection, (Converter) converter);
    }

    private static final <T, U> List<U> convert0(Collection<?> collection, Converter<T, ? extends U> converter) throws DataTypeException {
        ConvertAll<T> all = new ConvertAll<>(converter.fromType());
        ArrayList arrayList = new ArrayList(collection.size());
        for (Object o : collection) {
            arrayList.add(convert(all.from(o, Internal.converterContext()), converter));
        }
        return arrayList;
    }

    private Convert() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Convert$ConvertAll.class */
    public static final class ConvertAll<U> extends AbstractContextConverter<Object, U> {
        private final Class<? extends U> toClass;

        ConvertAll(Class<? extends U> toClass) {
            super(Object.class, toClass);
            this.toClass = toClass;
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r0v200, types: [U, org.jooq.QualifiedRecord] */
        @Override // org.jooq.ContextConverter
        public U from(Object obj, ConverterContext converterContext) {
            String literal;
            if (obj == 0) {
                if (this.toClass.isPrimitive()) {
                    if (this.toClass == Character.TYPE) {
                        return (U) (char) 0;
                    }
                    return (U) Convert.convert((Object) 0, (Class) this.toClass);
                }
                if (this.toClass == Optional.class) {
                    return (U) Optional.empty();
                }
                return null;
            }
            Class<?> cls = obj.getClass();
            if (this.toClass == cls) {
                return obj;
            }
            if (this.toClass.isAssignableFrom(cls)) {
                return obj;
            }
            Class wrapper = Reflect.wrapper(this.toClass);
            Class wrapper2 = Reflect.wrapper(cls);
            if (wrapper == wrapper2) {
                return obj;
            }
            if (cls == Optional.class) {
                return from(((Optional) obj).orElse(null), converterContext);
            }
            if (cls == byte[].class) {
                if (this.toClass == UUID.class) {
                    ByteBuffer wrap = ByteBuffer.wrap((byte[]) obj);
                    return (U) new UUID(wrap.getLong(), wrap.getLong());
                }
                if (this.toClass == ByteBuffer.class) {
                    return (U) ByteBuffer.wrap((byte[]) obj);
                }
                return (U) Convert.convert(new String((byte[]) obj), this.toClass);
            }
            if (cls.isArray()) {
                Object[] objArr = (Object[]) obj;
                if (Collection.class.isAssignableFrom(this.toClass) && this.toClass.isAssignableFrom(ArrayList.class)) {
                    return (U) new ArrayList(Arrays.asList(objArr));
                }
                if (Collection.class.isAssignableFrom(this.toClass) && this.toClass.isAssignableFrom(LinkedHashSet.class)) {
                    return (U) new LinkedHashSet(Arrays.asList(objArr));
                }
                if (this.toClass == java.sql.Array.class) {
                    return (U) new MockArray(null, objArr, cls);
                }
                return (U) Convert.convertArray(objArr, this.toClass);
            }
            if (java.sql.Array.class.isAssignableFrom(cls)) {
                try {
                    return (U) Convert.convertArray((Object[]) ((java.sql.Array) obj).getArray(), this.toClass);
                } catch (SQLException e) {
                    throw new DataTypeException("Cannot convert from JDBC array: ", e);
                }
            }
            if (Result.class.isAssignableFrom(cls) && this.toClass == String.class) {
                switch (Tools.emulateMultiset(Tools.configuration((Result) obj))) {
                    case XML:
                        return (U) ((Result) obj).formatXML(XMLFormat.DEFAULT_FOR_RECORDS);
                    case JSON:
                    case JSONB:
                        return (U) ((Result) obj).formatJSON(JSONFormat.DEFAULT_FOR_RECORDS);
                }
            }
            if (Result.class.isAssignableFrom(cls) && this.toClass == byte[].class) {
                return (U) Convert.convert(Convert.convert(obj, String.class), byte[].class);
            }
            if (Result.class.isAssignableFrom(cls) && this.toClass == ResultSet.class) {
                return (U) new MockResultSet((Result) obj);
            }
            if (Collection.class.isAssignableFrom(cls) && (this.toClass == java.sql.Array.class || this.toClass.isArray())) {
                Object[] array = ((Collection) obj).toArray();
                if (this.toClass == java.sql.Array.class) {
                    return (U) new MockArray(null, array, cls);
                }
                return (U) Convert.convertArray(array, this.toClass);
            }
            if (this.toClass == Optional.class) {
                return (U) Optional.of(obj);
            }
            if (this.toClass == String.class) {
                if (obj instanceof EnumType) {
                    return (U) ((EnumType) obj).getLiteral();
                }
                return (U) obj.toString();
            }
            if (this.toClass == byte[].class) {
                if (obj instanceof UUID) {
                    UUID uuid = (UUID) obj;
                    ByteBuffer wrap2 = ByteBuffer.wrap(new byte[16]);
                    wrap2.putLong(uuid.getMostSignificantBits());
                    wrap2.putLong(uuid.getLeastSignificantBits());
                    return (U) wrap2.array();
                }
                if (obj instanceof ByteBuffer) {
                    return (U) ((ByteBuffer) obj).array();
                }
                return (U) obj.toString().getBytes();
            }
            if (wrapper == Byte.class) {
                if (Number.class.isAssignableFrom(cls)) {
                    return (U) Byte.valueOf(((Number) obj).byteValue());
                }
                if (wrapper2 == Boolean.class) {
                    return ((Boolean) obj).booleanValue() ? (U) (byte) 1 : (U) (byte) 0;
                }
                try {
                    String trim = obj.toString().trim();
                    Integer tryParse = Ints.tryParse(trim);
                    return (U) Byte.valueOf(tryParse != null ? tryParse.byteValue() : new BigDecimal(trim).byteValue());
                } catch (NumberFormatException e2) {
                    return (U) Reflect.initValue(this.toClass);
                }
            }
            if (wrapper == Short.class) {
                if (Number.class.isAssignableFrom(cls)) {
                    return (U) Short.valueOf(((Number) obj).shortValue());
                }
                if (wrapper2 == Boolean.class) {
                    return ((Boolean) obj).booleanValue() ? (U) (short) 1 : (U) (short) 0;
                }
                try {
                    String trim2 = obj.toString().trim();
                    Integer tryParse2 = Ints.tryParse(trim2);
                    return (U) Short.valueOf(tryParse2 != null ? tryParse2.shortValue() : new BigDecimal(trim2).shortValue());
                } catch (NumberFormatException e3) {
                    return (U) Reflect.initValue(this.toClass);
                }
            }
            if (wrapper == Integer.class) {
                if (Number.class.isAssignableFrom(cls)) {
                    return (U) Integer.valueOf(((Number) obj).intValue());
                }
                if (wrapper2 == Boolean.class) {
                    return ((Boolean) obj).booleanValue() ? (U) 1 : (U) 0;
                }
                try {
                    String trim3 = obj.toString().trim();
                    Integer tryParse3 = Ints.tryParse(trim3);
                    return (U) Integer.valueOf(tryParse3 != null ? tryParse3.intValue() : new BigDecimal(trim3).intValue());
                } catch (NumberFormatException e4) {
                    return (U) Reflect.initValue(this.toClass);
                }
            }
            if (wrapper == Long.class) {
                if (Number.class.isAssignableFrom(cls)) {
                    return (U) Long.valueOf(((Number) obj).longValue());
                }
                if (wrapper2 == Boolean.class) {
                    return ((Boolean) obj).booleanValue() ? (U) 1L : (U) 0L;
                }
                if (wrapper2 == Year.class) {
                    return (U) Long.valueOf(((Year) obj).getValue());
                }
                if (Date.class.isAssignableFrom(cls)) {
                    return (U) Long.valueOf(((Date) obj).getTime());
                }
                if (Temporal.class.isAssignableFrom(cls)) {
                    return (U) Long.valueOf(millis((Temporal) obj));
                }
                try {
                    String trim4 = obj.toString().trim();
                    Long tryParse4 = Longs.tryParse(trim4);
                    return (U) Long.valueOf(tryParse4 != null ? tryParse4.longValue() : new BigDecimal(trim4).longValue());
                } catch (NumberFormatException e5) {
                    return (U) Reflect.initValue(this.toClass);
                }
            }
            if (this.toClass == UByte.class) {
                try {
                    if (Number.class.isAssignableFrom(cls)) {
                        return (U) Unsigned.ubyte(((Number) obj).shortValue());
                    }
                    if (wrapper2 == Boolean.class) {
                        return ((Boolean) obj).booleanValue() ? (U) Unsigned.ubyte(1) : (U) Unsigned.ubyte(0);
                    }
                    String trim5 = obj.toString().trim();
                    Integer tryParse5 = Ints.tryParse(trim5);
                    return (U) Unsigned.ubyte(tryParse5 != null ? tryParse5.shortValue() : new BigDecimal(trim5).shortValue());
                } catch (NumberFormatException e6) {
                    return null;
                }
            }
            if (this.toClass == UShort.class) {
                try {
                    if (Number.class.isAssignableFrom(cls)) {
                        return (U) Unsigned.ushort(((Number) obj).intValue());
                    }
                    if (wrapper2 == Boolean.class) {
                        return ((Boolean) obj).booleanValue() ? (U) Unsigned.ushort(1) : (U) Unsigned.ushort(0);
                    }
                    String trim6 = obj.toString().trim();
                    Integer tryParse6 = Ints.tryParse(trim6);
                    return (U) Unsigned.ushort(tryParse6 != null ? tryParse6.intValue() : new BigDecimal(trim6).intValue());
                } catch (NumberFormatException e7) {
                    return null;
                }
            }
            if (this.toClass == UInteger.class) {
                try {
                    if (Number.class.isAssignableFrom(cls)) {
                        return (U) Unsigned.uint(((Number) obj).longValue());
                    }
                    if (wrapper2 == Boolean.class) {
                        return ((Boolean) obj).booleanValue() ? (U) Unsigned.uint(1) : (U) Unsigned.uint(0);
                    }
                    String trim7 = obj.toString().trim();
                    Long tryParse7 = Longs.tryParse(trim7);
                    return (U) Unsigned.uint(tryParse7 != null ? tryParse7.longValue() : new BigDecimal(trim7).longValue());
                } catch (NumberFormatException e8) {
                    return null;
                }
            }
            if (this.toClass == ULong.class) {
                if (wrapper2 == Boolean.class) {
                    return ((Boolean) obj).booleanValue() ? (U) Unsigned.ulong(1L) : (U) Unsigned.ulong(0L);
                }
                if (Date.class.isAssignableFrom(cls)) {
                    return (U) Unsigned.ulong(((Date) obj).getTime());
                }
                if (Temporal.class.isAssignableFrom(cls)) {
                    return (U) Unsigned.ulong(millis((Temporal) obj));
                }
                try {
                    String trim8 = obj.toString().trim();
                    Long tryParse8 = Longs.tryParse(trim8);
                    return tryParse8 != null ? (U) Unsigned.ulong(tryParse8.longValue()) : (U) Unsigned.ulong(new BigDecimal(trim8).toBigInteger());
                } catch (NumberFormatException e9) {
                    return null;
                }
            }
            if (wrapper == Float.class) {
                if (Number.class.isAssignableFrom(cls)) {
                    return (U) Float.valueOf(((Number) obj).floatValue());
                }
                if (wrapper2 == Boolean.class) {
                    return ((Boolean) obj).booleanValue() ? (U) Float.valueOf(1.0f) : (U) Float.valueOf(0.0f);
                }
                try {
                    return (U) Float.valueOf(obj.toString().trim());
                } catch (NumberFormatException e10) {
                    return (U) Reflect.initValue(this.toClass);
                }
            }
            if (wrapper == Double.class) {
                if (Number.class.isAssignableFrom(cls)) {
                    return (U) Double.valueOf(((Number) obj).doubleValue());
                }
                if (wrapper2 == Boolean.class) {
                    return ((Boolean) obj).booleanValue() ? (U) Double.valueOf(1.0d) : (U) Double.valueOf(0.0d);
                }
                try {
                    return (U) Double.valueOf(obj.toString().trim());
                } catch (NumberFormatException e11) {
                    return (U) Reflect.initValue(this.toClass);
                }
            }
            if (this.toClass == BigDecimal.class) {
                if (wrapper2 == Boolean.class) {
                    return ((Boolean) obj).booleanValue() ? (U) BigDecimal.ONE : (U) BigDecimal.ZERO;
                }
                try {
                    return (U) new BigDecimal(obj.toString().trim());
                } catch (NumberFormatException e12) {
                    return null;
                }
            }
            if (this.toClass == BigInteger.class) {
                if (wrapper2 == Boolean.class) {
                    return ((Boolean) obj).booleanValue() ? (U) BigInteger.ONE : (U) BigInteger.ZERO;
                }
                try {
                    return (U) new BigDecimal(obj.toString().trim()).toBigInteger();
                } catch (NumberFormatException e13) {
                    return null;
                }
            }
            if (this.toClass == Year.class) {
                if (Number.class.isAssignableFrom(wrapper2)) {
                    return (U) Year.of(((Number) obj).intValue());
                }
                try {
                    return (U) Year.parse(obj.toString().trim());
                } catch (DateTimeParseException e14) {
                    return null;
                }
            }
            if (wrapper == Boolean.class) {
                String trim9 = obj.toString().toLowerCase().trim();
                if (Convert.TRUE_VALUES.contains(trim9)) {
                    return (U) Boolean.TRUE;
                }
                if (Convert.FALSE_VALUES.contains(trim9)) {
                    return (U) Boolean.FALSE;
                }
                if (this.toClass == Boolean.class) {
                    return null;
                }
                return (U) false;
            }
            if (wrapper == Character.class) {
                if (wrapper2 == Boolean.class) {
                    return ((Boolean) obj).booleanValue() ? (U) '1' : (U) '0';
                }
                if (obj.toString().length() < 1) {
                    return (U) Reflect.initValue(this.toClass);
                }
                return (U) Character.valueOf(obj.toString().charAt(0));
            }
            if (cls == String.class && this.toClass == URL.class) {
                try {
                    return (U) new URI(obj.toString()).toURL();
                } catch (Exception e15) {
                    return null;
                }
            }
            if (Date.class.isAssignableFrom(cls)) {
                if (Timestamp.class == cls) {
                    if (LocalDateTime.class == this.toClass) {
                        return (U) ((Timestamp) obj).toLocalDateTime();
                    }
                    return (U) toDate(((Timestamp) obj).getTime(), ((Timestamp) obj).getNanos(), this.toClass);
                }
                if (java.sql.Date.class == cls && LocalDate.class == this.toClass) {
                    return (U) ((java.sql.Date) obj).toLocalDate();
                }
                if (Time.class == cls && LocalTime.class == this.toClass) {
                    return (U) ((Time) obj).toLocalTime();
                }
                return (U) toDate(((Date) obj).getTime(), this.toClass);
            }
            if (Temporal.class.isAssignableFrom(cls)) {
                if (LocalDateTime.class == cls && Timestamp.class == this.toClass) {
                    return (U) Timestamp.valueOf((LocalDateTime) obj);
                }
                if (LocalDateTime.class == cls && Temporal.class.isAssignableFrom(this.toClass)) {
                    return (U) toDate(((LocalDateTime) obj).toInstant(OffsetTime.now().getOffset()).toEpochMilli(), ((LocalDateTime) obj).getNano(), this.toClass);
                }
                if (LocalDate.class == cls && java.sql.Date.class == this.toClass) {
                    return (U) java.sql.Date.valueOf((LocalDate) obj);
                }
                if (LocalTime.class == cls && Time.class == this.toClass) {
                    return (U) Time.valueOf((LocalTime) obj);
                }
                if (OffsetDateTime.class == cls && (Timestamp.class == this.toClass || Temporal.class.isAssignableFrom(this.toClass))) {
                    return (U) toDate(((OffsetDateTime) obj).toInstant().toEpochMilli(), ((OffsetDateTime) obj).getNano(), this.toClass);
                }
                if (Instant.class == cls && (Timestamp.class == this.toClass || Temporal.class.isAssignableFrom(this.toClass))) {
                    return (U) toDate(((Instant) obj).toEpochMilli(), ((Instant) obj).getNano(), this.toClass);
                }
                return (U) toDate(((Long) Convert.convert(obj, Long.class)).longValue(), this.toClass);
            }
            if (wrapper2 == Long.class && Date.class.isAssignableFrom(this.toClass)) {
                return (U) toDate(((Long) obj).longValue(), this.toClass);
            }
            if (wrapper2 == Long.class && Temporal.class.isAssignableFrom(this.toClass)) {
                return (U) toDate(((Long) obj).longValue(), this.toClass);
            }
            if (cls == String.class && this.toClass == java.sql.Date.class) {
                try {
                    return (U) java.sql.Date.valueOf(Convert.patchIso8601Date((String) obj));
                } catch (IllegalArgumentException e16) {
                    return null;
                }
            }
            if (cls == String.class && this.toClass == Time.class) {
                try {
                    return (U) Time.valueOf(Convert.patchFractionalSeconds(Convert.patchIso8601Time((String) obj)));
                } catch (IllegalArgumentException e17) {
                    return null;
                }
            }
            if (cls == String.class && this.toClass == Timestamp.class) {
                try {
                    return (U) Timestamp.valueOf(Convert.patchIso8601Timestamp((String) obj, false));
                } catch (IllegalArgumentException e18) {
                    return null;
                }
            }
            if (cls == String.class && this.toClass == LocalDate.class) {
                String patchIso8601Date = Convert.patchIso8601Date((String) obj);
                try {
                    return (U) java.sql.Date.valueOf(patchIso8601Date).toLocalDate();
                } catch (IllegalArgumentException e19) {
                    try {
                        return (U) LocalDate.parse(patchIso8601Date);
                    } catch (DateTimeParseException e20) {
                        return null;
                    }
                }
            }
            if (cls == String.class && this.toClass == LocalTime.class) {
                try {
                    return (U) LocalTime.parse(Convert.patchIso8601Time((String) obj));
                } catch (DateTimeParseException e21) {
                    return null;
                }
            }
            if (cls == String.class && this.toClass == OffsetTime.class) {
                try {
                    return (U) Time.valueOf((String) obj).toLocalTime().atOffset(OffsetTime.now().getOffset());
                } catch (IllegalArgumentException e22) {
                    try {
                        return (U) OffsetTime.parse((String) obj);
                    } catch (DateTimeParseException e23) {
                        return null;
                    }
                }
            }
            if (cls == String.class && this.toClass == LocalDateTime.class) {
                try {
                    return (U) LocalDateTime.parse(Convert.patchIso8601Timestamp((String) obj, true));
                } catch (DateTimeParseException e24) {
                    return null;
                }
            }
            if (cls == String.class && this.toClass == OffsetDateTime.class) {
                try {
                    return (U) Timestamp.valueOf((String) obj).toLocalDateTime().atOffset(OffsetDateTime.now().getOffset());
                } catch (IllegalArgumentException e25) {
                    try {
                        return (U) OffsetDateTime.parse(Convert.patchIso8601Timestamp((String) obj, true));
                    } catch (DateTimeParseException e26) {
                        return null;
                    }
                }
            }
            if (cls == String.class && this.toClass == Instant.class) {
                try {
                    return (U) Timestamp.valueOf((String) obj).toLocalDateTime().atOffset(OffsetDateTime.now().getOffset()).toInstant();
                } catch (IllegalArgumentException e27) {
                    try {
                        return (U) OffsetDateTime.parse(Convert.patchIso8601Timestamp((String) obj, true)).toInstant();
                    } catch (DateTimeParseException e28) {
                        return null;
                    }
                }
            }
            if (cls == String.class && this.toClass == YearToMonth.class) {
                U u = (U) YearToMonth.valueOf((String) obj);
                if (u != null) {
                    return u;
                }
                if (((String) obj).startsWith("INTERVAL")) {
                    try {
                        return (U) ((YearToMonth) ((Param) converterContext.dsl().parser().parseField((String) obj)).getValue());
                    } catch (Exception e29) {
                    }
                }
                try {
                    return (U) PostgresUtils.toYearToMonth(obj);
                } catch (Exception e30) {
                    return null;
                }
            }
            if (cls == String.class && this.toClass == DayToSecond.class) {
                U u2 = (U) DayToSecond.valueOf((String) obj);
                if (u2 != null) {
                    return u2;
                }
                if (((String) obj).startsWith("INTERVAL")) {
                    try {
                        return (U) ((DayToSecond) ((Param) converterContext.dsl().parser().parseField((String) obj)).getValue());
                    } catch (Exception e31) {
                    }
                }
                try {
                    return (U) PostgresUtils.toDayToSecond(obj);
                } catch (Exception e32) {
                    return null;
                }
            }
            if (cls == String.class && this.toClass == YearToSecond.class) {
                U u3 = (U) YearToSecond.valueOf((String) obj);
                if (u3 != null) {
                    return u3;
                }
                try {
                    return (U) PostgresUtils.toYearToSecond(obj);
                } catch (Exception e33) {
                    return null;
                }
            }
            if (Enum.class.isAssignableFrom(this.toClass) && (cls == String.class || (obj instanceof Enum) || (obj instanceof EnumType))) {
                try {
                    if (cls == String.class) {
                        literal = (String) obj;
                    } else {
                        literal = obj instanceof EnumType ? ((EnumType) obj).getLiteral() : ((Enum) obj).name();
                    }
                    String str = literal;
                    if (str == null) {
                        return null;
                    }
                    if (EnumType.class.isAssignableFrom(this.toClass)) {
                        for (U u4 : this.toClass.getEnumConstants()) {
                            if (str.equals(((EnumType) u4).getLiteral())) {
                                return u4;
                            }
                        }
                        return null;
                    }
                    return (U) Enum.valueOf(this.toClass, str);
                } catch (IllegalArgumentException e34) {
                    return null;
                }
            }
            if (cls == String.class && this.toClass == UUID.class) {
                try {
                    return (U) parseUUID((String) obj);
                } catch (IllegalArgumentException e35) {
                    return null;
                }
            }
            if (cls == String.class && this.toClass == JSON.class) {
                return (U) JSON.valueOf((String) obj);
            }
            if (cls == String.class && this.toClass == JSONB.class) {
                return (U) JSONB.valueOf((String) obj);
            }
            if (Map.class.isAssignableFrom(cls) && this.toClass == JSON.class) {
                return (U) JSON.valueOf(org.jooq.tools.json.JSONObject.toJSONString((Map) obj));
            }
            if (Map.class.isAssignableFrom(cls) && this.toClass == JSONB.class) {
                return (U) JSONB.valueOf(org.jooq.tools.json.JSONObject.toJSONString((Map) obj));
            }
            if (List.class.isAssignableFrom(cls) && this.toClass == JSON.class) {
                return (U) JSON.valueOf(org.jooq.tools.json.JSONArray.toJSONString((List) obj));
            }
            if (List.class.isAssignableFrom(cls) && this.toClass == JSONB.class) {
                return (U) JSONB.valueOf(org.jooq.tools.json.JSONArray.toJSONString((List) obj));
            }
            if (cls == JSON.class && Map.class.isAssignableFrom(this.toClass)) {
                try {
                    return (U) require(this.toClass, new JSONParser().parse(((JSON) obj).data(), containerFactoryForMaps(this.toClass)));
                } catch (ParseException e36) {
                    throw new DataTypeException("Error while mapping JSON to Map", e36);
                }
            }
            if (cls == JSONB.class && Map.class.isAssignableFrom(this.toClass)) {
                try {
                    return (U) require(this.toClass, new JSONParser().parse(((JSONB) obj).data(), containerFactoryForMaps(this.toClass)));
                } catch (ParseException e37) {
                    throw new DataTypeException("Error while mapping JSONB to Map", e37);
                }
            }
            if (cls == JSON.class && List.class.isAssignableFrom(this.toClass)) {
                try {
                    return (U) require(this.toClass, new JSONParser().parse(((JSON) obj).data(), containerFactoryForLists(this.toClass)));
                } catch (ParseException e38) {
                    throw new DataTypeException("Error while mapping JSON to List", e38);
                }
            }
            if (cls == JSONB.class && List.class.isAssignableFrom(this.toClass)) {
                try {
                    return (U) require(this.toClass, new JSONParser().parse(((JSONB) obj).data(), containerFactoryForLists(this.toClass)));
                } catch (ParseException e39) {
                    throw new DataTypeException("Error while mapping JSONB to List", e39);
                }
            }
            if (cls == JSON.class && _JSON.JSON_MAPPER != null) {
                try {
                    return (U) _JSON.JSON_READ_METHOD.invoke(_JSON.JSON_MAPPER, ((JSON) obj).data(), this.toClass);
                } catch (Exception e40) {
                    throw new DataTypeException("Error while mapping JSON to POJO using Jackson", e40);
                }
            }
            if (cls == JSONB.class && _JSON.JSON_MAPPER != null) {
                try {
                    return (U) _JSON.JSON_READ_METHOD.invoke(_JSON.JSON_MAPPER, ((JSONB) obj).data(), this.toClass);
                } catch (Exception e41) {
                    throw new DataTypeException("Error while mapping JSON to POJO using Jackson", e41);
                }
            }
            if (Map.class.isAssignableFrom(cls) && _JSON.JSON_MAPPER != null) {
                try {
                    return (U) _JSON.JSON_READ_METHOD.invoke(_JSON.JSON_MAPPER, _JSON.JSON_WRITE_METHOD.invoke(_JSON.JSON_MAPPER, obj), this.toClass);
                } catch (Exception e42) {
                    throw new DataTypeException("Error while mapping JSON to POJO using Jackson", e42);
                }
            }
            if (cls == XML.class && _XML.JAXB_AVAILABLE) {
                try {
                    return (U) JAXB.unmarshal(new StringReader(((XML) obj).data()), this.toClass);
                } catch (Exception e43) {
                    throw new DataTypeException("Error while mapping XML to POJO using JAXB", e43);
                }
            }
            if (Record.class.isAssignableFrom(cls)) {
                return (U) ((Record) obj).into((Class) this.toClass);
            }
            if (Struct.class.isAssignableFrom(cls)) {
                Struct struct = (Struct) obj;
                if (QualifiedRecord.class.isAssignableFrom(this.toClass)) {
                    try {
                        ?? r0 = (U) ((QualifiedRecord) this.toClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]));
                        r0.from(struct.getAttributes());
                        return r0;
                    } catch (Exception e44) {
                        throw new DataTypeException("Cannot convert from " + String.valueOf(cls) + " to " + String.valueOf(this.toClass), e44);
                    }
                }
            } else if (Collection.class.isAssignableFrom(cls) && Collection.class.isAssignableFrom(this.toClass)) {
                return copyCollection(cls, (Collection) obj);
            }
            for (Constructor<?> constructor : this.toClass.getConstructors()) {
                Class<?>[] parameterTypes = constructor.getParameterTypes();
                if (parameterTypes.length == 1 && parameterTypes[0] != this.toClass) {
                    try {
                        return (U) constructor.newInstance(Convert.convert(obj, parameterTypes[0]));
                    } catch (Exception e45) {
                    }
                }
            }
            for (Constructor<?> constructor2 : this.toClass.getDeclaredConstructors()) {
                Class<?>[] parameterTypes2 = constructor2.getParameterTypes();
                if (parameterTypes2.length == 1 && parameterTypes2[0] != this.toClass) {
                    try {
                        return (U) ((Constructor) Reflect.accessible(constructor2)).newInstance(Convert.convert(obj, parameterTypes2[0]));
                    } catch (Exception e46) {
                    }
                }
            }
            throw fail(obj, this.toClass);
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r0v14, types: [java.util.Collection] */
        private final U copyCollection(Class<?> cls, Collection<?> collection) {
            AbstractCollection arrayList;
            try {
                if (!this.toClass.isInterface()) {
                    arrayList = (Collection) this.toClass.newInstance();
                } else if (Set.class.isAssignableFrom(this.toClass)) {
                    arrayList = new LinkedHashSet();
                } else {
                    arrayList = new ArrayList();
                }
                arrayList.addAll(collection);
                return (U) arrayList;
            } catch (Exception e) {
                throw new DataTypeException("Cannot convert from " + String.valueOf(cls) + " to " + String.valueOf(this.toClass), e);
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        private static final <T> T require(Class<? extends T> type, Object obj) {
            if (obj == 0 || type.isInstance(obj)) {
                return obj;
            }
            throw new DataTypeException("Type " + String.valueOf(type) + " expected. Got: " + String.valueOf(obj.getClass()));
        }

        private static final ContainerFactory containerFactoryForMaps(final Class<?> mapClass) {
            return new ContainerFactory() { // from class: org.jooq.impl.Convert.ConvertAll.1
                @Override // org.jooq.tools.json.ContainerFactory
                public Map createObjectContainer() {
                    try {
                        if (mapClass == Map.class) {
                            return new LinkedHashMap();
                        }
                        return (Map) mapClass.getConstructor(new Class[0]).newInstance(new Object[0]);
                    } catch (Exception e) {
                        throw new DataTypeException("Error while mapping JSON to Map", e);
                    }
                }

                @Override // org.jooq.tools.json.ContainerFactory
                public List createArrayContainer() {
                    return new ArrayList();
                }
            };
        }

        private static final ContainerFactory containerFactoryForLists(final Class<?> listClass) {
            return new ContainerFactory() { // from class: org.jooq.impl.Convert.ConvertAll.2
                @Override // org.jooq.tools.json.ContainerFactory
                public Map createObjectContainer() {
                    return new LinkedHashMap();
                }

                @Override // org.jooq.tools.json.ContainerFactory
                public List createArrayContainer() {
                    try {
                        if (listClass == List.class) {
                            return new ArrayList();
                        }
                        return (List) listClass.getConstructor(new Class[0]).newInstance(new Object[0]);
                    } catch (Exception e) {
                        throw new DataTypeException("Error while mapping JSON to List", e);
                    }
                }
            };
        }

        @Override // org.jooq.ContextConverter
        public Object to(U to, ConverterContext scope) {
            return to;
        }

        private static <X> X toDate(long j, Class<X> cls) {
            return (X) toDate(j, 0, cls);
        }

        /* JADX WARN: Type inference failed for: r0v36, types: [java.util.Calendar, X] */
        private static <X> X toDate(long j, int i, Class<X> cls) {
            if (cls == java.sql.Date.class) {
                return (X) new java.sql.Date(j);
            }
            if (cls == Time.class) {
                return (X) new Time(j);
            }
            if (cls == Timestamp.class) {
                return (X) toTimestamp(j, i);
            }
            if (cls == Date.class) {
                return (X) new Date(j);
            }
            if (cls == Calendar.class) {
                ?? r0 = (X) Calendar.getInstance();
                r0.setTimeInMillis(j);
                return r0;
            }
            if (cls == LocalDate.class) {
                return (X) new java.sql.Date(j).toLocalDate();
            }
            if (cls == LocalTime.class) {
                return (X) new Time(j).toLocalTime();
            }
            if (cls == OffsetTime.class) {
                return (X) new Time(j).toLocalTime().atOffset(OffsetTime.now().getOffset());
            }
            if (cls == LocalDateTime.class) {
                return (X) toTimestamp(j, i).toLocalDateTime();
            }
            if (cls == OffsetDateTime.class) {
                return (X) toTimestamp(j, i).toLocalDateTime().atOffset(OffsetDateTime.now().getOffset());
            }
            if (cls != Instant.class) {
                throw fail(Long.valueOf(j), cls);
            }
            if (i == 0) {
                return (X) Instant.ofEpochMilli(j);
            }
            return (X) Instant.ofEpochSecond(j / 1000, i);
        }

        private static Timestamp toTimestamp(long time, int nanos) {
            if (nanos == 0) {
                return new Timestamp(time);
            }
            Timestamp ts = new Timestamp((time / 1000) * 1000);
            ts.setNanos(nanos);
            return ts;
        }

        private static final long millis(Temporal temporal) {
            if (temporal instanceof LocalDate) {
                LocalDate ld = (LocalDate) temporal;
                return java.sql.Date.valueOf(ld).getTime();
            }
            if (temporal instanceof LocalTime) {
                LocalTime lt = (LocalTime) temporal;
                return Time.valueOf(lt).getTime();
            }
            if (temporal instanceof LocalDateTime) {
                LocalDateTime ldt = (LocalDateTime) temporal;
                return Timestamp.valueOf(ldt).getTime();
            }
            if (temporal.isSupported(ChronoField.INSTANT_SECONDS)) {
                return (1000 * temporal.getLong(ChronoField.INSTANT_SECONDS)) + temporal.getLong(ChronoField.MILLI_OF_SECOND);
            }
            if (temporal.isSupported(ChronoField.MILLI_OF_DAY)) {
                return temporal.getLong(ChronoField.MILLI_OF_DAY);
            }
            throw fail(temporal, Long.class);
        }

        private static final UUID parseUUID(String string) {
            if (string == null) {
                return null;
            }
            if (string.contains("-")) {
                return UUID.fromString(string);
            }
            return UUID.fromString(Convert.UUID_PATTERN.matcher(string).replaceAll("$1-$2-$3-$4-$5"));
        }

        private static final DataTypeException fail(Object from, Class<?> toClass) {
            String message = "Cannot convert from " + String.valueOf(from) + " (" + String.valueOf(from.getClass()) + ") to " + String.valueOf(toClass);
            if (((from instanceof JSON) || (from instanceof JSONB)) && _JSON.JSON_MAPPER == null) {
                return new DataTypeException(message + ". Check your classpath to see if Jackson or Gson is available to jOOQ.");
            }
            if ((from instanceof XML) && !_XML.JAXB_AVAILABLE) {
                return new DataTypeException(message + ". Check your classpath to see if JAXB is available to jOOQ.");
            }
            return new DataTypeException(message);
        }
    }

    static final String patchFractionalSeconds(String string) {
        if (string.length() > 8) {
            return P_FRACTIONAL_SECONDS.matcher(string).replaceFirst("$1");
        }
        return string;
    }

    static final String patchIso8601Time(String s) {
        int l = s.length();
        int c1 = s.indexOf(58);
        if (c1 >= 0) {
            int c2 = s.indexOf(58, c1 + 1);
            if (c2 == -1) {
                return padLead2(s, c1) + ":" + padMid2(s, c1) + ":00";
            }
            if (l < 8 || c2 != l - 3 || c1 != l - 6) {
                return padLead2(s, c1) + ":" + padMid2(s, c1, c2) + ":" + padMid2(s, c2);
            }
        } else if (s.indexOf(46) >= 0) {
            return patchIso8601Time(s.replace('.', ':'));
        }
        return s;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final String patchIso8601Timestamp(String s, boolean t) {
        if (s.endsWith(" UTC")) {
            s = s.replace(" UTC", "Z");
        }
        int l = s.length();
        int d1 = s.indexOf(45);
        int d2 = s.indexOf(45, d1 + 1);
        int ss = s.indexOf(32, d2 + 1);
        int st = s.indexOf(84, d2 + 1);
        int sx = Math.max(ss, st);
        int c1 = s.indexOf(58, sx + 1);
        int c2 = s.indexOf(58, c1 + 1);
        if (d1 == -1 || d2 == -1) {
            return s;
        }
        if (sx == -1) {
            return padLead4(s, d1) + "-" + padMid2(s, d1, d2) + "-" + padMid2(s, d2) + (t ? "T00:00:00" : " 00:00:00");
        }
        if (c2 == -1) {
            return padLead4(s, d1) + "-" + padMid2(s, d1, d2) + "-" + padMid2(s, d2, sx) + (t ? 'T' : ' ') + padMid2(s, sx, c1) + ":" + padMid2(s, c1) + ":00";
        }
        if (t == (st == -1) || l - c2 < 3 || c2 - c1 < 3 || c1 - sx < 3 || sx - d2 < 3 || d2 - d1 < 3) {
            return padLead4(s, d1) + "-" + padMid2(s, d1, d2) + "-" + padMid2(s, d2, sx) + (t ? 'T' : ' ') + padMid2(s, sx, c1) + ":" + padMid2(s, c1, c2) + ":" + padMid2(s, c2);
        }
        return s;
    }

    static final String patchIso8601Date(String s) {
        if (s.endsWith(" UTC")) {
            s = s.replace(" UTC", "Z");
        }
        int l = s.length();
        int d1 = s.indexOf(45);
        int d2 = s.indexOf(45, d1 + 1);
        int ss = s.indexOf(32, d2 + 1);
        int st = s.indexOf(84, d2 + 1);
        int sx = Math.max(ss, st);
        if (d1 == -1 || d2 == -1) {
            return s;
        }
        if (sx == -1) {
            if (l - d2 < 3 || d2 - d1 < 3) {
                return padLead4(s, d1) + "-" + padMid2(s, d1, d2) + "-" + padMid2(s, d2);
            }
            return s;
        }
        return padLead4(s, d1) + "-" + padMid2(s, d1, d2) + "-" + padMid2(s, d2, sx);
    }

    private static final String padLead2(String s, int i1) {
        return StringUtils.leftPad(s.substring(0, i1), 2, '0');
    }

    private static final String padLead4(String s, int i1) {
        return StringUtils.leftPad(s.substring(0, i1), 4, '0');
    }

    private static final String padMid2(String s, int i1) {
        return StringUtils.leftPad(s.substring(i1 + 1), 2, '0');
    }

    private static final String padMid2(String s, int i1, int i2) {
        return StringUtils.leftPad(s.substring(i1 + 1, i2), 2, '0');
    }
}

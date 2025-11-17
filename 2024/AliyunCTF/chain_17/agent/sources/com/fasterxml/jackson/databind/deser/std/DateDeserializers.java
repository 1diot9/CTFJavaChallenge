package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.TimeZone;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/DateDeserializers.class */
public class DateDeserializers {
    private static final HashSet<String> _utilClasses = new HashSet<>();

    static {
        _utilClasses.add("java.util.Calendar");
        _utilClasses.add("java.util.GregorianCalendar");
        _utilClasses.add("java.util.Date");
    }

    public static JsonDeserializer<?> find(Class<?> rawType, String clsName) {
        if (_utilClasses.contains(clsName)) {
            if (rawType == Calendar.class) {
                return new CalendarDeserializer();
            }
            if (rawType == Date.class) {
                return DateDeserializer.instance;
            }
            if (rawType == GregorianCalendar.class) {
                return new CalendarDeserializer(GregorianCalendar.class);
            }
            return null;
        }
        return null;
    }

    public static boolean hasDeserializerFor(Class<?> rawType) {
        return _utilClasses.contains(rawType.getName());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/DateDeserializers$DateBasedDeserializer.class */
    public static abstract class DateBasedDeserializer<T> extends StdScalarDeserializer<T> implements ContextualDeserializer {
        protected final DateFormat _customFormat;
        protected final String _formatString;

        /* renamed from: withDateFormat */
        protected abstract DateBasedDeserializer<T> withDateFormat2(DateFormat dateFormat, String str);

        protected DateBasedDeserializer(Class<?> clz) {
            super(clz);
            this._customFormat = null;
            this._formatString = null;
        }

        protected DateBasedDeserializer(DateBasedDeserializer<T> base, DateFormat format, String formatStr) {
            super(base._valueClass);
            this._customFormat = format;
            this._formatString = formatStr;
        }

        @Override // com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
        public LogicalType logicalType() {
            return LogicalType.DateTime;
        }

        public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
            DateFormat df;
            DateFormat df2;
            JsonFormat.Value format = findFormatOverrides(ctxt, property, handledType());
            if (format != null) {
                TimeZone tz = format.getTimeZone();
                Boolean lenient = format.getLenient();
                if (format.hasPattern()) {
                    String pattern = format.getPattern();
                    Locale loc = format.hasLocale() ? format.getLocale() : ctxt.getLocale();
                    SimpleDateFormat df3 = new SimpleDateFormat(pattern, loc);
                    if (tz == null) {
                        tz = ctxt.getTimeZone();
                    }
                    df3.setTimeZone(tz);
                    if (lenient != null) {
                        df3.setLenient(lenient.booleanValue());
                    }
                    return withDateFormat2(df3, pattern);
                }
                if (tz != null) {
                    DateFormat df4 = ctxt.getConfig().getDateFormat();
                    if (df4.getClass() == StdDateFormat.class) {
                        Locale loc2 = format.hasLocale() ? format.getLocale() : ctxt.getLocale();
                        StdDateFormat std = ((StdDateFormat) df4).withTimeZone(tz).withLocale(loc2);
                        if (lenient != null) {
                            std = std.withLenient(lenient);
                        }
                        df2 = std;
                    } else {
                        df2 = (DateFormat) df4.clone();
                        df2.setTimeZone(tz);
                        if (lenient != null) {
                            df2.setLenient(lenient.booleanValue());
                        }
                    }
                    return withDateFormat2(df2, this._formatString);
                }
                if (lenient != null) {
                    DateFormat df5 = ctxt.getConfig().getDateFormat();
                    String pattern2 = this._formatString;
                    if (df5.getClass() == StdDateFormat.class) {
                        StdDateFormat std2 = ((StdDateFormat) df5).withLenient(lenient);
                        df = std2;
                        pattern2 = std2.toPattern();
                    } else {
                        df = (DateFormat) df5.clone();
                        df.setLenient(lenient.booleanValue());
                        if (df instanceof SimpleDateFormat) {
                            ((SimpleDateFormat) df).toPattern();
                        }
                    }
                    if (pattern2 == null) {
                        pattern2 = "[unknown]";
                    }
                    return withDateFormat2(df, pattern2);
                }
            }
            return this;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.fasterxml.jackson.databind.deser.std.StdDeserializer
        public Date _parseDate(JsonParser p, DeserializationContext ctxt) throws IOException {
            Date parse;
            if (this._customFormat != null && p.hasToken(JsonToken.VALUE_STRING)) {
                String str = p.getText().trim();
                if (str.isEmpty()) {
                    CoercionAction act = _checkFromStringCoercion(ctxt, str);
                    switch (act) {
                        case AsEmpty:
                            return new Date(0L);
                        case AsNull:
                        case TryConvert:
                        default:
                            return null;
                    }
                }
                synchronized (this._customFormat) {
                    try {
                        parse = this._customFormat.parse(str);
                    } catch (ParseException e) {
                        return (Date) ctxt.handleWeirdStringValue(handledType(), str, "expected format \"%s\"", this._formatString);
                    }
                }
                return parse;
            }
            return super._parseDate(p, ctxt);
        }
    }

    @JacksonStdImpl
    /* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/DateDeserializers$CalendarDeserializer.class */
    public static class CalendarDeserializer extends DateBasedDeserializer<Calendar> {
        protected final Constructor<Calendar> _defaultCtor;

        @Override // com.fasterxml.jackson.databind.deser.std.DateDeserializers.DateBasedDeserializer, com.fasterxml.jackson.databind.deser.ContextualDeserializer
        public /* bridge */ /* synthetic */ JsonDeserializer createContextual(DeserializationContext deserializationContext, BeanProperty beanProperty) throws JsonMappingException {
            return super.createContextual(deserializationContext, beanProperty);
        }

        @Override // com.fasterxml.jackson.databind.deser.std.DateDeserializers.DateBasedDeserializer, com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
        public /* bridge */ /* synthetic */ LogicalType logicalType() {
            return super.logicalType();
        }

        public CalendarDeserializer() {
            super(Calendar.class);
            this._defaultCtor = null;
        }

        public CalendarDeserializer(Class<? extends Calendar> cc) {
            super(cc);
            this._defaultCtor = ClassUtil.findConstructor(cc, false);
        }

        public CalendarDeserializer(CalendarDeserializer src, DateFormat df, String formatString) {
            super(src, df, formatString);
            this._defaultCtor = src._defaultCtor;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.fasterxml.jackson.databind.deser.std.DateDeserializers.DateBasedDeserializer
        /* renamed from: withDateFormat, reason: merged with bridge method [inline-methods] */
        public DateBasedDeserializer<Calendar> withDateFormat2(DateFormat df, String formatString) {
            return new CalendarDeserializer(this, df, formatString);
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public Object getEmptyValue(DeserializationContext ctxt) {
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTimeInMillis(0L);
            return cal;
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public Calendar deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            Date d = _parseDate(p, ctxt);
            if (d == null) {
                return null;
            }
            if (this._defaultCtor == null) {
                return ctxt.constructCalendar(d);
            }
            try {
                Calendar c = this._defaultCtor.newInstance(new Object[0]);
                c.setTimeInMillis(d.getTime());
                TimeZone tz = ctxt.getTimeZone();
                if (tz != null) {
                    c.setTimeZone(tz);
                }
                return c;
            } catch (Exception e) {
                return (Calendar) ctxt.handleInstantiationProblem(handledType(), d, e);
            }
        }
    }

    @JacksonStdImpl
    /* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/DateDeserializers$DateDeserializer.class */
    public static class DateDeserializer extends DateBasedDeserializer<Date> {
        public static final DateDeserializer instance = new DateDeserializer();

        @Override // com.fasterxml.jackson.databind.deser.std.DateDeserializers.DateBasedDeserializer, com.fasterxml.jackson.databind.deser.ContextualDeserializer
        public /* bridge */ /* synthetic */ JsonDeserializer createContextual(DeserializationContext deserializationContext, BeanProperty beanProperty) throws JsonMappingException {
            return super.createContextual(deserializationContext, beanProperty);
        }

        @Override // com.fasterxml.jackson.databind.deser.std.DateDeserializers.DateBasedDeserializer, com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
        public /* bridge */ /* synthetic */ LogicalType logicalType() {
            return super.logicalType();
        }

        public DateDeserializer() {
            super(Date.class);
        }

        public DateDeserializer(DateDeserializer base, DateFormat df, String formatString) {
            super(base, df, formatString);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.fasterxml.jackson.databind.deser.std.DateDeserializers.DateBasedDeserializer
        /* renamed from: withDateFormat */
        public DateBasedDeserializer<Date> withDateFormat2(DateFormat df, String formatString) {
            return new DateDeserializer(this, df, formatString);
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public Object getEmptyValue(DeserializationContext ctxt) {
            return new Date(0L);
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return _parseDate(p, ctxt);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/DateDeserializers$SqlDateDeserializer.class */
    public static class SqlDateDeserializer extends DateBasedDeserializer<java.sql.Date> {
        @Override // com.fasterxml.jackson.databind.deser.std.DateDeserializers.DateBasedDeserializer, com.fasterxml.jackson.databind.deser.ContextualDeserializer
        public /* bridge */ /* synthetic */ JsonDeserializer createContextual(DeserializationContext deserializationContext, BeanProperty beanProperty) throws JsonMappingException {
            return super.createContextual(deserializationContext, beanProperty);
        }

        @Override // com.fasterxml.jackson.databind.deser.std.DateDeserializers.DateBasedDeserializer, com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
        public /* bridge */ /* synthetic */ LogicalType logicalType() {
            return super.logicalType();
        }

        public SqlDateDeserializer() {
            super(java.sql.Date.class);
        }

        public SqlDateDeserializer(SqlDateDeserializer src, DateFormat df, String formatString) {
            super(src, df, formatString);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.fasterxml.jackson.databind.deser.std.DateDeserializers.DateBasedDeserializer
        /* renamed from: withDateFormat */
        public DateBasedDeserializer<java.sql.Date> withDateFormat2(DateFormat df, String formatString) {
            return new SqlDateDeserializer(this, df, formatString);
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public Object getEmptyValue(DeserializationContext ctxt) {
            return new java.sql.Date(0L);
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public java.sql.Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            Date d = _parseDate(p, ctxt);
            if (d == null) {
                return null;
            }
            return new java.sql.Date(d.getTime());
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/DateDeserializers$TimestampDeserializer.class */
    public static class TimestampDeserializer extends DateBasedDeserializer<Timestamp> {
        @Override // com.fasterxml.jackson.databind.deser.std.DateDeserializers.DateBasedDeserializer, com.fasterxml.jackson.databind.deser.ContextualDeserializer
        public /* bridge */ /* synthetic */ JsonDeserializer createContextual(DeserializationContext deserializationContext, BeanProperty beanProperty) throws JsonMappingException {
            return super.createContextual(deserializationContext, beanProperty);
        }

        @Override // com.fasterxml.jackson.databind.deser.std.DateDeserializers.DateBasedDeserializer, com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
        public /* bridge */ /* synthetic */ LogicalType logicalType() {
            return super.logicalType();
        }

        public TimestampDeserializer() {
            super(Timestamp.class);
        }

        public TimestampDeserializer(TimestampDeserializer src, DateFormat df, String formatString) {
            super(src, df, formatString);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.fasterxml.jackson.databind.deser.std.DateDeserializers.DateBasedDeserializer
        /* renamed from: withDateFormat */
        public DateBasedDeserializer<Timestamp> withDateFormat2(DateFormat df, String formatString) {
            return new TimestampDeserializer(this, df, formatString);
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public Object getEmptyValue(DeserializationContext ctxt) {
            return new Timestamp(0L);
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public Timestamp deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            Date d = _parseDate(p, ctxt);
            if (d == null) {
                return null;
            }
            return new Timestamp(d.getTime());
        }
    }
}

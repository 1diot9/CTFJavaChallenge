package com.fasterxml.jackson.annotation;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Locale;
import java.util.TimeZone;

@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE})
@JacksonAnnotation
@Retention(RetentionPolicy.RUNTIME)
/* loaded from: server.jar:BOOT-INF/lib/jackson-annotations-2.15.3.jar:com/fasterxml/jackson/annotation/JsonFormat.class */
public @interface JsonFormat {
    public static final String DEFAULT_LOCALE = "##default";
    public static final String DEFAULT_TIMEZONE = "##default";

    /* loaded from: server.jar:BOOT-INF/lib/jackson-annotations-2.15.3.jar:com/fasterxml/jackson/annotation/JsonFormat$Feature.class */
    public enum Feature {
        ACCEPT_SINGLE_VALUE_AS_ARRAY,
        ACCEPT_CASE_INSENSITIVE_PROPERTIES,
        READ_UNKNOWN_ENUM_VALUES_AS_NULL,
        READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE,
        READ_DATE_TIMESTAMPS_AS_NANOSECONDS,
        ACCEPT_CASE_INSENSITIVE_VALUES,
        WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS,
        WRITE_DATES_WITH_ZONE_ID,
        WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED,
        WRITE_SORTED_MAP_ENTRIES,
        ADJUST_DATES_TO_CONTEXT_TIME_ZONE
    }

    String pattern() default "";

    Shape shape() default Shape.ANY;

    String locale() default "##default";

    String timezone() default "##default";

    OptBoolean lenient() default OptBoolean.DEFAULT;

    Feature[] with() default {};

    Feature[] without() default {};

    /* loaded from: server.jar:BOOT-INF/lib/jackson-annotations-2.15.3.jar:com/fasterxml/jackson/annotation/JsonFormat$Shape.class */
    public enum Shape {
        ANY,
        NATURAL,
        SCALAR,
        ARRAY,
        OBJECT,
        NUMBER,
        NUMBER_FLOAT,
        NUMBER_INT,
        STRING,
        BOOLEAN,
        BINARY;

        public boolean isNumeric() {
            return this == NUMBER || this == NUMBER_INT || this == NUMBER_FLOAT;
        }

        public boolean isStructured() {
            return this == OBJECT || this == ARRAY;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jackson-annotations-2.15.3.jar:com/fasterxml/jackson/annotation/JsonFormat$Features.class */
    public static class Features {
        private final int _enabled;
        private final int _disabled;
        private static final Features EMPTY = new Features(0, 0);

        private Features(int e, int d) {
            this._enabled = e;
            this._disabled = d;
        }

        public static Features empty() {
            return EMPTY;
        }

        public static Features construct(JsonFormat f) {
            return construct(f.with(), f.without());
        }

        public static Features construct(Feature[] enabled, Feature[] disabled) {
            int e = 0;
            for (Feature f : enabled) {
                e |= 1 << f.ordinal();
            }
            int d = 0;
            for (Feature f2 : disabled) {
                d |= 1 << f2.ordinal();
            }
            return new Features(e, d);
        }

        public Features withOverrides(Features overrides) {
            if (overrides == null) {
                return this;
            }
            int overrideD = overrides._disabled;
            int overrideE = overrides._enabled;
            if (overrideD == 0 && overrideE == 0) {
                return this;
            }
            if (this._enabled == 0 && this._disabled == 0) {
                return overrides;
            }
            int newE = (this._enabled & (overrideD ^ (-1))) | overrideE;
            int newD = (this._disabled & (overrideE ^ (-1))) | overrideD;
            if (newE == this._enabled && newD == this._disabled) {
                return this;
            }
            return new Features(newE, newD);
        }

        public Features with(Feature... features) {
            int e = this._enabled;
            for (Feature f : features) {
                e |= 1 << f.ordinal();
            }
            return e == this._enabled ? this : new Features(e, this._disabled);
        }

        public Features without(Feature... features) {
            int d = this._disabled;
            for (Feature f : features) {
                d |= 1 << f.ordinal();
            }
            return d == this._disabled ? this : new Features(this._enabled, d);
        }

        public Boolean get(Feature f) {
            int mask = 1 << f.ordinal();
            if ((this._disabled & mask) != 0) {
                return Boolean.FALSE;
            }
            if ((this._enabled & mask) != 0) {
                return Boolean.TRUE;
            }
            return null;
        }

        public String toString() {
            if (this == EMPTY) {
                return "EMPTY";
            }
            return String.format("(enabled=0x%x,disabled=0x%x)", Integer.valueOf(this._enabled), Integer.valueOf(this._disabled));
        }

        public int hashCode() {
            return this._disabled + this._enabled;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o == null || o.getClass() != getClass()) {
                return false;
            }
            Features other = (Features) o;
            return other._enabled == this._enabled && other._disabled == this._disabled;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jackson-annotations-2.15.3.jar:com/fasterxml/jackson/annotation/JsonFormat$Value.class */
    public static class Value implements JacksonAnnotationValue<JsonFormat>, Serializable {
        private static final long serialVersionUID = 1;
        private static final Value EMPTY = new Value();
        private final String _pattern;
        private final Shape _shape;
        private final Locale _locale;
        private final String _timezoneStr;
        private final Boolean _lenient;
        private final Features _features;
        private transient TimeZone _timezone;

        public Value() {
            this("", Shape.ANY, "", "", Features.empty(), (Boolean) null);
        }

        public Value(JsonFormat ann) {
            this(ann.pattern(), ann.shape(), ann.locale(), ann.timezone(), Features.construct(ann), ann.lenient().asBoolean());
        }

        public Value(String p, Shape sh, String localeStr, String tzStr, Features f, Boolean lenient) {
            this(p, sh, (localeStr == null || localeStr.length() == 0 || "##default".equals(localeStr)) ? null : new Locale(localeStr), (tzStr == null || tzStr.length() == 0 || "##default".equals(tzStr)) ? null : tzStr, null, f, lenient);
        }

        public Value(String p, Shape sh, Locale l, TimeZone tz, Features f, Boolean lenient) {
            this._pattern = p == null ? "" : p;
            this._shape = sh == null ? Shape.ANY : sh;
            this._locale = l;
            this._timezone = tz;
            this._timezoneStr = null;
            this._features = f == null ? Features.empty() : f;
            this._lenient = lenient;
        }

        public Value(String p, Shape sh, Locale l, String tzStr, TimeZone tz, Features f, Boolean lenient) {
            this._pattern = p == null ? "" : p;
            this._shape = sh == null ? Shape.ANY : sh;
            this._locale = l;
            this._timezone = tz;
            this._timezoneStr = tzStr;
            this._features = f == null ? Features.empty() : f;
            this._lenient = lenient;
        }

        @Deprecated
        public Value(String p, Shape sh, Locale l, String tzStr, TimeZone tz, Features f) {
            this(p, sh, l, tzStr, tz, f, null);
        }

        @Deprecated
        public Value(String p, Shape sh, String localeStr, String tzStr, Features f) {
            this(p, sh, localeStr, tzStr, f, (Boolean) null);
        }

        @Deprecated
        public Value(String p, Shape sh, Locale l, TimeZone tz, Features f) {
            this(p, sh, l, tz, f, (Boolean) null);
        }

        public static final Value empty() {
            return EMPTY;
        }

        public static Value merge(Value base, Value overrides) {
            return base == null ? overrides : base.withOverrides(overrides);
        }

        public static Value mergeAll(Value... values) {
            Value result = null;
            for (Value curr : values) {
                if (curr != null) {
                    result = result == null ? curr : result.withOverrides(curr);
                }
            }
            return result;
        }

        public static final Value from(JsonFormat ann) {
            return ann == null ? EMPTY : new Value(ann);
        }

        public final Value withOverrides(Value overrides) {
            Features f;
            TimeZone tz;
            if (overrides == null || overrides == EMPTY || overrides == this) {
                return this;
            }
            if (this == EMPTY) {
                return overrides;
            }
            String p = overrides._pattern;
            if (p == null || p.isEmpty()) {
                p = this._pattern;
            }
            Shape sh = overrides._shape;
            if (sh == Shape.ANY) {
                sh = this._shape;
            }
            Locale l = overrides._locale;
            if (l == null) {
                l = this._locale;
            }
            Features f2 = this._features;
            if (f2 == null) {
                f = overrides._features;
            } else {
                f = f2.withOverrides(overrides._features);
            }
            Boolean lenient = overrides._lenient;
            if (lenient == null) {
                lenient = this._lenient;
            }
            String tzStr = overrides._timezoneStr;
            if (tzStr == null || tzStr.isEmpty()) {
                tzStr = this._timezoneStr;
                tz = this._timezone;
            } else {
                tz = overrides._timezone;
            }
            return new Value(p, sh, l, tzStr, tz, f, lenient);
        }

        public static Value forPattern(String p) {
            return new Value(p, null, null, null, null, Features.empty(), null);
        }

        public static Value forShape(Shape sh) {
            return new Value("", sh, null, null, null, Features.empty(), null);
        }

        public static Value forLeniency(boolean lenient) {
            return new Value("", null, null, null, null, Features.empty(), Boolean.valueOf(lenient));
        }

        public Value withPattern(String p) {
            return new Value(p, this._shape, this._locale, this._timezoneStr, this._timezone, this._features, this._lenient);
        }

        public Value withShape(Shape s) {
            if (s == this._shape) {
                return this;
            }
            return new Value(this._pattern, s, this._locale, this._timezoneStr, this._timezone, this._features, this._lenient);
        }

        public Value withLocale(Locale l) {
            return new Value(this._pattern, this._shape, l, this._timezoneStr, this._timezone, this._features, this._lenient);
        }

        public Value withTimeZone(TimeZone tz) {
            return new Value(this._pattern, this._shape, this._locale, null, tz, this._features, this._lenient);
        }

        public Value withLenient(Boolean lenient) {
            if (lenient == this._lenient) {
                return this;
            }
            return new Value(this._pattern, this._shape, this._locale, this._timezoneStr, this._timezone, this._features, lenient);
        }

        public Value withFeature(Feature f) {
            Features newFeats = this._features.with(f);
            return newFeats == this._features ? this : new Value(this._pattern, this._shape, this._locale, this._timezoneStr, this._timezone, newFeats, this._lenient);
        }

        public Value withoutFeature(Feature f) {
            Features newFeats = this._features.without(f);
            return newFeats == this._features ? this : new Value(this._pattern, this._shape, this._locale, this._timezoneStr, this._timezone, newFeats, this._lenient);
        }

        @Override // com.fasterxml.jackson.annotation.JacksonAnnotationValue
        public Class<JsonFormat> valueFor() {
            return JsonFormat.class;
        }

        public String getPattern() {
            return this._pattern;
        }

        public Shape getShape() {
            return this._shape;
        }

        public Locale getLocale() {
            return this._locale;
        }

        public Boolean getLenient() {
            return this._lenient;
        }

        public boolean isLenient() {
            return Boolean.TRUE.equals(this._lenient);
        }

        public String timeZoneAsString() {
            if (this._timezone != null) {
                return this._timezone.getID();
            }
            return this._timezoneStr;
        }

        public TimeZone getTimeZone() {
            TimeZone tz = this._timezone;
            if (tz == null) {
                if (this._timezoneStr == null) {
                    return null;
                }
                tz = TimeZone.getTimeZone(this._timezoneStr);
                this._timezone = tz;
            }
            return tz;
        }

        public boolean hasShape() {
            return this._shape != Shape.ANY;
        }

        public boolean hasPattern() {
            return this._pattern != null && this._pattern.length() > 0;
        }

        public boolean hasLocale() {
            return this._locale != null;
        }

        public boolean hasTimeZone() {
            return (this._timezone == null && (this._timezoneStr == null || this._timezoneStr.isEmpty())) ? false : true;
        }

        public boolean hasLenient() {
            return this._lenient != null;
        }

        public Boolean getFeature(Feature f) {
            return this._features.get(f);
        }

        public Features getFeatures() {
            return this._features;
        }

        public String toString() {
            return String.format("JsonFormat.Value(pattern=%s,shape=%s,lenient=%s,locale=%s,timezone=%s,features=%s)", this._pattern, this._shape, this._lenient, this._locale, this._timezoneStr, this._features);
        }

        public int hashCode() {
            int hash = this._timezoneStr == null ? 1 : this._timezoneStr.hashCode();
            if (this._pattern != null) {
                hash ^= this._pattern.hashCode();
            }
            int hash2 = hash + this._shape.hashCode();
            if (this._lenient != null) {
                hash2 ^= this._lenient.hashCode();
            }
            if (this._locale != null) {
                hash2 += this._locale.hashCode();
            }
            return hash2 ^ this._features.hashCode();
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o == null || o.getClass() != getClass()) {
                return false;
            }
            Value other = (Value) o;
            return this._shape == other._shape && this._features.equals(other._features) && _equal(this._lenient, other._lenient) && _equal(this._timezoneStr, other._timezoneStr) && _equal(this._pattern, other._pattern) && _equal(this._timezone, other._timezone) && _equal(this._locale, other._locale);
        }

        private static <T> boolean _equal(T value1, T value2) {
            if (value1 == null) {
                return value2 == null;
            }
            if (value2 == null) {
                return false;
            }
            return value1.equals(value2);
        }
    }
}

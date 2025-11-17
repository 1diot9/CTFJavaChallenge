package com.fasterxml.jackson.datatype.jsr310.deser;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.io.NumberInput;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.jsr310.DecimalUtils;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-datatype-jsr310-2.15.3.jar:com/fasterxml/jackson/datatype/jsr310/deser/InstantDeserializer.class */
public class InstantDeserializer<T extends Temporal> extends JSR310DateTimeDeserializerBase<T> {
    private static final long serialVersionUID = 1;
    protected static final Pattern ISO8601_COLONLESS_OFFSET_REGEX = Pattern.compile("[+-][0-9]{4}(?=\\[|$)");
    public static final InstantDeserializer<Instant> INSTANT = new InstantDeserializer<>(Instant.class, DateTimeFormatter.ISO_INSTANT, Instant::from, a -> {
        return Instant.ofEpochMilli(a.value);
    }, a2 -> {
        return Instant.ofEpochSecond(a2.integer, a2.fraction);
    }, null, true);
    public static final InstantDeserializer<OffsetDateTime> OFFSET_DATE_TIME = new InstantDeserializer<>(OffsetDateTime.class, DateTimeFormatter.ISO_OFFSET_DATE_TIME, OffsetDateTime::from, a -> {
        return OffsetDateTime.ofInstant(Instant.ofEpochMilli(a.value), a.zoneId);
    }, a2 -> {
        return OffsetDateTime.ofInstant(Instant.ofEpochSecond(a2.integer, a2.fraction), a2.zoneId);
    }, (d, z) -> {
        return (d.isEqual(OffsetDateTime.MIN) || d.isEqual(OffsetDateTime.MAX)) ? d : d.withOffsetSameInstant(z.getRules().getOffset(d.toLocalDateTime()));
    }, true);
    public static final InstantDeserializer<ZonedDateTime> ZONED_DATE_TIME = new InstantDeserializer<>(ZonedDateTime.class, DateTimeFormatter.ISO_ZONED_DATE_TIME, ZonedDateTime::from, a -> {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(a.value), a.zoneId);
    }, a2 -> {
        return ZonedDateTime.ofInstant(Instant.ofEpochSecond(a2.integer, a2.fraction), a2.zoneId);
    }, (v0, v1) -> {
        return v0.withZoneSameInstant(v1);
    }, false);
    protected final Function<FromIntegerArguments, T> fromMilliseconds;
    protected final Function<FromDecimalArguments, T> fromNanoseconds;
    protected final Function<TemporalAccessor, T> parsedToValue;
    protected final BiFunction<T, ZoneId, T> adjust;
    protected final boolean replaceZeroOffsetAsZ;
    protected final Boolean _adjustToContextTZOverride;

    protected InstantDeserializer(Class<T> supportedType, DateTimeFormatter formatter, Function<TemporalAccessor, T> parsedToValue, Function<FromIntegerArguments, T> fromMilliseconds, Function<FromDecimalArguments, T> fromNanoseconds, BiFunction<T, ZoneId, T> adjust, boolean replaceZeroOffsetAsZ) {
        super(supportedType, formatter);
        this.parsedToValue = parsedToValue;
        this.fromMilliseconds = fromMilliseconds;
        this.fromNanoseconds = fromNanoseconds;
        this.adjust = adjust == null ? (d, z) -> {
            return d;
        } : adjust;
        this.replaceZeroOffsetAsZ = replaceZeroOffsetAsZ;
        this._adjustToContextTZOverride = null;
    }

    protected InstantDeserializer(InstantDeserializer<T> base, DateTimeFormatter f) {
        super(base.handledType(), f);
        this.parsedToValue = base.parsedToValue;
        this.fromMilliseconds = base.fromMilliseconds;
        this.fromNanoseconds = base.fromNanoseconds;
        this.adjust = base.adjust;
        this.replaceZeroOffsetAsZ = this._formatter == DateTimeFormatter.ISO_INSTANT;
        this._adjustToContextTZOverride = base._adjustToContextTZOverride;
    }

    protected InstantDeserializer(InstantDeserializer<T> base, Boolean adjustToContextTimezoneOverride) {
        super(base.handledType(), base._formatter);
        this.parsedToValue = base.parsedToValue;
        this.fromMilliseconds = base.fromMilliseconds;
        this.fromNanoseconds = base.fromNanoseconds;
        this.adjust = base.adjust;
        this.replaceZeroOffsetAsZ = base.replaceZeroOffsetAsZ;
        this._adjustToContextTZOverride = adjustToContextTimezoneOverride;
    }

    protected InstantDeserializer(InstantDeserializer<T> base, DateTimeFormatter f, Boolean leniency) {
        super(base.handledType(), f, leniency);
        this.parsedToValue = base.parsedToValue;
        this.fromMilliseconds = base.fromMilliseconds;
        this.fromNanoseconds = base.fromNanoseconds;
        this.adjust = base.adjust;
        this.replaceZeroOffsetAsZ = this._formatter == DateTimeFormatter.ISO_INSTANT;
        this._adjustToContextTZOverride = base._adjustToContextTZOverride;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.datatype.jsr310.deser.JSR310DateTimeDeserializerBase
    /* renamed from: withDateFormat */
    public InstantDeserializer<T> withDateFormat2(DateTimeFormatter dtf) {
        if (dtf == this._formatter) {
            return this;
        }
        return new InstantDeserializer<>(this, dtf);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.datatype.jsr310.deser.JSR310DateTimeDeserializerBase, com.fasterxml.jackson.datatype.jsr310.deser.JSR310DeserializerBase
    public InstantDeserializer<T> withLeniency(Boolean leniency) {
        return new InstantDeserializer<>(this, this._formatter, leniency);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.datatype.jsr310.deser.JSR310DateTimeDeserializerBase
    /* renamed from: withShape */
    public InstantDeserializer<T> withShape2(JsonFormat.Shape shape) {
        return this;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.datatype.jsr310.deser.JSR310DateTimeDeserializerBase
    public JSR310DateTimeDeserializerBase<?> _withFormatOverrides(DeserializationContext ctxt, BeanProperty property, JsonFormat.Value formatOverrides) {
        InstantDeserializer<T> deser = (InstantDeserializer) super._withFormatOverrides(ctxt, property, formatOverrides);
        Boolean B = formatOverrides.getFeature(JsonFormat.Feature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        if (!Objects.equals(B, deser._adjustToContextTZOverride)) {
            return new InstantDeserializer(deser, B);
        }
        return deser;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public T deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        switch (parser.currentTokenId()) {
            case 1:
                return _fromString(parser, context, context.extractScalarFromObject(parser, this, handledType()));
            case 2:
            case 4:
            case 5:
            case 9:
            case 10:
            case 11:
            default:
                return (T) _handleUnexpectedToken(context, parser, JsonToken.VALUE_STRING, JsonToken.VALUE_NUMBER_INT, JsonToken.VALUE_NUMBER_FLOAT);
            case 3:
                return (T) _deserializeFromArray(parser, context);
            case 6:
                return _fromString(parser, context, parser.getText());
            case 7:
                return _fromLong(context, parser.getLongValue());
            case 8:
                return _fromDecimal(context, parser.getDecimalValue());
            case 12:
                return (T) parser.getEmbeddedObject();
        }
    }

    protected boolean shouldAdjustToContextTimezone(DeserializationContext context) {
        return this._adjustToContextTZOverride != null ? this._adjustToContextTZOverride.booleanValue() : context.isEnabled(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
    }

    protected int _countPeriods(String str) {
        int commas = 0;
        int end = str.length();
        for (int i = 0; i < end; i++) {
            int ch2 = str.charAt(i);
            if (ch2 < 48 || ch2 > 57) {
                if (ch2 == 46) {
                    commas++;
                } else {
                    return -1;
                }
            }
        }
        return commas;
    }

    protected T _fromString(JsonParser jsonParser, DeserializationContext deserializationContext, String str) throws IOException {
        Temporal temporal;
        String trim = str.trim();
        if (trim.length() == 0) {
            return (T) _fromEmptyString(jsonParser, deserializationContext, trim);
        }
        if (this._formatter == DateTimeFormatter.ISO_INSTANT || this._formatter == DateTimeFormatter.ISO_OFFSET_DATE_TIME || this._formatter == DateTimeFormatter.ISO_ZONED_DATE_TIME) {
            int _countPeriods = _countPeriods(trim);
            if (_countPeriods >= 0) {
                try {
                    if (_countPeriods == 0) {
                        return _fromLong(deserializationContext, NumberInput.parseLong(trim));
                    }
                    if (_countPeriods == 1) {
                        return _fromDecimal(deserializationContext, NumberInput.parseBigDecimal(trim));
                    }
                } catch (NumberFormatException e) {
                }
            }
            trim = replaceZeroOffsetAsZIfNecessary(trim);
        }
        if (this._formatter == DateTimeFormatter.ISO_OFFSET_DATE_TIME || this._formatter == DateTimeFormatter.ISO_ZONED_DATE_TIME) {
            trim = addInColonToOffsetIfMissing(trim);
        }
        try {
            temporal = this.parsedToValue.apply(this._formatter.parse(trim));
            if (shouldAdjustToContextTimezone(deserializationContext)) {
                return (T) this.adjust.apply(temporal, getZone(deserializationContext));
            }
        } catch (DateTimeException e2) {
            temporal = (Temporal) _handleDateTimeException(deserializationContext, e2, trim);
        }
        return (T) temporal;
    }

    protected T _fromLong(DeserializationContext context, long timestamp) {
        if (context.isEnabled(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)) {
            return this.fromNanoseconds.apply(new FromDecimalArguments(timestamp, 0, getZone(context)));
        }
        return this.fromMilliseconds.apply(new FromIntegerArguments(timestamp, getZone(context)));
    }

    protected T _fromDecimal(DeserializationContext context, BigDecimal value) {
        FromDecimalArguments args = (FromDecimalArguments) DecimalUtils.extractSecondsAndNanos(value, (s, ns) -> {
            return new FromDecimalArguments(s.longValue(), ns.intValue(), getZone(context));
        });
        return this.fromNanoseconds.apply(args);
    }

    private ZoneId getZone(DeserializationContext context) {
        if (this._valueClass == Instant.class) {
            return null;
        }
        return context.getTimeZone().toZoneId().normalized();
    }

    private String replaceZeroOffsetAsZIfNecessary(String text) {
        if (this.replaceZeroOffsetAsZ) {
            return replaceZeroOffsetAsZ(text);
        }
        return text;
    }

    private static String replaceZeroOffsetAsZ(String text) {
        int plusIndex = text.lastIndexOf(43);
        if (plusIndex < 0) {
            return text;
        }
        int maybeOffsetIndex = plusIndex + 1;
        int remaining = text.length() - maybeOffsetIndex;
        switch (remaining) {
            case 2:
                return text.regionMatches(maybeOffsetIndex, "00", 0, remaining) ? text.substring(0, plusIndex) + 'Z' : text;
            case 3:
            default:
                return text;
            case 4:
                return text.regionMatches(maybeOffsetIndex, "0000", 0, remaining) ? text.substring(0, plusIndex) + 'Z' : text;
            case 5:
                return text.regionMatches(maybeOffsetIndex, "00:00", 0, remaining) ? text.substring(0, plusIndex) + 'Z' : text;
        }
    }

    private String addInColonToOffsetIfMissing(String text) {
        Matcher matcher = ISO8601_COLONLESS_OFFSET_REGEX.matcher(text);
        if (matcher.find()) {
            StringBuilder sb = new StringBuilder(matcher.group(0));
            sb.insert(3, ":");
            return matcher.replaceFirst(sb.toString());
        }
        return text;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/jackson-datatype-jsr310-2.15.3.jar:com/fasterxml/jackson/datatype/jsr310/deser/InstantDeserializer$FromIntegerArguments.class */
    public static class FromIntegerArguments {
        public final long value;
        public final ZoneId zoneId;

        FromIntegerArguments(long value, ZoneId zoneId) {
            this.value = value;
            this.zoneId = zoneId;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/jackson-datatype-jsr310-2.15.3.jar:com/fasterxml/jackson/datatype/jsr310/deser/InstantDeserializer$FromDecimalArguments.class */
    public static class FromDecimalArguments {
        public final long integer;
        public final int fraction;
        public final ZoneId zoneId;

        FromDecimalArguments(long integer, int fraction, ZoneId zoneId) {
            this.integer = integer;
            this.fraction = fraction;
            this.zoneId = zoneId;
        }
    }
}

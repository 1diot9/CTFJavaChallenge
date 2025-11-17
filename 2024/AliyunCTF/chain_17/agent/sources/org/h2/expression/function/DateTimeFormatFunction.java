package org.h2.expression.function;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalQueries;
import java.time.zone.ZoneRules;
import java.util.Locale;
import java.util.Objects;
import org.h2.api.ErrorCode;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.TypedValueExpression;
import org.h2.message.DbException;
import org.h2.util.JSR310Utils;
import org.h2.util.SmallLRUCache;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueTime;
import org.h2.value.ValueTimestampTimeZone;
import org.h2.value.ValueVarchar;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/function/DateTimeFormatFunction.class */
public final class DateTimeFormatFunction extends FunctionN {
    public static final int FORMATDATETIME = 0;
    public static final int PARSEDATETIME = 1;
    private static final String[] NAMES = {"FORMATDATETIME", "PARSEDATETIME"};
    private static final SmallLRUCache<CacheKey, CacheValue> CACHE = SmallLRUCache.newInstance(100);
    private final int function;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/function/DateTimeFormatFunction$CacheKey.class */
    public static final class CacheKey {
        private final String format;
        private final String locale;
        private final String timeZone;

        CacheKey(String str, String str2, String str3) {
            this.format = str;
            this.locale = str2;
            this.timeZone = str3;
        }

        public int hashCode() {
            return (31 * ((31 * ((31 * 1) + this.format.hashCode())) + (this.locale == null ? 0 : this.locale.hashCode()))) + (this.timeZone == null ? 0 : this.timeZone.hashCode());
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof CacheKey)) {
                return false;
            }
            CacheKey cacheKey = (CacheKey) obj;
            return this.format.equals(cacheKey.format) && Objects.equals(this.locale, cacheKey.locale) && Objects.equals(this.timeZone, cacheKey.timeZone);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/function/DateTimeFormatFunction$CacheValue.class */
    public static final class CacheValue {
        final DateTimeFormatter formatter;
        final ZoneId zoneId;

        CacheValue(DateTimeFormatter dateTimeFormatter, ZoneId zoneId) {
            this.formatter = dateTimeFormatter;
            this.zoneId = zoneId;
        }
    }

    public DateTimeFormatFunction(int i) {
        super(new Expression[4]);
        this.function = i;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v16, types: [org.h2.value.Value] */
    @Override // org.h2.expression.function.FunctionN
    public Value getValue(SessionLocal sessionLocal, Value value, Value value2, Value value3) {
        String str;
        String str2;
        ValueTimestampTimeZone parseDateTime;
        String string = value2.getString();
        if (value3 != null) {
            str = value3.getString();
            str2 = this.args.length > 3 ? this.args[3].getValue(sessionLocal).getString() : null;
        } else {
            str = null;
            str2 = null;
        }
        switch (this.function) {
            case 0:
                parseDateTime = ValueVarchar.get(formatDateTime(sessionLocal, value, string, str, str2));
                break;
            case 1:
                parseDateTime = parseDateTime(sessionLocal, value.getString(), string, str, str2);
                break;
            default:
                throw DbException.getInternalError("function=" + this.function);
        }
        return parseDateTime;
    }

    public static String formatDateTime(SessionLocal sessionLocal, Value value, String str, String str2, String str3) {
        ZoneId ofOffset;
        Temporal atZoneSameInstant;
        CacheValue dateFormat = getDateFormat(str, str2, str3);
        ZoneId zoneId = dateFormat.zoneId;
        switch (value.getValueType()) {
            case 17:
            case 20:
                atZoneSameInstant = JSR310Utils.valueToLocalDateTime(value, sessionLocal).atZone(zoneId != null ? zoneId : ZoneId.of(sessionLocal.currentTimeZone().getId()));
                break;
            case 18:
                LocalTime valueToLocalTime = JSR310Utils.valueToLocalTime(value, sessionLocal);
                atZoneSameInstant = zoneId != null ? valueToLocalTime.atOffset(getTimeOffset(zoneId, str3)) : valueToLocalTime;
                break;
            case 19:
                OffsetTime valueToOffsetTime = JSR310Utils.valueToOffsetTime(value, sessionLocal);
                atZoneSameInstant = zoneId != null ? valueToOffsetTime.withOffsetSameInstant(getTimeOffset(zoneId, str3)) : valueToOffsetTime;
                break;
            case 21:
                OffsetDateTime valueToOffsetDateTime = JSR310Utils.valueToOffsetDateTime(value, sessionLocal);
                if (zoneId != null) {
                    ofOffset = zoneId;
                } else {
                    ZoneOffset offset = valueToOffsetDateTime.getOffset();
                    ofOffset = ZoneId.ofOffset(offset.getTotalSeconds() == 0 ? "UTC" : "GMT", offset);
                }
                atZoneSameInstant = valueToOffsetDateTime.atZoneSameInstant(ofOffset);
                break;
            default:
                throw DbException.getInvalidValueException("dateTime", value.getTraceSQL());
        }
        try {
            return dateFormat.formatter.format(atZoneSameInstant);
        } catch (DateTimeException e) {
            throw DbException.getInvalidValueException(e, "format", str);
        }
    }

    private static ZoneOffset getTimeOffset(ZoneId zoneId, String str) {
        if (zoneId instanceof ZoneOffset) {
            return (ZoneOffset) zoneId;
        }
        ZoneRules rules = zoneId.getRules();
        if (!rules.isFixedOffset()) {
            throw DbException.getInvalidValueException("timeZone", str);
        }
        return rules.getOffset(Instant.EPOCH);
    }

    /* JADX WARN: Type inference failed for: r0v46, types: [java.time.ZonedDateTime] */
    public static ValueTimestampTimeZone parseDateTime(SessionLocal sessionLocal, String str, String str2, String str3, String str4) {
        ValueTimestampTimeZone valueTimestampTimeZone;
        ValueTimestampTimeZone valueTimestampTimeZone2;
        ValueTimestampTimeZone valueTimestampTimeZone3;
        CacheValue dateFormat = getDateFormat(str2, str3, str4);
        try {
            TemporalAccessor parse = dateFormat.formatter.parse(str);
            ZoneId zoneId = (ZoneId) parse.query(TemporalQueries.zoneId());
            if (parse.isSupported(ChronoField.OFFSET_SECONDS)) {
                valueTimestampTimeZone2 = JSR310Utils.offsetDateTimeToValue(OffsetDateTime.from(parse));
            } else if (parse.isSupported(ChronoField.INSTANT_SECONDS)) {
                Instant from = Instant.from(parse);
                if (zoneId == null) {
                    zoneId = dateFormat.zoneId;
                }
                if (zoneId != null) {
                    valueTimestampTimeZone2 = JSR310Utils.zonedDateTimeToValue(from.atZone(zoneId));
                } else {
                    valueTimestampTimeZone2 = JSR310Utils.offsetDateTimeToValue(from.atOffset(ZoneOffset.ofTotalSeconds(sessionLocal.currentTimeZone().getTimeZoneOffsetUTC(from.getEpochSecond()))));
                }
            } else {
                LocalDate localDate = (LocalDate) parse.query(TemporalQueries.localDate());
                LocalTime localTime = (LocalTime) parse.query(TemporalQueries.localTime());
                if (zoneId == null) {
                    zoneId = dateFormat.zoneId;
                }
                if (localDate != null) {
                    LocalDateTime of = localTime != null ? LocalDateTime.of(localDate, localTime) : localDate.atStartOfDay();
                    if (zoneId != null) {
                        valueTimestampTimeZone3 = JSR310Utils.zonedDateTimeToValue(of.atZone(zoneId));
                    } else {
                        valueTimestampTimeZone3 = (ValueTimestampTimeZone) JSR310Utils.localDateTimeToValue(of).convertTo(21, sessionLocal);
                    }
                    valueTimestampTimeZone2 = valueTimestampTimeZone3;
                } else {
                    if (zoneId != null) {
                        valueTimestampTimeZone = JSR310Utils.zonedDateTimeToValue(JSR310Utils.valueToInstant(sessionLocal.currentTimestamp(), sessionLocal).atZone(zoneId).with((TemporalAdjuster) localTime));
                    } else {
                        valueTimestampTimeZone = (ValueTimestampTimeZone) ValueTime.fromNanos(localTime.toNanoOfDay()).convertTo(21, sessionLocal);
                    }
                    valueTimestampTimeZone2 = valueTimestampTimeZone;
                }
            }
            return valueTimestampTimeZone2;
        } catch (RuntimeException e) {
            throw DbException.get(ErrorCode.PARSE_ERROR_1, e, str);
        }
    }

    private static CacheValue getDateFormat(String str, String str2, String str3) {
        CacheValue cacheValue;
        ZoneId zoneId;
        Exception exc = null;
        if (str.length() <= 100) {
            try {
                CacheKey cacheKey = new CacheKey(str, str2, str3);
                synchronized (CACHE) {
                    cacheValue = CACHE.get(cacheKey);
                    if (cacheValue == null) {
                        DateTimeFormatter formatter = new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern(str).toFormatter(str2 == null ? Locale.getDefault(Locale.Category.FORMAT) : new Locale(str2));
                        if (str3 != null) {
                            zoneId = getZoneId(str3);
                            formatter = formatter.withZone(zoneId);
                        } else {
                            zoneId = null;
                        }
                        cacheValue = new CacheValue(formatter, zoneId);
                        CACHE.put(cacheKey, cacheValue);
                    }
                }
                return cacheValue;
            } catch (Exception e) {
                exc = e;
            }
        }
        throw DbException.get(ErrorCode.PARSE_ERROR_1, exc, str + '/' + str2);
    }

    private static ZoneId getZoneId(String str) {
        try {
            return ZoneId.of(str, ZoneId.SHORT_IDS);
        } catch (RuntimeException e) {
            throw DbException.getInvalidValueException("TIME ZONE", str);
        }
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        boolean optimizeArguments = optimizeArguments(sessionLocal, true);
        switch (this.function) {
            case 0:
                this.type = TypeInfo.TYPE_VARCHAR;
                break;
            case 1:
                this.type = TypeInfo.TYPE_TIMESTAMP_TZ;
                break;
            default:
                throw DbException.getInternalError("function=" + this.function);
        }
        if (optimizeArguments) {
            return TypedValueExpression.getTypedIfNull(getValue(sessionLocal), this.type);
        }
        return this;
    }

    @Override // org.h2.expression.function.NamedExpression
    public String getName() {
        return NAMES[this.function];
    }
}

package cn.hutool.core.convert.impl;

import cn.hutool.core.convert.AbstractConverter;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.convert.ConvertException;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.format.GlobalCustomFormat;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.Era;
import java.time.chrono.IsoEra;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/convert/impl/TemporalAccessorConverter.class */
public class TemporalAccessorConverter extends AbstractConverter<TemporalAccessor> {
    private static final long serialVersionUID = 1;
    private final Class<?> targetType;
    private String format;

    public TemporalAccessorConverter(Class<?> targetType) {
        this(targetType, null);
    }

    public TemporalAccessorConverter(Class<?> targetType, String format) {
        this.targetType = targetType;
        this.format = format;
    }

    public String getFormat() {
        return this.format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    @Override // cn.hutool.core.convert.AbstractConverter
    public Class<TemporalAccessor> getTargetType() {
        return this.targetType;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.core.convert.AbstractConverter
    public TemporalAccessor convertInternal(Object value) {
        if (value instanceof Number) {
            return parseFromLong(Long.valueOf(((Number) value).longValue()));
        }
        if (value instanceof TemporalAccessor) {
            return parseFromTemporalAccessor((TemporalAccessor) value);
        }
        if (value instanceof Date) {
            DateTime dateTime = DateUtil.date((Date) value);
            return parseFromInstant(dateTime.toInstant(), dateTime.getZoneId());
        }
        if (value instanceof Calendar) {
            Calendar calendar = (Calendar) value;
            return parseFromInstant(calendar.toInstant(), calendar.getTimeZone().toZoneId());
        }
        if (value instanceof Map) {
            Map<?, ?> map = (Map) value;
            if (LocalDate.class.equals(this.targetType)) {
                return LocalDate.of(Convert.toInt(map.get("year")).intValue(), Convert.toInt(map.get("month")).intValue(), Convert.toInt(map.get("day")).intValue());
            }
            if (LocalDateTime.class.equals(this.targetType)) {
                return LocalDateTime.of(Convert.toInt(map.get("year")).intValue(), Convert.toInt(map.get("month")).intValue(), Convert.toInt(map.get("day")).intValue(), Convert.toInt(map.get("hour")).intValue(), Convert.toInt(map.get("minute")).intValue(), Convert.toInt(map.get("second")).intValue(), Convert.toInt(map.get("second")).intValue());
            }
            if (LocalTime.class.equals(this.targetType)) {
                return LocalTime.of(Convert.toInt(map.get("hour")).intValue(), Convert.toInt(map.get("minute")).intValue(), Convert.toInt(map.get("second")).intValue(), Convert.toInt(map.get("nano")).intValue());
            }
            throw new ConvertException("Unsupported type: [{}] from map: [{}]", this.targetType, map);
        }
        return parseFromCharSequence(convertToStr(value));
    }

    private TemporalAccessor parseFromCharSequence(CharSequence value) {
        Instant instant;
        ZoneId zoneId;
        if (StrUtil.isBlank(value)) {
            return null;
        }
        if (DayOfWeek.class.equals(this.targetType)) {
            return DayOfWeek.valueOf(StrUtil.toString(value));
        }
        if (Month.class.equals(this.targetType)) {
            return Month.valueOf(StrUtil.toString(value));
        }
        if (Era.class.equals(this.targetType)) {
            return IsoEra.valueOf(StrUtil.toString(value));
        }
        if (MonthDay.class.equals(this.targetType)) {
            return MonthDay.parse(value);
        }
        if (null != this.format) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(this.format);
            instant = (Instant) formatter.parse(value, Instant::from);
            zoneId = formatter.getZone();
        } else {
            DateTime dateTime = DateUtil.parse(value);
            instant = ((DateTime) Objects.requireNonNull(dateTime)).toInstant();
            zoneId = dateTime.getZoneId();
        }
        return parseFromInstant(instant, zoneId);
    }

    private TemporalAccessor parseFromLong(Long time) {
        Instant instant;
        if (DayOfWeek.class.equals(this.targetType)) {
            return DayOfWeek.of(Math.toIntExact(time.longValue()));
        }
        if (Month.class.equals(this.targetType)) {
            return Month.of(Math.toIntExact(time.longValue()));
        }
        if (Era.class.equals(this.targetType)) {
            return IsoEra.of(Math.toIntExact(time.longValue()));
        }
        if (GlobalCustomFormat.FORMAT_SECONDS.equals(this.format)) {
            instant = Instant.ofEpochSecond(time.longValue());
        } else {
            instant = Instant.ofEpochMilli(time.longValue());
        }
        return parseFromInstant(instant, null);
    }

    private TemporalAccessor parseFromTemporalAccessor(TemporalAccessor temporalAccessor) {
        if (DayOfWeek.class.equals(this.targetType)) {
            return DayOfWeek.from(temporalAccessor);
        }
        if (Month.class.equals(this.targetType)) {
            return Month.from(temporalAccessor);
        }
        if (MonthDay.class.equals(this.targetType)) {
            return MonthDay.from(temporalAccessor);
        }
        TemporalAccessor result = null;
        if (temporalAccessor instanceof LocalDateTime) {
            result = parseFromLocalDateTime((LocalDateTime) temporalAccessor);
        } else if (temporalAccessor instanceof ZonedDateTime) {
            result = parseFromZonedDateTime((ZonedDateTime) temporalAccessor);
        }
        if (null == result) {
            result = parseFromInstant(DateUtil.toInstant(temporalAccessor), null);
        }
        return result;
    }

    /* JADX WARN: Type inference failed for: r0v14, types: [java.time.ZonedDateTime] */
    /* JADX WARN: Type inference failed for: r0v18, types: [java.time.ZonedDateTime] */
    private TemporalAccessor parseFromLocalDateTime(LocalDateTime localDateTime) {
        if (Instant.class.equals(this.targetType)) {
            return DateUtil.toInstant(localDateTime);
        }
        if (LocalDate.class.equals(this.targetType)) {
            return localDateTime.toLocalDate();
        }
        if (LocalTime.class.equals(this.targetType)) {
            return localDateTime.toLocalTime();
        }
        if (ZonedDateTime.class.equals(this.targetType)) {
            return localDateTime.atZone(ZoneId.systemDefault());
        }
        if (OffsetDateTime.class.equals(this.targetType)) {
            return localDateTime.atZone(ZoneId.systemDefault()).toOffsetDateTime();
        }
        if (OffsetTime.class.equals(this.targetType)) {
            return localDateTime.atZone(ZoneId.systemDefault()).toOffsetDateTime().toOffsetTime();
        }
        return null;
    }

    private TemporalAccessor parseFromZonedDateTime(ZonedDateTime zonedDateTime) {
        if (Instant.class.equals(this.targetType)) {
            return DateUtil.toInstant(zonedDateTime);
        }
        if (LocalDateTime.class.equals(this.targetType)) {
            return zonedDateTime.toLocalDateTime();
        }
        if (LocalDate.class.equals(this.targetType)) {
            return zonedDateTime.toLocalDate();
        }
        if (LocalTime.class.equals(this.targetType)) {
            return zonedDateTime.toLocalTime();
        }
        if (OffsetDateTime.class.equals(this.targetType)) {
            return zonedDateTime.toOffsetDateTime();
        }
        if (OffsetTime.class.equals(this.targetType)) {
            return zonedDateTime.toOffsetDateTime().toOffsetTime();
        }
        return null;
    }

    private TemporalAccessor parseFromInstant(Instant instant, ZoneId zoneId) {
        if (Instant.class.equals(this.targetType)) {
            return instant;
        }
        ZoneId zoneId2 = (ZoneId) ObjectUtil.defaultIfNull(zoneId, (Supplier<? extends ZoneId>) ZoneId::systemDefault);
        TemporalAccessor result = null;
        if (LocalDateTime.class.equals(this.targetType)) {
            result = LocalDateTime.ofInstant(instant, zoneId2);
        } else if (LocalDate.class.equals(this.targetType)) {
            result = instant.atZone(zoneId2).toLocalDate();
        } else if (LocalTime.class.equals(this.targetType)) {
            result = instant.atZone(zoneId2).toLocalTime();
        } else if (ZonedDateTime.class.equals(this.targetType)) {
            result = instant.atZone(zoneId2);
        } else if (OffsetDateTime.class.equals(this.targetType)) {
            result = OffsetDateTime.ofInstant(instant, zoneId2);
        } else if (OffsetTime.class.equals(this.targetType)) {
            result = OffsetTime.ofInstant(instant, zoneId2);
        }
        return result;
    }
}

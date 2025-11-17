package ch.qos.logback.core.rolling.helper;

import ch.qos.logback.core.pattern.DynamicConverter;
import ch.qos.logback.core.util.CachingDateFormatter;
import ch.qos.logback.core.util.DatePatternToRegexUtil;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/rolling/helper/DateTokenConverter.class */
public class DateTokenConverter<E> extends DynamicConverter<E> implements MonoTypedConverter {
    public static final String CONVERTER_KEY = "d";
    public static final String AUXILIARY_TOKEN = "AUX";
    public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
    private String datePattern;
    private ZoneId zoneId;
    private CachingDateFormatter cdf;
    private boolean primary = true;

    @Override // ch.qos.logback.core.pattern.DynamicConverter, ch.qos.logback.core.spi.LifeCycle
    public void start() {
        this.datePattern = getFirstOption();
        if (this.datePattern == null) {
            this.datePattern = "yyyy-MM-dd";
        }
        List<String> optionList = getOptionList();
        if (optionList != null) {
            for (int optionIndex = 1; optionIndex < optionList.size(); optionIndex++) {
                String option = optionList.get(optionIndex);
                if (AUXILIARY_TOKEN.equalsIgnoreCase(option)) {
                    this.primary = false;
                } else {
                    this.zoneId = ZoneId.of(option);
                }
            }
        }
        this.cdf = new CachingDateFormatter(this.datePattern, this.zoneId);
    }

    public String convert(Date date) {
        return this.cdf.format(date.getTime());
    }

    public String convert(Instant instant) {
        return this.cdf.format(instant.toEpochMilli());
    }

    @Override // ch.qos.logback.core.pattern.Converter
    public String convert(Object o) {
        if (o == null) {
            throw new IllegalArgumentException("Null argument forbidden");
        }
        if (o instanceof Date) {
            return convert((Date) o);
        }
        if (o instanceof Instant) {
            return convert((Instant) o);
        }
        throw new IllegalArgumentException("Cannot convert " + String.valueOf(o) + " of type" + o.getClass().getName());
    }

    public String getDatePattern() {
        return this.datePattern;
    }

    public ZoneId getZoneId() {
        return this.zoneId;
    }

    @Override // ch.qos.logback.core.rolling.helper.MonoTypedConverter
    public boolean isApplicable(Object o) {
        if ((o instanceof Date) || (o instanceof Instant)) {
            return true;
        }
        return false;
    }

    public String toRegex() {
        DatePatternToRegexUtil datePatternToRegexUtil = new DatePatternToRegexUtil(this.datePattern);
        return datePatternToRegexUtil.toRegex();
    }

    public boolean isPrimary() {
        return this.primary;
    }
}

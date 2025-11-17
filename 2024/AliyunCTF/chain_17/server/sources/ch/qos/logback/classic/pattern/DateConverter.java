package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.util.CachingDateFormatter;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/pattern/DateConverter.class */
public class DateConverter extends ClassicConverter {
    long lastTimestamp = -1;
    String timestampStrCache = null;
    CachingDateFormatter cachingDateFormatter = null;

    @Override // ch.qos.logback.core.pattern.DynamicConverter, ch.qos.logback.core.spi.LifeCycle
    public void start() {
        String datePattern = getFirstOption();
        if (datePattern == null) {
            datePattern = CoreConstants.ISO8601_PATTERN;
        }
        if (datePattern.equals(CoreConstants.ISO8601_STR)) {
            datePattern = CoreConstants.ISO8601_PATTERN;
        }
        List<String> optionList = getOptionList();
        ZoneId zoneId = null;
        if (optionList != null && optionList.size() > 1) {
            String zoneIdString = optionList.get(1);
            zoneId = ZoneId.of(zoneIdString);
            addInfo("Setting zoneId to \"" + String.valueOf(zoneId) + "\"");
        }
        Locale locale = null;
        if (optionList != null && optionList.size() > 2) {
            String localeIdStr = optionList.get(2);
            locale = Locale.forLanguageTag(localeIdStr);
            addInfo("Setting locale to \"" + String.valueOf(locale) + "\"");
        }
        try {
            this.cachingDateFormatter = new CachingDateFormatter(datePattern, zoneId, locale);
        } catch (IllegalArgumentException e) {
            addWarn("Could not instantiate SimpleDateFormat with pattern " + datePattern, e);
            this.cachingDateFormatter = new CachingDateFormatter(CoreConstants.ISO8601_PATTERN, zoneId);
        }
        super.start();
    }

    @Override // ch.qos.logback.core.pattern.Converter
    public String convert(ILoggingEvent le) {
        long timestamp = le.getTimeStamp();
        return this.cachingDateFormatter.format(timestamp);
    }
}

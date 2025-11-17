package cn.hutool.core.date.format;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Tuple;
import cn.hutool.core.map.SafeConcurrentHashMap;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentMap;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/format/FormatCache.class */
public abstract class FormatCache<F extends Format> {
    static final int NONE = -1;
    private final ConcurrentMap<Tuple, F> cInstanceCache = new SafeConcurrentHashMap(7);
    private static final ConcurrentMap<Tuple, String> C_DATE_TIME_INSTANCE_CACHE = new SafeConcurrentHashMap(7);

    protected abstract F createInstance(String str, TimeZone timeZone, Locale locale);

    public F getInstance() {
        return getDateTimeInstance(3, 3, null, null);
    }

    public F getInstance(String pattern, TimeZone timeZone, Locale locale) {
        Assert.notBlank(pattern, "pattern must not be blank", new Object[0]);
        if (timeZone == null) {
            timeZone = TimeZone.getDefault();
        }
        if (locale == null) {
            locale = Locale.getDefault();
        }
        Tuple key = new Tuple(pattern, timeZone, locale);
        F format = this.cInstanceCache.get(key);
        if (format == null) {
            format = createInstance(pattern, timeZone, locale);
            F previousValue = this.cInstanceCache.putIfAbsent(key, format);
            if (previousValue != null) {
                format = previousValue;
            }
        }
        return format;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public F getDateTimeInstance(Integer dateStyle, Integer timeStyle, TimeZone timeZone, Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        String pattern = getPatternForStyle(dateStyle, timeStyle, locale);
        return getInstance(pattern, timeZone, locale);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public F getDateInstance(int dateStyle, TimeZone timeZone, Locale locale) {
        return getDateTimeInstance(Integer.valueOf(dateStyle), null, timeZone, locale);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public F getTimeInstance(int timeStyle, TimeZone timeZone, Locale locale) {
        return getDateTimeInstance(null, Integer.valueOf(timeStyle), timeZone, locale);
    }

    static String getPatternForStyle(Integer dateStyle, Integer timeStyle, Locale locale) {
        DateFormat formatter;
        Tuple key = new Tuple(dateStyle, timeStyle, locale);
        String pattern = C_DATE_TIME_INSTANCE_CACHE.get(key);
        if (pattern == null) {
            try {
                if (dateStyle == null) {
                    formatter = DateFormat.getTimeInstance(timeStyle.intValue(), locale);
                } else if (timeStyle == null) {
                    formatter = DateFormat.getDateInstance(dateStyle.intValue(), locale);
                } else {
                    formatter = DateFormat.getDateTimeInstance(dateStyle.intValue(), timeStyle.intValue(), locale);
                }
                pattern = ((SimpleDateFormat) formatter).toPattern();
                String previous = C_DATE_TIME_INSTANCE_CACHE.putIfAbsent(key, pattern);
                if (previous != null) {
                    pattern = previous;
                }
            } catch (ClassCastException e) {
                throw new IllegalArgumentException("No date time pattern for locale: " + locale);
            }
        }
        return pattern;
    }
}

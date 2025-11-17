package org.apache.tomcat.util.http;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/FastHttpDateFormat.class */
public final class FastHttpDateFormat {
    private static final int CACHE_SIZE = Integer.getInteger("org.apache.tomcat.util.http.FastHttpDateFormat.CACHE_SIZE", 1000).intValue();
    private static final String DATE_RFC5322 = "EEE, dd MMM yyyy HH:mm:ss z";
    private static final String DATE_OBSOLETE_RFC850 = "EEEEEE, dd-MMM-yy HH:mm:ss zzz";
    private static final String DATE_OBSOLETE_ASCTIME = "EEE MMMM d HH:mm:ss yyyy";
    private static final ConcurrentDateFormat FORMAT_RFC5322;
    private static final ConcurrentDateFormat FORMAT_OBSOLETE_RFC850;
    private static final ConcurrentDateFormat FORMAT_OBSOLETE_ASCTIME;
    private static final ConcurrentDateFormat[] httpParseFormats;
    private static volatile long currentDateGenerated;
    private static String currentDate;
    private static final Map<Long, String> formatCache;
    private static final Map<String, Long> parseCache;

    static {
        TimeZone tz = TimeZone.getTimeZone("GMT");
        FORMAT_RFC5322 = new ConcurrentDateFormat(DATE_RFC5322, Locale.US, tz);
        FORMAT_OBSOLETE_RFC850 = new ConcurrentDateFormat(DATE_OBSOLETE_RFC850, Locale.US, tz);
        FORMAT_OBSOLETE_ASCTIME = new ConcurrentDateFormat(DATE_OBSOLETE_ASCTIME, Locale.US, tz);
        httpParseFormats = new ConcurrentDateFormat[]{FORMAT_RFC5322, FORMAT_OBSOLETE_RFC850, FORMAT_OBSOLETE_ASCTIME};
        currentDateGenerated = 0L;
        currentDate = null;
        formatCache = new ConcurrentHashMap(CACHE_SIZE);
        parseCache = new ConcurrentHashMap(CACHE_SIZE);
    }

    public static String getCurrentDate() {
        long now = System.currentTimeMillis();
        if (Math.abs(now - currentDateGenerated) > 1000) {
            currentDate = FORMAT_RFC5322.format(new Date(now));
            currentDateGenerated = now;
        }
        return currentDate;
    }

    public static String formatDate(long value) {
        Long longValue = Long.valueOf(value);
        String cachedDate = formatCache.get(longValue);
        if (cachedDate != null) {
            return cachedDate;
        }
        String newDate = FORMAT_RFC5322.format(new Date(value));
        updateFormatCache(longValue, newDate);
        return newDate;
    }

    public static long parseDate(String value) {
        Long cachedDate = parseCache.get(value);
        if (cachedDate != null) {
            return cachedDate.longValue();
        }
        long date = -1;
        for (int i = 0; date == -1 && i < httpParseFormats.length; i++) {
            try {
                date = httpParseFormats[i].parse(value).getTime();
                updateParseCache(value, Long.valueOf(date));
            } catch (ParseException e) {
            }
        }
        return date;
    }

    private static void updateFormatCache(Long key, String value) {
        if (value == null) {
            return;
        }
        if (formatCache.size() > CACHE_SIZE) {
            formatCache.clear();
        }
        formatCache.put(key, value);
    }

    private static void updateParseCache(String key, Long value) {
        if (value == null) {
            return;
        }
        if (parseCache.size() > CACHE_SIZE) {
            parseCache.clear();
        }
        parseCache.put(key, value);
    }
}

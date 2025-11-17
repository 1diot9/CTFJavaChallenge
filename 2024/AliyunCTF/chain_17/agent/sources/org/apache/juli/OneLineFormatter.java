package org.apache.juli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Formatter;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/juli/OneLineFormatter.class */
public class OneLineFormatter extends Formatter {
    private static final String UNKNOWN_THREAD_NAME = "Unknown thread with ID ";
    private static final int THREAD_NAME_CACHE_SIZE = 10000;
    private static final String DEFAULT_TIME_FORMAT = "dd-MMM-yyyy HH:mm:ss.SSS";
    private static final int globalCacheSize = 30;
    private static final int localCacheSize = 5;
    private ThreadLocal<DateFormatCache> localDateCache;
    private volatile MillisHandling millisHandling = MillisHandling.APPEND;
    private static final Object threadMxBeanLock = new Object();
    private static volatile ThreadMXBean threadMxBean = null;
    private static final ThreadLocal<ThreadNameCache> threadNameCache = ThreadLocal.withInitial(() -> {
        return new ThreadNameCache(10000);
    });

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/juli/OneLineFormatter$MillisHandling.class */
    public enum MillisHandling {
        NONE,
        APPEND,
        REPLACE_S,
        REPLACE_SS,
        REPLACE_SSS
    }

    public OneLineFormatter() {
        String timeFormat = LogManager.getLogManager().getProperty(OneLineFormatter.class.getName() + ".timeFormat");
        setTimeFormat(timeFormat == null ? DEFAULT_TIME_FORMAT : timeFormat);
    }

    public void setTimeFormat(String timeFormat) {
        String cachedTimeFormat;
        if (timeFormat.endsWith(".SSS")) {
            cachedTimeFormat = timeFormat.substring(0, timeFormat.length() - 4);
            this.millisHandling = MillisHandling.APPEND;
        } else if (timeFormat.contains("SSS")) {
            this.millisHandling = MillisHandling.REPLACE_SSS;
            cachedTimeFormat = timeFormat;
        } else if (timeFormat.contains("SS")) {
            this.millisHandling = MillisHandling.REPLACE_SS;
            cachedTimeFormat = timeFormat;
        } else if (timeFormat.contains("S")) {
            this.millisHandling = MillisHandling.REPLACE_S;
            cachedTimeFormat = timeFormat;
        } else {
            this.millisHandling = MillisHandling.NONE;
            cachedTimeFormat = timeFormat;
        }
        DateFormatCache globalDateCache = new DateFormatCache(30, cachedTimeFormat, null);
        String str = cachedTimeFormat;
        this.localDateCache = ThreadLocal.withInitial(() -> {
            return new DateFormatCache(5, str, globalDateCache);
        });
    }

    public String getTimeFormat() {
        return this.localDateCache.get().getTimeFormat();
    }

    @Override // java.util.logging.Formatter
    public String format(LogRecord record) {
        StringBuilder sb = new StringBuilder();
        addTimestamp(sb, record.getMillis());
        sb.append(' ');
        sb.append(record.getLevel().getLocalizedName());
        sb.append(' ');
        sb.append('[');
        String threadName = Thread.currentThread().getName();
        if (threadName != null && threadName.startsWith("AsyncFileHandlerWriter-")) {
            sb.append(getThreadName(record.getThreadID()));
        } else {
            sb.append(threadName);
        }
        sb.append(']');
        sb.append(' ');
        sb.append(record.getSourceClassName());
        sb.append('.');
        sb.append(record.getSourceMethodName());
        sb.append(' ');
        sb.append(formatMessage(record));
        sb.append(System.lineSeparator());
        if (record.getThrown() != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new IndentingPrintWriter(sw);
            record.getThrown().printStackTrace(pw);
            pw.close();
            sb.append(sw.getBuffer());
        }
        return sb.toString();
    }

    protected void addTimestamp(StringBuilder buf, long timestamp) {
        String cachedTimeStamp = this.localDateCache.get().getFormat(timestamp);
        if (this.millisHandling == MillisHandling.NONE) {
            buf.append(cachedTimeStamp);
            return;
        }
        if (this.millisHandling == MillisHandling.APPEND) {
            buf.append(cachedTimeStamp);
            long frac = timestamp % 1000;
            buf.append('.');
            if (frac < 100) {
                if (frac < 10) {
                    buf.append('0');
                    buf.append('0');
                } else {
                    buf.append('0');
                }
            }
            buf.append(frac);
            return;
        }
        long frac2 = timestamp % 1000;
        int insertStart = cachedTimeStamp.indexOf(35);
        buf.append(cachedTimeStamp.subSequence(0, insertStart));
        if (frac2 < 100 && this.millisHandling == MillisHandling.REPLACE_SSS) {
            buf.append('0');
            if (frac2 < 10) {
                buf.append('0');
            }
        } else if (frac2 < 10 && this.millisHandling == MillisHandling.REPLACE_SS) {
            buf.append('0');
        }
        buf.append(frac2);
        if (this.millisHandling == MillisHandling.REPLACE_SSS) {
            buf.append(cachedTimeStamp.substring(insertStart + 3));
        } else if (this.millisHandling == MillisHandling.REPLACE_SS) {
            buf.append(cachedTimeStamp.substring(insertStart + 2));
        } else {
            buf.append(cachedTimeStamp.substring(insertStart + 1));
        }
    }

    private static String getThreadName(int logRecordThreadId) {
        String result;
        Map<Integer, String> cache = threadNameCache.get();
        String result2 = cache.get(Integer.valueOf(logRecordThreadId));
        if (result2 != null) {
            return result2;
        }
        if (logRecordThreadId > 1073741823) {
            result = "Unknown thread with ID " + logRecordThreadId;
        } else {
            if (threadMxBean == null) {
                synchronized (threadMxBeanLock) {
                    if (threadMxBean == null) {
                        threadMxBean = ManagementFactory.getThreadMXBean();
                    }
                }
            }
            ThreadInfo threadInfo = threadMxBean.getThreadInfo(logRecordThreadId);
            if (threadInfo == null) {
                return Long.toString(logRecordThreadId);
            }
            result = threadInfo.getThreadName();
        }
        cache.put(Integer.valueOf(logRecordThreadId), result);
        return result;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/juli/OneLineFormatter$ThreadNameCache.class */
    public static class ThreadNameCache extends LinkedHashMap<Integer, String> {
        private static final long serialVersionUID = 1;
        private final int cacheSize;

        ThreadNameCache(int cacheSize) {
            super(cacheSize, 0.75f, true);
            this.cacheSize = cacheSize;
        }

        @Override // java.util.LinkedHashMap
        protected boolean removeEldestEntry(Map.Entry<Integer, String> eldest) {
            return size() > this.cacheSize;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/juli/OneLineFormatter$IndentingPrintWriter.class */
    private static class IndentingPrintWriter extends PrintWriter {
        IndentingPrintWriter(Writer out) {
            super(out);
        }

        @Override // java.io.PrintWriter
        public void println(Object x) {
            super.print('\t');
            super.println(x);
        }
    }
}

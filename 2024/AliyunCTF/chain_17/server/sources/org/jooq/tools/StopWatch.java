package org.jooq.tools;

import org.springframework.beans.propertyeditors.CustomBooleanEditor;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/tools/StopWatch.class */
public final class StopWatch {
    private static final JooqLogger log = JooqLogger.getLogger((Class<?>) StopWatch.class);
    private long start = System.nanoTime();
    private long split = this.start;

    public void splitTrace(String message) {
        if (log.isTraceEnabled()) {
            log.trace(message, splitMessage(0L));
        }
    }

    public void splitTrace(String message, long thresholdNano) {
        String splitMessage;
        if (log.isTraceEnabled() && (splitMessage = splitMessage(thresholdNano)) != null) {
            log.trace(message, splitMessage);
        }
    }

    public void splitDebug(String message) {
        if (log.isDebugEnabled()) {
            log.debug(message, splitMessage(0L));
        }
    }

    public void splitDebug(String message, long thresholdNano) {
        String splitMessage;
        if (log.isDebugEnabled() && (splitMessage = splitMessage(thresholdNano)) != null) {
            log.debug(message, splitMessage);
        }
    }

    public void splitInfo(String message) {
        if (log.isInfoEnabled()) {
            log.info(message, splitMessage(0L));
        }
    }

    public void splitInfo(String message, long thresholdNano) {
        String splitMessage;
        if (log.isInfoEnabled() && (splitMessage = splitMessage(thresholdNano)) != null) {
            log.info(message, splitMessage);
        }
    }

    public void splitWarn(String message) {
        log.warn(message, splitMessage(0L));
    }

    public void splitWarn(String message, long thresholdNano) {
        String splitMessage = splitMessage(thresholdNano);
        if (splitMessage != null) {
            log.warn(message, splitMessage);
        }
    }

    public long split() {
        return System.nanoTime() - this.start;
    }

    private String splitMessage(long thresholdNano) {
        long temp = this.split;
        this.split = System.nanoTime();
        long inc = this.split - temp;
        if (thresholdNano > 0 && inc < thresholdNano) {
            return null;
        }
        if (temp == this.start) {
            return "Total: " + format(this.split - this.start);
        }
        return "Total: " + format(this.split - this.start) + ", +" + format(inc);
    }

    public static String format(long nanoTime) {
        if (nanoTime > 60000000000L) {
            return formatHours(nanoTime / 1000000000);
        }
        if (nanoTime > 1000000000) {
            return ((nanoTime / 1000000) / 1000.0d) + "s";
        }
        return ((nanoTime / 1000) / 1000.0d) + "ms";
    }

    public static String formatHours(long seconds) {
        long s = seconds % 60;
        long m = (seconds / 60) % 60;
        long h = seconds / 3600;
        StringBuilder sb = new StringBuilder();
        if (h != 0) {
            if (h < 10) {
                sb.append(CustomBooleanEditor.VALUE_0);
                sb.append(h);
                sb.append(":");
            } else {
                sb.append(h);
                sb.append(":");
            }
        }
        if (m < 10) {
            sb.append(CustomBooleanEditor.VALUE_0);
            sb.append(m);
            sb.append(":");
        } else {
            sb.append(m);
            sb.append(":");
        }
        if (s < 10) {
            sb.append(CustomBooleanEditor.VALUE_0);
            sb.append(s);
        } else {
            sb.append(s);
        }
        return sb.toString();
    }
}

package ch.qos.logback.core.util;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/util/InvocationGate.class */
public interface InvocationGate {
    public static final long TIME_UNAVAILABLE = -1;

    boolean isTooSoon(long j);
}

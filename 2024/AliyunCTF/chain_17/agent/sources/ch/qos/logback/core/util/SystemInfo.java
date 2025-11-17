package ch.qos.logback.core.util;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/util/SystemInfo.class */
public class SystemInfo {
    public static String getJavaVendor() {
        return OptionHelper.getSystemProperty("java.vendor", null);
    }
}

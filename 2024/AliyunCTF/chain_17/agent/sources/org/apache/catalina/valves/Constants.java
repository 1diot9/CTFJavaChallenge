package org.apache.catalina.valves;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/Constants.class */
public final class Constants {
    public static final String Package = "org.apache.catalina.valves";

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/Constants$AccessLog.class */
    public static final class AccessLog {
        public static final String COMMON_ALIAS = "common";
        public static final String COMMON_PATTERN = "%h %l %u %t \"%r\" %s %b";
        public static final String COMBINED_ALIAS = "combined";
        public static final String COMBINED_PATTERN = "%h %l %u %t \"%r\" %s %b \"%{Referer}i\" \"%{User-Agent}i\"";
    }
}

package org.apache.coyote;

import org.apache.tomcat.util.res.StringManager;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/ContinueResponseTiming.class */
public enum ContinueResponseTiming {
    IMMEDIATELY("immediately"),
    ON_REQUEST_BODY_READ("onRead"),
    ALWAYS("always");

    private static final StringManager sm = StringManager.getManager((Class<?>) ContinueResponseTiming.class);
    private final String configValue;

    public static ContinueResponseTiming fromString(String value) {
        if (IMMEDIATELY.toString().equalsIgnoreCase(value)) {
            return IMMEDIATELY;
        }
        if (ON_REQUEST_BODY_READ.toString().equalsIgnoreCase(value)) {
            return ON_REQUEST_BODY_READ;
        }
        throw new IllegalArgumentException(sm.getString("continueResponseTiming.invalid", value));
    }

    ContinueResponseTiming(String configValue) {
        this.configValue = configValue;
    }

    @Override // java.lang.Enum
    public String toString() {
        return this.configValue;
    }
}

package org.jooq.conf;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "FetchTriggerValuesAfterReturning")
@XmlEnum
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/conf/FetchTriggerValuesAfterReturning.class */
public enum FetchTriggerValuesAfterReturning {
    NEVER,
    WHEN_NEEDED,
    ALWAYS;

    public String value() {
        return name();
    }

    public static FetchTriggerValuesAfterReturning fromValue(String v) {
        return valueOf(v);
    }
}

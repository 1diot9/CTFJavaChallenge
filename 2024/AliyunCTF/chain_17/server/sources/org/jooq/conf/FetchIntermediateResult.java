package org.jooq.conf;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "FetchIntermediateResult")
@XmlEnum
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/conf/FetchIntermediateResult.class */
public enum FetchIntermediateResult {
    ALWAYS,
    WHEN_EXECUTE_LISTENERS_PRESENT,
    WHEN_RESULT_REQUESTED;

    public String value() {
        return name();
    }

    public static FetchIntermediateResult fromValue(String v) {
        return valueOf(v);
    }
}

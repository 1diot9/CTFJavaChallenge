package org.jooq.conf;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "UpdateUnchangedRecords")
@XmlEnum
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/conf/UpdateUnchangedRecords.class */
public enum UpdateUnchangedRecords {
    NEVER,
    SET_PRIMARY_KEY_TO_ITSELF,
    SET_NON_PRIMARY_KEY_TO_THEMSELVES,
    SET_NON_PRIMARY_KEY_TO_RECORD_VALUES;

    public String value() {
        return name();
    }

    public static UpdateUnchangedRecords fromValue(String v) {
        return valueOf(v);
    }
}

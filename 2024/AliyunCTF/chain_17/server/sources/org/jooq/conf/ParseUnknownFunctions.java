package org.jooq.conf;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "ParseUnknownFunctions")
@XmlEnum
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/conf/ParseUnknownFunctions.class */
public enum ParseUnknownFunctions {
    FAIL,
    IGNORE;

    public String value() {
        return name();
    }

    public static ParseUnknownFunctions fromValue(String v) {
        return valueOf(v);
    }
}

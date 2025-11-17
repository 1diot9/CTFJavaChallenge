package org.jooq.conf;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "ParseUnsupportedSyntax")
@XmlEnum
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/conf/ParseUnsupportedSyntax.class */
public enum ParseUnsupportedSyntax {
    FAIL,
    IGNORE;

    public String value() {
        return name();
    }

    public static ParseUnsupportedSyntax fromValue(String v) {
        return valueOf(v);
    }
}

package org.jooq.conf;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "InterpreterNameLookupCaseSensitivity")
@XmlEnum
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/conf/InterpreterNameLookupCaseSensitivity.class */
public enum InterpreterNameLookupCaseSensitivity {
    DEFAULT,
    ALWAYS,
    WHEN_QUOTED,
    NEVER;

    public String value() {
        return name();
    }

    public static InterpreterNameLookupCaseSensitivity fromValue(String v) {
        return valueOf(v);
    }
}

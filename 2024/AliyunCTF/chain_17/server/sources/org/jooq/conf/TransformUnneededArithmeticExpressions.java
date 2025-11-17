package org.jooq.conf;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "TransformUnneededArithmeticExpressions")
@XmlEnum
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/conf/TransformUnneededArithmeticExpressions.class */
public enum TransformUnneededArithmeticExpressions {
    NEVER,
    INTERNAL,
    ALWAYS;

    public String value() {
        return name();
    }

    public static TransformUnneededArithmeticExpressions fromValue(String v) {
        return valueOf(v);
    }
}

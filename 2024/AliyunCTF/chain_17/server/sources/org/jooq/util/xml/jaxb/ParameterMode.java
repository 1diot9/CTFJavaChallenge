package org.jooq.util.xml.jaxb;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "ParameterMode")
@XmlEnum
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/util/xml/jaxb/ParameterMode.class */
public enum ParameterMode {
    IN,
    INOUT,
    OUT;

    public String value() {
        return name();
    }

    public static ParameterMode fromValue(String v) {
        return valueOf(v);
    }
}

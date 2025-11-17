package org.jooq.conf;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "QueryPoolable")
@XmlEnum
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/conf/QueryPoolable.class */
public enum QueryPoolable {
    TRUE,
    FALSE,
    DEFAULT;

    public String value() {
        return name();
    }

    public static QueryPoolable fromValue(String v) {
        return valueOf(v);
    }
}

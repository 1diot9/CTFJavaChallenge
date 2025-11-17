package org.jooq;

import java.io.Serializable;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/QueryPart.class */
public interface QueryPart extends Serializable {
    String toString();

    boolean equals(Object obj);

    int hashCode();
}

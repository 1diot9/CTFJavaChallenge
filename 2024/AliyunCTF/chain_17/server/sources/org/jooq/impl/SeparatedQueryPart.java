package org.jooq.impl;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/SeparatedQueryPart.class */
interface SeparatedQueryPart {
    default boolean rendersSeparator() {
        return true;
    }
}

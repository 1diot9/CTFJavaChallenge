package org.jooq.impl;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/SubqueryCharacteristics.class */
final class SubqueryCharacteristics {
    static final int DERIVED_TABLE = 1;
    static final int SET_OPERATION = 2;
    static final int SCALAR = 4;
    static final int PREDICAND = 256;
    static final int CORRELATED = 512;

    SubqueryCharacteristics() {
    }
}

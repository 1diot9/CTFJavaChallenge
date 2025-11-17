package org.jooq.impl;

import org.jooq.QueryPart;
import org.jooq.impl.ScopeMappableWrapper;

/* compiled from: ScopeMappable.java */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ScopeMappableWrapper.class */
interface ScopeMappableWrapper<WRAPPER extends ScopeMappableWrapper<WRAPPER, WRAPPED>, WRAPPED extends QueryPart> extends ScopeMappable {
    WRAPPER wrap(WRAPPED wrapped);
}

package org.jooq.impl;

import org.jooq.Context;
import org.jooq.QueryPart;
import org.jooq.QueryPartInternal;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AutoAlias.class */
interface AutoAlias<Q extends QueryPart> extends QueryPartInternal {
    Q autoAlias(Context<?> context, Q q);
}

package org.jooq.impl;

import org.jooq.Context;
import org.jooq.QueryPartInternal;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/SimpleCheckQueryPart.class */
public interface SimpleCheckQueryPart extends QueryPartInternal {
    default boolean isSimple(Context<?> ctx) {
        return true;
    }
}

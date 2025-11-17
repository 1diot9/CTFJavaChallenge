package org.jooq.impl;

import org.jooq.WindowDefinition;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/WindowList.class */
public final class WindowList extends QueryPartList<WindowDefinition> {
    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final boolean declaresWindows() {
        return true;
    }
}

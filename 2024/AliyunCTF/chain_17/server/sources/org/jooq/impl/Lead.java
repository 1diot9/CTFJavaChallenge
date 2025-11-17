package org.jooq.impl;

import org.jooq.Field;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Lead.class */
public final class Lead<T> extends AbstractLeadLag<T> implements QOM.Lead<T> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public Lead(Field<T> field, Field<Integer> offset, Field<T> defaultValue) {
        super(Names.N_LEAD, field, offset, defaultValue);
    }
}

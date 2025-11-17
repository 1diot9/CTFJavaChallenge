package org.jooq.impl;

import java.util.Map;
import org.jooq.Condition;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/MapCondition.class */
final class MapCondition extends AbstractCondition implements QOM.UEmpty {
    private final Map<Field<?>, ?> map;

    /* JADX INFO: Access modifiers changed from: package-private */
    public MapCondition(Map<Field<?>, ?> map) {
        this.map = map;
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public void accept(Context<?> ctx) {
        Condition c = DSL.noCondition();
        for (Map.Entry<Field<?>, ?> e : this.map.entrySet()) {
            c = c.and(e.getKey().eq(Tools.field(e.getValue(), e.getKey())));
        }
        ctx.visit(c);
    }
}

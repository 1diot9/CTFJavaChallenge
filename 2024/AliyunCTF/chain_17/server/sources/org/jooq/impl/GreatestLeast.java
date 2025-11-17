package org.jooq.impl;

import java.util.function.BiFunction;
import org.jooq.Condition;
import org.jooq.Context;
import org.jooq.Field;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/GreatestLeast.class */
final class GreatestLeast<T> {
    GreatestLeast() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> void acceptCaseEmulation(Context<?> ctx, QueryPartListView<Field<T>> args, BiFunction<? super Field<T>, ? super Field<?>[], ? extends Field<T>> greatestLeast, BiFunction<? super Field<T>, ? super Field<T>, ? extends Condition> gtLt) {
        Field<T> first = args.get(0);
        Field<T> other = args.get(1);
        if (args.size() > 2) {
            Object obj = (Field[]) args.subList(2, args.size()).toArray(Tools.EMPTY_FIELD);
            ctx.visit(DSL.when(gtLt.apply(first, other), (Field) greatestLeast.apply(first, obj)).otherwise((Field) greatestLeast.apply(other, obj)));
        } else {
            ctx.visit(DSL.when(gtLt.apply(first, other), (Field) first).otherwise((Field) other));
        }
    }
}

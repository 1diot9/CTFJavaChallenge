package org.jooq.impl;

import java.util.ArrayList;
import java.util.List;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/FieldFunction.class */
public final class FieldFunction<T> extends AbstractField<Integer> implements QOM.FieldFunction<T> {
    private final Field<T> field;
    private final QueryPartListView<Field<T>> arguments;

    /* JADX INFO: Access modifiers changed from: package-private */
    public FieldFunction(Field<T> field, Field<T>[] arguments) {
        super(Names.N_FIELD, SQLDataType.INTEGER);
        this.field = field;
        this.arguments = QueryPartListView.wrap(arguments);
    }

    /* JADX WARN: Type inference failed for: r0v8, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case MARIADB:
            case MYSQL:
                if (this.arguments.size() > 1) {
                    ctx.visit(Names.N_FIELD).sql('(').visit(QueryPartListView.wrap(Tools.combine((Field<?>) this.field, (Field<?>[]) this.arguments.toArray(Tools.EMPTY_FIELD)))).sql(')');
                    return;
                } else {
                    acceptDefault(ctx);
                    return;
                }
            default:
                acceptDefault(ctx);
                return;
        }
    }

    private final void acceptDefault(Context<?> ctx) {
        int size = this.arguments.size();
        if (size == 0) {
            ctx.visit((Field<?>) DSL.zero());
            return;
        }
        List<Field<?>> args = new ArrayList<>();
        args.add(this.field);
        for (int i = 0; i < size; i++) {
            args.add(this.arguments.get(i));
            args.add(DSL.inline(i + 1));
        }
        args.add(DSL.inline(0));
        ctx.visit(DSL.decode(args.get(0), args.get(1), args.get(2), args.subList(3, args.size()).toArray(Tools.EMPTY_FIELD)));
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<T> $arg1() {
        return this.field;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.UnmodifiableList<? extends Field<T>> $arg2() {
        return QOM.unmodifiable((List) this.arguments);
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Function2<? super Field<T>, ? super QOM.UnmodifiableList<? extends Field<T>>, ? extends QOM.FieldFunction<T>> $constructor() {
        return (f, a) -> {
            return new FieldFunction(f, (Field[]) a.toArray(Tools.EMPTY_FIELD));
        };
    }
}

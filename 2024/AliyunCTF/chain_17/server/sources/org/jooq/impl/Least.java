package org.jooq.impl;

import java.util.List;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Least.class */
public final class Least<T> extends AbstractField<T> implements QOM.Least<T> {
    private final QueryPartListView<Field<T>> args;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Least(Field<?>... args) {
        this(args, Tools.nullSafeDataType(args));
    }

    Least(Field<?>[] args, DataType<T> type) {
        super(Names.N_LEAST, type);
        this.args = QueryPartListView.wrap(args);
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> context) {
        if (this.args.isEmpty()) {
            context.visit((Field<?>) DSL.inline((Object) null, getDataType()));
            return;
        }
        if (this.args.size() == 1) {
            context.visit((Field<?>) this.args.get(0));
            return;
        }
        switch (context.family()) {
            case DERBY:
                GreatestLeast.acceptCaseEmulation(context, this.args, DSL::least, (v0, v1) -> {
                    return v0.lt(v1);
                });
                return;
            case FIREBIRD:
                context.visit(DSL.function(Names.N_MINVALUE, getDataType(), (Field<?>[]) this.args.toArray(Tools.EMPTY_FIELD)));
                return;
            case SQLITE:
                context.visit(DSL.function(Names.N_MIN, getDataType(), (Field<?>[]) this.args.toArray(Tools.EMPTY_FIELD)));
                return;
            default:
                context.visit(DSL.function(Names.N_LEAST, getDataType(), (Field<?>[]) this.args.toArray(Tools.EMPTY_FIELD)));
                return;
        }
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final QOM.UnmodifiableList<? extends Field<T>> $arg1() {
        return QOM.unmodifiable((List) this.args);
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final org.jooq.Function1<? super QOM.UnmodifiableList<? extends Field<T>>, ? extends QOM.Least<T>> $constructor() {
        return a -> {
            if (a.isEmpty()) {
                return new Least(Tools.EMPTY_FIELD, getDataType());
            }
            return new Least((Field[]) a.toArray(Tools.EMPTY_FIELD));
        };
    }
}

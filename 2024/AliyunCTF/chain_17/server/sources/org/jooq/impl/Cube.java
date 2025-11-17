package org.jooq.impl;

import java.util.List;
import org.jooq.Context;
import org.jooq.FieldOrRow;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Cube.class */
public final class Cube extends AbstractField<Object> implements QOM.Cube {
    private QueryPartList<FieldOrRow> arguments;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Cube(FieldOrRow... arguments) {
        super(Names.N_CUBE, SQLDataType.OTHER);
        this.arguments = new QueryPartList<>(arguments);
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        ctx.visit(Keywords.K_CUBE).sql(" (").visit(this.arguments).sql(')');
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator1
    public final QOM.UnmodifiableList<? extends FieldOrRow> $arg1() {
        return QOM.unmodifiable((List) this.arguments);
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final org.jooq.Function1<? super QOM.UnmodifiableList<? extends FieldOrRow>, ? extends QOM.Cube> $constructor() {
        return l -> {
            return new Cube((FieldOrRow[]) l.toArray(Tools.EMPTY_FIELD_OR_ROW));
        };
    }
}

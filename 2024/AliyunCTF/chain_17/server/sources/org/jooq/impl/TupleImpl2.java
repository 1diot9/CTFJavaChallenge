package org.jooq.impl;

import java.util.Objects;
import org.jooq.Context;
import org.jooq.QueryPart;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/TupleImpl2.class */
public final class TupleImpl2<Q1 extends QueryPart, Q2 extends QueryPart> extends AbstractQueryPart implements QOM.Tuple2<Q1, Q2> {
    private final Q1 part1;
    private final Q2 part2;

    /* JADX INFO: Access modifiers changed from: package-private */
    public TupleImpl2(Q1 part1, Q2 part2) {
        this.part1 = part1;
        this.part2 = part2;
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        ctx.sql('(').visit(this.part1).sql(", ").visit(this.part2).sql(')');
    }

    @Override // org.jooq.impl.QOM.Tuple2
    public final Q1 $1() {
        return this.part1;
    }

    @Override // org.jooq.impl.QOM.Tuple2
    public final Q2 $2() {
        return this.part2;
    }

    @Override // org.jooq.impl.QOM.Tuple2
    public final QOM.Tuple2<Q1, Q2> $1(Q1 newPart1) {
        return QOM.tuple(newPart1, this.part2);
    }

    @Override // org.jooq.impl.QOM.Tuple2
    public final QOM.Tuple2<Q1, Q2> $2(Q2 newPart2) {
        return QOM.tuple(this.part1, newPart2);
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public int hashCode() {
        int result = super.hashCode();
        return (31 * result) + Objects.hash(this.part1, this.part2);
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj) || getClass() != obj.getClass()) {
            return false;
        }
        TupleImpl2<?, ?> other = (TupleImpl2) obj;
        return Objects.equals(this.part1, other.part1) && Objects.equals(this.part2, other.part2);
    }
}

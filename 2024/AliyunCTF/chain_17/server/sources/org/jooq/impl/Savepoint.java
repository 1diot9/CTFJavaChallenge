package org.jooq.impl;

import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.Name;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Savepoint.class */
final class Savepoint extends AbstractRowCountQuery implements QOM.Savepoint {
    final Name name;

    /* renamed from: org.jooq.impl.Savepoint$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Savepoint$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Savepoint(Configuration configuration, Name name) {
        super(configuration);
        this.name = name;
    }

    /* JADX WARN: Type inference failed for: r0v3, types: [org.jooq.Context] */
    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
            default:
                ctx.visit(Keywords.K_SAVEPOINT).sql(' ').visit(this.name);
                return;
        }
    }

    @Override // org.jooq.impl.QOM.Savepoint
    public final Name $name() {
        return this.name;
    }

    @Override // org.jooq.impl.QOM.Savepoint
    public final QOM.Savepoint $name(Name newValue) {
        return $constructor().apply(newValue);
    }

    public final org.jooq.Function1<? super Name, ? extends QOM.Savepoint> $constructor() {
        return a1 -> {
            return new Savepoint(configuration(), a1);
        };
    }
}

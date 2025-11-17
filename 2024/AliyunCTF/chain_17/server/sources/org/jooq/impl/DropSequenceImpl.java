package org.jooq.impl;

import java.util.Set;
import org.jooq.Clause;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.DropSequenceFinalStep;
import org.jooq.Function2;
import org.jooq.SQLDialect;
import org.jooq.Sequence;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DropSequenceImpl.class */
public final class DropSequenceImpl extends AbstractDDLQuery implements QOM.DropSequence, DropSequenceFinalStep {
    final Sequence<?> sequence;
    final boolean ifExists;
    private static final Clause[] CLAUSES = {Clause.DROP_SEQUENCE};
    private static final Set<SQLDialect> NO_SUPPORT_IF_EXISTS = SQLDialect.supportedUntil(SQLDialect.DERBY, SQLDialect.FIREBIRD);

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.jooq.impl.DropSequenceImpl$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DropSequenceImpl$1.class */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DropSequenceImpl(Configuration configuration, Sequence<?> sequence, boolean ifExists) {
        super(configuration);
        this.sequence = sequence;
        this.ifExists = ifExists;
    }

    private final boolean supportsIfExists(Context<?> ctx) {
        return !NO_SUPPORT_IF_EXISTS.contains(ctx.dialect());
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (this.ifExists && !supportsIfExists(ctx)) {
            Tools.tryCatch(ctx, DDLStatementType.DROP_SEQUENCE, c -> {
                accept0(c);
            });
        } else {
            accept0(ctx);
        }
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v17, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v22, types: [org.jooq.Context] */
    private void accept0(Context<?> ctx) {
        ctx.start(Clause.DROP_SEQUENCE_SEQUENCE).visit(Keywords.K_DROP).sql(' ').visit(ctx.family() == SQLDialect.CUBRID ? Keywords.K_SERIAL : Keywords.K_SEQUENCE).sql(' ');
        if (this.ifExists && supportsIfExists(ctx)) {
            ctx.visit(Keywords.K_IF_EXISTS).sql(' ');
        }
        switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
            default:
                ctx.visit(this.sequence);
                if (ctx.family() == SQLDialect.DERBY) {
                    ctx.sql(' ').visit(Keywords.K_RESTRICT);
                }
                ctx.end(Clause.DROP_SEQUENCE_SEQUENCE);
                return;
        }
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    @Override // org.jooq.impl.QOM.DropSequence
    public final Sequence<?> $sequence() {
        return this.sequence;
    }

    @Override // org.jooq.impl.QOM.DropSequence
    public final boolean $ifExists() {
        return this.ifExists;
    }

    @Override // org.jooq.impl.QOM.DropSequence
    public final QOM.DropSequence $sequence(Sequence<?> newValue) {
        return $constructor().apply(newValue, Boolean.valueOf($ifExists()));
    }

    @Override // org.jooq.impl.QOM.DropSequence
    public final QOM.DropSequence $ifExists(boolean newValue) {
        return $constructor().apply($sequence(), Boolean.valueOf(newValue));
    }

    public final Function2<? super Sequence<?>, ? super Boolean, ? extends QOM.DropSequence> $constructor() {
        return (a1, a2) -> {
            return new DropSequenceImpl(configuration(), a1, a2.booleanValue());
        };
    }
}

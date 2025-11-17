package org.jooq.impl;

import java.lang.Number;
import java.util.Set;
import org.jooq.AlterSequenceFinalStep;
import org.jooq.AlterSequenceFlagsStep;
import org.jooq.AlterSequenceStep;
import org.jooq.Clause;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function14;
import org.jooq.Name;
import org.jooq.SQLDialect;
import org.jooq.Sequence;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AlterSequenceImpl.class */
public final class AlterSequenceImpl<T extends Number> extends AbstractDDLQuery implements QOM.AlterSequence<T>, AlterSequenceStep<T>, AlterSequenceFlagsStep<T>, AlterSequenceFinalStep {
    final Sequence<T> sequence;
    final boolean ifExists;
    Sequence<?> renameTo;
    boolean restart;
    Field<T> restartWith;
    Field<T> startWith;
    Field<T> incrementBy;
    Field<T> minvalue;
    boolean noMinvalue;
    Field<T> maxvalue;
    boolean noMaxvalue;
    QOM.CycleOption cycle;
    Field<T> cache;
    boolean noCache;
    private static final Clause[] CLAUSES = {Clause.ALTER_SEQUENCE};
    private static final Set<SQLDialect> NO_SUPPORT_IF_EXISTS = SQLDialect.supportedUntil(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD);
    private static final Set<SQLDialect> NO_SUPPORT_RENAME_IF_EXISTS = SQLDialect.supportedBy(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD);
    private static final Set<SQLDialect> NO_SEPARATOR = SQLDialect.supportedBy(SQLDialect.CUBRID, SQLDialect.MARIADB);
    private static final Set<SQLDialect> NO_SUPPORT_CACHE = SQLDialect.supportedBy(SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.HSQLDB);
    private static final Set<SQLDialect> EMULATE_NO_CACHE = SQLDialect.supportedBy(SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB);

    @Override // org.jooq.AlterSequenceStep
    public /* bridge */ /* synthetic */ AlterSequenceFinalStep renameTo(Sequence sequence) {
        return renameTo((Sequence<?>) sequence);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.AlterSequenceFlagsStep
    public /* bridge */ /* synthetic */ AlterSequenceFlagsStep cache(Number number) {
        return cache((AlterSequenceImpl<T>) number);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.AlterSequenceFlagsStep
    public /* bridge */ /* synthetic */ AlterSequenceFlagsStep maxvalue(Number number) {
        return maxvalue((AlterSequenceImpl<T>) number);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.AlterSequenceFlagsStep
    public /* bridge */ /* synthetic */ AlterSequenceFlagsStep minvalue(Number number) {
        return minvalue((AlterSequenceImpl<T>) number);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.AlterSequenceFlagsStep
    public /* bridge */ /* synthetic */ AlterSequenceFlagsStep incrementBy(Number number) {
        return incrementBy((AlterSequenceImpl<T>) number);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.AlterSequenceFlagsStep
    public /* bridge */ /* synthetic */ AlterSequenceFlagsStep startWith(Number number) {
        return startWith((AlterSequenceImpl<T>) number);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.AlterSequenceFlagsStep
    public /* bridge */ /* synthetic */ AlterSequenceFlagsStep restartWith(Number number) {
        return restartWith((AlterSequenceImpl<T>) number);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AlterSequenceImpl(Configuration configuration, Sequence<T> sequence, boolean ifExists) {
        this(configuration, sequence, ifExists, null, false, null, null, null, null, false, null, false, null, null, false);
    }

    AlterSequenceImpl(Configuration configuration, Sequence<T> sequence, boolean ifExists, Sequence<?> renameTo, boolean restart, Field<T> restartWith, Field<T> startWith, Field<T> incrementBy, Field<T> minvalue, boolean noMinvalue, Field<T> maxvalue, boolean noMaxvalue, QOM.CycleOption cycle, Field<T> cache, boolean noCache) {
        super(configuration);
        this.sequence = sequence;
        this.ifExists = ifExists;
        this.renameTo = renameTo;
        this.restart = restart;
        this.restartWith = restartWith;
        this.startWith = startWith;
        this.incrementBy = incrementBy;
        this.minvalue = minvalue;
        this.noMinvalue = noMinvalue;
        this.maxvalue = maxvalue;
        this.noMaxvalue = noMaxvalue;
        this.cycle = cycle;
        this.cache = cache;
        this.noCache = noCache;
    }

    @Override // org.jooq.AlterSequenceStep
    public final AlterSequenceImpl<T> renameTo(String renameTo) {
        return renameTo((Sequence<?>) DSL.sequence(DSL.name(renameTo)));
    }

    @Override // org.jooq.AlterSequenceStep
    public final AlterSequenceImpl<T> renameTo(Name renameTo) {
        return renameTo((Sequence<?>) DSL.sequence(renameTo));
    }

    @Override // org.jooq.AlterSequenceStep
    public final AlterSequenceImpl<T> renameTo(Sequence<?> renameTo) {
        this.renameTo = renameTo;
        return this;
    }

    @Override // org.jooq.AlterSequenceFlagsStep
    public final AlterSequenceImpl<T> restart() {
        this.restart = true;
        return this;
    }

    @Override // org.jooq.AlterSequenceFlagsStep
    public final AlterSequenceImpl<T> restartWith(T restartWith) {
        return restartWith((Field) Tools.field(restartWith));
    }

    @Override // org.jooq.AlterSequenceFlagsStep
    public final AlterSequenceImpl<T> restartWith(Field<T> restartWith) {
        this.restartWith = restartWith;
        return this;
    }

    @Override // org.jooq.AlterSequenceFlagsStep
    public final AlterSequenceImpl<T> startWith(T startWith) {
        return startWith((Field) Tools.field(startWith));
    }

    @Override // org.jooq.AlterSequenceFlagsStep
    public final AlterSequenceImpl<T> startWith(Field<T> startWith) {
        this.startWith = startWith;
        return this;
    }

    @Override // org.jooq.AlterSequenceFlagsStep
    public final AlterSequenceImpl<T> incrementBy(T incrementBy) {
        return incrementBy((Field) Tools.field(incrementBy));
    }

    @Override // org.jooq.AlterSequenceFlagsStep
    public final AlterSequenceImpl<T> incrementBy(Field<T> incrementBy) {
        this.incrementBy = incrementBy;
        return this;
    }

    @Override // org.jooq.AlterSequenceFlagsStep
    public final AlterSequenceImpl<T> minvalue(T minvalue) {
        return minvalue((Field) Tools.field(minvalue));
    }

    @Override // org.jooq.AlterSequenceFlagsStep
    public final AlterSequenceImpl<T> minvalue(Field<T> minvalue) {
        this.minvalue = minvalue;
        return this;
    }

    @Override // org.jooq.AlterSequenceFlagsStep
    public final AlterSequenceImpl<T> noMinvalue() {
        this.noMinvalue = true;
        return this;
    }

    @Override // org.jooq.AlterSequenceFlagsStep
    public final AlterSequenceImpl<T> maxvalue(T maxvalue) {
        return maxvalue((Field) Tools.field(maxvalue));
    }

    @Override // org.jooq.AlterSequenceFlagsStep
    public final AlterSequenceImpl<T> maxvalue(Field<T> maxvalue) {
        this.maxvalue = maxvalue;
        return this;
    }

    @Override // org.jooq.AlterSequenceFlagsStep
    public final AlterSequenceImpl<T> noMaxvalue() {
        this.noMaxvalue = true;
        return this;
    }

    @Override // org.jooq.AlterSequenceFlagsStep
    public final AlterSequenceImpl<T> cycle() {
        this.cycle = QOM.CycleOption.CYCLE;
        return this;
    }

    @Override // org.jooq.AlterSequenceFlagsStep
    public final AlterSequenceImpl<T> noCycle() {
        this.cycle = QOM.CycleOption.NO_CYCLE;
        return this;
    }

    @Override // org.jooq.AlterSequenceFlagsStep
    public final AlterSequenceImpl<T> cache(T cache) {
        return cache((Field) Tools.field(cache));
    }

    @Override // org.jooq.AlterSequenceFlagsStep
    public final AlterSequenceImpl<T> cache(Field<T> cache) {
        this.cache = cache;
        return this;
    }

    @Override // org.jooq.AlterSequenceFlagsStep
    public final AlterSequenceImpl<T> noCache() {
        this.noCache = true;
        return this;
    }

    private final boolean supportsIfExists(Context<?> ctx) {
        return this.renameTo != null ? !NO_SUPPORT_RENAME_IF_EXISTS.contains(ctx.dialect()) : !NO_SUPPORT_IF_EXISTS.contains(ctx.dialect());
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (this.ifExists && !supportsIfExists(ctx)) {
            Tools.tryCatch(ctx, DDLStatementType.ALTER_SEQUENCE, c -> {
                accept0(c);
            });
        } else {
            accept0(ctx);
        }
    }

    private final void accept0(Context<?> ctx) {
        switch (ctx.family()) {
            case MARIADB:
                if (this.renameTo != null) {
                    acceptRenameTable(ctx);
                    return;
                } else {
                    accept1(ctx);
                    return;
                }
            default:
                accept1(ctx);
                return;
        }
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    private final void acceptRenameTable(Context<?> ctx) {
        ctx.start(Clause.ALTER_SEQUENCE_SEQUENCE).start(Clause.ALTER_SEQUENCE_RENAME).visit(Keywords.K_ALTER_TABLE).sql(' ').visit(this.sequence).sql(' ').visit(Keywords.K_RENAME_TO).sql(' ').qualifySchema(false, c -> {
            c.visit(this.renameTo);
        }).end(Clause.ALTER_SEQUENCE_RENAME).end(Clause.ALTER_SEQUENCE_SEQUENCE);
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v10, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v101, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v106, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v111, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v116, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v122, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v131, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v29, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v38, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v51, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v56, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v67, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v74, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v83, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v88, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v93, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v98, types: [org.jooq.Context] */
    private final void accept1(Context<?> ctx) {
        ctx.start(Clause.ALTER_SEQUENCE_SEQUENCE).visit(Keywords.K_ALTER).sql(' ').visit(ctx.family() == SQLDialect.CUBRID ? Keywords.K_SERIAL : Keywords.K_SEQUENCE);
        if (this.ifExists && supportsIfExists(ctx)) {
            ctx.sql(' ').visit(Keywords.K_IF_EXISTS);
        }
        switch (ctx.family()) {
            default:
                ctx.sql(' ').visit(this.sequence);
                ctx.end(Clause.ALTER_SEQUENCE_SEQUENCE);
                if (this.renameTo != null) {
                    ctx.start(Clause.ALTER_SEQUENCE_RENAME).sql(' ').visit(Keywords.K_RENAME_TO).sql(' ').qualify(false, c -> {
                        c.visit(this.renameTo);
                    }).end(Clause.ALTER_SEQUENCE_RENAME);
                    return;
                }
                ctx.start(Clause.ALTER_SEQUENCE_RESTART);
                String noSeparator = NO_SEPARATOR.contains(ctx.dialect()) ? "" : " ";
                if (this.incrementBy != null) {
                    ctx.sql(' ').visit(Keywords.K_INCREMENT_BY).sql(' ').visit((Field<?>) this.incrementBy);
                }
                if (this.minvalue != null) {
                    ctx.sql(' ').visit(Keywords.K_MINVALUE).sql(' ').visit((Field<?>) this.minvalue);
                } else if (this.noMinvalue) {
                    ctx.sql(' ').visit(Keywords.K_NO).sql(noSeparator).visit(Keywords.K_MINVALUE);
                }
                if (this.maxvalue != null) {
                    ctx.sql(' ').visit(Keywords.K_MAXVALUE).sql(' ').visit((Field<?>) this.maxvalue);
                } else if (this.noMaxvalue) {
                    ctx.sql(' ').visit(Keywords.K_NO).sql(noSeparator).visit(Keywords.K_MAXVALUE);
                }
                if (this.startWith != null) {
                    ctx.sql(' ').visit(Keywords.K_START_WITH).sql(' ').visit((Field<?>) this.startWith);
                }
                if (this.restart) {
                    ctx.sql(' ').visit(Keywords.K_RESTART);
                } else if (this.restartWith != null) {
                    if (ctx.family() == SQLDialect.CUBRID) {
                        ctx.sql(' ').visit(Keywords.K_START_WITH).sql(' ').visit((Field<?>) this.restartWith);
                    } else {
                        ctx.sql(' ').visit(Keywords.K_RESTART_WITH).sql(' ').visit((Field<?>) this.restartWith);
                    }
                }
                if (!NO_SUPPORT_CACHE.contains(ctx.dialect())) {
                    if (this.cache != null) {
                        ctx.sql(' ').visit(Keywords.K_CACHE).sql(' ').visit((Field<?>) this.cache);
                    } else if (this.noCache) {
                        if (EMULATE_NO_CACHE.contains(ctx.dialect())) {
                            ctx.sql(' ').visit(Keywords.K_CACHE).sql(' ').sql(1);
                        } else {
                            ctx.sql(' ').visit(Keywords.K_NO).sql(noSeparator).visit(Keywords.K_CACHE);
                        }
                    }
                }
                if (this.cycle == QOM.CycleOption.CYCLE) {
                    ctx.sql(' ').visit(Keywords.K_CYCLE);
                } else if (this.cycle == QOM.CycleOption.NO_CYCLE) {
                    ctx.sql(' ').visit(Keywords.K_NO).sql(noSeparator).visit(Keywords.K_CYCLE);
                }
                ctx.end(Clause.ALTER_SEQUENCE_RESTART);
                return;
        }
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    @Override // org.jooq.impl.QOM.AlterSequence
    public final Sequence<T> $sequence() {
        return this.sequence;
    }

    @Override // org.jooq.impl.QOM.AlterSequence
    public final boolean $ifExists() {
        return this.ifExists;
    }

    @Override // org.jooq.impl.QOM.AlterSequence
    public final Sequence<?> $renameTo() {
        return this.renameTo;
    }

    @Override // org.jooq.impl.QOM.AlterSequence
    public final boolean $restart() {
        return this.restart;
    }

    @Override // org.jooq.impl.QOM.AlterSequence
    public final Field<T> $restartWith() {
        return this.restartWith;
    }

    @Override // org.jooq.impl.QOM.AlterSequence
    public final Field<T> $startWith() {
        return this.startWith;
    }

    @Override // org.jooq.impl.QOM.AlterSequence
    public final Field<T> $incrementBy() {
        return this.incrementBy;
    }

    @Override // org.jooq.impl.QOM.AlterSequence
    public final Field<T> $minvalue() {
        return this.minvalue;
    }

    @Override // org.jooq.impl.QOM.AlterSequence
    public final boolean $noMinvalue() {
        return this.noMinvalue;
    }

    @Override // org.jooq.impl.QOM.AlterSequence
    public final Field<T> $maxvalue() {
        return this.maxvalue;
    }

    @Override // org.jooq.impl.QOM.AlterSequence
    public final boolean $noMaxvalue() {
        return this.noMaxvalue;
    }

    @Override // org.jooq.impl.QOM.AlterSequence
    public final QOM.CycleOption $cycle() {
        return this.cycle;
    }

    @Override // org.jooq.impl.QOM.AlterSequence
    public final Field<T> $cache() {
        return this.cache;
    }

    @Override // org.jooq.impl.QOM.AlterSequence
    public final boolean $noCache() {
        return this.noCache;
    }

    @Override // org.jooq.impl.QOM.AlterSequence
    public final QOM.AlterSequence<T> $sequence(Sequence<T> newValue) {
        return $constructor().apply(newValue, Boolean.valueOf($ifExists()), $renameTo(), Boolean.valueOf($restart()), $restartWith(), $startWith(), $incrementBy(), $minvalue(), Boolean.valueOf($noMinvalue()), $maxvalue(), Boolean.valueOf($noMaxvalue()), $cycle(), $cache(), Boolean.valueOf($noCache()));
    }

    @Override // org.jooq.impl.QOM.AlterSequence
    public final QOM.AlterSequence<T> $ifExists(boolean newValue) {
        return $constructor().apply($sequence(), Boolean.valueOf(newValue), $renameTo(), Boolean.valueOf($restart()), $restartWith(), $startWith(), $incrementBy(), $minvalue(), Boolean.valueOf($noMinvalue()), $maxvalue(), Boolean.valueOf($noMaxvalue()), $cycle(), $cache(), Boolean.valueOf($noCache()));
    }

    @Override // org.jooq.impl.QOM.AlterSequence
    public final QOM.AlterSequence<T> $renameTo(Sequence<?> newValue) {
        return $constructor().apply($sequence(), Boolean.valueOf($ifExists()), newValue, Boolean.valueOf($restart()), $restartWith(), $startWith(), $incrementBy(), $minvalue(), Boolean.valueOf($noMinvalue()), $maxvalue(), Boolean.valueOf($noMaxvalue()), $cycle(), $cache(), Boolean.valueOf($noCache()));
    }

    @Override // org.jooq.impl.QOM.AlterSequence
    public final QOM.AlterSequence<T> $restart(boolean newValue) {
        return $constructor().apply($sequence(), Boolean.valueOf($ifExists()), $renameTo(), Boolean.valueOf(newValue), $restartWith(), $startWith(), $incrementBy(), $minvalue(), Boolean.valueOf($noMinvalue()), $maxvalue(), Boolean.valueOf($noMaxvalue()), $cycle(), $cache(), Boolean.valueOf($noCache()));
    }

    @Override // org.jooq.impl.QOM.AlterSequence
    public final QOM.AlterSequence<T> $restartWith(Field<T> newValue) {
        return $constructor().apply($sequence(), Boolean.valueOf($ifExists()), $renameTo(), Boolean.valueOf($restart()), newValue, $startWith(), $incrementBy(), $minvalue(), Boolean.valueOf($noMinvalue()), $maxvalue(), Boolean.valueOf($noMaxvalue()), $cycle(), $cache(), Boolean.valueOf($noCache()));
    }

    @Override // org.jooq.impl.QOM.AlterSequence
    public final QOM.AlterSequence<T> $startWith(Field<T> newValue) {
        return $constructor().apply($sequence(), Boolean.valueOf($ifExists()), $renameTo(), Boolean.valueOf($restart()), $restartWith(), newValue, $incrementBy(), $minvalue(), Boolean.valueOf($noMinvalue()), $maxvalue(), Boolean.valueOf($noMaxvalue()), $cycle(), $cache(), Boolean.valueOf($noCache()));
    }

    @Override // org.jooq.impl.QOM.AlterSequence
    public final QOM.AlterSequence<T> $incrementBy(Field<T> newValue) {
        return $constructor().apply($sequence(), Boolean.valueOf($ifExists()), $renameTo(), Boolean.valueOf($restart()), $restartWith(), $startWith(), newValue, $minvalue(), Boolean.valueOf($noMinvalue()), $maxvalue(), Boolean.valueOf($noMaxvalue()), $cycle(), $cache(), Boolean.valueOf($noCache()));
    }

    @Override // org.jooq.impl.QOM.AlterSequence
    public final QOM.AlterSequence<T> $minvalue(Field<T> newValue) {
        return $constructor().apply($sequence(), Boolean.valueOf($ifExists()), $renameTo(), Boolean.valueOf($restart()), $restartWith(), $startWith(), $incrementBy(), newValue, Boolean.valueOf($noMinvalue()), $maxvalue(), Boolean.valueOf($noMaxvalue()), $cycle(), $cache(), Boolean.valueOf($noCache()));
    }

    @Override // org.jooq.impl.QOM.AlterSequence
    public final QOM.AlterSequence<T> $noMinvalue(boolean newValue) {
        return $constructor().apply($sequence(), Boolean.valueOf($ifExists()), $renameTo(), Boolean.valueOf($restart()), $restartWith(), $startWith(), $incrementBy(), $minvalue(), Boolean.valueOf(newValue), $maxvalue(), Boolean.valueOf($noMaxvalue()), $cycle(), $cache(), Boolean.valueOf($noCache()));
    }

    @Override // org.jooq.impl.QOM.AlterSequence
    public final QOM.AlterSequence<T> $maxvalue(Field<T> newValue) {
        return $constructor().apply($sequence(), Boolean.valueOf($ifExists()), $renameTo(), Boolean.valueOf($restart()), $restartWith(), $startWith(), $incrementBy(), $minvalue(), Boolean.valueOf($noMinvalue()), newValue, Boolean.valueOf($noMaxvalue()), $cycle(), $cache(), Boolean.valueOf($noCache()));
    }

    @Override // org.jooq.impl.QOM.AlterSequence
    public final QOM.AlterSequence<T> $noMaxvalue(boolean newValue) {
        return $constructor().apply($sequence(), Boolean.valueOf($ifExists()), $renameTo(), Boolean.valueOf($restart()), $restartWith(), $startWith(), $incrementBy(), $minvalue(), Boolean.valueOf($noMinvalue()), $maxvalue(), Boolean.valueOf(newValue), $cycle(), $cache(), Boolean.valueOf($noCache()));
    }

    @Override // org.jooq.impl.QOM.AlterSequence
    public final QOM.AlterSequence<T> $cycle(QOM.CycleOption newValue) {
        return $constructor().apply($sequence(), Boolean.valueOf($ifExists()), $renameTo(), Boolean.valueOf($restart()), $restartWith(), $startWith(), $incrementBy(), $minvalue(), Boolean.valueOf($noMinvalue()), $maxvalue(), Boolean.valueOf($noMaxvalue()), newValue, $cache(), Boolean.valueOf($noCache()));
    }

    @Override // org.jooq.impl.QOM.AlterSequence
    public final QOM.AlterSequence<T> $cache(Field<T> newValue) {
        return $constructor().apply($sequence(), Boolean.valueOf($ifExists()), $renameTo(), Boolean.valueOf($restart()), $restartWith(), $startWith(), $incrementBy(), $minvalue(), Boolean.valueOf($noMinvalue()), $maxvalue(), Boolean.valueOf($noMaxvalue()), $cycle(), newValue, Boolean.valueOf($noCache()));
    }

    @Override // org.jooq.impl.QOM.AlterSequence
    public final QOM.AlterSequence<T> $noCache(boolean newValue) {
        return $constructor().apply($sequence(), Boolean.valueOf($ifExists()), $renameTo(), Boolean.valueOf($restart()), $restartWith(), $startWith(), $incrementBy(), $minvalue(), Boolean.valueOf($noMinvalue()), $maxvalue(), Boolean.valueOf($noMaxvalue()), $cycle(), $cache(), Boolean.valueOf(newValue));
    }

    public final Function14<? super Sequence<T>, ? super Boolean, ? super Sequence<?>, ? super Boolean, ? super Field<T>, ? super Field<T>, ? super Field<T>, ? super Field<T>, ? super Boolean, ? super Field<T>, ? super Boolean, ? super QOM.CycleOption, ? super Field<T>, ? super Boolean, ? extends QOM.AlterSequence<T>> $constructor() {
        return (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14) -> {
            return new AlterSequenceImpl(configuration(), a1, a2.booleanValue(), a3, a4.booleanValue(), a5, a6, a7, a8, a9.booleanValue(), a10, a11.booleanValue(), a12, a13, a14.booleanValue());
        };
    }
}
